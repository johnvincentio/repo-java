package com.idc.test1;

import java.io.FileOutputStream;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

public class Pdf1 {
	private static final String OUTPUT_PDF = "C:/irac7/wrkspc/Rda/Pdf/output/pdf1.pdf";;
	private static final String IMAGE = "C:/irac7/wrkspc/Rda/Pdf/images/hertz.jpg";

	public static void main(String arg[]) throws Exception {
		Pdf1 pdf1 = new Pdf1();
		pdf1.test();
	}
	public void test() throws Exception {
		Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance (document, new FileOutputStream (OUTPUT_PDF));
		writer.setCompressionLevel(0);		//not sure about this
		document.open();

		doImage (document);

//		document.newPage(); doParagraph (document);

		document.newPage(); doTest1 (writer);

//		document.newPage(); doTest2 (document);

//		document.newPage(); doSections (document);

		document.close();
	}

	private void doImage (Document document) throws Exception {
		Image image = Image.getInstance(IMAGE);
		document.add(new Paragraph("Test image"));
		document.add(image);
	}
	private void doParagraph (Document document) throws Exception {
		document.add(new Paragraph("Page 2"));
	}
	private void doTest1 (PdfWriter writer) throws Exception {
		Phrase hello = new Phrase("Hello World");
		PdfContentByte canvas = writer.getDirectContentUnder();
		ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, hello, 36, 788, 0);
	}
	private void doTest2 (Document document) throws Exception {
		document.add(new Paragraph("Page doTest2"));
		Paragraph title2 = new Paragraph("This is Chapter 2", FontFactory.getFont(FontFactory.HELVETICA, 18, Font.BOLDITALIC, new BaseColor(0, 0, 255)));
		Chapter chapter2 = new Chapter(title2, 2);
		Paragraph someText = new Paragraph("This is some text");
		chapter2.add(someText);
		document.add(chapter2);

		Paragraph title21 = new Paragraph("This is Section 1 in Chapter 2", FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD, new BaseColor(255, 0, 0)));
		Section section1 = chapter2.addSection(title21);
		Paragraph someSectionText = new Paragraph("This is some silly paragraph in a chapter and/or section. It contains some text to test the functionality of Chapters and Section.");
		section1.add(someSectionText);
		document.add(section1);

		Paragraph title211 = new Paragraph("This is SubSection 1 in Section 1 in Chapter 2", FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD, new BaseColor(255, 0, 0)));
		Section section11 = section1.addSection(40, title211, 2);
		section11.add(someSectionText);
		document.add(section11);
	}
	private void doSections (Document document) throws Exception {
		Chapter chapter = new Chapter(new Paragraph("Chapter1"), 1);
		Section section1 = chapter.addSection(new Paragraph("Section1"));
		section1.add(new Paragraph("Rose India"));
		Section section2 = chapter.addSection(new Paragraph("section2"));
		section2.add(new Paragraph("roseinia.net"));
		document.add(chapter);
	}
}
