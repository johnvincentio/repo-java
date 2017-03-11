package com.idc.dbtool;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

public class TabbedPaneFrame {
	public static void main(String[] args) {
		JFrame f = new JFrame("TabbedPaneFrame");
		f.addWindowListener(new WindowAdapter(  ) {
			public void windowClosing(WindowEvent we) { System.exit(0); }
		});
		f.setSize(200, 200);
		f.setLocation(200, 200);

		JTabbedPane tabby = new JTabbedPane(  );

		JPanel controls = new JPanel(  );
		controls.add(new JLabel("Service:"));
		JList list = new JList(
				new String[] { "Web server", "FTP server" });
		list.setBorder(BorderFactory.createEtchedBorder(  ));
		controls.add(list);
		controls.add(new JButton("Start"));

		String filename = "Piazza di Spagna.jpg";
		Image image = Toolkit.getDefaultToolkit(  ).getImage(filename);
		JComponent picture = new JScrollPane(new ImageComponent(image));

		tabby.addTab("Controls", controls);
		tabby.addTab("Picture", picture);

		f.getContentPane(  ).add(tabby);
		f.setVisible(true);
	}
}
