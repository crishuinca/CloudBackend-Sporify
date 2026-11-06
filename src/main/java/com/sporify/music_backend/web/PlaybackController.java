package com.sporify.music_backend.web;

import java.time.Instant;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sporify.music_backend.dto.RecordPlaybackRequest;
import com.sporify.music_backend.messaging.kafka.KafkaEventPublisher;
import com.sporify.music_backend.messaging.kafka.PlaybackEvent;
import com.sporify.music_backend.service.CurrentUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/playbacks")
public class PlaybackController {

	private final CurrentUserService currentUserService;
	private final KafkaEventPublisher publisher;

	public PlaybackController(CurrentUserService currentUserService, KafkaEventPublisher publisher) {
		this.currentUserService = currentUserService;
		this.publisher = publisher;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.ACCEPTED)
	@PreAuthorize("hasAuthority('SCOPE_Music.Write')")
	public void record(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody RecordPlaybackRequest request) {
		String userId = currentUserService.getOrCreate(jwt).getAzureSub();
		publisher.publishPlayback(new PlaybackEvent(
				UUID.randomUUID().toString(),
				userId,
				request.trackId().trim(),
				request.source().trim(),
				Instant.now()));
	}
}
