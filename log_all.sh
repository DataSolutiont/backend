#! /bin/sh

echo "\nAUTH APP\n"
docker logs auth-app

echo "\nCV APP\n"
docker logs cv-app

echo "\nGATEWAY\n"
docker logs api-gateway
