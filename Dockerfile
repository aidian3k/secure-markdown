FROM gradle
WORKDIR /app
COPY gradle .
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY src/ .


ENTRYPOINT ["./gradlew bootRun"]