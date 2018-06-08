package com.microting.report.jasper.exceptions;

public class WrongArgumentsNumberException extends Exception {

	private static final long serialVersionUID = -8360912886281716761L;

	public WrongArgumentsNumberException(Throwable e) {
		super(e);
	}

	public WrongArgumentsNumberException(String message) {
		super(message);
	}
}