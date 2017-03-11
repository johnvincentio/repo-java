package com.idc.explorer.jv4;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class GenealogyExample extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final String WORKING_DIRECTORY = "c:/jvExplorer";
	public static final String EDITOR = "C:/Program Files/EditPlus 2/editplus.exe";
	public static final String BROWSER = "C:/Program Files/Internet Explorer/iexplore.exe";

	public GenealogyExample() {
		super(new BorderLayout());
		System.out.println(">>> GenealogyExample");

		String strFile = "c:/tmp/1";
		GenealogyTree tree = new GenealogyTree (new File (WORKING_DIRECTORY), new File (strFile));
		JScrollPane scrollPane = new JScrollPane(tree);
		scrollPane.setPreferredSize(new Dimension(200, 200));
        add (scrollPane, BorderLayout.CENTER);
		System.out.println("<<< GenealogyExample");
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		System.out.println(">>> GenealogyExample::createAndShowGUI");
		// Create and set up the window.
		JFrame frame = new JFrame("GenealogyExample");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		GenealogyExample newContentPane = new GenealogyExample();
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
		System.out.println("<<< GenealogyExample::createAndShowGUI");
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}
