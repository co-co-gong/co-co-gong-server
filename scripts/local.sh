#!/bin/bash

./gradlew build
java -jar build/libs/*SNAPSHOT.jar
