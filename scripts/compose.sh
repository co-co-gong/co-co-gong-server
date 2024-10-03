#!/bin/bash

docker compose down -v
docker rmi co-co-gong-server-server --force
docker compose up -d
