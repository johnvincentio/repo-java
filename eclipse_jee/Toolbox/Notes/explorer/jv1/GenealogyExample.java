package com.idc.explorer.jv1;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class GenealogyExample extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;

	private String m_strWorkDir = "c:/jvExplorer";
	private GenealogyTree tree;

	public GenealogyExample() {
		super(new BorderLayout());
		System.out.println(">>> GenealogyExample");

		Expander m_expander = new Expander (new File (m_strWorkDir));
		String strFile = "c:/tmp/1";
		NodeItemInfo nodeItemInfo = m_expander.unPack (new File(strFile));
		NodeItemInfo.show (0, nodeItemInfo);

		// Construct the tree.
		tree = new GenealogyTree (nodeItemInfo);
		JScrollPane scrollPane = new JScrollPane(tree);
		scrollPane.setPreferredSize(new Dimension(200, 200));
        add (scrollPane, BorderLayout.CENTER);
		System.out.println("<<< GenealogyExample");
	}

	/**
	 * Required by the ActionListener interface. Handle events on the
	 * showDescendant and showAncestore buttons.
	 */
	public void actionPerformed(ActionEvent ae) {
		System.out.println(">>> GenealogyExample::actionPerformed");
		tree.showAncestor(false);
		System.out.println("<<< GenealogyExample::actionPerformed");
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
