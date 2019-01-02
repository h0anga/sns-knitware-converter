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

---

minikube config set memory 4096
minikube config set cpus 4
minikube delete
minikube start

---

https://technology.amis.nl/2018/04/19/15-minutes-to-get-a-kafka-cluster-running-on-kubernetes-and-start-producing-and-consuming-from-a-node-application/

kubectl apply -f configure/minikube-storageclasses.yml
kubectl apply -f 00-namespace.yml
kubectl apply -f ./zookeeper/
kubectl apply -f ./kafka/

//todo
Make kafka accessible from outside k8s:
kubectl apply -f ./outside-services

Add Yahoo Kafka manager:
kubectl apply -f ./yahoo-kafka-manager


To stop all oods and remove a namespace:
kubectl delete ns kafka


---

##Strimzi
cd strimzi-kafka-operator

###Create the namespace
Create a file “kafka-namespace.yaml” with the following content:


```---
apiVersion: v1
kind: Namespace
metadata:
  name: kafka```
kubectl apply -f kafka-namespace.yaml

###Apply the installation file:
curl -L https://github.com/strimzi/strimzi-kafka-operator/releases/download/0.9.0/strimzi-cluster-operator-0.9.0.yaml   | sed ‘s/namespace: .*/namespace: kafka/’ > strimzi-cluster-operator-0.9.0.yaml

kubectl -n kafka apply -f strimzi-cluster-operator-0.9.0.yaml

###Provision the Kafka cluster
kubectl apply -f examples/kafka/kafka-persistent.yaml -n kafka
