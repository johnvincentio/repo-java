
package com.idc.coder;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

/**
 * @author John Vincent
 *
 */

public class AppCode extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1;
	private App m_app;
	private JTextArea m_textArea;

	public AppCode(App app) {m_app = app;}
	public Container makeContentPane() {
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());

		JPanel midPane = new JPanel();
		midPane.setLayout(new BorderLayout());
		m_textArea = new JTextArea(50,35);
		m_textArea.setLineWrap(false);
		m_textArea.setEditable(false);
		m_textArea.setTabSize(4);
		m_textArea.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(5,5,5,5),m_textArea.getBorder()));

		JScrollPane scroll = new JScrollPane(m_textArea,
		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		midPane.add(scroll, BorderLayout.CENTER);
		midPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(5,5,0,0),midPane.getBorder()));//t,l,b,r

		pane.add(midPane,BorderLayout.CENTER);
		return pane;
	}
	public Dimension getPreferredSize() {return new Dimension(400, 800);}
	public Dimension getMinimumSize() {return getPreferredSize();}

	private void setMessagesArea (final String msg) {
		SwingUtilities.invokeLater (
		new Runnable() {
			public void run() {
				m_textArea.append(msg);
				m_textArea.append("\n");
			}
		}
		);
	}
	public void resetMessagesArea () {
		SwingUtilities.invokeLater (
		new Runnable() {
			public void run() {
				m_textArea.setText("");
			}
		}
		);
	}
	public void addCode (final String s) {
		setMessagesArea (s);
	}
	public void actionPerformed (ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof JMenuItem) {
			JMenuItem mi = (JMenuItem) source;
			String itemName = mi.getText();
			System.out.println("name :"+itemName+":");

			resetMessagesArea();
			CodeTable codeTable = (new CodeParser()).parser
			(m_app.getAppInput().getUserCode());

			if (itemName.equals("Bean - ArrayList")) {
				addCode (((new MakeBean(codeTable)).makeCode()));
			}
			else if (itemName.equals("Bean - Set")) {
				addCode (((new MakeBeanSet(codeTable)).makeCode()));
			}
			else if (itemName.equals("Collection - ArrayList")) {
				addCode (((new MakeCollection(codeTable)).makeCode()));
			}
			else if (itemName.equals("Collection - Set")) {
				addCode (((new MakeCollectionSet(codeTable)).makeCode()));
			}
			else if (itemName.equals("FormInfo")) {
				addCode (((new MakeFormInfo(codeTable)).makeCode()));
			}
			else if (itemName.equals("HttpPageInfo")) {
				addCode (((new MakeHttpPageInfo(codeTable)).makeCode()));
			}
			else if (itemName.equals("HttpInfo")) {
				addCode (((new MakeHttpInfo(codeTable)).makeCode()));
			}
			else if (itemName.equals("Jsp")) {
				addCode (((new MakeJsp(codeTable)).makeCode()));
			}
			else if (itemName.equals("Handler")) {
				addCode (((new MakeHandler(codeTable)).makeCode()));
			}
			else if (itemName.equals("PageBroker")) {
				addCode (((new MakePageHelper(codeTable)).makeCode()));
			}
			else if (itemName.equals("Data")) {
				addCode (((new MakeDataV2(codeTable)).makeCode()));
			}
			else if (itemName.equals("Output")) {
				resetMessagesArea();
			}
		}
	}
}
