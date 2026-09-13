package com.sporify.music_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sporify.music_backend.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByAzureSub(String azureSub);

	boolean existsByAzureSub(String azureSub);
}
