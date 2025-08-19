# Etapa 1: Build con Maven y JDK 21
FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Runtime con JRE 21
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar


EXPOSE 3000
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]
