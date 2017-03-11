package com.idc.test.pdf;

import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class MetaData {
	private static final String OUTPUT_PDF = "C:/irac7/wrkspc/Rda/Pdf/test/pdf/MetaData.pdf";

	public static void main(String[] args) throws Exception {
		System.out.println("Meta data");
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(OUTPUT_PDF));
		document.addTitle("Meta Data");
		document.addSubject("This example explains how to add metadata.");
		document.addKeywords("Rose India");
		document.addCreator("My program using iText");
		document.addAuthor("Rajesh Kumar");
		document.open();
		document.add(new Paragraph("Rose india"));
		document.close();
	}
}
