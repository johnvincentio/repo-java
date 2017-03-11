package com.idc.test.pdf;

import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfWriter;

public class FontSelection {
	private static final String OUTPUT_PDF = "C:/irac7/wrkspc/Rda/Pdf/test/pdf/FontSelection.pdf";

	public static void main(String[] args) throws Exception {
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(OUTPUT_PDF));
		document.open();
		String input = "Rose india is a  \u275d java \u275e site. Roseindia provide  "
				+ "online \u2798 tutorial \u279a of core java,j2ee,jsp and strust.\n\n"
				+ "\u2766\u00a0\u0ea0\u039c\u03c7\u03bd\u03b9\u0dbd \u03b1 \u03b5\u03d9\u0db4\u02b5, \u03b1\u03b5\u0311, \u03a0\u03b7\u03bb\u03b7 \u03b1\u0db1\u03b4\u03b5\u02c9 \u0391\u03c7\u03b9\u03bb\u03b7\u03bf\u03c2";
		FontSelector fontselector = new FontSelector();
		fontselector.addFont(FontFactory.getFont(FontFactory.TIMES_ROMAN, 12));
		fontselector.addFont(FontFactory.getFont(FontFactory.ZAPFDINGBATS, 12));
		fontselector.addFont(FontFactory.getFont(FontFactory.SYMBOL, 12));
		Phrase ph = fontselector.process(input);
		document.add(new Paragraph(ph));
		document.close();
	}
}
