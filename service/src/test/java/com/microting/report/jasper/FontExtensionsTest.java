package com.microting.report.jasper;

import com.microting.report.jasper.JasperExporter.ExitCode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.io.FilenameUtils.removeExtension;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class FontExtensionsTest {

	private static final String REPORT_TEMPLATE_CHECK_FONT_AT_PDF = "check_font_pdf.jrxml";
	private static final String REPORT1_DATASOURCE = "201805281421570431_5491.xml";

	private static final Report[] REPORTS = {Report.builder().template(REPORT_TEMPLATE_CHECK_FONT_AT_PDF)
			.inputDataUri(REPORT1_DATASOURCE).build()};

	@Before
	public void setUp() {
		try {
			for (Report report : REPORTS) {
				Files.walk(Paths.get(ClassLoader.getSystemResource(report.getTemplate()).toURI()).getParent())
						.filter(Files::isRegularFile)
						.map(Path::toFile)
						.filter(file -> getExtension(file.getName()).equalsIgnoreCase("jasper"))
						.forEach(File::delete);
			}
		} catch (URISyntaxException | IOException e) {
			throw new RuntimeException(e);
		}
		createOutputFolder();
	}

	private void createOutputFolder() {
		new File("./out").mkdirs();
	}

	@Parameters
	public static Collection<Object[]> data() {
		Collection<Object[]> data = new ArrayList<>();
		for (Report report : REPORTS) {
			try {
				data.add(new Object[]{Paths.get(ClassLoader.getSystemResource(report.getTemplate()).toURI()).toString(),
						ClassLoader.getSystemResource(report.getInputDataUri()).getPath(),
						ExportType.PDF.name().toLowerCase(),
						generateOutputFileName(removeExtension(report.getTemplate()), ExportType.PDF.name().toLowerCase())});
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		}
		return data;
	}

	private static String generateOutputFileName(String template, String type) {
		return String.format("./out/%s__%s.%s", template, new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()), type);
	}

	@Parameter
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
		JasperExporter endpoint = new JasperExporter(templateArg(template), uriArg(uri),
				typeArg(type), outputFileArg(outputFile));
		assertEquals(ExitCode.NORMAL.getCode(), endpoint.buildReport());
	}
}