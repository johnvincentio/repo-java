package com.idc.test.pdf;

import java.io.FileOutputStream;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class MeasureOfWidth {
	private static final String OUTPUT_PDF = "C:/irac7/wrkspc/Rda/Pdf/test/pdf/MeasureOfWidth.pdf";

	public static void main(String[] args) throws Exception {
		System.out.println("Example of measure Width of the chunk");
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(OUTPUT_PDF));
		document.open();
		Chunk c = new Chunk("Rose India");
		float w = c.getWidthPoint();
		Paragraph p = new Paragraph("The width of the chunk: '");
		p.add(c);
		p.add("' is ");
		p.add(String.valueOf(w));
		p.add(" points or ");
		p.add(String.valueOf(w / 72f));
		p.add(" inches.");
		document.add(p);
		document.add(c);
		document.add(Chunk.NEWLINE);
		c.setHorizontalScaling(0.5f);
		document.add(c);
		document.add(c);
		document.close();
	}
}
