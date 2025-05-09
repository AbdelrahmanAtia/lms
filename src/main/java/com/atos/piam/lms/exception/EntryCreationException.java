package com.atos.piam.lms.exception;

public class EntryCreationException extends RuntimeException {
	public EntryCreationException(String message) {
		super(message);
	}

	public EntryCreationException(String message, Throwable cause) {
		super(message, cause);
	}
}