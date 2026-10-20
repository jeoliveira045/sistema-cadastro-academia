#/usr/bin/bash

IMGNAME="sistema-usuario:1.0.0"

kubectl delete -k ./k8s/overlays/dev/
eval $(minikube docker-env)
docker rmi $IMGNAME
docker build . -t $IMGNAME
eval $(minikube docker-env --unset)
kubectl apply -k ./k8s/overlays/dev/
