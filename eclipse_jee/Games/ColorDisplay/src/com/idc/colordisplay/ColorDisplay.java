package com.idc.colordisplay;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class ColorDisplay extends JFrame {
	private static final long serialVersionUID = 1;

	JSlider redBar; // Slider for adjusting red from 0-255

	JSlider grnBar;

	JSlider bluBar;

	int red = 128;

	int green = 128;

	int blue = 128;

	GraphicsPanel palette;

	JTextField txtField;

	public ColorDisplay() {
		palette = new GraphicsPanel();

		// Create a textfield for displaying the values
		txtField = new JTextField();
		txtField.setFont(new Font("Monospaced", Font.PLAIN, 12));
		txtField.setText("Color(" + red + ", " + green + ", " + blue + ") or "
				+ "Color(0x"
				+ Integer.toHexString(blue + 256 * (green + 256 * red)) + ")");
		txtField.setEditable(false);

		// Create one object that will listen to all sliders
		ChangeListener al = new ColorListener();

		redBar = new JSlider(JSlider.VERTICAL, 0, 255, red);
		redBar.addChangeListener(al);
		redBar.setBackground(Color.red);

		grnBar = new JSlider(JSlider.VERTICAL, 0, 255, green);
		grnBar.addChangeListener(al);
		grnBar.setBackground(Color.green);

		bluBar = new JSlider(JSlider.VERTICAL, 0, 255, blue);
		bluBar.addChangeListener(al);
		bluBar.setBackground(Color.blue);

		// === Put the color palette and textfield in a box
		Box paletteText = Box.createVerticalBox();
		paletteText.add(palette);
		paletteText.add(txtField);

		// === put the 3 sliders in a gridlayout panel
		JPanel colorControls = new JPanel();
		colorControls.setLayout(new GridLayout(1, 3));
		colorControls.add(redBar);
		colorControls.add(grnBar);
		colorControls.add(bluBar);

		// === Now let's build the content pane
		Container content = this.getContentPane();
		content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));
		content.add(paletteText);
		content.add(colorControls);

		this.setTitle("ColorDisplay");
		this.pack();
	}

	class GraphicsPanel extends JPanel {
		private static final long serialVersionUID = 1;

		public GraphicsPanel() {
			this.setPreferredSize(new Dimension(300, 300));
			this.setBackground(Color.white);
			this.setForeground(Color.black);
		}
		public void paintComponent(Graphics g) {
			super.paintComponent(g); // paint background, border
			g.setColor(new Color(red, green, blue)); // Set the color
			g.fillRect(0, 0, this.getWidth(), this.getHeight()); 
		}
	}

	class ColorListener implements ChangeListener {
		public void stateChanged(ChangeEvent ae) {
			red = redBar.getValue();
			green = grnBar.getValue();
			blue = bluBar.getValue();
			txtField.setText("Color(" + red + ", " + green + ", " + blue
					+ ") or " + "Color(0x"
					+ Integer.toHexString(blue + 256 * (green + 256 * red))
					+ ")");
			palette.repaint();
		}
	}

	public static void main(String[] args) {
		JFrame window = new ColorDisplay();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}
}
