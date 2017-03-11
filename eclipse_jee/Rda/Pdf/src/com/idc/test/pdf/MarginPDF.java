package com.idc.test.pdf;

import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class MarginPDF {
	private static final String OUTPUT_PDF = "C:/irac7/wrkspc/Rda/Pdf/test/pdf/MarginPDF.pdf";

	public static void main(String arg[]) throws Exception {
		Paragraph paragraph = new Paragraph();
		Document document = new Document(PageSize.A4, 36, 72, 108, 180);
		PdfWriter.getInstance(document, new FileOutputStream(OUTPUT_PDF));
		document.open();
		document.add(new Paragraph("Margin--->>roseinia.net"));
		document.setMargins(180, 108, 71, 10);
		document.add(new Paragraph(
				"You can visit roseindia.net for more java tutorials"));
		for (int i = 0; i < 200; i++) {
			paragraph
					.add("Hello roseindia.net,Hello roseindia.net,Hello roseindia.net,Hello roseindia.net,Hello roseindia.net,Hello roseindia.net,Hello roseindia.net,Hello roseindia.net.");
		}
		document.setMarginMirroring(true);
		document.add(new Paragraph("Starting on the next page, the margins will be mirrored."));
		document.add(paragraph);
		document.close();
	}
}
