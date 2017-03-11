package com.idc.coder.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ClazzItemInfo {
	private Class<?> clazz;
	private ClazzItemInfo superClazz;
	private ConstructorInfo constructorInfo = new ConstructorInfo();
	private InterfaceInfo interfaceInfo = new InterfaceInfo();
	private FieldInfo fieldInfo = new FieldInfo();
	private MethodInfo methodInfo = new MethodInfo();
	public ClazzItemInfo (Class<?> clazz) {
		System.out.println(">>> ClazzItemInfo constructor; class "+clazz.getSimpleName());
		this.clazz = clazz;

		clazz.getModifiers();
		Class<?> sup = clazz.getSuperclass();
		if (sup != null) superClazz = new ClazzItemInfo (sup);
	
		for (Class<?> interfaze : clazz.getInterfaces()) {
			interfaceInfo.add (new InterfaceItemInfo (interfaze));
		}

		for (Constructor<?> constructor : clazz.getConstructors()) {
			constructorInfo.add (new ConstructorItemInfo (constructor));
		}
		for (Field field : clazz.getDeclaredFields()) {
			fieldInfo.add (new FieldItemInfo (field));
		}
		for (Method method : clazz.getMethods()) {
			methodInfo.add (new MethodItemInfo (method));
		}
		System.out.println("<<< ClazzItemInfo constructor; class "+clazz.getSimpleName());
	}
	public Class<?> getClazz() {return clazz;}
	public boolean isSuperClazz() {
		if (superClazz == null) return false;
		if (superClazz.getName().equals("Object")) return false;
		return true;
	}
	public ClazzItemInfo getSuperClazz() {return superClazz;}
	public ConstructorInfo getConstructorInfo() {return constructorInfo;}

	public InterfaceInfo getInterfaceInfo() {return interfaceInfo;}
	public FieldInfo getFieldInfo() {return fieldInfo;}
	public MethodInfo getMethodInfo() {return methodInfo;}

	public String getName() {return clazz.getSimpleName();}
	public String getPackage() {return clazz.getPackage().getName();}

	public boolean isAbstract() {return Modifier.isAbstract (clazz.getModifiers());}
	public boolean isFinal() {return Modifier.isFinal (clazz.getModifiers());}
	public boolean isInterface() {return Modifier.isInterface (clazz.getModifiers());}
	public boolean isPrivate() {return Modifier.isPrivate (clazz.getModifiers());}
	public boolean isProtected() {return Modifier.isProtected(clazz.getModifiers());}
	public boolean isPublic() {return Modifier.isPublic (clazz.getModifiers());}
	public boolean isStatic() {return Modifier.isStatic (clazz.getModifiers());}
	public boolean isTransient() {return Modifier.isTransient (clazz.getModifiers());}

	public String createDefinition() {
		StringBuffer buf = new StringBuffer();
		if (isPublic())
			buf.append ("public ");
		else if (isPrivate())
			buf.append ("private ");
		else if (isProtected())
			buf.append ("protected ");

		if (isAbstract())
			buf.append ("abstract ");

		if (isInterface())
			buf.append ("interface ");
		else
			buf.append ("class ");

		buf.append (getName()).append(" ");

		if (isSuperClazz())
			buf.append ("extends ").append (getSuperClazz().getName()).append(" ");

		if (getInterfaceInfo().isInterface())
			buf.append ("implements ").append (getInterfaceInfo().getInterfaces()).append(" ");

		buf.append ("{");
		
		return buf.toString();
	}

	public String toString() {
		return "(\n"+
				"\t"+"class = "+getClazz()+"\n"+
				"\t"+"name "+getName()+"\n "+
				"\t"+"package "+getPackage()+"\n"+
				"\t"+getConstructorInfo()+
				"\t"+getInterfaceInfo()+
				"\t"+getFieldInfo()+
				"\t"+getMethodInfo()+
				")";
	}
}
