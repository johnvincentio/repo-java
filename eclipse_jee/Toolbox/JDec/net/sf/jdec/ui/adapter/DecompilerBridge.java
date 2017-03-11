/*
 *  DecompilerBridge.java Copyright (c) 2006,07 Swaroop Belur 
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

package net.sf.jdec.ui.adapter;

import net.sf.jdec.config.Configuration;
import net.sf.jdec.core.InnerClassTracker;
import net.sf.jdec.exceptions.ApplicationException;
import net.sf.jdec.exceptions.IOError;
import net.sf.jdec.exceptions.InvalidInputException;
import net.sf.jdec.io.Writer;
import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.reflection.JavaClass;
import net.sf.jdec.ui.core.*;
import net.sf.jdec.ui.exceptions.UIException;
import net.sf.jdec.ui.main.UILauncher;
import net.sf.jdec.ui.util.ConsantPoolDescription;
import net.sf.jdec.ui.util.LogWriter;
import net.sf.jdec.ui.util.ShowMethods;
import net.sf.jdec.ui.util.UIUtil;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;


public class DecompilerBridge {

	private UIUtil util;

	private static DecompilerBridge ref = null;

	private java.lang.String option;

	private Object[] result;

	private File[] resultFiles = null;

	private String classFile = ""; // Use it to override config.properties
	// values

	private String jarFile = ""; // Use it to override config.properties values

	private DecompilerBridge(UIUtil util) {
		this.util = util;
		option = util.getJdecOption();
	}

	public static DecompilerBridge getInstance(UIUtil util) {
		if (ref == null) {
			ref = new DecompilerBridge(util);
			return ref;
		} else
			return ref;
	}

	public void setDecompilerConfig(String from) {

		// Get From UIUtil. (Since this object would have read from
		// config.properties file)
		if (option == null || option.trim().length() == 0) {
			throw new ApplicationException(
			"Decompiler Option Not set Before Trying to set Decompiler Options....\n");
		}
		String outMode = util.getMode();
		Configuration.setOutputMode(outMode);
		String folderPath = util.getOutputFilePath();
        Configuration.backupOriginalOutputFilePath(folderPath);
		Configuration.setInnerdepth(util.getInnerDepth());
		Configuration.setDetailedExceptionTableInfo(util.getInterpret());
		Configuration.setFileExtension(util.getExtension());
		Configuration.setTempDir(util.getProjectTempDir());

		String logMode = util.getLogMode();
		Configuration.setLogMode(logMode);
		String logPath = util.getLogPath();
		String logLevel = util.getLogLevel();
		String classFile = util.getJavaClassFile();
		String jarFile = util.getJarFilePath();
		String showImport = util.getShowImport();

		// Now set in config object
		Configuration.setOutputMode("file");
		//System.out.println("!UILauncher.getUIutil().isJarOption()
		// "+!UILauncher.getUIutil().isJarOption());
		if (!UILauncher.getUIutil().isJarOption())
			Configuration.setOutputFolderPath(folderPath);
		else {
			// Just Making sure directory exists....
			File temp = new File(folderPath + File.separator + "JARDECOMPILED");
			if (temp.exists() == false) {
				temp.mkdir();
			}
			Configuration.setOutputFolderPath(folderPath + File.separator
					+ "JARDECOMPILED");
			//System.out.println(folderPath+File.separator+"JARDECOMPILED");
		}
		Configuration.setLogMode("file");
		Configuration.setLogFilePath(logPath);
		Configuration.setLogLevel(logLevel);
		if (from.equals("null")) {
			String tmp = Configuration.getJavaClassFile();
			//System.out.println(tmp+"tmp???");
			if (tmp == null || tmp.trim().length() == 0
					|| tmp.equals("***Not-Initialized***")) {
				if (ConsoleLauncher.getCurrentSourceFile() == null) // Check Required
				{
					//System.out.println("In if");
					Configuration.setClassFilePath(classFile);
					Configuration.setJavaClassFile(classFile);
					ConsoleLauncher.setCurrentSourceFile(new File(classFile));
				} else {
					//System.out.println("Else");
					Configuration.setClassFilePath(ConsoleLauncher.getCurrentSourceFile()
							.getAbsolutePath());
					Configuration.setJavaClassFile(ConsoleLauncher.getCurrentSourceFile()
							.getAbsolutePath());
				}
			}

			if (Configuration.getClassPath() == null) {
				//System.out.println("WAS NULL");
				Configuration.setClassFilePath(ConsoleLauncher.getCurrentSourceFile()
						.getAbsolutePath());
			}

			//System.out.println(Configuration.getClassPath()+"XXX");
			tmp = Configuration.getJarPath();
			if (tmp == null || tmp.trim().length() == 0) {
				if (ConsoleLauncher.getCurrentJarSourceFile() == null) // Check Required
				{
					Configuration.setJarPath(jarFile);
					ConsoleLauncher.setCurrentJarSourceFile(new File(jarFile));
				} else {
					Configuration.setJarPath(ConsoleLauncher.getCurrentJarSourceFile()
							.getAbsolutePath());
				}
			}

		}
		if (from.equals("run")) {
			Configuration.setClassFilePath(classFile);
			Configuration.setJavaClassFile(classFile);
			ConsoleLauncher.setCurrentSourceFile(new File(classFile));
			Configuration.setJarPath(jarFile);
			ConsoleLauncher.setCurrentJarSourceFile(new File(jarFile));
		}

		//println("JAR FILE SET TO "+Configuration.getJarPath());
		Configuration.setShowImport(showImport);

		if (jarFile != null && jarFile.trim().length() > 0)
			Configuration.setJarSpecified(true);
		if (jarFile == null || jarFile.trim().length() == 0)
			Configuration.setJarSpecified(false);
		if (classFile != null && classFile.trim().length() > 0)
			Configuration.setSingleClassSpecified(true);
		if (classFile == null || classFile.trim().length() == 0)
			Configuration.setSingleClassSpecified(false);
		//        System.out.println(option+"option!!!") ;
		if (option.equalsIgnoreCase("decompileClass") || option.equals("dc")) {
			Configuration.setDecompileroption("dc");
		} else if (option.equalsIgnoreCase("disassemble") || option.equals("dis")) {
			Configuration.setDecompileroption("dis");
		} else if (option.equalsIgnoreCase("decompileJar") || option.equals("jar")) {
			Configuration.setDecompileroption("jar");
		} else if (option.equalsIgnoreCase("ConstantPool") || option.equals("vcp")) {
			Configuration.setDecompileroption("vcp");
		} else if (option.equalsIgnoreCase("localVariables") || option.equals("llv")) // Do not make it
			// Public . Only
			// For Debugging
			// Purpose
		{
			Configuration.setDecompileroption("llv");
		} else if (option.equalsIgnoreCase("Skeleton") || option.equals("nocode")) {
			Configuration.setDecompileroption("nocode");
		} else if (option.equalsIgnoreCase("help") ) {
			Configuration.setDecompileroption("help");
		}
        else if (option.equalsIgnoreCase("general") ) {
			Configuration.setDecompileroption("general");
		}

		LogWriter wr=LogWriter.getInstance();
		String m="Finished applying jdec config options";;
		wr.writeLog(m,UIUtil.getUIUtil().getLogLevel());
		wr.flush();
	}

	private void checkValidity(String path) throws InvalidInputException {
		path = ConsoleLauncher.checkFilePath(path);
		boolean valid = ConsoleLauncher.validInput(path);
		if (!valid) {
			throw new InvalidInputException("Class File Not Reachable...");
		}

	}

	private void checkDecompilerConfig() throws UIException {

		if (option.equalsIgnoreCase("decompileClass")) {
			java.lang.String jClass = Configuration.getJavaClassFile();
			if (jClass == null || jClass.trim().length() == 0) {
				throw new UIException(
				"Check Decompiler Property/Configuration File Again...\n Class File Not Initialized...");
			}
		} else if (option.equalsIgnoreCase("disassemble")) {
			java.lang.String jClass = Configuration.getJavaClassFile();
			if (jClass == null || jClass.trim().length() == 0) {
				throw new UIException(
				"Check Decompiler Property/Configuration File Again...\n Class File Not Initialized...");
			}

		} else if (option.equalsIgnoreCase("decompileJar")) {
			java.lang.String jarPath = Configuration.getJarPath();
			if (jarPath == null || jarPath.trim().length() == 0) {
				throw new UIException(
				"Check Decompiler Property/Configuration File Again...\n Jar File Path Not Initialized...");
			}
		} else if (option.equalsIgnoreCase("ConstantPool")) {
			java.lang.String jClass = Configuration.getJavaClassFile();
			if (jClass == null || jClass.trim().length() == 0) {
				throw new UIException(
				"Check Decompiler Property/Configuration File Again...\n Class File Not Initialized...");
			}

		} else if (option.equalsIgnoreCase("localVariables")) {
			java.lang.String jClass = Configuration.getJavaClassFile();
			if (jClass == null || jClass.trim().length() == 0) {
				throw new UIException(
				"Check Decompiler Property/Configuration File Again...\n Class File Not Initialized...");
			}

		}

		else if (option.equalsIgnoreCase("Skeleton")) {

			java.lang.String jClass = Configuration.getJavaClassFile();
			if (jClass == null || jClass.trim().length() == 0) {
				throw new UIException(
				"Check Decompiler Property/Configuration File Again...\n Class File Not Initialized...");
			}

		} else if (option.equalsIgnoreCase("help")) {

		}

	}

	public void execute() {
		try {
                        InnerClassTracker.reinitializeStaticMembers();
                        ConsoleLauncher.allClassStructures=new ArrayList();
                        ConsoleLauncher.currentIsInner=false;
                        ShowMethods.createClone=true;
                        ConsoleLauncher.classMethodMap=new HashMap();
                        ConsoleLauncher.classMethodRefMap=new HashMap();
			ConsoleLauncher.reInitializeConstantPoolEntries();
			ConsoleLauncher.reInitializeConstantPoolDesc();
            setDecompilerConfig("run");
            checkDecompilerConfig();
            invokeJdec();

		} catch (UIException uie) {
			java.lang.String desc = uie.toString();
			ConsoleLauncher.fatalErrorOccured=true;
			Manager.getManager().setShowProgressBar(false);
			JOptionPane.showMessageDialog(UILauncher.getMainFrame(), desc,
					"Run Jdec Decompiler", JOptionPane.ERROR_MESSAGE);
			try {
				uie.printStackTrace(Writer.getWriter("log"));
			} catch (IOException e) {
				//e.printStackTrace();
			}
		} catch (IOException ioe) {
			try {
				LogWriter writer = LogWriter.getInstance();
				String msg = "***************************************************************";
				msg += "\n" + "EXCEPTION STACK TRACE";
				msg += "***************************************************************";
				msg += "\n\n";
				msg += "Message :" + ioe.getMessage();
				msg += "\n" + "Cause :" + ioe.getCause();
				writer.writeLog(msg);
				ioe.printStackTrace(writer.getPrintWriter());
				writer.flush();
				String desc = "An io Exception occured while processing";
				desc += "\nPlease refer to the Log Files\n";
				desc += "Please Report any Jdec Related Error At Project Home Site\n";
				desc += "UILogFile :" + writer.logFilePath();
				desc += "\nDecompiler Log File :"
					+ UILauncher.getUIutil().getLogPath();
				ConsoleLauncher.fatalErrorOccured=true;
				Manager.getManager().setShowProgressBar(false);
				JOptionPane.showMessageDialog(UILauncher.getMainFrame(), desc,
						"Run Jdec Decompiler", JOptionPane.ERROR_MESSAGE);
				

			} catch (Exception ex) {
			}

		} catch (IOError ioer) {
			try {
				LogWriter writer = LogWriter.getInstance();
				String msg = "***************************************************************";
				msg += "\n" + "EXCEPTION STACK TRACE";
				msg += "***************************************************************";
				msg += "\n\n";
				msg += "Message :" + ioer.getMessage();
				msg += "\n" + "Cause :" + ioer.getCause();
				writer.writeLog(msg);
				ioer.printStackTrace(writer.getPrintWriter());
				writer.flush();
				String desc = "An io Error occured while processing";
				desc += "\nPlease refer to the Log Files\n";
				desc += "UILogFile :" + writer.logFilePath();
				desc += "\nDecompiler Log File :"
					+ UILauncher.getUIutil().getLogPath();
				ConsoleLauncher.fatalErrorOccured=true;
				Manager.getManager().setShowProgressBar(false);
				JOptionPane.showMessageDialog(UILauncher.getMainFrame(), desc,
						"Run Jdec Decompiler", JOptionPane.ERROR_MESSAGE);
				

			} catch (Exception ex) {
				ConsoleLauncher.fatalErrorOccured=true;
				Manager.getManager().setShowProgressBar(false);
				try {
					ex.printStackTrace(Writer.getWriter("log"));
				} catch (IOException e) {
					
				}
			}

		}
		catch(Throwable t)
		{
			ConsoleLauncher.fatalErrorOccured=true;
			Manager.getManager().setShowProgressBar(false);
			try {
				t.printStackTrace(Writer.getWriter("log"));
			} catch (IOException e) {
				
			}
		}

	}

	public void execute(java.lang.String command, java.lang.String str,
			boolean override, String menu) {
                        
		try{
			InnerClassTracker.reinitializeStaticMembers();
                        ConsoleLauncher.currentIsInner=false;
                        ShowMethods.createClone=true;
                        ConsoleLauncher.classMethodMap=new HashMap();
                        ConsoleLauncher.classMethodRefMap=new HashMap();
                        LogWriter wr=LogWriter.getInstance();
			String m="Proceeding to clean up the constant pool structures...";
			wr.writeLog(m,UIUtil.getUIUtil().getLogLevel());
			ConsoleLauncher.reInitializeConstantPoolEntries();
			ConsoleLauncher.reInitializeConstantPoolDesc();
			m="Finished cleaning up the constant pool structures...";
			wr.writeLog(m,UIUtil.getUIUtil().getLogLevel());
            
			java.lang.String cmd = "";
			int opt = JOptionPane.NO_OPTION;
			if (override)
				cmd = str;
			else
				cmd = command; // cmd not used??
			if (menu.equals("view")) {
				java.lang.String message = "User chose to  execute command " + cmd
				+ "\n";
				message += "[NOTE]: Executing View Menu Option is supported only for Java Class File and Not for Jar Files\n";
				message += "Jdec will now proceed to execute the command For The Current Class File\n\n";
				message += "Please click Yes to Proceed...";
				opt = JOptionPane.showConfirmDialog(UILauncher.getMainFrame(),
						message, "User Info", JOptionPane.INFORMATION_MESSAGE);
			}
			
			
			Thread progbar = new Thread() {

				public void run() {

					

					if(UIUtil.getUIUtil().getProgressBarFrame()!=null)return;
					JDialog proframe=UIUtil.launchProgressBarFrame();
                    if(proframe!=null)
                      proframe.setVisible(true);
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

                        }


				}

			};
			
			progbar.setPriority(Thread.NORM_PRIORITY);
			if ((opt == JOptionPane.YES_OPTION && menu.equals("view"))
					|| menu.equals("null")) {

				try {
					if(UIUtil.getProgressBarFrame()==null  && Manager.getManager().isShowProgressBar())
					{
					
						progbar.start();
					}
					option = command;
					m="Decompiler option set to "+option;
					wr.writeLog(m,UIUtil.getUIUtil().getLogLevel());

                    setDecompilerConfig("null");
					checkDecompilerConfig();
					m="Going to invoke Jdec engine now....";
					wr.writeLog(m,UIUtil.getUIUtil().getLogLevel());
					invokeJdec();
					m="Jdec Invocation done!!!";
					wr.writeLog(m,UIUtil.getUIUtil().getLogLevel());
					userCanceled = false;
				} catch (UIException uie) {
					java.lang.String desc = uie.toString();
					ConsoleLauncher.fatalErrorOccured=true;
					Manager.getManager().setShowProgressBar(false);
					JOptionPane.showMessageDialog(UILauncher.getMainFrame(), desc,
							"Run Jdec Decompiler", JOptionPane.ERROR_MESSAGE);
					try {
						uie.printStackTrace(Writer.getWriter("log"));
					} catch (IOException e) {
						
					}

				} catch (IOException ioe) {
					try {
						LogWriter writer = LogWriter.getInstance();
						String msg = "***************************************************************";
						msg += "\n" + "EXCEPTION STACK TRACE";
						msg += "***************************************************************";
						msg += "\n\n";
						msg += "Message :" + ioe.getMessage();
						msg += "\n" + "Cause :" + ioe.getCause();
						writer.writeLog(msg);
						ioe.printStackTrace(writer.getPrintWriter());
						writer.flush();
						String desc = "An io Exception occured while processing";
						desc += "\nPlease refer to the Log Files\n";
						desc += "Please Report any Jdec Related Error At Project Home Site\n";
						desc += "UILogFile :" + writer.logFilePath();
						desc += "\nDecompiler Log File :"
							+ UILauncher.getUIutil().getLogPath();
						ConsoleLauncher.fatalErrorOccured=true;
						Manager.getManager().setShowProgressBar(false);
						JOptionPane.showMessageDialog(UILauncher.getMainFrame(),
								desc, "Run Jdec Decompiler",
								JOptionPane.ERROR_MESSAGE);
						try {
							ioe.printStackTrace(Writer.getWriter("log"));
						} catch (IOException e) {
							
						}

					} catch (Exception ex) {
					}
				} catch (IOError ioerror) {
					try {
						LogWriter writer = LogWriter.getInstance();
						String msg = "***************************************************************";
						msg += "\n" + "EXCEPTION STACK TRACE";
						msg += "***************************************************************";
						msg += "\n\n";
						msg += "Message :" + ioerror.getMessage();
						msg += "\n" + "Cause :" + ioerror.getCause();
						writer.writeLog(msg);
						ioerror.printStackTrace(writer.getPrintWriter());
						writer.flush();
						String desc = "An Error occured while processing";
						desc += "\nPlease refer to the Log Files\n";
						desc += "Please Report any Jdec Related Error At Project Home Site\n";
						desc += "UILogFile :" + writer.logFilePath();
						desc += "\nDecompiler Log File :"
							+ UILauncher.getUIutil().getLogPath();
						ConsoleLauncher.fatalErrorOccured=true;
						Manager.getManager().setShowProgressBar(false);
						JOptionPane.showMessageDialog(UILauncher.getMainFrame(),
								desc, "Run Jdec Decompiler",
								JOptionPane.ERROR_MESSAGE);
						
						return;

					} catch (Exception ex) {
						ConsoleLauncher.fatalErrorOccured=true;
						Manager.getManager().setShowProgressBar(false);
						try {
							ex.printStackTrace(Writer.getWriter("log"));
						} catch (IOException e) {
							
						}
					}
				}

			}
			if (menu.equals("view")
					&& (opt == JOptionPane.NO_OPTION || opt == JOptionPane.CANCEL_OPTION)) {
				userCanceled = true;
				Manager.getManager().setShowProgressBar(false);
				return;
			}
		}
		catch(Throwable t)
		{
			ConsoleLauncher.fatalErrorOccured=true;
			Manager.getManager().setShowProgressBar(false);
			try {
				t.printStackTrace(Writer.getWriter("log"));
			} catch (IOException e) {
				
			}
		}

	}

	private void invokeJdec() throws IOException {

		try
		{
			OutputFrame of=UIUtil.getUIUtil().decompileFrame;
			if(of!=null)
			{
				((JEditorPane)((JScrollPane)of.getComponent()).getViewport().getView()).setText("");
				JTabbedPane ltb=UIUtil.getUIUtil().getLeftTabbedPane();
				if(ltb.indexOfTab("Dir")!=-1)
					ltb.setSelectedIndex(ltb.indexOfTab("Dir"));
			}
			ConsoleLauncher.fatalErrorOccured=false;
			ConsoleLauncher.currentClassMethods(null);
			java.lang.String opt = Configuration.getDecompileroption();
			String s = Configuration.getJavaClassFile();
			String s2 = Configuration.getJarPath();
			File file1 = new File(s);
			File file2 = new File(s2);
			if (opt.equals("dc") && (!file1.exists())
					|| (s != null && opt.equals("dc") && !s.trim().endsWith(".class"))) {
				return;
			}
                        
			if (opt.equals("jar") && !(file2.exists())
					|| (s2 != null && opt.equals("jar") && !isValidArchiveFileExt(s2.trim()))) {
				return;
			}
			//System.out.println(opt+"belurs;opt");
			if (opt.equals("dc")) {
				//	Writer writer=Writer.getWriter("output");
				//	writer.close("output");
				ConsoleLauncher.decompileClassFromUI(Configuration.getJavaClassFile());
				//System.out.println("RESULT FILE");

				setOutputFiles(new File[] { new File(ConsoleLauncher.getResultFileName()) });

			} else if (opt.equals("dis")) {
				//Writer writer=Writer.getWriter("output");
				//writer.close("output");
				ConsoleLauncher.disassemble(Configuration.getJavaClassFile());
				setOutputFiles(new File[] { new File(ConsoleLauncher.getResultFileName()) });
			} else if (opt.equals("vcp")) {
				//Writer writer=Writer.getWriter("output");
				//writer.close("output");
				ConsoleLauncher.constantPool(Configuration.getJavaClassFile());
				File f = new File(Configuration.getOutputFolderPath() + File.separator
						+ "ConstantPoolCoontent.txt");
				//System.out.println(f.getAbsolutePath()+ " "+f.exists());
				//setOutputFiles(new File[] { f });
				setOutputFiles(new File[] { new File(ConsoleLauncher.getResultFileName()) });

			} else if (opt.equals("llv")) {
				//Writer writer=Writer.getWriter("output");
				//writer.close("output");
				ConsoleLauncher.showLocalVariables(Configuration.getJavaClassFile());

			}else if (opt.equals("general")) {

				ConsoleLauncher.showGeneralInformation(Configuration.getJavaClassFile());

			}

            else if (opt.equals("nocode")) {
				//Writer writer=Writer.getWriter("output");
				//writer.close("output");
				ConsoleLauncher.showSkeletonClass(Configuration.getJavaClassFile());
				setOutputFiles(new File[] { new File(ConsoleLauncher.getResultFileName()) });

			} else if (opt.equals("jar")) {

				
				try {
					ConsoleLauncher.decompileJarFromUI(Configuration.getJarPath(),UIUtil.registeredClasses);
				} catch (RuntimeException e) {
					 	try
	                    {
	                        UIManager.setLookAndFeel(UILauncher.getUIutil().getCurrentLNF());
	                        SwingUtilities.updateComponentTreeUI(UILauncher.getMainFrame());
	                    }
	                    catch(Exception ep) {}
	                    
	                    finally
	                    {
	                    	ConsoleLauncher.fatalErrorOccured=false;
	                    	JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"RuntimeException occured while processing...Please check up log files");
	                    	try
		                    {
		                        UIManager.setLookAndFeel(UILauncher.getUIutil().getCurrentLNF());
		                        SwingUtilities.updateComponentTreeUI(UILauncher.getMainFrame());
		                    }
		                    catch(Exception ep) {}
	                    }
					return;
				}

			} else if (opt.equals("help")) {
				ConsoleLauncher.showHelp();
				setOutputFiles(new File[] { new File(ConsoleLauncher.getResultFileName()) });
			}

		}
		catch(Exception e)
		{
			ConsoleLauncher.fatalErrorOccured=true;
			Manager.getManager().setShowProgressBar(false);
			try {
				e.printStackTrace(Writer.getWriter("log"));
			} catch (IOException e2) {
				
			}
		}
	}

    public File[] getResultFiles() {
        return resultFiles;
    }

    public void setOutputFiles(File[] resultFiles) {
		this.resultFiles = resultFiles;
        UILauncher.getUIConfigRef().setCurrentResultFile(resultFiles[0]);
	}

	private void readResultFile() {

	}

	public void showResult(JTabbedPane tabs) {
		try {
			
                   
                    
                    File rest[] = this.resultFiles;
			if (rest != null && rest.length > 0) {
				File f = rest[0];
				UIUtil.decompiledClasses.add(f.getAbsolutePath());
				/*if(UIUtil.decompiledClasses.size() > 1)
				{
					JButton back=Manager.getManager().back;
					JButton fwd=Manager.getManager().fwd;
					if(back!=null)
				}*/
			}
			
			JEditorPane rdwrPane;
			java.lang.String tabName = null;
			int tabindex=-1;
			JTabbedPane rtab = getRightTabbedPane();
                        OutputFrame of=Manager.getManager().decoutputframe;
                        if(of!=null){
                            of.bracketMarkedPositions=new ArrayList();
                        }
			if (Configuration.getDecompileroption().equals("dc"))
			{
				tabName = "Decompiled Output";

				tabindex=2;
			}
			if(Configuration.getDecompileroption().equals("nocode"))
			{
				tabName="Skeleton Output";
				tabindex=3;
			}
			if (Configuration.getDecompileroption().equals("dis"))
			{
				tabName = "Disassembled Output";
				tabindex=1;
			}
			if(tabindex!=-1)
			{
				UIUtil.getUIUtil().removeFileTabMap(tabindex);
			}
			if (Configuration.getDecompileroption().equals("help")) {
				JTabbedPane righttab = getRightTabbedPane();
				int index = righttab.indexOfTab("Jdec Help");
				if (index != -1)
					righttab.remove(index);
				JInternalFrame in;//=new JInternalFrame();
				OutputFrame helpoutput = new OutputFrame(UILauncher
						.getObserver(), "","plain");
				//Dimension d=in.getToolkit().getScreenSize();
				//in.setSize(d);
				righttab.addTab("Jdec Help", helpoutput.getComponent());
				tabs.setSelectedIndex(tabs.indexOfTab("Jdec Help"));
				/*
				 * in.setSize(570, 200); in.setVisible(true);
				 * in.getContentPane().setLayout(new FlowLayout()); JScrollPane
				 * pa=new JScrollPane(); pa.setSize(550, 150); JEditorPane
				 * ed=new JEditorPane(); pa.getViewport().setView(ed);
				 * in.getContentPane().add(pa); righttab.addTab("Jdec Help",
				 * in);
				 */
				Component editor = tabs.getSelectedComponent();
				JScrollPane editorTab = (JScrollPane) editor;
				Object o = editorTab.getComponent(0);
				JViewport view = (JViewport) o;
				Object o2 = view.getView();
				if (o2 != null) {
					rdwrPane = (JEditorPane) o2;
					File res[] = this.resultFiles;
					if (res != null && res.length > 0) {
						File f = res[0];
						FileReader fr = new FileReader(f);
						rdwrPane.read(fr, null);
						rdwrPane.setEditable(true);

					}

				}

				Manager.getManager().setShowProgressBar(false);

			}
			// TODO handle Combined Output

			if (tabs != null && tabName != null && tabName.length() > 0) {
				
				if(tabs.indexOfTab(tabName)==-1)
				{
					OutputFrame skeoutput=new OutputFrame(UILauncher.getObserver(),"","plain");
					tabs.addTab(tabName,skeoutput.getComponent());
				}
				
				// Comment me
				/*tabName="Unicode Test"
				OutputFrame skeoutput=new OutputFrame(UILauncher.getObserver(),"","plain");
				tabs.addTab(tabName,skeoutput.getComponent());*/
				
				tabs.setSelectedIndex(tabs.indexOfTab(tabName));
				Component editor = tabs.getSelectedComponent();
				JScrollPane editorTab = (JScrollPane) editor;

				Object o = editorTab.getComponent(0);
				JViewport view = (JViewport) o;
				Object o2 = view.getView();
				if (o2 != null) {
					rdwrPane = (JEditorPane) o2;
					rdwrPane.setText("");
					File res[] = this.resultFiles;
					if (res != null && res.length > 0) {
						File f = res[0];
						FileReader fr = new FileReader(f);
						rdwrPane.read(fr, null);
						rdwrPane.setEditable(true);

						UIUtil u=UILauncher.getUIutil();
						FileInfoFrame finfo=u.getInfoFrame();
						String s= Configuration.getJavaClassFile();    // TODO NULL POINTER   Fixme
						
							int winslash=s.indexOf("\\");
							int unixslash=s.indexOf("/");

							if(winslash!=-1)
							{
								s=s.replace('\\',File.separator.charAt(0));
							}
							if(unixslash!=-1)
							{
								s=s.replace('/',File.separator.charAt(0));
							}


						
						finfo.recreateDetailsFrame(s);      //javax.swing.Bo info;
						Splitter split=Manager.getManager().getSplitterRef();
						split.resetInfoFrame(finfo);
						UILauncher.getMainFrame().repaint();

						if (tabName.equals("Decompiled Output")) {
							ConsoleLauncher.setCurrentDecompiledFile(Configuration
                  .getJavaClassFile());
							//System.out.println(ConsoleLauncher.getCurrentDecompiledFile()+"???");
						}
                        Manager.getManager().setShowProgressBar(false);
					}

				}
			}

			if (tabName == null && Configuration.getDecompileroption().equals("vcp")) {
				ConsantPoolDescription ref = null;
				if (UIUtil.getUIUtil().getcpdesc() != null
						&& UIUtil.getUIUtil().getcpdesc().size() > 0)
					ref = new ConsantPoolDescription(UIUtil.getUIUtil()
							.getcpdesc());
				else
					ref = null;
				//SystemProperties ref =
				// Manager.getManager().getSystemPropertyRef();
				if (ref != null) {
					JTabbedPane righttab = getRightTabbedPane();
					int index = righttab.indexOfTab("Constant Pool");
					if (index != -1)
						righttab.remove(index);
					righttab.addTab("Constant Pool", ref);
					righttab.setSelectedComponent(ref);
					Manager.getManager().setShowProgressBar(false);

					UIUtil u=UILauncher.getUIutil();
					FileInfoFrame finfo=u.getInfoFrame();
					String s=u.getJavaClassFile();
					
						int winslash=s.indexOf("\\");
						int unixslash=s.indexOf("/");

						if(winslash!=-1)
						{
							s=s.replace('\\',File.separator.charAt(0));
						}
						if(unixslash!=-1)
						{
							s=s.replace('/',File.separator.charAt(0));
						}


					
					finfo.recreateDetailsFrame(s);
					Splitter split=Manager.getManager().getSplitterRef();
					split.resetInfoFrame(finfo);
					UILauncher.getMainFrame().repaint();


				} else {
					JOptionPane.showMessageDialog(UILauncher.getMainFrame(),
							"[ERROR] :Constant Pool could not be loaded.....",
							"Loading Error ", JOptionPane.ERROR_MESSAGE);

				}

				/*
				 * OutputFrame cpool=new
				 * OutputFrame(UILauncher.getObserver(),"Viewing Constant pool
				 * Content..."); int present=tabs.indexOfTab("Constant Pool");
				 * if(present!=-1)tabs.remove(present); tabs.addTab("Constant
				 * Pool", cpool.getComponent()); tabName="Constant Pool";
				 * tabs.setSelectedIndex(tabs.indexOfTab(tabName)); Component
				 * editor=tabs.getSelectedComponent(); JScrollPane
				 * editorTab=(JScrollPane)editor; Object
				 * o=editorTab.getComponent(0); JViewport view=(JViewport)o;
				 * Object o2=view.getView(); if(o2!=null) {
				 * rdwrPane=(JEditorPane)o2; File res[]=this.resultFiles;
				 * Manager.getManager().setShowProgressBar(false); if(res!=null &&
				 * res.length > 0) { File f=new
				 * File(UILauncher.getUIutil().getConstantPoolResultFilePath());//res[0];
				 * System.out.println("Constant Pool Result File
				 * got:"+f.getAbsolutePath()); FileReader fr=new FileReader(f);
				 * rdwrPane.setText(""); rdwrPane.read(fr,null);
				 * //rdwrPane.setEditable(false); //reRenderContent(rdwrPane);
				 * rdwrPane.setEditable(false);
				 * JOptionPane.showMessageDialog(UILauncher.getMainFrame(),
				 * "Select Help option For More Detailed Understanding of
				 * Constant Pool","Done... ",JOptionPane.INFORMATION_MESSAGE); } }
				 */

			}
			
			//System.out.println(Configuration.getDecompileroption()+"Configuration.getDecompileroption()");
			//  To show the methods...
			ArrayList methodnames=new ArrayList();
			if(Configuration.getDecompileroption().equals("dc"))
			{
				if(Manager.getManager(). currentmethods!=null)
				{
					Manager.getManager(). currentmethods.getItem().setEnabled(true);
				}
				if(UIUtil.getUIUtil().tiplabel!=null)
				{
					 UIUtil.getUIUtil().tiplabel.setEnabled(true);
					 UIUtil.getUIUtil().tiplabel.setText("Hover to view ToolTip");
				}
				JToolTip tip=new JToolTip();
				tip.setTipText("Jdec TIP");
				tip.setBorder(new EtchedBorder());
				tip.setToolTipText("Press CTRL-M to view methods list in popup window");
				int x=UILauncher.getMainFrame().getX();
				int y=UILauncher.getMainFrame().getY();
				int width=UILauncher.getMainFrame().getWidth();
				tip.setBounds(300,300,100,100);
				tip.setVisible(true);
				tip.setFocusable(true);
				JavaClass clz= ConsoleLauncher.getClazzRef();
				if(clz!=null)
				{
					ArrayList list=clz.getALLMethods();
					for(int k=0;k<list.size();k++)
					{
						Behaviour b=(Behaviour)list.get(k);
						String name=b.getBehaviourName();
						String mn=name.concat(b.getUserFriendlyMethodParams()); 
						methodnames.add(mn);
	
					}
					//ConsoleLauncher.currentClassMethods(methodnames);
					JdecTree tree=new JdecTree(ConsoleLauncher.mainClassStructure,true);
					JTabbedPane tabPane=UIUtil.getUIUtil().getLeftTabbedPane();
					int cmTabIndex=tabPane.indexOfTab("Class Structure");
					
					if(cmTabIndex >= 0)
					{
						Component c=tabPane.getComponentAt(cmTabIndex);	
						JFrame mainFrame=UILauncher.getMainFrame();
						try
						{
							Component c1=tabPane.getComponentAt(cmTabIndex);
							tabPane.remove(cmTabIndex);
							tree.setSize(c1.getWidth(),c1.getHeight());
							tabPane.addTab("Class Structure",tree);
							tabPane.setSelectedComponent(tree);
							mainFrame.validate();
							mainFrame.repaint();
						}
						catch(Throwable t)
						{
							//tabPane.remove(jarTabIndex);
							//tabPane.addTab("Class Structure",c);
							JOptionPane.showMessageDialog(UILauncher.getMainFrame(), "A Runtime Exception Occured while Processing The File...\nPlease restart jdec",
									"UI Rendering Error ", JOptionPane.ERROR_MESSAGE);
                                                        Writer w=Writer.getWriter("log");
							t.printStackTrace(w);
                                                        t.printStackTrace();
							//System.exit(1);
							
						}
						if(tree.getTipLabel()!=null)
							tree.getTipLabel().setToolTipText("Press CTRL-M to view methods list in popup window");
					}
					
					
				}
			}
			else
			{
				if(Manager.getManager(). currentmethods!=null)
				{
					Manager.getManager(). currentmethods.getItem().setEnabled(false);
				}
				if(UIUtil.getUIUtil().tiplabel!=null)
				{
					 UIUtil.getUIUtil().tiplabel.setEnabled(false);
					 UIUtil.getUIUtil().tiplabel.setText("");
				}
			}
			
			
		}
		catch (RuntimeException ex) {
			JOptionPane.showMessageDialog(UILauncher.getMainFrame(), "A Runtime Exception Occured while Processing The File...",
					"Run Jdec Decompiler", JOptionPane.ERROR_MESSAGE);
			ConsoleLauncher.fatalErrorOccured=true;
			Manager.getManager().setShowProgressBar(false);
			try {
				ex.printStackTrace(Writer.getWriter("log"));
                                
			} catch (IOException e) {
		
			}
		}
		catch (IOException ioe) {
			try {
				Manager.getManager().setShowProgressBar(false);
				LogWriter writer = LogWriter.getInstance();
				String msg = "***************************************************************";
				msg += "\n" + "EXCEPTION STACK TRACE";
				msg += "***************************************************************";
				msg += "\n\n";
				msg += "Message :" + ioe.getMessage();
				msg += "\n" + "Cause :" + ioe.getCause();
				writer.writeLog(msg);
				ioe.printStackTrace(writer.getPrintWriter());
				writer.flush();
				String desc = "An io Exception occured while processing";
				desc += "\nPlease refer to the Log Files\n";
				desc += "Please Report any Jdec Related Error At Project Home Site\n";
				desc += "UILogFile :" + writer.logFilePath();
				desc += "\nDecompiler Log File :"
					+ UILauncher.getUIutil().getLogPath();
				ConsoleLauncher.fatalErrorOccured=true;
				Manager.getManager().setShowProgressBar(false);
				JOptionPane.showMessageDialog(UILauncher.getMainFrame(), desc,
						"Run Jdec Decompiler", JOptionPane.ERROR_MESSAGE);

			}
			catch (Exception ex) {
				JOptionPane.showMessageDialog(UILauncher.getMainFrame(), "An Error Occured while Processing The File...",
						"Run Jdec Decompiler", JOptionPane.ERROR_MESSAGE);
				ConsoleLauncher.fatalErrorOccured=true;
				Manager.getManager().setShowProgressBar(false);
			}
			catch(Throwable t)
			{
				ConsoleLauncher.fatalErrorOccured=true;
				JOptionPane.showMessageDialog(UILauncher.getMainFrame(), "An Error Occured while Processing The File...",
						"Run Jdec Decompiler", JOptionPane.ERROR_MESSAGE);
				Manager.getManager().setShowProgressBar(false);
			}
		}
		UIUtil.getUIUtil().setProframe(null);
		UIUtil.getUIUtil().setVersionproframe(null);

	}

	private void reRenderContent(JEditorPane editor) {
		editor.selectAll();
		editor.copy();
		StringBuffer text = new StringBuffer("");
		int counter = 1;
		try {

			Clipboard clip = editor.getToolkit().getSystemClipboard();
			Transferable tr = clip.getContents(null);
			if (tr != null) {
				DataFlavor[] fl = tr.getTransferDataFlavors();
				if (fl != null) {
					ArrayList temp = new ArrayList();

					for (int s = 0; s < fl.length; s++) {
						if (fl[s].isFlavorTextType()) {
							Object o = tr.getTransferData(fl[s]);
							if (o instanceof java.lang.String) {
								// check whether already added
								if (temp.size() == 0)
									temp.add(o.toString());
								else {
									java.lang.String str[] = (String[]) temp
									.toArray(new String[] {});
									Arrays.sort(str);
									for (int i = 1; i < str.length; i++) {

										String content = str[i];
										String prev = str[i - 1];
										if (content.equals(prev)) {

										} else {
											temp.add(o.toString());
											//text.append(counter++
											// +"\t\t"+o.toString()+"\n");
										}

									}

								}

							}
						}

					}

					for (int y = 0; y < temp.size(); y++) {

						String s1 = (String) temp.get(y);
						text.append(y + 1 + "\t\t" + s1 + "\n");

					}

					if (text.toString().length() > 0) {

						editor.setText(text.toString());
					}
				}
			}
		} catch (Exception exp) {
		} finally {

		}

	}

	private void checkForException() {

	}

	public String getClassFile() {
		return classFile;
	}

	public void setClassFile(String classFile) {
		this.classFile = classFile;
		Configuration.setJavaClassFile(classFile);
		Configuration.setClassFilePath(classFile);
		Configuration.setSingleClassSpecified(true);
	}

	public String getJarFile() {
		return jarFile;
	}

	public void setJarFile(String jarFile) {
		this.jarFile = jarFile;
		Configuration.setJarPath(jarFile);
		Configuration.setJarSpecified(true);
	}

	public void setOption(String option) {
		this.option = option;
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
	private boolean userCanceled = false;

	/**
	 * @return Returns the userCanceled.
	 */
	public boolean isUserCanceled() {
		return userCanceled;
	}

	/**
	 * @param userCanceled
	 *            The userCanceled to set.
	 */
	public void setUserCanceled(boolean userCanceled) {
		this.userCanceled = userCanceled;
	}



	private void gotoSleep() {
		try {
			Thread.sleep(1000);

		} catch (InterruptedException e) {
			LogWriter l=LogWriter.getInstance();
			e.printStackTrace(l.getPrintWriter());
		}


	}
        
        private boolean isValidArchiveFileExt(String filename) {
        if(filename==null)return false;
        String types=UILauncher.getUIConfigRef().getArchiveTypes();
        StringTokenizer st=new StringTokenizer(types,",");
        while(st.hasMoreTokens()) {
            String c=(String)st.nextToken();
            if(filename.endsWith(c)){
                return true;
            }
        }
        return false;
    }
}

//GridBagLayout g;