package com.microting.report.jasper;

import com.microting.report.jasper.convertion.JasperReportConvertor;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.microting.report.jasper.exceptions.ReportExportException;
import lombok.extern.log4j.Log4j2;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.SimpleFileResolver;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.util.Strings;

@Log4j2
class JasperExporterEngine {

	private URI uri;
	private String mainTemplate;
	private OutputStream outputStream;
	private ExportType exportType;

	private static final Pattern JASPER_WITH_QUOTES = Pattern.compile("(\".*\\.jasper\")");
	private static final Pattern JASPER_WITHOUT_QUOTES = Pattern.compile("(\\([^()].*\\.jasper\\))");

	void export() throws ReportExportException {
		try {
			JasperReport template = loadTemplate(mainTemplate);
			String query = template.getQuery().getText();
			if ((query == null) || ("".equals(query))) {
				query = ".";
			}
			JRXmlDataSource datasource = new JRXmlDataSource(uri.toString(), query);
			JasperPrint jasperPrint = JasperFillManager.fillReport(template, getDefaultConfiguration(mainTemplate), datasource);
			JasperReportConvertor reportConvertor = new JasperReportConvertor(outputStream, exportType);
			reportConvertor.convert(jasperPrint);
		} catch (Throwable e) {
			throw new ReportExportException(e);
		}
	}

	private static Map<String, Object> getDefaultConfiguration(String template) throws IOException {
		Map<String, Object> config = new HashMap();
		config.put("REPORT_FILE_RESOLVER", new SimpleFileResolver(new File(ReportExporterHelper.getTemplateFolder(template))));

		ClassLoader classLoader = new URLClassLoader(new URL[]{Paths.get(ReportExporterHelper.getTemplateFolder(template)).toUri().toURL()});
		config.put(JRParameter.REPORT_CLASS_LOADER, classLoader);

		return config;
	}

	private JasperReport loadTemplate(String templateFile) throws JRException {
		if (Strings.isBlank(templateFile) || !FilenameUtils.isExtension(templateFile, new String[]{"jrxml", "jasper"})) {
			throw new JRException("Invalid template extension for this file" + templateFile);
		}
		if (!ReportExporterHelper.isNecessaryTemplateCompilation(templateFile)) {
			String compiledFile = ReportExporterHelper.changeFileExtension(templateFile, "jasper");
			return (JasperReport) JRLoader.loadObjectFromFile(compiledFile);
		}
		return compileTemplateAndDependencies(templateFile);
	}

	private static String findSubreportName(String value) {
		if (value.endsWith("jasper")) {
			return value;
		} else {
			Matcher matcher = JASPER_WITH_QUOTES.matcher(value);
			if (matcher.find() && matcher.groupCount() > 0) {
				return matcher.group().trim();
			}
			matcher = JASPER_WITHOUT_QUOTES.matcher(value);
			if (matcher.find() && matcher.groupCount() > 0) {
				return matcher.group().trim().replace("(", "").replace(")", "");
			}
		}
		return null;
	}

	private JasperReport compileTemplateAndDependencies(String templateFile) throws JRException {
		templateFile = templateFile.endsWith("jrxml") ? templateFile : ReportExporterHelper.changeFileExtension(templateFile, "jrxml");

		JasperDesign design;
		try {
			design = JRXmlLoader.load(templateFile);
		} catch (JRException e) {
			log.error("Failed to load template: {}", templateFile, e);
			throw e;
		}
		for (JRExpression expr : JRExpressionCollector.collectExpressions(DefaultJasperReportsContext.getInstance(), design)) {
			String value = expr.getText().replaceAll("\"", "");
			if (expr instanceof JRDesignExpression) {
				String subreportName = findSubreportName(value);
				if (Strings.isNotBlank(subreportName)) {
					String subReportFile = FilenameUtils.concat(ReportExporterHelper.getTemplateFolder(templateFile), subreportName);
					compileTemplateAndDependencies(subReportFile);
				}
			}
		}
		String compiledFile = ReportExporterHelper.changeFileExtension(templateFile, "jasper");
		FileUtils.deleteQuietly(new File(compiledFile));
		JasperCompileManager.compileReportToFile(design, compiledFile);
		return (JasperReport) JRLoader.loadObjectFromFile(compiledFile);
	}

	void setTemplate(String templateFile) {
		if (!ReportExporterHelper.fileExists(templateFile)) {
			throw new IllegalArgumentException("This template file does not exists " + templateFile);
		}
		mainTemplate = templateFile;
	}

	void setReportData(URI uri) {
		this.uri = uri;
	}

	void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	void setExportType(ExportType exportType) {
		this.exportType = exportType;
	}
}