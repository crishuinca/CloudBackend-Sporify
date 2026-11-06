package com.sporify.music_backend.messaging.kafka;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.sporify.music_backend.config.SporifyKafkaProperties;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

/**
 * Productor Kafka. La clave es el usuario, así que sus eventos caen en la misma partición.
 */
@Service
public class KafkaEventPublisher {

	public static final String CORRELATION_HEADER = "correlationId";

	private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisher.class);

	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final SporifyKafkaProperties props;
	private final Validator validator;

	public KafkaEventPublisher(
			KafkaTemplate<String, Object> kafkaTemplate,
			SporifyKafkaProperties props,
			Validator validator) {
		this.kafkaTemplate = kafkaTemplate;
		this.props = props;
		this.validator = validator;
	}

	public void publishPlayback(PlaybackEvent event) {
		validate(event);
		send(props.getPlaybackTopic(), event.userId(), event, event.correlationId());
	}

	public void publishAlert(ActivityAlert event) {
		validate(event);
		send(props.getAlertsTopic(), event.userId(), event, event.correlationId());
	}

	private void send(String topic, String key, Object payload, String correlationId) {
		try {
			ProducerRecord<String, Object> record = new ProducerRecord<>(topic, key, payload);
			if (correlationId != null) {
				record.headers().add(CORRELATION_HEADER, correlationId.getBytes(StandardCharsets.UTF_8));
			}
			kafkaTemplate.send(record);
			log.info("Kafka publish OK topic={} key={} correlationId={}", topic, key, correlationId);
		}
		catch (RuntimeException ex) {
			log.error("Kafka publish FAIL topic={} key={} correlationId={}: {}",
					topic, key, correlationId, ex.getMessage());
			throw ex;
		}
	}

	private void validate(Object event) {
		Set<ConstraintViolation<Object>> violations = validator.validate(event);
		if (violations.isEmpty()) {
			return;
		}
		String detail = violations.stream()
				.map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
				.collect(Collectors.joining("; "));
		log.warn("Kafka payload invalido correlationId={}: {}", correlationIdOf(event), detail);
		throw new ConstraintViolationException(violations);
	}

	private static String correlationIdOf(Object event) {
		if (event instanceof PlaybackEvent playback) {
			return playback.correlationId();
		}
		if (event instanceof ActivityAlert alert) {
			return alert.correlationId();
		}
		return "sin-id";
	}
}
