package io.johnvincent.mermaid;

import java.io.File;
import java.util.List;

/*
 https://stackoverflow.com/questions/61246154/dynamically-loading-and-instantiating-a-class-that-is-not-in-the-classpath
 
 http://www.java2s.com/Tutorial/Java/0125__Reflection/LoadingaClassThatIsNotontheClasspath.htm
 
 
 * 
 */
public class App {
	
	private static final String FOLDER = 
		"/Users/jv/Desktop/MyDevelopment/github/website/gomoku/gomoku-server/gomoku-server" + 
//		"/target/classes/io/johnvincent/gomoku/library/";
	"/target/classes/io/johnvincent/gomoku/library/pattern/";
	private static final String SUFFIX = ".class";
	
	private static final String TESTFILE = 
		"/Users/jv/Desktop/MyDevelopment/github/website/gomoku/gomoku-server/gomoku-server/target" + 
	"/classes/io/johnvincent/gomoku/library/pattern/Pattern.class";
	
	private static final String AA = 
		"/Users/jv/Desktop/MyDevelopment/github/website/gomoku/gomoku-server/gomoku-server/" + 
		"/src/main/java/io/johnvincent/gomoku/library/pattern/Pattern.java";
	
	private static final String BB = 
		"/Users/jv/Desktop/MyDevelopment/github/website/gomoku/gomoku-server/gomoku-server/target/classes/";
	
    public static void main(String[] args) {
    	List <String> filesList = (new MakeList(new File(FOLDER), SUFFIX).makeList());
        System.out.println("Included files follow:");
        for (String file:filesList) {
            System.out.println(file);
        }
        System.out.println("End of Included files");
        
        LoadClasses loadClasses = new LoadClasses(filesList);
//        loadClasses.doWork(AA);
        
        loadClasses.doWork2(BB);
        
    }
}
