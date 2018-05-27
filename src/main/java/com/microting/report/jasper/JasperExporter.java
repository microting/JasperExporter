package com.microting.report.jasper;

import java.io.File;
import java.io.FileOutputStream;

public class JasperExporter {

	private static final String TEMPLATE = "template";
	private static final String OUPUTFILE = "outputFile";
	private static final String URI = "uri";
	private static final String TYPE = "type";

	public static void main(String[] args) {
		try {
			JasperExporterEngine engine = new JasperExporterEngine();
			if ((args.length == 0) || (args.length != 4)) {
				printMessageAndExit(getUsageText());
			}

			for (int i = 0; i < args.length; i++) {
				String arg = args[i];
				String argValue = arg.substring(arg.indexOf("=") + 1);
				if ((argValue == null) || ("".equals(argValue))) {
					printMessageAndExit("Invalid value for arg:" + arg);
				}

				if (arg.contains(TEMPLATE)) {
					engine.setTemplate(argValue);
				} else if (arg.contains(URI)) {
					engine.setReportData(new java.net.URI(argValue));
				} else if (arg.contains(OUPUTFILE)) {
					if (argValue.contains(File.separator)) {
						new File(argValue.substring(0, argValue.lastIndexOf(File.separator))).mkdirs();
					}
					engine.setOutputStream(new FileOutputStream(argValue));
				} else if (arg.contains(TYPE)) {
					engine.setExportType(ExportType.byName(argValue.trim().replace(".", "")));
				}
			}
			engine.export();
		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(-50);
		}
	}

	private static void printMessageAndExit(String x) {
		System.out.println(x);
		System.exit(0);
	}

	private static String getUsageText() {
		String usage = "Usage: java -jar JasperExporter.jar";
		usage = usage + String.format(" -%s=<template.jrxml|template.jasper>", TEMPLATE);
		usage = usage + String.format(" -%s=<http|file|ftp://pathToMyData >", URI);
		usage = usage + String.format(" -%s=<pdf|rtf|odt|doc|docx|xls|xlsx|ppt|pptx>", TYPE);
		usage = usage + String.format(" -%s=<myExportedFile.pdf>", OUPUTFILE);
		return usage;
	}
}