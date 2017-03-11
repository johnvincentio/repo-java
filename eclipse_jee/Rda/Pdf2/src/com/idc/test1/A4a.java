package com.idc.test1;

import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class A4a {
	public static final String RESULT = "results/a4a.pdf";

	public static void main (String[] args) throws Exception {
		A4a a4a = new A4a();
		a4a.doTest();
	}
	private void doTest() throws Exception {
		Document document = new Document();
		PdfWriter.getInstance (document, new FileOutputStream(RESULT));
		document.open();
//		document.add (new Paragraph ("Hello World!"));
		
		Image image = Image.getInstance ("resources/hertz.jpg");
		image.setRotationDegrees(45f);
		document.add (image);

		document.newPage();
		image = Image.getInstance ("resources/herc_pdf_watermark.gif");
		image.setRotationDegrees(90f);
		image.scalePercent(30f);
		document.add (image);
		document.close();
	}
}
