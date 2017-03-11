package com.idc.a1;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class BBPanel extends JPanel {
	private static final long serialVersionUID = 1;

	BallInBox m_bb; // The bouncing ball panel

	/** Creates a panel with the controls and bouncing ball display. */
	BBPanel() {
		// ... Create components
		m_bb = new BallInBox();
		JButton startButton = new JButton("Start");
		JButton stopButton = new JButton("Stop");

		// ... Add Listeners
		startButton.addActionListener(new StartAction());
		stopButton.addActionListener(new StopAction());

		// ... Layout inner panel with two buttons horizontally
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(startButton);
		buttonPanel.add(stopButton);

		// ... Layout outer panel with button panel above bouncing ball
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
