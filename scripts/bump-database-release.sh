#!/usr/bin/env bash
# Bump CalVer in app/build.gradle.kts and add a Play Store changelog for a database release.
# Usage: scripts/bump-database-release.sh
set -euo pipefail

cd "$(dirname "$0")/.."

BUILD_GRADLE="app/build.gradle.kts"
CHANGELOG_DIR="fastlane/metadata/android/en-US/changelogs"

read_version() {
  local key=$1
  grep "val ${key} =" "$BUILD_GRADLE" | sed 's/.*= //; s/[^0-9].*//'
}

current_year=$(read_version releaseVersionYear)
current_month=$(read_version releaseVersionMonth)
current_patch=$(read_version releaseVersionPatch)

new_year=$(date +%y)
new_month=$((10#$(date +%m)))

if [[ "$current_year" -eq "$new_year" && "$current_month" -eq "$new_month" ]]; then
  new_patch=$((current_patch + 1))
else
  new_patch=0
fi

version_code=$((new_year * 1000000 + new_month * 10000 + new_patch))
version_name="${new_year}.${new_month}.${new_patch}"
changelog_file="${CHANGELOG_DIR}/${version_code}.txt"

perl -pi -e "
  s/val releaseVersionYear = \\d+/val releaseVersionYear = ${new_year}/;
  s/val releaseVersionMonth = \\d+/val releaseVersionMonth = ${new_month}/;
  s/val releaseVersionPatch = \\d+/val releaseVersionPatch = ${new_patch}/;
" "$BUILD_GRADLE"

mkdir -p "$CHANGELOG_DIR"
printf '%s\n' "Database update." > "$changelog_file"

echo "Updated to ${version_name} (versionCode ${version_code})"
echo "Changelog: ${changelog_file}"
