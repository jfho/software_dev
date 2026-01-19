#!/bin/bash
set -e

pushd dtu_pay
# End the docker
docker compose down
popd

# Test and compile service
pushd dtu_pay/token_manager_service
# Run tests
mvn clean test -B
# Create jar file
mvn clean package -B
popd

# Test and compile service
pushd dtu_pay/payment_service
# Run tests
mvn clean test -B
# Create jar file
mvn clean package -B
popd

# Test and compile service
pushd dtu_pay/account_service
# Run tests
mvn clean test -B
# Create jar file
mvn clean package  -B
popd

# Test and compile service
pushd dtu_pay/reporting_service
# Run tests
mvn clean test -B
# Create jar file
mvn clean package  -B
popd

pushd dtu_pay
# Create a new docker image if necessary.
docker compose build
# Restarts the container with the new image if necessary
docker compose up -d --remove-orphans
# The server stays running.
# To terminate the server run docker-compose down in the
# simple-rest directory
# clean up images
docker image prune -f 
popd

# Give the Web server a chance to finish start up
sleep 3

# Run End to End tests
#pushd dtu_pay/dtu_pay_client
#mvn clean test
#popd