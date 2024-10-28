#!/bin/bash

rm -r build

./gradlew clean build
java -jar build/libs/*SNAPSHOT.jar
