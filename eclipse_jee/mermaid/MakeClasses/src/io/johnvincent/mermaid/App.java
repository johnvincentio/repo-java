package io.johnvincent.mermaid;

import java.io.File;

public class App {
	
	private static final String FOLDER = 
		"/Users/jv/Desktop/MyDevelopment/github/website/gomoku/gomoku-server/gomoku-server" + 
		"/target/classes/io/johnvincent/gomoku/library/pattern/";
	private static final String SUFFIX = ".class";
	
	private static final String BB = 
		"/Users/jv/Desktop/MyDevelopment/github/website/gomoku/gomoku-server/gomoku-server/target/classes/";
	
    public static void main(String[] args) {
    	MakeList makeList = new MakeList(new File(FOLDER), SUFFIX);
    	makeList.show();
    	FileList fileList = makeList.getList();
        
        LoadClasses loadClasses = new LoadClasses(FOLDER, fileList);
//        loadClasses.doWork(AA);
        
        loadClasses.doWork2(BB);
        
    }
}
