package com.microting.report.jasper;

import com.microting.report.jasper.JasperExporter.ExitCode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class DifferentOutputFormatsTest {

	private static final String REPORT1_TEMPLATE_WITH_SUBREPORT_EXTERNAL_IMAGES = "C:\\Projects\\Microting\\JasperExporter\\src\\test\\resources\\1316.jrxml";
	private static final String REPORT1_DATASOURCE = "C:/Projects/Microting/JasperExporter/src/test/resources/201805281421570431_5491.xml";

	private static final Report[] REPORTS = {Report.builder().template(REPORT1_TEMPLATE_WITH_SUBREPORT_EXTERNAL_IMAGES).inputDataUri(REPORT1_DATASOURCE).build()};

	@Parameters
	public static Collection<Object[]> data() {
		Collection<Object[]> data = new ArrayList<>();
		for (Report report : REPORTS) {
			for (ExportType exportType : ExportType.values()) {
				data.add(new Object[]{report.getTemplate(), report.getInputDataUri(), exportType.name().toLowerCase(), generateOutputFileName(exportType.name().toLowerCase())});
			}
		}

		return data;
	}

	private static String generateOutputFileName(String type) {
		return String.format("./out/%s.%s",
				new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()),
				type);
	}

	@Parameter(0)
	public String template;

	@Parameter(1)
	public String uri;

	@Parameter(2)
	public String type;

	@Parameter(3)
	public String outputFile;

	private static String templateArg(String value) {
		return getArgDef("template", value);
	}

	private static String uriArg(String value) {
		return getArgDef("uri", value);
	}

	private static String typeArg(String value) {
		return getArgDef("type", value);
	}

	private static String outputFileArg(String value) {
		return getArgDef("outputFile", value);
	}

	private static String getArgDef(String name, String value) {
		return String.format("-%1s=%2s", name, value);
	}

	@Test
	public void buildReport() {
		JasperExporter endpoint = new JasperExporter(templateArg(template), uriArg(uri), typeArg(type), outputFileArg(outputFile));
		assertEquals(endpoint.buildReport(), ExitCode.NORMAL.getCode());
	}
}