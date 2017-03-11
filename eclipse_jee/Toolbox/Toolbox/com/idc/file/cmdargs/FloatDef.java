package com.idc.file.cmdargs;

public class FloatDef extends ParameterDef {
	protected float m_value;
	private final float m_min;
	private final float m_max;

	public FloatDef (float value, char flag, String desc, float min, float max) {
		super (flag, desc);
		m_value = value;
		m_min = min;
		m_max = max;
	}
	public float getValue() {return m_value;}
	public float getMin() {return m_min;}
	public float getMax() {return m_max;}
	public void setValue (String str) {
		float num;
		try {
			num = Float.valueOf(str).floatValue();
		}
		catch (Exception ex) {
			throw new ProcessorException (str+" is not a valid float");
		}
		if (num < m_min || num > m_max)
			throw new ProcessorException(str+" is out of bounds");
		m_value = num;
	}
}

