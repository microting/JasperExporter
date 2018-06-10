package com.microting.report.jasper;

import com.microting.report.jasper.JasperExporter.ExitCode;
import org.junit.Test;

import static org.junit.Assert.*;

public class NegativeArgumentsTest {

	@Test
	public void testArguments_WrongNumberOfArguments_Failed() {
		JasperExporter endpoint = new JasperExporter("wrong_argument");
		assertEquals(ExitCode.WRONG_ARGUMENTS.getCode(), endpoint.buildReport());
	}

	@Test
	public void testArguments_WrongArgumentDeclaration_Failed() {
		JasperExporter endpoint = new JasperExporter("arg1", "arg2", "arg3", "arg4");
		assertEquals(ExitCode.WRONG_ARGUMENTS.getCode(), endpoint.buildReport());
	}

	@Test
	public void testArguments_UnsupportedArgument_Failed() {
		JasperExporter endpoint = new JasperExporter("-arg1=agrVal1", "-arg2=agrVal2", "-arg3=agrVal3", "-arg4=argVal4");
		assertEquals(ExitCode.WRONG_ARGUMENTS.getCode(), endpoint.buildReport());
	}
}