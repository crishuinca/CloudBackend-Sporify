package com.sporify.music_backend.messaging.kafka;

import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

/**
 * Deja en el log el aviso ya filtrado, para poder mostrarlo en la presentación.
 */
@Service
public class ActivityAlertLog {

	private static final Logger log = LoggerFactory.getLogger(ActivityAlertLog.class);

	private final Validator validator;

	public ActivityAlertLog(Validator validator) {
		this.validator = validator;
	}

	public void onAlert(ActivityAlert event) {
		Set<ConstraintViolation<ActivityAlert>> violations = validator.validate(event);
		if (!violations.isEmpty()) {
			String detail = violations.stream()
					.map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
					.collect(Collectors.joining("; "));
			throw new ConstraintViolationException("Aviso invalido: " + detail, violations);
		}
		log.info(
				"Alert correlationId={} userId={} kind={} playCount={}",
				event.correlationId(),
				event.userId(),
				event.kind(),
				event.playCount());
	}
}
