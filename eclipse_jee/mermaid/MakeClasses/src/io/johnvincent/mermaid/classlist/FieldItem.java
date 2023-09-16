package io.johnvincent.mermaid.classlist;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class FieldItem {

	public FieldItem(Field field) {
		System.out.println("FieldList::add; field "+field);
		
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

//		System.out.println(Modifier.toString(Modifier.classModifiers()));
		System.out.println(Modifier.toString(field.getModifiers()));
	};
}
