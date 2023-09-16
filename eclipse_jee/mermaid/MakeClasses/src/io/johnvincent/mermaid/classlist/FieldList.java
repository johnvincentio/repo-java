package io.johnvincent.mermaid.classlist;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FieldList {
	private List<FieldItem> m_collection = new ArrayList<FieldItem>();
	
	public void add(FieldItem item) {
		if (item != null) m_collection.add(item);
	}
	
	public int getSize() { return m_collection.size(); }
	public FieldItem getItem(int id) { return m_collection.get(id); }
	
	public void add(Field[] fields) {
		if (fields == null) return;
		for (Field field:fields) {
			FieldItem fieldItem = new FieldItem(field);
			add(fieldItem);
		}
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append((m_collection.get(i)).toString());
		return "("+buf.toString()+")";
	}
}

