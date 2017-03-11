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
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class HelpFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1;
	public HelpFrame(AlarmClock frame) {
		setSize(440,400);
		setResizable(false);
		setTitle("Free Digital Alarm Clock - Help");
		setLocation((int)frame.getLocation().getX()+20, (int)frame.getLocation().getY()+20);
		String text = "";
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(HelpFrame.class.getResourceAsStream("/help.txt")));
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
			text = "Help file not found.";
		}
		catch(IOException e) {
			text = "Error reading help file.";
		}
		
		JPanel helpPanel = new JPanel();
		JTextArea ta  = null;
		if(frame.getOperatingSystem().equals("Windows")) {
			ta = new JTextArea(18,59);
		}
		else {
			ta = new JTextArea(24,59);
		}
		
		ta.setFont(new Font("Courier", ta.getFont().getStyle(), ta.getFont().getSize()));
		String version = "" + (frame.getVersion()/10) + "." + frame.getVersion()%10;
		ta.setText("Free Digital Alarm Clock Version: " + version + "\nCredits:\nAuthor: Edward Wegner (Nu Systems)\nDesign:Edward Wegner");
		ta.append(text);
		ta.setEditable(false);
		ta.setCaretPosition(0);
		JScrollPane scroll = new JScrollPane(ta);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		helpPanel.add(scroll);
		
		JPanel buttonPanel = new JPanel();
		JButton close = new JButton("Close");
		close.addActionListener(this);
		buttonPanel.add(close);
		
		WindowDestroyer wd = new WindowDestroyer();
		this.addWindowListener(wd);
		
		getContentPane().add(helpPanel, "Center");
		getContentPane().add(buttonPanel, "South");
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Close")) {
			dispose();
		}
	}
	
	public class WindowDestroyer extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			dispose();
		}
	}
}