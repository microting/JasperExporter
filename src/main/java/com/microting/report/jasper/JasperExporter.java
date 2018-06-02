package com.microting.report.jasper;

import com.microting.report.jasper.exceptions.ArgumentException;
import com.microting.report.jasper.exceptions.ArgumentIsMissingException;
import com.microting.report.jasper.exceptions.UnsupportedArgumentException;
import com.microting.report.jasper.exceptions.WrongArgumentException;
import com.microting.report.jasper.exceptions.WrongArgumentsNumberException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.time.StopWatch;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.System.out;

@Log4j2
public class JasperExporter {

	private enum Argument {
		TEMPLATE("template"), OUPUTFILE("outputFile"), URI("uri"), TYPE("type");

		private String name;

		Argument(String name) {
			this.name = name;
		}

		private static Argument byName(String name) {
			for (Argument argument : Argument.values()) {
				if (argument.name.equalsIgnoreCase(name)) {
					return argument;
				}
			}
			return null;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private String[] arguments;

	enum ExitCode {
		ERROR(-50), WRONG_ARGUMENTS(0), NORMAL(0);

		private int code;

		ExitCode(int code) {
			this.code = code;
		}

		int getCode() {
			return code;
		}
	}

	JasperExporter(String... arguments) {
		this.arguments = arguments;
	}

	public static void main(String... args) {
		JasperExporter endpoint = new JasperExporter(args);
		System.exit(endpoint.buildReport());
	}

	private Map<Argument, String> parseArguments() throws WrongArgumentsNumberException, WrongArgumentException, UnsupportedArgumentException {
		Map<Argument, String> argumentsMap = new EnumMap<>(Argument.class);

		if (arguments.length == 0 || arguments.length != 4) {
			throw new WrongArgumentsNumberException("Wrong number of arguments");
		}
		for (String arg : arguments) {
			String[] argDef = arg.split("=");
			if (argDef.length < 2) {
				throw new WrongArgumentException(arg);
			}
			Argument argument = Optional.ofNullable(Argument.byName(argDef[0].substring(1))).orElseThrow(() -> new UnsupportedArgumentException(arg));
			argumentsMap.put(argument, argDef[1]);
		}
		return argumentsMap;
	}

	int buildReport() {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		try {
			JasperExporterEngine engine = new JasperExporterEngine();

			Map<Argument, String> argumentsMap = parseArguments();
			engine.setTemplate(Optional.ofNullable(argumentsMap.get(Argument.TEMPLATE)).orElseThrow(() -> new ArgumentIsMissingException(Argument.TEMPLATE.toString())));
			engine.setReportData(new URI(Optional.ofNullable(argumentsMap.get(Argument.URI)).orElseThrow(() -> new ArgumentIsMissingException(Argument.URI.toString()))));
			engine.setExportType(Optional.ofNullable(argumentsMap.get(Argument.TYPE)).map(s -> ExportType.byName(s.trim().replace(".", ""))).orElseThrow(() -> new ArgumentIsMissingException(Argument.TYPE.toString())));
			String ouputFileArg = Optional.ofNullable(argumentsMap.get(Argument.OUPUTFILE)).orElseThrow(() -> new ArgumentIsMissingException(Argument.OUPUTFILE.toString()));
			if (ouputFileArg.contains(File.separator)) {
				new File(ouputFileArg.substring(0, ouputFileArg.lastIndexOf(File.separator))).mkdirs();
			}
			engine.setOutputStream(new FileOutputStream(ouputFileArg));
			log.debug("Start building report. Template: {}, DataSource: {}, Type: {}, Output file: {}", argumentsMap.get(Argument.TEMPLATE),
					argumentsMap.get(Argument.URI), argumentsMap.get(Argument.TYPE), argumentsMap.get(Argument.OUPUTFILE));

			engine.export();
		} catch (WrongArgumentsNumberException e) {
			log.error(e.getMessage());
			printMessage(getUsageText());
			return ExitCode.WRONG_ARGUMENTS.getCode();
		} catch (ArgumentException e) {
			log.error(e.getMessage());
			printMessage(getUsageText());
			printMessage(e.getMessage());
			return ExitCode.WRONG_ARGUMENTS.getCode();
		} catch (Throwable e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			return ExitCode.ERROR.getCode();
		}
		log.debug("Report was build in {} ms", stopWatch.getTime());
		return ExitCode.NORMAL.getCode();
	}

	private static void printMessage(String x) {
		out.println(x);
	}

	private static String getUsageText() {
		String usage = "Usage: java -jar JasperExporter.jar";
		usage = usage + String.format(" -%s=<template.jrxml|template.jasper>", Argument.TEMPLATE);
		usage = usage + String.format(" -%s=<http|file|ftp://pathToMyData >", Argument.URI);
		usage = usage + String.format(" -%s=<pdf|rtf|odt|doc|docx|xls|xlsx|ppt|pptx>", Argument.TYPE);
		usage = usage + String.format(" -%s=<myExportedFile.pdf>", Argument.OUPUTFILE);
		return usage;
	}
}