
package com.idc.swing;

import javax.swing.*;

public class JVLabel {
	private JLabel m_label;
	public JVLabel() {m_label = new JLabel();}
	public JVLabel(String text) {m_label = new JLabel(text);}
	public JLabel getLabel() {return m_label;}
	public void setText (final String msg) {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_label.setText(msg);
//					validate();
				}
			}
		);
	}
}
