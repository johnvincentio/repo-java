
package io.johnvincent.mermaid.classlist;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class MethodItem {

	private String m_name;
	private String m_returnType;
	private List<String> m_types = new ArrayList<String>();
	
	private boolean bPrivate;
	private boolean bPublic;
	private boolean bProtected;
	private boolean bStatic;
	private boolean bFinal;
	private boolean bInterface;
	
	public MethodItem(Method method) {
		m_name = method.getName();
		m_returnType = method.getReturnType().getName();
		
		Class<?>[] clazzes = method.getParameterTypes();
		for (Class<?> clazz:clazzes) {
			m_types.add(clazz.getSimpleName());
		}
		
		int mods = method.getModifiers();

		bPrivate = Modifier.isPrivate(mods);
		bPublic = Modifier.isPublic(mods);
		bProtected = Modifier.isProtected(mods);
		bStatic = Modifier.isStatic(mods);
		bFinal = Modifier.isFinal(mods);
		bInterface = Modifier.isInterface(mods);
	}
}

/*

		Class<?> clz = method.getReturnType();

		System.out.println("clazz; (1) "+clazz);
		System.out.println("clazz; (2) "+clazz.getCanonicalName());
		System.out.println("clazz; (3) "+clazz.getName());
		System.out.println("clazz; (4) "+clazz.getSimpleName());
		System.out.println("clazz; (5) "+clazz.getTypeName());
 
		System.out.println("MethodItem; (1) method "+method);
		System.out.println("MethodItem; (1a) method "+method.getName());
		System.out.println("MethodItem; (2) method "+method.getParameters());
		System.out.println("MethodItem; (3) method "+method.getParameterTypes());
		System.out.println("MethodItem; (4) method "+method.getReturnType());
		
		System.out.println("ReturnType; (1) "+clz.getCanonicalName());
		System.out.println("ReturnType; (2) "+clz.descriptorString());
		System.out.println("ReturnType; (3) "+clz.getTypeName());
		System.out.println("ReturnType; (4) "+clz.getName());

//		Parameter[] parameters = method.getParameters();
//		for (Parameter parameter:parameters) {
//			System.out.println("parameter "+parameter);
//		}

*/

