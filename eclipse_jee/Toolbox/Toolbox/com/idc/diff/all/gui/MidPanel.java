package com.idc.diff.all.gui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.idc.swing.JVMessagesArea;

public class MidPanel extends JPanel {
	private static final long serialVersionUID = 1;

	private JVMessagesArea m_messagesArea;
	public JVMessagesArea getMessagesArea() {return m_messagesArea;}

	public MidPanel() {
		setLayout (new BorderLayout());
		setBorder (BorderFactory.createEmptyBorder(10, 20, 10, 20));
		m_messagesArea = new JVMessagesArea();
		m_messagesArea.makeContentPane (25, 100);
		add (new JScrollPane (m_messagesArea.getTextArea()), BorderLayout.CENTER);
	}
}
