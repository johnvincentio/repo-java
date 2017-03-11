/*
 *  ArchiveSettings.java Copyright (c) 2006,07 Swaroop Belur
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import net.sf.jdec.ui.config.UIConfig;
import net.sf.jdec.ui.main.UILauncher;

public class ArchiveSettings extends JFrame implements ActionListener {

	private JTextField registerType_text=new JTextField();
	{
		registerType_text.setText("");
		registerType_text.setColumns(10);
	}
	private JButton register=new JButton("Register Type");
	private JComboBox rtypes=new JComboBox();
	private JButton restore=new JButton("Restore Original Types");
	private JLabel supported=new JLabel();
	
	public static ArrayList currentlist=new ArrayList();
	
	
	public ArchiveSettings() {
		super("Archive ExceptionSettings");
		readCurrentRegsitered();
		initialize0();
		initialize1();
		addNoteAndApply();
		setBounds(100,100,550,250);
		setVisible(true);
	}
	
	
	private void readCurrentRegsitered()
	{
		String s=UILauncher.getUIConfigRef().getArchiveTypes();
		//System.out.println(s+"!!!");
		if(s!=null)
		{
			 StringTokenizer tok=new StringTokenizer(s,",");
			 while(tok.hasMoreTokens())
			 {
				 String s1=(String)tok.nextToken();
				 rtypes.addItem(s1);
			 }
			 rtypes.revalidate();
		}
		else
		{
			rtypes.addItem("jar");
			rtypes.addItem("zip");
			rtypes.revalidate();
		}
	}
	
	private void addNoteAndApply()
	{
		String s=UILauncher.getUIConfigRef().getArchiveTypes();
		if(s==null)
		{
			s="jar, zip";
		}
		JLabel cur=new JLabel("Current Registered Types: "+s+"						");
		cur.setForeground(Color.BLUE);
		JButton app=new JButton("Apply");
		app.addActionListener(this);
		app.setActionCommand("apply");
		JButton appcl=new JButton("Apply & Close");
		appcl.addActionListener(this);
		appcl.setActionCommand("applyNclose");
		
		JPanel panel=new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		panel.add(cur);
		panel.add(app);
		panel.add(appcl);
		getContentPane().add(panel,BorderLayout.SOUTH);
		
		
	}
	
	private JCheckBox clazz0=null;
	private JCheckBox clazz1=null;
	private JCheckBox arch=null;
	private void initialize1()
	{
		JPanel acPanel=new JPanel();
		acPanel.setBorder(new TitledBorder("Update Actions"));
		clazz0=new JCheckBox("Process class files belonging to any package");
		clazz0.setSelected(true);
		clazz1=new JCheckBox("Process only class files belonging to default package");
		arch=new JCheckBox("Process enclosed archive files");
		acPanel.setLayout(new BoxLayout(acPanel,BoxLayout.Y_AXIS));
		acPanel.add(clazz0);
		acPanel.add(clazz1);
		acPanel.add(arch);
		
		ButtonGroup grp=new ButtonGroup();
		grp.add(clazz0);grp.add(clazz1);
		String s=UIConfig.getUIConfig().getAll_classes_in_archive();
		//System.out.println(s);
		if(s==null || (s.trim().equals("true")))
		{
			clazz0.setSelected(true);
		}
		else
		{
			clazz0.setSelected(false);
		}
		s=UIConfig.getUIConfig().getOnly_default_classes_in_archive();
		//System.out.println(s);
		if(s==null || (s.trim().equals("true")))
		{
			clazz1.setSelected(true);
		}
		else
		{
			clazz1.setSelected(false);
		}
		s=UIConfig.getUIConfig().getEnclosed_archives_in_archive();
		//System.out.println(s);
		if(s==null || (s.trim().equals("true")))
		{
			arch.setSelected(true);
		}
		else
		{
			arch.setSelected(false);
		}

		
		getContentPane().add(acPanel,BorderLayout.CENTER);
		//getContentPane().add(buttons,BorderLayout.EAST);
	}
	
	private void initialize0()
	{
		JPanel regPanel=new JPanel();
		JPanel buttons=new JPanel();
		//buttons.setLayout(new BoxLayout(buttons,BoxLayout.Y_AXIS));
		//buttons.add(register);
		buttons.add(restore);
		restore.addActionListener(this);
		restore.setActionCommand("restore");
		regPanel.setBorder(new TitledBorder("Regsiter new Archive Type"));
		regPanel.add(registerType_text,BorderLayout.WEST);
		regPanel.add(register,BorderLayout.CENTER);
		regPanel.add(rtypes,BorderLayout.EAST);
		register.addActionListener(this);
		register.setActionCommand("register");

		getContentPane().add(regPanel,BorderLayout.NORTH);
		getContentPane().add(buttons,BorderLayout.EAST);
	}

	public void actionPerformed(ActionEvent e) {

		if(e.getActionCommand().equals("restore"))
		{
			rtypes.removeAllItems();
			rtypes.addItem("jar");
			rtypes.addItem("zip");
			rtypes.revalidate();
		}
		else if(e.getActionCommand().equals("register"))
		{
			String s=registerType_text.getText();
			if(s!=null)
			{
				rtypes.addItem(s.trim());
				rtypes.revalidate();
			}
		}
		else if(e.getActionCommand().equals("apply"))
		{
			int c=rtypes.getItemCount();
			String s="";
			for(int j=0;j<c;j++)
			{
				s+=rtypes.getItemAt(j).toString();
				if(j!=c-1)
					s+=",";
			}
			if(s.length()==0)
			{
				s="jar,zip";
			}
			UIConfig.getUIConfig().addPref("registered_archive_types",s);
			UIConfig.getUIConfig().setArchiveTypes(s);
			
			boolean b1=clazz0.isSelected();
			boolean b2=clazz1.isSelected();
			boolean b3=arch.isSelected();
			
			UIConfig.getUIConfig().addPref("all_classes_in_archive",""+b1);
			UIConfig.getUIConfig().addPref("only_default_classes_in_archive",""+b2);
			UIConfig.getUIConfig().addPref("enclosed_archives_in_archive",""+b3);
			UIConfig.getUIConfig().setAll_classes_in_archive(""+b1);
			UIConfig.getUIConfig().setOnly_default_classes_in_archive(""+b2);
			UIConfig.getUIConfig().setEnclosed_archives_in_archive(""+b3);
			
			
			
		}
		else if(e.getActionCommand().equals("applyNclose"))
		{
			int c=rtypes.getItemCount();
			String s="";
			for(int j=0;j<c;j++)
			{
				s+=rtypes.getItemAt(j).toString();
				if(j!=c-1)
					s+=",";
			}
			if(s.length()==0)
			{
				s="jar,zip";
			}
			UIConfig.getUIConfig().addPref("registered_archive_types",s);
			UIConfig.getUIConfig().setArchiveTypes(s);
			boolean b1=clazz0.isSelected();
			boolean b2=clazz1.isSelected();
			boolean b3=arch.isSelected();
			
			UIConfig.getUIConfig().addPref("all_classes_in_archive",""+b1);
			UIConfig.getUIConfig().addPref("only_default_classes_in_archive",""+b2);
			UIConfig.getUIConfig().addPref("enclosed_archives_in_archive",""+b3);
			UIConfig.getUIConfig().setAll_classes_in_archive(""+b1);
			UIConfig.getUIConfig().setOnly_default_classes_in_archive(""+b2);
			UIConfig.getUIConfig().setEnclosed_archives_in_archive(""+b3);
	
			setEnabled(false);
			setVisible(false);
			dispose();
		}


	}
	public static void main(String[] args) {
		new ArchiveSettings();
	}

}
