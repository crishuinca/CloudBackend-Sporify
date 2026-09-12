package com.sporify.music_backend.dto;

public record JamendoTrackResponse(
		String externalId,
		String title,
		String artist,
		String streamUrl,
		String imageUrl,
		Integer durationSeconds) {
}
