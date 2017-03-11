package com.idc.textfilter;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.idc.swing.JVMessagesArea;

/**
 * @author John Vincent
 */

public class AppOutput extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1;
	private JVMessagesArea m_messagesArea;
	private JButton m_btnClearText;

	public AppOutput() {}
	public Container makeContentPane() {
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());

		JPanel midPane = new JPanel();
		midPane.setLayout(new BorderLayout());
		m_messagesArea = new JVMessagesArea();
		m_messagesArea.makeContentPane(40, 80);		
		m_messagesArea.getTextArea().setLineWrap(false);
		m_messagesArea.getTextArea().setEditable(false);
		m_messagesArea.getTextArea().setBorder(BorderFactory.createCompoundBorder (
				BorderFactory.createEmptyBorder(5,5,5,5),m_messagesArea.getTextArea().getBorder()));

		JScrollPane scroll = new JScrollPane (m_messagesArea.getTextArea(),
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		midPane.add(scroll, BorderLayout.CENTER);
		midPane.setBorder(BorderFactory.createCompoundBorder (
				BorderFactory.createEmptyBorder(5,5,0,0),midPane.getBorder()));//t,l,b,r

		JPanel lowPane = new JPanel();
		m_btnClearText = new JButton("Clear Text");
		m_btnClearText.addActionListener(this);
		lowPane.add(m_btnClearText);

		pane.add(midPane,BorderLayout.CENTER);
		pane.add(lowPane,BorderLayout.SOUTH);
		return pane;
	}
	public JVMessagesArea getMessagesArea() {return m_messagesArea;}

	public Dimension getPreferredSize() {return new Dimension(400, 800);}
	public Dimension getMinimumSize() {return getPreferredSize();}

	public void actionPerformed (ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof JButton) {
			if (source == m_btnClearText)
				m_messagesArea.clear();
		}
	}
}
