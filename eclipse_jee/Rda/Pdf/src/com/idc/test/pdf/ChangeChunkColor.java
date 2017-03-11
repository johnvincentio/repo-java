package com.idc.test.pdf;

import java.io.FileOutputStream;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class ChangeChunkColor {
	private static final String OUTPUT_PDF = "C:/irac7/wrkspc/Rda/Pdf/test/pdf/ChangeChunkColor.pdf";

	public static void main(String[] args) throws Exception {
		System.out.println("FontColor");
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(OUTPUT_PDF));
		document.open();
		Paragraph paragraph;
		paragraph = new Paragraph("Roses");
		paragraph.add(new Chunk("india", FontFactory.getFont(FontFactory.HELVETICA, Font.DEFAULTSIZE, Font.BOLD, new BaseColor(0xFF, 0x00, 0x00))));
		document.add(paragraph);
		paragraph = new Paragraph("India");
		paragraph.add(new Chunk(".net", FontFactory.getFont(FontFactory.HELVETICA, Font.DEFAULTSIZE, Font.ITALIC, new BaseColor(0x00, 0x00, 0xFF))));
		document.add(paragraph);
		document.close();
	}
}
