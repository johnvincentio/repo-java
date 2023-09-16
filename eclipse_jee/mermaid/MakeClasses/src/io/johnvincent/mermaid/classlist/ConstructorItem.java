package io.johnvincent.mermaid.classlist;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ConstructorItem {

	private String m_packageName;
	private String m_name;
	private String m_fullName;
	private String m_returnType;
	private List<String> m_types = new ArrayList<String>();
	
	private boolean bPrivate;
	private boolean bPublic;
	private boolean bProtected;
	private boolean bStatic;
	private boolean bFinal;
	private boolean bInterface;
	
	public ConstructorItem(String packageName, Constructor<?> constructor) {
		m_packageName = packageName;

		m_fullName = constructor.getName();
		m_name = m_fullName.replace(m_packageName+".", "");
		System.out.println("m_packageName "+m_packageName+" m_name "+m_name);
		
		System.out.println("ConstructorItem; (1) "+constructor.getName());
		System.out.println("ConstructorItem; (2) "+constructor.getClass());
//		System.out.println("ConstructorItem; (1) "+constructor.getPackageName());
//		System.out.println("ConstructorItem; (1) "+constructor);
//		System.out.println("ConstructorItem; (1) "+constructor);
//		System.out.println("ConstructorItem; (1) "+constructor);
//		System.out.println("ConstructorItem; (1) "+constructor);
		
		Class<?> clz = constructor.getClass();
		System.out.println("clz; (1) "+clz.getName());
		System.out.println("clz; (2) "+clz.getCanonicalName());
		System.out.println("clz; (3) "+clz.getSimpleName());
		System.out.println("clz; (4) "+clz.getTypeName());
		System.out.println("clz; (5) "+clz.getPackageName());
//		System.out.println("clz; (1) "+clz.);
//		System.out.println("clz; (1) "+clz);
		
		
		Class<?>[] clazzes = constructor.getParameterTypes();
		for (Class<?> clazz:clazzes) {
			m_types.add(clazz.getSimpleName());
		}
		
		int mods = constructor.getModifiers();

		bPrivate = Modifier.isPrivate(mods);
		bPublic = Modifier.isPublic(mods);
		bProtected = Modifier.isProtected(mods);
		bStatic = Modifier.isStatic(mods);
		bFinal = Modifier.isFinal(mods);
		bInterface = Modifier.isInterface(mods);
	}

	public String getMermaid() {
		StringBuffer buf = new StringBuffer();
		buf.append(getAccessMark()).append(m_name);
		buf.append("(").append(String.join(",", m_types)).append(") ");
		buf.append(m_returnType);
		return buf.toString();
	}
	
	private String getAccessMark() {
		if (bPrivate) return "-";
		if (bPublic) return "+";
		if (bProtected) return "#";
		return "";
	}

	public boolean isStatic() {return bStatic;}
	public boolean isFinal() {return bFinal;}
	public boolean isInterface() {return bInterface;}
	
	public String toString() {
		return "("+m_name+","+m_returnType+",["+String.join(",", m_types)+"],"+
				bPrivate+","+bPublic+","+bProtected+","+bStatic+","+bFinal+","+bInterface+
			")";
	}
}
