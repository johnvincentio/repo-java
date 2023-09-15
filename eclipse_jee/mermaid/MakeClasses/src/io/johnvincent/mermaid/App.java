package io.johnvincent.mermaid;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class App {
	
	private static final String FOLDER = 
		"/Users/jv/Desktop/MyDevelopment/github/website/gomoku/gomoku-server/gomoku-server" + 
//		"/target/classes/io/johnvincent/gomoku/library/";
	"/target/classes/io/johnvincent/gomoku/library/pattern/";
	private static final String SUFFIX = ".class";
	
    public static List <String> getFilesRecursively(File dir){
    	System.out.println(">>> getFilesRecursively; dir "+dir);
        List <String> filesList = new ArrayList<String>();
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
//            	System.out.println("(2) file "+file);
            	String str1 = String.valueOf(file);
//            	System.out.println("(3) str1 "+str1);
                if(file.isDirectory()) {
//                	filesList.add(str1);
                	filesList.addAll(getFilesRecursively(file));
//                	getFilesRecursively(file);
                } else {
//                	System.out.println("(5) str1 "+str1);
                	if (str1.toLowerCase().endsWith(SUFFIX)) filesList.add(str1);       
                }
            }
        }
        else {
        	String str1 = String.valueOf(dir);
//        	System.out.println("(10) str1 "+str1);
        	if (str1.toLowerCase().endsWith(SUFFIX)) filesList.add(str1);
        }
        System.out.println("<<< getFilesRecursively; size "+filesList.size());
        return filesList;
    }

    public static void main(String[] args) {
        List <String> ls = getFilesRecursively(new File(FOLDER));
        System.out.println("Included files follow:");
        for (String file:ls) {
            System.out.println(file);
        }
        System.out.println("End of Included files");
//        System.out.println(ls.size());
    }
}
