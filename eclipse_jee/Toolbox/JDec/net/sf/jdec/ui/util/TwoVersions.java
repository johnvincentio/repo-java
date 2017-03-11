package net.sf.jdec.ui.util;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.ui.adapter.DecompilerBridge;
import net.sf.jdec.ui.core.Console;
import net.sf.jdec.ui.core.Manager;
import net.sf.jdec.ui.core.OutputFrame;
import net.sf.jdec.ui.main.UILauncher;
import net.sf.jdec.config.Configuration;

public class TwoVersions extends JFrame {

	private JLabel classlabel=null;
	private JLabel path1label=null; 
	private JLabel path2label=null;
	private JTextField classtext=null;
	private static JTextField path1text=null;
	private static JTextField path2text=null;
	private JButton path1but=null;
	private JButton path2but=null;
	private JButton process=null;
	private JButton close=null;
	private JCheckBox jar1=null;
	private JCheckBox jar2=null;
	
	
	public TwoVersions() throws HeadlessException {
		super();
		initializeNShow();

	}

	public TwoVersions(GraphicsConfiguration gc) {
		super(gc);
		initializeNShow();
	}

	public TwoVersions(String title) throws HeadlessException {
		super(title);
		initializeNShow();

	}

	public TwoVersions(String title, GraphicsConfiguration gc) {
		super(title, gc);
		initializeNShow();
		UILauncher.addTempFrame(this);
	}
	
