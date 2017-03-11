package com.idc.pdf;

import java.io.FileOutputStream;

import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfWriter;

public class SectionPDF {
	public static void main(String arg[]) throws Exception {
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream("sectionPDF.pdf"));
		document.open();
		Chapter chapter = new Chapter(new Paragraph("Chapter1"), 1);
		Section section1 = chapter.addSection(new Paragraph("Section1"));
		section1.add(new Paragraph("Rose India"));
		Section section2 = chapter.addSection(new Paragraph("section2"));
		section2.add(new Paragraph("roseinia.net"));
		document.add(chapter);
		document.close();
	}
}
