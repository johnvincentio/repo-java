/*
 * Tips.java Copyright (c) 2006,07 Swaroop Belur
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package net.sf.jdec.ui.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import net.sf.jdec.ui.main.UILauncher;


/**
 * @author sbelur
 *
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Tips extends JFrame implements ActionListener,KeyListener,MouseListener


{

	public Tips()
	{
		super("Jdec Tips");
		createTipFramework();
		setSize(385,300);
		Dimension d=getToolkit().getScreenSize();
		setBounds((int)d.getWidth()/2-140,(int)d.getHeight()/2-140,480,308);
        setVisible(true);
        Thread t=new Thread(new blink());
        t.setDaemon(true);
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();

	}
	String space=" \n \n \n \n \n \n \n  \n \n \n \n \n";
	JTextArea jta;
    JLabel wn=null;


	private void createTipFramework()
	{
		JPanel panel=new JPanel();
		java.lang.String fsep=File.separator;
		java.lang.String prefix="imgs"+fsep;
		File img=new File(prefix+"tip.gif");
		JLabel lbl=new JLabel(new ImageIcon(img.getAbsolutePath()));
		jta=new JTextArea();

		jta.setText("\n\n   Welcome to Jdec Tips...\n\n\n   Jdec Tips is meant to provide you \n   with useful tips to make you, the End user, \n   comfortable with The Jdec UI \n    and Decompiler ofcourse ... \n\n\nPLEASE READ ON.."+space);
		jta.setFont(new Font(null,Font.BOLD,12));
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		panel.add(lbl);//,BorderLayout.WEST);
		panel.add(jta);//,BorderLayout.CENTER);
		jta.setEditable(false);
		JPanel buttons=new JPanel();
		JPanel twominsP=new JPanel();
		JCheckBox cb=new JCheckBox();
		cb.setText("Show At Startup");
		JLabel two=new JLabel();
		two.setText("Learn how to use jdec in 2 mins");
		two.setForeground(Color.BLUE);
		String s=UILauncher.getUIConfigRef().getPref("ShowTip");
		cb.setSelected(new Boolean(s).booleanValue());
		cb.setActionCommand("cb");
		cb.addActionListener(this);
		buttons.add(cb,BorderLayout.WEST);
		twominsP.add(two,BorderLayout.WEST);
		JButton twobutton=new JButton("Go");
		twobutton.addActionListener(this);
		twobutton.setActionCommand("twomins");
		twominsP.add(twobutton,BorderLayout.WEST);
        wn=new JLabel("         Whats New Click Me");
        wn.setForeground(Color.RED);
		wn.addMouseListener(this);

		twominsP.add(wn,BorderLayout.EAST);

		JButton nexttip=new JButton("Next Tip");
		JButton close=new JButton("Close");
		buttons.add(nexttip,BorderLayout.CENTER);
		buttons.add(close,BorderLayout.EAST);
		getContentPane().add(panel,BorderLayout.CENTER);
		JPanel footer=new JPanel();
		footer.setLayout(new BoxLayout(footer,BoxLayout.Y_AXIS));
		footer.add(buttons);
		footer.add(twominsP);
		getContentPane().add(footer,BorderLayout.SOUTH);
		//getContentPane().add(twominsP,BorderLayout.SOUTH);
		nexttip.setActionCommand("nt");
		nexttip.addActionListener(this);
		close.setActionCommand("close");
		close.addActionListener(this);
		createAllTips();

	}

	private ArrayList alltips=new ArrayList();


	void createAllTips()
	{
		String s="\n\n\n   First Time Users Are advised to load \n    the Jdec Decompiler options by using \n";
		s+="   Option Under Configuration Menu\n";
		s+="   Please update the jdec config \n   before using the UI";
		alltips.add(s);

		s="\nYou Can update the Decompiler config details\n";
		s+=" You can do this by ...\n 1> Open Configuration Details In Tab\n2> Change the 'Value' Column's Data\n3> Hit Enter So it gets updated in table\n4> Click Update\n\nNOTE: Changes get updated only in memory \nThe changes will be made permanent at the end\nIf YOU Desire it to be...";
		alltips.add(s);

		s="";

		s="\n\n\n   The Easiest Way to use the UI is to use the \n   Run Image Button";
		s+=" Which is \n   visible below the Menubar.\n";
		s+="   Make sure your jdec options \n   are correct though..\n";
		alltips.add(s);
		s="";
		s="\n\n\n   Jdec Enables the user to decompile Not Only\n    A Java Class File BUT also a JAR FILE \n   ";
		s+="Please select the menuitem \n   'New Decompiler Task' For This\n   ";
		alltips.add(s);

		s="\n\n\n   You Can Also Decompile entire jar \n   by clicking on Jar File\n   ";
		s+="From The Directory Tree \n   Displayed for Convenience";
		alltips.add(s);

        s="\n\n\n Users can choose to run jdec faster \nSee Decompiler Filter ";
		s+="Options For This...\n";
        s+="[Configuration->Jdec[Decompiler] Filters]";
		alltips.add(s);

        s="\n\n\n Users can also disable Syntax Highlighting \nSee Preferences Menu ";
		s+="For This.\n This will  cause the output\n to be rendered faster.";
		alltips.add(s);

        s="\n\n\n Users can view the decompiled output\nin custom editor also\n ";
		s+="Please see preferences option.";
		alltips.add(s);

        s="\n\n\n Users can change the color/font of \nrendered text any time\n";
		s+="Please see jdec syntax highlighting\n module for this.\n";
        s+="[Configuration->Syntax and Coloring]\n";
		alltips.add(s);

        s="\n\n\n Jdec can also be used as More than a \\simple editor also\n";
        s+="1>It can be used for compiling a java file\n2>running a class file\n";
        s+="3>It also finds a jar for a class file...";
        alltips.add(s);

		s="\n\n\n   You Can Even Create a new Java File\n   Or Open an existing java file\n   ";
		s+="Infact Jdec Provides a decent Editor\n   for this Purpose.\n   Please continue to get \n   to know about these features.";
		alltips.add(s);

		s="\n\n\n   Developers can Find this tool \n   very useful in many ways\n   ";
		s+="You Can  view detailed information \n   related to class file like \n   Constant Pool Information,\n   Local variables for each method\n   Please use the \"View\" menu option for this.";
		alltips.add(s);
		s="\n\n\n   You Can change the look and Feel \n ";
		s+="Please choose the preferences option for this...\n   ";
		alltips.add(s);

		s="\n\n\n   Jdec UI Provides good BUT simple \n   editor features as well \n   ";
		s+="You Can either type directly in the \n   Jdec Editor Window \n   Or use the";
		s+="Simple editor provided \n   for user.Please use the \n   menu option under 'Utilities' menu";
		alltips.add(s);


		s="\n\n\n   The user can also Compile \n   and Run a Java class file \n   ";
		s+="Please use the menu option under 'Utilities' menu";
		alltips.add(s);


		s="\n\n\n   For The previous tip feature to work ,\n   The user will have to configure\n   ";
		s+="Some options under 'preferences' \n  (under 'edit') though";
		alltips.add(s);


		s="\n\n\n   Another Very Cool Feature for Developers \n   is to view the System Properites \n   Jdec Provides this feature also. \n   ";
		s+="Please use the menu option under 'Utilities' menu";
		alltips.add(s);

		s="\n\n\n The Refresh Action Item Is meant only for \ndecompiling task.That is it ";
		s+="decompiles the \npreviously decompiled File again.";
		alltips.add(s);

		s="\n\n\n The Save Action Item Is meant only for \nSaving Java Files ";
		s+="It is not a genieric\n save File Menu item.\n\nHowever the user Can Use the Simple \nText Editor For Saving other file \ntypes...";
		alltips.add(s);




		s="\n\n\nFinally Dont Forget to Visit Jdec Home \nfor any issues,bugs";
		s+="\nJust Click on The Home Button\n\n\n[Please configure your browser first from Preferences....]\n";
		alltips.add(s);


		s="\n\n\n   Thank You for downloading and using Jdec\n\nAny Suggestions/Tips Are always welcome ";
		alltips.add(s);

	}
	int tipnumber=0;

	public void actionPerformed(ActionEvent ae)
	{

        if(ae.getActionCommand().equals("new"))
        {
            WhatsNew w=new WhatsNew();
            w.createMe();
        }

		if(ae.getActionCommand().equals("twomins"))
		{
			QuickTutorial quick=new QuickTutorial("Learn to use jdec in 2 mins");
		}
		if(ae.getActionCommand().equals("cb"))
		{
			JCheckBox cb=(JCheckBox)ae.getSource();

			if(cb.isSelected())
			{
				cb.setSelected(true);
				UILauncher.getUIConfigRef().addPref("ShowTip","true");
			}
			else
			{
				cb.setSelected(false);
				UILauncher.getUIConfigRef().addPref("ShowTip","false");
			}
		}

		else if(ae.getActionCommand().equals("nt"))
		{
			if(alltips!=null && alltips.size() >0 )
			{
				if(tipnumber >= alltips.size())tipnumber=0;
				String s=(String)alltips.get(tipnumber);
				tipnumber++;
				s=s+"\n"+space;
				jta.setEditable(true);
				jta.setText(s);
				jta.setEditable(false);
			}


		}
		else if(ae.getActionCommand().equals("close"))
		{

			this.setVisible(false);
			this.dispose();

		}
	}

    public void keyPressed(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {

    }

    public void keyTyped(KeyEvent e) {

    }

    public void mouseClicked(MouseEvent e) {
           WhatsNew w=new WhatsNew();
           w.createMe();

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {

    }


    class blink implements Runnable
    
    {
    	public void run(){
    		if(wn!=null)
    		{
    			try
    			{
    				for(;;)
    				{
    					wn.setForeground(new Color(153,0,51));
    					wn.setFont(new Font("MONOSPACE",Font.BOLD,11));
    					Thread.sleep(700);
    					wn.setForeground(Color.YELLOW);//new Color(0,102,102));
    					Thread.sleep(300);
    				}
    			}
    			catch(InterruptedException ie)
    			{
    				
    			}
    			
    			
    		}
    	}
    }

}
