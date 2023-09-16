package io.johnvincent.mermaid.filelist;

public class FileItem {
	private String m_name;
	private String m_path;
	private String m_subpath;
	private String m_package;
	
	public FileItem(String path, String subpath) {
		m_path = path;
		m_subpath = subpath;
		String str = m_subpath.replace(".class", "").replace("/", ".");
		m_name = str.substring(str.lastIndexOf(".") + 1);
		m_package = str.substring(0, str.lastIndexOf("."));
	}
	public String getPath() { return m_path; }
	public String getSubpath() { return m_subpath; }
	public String getPackage() { return m_package; }
	
	public String getClassLoaderName() {
		return m_subpath.replace(".class", "").replace("/", ".");
	}

	public String toString() {
		return "("+m_name+","+m_package+","+m_path+","+m_subpath+")";
	}
}
