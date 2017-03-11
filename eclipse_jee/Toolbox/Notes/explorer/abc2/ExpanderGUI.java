package com.idc.explorer.abc2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ExpanderGUI extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final String WORKING_DIRECTORY = "c:/jvExplorer";
	public static final String EDITOR = "C:/Program Files/EditPlus 2/editplus.exe";
	public static final String BROWSER = "C:/Program Files/Internet Explorer/iexplore.exe";

	public ExpanderGUI() {
		super (new BorderLayout());
		System.out.println(">>> ExpanderGUI");

		String strFile = "c:/tmp/1";
		File startFile = new File (strFile);
		startFile = null;
		ExpanderTree tree = new ExpanderTree (new File (WORKING_DIRECTORY), startFile);
		JScrollPane scrollPane = new JScrollPane(tree);
		scrollPane.setPreferredSize(new Dimension(200, 200));
        add (scrollPane, BorderLayout.CENTER);
		System.out.println("<<< ExpanderGUI");
	}

	private static void createAndShowGUI() {
		System.out.println(">>> ExpanderGUI::createAndShowGUI");
		JFrame frame = new JFrame("ExpanderGUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		ExpanderGUI newContentPane = new ExpanderGUI();
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		frame.pack();
		frame.setVisible(true);
		System.out.println("<<< ExpanderGUI::createAndShowGUI");
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}
