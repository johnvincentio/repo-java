package com.idc.diff.file;

import java.awt.Container;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class AppGUI extends JFrame {
	private static final long serialVersionUID = -6103801162166050475L;

	private File m_workDirectory;

	public AppGUI (String msg) {
		super(msg);
		setContentPane (makeMyContentPane());
		setSize(1250,900);		// width, height
		setVisible(true);
		m_workDirectory = new File (getWorkDirectory() + File.separatorChar + Long.toString (System.currentTimeMillis()));
		m_workDirectory.mkdirs();
	}
	public static void main (String args[]) {
		JFrame frame = new AppGUI ("Compare Application");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	private Container makeMyContentPane() {
		JPanel pane = new JPanel();
		pane.setLayout (new GridLayout(1, 2, 0, 0));

		Appfile m_onefile = new Appfile (this);
		Appfile m_twofile = new Appfile (this);
		Appdiff onediff = new Appdiff (m_onefile, m_twofile);

		JSplitPane splitPane1 = new JSplitPane (
									JSplitPane.HORIZONTAL_SPLIT,
									m_onefile.makeContentPane(), 
									m_twofile.makeContentPane());
		splitPane1.setDividerSize(10);	
		splitPane1.setDividerLocation(435);	
		splitPane1.setOneTouchExpandable(true);

		JSplitPane splitPane = new JSplitPane (
									JSplitPane.HORIZONTAL_SPLIT,
									splitPane1, 
									onediff.makeContentPane());
		splitPane.setDividerLocation(900);	
		splitPane.setOneTouchExpandable(true);

		pane.add (splitPane);
		return pane;
	}
	public String getWorkDirectory() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.indexOf("win") >= 0) return "C:/TEMP";
		return "/tmp";
		
	}
	public File getTempFileName() {return new File (m_workDirectory.getPath() + File.separatorChar + Long.toString (System.currentTimeMillis()) + ".txt");}
}
