package com.atos.piam.lms.exception;

public class LdapRepositoryException extends RuntimeException {
	public LdapRepositoryException() {
	}

	public LdapRepositoryException(String message) {
		super(message);
	}

	public LdapRepositoryException(String message, Throwable cause) {
		super(message, cause);
	}

	public LdapRepositoryException(Throwable cause) {
		super(cause);
	}
}