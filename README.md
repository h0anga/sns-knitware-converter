This application *can* be run standalone, but is intended to be used as a Docker image from within Kubernetes.

##Local

1) Ensure you have minikube and kubectl installed.
2) Start minikube

3) You will need to expose a port from the host where kafka is running, so that
this application, running within a minikube pod, will be able to access it.
Its not too difficult:
  - First, establish the IP address that virtualbox has assigned to 
your minikube VM (its likely to be 192.168.99.1):
    ```
    ifconfig | grep vboxnet0 -A 2 | grep inet
    ```
  - Next, Ensure the hostServices and hostEndpoint yaml files in this project
  reflect what you require. Ensure the lines commented for the Host port
  and the minikube port are suitable (9092 for both would suit kafka!).
  - Once happy, run the 2 yaml files:
      ```
      kubectl create -f hostServices.yaml
      kubectl create -f hostEndpoint.yaml
      ```
  - Now, check the IP ("Cluster-IP) you've been assigned for your service:
      ```
      kubectl get services
      ```
  - You can access your host service (kafka!) using that IP and the port (9092).
  - Create the Environment variables the app needs within Kubernetes by running the following, 
    substituting in your values for the server and port:
      ```
        kubectl create configmap kafka-broker-config --from-literal=KAFKA_BROKER_SERVER=10.99.248.38 --from-literal=KAFKA_BROKER_PORT=9092
      ```  
3) Build the docker image using miniKube's docker:
    ```
    eval $(minikube docker-env)
    sbt docker:publishLocal
    ```
4) Create and deploy a k8s pod with the application running within:
    ```
    kubectl apply -f knitware-converter.yaml
    ```
5) Check the pod status:
    ```
    kubectl get pod sns-knitware-converter
    kubectl describe pod sns-knitware-converter
    ```

You could now try running the E2E tests!



##Other attempts
kubectl apply -f configure/minikube-storageclasses.yml 
kubectl apply -f 00-namespace.yml
kubectl apply -f rbac-namespace-default/ 

Start Zookeeper:
kubectl apply -f ./zookeeper/

Wait until the pods are up:
kubectl get pods -n kafka

Start kafka:
kubectl apply -f ./kafka/

Wait until the pods are up:
kubectl get pods -n kafka

//todo
Make kafka accessible from outside k8s:
kubectl apply -f ./outside-services

Add Yahoo Kafka manager:
kubectl apply -f ./yahoo-kafka-manager


To stop all pods and remove a namespace:
kubectl delete ns kafka


###Strimzi
cd strimzi-kafka-operator

####Create the namespace
Create a file "kafka-namespace.yaml" with the following content:

```

---
apiVersion: v1
kind: Namespace
metadata:
  name: kafka
```
kubectl apply -f kafka-namespace.yaml 

###Apply the installation file:
curl -L https://github.com/strimzi/strimzi-kafka-operator/releases/download/0.9.0/strimzi-cluster-operator-0.9.0.yaml   | sed 's/namespace: .*/namespace: kafka/' > strimzi-cluster-operator-0.9.0.yaml

kubectl -n kafka apply -f strimzi-cluster-operator-0.9.0.yaml 

###Provision the Kafka cluster
kubectl apply -f examples/kafka/kafka-persistent.yaml -n kafka

