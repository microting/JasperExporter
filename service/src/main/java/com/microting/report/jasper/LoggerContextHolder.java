package com.microting.report.jasper;

import org.apache.logging.log4j.core.LoggerContext;

public enum LoggerContextHolder {

	INSTANCE;

	private LoggerContext value;

	public LoggerContext getValue() {
		return value;
	}

	public void setValue(LoggerContext loggerContext) {
		value = loggerContext;
	}
}