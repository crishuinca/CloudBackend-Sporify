package com.sporify.music_backend.web;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sporify.music_backend.dto.JamendoTrackResponse;
import com.sporify.music_backend.service.JamendoService;

@RestController
@RequestMapping("/api/tracks")
public class TrackSearchController {

	private final JamendoService jamendoService;

	public TrackSearchController(JamendoService jamendoService) {
		this.jamendoService = jamendoService;
	}

	@GetMapping("/search")
	@PreAuthorize("hasAuthority('SCOPE_Music.Read')")
	public List<JamendoTrackResponse> search(
			@RequestParam String q,
			@RequestParam(defaultValue = "20") int limit) {
		return jamendoService.search(q, limit);
	}
}
