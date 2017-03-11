
/*
 * LocalVariableWindow.java Copyright (c) 2006,07 Swaroop Belur 
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.reflection.JavaClass;
import net.sf.jdec.ui.adapter.DecompilerBridge;
import net.sf.jdec.ui.core.Console;
import net.sf.jdec.ui.core.Manager;
import net.sf.jdec.ui.main.UILauncher;
import net.sf.jdec.config.Configuration;
import net.sf.jdec.core.LocalVariableStructure;


public class LocalVariableWindow extends JInternalFrame implements ActionListener
{
    




    JavaClass clazz;
    Hashtable ht;


    private void repaintWindow()
    {
        Container c=this.getContentPane();
        c.removeAll();
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
        //setBounds(200,200,400,300);
        //System.out.println(details+"details");
        if(details!=null)
        {
            JPanel panel2=new JPanel();
            panel2.add(details,BorderLayout.CENTER);
            panel2.setVisible(true);
            c.add(details,BorderLayout.CENTER);
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
        }
        pack();
        setVisible(true);


    }

    public LocalVariableWindow(String type)
    {

            
    		super("Method Look Up");

          JTabbedPane rightTab=UIUtil.getUIUtil().getRightTabbedPane();
          Console c=UILauncher.getUIutil().getConsoleFrame();
          int cp=rightTab.indexOfTab("Local Variables"); 
          if(cp!=-1)rightTab.remove(cp);
          /*if(rightTab.indexOfTab("Local Variables")!=-1)
          {
            //  Note : Need to write better check here
            //	JOptionPane.showMessageDialog(UILauncher.getMainFrame(), "Constant Pool Is Already Displayed");
          }*/
          /*else
          {*/

              DecompilerBridge bridge=DecompilerBridge.getInstance(UILauncher.getUIutil());
              //bridge.setDecompilerConfig();

      		if(c!=null){
      			JEditorPane rdwr=c.getComponent();
      			rdwr.setText("");
      			String s="Current Task: View Local Variables\n";
      			s=rdwr.getText()+"\n\n"+s;
      			rdwr.setText(s);
      		}
      	
              bridge.execute("localVariables","",false,type);
              if(bridge.isUserCanceled())return;
              if(c!=null){
        			JEditorPane rdwr=c.getComponent();
        			rdwr.setText("");
        			String s="Current File: "+ Configuration.getJavaClassFile();
        			s=rdwr.getText()+"\n\n"+s;
        			rdwr.setText(s);
        		}


              ht= ConsoleLauncher.getMethodSignMethodLookup();

              //bridge.showResult(rightTab);
          //}


        clazz= ConsoleLauncher.getClazzRef();
        Container cpane=getContentPane();
        JPanel panel=new JPanel();
        if(c!=null){
			JEditorPane rdwr=c.getComponent();
			rdwr.setText("");
			String s="Done....\nPlease Wait For UI to render the local variables\nPlease Check up Log Files For any error(s)";
			s=rdwr.getText()+"\n\n"+s;
			rdwr.setText(s);
		}

         Manager.getManager().setShowProgressBar(false);

        if(ht!=null && ht.size() > 0)
        {
            JLabel label=new JLabel("Please Select A Method From List...");
            panel.add(label,BorderLayout.NORTH);
            JComboBox combo=new JComboBox();
            populate(ht,combo);
            panel.add(combo,BorderLayout.SOUTH);
            panel.setVisible(true);
        }
        cpane.add(panel,BorderLayout.CENTER);
        setBounds(200,200,400,300);
        if(details!=null)
        cpane.add(details,BorderLayout.SOUTH);

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
        
        if(ae.getActionCommand().equals("combo"))
        {

            JComboBox methods=(JComboBox)ae.getSource();
           // System.out.println(methods.getSelectedItem());
            java.lang.String desc=(java.lang.String)methods.getSelectedItem();
            Behaviour b=(Behaviour)ht.get(desc);
            LocalVariableStructure structure=b.getLocalVariables();
            if(structure!=null)
            {
                ArrayList localVars=structure.getMethodLocalVaribales();
                if(localVars!=null){
                    LocalVariableDetails details=new LocalVariableDetails(localVars);
                    setDetailsFrame(details);
                    repaintWindow();

                }
                if(localVars==null || localVars.size()==0)
                {
                    setDetailsFrame(null);
                    String mesg="Could Not Display Local Variables For Method...\n";
                    mesg+="Either There are no local Variables For This Method Or \n IF you have access to source code Please compile with -g option";
                    JOptionPane.showMessageDialog(UILauncher.getMainFrame(),mesg);
                }
            }
            else
            {
                setDetailsFrame(null);
                String mesg="Could Not Display Local Variables For Method...\n";
                mesg+="Either There are no local Variables For This Method Or \n IF you have access to source code Please compile with -g option";
                JOptionPane.showMessageDialog(UILauncher.getMainFrame(),mesg);
            }

        }
    }

    private JInternalFrame details=null;
    private void setDetailsFrame(JInternalFrame details)
    {
        this.details=details;
    }




}
