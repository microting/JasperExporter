/*    */ package com.microting.report.jasper.convertion;
/*    */ 
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.OutputStream;
/*    */ import net.sf.jasperreports.engine.JRException;
/*    */ import net.sf.jasperreports.engine.JRExporter;
/*    */ import net.sf.jasperreports.engine.JRExporterParameter;
/*    */ import net.sf.jasperreports.engine.JasperExportManager;
/*    */ import net.sf.jasperreports.engine.JasperPrint;
/*    */ import net.sf.jasperreports.engine.export.JExcelApiExporter;
/*    */ import net.sf.jasperreports.engine.export.JRRtfExporter;
/*    */ import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
/*    */ import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
/*    */ import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
/*    */ import net.sf.jasperreports.engine.export.ooxml.JRDocxExporterParameter;
/*    */ import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
/*    */ 
/*    */ public class JasperReportConvertor
/*    */ {
/*    */   private final String exportType;
/*    */   private final OutputStream outputStream;
/*    */   
/*    */   public JasperReportConvertor(OutputStream outputStream, String exportType)
/*    */   {
/* 25 */     this.outputStream = outputStream;
/* 26 */     this.exportType = exportType.trim().replace(".", "");
/*    */   }
/*    */   
/*    */   public void convert(JasperPrint jasperPrint) throws ReportConversionException {
/*    */     try {
/* 31 */       if ("pdf".equalsIgnoreCase(this.exportType)) {
/* 32 */         JasperExportManager.exportReportToPdfStream(jasperPrint, this.outputStream);
/* 33 */       } else if (("xls".equalsIgnoreCase(this.exportType)) || ("xlsx".equalsIgnoreCase(this.exportType))) {
/* 34 */         exportToExcel(jasperPrint);
/* 35 */       } else if (("doc".equalsIgnoreCase(this.exportType)) || ("docx".equalsIgnoreCase(this.exportType))) {
/* 36 */         exportToWord(jasperPrint);
/* 37 */       } else if ("rtf".equalsIgnoreCase(this.exportType)) {
/* 38 */         export(jasperPrint, new JRRtfExporter());
/* 39 */       } else if ("odt".equalsIgnoreCase(this.exportType)) {
/* 40 */         export(jasperPrint, new JROdtExporter());
/* 41 */       } else if (("ppt".equalsIgnoreCase(this.exportType)) || ("pptx".equalsIgnoreCase(this.exportType))) {
/* 42 */         export(jasperPrint, new JRPptxExporter());
/*    */       } else
/* 44 */         throw new IllegalArgumentException("Invalid export file type for " + this.exportType);
/*    */     } catch (Throwable e) {
/* 46 */       throw new ReportConversionException(e);
/*    */     }
/*    */   }
/*    */   
/*    */   private void exportToWord(JasperPrint jasperPrint) throws JRException
/*    */   {
/* 52 */     JRDocxExporter exporter = new JRDocxExporter();
/* 53 */     exporter.setParameter(JRDocxExporterParameter.FLEXIBLE_ROW_HEIGHT, Boolean.TRUE);
/* 54 */     export(jasperPrint, exporter);
/*    */   }
/*    */   
/*    */   private void exportToExcel(JasperPrint jasperPrint)
/*    */     throws FileNotFoundException, JRException
/*    */   {
/* 60 */     JExcelApiExporter exporter = new JExcelApiExporter();
/* 61 */     exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
/* 62 */     exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
/* 63 */     exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
/* 64 */     export(jasperPrint, exporter);
/*    */   }
/*    */   
/*    */   private void export(JasperPrint jasperPrint, JRExporter exporter) throws JRException
/*    */   {
/* 69 */     exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
/* 70 */     exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, this.outputStream);
/* 71 */     exporter.exportReport();
/*    */   }
/*    */ }


/* Location:              /Users/rene/Downloads/JasperExporter.jar!/com/microting/report/jasper/convertion/JasperReportConvertor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */