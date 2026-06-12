#!/usr/bin/env bash
# Download a checksum-pinned sops binary onto the runner.
#
# A sops already on PATH is not trusted by default since it runs with
# SOPS_AGE_KEY in its environment; set INSTALL_SOPS_ALLOW_SYSTEM=1 to use it.
set -euo pipefail

if [ "${INSTALL_SOPS_ALLOW_SYSTEM:-0}" = "1" ] && command -v sops >/dev/null 2>&1; then
  echo "INSTALL_SOPS_ALLOW_SYSTEM=1: using existing sops at $(command -v sops): $(sops --version 2>/dev/null | head -n1)"
  exit 0
fi

SOPS_VERSION="3.13.1"
case "$(uname -s)-$(uname -m)" in
  Linux-x86_64)
    ASSET="sops-v${SOPS_VERSION}.linux.amd64"
    SHA256="620a9d7e3352ababeca6908cea24a6e8b14ce89a448ddbd3f94f1ef3398f470a"
    ;;
  Linux-aarch64 | Linux-arm64)
    ASSET="sops-v${SOPS_VERSION}.linux.arm64"
    SHA256="19576fb1734dbf8fb77eda0cf0f3a2218f99bf4d33b814318e5e10d6babb9820"
    ;;
  Darwin-arm64)
    ASSET="sops-v${SOPS_VERSION}.darwin.arm64"
    SHA256="a2c0dd37eb031068af6ef213b78cfa67b7f1afd76c2e5cc404257f42bbc8367d"
    ;;
  Darwin-x86_64)
    ASSET="sops-v${SOPS_VERSION}.darwin.amd64"
    SHA256="dad79d1b1dea767ca38ffaa50e10330a3e807dd13c853ef9c880567acef4f1ef"
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
