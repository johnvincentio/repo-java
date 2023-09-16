
package io.johnvincent.mermaid.classlist;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MethodList {
	private List<MethodItem> m_collection = new ArrayList<MethodItem>();
	
	public int getSize() { return m_collection.size(); }
	public MethodItem getItem(int id) { return m_collection.get(id); }
	
	public void add(Method[] methods) {
		if (methods == null) return;
		for (Method method:methods) {
			MethodItem methodItem = new MethodItem(method);
			if (methodItem != null) m_collection.add(methodItem);
		}
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append((m_collection.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
