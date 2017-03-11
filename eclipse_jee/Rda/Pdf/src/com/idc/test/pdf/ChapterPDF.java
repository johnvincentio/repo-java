package com.idc.test.pdf;

import java.io.FileOutputStream;

import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class ChapterPDF {
	private static final String OUTPUT_PDF = "C:/irac7/wrkspc/Rda/Pdf/test/pdf/ChapterPDF.pdf";

	public static void main(String arg[]) throws Exception {
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(OUTPUT_PDF));
		document.open();
		Paragraph paragraph = new Paragraph("Rose-Java");
		Paragraph paragraph1 = new Paragraph("Rose-Java1");
		Paragraph paragraph2 = new Paragraph("Rose-Java2");
		Paragraph paragraph3 = new Paragraph("Rose-Java3");
		Paragraph paragraph4 = new Paragraph("Rose-Java4");
		Paragraph paragraph5 = new Paragraph("Rose-Java5");
		Chapter chapter = new Chapter(paragraph, 0);
		Chapter chapter1 = new Chapter(paragraph1, 1);
		Chapter chapter2 = new Chapter(paragraph2, 2);
		Chapter chapter3 = new Chapter(paragraph3, 3);
		Chapter chapter4 = new Chapter(paragraph4, 4);
		Chapter chapter5 = new Chapter(paragraph5, 5);
		document.add(chapter);
		document.add(chapter1);
		document.add(chapter2);
		document.add(chapter3);
		document.add(chapter4);
		document.add(chapter5);
		document.close();
	}
}