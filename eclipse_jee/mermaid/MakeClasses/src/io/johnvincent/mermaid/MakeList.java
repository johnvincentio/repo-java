package io.johnvincent.mermaid;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
//    	List<String> filesList = new ArrayList<String>();
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
//            	System.out.println("(2) file "+file);
            	String str1 = String.valueOf(file);
//            	System.out.println("(3) str1 "+str1);
                if(file.isDirectory()) {
                	getFilesRecursively(file);
                } else {
//                	System.out.println("(5) str1 "+str1);
                	if (str1.toLowerCase().endsWith(m_suffix)) m_fileList.add(str1);       
                }
            }
        }
        else {
        	String str1 = String.valueOf(dir);
//        	System.out.println("(10) str1 "+str1);
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
