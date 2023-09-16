
package io.johnvincent.mermaid.classlist;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MethodList {
	private List<MethodItem> m_list = new ArrayList<MethodItem>();
	
	public void add(MethodItem item) {
		if (item != null) m_list.add(item);
	}
	
	public int getSize() { return m_list.size(); }
	public MethodItem getItem(int id) { return m_list.get(id); }
	
	public void add(Method[] methods) {
		if (methods == null) return;
		for (Method method:methods) {
			MethodItem methodItem = new MethodItem(method);
			add(methodItem);
		}
	}
}


