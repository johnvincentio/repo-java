package io.johnvincent.mermaid;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;

public class ClassInfo {
	private String m_name;
	private String m_package;
	private Constructor[] m_constructors;
	private Method[] m_methods;
	private Field[] m_fields;
	
	public ClassInfo() {};
	
	
}
