mvn clean package -DskipTests spring-boot:repackage
docker build -t valenet-spring .