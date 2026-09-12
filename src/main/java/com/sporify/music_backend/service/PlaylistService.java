package com.sporify.music_backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sporify.music_backend.domain.Playlist;
import com.sporify.music_backend.domain.PlaylistTrack;
import com.sporify.music_backend.domain.PlaylistType;
import com.sporify.music_backend.domain.TrackSource;
import com.sporify.music_backend.domain.User;
import com.sporify.music_backend.dto.AddTrackRequest;
import com.sporify.music_backend.dto.CreatePlaylistRequest;
import com.sporify.music_backend.dto.PlaylistResponse;
import com.sporify.music_backend.dto.PlaylistTrackResponse;
import com.sporify.music_backend.dto.UpdatePlaylistRequest;
import com.sporify.music_backend.repository.PlaylistRepository;
import com.sporify.music_backend.repository.PlaylistTrackRepository;
import com.sporify.music_backend.web.ApiException;

@Service
public class PlaylistService {

	private final CurrentUserService currentUserService;
	private final PlaylistRepository playlistRepository;
	private final PlaylistTrackRepository playlistTrackRepository;

	public PlaylistService(
			CurrentUserService currentUserService,
			PlaylistRepository playlistRepository,
			PlaylistTrackRepository playlistTrackRepository) {
		this.currentUserService = currentUserService;
		this.playlistRepository = playlistRepository;
		this.playlistTrackRepository = playlistTrackRepository;
	}

	@Transactional
	public List<PlaylistResponse> list(Jwt jwt) {
		User user = currentUserService.getOrCreate(jwt);
		return playlistRepository.findByUser(user).stream()
				.map(PlaylistResponse::summary)
				.toList();
	}

	@Transactional
	public PlaylistResponse get(Jwt jwt, Long playlistId) {
		return toDetail(ownedPlaylist(jwt, playlistId));
	}

	@Transactional
	public PlaylistResponse create(Jwt jwt, CreatePlaylistRequest request) {
		User user = currentUserService.getOrCreate(jwt);
		Playlist playlist = new Playlist();
		playlist.setUser(user);
		playlist.setName(requiredName(request == null ? null : request.name()));
		playlist.setType(PlaylistType.CUSTOM);
		return PlaylistResponse.summary(playlistRepository.save(playlist));
	}

	@Transactional
	public PlaylistResponse rename(Jwt jwt, Long playlistId, UpdatePlaylistRequest request) {
		Playlist playlist = ownedPlaylist(jwt, playlistId);
		assertCustom(playlist);
		playlist.setName(requiredName(request == null ? null : request.name()));
		return PlaylistResponse.summary(playlist);
	}

	@Transactional
	public void delete(Jwt jwt, Long playlistId) {
		Playlist playlist = ownedPlaylist(jwt, playlistId);
		assertCustom(playlist);
		playlistRepository.delete(playlist);
	}

	@Transactional
	public PlaylistResponse likes(Jwt jwt) {
		User user = currentUserService.getOrCreate(jwt);
		return toDetail(currentUserService.likesPlaylist(user));
	}

	@Transactional
	public PlaylistTrackResponse addToLikes(Jwt jwt, AddTrackRequest request) {
		User user = currentUserService.getOrCreate(jwt);
		return addTrack(currentUserService.likesPlaylist(user), request);
	}

	@Transactional
	public PlaylistTrackResponse addTrack(Jwt jwt, Long playlistId, AddTrackRequest request) {
		return addTrack(ownedPlaylist(jwt, playlistId), request);
	}

	@Transactional
	public void removeFromLikes(Jwt jwt, Long trackId) {
		User user = currentUserService.getOrCreate(jwt);
		removeTrack(currentUserService.likesPlaylist(user), trackId);
	}

	@Transactional
	public void removeTrack(Jwt jwt, Long playlistId, Long trackId) {
		removeTrack(ownedPlaylist(jwt, playlistId), trackId);
	}

	private PlaylistTrackResponse addTrack(Playlist playlist, AddTrackRequest request) {
		if (request == null) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "El cuerpo de la pista es obligatorio");
		}
		String externalId = required(request.externalId(), "externalId");
		String title = required(request.title(), "title");
		String artist = required(request.artist(), "artist");
		String streamUrl = required(request.streamUrl(), "streamUrl");

		if (playlistTrackRepository.existsByPlaylistAndExternalIdAndSource(playlist, externalId, TrackSource.JAMENDO)) {
			throw new ApiException(HttpStatus.CONFLICT, "La pista ya está en la playlist");
		}

		int nextPosition = java.util.Optional.ofNullable(playlistTrackRepository.findMaxPosition(playlist)).orElse(-1) + 1;
		PlaylistTrack track = new PlaylistTrack();
		track.setPlaylist(playlist);
		track.setExternalId(externalId);
		track.setSource(TrackSource.JAMENDO);
		track.setTitle(title);
		track.setArtist(artist);
		track.setStreamUrl(streamUrl);
		track.setPosition(nextPosition);
		playlist.getTracks().add(track);
		return PlaylistTrackResponse.from(playlistTrackRepository.save(track));
	}

	private void removeTrack(Playlist playlist, Long trackId) {
		PlaylistTrack track = playlistTrackRepository.findByIdAndPlaylist(trackId, playlist)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Pista no encontrada"));
		playlist.getTracks().remove(track);
		playlistTrackRepository.delete(track);
	}

	private Playlist ownedPlaylist(Jwt jwt, Long playlistId) {
		User user = currentUserService.getOrCreate(jwt);
		return playlistRepository.findByIdAndUser(playlistId, user)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Playlist no encontrada"));
	}

	private PlaylistResponse toDetail(Playlist playlist) {
		List<PlaylistTrackResponse> tracks = playlistTrackRepository
				.findByPlaylistOrderByPositionAsc(playlist)
				.stream()
				.map(PlaylistTrackResponse::from)
				.toList();
		return PlaylistResponse.from(playlist, tracks);
	}

	private static void assertCustom(Playlist playlist) {
		if (playlist.getType() != PlaylistType.CUSTOM) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "La playlist de Me gusta no se puede editar de esa forma");
		}
	}

	private static String requiredName(String name) {
		return required(name, "name");
	}

	private static String required(String value, String field) {
		if (value == null || value.isBlank()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "El campo " + field + " es obligatorio");
		}
		return value.trim();
	}
}
