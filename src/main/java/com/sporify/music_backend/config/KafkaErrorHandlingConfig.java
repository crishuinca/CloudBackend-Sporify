package com.sporify.music_backend.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.boot.kafka.autoconfigure.DefaultKafkaProducerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.kafka.support.serializer.DelegatingByTypeSerializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import com.sporify.music_backend.messaging.kafka.ActivityAlert;
import com.sporify.music_backend.messaging.kafka.KafkaFailureLog;
import com.sporify.music_backend.messaging.kafka.PlaybackEvent;

/**
 * Reintento con espera y, si sigue fallando, publicación en el tópico original más ".DLT".
 * Un JSON ilegible no se reintenta: va directo al DLT.
 */
@Configuration
public class KafkaErrorHandlingConfig {

	private static final String DLT_SUFFIX = ".DLT";

	@Bean
	DefaultKafkaProducerFactoryCustomizer kafkaValueSerializer() {
		JacksonJsonSerializer<Object> json = new JacksonJsonSerializer<>();
		json.setAddTypeInfo(true);
		Map<Class<?>, Serializer<?>> byType = new LinkedHashMap<>();
		byType.put(byte[].class, new ByteArraySerializer());
		byType.put(PlaybackEvent.class, json);
		byType.put(ActivityAlert.class, json);
		DelegatingByTypeSerializer serializer = new DelegatingByTypeSerializer(byType);
		return factory -> {
			@SuppressWarnings({"rawtypes", "unchecked"})
			DefaultKafkaProducerFactory raw = (DefaultKafkaProducerFactory) factory;
			raw.setValueSerializer(serializer);
		};
	}

	@Bean
	DefaultErrorHandler kafkaErrorHandler(KafkaOperations<Object, Object> template, KafkaFailureLog failureLog) {
		DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
				template,
				(record, ex) -> new TopicPartition(record.topic() + DLT_SUFFIX, record.partition()));
		DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3L));
		handler.addNotRetryableExceptions(DeserializationException.class, SerializationException.class);
		handler.setRetryListeners(failureLog);
		return handler;
	}

	@Bean
	NewTopic playbackDeadLetterTopic(SporifyKafkaProperties props) {
		return deadLetter(props.getPlaybackTopic());
	}

	@Bean
	NewTopic alertsDeadLetterTopic(SporifyKafkaProperties props) {
		return deadLetter(props.getAlertsTopic());
	}

	private static NewTopic deadLetter(String topic) {
		return TopicBuilder.name(topic + DLT_SUFFIX).partitions(3).replicas(3).build();
	}
}
