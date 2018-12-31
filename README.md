This application *can* be run standalone, but is intended to be used as a Docker image from within Kubernetes.

##Local

1) Ensure you have minikube and kubectl installed.
2) Start minikube
3) Build the docker image using miniKube's docker:
```
eval $(minikube docker-env)
sbt docker:publishLocal
```
4) Create and deploy a k8s pod with the application running within:
kubectl apply -f knitware-converter.yaml
5) Check the pod status:
```
kubectl get pod sns-knitware-converter
kubectl describe pod sns-knitware-converter
```

You could now try running the E2E tests!
