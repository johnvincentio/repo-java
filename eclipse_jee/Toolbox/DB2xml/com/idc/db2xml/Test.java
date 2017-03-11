package com.idc.db2xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.sql.Types;

public class Test {
	private DB m_db;
	private Output m_output;
	private ArrayList<TableInfo> allTableInfo = new ArrayList<TableInfo>();
	private static final String PROPERTIES_FILE="db2.properties";
	private static final String OUTPUT_FILE="jv.xml";	

	public Test() {}
	public void doWork() {

		System.out.println("Opening file");		
		m_output = new Output();
		if (! m_output.open (OUTPUT_FILE)) {
			System.out.println("giving up...");
			System.exit(1);
		}
		
		System.out.println("Connecting to database");
		m_db = new DB();
		if (! m_db.getConnection (PROPERTIES_FILE)) {
			System.out.println("giving up...");
			System.exit(1);
		}

		System.out.println("Getting all tables");
		ArrayList<String> allTables = m_db.getTables();
		for (String tableName : allTables) {
			tableName.toUpperCase();
			TableInfo tbinfo = new TableInfo (tableName);	// default to a _CONTROL table
			tbinfo.setDisplayName(tableName+"_CONTROL");
			tbinfo.setDialectName(tableName);
			tbinfo.setTranslationType("A");			
			tbinfo.setTypeOption("2");		
			allTableInfo.add (tbinfo);
		}
		
		System.out.println("Removing unwanted tables from list");
		for (TableInfo tbinfo : allTableInfo) {
			String tableName = tbinfo.getName();
			if (tableName.indexOf("_TEMP") > -1) {tbinfo.setDelete();}
			if (tableName.indexOf("_TMP") > -1) {tbinfo.setDelete();}			
			if (tableName.indexOf("_OLD") > -1) {tbinfo.setDelete();}
			if (tableName.indexOf("WEATHER") > -1) {tbinfo.setDelete();}
			if (tableName.indexOf("_APPROVAL") > -1) {tbinfo.setDelete();}			
			if (tableName.indexOf("LOCATION_INFO_") > -1) {tbinfo.setDelete();}
			if (tableName.indexOf("POLICY_DESC_") > -1) {tbinfo.setDelete();}
			if (tableName.indexOf("POLICY_TITLE_") > -1) {tbinfo.setDelete();}						
			if (tableName.indexOf("COUNTRY_NAME_") > -1) {tbinfo.setDelete();}
			if (tableName.indexOf("COUNTRY_RES_CENTER") > -1) {tbinfo.setDelete();}
			if (tableName.indexOf("EMPLOYMENT_OPPORTUNITY") > -1) {tbinfo.setDelete();}
			if (tableName.indexOf("VEHICLE_DESCRIPTION_") > -1) {tbinfo.setDelete();}						
			if (tableName.indexOf("VEHICLE_SIPP_") > -1) {tbinfo.setDelete();}
										
			if (tableName.equals("LOCATION")) {tbinfo.setDelete();}
			if (tableName.equals("POLICY")) {tbinfo.setDelete();}
			if (tableName.equals("COUNTRY")) {tbinfo.setDelete();}
			if (tableName.equals("PARTNERS")) {tbinfo.setDelete();}						
			if (tableName.equals("VEHICLE_DATA")) {tbinfo.setDelete();}											
		}

		System.out.println("Handling Dialect Tables");		
		for (TableInfo tbinfo : allTableInfo) {
			if (tbinfo.getDelete()) continue;
			String tableName = tbinfo.getName();
			String dialect = tableName.substring(tableName.length()-4,tableName.length());
			if (dialect.equals("ENUS") || dialect.equals("FRFR") || dialect.equals("DEDE")) {
				String str1 = tableName.substring(0,tableName.length()-5);
				tbinfo.setName(str1);				
				tbinfo.setDisplayName(str1+"_DIALECT");
				tbinfo.setDialectName(tableName);
				tbinfo.setTranslationType("A");			
				tbinfo.setTypeOption("3");	// Complex type
				tbinfo.setDialect();			
			}										
		}
		System.out.println("Removing duplicated dialect tables from list");
		TableInfo tmpInfo;
		String strTmp;
		for (int i=0; i<allTableInfo.size(); i++) {	// remove duplicates
			TableInfo tbinfo = (TableInfo) allTableInfo.get(i);
			if (tbinfo.getDelete()) continue;
			if (! tbinfo.getDialect()) continue;						
			String tableName = tbinfo.getName();			
//			System.out.println("test; tableName :"+tableName+": dialectName :"+tbinfo.getDialectName()+":");
			for (int j=i+1; j<allTableInfo.size(); j++) {
				tmpInfo = (TableInfo) allTableInfo.get(j);
				if (tmpInfo.getDelete()) continue;
				if (! tmpInfo.getDialect()) continue;						
				strTmp = tmpInfo.getName();
//				System.out.println("tableName :"+tableName+": strTmp :"+strTmp+":");
				if (tableName.equals(strTmp)) {
//					System.out.println("Equals - delete "+j+" keep "+i);
					tmpInfo.setDelete();				
				}
			}
		}

		System.out.println("Getting Primary Key and Column info");
		for (TableInfo tbinfo : allTableInfo) {
			if (tbinfo.getDelete()) continue;								
			tbinfo = getTable(tbinfo.getName());
			tbinfo.addPklist (m_db.getPrimaryKeys (tbinfo.getDialectName()));	// get primary key info
			tbinfo.addColumnlist (m_db.getColumns (tbinfo.getDialectName()));	// get column info
		}

		System.out.println("Disconnecting from database");
		m_db.disConnect();

		System.out.println("Rationalizing primary key info");
		for (TableInfo tbinfo : allTableInfo) {
			if (tbinfo.getDelete()) continue;					
			Iterator<String> iter1 = tbinfo.getPklist().iterator();
			while (iter1.hasNext()) {
				String column = (String) iter1.next();
//				System.out.println("Primary key: "+column);
				ColumnInfo colinfo = tbinfo.getColumn(column);
				colinfo.setPk(true);
			}
		}

//		doShowInfo();	// trace the internals

		System.out.println("Writing the xml file");
		putString("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");	
		putString("<contexts>\n");
		for (TableInfo tbinfo : allTableInfo) {
			if (tbinfo.getDelete()) continue;						
//			String tableName = tbinfo.getName();
			putString("\t<context>\n");
			putString("\t\t<DisplayName>");
			putString(tbinfo.getDisplayName());
			putString("</DisplayName>\n");
			putString("\t\t<translationType>");
			putString(tbinfo.getTranslationType());
			putString("</translationType>\n");
			
			putString("\t\t<type option=\"");
			putString(tbinfo.getTypeOption());
			putString("\">");
						
			if (tbinfo.getTypeOption().equals("3"))
				putString("Complex");
			else if (tbinfo.getTypeOption().equals("2"))
				putString("Simple");			
			else if (tbinfo.getTypeOption().equals("1"))
				putString("Dynamic");					
			else {
				System.out.println("Unknown type "+tbinfo.getTypeOption());
				System.exit(1);
			}
					
			putString("</type>\n");
			
			putString("\t\t<table name=\"");
			putString(tbinfo.getName());
			putString("\">\n");

			ColumnInfo info;		
			Iterator<ColumnInfo> iter1 = tbinfo.getCollist().iterator();
			while (iter1.hasNext()) {
				info = (ColumnInfo) iter1.next();
				putString("\t\t\t<column");
				putString(" schemaName=\"");
				putString(info.getName());
				putString("\" display=\"true\"");
				
				if (info.getLen() > 0) {
					putString(" length=\"");
					putString(info.getLen());
					putString("\"");
				}
				else
					putString(" length=\"11\"");	// as per Tim 3/4/2005
					
				putString(" type=\"");
				if (info.getType() == Types.CHAR)
					putString("char");
				else if (info.getType() == Types.VARCHAR)
					putString("varchar");
				else if (info.getType() == Types.SMALLINT)
					putString("smallint");
				else if (info.getType() == Types.INTEGER)
					putString("integer");
				else if (info.getType() == Types.BIGINT)
					putString("bigint");
				else if (info.getType() == Types.FLOAT)
					putString("float");										
				else if (info.getType() == Types.DOUBLE)
					putString("double");
				else if (info.getType() == Types.DECIMAL)
					putString("decimal");										
				else if (info.getType() == Types.DATE)
					putString("date");					
				else if (info.getType() == Types.TIMESTAMP)
					putString("timestamp");
				else if (info.getType() == Types.TIME)
					putString("time");										
				else
					System.out.println("unknown type "+info.getType()+" column "+info.getName()+
						" in table "+tbinfo.getDialectName());
				putString("\"");
				
				if (info.getPk())
					putString(" edit=\"false\" pk=\"true\">");
				else
					putString(" edit=\"true\" pk=\"false\">");					
				putString(info.getName());			
				putString("</column>\n");
			}				
			putString("\t\t</table>\n");						
			putString("\t</context>\n");			
		}
		putString("</contexts>\n");
		m_output.close();
		System.out.println("exiting...");
	}
	public TableInfo getTable(String name) {
		TableInfo info;
		Iterator<TableInfo> iterator = allTableInfo.iterator();
		while (iterator.hasNext()) {
			info = (TableInfo) iterator.next();
			if ((info.getName()).equals(name)) return info;
		}
		return null;
	}		
    public void putString (String msg) {m_output.write(msg);}
    public void putString (int num) {putString(Integer.toString(num));}
}
/*
	private void doShowInfo() {
		TableInfo info;
		Iterator iterator = allTableInfo.iterator();
		while (iterator.hasNext()) {
			info = (TableInfo) iterator.next();
			info.doShowInfo();
		}
	}
	private void doShowDialectInfo() {
		TableInfo info;
		for (int i=0; i<allTableInfo.size(); i++) {
			info = (TableInfo) allTableInfo.get(i);
			if (info.getDelete()) continue;
			if (! info.getDialect()) continue;								
			System.out.println("List; i "+i+" tableName :"+info.getName());
		}
	}
*/