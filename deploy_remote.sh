set -euo pipefail

APP_DIR="${APP_DIR:-/home/ubuntu/apps/jupjup-monitoring-backend}"
COMPOSE_FILE="${COMPOSE_FILE:-docker-compose.deploy.yml}"
APP_CONTAINER="${APP_CONTAINER:-integrated-login-app}"
APP_IMAGE="${APP_IMAGE:?APP_IMAGE must be set}"
HEALTHCHECK_URL="${DEPLOY_HEALTHCHECK_URL:-http://localhost:8080/actuator/health}"
HEALTHCHECK_RETRIES="${DEPLOY_HEALTHCHECK_RETRIES:-18}"
HEALTHCHECK_SLEEP_SECONDS="${DEPLOY_HEALTHCHECK_SLEEP_SECONDS:-5}"

cd "$APP_DIR"

cleanup_port_8080_conflicts() {
  local port_holders
  port_holders="$(sudo docker ps -a --filter publish=8080 --format '{{.Names}}' || true)"

  if [ -z "$port_holders" ]; then
    return 0
  fi

  echo "[stop port 8080 holders]"
  for container in $port_holders; do
    if [ "$container" = "$APP_CONTAINER" ]; then
      continue
    fi

    echo "$container"
    sudo docker rm -f "$container" 2>/dev/null || true
  done
}

echo "[stop existing app]"
sudo docker rm -f "$APP_CONTAINER" 2>/dev/null || true
cleanup_port_8080_conflicts

echo "[image] $APP_IMAGE"
sudo docker pull "$APP_IMAGE"

echo "[compose up] $COMPOSE_FILE"
if ! sudo APP_IMAGE="$APP_IMAGE" docker compose -f "$COMPOSE_FILE" up -d --no-build; then
  echo "[port 8080 status]"
  sudo docker ps -a --filter publish=8080 --format 'table {{.Names}}\t{{.Ports}}\t{{.Status}}' || true
  sudo ss -tulpn | grep ':8080' || true
  exit 1
fi

echo "[containers]"
sudo docker compose -f "$COMPOSE_FILE" ps

echo "[healthcheck] $HEALTHCHECK_URL"
for i in $(seq 1 "$HEALTHCHECK_RETRIES"); do
  if curl --fail --silent "$HEALTHCHECK_URL" > /dev/null; then
    break
  fi

  if [ "$i" -eq "$HEALTHCHECK_RETRIES" ]; then
    echo "[logs]"
    sudo docker logs --tail 200 "$APP_CONTAINER" || true
    exit 1
  fi

  sleep "$HEALTHCHECK_SLEEP_SECONDS"
done

echo "[metrics]"
curl --fail --silent "${HEALTHCHECK_URL%/health}/prometheus" | grep hikaricp_connections_max || true
