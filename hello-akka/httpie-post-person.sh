#!/bin/bash
#export CONDUCTR_IP=$(docker-machine ip default)
export CONDUCTR_IP=127.0.0.1
export HELLOWORLD_ENDPOINT=web
http GET $CONDUCTR_IP:9000/$HELLOWORLD_ENDPOINT/helloworld
http POST $CONDUCTR_IP:9000/$HELLOWORLD_ENDPOINT/person name=John age:=35