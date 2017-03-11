
/*
 *  Console.java Copyright (c) 2006,07 Swaroop Belur 
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
package net.sf.jdec.ui.core;

import java.awt.Dimension;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

/**
 *@author swaroop belur
 */
public class Console extends UIObject  {

     private JEditorPane jeditor=null;
     private JScrollPane scroll=null;
     public void setDesc(java.lang.String desc)
     {
         this.whatAmI=desc;
     }

     public Console()
     {
         jeditor=new JEditorPane("text/plain","Jdec Console window");
         jeditor.setMaximumSize(new Dimension(200,300));
         //jeditor.setBackground(Color.LIGHT_GRAY);
     }

     public JEditorPane getComponent()
     {
	 //scroll=new JScrollPane(jeditor);
	  	//scroll.setMinimumSize(new Dimension(scroll.getWidth(),scroll.getHeight()+255));	 
	         return jeditor;

     }
}
