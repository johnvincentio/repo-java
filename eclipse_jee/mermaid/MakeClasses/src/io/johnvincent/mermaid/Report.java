package io.johnvincent.mermaid;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import io.johnvincent.mermaid.classlist.ClassItem;
import io.johnvincent.mermaid.classlist.ClassList;
import io.johnvincent.mermaid.classlist.FieldItem;
import io.johnvincent.mermaid.classlist.FieldList;
import io.johnvincent.mermaid.classlist.MethodItem;
import io.johnvincent.mermaid.classlist.MethodList;

public class Report {
	private ClassList m_classList;

	public Report(ClassList classList) {
		m_classList = classList;
	}

	// methodModifiers()
	// classModifiers()
	// interfaceModifiers
	private void handleFields(ClassItem classItem, PrintWriter printWriter) {
		FieldList fieldList = classItem.getFieldList();
		if (fieldList == null) return;
		
		for (int i = 0; i < fieldList.getSize(); i++) {
			FieldItem fieldItem = fieldList.getItem(i);
			StringBuffer buf = new StringBuffer();
			buf.append(fieldItem.getAccessMark()).append(fieldItem.getType()).append(" ")
				.append(fieldItem.getName());
			printWriter.println(buf);
		}
	}
	
	private void handleMethods(ClassItem classItem, PrintWriter printWriter) {
		MethodList methodList = classItem.getMethodList();
		if (methodList == null) return;
		
		for (int i = 0; i < methodList.getSize(); i++) {
			MethodItem methodItem = methodList.getItem(i);

//			StringBuffer buf = new StringBuffer();
//			buf.append(fieldItem.getAccessMark()).append(fieldItem.getType()).append(" ")
//				.append(fieldItem.getName());
//			printWriter.println(buf);
		}
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
				handleMethods(classItem, printWriter);

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
