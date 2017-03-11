package com.idc.test.pdf;

import java.io.FileOutputStream;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;

public class ExampleOfSubSupScript {
	private static final String OUTPUT_PDF = "C:/irac7/wrkspc/Rda/Pdf/test/pdf/ExampleOfSubSupScript.pdf";

	public static void main(String[] args) throws Exception {
		System.out.println("Sub- and Superscript");
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(OUTPUT_PDF));
		document.open();
		Chunk c1, c2, c3;
		c1 = new Chunk("Rose");
		c1.setTextRise(7.0f);
		document.add(c1);
		c2 = new Chunk("India");
		c2.setTextRise(3.0f);
		document.add(c2);
		c3 = new Chunk(".net");
		c3.setTextRise(-2.0f);
		document.add(c3);
		document.close();
	}
}