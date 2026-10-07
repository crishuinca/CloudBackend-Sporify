package com.sporify.music_backend.messaging.event;

import java.time.Instant;

/**
 * Evento: notificación asíncrona al usuario (exchange topic).
 */
public record NotificationEvent(
		Long userId,
		String azureSub,
		String type,
		String message,
		Instant occurredAt) {
}
