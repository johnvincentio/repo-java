
package com.idc.coder;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author John Vincent
 *
 */

public class App extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1;
	private AppInput m_appInput;
	private AppCode m_appCode;
	public App(String msg) {
		super(msg);
		setContentPane(makeMyContentPane());
		setJMenuBar (makeMenu());
		setSize(900,700);		 		 // width, height
		setVisible(true);
	}
	public static void main (String args[]) {
		JFrame frame = new App ("Code Helper Program");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	private Container makeMyContentPane() {
		JPanel pane = new JPanel();
		pane.setLayout(new GridLayout(1, 2, 0, 0));

		m_appInput = new AppInput();
		m_appCode = new AppCode(this);
		JSplitPane splitPane = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT,
				m_appInput.makeContentPane(),
				m_appCode.makeContentPane());

		splitPane.setDividerLocation(300);
		splitPane.setOneTouchExpandable(true);
		pane.add(splitPane);
		return pane;
	}
	private JMenuBar makeMenu() {
		JMenu menuFile = new JMenu("File");
		menuFile.add (new JVMenuItem("Exit", this));

		JMenu menuCode = new JMenu("Code");
		menuCode.add (new JVMenuItem("Bean - ArrayList", getAppCode()));
		menuCode.add (new JVMenuItem("Bean - Set", getAppCode()));
		menuCode.add (new JVMenuItem("Collection - ArrayList", getAppCode()));
		menuCode.add (new JVMenuItem("Collection - Set", getAppCode()));

		JMenu menu = new JMenu("FormInfo");
		menu.add (new JVMenuItem("FormInfo", getAppCode()));
		menu.add (new JVMenuItem("HttpPageInfo", getAppCode()));
		menu.add (new JVMenuItem("HttpInfo", getAppCode()));
		menu.add (new JVMenuItem("Jsp", getAppCode()));
		menu.add (new JVMenuItem("Handler", getAppCode()));
		menu.add (new JVMenuItem("PageBroker", getAppCode()));
		menuCode.add (menu);

		JMenu menu2 = new JMenu("Data");
		menu2.add (new JVMenuItem("Data", getAppCode()));
		menuCode.add (menu2);

		JMenu menuClear = new JMenu("Clear");
		menuClear.add (new JVMenuItem("Input", getAppInput()));
		menuClear.add (new JVMenuItem("Output", getAppCode()));

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(menuFile);
		menuBar.add(menuCode);
		menuBar.add(menuClear);
		return menuBar;
	}
	public void actionPerformed (ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof JMenuItem) {
			JMenuItem mi = (JMenuItem) source;
			String itemName = mi.getText();
//			System.out.println("name :"+itemName+":");
			if (itemName.equals("Exit")) {
				doStopClient();
			}
		}
	}
	public void doStopClient() {System.exit(0);}
	public AppInput getAppInput() {return m_appInput;}
	public AppCode getAppCode() {return m_appCode;}

	private final class JVMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1;
		public JVMenuItem (String name, ActionListener listener) {
			super (name);
			this.addActionListener (listener);
		}
	}
}