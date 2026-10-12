package com.sporify.music_backend.messaging;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sporify.music_backend.messaging.event.LikesChangedEvent;
import com.sporify.music_backend.messaging.event.NotificationEvent;
import com.sporify.music_backend.messaging.event.PlaylistTrackAddedEvent;
import com.sporify.music_backend.web.ApiException;

/**
 * Publica mensajes con FORCE_DLQ para demostrar NACK → DLQ en Management UI.
 */
@Service
public class RabbitDlqDemoService {

	public static final String FORCE_MARKER = "FORCE_DLQ";

	private final SporifyEventPublisher eventPublisher;

	public RabbitDlqDemoService(SporifyEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	/**
	 * @param target playlist | likes | notifications | all
	 * @return etiquetas de colas a las que se publicó
	 */
	public List<String> publishForceDlq(String target) {
		String key = target == null || target.isBlank() ? "playlist" : target.trim().toLowerCase(Locale.ROOT);
		Instant now = Instant.now();
		List<String> published = new ArrayList<>();

		switch (key) {
			case "playlist" -> {
				publishPlaylist(now);
				published.add("sporify.playlist.track-added → .dlq");
			}
			case "likes" -> {
				publishLikes(now);
				published.add("sporify.likes.changed → .dlq");
			}
			case "notifications" -> {
				publishNotifications(now);
				published.add("sporify.notifications → .dlq");
			}
			case "all" -> {
				publishPlaylist(now);
				publishLikes(now);
				publishNotifications(now);
				published.add("sporify.playlist.track-added → .dlq");
				published.add("sporify.likes.changed → .dlq");
				published.add("sporify.notifications → .dlq");
			}
			default -> throw new ApiException(
					HttpStatus.BAD_REQUEST,
					"target inválido. Usa: playlist, likes, notifications o all");
		}
		return published;
	}

	private void publishPlaylist(Instant now) {
		eventPublisher.publishTrackAdded(new PlaylistTrackAddedEvent(
				0L,
				"demo-dlq",
				0L,
				"demo-azure-sub",
				0L,
				"demo-ext",
				FORCE_MARKER + " pista demo",
				"Demo Artist",
				now));
	}

	private void publishLikes(Instant now) {
		eventPublisher.publishLikesChanged(new LikesChangedEvent(
				0L,
				"demo-azure-sub",
				"ADDED",
				0L,
				"demo-ext",
				FORCE_MARKER + " like demo",
				now));
	}

	private void publishNotifications(Instant now) {
		eventPublisher.publishNotification(new NotificationEvent(
				0L,
				"demo-azure-sub",
				"DEMO_DLQ",
				FORCE_MARKER + " notificación demo",
				now));
	}
}
