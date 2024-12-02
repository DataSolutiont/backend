#! /bin/sh

docker compose -f ./cvservice//docker-compose.yaml up -d
docker compose -f ./auth/docker-compose.yaml up -d
docker compose -f ./gateway/docker-compose.yaml up -d
