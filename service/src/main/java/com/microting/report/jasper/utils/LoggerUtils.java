package com.microting.report.jasper.utils;

public class LoggerUtils {

	public static String toLoggerName(final Class<?> cls) {
		String canonicalName = cls.getCanonicalName();
		return canonicalName != null ? canonicalName : cls.getName();
	}
}