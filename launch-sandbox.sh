#!/bin/bash
export CONDUCTR_IP=127.0.0.1
# sandbox init
# docker-machine start default
# eval $(docker-machine env default --shell=bash)
# export CONDUCTR_IP=$(docker-machine ip default)
# echo "Conductr ip: $CONDUCTR_IP"
docker rm -f $(docker ps -aq)
# see: https://bintray.com/typesafe/registry-for-subscribers-only/conductr%3Aconductr
#sandbox run 1.1.10 --feature visualization --nr-of-containers 3 -e DOCKER_HOST_IP=127.0.0.1
sandbox run 1.1.10 --feature visualization --nr-of-containers 3
#sandbox run 2.0.0-beta.2 --feature visualization --nr-of-containers 3 -e DOCKER_HOST_IP=$CONDUCTR_IP
open http://$CONDUCTR_IP:9999