package com.microting.report.jasper.convertion;

import java.io.OutputStream;

import com.microting.report.jasper.ExportType;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleDocxReportConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;

public class JasperReportConvertor {

	private final ExportType exportType;
	private final OutputStream outputStream;

	public JasperReportConvertor(OutputStream outputStream, ExportType exportType) {
		this.outputStream = outputStream;
		this.exportType = exportType;
	}

	public void convert(JasperPrint jasperPrint) throws ReportConversionException {
		try {
			switch (exportType) {
				case PDF:
					generatePdf(jasperPrint);
					break;
				case DOC:
				case DOCX:
					generateDoc(jasperPrint);
					break;
				case ODT:
					genarateOdt(jasperPrint);
					break;
				case RTF:
					generateRtf(jasperPrint);
					break;
				case XSL:
					generateXls(jasperPrint);
					break;
				case XLSX:
					generateXlsx(jasperPrint);
					break;
				case PPT:
				case PPTX:
					generatePptx(jasperPrint);
					break;
				default:
					throw new IllegalArgumentException("Invalid export file type for " + exportType);
			}
		} catch (Throwable e) {
			throw new ReportConversionException(e);
		}
	}

	private void generatePdf(JasperPrint jasperPrint) throws JRException {
		Exporter exporter = new JRPdfExporter();
		SimpleOutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(outputStream);
		exporter.setExporterOutput(exporterOutput);
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.exportReport();
	}

	private void generateDoc(JasperPrint jasperPrint) throws JRException {
		Exporter exporter = new JRDocxExporter();
		SimpleOutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(outputStream);
		exporter.setExporterOutput(exporterOutput);
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));

		SimpleDocxReportConfiguration reportConfiguration = new SimpleDocxReportConfiguration();
		reportConfiguration.setFlexibleRowHeight(Boolean.TRUE);
		exporter.setConfiguration(reportConfiguration);

		exporter.exportReport();
	}

	private void genarateOdt(JasperPrint jasperPrint) throws JRException {
		Exporter exporter = new JROdtExporter();
		SimpleOutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(outputStream);
		exporter.setExporterOutput(exporterOutput);
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.exportReport();
	}

	private void generateRtf(JasperPrint jasperPrint) throws JRException {
		Exporter exporter = new JRRtfExporter();
		SimpleOutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(outputStream);
		exporter.setExporterOutput(exporterOutput);
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.exportReport();
	}

	private void generateXls(JasperPrint jasperPrint) throws JRException {
		Exporter exporter = new JRXlsExporter();
		SimpleOutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(outputStream);
		exporter.setExporterOutput(exporterOutput);
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));

		SimpleXlsReportConfiguration reportConfiguration = new SimpleXlsReportConfiguration();
		reportConfiguration.setDetectCellType(Boolean.TRUE);
		reportConfiguration.setWhitePageBackground(Boolean.FALSE);
		reportConfiguration.setRemoveEmptySpaceBetweenColumns(Boolean.TRUE);

		exporter.setConfiguration(reportConfiguration);

		exporter.exportReport();
	}

	private void generateXlsx(JasperPrint jasperPrint) throws JRException {
		Exporter exporter = new JRXlsxExporter();
		SimpleOutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(outputStream);
		exporter.setExporterOutput(exporterOutput);
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));

		SimpleXlsReportConfiguration reportConfiguration = new SimpleXlsReportConfiguration();
		reportConfiguration.setDetectCellType(Boolean.TRUE);
		reportConfiguration.setWhitePageBackground(Boolean.FALSE);
		reportConfiguration.setRemoveEmptySpaceBetweenColumns(Boolean.TRUE);

		exporter.setConfiguration(reportConfiguration);

		exporter.exportReport();
	}


	private void generatePptx(JasperPrint jasperPrint) throws JRException {
		Exporter exporter = new JRPptxExporter();
		SimpleOutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(outputStream);
		exporter.setExporterOutput(exporterOutput);
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.exportReport();
	}
}