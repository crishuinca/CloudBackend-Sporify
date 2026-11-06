package com.sporify.music_backend.messaging.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Transporte del tópico de avisos. El rastro lo deja {@link ActivityAlertLog}.
 */
@Component
public class ActivityAlertListener {

	private final ActivityAlertLog alertLog;

	public ActivityAlertListener(ActivityAlertLog alertLog) {
		this.alertLog = alertLog;
	}

	@KafkaListener(
			topics = "${sporify.kafka.alerts-topic}",
			groupId = "${sporify.kafka.alerts-group}")
	public void onAlert(ActivityAlert event) {
		alertLog.onAlert(event);
	}
}
