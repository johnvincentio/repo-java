package com.idc.test1;

import java.io.FileOutputStream;

import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class A4b {
	public static final String INPUT = "results/a2.pdf";
	public static final String RESULT = "results/a4b.pdf";
	private static final String WATERMARK = "resources/herc_pdf_watermark.gif";

	public static void main(String[] args) throws Exception {
		A4b a4b = new A4b();
		a4b.doTest();
	}

	private void doTest() throws Exception {
		PdfReader Read_PDF_To_Watermark = new PdfReader(INPUT);
		int number_of_pages = Read_PDF_To_Watermark.getNumberOfPages();
		PdfStamper stamp = new PdfStamper(Read_PDF_To_Watermark, new FileOutputStream(RESULT));
		int i = 0;
		Image watermark_image = Image.getInstance(WATERMARK);
		watermark_image.setRotationDegrees(90f);
		watermark_image.setAbsolutePosition(250, 200);
		PdfContentByte add_watermark;
		while (i < number_of_pages) {
			i++;
			add_watermark = stamp.getUnderContent(i);
			add_watermark.addImage(watermark_image);
		}
		stamp.close();
	}
}
