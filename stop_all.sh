#! /bin/sh

docker-compose -f ./auth/docker-compose.yaml down
docker-compose -f ./cvservice//docker-compose.yaml down
docker-compose -f ./gateway/docker-compose.yaml down

