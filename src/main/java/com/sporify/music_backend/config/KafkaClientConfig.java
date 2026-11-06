package com.sporify.music_backend.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Activa los nombres de tópicos y groups.
 * ProducerFactory y ConsumerFactory los arma Spring Boot con spring.kafka.*.
 */
@Configuration
@EnableConfigurationProperties(SporifyKafkaProperties.class)
public class KafkaClientConfig {
}
