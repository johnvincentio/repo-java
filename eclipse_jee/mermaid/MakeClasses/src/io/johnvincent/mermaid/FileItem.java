package io.johnvincent.mermaid;

public class FileItem {
	private String m_path;
	private String m_subpath;
	
	public FileItem(String path) {
		m_path = path;
		
		System.out.println("FileItem; m_path "+m_path);
	}

	public String toString() {
		return "("+m_path+","+m_subpath+")";
	}
}
