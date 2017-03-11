package com.idc.test.pdf;

import java.awt.Font;
import java.io.FileOutputStream;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

public class CreatePDFiText {
	private static final String OUTPUT = "C:/irac7/wrkspc/Rda/Pdf/test/pdf/CreatePDFiText.pdf";

	public static void main(String[] args) {
		try {
			Document document = new Document(PageSize.A4, 50, 50, 50, 50);
			PdfWriter.getInstance(document, new FileOutputStream(OUTPUT));
			document.open();

			// create a chunk object using chunk class of itext library.
			Chunk underlined = new Chunk("This is sample pdf file created by : ");

			// set the distance between text and line.
			underlined.setTextRise(8.0f);

			// set the width of the line, 'y' position, color and design of the line
			underlined.setUnderline (new BaseColor(0x00, 0x00, 0xFF), 0.0f, 0.2f, 3.0f, 0.0f, PdfContentByte.LINE_CAP_PROJECTING_SQUARE);

			// finally add object to the document.
			document.add(underlined);
			document.add(new Paragraph("Mahendra Singh", FontFactory.getFont(FontFactory.COURIER, 14.0f, Font.BOLD, new BaseColor(255, 150, 200))));
			document.close();
		} catch (Exception e2) {
			System.out.println(e2.getMessage());
		}
	}
}
