#!/bin/bash

rm -r build

if ./gradlew clean build; then
	echo "Build success!"
else
	echo "Build failed..."
	exit 1
fi
java -jar build/libs/*SNAPSHOT.jar
