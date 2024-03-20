#!/bin/bash
set -euo pipefail

args=()
args+=("--workflow" "vcf")
args+=("--input" "${INPUT_DIR}/samplesheet.tsv")
args+=("--config" "${INPUT_DIR}/config/config.cfg")
args+=("--output" "${OUTPUT_DIR}")
args+=("--profile" "local")
vip "${args[@]}" 1> /dev/null
