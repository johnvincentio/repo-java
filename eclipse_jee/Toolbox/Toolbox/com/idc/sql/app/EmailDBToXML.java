/********************************************************************
*			Copyright (c) 2006 The Hertz Corporation				*
*			  All Rights Reserved.  (Unpublished.)					*
*																	*
*		The information contained herein is confidential and		*
*		proprietary to The Hertz Corporation and may not be			*
*		duplicated, disclosed to third parties, or used for any		*
*		purpose not expressly authorized by it.  Any unauthorized	*
*		use, duplication, or disclosure is prohibited by law.		*
*																	*
*********************************************************************/

package com.idc.sql.app;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import com.idc.sql.lib.DB;
import com.idc.sql.lib.DBQuery;
import com.idc.sql.lib.QueryRowInfo;
import com.idc.sql.lib.QueryRowItemInfo;
import com.idc.utils.JVString;
import com.idc.utils.UtilHelper;

/**
* @author John Vincent
*/

public class EmailDBToXML {
	private static final String NEWLINE = "\n";
	private static final String TAB = "\t";
	private DB m_db;

	public static void main(String[] args) {
		EmailDBToXML test = new EmailDBToXML();
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
		doXML ("c:/work2/makeXML", dataInfo);

		System.out.println("Disconnecting from database");
		m_db.disConnect();
		System.out.println("exiting...");
	}

	private void doXML (String dir, DataInfo dataInfo) {
		Iterator<DataItemInfo> iter = dataInfo.getItems();
		while (iter.hasNext()) {
			DataItemInfo dataItemInfo = iter.next();
			System.out.println("dataItemInfo "+dataItemInfo);
			System.out.println("Category :"+dataItemInfo.getCategory());
			File xmlFile = new File (dir + "/" + dataItemInfo.getCategory().toLowerCase()+ ".xml");
			StringBuffer buf = new StringBuffer();
			buf.append ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append (NEWLINE);
			buf.append ("<email>").append (NEWLINE);
			buf.append (dataItemInfo.getXML());
			buf.append ("</email>").append (NEWLINE);
			UtilHelper.writeFile (buf, xmlFile);
			buf = null;

			if (dataItemInfo.isText()) {
				File txtFile = new File (dir + "/" + dataItemInfo.getCategory().toLowerCase()+ ".txt");
				UtilHelper.writeFile (dataItemInfo.getTreatedText(), txtFile);
			}

			if (dataItemInfo.isHtml()) {
				File htmlFile = new File (dir + "/" + dataItemInfo.getCategory().toLowerCase()+ ".html");
				UtilHelper.writeFile (new StringBuffer (dataItemInfo.getHtml()), htmlFile);
			}
		}
	}

	private DataInfo doTest1() {
		String sql = "select C.CATEGORY_CODE, C.DESCRIPTION, C.PRIORITY, C.OUTBOUND, C.ROUTING, " +
			"A.EMAIL, A.BOUNCE_BACK, A.EMAIL_FROM_ALIAS, A.EMAIL_ADDTL_DYNAMIC, " +
			"D.ENCODING, D.SUBJECT, D.BODY_TXT, D.BODY_HTML " +
			"from hercdb.EMAIL_CATEGORY C, hercdb.EMAIL_ADDRESS A, hercdb.EMAIL_DEFINITION_ENUS D " +
			"where C.CATEGORY_CODE = A.CATEGORY_CODE " + 
			"and C.CATEGORY_CODE = D.CATEGORY_CODE " +
			"and A.LANG_CODE = 'EN' and A.COUNTRY_CODE = 'US' " +
			"and D.COUNTRY_CODE = 'US' " + 
			"order by C.CATEGORY_CODE";
		DBQuery dbQuery = new DBQuery (m_db, sql);
		dbQuery.executeQuery();
		QueryRowInfo queryRowInfo = dbQuery.getQueryRowInfo();

		DataInfo dataInfo = new DataInfo();
//		System.out.println(" queryRowInfo "+queryRowInfo);
		Iterator<QueryRowItemInfo> iter = queryRowInfo.getItems();
		int col = 1;
		DataItemInfo dataItemInfo = new DataItemInfo();
		while (iter.hasNext()) {
			QueryRowItemInfo queryRowItemInfo = iter.next();
			String value = queryRowItemInfo.getValue();
			if (col > 13) {
				col = 1;
				dataItemInfo = new DataItemInfo();
			}
//			System.out.println("col "+col+" value "+value);
			if (col == 1)
				dataItemInfo.setCategory(value);
			else if (col == 2)
				dataItemInfo.setDescription(value);
			else if (col == 3)
				dataItemInfo.setPriority(value);
			else if (col == 4)
				dataItemInfo.setOutbound(value);
			else if (col == 5)
				dataItemInfo.setRouting(value);
			else if (col == 6)
				dataItemInfo.setEmail(value);
			else if (col == 7)
				dataItemInfo.setBounceBack(value);
			else if (col == 8)
				dataItemInfo.setEmailFromAlias(value);
			else if (col == 9)
				dataItemInfo.setEmailAddtlDynamic(value);
			else if (col == 10)
				dataItemInfo.setEncoding(value);
			else if (col == 11)
				dataItemInfo.setSubject(value);
			else if (col == 12)
				dataItemInfo.setText(value);
			else if (col == 13) {
				dataItemInfo.setHtml(value);
				dataInfo.add (dataItemInfo);
			}
			col++;
		}
//		System.out.println("dataInfo "+dataInfo);
		return dataInfo;
	}

