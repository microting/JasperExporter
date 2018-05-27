package com.microting.report.jasper;

import com.microting.report.jasper.convertion.JasperReportConvertor;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
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

public class JasperExporterEngine {

	private URI uri;
	private String mainTemplate;
	private OutputStream outputStream;
	private ExportType exportType;

	public void export() throws ReportExportException {
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

	public static HashMap<String, Object> getDefaultConfiguration(String template) throws IOException {
		HashMap<String, Object> config = new HashMap();
		config.put("REPORT_FILE_RESOLVER", new SimpleFileResolver(new File(ReportExporterHelper.getTemplateFolder(template))));
		return config;
	}

	protected JasperReport loadTemplate(String templateFile) throws JRException {
		if ((templateFile != null) && (!"".equals(templateFile))) {
			if (FilenameUtils.isExtension(templateFile, new String[]{"jrxml", "jasper"})) {
			}
		} else {
			throw new JRException("Invalid template extention for this file" + templateFile);
		}
		if (!ReportExporterHelper.isNecessaryTemplateCompilation(templateFile)) {
			String compiledFile = ReportExporterHelper.changeFileExtension(templateFile, "jasper");
			return (JasperReport) JRLoader.loadObjectFromFile(compiledFile);
		}
		return compileTemplateAndDependencies(templateFile);
	}

	protected JasperReport compileTemplateAndDependencies(String templateFile) throws JRException {
		templateFile = templateFile.endsWith("jrxml") ? templateFile : ReportExporterHelper.changeFileExtension(templateFile, "jrxml");

		JasperDesign design = JRXmlLoader.load(templateFile);
		for (JRExpression expr : JRExpressionCollector.collectExpressions(DefaultJasperReportsContext.getInstance(), design)) {
			String value = expr.getText().replaceAll("\"", "");
			if (((expr instanceof JRDesignExpression)) && (value.endsWith("jasper"))) {
				String subReportFile = FilenameUtils.concat(ReportExporterHelper.getTemplateFolder(templateFile), value);
				compileTemplateAndDependencies(subReportFile);
			}
		}
		String compiledFile = ReportExporterHelper.changeFileExtension(templateFile, "jasper");
		FileUtils.deleteQuietly(new File(compiledFile));
		JasperCompileManager.compileReportToFile(design, compiledFile);
		return (JasperReport) JRLoader.loadObjectFromFile(compiledFile);
	}

	public void setTemplate(String templateFile) {
		if (!ReportExporterHelper.fileExists(templateFile)) {
			throw new IllegalArgumentException("This template file does not exists " + templateFile);
		}
		mainTemplate = templateFile;
	}

	public void setReportData(URI uri) {
		this.uri = uri;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public void setExportType(ExportType exportType) {
		this.exportType = exportType;
	}
}