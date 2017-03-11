package com.idc.pdf;

import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class HelloWorldMirroredMargins {
	public static final String RESULT = "hello_mirrored_margins.pdf";

	/**
	 * Creates a PDF file: hello_mirrored_margins.pdf
	 * 
	 * @param args
	 *            no arguments needed
	 */
	public static void main(String[] args) throws DocumentException,
			IOException {
		// step 1
		Document document = new Document();
		// step 2
		PdfWriter.getInstance(document, new FileOutputStream(RESULT));
		document.setPageSize(PageSize.A5);
		document.setMargins(36, 72, 108, 180);
		document.setMarginMirroring(true);
		// step 3
		document.open();
		// step 4
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