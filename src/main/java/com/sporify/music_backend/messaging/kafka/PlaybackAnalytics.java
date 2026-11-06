package com.sporify.music_backend.messaging.kafka;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

/**
 * Analítica de reproducciones. Al llegar a cinco del mismo usuario, publica un aviso.
 */
@Service
public class PlaybackAnalytics {

	static final int BURST_THRESHOLD = 5;

	private static final Logger log = LoggerFactory.getLogger(PlaybackAnalytics.class);

	private final KafkaEventPublisher publisher;
	private final Validator validator;
	private final ConcurrentMap<String, Integer> playsByUser = new ConcurrentHashMap<>();

	public PlaybackAnalytics(KafkaEventPublisher publisher, Validator validator) {
		this.publisher = publisher;
		this.validator = validator;
	}

	public void onPlayback(PlaybackEvent event) {
		validate(event);
		int plays = playsByUser.merge(event.userId(), 1, Integer::sum);
		log.info(
				"Playback correlationId={} userId={} trackId={} source={} plays={}",
				event.correlationId(),
				event.userId(),
				event.trackId(),
				event.source(),
				plays);
		if (plays == BURST_THRESHOLD) {
			publisher.publishAlert(new ActivityAlert(
					event.correlationId(),
					event.userId(),
					"BURST",
					plays,
					Instant.now()));
		}
	}

	private void validate(PlaybackEvent event) {
		Set<ConstraintViolation<PlaybackEvent>> violations = validator.validate(event);
		if (violations.isEmpty()) {
			return;
		}
		String detail = violations.stream()
				.map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
				.collect(Collectors.joining("; "));
		throw new ConstraintViolationException("Playback invalido: " + detail, violations);
	}
}
