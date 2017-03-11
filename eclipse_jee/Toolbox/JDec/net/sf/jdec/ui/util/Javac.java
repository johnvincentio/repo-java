
/*
 * Javac.java Copyright (c) 2006,07 Swaroop Belur 
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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.sf.jdec.ui.main.UILauncher;



public class Javac extends JDialog implements ActionListener {

	


		public Javac( JFrame frame ) {
			
			super( frame, true );
			setTitle( "Compile Java" );
			setSize( 500, 300 );
			setBounds(100,100,500,300);
			

			JPanel panel = new JPanel( new BorderLayout() );
			panel.setLayout(new GridBagLayout() );

			panel.setBorder( new EmptyBorder( new Insets( 5, 5, 5, 5 ) ) );
			getContentPane().add( BorderLayout.CENTER, panel );
			GridBagConstraints c = new GridBagConstraints();

			Dimension shortField = new Dimension( 40, 20 );
			Dimension mediumField = new Dimension( 120, 20 );
			Dimension longField = new Dimension( 240, 20 );
			Dimension hugeField = new Dimension( 240, 80 );

			EmptyBorder border = new EmptyBorder( new Insets( 0, 0, 0, 10 ) );
			EmptyBorder border1 = new EmptyBorder( new Insets( 0, 20, 0, 10 ) );

			c.insets = new Insets( 2, 2, 2, 2 );

			c.anchor = GridBagConstraints.WEST;
			JLabel lbl1 = new JLabel( "File Path" );
			lbl1.setBorder( border );
			panel.add( lbl1, c );
			JTextField txt1 = new JTextField();
			txt1.setActionCommand("filepath");
			txt1.addActionListener(this);
			
			txt1.setPreferredSize( longField );
			c.gridx = 1;
			c.weightx = 1.0; 
			c.gridwidth = 3; 
			c.fill = GridBagConstraints.HORIZONTAL; 
			panel.add( txt1, c );
			setFilePathRef(txt1);
			JLabel lbl2 = new JLabel( "Classpath" );
			lbl2.setBorder( border );
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = 1;;
			c.weightx = 0.0;
			panel.add( lbl2, c );
			JTextField classpath = new JTextField();
			classpath.setPreferredSize( longField );
			c.gridx = 1;
			c.weightx = 1.0;
			c.gridwidth = 3;
			c.fill = GridBagConstraints.HORIZONTAL;
			panel.add( classpath, c );
			setClassPathTextFieldRef(classpath);
			JLabel lbl3 = new JLabel( "Enable Debug Flag" );
			lbl3.setBorder( border );
			c.gridx = 0;
			c.gridy = 3;
			c.gridwidth = 1;
			c.gridheight = 1;
			c.weightx = 0.0;
			c.weighty = 0.0;
			c.fill = GridBagConstraints.NONE;
			panel.add( lbl3, c );
			JComboBox combo3 = new JComboBox();

			combo3.insertItemAt("No", 0);
			combo3.insertItemAt("Yes", 1);
			combo3.setActionCommand("debug");
			combo3.addActionListener(this);
			c.gridx=1;
			panel.add(combo3,c);
			JPanel radioPanel = new JPanel();
			radioPanel.setLayout( new FlowLayout( FlowLayout.LEFT, 5, 0 ) );
			ButtonGroup group = new ButtonGroup();
			JRadioButton radio1 = new JRadioButton( "Output to File" );
			group.add( radio1 );
			JRadioButton radio2 = new JRadioButton( "Jdec Editor Window" );
			group.add( radio2 );
			radioPanel.add( radio1 );
			radioPanel.add( radio2 );
			radio2.setSelected( true );
			radio1.setActionCommand("fileoutput");
			radio1.addActionListener(this);
			radio2.setActionCommand("JDecEditor");
			radio2.addActionListener(this);
			c.gridx = 2;
			c.gridwidth = 3;
			panel.add( radioPanel, c);
			JLabel lbl8 = new JLabel( "Output File" );
			lbl8.setFont(new Font(null,Font.PLAIN,10));
			setOutputLabelRef(lbl8);
			lbl8.setBorder( border );
			c.gridx = 0;
			c.gridy = 7;
			c.gridwidth = 1;
			panel.add( lbl8, c );
			JTextField txt8 = new JTextField();
			txt8.setPreferredSize( mediumField );
			txt8.setEditable(false);
			txt8.setColumns(20);
			setResultFileTextRef(txt8);
			c.gridx = 1;
			c.gridwidth = 3;
			panel.add( txt8, c );
			
			JButton selFile = new JButton( "Select File" );
			selFile.setActionCommand("selfile");
			selFile.addActionListener(this);
			c.gridx = 4;
			c.gridy = 0;
			c.gridwidth = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			panel.add( selFile, c );
			JButton edit = new JButton( "Display" );
			edit.setActionCommand("display");
			edit.addActionListener(this);
			c.gridy = 1;

			JButton apply = new JButton( "Compile" );
			apply.setActionCommand("apply");
			apply.addActionListener(this);
			c.gridy = 2;
			c.anchor = GridBagConstraints.NORTH; // anchor north
			panel.add( apply, c );
			
			JButton cl = new JButton( "Close" );
			cl.setActionCommand("close");
			cl.addActionListener(this);
			c.gridy = 4;
			c.anchor = GridBagConstraints.NORTH; // anchor north
			panel.add( cl, c );
			
			JButton outputBtn = new JButton( "Select Output File" );
			outputBtn.setActionCommand("seloutputfile");
			outputBtn.addActionListener(this);
			c.gridx=5;
			c.gridy = 3;

			WindowListener wndCloser = new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					Javac.this.setVisible(false);
					Javac.this.dispose();
				}
			};
			addWindowListener( wndCloser );
			setVisible( true );
		}
		
		public void actionPerformed(ActionEvent ae)
		{
			
			
			if(ae.getActionCommand().equals("close"))
			{
				setVisible(false);
				dispose();
			}
			if(ae.getActionCommand().equals("display"))
			{
				
						JTextField cp=getClassPathTextRef();
						JFrame temp=new JFrame("Classpath Set");
						temp.getContentPane().setLayout(new FlowLayout());
						JTextArea ta=new JTextArea();
						ta.setColumns(temp.getWidth());
						ta.setText(cp.getText());
						temp.getContentPane().add(ta);
						//temp.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
						WindowListener wndCloser = new WindowAdapter() {
							public void windowClosing(WindowEvent e) {
								Javac.this.setVisible(false);
							}
						};
						temp.addWindowListener(wndCloser );
						temp.setBounds(200,200,300,300);
						temp.setVisible(true);
						
				
			}
			else if(ae.getActionCommand().equals("selfile"))
			{
				
				JFileChooser chooser=new JFileChooser(".");
				//chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int option=chooser.showOpenDialog(this);
				if(option==JFileChooser.APPROVE_OPTION)
				{
					try{
						File name=chooser.getSelectedFile();
						setFileToBeCompiled(name);
						if(filePathtxt!=null)
							filePathtxt.setText(name.getAbsolutePath());
						
					}
					catch(Exception e)
					{
						
					}
				}
				
			
				
			}
			else if(ae.getActionCommand().equals("apply"))
			{
				//Preferences pref=Manager.getManager().getPrefRef();
				// Persist Information
				try
				{
					compileFile();
				}
				catch(Exception e)
				{
					e.printStackTrace(LogWriter.getInstance().getPrintWriter());
				}
				
				
				this.setVisible(false);
				this.dispose();
				
				
			}
			else if(ae.getActionCommand().equals("fileoutput"))
			{
				JFileChooser chooser=new JFileChooser();
				int opt=chooser.showOpenDialog(this);
				if(opt==JFileChooser.APPROVE_OPTION)
				{
					setResultOutputFile(chooser.getSelectedFile().getAbsolutePath());
				}
				
			}
			else if(ae.getActionCommand().equals("JDecEditor"))
			{
				setResultOutputFile(null);
			}
			else if(ae.getActionCommand().equals("debug"))
			{
				JComboBox combo=(JComboBox)ae.getSource();
				int index=combo.getSelectedIndex();
				if(index==0)
				{
				    setDebugOption(false);	
				}
				else
				{
					setDebugOption(true);
				}
			}
			
			
		}
		
		
		File javaFolder=null;
		private void setFileToBeCompiled(File f)
		{
			UILauncher.getUIutil().setFileToBeCompiledOrRun(f);
		}
		
		private void setOutputLabelRef(JLabel lbl)
		{
			this.lbl=lbl;
		}
		private JLabel lbl=null;
		private boolean debugMode=false;
		
		private void setDebugOption(boolean b)
		{
			debugMode=b;
		}
		
		
		private void compileFile() throws IOException
		{
			JTextField txt1=filePathtxt;
			if(txt1!=null)
			{
				String str=txt1.getText().trim();
				if(str.length() > 0)
				{
					String q="\"";
                    str=q+str;
                    str=str+q;
					JTextField cp=getClassPathTextRef();
					java.lang.String classpath=cp.getText();
					boolean debug=debugMode;
					String javaHome=UILauncher.getUIutil().getJavaHomePath();
					javaHome=UILauncher.getUIutil().getJavaHomePath(); 
					if(javaHome!=null){
						java.lang.String cmd=javaHome+File.separator+"bin"+File.separator+"javac.exe ";
						File jc=new File(cmd);
						if(!jc.exists()){JOptionPane.showMessageDialog(this,"Please set java Home First[Edit-->preferences]");
						return ;
						}
						if(debug)cmd+="-g ";
						cmd+="-classpath "+classpath;
						cmd+=" "+str;
						cmd="cmd /c "+cmd ;
						JTextField res=getResultFileTextRef();
						//System.out.println(cmd);
						
						Process p=null;
						try
						{
							Runtime rt=Runtime.getRuntime();
							System.out.println(cmd);
							p=rt.exec(cmd);
						}
						catch(Exception e)
						{
							
						}
						if(res!=null)
						{
							String resFile=res.getText();
							if(resFile.trim().length()!=0)
							{
								if(p!=null)
								{
									InputStream is=p.getInputStream();
									BufferedInputStream bis=new BufferedInputStream(is);
									int i=bis.read();
									String s="";
									while(i!=-1)
									{
										char c=(char)i;
										s+=c;
										i=bis.read();
									}
									
									is=p.getErrorStream();
									bis=new BufferedInputStream(is);
									i=bis.read();
									
									while(i!=-1)
									{
										char c=(char)i;
										s+=c;
										i=bis.read();
									}
									
									if(s.length()>0)
									{
									
										String opfile=res.getText();
										File op=new File(opfile);
										BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(op)));
										bw.write(s);
										bw.flush();
										bw.close();
										bw=null;
										JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Compilation Error ...\nPlease check the output File\n\n[TIP :] You Can Open The output file in Simple editor under Utilities menu...");
									}
									else
									{
										JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Compiled Successfully....\nYou Can Choose To Decompile/Run the File now");
									}
								}
							}
							else // op to ConsoleLauncher editor
							{
								if(p!=null)
								{
									InputStream is=p.getInputStream();
									BufferedInputStream bis=new BufferedInputStream(is);
									int i=bis.read();
									String s="";
									while(i!=-1)
									{
										char c=(char)i;
										s+=c;
										i=bis.read();
									}
									
								
									is=p.getErrorStream();
									bis=new BufferedInputStream(is);
									i=bis.read();
								//	System.out.println(s.length()+"Before Error");
									while(i!=-1)
									{
										char c=(char)i;
										s+=c;
										i=bis.read();
									}
								
									
									JEditorPane editor=UIUtil.getUIUtil().getEditorWindow();
									editor.setText("");
									editor.setText(s);
									if(s.length()>0)
									{
										JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Compilation Error");
									}
									else
									{
										JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Compiled Successfully");
									}
								}
								
								
								
							}
								
						} 
						else // op to ConsoleLauncher editor
						{
						
							if(p!=null)
							{
								InputStream is=p.getInputStream();
								BufferedInputStream bis=new BufferedInputStream(is);
								int i=bis.read();
								String s="";
								while(i!=-1)
								{
									char c=(char)i;
									s+=c;
									i=bis.read();
								}
								is=p.getErrorStream();
								bis=new BufferedInputStream(is);
								i=bis.read();
								//System.out.println(s.length()+"Before Error");
								while(i!=-1)
								{
									char c=(char)i;
									s+=c;
									i=bis.read();
								}
								JEditorPane editor=UIUtil.getUIUtil().getEditorWindow();
								editor.setText("");
								editor.setText(s);
								if(s.length()>0)
								{
									JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Compilation Error");
								}
								else
								{
									JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Compiled Successfully");
								}
								
							}
							
						}
						
					}
					else
					{
						JOptionPane.showMessageDialog(UILauncher.getMainFrame(), "Java Home Not Set!", "Javac Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			
		}
		private void setResultFileTextRef(JTextField txt8)
		{
			resultFileRef=txt8;
		}
		
		private JTextField getResultFileTextRef()
		{
			return resultFileRef;
		}
		private void setResultOutputFile(String file)
		{
		 JTextField tmp=getResultFileTextRef();
		 if(tmp!=null && file!=null)
		 {
			 tmp.setText(file);
			 tmp.setEditable(true);
			 if(lbl!=null)lbl.setFont(new Font(null,Font.BOLD,10));
		 }
		 UILauncher.getUIutil().setJavacResultFile(file);
		}
		private JTextField resultFileRef;
		private void setFilePathRef(JTextField txt1)
		{
			filePathtxt=txt1;
		}
		private JTextField filePathtxt=null;
		
		
		private String getJavaHomePath()
		{
			return javaFolder.getAbsolutePath();
		}
		
		public void setClassPathTextFieldRef(JTextField classpath)
		{
		 cp=classpath;	
		}
		private JTextField getClassPathTextRef()
		{
			return cp;
		}
		private JTextField cp=null;
		File browserPath=null;
		private void setBrowserPath(File f)
		{
			browserPath=f;
			UILauncher.getUIutil().setBrowserPath(f);
		}
		
		private String getBrowserPath()
		{
			return browserPath.getAbsolutePath();
		}
		
		
	}



