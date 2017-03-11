package com.idc.file.cmdargs;

public class IntDef extends ParameterDef {
	protected int m_value;
	private final int m_min;
	private final int m_max;

	public IntDef (int value, char flag, String desc, int min, int max) {
		super (flag, desc);
		m_value = value;
		m_min = min;
		m_max = max;
	}
	public int getValue() {return m_value;}
	public int getMin() {return m_min;}
	public int getMax() {return m_max;}
	public void setValue (String str) {
		int num;
		try {
			num = Integer.valueOf(str).intValue();
		}
		catch (Exception ex) {
			throw new ProcessorException (str+" is not a valid integer");
		}
		if (num < m_min || num > m_max)
			throw new ProcessorException(str+" is out of bounds");
		m_value = num;
	}
}

