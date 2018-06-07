package com.microting.report.jasper;

public enum ExportType {

	PDF, XLS, XLSX, DOC, DOCX, RTF, ODT, PPT, PPTX;

	public static ExportType byName(String name) {
		for (ExportType type : ExportType.values()) {
			if (type.name().equalsIgnoreCase(name)) {
				return type;
			}
		}
		return null;
	}
}