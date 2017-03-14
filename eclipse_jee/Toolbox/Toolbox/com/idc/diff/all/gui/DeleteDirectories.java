package com.idc.diff.all.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.SoftBevelBorder;

public class DeleteDirectories extends JPanel {
	private static final long serialVersionUID = 1398936969976221437L;

	private Extensions m_ext = new Extensions();
	public Extensions getExtensions() {return m_ext;}

	public DeleteDirectories() {
		m_ext.add (new Extension(".*?\\.git", "*.git", true));
		m_ext.add (new Extension(".*?\\.metadata", "*.metadata", true));
		m_ext.add (new Extension("node_modules", "node_modules", true));
		m_ext.add (new Extension(".*?\\.sass-cache", "*.sass-cache", true));
		m_ext.add (new Extension(".*?\\.svn", "*.svn", true));

		JPanel paneText = new JPanel();
		paneText.setLayout (new BorderLayout());
		paneText.add (new JLabel ("Delete Directories (will delete these directories)"), BorderLayout.WEST);

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
