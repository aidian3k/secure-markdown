FROM openjdk:17-jdk-slim

COPY gradle/wrapper gradle/wrapper

COPY gradlew .

COPY build.gradle .
COPY settings.gradle .
COPY src ./src

RUN ./gradlew build

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "build/libs/secure-markdown-0.0.1-SNAPSHOT.jar"]