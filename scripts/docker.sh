#!/bin/bash

source .env
export JWT_SECRET_KEY=$(openssl rand -base64 32)

docker build --no-cache \
	--build-arg POSTGRES_HOST=${POSTGRES_HOST} \
	--build-arg POSTGRES_PORT=${POSTGRES_PORT} \
	--build-arg POSTGRES_USER=${POSTGRES_USER} \
	--build-arg POSTGRES_PASSWORD=${POSTGRES_PASSWORD} \
	--build-arg POSTGRES_DB=${POSTGRES_DB} \
	--build-arg GITHUB_CLIENT_ID=${GITHUB_CLIENT_ID} \
	--build-arg GITHUB_CLIENT_SECRET=${GITHUB_CLIENT_SECRET} \
	--build-arg JWT_SECRET_KEY=${JWT_SECRET_KEY} \
	-t test .
