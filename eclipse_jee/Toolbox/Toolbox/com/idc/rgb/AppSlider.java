package com.idc.rgb;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import org.apache.log4j.*;

public class AppSlider implements ActionListener, ChangeListener {

	private static Logger debug = Logger.getRootLogger();
	private App m_app;
	private String m_strColor;
//	private int m_nNumber;
	private JSlider m_slider;
	private JTextField m_valueText;

	public AppSlider(App app, String strColor, int nNumber) {
		m_app = app;
		m_strColor = strColor;
//		m_nNumber = nNumber;
	}
	public void setSliderValue (int nValue) {
		debug.info (">>> AppSlider::setSliderValue; value "+nValue);
		m_slider.setValue(nValue);
		m_valueText.setText(Integer.toString(nValue));
		debug.info ("<<< AppSlider::setSliderValue; value "+nValue);
	}
	public int getSliderValue () {return m_slider.getValue();}

	public Container makeContentPane() {
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());

		JLabel lbl = new JLabel(m_strColor);
		pane.add(lbl,BorderLayout.NORTH);

		m_slider = new JSlider(JSlider.HORIZONTAL,0,255,0);
		m_slider.addChangeListener(this);
		m_slider.setMajorTickSpacing(50);
		m_slider.setMinorTickSpacing(10);
		m_slider.setPaintTicks(true);
		m_slider.setPaintLabels(true);
		pane.add(m_slider,BorderLayout.CENTER);

		JPanel pane1c = new JPanel();
		pane1c.setLayout(new FlowLayout());
		m_valueText = new JTextField(3);
		m_valueText.addActionListener(this);
		pane1c.add(m_valueText);
		pane.add(pane1c,BorderLayout.SOUTH);
		return (pane);
	}
	public void stateChanged(ChangeEvent e) {
		debug.info (">>> AppSlider::stateChanged");
		JSlider source = (JSlider)e.getSource();
		int fps = (int)source.getValue();
		if (! source.getValueIsAdjusting()) { //done adjusting
			m_app.setMyHex();
			m_app.setMyRGB();
		}
		else { //value is adjusting; just set the text
			m_valueText.setText(String.valueOf(fps));
		}
		debug.info ("<<< AppSlider::stateChanged");
	}
	public void actionPerformed (ActionEvent e) {
		debug.info (">>> AppSlider::actionPerformed");
		Object source = e.getSource();
		if (source instanceof JTextField) {
			int nValue = Integer.parseInt(m_valueText.getText());
			m_slider.setValue(nValue);
		}
		debug.info ("<<< AppSlider::actionPerformed");
	}
}

