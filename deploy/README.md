# Deploy info

## Prerequisites

Install:
- kubectl 
- helm

And configure kubectl to work with cluster.

## First time deploy

### nginx-ingress
Install nginx-ingress using 1-click-app from DigitalOcean or manually using helm:

```shell
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update
helm install nginx-ingress ingress-nginx/ingress-nginx --set controller.publishService.enabled=true
kubectl --namespace default get services -o wide -w nginx-ingress-ingress-nginx-controller
```

### cert-manager

Install cert manager using helm:

```shell
kubectl create namespace cert-manager
helm repo add jetstack https://charts.jetstack.io
helm repo update
helm install cert-manager jetstack/cert-manager --namespace cert-manager --version v1.2.0 --set installCRDs=true
kubectl apply -f deploy/production_issuer.yaml
```

Fix an [issue](https://github.com/cert-manager/cert-manager/issues/3238) with http-01 challenge from inside the pod
using [hairpin-proxy](https://github.com/compumike/hairpin-proxy) config:

```shell
kubectl apply -f deploy/hairpin_proxy.yaml
```
*Or get it latest version from [github](https://raw.githubusercontent.com/compumike/hairpin-proxy/v0.2.1/deploy.yml).*

### Deploy app

Now you can deploy the app:

```shell
IMAGE_VERSION=<some-next-version> kubectl apply -f deploy/medbook.yaml
```
