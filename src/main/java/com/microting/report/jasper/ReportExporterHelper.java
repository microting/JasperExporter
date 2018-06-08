package com.microting.report.jasper;

import java.io.File;

import net.sf.jasperreports.engine.JRException;
import org.apache.commons.io.FilenameUtils;

public class ReportExporterHelper {

	protected static boolean isNecessaryTemplateCompilation(String templateFile) throws JRException {
		templateFile = templateFile.trim();
		if (!new File(templateFile).exists()) {
			throw new JRException("Template file not found " + templateFile);
		}
		if (templateFile.endsWith("jasper")) {
			return false;
		}
		String compiledTemplateFile = changeFileExtension(templateFile, "jasper");
		if ((templateFile.endsWith("jrxml")) && (!new File(compiledTemplateFile).exists())) {
			return true;
		}
		return new File(templateFile).lastModified() > new File(compiledTemplateFile).lastModified();
	}

	protected static String getTemplateFolder(String templateFile) {
		String canonicalPath = FilenameUtils.getFullPath(templateFile);
		return canonicalPath.substring(0, canonicalPath.lastIndexOf(File.separatorChar) + 1);
	}

	public static String changeFileExtension(String filename, String extension) {
		return FilenameUtils.removeExtension(filename).concat("." + extension);
	}

	public static boolean fileExists(String file) {
		return new File(file).exists();
	}
}