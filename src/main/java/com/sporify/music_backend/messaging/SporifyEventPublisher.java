package com.sporify.music_backend.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.sporify.music_backend.config.RabbitMqProperties;
import com.sporify.music_backend.messaging.event.LikesChangedEvent;
import com.sporify.music_backend.messaging.event.NotificationEvent;
import com.sporify.music_backend.messaging.event.PlaylistTrackAddedEvent;

/**
 * Productor: publica eventos de dominio vía RabbitTemplate.
 * Los nombres de exchange/routing key vienen de {@link RabbitMqProperties}.
 */
@Service
public class SporifyEventPublisher {

	private static final Logger log = LoggerFactory.getLogger(SporifyEventPublisher.class);

	private final RabbitTemplate rabbitTemplate;
	private final RabbitMqProperties props;

	public SporifyEventPublisher(RabbitTemplate rabbitTemplate, RabbitMqProperties props) {
		this.rabbitTemplate = rabbitTemplate;
		this.props = props;
	}

	public void publishTrackAdded(PlaylistTrackAddedEvent event) {
		publish(props.getDirectExchange(), props.getPlaylistTrackAddedRoutingKey(), event, "playlist.track-added");
	}

	public void publishLikesChanged(LikesChangedEvent event) {
		publish(props.getTopicExchange(), props.getLikesChangedRoutingKey(), event, "likes.changed");
	}

	public void publishNotification(NotificationEvent event) {
		publish(props.getTopicExchange(), props.getNotificationsRoutingKey(), event, "notifications");
	}

	private void publish(String exchange, String routingKey, Object payload, String label) {
		try {
			rabbitTemplate.convertAndSend(exchange, routingKey, payload);
			log.info("Rabbit publish OK [{}] exchange={} key={}", label, exchange, routingKey);
		}
		catch (Exception ex) {
			log.error("Rabbit publish FAIL [{}] exchange={} key={}: {}", label, exchange, routingKey, ex.getMessage());
		}
	}
}
