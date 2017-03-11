package com.idc.test.pdf;

import java.io.FileOutputStream;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

public class Lines {
	private static final String OUTPUT_PDF = "C:/irac7/wrkspc/Rda/Pdf/test/pdf/Lines.pdf";

	public static void main(String[] args) throws Exception {
		System.out.println("Underline, Strike through,...");
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(OUTPUT_PDF));
		document.open();
		Chunk underlined = new Chunk("Rose India-->>underlined");
		underlined.setUnderline(0.2f, -2f);
		Paragraph p = new Paragraph("The following chunk is ");
		p.add(underlined);
		document.add(p);
		Chunk strikethru = new Chunk("Rose India-->>Strike");
		strikethru.setUnderline(0.5f, 3f);
		document.add(strikethru);
		document.add(Chunk.NEWLINE);
		document.add(Chunk.NEWLINE);
		document.add(Chunk.NEWLINE);
		Chunk c;
		c = new Chunk("rose India-->>Multiple lines");
		c.setUnderline(new BaseColor(0xFF, 0x0f, 0x0f), 0.0f, 0.3f, 0.0f, 0.4f,
				PdfContentByte.LINE_CAP_ROUND);
		c.setUnderline(new BaseColor(0x0f, 0xFF, 0x0f), 5.0f, 0.0f, 0.0f, -0.5f,
				PdfContentByte.LINE_CAP_PROJECTING_SQUARE);
		c.setUnderline(new BaseColor(0x00, 0x0f, 0xFF), 0.0f, 0.2f, 15.0f, 0.0f,
				PdfContentByte.LINE_CAP_BUTT);
		document.add(c);
		document.add(Chunk.NEWLINE);
		document.add(Chunk.NEWLINE);
		document.add(Chunk.NEWLINE);
		c = new Chunk("Multiple lines", FontFactory.getFont(
				FontFactory.HELVETICA, 24));
		c.setUnderline(new BaseColor(0xFF, 0x0f, 0x00), 0.0f, 0.3f, 0.0f, 0.4f,
				PdfContentByte.LINE_CAP_ROUND);
		c.setUnderline(new BaseColor(0x0f, 0xF0, 0x00), 5.0f, 0.0f, 0.0f, -0.5f,
				PdfContentByte.LINE_CAP_PROJECTING_SQUARE);
		c.setUnderline(new BaseColor(0x0f, 0x0f, 0xFF), 0.0f, 0.2f, 15.0f, 0.0f,
				PdfContentByte.LINE_CAP_BUTT);
		document.add(c);
		document.close();
	}
}
