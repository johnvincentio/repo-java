package com.idc.bouncingball;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class BBPanel extends JPanel {
	private static final long serialVersionUID = 1;
	BallInBox m_bb;

	BBPanel() {
		m_bb = new BallInBox();
		JButton startButton = new JButton("Start");
		JButton stopButton = new JButton("Stop");

		startButton.addActionListener(new StartAction());
		stopButton.addActionListener(new StopAction());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(startButton);
		buttonPanel.add(stopButton);

		this.setLayout(new BorderLayout());
		this.add(buttonPanel, BorderLayout.NORTH);
		this.add(m_bb, BorderLayout.CENTER);
	}

	class StartAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			m_bb.setAnimation(true);
		}
	}

	class StopAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			m_bb.setAnimation(false);
		}
	}
}
