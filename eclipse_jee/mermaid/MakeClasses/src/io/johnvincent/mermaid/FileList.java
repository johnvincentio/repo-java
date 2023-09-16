package io.johnvincent.mermaid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileList {

	List<FileItem> m_collection = new ArrayList<FileItem> ();
	
	public FileList() {};

	public void add(String path) {
		if (path != null) m_collection.add (new FileItem(path));	
	}
	public void add(FileItem item) {
		if (item != null) m_collection.add (item);
	}
	public Iterator<FileItem> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(((FileItem) m_collection.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
