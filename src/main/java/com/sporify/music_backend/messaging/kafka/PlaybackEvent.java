package com.sporify.music_backend.messaging.kafka;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Una reproducción. Mucho volumen, clave el usuario, tópico sporify.playback.events.
 */
public record PlaybackEvent(
		@NotBlank String correlationId,
		@NotBlank String userId,
		@NotBlank String trackId,
		@NotBlank String source,
		@NotNull Instant occurredAt) {
}
