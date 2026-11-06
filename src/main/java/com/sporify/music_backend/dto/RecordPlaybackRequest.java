package com.sporify.music_backend.dto;

import jakarta.validation.constraints.NotBlank;

public record RecordPlaybackRequest(
		@NotBlank String trackId,
		@NotBlank String source) {
}
