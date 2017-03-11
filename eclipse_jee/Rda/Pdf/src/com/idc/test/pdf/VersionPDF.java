package com.idc.test.pdf;

import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

public class VersionPDF {
	private static final String OUTPUT_PDF = "C:/irac7/wrkspc/Rda/Pdf/test/pdf/VersionPDF.pdf";

	public static void main(String[] args) throws Exception {
		System.out.println("Example of a Find version of iText");
		Rectangle pageSize = new Rectangle(288, 720);
		Document document = new Document(pageSize, 36, 18, 72, 72);
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(OUTPUT_PDF));
		document.open();
		document.add(new Paragraph("Version Of iText-->Rose India"));
		document.add(new Paragraph("This page was made using " + Document.getVersion()));
		document.add(new Paragraph("-->>RoseIndai.net"));
		document.close();
	}
}
