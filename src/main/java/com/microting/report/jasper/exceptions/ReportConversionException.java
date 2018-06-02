package com.microting.report.jasper.exceptions;

public class ReportConversionException extends Exception {

	private static final long serialVersionUID = 3612660591827286357L;

	public ReportConversionException(Throwable e) {
		super(e);
	}

	public ReportConversionException(String message) {
		super(message);
	}
}