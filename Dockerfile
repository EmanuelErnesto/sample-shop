FROM ubuntu:latest AS build

RUN apt-get update && apt-get install openjdk-17-jdk -y

COPY . .

RUN apt-get install maven -y

RUN mvn clean install -DskipTests -Dmaven.test.skip=true

FROM openjdk:17-jdk-slim

EXPOSE 8081

COPY --from=build /target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]