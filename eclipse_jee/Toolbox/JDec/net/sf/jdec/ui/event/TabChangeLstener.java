

/*
 * TabChangeListener.java Copyright (c) 2006,07 Swaroop Belur 
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
package net.sf.jdec.ui.event;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.ui.adapter.DecompilerBridge;
import net.sf.jdec.ui.core.JdecTree;
import net.sf.jdec.ui.core.Manager;
import net.sf.jdec.ui.main.UILauncher;
import net.sf.jdec.ui.util.LogWriter;
import net.sf.jdec.ui.util.UIConstants;
import net.sf.jdec.ui.util.UIUtil;
import net.sf.jdec.config.Configuration;


public class TabChangeLstener implements ChangeListener {

	public void stateChanged(ChangeEvent e) {
		try
		{
		
		final JTabbedPane rightTab=getRightTabbedPane();
		if(e.getSource()==rightTab) 
		{
			Component c=rightTab.getSelectedComponent();
			
			int index=rightTab.getSelectedIndex();
			String title=rightTab.getTitleAt(index);
			String opt= Configuration.getDecompileroption();
		
			
			if(title.equals("Disassembled Output"))
			{
				
				final File f= ConsoleLauncher.getCurrentSourceFile();
				if(f!=null)
				{
					//Writer outputwriter=Writer.getWriter("output");
					//outputwriter.close("output");
					Thread t1=new Thread()
					{
						
						public void run()	{
								DecompilerBridge bridge=DecompilerBridge.getInstance(UIUtil.getUIUtil());
								bridge.setClassFile(f.getAbsolutePath());
								Manager.getManager().setShowProgressBar(true);
								bridge.execute("disassemble", "", false,"null");
								bridge.showResult(rightTab);
                            resetTabs();
						}
					};
					Thread t2 = new Thread() {

						public void run() {


							JDialog proframe=UIUtil.launchProgressBarFrame();
                             if(proframe!=null){
                            proframe.setVisible(true);
                             }
                            //proframe.requestFocusInWindow();


                                while(Manager.getManager().isShowProgressBar()==true)
                                {

                                    gotoSleep();
                                }

                                if(!Manager.getManager().isShowProgressBar())
                                {
                                  if(proframe!=null){
                                    proframe.setVisible(false);
                                    proframe.dispose();
                                  }
                                    proframe=null;
                                    if(ConsoleLauncher.fatalErrorOccured)
                                    	JOptionPane.showMessageDialog(UILauncher.getMainFrame(),UIConstants.jdecTaskError,"Jdec Status",JOptionPane.INFORMATION_MESSAGE);
                                }


						}

					};
					t1.setPriority(Thread.MAX_PRIORITY);
					t2.setPriority(Thread.NORM_PRIORITY);
					Manager.getManager().setStatusThread(t1);
                    if(UIUtil.checkForInvalidEntries(""))
                    {
                    	t1.setPriority(Thread.MAX_PRIORITY);
                    	t2.setPriority(Thread.NORM_PRIORITY);
                        t1.start();
                        t2.start();
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Please Check The Decompiler Configuration Property Values Again","JDEC WARNING",JOptionPane.INFORMATION_MESSAGE);
                    }
					
					
				}
				
			}
			else if(title.equals("Decompiled Output"))
			{
				
				final File f= ConsoleLauncher.getCurrentSourceFile();
				if(f!=null)
				{
					//Writer outputwriter=Writer.getWriter("output");
					//outputwriter.close("output");
					Thread t1=new Thread()
					{
						
						public void run()	{
								Manager.getManager().setShowProgressBar(true);	
								DecompilerBridge bridge=DecompilerBridge.getInstance(UIUtil.getUIUtil());
								bridge.setClassFile(f.getAbsolutePath());
								bridge.execute("decompileClass", "", false,"null");
								bridge.showResult(rightTab);
                                resetTabs();

						}
					};
					
					Thread t2 = new Thread() {

						public void run() {


							JDialog proframe=UIUtil.launchProgressBarFrame();
                             if(proframe!=null){
                            proframe.setVisible(true);
                             }
                            //proframe.requestFocusInWindow();


                                while(Manager.getManager().isShowProgressBar()==true)
                                {

                                    gotoSleep();
                                }

                                if(!Manager.getManager().isShowProgressBar())
                                {
                                  if(proframe!=null){
                                    proframe.setVisible(false);
                                    proframe.dispose();
                                  }
                                    proframe=null;
                                    if(ConsoleLauncher.fatalErrorOccured)
                                    	JOptionPane.showMessageDialog(UILauncher.getMainFrame(),UIConstants.jdecTaskError,"Jdec Status",JOptionPane.INFORMATION_MESSAGE);
                                }
							

						}

					};
					
					boolean b= fireOnTabChange(f.getAbsolutePath());
                    if(b)
                    {
                        if(UIUtil.checkForInvalidEntries(""))
                        {
                        	t1.setPriority(Thread.MAX_PRIORITY);
                        	t2.setPriority(Thread.NORM_PRIORITY);
                            t1.start();
                        	t2.start();
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Please Check The Decompiler Configuration Property Values Again","JDEC WARNING",JOptionPane.INFORMATION_MESSAGE);
                        }
                    }


				}
			}
			else if(title.equals("Combined Output"))
			{
				
			}
				
			
		}
		
		}
		catch(Exception ioe)
		{
			// ignore
		}
		
	}
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
	               Component c=tabs.getComponent(0);
				   if((c instanceof JdecTree)==false)
				   {
		               if(tabs.getTabCount() > 2)
		               {
		                   break;
		               }
				   }

	           }
	       }

		return tabs;
	}


	private void gotoSleep() {
		try {
			Thread.sleep(1000);
			
		} catch (InterruptedException e) {
			LogWriter l=LogWriter.getInstance();
			e.printStackTrace(l.getPrintWriter());
		}


	}
	private static TabChangeLstener listener;
	private static Object lock;
	static
	{
		lock=new Object();
	}
	public static TabChangeLstener getListener()
	{
		if(listener!=null)
		{
			return listener;
		}
		else
		{
			synchronized(lock)
			{
				listener=new TabChangeLstener();
				return listener;
			}
		}
	
	}
	
	private TabChangeLstener()
	{
		
	}

    private boolean fireOnTabChange(java.lang.String path)
    {
        ArrayList list= ConsoleLauncher.getTabChangeList();
        if(list!=null && list.size() > 0)
        {
            java.lang.String p=(java.lang.String)list.get(0);
            if(p.equals(path))
            {
                return false;
            }
        }
    return true;    
    }

    private void resetTabs()
    {
             JTabbedPane tabs=getRightTabbedPane();
			int index=tabs.indexOfTab("Jdec Configuration");
			if(index!=-1)tabs.remove(index);
			index=tabs.indexOfTab("Constant Pool");
			if(index!=-1)tabs.remove(index);
			index=tabs.indexOfTab("Class Details");
			if(index!=-1)tabs.remove(index);
			index=tabs.indexOfTab("Exception Tables");
			if(index!=-1)tabs.remove(index);
			index=tabs.indexOfTab("Local Variables");
			if(index!=-1)tabs.remove(index);
			index=tabs.indexOfTab("System Properties");
			if(index!=-1)tabs.remove(index);
			index=tabs.indexOfTab("Jdec Help");
			if(index!=-1)tabs.remove(index);
			index=tabs.indexOfTab("Skeleton Output");
			if(index!=-1)tabs.remove(index);
			index=tabs.indexOfTab("UILog Output");
			if(index!=-1)tabs.remove(index);
			index=tabs.indexOfTab("DecompilerLog Output");
			if(index!=-1)tabs.remove(index);
    }


}
