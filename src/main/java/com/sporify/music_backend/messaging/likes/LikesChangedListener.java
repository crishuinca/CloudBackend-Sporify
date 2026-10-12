package com.sporify.music_backend.messaging.likes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.sporify.music_backend.messaging.event.LikesChangedEvent;

/**
 * Consumidor del dominio likes: like agregado o quitado.
 * ACK manual; error no recuperable → NACK sin requeue (DLQ).
 */
@Component
public class LikesChangedListener {

	private static final Logger log = LoggerFactory.getLogger(LikesChangedListener.class);

	@RabbitListener(queues = "${sporify.rabbit.likes-changed-queue}")
	public void onLikesChanged(
			LikesChangedEvent event,
			Channel channel,
			@Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws Exception {
		try {
			if (event == null || event.userId() == null || event.action() == null || event.action().isBlank()) {
				throw new IllegalArgumentException("Evento likes.changed inválido o incompleto");
			}
			if (event.title() != null && event.title().contains("FORCE_DLQ")) {
				throw new IllegalStateException("Fallo forzado para demostrar DLQ: " + event.title());
			}

			log.info(
					"Consumido likes.changed OK action={} userId={} trackId={} title='{}'",
					event.action(),
					event.userId(),
					event.trackId(),
					event.title());
			channel.basicAck(deliveryTag, false);
		}
		catch (Exception ex) {
			log.error(
					"Error en likes.changed → NACK sin requeue (DLQ). deliveryTag={} cause={}",
					deliveryTag,
					ex.getMessage());
			channel.basicNack(deliveryTag, false, false);
		}
	}
}
