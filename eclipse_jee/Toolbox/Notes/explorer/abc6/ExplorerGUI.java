package com.idc.explorer.abc6;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/*
 * TODO;
 * DnD from JTree to Explorer
 * after expand, decompile; node item worked upon should be selected, and expanded.
 * popup menu should be context sensitive
 */
public class ExplorerGUI extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final String WORKING_DIRECTORY = "c:/jvExplorer";
	public static final String EDITOR = "C:/Program Files/EditPlus 2/editplus.exe";
	public static final String BROWSER = "C:/Program Files/Internet Explorer/iexplore.exe";

	public ExplorerGUI (String[] args) {
		super (new BorderLayout());
		System.out.println(">>> ExplorerGUI");
		File startFile = null;
		if (args.length > 0) startFile = new File (args[0]);
		add (makeContentPane (startFile), BorderLayout.CENTER);
		System.out.println("<<< ExplorerGUI");
	}

	private Container makeContentPane (File file) {
		System.out.println("--- ExplorerGUI::makeContentPane");
		ExplorerTree tree = new ExplorerTree (this, new File (WORKING_DIRECTORY), file);
		JScrollPane scrollPane = new JScrollPane (tree);
		scrollPane.setPreferredSize (new Dimension (200, 200));
		return scrollPane;
	}

	public void remakeContentPane (File file) {
		System.out.println(">>> ExplorerGUI::remakeContentPane");
		removeAll();
		add (makeContentPane (file));
		validate();
		repaint();
		System.out.println("<<< ExplorerGUI::remakeContentPane");
	}

	private static void createAndShowGUI (String[] args) {
		System.out.println(">>> ExplorerGUI::createAndShowGUI");
		JFrame frame = new JFrame ("ExplorerGUI");
		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

		ExplorerGUI newContentPane = new ExplorerGUI (args);
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);
		frame.pack();
		frame.setVisible(true);
		System.out.println("<<< ExplorerGUI::createAndShowGUI");
	}

	public static void main (final String[] args) {
		javax.swing.SwingUtilities.invokeLater (new Runnable() {
			public void run() {
				createAndShowGUI (args);
			}
		});
	}
}
