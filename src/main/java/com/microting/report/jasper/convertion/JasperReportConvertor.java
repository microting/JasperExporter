package com.microting.report.jasper.convertion;

import java.io.FileNotFoundException;
import java.io.OutputStream;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;

public class JasperReportConvertor {

	private final String exportType;
	private final OutputStream outputStream;

	public JasperReportConvertor(OutputStream outputStream, String exportType) {
		this.outputStream = outputStream;
		this.exportType = exportType.trim().replace(".", "");
	}

	public void convert(JasperPrint jasperPrint) throws ReportConversionException {
		try {
			if ("pdf".equalsIgnoreCase(this.exportType)) {
				JasperExportManager.exportReportToPdfStream(jasperPrint, this.outputStream);
			} else if (("xls".equalsIgnoreCase(this.exportType)) || ("xlsx".equalsIgnoreCase(this.exportType))) {
				exportToExcel(jasperPrint);
			} else if (("doc".equalsIgnoreCase(this.exportType)) || ("docx".equalsIgnoreCase(this.exportType))) {
				exportToWord(jasperPrint);
			} else if ("rtf".equalsIgnoreCase(this.exportType)) {
				export(jasperPrint, new JRRtfExporter());
			} else if ("odt".equalsIgnoreCase(this.exportType)) {
				export(jasperPrint, new JROdtExporter());
			} else if (("ppt".equalsIgnoreCase(this.exportType)) || ("pptx".equalsIgnoreCase(this.exportType))) {
				export(jasperPrint, new JRPptxExporter());
			} else {
				throw new IllegalArgumentException("Invalid export file type for " + this.exportType);
			}
		} catch (Throwable e) {
			throw new ReportConversionException(e);
		}
	}

	private void exportToWord(JasperPrint jasperPrint) throws JRException {
		JRDocxExporter exporter = new JRDocxExporter();
		exporter.setParameter(JRDocxExporterParameter.FLEXIBLE_ROW_HEIGHT, Boolean.TRUE);
		export(jasperPrint, exporter);
	}

	private void exportToExcel(JasperPrint jasperPrint) throws FileNotFoundException, JRException {
		JRXlsExporter exporter = new JRXlsExporter();
		exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
		exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
		exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
		export(jasperPrint, exporter);
	}

	private void export(JasperPrint jasperPrint, JRExporter exporter) throws JRException {
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, this.outputStream);
		exporter.exportReport();
	}
}