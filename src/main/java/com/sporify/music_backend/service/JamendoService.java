package com.sporify.music_backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.sporify.music_backend.dto.JamendoTrackResponse;
import com.sporify.music_backend.web.ApiException;

@Service
public class JamendoService {

	private static final ParameterizedTypeReference<Map<String, Object>> MAP_TYPE = new ParameterizedTypeReference<>() {
	};

	private final RestClient restClient;
	private final String clientId;

	public JamendoService(
			@Value("${sporify.jamendo.base-url}") String baseUrl,
			@Value("${sporify.jamendo.client-id:}") String clientId) {
		this.restClient = RestClient.builder().baseUrl(baseUrl).build();
		this.clientId = clientId;
	}

	public List<JamendoTrackResponse> search(String query, int limit) {
		if (clientId == null || clientId.isBlank()) {
			throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE,
					"Configura sporify.jamendo.client-id (client_id de Jamendo)");
		}
		String q = query == null ? "" : query.trim();
		if (q.isBlank()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "El parámetro q es obligatorio");
		}
		int safeLimit = Math.min(Math.max(limit, 1), 50);

		Map<String, Object> root;
		try {
			root = restClient.get()
					.uri(uri -> uri.path("/tracks/")
							.queryParam("client_id", clientId)
							.queryParam("format", "json")
							.queryParam("limit", safeLimit)
							.queryParam("search", q)
							.build())
					.retrieve()
					.body(MAP_TYPE);
		} catch (RestClientException ex) {
			throw new ApiException(HttpStatus.BAD_GATEWAY, "No se pudo consultar Jamendo");
		}

		if (root == null) {
			return List.of();
		}

		Object headersObj = root.get("headers");
		if (headersObj instanceof Map<?, ?> headers) {
			Object status = headers.get("status");
			if (status != null && !"success".equalsIgnoreCase(String.valueOf(status))) {
				throw new ApiException(HttpStatus.BAD_GATEWAY, "Jamendo rechazó la búsqueda");
			}
		}

		List<JamendoTrackResponse> tracks = new ArrayList<>();
		Object resultsObj = root.get("results");
		if (resultsObj instanceof List<?> results) {
			for (Object itemObj : results) {
				if (itemObj instanceof Map<?, ?> item) {
					tracks.add(new JamendoTrackResponse(
							text(item.get("id")),
							text(item.get("name")),
							text(item.get("artist_name")),
							text(item.get("audio")),
							firstImage(item)));
				}
			}
		}
		return tracks;
	}

	private static String firstImage(Map<?, ?> item) {
		String album = text(item.get("album_image"));
		if (album != null) {
			return album;
		}
		return text(item.get("image"));
	}

	private static String text(Object value) {
		if (value == null) {
			return null;
		}
		String text = String.valueOf(value);
		return text.isBlank() ? null : text;
	}
}
