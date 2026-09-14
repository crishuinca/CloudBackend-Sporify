package com.sporify.music_backend.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sporify.music_backend.dto.AddTrackRequest;
import com.sporify.music_backend.dto.CreatePlaylistRequest;
import com.sporify.music_backend.dto.PlaylistResponse;
import com.sporify.music_backend.dto.PlaylistTrackResponse;
import com.sporify.music_backend.dto.UpdatePlaylistRequest;
import com.sporify.music_backend.service.PlaylistService;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

	private final PlaylistService playlistService;

	public PlaylistController(PlaylistService playlistService) {
		this.playlistService = playlistService;
	}

	@GetMapping
	@PreAuthorize("hasAuthority('SCOPE_Music.Read')")
	public List<PlaylistResponse> list(@AuthenticationPrincipal Jwt jwt) {
		return playlistService.list(jwt);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('SCOPE_Music.Read')")
	public PlaylistResponse get(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
		return playlistService.get(jwt, id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAuthority('SCOPE_Music.Write')")
	public PlaylistResponse create(@AuthenticationPrincipal Jwt jwt, @RequestBody CreatePlaylistRequest request) {
		return playlistService.create(jwt, request);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('SCOPE_Music.Write')")
	public PlaylistResponse rename(
			@AuthenticationPrincipal Jwt jwt,
			@PathVariable Long id,
			@RequestBody UpdatePlaylistRequest request) {
		return playlistService.update(jwt, id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('SCOPE_Music.Write')")
	public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
		playlistService.delete(jwt, id);
	}

	@PostMapping("/{id}/tracks")
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAuthority('SCOPE_Music.Write')")
	public PlaylistTrackResponse addTrack(
			@AuthenticationPrincipal Jwt jwt,
			@PathVariable Long id,
			@RequestBody AddTrackRequest request) {
		return playlistService.addTrack(jwt, id, request);
	}

	@DeleteMapping("/{id}/tracks/{trackId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('SCOPE_Music.Write')")
	public void removeTrack(
			@AuthenticationPrincipal Jwt jwt,
			@PathVariable Long id,
			@PathVariable Long trackId) {
		playlistService.removeTrack(jwt, id, trackId);
	}
}
