package com.backend.ECApplication.Exception;

import java.util.List;
import java.util.ArrayList;

public class ErrorResponse {
	
	private List<ErrorItem> errors = new ArrayList<>();

	public List<ErrorItem> getErrors() {
		return errors;
	}

	public void setErrors(List<ErrorItem> errors) {
		this.errors = errors;
	}
	
	public void addError(ErrorItem error) {
		this.errors.add(error);
	}

}
