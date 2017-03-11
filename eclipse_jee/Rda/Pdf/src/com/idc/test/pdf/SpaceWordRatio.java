package com.idc.test.pdf;

import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class SpaceWordRatio {
	private static final String OUTPUT_PDF = "C:/irac7/wrkspc/Rda/Pdf/test/pdf/SpaceWordRatio.pdf";

	public static void main(String[] args) throws Exception {
		System.out.println("Example of Space Word Ratio");
		Document document = new Document(PageSize.A4, 50, 350, 50, 50);
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(OUTPUT_PDF));
		document.open();
		String input = "Rose india is a java site which provide you online tutorial and online interview question help";
		Paragraph paragraph = new Paragraph(input);
		paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(paragraph);
		document.newPage();
		writer.setSpaceCharRatio(PdfWriter.NO_SPACE_CHAR_RATIO);
		document.add(paragraph);
		document.close();
	}
}
