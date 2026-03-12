#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

echo "Running base quality gate..."
./gradlew qualityGateBase --no-daemon

echo
echo -n "Visual simulator tests meenemen (paparazzi + shot + maestro)? [ja/nee, default nee na 10s]: "
ANSWER="nee"
if read -r -t 10 INPUT; then
  if [[ -n "${INPUT:-}" ]]; then
    ANSWER="$INPUT"
  fi
else
  echo
  echo "Geen input binnen 10s, visual tests worden overgeslagen."
fi

ANSWER_LOWER="$(echo "$ANSWER" | tr '[:upper:]' '[:lower:]')"
if [[ "$ANSWER_LOWER" == "ja" || "$ANSWER_LOWER" == "j" || "$ANSWER_LOWER" == "yes" || "$ANSWER_LOWER" == "y" ]]; then
  SNAPSHOT_DIR="$ROOT_DIR/apps/android/src/test/snapshots"
  if [[ ! -d "$SNAPSHOT_DIR" ]] || [[ -z "$(find "$SNAPSHOT_DIR" -type f 2>/dev/null | head -n 1)" ]]; then
    echo "No Paparazzi baselines found, recording initial snapshots..."
    ./gradlew :apps:android:recordPaparazziDebug --no-daemon
  fi

  echo "Running Paparazzi verification..."
  ./gradlew :apps:android:verifyPaparazziDebug --no-daemon

  echo "Running Shot screenshot tests..."
  ./gradlew :apps:android:debugExecuteScreenshotTests --no-daemon

  if command -v maestro >/dev/null 2>&1; then
    if [[ -f "$ROOT_DIR/.maestro/flow_smoke.yaml" ]]; then
      echo "Running Maestro flow..."
      maestro test "$ROOT_DIR/.maestro/flow_smoke.yaml"
    else
      echo "Maestro flow file not found, skipping Maestro."
    fi
  else
    echo "Maestro CLI not found, skipping Maestro."
  fi
else
  echo "Visual simulator tests overgeslagen."
fi

echo "Local quality gate finished."
