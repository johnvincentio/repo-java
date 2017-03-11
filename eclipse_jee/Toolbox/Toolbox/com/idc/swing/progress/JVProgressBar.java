package com.idc.swing.progress;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class JVProgressBar extends JProgressBar {
	private static final long serialVersionUID = 1;
	public JVProgressBar() {}
	public void initProgressBar () {initProgressBar (0,100);}
	public void initProgressBar (final int iMin, final int iMax) {
		SwingUtilities.invokeLater (
				new Runnable() {
					public void run() {
						setMinimum(iMin);
						setMaximum(iMax);
						setValue(iMin);
						setStringPainted(false);        // true for %age
//						setIndeterminate(true);                // jdk 1.4
					}
				}
		);
	}
	public void setProgressBar (final int value) {
		SwingUtilities.invokeLater (
				new Runnable() {
					public void run() {setValue(value);}
				}
		);
	}
	public void setProgressBar () {
		int value = getValue() + 1;
		if (value > getMaximum()) value = getMinimum();
		final int newValue = value;
		SwingUtilities.invokeLater (
				new Runnable() {
					public void run() {setValue(newValue);}
				}
		);
	}
	public void setMaxProgressBar () {
		final int newValue = getMaximum();
		SwingUtilities.invokeLater (
				new Runnable() {
					public void run() {setValue(newValue);}
				}
		);
	}
}
