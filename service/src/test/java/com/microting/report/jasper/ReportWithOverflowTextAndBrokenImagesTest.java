package com.microting.report.jasper;

import net.sf.jasperreports.data.cache.ColumnValues;
import net.sf.jasperreports.data.cache.ColumnValuesDataSource;
import net.sf.jasperreports.data.cache.SingleObjectValue;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

@RunWith(Parameterized.class)
public class ReportWithOverflowTextAndBrokenImagesTest {

	@Before
	public void setUp() {
		createOutputFolder();
	}

	private void createOutputFolder() {
		new File("./out").mkdirs();
	}

	@Parameter
	public String date;

	@Parameter(1)
	public String auditor;

	@Parameter(2)
	public String plads;

	@Parameter(3)
	public String miljopolitik_comment;

	@Parameter(4)
	public String miljopolitik_status;

	@Parameter(5)
	public String miljo_plan_comment;

	@Parameter(6)
	public String miljo_plan_status;

	@Parameter(7)
	public String validImage;

	@Parameter(8)
	public String wrongImage;

	@Parameters
	public static Collection<Object[]> data() {
		Collection<Object[]> data = new ArrayList<>();
		data.add(new Object[]{"", "", "", "", "", "", "", "", ""});
		data.add(new Object[]{"01.09.2018", "auditor number one", "some string", "comment 1", "status1", "comment 2", "status2", "", ""});
		data.add(new Object[]{"01.09.2018", "See you later alligator, after 'while crocodile. See you later alligator, after 'while crocodile. Can't you see you're in my way now.",
				"Walking in the jungle. Walking in the jungle. We’re not afraid. We’re not afraid.",
				"Five little monkeys jumping on the bed.  One fell off and bumped his head. Mama called the doctor. And the doctor said: No more monkeys jumping on the bed. Four little monkeys jumping on the bed. One fell off and bumped his head.Mama called the doctor.  And the doctor said: No more monkeys jumping on the bed. Three little monkeys jumping on the bed. One fell off and bumped his head. Mama called the doctor. And the doctor said: No more monkeys jumping on the bed",
				"Status1",
				"Twinkle twinkle little star. How I wonder what you are. Up above the world so high. Like a diamond in the sky. Twinkle twinkle little star. How I wonder what you are.",
				"Status2",
				"http://www.drawingteachers.com/image-files/how-to-draw-a-cartoon-fish-200px.jpg",
				"http://www.22drawingteachers22.com/image-files/how-to-draw-a-cartoon-fish-200px.jpg"});
		return data;
	}

	@Test
	public void testReport() {
		try {
			JasperReport jasperReport;
			try (InputStream inputStream = JRLoader.getResourceInputStream("test_with_image.jrxml")) {
				jasperReport = JasperCompileManager.compileReport(JRXmlLoader.load(inputStream));
			}
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(),
					new ColumnValuesDataSource(new String[]{"date", "auditor", "plads", "miljopolitik_comment",
							"miljopolitik_status", "miljo_plan_comment", "miljo_plan_status", "validImage", "wrongImage", "nullImage"}, 1, new ColumnValues[]{
							new SingleObjectValue(date),
							new SingleObjectValue(auditor),
							new SingleObjectValue(plads),
							new SingleObjectValue(miljopolitik_comment),
							new SingleObjectValue(miljopolitik_status),
							new SingleObjectValue(miljo_plan_comment),
							new SingleObjectValue(miljo_plan_status),
							new SingleObjectValue(validImage),
							new SingleObjectValue(wrongImage),
							new SingleObjectValue(null)
					}));

			Exporter exporter = new JRPdfExporter();
			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			SimpleOutputStreamExporterOutput exporterOutput = null;
			try {
				File file = new File("./out/test_with_image__" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".pdf");

				exporterOutput = new SimpleOutputStreamExporterOutput(file);
				exporter.setExporterOutput(exporterOutput);
				exporter.exportReport();
			} finally {
				if (exporterOutput != null) {
					exporterOutput.close();
				}
			}
		} catch (IOException | JRException e) {
			e.printStackTrace();
		}
	}
}