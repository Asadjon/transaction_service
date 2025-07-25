FROM gradle:8.14.3-jdk21 AS build
WORKDIR /home/app
COPY --chown=gradle:gradle . .
RUN gradle clean build -x test

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /home/app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8083
