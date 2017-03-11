/*
 * This class is part of the book "iText in Action - 2nd Edition"
 * written by Bruno Lowagie (ISBN: 9781935182610)
 * For more info, go to: http://itextpdf.com/examples/
 * This example only works with the AGPL version of iText.
 */

package part1.chapter01;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Writing one PDF file to a ZipOutputStream.
 */
public class HelloZip {

	/** Path to the resulting PDF file. */
	public static final String RESULT = "results/part1/chapter01/hello.zip";

	/**
	 * Creates a zip file with five PDF documents: hello1.pdf to hello5.pdf
	 */
	public static void main(String[] args) throws DocumentException,
			IOException {
		// creating a zip file with different PDF documents
		ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(RESULT));
		for (int i = 1; i <= 3; i++) {
			ZipEntry entry = new ZipEntry("hello_" + i + ".pdf");
			zip.putNextEntry(entry);

			Document document = new Document();
			PdfWriter writer = PdfWriter.getInstance(document, zip);
			writer.setCloseStream(false);
			document.open();
			document.add(new Paragraph("Hello " + i));
			document.close();

			zip.closeEntry();
		}
		zip.close();
	}
}
