package com.backend.ECApplication.Exception;

public class UnprocessableException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7922488593767424349L;

	public UnprocessableException() {
		super();
	}
	
	public UnprocessableException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	public UnprocessableException(final String message) {
		super(message);
	}
	
	public UnprocessableException(final Throwable cause) {
        super(cause);
    }

}
