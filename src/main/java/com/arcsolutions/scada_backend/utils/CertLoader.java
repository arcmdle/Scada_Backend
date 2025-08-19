package com.arcsolutions.scada_backend.utils;

import java.io.FileOutputStream;
import java.util.Base64;

public class CertLoader {
    public static String writeCertFromEnv(String envVarName, String outputPath) throws Exception {
        String base64Cert = System.getenv(envVarName);
        if (base64Cert == null || base64Cert.isEmpty()) {
            throw new IllegalStateException("Variable de entorno " + envVarName + " no está definida.");
        }

        byte[] decoded = Base64.getDecoder().decode(base64Cert);
        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            fos.write(decoded);
        }

        return outputPath;
    }
}
