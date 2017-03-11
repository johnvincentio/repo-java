package com.idc.nothing;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MyMouseListener implements MouseListener {
	public void mousePressed(MouseEvent e) {
		saySomething("Mouse pressed; # of clicks: " + e.getClickCount(), e);
	}

	public void mouseReleased(MouseEvent e) {
		saySomething("Mouse released; # of clicks: " + e.getClickCount(), e);
	}

	public void mouseEntered(MouseEvent e) {
		saySomething("Mouse entered", e);
	}

	public void mouseExited(MouseEvent e) {
		saySomething("Mouse exited", e);
	}

	public void mouseClicked(MouseEvent e) {
		saySomething("Mouse clicked (# of clicks: " + e.getClickCount() + ")", e);
	}

	void saySomething(String eventDescription, MouseEvent e) {
		System.out.println(">>> saySomething; "+eventDescription + " detected on " + e.getComponent().getClass().getName() + ".");
	}
}
