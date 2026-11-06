package com.sporify.music_backend.messaging.kafka;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Aviso ya filtrado, por ejemplo una ráfaga de reproducciones.
 * Poco volumen, tópico sporify.activity.alerts.
 */
public record ActivityAlert(
		@NotBlank String correlationId,
		@NotBlank String userId,
		@NotBlank String kind,
		@NotNull @Positive Integer playCount,
		@NotNull Instant occurredAt) {
}
