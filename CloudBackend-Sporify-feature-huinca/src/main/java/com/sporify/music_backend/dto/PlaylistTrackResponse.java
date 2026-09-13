package com.sporify.music_backend.dto;

import com.sporify.music_backend.domain.PlaylistTrack;
import com.sporify.music_backend.domain.TrackSource;

public record PlaylistTrackResponse(
		Long id,
		String externalId,
		TrackSource source,
		String title,
		String artist,
		String streamUrl,
		String imageUrl,
		Integer durationSeconds,
		Integer position) {

	public static PlaylistTrackResponse from(PlaylistTrack track) {
		return new PlaylistTrackResponse(
				track.getId(),
				track.getExternalId(),
				track.getSource(),
				track.getTitle(),
				track.getArtist(),
				track.getStreamUrl(),
				track.getImageUrl(),
				track.getDurationSeconds(),
				track.getPosition());
	}
}
