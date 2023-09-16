package io.johnvincent.mermaid.classlist;

import java.util.ArrayList;
import java.util.List;

import io.johnvincent.mermaid.filelist.FileItem;

public class ClassList {
	private String m_baseDir;
	private List<ClassItem> m_collection = new ArrayList<ClassItem>();
	
	public ClassList(String baseDir) {
		m_baseDir = baseDir;
	}

	public String getBaseDir() { return m_baseDir; }
	public void add(ClassItem item) {
		if (item != null) m_collection.add(item);
	}
	
	public int getSize() { return m_collection.size(); }
	public ClassItem getItem(int id) { return m_collection.get(id); }
	
    public void show() {
        System.out.println(">>> ClassList::show");
		for (int i = 0; i < m_collection.size(); i++) {
			ClassItem classItem = m_collection.get(i);
			System.out.println(classItem.toString());
		}
		System.out.println("<<< ClassList::show");
    }
}
