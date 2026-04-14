set -euo pipefail

APP_DIR="${APP_DIR:-/home/ubuntu/apps/jupjup-monitoring-backend}"
APP_SERVICE="${APP_SERVICE:-app}"
APP_CONTAINER="${APP_CONTAINER:-integrated-login-app}"
HEALTHCHECK_URL="${DEPLOY_HEALTHCHECK_URL:-http://localhost:8080/actuator/health}"
HEALTHCHECK_RETRIES="${DEPLOY_HEALTHCHECK_RETRIES:-18}"
HEALTHCHECK_SLEEP_SECONDS="${DEPLOY_HEALTHCHECK_SLEEP_SECONDS:-5}"

cd "$APP_DIR"

echo "[build]"
chmod +x ./gradlew
./gradlew clean bootJar -x test

echo "[compose up $APP_SERVICE]"
sudo docker compose up -d --build "$APP_SERVICE"

echo "[containers]"
sudo docker compose ps

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
