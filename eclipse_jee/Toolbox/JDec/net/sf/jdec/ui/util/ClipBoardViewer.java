/*
 *  ClipBoardViewer.java Copyright (c) 2006,07 Swaroop Belur 
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


import java.awt.Color;
import java.awt.Container;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
public class ClipBoardViewer extends JFrame implements CaretListener{
	JEditorPane editor=null;
	public ClipBoardViewer(java.lang.String s)
	{
		super(s);
		JMenuBar bar=new JMenuBar();
		setJMenuBar(bar);
		JMenu edit=new JMenu("Edit");
		JMenu refresh=new JMenu("Refresh Viewer");
		JMenuItem copy=new JMenuItem("Copy");
		JMenuItem all=new JMenuItem("Select All");
		JMenuItem  ref=new JMenuItem("Refresh Viewer");
		JMenuItem close=new JMenuItem("Close");
		editor=new JEditorPane();
        editor.addCaretListener(this);
        editor.setBackground(Color.LIGHT_GRAY);
		JScrollPane scroll=new JScrollPane(editor);
		Container c=getContentPane();
		setJMenuBar(bar);
		c.add(scroll);
		listener l=new listener();
		close.addActionListener(l);
		copy.addActionListener(l);
		all.addActionListener(l);
		ref.addActionListener(l);
		addClipBoardText(editor);
		bar.add(edit);
		bar.add(refresh);
		edit.add(copy);
		edit.add(all);
		edit.add(close);
		refresh.add(ref);
		WindowListener wndCloser = new WindowAdapter() {
	    	public void windowClosing(WindowEvent e) {
	    		ClipBoardViewer.this.setVisible(false);
	    		ClipBoardViewer.this.dispose();
				
	    	}
	    	};
	    	this.addWindowListener(wndCloser);
		
	}
	
	public void display()
	{
		java.awt.Dimension d=getToolkit().getScreenSize();
		setSize((int)d.getWidth()-100,400);
		setVisible(true);
		
	}
	
	private void addClipBoardText(JEditorPane editor)
	{
		
		StringBuffer text=new StringBuffer("");
		int counter=1;
		try
		{

			Clipboard clip=editor.getToolkit().getSystemClipboard();
			Transferable tr=clip.getContents(null);
			if(tr!=null)
			{
				DataFlavor[] fl=tr.getTransferDataFlavors() ;

				if(fl!=null)
				{
					for(int s=0;s<fl.length;s++)
					{
						Class cl=fl[s].getRepresentationClass().getClass();
						if(fl[s].isFlavorTextType())
						{
						Object o=tr.getTransferData(fl[s]) ;
						if(o instanceof java.lang.String)
							text.append(o.toString()+"\n");
						}


					}

					if(text.toString().length() > 0)
					{
						StringBuffer str=new StringBuffer("Below Is the Current List of Strings Copied and Present in System ClipBoard\n");
						str.append("[NOTE] : Anything missing from this list was not present as a String in ClipBoard\n");
						str.append("[NOTE] : You Can Copy Any Text and Paste in Jdec Editor Window...\n\n");
                        editor.setText(str.toString()+"\n\n"+text.toString());
					}
				}
			}

            ArrayList list=UIUtil.getCurrentSels();
            StringBuffer prevSels=new StringBuffer("");
            if(list!=null)
            {
                for(int z=0;z<list.size();z++)
                {
                    String s=(String)list.get(z);
                    prevSels.append(s);
                    prevSels.append("\n");

                }
            }
            if(prevSels.toString().length() > 0)
            {
                editor.setText(editor.getText()+"\n\n***Below is the list of Previously copied text***\n\n"+prevSels.toString());
            }

		}
		
		catch(Exception e)
		{
				// Dont Handle...
				// Some MalFormedUrlException was coming when trying to add clipboard
			    // text t editor. Dont know why...
			    // BUT viewer works properly....
			    // So Skip Reporting
		}
		
		
		
	}
    int start=-1;
    int end=-1;
    String currentSel="";
    ArrayList currentSels=new ArrayList();

    public void caretUpdate(CaretEvent e) {

        try
        {
          start=e.getDot();
          end=e.getMark();
          if(start > end)
          {
            currentSel=editor.getDocument().getText(end,start-end);
          }
          else
          {
              currentSel=editor.getDocument().getText(start,end-start);
          }
        }
        catch(BadLocationException b){

            currentSel="";

        }



    }


    class listener implements ActionListener
	{
		public void actionPerformed(ActionEvent ae)
		{
			if(ae.getActionCommand().equals("Copy"))
			{
                if(currentSel!=null && currentSel.length() > 0)
                    UIUtil.addSelection(currentSel);
				editor.copy();
			}
			if(ae.getActionCommand().equals("Select All"))
			{
				editor.selectAll();
			}
			if(ae.getActionCommand().equals("Refresh Viewer"))
			{
						editor.setText("");
							addClipBoardText(editor);
				
			}
			if(ae.getActionCommand().equals("Close"))
			{
				setVisible(false);
				dispose();
			}
					
		}
	}
	
	
}
