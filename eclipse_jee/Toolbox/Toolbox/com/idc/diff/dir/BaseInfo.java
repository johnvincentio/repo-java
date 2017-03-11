
package com.idc.diff.dir;

import java.util.ArrayList;
import java.util.Iterator;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;

public class BaseInfo implements Serializable {
	private static final long serialVersionUID = 1;
	private ArrayList<BaseItemInfo> m_list = new ArrayList<BaseItemInfo>();
	private String m_base;

	public BaseInfo (String base) {m_base = base;}
	public String getBase() {return m_base;}
	public Iterator<BaseItemInfo> getItems() {return m_list.iterator();}
	public void add (BaseItemInfo item) {
		if (item != null) m_list.add(item);
	}
	public int getSize() {return m_list.size();}
	public boolean isNone() {return getSize() < 1;}

	public BaseItemInfo getByRelativeName (BaseItemInfo item) {
//		System.out.println("stage 1 :"+item.getRelative()+":");
		if (item == null) return null;
		BaseItemInfo baseItemInfo;
		Iterator<BaseItemInfo> iter = getItems();
		while (iter.hasNext()) {
			baseItemInfo = (BaseItemInfo) iter.next();
//			System.out.println("stage 4 :"+baseItemInfo.getRelative()+":");
			if (baseItemInfo == null) continue;
			if (baseItemInfo.getRelative().equals(item.getRelative()))
				return baseItemInfo;
		}
//		System.out.println("stage 1a :"+item.getRelative()+":");
//		System.out.println("stage 7");
		return null;
	}
	public BaseInfo getByName (BaseItemInfo item) {
//		System.out.println(">>> getByName :"+item.getName()+":");
		if (item == null) return null;
		BaseInfo baseInfo = new BaseInfo(m_base);
		BaseItemInfo baseItemInfo;
		Iterator<BaseItemInfo> iter = getItems();
		while (iter.hasNext()) {
			baseItemInfo = (BaseItemInfo) iter.next();
			if (baseItemInfo == null) continue;
			if (baseItemInfo.getName().equals(item.getName()))
				baseInfo.add(baseItemInfo);
		}
//		System.out.println("<<< getByName :"+baseInfo.toString());
		return baseInfo;
	}

	public void sortByName() {Collections.sort (m_list, new SortFileName());}
	private class SortFileName implements Comparator <BaseItemInfo> {
		public int compare(BaseItemInfo a, BaseItemInfo b) {
			return a.getName().compareTo(b.getName());
		}
	}

	public void sortByRelative() {Collections.sort (m_list, new SortRelativeName());}
	private class SortRelativeName implements Comparator<BaseItemInfo> {
		public int compare(BaseItemInfo a, BaseItemInfo b) {
			return a.getRelative().compareTo(b.getRelative());
		}
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((BaseItemInfo) m_list.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
