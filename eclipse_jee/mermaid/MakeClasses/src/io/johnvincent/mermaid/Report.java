package io.johnvincent.mermaid;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Report {
	private ClassList m_classList;
	
	public Report(ClassList classList) {
		m_classList = classList;
	}
	
	public void makeMermaid(String title, String output) {
		try {
			File file = new File(output);
			FileWriter fileWriter = new FileWriter(file);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			
			printWriter.write("---");
			printWriter.write(title);
			printWriter.write("---");
			printWriter.write("classDiagram");
	
			for (int i = 0; i < m_classList.getSize(); i++) {
				ClassItem classItem = m_classList.getItem(i);
				String name = classItem.getName();
				printWriter.write("class "+name+" {");
				printWriter.write(classItem.toString());
				printWriter.write("}");
			}
			
			printWriter.close();
			fileWriter.close();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
