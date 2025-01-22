#!/bin/bash
set -euo pipefail

SCRIPT_NAME="$(basename "$0")"
SCRIPT_DIR="$(realpath "$(dirname "$0")")"

execute() {
  local -r job_dir="${1}"
  local -r cookie="${2}"
  local -r vip_job_id="${3}"

  local -r time="${SLURM_TIMELIMIT:-"05:59:59"}"
  local -r output_dir="${job_dir}/output"
  local -r tmp_dir="${job_dir}/tmp"
  local -r nxf_home_dir="${tmp_dir}/nxf/home"
  local -r nxf_tmp_dir="${tmp_dir}/nxf/tmp"
  local -r nxf_work_dir="${tmp_dir}/nxf/work"

  mkdir -p "${output_dir}"
  mkdir -p "${tmp_dir}"
  mkdir -p "${nxf_home_dir}"
  mkdir -p "${nxf_tmp_dir}"
  mkdir -p "${nxf_work_dir}"

  local sbatch_args=()
  sbatch_args+=("--parsable")
  sbatch_args+=("--job-name=vipweb_${vip_job_id}")
  sbatch_args+=("--time=${time}")
  sbatch_args+=("--cpus-per-task=5") # 4 + 1 for driver job
  sbatch_args+=("--mem=10gb")        # 8 + 2 for driver job
  sbatch_args+=("--nodes=1")
  sbatch_args+=("--open-mode=append")
  sbatch_args+=("--export=TMPDIR=${tmp_dir}/tmp,NXF_HOME=${nxf_home_dir},NXF_TEMP=${nxf_tmp_dir},NXF_WORK=${nxf_work_dir},INPUT_DIR=${job_dir},OUTPUT_DIR=${output_dir}")
  sbatch_args+=("--get-user-env=L")
  sbatch_args+=("--output=${output_dir}/job.out")
  sbatch_args+=("--error=${output_dir}/job.err")
  sbatch_args+=("--chdir=${output_dir}")
  sbatch_args+=("${SCRIPT_DIR}/run_vip.sh")

  local -r job_id="$(SBATCH_QOS="priority" sbatch "${sbatch_args[@]}")"

  local job_status
  while true; do
    job_status="$(sacct -j "${job_id}" -o State | awk 'FNR == 3 {print $1}')"

    # 'CANCELLED+' triggers when job is cancelled by user, e.g. 'CANCELLED by 12345678'
    if [[ "${job_status}" == "COMPLETED" || "${job_status}" == "FAILED" || "${job_status}" == "CANCELLED" || "${job_status}" == "CANCELLED+" || "${job_status}" == "PREEMPTED" ]]; then
      break
    fi
  
    # take a break before checking again
    sleep 1
  done

  if [[ "${job_status}" == "COMPLETED" ]]; then
    (cd "${output_dir}" && curl --fail --silent --cookie "${cookie}" --upload-file "vip.html" "https://vip.molgeniscloud.org/api/job/${vip_job_id}/report/")
  else
    local status
    if [[ "${job_status}" == "FAILED" ]]; then
      status="FAILED"
    else
      status="CANCELLED"
    fi
    curl --fail --silent --cookie "${cookie}" --request PATCH "https://vip.molgeniscloud.org/api/job/${vip_job_id}/status/${status}"
  fi
}

run_job() {
  local -r job_id="${1}"
  local -r cookie="${2}"

  local -r job_dir="${SCRIPT_DIR}/runs/${job_id}"

  mkdir -p "${job_dir}/data"
  (cd "${job_dir}/data" && curl --fail --silent --cookie "${cookie}" --request GET "https://vip.molgeniscloud.org/api/job/${job_id}/vcf" --remote-name --remote-header-name)
  
  mkdir -p "${job_dir}"
  (cd "${job_dir}" && curl --fail --silent --cookie "${cookie}" --request GET "https://vip.molgeniscloud.org/api/job/${job_id}/samplesheet" --remote-name --remote-header-name)

  mkdir -p "${job_dir}/config"
  (cd "${job_dir}/config" && curl --fail --silent --cookie "${cookie}" --request GET "https://vip.molgeniscloud.org/api/job/${job_id}/filtertree/variant" --remote-name --remote-header-name)
  (cd "${job_dir}/config" && curl --fail --silent --cookie "${cookie}" --request GET "https://vip.molgeniscloud.org/api/job/${job_id}/filtertree/sample" --remote-name --remote-header-name)

  local -r variantClasses="$(curl --fail --silent --cookie "${cookie}" --request GET "https://vip.molgeniscloud.org/api/job/${job_id}/filterclasses/variant")"
  local -r sampleClasses="$(curl --fail --silent --cookie "${cookie}" --request GET "https://vip.molgeniscloud.org/api/job/${job_id}/filterclasses/sample")"

cat <<EOF > "${job_dir}/config/config.cfg"
params {
  vcf {
    classify {
      GRCh38 {
        decision_tree = "${job_dir}/config/classification_tree_variants.json"
      }
    }
    classify_samples {
      GRCh38 {
        decision_tree = "${job_dir}/config/classification_tree_samples.json"
      }
    }
    filter {
      classes = "${variantClasses}"
    }
    filter_samples {
      classes = "${sampleClasses}"
    }
  }
}
EOF

  execute "${job_dir}" "${cookie}" "${job_id}"
}

run() {
  local -r runs_dir="${1}"
  local -r cookie="${runs_dir}/rememberme_cookie.txt"
  if [[ ! -f ${cookie} ]]; then
    # shellcheck disable=SC2034
    local -r user_details=$(curl --fail --silent --cookie-jar "${cookie}" --header "Content-Type: application/x-www-form-urlencoded" --data "username=${VIPWEB_VIPBOT_USERNAME}&password=${VIPWEB_VIPBOT_PASSWORD}" --request POST "https://vip.molgeniscloud.org/api/auth/login")
  fi

  local -r jobId="$(curl --fail --silent --cookie "${cookie}" --request POST 'https://vip.molgeniscloud.org/api/job/claim')"
  if [ -n "${jobId}" ]; then
    run_job "${jobId}" "${cookie}"
  fi
}

usage() {
  echo -e "usage: ${SCRIPT_NAME}
  -h, --help             Print this message and exit
  environment variables 'VIPWEB_VIPBOT_USERNAME', 'VIPWEB_VIPBOT_PASSWORD' and 'VIP_DIR' must be set"
}

main() {
  local -r args=$(getopt -a -n pipeline -o h --long help -- "$@")
  # shellcheck disable=SC2181
  if [[ $? != 0 ]]; then
    usage
    exit 2
  fi

  eval set -- "${args}"
  while :; do
    case "$1" in
    -h | --help)
      usage
      exit 0
      ;;
    --)
      shift
      break
      ;;
    *)
      usage
      exit 2
      ;;
    esac
  done

  if [ -z ${VIPWEB_VIPBOT_USERNAME+x} ]; then
    echo "error: required environment variable 'VIPWEB_VIPBOT_USERNAME' is unset" >&2
    exit 1
  fi
  if [ -z ${VIPWEB_VIPBOT_PASSWORD+x} ]; then
    echo "error: required environment variable 'VIPWEB_VIPBOT_PASSWORD' is unset" >&2
    exit 1
  fi
  if [ -z ${VIP_DIR+x} ]; then
    echo "error: required environment variable 'VIP_DIR' is unset" >&2
    exit 1
  fi

  local -r runs_dir="${SCRIPT_DIR}/runs"
  mkdir -p "${runs_dir}"

  run "${runs_dir}"
}

main "$@"
