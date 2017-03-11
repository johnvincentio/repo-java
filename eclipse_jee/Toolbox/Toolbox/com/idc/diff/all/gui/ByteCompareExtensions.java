package com.idc.diff.all.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.SoftBevelBorder;

public class ByteCompareExtensions extends JPanel {
	private static final long serialVersionUID = 1;

	private Extensions m_ext = new Extensions();
	public Extensions getExtensions() {return m_ext;}

	public ByteCompareExtensions() {
		m_ext.add (new Extension(".*?\\.class", "*.class", true));
		m_ext.add (new Extension(".*?\\.ear", "*.ear", true));
		m_ext.add (new Extension(".*?\\.jar", "*.jar", true));
		m_ext.add (new Extension(".*?\\.war", "*.war", true));
		m_ext.add (new Extension(".*?\\.zip", "*.zip", true));

		JPanel paneText = new JPanel();
		paneText.setLayout (new BorderLayout());
		paneText.add (new JLabel ("Byte Compare Extensions"), BorderLayout.WEST);

		JPanel paneOptions = new JPanel();
		paneOptions.setBorder (new SoftBevelBorder(SoftBevelBorder.LOWERED));
		int col = 6;
		int num1 = m_ext.getSize();
		int num2 = num1 / col;
		if (num2 * col < num1) num2++;
		System.out.println("num1 "+num1+" num2 "+num2);
		paneOptions.setLayout (new GridLayout (num2, col, 0, 0));
		Iterator<Extension> iter = m_ext.getItems();
		while (iter.hasNext()) 
			paneOptions.add (((Extension) iter.next()).getCheckBox());

		setLayout (new BorderLayout());
		add (paneText, BorderLayout.NORTH);
		add (paneOptions, BorderLayout.CENTER);
	}
}
