package com.sporify.music_backend.dto;

public record AddTrackRequest(
		String externalId,
		String title,
		String artist,
		String streamUrl,
		String imageUrl,
		Integer durationSeconds) {
}
