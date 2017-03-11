package com.idc.test.pdf;

import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class A1 {
	public static final String RESULT = "/home/jv/vc/projects/Rda/Pdf/a1.pdf";

	public static void main(String[] args) throws DocumentException, IOException {
		new A1().createPdf(RESULT);
	}

	public void createPdf(String filename) throws DocumentException, IOException {
		Document document = new Document();
		PdfWriter.getInstance (document, new FileOutputStream (filename));
		Image image = Image.getInstance ("alet001s.jpg");
		document.open();
		document.add (new Paragraph ("Hello World!"));
		document.add (image);
		document.close();
	}
}