/*
 * FontChooser.java Copyright (c) 2006,07 Swaroop Belur
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


package net.sf.jdec.ui.util.highlight;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import net.sf.jdec.ui.main.UILauncher;

public class FontChooser extends JDialog  implements ActionListener{

	String fonts[]=null;
	String sizes[]={"8","10","12","14","16","18","20"};
	JLabel curfmly=new JLabel("");
	JLabel cursz=new JLabel("");
	JButton apply= new JButton("Apply");
	JButton prev= new JButton("Preview");
	JComboBox fmly=null;
	JComboBox fsizes=null;
	JPanel prevP=null;
	JLabel sample=null;
	public FontChooser(JFrame frame)
	{
		super(frame);
		setTitle("Jdec Font Chooser");

	}
	private JLabel name=null;
	private JLabel sz=null;
	public void createAllComponents()
	{

		GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
		fonts=ge.getAvailableFontFamilyNames();
		JPanel panel=new JPanel();//new GridLayout(3,1,1,1));
		name=new JLabel("Current User Selected Font Family:");
		name.setText("Current User Selected Font Family:");
		name.setForeground(Color.RED);
		sz=new JLabel("Current User Selected Font Size:");
		sz.setText("Current User Selected Font Size:");
		sz.setForeground(Color.RED);
		JLabel sel1=new JLabel("Select Font Family:");
		JLabel sel2=new JLabel("Select Font Size:                                          ");
		sel1.setForeground(Color.blue);
		sel2.setForeground(Color.blue);
		fmly=new JComboBox(fonts);

		fsizes=new JComboBox(sizes);
		//panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));
		JPanel nameP=new JPanel();
		//nameP.setLayout(new BoxLayout(nameP,BoxLayout.X_AXIS));
		nameP.add(name,BorderLayout.CENTER);
		nameP.add(curfmly,BorderLayout.SOUTH);

		JPanel szP=new JPanel();
		//szP.setLayout(new BoxLayout(szP,BoxLayout.X_AXIS));
		szP.add(sz);
		szP.add(cursz);

		JPanel curSel=new JPanel();
		curSel.setLayout(new BoxLayout(curSel,BoxLayout.Y_AXIS));
		curSel.add(nameP);
		curSel.add(szP);
		curSel.setBorder(new EtchedBorder());

		panel.add(curSel);//,BorderLayout.NORTH);

		JPanel selFml=new JPanel(new GridLayout(1, 2, 10, 2));
		//selFml.setLayout(new BoxLayout(selFml,BoxLayout.X_AXIS));
		selFml.add(sel1);
		selFml.add(fmly);


		JPanel selSize=new JPanel(new GridLayout(1, 2, 10, 2));

		//selSize.setLayout(new BoxLayout(selSize,BoxLayout.X_AXIS));
		selSize.add(sel2);
		selSize.add(fsizes);
		panel.add(new JSeparator());
		panel.add(selFml);//,BorderLayout.CENTER);
		panel.add(selSize);//,BorderLayout.SOUTH);
		panel.setBorder(new TitledBorder("Select Font"));

		prevP=new JPanel();
		sample=new JLabel("Text To Be Previewed");
		//sample.setForeground(Color.blue);
		//prevP.add(sample);
		prevP.add(apply);
		JButton cl=new JButton("Close");
		cl.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				FontChooser.this.setVisible(false);
				FontChooser.this.dispose();
			}
		});
		prevP.add(cl);
		//prevP.add(prev);
		getContentPane().add(panel,BorderLayout.CENTER);
		getContentPane().add(prevP,BorderLayout.SOUTH);
		apply.addActionListener(this);
		prev.addActionListener(this);
		showAnyUserFonts();
	}


	private void showAnyUserFonts()
	{
	  name.setText("Current User Selected Font Family:");
	  sz.setText("Current User Selected Font Size:");
      if(cat!=null && cat.indexOf("Keyword")!=-1)
      {
		   String s1=UILauncher.getUIConfigRef().getFontFamilykwd();
		   String s2=UILauncher.getUIConfigRef().getFontSizekwd();
		   if(name.getText()==null)name.setText("");
		   if(sz.getText()==null)sz.setText("");
		   if(s1==null)s1="";
		   if(s2==null)s2="";
		   name.setText(name.getText()+" (keyword) "+s1);
		   sz.setText(sz.getText()+"   "+s2);
      }
      if(cat!=null && cat.indexOf("Number")!=-1)
      {
		   String s1=UILauncher.getUIConfigRef().getFontFamilynum();
		   String s2=UILauncher.getUIConfigRef().getFontSizenum();
		   if(name.getText()==null)name.setText("");
		   if(sz.getText()==null)sz.setText("");
		   if(s1==null)s1="";
		   if(s2==null)s2="";
		   name.setText(name.getText()+" (Number) "+s1);
		   sz.setText(sz.getText()+"   "+s2);
      }
      if(cat!=null && cat.indexOf("Operator")!=-1)
      {
		   String s1=UILauncher.getUIConfigRef().getFontFamilyop();
		   String s2=UILauncher.getUIConfigRef().getFontSizeop();
		   if(name.getText()==null)name.setText("");
		   if(sz.getText()==null)sz.setText("");
		   if(s1==null)s1="";
		   if(s2==null)s2="";
		   name.setText(name.getText()+" (Operator) "+s1);
		   sz.setText(sz.getText()+"   "+s2);
      }
      if(cat!=null && cat.indexOf("String")!=-1)
      {
		   String s1=UILauncher.getUIConfigRef().getFontFamilystr();
		   String s2=UILauncher.getUIConfigRef().getFontSizestr();
		   if(name.getText()==null)name.setText("");
		   if(sz.getText()==null)sz.setText("");
		   if(s1==null)s1="";
		   if(s2==null)s2="";
		   name.setText(name.getText()+" (String) "+s1);
		   sz.setText(sz.getText()+"   "+s2);
      }
	}


	public void showFontWindow()
	{
		setSize(500,400);
		setVisible(true);
		//pack();
	}


	public static void main(String[] args) {
		FontChooser fc=new FontChooser(new JFrame());
		fc.createAllComponents();
		fc.showFontWindow();

	}

	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource()==apply)
		{
			Object o=fmly.getSelectedItem();
			name.setText("Current User Selected Font Family:");
			sz.setText("Current User Selected Font Size:");
			if(o!=null)
			{
				String fmName=o.toString();
				if(cat.indexOf("Keyword")!=-1)
				{
					UILauncher.getUIConfigRef().setFontFamilykwd(fmName);
					UILauncher.getUIConfigRef().addPref("font_family_kwd",fmName);
					name.setText(name.getText()+" (Keyword)  "+fmName);

				}
				else if(cat.indexOf("Number")!=-1)
				{
					UILauncher.getUIConfigRef().setFontFamilynum(fmName);
					UILauncher.getUIConfigRef().addPref("font_family_num",fmName);
					name.setText(name.getText()+" (Number)  "+fmName);
				}
				else if(cat.indexOf("Operator")!=-1)
				{
					UILauncher.getUIConfigRef().setFontFamilyop(fmName);
					UILauncher.getUIConfigRef().addPref("font_family_op",fmName);
					name.setText(name.getText()+" (Operator)  "+fmName);
				}
				else if(cat.indexOf("String")!=-1)
				{
					UILauncher.getUIConfigRef().setFontFamilystr(fmName);
					UILauncher.getUIConfigRef().addPref("font_family_str",fmName);
					name.setText(name.getText()+"  (String) "+fmName);
				}
				else if(cat.indexOf("Annotation")!=-1)
				{
					UILauncher.getUIConfigRef().setFontFamilyann(fmName);
					UILauncher.getUIConfigRef().addPref("font_family_ann",fmName);
					name.setText(name.getText()+"  (Annotation) "+fmName);
				}


			}
			Object o2=fsizes.getSelectedItem();
			if(o2!=null)
			{
				String size=o2.toString();
				if(cat.indexOf("Keyword")!=-1)
				{
					UILauncher.getUIConfigRef().setFontSizekwd(size);
					UILauncher.getUIConfigRef().addPref("font_size_kwd",size);
					sz.setText(sz.getText()+"   "+size);

				}
				else if(cat.indexOf("Number")!=-1)
				{
					UILauncher.getUIConfigRef().setFontSizenum(size);
					UILauncher.getUIConfigRef().addPref("font_size_num",size);
					sz.setText(sz.getText()+"   "+size);
				}
				else if(cat.indexOf("Operator")!=-1)
				{
					UILauncher.getUIConfigRef().setFontSizeop(size);
					UILauncher.getUIConfigRef().addPref("font_size_op",size);
					sz.setText(sz.getText()+"   "+size);
				}
				else if(cat.indexOf("String")!=-1)
				{
					UILauncher.getUIConfigRef().setFontSizestr(size);
					UILauncher.getUIConfigRef().addPref("font_size_str",size);
					sz.setText(sz.getText()+"   "+size);
				}
				else if(cat.indexOf("Annotation")!=-1)
				{
					UILauncher.getUIConfigRef().setFontSizeann(size);
					UILauncher.getUIConfigRef().addPref("font_size_ann",size);
					sz.setText(sz.getText()+"   "+size);
				}
			}
		}
		else if(ae.getSource()==prev)
		{

		}
	}

	private String cat="";
	public void setCategory(String cat)
	{
		this.cat=cat;
	}
}
