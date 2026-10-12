package com.sporify.music_backend.web;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sporify.music_backend.messaging.RabbitDlqDemoService;

/**
 * Endpoint de demo para la presentación EP2: fuerza mensajes a DLQ.
 * POST /api/demo/rabbit/force-dlq?target=playlist|likes|notifications|all
 */
@RestController
@RequestMapping("/api/demo/rabbit")
public class RabbitDlqDemoController {

	private final RabbitDlqDemoService demoService;

	public RabbitDlqDemoController(RabbitDlqDemoService demoService) {
		this.demoService = demoService;
	}

	@PostMapping("/force-dlq")
	@PreAuthorize("hasAuthority('SCOPE_Music.Write')")
	public Map<String, Object> forceDlq(@RequestParam(defaultValue = "playlist") String target) {
		List<String> published = demoService.publishForceDlq(target);
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("ok", true);
		body.put("marker", RabbitDlqDemoService.FORCE_MARKER);
		body.put("target", target);
		body.put("published", published);
		body.put("hint", "Revisá la DLQ correspondiente en Rabbit Management UI (y el log del backend)");
		return body;
	}
}
