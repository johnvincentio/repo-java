package com.idc.test.pdf;

import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class ImagesPDF {
	private static final String OUTPUT_PDF = "C:/irac7/wrkspc/Rda/Pdf/test/imagesPDF.pdf";
	private static final String IMAGE = "C:/irac7/wrkspc/Rda/Pdf/images/a.jpg";

	public static void main(String arg[]) throws Exception {
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream (OUTPUT_PDF));
		document.open();
		Image image = Image.getInstance(IMAGE);
		document.add(new Paragraph("Test image"));
		document.add(image);
		document.close();
	}
}
