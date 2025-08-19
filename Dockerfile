# 🏗️ Etapa 1: Build con Maven y JDK 21
FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 🚀 Etapa 2: Runtime con JRE 21
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiar el .jar generado
COPY --from=builder /app/target/*.jar app.jar

# Copiar el certificado MQTT si tu app lo necesita
COPY src/main/resources/certs/emqxsl-ca.crt /app/certs/emqxsl-ca.crt

# 🔓 Exponer el puerto HTTP/WebSocket
EXPOSE 3000

# 🧠 Usar la variable PORT si está definida, o 3000 por defecto
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-3000}"]
