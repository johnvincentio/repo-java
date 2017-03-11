
/*
 * Preferences.java Copyright (c) 2006,07 Swaroop Belur
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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import net.sf.jdec.ui.config.UIConfig;
import net.sf.jdec.ui.main.UILauncher;


public class Preferences extends JDialog  implements ActionListener {

    private JTextField selced=null;
    private JComboBox comboSyn=null;

    public String getFavoriteEditor() {
        return favoriteEditor;
    }

    public void setFavoriteEditor(String favoriteEditor) {
        this.favoriteEditor = favoriteEditor;
    }

    private String favoriteEditor=null;


	public Preferences( JFrame frame ) {

		super( frame, true );
		setTitle( "Jdec Preferences Window" );
		setSize( 700, 300 );
        Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
        int x=(int)d.getWidth()/2-350;
        int y=(int)d.getHeight()/2-150;
		setBounds(x,y,700,300);


		JPanel panel = new JPanel( new BorderLayout() );
		panel.setLayout(new GridBagLayout() );
		panel.setBorder( new EmptyBorder( new Insets( 5, 5, 5, 5 ) ) );
		getContentPane().add( BorderLayout.CENTER, panel );
		GridBagConstraints c = new GridBagConstraints();

		Dimension shortField = new Dimension( 40, 20 );
		Dimension mediumField = new Dimension( 120, 20 );
		Dimension longField = new Dimension( 240, 20 );
		Dimension hugeField = new Dimension( 240, 80 );

		EmptyBorder border = new EmptyBorder( new Insets( 0, 0, 0, 10 ) );
		EmptyBorder border1 = new EmptyBorder( new Insets( 0, 20, 0, 10 ) );

		c.insets = new Insets( 2, 2, 2, 2 );
        
		c.anchor = GridBagConstraints.WEST;
		JLabel lbl1 = new JLabel( "JAVA HOME" );
        lbl1.setForeground(new Color(3,3,62));
		lbl1.setBorder( border );
		panel.add( lbl1, c );
		JTextField txt1 = new JTextField();
		txt1.setActionCommand("javahome");
		txt1.addActionListener(this);
		javahome=txt1;
		String jh=UILauncher.getUIutil().getJavaHomePath();

		if(jh!=null && jh.trim().length()!=0)
			txt1.setText(jh);
		txt1.setPreferredSize( longField );
		c.gridx = 1;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add( txt1, c );
		JLabel lbl2 = new JLabel( "SYSTEM BROWSER PATH" );
        lbl2.setForeground(new Color(3,3,62));
		lbl2.setBorder( border );
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;;
		c.weightx = 0.0;
		panel.add( lbl2, c );
		JTextField txt2 = new JTextField();
		browserpath=txt2;
		txt2.setActionCommand("browser");
		txt2.addActionListener(this);

		txt2.setPreferredSize( longField );
		File bp=UILauncher.getUIutil().getBrowserPath();
		if(bp!=null)
			txt2.setText(bp.getAbsolutePath());


		c.gridx = 1;
		c.weightx = 1.0;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add( txt2, c );


		/**
		 * For LnkFeel
		 */
		JLabel lbl3 = new JLabel( "Default Look & Feel" );
        lbl3.setForeground(new Color(3,3,62));
		lbl3.setBorder( border );
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0.0;
		panel.add( lbl3, c );
		JComboBox combo = new JComboBox();
		combo.setActionCommand("lnk");
		combo.addActionListener(this);
		combo.setPreferredSize( longField );
		String s=UILauncher.getUIutil().getDefaultLnkNFeelName();
		s=UILauncher.getUIutil().getTypeGivenClassName(s);
		combo.insertItemAt("userProvidedAtLaunch", 0);
		combo.insertItemAt("WindowsLookAndFeel", 1);
		combo.insertItemAt("GTKLookAndFeel", 2);
		combo.insertItemAt("MetalLookAndFeel", 3);
		combo.insertItemAt("MotifLookAndFeel", 4);
		combo.insertItemAt("SystemDefault",5);
		int reqd=-1;
		for(int pos=0;pos < combo.getItemCount();pos++)
		{
			if(combo.getItemAt(pos).equals(s))
			{
				reqd=pos;
				break;
			}
		}
		if(reqd!=-1)combo.setSelectedIndex(reqd);
		c.gridx = 1;
		c.weightx = 1.0;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add( combo, c );


        /***
         * For Syntax
         */
        JLabel syn = new JLabel( "Enable Syntax Highlighting [Requires Restart]***" );
        syn.setForeground(new Color(3,3,62));
		lbl3.setBorder( border );
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 4;
		c.weightx = 0.0;
		panel.add( syn, c );
		comboSyn = new JComboBox();

		comboSyn.addActionListener(this);
		comboSyn.setPreferredSize( longField );
		comboSyn.insertItemAt("Yes", 0);
		comboSyn.insertItemAt("No", 1);
        java.lang.String en=UILauncher.getUIConfigRef().getSyntaxEnabled();
        if(en!=null)
        {
          int in=0;
          if(en.equalsIgnoreCase("yes"))
          {
            in=0;
          }
          else
          {
            in=1;
          }
          comboSyn.setSelectedIndex(in);
        }
        else
        {
         comboSyn.setSelectedIndex(0);
        }

        

        comboSyn.addActionListener(this);
        comboSyn.setActionCommand("syntax");
		reqd=0;
		c.gridx = 1;
		c.weightx = 1.0;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add( comboSyn, c );

       /* JLabel synCom = new JLabel( "[NOTE: Change of Syntax to be effective Requires Jdec Restart]");
        synCom.setForeground(Color.RED);
		synCom.setBorder( border );
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 6;
		c.weightx = 0.0;
		panel.add(synCom, c );*/


        JLabel ced = new JLabel( "Set your favorite editor p" +
                "ath" );
        ced.setForeground(new Color(3,3,62));
		ced.setBorder( border );
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 8;
		c.weightx = 0.0;
		panel.add( ced, c );
		selced = new JTextField();

		selced.addActionListener(this);
		selced.setPreferredSize( longField );
		selced.setActionCommand("seluserced");
        String usered=UILauncher.getUIConfigRef().getCustomeEditorPath();
        if(usered!=null)
        {
            selced.setText(usered);
        }


		reqd=0;
		c.gridx = 1;
		c.weightx = 1.0;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add( selced, c );



		JButton submitBtn = new JButton( "Test" );
		submitBtn.setActionCommand("test");
		submitBtn.addActionListener(this);
		c.gridx = 4;
		c.gridy = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add( submitBtn, c );
		JButton selBtn = new JButton( "Select File" );
		selBtn.setActionCommand("selfile");
		selBtn.addActionListener(this);
		c.gridy = 1;
		panel.add( selBtn, c );
        JButton selEdBtn = new JButton( "Select Editor" );
		selEdBtn.setActionCommand("seledfile");
		selEdBtn.addActionListener(this);
		c.gridy = 8;
        c.weightx = 1.0;
		panel.add( selEdBtn, c );
		
		
		JButton lnfhint = new JButton( "LNF Hint" );
		lnfhint.setActionCommand("lnfhint");
		lnfhint.addActionListener(this);
		c.gridy = 10;
        c.weightx = 1.0;
		panel.add(lnfhint, c );
		
		JButton close = new JButton( "Close" );
		close.setActionCommand("close");
		close.addActionListener(this);
		c.gridy = 12;
        c.weightx = 1.0;
		panel.add(close, c );



		JButton helpBtn = new JButton( "Apply" );
		helpBtn.setActionCommand("apply");
		helpBtn.addActionListener(this);
		c.gridy = 2;
		c.anchor = GridBagConstraints.NORTH;
		panel.add( helpBtn, c );
		WindowListener wndCloser = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Preferences.this.dispose();
			}
		};
		addWindowListener( wndCloser );


		JLabel lab = new JLabel("[NOTE:]In order for The values to Be Set propertly Please Click On Buttons test and Select File");

		lab.setBorder( border );
		c.gridx = 0;
		c.gridy = 7;
		c.gridwidth = 1;
		setVisible( true );
	}


     public static String userProvidedAtLaunch=null;
	static HashMap lnkmap=new HashMap();
	static{
		lnkmap.put(new Integer(0),"userProvidedAtLaunch");
		lnkmap.put(new Integer(1),"WindowsLookAndFeel");
		lnkmap.put(new Integer(2),"GTKLookAndFeel");
		lnkmap.put(new Integer(3),"MetalLookAndFeel");
		lnkmap.put(new Integer(4),"MotifLookAndFeel");
		lnkmap.put(new Integer(5),"SystemDefault");
		
	}

	public void actionPerformed(ActionEvent ae)
	{

		
		
		if(ae.getActionCommand().equals("lnfhint"))
		{
			String message="User can start jdec with custom look and feel by passing \n" +
					"System property at launch time\n"+
					"System Property->user.lookNfeel.choice\n"+
					"[Dont Forget to include the supporting jar in classpath]";
			JOptionPane.showMessageDialog(this,message);
			
		}
		if(ae.getActionCommand().equals("close"))
		{
			setVisible(false);
			dispose();
		}
        if(ae.getActionCommand().equals("seledfile"))
        {
              JFileChooser chooser=new JFileChooser(".");
			int option=chooser.showOpenDialog(UILauncher.getMainFrame());
			if(option==JFileChooser.APPROVE_OPTION)
			{
				try{
					File name=chooser.getSelectedFile();
					setFavoriteEditor(name.getAbsolutePath());
					selced.setText(name.getAbsolutePath());
                    UILauncher.getUIConfigRef().setCustomeEditorPath(name.getAbsolutePath());
                    UILauncher.getUIConfigRef().addPref("FavoriteEditor",name.getAbsolutePath());
				}
				catch(Exception e)
				{

				}
			}


        }


        if(ae.getActionCommand().equals("syntax"))
		{
            JComboBox syn=(JComboBox)ae.getSource();
			int index=syn.getSelectedIndex();
            if(index==0)
            {
                UILauncher.getUIConfigRef().setSyntaxEnabled("yes");
                UILauncher.getUIConfigRef().addPref("enableSyntax","yes");
            }
            else if(index==1)
            {
                UILauncher.getUIConfigRef().setSyntaxEnabled("no");
                UILauncher.getUIConfigRef().addPref("enableSyntax","no");
            }
            else
            {
                UILauncher.getUIConfigRef().setSyntaxEnabled("yes");
                UILauncher.getUIConfigRef().addPref("enableSyntax","yes");
            }
        }
		if(ae.getActionCommand().equals("lnk"))
		{
			JComboBox lnkfeel=(JComboBox)ae.getSource();
			int index=lnkfeel.getSelectedIndex();
			String type=(String)lnkmap.get(new Integer(index));
			String classname="";
			if(type.equals("userProvidedAtLaunch") && userProvidedAtLaunch!=null)
			{
				classname=userProvidedAtLaunch;
			}
			else if(type.equals("userProvidedAtLaunch") && userProvidedAtLaunch==null)
			{
				JOptionPane.showMessageDialog(this,"Either No Class was Specified Or the class could not be loaded");
				return;
			}
			else if(type.equals("SystemDefault"))
			{
				classname=UIManager.getSystemLookAndFeelClassName();
			}
			else
			{
				classname=UILauncher.getUIutil().getLookAndFeelClass(type);
			}
			lnkfeel.setSelectedIndex(index);
			UILauncher.getUIutil().setDefaultLnkNFeelName(classname);
			try
			{
				try
				{
					UIManager.setLookAndFeel(classname);
				}
				catch(Exception exp)
				{
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				}
				SwingUtilities.updateComponentTreeUI(UILauncher.getMainFrame());
				// Add to user prefs
				UILauncher.getUIConfigRef().addPref("DefaultLNKFeel",classname);
				UILauncher.getUIutil().setCurrentLNF(UILauncher.getUIutil().getTypeGivenClassName(classname));
				UILauncher.getMainFrame().repaint();
			}
			catch(Exception e)
			{
				JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"A Problem occured while trying to update to new look and feel\nPlease Report Check the log files..\nIn Case of Jdec Related Error Please report to Project Home Site...");
				try
                {
                        LogWriter lg=LogWriter.getInstance();
                        lg.writeLog("[ERROR]: Method: actionPerformed\n\tClass: Prefernces.class");
                        lg.writeLog("------------------------------------------------");
                        lg.writeLog("Exception Stack Trace");
                        e.printStackTrace(lg.getPrintWriter());
                        lg.flush();
                }
                catch(Exception exp)
                {

                }

			}
		}

		if(ae.getActionCommand().equals("browser"))
		{
			setBrowserPath(new File(((JTextField)ae.getSource()).getText()));

		}

		if(ae.getActionCommand().equals("javahome"))
		{
			setJavaFolderPath(new File(((JTextField)ae.getSource()).getText()));

		}
		if(ae.getActionCommand().equals("test"))
		{

			String  name=javahome.getText();
			File javac=new File(name+"\\bin\\javac.exe");
			if(!javac.exists())
			{
				JOptionPane.showMessageDialog(this, "Java Home Path Not Valid", "Set Java Home", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				JOptionPane.showMessageDialog(this, "Correct", "Set Java Home", JOptionPane.INFORMATION_MESSAGE);
				setJavaFolderPath(new File(name));
			}



		}
		else if(ae.getActionCommand().equals("selfile"))
		{

			JFileChooser chooser=new JFileChooser(".");
			int option=chooser.showOpenDialog(UILauncher.getMainFrame());
			if(option==JFileChooser.APPROVE_OPTION)
			{
				try{
					File name=chooser.getSelectedFile();
					setBrowserPath(name);
					browserpath.setText(name.getAbsolutePath());
				}
				catch(Exception e)
				{

				}
			}



		}
		else if(ae.getActionCommand().equals("apply"))
		{
			//Preferences pref=Manager.getManager().getPrefRef();
			// Persist Information
			String s="";
			String b="";
			if(javaFolder!=null)
				s=javaFolder.getAbsolutePath();
			else
			{

				s=javahome.getText();
				setJavaFolderPath(new File(s));
			}
			if(browserPath!=null)
				b=browserPath.getAbsolutePath();
			else{
				b=browserpath.getText();
				setBrowserPath(new File(b));
			}
			UIConfig config=UILauncher.getUIConfigRef();
			config.addPref("JavaHome",s);
			config.addPref("browserPath",b);
            if(favoriteEditor==null)favoriteEditor="";
            config.addPref("FavoriteEditor",favoriteEditor);
            UILauncher.getUIConfigRef().addPref("enableSyntax",comboSyn.getSelectedItem().toString());
			this.setVisible(false);
			this.dispose();


		}


	}

	File javaFolder=null;
	private void setJavaFolderPath(File f)
	{
		javaFolder=f;
		UILauncher.getUIutil().setJavaFolderPath(f);
	}

	private String getJavaHomePath()
	{
		return javaFolder.getAbsolutePath();
	}
	File browserPath=null;
	private void setBrowserPath(File f)
	{
		browserPath=f;
		UILauncher.getUIutil().setBrowserPath(f);
	}

	private String getBrowserPath()
	{
		return browserPath.getAbsolutePath();
	}
	private JTextField javahome=null;
	private JTextField browserpath=null;
}

