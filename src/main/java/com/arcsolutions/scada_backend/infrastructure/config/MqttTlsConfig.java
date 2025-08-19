package com.arcsolutions.scada_backend.infrastructure.config;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttTlsConfig {

    @Bean
    public SSLSocketFactory mqttSocketFactory() throws Exception {
        String base64Cert = System.getenv("MQTT_CA_CERT_BASE64");
        if (base64Cert == null || base64Cert.isEmpty()) {
            throw new IllegalStateException("MQTT_CA_CERT_BASE64 no está definida.");
        }

        byte[] decoded = Base64.getDecoder().decode(base64Cert);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate caCert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(decoded));

        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        ks.setCertificateEntry("emqx-ca", caCert);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);

        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, tmf.getTrustManagers(), null);
        return ctx.getSocketFactory();
    }
}
