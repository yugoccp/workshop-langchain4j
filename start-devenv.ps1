docker build -t localhost/workshop-devenv  .
docker run -p 8080:8080 -v ./src:/workspace/src --name workshop-devenv --rm localhost/workshop-devenv
docker exec -it workshop-devenv bash