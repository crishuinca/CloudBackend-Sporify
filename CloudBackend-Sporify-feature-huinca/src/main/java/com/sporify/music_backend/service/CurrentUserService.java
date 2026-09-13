package com.sporify.music_backend.service;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sporify.music_backend.domain.Playlist;
import com.sporify.music_backend.domain.PlaylistType;
import com.sporify.music_backend.domain.User;
import com.sporify.music_backend.repository.PlaylistRepository;
import com.sporify.music_backend.repository.UserRepository;

@Service
public class CurrentUserService {

	private final UserRepository userRepository;
	private final PlaylistRepository playlistRepository;

	public CurrentUserService(UserRepository userRepository, PlaylistRepository playlistRepository) {
		this.userRepository = userRepository;
		this.playlistRepository = playlistRepository;
	}

	@Transactional
	public User getOrCreate(Jwt jwt) {
		String azureSub = firstNonBlank(jwt.getClaimAsString("oid"), jwt.getSubject());
		return userRepository.findByAzureSub(azureSub).orElseGet(() -> createUser(jwt, azureSub));
	}

	private User createUser(Jwt jwt, String azureSub) {
		User user = new User();
		user.setAzureSub(azureSub);
		user.setDisplayName(firstNonBlank(
				jwt.getClaimAsString("name"),
				jwt.getClaimAsString("preferred_username"),
				"Usuario"));
		user.setAvatarUrl(jwt.getClaimAsString("picture"));

		Playlist likes = new Playlist();
		likes.setUser(user);
		likes.setName("Me gusta");
		likes.setType(PlaylistType.LIKES);
		user.getPlaylists().add(likes);

		return userRepository.save(user);
	}

	@Transactional
	public Playlist likesPlaylist(User user) {
		return playlistRepository.findByUserAndType(user, PlaylistType.LIKES).orElseGet(() -> {
			Playlist likes = new Playlist();
			likes.setUser(user);
			likes.setName("Me gusta");
			likes.setType(PlaylistType.LIKES);
			return playlistRepository.save(likes);
		});
	}

	private static String firstNonBlank(String... values) {
		if (values == null) {
			return null;
		}
		for (String value : values) {
			if (value != null && !value.isBlank()) {
				return value.trim();
			}
		}
		return null;
	}
}
