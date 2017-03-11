package com.idc.test1;

import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

public class A4 {
	public static final String RESULT = "results/a4.pdf";

	/**
	 * Inner class to add a table as header.
	 */
	class TableHeader extends PdfPageEventHelper {
		/** The header text. */
		String header;
		/** The template with the total number of pages. */
		PdfTemplate total;

		/**
		 * Allows us to change the content of the header.
		 * 
		 * @param header
		 *            The new header String
		 */
		public void setHeader(String header) {
			this.header = header;
		}

		/**
		 * Creates the PdfTemplate that will hold the total number of pages.
		 * 
		 * @see com.itextpdf.text.pdf.PdfPageEventHelper#onOpenDocument(com.itextpdf.text.pdf.PdfWriter,
		 *      com.itextpdf.text.Document)
		 */
		public void onOpenDocument(PdfWriter writer, Document document) {
			total = writer.getDirectContent().createTemplate(30, 16);
		}

		/**
		 * Adds a header to every page
		 * 
		 * @see com.itextpdf.text.pdf.PdfPageEventHelper#onEndPage(com.itextpdf.text.pdf.PdfWriter,
		 *      com.itextpdf.text.Document)
		 */
		public void onEndPage(PdfWriter writer, Document document) {
			PdfPTable table = new PdfPTable(3);
			try {
				table.setWidths(new int[] { 24, 24, 2 });
				table.setTotalWidth(527);
				table.setLockedWidth(true);
				table.getDefaultCell().setFixedHeight(20);
				table.getDefaultCell().setBorder(Rectangle.BOTTOM);
				table.addCell(header);
				table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
				table.addCell(String.format("Page %d of", writer.getPageNumber()));
				PdfPCell cell = new PdfPCell(Image.getInstance(total));
				cell.setBorder(Rectangle.BOTTOM);
				table.addCell(cell);
				table.writeSelectedRows(0, -1, 34, 803, writer.getDirectContent());
			} catch (DocumentException de) {
				throw new ExceptionConverter(de);
			}
		}

		/**
		 * Fills out the total number of pages before the document is closed.
		 * 
		 * @see com.itextpdf.text.pdf.PdfPageEventHelper#onCloseDocument(com.itextpdf.text.pdf.PdfWriter,
		 *      com.itextpdf.text.Document)
		 */
		public void onCloseDocument(PdfWriter writer, Document document) {
			ColumnText.showTextAligned(total, Element.ALIGN_LEFT, new Phrase(
					String.valueOf(writer.getPageNumber() - 1)), 2, 2, 0);
		}
	}

	/**
	 * Inner class to add a watermark to every page.
	 */
	class Watermark extends PdfPageEventHelper {

		Font FONT = new Font(FontFamily.HELVETICA, 52, Font.BOLD, new GrayColor(0.75f));

		public void onEndPage(PdfWriter writer, Document document) {
			ColumnText.showTextAligned(writer.getDirectContentUnder(),
					Element.ALIGN_CENTER, new Phrase("FOOBAR FILM FESTIVAL", FONT), 297.5f, 421,
					writer.getPageNumber() % 2 == 1 ? 45 : -45);
		}
	}

	public void doTest() throws Exception {
		Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(RESULT));
		TableHeader event = new TableHeader();
		writer.setPageEvent(event);
		writer.setPageEvent(new Watermark());
		document.open();

		for (int cnt = 0; cnt < 5; cnt++) {
			event.setHeader(Integer.toString(cnt));
			Phrase p = new Phrase ("some text "+cnt);
			document.add(new Paragraph (p));
			document.newPage();
		}

		document.close();
	}

	public static void main(String[] args) throws Exception {
		A4 a4 = new A4();
		a4.doTest();
	}

}