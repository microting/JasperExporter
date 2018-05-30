package com.microting.report.jasper.convertion;

import java.io.OutputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import net.sf.jasperreports.export.*;

public class JasperReportConvertor {

	private static SimpleDocxReportConfiguration get() {
		SimpleDocxReportConfiguration reportConfiguration = new SimpleDocxReportConfiguration();
		reportConfiguration.setFlexibleRowHeight(Boolean.TRUE);
		return reportConfiguration;
	}

	private static class ReportExporter<T extends Exporter<? extends ExporterInput, IC, ? extends ExporterConfiguration, ? extends ExporterOutput>, IC extends ReportExportConfiguration> {

		private Class<T> exporterClass;
		private Supplier<IC> getReportConfiguration;

		public ReportExporter(Class<T> exporterClass, Supplier<IC> getReportConfiguration) {
			this.exporterClass = exporterClass;
			this.getReportConfiguration = getReportConfiguration;
		}
	}

	private static final Map<ExportType, ReportExporter> EXPORTERS =
			Collections.unmodifiableMap(Stream.of(
					new SimpleEntry<>(ExportType.DOC, new ReportExporter<>(JRDocxExporter.class, () -> {
						SimpleDocxReportConfiguration reportConfiguration = new SimpleDocxReportConfiguration();
						reportConfiguration.setFlexibleRowHeight(Boolean.TRUE);
						return reportConfiguration;
					}))
					/*new SimpleEntry<>(ExportType.DOCX, JRDocxExporter.class),
					new SimpleEntry<>(ExportType.ODT, JROdtExporter.class),
					new SimpleEntry<>(ExportType.PDF, JRPdfExporter.class),
					new SimpleEntry<>(ExportType.RTF, JRRtfExporter.class),
					new SimpleEntry<>(ExportType.PPT, JRPptxExporter.class),
					new SimpleEntry<>(ExportType.PPTX, JRPptxExporter.class),
					new SimpleEntry<>(ExportType.XLSX, JRXlsxExporter.class),
					new SimpleEntry<>(ExportType.XLS, JRXlsExporter.class)*/)
					.collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue, (l, r) -> {
						throw new IllegalArgumentException("Duplicate keys " + l + "and " + r + ".");
					}, () -> new EnumMap<>(ExportType.class))));

	private final ExportType exportType;
	private final OutputStream outputStream;

	public JasperReportConvertor(OutputStream outputStream, ExportType exportType) {
		this.outputStream = outputStream;
		this.exportType = exportType;
	}

	public void convert(JasperPrint jasperPrint) throws ReportConversionException {
		try {

//			Optional.ofNullable(EXPORTERS.get(exportType)).map(v -> v.).orElse()

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
				case XLS:
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
		Exporter<ExporterInput, PdfReportConfiguration, PdfExporterConfiguration, OutputStreamExporterOutput> exporter = new JRPdfExporter();
		SimpleOutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(outputStream);
		exporter.setExporterOutput(exporterOutput);
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.exportReport();
	}

	private void generateDoc(JasperPrint jasperPrint) throws JRException {
		Exporter<ExporterInput, DocxReportConfiguration, DocxExporterConfiguration, OutputStreamExporterOutput> exporter = new JRDocxExporter();
		SimpleOutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(outputStream);
		exporter.setExporterOutput(exporterOutput);
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));

		SimpleDocxReportConfiguration reportConfiguration = new SimpleDocxReportConfiguration();
		reportConfiguration.setFlexibleRowHeight(Boolean.TRUE);
		exporter.setConfiguration(reportConfiguration);

		exporter.exportReport();
	}

	private void genarateOdt(JasperPrint jasperPrint) throws JRException {
		Exporter<ExporterInput, OdtReportConfiguration, OdtExporterConfiguration, OutputStreamExporterOutput> exporter = new JROdtExporter();
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
		Exporter<ExporterInput, XlsReportConfiguration, XlsExporterConfiguration, OutputStreamExporterOutput> exporter = new JRXlsExporter();
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
		Exporter<ExporterInput, PptxReportConfiguration, PptxExporterConfiguration, OutputStreamExporterOutput> exporter = new JRPptxExporter();
		SimpleOutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(outputStream);
		exporter.setExporterOutput(exporterOutput);
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.exportReport();
	}
}