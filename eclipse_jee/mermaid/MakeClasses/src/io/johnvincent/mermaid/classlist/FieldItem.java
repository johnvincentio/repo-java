package io.johnvincent.mermaid.classlist;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class FieldItem {
	private String m_name;
	private String m_type;

	private boolean bPrivate;
	private boolean bPublic;
	private boolean bProtected;
	private boolean bStatic;
	private boolean bFinal;
	private boolean bInterface;

	public FieldItem(Field field) {
		Class<?> tp = field.getType();
		int mods = field.getModifiers();

		m_name = field.getName();
		m_type = tp.getSimpleName();		// ex; String

		bPrivate = Modifier.isPrivate(mods);
		bPublic = Modifier.isPublic(mods);
		bProtected = Modifier.isProtected(mods);
		bStatic = Modifier.isStatic(mods);
		bFinal = Modifier.isFinal(mods);
		bInterface = Modifier.isInterface(mods);
	};

	public String getName() {
		return m_name;
	}

	public String getType() {
		return m_type;
	}

	private String getAccessMark() {
		if (bPrivate)
			return "-";
		if (bPublic)
			return "+";
		if (bProtected)
			return "#";
		return "";
	}

	public String getMermaid() {
		StringBuffer buf = new StringBuffer();
		buf.append(getAccessMark()).append(getType()).append(" ").append(getName());
		return buf.toString();
	}
	
	public boolean isStatic() {
		return bStatic;
	}

	public boolean isFinal() {
		return bFinal;
	}

	public boolean isInterface() {
		return bInterface;
	}
}

//System.out.println(Modifier.toString(Modifier.classModifiers()));

/*
 * System.out.println(Modifier.toString(field.getModifiers()));
 * 
 * System.out.println("field.getModifiers() " + field.getModifiers());
 * System.out.println("field (1) " + field.toGenericString());
 * System.out.println("field (2) " + field.getAnnotatedType());
 * System.out.println("field (3) " + field.getGenericType());
 * 
 * Class<?> tp = field.getType(); // gets the type
 * System.out.println("type (1) "+tp);
 * System.out.println("type (2) "+tp.getCanonicalName());
 * System.out.println("type (3) "+tp.getName());
 * System.out.println("type (4) "+tp.getPackageName());
 * System.out.println("type (5) "+tp.getSimpleName());
 * System.out.println("type (6) "+tp.getTypeName());
 * 
 * System.out.println("Modifier: " + Modifier.isPrivate(mods));
 * System.out.println("Modifier: " + Modifier.isFinal(mods));
 * System.out.println("Modifier: " + Modifier.isInterface(mods));
 * System.out.println("Modifier: " + Modifier.isProtected(mods));
 * System.out.println("Modifier: " + Modifier.isPublic(mods));
 * System.out.println("Modifier: " + Modifier.isStatic(mods));
 */
