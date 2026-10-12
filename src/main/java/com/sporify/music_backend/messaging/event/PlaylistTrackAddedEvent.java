package com.sporify.music_backend.messaging.event;

import java.time.Instant;

/**
 * Evento: se agregó una pista a una playlist (exchange direct).
 */
public record PlaylistTrackAddedEvent(
		Long playlistId,
		String playlistName,
		Long userId,
		String azureSub,
		Long trackId,
		String externalId,
		String title,
		String artist,
		Instant occurredAt) {
}
