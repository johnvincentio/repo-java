package com.idc.test1;

import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class A1 {
	public static final String RESULT = "results/a1.pdf";

	public static void main(String[] args) throws Exception {
		A1 a1 = new A1();
		a1.doTest();
	}

	private void doTest() throws DocumentException, IOException {
		Document document = new Document();
		PdfWriter.getInstance (document, new FileOutputStream(RESULT));
		document.setPageSize (PageSize.A4);
		document.setMargins(36, 36, 36, 36);
		document.open();

		document.add(new Paragraph(
				"The left margin of this odd page is 36pt (0.5 inch); "
						+ "the right margin 72pt (1 inch); "
						+ "the top margin 108pt (1.5 inch); "
						+ "the bottom margin 180pt (2.5 inch)."));
		Paragraph paragraph = new Paragraph();
		paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
		for (int i = 0; i < 20; i++) {
			paragraph.add("Hello World! Hello People! "
					+ "Hello Sky! Hello Sun! Hello Moon! Hello Stars!");
		}
		document.add(paragraph);
		document.add(new Paragraph(
				"The right margin of this even page is 36pt (0.5 inch); "
						+ "the left margin 72pt (1 inch)."));
		// step 5
		document.close();
	}
}
