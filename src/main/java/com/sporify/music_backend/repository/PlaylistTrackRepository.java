package com.sporify.music_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sporify.music_backend.domain.Playlist;
import com.sporify.music_backend.domain.PlaylistTrack;
import com.sporify.music_backend.domain.TrackSource;

public interface PlaylistTrackRepository extends JpaRepository<PlaylistTrack, Long> {

	List<PlaylistTrack> findByPlaylistOrderByPositionAsc(Playlist playlist);

	Optional<PlaylistTrack> findByIdAndPlaylist(Long id, Playlist playlist);

	boolean existsByPlaylistAndExternalIdAndSource(Playlist playlist, String externalId, TrackSource source);
}
