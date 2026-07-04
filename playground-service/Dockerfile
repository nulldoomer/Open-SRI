# ------------------------------ BUILD -----------------------------------------

FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

COPY . .

RUN chmod +x gradlew

RUN ./gradlew bootJar --no-daemon

# ------------------------------ RUNTIME ---------------------------------------

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/build/libs/playground-service-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]