package com.arcsolutions.scada_backend.infrastructure.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.router.HeaderValueRouter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MqttConfig {

    private static final String HOST = "tcp://localhost:1883"; // si algún día metes el backend en Docker, usa "tcp://mosquitto:1883"
    private static final String USER = "backend_user";
    private static final String PASS = "platinaL1";

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{HOST});
        options.setUserName(USER);
        options.setPassword(PASS.toCharArray());
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(30);
        options.setAutomaticReconnect(true);
        factory.setConnectionOptions(options);
        return factory;
    }

    // Outbound (publicación)
    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound(MqttPahoClientFactory factory) {
        MqttPahoMessageHandler messageHandler =
                new MqttPahoMessageHandler("scada_backend_outbound", factory);
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic("others");
        messageHandler.setDefaultRetained(false);
        messageHandler.setDefaultQos(1);
        messageHandler.setConverter(new DefaultPahoMessageConverter());
        messageHandler.setTopicExpressionString("headers['mqtt_topic']");
        return messageHandler;
    }

    @Bean
    public MessageChannel mqttInboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel sensorReadingChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel statusChannel() {
        return new DirectChannel();
    }

    @Bean
    @Router(inputChannel = "mqttInboundChannel")
    public HeaderValueRouter topicRouter() {
        HeaderValueRouter router = new HeaderValueRouter("mqtt_receivedTopic");
        router.setChannelMapping("devices/sensor1/reading", "sensorReadingChannel");
        router.setChannelMapping("devices/pump/status", "statusChannel");
        router.setChannelMapping("devices/valve/status", "statusChannel");
        return router;
    }


    @Bean
    public MessageProducer mqttInboundAdapter(MqttPahoClientFactory factory) {
        // IMPORTANTE: usar el constructor que recibe el factory (así aplica las credenciales)
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        "scada_backend_inbound",
                        factory,
                        "devices/pump/status",
                        "devices/valve/status",
                        "devices/+/reading"
                );
        adapter.setQos(1);
        adapter.setCompletionTimeout(5000);
        adapter.setAutoStartup(true);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setOutputChannel(mqttInboundChannel());
        return adapter;
    }
}
