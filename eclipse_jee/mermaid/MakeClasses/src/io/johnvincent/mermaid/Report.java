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

		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			System.out.println("field.getName() " + field.getName());
			System.out.println("field.getModifiers() " + field.getModifiers());
			System.out.println("field (1) " + field.toGenericString());
			System.out.println("field (2) " + field.getAnnotatedType());
			System.out.println("field (3) " + field.getGenericType());

			System.out.println("Field: " + field);
			
			int mods = field.getModifiers();
			System.out.println("Modifier: " + Modifier.isPrivate(mods));
			System.out.println("Modifier: " + Modifier.isFinal(mods));
			System.out.println("Modifier: " + Modifier.isInterface(mods));
			System.out.println("Modifier: " + Modifier.isProtected(mods));
			System.out.println("Modifier: " + Modifier.isPublic(mods));
			System.out.println("Modifier: " + Modifier.isStatic(mods));

			Class<?> tp = field.getType(); // gets the type
			System.out.println("type (1) "+tp);
			System.out.println("type (2) "+tp.getCanonicalName());
			System.out.println("type (3) "+tp.getName());
			System.out.println("type (4) "+tp.getPackageName());
			System.out.println("type (5) "+tp.getSimpleName());
			System.out.println("type (6) "+tp.getTypeName());

//			System.out.println(Modifier.toString(Modifier.classModifiers()));
			System.out.println(Modifier.toString(field.getModifiers()));
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
