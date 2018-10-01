package com.microting.report.jasper.exceptions;

public class WrongArgumentException extends ArgumentException {

	private static final long serialVersionUID = 1075899291053788607L;

	public WrongArgumentException(Throwable e) {
		super(e);
	}

	public WrongArgumentException(String argumentDefinition) {
		super(String.format("Wrong argument definition: %s", argumentDefinition));
	}
}