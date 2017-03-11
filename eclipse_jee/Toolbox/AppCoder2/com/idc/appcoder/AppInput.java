
package com.idc.appcoder;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * @author John Vincent
 *
 */

public class AppInput extends JPanel {
	private static final long serialVersionUID = 1;
	private JTextArea m_textArea;

	public AppInput() {}
	public Container makeContentPane() {
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());

		JPanel midPane = new JPanel();
		midPane.setLayout(new BorderLayout());
		m_textArea = new JTextArea(50,35);
		m_textArea.setLineWrap(false);
		m_textArea.setEditable(true);
		m_textArea.setTabSize(4);
		m_textArea.setBorder(BorderFactory.createCompoundBorder (
				BorderFactory.createEmptyBorder(5,5,5,5), m_textArea.getBorder()));

		JScrollPane scroll = new JScrollPane (m_textArea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		midPane.add (scroll, BorderLayout.CENTER);
		midPane.setBorder (BorderFactory.createCompoundBorder (
				BorderFactory.createEmptyBorder(5,5,0,0),midPane.getBorder()));		 //t,l,b,r

		pane.add (midPane,BorderLayout.CENTER);
		return pane;
	}
	public Dimension getPreferredSize() {return new Dimension (200, 800);}
	public Dimension getMinimumSize() {return getPreferredSize();}

	public void setMessagesArea (final String msg) {
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
	public String getUserCode() {return m_textArea.getText();}
}
