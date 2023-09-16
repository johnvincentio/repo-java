package io.johnvincent.mermaid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileList {

	private String m_baseDir;
	List<FileItem> m_collection = new ArrayList<FileItem> ();
	
	public FileList(String baseDir) { m_baseDir = baseDir; };

	public void add(String path) {
		if (path != null) {
			String subpath = path.replace(m_baseDir, "");
			m_collection.add(new FileItem(path, subpath));	
		}
	}
	public void add(FileItem item) { if (item != null) m_collection.add (item); }
	public Iterator<FileItem> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

    public void show() {
        System.out.println(">>> FileList::show");
		for (int i = 0; i < m_collection.size(); i++) {
			FileItem fileItem = m_collection.get(i);
			System.out.println(fileItem.toString());
		}
		System.out.println("<<< FileList::show");
    }
}
