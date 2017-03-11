package com.idc.a8;

import java.awt.Container;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class BullseyeWindow extends JFrame {
	private static final long serialVersionUID = 1;
	int initialRings = 10; // Number of rings to start with.
	Bullseye be; // The panel that draws the bullseye

	public BullseyeWindow() {
		JSlider slide = new JSlider(JSlider.HORIZONTAL, 0, 30, initialRings);
		slide.setMajorTickSpacing(5); // sets numbers for biggest tick marks
		slide.setPaintLabels(true); // display numbers on major ticks
		slide.setMinorTickSpacing(1); // smaller tick marks
		slide.setPaintTicks(true); // display the ticks
		slide.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int num = ((JSlider) (e.getSource())).getValue();
				be.setRings(num);
			}
		});

		be = new Bullseye();
		be.setRings(initialRings);

		Container content = this.getContentPane();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.add(slide);
		content.add(be);

		this.setTitle("BullseyeDemo");
		this.pack();
		this.setResizable(false);
	}
}