	public class DataInfo implements Serializable {
		private static final long serialVersionUID = 5149444329227631431L;
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
		private static final long serialVersionUID = -7999035543205289518L;
		private String category;
		private String description;
		private String priority;
		private String outbound;
		private String routing;
		private String email;
		private String bounceBack;
		private String emailFromAlias;
		private String emailAddtlDynamic;
		private String encoding;
		private String subject;
		private String text;
		private String html;

		public String getCategory() {return category;}
		public String getDescription() {return description;}
		public String getPriority() {return priority;}
		public String getOutbound() {return outbound;}
		public String getRouting() {return routing;}
		public String getEmail() {return email;}
		public String getBounceBack() {return bounceBack;}
		public String getEmailFromAlias() {return emailFromAlias;}
		public String getEmailAddtlDynamic() {return emailAddtlDynamic;}
		public String getEncoding() {return encoding;}
		public String getSubject() {return subject;}
		public String getText() {return text;}
		public String getHtml() {return html;}

		public boolean isText() {
			if (text == null || text.length() < 1) return false;
			return true;
		}
		public boolean isHtml() {
			if (html == null || html.length() < 1) return false;
			return true;
		}

		public void setCategory (String category) {this.category = category;}
		public void setDescription (String description) {this.description = description;}
		public void setPriority (String priority) {this.priority = priority;}
		public void setOutbound (String outbound) {this.outbound = outbound;}
		public void setRouting (String routing) {this.routing = routing;}
		public void setEmail (String email) {this.email = email;}
		public void setBounceBack (String bounceBack) {this.bounceBack = bounceBack;}
		public void setEmailFromAlias (String emailFromAlias) {this.emailFromAlias = emailFromAlias;}
		public void setEmailAddtlDynamic (String emailAddtlDynamic) {this.emailAddtlDynamic = emailAddtlDynamic;}
		public void setEncoding (String encoding) {this.encoding = encoding;}
		public void setSubject (String subject) {this.subject = subject;}
		public void setText (String text) {this.text = text;}
		public void setHtml (String html) {this.html = html;}

		public String toString() {
			return "("+getCategory()+","+getDescription()+","+getPriority()+","+getOutbound()+","+getRouting()+","+getEmail()+","+getBounceBack()+","+getEmailFromAlias()+","+getEmailAddtlDynamic()+","+getEncoding()+","+getSubject()+","+getText()+","+getHtml()+")";
		}

		public StringBuffer getTreatedText() {
			String jv = JVString.replace (getText(), "\\n", "\n");
			jv = JVString.replace (jv, "{", "${");
			return new StringBuffer (jv);
		}

		public StringBuffer getTreatedSubject() {
			String jv = JVString.replace (getSubject(), "{", "${");
			return new StringBuffer (jv);
		}

		public StringBuffer getXML() {
			StringBuffer buf = new StringBuffer();
			buf.append (TAB+"<definition>").append (NEWLINE);
			buf.append (TAB+TAB+"<subject>").append(getTreatedSubject()).append("</subject>").append (NEWLINE);
			buf.append (TAB+TAB+"<encoding>").append(getEncoding()).append("</encoding>").append (NEWLINE);
			buf.append (TAB+TAB+"<priority>").append (getPriority()).append("</priority>").append (NEWLINE);
			buf.append (TAB+"</definition>").append (NEWLINE);

			buf.append (TAB+"<address>").append (NEWLINE);
			buf.append (TAB+TAB+"<email>").append(getEmail()).append("</email>").append (NEWLINE);
			buf.append (TAB+TAB+"<bounceBack>").append (getBounceBack()).append("</bounceBack>").append (NEWLINE);
			buf.append (TAB+TAB+"<emailFromAlias>").append (getEmailFromAlias()).append ("</emailFromAlias>").append (NEWLINE);
			buf.append (TAB+TAB+"<emailAddtlDynamic>").append (getEmailAddtlDynamic()).append ("</emailAddtlDynamic>").append (NEWLINE);
			buf.append (TAB+"</address>").append (NEWLINE);

			buf.append (TAB+"<category>").append (NEWLINE);
			buf.append (TAB+TAB+"<description>").append (getDescription()).append ("</description>").append (NEWLINE);
			buf.append (TAB+TAB+"<outbound>").append (getOutbound()).append ("</outbound>").append (NEWLINE);
			buf.append (TAB+TAB+"<routing>").append (getRouting()).append ("</routing>").append (NEWLINE);
			buf.append (TAB+"</category>").append (NEWLINE);
			return buf;
		}
	}
}
