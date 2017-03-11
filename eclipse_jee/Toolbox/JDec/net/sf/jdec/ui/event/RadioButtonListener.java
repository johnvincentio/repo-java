
/*
 *  RadioButtonListener.java Copyright (c) 2006,07 Swaroop Belur 
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.ui.adapter.DecompilerBridge;
import net.sf.jdec.ui.core.Console;
import net.sf.jdec.ui.core.JdecTree;
import net.sf.jdec.ui.core.Manager;
import net.sf.jdec.ui.main.UILauncher;
import net.sf.jdec.ui.util.ClassFileFilter;
import net.sf.jdec.ui.util.FileOpener;
import net.sf.jdec.ui.util.UIConstants;
import net.sf.jdec.ui.util.UIUtil;
import net.sf.jdec.config.Configuration;

public class RadioButtonListener implements ActionListener {
	
	private void gotoSleep(){
		try{
			Thread.sleep(1000);
			// i++;
		} catch(InterruptedException e){
			
		}
		// System.out.println(i);
		
	}
	public void actionPerformed(ActionEvent event)
	{
		
		if(event.getActionCommand().equals("Decompile Class File"))
		{
			
			FileOpener chooser=new FileOpener(new ClassFileFilter());
			Manager.getManager().setShowProgressBar(true);
			//Thread main=Thread.currentThread();
			 JFrame tempframes[]=UILauncher.getTempFrames();
 			for (int i = 0; i < tempframes.length; i++) {
 				JFrame tempframe=tempframes[i];
 				if(tempframe!=null)
 				{
 					tempframe.setVisible(false);
 					tempframe.dispose();
 				}	
 			}
			final File chosenFile=chooser.getSelectedFile();
			//System.out.println("XXX"+chosenFile.getAbsolutePath());
			
			
			Thread t1=new Thread()
			{
				
				public void run()	{
					
					decompileClass(chosenFile);
					ConsoleLauncher.setFileDecompiled(true);
					ConsoleLauncher.setCurrentSourceFile(chosenFile);  // Why is this being set after call ? Add note
					
					
				}
				
			};
			
			
			
			Thread t2=new Thread()
			{
				
				public void run()	{



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
			t1.start();
			t2.start();
			
			
			
			
			
		}
		if(event.getActionCommand().equals("Decompile Jar File"))
		{
			try
			{
				
				final FileOpener chooser=new FileOpener(null);
				 JFrame tempframes[]=UILauncher.getTempFrames();
     			for (int i = 0; i < tempframes.length; i++) {
     				JFrame tempframe=tempframes[i];
     				if(tempframe!=null)
     				{
     					tempframe.setVisible(false);
     					tempframe.dispose();
     				}	
     			}
				String message="Jar Tab Has been Updated...\n";
				message+="Would You like Jdec to Decompile Entire Jar ...\n\n";
				message+="[Warning:] This May take some time depending on size of Jar ...\n";
				message+="Would You Like to Proceed...?\n";
				
				
				JOptionPane option=new JOptionPane();//message,JOptionPane.QUESTION_MESSAGE,JOptionPane.YES_NO_CANCEL_OPTION);
				Object[] options = { "OK", "CANCEL" };
				int opt=option.showOptionDialog(UILauncher.getMainFrame(),message, "Jar Decompile Option", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				option.setVisible(true);
				if(opt==JOptionPane.OK_OPTION)
				{
					JTabbedPane tab=getLeftTabbedPane();
					tab.setSelectedIndex(1);
					//Writer outputwriter=Writer.getWriter("output");
					//outputwriter.close("output");
					///
					Thread t1=new Thread(){
						
						public void run()	
						{
							Console c=UILauncher.getUIutil().getConsoleFrame();
							if(c!=null)
							{
								JEditorPane rdwr=c.getComponent();
								rdwr.setText("");
								String s="Jdec Started..\n";
								s=rdwr.getText()+"\n\n"+s;
								rdwr.setText(s);
							}
							Manager.getManager().setShowProgressBar(true);
							DecompilerBridge bridge=DecompilerBridge.getInstance(UIUtil.getUIUtil());
							//bridge.setClassFile(chooser.getSelectedFile().getAbsolutePath());
							if(c!=null)
							{
								JEditorPane rdwr=c.getComponent();
								
								String s="Current Task :Decompile Jar\n";
								s=rdwr.getText()+"\n\n"+s;
								rdwr.setText(s);
							}
							bridge.execute("decompileJar", "", false,"null");
							if(c!=null)
							{
								JEditorPane rdwr=c.getComponent();
								
								String s="Current File :"+ Configuration.getJarPath()+"\n";
								// Reinitialize current executed files....
								ConsoleLauncher.setCurrentJarSourceFile(null);
								Configuration.setJarPath(null);

								s=rdwr.getText()+"\n\n"+s;
								rdwr.setText(s);
							}
							bridge.showResult(getRightTabbedPane());
							if(c!=null)
							{
								JEditorPane rdwr=c.getComponent();
								
								String s="Done...Please Wait for UI to render the output....\nPlease Check up the log files for any error(s)";
								s=rdwr.getText()+"\n\n"+s;
								rdwr.setText(s);
							}
							
						}
					};
				
				
					Thread t2=new Thread()
									{
										
										public void run()	
										{



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
				
					
					///
					if(chooser.getSelectedFile().getAbsolutePath().endsWith(".jar")){
					ConsoleLauncher.setCurrentJarSourceFile(chooser.getSelectedFile());
					UILauncher.getUIutil().setJarOption(true);	
					t1.start();
					t2.start();
					}
					else
					{
						JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Chosen File is not a jar file","Jar Option",JOptionPane.INFORMATION_MESSAGE);
						
					}
					
					
				}
				
				JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Jar Tab has been updated with directory containing Decompiled Files..\nPlease Click on any File to see the output...","User Info...",JOptionPane.INFORMATION_MESSAGE);
			}
			catch(Exception jarexcep)
			{
				// Handle todo
			}
			
		}
		
		else
		{
			JTabbedPane tab=getLeftTabbedPane();
			tab.setSelectedIndex(tab.indexOfTab("Jar"));
		}
		
		
		
		
	}
	int m_counter;
	


private void decompileClass(File f)
{
	
	DecompilerBridge bridge=DecompilerBridge.getInstance(UIUtil.getUIUtil());
	bridge.setClassFile(f.getAbsolutePath());
	bridge.execute("decompileClass", "", false,"null");
	JTabbedPane rightTab=getRightTabbedPane();
	bridge.showResult(rightTab);
	ConsoleLauncher.setCurrentDecompiledFile(Configuration.getJavaClassFile());
	//Configuration.setJavaClassFile(null);
	//Configuration.setClassFilePath(null);
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
			if(tabs.getTabCount() > 2)
			{
				break;
			}
			
		}
	}
	
	return tabs;
}
public JTabbedPane getLeftTabbedPane()
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
			if(c instanceof JdecTree)
			{
			
				tabs=(JTabbedPane)current;
				break;
			}
			
		}
	}
	
	return tabs;
}


}
