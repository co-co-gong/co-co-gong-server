#!/bin/bash

kubectl delete ns co-co-gong
kubectl create ns co-co-gong
kubectl apply -n co-co-gong -f k8s

# kubectl exec -it -n co-co-gong deploy/backend -- zsh
