package io.johnvincent.mermaid;

import io.johnvincent.mermaid.classlist.ClassList;
import io.johnvincent.mermaid.classlist.LoadClasses;
import io.johnvincent.mermaid.filelist.FileList;
import io.johnvincent.mermaid.filelist.MakeList;

public class App {

	private static final String SUFFIX = ".class";

	@SuppressWarnings("unused")
	private static final String FOLDER_2 = 
		"/Users/jv/Desktop/MyDevelopment/github/website/gomoku/gomoku-server/gomoku-server" +
		"/target/classes/io/johnvincent/gomoku/library/pattern/";

	private static final String FOLDER =
		"/Users/jv/Desktop/MyDevelopment/github/website/gomoku/gomoku-server/gomoku-server/target/classes/";

	private static final String REPORT = 
		"/Users/jv/Desktop/MyDevelopment/github/java/repo-java/eclipse_jee/mermaid/MakeClasses/jv.txt";
	
	public static void main(String[] args) {
		MakeList makeList = new MakeList(FOLDER, SUFFIX);
//		makeList.show();

		FileList fileList = makeList.getList();
//		fileList.show();

		LoadClasses loadClasses = new LoadClasses(fileList);
		loadClasses.loadClasses();
		ClassList classList = loadClasses.getClassList();
//		classList.show();
		
		Report report = new Report(classList);
		report.makeMermaid("Gomoku Classes", REPORT);
	}
}
