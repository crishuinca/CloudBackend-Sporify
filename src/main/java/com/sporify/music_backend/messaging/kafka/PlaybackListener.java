package com.sporify.music_backend.messaging.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Transporte del tópico de reproducciones. La decisión está en {@link PlaybackAnalytics}.
 */
@Component
public class PlaybackListener {

	private final PlaybackAnalytics analytics;

	public PlaybackListener(PlaybackAnalytics analytics) {
		this.analytics = analytics;
	}

	@KafkaListener(
			topics = "${sporify.kafka.playback-topic}",
			groupId = "${sporify.kafka.playback-group}")
	public void onPlayback(PlaybackEvent event) {
		analytics.onPlayback(event);
	}
}
