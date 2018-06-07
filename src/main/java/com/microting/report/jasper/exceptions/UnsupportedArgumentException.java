package com.microting.report.jasper.exceptions;

public class UnsupportedArgumentException extends ArgumentException {

	private static final long serialVersionUID = 8868335294511173919L;

	public UnsupportedArgumentException(Throwable e) {
		super(e);
	}

	public UnsupportedArgumentException(String argumentName) {
		super(String.format("Unsupported argument: %s", argumentName));
	}
}