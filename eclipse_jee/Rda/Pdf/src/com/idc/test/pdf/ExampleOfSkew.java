package com.idc.test.pdf;

import java.io.FileOutputStream;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class ExampleOfSkew {
	private static final String OUTPUT_PDF = "C:/irac7/wrkspc/Rda/Pdf/test/pdf/ExampleOfSkew.pdf";

	public static void main(String[] args) throws Exception {
		System.out.println("Example of Skew");
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(OUTPUT_PDF));
		document.open();
		Paragraph p = new Paragraph("Rose India.net");
		document.add(p);
		Chunk chunk = new Chunk("Rose India.net");
		chunk.setSkew(45f, 0f);
		document.add(chunk);
		document.add(Chunk.NEWLINE);
		chunk.setSkew(0f, 45f);
		document.add(chunk);
		document.add(Chunk.NEWLINE);
		chunk.setSkew(-45f, 0f);
		document.add(chunk);
		document.add(Chunk.NEWLINE);
		chunk.setSkew(0f, -45f);
		document.add(chunk);
		document.add(Chunk.NEWLINE);
		chunk.setSkew(16f, 35f);
		document.add(chunk);
		document.add(Chunk.NEWLINE);
		Chunk italic = new Chunk("This looks like Font.ITALIC");
		italic.setSkew(0f, 15f);
		document.add(italic);
		document.close();
	}
}
