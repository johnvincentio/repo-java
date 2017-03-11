package com.idc.diff.all.gui;

import javax.swing.JCheckBox;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Extension {
	private String m_display;
	private JCheckBox m_checkBox;
	private Pattern m_pattern;
	public Extension (String strPattern, String display, boolean active) {
		m_display = display;
		m_checkBox = new JCheckBox (m_display);
		m_checkBox.setSelected(active);
		m_pattern = Pattern.compile (strPattern);
	}
	public JCheckBox getCheckBox() {return m_checkBox;}
	public String getExtension() {return m_display;}
	public boolean isActive() {return m_checkBox.isSelected();}
	public boolean isMatch (String filename) {
		Matcher m = m_pattern.matcher (filename);
		return m.matches();
	}
}
