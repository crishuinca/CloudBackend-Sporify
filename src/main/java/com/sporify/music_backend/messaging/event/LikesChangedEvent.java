package com.sporify.music_backend.messaging.event;

import java.time.Instant;

/**
 * Evento: like agregado o quitado (exchange topic).
 */
public record LikesChangedEvent(
		Long userId,
		String azureSub,
		String action,
		Long trackId,
		String externalId,
		String title,
		Instant occurredAt) {
}
