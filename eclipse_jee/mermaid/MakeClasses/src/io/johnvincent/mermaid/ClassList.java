package io.johnvincent.mermaid;

import java.util.ArrayList;
import java.util.List;

public class ClassList {
	private String m_baseDir;
	private List<ClassItem> m_list = new ArrayList<ClassItem>();
	
	public ClassList(String baseDir) {
		m_baseDir = baseDir;
	}

	public String getBaseDir() { return m_baseDir; }
	public void add(ClassItem item) {
		if (item != null) m_list.add(item);
	}
	
	public int getSize() { return m_list.size(); }
	public ClassItem getItem(int id) { return m_list.get(id); }

}
