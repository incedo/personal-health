#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

if [[ ! -f "build/reports/kover/report.xml" ]]; then
  echo "Geen root coverage report gevonden. Run eerst:"
  echo "  ./gradlew koverXmlReport --no-daemon"
  exit 1
fi

modules=(
  "apps/android"
  "apps/desktop"
  "apps/web"
  "shared/app"
  "core/designsystem"
  "core/events"
  "core/health"
  "feature/home"
  "feature/onboarding"
  "integration/health-connect"
  "integration/healthkit"
)

target_for_module() {
  local module="$1"
  if [[ "$module" == core/* ]]; then
    echo "95"
  elif [[ "$module" == feature/* ]]; then
    echo "80"
  elif [[ "$module" == apps/* || "$module" == shared/* || "$module" == integration/* ]]; then
    echo "-"
  else
    echo "-"
  fi
}

coverage_line_from_xml() {
  local xml="$1"
  rg '<counter type="LINE"' "$xml" | tail -n 1
}

calc_percent() {
  local covered="$1"
  local missed="$2"
  local total=$((covered + missed))
  if [[ "$total" -eq 0 ]]; then
    echo "n/a"
  else
    awk -v c="$covered" -v t="$total" 'BEGIN { printf "%.2f", (c/t)*100 }'
  fi
}

echo "Coverage summary (line coverage)"
printf "%-28s %-12s %-10s %-10s %-10s\n" "Module" "Coverage" "Covered" "Missed" "Target"

for module in "${modules[@]}"; do
  xml="$module/build/reports/kover/report.xml"
  if [[ ! -f "$xml" ]]; then
    printf "%-28s %-12s %-10s %-10s %-10s\n" "$module" "missing" "-" "-" "$(target_for_module "$module")"
    continue
  fi

  line="$(coverage_line_from_xml "$xml")"
  covered="$(echo "$line" | sed -E 's/.*covered="([0-9]+)".*/\1/')"
  missed="$(echo "$line" | sed -E 's/.*missed="([0-9]+)".*/\1/')"
  pct="$(calc_percent "$covered" "$missed")"
  printf "%-28s %-12s %-10s %-10s %-10s\n" "$module" "${pct}%" "$covered" "$missed" "$(target_for_module "$module")"
done

root_line="$(coverage_line_from_xml "build/reports/kover/report.xml")"
root_covered="$(echo "$root_line" | sed -E 's/.*covered="([0-9]+)".*/\1/')"
root_missed="$(echo "$root_line" | sed -E 's/.*missed="([0-9]+)".*/\1/')"
root_pct="$(calc_percent "$root_covered" "$root_missed")"
echo
echo "Root total: ${root_pct}% (target: 70%)"
