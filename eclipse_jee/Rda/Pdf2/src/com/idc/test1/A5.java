package com.idc.test1;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfWriter;

public class A5 {
	public static final String RESULT = "results/a5.pdf";

	public static void main(String[] args) throws Exception {
		A5 a5 = new A5();
		a5.doTest();
	}

	private void doTest() throws Exception {
		Document document = new Document();

		try {
			PdfWriter.getInstance(document, new FileOutputStream(RESULT));

			document.open();

			Paragraph paragraph = new Paragraph();
			paragraph.add(new Phrase("This is a chapter."));

			Chapter chapter = new Chapter(paragraph, 1);

			Section section1 = chapter.addSection("This is section 1", 2);
			Section section2 = chapter.addSection("This is section 2", 2);

			document.add(chapter);

			document.close();

		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
}
