package com.idc.swing;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.idc.file.exec.OutputLine;

public class JVMessagesArea extends JPanel implements OutputLine {
	private static final long serialVersionUID = 1;

	private JTextArea m_messagesArea;
	public JTextArea getTextArea() {return m_messagesArea;}
	public void println(String msg) {add (msg);}
	public void close() {}

	public Container makeContentPane (int x, int y) {
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());
		m_messagesArea = new JTextArea(x,y);
		m_messagesArea.setEditable(false);
		m_messagesArea.setDragEnabled(false);
		m_messagesArea.setLineWrap(false);
		m_messagesArea.setTabSize(4);
		pane.add(m_messagesArea,BorderLayout.CENTER);
		return pane;
	}

	public void add() {add ("");}
	public void add (final String msg) {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_messagesArea.append(msg);
					m_messagesArea.append("\n");
					m_messagesArea.setCaretPosition(m_messagesArea.getText().length());
					validate();
				}
			}
		);
	}
	public void addNoNL (final String msg) {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_messagesArea.append(msg);
					m_messagesArea.setCaretPosition(m_messagesArea.getText().length());
					validate();
				}
			}
		);
	}
	public void clear () {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_messagesArea.setText("");
					m_messagesArea.setCaretPosition(m_messagesArea.getText().length());
					validate();
				}
			}
		);
	}
	public void reposition () {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_messagesArea.setCaretPosition(0);
					validate();
				}
			}
		);
	}
}
