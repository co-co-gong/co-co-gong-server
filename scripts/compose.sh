#!/bin/bash

source .env
export JWT_SECRET_KEY=$(openssl rand -base64 32)

docker compose down -v
docker rmi co-co-gong-server-server --force

if docker compose build --no-cache; then
	echo "Build success!"
else
	echo "Build failed..."
	exit 1
fi
docker compose up -d
