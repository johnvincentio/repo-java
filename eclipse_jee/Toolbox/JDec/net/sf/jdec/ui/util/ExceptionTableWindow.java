
/*
 *  ExceptionTableWindow.java Copyright (c) 2006,07 Swaroop Belur 
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
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
  import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
  import javax.swing.JTabbedPane;

import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.reflection.JavaClass;
import net.sf.jdec.ui.adapter.DecompilerBridge;
import net.sf.jdec.ui.core.Manager;
import net.sf.jdec.ui.main.UILauncher;


	public class ExceptionTableWindow extends JInternalFrame implements ActionListener
	{
	    private JTabbedPane getRightTabbedPane()
	    {
	        Manager manager=Manager.getManager();
	        ArrayList paneList=manager.getCurrentSplitPaneComponents();
	           JTabbedPane tabs=null;
	           for(int s=0;s<paneList.size();s++)
	           {
	               Object current=paneList.get(s);
	               if(current instanceof JTabbedPane)
	               {
	                   tabs=(JTabbedPane)current;
	                   if(tabs.getTabCount() > 2)
	                   {
	                       break;
	                   }

	               }
	           }

	        return tabs;
	    }




	    JavaClass clazz;
	    Hashtable ht;


	    private void repaintWindow()
	    {
	        Container c=this.getContentPane();
	        c.removeAll();
	        JPanel panel=new JPanel();



	        if(ht.size() > 0)
	        {
	            JLabel label=new JLabel("Please Select A Method From List");
	            JLabel label2=new JLabel("To Interpret Exception Table Select Help option");
	            panel.add(label,BorderLayout.NORTH);
	            //panel.add(label2,BorderLayout.EAST);
	            JComboBox combo=new JComboBox();
	            populate(ht,combo);
	            panel.add(combo,BorderLayout.SOUTH);
	            panel.setVisible(true);
	        }
	        c.add(panel,BorderLayout.NORTH);
	        //setBounds(200,200,400,300);
	     //   System.out.println(details+"details");
	    /*    if(details!=null)
	        {
	            /*JPanel panel2=new JPanel();
	            panel2.add(details,BorderLayout.CENTER);
	            panel2.setVisible(true);
	            c.add(details,BorderLayout.SOUTH);
	            details.setResizable(false);
	            details.setOpaque(false);
	            details.addPropertyChangeListener(new PropertyChangeListener(){

	            		public void propertyChange(PropertyChangeEvent e)
	            		{
	            			//	System.out.println(e.getPropertyName()+"xyz");
	            				if(e.getPropertyName().equals("selected"))
	            				{
	            						//System.out.println("Fired");
	            						JInternalFrame frame=(JInternalFrame)e.getSource();
	            						frame.setEnabled(false);

	            				}
	            		}

	            });

	          
	        }*/
	       
	        //setBounds(200,100,400,100);
	        if(details!=null)
	        c.add(details,BorderLayout.SOUTH);
	        

	        pack();
	        setVisible(true);


	    }

	    public ExceptionTableWindow()
	    {

	            super("Method Look Up");
	          JTabbedPane rightTab=getRightTabbedPane();
	         // int cp=rightTab.indexOfTab("Local Variables"); 
	        //  if(cp!=-1)rightTab.remove(cp);
	         /* if(rightTab.indexOfTab("Local Variables")!=-1)
	          {
	            //  Note : Need to write better check here
	            //	JOptionPane.showMessageDialog(UILauncher.getMainFrame(), "Constant Pool Is Already Displayed");
	          }
	          else
	          {*/

	              DecompilerBridge bridge=DecompilerBridge.getInstance(UILauncher.getUIutil());
	              //bridge.setDecompilerConfig();
	              //bridge.execute("decompileClass","localVariables",true,"view");
	              bridge.execute("localVariables","ExceptionTableDetails",true,"view");
	              ht= ConsoleLauncher.getMethodLookup();
	              	
	              if(bridge.isUserCanceled())return;
	              
	              //bridge.showResult(rightTab);
//	          }


	        clazz= ConsoleLauncher.getClazzRef();
	        Container c=getContentPane();
	        JPanel panel=new JPanel();



	        if(ht.size() > 0)
	        {
	            JLabel label=new JLabel("Please Select A Method From List...");
	            panel.add(label,BorderLayout.NORTH);
	            JComboBox combo=new JComboBox();
	            populate(ht,combo);
	            panel.add(combo,BorderLayout.SOUTH);
	            panel.setVisible(true);
	        }
	        c.add(panel,BorderLayout.NORTH);
	        setBounds(200,200,400,300);
	        if(details!=null)
	        c.add(details,BorderLayout.SOUTH);

	        setVisible(true);
	    }


	    private void populate(Hashtable methodSignatures,JComboBox combo)
	    {

	        Iterator it=methodSignatures.entrySet().iterator();
	        while(it.hasNext())
	        {
	            Map.Entry entry=(Map.Entry)it.next();
	          
	            combo.addItem(entry.getKey());
	        }
	        combo.setActionCommand("combo");
	        combo.addActionListener(this);

	    }


	    public void actionPerformed(ActionEvent ae)
	    {
	       // System.out.println(ae.getActionCommand());
	        if(ae.getActionCommand().equals("combo"))
	        {

	            JComboBox methods=(JComboBox)ae.getSource();
	           // System.out.println(methods.getSelectedItem());
	            java.lang.String desc=(java.lang.String)methods.getSelectedItem();
	            Behaviour b=(Behaviour)ht.get(desc);
	            ArrayList allExceptions=b.getExceptionTableList();
	            if(allExceptions!=null)
	            {
	                 ExceptionTableDetails details=new ExceptionTableDetails(allExceptions);
	                 setDetailsFrame(details);
	                 repaintWindow();
	            }
	            else
	            {
	                setDetailsFrame(null);
	                String mesg="Could Not Display Exception Table List For Method...\n";
	                JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"mesg");
	            }

	        }
	    }

	    private JInternalFrame details=null;
	    private void setDetailsFrame(JInternalFrame details)
	    {
	        this.details=details;
	    }




	}

