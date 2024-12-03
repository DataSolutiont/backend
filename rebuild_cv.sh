#! /bin/sh

docker compose -f ./cvservice/docker-compose.yaml build cv-app
docker compose -f ./cvservice/docker-compose.yaml up -d --force-recreate 
