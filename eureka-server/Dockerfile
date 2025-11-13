# syntax=docker/dockerfile:1

FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY . ./
RUN mvn -pl eureka-server -am -DskipTests package spring-boot:repackage \
    && JAR_PATH=$(find eureka-server/target -maxdepth 1 -name '*.jar' ! -name 'original-*.jar' -print -quit) \
    && cp "$JAR_PATH" eureka-server/target/app.jar

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /workspace/eureka-server/target/app.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","/app/app.jar"]
