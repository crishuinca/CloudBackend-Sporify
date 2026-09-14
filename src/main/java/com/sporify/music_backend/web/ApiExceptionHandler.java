package com.sporify.music_backend.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(ApiException.class)
	ResponseEntity<ErrorBody> handleApi(ApiException ex) {
		return ResponseEntity.status(ex.getStatus())
				.body(new ErrorBody(ex.getStatus().value(), ex.getMessage()));
	}

	public record ErrorBody(int status, String message) {
	}
}
