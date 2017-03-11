package com.idc.sql.app;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import com.idc.sql.lib.DB;
import com.idc.sql.lib.DBQuery;
import com.idc.sql.lib.QueryRowInfo;
import com.idc.sql.lib.QueryRowItemInfo;
import com.idc.utils.UtilHelper;

public class ImageDBToXML {

	private static final String NEWLINE = "\n";
	private static final String TAB = "\t";
	private DB m_db;

	public static void main(String[] args) {
		ImageDBToXML test = new ImageDBToXML();
		test.doWork();
	}

	public void doWork() {
		System.out.println("Connecting to database");
		m_db = new DB("c:/jv/utils/dbtoolgui.xml");
		if (! m_db.getConnection ("edevdb")) {
			System.out.println("giving up...");
			System.exit(1);
		}
		DataInfo dataInfo = doTest1();
		doXML ("c:/tmp/work901/", dataInfo);

		System.out.println("Disconnecting from database");
		m_db.disConnect();
		System.out.println("exiting...");
	}

	/*
	 * Generate an XML file from DataInfo object.
	 */
	private void doXML (String dir, DataInfo dataInfo) {
		Iterator<DataItemInfo> iter = dataInfo.getItems();
		File xmlFile = new File (dir + "/equipImage.xml");
		StringBuffer buf = new StringBuffer();
		buf.append ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append (NEWLINE);
		buf.append ("<equipmentImage>").append (NEWLINE);
		while (iter.hasNext()) {
			DataItemInfo dataItemInfo = iter.next();
			System.out.println("dataItemInfo "+dataItemInfo);
			buf.append (dataItemInfo.getXML());
		}
		buf.append ("</equipmentImage>").append (NEWLINE);
		UtilHelper.writeFile (buf, xmlFile);
		buf = null;
	}

    private String format(String value){
    	if(value == null || "".equals(value)) return "";
    	value = value.toLowerCase();
    	value = value.replaceAll("& ","");
    	value = value.replaceAll("/","");
    	value = value.replace(' ', '_');
    	
    	//Check if first char is a number 
        char firstChar = value.charAt(0);
        if(firstChar == '0' || firstChar == '1' || firstChar == '2' || firstChar == '3' || firstChar == '4' || 
        		firstChar == '5' || firstChar == '6' || firstChar == '7' || firstChar == '8' || firstChar == '9') {
        	value = updateNumberString(value);
        }
        return (value);
    }
    
    public String updateNumberString(String value){
    	 String[] values = new String[0];
    	 values = value.split("_");
    	 
         String updatedValue = "";
         for (int i = 0; i < values.length; i++){
         	if (i == 0) continue;
         	updatedValue += values[i];
         	updatedValue += "_";
         }
         updatedValue += values[0];
    	 return updatedValue;
    }
    
    /**
     * Get Distinct equipment info from RentalEquipment table. Create info object for each item. 
     * @return		Array of DataItemInfo objects
     */
	private DataInfo doTest1() {
		String sql = "select DISTINCT category, subcategory, subcategory2, equipimage from hercdb.RentalEquipment order by category, subcategory, subcategory2";
		DBQuery dbQuery = new DBQuery (m_db, sql);
		dbQuery.executeQuery();
		QueryRowInfo queryRowInfo = dbQuery.getQueryRowInfo();
		int rows = queryRowInfo.getRows();
		System.out.println(" Number of rows = "+rows);
		
		DataInfo dataInfo = new DataInfo();
//		System.out.println(" queryRowInfo "+queryRowInfo);
		Iterator<QueryRowItemInfo> iter = queryRowInfo.getItems();
		int col = 1;
		DataItemInfo dataItemInfo = new DataItemInfo();
		while (iter.hasNext()) {
			QueryRowItemInfo queryRowItemInfo = iter.next();
			String value = queryRowItemInfo.getValue();
			if (col > 4) {
				col = 1;
				dataItemInfo = new DataItemInfo();
			}
//			System.out.println("col "+col+" value "+value);
			if (col == 1)
				dataItemInfo.setCategory(value);
			else if (col == 2)
				dataItemInfo.setSubcategory(value);
			else if (col == 3)
				dataItemInfo.setSubcategory2(value);
			else if (col == 4) {
				dataItemInfo.setEquipimage(value);
				dataInfo.add (dataItemInfo);
			}
			col++;
		}
		System.out.println("dataInfo "+dataInfo);
		return dataInfo;
	}

	public class DataInfo implements Serializable {
		private static final long serialVersionUID = 8585509418017796059L;
		private ArrayList<DataItemInfo> m_collection;
		public DataInfo () {m_collection = new ArrayList<DataItemInfo>();}
		public void add (DataItemInfo item) {if (item != null) m_collection.add (item);}
		public Iterator<DataItemInfo> getItems() {return m_collection.iterator();}
		public int getSize() {return m_collection.size();}
		public boolean isNone() {return getSize() < 1;}
		public String toString() {
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < m_collection.size(); i++)
				buf.append((m_collection.get(i)).toString());
			return "("+buf.toString()+")";
		}
	}

	public class DataItemInfo implements Serializable {
		private static final long serialVersionUID = -2009868603485951051L;
		private String category;
		private String subcategory;
		private String subcategory2;
		private String equipimage;
		private String text;
		
		public String getCategory() {return category;}
		public String getSubcategory() {return subcategory;}
		public String getSubcategory2() {return subcategory2;}
		public String getEquipimage() {return equipimage;}
		public String getText() {return text;}

		public boolean isText() {
			if (text == null || text.length() < 1) return false;
			return true;
		}

		public void setCategory (String category) {this.category = category;}
		public void setSubcategory(String subcategory) {this.subcategory = subcategory;}
		public void setSubcategory2(String subcategory2) {this.subcategory2 = subcategory2;}
		public void setEquipimage(String equipimage) {this.equipimage = equipimage;}
		public void setText (String text) {this.text = text;}

		public String toString() {
			return "("+getCategory()+","+getSubcategory()+","+getSubcategory2()+","+getEquipimage()+","+getText()+")";
		}

		public StringBuffer getXML() {
			StringBuffer buf = new StringBuffer();
			buf.append (TAB+"<").append(format(getCategory())).append(">").append (NEWLINE);
			buf.append (TAB+TAB+"<").append(format(getSubcategory())).append(">").append (NEWLINE);
			if(UtilHelper.isSet(getSubcategory2()))
				buf.append (TAB+TAB+"<").append (format(getSubcategory2())).append(">").append (NEWLINE);
			buf.append (TAB+TAB+"<image>").append (NEWLINE);
			buf.append (TAB+TAB+TAB+"<name>").append("<![CDATA["+getEquipimage()+"]]>").append("</name>").append (NEWLINE);
			buf.append (TAB+TAB+TAB+"<text>").append("<![CDATA["+getText()+"]]>").append("</text>").append (NEWLINE);
			buf.append (TAB+TAB+"</image>").append (NEWLINE);
			if(UtilHelper.isSet(format(getSubcategory2())))
				buf.append (TAB+TAB+"</").append (format(getSubcategory2())).append(">").append (NEWLINE);
			buf.append (TAB+TAB+"</").append(format(getSubcategory())).append(">").append (NEWLINE);
			buf.append (TAB+"</").append(format(getCategory())).append(">").append (NEWLINE);
			return buf;
		}
	}
}
