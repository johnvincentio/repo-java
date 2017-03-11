package com.idc.explorer.test1;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;

public class DynamicTree extends JFrame {
	private static final long serialVersionUID = 1L;
	public static void main(String[] args) {
		int n = 5; // Number of children to give each node
		String jv = "3";
			try {
				n = Integer.parseInt(jv);
			} catch (NumberFormatException nfe) {
				System.out.println("Can't parse number; using default of " + n);
			}
		new DynamicTree(n);
	}

	public DynamicTree(int n) {
		super("Creating a Dynamic JTree");
		WindowUtilities.setNativeLookAndFeel();
		addWindowListener(new ExitListener());
		Container content = getContentPane();
		JTree tree = new JTree(new OutlineNode(1, n));
		content.add(new JScrollPane(tree), BorderLayout.CENTER);
		setSize(300, 475);
		setVisible(true);
	}
}
