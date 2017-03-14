package com.idc.diff.all.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.SoftBevelBorder;

public class DeleteExtensions extends JPanel {
	private static final long serialVersionUID = -7163136246992416392L;

	private Extensions m_ext = new Extensions();
	public Extensions getExtensions() {return m_ext;}

	public DeleteExtensions() {
		m_ext.add (new Extension(".*?\\.DS_Store", "*.DS_Store", true));
		m_ext.add (new Extension(".*?\\.bak", "*.bak", true));
		m_ext.add (new Extension(".*?\\.checkedout", "*.checkedout", true));
		m_ext.add (new Extension(".*?\\.class", "*.class", true));
		m_ext.add (new Extension(".*?\\.contrib", "*.contrib", true));
		m_ext.add (new Extension(".*?\\.dat", "*.dat", true));
		m_ext.add (new Extension(".*?\\.dnx", "*.dnx", true));
		m_ext.add (new Extension(".*?\\.ear", "*.ear", true));
		m_ext.add (new Extension(".*?\\.hijacked", "*.hijacked", true));
		m_ext.add (new Extension(".*?\\.index", "*.index", true));
		m_ext.add (new Extension(".*?\\.ini", "*.ini", true));
		m_ext.add (new Extension("ibm_ejbext.properties", "ibm_ejbext.properties", true));
		m_ext.add (new Extension(".*?\\.keep", "*.keep", true));
		m_ext.add (new Extension(".*?\\.lock", "*.lock", true));
		m_ext.add (new Extension(".*?\\.log", "*.log", true));
		m_ext.add (new Extension(".*?\\.psf", "*.psf", true));
		m_ext.add (new Extension(".*?\\.swp", "*.swp", true));
		m_ext.add (new Extension(".*?\\.webspheredeploy", "*.webspheredeploy", true));
		m_ext.add (new Extension("_.*?\\.java", "_*.java", true));
		m_ext.add (new Extension("EJS.*?\\.java", "EJS*.java",true));

		JPanel paneText = new JPanel();
		paneText.setLayout (new BorderLayout());
		paneText.add (new JLabel ("Delete Extensions (will delete files with these extensions)"), BorderLayout.WEST);

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
