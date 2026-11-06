package com.sporify.music_backend.messaging.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class PlaybackAnalyticsTest {

	private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	void quintoPlayPublicaUnAviso() {
		RecordingPublisher publisher = new RecordingPublisher();
		PlaybackAnalytics analytics = new PlaybackAnalytics(publisher, validator);
		PlaybackEvent event = play("corr-1");

		for (int i = 0; i < 4; i++) {
			analytics.onPlayback(event);
		}
		assertTrue(publisher.alerts.isEmpty());

		analytics.onPlayback(event);

		assertEquals(1, publisher.alerts.size());
		ActivityAlert alert = publisher.alerts.get(0);
		assertEquals("corr-1", alert.correlationId());
		assertEquals("user-1", alert.userId());
		assertEquals("BURST", alert.kind());
		assertEquals(5, alert.playCount());
	}

	@Test
	void eventoIncompletoNoSeProcesa() {
		PlaybackAnalytics analytics = new PlaybackAnalytics(new RecordingPublisher(), validator);
		PlaybackEvent invalid = new PlaybackEvent("", "user-1", "track-1", "jamendo", Instant.now());

		assertThrows(ConstraintViolationException.class, () -> analytics.onPlayback(invalid));
	}

	private static PlaybackEvent play(String correlationId) {
		return new PlaybackEvent(correlationId, "user-1", "track-1", "jamendo", Instant.parse("2026-11-05T00:00:00Z"));
	}

	private static final class RecordingPublisher extends KafkaEventPublisher {

		private final List<ActivityAlert> alerts = new ArrayList<>();

		private RecordingPublisher() {
			super(null, null, null);
		}

		@Override
		public void publishAlert(ActivityAlert event) {
			alerts.add(event);
		}
	}
}
