package com.idc.p4;

import java.awt.Container;

import javax.swing.JApplet;

/**
 * This applet implements Runnable and uses a thread to create a digital clock.
 */
public class ClockApplet extends JApplet implements Runnable {
	private static final long serialVersionUID = 1;

	// Will use thread reference as a flag
	Thread fThread = null;

	// Need panel reference in run ().
	DateFormatPanel fClockPanel = null;

	public void init() {
		Container content_pane = getContentPane();

		// Create an instance of the time panel
		fClockPanel = new DateFormatPanel();

		// Add the time panel to the applet's panel.
		content_pane.add(fClockPanel);
	} // init

	// This method called by browser when web page and
	// applet loaded.
	public void start() {
		// If the thread reference not null then a
		// thread is already running. Otherwise, create
		// a thread and start it.
		if (fThread == null) {
			fThread = new Thread(this);
			fThread.start();
		}
	} // start

	// This method called by browser when web page and
	// applet loaded.
	public void stop() {
		// Setting thread to null will cause loop in
		// run () to finish and kill the thread.
		fThread = null;
	} // stop

	public void run() {
		// Loop through sleep periods until
		// thread stopped by setting thread
		// reference to null.
		while (fThread != null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}

			// Repaint clock
			fClockPanel.repaint();
		}
	} // run

} // class ClockApplet
