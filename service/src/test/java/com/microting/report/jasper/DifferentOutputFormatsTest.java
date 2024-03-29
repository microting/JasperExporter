package com.microting.report.jasper;

import com.microting.report.jasper.JasperExporter.ExitCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

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
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DifferentOutputFormatsTest {

    private static final String REPORT1_TEMPLATE_WITH_SUBREPORT_EXTERNAL_IMAGES = "report_without_https_images.jrxml";
    private static final String REPORT1_DATASOURCE = "201805281421570431_5491.xml";

    private static final Report[] REPORTS = {Report.builder().template(REPORT1_TEMPLATE_WITH_SUBREPORT_EXTERNAL_IMAGES)
            .inputDataUri(REPORT1_DATASOURCE).build()};

    @BeforeEach
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

    public static Collection<Object[]> data() {
        Collection<Object[]> data = new ArrayList<>();
        for (Report report : REPORTS) {
            for (ExportType exportType : ExportType.values()) {
                try {
                    data.add(new Object[]{Paths.get(ClassLoader.getSystemResource(report.getTemplate()).toURI()).toString(),
                            ClassLoader.getSystemResource(report.getInputDataUri()).getPath(),
                            exportType.name().toLowerCase(),
                            generateOutputFileName(removeExtension(report.getTemplate()), exportType.name().toLowerCase())});
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return data;
    }

    private static String generateOutputFileName(String template, String type) {
        return String.format("./out/%s__%s.%s", template, new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()), type);
    }

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

    @ParameterizedTest
    @MethodSource("data")
    public void buildReport(String template, String uri, String type, String outputFile) {
        JasperExporter endpoint = new JasperExporter(templateArg(template), uriArg(uri),
                typeArg(type), outputFileArg(outputFile));
        assertEquals(ExitCode.NORMAL.getCode(), endpoint.buildReport());
    }
}