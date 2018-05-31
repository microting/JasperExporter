package com.microting.report.jasper.convertion;

import java.io.OutputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.microting.report.jasper.ExportType;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.ExporterConfiguration;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.ExporterOutput;
import net.sf.jasperreports.export.ReportExportConfiguration;
import net.sf.jasperreports.export.SimpleDocxReportConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOdtReportConfiguration;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;
import net.sf.jasperreports.export.SimplePptxReportConfiguration;
import net.sf.jasperreports.export.SimpleRtfReportConfiguration;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

public class JasperReportConvertor {

	private static class ReportExporter<T extends Exporter<? extends ExporterInput, IC, ? extends ExporterConfiguration, ? extends ExporterOutput>, IC extends ReportExportConfiguration> {

		private Class<T> exporterClass;
		private Supplier<IC> getReportConfiguration;

		public ReportExporter(Class<T> exporterClass, Supplier<IC> getReportConfiguration) {
			this.exporterClass = exporterClass;
			this.getReportConfiguration = getReportConfiguration;
		}
	}

	private static final Map<ExportType, ReportExporter<? extends Exporter, ? extends ReportExportConfiguration>> EXPORTERS =
			Collections.unmodifiableMap(Stream.of(
					new SimpleEntry<>(ExportType.PDF, new ReportExporter<>(JRPdfExporter.class, SimplePdfReportConfiguration::new)),

					new SimpleEntry<>(ExportType.DOC, new ReportExporter<>(JRDocxExporter.class, () -> {
						SimpleDocxReportConfiguration reportConfiguration = new SimpleDocxReportConfiguration();
						reportConfiguration.setFlexibleRowHeight(Boolean.TRUE);
						return reportConfiguration;
					})),
					new SimpleEntry<>(ExportType.DOCX, new ReportExporter<>(JRDocxExporter.class, () -> {
						SimpleDocxReportConfiguration reportConfiguration = new SimpleDocxReportConfiguration();
						reportConfiguration.setFlexibleRowHeight(Boolean.TRUE);
						return reportConfiguration;
					})),

					new SimpleEntry<>(ExportType.ODT, new ReportExporter<>(JROdtExporter.class, SimpleOdtReportConfiguration::new)),
					new SimpleEntry<>(ExportType.RTF, new ReportExporter<>(JRRtfExporter.class, SimpleRtfReportConfiguration::new)),
					new SimpleEntry<>(ExportType.XLSX, new ReportExporter<>(JRXlsxExporter.class, () -> {
						SimpleXlsxReportConfiguration reportConfiguration = new SimpleXlsxReportConfiguration();
						reportConfiguration.setDetectCellType(Boolean.TRUE);
						reportConfiguration.setWhitePageBackground(Boolean.FALSE);
						reportConfiguration.setRemoveEmptySpaceBetweenColumns(Boolean.TRUE);
						return reportConfiguration;
					})),

					new SimpleEntry<>(ExportType.XLS, new ReportExporter<>(JRXlsExporter.class, () -> {
						SimpleXlsReportConfiguration reportConfiguration = new SimpleXlsReportConfiguration();
						reportConfiguration.setDetectCellType(Boolean.TRUE);
						reportConfiguration.setWhitePageBackground(Boolean.FALSE);
						reportConfiguration.setRemoveEmptySpaceBetweenColumns(Boolean.TRUE);
						return reportConfiguration;
					})),
					new SimpleEntry<>(ExportType.PPT, new ReportExporter<>(JRPptxExporter.class, SimplePptxReportConfiguration::new)),
					new SimpleEntry<>(ExportType.PPTX, new ReportExporter<>(JRPptxExporter.class, SimplePptxReportConfiguration::new)))
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
			SimpleEntry<ReportExporter<? extends Exporter, ? extends ReportExportConfiguration>, Exporter> exporterPair = Optional.ofNullable(EXPORTERS.get(exportType))
					.map(v -> new SimpleEntry<ReportExporter<? extends Exporter, ? extends ReportExportConfiguration>, Exporter>(v, createExporter.apply(v)))
					.orElseThrow(() -> new ReportConversionException("Invalid export file type for " + exportType));

			Exporter exporter = setExporterConfig(exporterPair);

			SimpleOutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(outputStream);
			exporter.setExporterOutput(exporterOutput);
			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporter.exportReport();
		} catch (Throwable e) {
			throw new ReportConversionException(e);
		}
	}

	private Function<ReportExporter, Exporter> createExporter = JasperReportConvertor::createExporter;

	private static Exporter createExporter(ReportExporter<? extends Exporter, ? extends ReportExportConfiguration> v) {
		try {
			return v.exporterClass.newInstance();
		} catch (IllegalAccessException | InstantiationException e) {
			throw new RuntimeException(e);
		}
	}

	private static Exporter setExporterConfig(SimpleEntry<ReportExporter<? extends Exporter, ? extends ReportExportConfiguration>, Exporter> e) {
		e.getValue().setConfiguration(e.getKey().getReportConfiguration.get());
		return e.getValue();
	}
}