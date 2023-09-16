
package io.johnvincent.mermaid.classlist;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class MethodItem {

	private String m_name;
	
	private boolean bPrivate;
	private boolean bPublic;
	private boolean bProtected;
	private boolean bStatic;
	private boolean bFinal;
	private boolean bInterface;
	
	public MethodItem(Method method) {
		System.out.println("MethodItem; (1) method "+method);
		System.out.println("MethodItem; (1a) method "+method.getName());
		
		System.out.println("MethodItem; (2) method "+method.getParameters());
		System.out.println("MethodItem; (3) method "+method.getParameterTypes());
		System.out.println("MethodItem; (4) method "+method.getReturnType());
		
		Parameter[] parameters = method.getParameters();
		for (Parameter parameter:parameters) {
			System.out.println("parameter "+parameter);
		}
		
		Class<?>[] clazzes = method.getParameterTypes();
		for (Class<?> clazz:clazzes) {
			System.out.println("clazz "+clazz);
		}
		
//		Class<?> tp = method.getType();
		int mods = method.getModifiers();

		m_name = method.getName();
//		m_type = method.getSimpleName();		// ex; String

		bPrivate = Modifier.isPrivate(mods);
		bPublic = Modifier.isPublic(mods);
		bProtected = Modifier.isProtected(mods);
		bStatic = Modifier.isStatic(mods);
		bFinal = Modifier.isFinal(mods);
		bInterface = Modifier.isInterface(mods);
	}
}


