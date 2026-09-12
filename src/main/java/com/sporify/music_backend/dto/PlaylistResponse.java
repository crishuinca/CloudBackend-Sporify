package com.sporify.music_backend.dto;

import java.time.Instant;
import java.util.List;

import com.sporify.music_backend.domain.Playlist;
import com.sporify.music_backend.domain.PlaylistType;

public record PlaylistResponse(
		Long id,
		String name,
		PlaylistType type,
		Instant createdAt,
		List<PlaylistTrackResponse> tracks) {

	public static PlaylistResponse from(Playlist playlist, List<PlaylistTrackResponse> tracks) {
		return new PlaylistResponse(
				playlist.getId(),
				playlist.getName(),
				playlist.getType(),
				playlist.getCreatedAt(),
				tracks);
	}

	public static PlaylistResponse summary(Playlist playlist) {
		return from(playlist, List.of());
	}
}
