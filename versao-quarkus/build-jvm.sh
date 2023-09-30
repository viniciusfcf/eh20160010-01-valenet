quarkus build 
docker build -f src/main/docker/Dockerfile.jvm -t quarkus/versao-quarkus-jvm .
# docker run -i --rm -p 8080:8080 quarkus/versao-quarkus-jvm