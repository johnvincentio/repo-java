package com.idc.alarmclock;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class ControlPanel extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1;
	private AlarmClock frame;
	private final String[] timeZones = {"-11 GMT","-10 GMT","-9 GMT","-8 GMT","-7 GMT","-6 GMT","-5 GMT","-4 GMT","-3 GMT","-2 GTM","-1 GMT","0 GMT","1 GMT","2 GMT","3 GMT","4 GMT","5 GMT","6 GMT","7 GMT","8 GMT","9 GMT","10 GMT","11 GMT","12 GMT"};
	private final String[] soundList = {"tone1", "tone2", "tone3"};
	private JComboBox homeTimeZone, currentTimeZone, soundSelection;
	private JCheckBox checkbox;
	
	public ControlPanel(AlarmClock frame) {
		this.frame = frame;
		frame.setEnabled(false);
		setTitle("Free Digital Alarm Clock - Configuration");
		setSize(450,200);
		setResizable(false);
		setLocation((int)frame.getLocation().getX()+10,(int)frame.getLocation().getY()+10);
		
		JPanel main = new JPanel();
		JPanel buttons = new JPanel();
		
		//set up the form.
		main.setLayout(new FlowLayout(3));
		
		//set up time zones
		JPanel time = new JPanel();
		JLabel timeZone = new JLabel("Time Zone (Hrs GMT) :");
		homeTimeZone = new JComboBox(timeZones);
		homeTimeZone.insertItemAt("Home Time Zone", 0);
		homeTimeZone.setSelectedItem("" + frame.getHomeTimeZone() + " GMT");
		currentTimeZone = new JComboBox(timeZones);
		currentTimeZone.insertItemAt("Current Time Zone", 0);
		currentTimeZone.setSelectedItem("" + (frame.getHomeTimeZone() - frame.getOffsetHours()) + " GMT");
		time.add(timeZone);
		time.add(homeTimeZone);
		time.add(currentTimeZone);
		//end time zone setup
		
		//set up Don't let me sleep function
		JPanel dontSleep = new JPanel();
		String space = null;
		if(frame.getOperatingSystem().equals("Windows")) {
			space = "                                                                                     ";
		}
		else {
			space = "                                                                       ";
		}
		dontSleep.add(new JLabel("Don't Let Me Sleep:" + space));
		checkbox = new JCheckBox();
		if(frame.getDontLetMeSleep()) {
			checkbox.setSelected(true);
		}
		dontSleep.add(checkbox);
		//end Don't sleep function
		
		//set up sound to play controls when alarm goes off
		JPanel sound = new JPanel();
		sound.add(new JLabel("Play this sound when alarm goes off:  "));
		soundSelection = new JComboBox(soundList);
		soundSelection.setSelectedItem(frame.getAudioFilename());
		sound.add(soundSelection);
		JButton playsound = new JButton("Play Sound");
		playsound.addActionListener(this);
		sound.add(playsound);
		
		//set up the buttons JPanel
		//there are two (2) buttons. A save button and an ignore button.
		//the save button will save the changes made to the configuration
		//even if no changes have been made. The save will also close the frame.
		//ignore will ignore any and all changes.
		//the actual functions of the Buttons will take place in the actionPerformed
		//method.
		
		main.add(time);
		main.add(dontSleep);
		main.add(sound);
		
		JButton save = new JButton("Save");
		save.addActionListener(this);
		
		JButton ignore = new JButton("Ignore");
		ignore.addActionListener(this);
		
		buttons.add(save);
		buttons.add(ignore);
		
		WindowDestroyer wd = new WindowDestroyer(frame);
		this.addWindowListener(wd);
		
		getContentPane().add(main, "Center");
		getContentPane().add(buttons, "South");
		setVisible(true);
		repaint();
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("Save")) {
			try {
				saveConfiguration();
				frame.transferFocus();
				frame.setEnabled(true);
				dispose();
			}
			catch(IllegalArgumentException err) {
				JOptionPane.showMessageDialog(null, err.getMessage());
			}
		}
		else if(command.equals("Ignore")) {
			frame.transferFocus();
			frame.setEnabled(true);
			dispose();
		}
		else if(command.equals("Play Sound")) {
			playSound();
		}
	}
	
	public class WindowDestroyer extends WindowAdapter {
		AlarmClock frame;
		public WindowDestroyer(AlarmClock frame) {
			this.frame = frame;
		}
		
		public void windowClosing(WindowEvent e) {
			frame.transferFocus();
			frame.setEnabled(true);
			dispose();
		}
	}
	
	
	//saves the current configuration of the control panel upon "Saving"
	private void saveConfiguration() throws IllegalArgumentException {
		int offsetHours = 0;
//		boolean dontLetMeSleep = false;
		
		//get the selected item of the first JComboBox and parse the String
		//this process throws away the " GMT" part of the string which is
		//useless when converting to an Integer. A " " (SPACE) is used as the
		//delimiter for the arguement in StringTokenizer(String, String);
		
		String timezone = (String)homeTimeZone.getSelectedItem();
		StringTokenizer st = new StringTokenizer(timezone, " ");
		timezone = st.nextToken();
		
		//If the first token of the string happens to be "Home" which is the first
		//item in the JComboBox, throws an IllegalArgumentException. If the string
		//is valid, parse an Integer and assign it to the variable offsetHours.
		//After that assign offsetHours to the calling frames homeTimeZone using
		//frame.setHomeTimeZone(int);
		if(timezone.equals("Home")) {
			throw new IllegalArgumentException("Home Time Zone is Invalid!!!");
		}
		else {
			offsetHours = Integer.parseInt(timezone);
			frame.setHomeTimeZone(offsetHours);
		}
		
		//Take the selected item of the second JComboBox and take a the first token -
		//same as above.
		timezone = (String)currentTimeZone.getSelectedItem();
		st = new StringTokenizer(timezone, " ");
		timezone = st.nextToken();
		
		//check for invalid argument - same as above except check for "Current" in place
		//of "Home"
		//if the string is valid, perform the calculation offsetHours - parse an int
		//and assign it to offsetHours. This will get the difference between the time zones.
		//Useful when travelling and changing the system clock would be an inconvenience.
		if(timezone.equals("Current")) {
			throw new IllegalArgumentException("Current Time Zone is Invalid!!!");
		}
		else {
			offsetHours = offsetHours - Integer.parseInt(timezone);
		}
		
		//assign the current state of the checkbox to the frames dontLetMeSleep variable
		//also assign all other variables to the calling frames global variables via accessors
		//and finally call the calling frames saveConfiguration() method
		frame.setDontLetMeSleep(checkbox.isSelected());
		frame.setOffsetHours(offsetHours);
		frame.setAudioFilename((String)soundSelection.getSelectedItem());
		frame.saveConfiguration();
	}
	
	//plays the sound sample currently selected in the JComboBox soundSelection
	private void playSound() {
		String audioFilename = (String) soundSelection.getSelectedItem();
		AudioPlayer p = AudioPlayer.player;
		try{
			AudioStream as = new AudioStream(getClass().getResourceAsStream("/"+audioFilename+".au"));
			p.start(as);
		}
		catch(java.io.IOException e){
			e.printStackTrace();
		}
	}
}