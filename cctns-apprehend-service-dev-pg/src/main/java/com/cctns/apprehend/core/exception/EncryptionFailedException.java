package com.cctns.apprehend.core.exception;

public class EncryptionFailedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EncryptionFailedException(String message) {
		super(message);
	}
}