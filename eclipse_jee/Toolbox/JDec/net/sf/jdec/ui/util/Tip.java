package net.sf.jdec.ui.util;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolTip;
import javax.swing.border.EtchedBorder;

public class Tip {

	public Tip() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame f=new JFrame();
		JMenuBar b=new JMenuBar();
		JMenu m=new JMenu("X");
		m.setToolTipText("SSS");
		
		JMenuItem i=new JMenuItem("D");
		m.add(i);
		f.setJMenuBar(b);
		b.add(m);
		f.setBounds(350,350,300,300);
		f.setVisible(true);
		JToolTip tip=new JToolTip();
		tip.setComponent(i);
		tip.setTipText("Jdec TIP");
		tip.setBorder(new EtchedBorder());
		tip.setToolTipText("Press CTRL-M to view methods list in popup window");
		tip.setBounds(200,200,100,100);
		tip.setVisible(true);
		tip.setFocusable(true);

	}

}
