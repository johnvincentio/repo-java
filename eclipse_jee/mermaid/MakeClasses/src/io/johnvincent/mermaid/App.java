package io.johnvincent.mermaid;

public class App {
	
	private static final String FOLDER = 
		"/Users/jv/Desktop/MyDevelopment/github/website/gomoku/gomoku-server/gomoku-server" + 
		"/target/classes/io/johnvincent/gomoku/library/pattern/";
	private static final String SUFFIX = ".class";
	
	private static final String BB = 
		"/Users/jv/Desktop/MyDevelopment/github/website/gomoku/gomoku-server/gomoku-server/target/classes/";
	
    public static void main(String[] args) {
    	MakeList makeList = new MakeList(FOLDER, SUFFIX);
    	makeList.show();
    	
    	FileList fileList = makeList.getList();
    	fileList.show();
        
        LoadClasses loadClasses = new LoadClasses(FOLDER, fileList);
//        loadClasses.doWork(AA);
        
        loadClasses.doWork2(BB);
        
    }
}
