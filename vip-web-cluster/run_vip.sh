#!/bin/bash
set -euo pipefail

# load vip module with unspecified version so that latest version is used automagically
module purge
module load vip

args=()
args+=("--workflow" "vcf")
args+=("--input" "${INPUT_DIR}/samplesheet.tsv")
args+=("--config" "${INPUT_DIR}/config/config.cfg")
args+=("--output" "${OUTPUT_DIR}")
args+=("--profile" "local")

# set VIP_DIR as a workaround for "SCRIPT_DIR is incorrect when vip.sh is submitted as a Slurm job that is submitted as part of another Slurm job", see vip.sh
VIP_DIR="${EBROOTVIP}" vip "${args[@]}" 1> /dev/null
