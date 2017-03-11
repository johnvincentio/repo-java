package com.idc.appcoder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import com.idc.appcoder.coder.MakeCode;
import com.idc.appcoder.parser.CodeParser;
import com.idc.appcoder.parser.CodeTable;
import com.idc.appcoder.template.Template;
import com.idc.swing.JVMenuItem;

public class AppMenu extends JMenuBar implements ActionListener {
	private static final long serialVersionUID = 1;

	private App m_app;
	private JMenuBar m_menuBar;
	JMenuBar getMenuBar() {return m_menuBar;}

	public AppMenu (App app) {
		m_app = app;
		
		JMenu menuFile = new JMenu("File");
		menuFile.add (new JVMenuItem("Exit", 99, this));

		JMenu menuCode = new JMenu("Code");

		JMenu menuMercury = new JMenu("ArrayList (Mercury)");
		menuMercury.add (new JVMenuItem("Bean", 101, "templates/bean_simple.txt", this));
		menuMercury.add (new JVMenuItem("Collection", 102, "templates/mercury/collection.txt", this));
		menuCode.add (menuMercury);

		JMenu menuVenus = new JMenu("HashMap (Venus)");
		menuVenus.add (new JVMenuItem("Bean", 111, "templates/bean_simple.txt", this));
		menuVenus.add (new JVMenuItem("Key Bean", 112, "templates/bean_hashkey.txt", this));
		menuVenus.add (new JVMenuItem("Collection", 113, "templates/venus/collection.txt", this));
		menuCode.add (menuVenus);

		JMenu menuEarth = new JMenu("LinkHashMap (Earth)");
		menuEarth.add (new JVMenuItem("Bean", 121, "templates/bean_simple.txt", this));
		menuEarth.add (new JVMenuItem("Key Bean", 122, "templates/bean_hashkey.txt", this));
		menuEarth.add (new JVMenuItem("Collection", 123, "templates/earth/collection.txt", this));
		menuCode.add (menuEarth);

		JMenu menuMars = new JMenu("LinkHashMap + TreeMap (Mars)");
		menuMars.add (new JVMenuItem("Bean", 131, "templates/bean_simple.txt", this));
		menuMars.add (new JVMenuItem("Key Bean", 132, "templates/bean_hashkey.txt", this));
		menuMars.add (new JVMenuItem("Collection", 133, "templates/mars/collection.txt", this));
		menuCode.add (menuMars);

		JMenu menuJupiter = new JMenu("Set (Jupiter)");
		menuJupiter.add (new JVMenuItem("Bean", 141, "templates/bean_simple.txt", this));
		menuJupiter.add (new JVMenuItem("Key Bean", 142, "templates/bean_setkey.txt", this));
		menuJupiter.add (new JVMenuItem("Collection", 143, "templates/jupiter/collection.txt", this));
		menuCode.add (menuJupiter);

		menuCode.add (new JVMenuItem("FormInfo", 201, "templates/FormInfo.txt", this));
		menuCode.add (new JVMenuItem("HttpPageInfo", 202, "templates/HttpPageInfo.txt", this));
		menuCode.add (new JVMenuItem("HttpInfo", 203, "templates/HttpInfo.txt", this));
		menuCode.add (new JVMenuItem("Helper", 204, "templates/Helper.txt", this));
		menuCode.add (new JVMenuItem("DataHelper", 205, "templates/DataHelper.txt", this));

		JMenu menuClear = new JMenu("Clear");
		menuClear.add (new JVMenuItem("Input", 51, this));
		menuClear.add (new JVMenuItem("Output", 52, this));

		m_menuBar = new JMenuBar();
		m_menuBar.add(menuFile);
		m_menuBar.add(menuCode);
		m_menuBar.add(menuClear);
	}

	public void actionPerformed (ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof JVMenuItem) {
			JVMenuItem mi = (JVMenuItem) source;
			System.out.println("mi.getOption1() "+mi.getOption1());
			int number = mi.getNumber();
			switch (number) {
			case 99:
				m_app.doStopClient();
				return;
			case 51:		// clear input
				m_app.getAppInput().resetMessagesArea();
				return;
			case 52:		// clear output
				m_app.getAppCode().resetMessagesArea();
				return;
			default:
			}

			m_app.getAppCode().resetMessagesArea();
			CodeTable codeTable = (new CodeParser()).parser (m_app.getAppInput().getUserCode());
			MakeCode makeCode = new MakeCode (codeTable);
			m_app.getAppCode().addCode (Template.process (makeCode, mi.getOption1()));
		}
	}
}
