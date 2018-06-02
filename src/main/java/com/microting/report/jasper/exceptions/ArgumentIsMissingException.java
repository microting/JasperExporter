package com.microting.report.jasper.exceptions;

public class ArgumentIsMissingException extends ArgumentException {

	private static final long serialVersionUID = 8868335294511173919L;

	public ArgumentIsMissingException(Throwable e) {
		super(e);
	}

	public ArgumentIsMissingException(String argumentName) {
		super(String.format("Required argument %s is missing", argumentName));
	}
}