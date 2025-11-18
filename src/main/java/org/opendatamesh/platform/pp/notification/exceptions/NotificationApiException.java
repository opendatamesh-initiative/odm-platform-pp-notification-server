package org.opendatamesh.platform.pp.notification.exceptions;

import org.springframework.http.HttpStatus;

public abstract class NotificationApiException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3876573329263306459L;	
	
	public NotificationApiException() {
		super();
	}

	public NotificationApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NotificationApiException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotificationApiException(String message) {
		super(message);
	}

	public NotificationApiException(Throwable cause) {
		super(cause);
	}

	/**
	 * @return the errorName
	 */
	public String getErrorName() {
		return getClass().getSimpleName();	
	}

	/**
	 * @return the status
	 */
	public abstract HttpStatus getStatus();	
	

}