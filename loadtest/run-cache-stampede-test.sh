#!/usr/bin/env bash

set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
REDIS_CONTAINER="${REDIS_CONTAINER:-integrated-login-redis}"
APP_CONTAINER="${APP_CONTAINER:-jubjub-backend}"
K6_SCRIPT="${K6_SCRIPT:-loadtest/map-main-cache-spike.js}"
VUS="${VUS:-50}"
MAX_DURATION="${MAX_DURATION:-30s}"

SOURCE_PATH="${BASH_SOURCE[0]:-}"
if [[ -n "$SOURCE_PATH" && -f "$SOURCE_PATH" ]]; then
  SCRIPT_DIR="$(cd "$(dirname "$SOURCE_PATH")" && pwd)"
  REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
elif [[ -d "$(pwd)/loadtest" ]]; then
  REPO_ROOT="$(pwd)"
  SCRIPT_DIR="$REPO_ROOT/loadtest"
else
  echo "Unable to determine repository root. Run from the repo root or execute the script file directly." >&2
  exit 1
fi

LOG_DIR="${LOG_DIR:-$REPO_ROOT/loadtest}"
if [[ ! -d "$LOG_DIR" || ! -w "$LOG_DIR" ]]; then
  LOG_DIR="/tmp/jupjup-monitoring-loadtest"
  mkdir -p "$LOG_DIR"
fi

STATUS_PATH="$LOG_DIR/cache-stampede.status.log"
STDOUT_PATH="$LOG_DIR/cache-stampede.k6.out.log"
STDERR_PATH="$LOG_DIR/cache-stampede.k6.err.log"
SUMMARY_PATH="$LOG_DIR/cache-stampede.summary.json"
RESPONSE_PATH="$LOG_DIR/cache-stampede.clear-cache.response.txt"

rm -f "$STATUS_PATH" "$STDOUT_PATH" "$STDERR_PATH" "$SUMMARY_PATH" "$RESPONSE_PATH"

require_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Required command not found: $1" >&2
    exit 1
  fi
}

log_status() {
  local message="$1"
  echo "$message" | tee -a "$STATUS_PATH"
}

get_points_keys() {
  docker exec "$REDIS_CONTAINER" redis-cli KEYS "pointsMain*" | tr -d '\r'
}

count_db_selects_since() {
  local since_value="$1"
  docker logs --since "$since_value" "$APP_CONTAINER" 2>&1 \
    | grep -c 'sql=select distinct p1_0.id' || true
}

print_recent_db_selects_since() {
  local since_value="$1"
  docker logs --since "$since_value" "$APP_CONTAINER" 2>&1 \
    | grep 'sql=select distinct p1_0.id' || true
}

start_k6() {
  if command -v k6 >/dev/null 2>&1; then
    (
      cd "$REPO_ROOT"
      BASE_URL="$BASE_URL" VUS="$VUS" MAX_DURATION="$MAX_DURATION" \
        k6 run --summary-export "$SUMMARY_PATH" "$K6_SCRIPT"
    ) >"$STDOUT_PATH" 2>"$STDERR_PATH" &
    echo $!
    return
  fi

  require_command docker
  (
    cd "$REPO_ROOT"
    docker run --rm --network host \
      -e BASE_URL="$BASE_URL" \
      -e VUS="$VUS" \
      -e MAX_DURATION="$MAX_DURATION" \
      -v "$REPO_ROOT:/work" \
      -v "$LOG_DIR:/results" \
      -w /work \
      grafana/k6 run --summary-export /results/$(basename "$SUMMARY_PATH") "$K6_SCRIPT"
  ) >"$STDOUT_PATH" 2>"$STDERR_PATH" &
  echo $!
}

require_command curl
require_command docker

if [[ ! -f "$REPO_ROOT/$K6_SCRIPT" ]]; then
  echo "K6 script not found: $REPO_ROOT/$K6_SCRIPT" >&2
  exit 1
fi

BEFORE_KEYS="$(get_points_keys | paste -sd ',' -)"
SINCE_UTC="$(date -u +"%Y-%m-%dT%H:%M:%SZ")"

log_status "=== Cache Stampede Test ==="
log_status "Base URL            : $BASE_URL"
log_status "K6 script           : $REPO_ROOT/$K6_SCRIPT"
log_status "Concurrent VUs      : $VUS"
log_status "Max duration        : $MAX_DURATION"
log_status "App container       : $APP_CONTAINER"
log_status "Redis container     : $REDIS_CONTAINER"
log_status "Redis keys before   : ${BEFORE_KEYS:-<none>}"
log_status "Log baseline (UTC)  : $SINCE_UTC"
log_status ""

curl --fail --silent --show-error "$BASE_URL/api/map/test-clear-cache" | tee "$RESPONSE_PATH" >/dev/null
AFTER_CLEAR_KEYS="$(get_points_keys | paste -sd ',' -)"
log_status "Clear cache response : $(cat "$RESPONSE_PATH")"
log_status "Redis keys after clr : ${AFTER_CLEAR_KEYS:-<none>}"

K6_PID="$(start_k6)"
wait "$K6_PID"
K6_EXIT_CODE="$?"

AFTER_TEST_KEYS="$(get_points_keys | paste -sd ',' -)"
DB_SELECT_COUNT="$(count_db_selects_since "$SINCE_UTC")"

log_status ""
log_status "=== Result ==="
log_status "K6 exit code        : $K6_EXIT_CODE"
log_status "Redis keys after tst: ${AFTER_TEST_KEYS:-<none>}"
log_status "DB select count     : $DB_SELECT_COUNT"
log_status "K6 summary          : $SUMMARY_PATH"
log_status "K6 stdout log       : $STDOUT_PATH"
log_status "K6 stderr log       : $STDERR_PATH"

log_status ""
log_status "Recent DB selects   :"
print_recent_db_selects_since "$SINCE_UTC" | tee -a "$STATUS_PATH"

if [[ -f "$STDOUT_PATH" ]]; then
  log_status ""
  log_status "Recent k6 stdout    :"
  tail -n 20 "$STDOUT_PATH" | tee -a "$STATUS_PATH"
fi

if [[ -s "$STDERR_PATH" ]]; then
  log_status ""
  log_status "Recent k6 stderr    :"
  tail -n 20 "$STDERR_PATH" | tee -a "$STATUS_PATH"
fi

if [[ "$K6_EXIT_CODE" -ne 0 ]]; then
  echo "k6 failed. Check $STDERR_PATH" >&2
  exit "$K6_EXIT_CODE"
fi
