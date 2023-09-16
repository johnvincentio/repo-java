package io.johnvincent.mermaid;

public class FileItem {
	private String m_path;
	private String m_subpath;
	
	public FileItem(String path, String subpath) {
		m_path = path;
		m_subpath = subpath;
		
		System.out.println("FileItem; m_path "+m_path);
	}
	public String getPath() { return m_path; }
	public String getSubpath() { return m_subpath; }
	
	public String getClassLoaderName() {
		String str = m_subpath.replace(".class", "").replace("/", ".");
		return str;
		
	}

	public String toString() {
		return "("+m_path+","+m_subpath+")";
	}
}
