# SCADA Backend

Backend del sistema SCADA desarrollado en Java con Spring Boot.  
Este servicio se encarga de recibir datos desde el ESP32 vía MQTT, procesarlos, y controlar automáticamente los
dispositivos conectados (bomba y válvula).

---

## 🚀 Características

- Lectura de datos desde el ESP32 (distancia, estado).
- Control automático de bomba y válvula según lógica definida.
- Publicación de estados vía MQTT.
- Preparado para integración con control manual desde el frontend.

---

## 📦 Requisitos

- Java 17 o superior
- Maven 3.8+
- Broker MQTT (ej. Mosquitto)
- Git

---

## 📥 Instalación

1. Clona el repositorio:
   ```bash
   git clone https://github.com/tu-usuario/scada_backend.git
   cd scada_backend
2. Compila:

```bash 
mvn clean install
```

3. Ejecuta:

```bash 
mvn spring-boot:run
```

## ⚙️ Configuración

Edita el archivo application.yml o application.properties para configurar:

    Credenciales MQTT

    Tópicos utilizados

    Parámetros de control automático

## 📡 MQTT

El backend se comunica con los siguientes tópicos:
Tipo Tópico Descripción
Lectura devices/sensor1/reading Recibe distancia desde ESP32
Comando devices/pump/command Recibe comando manual para bomba
Comando devices/valve/command Recibe comando manual para válvula
Estado devices/pump/status Publica estado actual de la bomba
Estado devices/valve/status Publica estado actual de la válvula

## 📡 Tópicos MQTT utilizados

| Tipo    | Tópico                  | Descripción                    |
|---------|-------------------------|--------------------------------|
| Lectura | devices/sensor1/reading | Datos de distancia desde ESP32 |
| Comando | devices/pump/command    | Encender/apagar bomba          |
| Comando | devices/valve/command   | Abrir/cerrar válvula           |
| Estado  | devices/pump/status     | Estado actual de la bomba      |
| Estado  | devices/valve/status    | Estado actual de la válvula    |

## 🧪 Pruebas

Puedes simular el ESP32 usando un script Python o herramientas como MQTT Explorer para enviar mensajes de prueba.

## 📄 Licencia

Este proyecto está bajo la licencia MIT. Puedes usarlo, modificarlo y distribuirlo libremente.
