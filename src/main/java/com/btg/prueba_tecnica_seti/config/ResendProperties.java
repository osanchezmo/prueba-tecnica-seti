package com.btg.prueba_tecnica_seti.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "resend")
public record ResendProperties(
        String apiKey,
        String baseUrl,
        String from
) {
    public String resolvedBaseUrl() {
        if (baseUrl == null || baseUrl.isBlank()) {
            return "https://api.resend.com";
        }
        return baseUrl.trim();
    }

    public boolean isEnabled() {
        return apiKey != null && !apiKey.isBlank() && from != null && !from.isBlank();
    }
}

