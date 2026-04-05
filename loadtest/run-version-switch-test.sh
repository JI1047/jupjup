#!/usr/bin/env bash

set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
REDIS_CONTAINER="${REDIS_CONTAINER:-integrated-login-redis}"
K6_SCRIPT="${K6_SCRIPT:-loadtest/map-main-rps10-3m.js}"
TRIGGER_AFTER_SECONDS="${TRIGGER_AFTER_SECONDS:-30}"
POLL_INTERVAL_SECONDS="${POLL_INTERVAL_SECONDS:-5}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
SUMMARY_PATH="$REPO_ROOT/loadtest/k6-version-switch-summary.json"
STDOUT_PATH="$REPO_ROOT/loadtest/k6-version-switch.out.log"
STDERR_PATH="$REPO_ROOT/loadtest/k6-version-switch.err.log"

rm -f "$SUMMARY_PATH" "$STDOUT_PATH" "$STDERR_PATH"

require_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Required command not found: $1" >&2
    exit 1
  fi
}

format_value() {
  local value="${1:-}"
  if [[ -z "$value" ]]; then
    echo "<null>"
  else
    echo "$value"
  fi
}

get_active_version() {
  docker exec "$REDIS_CONTAINER" redis-cli GET pointsMain:latest_version | tr -d '\r' | tail -n 1
}

get_points_keys() {
  docker exec "$REDIS_CONTAINER" redis-cli KEYS "pointsMain*" | tr -d '\r'
}

start_k6() {
  if command -v k6 >/dev/null 2>&1; then
    (
      cd "$REPO_ROOT"
      BASE_URL="$BASE_URL" k6 run --summary-export "$SUMMARY_PATH" "$K6_SCRIPT"
    ) >"$STDOUT_PATH" 2>"$STDERR_PATH" &
    echo $!
    return
  fi

  require_command docker
  (
    cd "$REPO_ROOT"
    docker run --rm --network host \
      -e BASE_URL="$BASE_URL" \
      -v "$REPO_ROOT:/work" \
      -w /work \
      grafana/k6 run --summary-export "$SUMMARY_PATH" "$K6_SCRIPT"
  ) >"$STDOUT_PATH" 2>"$STDERR_PATH" &
  echo $!
}

require_command curl
require_command docker

if [[ ! -f "$REPO_ROOT/$K6_SCRIPT" ]]; then
  echo "K6 script not found: $REPO_ROOT/$K6_SCRIPT" >&2
  exit 1
fi

STARTED_AT="$(date +%s)"
INITIAL_VERSION="$(get_active_version)"
INITIAL_KEYS="$(get_points_keys | paste -sd ',' -)"

echo "=== Version Switch Load Test ==="
echo "Base URL            : $BASE_URL"
echo "K6 script           : $REPO_ROOT/$K6_SCRIPT"
echo "Initial version     : $(format_value "$INITIAL_VERSION")"
echo "Initial keys        : ${INITIAL_KEYS:-<none>}"
echo "Batch trigger after : ${TRIGGER_AFTER_SECONDS}s"
echo

K6_PID="$(start_k6)"
TRIGGERED=0
TRIGGERED_AT=0
VERSION_SWITCHED_AT=0

while kill -0 "$K6_PID" >/dev/null 2>&1; do
  ELAPSED_SECONDS="$(( $(date +%s) - STARTED_AT ))"

  if [[ "$TRIGGERED" -eq 0 && "$ELAPSED_SECONDS" -ge "$TRIGGER_AFTER_SECONDS" ]]; then
    echo
    echo "[$(date +%H:%M:%S)] Triggering batch refresh..."
    curl --fail --silent "$BASE_URL/api/map/test-import" >/dev/null
    TRIGGERED=1
    TRIGGERED_AT="$(date +%s)"
    echo "Batch response      : HTTP 200"
  fi

  CURRENT_VERSION="$(get_active_version)"
  echo "[$(date +%H:%M:%S)] Active version: $(format_value "$CURRENT_VERSION")"

  if [[ "$TRIGGERED" -eq 1 && "$VERSION_SWITCHED_AT" -eq 0 && -n "$CURRENT_VERSION" && "$CURRENT_VERSION" != "$INITIAL_VERSION" ]]; then
    VERSION_SWITCHED_AT="$(date +%s)"
    echo "Version switched    : $(format_value "$INITIAL_VERSION") -> $CURRENT_VERSION"
  fi

  sleep "$POLL_INTERVAL_SECONDS"
done

wait "$K6_PID"
K6_EXIT_CODE="$?"

FINAL_VERSION="$(get_active_version)"
FINAL_KEYS="$(get_points_keys | paste -sd ',' -)"

echo
echo "=== Result ==="
echo "K6 exit code        : $K6_EXIT_CODE"
echo "Final version       : $(format_value "$FINAL_VERSION")"
echo "Final keys          : ${FINAL_KEYS:-<none>}"
echo "K6 summary          : $SUMMARY_PATH"
echo "K6 stdout log       : $STDOUT_PATH"
echo "K6 stderr log       : $STDERR_PATH"

if [[ "$VERSION_SWITCHED_AT" -gt 0 ]]; then
  echo "Switch delay        : $(( VERSION_SWITCHED_AT - TRIGGERED_AT ))s after batch trigger"
else
  echo "Version switched    : not detected during this run"
fi

if [[ "$K6_EXIT_CODE" -ne 0 ]]; then
  echo "k6 failed. Check $STDERR_PATH" >&2
  exit "$K6_EXIT_CODE"
fi