	private void initializeNShow()
	{
                class1qualifiedvalue=null;
                class2qualifiedvalue=null;
		classlabel=new JLabel("Enter Class Name                      ");
		path1label=new JLabel("Enter path(Folder) for first class     ");
		path2label=new JLabel("Enter path(Folder) for second class");
		jar1=new JCheckBox("Scan From Jar");
		jar2=new JCheckBox("Scan From Jar");
		classtext=new JTextField();
		classtext.setColumns(25);
		path1text=new JTextField();
		path1text.setColumns(25);
		path2text=new JTextField();
		path2text.setColumns(25);
		path1but=new JButton("...");
		path2but=new JButton("...");
		process=new JButton("Decompile");
		
		close=new JButton("Close");
		close.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent ae){
				setVisible(false);
				dispose();
				
			}
		});
		
		
		JPanel panel1=new JPanel();
		JPanel panel2=new JPanel();
		JPanel panel3=new JPanel();
		
		//panel1.setLayout(new BoxLayout(panel1,BoxLayout.X_AXIS));
		panel1.add(classlabel);
		panel1.add(classtext);
		JButton ex=new JButton("Example");
		ex.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent ae)
			{
				JOptionPane.showMessageDialog(TwoVersions.this,"Object.class");
			}
		});
		panel1.add(ex);
		
		
		JPanel panel4=new JPanel();
		//panel4.setLayout(new BoxLayout(panel4,BoxLayout.X_AXIS));
		panel4.add(path1label);
		panel4.add(path1text);
		panel4.add(path1but);
		
		JPanel panel5=new JPanel();
		panel5.add(this.jar1);
		
		JPanel panel6=new JPanel();
		//panel6.setLayout(new BoxLayout(panel6,BoxLayout.X_AXIS));
		panel6.add(path2label);
		panel6.add(path2text);
		panel6.add(path2but);
		
		JPanel panel7=new JPanel();
		//panel7.setLayout(new BoxLayout(panel7,BoxLayout.X_AXIS));
		panel7.add(this.jar2);
		
		//panel2.setLayout(new BoxLayout(panel2,BoxLayout.Y_AXIS));
		panel2.add(panel4);
		panel2.add(panel5);
		panel2.add(panel6);
		panel2.add(panel7);
		
		
		panel3.setLayout(new BoxLayout(panel3,BoxLayout.X_AXIS));
		panel3.add(process);
		panel3.add(close);
		
		
		Container c=getContentPane();
		//c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
		c.add(panel1,BorderLayout.NORTH);
		c.add(panel2,BorderLayout.CENTER);
		c.add(panel3,BorderLayout.SOUTH);
		
		setBounds(300,300,600,400);
		l l=new l();
		jar1.addActionListener(l);
		jar2.addActionListener(l);
		process.addActionListener(l);
		path1but.addActionListener(l);
		path2but.addActionListener(l);
		setVisible(true);
		setResizable(true);
		//pack();
	}
	
	private static boolean jar1selected=false;
	private static boolean jar2selected=false;
	
	static class jl implements ActionListener
	{
		private static jl obj=null; 
		public static jl getInstance()
		{
			if(obj==null)
			{
				obj=new jl();
				return obj;
			}
			return obj;
		}
		
		public void actionPerformed(ActionEvent ae)
		{
			if(TwoVersions.jar1selected)
			{
				String qn=temp1.getText();
				String jp=temp2.getText();
				jar1pathvalue=jp;
				class1qualifiedvalue=qn;
				tempframe.setVisible(false);
				tempframe.dispose();
				path1text.setText(jp);
				
			}
			
		}
	}
	static class jl2 implements ActionListener
	{
		private static jl2 obj=null; 
		public static jl2 getInstance()
		{
			if(obj==null)
			{
				obj=new jl2();
				return obj;
			}
			return obj;
		}
		
		public void actionPerformed(ActionEvent ae)
		{
			
			if(TwoVersions.jar2selected)
			{
				String qn=temp1.getText();
				String jp=temp2.getText();
				jar2pathvalue=jp;
				class2qualifiedvalue=qn;
				tempframe.setVisible(false);
				tempframe.dispose();
				path2text.setText(jp);
			}
		}
	}
	
	class l implements ActionListener
	{
		public void actionPerformed(ActionEvent ae)
		{
			if(ae.getSource()==path1but)
			{
				jar1selected=false;
				JFileChooser chooser=new JFileChooser(".");
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				int choice=chooser.showOpenDialog(TwoVersions.this);
				if(choice==JFileChooser.APPROVE_OPTION)
				{
					String s=chooser.getSelectedFile().getAbsolutePath();
					if(s!=null && s.trim().length() > 0)
					{
						path1text.setText(s);
					}
				}
				
			}
			if(ae.getSource()==path2but)
			{
				jar2selected=false;
				JFileChooser chooser=new JFileChooser(".");
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				int choice=chooser.showOpenDialog(TwoVersions.this);
				if(choice==JFileChooser.APPROVE_OPTION)
				{
					String s=chooser.getSelectedFile().getAbsolutePath();
					if(s!=null && s.trim().length() > 0)
					{
						path2text.setText(s);
					}
				}
			}
			if(ae.getSource()==jar1)
			{
				jar1selected=true;
				tempframe=new JFrame();
				temp1=new JTextField();
				temp1.setColumns(25);
				JLabel l1=new JLabel("Fully Qualified Class Name");
				JLabel l2=new JLabel("Full  Path of Jar         ");
				temp2=new JTextField();
				temp2.setColumns(25);
                                if(class2qualifiedvalue!=null){
                                    temp1.setText(class2qualifiedvalue);
                                }
				JButton b2=new JButton("...");
				JButton b1=new JButton("Example");
				JPanel p1=new JPanel();
				JPanel p2=new JPanel();
				JPanel p3=new JPanel();
				p1.add(l1);
				p1.add(temp1);
				p1.add(b1);
				
				p2.add(l2);
				p2.add(temp2);
				p2.add(b2);
				
				
				
				JButton ok=new JButton("Ok");
				JButton cancel=new JButton("Cancel");
				
				p3.add(ok);
				p3.add(cancel);
				
				tempframe.getContentPane().add(p1,BorderLayout.NORTH);	
				tempframe.getContentPane().add(p2,BorderLayout.CENTER);
				tempframe.getContentPane().add(p3,BorderLayout.SOUTH);
				tempframe.setBounds(300,300,540,300);
				tempframe.setVisible(true);
				jl obj=jl.getInstance();
				ok.addActionListener(obj);
				b2.addActionListener(new ActionListener(){
					
					public void actionPerformed(ActionEvent ae)
					{
						JFileChooser fc=new JFileChooser(".");
						fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
						int choice=fc.showOpenDialog(tempframe);
						if(choice==JFileChooser.APPROVE_OPTION)
						{
							File file=fc.getSelectedFile();
							temp2.setText(file.getAbsolutePath());
						}
					}
					
				});
				
				b1.addActionListener(new ActionListener(){
					
					public void actionPerformed(ActionEvent ae)
					{
						JOptionPane.showMessageDialog(tempframe,"java/lang/Object.class");
					}
				});
				
				cancel.addActionListener(new ActionListener(){
					
					public void actionPerformed(ActionEvent ae)
					{
						tempframe.setVisible(false);
						tempframe.dispose();
					}
				});
	
				
			}
			else if(ae.getSource()==jar2)
			{
				
				jar2selected=true;
				tempframe=new JFrame();
				temp1=new JTextField();
				temp1.setColumns(25);
				JLabel l1=new JLabel("Fully Qualified Class Name");
				JLabel l2=new JLabel("Full  Path of Jar         ");
				temp2=new JTextField();
				temp2.setColumns(25);
                                if(class1qualifiedvalue!=null){
                                    temp1.setText(class1qualifiedvalue);
                                }
				JButton b2=new JButton("...");
				JButton b1=new JButton("Example");
				JPanel p1=new JPanel();
				JPanel p2=new JPanel();
				JPanel p3=new JPanel();
				p1.add(l1);
				p1.add(temp1);
				p1.add(b1);
				
				p2.add(l2);
				p2.add(temp2);
				p2.add(b2);
				
				
				
				JButton ok=new JButton("Ok");
				JButton cancel=new JButton("Cancel");
				
				p3.add(ok);
				p3.add(cancel);
				
				jl2 obj=jl2.getInstance();
				ok.addActionListener(obj);
				
				tempframe.getContentPane().add(p1,BorderLayout.NORTH);	
				tempframe.getContentPane().add(p2,BorderLayout.CENTER);
				tempframe.getContentPane().add(p3,BorderLayout.SOUTH);
				tempframe.setBounds(300,300,540,300);
				tempframe.setVisible(true);
				
				b2.addActionListener(new ActionListener(){
					
					public void actionPerformed(ActionEvent ae)
					{
						JFileChooser fc=new JFileChooser(".");
						fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
						int choice=fc.showOpenDialog(tempframe);
						if(choice==JFileChooser.APPROVE_OPTION)
						{
							File file=fc.getSelectedFile();
							temp2.setText(file.getAbsolutePath());
						}
					}
					
				});
				
				b1.addActionListener(new ActionListener(){
					
					public void actionPerformed(ActionEvent ae)
					{
						JOptionPane.showMessageDialog(tempframe,"java/lang/Object.class");
					}
				});
				
				cancel.addActionListener(new ActionListener(){
					
					public void actionPerformed(ActionEvent ae)
					{
						tempframe.setVisible(false);
						tempframe.dispose();
					}
				});
				
			}
			else if(ae.getSource()==process)
			{
				if(jar1selected && jar2selected)
				{
					String s1=path1text.getText();
					String s2=path2text.getText();
					File f1=new File(s1);
					File f2=new File(s2);
					if(f1.exists()==false || f1.isDirectory()==true)
					{
						JOptionPane.showMessageDialog(TwoVersions.this,"Please check first version path");
						return;
					}
					if(f2.exists()==false || f2.isDirectory()==true)
					{
						JOptionPane.showMessageDialog(TwoVersions.this,"Please check second version path");
						return;
					}
				}
				else if(!jar1selected && !jar2selected)
				{
					String classname=classtext.getText();
					if(classname==null || classname.trim().length()==0)
					{
						JOptionPane.showMessageDialog(TwoVersions.this,"Please enter class name in first textfield");
						return;
						
					}
					else
					{
						String s1=path1text.getText();
						String s2=path2text.getText();
						if(s1==null || s1.length()==0 ||  s2==null || s2.trim().length()==0)
						{
							JOptionPane.showMessageDialog(TwoVersions.this,"Please enter path for class File");
							return;
						}
						
					}
					String s1=path1text.getText();
					String s2=path2text.getText();
					File f1=new File(s1);
					File f2=new File(s2);
					if(f1.exists()==false || f1.isDirectory()==false)
					{
						JOptionPane.showMessageDialog(TwoVersions.this,"Please check first version path");
						return;
					}
					if(f2.exists()==false || f2.isDirectory()==false)
					{
						JOptionPane.showMessageDialog(TwoVersions.this,"Please check second version path");
						return;
					}
				}
				else if(!jar1selected && jar2selected)
				{
					String s1=path1text.getText();
					File f1=new File(s1);
					String s2=path2text.getText();
					File f2=new File(s2);
					if(f1.exists()==false || f1.isDirectory()==false)
					{
						JOptionPane.showMessageDialog(TwoVersions.this,"Please check first version path");
						return;
					}
					if(f2.exists()==false || f2.isDirectory()==true)
					{
						JOptionPane.showMessageDialog(TwoVersions.this,"Please check second version path");
						return;
					}
					String classname=classtext.getText();
					if(classname==null || classname.trim().length()==0)
					{
						JOptionPane.showMessageDialog(TwoVersions.this,"Please enter class name in first textfield");
						return;
						
					}
					
					
				}
				else if(jar1selected && !jar2selected)
				{
					String s1=path1text.getText();
					File f1=new File(s1);
					String s2=path2text.getText();
					File f2=new File(s2);
					if(f1.exists()==false || f1.isDirectory()==true)
					{
						JOptionPane.showMessageDialog(TwoVersions.this,"Please check first version path");
						return;
					}
					if(f2.exists()==false || f2.isDirectory()==false)
					{
						JOptionPane.showMessageDialog(TwoVersions.this,"Please check second version path");
						return;
					}
					String classname=classtext.getText();
					if(classname==null || classname.trim().length()==0)
					{
						JOptionPane.showMessageDialog(TwoVersions.this,"Please enter class name in first textfield");
						return;
						
					}
					
				}
				if(jar1selected)
				{
					if(class1qualifiedvalue==null || class1qualifiedvalue.trim().length()==0)
					{
						if(jar2selected && (class2qualifiedvalue==null || class2qualifiedvalue.trim().length()==0))
						{
							JOptionPane.showMessageDialog(TwoVersions.this,"Please enter fully qualified name for class(first version)");
							return;
						}
						if(!jar2selected)
						{
							JOptionPane.showMessageDialog(TwoVersions.this,"Please enter fully qualified name for class(first version)");
							return;
						}
						if(jar2selected)
							class1qualifiedvalue=class2qualifiedvalue;
					}
				}
				if(jar2selected)
				{
					if(class2qualifiedvalue==null || class2qualifiedvalue.trim().length()==0)
					{
						if(jar1selected && (class1qualifiedvalue==null || class1qualifiedvalue.trim().length()==0))
						{
							JOptionPane.showMessageDialog(TwoVersions.this,"Please enter fully qualified name for class(second version)");
							return;
						}
						if(!jar1selected)
						{
							JOptionPane.showMessageDialog(TwoVersions.this,"Please enter fully qualified name for class(second version)");
							return;
						}
						if(jar1selected)
							class2qualifiedvalue=class1qualifiedvalue;
					}
				}
				File f1=null;
				File f2=null;
				String p1=null;
				String p2=null;
				String source1=null;
				String source2=null;
				try {
					if(jar1selected && jar2selected)
					{
						 p1= ConsoleLauncher.getFilePathForClassEntryInJar(new File(jar1pathvalue),class1qualifiedvalue,"version1");
						 if(p1==null)
						 {
							 throw new IOException("Error while creating File for first version");
						 }
						 p2= ConsoleLauncher.getFilePathForClassEntryInJar(new File(jar2pathvalue),class2qualifiedvalue,"version2");
						 if(p2==null)
						 {
							 throw new IOException("Error while creating File for second version");
						 }
						 source1=jar1pathvalue;
						 source2=jar2pathvalue;
						
					}
					if(jar1selected && !jar2selected)
					{
						 p1= ConsoleLauncher.getFilePathForClassEntryInJar(new File(jar1pathvalue),class1qualifiedvalue,"version1");
						 if(p1==null)
						 {
							 throw new IOException("Error while creating File for first version");
						 }
						 p2=path2text.getText();
						 source1=jar1pathvalue;
						 source2=p2;
						 if(p2.endsWith(File.separator)==false)
							 p2+=File.separator+classtext.getText();
						 else
							 p2+=classtext.getText();
						 
						
						
					}
					if(!jar1selected && jar2selected)
					{
						 p2= ConsoleLauncher.getFilePathForClassEntryInJar(new File(jar2pathvalue),class2qualifiedvalue,"version2");
						 if(p2==null)
						 {
							 throw new IOException("Error while creating File for second version");
						 }
						 p1=path1text.getText();
						 source1=p1;
						 source2=jar2pathvalue;
						 if(p1.endsWith(File.separator)==false)
							 p1+=File.separator+classtext.getText();
						 else
							 p1+=classtext.getText();
						 
						 
						 
						
					}
					if(!jar1selected && !jar2selected)
					{
						 p1=path1text.getText();
						 source1=p1;
						 if(p1.endsWith(File.separator)==false)
							 p1+=File.separator+classtext.getText();
						 else
							 p1+=classtext.getText();
						 
						 p2=path2text.getText();
						 source2=p2;
						 if(p2.endsWith(File.separator)==false)
							 p2+=File.separator+classtext.getText();
						 else
							 p2+=classtext.getText();
						
					}
				} catch (IOException e) {
					if(e.getMessage()!=null && e.getMessage().length() > 0)
						JOptionPane.showMessageDialog(TwoVersions.this,e.getMessage());
					
				}
				
				if(p1==null || (new File(p1)).exists()==false)
				{
					JOptionPane.showMessageDialog(TwoVersions.this,"Please check First version file");
					return;
				}
				if(p2==null || (new File(p2)).exists()==false)
				{
					JOptionPane.showMessageDialog(TwoVersions.this,"Please check Second version file");
					return;
				}
				
			    execute(p1,p2,source1,source2); 
				
			}
			
			
		}
		
		private void execute(final String p1,final String p2,final String src1,final String src2)
		{
			UILauncher.addTempFrame(TwoVersions.this);
			JFrame tempframes[]=UILauncher.getTempFrames();
			for (int i = 0; i < tempframes.length; i++) {
				tempframe=tempframes[i];
				if(tempframe!=null)
				{
					tempframe.setVisible(false);
					tempframe.dispose();
				}	
			}
			
			 Thread t1=new Thread(){

                 public void run()
                 {
                	 Manager.getManager().setVersionprogressbar(true);
                	 Manager.getManager().setShowProgressBar(false);
                     Console c=UILauncher.getUIutil().getConsoleFrame();
                     if(c!=null)
                     {
                         JEditorPane rdwr=c.getComponent();
                         rdwr.setText("");
                         String s="Jdec Started..\n";
                         s=rdwr.getText()+"\n\n"+s;
                         rdwr.setText(s);
                     }
                     
                     //System.out.println("Hello");
                     
                     DecompilerBridge bridge=DecompilerBridge.getInstance(UIUtil.getUIUtil());
                     bridge.setClassFile(p1);
                     bridge.setOption("decompileClass"); 
                     Configuration.setDecompileroption("dc");
                     if(c!=null)
                     {
                         JEditorPane rdwr=c.getComponent();
                         String s="Current Task :Decompile 2 versions of Class \n";
                         s=rdwr.getText()+"\n\n"+s;
                         rdwr.setText(s);
                     }
                     Configuration.setJarSpecified(false);
                     String msg1="Processing version1 class File";
                     String msg2=p1;
                     UIUtil.versionproframedetails_class.setText(msg1);
                     UIUtil.versionproframedetails_method.setText(msg2);
                     try {
 						Thread.sleep(2000);
 					} catch (InterruptedException e) {
 					}
                     Manager.getManager().setShowProgressBar(false);
                    // System.out.println("Hello1");
                     bridge.execute("decompileClass", "", false,"null");
                     //System.out.println("Hello2");
                     if(c!=null)
                     {
                         JEditorPane rdwr=c.getComponent();
                         ConsoleLauncher.setCurrentJarSourceFile(null);
                         Configuration.setJarPath(null);
                         String s=rdwr.getText()+"\n\n"+p1;
                         rdwr.setText(s);
                     }
                     UIUtil.versionproframedetails_class.setText("Version1 processing complete...");
                     String version1text= ConsoleLauncher.currentdecompiledtext;
                     //System.out.println("version1text");
                     //System.out.println(version1text);
                     try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
                     msg1="Processing version2 class File";
                     msg2=p2;
                     UIUtil.versionproframedetails_class.setText(msg1);
                     UIUtil.versionproframedetails_method.setText(msg2);
                     bridge.setClassFile(p2);
                     bridge.setOption("decompileClass"); 
                     Configuration.setDecompileroption("dc");
                     if(c!=null)
                     {
                         JEditorPane rdwr=c.getComponent();
                         String s="Current Task :Decompile 2 versions of Class \n";
                         s=rdwr.getText()+"\n\n"+s;
                         rdwr.setText(s);
                     }
                     Configuration.setJarSpecified(false);
                     Manager.getManager().setShowProgressBar(false);
                     //System.out.println("Hello3");
                     bridge.execute("decompileClass", "", false,"null");
                     //System.out.println("Hello4");
                     if(c!=null)
                     {
                         JEditorPane rdwr=c.getComponent();
                         ConsoleLauncher.setCurrentJarSourceFile(null);
                         Configuration.setJarPath(null);
                         String s=rdwr.getText()+"\n\n"+p1;
                         rdwr.setText(s);
                     }
                     UIUtil.versionproframedetails_class.setText("Version2 processing complete...");
                     java.lang.String version2text= ConsoleLauncher.currentdecompiledtext;
                     //System.out.println("version2text");
                     //System.out.println(version2text);
                     if(version1text.length() > 0 && version2text.length() > 0)
                     {	 
                    	 Manager.getManager().setVersionprogressbar(false);
                         //System.out.println("Hello6");
	                     OutputFrame version1op=new OutputFrame(UILauncher.getObserver(),"","style","version1");
	                     OutputFrame version2op=new OutputFrame(UILauncher.getObserver(),"","style","version2");
                             // Render output Now
                             JScrollPane ver1sp=(JScrollPane)version1op.getComponent();
                             JEditorPane ver1ep=(JEditorPane)ver1sp.getViewport().getView();
                             JScrollPane ver2sp=(JScrollPane)version2op.getComponent();
                             JEditorPane ver2ep=(JEditorPane)ver2sp.getViewport().getView();
                             ver1ep.setText(version1text);
                             ver2ep.setText(version2text);
                             // End
                             
                             //System.out.println("Hello7");
	                     JTabbedPane rt=UIUtil.getUIUtil().getRightTabbedPane();
	                     int index=rt.indexOfTab("Version1");
	         			 if(index!=-1)rt.remove(index);
	         			 index=rt.indexOfTab("Version2");
	         			 if(index!=-1)rt.remove(index);
	                     //rt.addTab("Version1",version1op.getComponent());
	                     //rt.addTab("Version2",version2op.getComponent());
                             JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, version1op.getComponent(), version2op.getComponent());
                             split.setDividerLocation(0.5);
                             split.setDividerSize(5);
                             split.setResizeWeight(0.5);
                             int difftab=rt.indexOfTab("Diff View");
                             if(difftab!=-1)
                             {
                                 rt.remove(difftab);
                             }
                             rt.addTab("Diff View",split);
                             rt.setSelectedComponent(split);
	                     JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Left Pane.->Version1: ["+src1+"]\nRight Pane. ->Version2: ["+src2+"]\n\n");
                             
                            
                             /*System.out.println("*************************************************");
                             printLine(version1lines);
                             System.out.println("1!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                                                         
                             System.out.println("*************************************************");
                             printLine(version2lines);
                             System.out.println("2!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");*/
                             
                             
                     }
                     else
                     {
                    	 String msg="";
                    	 if(version1text.length() <= 0)
                    	 {
                    		 msg+="Error Handling First Version File";
                    	 }
                    	 if(version2text.length() <= 0)
                    	 {
                    		 if(msg.length() > 0)
                    			 msg+="\nError Handling Second Version File";
                    		 else
                    			 msg+="Error Handling Second Version File";
                    	 }
                    	 Manager.getManager().setVersionprogressbar(false);
                    	 JOptionPane.showMessageDialog(UILauncher.getMainFrame(),msg);
                     }
                    
                     
                     
                 }
             };
			Thread t2=new Thread()
			{
				
				public void run()
				{
					
					
					JDialog proframe=UIUtil.launchVersionProgressBarFrame();
					proframe.setVisible(true);
					while(Manager.getManager().isVersionprogressbar()==true)
					{
						
						gotoSleep();
					}
					
					if(!Manager.getManager().isVersionprogressbar())
					{
						proframe.setVisible(false);
						proframe.dispose();
						proframe=null;
						if(ConsoleLauncher.fatalErrorOccured)
                        	JOptionPane.showMessageDialog(UILauncher.getMainFrame(),UIConstants.jdecTaskError,"Jdec Status",JOptionPane.INFORMATION_MESSAGE);
					}
					
					
				}
				
			};
			t1.setPriority(Thread.MAX_PRIORITY);
			t2.setPriority(Thread.NORM_PRIORITY);
			Manager.getManager().setVersionprogressbar(true);
			t2.start();
			t1.start();
			
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
	
	
	public static String jar1pathvalue=null;
	public static String jar2pathvalue=null;
	public static String class1qualifiedvalue=null;
	public static String class2qualifiedvalue=null;
	static JTextField temp1;
	static JTextField temp2;
	static JFrame tempframe;
        
          private void printLine(ArrayList lines)
    {
	for(int c=0;c<lines.size();c++){
          ArrayList al=((ArrayList)lines.get(c));
	String s[]=(String[])al.toArray(new String[al.size()]);
	StringBuffer temp=new StringBuffer();
	for (int i = 0; i < s.length; i++) {
	    temp.append(s[i]);
	}
	
        }
    }
}
