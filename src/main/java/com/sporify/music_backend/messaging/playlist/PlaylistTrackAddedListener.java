package com.sporify.music_backend.messaging.playlist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.sporify.music_backend.messaging.event.PlaylistTrackAddedEvent;

/**
 * Consumidor del dominio playlist: pista agregada.
 * ACK manual; error no recuperable → NACK sin requeue (va a DLQ vía x-dead-letter).
 */
@Component
public class PlaylistTrackAddedListener {

	private static final Logger log = LoggerFactory.getLogger(PlaylistTrackAddedListener.class);

	@RabbitListener(queues = "${sporify.rabbit.playlist-track-added-queue}")
	public void onTrackAdded(
			PlaylistTrackAddedEvent event,
			Channel channel,
			@Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws Exception {
		try {
			if (event == null || event.trackId() == null) {
				throw new IllegalArgumentException("Evento playlist.track-added inválido o incompleto");
			}
			// Demo DLQ: título especial fuerza fallo controlado
			if (event.title() != null && event.title().contains("FORCE_DLQ")) {
				throw new IllegalStateException("Fallo forzado para demostrar DLQ: " + event.title());
			}

			log.info(
					"Consumido playlist.track-added OK playlistId={} trackId={} title='{}' userId={}",
					event.playlistId(),
					event.trackId(),
					event.title(),
					event.userId());
			channel.basicAck(deliveryTag, false);
		}
		catch (Exception ex) {
			log.error(
					"Error en playlist.track-added → NACK sin requeue (DLQ). deliveryTag={} cause={}",
					deliveryTag,
					ex.getMessage());
			channel.basicNack(deliveryTag, false, false);
		}
	}
}
