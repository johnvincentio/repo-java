package io.johnvincent.mermaid.filelist;

import java.io.File;

public class MakeList {

	private String m_baseDir;
	private String m_suffix;
	
	private FileList m_fileList;

	public MakeList(String baseDir, String suffix) {
		m_baseDir = baseDir;
		m_suffix = suffix;
		
		m_fileList = new FileList(m_baseDir);
		getFilesRecursively(new File(m_baseDir));
	}
	public FileList getList() { return m_fileList; }
	
    private void getFilesRecursively(File dir) {
//    	System.out.println(">>> getFilesRecursively; dir "+dir);
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
            	String str1 = String.valueOf(file);
                if(file.isDirectory()) {
                	getFilesRecursively(file);
                } else {
                	if (str1.toLowerCase().endsWith(m_suffix)) m_fileList.add(str1);       
                }
            }
        }
        else {
        	String str1 = String.valueOf(dir);
        	if (str1.toLowerCase().endsWith(m_suffix)) m_fileList.add(str1);
        }
//        System.out.println("<<< getFilesRecursively; size "+filesList.size());
    }
    
    public void show() {
    	System.out.println(">>> MakeList::show");
    	System.out.println("baseDir : "+m_baseDir);
    	System.out.println("suffix : "+m_suffix);
    	m_fileList.show();
        System.out.println("<<< MakeList::show");
    }
}
