package com.idc.test.pdf;

import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

public class HelloWorldMaximum {
	public static final String RESULT = "hello_maximum.pdf";

	/**
	 * Creates a PDF file: hello_maximum.pdf Important notice: the PDF is valid
	 * (in conformance with ISO-32000), but some PDF viewers won't be able to
	 * render the PDF correctly due to their own limitations.
	 * 
	 * @param args
	 *            no arguments needed
	 */
	public static void main(String[] args) throws DocumentException,
			IOException {
		// step 1
		// maximum page size
		Document document = new Document(new Rectangle(14400, 14400));
		// step 2
		PdfWriter writer = PdfWriter.getInstance(document,
				new FileOutputStream(RESULT));
		// changes the user unit
		writer.setUserunit(75000f);
		// step 3
		document.open();
		// step 4
		document.add(new Paragraph("Hello World"));
		// step 5
		document.close();
	}
}
