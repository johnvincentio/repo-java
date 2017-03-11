package com.idc.pdf;

import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.BaseColor;

public class A2 {
	public static void main(String arg[]) throws Exception {
		Rectangle pageSize = new Rectangle(400, 400);
		pageSize.setBackgroundColor (new BaseColor (0xDF, 0xFF, 0xDE));
		Document document = new Document(pageSize);
		PdfWriter.getInstance(document, new FileOutputStream("backgroundColorPDF.pdf"));
		document.open();
		Paragraph para = new Paragraph("Page Size and Background color");
		document.add(para);
		document.add(new Paragraph("Background color--->>roseinia.net"));
		document.close();
	}
}
