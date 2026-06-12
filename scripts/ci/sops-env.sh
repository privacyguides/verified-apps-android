#!/usr/bin/env bash
# Decrypt named keys from a SOPS file and export them as masked env vars via
# GITHUB_ENV for later steps. Fails on missing, empty, or CHANGEME values.
#
# Usage: sops-env.sh <sops-file> KEY [KEY ...]
set -euo pipefail
set +x  # never echo plaintext, even if xtrace is inherited

if [ "$#" -lt 2 ]; then
  echo "usage: $0 <sops-file> KEY [KEY ...]" >&2
  exit 2
fi

FILE="$1"
shift

if [ -z "${GITHUB_ENV:-}" ]; then
  echo "GITHUB_ENV is not set; this script is meant for GitHub Actions or a compatible runner." >&2
  exit 1
fi

if [ ! -f "$FILE" ]; then
  echo "::error::Secrets file '$FILE' not found in this checkout." >&2
  exit 1
fi

for key in "$@"; do
  if ! value="$(sops decrypt --extract "[\"${key}\"]" "$FILE")"; then
    echo "::error::Failed to decrypt '${key}' from ${FILE}. Is SOPS_AGE_KEY set and does it match .sops.yaml?" >&2
    exit 1
  fi
  if [ -z "$value" ] || [ "$value" = "CHANGEME" ] || [ "$value" = "null" ]; then
    echo "::error::Secret '${key}' in ${FILE} is empty or still a placeholder. Populate it with: sops edit ${FILE}" >&2
    exit 1
  fi

  # Mask each line before it can reach a log. % and CR are escaped because the
  # runner percent-decodes mask payloads.
  while IFS= read -r line; do
    [ -n "$line" ] || continue
    masked=${line//'%'/%25}
    masked=${masked//$'\r'/%0D}
    printf '%s\n' "::add-mask::${masked}"
  done <<<"$value"

  # Random delimiter so a value can't terminate its own heredoc.
  delim="SOPS_EOF_$(od -An -N16 -tx1 /dev/urandom | tr -d ' \n')"
  while [ "${value#*"$delim"}" != "$value" ]; do
    delim="SOPS_EOF_$(od -An -N16 -tx1 /dev/urandom | tr -d ' \n')"
  done
  {
    printf '%s\n' "${key}<<${delim}"
    printf '%s\n' "$value"
    printf '%s\n' "${delim}"
  } >> "$GITHUB_ENV"
done

echo "Exported from ${FILE}: $*"
