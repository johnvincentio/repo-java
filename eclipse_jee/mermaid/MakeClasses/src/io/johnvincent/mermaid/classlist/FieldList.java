package io.johnvincent.mermaid.classlist;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FieldList {
	private List<FieldItem> m_list = new ArrayList<FieldItem>();
	
	public void add(FieldItem item) {
		if (item != null) m_list.add(item);
	}
	
	public int getSize() { return m_list.size(); }
	public FieldItem getItem(int id) { return m_list.get(id); }
	
	public void add(Field[] fields) {
		if (fields == null) return;
		for (Field field:fields) {
			FieldItem fieldItem = new FieldItem(field);
			add(fieldItem);
		}
	}
}

