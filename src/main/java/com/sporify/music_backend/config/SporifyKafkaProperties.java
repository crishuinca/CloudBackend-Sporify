package com.sporify.music_backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Nombres de tópicos y consumer groups de Kafka.
 * El transporte (brokers, serializers, acks, linger, batch) está en application.properties.
 */
@ConfigurationProperties(prefix = "sporify.kafka")
public class SporifyKafkaProperties {

	private String playbackTopic = "sporify.playback.events";
	private String alertsTopic = "sporify.activity.alerts";
	private String playbackGroup = "sporify.playback.analytics";
	private String alertsGroup = "sporify.activity.alerts";

	public String getPlaybackTopic() {
		return playbackTopic;
	}

	public void setPlaybackTopic(String playbackTopic) {
		this.playbackTopic = playbackTopic;
	}

	public String getAlertsTopic() {
		return alertsTopic;
	}

	public void setAlertsTopic(String alertsTopic) {
		this.alertsTopic = alertsTopic;
	}

	public String getPlaybackGroup() {
		return playbackGroup;
	}

	public void setPlaybackGroup(String playbackGroup) {
		this.playbackGroup = playbackGroup;
	}

	public String getAlertsGroup() {
		return alertsGroup;
	}

	public void setAlertsGroup(String alertsGroup) {
		this.alertsGroup = alertsGroup;
	}
}
