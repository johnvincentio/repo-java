package com.idc.test.pdf;

import java.io.FileOutputStream;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;

public class Phrases {
	private static final String OUTPUT_PDF = "C:/irac7/wrkspc/Rda/Pdf/test/pdf/Phrases.pdf";

	public static void main(String[] args) throws Exception {
		System.out.println("Phrases");
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(OUTPUT_PDF));
		document.open();
		Phrase phrase0 = new Phrase();
		Phrase phrase1 = new Phrase("This is a phrase\n");
		Phrase phrase2 = new Phrase(25, "This is a phrase with leading 25. \n");
		Phrase phrase3 = new Phrase(
				"This is a phrase with a red, normal font Courier20",
				FontFactory.getFont(FontFactory.COURIER, 20, Font.NORMAL, new BaseColor(255, 0, 0)));
		Phrase phrase4 = new Phrase(new Chunk("(4) this is a phrase\n"));
		Phrase phrase5 = new Phrase(
				18,
				new Chunk(
						"This is a phrase in Helvetica, bold, red and size 16 with a given leading of 18 points.\n",
						FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD, new BaseColor(255, 0, 0))));
		Chunk chunk = new Chunk("This is a font: ");
		Phrase phrase6 = new Phrase();
		phrase6.add(chunk);
		phrase6.add(new Chunk("Helvetica", FontFactory.getFont(FontFactory.HELVETICA, 12)));
		phrase6.add(chunk);
		phrase6.add(new Chunk("ZapfDingBats", FontFactory.getFont(FontFactory.ZAPFDINGBATS, 12)));
		Phrase phrase7 = new Phrase("If you don't add a newline !");
		document.add(phrase1);
		document.add(phrase2);
		document.add(phrase3);
		document.add(phrase4);
		document.add(phrase5);
		document.add(phrase6);
		document.add(phrase7);
		document.close();
	}
}
