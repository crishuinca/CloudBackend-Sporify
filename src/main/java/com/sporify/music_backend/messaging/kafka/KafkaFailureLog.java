package com.sporify.music_backend.messaging.kafka;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.RetryListener;
import org.springframework.stereotype.Component;

/**
 * Deja en el log cada reintento y el pase al tópico .DLT, con el id de correlación.
 */
@Component
public class KafkaFailureLog implements RetryListener {

	private static final Logger log = LoggerFactory.getLogger(KafkaFailureLog.class);

	@Override
	public void failedDelivery(ConsumerRecord<?, ?> record, Exception exception, int deliveryAttempt) {
		log.warn(
				"Kafka reintento attempt={} topic={} partition={} offset={} correlationId={} cause={}",
				deliveryAttempt,
				record.topic(),
				record.partition(),
				record.offset(),
				correlationId(record),
				exception.getMessage());
	}

	@Override
	public void recovered(ConsumerRecord<?, ?> record, Exception exception) {
		log.error(
				"Kafka a DLT topic={} partition={} offset={} correlationId={} cause={}",
				record.topic(),
				record.partition(),
				record.offset(),
				correlationId(record),
				exception.getMessage());
	}

	private static String correlationId(ConsumerRecord<?, ?> record) {
		if (record.value() instanceof PlaybackEvent playback && playback.correlationId() != null) {
			return playback.correlationId();
		}
		if (record.value() instanceof ActivityAlert alert && alert.correlationId() != null) {
			return alert.correlationId();
		}
		Header header = record.headers().lastHeader(KafkaEventPublisher.CORRELATION_HEADER);
		if (header == null || header.value() == null) {
			return "sin-id";
		}
		return new String(header.value(), StandardCharsets.UTF_8);
	}
}
