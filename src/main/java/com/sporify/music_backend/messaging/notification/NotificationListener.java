package com.sporify.music_backend.messaging.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.sporify.music_backend.messaging.event.NotificationEvent;

/**
 * Consumidor del dominio notificaciones.
 * ACK manual; error no recuperable → NACK sin requeue (DLQ).
 */
@Component
public class NotificationListener {

	private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

	@RabbitListener(queues = "${sporify.rabbit.notifications-queue}")
	public void onNotification(
			NotificationEvent event,
			Channel channel,
			@Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws Exception {
		try {
			if (event == null || event.userId() == null || event.message() == null || event.message().isBlank()) {
				throw new IllegalArgumentException("Evento notifications inválido o incompleto");
			}
			if (event.message().contains("FORCE_DLQ")) {
				throw new IllegalStateException("Fallo forzado para demostrar DLQ: " + event.message());
			}

			log.info(
					"Consumido notifications OK type={} userId={} message='{}'",
					event.type(),
					event.userId(),
					event.message());
			channel.basicAck(deliveryTag, false);
		}
		catch (Exception ex) {
			log.error(
					"Error en notifications → NACK sin requeue (DLQ). deliveryTag={} cause={}",
					deliveryTag,
					ex.getMessage());
			channel.basicNack(deliveryTag, false, false);
		}
	}
}
