package com.idc.test.pdf;

import java.io.FileOutputStream;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

public class ExampleOfEndOfLine {
	private static final String OUTPUT_PDF = "C:/irac7/wrkspc/Rda/Pdf/test/pdf/ExampleOfEndOfLine.pdf";

	public static void main(String[] args) throws Exception {
		System.out.println("Example of End of Line");
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(OUTPUT_PDF));
		document.open();
		Chunk chunk = new Chunk("Rose India Net-->Rose India .Net ");
		for (int i = 0; i < 5; i++) {
			chunk.setTextRenderMode(PdfContentByte.TEXT_RENDER_MODE_STROKE, 0.5f, new BaseColor(i * 35, i * 30, i * 35));
			document.add(chunk);
		}
		document.newPage();
		Phrase p = new Phrase(16f);
		for (int i = 0; i < 5; i++) {
			chunk = new Chunk("Rose India .Net-->Rose India .Net ");
			chunk.setTextRenderMode(PdfContentByte.TEXT_RENDER_MODE_STROKE, 0.5f, new BaseColor(i * 35, i * 30, i * 35));
			p.add(chunk);
		}
		document.add(p);
		document.close();
	}
}