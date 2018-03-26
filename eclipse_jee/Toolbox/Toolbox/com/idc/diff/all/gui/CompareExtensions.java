package com.idc.diff.all.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.SoftBevelBorder;

public class CompareExtensions extends JPanel {
	private static final long serialVersionUID = 8866696522846475033L;

	private Extensions m_ext = new Extensions();
	public Extensions getExtensions() {return m_ext;}

	public CompareExtensions() {
		m_ext.add (new Extension("^[^.]*$", "<none>", true));
		m_ext.add (new Extension(".*?\\.babelrc", "*.babelrc", true));
		m_ext.add (new Extension(".*?\\.bat", "*.bat", true));
		m_ext.add (new Extension(".*?\\.classpath", "*.classpath", true));
		m_ext.add (new Extension(".*?\\.compatibility", "*.compatibility", true));
		m_ext.add (new Extension(".*?\\.conxmi", "*.conxmi", true));
		m_ext.add (new Extension(".*?\\.css", "*.css", true));
		m_ext.add (new Extension(".*?\\.dbxmi", "*.dbxmi", true));
		m_ext.add (new Extension(".*?\\.dnx", "*.dnx", true));
		m_ext.add (new Extension(".*?\\.dtd", "*.dtd", true));
		m_ext.add (new Extension(".*?\\.env", "*.env", true));
		m_ext.add (new Extension(".*?\\.gph", "*.gph", true));
		m_ext.add (new Extension(".*?\\.gitignore", "*.gitignore", true));
		m_ext.add (new Extension(".*?\\.html", "*.html", true));
		m_ext.add (new Extension(".*?\\.iex", "*.iex", true));
		m_ext.add (new Extension(".*?\\.j2ee", "*.j2ee", true));
		m_ext.add (new Extension(".*?\\.jacl", "*.jacl", true));		
		m_ext.add (new Extension(".*?\\.java", "*.java", true));
		m_ext.add (new Extension(".*?\\.js", "*.js", true));
		m_ext.add (new Extension(".*?\\.jsx", "*.jsx", true));
		m_ext.add (new Extension(".*?\\.json", "*.json", true));
		m_ext.add (new Extension(".*?\\.jsp", "*.jsp", true));
		m_ext.add (new Extension(".*?\\.md", "*.md", true));
		m_ext.add (new Extension(".*?\\.modulemaps", "*.modulemaps", true));
		m_ext.add (new Extension(".*?\\.mf", "*.mf", true));
		m_ext.add (new Extension(".*?\\.MF", "*.MF", true));
		m_ext.add (new Extension(".*?\\.prefs", "*.prefs", true));
		m_ext.add (new Extension(".*?\\.prettierignore", "*.prettierignore", true));
		m_ext.add (new Extension(".*?\\.prettierrc", "*.prettierrc", true));
		m_ext.add (new Extension(".*?\\.project", "*.project", true));
		m_ext.add (new Extension(".*?\\.properties", "*.properties", true));
		m_ext.add (new Extension(".*?\\.rlconxmi", "*.rlconxmi", true));
		m_ext.add (new Extension(".*?\\.runtime", "*.runtime", true));
		m_ext.add (new Extension(".*?\\.schxmi", "*.schxmi", true));
		m_ext.add (new Extension(".*?\\.scss", "*.scss", true));
		m_ext.add (new Extension(".*?\\.serverPreference", "*.serverPreference", true));
		m_ext.add (new Extension(".*?\\.sql", "*.sql", true));
		m_ext.add (new Extension(".*?\\.tblxmi", "*.tblxmi", true));
		m_ext.add (new Extension(".*?\\.tld", "*.tld", true));
		m_ext.add (new Extension(".*?\\.todo", "*.todo", true));
		m_ext.add (new Extension(".*?\\.txt", "*.txt", true));
		m_ext.add (new Extension(".*?\\.unknown", "*.unknown", true));
		m_ext.add (new Extension(".*?\\.values", "*.values", true));
		m_ext.add (new Extension(".*?\\.websettings", "*.websettings", true));
		m_ext.add (new Extension(".*?\\.website-config", "*.website-config", true));
		m_ext.add (new Extension(".*?\\.wsdd", "*.wsdd", true));
		m_ext.add (new Extension(".*?\\.xmi", "*.xmi", true));
		m_ext.add (new Extension(".*?\\.xml", "*.xml", true));
		m_ext.add (new Extension(".*?\\.xsd", "*.xsd", true));
		m_ext.add (new Extension(".*?\\.xsl", "*.xsl", true));
		m_ext.add (new Extension(".*?\\.yml", "*.yml", true));

		JPanel paneText = new JPanel();
		paneText.setLayout (new BorderLayout());
		paneText.add (new JLabel ("Compare Extensions"), BorderLayout.WEST);

		JPanel paneOptions = new JPanel();
		paneOptions.setBorder (new SoftBevelBorder (SoftBevelBorder.LOWERED));
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
