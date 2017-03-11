package com.idc.test.pdf;

import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;

public class NegativeLeading {
	private static final String OUTPUT_PDF = "C:/irac7/wrkspc/Rda/Pdf/test/pdf/NegativeLeading.pdf";

	public static void main(String[] args) throws Exception {
		System.out.println("Negative Leading");
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(OUTPUT_PDF));
		document.open();
		document
				.add(new Phrase(
						16,
						"Rose india provides online java interview question and answer.Rose india provides online java"));
		document
				.add(new Phrase(
						-5,
						"Rose india provides online java interview question and answer.Rose india provides online java"));
		document.close();
	}
}
