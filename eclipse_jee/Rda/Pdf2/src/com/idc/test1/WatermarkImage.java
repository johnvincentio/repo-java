package com.idc.test1;

import java.io.FileOutputStream;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class WatermarkImage {
	public static final String RESULT = "results/WatermarkImage.pdf";
	private static final String WATERMARK = "resources/herc_pdf_watermark.gif";

	public static void main(String[] args) throws Exception {
		WatermarkImage a4c = new WatermarkImage();
		a4c.doTest();
	}

	private void doTest() throws Exception {
		Document document = new Document (PageSize.A4);
		PdfWriter writer = PdfWriter.getInstance (document, new FileOutputStream(RESULT));
		writer.setPageEvent(new Watermark());
		document.open();
		document.setPageSize (PageSize.A4);
		document.setMargins(36, 36, 36, 36);

		for (int cnt = 0; cnt < 5; cnt++) {
			document.add(createParagraph());
			document.newPage();
		}
		document.close();
	}

	private Paragraph createParagraph () {
		Paragraph paragraph = new Paragraph();
		paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
		for (int i = 0; i < 20; i++) {
			paragraph.add("Hello World! Hello People! "
					+ "Hello Sky! Hello Sun! Hello Moon! Hello Stars!");
		}
		return paragraph;
	}

	class Watermark extends PdfPageEventHelper {
		private Image image;
		private PdfGState state;
		public Watermark() {
			try {
				image = Image.getInstance (WATERMARK);
				image.setRotationDegrees(90f);
				image.setAbsolutePosition(200, 300);
				state = new PdfGState();
				state.setFillOpacity(0.8f);
			}
			catch (Exception ex) {
				System.out.println("Exception "+ex.getMessage());
			}
		}

		public void onEndPage (PdfWriter writer, Document document) {
			try {
				PdfContentByte underContent = writer.getDirectContentUnder();
		        underContent.setGState (state);
		        underContent.addImage(image);
//		        underContent.restoreState();
			}
			catch (Exception ex) {
				System.out.println("Exception "+ex.getMessage());
			}
		}
	}

	class WatermarkText2 extends PdfPageEventHelper {
		Image image;
		public void onOpenDocument (PdfWriter writer, Document document) {
			try {
				image = Image.getInstance ("resources/hertz.jpg");
				image.setRotationDegrees(45f);
			}
			catch (Exception ex) {
				System.out.println("Exception "+ex.getMessage());
			}
		}

		public void onEndPage (PdfWriter writer, Document document) {
			try {
				PdfContentByte underContent = writer.getDirectContentUnder();
		        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
		        PdfGState gs = new PdfGState();
		        gs.setFillOpacity(0.8f);
		        underContent.setGState(gs);
		        underContent.beginText();
		        underContent.setFontAndSize(bf, 20);
		        underContent.setColorFill(BaseColor.LIGHT_GRAY);
		        underContent.showTextAligned(Element.ALIGN_CENTER, "www.java-connect.com", 300, 700, 0);
		        underContent.endText();
			}
			catch (Exception ex) {
				System.out.println("Exception "+ex.getMessage());
			}
		}
	}

	class WatermarkText extends PdfPageEventHelper {
		Font FONT = new Font(FontFamily.HELVETICA, 52, Font.BOLD, new GrayColor(0.75f));
		public void onEndPage(PdfWriter writer, Document document) {
			ColumnText.showTextAligned(writer.getDirectContentUnder(),
					Element.ALIGN_CENTER, new Phrase("FOOBAR FILM FESTIVAL", FONT), 297.5f, 421,
					writer.getPageNumber() % 2 == 1 ? 45 : -45);
		}
	}
}
