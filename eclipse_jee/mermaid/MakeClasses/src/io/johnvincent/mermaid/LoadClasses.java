package io.johnvincent.mermaid;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

// /Users/jv/Desktop/MyDevelopment/github/website/gomoku/gomoku-server/gomoku-server/target/classes/io/johnvincent/gomoku/library/pattern/
// /Users/jv/Desktop/MyDevelopment/github/website/gomoku/gomoku-server/gomoku-server/target/classes

public class LoadClasses {

	private String m_baseDir;
	private ClassList m_classList;
	private FileList m_fileList;

	public LoadClasses(String baseDir, FileList fileList) {
		m_baseDir = baseDir;
		m_fileList = fileList;
	}
	
	public void loadClasses() {
		for (int i = 0; i < m_fileList.getSize(); i++) {
			
		}
		
	}

	public void doWork2(String str) {
		System.out.println(">>> doWork2; file " + str);
		
		m_classList = new ClassList(str);
		
		String a1 = "/Users/jv/Desktop/MyDevelopment/github/website/gomoku/gomoku-server/gomoku-server/target/classes/";

		String p1 = "io.johnvincent.gomoku.library.pattern.Pattern";
		try {
			System.out.println("doWork2 - stage 1");
			File file = new File(a1);
			System.out.println("doWork2 - stage 2");

			URL url = file.toURI().toURL();
			System.out.println("doWork2 - stage 3");
			URL[] urls = new URL[] { url };
			System.out.println("doWork2 - stage 4");

			URLClassLoader loader = new URLClassLoader(urls);
			System.out.println("doWork2 - stage 5");

			Class<?> clazz = loader.loadClass(p1);
			loader.close();
			
			ClassItem classItem = new ClassItem(clazz);
			classItem.show();

			System.out.println("doWork2 - stage 100");
		} catch (Exception ex) {
			System.out.println("Exception; ex " + ex);
		}
	}
}

/*
 * public void doWork1(String str) { System.out.println(">>> doWork1; file "+
 * str); String a1 =
 * "file:/Users/jv/Desktop/MyDevelopment/github/website/gomoku/gomoku-server/gomoku-server/target/classes";
 * 
 * try { URL[] urls = new URL[]{new URL(a1)}; System.out.println("urls "+urls);
 * ClassLoader cl = new URLClassLoader(urls); String p1 =
 * "io.johnvincent.gomoku.library.pattern.Pattern";
 * cl.loadClass(p1).getDeclaredConstructor().newInstance(); } catch(Exception
 * ex) { System.out.println("Exception; ex "+ex); } }
 */
/*
 * /Users/jv/Desktop/MyDevelopment/github/website/gomoku/gomoku-server/gomoku-
 * server/target/classes/io/johnvincent/gomoku/library/pattern
 */

// /Users/jv/Desktop/MyDevelopment/github/website/gomoku/gomoku-server/gomoku-server/target/classes/
