package io.johnvincent.mermaid.classlist;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ConstructorList {
	private List<ConstructorItem> m_collection = new ArrayList<ConstructorItem>();
	
	public int getSize() { return m_collection.size(); }
	public ConstructorItem getItem(int id) { return m_collection.get(id); }
	
	public void add(Constructor<?>[] constructors) {
		if (constructors == null) return;
		for (Constructor<?> constructor:constructors) {
			ConstructorItem constructorItem = new ConstructorItem(constructor);
			if (constructorItem != null) m_collection.add(constructorItem);
		}
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append((m_collection.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
