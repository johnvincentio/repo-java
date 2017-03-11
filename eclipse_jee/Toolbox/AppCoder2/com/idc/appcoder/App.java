
package com.idc.appcoder;

import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 * @author John Vincent
 *
 */

public class App extends JFrame {
	private static final long serialVersionUID = 1;
	private AppInput m_appInput;
	private AppCode m_appCode;
	public App (String msg) {
		super (msg);
		setContentPane (makeMyContentPane());
		setJMenuBar ((new AppMenu(this)).getMenuBar());
		setSize (900,700);		 		 // width, height
		setVisible (true);
	}
	public static void main (String args[]) {
		JFrame frame = new App ("Code Helper Program");
		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
	}
	private Container makeMyContentPane() {
		JPanel pane = new JPanel();
		pane.setLayout (new GridLayout(1, 2, 0, 0));

		m_appInput = new AppInput();
		m_appCode = new AppCode();
		JSplitPane splitPane = new JSplitPane (
				JSplitPane.HORIZONTAL_SPLIT,
				m_appInput.makeContentPane(),
				m_appCode.makeContentPane());

		splitPane.setDividerLocation (300);
		splitPane.setOneTouchExpandable (true);
		pane.add (splitPane);
		return pane;
	}

	public void doStopClient() {System.exit(0);}
	public AppInput getAppInput() {return m_appInput;}
	public AppCode getAppCode() {return m_appCode;}
}
