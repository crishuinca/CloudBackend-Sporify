package com.sporify.music_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sporify.music_backend.domain.Playlist;
import com.sporify.music_backend.domain.PlaylistType;
import com.sporify.music_backend.domain.User;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

	List<Playlist> findByUser(User user);

	Optional<Playlist> findByUserAndType(User user, PlaylistType type);

	Optional<Playlist> findByIdAndUser(Long id, User user);
}
