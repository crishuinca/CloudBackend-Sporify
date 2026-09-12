package com.sporify.music_backend.web;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sporify.music_backend.dto.AddTrackRequest;
import com.sporify.music_backend.dto.PlaylistResponse;
import com.sporify.music_backend.dto.PlaylistTrackResponse;
import com.sporify.music_backend.service.PlaylistService;

@RestController
@RequestMapping("/api/likes")
public class LikesController {

	private final PlaylistService playlistService;

	public LikesController(PlaylistService playlistService) {
		this.playlistService = playlistService;
	}

	@GetMapping
	@PreAuthorize("hasAuthority('SCOPE_Music.Read')")
	public PlaylistResponse likes(@AuthenticationPrincipal Jwt jwt) {
		return playlistService.likes(jwt);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAuthority('SCOPE_Music.Write')")
	public PlaylistTrackResponse add(@AuthenticationPrincipal Jwt jwt, @RequestBody AddTrackRequest request) {
		return playlistService.addToLikes(jwt, request);
	}

	@DeleteMapping("/{trackId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('SCOPE_Music.Write')")
	public void remove(@AuthenticationPrincipal Jwt jwt, @PathVariable Long trackId) {
		playlistService.removeFromLikes(jwt, trackId);
	}
}
