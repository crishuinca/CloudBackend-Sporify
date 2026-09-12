package com.sporify.music_backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "playlist_track")
@Getter
@Setter
@NoArgsConstructor
public class PlaylistTrack {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "playlist_id", nullable = false)
	private Playlist playlist;

	@Column(nullable = false, length = 64)
	private String externalId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TrackSource source;

	@Column(nullable = false, length = 200)
	private String title;

	@Column(nullable = false, length = 200)
	private String artist;

	@Column(nullable = false, length = 1024)
	private String streamUrl;

	@Column(nullable = false)
	private Integer position;
}
