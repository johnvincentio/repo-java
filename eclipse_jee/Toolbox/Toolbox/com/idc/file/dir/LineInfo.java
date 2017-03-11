
package com.idc.file.dir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class LineInfo {
	private ArrayList<LineItemInfo> m_list = new ArrayList<LineItemInfo>();

	public void add (String s) {add (new LineItemInfo(s));}
	private void add (LineItemInfo item) {addItem(item);}
	private void addItem (LineItemInfo item) {
//		System.out.println("ext :"+item.getExtension()+":");
		if (include(item.getExtension())) m_list.add(item);
	}
	private boolean include (String ext) {
		if (ext.equalsIgnoreCase("java")) return true;
		if (ext.equalsIgnoreCase("xml")) return true;
		if (ext.equalsIgnoreCase("txt")) return true;
		if (ext.equalsIgnoreCase("sql")) return true;

		if (ext.equalsIgnoreCase("project")) return false;
		if (ext.equalsIgnoreCase("rlconxmi")) return false;
		if (ext.equalsIgnoreCase("conxmi")) return false;
		if (ext.equalsIgnoreCase("dbxmi")) return false;
		if (ext.equalsIgnoreCase("spf")) return false;
		if (ext.equalsIgnoreCase("dbxmi")) return false;
		if (ext.equalsIgnoreCase("schxmi")) return false;
		if (ext.equalsIgnoreCase("iex")) return false;
		if (ext.equalsIgnoreCase("class")) return false;
		if (ext.equalsIgnoreCase("dnx")) return false;
		if (ext.equalsIgnoreCase("classpath")) return false;
		if (ext.equalsIgnoreCase("j2ee")) return false;
		if (ext.equalsIgnoreCase("runtime")) return false;
		if (ext.equalsIgnoreCase("webspheredeploy")) return false;
		if (ext.equalsIgnoreCase("xmi")) return false;
		if (ext.equalsIgnoreCase("serverPreference")) return false;
		if (ext.equalsIgnoreCase("bak")) return false;
		if (ext.equalsIgnoreCase("mf")) return false;
		if (ext.equalsIgnoreCase("psf")) return false;
		if (ext.equalsIgnoreCase("modulemaps")) return false;
		if (ext.equalsIgnoreCase("jar")) return false;
		if (ext.equalsIgnoreCase("zip")) return false;
		if (ext.equalsIgnoreCase("gph")) return false;
		if (ext.equalsIgnoreCase("gif")) return false;
		if (ext.equalsIgnoreCase("keep")) return false;
		if (ext.equalsIgnoreCase("compatibility")) return false;
		if (ext.equalsIgnoreCase("websettings")) return false;
		if (ext.equalsIgnoreCase("website-config")) return false;
		if (ext.equalsIgnoreCase("ear")) return false;
		if (ext.equalsIgnoreCase("dat")) return false;
		if (ext.equalsIgnoreCase("checkedout")) return false;
		if (ext.equalsIgnoreCase("hijacked")) return false;
		if (ext.equalsIgnoreCase("tblxmi")) return false;
		
		return true;
	}

	public Iterator<LineItemInfo> getItems() {return m_list.iterator();}
	public int getSize() {return m_list.size();}
	public boolean isNone() {return getSize() < 1;}

	public void sort() {
		Collections.sort(m_list, new SortAsc());
	}
	private class SortAsc implements Comparator<LineItemInfo> {
		public int compare(LineItemInfo a, LineItemInfo b) {
			return a.getLine().compareTo(b.getLine());
		}
	}
}
