package io.johnvincent.mermaid;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MakeList {

	private File m_dir;
	private String m_suffix;
	
	List<String> m_filesList = new ArrayList<String>();

	public MakeList(File dir, String suffix) {
		m_dir = dir;
		m_suffix = suffix;
		
		m_filesList = getFilesRecursively(m_dir);
	}
	
	public List<String> getList() { return m_filesList; }
	
    private List <String> getFilesRecursively(File dir) {
//    	System.out.println(">>> getFilesRecursively; dir "+dir);
    	List<String> filesList = new ArrayList<String>();
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
//            	System.out.println("(2) file "+file);
            	String str1 = String.valueOf(file);
//            	System.out.println("(3) str1 "+str1);
                if(file.isDirectory()) {
                	filesList.addAll(getFilesRecursively(file));
                } else {
//                	System.out.println("(5) str1 "+str1);
                	if (str1.toLowerCase().endsWith(m_suffix)) filesList.add(str1);       
                }
            }
        }
        else {
        	String str1 = String.valueOf(dir);
//        	System.out.println("(10) str1 "+str1);
        	if (str1.toLowerCase().endsWith(m_suffix)) filesList.add(str1);
        }
//        System.out.println("<<< getFilesRecursively; size "+filesList.size());
        return filesList;
    }
    
    public void show() {
        System.out.println("Included files follow:");
        for (String file:m_filesList) {
            System.out.println(file);
        }
        System.out.println("End of Included files");
    }
}
