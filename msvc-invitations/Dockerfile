# syntax=docker/dockerfile:1

FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY . ./
RUN mvn -pl msvc-invitations -am -DskipTests package spring-boot:repackage \
    && JAR_PATH=$(find msvc-invitations/target -maxdepth 1 -name '*.jar' ! -name 'original-*.jar' -print -quit) \
    && cp "$JAR_PATH" msvc-invitations/target/app.jar

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /workspace/msvc-invitations/target/app.jar app.jar
EXPOSE 8004
ENTRYPOINT ["java","-jar","/app/app.jar"]
