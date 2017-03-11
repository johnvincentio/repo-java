/*
 * ColorChooser.java Copyright (c) 2006,07 Swaroop Belur
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.jdec.ui.main.UILauncher;

public class ColorChooser extends JFrame  implements ChangeListener,ActionListener{

	private JLabel banner=null;
	private JColorChooser colorChooser=null;
	private JButton apply=new JButton("Select");
	private JButton close=new JButton("Close");
	public ColorChooser() {


		JPanel colorPanel=new JPanel();
		banner = new JLabel("Please Select A Color",JLabel.CENTER);
		banner.setForeground(Color.yellow);
		colorPanel.add(banner,BorderLayout.NORTH);
		colorChooser = new JColorChooser(banner.getForeground());
		colorPanel.add(colorChooser, BorderLayout.CENTER);
		colorChooser.getSelectionModel().addChangeListener(this);
		JPanel actions=new JPanel();
        actions.add(apply);
        actions.add(close);
        colorPanel.add(actions, BorderLayout.CENTER);
        getContentPane().add(colorPanel);
        apply.addActionListener(this);
        close.addActionListener(this);
        setSize(500,500);
		setVisible(true);
	}

	public static void main(String a[]){
		new ColorChooser();
	}

	public void stateChanged(ChangeEvent e) {

		Color newColor = colorChooser.getColor();
		//JOptionPane.showMessageDialog(this,"["+newColor.getRed()+" "+newColor.getGreen()+" "+newColor.getBlue()+"]");
	    banner.setForeground(newColor);
	}

	public void actionPerformed(ActionEvent e) {

		if(e.getSource()==apply)
		{
			Color newColor = colorChooser.getColor();
			int r=newColor.getRed();
			int g=newColor.getGreen();
			int b=newColor.getBlue();
			String color="["+r+","+g+","+b+"]";
			CategoryChooser ch=UILauncher.getUIConfigRef().cc;
			if(ch!=null)
			{
				if(!isForeG)
				{

					Object at1=ch.getBackg().getItemAt(1);
					if(at1!=null && at1.toString().indexOf("Choose")==-1)
					{
						ch.getBackg().removeItemAt(1);
					}
					ch.getBackg().insertItemAt(color,1);
					ch.getBackg().setSelectedIndex(1);
					ch.getTypes2().revalidate();
					ch.getValues().revalidate();
				}
				else
				{

					Object at1=ch.getForeg().getItemAt(1);
					if(at1!=null && at1.toString().indexOf("Choose")==-1)
					{
						ch.getForeg().removeItemAt(1);
					}
					ch.getForeg().insertItemAt(color,1);
					ch.getForeg().setSelectedIndex(1);
					ch.getTypes2().revalidate();
					ch.getValues().revalidate();
				}


			}

		}

		else if(e.getSource()==close)
		{
			this.setVisible(false);
			this.dispose();
		}

	}

	public  void setForeGround(boolean b)
	{
		isForeG=b;
	}

	private boolean isForeG=false;
}