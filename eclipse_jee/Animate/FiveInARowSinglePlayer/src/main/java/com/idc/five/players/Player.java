package com.idc.five.players;

import java.awt.Color;

public class Player {
	private String name;
	private Color color;
	private boolean computer = true;

	public Player(String name, Color color, boolean computer) {
		this.name = name;
		this.color = color;
		this.computer = computer;
	}

	public String getName() {
		return name;
	}
	public Color getColor() {
		return color;
	}
	public boolean isComputer() {
		return computer;
	}
	public void setComputer(boolean computer) {
		this.computer = computer;
	}
	@Override
	public String toString() {
		return "Player [name=" + name + ", color=" + color + ", computer=" + computer + "]";
	}
}
