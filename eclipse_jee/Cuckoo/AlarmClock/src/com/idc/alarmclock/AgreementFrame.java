package com.idc.alarmclock;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class AgreementFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1;
	private JCheckBox agree;
	private AlarmClock frame;
	public AgreementFrame(AlarmClock frame) {
		this.frame = frame;
		frame.setEnabled(false);
		setLocation(200,200);
		setSize(385,380);
		setResizable(false);
		setTitle("First Use - Agreement");
		
		WindowDestroyer wd = new WindowDestroyer();
		this.addWindowListener(wd);
		
		String text = "";
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(AgreementFrame.class.getResourceAsStream("/agreement.txt")));
			String tmp = null;
			while(true) {
				tmp = in.readLine();
				if(tmp == null) {
					text += "\n";
					break;
				}
				text += "\n" + tmp;
			}
			in.close();
		}
		catch(FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Agreement File not found. Program will exit.\nPlease try again. If problem persists try to\ndowload again and then report a bug if the\nproblem continues.");
			frame.dispose();
			dispose();
			System.exit(0);
		}
		catch(IOException e) {
			JOptionPane.showMessageDialog(null, "Error while reading Agreement File. Program will exit.\nPlease try again. If problem persists try to\ndowload again and then report a bug if the\nproblem continues.");
			frame.dispose();
			dispose();
			System.exit(0);
		}
		
		JPanel main = new JPanel();
		JTextArea ta = null;
		if(frame.getOperatingSystem().equals("Windows")) {
			ta = new JTextArea(16,51);
		}
		else {
			ta = new JTextArea(20,51);
		}
		ta.setFont(new Font("Courier", ta.getFont().getStyle(), ta.getFont().getSize()));
		String version = "" + (frame.getVersion()/10) + "." + frame.getVersion()%10;
		ta.setText("Free Digital Alarm Clock Version: " + version);
		ta.append(text);
		ta.setEditable(false);
		ta.setLineWrap(true);
		
		JScrollPane scroll = new JScrollPane(ta);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		agree = new JCheckBox("   I Agree");
		main.add(scroll, "Center");
		main.add(agree, "South");
		
		JPanel buttons = new JPanel();
		JButton continueButton = new JButton("Continue");
		continueButton.addActionListener(this);
		buttons.add(continueButton);
		
		getContentPane().add(main, "Center");
		getContentPane().add(buttons, "South");
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Continue")) {
			if(agree.isSelected()) {
				frame.setAgree(true);
				frame.setEnabled(true);
				frame.transferFocus();
				dispose();
			}
			else {
				frame.setAgree(false);
				dispose();
				frame.dispose();
			}
		}
	}
	
	public class WindowDestroyer extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			dispose();
			frame.dispose();
			frame.exit(0);
		}
	}
}