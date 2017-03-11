package com.idc.test.pdf;

import java.io.FileOutputStream;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

public class UsesOfChunks {
	private static final String OUTPUT_PDF = "C:/irac7/wrkspc/Rda/Pdf/test/pdf/UsesOfChunks.pdf";

	public static void main(String[] args) throws Exception {
		System.out.println("The Chunk object");
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(OUTPUT_PDF));
		document.open();
		Chunk chunk = new Chunk("Rose India");
		chunk.setTextRise(6.0f);
		chunk.setBackground(new BaseColor(0xFc, 0xDc, 0xAc));
		Chunk chunk3 = new Chunk(" roseindia.net ");
		Chunk chunk2 = new Chunk("rose");
		chunk2.setTextRise(-8.0f);
		chunk2.setUnderline(new BaseColor(0xFF, 0x0c, 0x0c), 3.0f, 0.0f, 3.0f, 0.0f, PdfContentByte.LINE_CAP_ROUND);
		document.add(chunk);
		document.add(chunk2);
		document.add(chunk3);
		document.close();
	}
}
