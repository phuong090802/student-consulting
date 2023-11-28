package com.ute.studentconsulting.firebase.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.ute.studentconsulting.exception.InternalServerErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class FireBaseConfig {
    @Value("${firebase.configuration-file}")
    private String configPath;

    @Bean
    public FirebaseApp firebaseApp() {
        FirebaseOptions options;
        try {
            options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(configPath).getInputStream()))
                    .build();
        } catch (IOException e) {
            throw new InternalServerErrorException("Lỗi server", e.getMessage(), 10012);
        }
        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }
}
