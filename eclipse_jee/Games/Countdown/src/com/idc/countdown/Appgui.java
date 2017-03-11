package com.idc.countdown;

import java.util.Iterator;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Appgui extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1;
	private JTextField m_textTarget = new JTextField(5);
	private JTextField[] m_textNumbers = new JTextField[Constants.MAX_NUMBERS];
	private JTextArea m_messagesArea;
	private JButton m_btnCalculate;
	private JButton m_btnExit;

	public static void main (String[] args) {(new Appgui()).doApp();}
	private void doApp() {
		setContentPane(makeContentPane());
		this.addWindowListener (new WindowAdapter() {
			public void windowClosing (WindowEvent e) {
				doStop();
			}
		});
		setSize(700,900);
		pack();
		setVisible(true);			
	}

	public void doStop() {System.exit(0);}
	private Container makeContentPane() {
		JPanel boardPanel = new JPanel();  // panel for the squares
		boardPanel.add (new JLabel("Numbers:"));
		for (int i=0; i<6; i++) {
			m_textNumbers[i] = new JTextField(5);
			boardPanel.add (m_textNumbers[i]);
		}
		boardPanel.add (new JLabel("Target: "));
		boardPanel.add (m_textTarget);
		
		JPanel topPane = new JPanel(); // panel for the board
		topPane.setLayout(new BorderLayout());
		topPane.add(boardPanel, BorderLayout.CENTER);

		JPanel midPane = new JPanel();
		m_messagesArea = new JTextArea(30,40);
		m_messagesArea.setEditable(false);		
		m_messagesArea.setDragEnabled(true);	
		midPane.add(new JScrollPane(m_messagesArea));

		JPanel lowPane = new JPanel();
		m_btnCalculate = new JButton("Start");
		m_btnCalculate.addActionListener(this);
		m_btnExit = new JButton("Exit");
		m_btnExit.addActionListener(this);
		lowPane.add(m_btnCalculate);
		lowPane.add(m_btnExit);	

		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());
		pane.add(topPane,BorderLayout.NORTH);
		pane.add(midPane,BorderLayout.CENTER);
		pane.add(lowPane,BorderLayout.SOUTH);			
		return pane;
	}	
	public void actionPerformed (ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof JButton) {
			if (source == m_btnCalculate) {
				doCalculate();
			}
			else if (source == m_btnExit) {
				doStop();
			}
		}
	}
	private void doCalculate() {
		clearMessagesArea();
		Numbers numbers = new Numbers();
		try {
			for (int i=0; i<Constants.MAX_NUMBERS; i++)
				numbers.set(Utils.makePositive(Utils.makeInt(m_textNumbers[i].getText())));
			numbers.setTarget(Utils.makePositive(Utils.makeInt(m_textTarget.getText())));
//			System.out.println("(early) Numbers: "+numbers.show());
		}
		catch (Exception ex) {
			setMessagesArea ("Unable to make sense of your numbers.");
			setMessagesArea ("Please check them and try again.");
			return;
		}
		setMessagesArea("Numbers are OK");
		Answers all = Utils.doCalculate (numbers);
		setMessagesArea("Numbers: "+numbers.show());
		setMessagesArea("Solutions: "+all.getSize());
		Answer answer;
		Iterator iter = all.getAnswers();
		while (iter.hasNext()) {
			answer = (Answer) iter.next();
			setMessagesArea(Utils.show (answer));
		}
	}
	public void setMessagesArea (final String msg) {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_messagesArea.append(msg);
					m_messagesArea.append("\n");
					m_messagesArea.setCaretPosition(
						m_messagesArea.getText().length());
					validate();
				}
			}
		);
	}
	public void clearMessagesArea () {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_messagesArea.setText("");
					m_messagesArea.setCaretPosition(
						m_messagesArea.getText().length());
					validate();
				}
			}
		);
	}
}
