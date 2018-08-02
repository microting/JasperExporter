package com.microting.report.jasper;

import com.microting.report.jasper.convertion.JasperReportConvertor;

import java.io.File;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.microting.report.jasper.exceptions.ReportExportException;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.SimpleJasperReportsContext;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.repo.FileRepositoryPersistenceServiceFactory;
import net.sf.jasperreports.repo.FileRepositoryService;
import net.sf.jasperreports.repo.PersistenceServiceFactory;
import net.sf.jasperreports.repo.RepositoryService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Logger;

import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

class JasperExporterEngine {

	private final Logger log;

	private URI uri;
	private String mainTemplate;
	private OutputStream outputStream;
	private ExportType exportType;

	private static final Pattern JASPER_WITH_QUOTES = Pattern.compile("(\".*\\.jasper\")");
	private static final Pattern JASPER_WITHOUT_QUOTES = Pattern.compile("(\\([^()].*\\.jasper\\))");

	JasperExporterEngine(Logger log) {
		this.log = log;
	}

	void export() throws ReportExportException {
		try {
			JasperReport template = loadTemplate(mainTemplate);
			String query = template.getQuery().getText();
			if (isBlank(query)) {
				query = ".";
			}
			JRXmlDataSource datasource = new JRXmlDataSource(uri.toString(), query);

			SimpleJasperReportsContext context = new SimpleJasperReportsContext();
			FileRepositoryService fileRepository = new FileRepositoryService(context, ReportExporterHelper.getTemplateFolder(mainTemplate), false);
			context.setExtensions(RepositoryService.class, Collections.singletonList(fileRepository));
			context.setExtensions(PersistenceServiceFactory.class, Collections.singletonList(FileRepositoryPersistenceServiceFactory.getInstance()));

			JasperPrint jasperPrint = JasperFillManager.getInstance(context).fill(template, new HashMap<>(), datasource);

			new JasperReportConvertor(outputStream, exportType).convert(jasperPrint);
		} catch (Throwable e) {
			throw new ReportExportException(e);
		}
	}

	private JasperReport loadTemplate(String templateFile) throws JRException {
		if (isBlank(templateFile) || !FilenameUtils.isExtension(templateFile, new String[]{"jrxml", "jasper"})) {
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
				if (isNotBlank(subreportName)) {
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