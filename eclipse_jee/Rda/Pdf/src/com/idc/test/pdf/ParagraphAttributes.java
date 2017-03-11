package com.idc.test.pdf;

import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

/*
   1. Element.ALIGN_LEFT
   2. Element.ALIGN_CENTER
   3. Element.ALIGN_RIGHT
   4. Element.ALIGN_JUSTIFIED
*/

public class ParagraphAttributes {
	private static final String OUTPUT_PDF = "C:/irac7/wrkspc/Rda/Pdf/test/pdf/ParagraphAttributes.pdf";

	public static void main(String[] args) throws Exception {
		System.out.println("The Paragraph object");
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(OUTPUT_PDF));
		document.open();
		Paragraph[] p = new Paragraph[5];
		p[0] = new Paragraph("RoseIndia.net");
		p[1] = new Paragraph("RoseIndia.net");
		p[2] = new Paragraph("RoseIndia.net");
		p[3] = new Paragraph("RoseIndia.net");
		p[4] = new Paragraph("RoseIndia.net");
		for (int i = 0; i < 5; i++) {
			p[i].setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(p[i]);
		}
		document.close();
	}
}
