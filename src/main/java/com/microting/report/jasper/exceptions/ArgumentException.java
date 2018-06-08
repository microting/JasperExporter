package com.microting.report.jasper.exceptions;

public class ArgumentException extends Exception {

	private static final long serialVersionUID = -5778898171036984582L;

	public ArgumentException(Throwable e) {
		super(e);
	}

	public ArgumentException(String message) {
		super(message);
	}
}