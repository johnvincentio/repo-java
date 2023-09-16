package io.johnvincent.mermaid;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import io.johnvincent.mermaid.classlist.ClassItem;
import io.johnvincent.mermaid.classlist.ClassList;

public class Report {
	private ClassList m_classList;

	public Report(ClassList classList) {
		m_classList = classList;
	}

	// methodModifiers()
	// classModifiers()
	// interfaceModifiers
	private void handleFields(ClassItem classItem, PrintWriter printWriter) {
		Field[] fields = classItem.getFields();
		if (fields == null)
			return;


	}

	public void makeMermaid(String title, String output) {
		try {
			File file = new File(output);
			FileWriter fileWriter = new FileWriter(file);
			PrintWriter printWriter = new PrintWriter(fileWriter);

			printWriter.println("---");
			printWriter.println(title);
			printWriter.println("---");
			printWriter.println("");
			printWriter.println("classDiagram");

			for (int i = 0; i < m_classList.getSize(); i++) {
				ClassItem classItem = m_classList.getItem(i);
				String name = classItem.getSimpleName();
				System.out.println("classItem " + classItem.toString());
				printWriter.println("class " + name + " {");

				handleFields(classItem, printWriter);

//				printWriter.write(classItem.toString());
				printWriter.println("}");
				break;
			}

			printWriter.close();
			fileWriter.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
