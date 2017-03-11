package com.idc.file.cmdargs;

public class StringDef extends ParameterDef {
	protected String m_value;
	public StringDef (String value, char flag, String desc) {
		super (flag, desc);
		m_value = value;
	}
	public String getValue() {return m_value;}
	public void setValue (String s) {m_value = s;}
}

