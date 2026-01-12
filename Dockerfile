FROM gradle:8.5-jdk17 AS build

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY src ./src

RUN gradle build --no-daemon

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

RUN mkdir -p logs

ENV JAVA_OPTS="-Xmx512m -XX:+UseContainerSupport"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
