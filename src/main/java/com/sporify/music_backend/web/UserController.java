package com.sporify.music_backend.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sporify.music_backend.dto.UserResponse;
import com.sporify.music_backend.service.CurrentUserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final CurrentUserService currentUserService;

	public UserController(CurrentUserService currentUserService) {
		this.currentUserService = currentUserService;
	}

	@GetMapping("/me")
	@PreAuthorize("hasAuthority('SCOPE_Music.Read')")
	public UserResponse me(@AuthenticationPrincipal Jwt jwt) {
		return UserResponse.from(currentUserService.getOrCreate(jwt));
	}
}
