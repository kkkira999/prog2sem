FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

COPY gradlew ./
COPY gradle ./gradle
COPY settings.gradle ./
COPY build.gradle ./
COPY common ./common
COPY server ./server
COPY client ./client

RUN chmod +x gradlew
RUN GRADLE_USER_HOME=/tmp/gradle-home ./gradlew :server:installDist

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /app/.gradle-build/server/install/server ./server
COPY data.csv ./data.csv

EXPOSE 1234/udp

CMD ["./server/bin/server", "1234"]
