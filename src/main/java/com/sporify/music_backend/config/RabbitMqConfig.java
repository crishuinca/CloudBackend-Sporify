package com.sporify.music_backend.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Beans Queue / Exchange / Binding separados de la lógica de negocio .
 * Tres colas de dominio + tres DLQ; exchanges direct, topic y DLX.
 */
@Configuration
@EnableConfigurationProperties(RabbitMqProperties.class)
public class RabbitMqConfig {

	public static final String PLAYLIST_TRACK_ADDED_QUEUE = "playlistTrackAddedQueue";
	public static final String PLAYLIST_TRACK_ADDED_DLQ = "playlistTrackAddedDlq";
	public static final String LIKES_CHANGED_QUEUE = "likesChangedQueue";
	public static final String LIKES_CHANGED_DLQ = "likesChangedDlq";
	public static final String NOTIFICATIONS_QUEUE = "notificationsQueue";
	public static final String NOTIFICATIONS_DLQ = "notificationsDlq";

	private final RabbitMqProperties props;

	public RabbitMqConfig(RabbitMqProperties props) {
		this.props = props;
	}

	@Bean
	MessageConverter rabbitMessageConverter() {
		// Spring Boot 4 / AMQP 4 → Jackson 3 (Jackson2JsonMessageConverter está deprecado)
		return new JacksonJsonMessageConverter();
	}

	@Bean
	RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter rabbitMessageConverter) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(rabbitMessageConverter);
		return template;
	}

	@Bean
	DirectExchange sporifyDirectExchange() {
		return new DirectExchange(props.getDirectExchange(), true, false);
	}

	@Bean
	TopicExchange sporifyTopicExchange() {
		return new TopicExchange(props.getTopicExchange(), true, false);
	}

	@Bean
	DirectExchange sporifyDlxExchange() {
		return new DirectExchange(props.getDlxExchange(), true, false);
	}

	// --- Caso 1: canción agregada a playlist (direct) ---

	@Bean(name = PLAYLIST_TRACK_ADDED_DLQ)
	Queue playlistTrackAddedDlq() {
		return QueueBuilder.durable(props.getPlaylistTrackAddedDlq()).build();
	}

	@Bean(name = PLAYLIST_TRACK_ADDED_QUEUE)
	Queue playlistTrackAddedQueue() {
		return QueueBuilder.durable(props.getPlaylistTrackAddedQueue())
				.withArgument("x-dead-letter-exchange", props.getDlxExchange())
				.withArgument("x-dead-letter-routing-key", props.getPlaylistTrackAddedDlq())
				.build();
	}

	@Bean
	Binding playlistTrackAddedBinding(
			@Qualifier(PLAYLIST_TRACK_ADDED_QUEUE) Queue playlistTrackAddedQueue,
			DirectExchange sporifyDirectExchange) {
		return BindingBuilder.bind(playlistTrackAddedQueue)
				.to(sporifyDirectExchange)
				.with(props.getPlaylistTrackAddedRoutingKey());
	}

	@Bean
	Binding playlistTrackAddedDlqBinding(
			@Qualifier(PLAYLIST_TRACK_ADDED_DLQ) Queue playlistTrackAddedDlq,
			DirectExchange sporifyDlxExchange) {
		return BindingBuilder.bind(playlistTrackAddedDlq)
				.to(sporifyDlxExchange)
				.with(props.getPlaylistTrackAddedDlq());
	}

	// --- Caso 2: like (topic) ---

	@Bean(name = LIKES_CHANGED_DLQ)
	Queue likesChangedDlq() {
		return QueueBuilder.durable(props.getLikesChangedDlq()).build();
	}

	@Bean(name = LIKES_CHANGED_QUEUE)
	Queue likesChangedQueue() {
		return QueueBuilder.durable(props.getLikesChangedQueue())
				.withArgument("x-dead-letter-exchange", props.getDlxExchange())
				.withArgument("x-dead-letter-routing-key", props.getLikesChangedDlq())
				.build();
	}

	@Bean
	Binding likesChangedBinding(
			@Qualifier(LIKES_CHANGED_QUEUE) Queue likesChangedQueue,
			TopicExchange sporifyTopicExchange) {
		return BindingBuilder.bind(likesChangedQueue)
				.to(sporifyTopicExchange)
				.with(props.getLikesChangedRoutingKey());
	}

	@Bean
	Binding likesChangedDlqBinding(
			@Qualifier(LIKES_CHANGED_DLQ) Queue likesChangedDlq,
			DirectExchange sporifyDlxExchange) {
		return BindingBuilder.bind(likesChangedDlq)
				.to(sporifyDlxExchange)
				.with(props.getLikesChangedDlq());
	}

	// --- Caso 3: notificaciones (topic) ---

	@Bean(name = NOTIFICATIONS_DLQ)
	Queue notificationsDlq() {
		return QueueBuilder.durable(props.getNotificationsDlq()).build();
	}

	@Bean(name = NOTIFICATIONS_QUEUE)
	Queue notificationsQueue() {
		return QueueBuilder.durable(props.getNotificationsQueue())
				.withArgument("x-dead-letter-exchange", props.getDlxExchange())
				.withArgument("x-dead-letter-routing-key", props.getNotificationsDlq())
				.build();
	}

	@Bean
	Binding notificationsBinding(
			@Qualifier(NOTIFICATIONS_QUEUE) Queue notificationsQueue,
			TopicExchange sporifyTopicExchange) {
		return BindingBuilder.bind(notificationsQueue)
				.to(sporifyTopicExchange)
				.with(props.getNotificationsRoutingKey());
	}

	@Bean
	Binding notificationsDlqBinding(
			@Qualifier(NOTIFICATIONS_DLQ) Queue notificationsDlq,
			DirectExchange sporifyDlxExchange) {
		return BindingBuilder.bind(notificationsDlq)
				.to(sporifyDlxExchange)
				.with(props.getNotificationsDlq());
	}
}
