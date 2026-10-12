package com.sporify.music_backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Nombres de colas, exchanges y routing keys centralizados .
 */
@ConfigurationProperties(prefix = "sporify.rabbit")
public class RabbitMqProperties {

	private String directExchange = "sporify.events.direct";
	private String topicExchange = "sporify.events.topic";
	private String dlxExchange = "sporify.dlx";

	private String playlistTrackAddedQueue = "sporify.playlist.track-added";
	private String playlistTrackAddedDlq = "sporify.playlist.track-added.dlq";
	private String playlistTrackAddedRoutingKey = "playlist.track.added";

	private String likesChangedQueue = "sporify.likes.changed";
	private String likesChangedDlq = "sporify.likes.changed.dlq";
	private String likesChangedRoutingKey = "likes.changed";

	private String notificationsQueue = "sporify.notifications";
	private String notificationsDlq = "sporify.notifications.dlq";
	private String notificationsRoutingKey = "notify.user";

	public String getDirectExchange() {
		return directExchange;
	}

	public void setDirectExchange(String directExchange) {
		this.directExchange = directExchange;
	}

	public String getTopicExchange() {
		return topicExchange;
	}

	public void setTopicExchange(String topicExchange) {
		this.topicExchange = topicExchange;
	}

	public String getDlxExchange() {
		return dlxExchange;
	}

	public void setDlxExchange(String dlxExchange) {
		this.dlxExchange = dlxExchange;
	}

	public String getPlaylistTrackAddedQueue() {
		return playlistTrackAddedQueue;
	}

	public void setPlaylistTrackAddedQueue(String playlistTrackAddedQueue) {
		this.playlistTrackAddedQueue = playlistTrackAddedQueue;
	}

	public String getPlaylistTrackAddedDlq() {
		return playlistTrackAddedDlq;
	}

	public void setPlaylistTrackAddedDlq(String playlistTrackAddedDlq) {
		this.playlistTrackAddedDlq = playlistTrackAddedDlq;
	}

	public String getPlaylistTrackAddedRoutingKey() {
		return playlistTrackAddedRoutingKey;
	}

	public void setPlaylistTrackAddedRoutingKey(String playlistTrackAddedRoutingKey) {
		this.playlistTrackAddedRoutingKey = playlistTrackAddedRoutingKey;
	}

	public String getLikesChangedQueue() {
		return likesChangedQueue;
	}

	public void setLikesChangedQueue(String likesChangedQueue) {
		this.likesChangedQueue = likesChangedQueue;
	}

	public String getLikesChangedDlq() {
		return likesChangedDlq;
	}

	public void setLikesChangedDlq(String likesChangedDlq) {
		this.likesChangedDlq = likesChangedDlq;
	}

	public String getLikesChangedRoutingKey() {
		return likesChangedRoutingKey;
	}

	public void setLikesChangedRoutingKey(String likesChangedRoutingKey) {
		this.likesChangedRoutingKey = likesChangedRoutingKey;
	}

	public String getNotificationsQueue() {
		return notificationsQueue;
	}

	public void setNotificationsQueue(String notificationsQueue) {
		this.notificationsQueue = notificationsQueue;
	}

	public String getNotificationsDlq() {
		return notificationsDlq;
	}

	public void setNotificationsDlq(String notificationsDlq) {
		this.notificationsDlq = notificationsDlq;
	}

	public String getNotificationsRoutingKey() {
		return notificationsRoutingKey;
	}

	public void setNotificationsRoutingKey(String notificationsRoutingKey) {
		this.notificationsRoutingKey = notificationsRoutingKey;
	}
}
