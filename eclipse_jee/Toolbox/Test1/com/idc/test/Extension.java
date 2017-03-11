package com.idc.test;

import javax.swing.JCheckBox;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Extension {
	private String m_strExt;
	private JCheckBox m_checkBox;
	private Pattern m_pattern;
	public Extension (String str, boolean bool) {
		m_strExt = str;
		m_checkBox = new JCheckBox (m_strExt);
		m_checkBox.setSelected(bool);
		m_pattern = Pattern.compile (m_strExt);
	}
	public JCheckBox getCheckBox() {return m_checkBox;}
	public String getExtension() {return m_strExt;}
	public boolean isActive() {return m_checkBox.isSelected();}
	public boolean isMatch (String filename) {
		Matcher m = m_pattern.matcher (filename);
		return m.matches();
	}
}
