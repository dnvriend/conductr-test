#!/bin/bash
export CONDUCTR_IP=$(docker-machine ip default)
sbt "conduct unload --ip $CONDUCTR_IP conductr-test"