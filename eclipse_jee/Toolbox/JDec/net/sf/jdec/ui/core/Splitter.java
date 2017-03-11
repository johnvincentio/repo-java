/*
 * Splitter.java Copyright (c) 2006,07 Swaroop Belur 
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

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import net.sf.jdec.ui.main.UILauncher;


/**
 *@author swaroop belur
 */
public class Splitter extends UIObject {


    public JSplitPane getSplitPlane() {
        return splitPlane;
    }

    JSplitPane splitPlane=null;

     public void setDesc(java.lang.String desc)
     {
         this.whatAmI=desc;
     }

     public Splitter(FileInfoFrame frame,JdecStatusFrame statusFrame,JTabbedPane tabs,JTabbedPane outputframe,Console consoleFrame)
     {
        createSplitter(frame,statusFrame,tabs,outputframe,consoleFrame);
     }

     JSplitPane jsp1;
     FileInfoFrame frame=null;
     Component FileInfoComponent=null;
     Component c1;
     JdecStatusFrame jdecstatusFrame=null;
     JTabbedPane lefttabs=null;
     Console splitPaneConsoleFrame=null;

     public void resetInfoFrame(FileInfoFrame newframe)
     {
     	//frame=newframe;
     	FileInfoComponent=frame.getDetails();
     	//c1=FileInfoComponent;
     	frame.getModel().getValueAt(0,1);
     	//frame.getModel().setValueAt("Dummy Value",0,1);
     	jsp1.remove(c1);
     	FileInfoComponent.setSize(c1.getWidth(),c1.getHeight());
     	c1=FileInfoComponent;
     	jsp1.add(c1);
    	JFrame mainFrame=UILauncher.getMainFrame();
		mainFrame.repaint();
     }
     
    public  Splitter createSplitter(FileInfoFrame frame,JdecStatusFrame statusFrame,JTabbedPane tabs,JTabbedPane outputframe,Console consoleFrame)
    {
        //splitPlane
    	this.frame=frame;
        c1=frame.getDetails();
        //FileInfoComponent=c1;
        Component c2=statusFrame.getStatus();
        splitPaneConsoleFrame=consoleFrame;
        lefttabs=tabs;
        jdecstatusFrame=statusFrame;
        //Dimension d=c1.getToolkit().getScreenSize();
        //c1.setSize(100, 100);
        //c1.setSize(100, 100);
        jsp1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, c1, null); // Removed Status frame
        c1.setSize(jsp1.getWidth()+20,jsp1.getHeight()+75);
        jsp1.setDividerSize(8);
        jsp1.setContinuousLayout(true);
        Component c3=tabs;
        Component c33=new JdecTree();
        Dimension d=jsp1.getSize();
        
        jsp1.setMinimumSize(new Dimension((int)d.getWidth()+20,(int)d.getHeight()+50));
        d=jsp1.getSize();
        
        /*JTabbedPane drives=new JTabbedPane(SwingConstants.BOTTOM);
        drives.addTab("Dir", treeFrame);
        drives.addTab("Jar",new JLabel("Jar File Viewer..."));
        drives.doLayout();
        drives.validate();
        drives.setEnabled(true);
        drives.setVisible(true);
        drives.setSelectedIndex(0);*/
        /*JFrame tabs=new JFrame();
        tabs.setSize(300,300);
        tabs.getContentPane().setLayout(new GridLayout(2,1));
        tabs.getContentPane().add(drives);
        tabs.setVisible(true);*/


        JSplitPane spLeft = new JSplitPane(JSplitPane.VERTICAL_SPLIT, jsp1,c3);
        jsp1.setToolTipText("Drag To Increase Visbility");


       
        c3.setSize(0,0);
        
        jsp1.setMinimumSize(new Dimension((int)c3.getMinimumSize().getWidth(),(int)c3.getMinimumSize().getHeight()));

        
        Component c4 = outputframe;
        Component c5 = consoleFrame.getComponent();
        spLeft.setContinuousLayout(true);
        alloutputFrames=c4;
        consoleSplitFrame=c5;
        spRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT, c4, c5);
        //ref.getLeftSplitPane().setSize(ref.getLeftSplitPane().getWidth(),c4.getHeight()+75);
        //c4.setSize(w, c4.getHeight()+75);
        //right.setDividerSize(8);
        spRight.setContinuousLayout(true);
        spRight.setOrientation(0);
        spRight.setDividerSize(5);
        spRight.setOneTouchExpandable(true);
        spRight.setLastDividerLocation(1);
        spRight.setResizeWeight(0.8);
        spRight.setBounds(253,1,752,631);
        spRight.setMinimumSize(new Dimension(spRight.getWidth()-75,spRight.getHeight()));
        
        //spRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT, c4, c5);
        c4.setSize(spRight.getWidth(), c4.getHeight()+75);
        //spRight.setDividerSize(1);
        //spRight.setContinuousLayout(true);
        spLeftPane=spLeft;
        //spRight.setMinimumSize(new Dimension(spRight.getWidth()-75,spRight.getHeight()));
        //spRight.setDividerLocation(0.7);
        splitPlane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,spLeft, spRight);
        spLeft.setToolTipText("Drag To Increase Visibility");
        splitPlane.setContinuousLayout(true);
        splitPlane.setOneTouchExpandable(true);
        splitPlane.setAutoscrolls(true);
        
        
       
               
        return this;
    }
    private JSplitPane spLeftPane=null;

      public JSplitPane getLeftSplitPane()
      {
          return spLeftPane;
      }

    public void setSpRight(JSplitPane spRight) {
        this.spRight = spRight;
    }

    private JSplitPane spRight=null;

    public JSplitPane getRightSplitPane()
    {
        return spRight;
    }

    private Component consoleSplitFrame=null;

    public Component getconsoleFrameComponent()
    {
        return consoleSplitFrame;
    }

    private Component alloutputFrames=null;

    public Component getOutputTabFrameComponent()
    {
        return alloutputFrames;
    }

    public Splitter resetRightSplitPane(java.lang.String action)
    {
     if(action==null)return null;
     if(action.equals("show"))
     {

        return createSplitter(frame,jdecstatusFrame,lefttabs,(JTabbedPane)alloutputFrames,splitPaneConsoleFrame);
        //spRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getOutputTabFrameComponent(), getconsoleFrameComponent());
     }
     else if(action.equals("hide"))
     {
         spRight.remove(1);
         spRight.revalidate();
         //spRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getOutputTabFrameComponent(), null);
     }
     //splitPlane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,spLeftPane,spRight);

    return null;


    }




    private void createOtherSplitters()
    {

    	// Without Details




    }

    public void recreateSplitter(ArrayList currentList)
    {

    	if(currentList!=null && currentList.size() > 0)
    	{

    		FileInfoFrame info=null;
    		JdecStatusFrame status=null;
    		JTabbedPane tabs=null;
    		Console console=null;

    		for(int s=0;s<currentList.size();s++)
    		{
    			Object o=currentList.get(s);
    			if(o instanceof FileInfoFrame)
    			{
    				info=(FileInfoFrame)o;
    			}
    			else if(o instanceof JdecStatusFrame)
    			{
    				status=(JdecStatusFrame)o;
    			}
    			else if(o instanceof JdecTree)
    			{
    				tabs=(JTabbedPane)o;
    			}
    			else if(o instanceof Console)
    			{
    				console=(Console)o;
    			}

    		}
    //		if(info!=null)



    	}


    }



}



