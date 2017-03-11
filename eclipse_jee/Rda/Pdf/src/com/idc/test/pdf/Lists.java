package com.idc.test.pdf;

import java.io.FileOutputStream;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class Lists {
	private static final String OUTPUT_PDF = "C:/irac7/wrkspc/Rda/Pdf/test/pdf/Lines.pdf";

	public static void main(String[] args) throws Exception {
		System.out.println("Create List object");
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(OUTPUT_PDF));
		document.open();
		List list = new List(true, 20);
		list.add(new ListItem("First line"));
		list.add(new ListItem("The second "));
		list.add(new ListItem("Third line"));
		document.add(list);
		document.add(new Paragraph("Tutorials Provided By Roseindia.net"));
		ListItem listItem;
		list = new List(true, 15);
		listItem = new ListItem("Core Java", FontFactory.getFont(
				FontFactory.TIMES_ROMAN, 13));
		listItem.add(new Chunk(" by rose india", FontFactory.getFont(
				FontFactory.TIMES_ROMAN, 13, Font.ITALIC)));
		list.add(listItem);
		listItem = new ListItem("J2EE", FontFactory.getFont(
				FontFactory.TIMES_ROMAN, 12));
		listItem.add(new Chunk(" by rose india", FontFactory.getFont(
				FontFactory.TIMES_ROMAN, 13, Font.ITALIC)));
		list.add(listItem);
		listItem = new ListItem("JSP", FontFactory.getFont(
				FontFactory.TIMES_ROMAN, 12));
		listItem.add(new Chunk(" by rose india", FontFactory.getFont(
				FontFactory.TIMES_ROMAN, 13, Font.ITALIC)));
		list.add(listItem);
		document.add(list);
		Paragraph paragraph = new Paragraph("Some open source project");
		list = new List(false, 10);
		list.add("chat server");
		list.add("pie chart");
		list.add("online shopping");
		paragraph.add(list);
		document.add(paragraph);
		document.add(new Paragraph("Some iText Example"));
		list = new List(false, 20);
		list.setListSymbol(new Chunk("\u2021", FontFactory.getFont(
				FontFactory.HELVETICA, 21, Font.BOLD)));
		listItem = new ListItem("Generates a simple 'Hello World' PDF" + "file");
		list.add(listItem);
		List sublist;
		sublist = new List(false, true, 10);
		sublist.setListSymbol(new Chunk("", FontFactory.getFont(
				FontFactory.HELVETICA, 7)));
		sublist.add("Creating Paragraph using iText");
		sublist.add("Creating Section using iText");
		sublist.add("Creating A4 PDF using iText.");
		sublist.add("Create size(509,50,50,50) A4 PDF Using iText");
		list.add(sublist);
		listItem = new ListItem("Craeting  table object using iText");
		list.add(listItem);
		sublist = new List(false, true, 10);
		sublist.setFirst('a');
		sublist.setListSymbol(new Chunk("", FontFactory.getFont(
				FontFactory.HELVETICA, 7)));
		sublist.add("Creating  list object using iText ");
		sublist.add("Hotel New Hampshire");
		sublist.add("Creating  list object using iText ");
		sublist.add("Creating  list object using iText ");
		list.add(sublist);
		listItem = new ListItem("Creating  list object using iText ");
		list.add(listItem);
		sublist = new List(false, true, 10);
		sublist.setListSymbol(new Chunk("", FontFactory.getFont(
				FontFactory.HELVETICA, 7)));
		sublist.add("Creating  list object using iText ");
		sublist.add("Creating  list object using iText ");
		sublist.add("Creating  list object using iText ");
		sublist.add("Creating  list object using iText ");
		list.add(sublist);
		document.add(list);
		document.close();
	}
}
