FROM eclipse-temurin:17-jdk-alpine
RUN mkdir /app
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app/eh20160010-01-valenet.jar
EXPOSE 8081
EXPOSE 8080
RUN chown -R 1001:1001 /app
USER 1001
HEALTHCHECK CMD curl --fail http://localhost:8080 || exit 1
ENTRYPOINT java -jar /app/eh20160010-01-valenet.jar

