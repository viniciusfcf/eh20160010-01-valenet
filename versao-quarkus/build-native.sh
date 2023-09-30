quarkus build --native --no-tests -Dquarkus.native.container-build=true
docker build -f src/main/docker/Dockerfile.native-micro -t quarkus/versao-quarkus .
# docker run -i --rm -p 8080:8080 quarkus/versao-quarkus