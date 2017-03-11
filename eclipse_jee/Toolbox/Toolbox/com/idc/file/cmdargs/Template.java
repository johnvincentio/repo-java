package com.idc.file.cmdargs;

import java.util.ArrayList;
import java.util.List;

public class Template {
	private List<ParameterDef> m_list = new ArrayList<ParameterDef>();
	private String m_strObject;

	public Template (Object obj) {
		m_strObject = obj.getClass().getName();
	}
	public void add (ParameterDef def) {m_list.add(def);}

	public ParameterDef find (char flag) {
		ParameterDef def;
		for (int i=0; i<m_list.size(); i++) {
			def = (ParameterDef) m_list.get(i);
			if (def.getFlag() == flag)
				return def;
		}
		return null;
	}
	public ParameterDef index (int index) {
		if (index >= 0 && index < m_list.size())
			return (ParameterDef) m_list.get(index);
		else
			return null;
	}
	public void show(String msg) {
		System.out.println(">>> Template::show; "+msg);
		for (int i=0; i<m_list.size(); i++) {
			ParameterDef def = (ParameterDef) m_list.get(i);
			System.out.print("(F,D,V) ("+def.getFlag()+","+
					def.getDescription()+",");
			if (def instanceof BoolDef)
				System.out.print(((BoolDef) def).getValue());
			else if (def instanceof StringDef)
				System.out.print(((StringDef) def).getValue());
			else if (def instanceof IntDef)
				System.out.print(((IntDef) def).getValue());
			else if (def instanceof FloatDef)
				System.out.print(((FloatDef) def).getValue());
			System.out.println(")");
		}
		System.out.println("<<< Template::show");
	}
	public void showUsage() {
		System.out.print("Usage: "+m_strObject);
		if (m_list.size() > 0) System.out.print(" -");
		for (int i=0; i<m_list.size(); i++) {
			ParameterDef def = (ParameterDef) m_list.get(i);
			System.out.print(def.getFlag());
		}
		System.out.print(" arg1 arg2 .....");
		System.out.println();
	}
}

