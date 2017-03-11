package com.idc.file.cmdargs;

public class BoolDef extends ParameterDef {
	protected boolean m_value;
	public BoolDef (boolean value, char flag, String desc) {
		super (flag, desc);
		m_value = value;
	}
	public boolean getValue() {return m_value;}
	public void setValue (String s) {m_value = !m_value;}
}

