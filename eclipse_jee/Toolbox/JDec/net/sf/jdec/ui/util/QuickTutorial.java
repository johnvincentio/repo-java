/*
 * QuickTutorial.java Copyright (c) 2006,07 Swaroop Belur
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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class QuickTutorial extends JFrame implements ActionListener {

	private ArrayList list=null;
	private JPanel panel=null;
	private JButton next=null;
	private JButton close=null;
	private JButton prev=null;
	private JLabel lbl=null;
	private JTextArea textarea=null;
	private int pos=1;

	public static void main(String[] args) {
		QuickTutorial obj=new QuickTutorial("Jdec Quick Tutorial");

	}


	public void actionPerformed(ActionEvent e) {


		if(e.getSource()==close)
		{
			setVisible(false);
			dispose();
			
		}
			if(e.getSource()==next)
			{
				if(pos < 0)
				{
					next.setEnabled(true);
					prev.setEnabled(false);
					pos=0;
					textarea.setText(list.get(pos).toString());
					pos++;

				}
				else if(pos >= list.size())
				{
					next.setEnabled(false);
					prev.setEnabled(true);
				}
				else
				{
					if(pos < list.size())
					{
						textarea.setText(list.get(pos).toString());
						pos++;
						if(pos==list.size())
							next.setEnabled(false);
						else
							next.setEnabled(true);
						prev.setEnabled(true);
					}
					else
					{
						next.setEnabled(false);
						prev.setEnabled(true);
					}

				}



			}
			else if(e.getActionCommand().equals("prev"))
			{
				if(pos <= 0)
				{
					next.setEnabled(true);
					prev.setEnabled(false);
				}
				else if(pos > 0 && pos < list.size())
				{
					pos--;
					textarea.setText(list.get(pos).toString());
					next.setEnabled(true);
					if(pos==0)
						prev.setEnabled(false);
					else
						prev.setEnabled(true);


				}
				else
				{
					next.setEnabled(false);
					prev.setEnabled(true);
					pos=list.size()-2;
					textarea.setText(list.get(pos).toString());
					next.setEnabled(true);

				}




			}

	}

	public QuickTutorial(String s)
	{
		super(s);
		createTutorial();
		panel=new JPanel();
		next=new JButton("Next");
		close=new JButton("close");
		prev=new JButton("prev");
		next.addActionListener(this);
		close.addActionListener(this);
		prev.addActionListener(this);
		lbl=new JLabel(list.get(0).toString());
		textarea=new JTextArea();
		textarea.setWrapStyleWord(true);
		textarea.setColumns(50);
		textarea.setAutoscrolls(true);
		textarea.setFont(new Font(null,Font.BOLD,12));
		textarea.setEditable(false);
		textarea.setText(list.get(0).toString());
		getContentPane().add(textarea,BorderLayout.CENTER);
		panel.add(prev);
		panel.add(next);
		panel.add(close);
		getContentPane().add(panel,BorderLayout.SOUTH);
		setBounds(300,300,500,200);
		prev.setEnabled(false);
		setVisible(true);
    }

	private void createTutorial()
	{
		 list=new ArrayList();
		 String s="";
		 s="There are various ways in which user can use the tool\n\n";
		 s+="Please use this Quick tutorial to find out how !";
		 list.add(s);
		 s="In all cases first the user has to configure the tool options first\n";
		 s+="1>Navigate to Configuration menu\n\n";
		 s+="2>Click on jdec config options\n\n";
		 s+="3>Here the user can update the options\n\n";
		 list.add(s);
		 s="Once the config options have been updated\n\n";
		 s+="User can run the tool in various ways\n\n";
		 s+="First Approach\n\n";
		 s+="Directly Click on Run Jdec Image Button";
		 list.add(s);
		 s="Second Approach\n\n";
		 s+="Choose the .class File from the folder/file list\n\n";
		 s+="In the left panel\n\n";
		 list.add(s);
		 s="Third Approach\n\n";
		 s+="Navigate to File Menu\n\n";
		 s+="Click on new jdec task\n\n";
		 s+="Choose the .class File from the file system\n\n";
		 list.add(s);
	}

}
