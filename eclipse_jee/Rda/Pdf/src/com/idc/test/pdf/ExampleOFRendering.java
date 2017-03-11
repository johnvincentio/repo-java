package com.idc.test.pdf;

import java.io.FileOutputStream;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

public class ExampleOFRendering {
	private static final String OUTPUT_PDF = "C:/irac7/wrkspc/Rda/Pdf/test/pdf/ExampleOFRendering.pdf";

	public static void main(String[] args) throws Exception {
		System.out.println("Example of Rendering");
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(OUTPUT_PDF));
		document.open();
		Paragraph p = new Paragraph("Rose Text Rendering:");
		document.add(p);
		Chunk chunk = new Chunk("Rendering Test");
		chunk.setTextRenderMode(PdfContentByte.TEXT_RENDER_MODE_FILL, 100f, new BaseColor(0xc0, 0x00, 0x0f));
		document.add(chunk);
		document.add(Chunk.NEWLINE);
		chunk.setTextRenderMode(PdfContentByte.TEXT_RENDER_MODE_FILL_STROKE, 0.3f, new BaseColor(0xFc, 0x00, 0x00));
		document.add(chunk);
		document.add(Chunk.NEWLINE);
		chunk.setTextRenderMode(PdfContentByte.TEXT_RENDER_MODE_INVISIBLE, 100f, new BaseColor(0x0c, 0xF0, 0x00));
		document.add(chunk);
		document.add(Chunk.NEWLINE);
		chunk.setTextRenderMode(PdfContentByte.TEXT_RENDER_MODE_FILL_STROKE, 0.3f, new BaseColor(0x0c, 0x00, 0xF0));
		document.add(chunk);
		document.add(Chunk.NEWLINE);
		Chunk bold = new Chunk("This looks like STROKE for Bold look");
		bold.setTextRenderMode(PdfContentByte.TEXT_RENDER_MODE_FILL_STROKE, 0.5f, new BaseColor(0x0c, 0x00, 0x00));
		document.add(bold);
		document.close();
	}
}
