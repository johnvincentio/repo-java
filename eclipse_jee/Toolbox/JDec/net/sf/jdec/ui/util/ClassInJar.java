/*
 *  ClassInJar.java Copyright (c) 2006,07 Swaroop Belur 
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.ui.adapter.DecompilerBridge;
import net.sf.jdec.ui.core.Console;
import net.sf.jdec.ui.core.Manager;
import net.sf.jdec.ui.main.UILauncher;
import net.sf.jdec.config.Configuration;

public class ClassInJar {

	
	JFrame frame=null;
	JTextField claxx=null;
	JTextField jar=null;
	JButton choose=null;
	ActionListener al=null;
	JButton ok=null;
	JButton cancel=null;
	JLabel cl=null;
	JLabel cj=null;
	JButton example=null;
	
	public ClassInJar() {
		super();
		
	}
	
	public void start()
	{
		initialize();
		show();
	}

	private void show()
	{
		JPanel c=(JPanel)frame.getContentPane();
		JPanel p1=new JPanel();
		//p1.setLayout(new BoxLayout(p1,BoxLayout.X_AXIS));
		p1.add(cl);
		p1.add(claxx);
		JPanel p2=new JPanel();
		///p2.setLayout(new BoxLayout(p2,BoxLayout.X_AXIS));
		p2.add(cj);
		p2.add(jar);
		p2.add(choose);
		JPanel p3=new JPanel();
		//p3.setLayout(new BoxLayout(p3,BoxLayout.X_AXIS));
		p3.add(ok);
		p3.add(cancel);
		p3.add(example);
		c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
		c.add(p1);//,BorderLayout.NORTH);
		c.add(p2);//,BorderLayout.CENTER);
		c.add(p3);//,BorderLayout.SOUTH);
		
		frame.setBounds(300,300,400,250);
		//frame.pack();
		frame.setResizable(true);
		frame.setVisible(true);
		
	}
	
	private void initialize()
	{
		frame=new JFrame("Class In Jar Task");
		cl=new JLabel("Fully Qualified Class Name:");
		cj=new JLabel("                    Full Path of Jar:");
		claxx=new JTextField();
		claxx.setColumns(25);
		jar=new JTextField();
		jar.setColumns(25);
		choose=new JButton("Choose Jar");
		choose.addActionListener(new l());
		ok=new JButton("Decompile");
		cancel=new JButton("Cancel");
		example=new JButton("Show Example");
		example.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent ae)
					{
						JOptionPane.showMessageDialog(frame,"java/lang/Object.class");
					}
				});
		Process p=new Process();
		ok.addActionListener(p);
		cancel.addActionListener(p);
	}
	
	class Process implements ActionListener
	{
		public void actionPerformed(ActionEvent ae)
		{
			if(ae.getSource()==ok) 
			{
				String clx=claxx.getText();
				if(clx==null || clx.length()==0)return;
				String sjar=jar.getText();
				if(sjar==null || sjar.length()==0)return;
				clx=clx.trim();
				sjar=sjar.trim();
				JFrame tempframes[]=UILauncher.getTempFrames();
				for (int i = 0; i < tempframes.length; i++) {
					JFrame tempframe=tempframes[i];
					if(tempframe!=null)
					{
						tempframe.setVisible(false);
						tempframe.dispose();
					}	
				}
				frame.setVisible(false);
				frame.dispose();
				processClassInJarTask(clx,sjar);
				
			}
			if(ae.getSource()==cancel)
			{
				frame.setVisible(false);
				frame.dispose();
				frame=null;
			}
		}
		
		private void processClassInJarTask(final String clx,final String jar)
		{
			
			
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
                     String p= ConsoleLauncher.getFilePathForClassEntryInJar(new File(jar),clx,null);
                     if(p==null)
                     {
                    	 Manager.getManager().setShowProgressBar(false);
                    	 return;
                     }
                     bridge.setClassFile(p);
                     bridge.setOption("decompileClass"); 
                     Configuration.setDecompileroption("dc");
                     if(c!=null)
                     {
                         JEditorPane rdwr=c.getComponent();
                         String s="Current Task :Decompile Class In Jar\n";
                         s=rdwr.getText()+"\n\n"+s;
                         rdwr.setText(s);
                     }
                     Configuration.setJarSpecified(false);
                     bridge.execute("decompileClass", "", false,"null");
                     UILauncher.getUIutil().addRecentFile(new File(p));
                     if(c!=null)
                     {
                         JEditorPane rdwr=c.getComponent();
                         ConsoleLauncher.setCurrentJarSourceFile(null);
                         Configuration.setJarPath(null);
                         String s=rdwr.getText()+"\n\n"+p;
                         rdwr.setText(s);
                     }
                     bridge.showResult(UIUtil.getUIUtil().getRightTabbedPane());
                     ConsoleLauncher.setCurrentSourceFile(new File(p));
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
			t1.setPriority(Thread.MAX_PRIORITY);
			t2.setPriority(Thread.NORM_PRIORITY);
			t1.start();
			t2.start();
		}
		
		 private void gotoSleep(){
		        try{
		            Thread.sleep(1000);
		            // i++;
		        } catch(InterruptedException e){
		          
		        }
		        // System.out.println(i);

		    }
	}	
	
	
	class l implements ActionListener
	{
		public void actionPerformed(ActionEvent ae)
		{
			JFileChooser chooser=new JFileChooser();
			int opt=chooser.showOpenDialog(frame);
			if(opt==JFileChooser.APPROVE_OPTION)
			{
				File f=chooser.getSelectedFile();
				if(f==null || f.isDirectory()) 
				{
					
				}
				else
				{
			      if(f.getAbsolutePath().endsWith(".jar"))
			    	  jar.setText(f.getAbsolutePath());
			      else
			    	  JOptionPane.showMessageDialog(frame,"Please choose a jar file");
				}
			}
		}
	}
	
	
}
;