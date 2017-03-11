package com.idc.file.cmdargs;

public abstract class ParameterDef {
	protected final char m_flag;
	protected final String m_description;
	public ParameterDef (char flag, String desc) {
		m_flag = flag;
		m_description = (desc == null) ? "(see code for description)" : desc;
	}
	public String getAbbreviation() {return "-" + m_flag;}
	public char getFlag() {return m_flag;}
	public String getDescription() {return m_description;}

	public abstract void setValue (String s);
}

