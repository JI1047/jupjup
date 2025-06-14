#!/bin/bash

# === 설정 부분 ===
PEM_PATH="jupjup.pem"
EC2_USER="ec2-user"
EC2_HOST="13.209.202.27"
JAR_NAME="Integrated-login-0.0.1-SNAPSHOT.jar"
REMOTE_DIR="~"

# === 1. Spring Boot 빌드 ===
echo "🔧 Building Spring Boot JAR..."
./gradlew bootJar || { echo "❌ Build failed"; exit 1; }

# === 2. EC2로 JAR 전송 ===
echo "🚀 Uploading JAR to EC2..."
scp -i "$PEM_PATH" "build/libs/$JAR_NAME" $EC2_USER@$EC2_HOST:$REMOTE_DIR || { echo "❌ SCP failed"; exit 1; }

# === 3. Docker 이미지 재빌드 & 컨테이너 재시작 (EC2 내부 명령 실행) ===
echo "🔁 Rebuilding Docker image and restarting container on EC2..."
ssh -i "$PEM_PATH" $EC2_USER@$EC2_HOST << EOF
  docker stop \$(docker ps -q) || echo "No container to stop"
  docker rm \$(docker ps -aq) || echo "No container to remove"
  docker build -t my-backend-app .
  docker run -d -p 8080:8080 my-backend-app
EOF

echo "✅ 배포 완료! 서버 주소: http://$EC2_HOST:8080"
