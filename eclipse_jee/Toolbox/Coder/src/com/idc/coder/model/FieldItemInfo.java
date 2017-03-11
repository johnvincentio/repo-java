package com.idc.coder.model;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class FieldItemInfo {
	private Field field;
	public FieldItemInfo (Field field) {
//		System.out.println(">>> FieldItemInfo constructor; field "+field.getName());
		this.field = field;
//		System.out.println("<<< FieldItemInfo constructor");
	}
	public Field getField() {return field;}

	private boolean isFinal() {return Modifier.isFinal (field.getModifiers());}
	private boolean isPrivate() {return Modifier.isPrivate (field.getModifiers());}
	private boolean isProtected() {return Modifier.isProtected(field.getModifiers());}
	private boolean isPublic() {return Modifier.isPublic (field.getModifiers());}
	private boolean isStatic() {return Modifier.isStatic (field.getModifiers());}
	private boolean isTransient() {return Modifier.isTransient (field.getModifiers());}

	public String createDefinition() {
		StringBuffer buf = new StringBuffer();
		if (isPublic())
			buf.append ("public ");
		else if (isPrivate())
			buf.append ("private ");
		else if (isProtected())
			buf.append ("protected ");
		if (isStatic()) buf.append ("static ");
		if (isFinal()) buf.append ("final ");
		if (isTransient()) buf.append ("transient ");

		Class<?> type = getField().getType();
		buf.append (type.getSimpleName());

		buf.append (" ").append (getField().getName());

		if (type.isArray()) buf.append ("[]");
		buf.append (";");
		return buf.toString();
	}
	public String toString() {
		return "("+getField()+")";
	}
}
