package com.backend.ECApplication.Exception;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

/*
 * Other custom defined ResourceNotFoundException :
 * https://github.com/eugenp/tutorials/blob/master/spring-boot-angular/src/main/java/com/baeldung/ecommerce/exception/ResourceNotFoundException.java
 */

@ControllerAdvice
public class ApiExceptionHandler {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handle (MethodArgumentNotValidException e) {
		ErrorResponse errors = new ErrorResponse();
		for (FieldError violation : e.getBindingResult().getFieldErrors()) {
			ErrorItem error = new ErrorItem();
			error.setCode(violation.getField());
			error.setMessage(violation.getDefaultMessage());
			
			errors.addError(error);
		}
		
		for (ObjectError violation : e.getBindingResult().getGlobalErrors()) {
			ErrorItem error = new ErrorItem();
			error.setCode(violation.getObjectName());
			error.setMessage(violation.getDefaultMessage());
			
			errors.addError(error);
		}
		
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handle(ConstraintViolationException e) {
		ErrorResponse errors = new ErrorResponse();
		for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
			ErrorItem error = new ErrorItem();
			error.setCode(violation.getMessageTemplate());
			error.setMessage(violation.getMessage());
			
			errors.addError(error);
		}
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorItem> handle(HttpRequestMethodNotSupportedException e) {
		ErrorItem error = new ErrorItem();
		
		StringBuilder builder = new StringBuilder();
		builder.append(e.getMethod());
		builder.append(
		      " method is not supported for this request. Supported methods are ");
		e.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));
		
		error.setCode(builder.toString());
		error.setMessage(e.getMessage());
		
		return new ResponseEntity<>(error, HttpStatus.METHOD_NOT_ALLOWED);
	}
	
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<ErrorItem> handle(HttpMediaTypeNotSupportedException e) {
		ErrorItem error = new ErrorItem();
		
		StringBuilder builder = new StringBuilder();
		builder.append(e.getContentType());
		builder.append(" media type is not supported. Supported media types are ");
		e.getSupportedMediaTypes().forEach(t -> builder.append(t + ", "));
		
		error.setCode(builder.substring(0, builder.length() - 2));
		error.setMessage(e.getMessage());
		
		return new ResponseEntity<>(error, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorItem> handle(ResourceNotFoundException e) {
		ErrorItem error = new ErrorItem();
		error.setMessage(e.getMessage());
		
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> finalHandle(Exception e) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}

}
