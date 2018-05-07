package com.microting.report.jasper;

import java.io.File;
import java.io.FileOutputStream;

public class JasperExporter
{
    private static final String TEMPLATE = "template";
    private static final String OUPUTFILE = "outputFile";
    private static final String URI = "uri";
    private static final String TYPE = "type";

    public static void main(String[] args)
    {
        try
        {
            JasperExporterEngine engine = new JasperExporterEngine();
            if ((args.length == 0) || (args.length != 4)) {
                printMessageAndExit(getUsageText());
            }

            String[] arrayOfString = args;int j = args.length; for (int i = 0; i < j; i++) { String arg = arrayOfString[i];
            String argValue = arg.substring(arg.indexOf("=") + 1);
            if ((argValue == null) || ("".equals(argValue))) {
                printMessageAndExit("Invalid value for arg:" + arg);
            }

            if (arg.contains("template")) {
                engine.setTemplate(argValue);
            } else if (arg.contains("uri")) {
                engine.setReportData(new java.net.URI(argValue));
            } else if (arg.contains("outputFile")) {
                if (argValue.contains(File.separator))
                    new File(argValue.substring(0, argValue.lastIndexOf(File.separator))).mkdirs();
                engine.setOutputStream(new FileOutputStream(argValue));
            } else if (arg.contains("type")) {
                engine.setExportType(argValue);
            }
        }
            engine.export();
        }
        catch (Throwable e) {
            e.printStackTrace();
            System.exit(-50);
        }
    }

    private static void printMessageAndExit(String x) {
        System.out.println(x);
        System.exit(0);
    }

    private static String getUsageText() {
        String usage = "Usage: java -jar JasperExporter.jar";
        usage = usage + " -template=<template.jrxml|template.jasper>";
        usage = usage + " -uri=<http|file|ftp://pathToMyData >";
        usage = usage + " -type=<pdf|rtf|odt|doc|docx|xls|xlsx|ppt|pptx>";
        usage = usage + " -outputFile=<myExportedFile.pdf>";
        return usage;
    }
}
