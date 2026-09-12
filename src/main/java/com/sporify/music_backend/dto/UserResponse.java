package com.sporify.music_backend.dto;

import java.time.Instant;

import com.sporify.music_backend.domain.User;

public record UserResponse(
		Long id,
		String azureSub,
		String displayName,
		String avatarUrl,
		Instant createdAt) {

	public static UserResponse from(User user) {
		return new UserResponse(
				user.getId(),
				user.getAzureSub(),
				user.getDisplayName(),
				user.getAvatarUrl(),
				user.getCreatedAt());
	}
}
