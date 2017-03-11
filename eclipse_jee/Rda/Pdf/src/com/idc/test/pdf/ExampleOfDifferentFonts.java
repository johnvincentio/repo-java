package com.idc.test.pdf;

import java.io.FileOutputStream;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class ExampleOfDifferentFonts {
	private static final String OUTPUT_PDF = "C:/irac7/wrkspc/Rda/Pdf/test/pdf/ExampleOfDifferentFonts.pdf";

	public static void main(String[] args) throws Exception {
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(OUTPUT_PDF));
		document.open();
		Paragraph p = new Paragraph();
		p.add(new Chunk("This text is in Times Roman. ", FontFactory.getFont(FontFactory.TIMES_ROMAN, 12)));
		p.add(new Chunk("This is ZapfDingbats: ", FontFactory.getFont(FontFactory.ZAPFDINGBATS, 12)));
		p.add(new Chunk(". This is font Symbol: ", FontFactory.getFont(FontFactory.TIMES_ROMAN, 12)));
		p.add(new Chunk("This text is in Times Roman.", FontFactory.getFont(FontFactory.SYMBOL, 12)));
		document.add(new Paragraph(p));
		document.close();
	}
}
