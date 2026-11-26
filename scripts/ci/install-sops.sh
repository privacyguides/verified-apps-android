#!/usr/bin/env bash
# SPDX-FileCopyrightText: 2026 Jonah Aragon
# SPDX-License-Identifier: MIT

# Download a checksum-pinned sops binary onto the runner.
#
# A sops already on PATH is not trusted by default since it runs with
# SOPS_AGE_KEY in its environment; set INSTALL_SOPS_ALLOW_SYSTEM=1 to use it.
set -euo pipefail

if [ "${INSTALL_SOPS_ALLOW_SYSTEM:-0}" = "1" ] && command -v sops >/dev/null 2>&1; then
  echo "INSTALL_SOPS_ALLOW_SYSTEM=1: using existing sops at $(command -v sops): $(sops --version 2>/dev/null | head -n1)"
  exit 0
fi

SOPS_VERSION="3.13.3"
case "$(uname -s)-$(uname -m)" in
  Linux-x86_64)
    ASSET="sops-v${SOPS_VERSION}.linux.amd64"
    SHA256="e5bec3346a873ae91d871550f3e698c1aad962aff462a080e40f25fde17fef6b"
    ;;
  Linux-aarch64 | Linux-arm64)
    ASSET="sops-v${SOPS_VERSION}.linux.arm64"
    SHA256="53b0abacd38ef1b12a66d6c100956691b9cefce018d91f81e73ddf7438b94d77"
    ;;
  Darwin-arm64)
    ASSET="sops-v${SOPS_VERSION}.darwin.arm64"
    SHA256="b97c0d434aab577dc40310e8d22ff9e45eef4c80638ab978daae9b4681c59286"
    ;;
  Darwin-x86_64)
    ASSET="sops-v${SOPS_VERSION}.darwin.amd64"
    SHA256="42162d5cef10b74fcf80a045a70e658d7ce6e63d6ea1be6f347e44015714468d"
    ;;
  *)
    echo "Unsupported platform: $(uname -s)-$(uname -m)" >&2
    exit 1
    ;;
esac

DEST="${RUNNER_TEMP:-${TMPDIR:-/tmp}}/sops-bin"
mkdir -p "$DEST"
curl -fsSL --retry 3 -o "$DEST/sops" \
  "https://github.com/getsops/sops/releases/download/v${SOPS_VERSION}/${ASSET}"

if command -v sha256sum >/dev/null 2>&1; then
  echo "${SHA256}  ${DEST}/sops" | sha256sum -c -
else
  echo "${SHA256}  ${DEST}/sops" | shasum -a 256 -c -
fi

chmod +x "$DEST/sops"
if [ -n "${GITHUB_PATH:-}" ]; then
  echo "$DEST" >> "$GITHUB_PATH"
else
  echo "Installed to $DEST/sops (GITHUB_PATH not set; add it to PATH yourself)."
fi
echo "Installed sops v${SOPS_VERSION} (${ASSET})."
