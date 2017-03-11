package com.idc.a1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFPictureData;
//import org.apache.poi.ss.usermodel.PictureData;

public class A2 {
	private static final String XML = "/home/jv/vc/projects/Rda/Notes/jv/jv (copy).xls";
	private static final String PNG = "/home/jv/vc/projects/Rda/Notes/output/a.png";

	public static void main(String[] args) {
		(new A2()).doTest();
	}
	private void doTest() {
		HSSFWorkbook workbook = null;
		try {
			InputStream myxls = new FileInputStream (XML);
			workbook = new HSSFWorkbook (myxls);
		} catch (Exception ex) {
			System.err.println("Exception; " + ex.getMessage());
		}
		if (workbook == null) return;

		try {
		    List<HSSFPictureData> lst = workbook.getAllPictures();
		    for (Iterator<HSSFPictureData> it = lst.iterator(); it.hasNext(); ) {
		    	HSSFPictureData pict = it.next();
		        String ext = pict.suggestFileExtension();
		        byte[] data = pict.getData();
		        if (ext.equals("png")) {
		          FileOutputStream out = new FileOutputStream(PNG);
		          out.write(data);
		          out.close();
		        }
		    }
		} catch (Exception ex) {
			System.err.println("Exception; " + ex.getMessage());
		}
	}
}
