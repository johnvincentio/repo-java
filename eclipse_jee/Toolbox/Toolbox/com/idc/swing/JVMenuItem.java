package com.idc.swing;

import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

public final class JVMenuItem extends JMenuItem {
	private static final long serialVersionUID = 1;
	private int number;
	private String option1;
	public JVMenuItem (String name, ActionListener listener) {
		this (name, 0, "", listener);
	}
	public JVMenuItem (String name, int number, ActionListener listener) {
		this (name, number, "", listener);
	}
	public JVMenuItem (String name, int number, String option1, ActionListener listener) {
		super (name);
		this.number = number;
		this.option1 = option1;
		this.addActionListener (listener);
	}
	public int getNumber() {return number;}
	public String getOption1() {return option1;}
}
