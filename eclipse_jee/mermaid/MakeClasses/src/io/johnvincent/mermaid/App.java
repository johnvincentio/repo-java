package io.johnvincent.mermaid;

import java.io.File;
import java.util.List;

public class App {
	
	private static final String FOLDER = 
		"/Users/jv/Desktop/MyDevelopment/github/website/gomoku/gomoku-server/gomoku-server" + 
//		"/target/classes/io/johnvincent/gomoku/library/";
	"/target/classes/io/johnvincent/gomoku/library/pattern/";
	private static final String SUFFIX = ".class";
	
    public static void main(String[] args) {
    	List <String> filesList = (new MakeList(new File(FOLDER), SUFFIX).makeList());
        System.out.println("Included files follow:");
        for (String file:filesList) {
            System.out.println(file);
        }
        System.out.println("End of Included files");
    }
}
