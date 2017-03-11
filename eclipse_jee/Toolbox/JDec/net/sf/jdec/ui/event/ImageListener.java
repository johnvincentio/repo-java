/*
 *  ImageListener.java Copyright (c) 2006,07 Swaroop Belur
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;

import net.sf.jdec.format.Formatter;
import net.sf.jdec.io.Writer;
import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.ui.adapter.DecompilerBridge;
import net.sf.jdec.ui.config.UIConfig;
import net.sf.jdec.ui.core.Console;
import net.sf.jdec.ui.core.JdecTree;
import net.sf.jdec.ui.core.Manager;
import net.sf.jdec.ui.main.UILauncher;
import net.sf.jdec.ui.util.ClassFileFilter;
import net.sf.jdec.ui.util.FileOpener;
import net.sf.jdec.ui.util.FindDialog;
import net.sf.jdec.ui.util.JavaFileFilter;
import net.sf.jdec.ui.util.LocalVariableWindow;
import net.sf.jdec.ui.util.LogWriter;
import net.sf.jdec.ui.util.Tips;
import net.sf.jdec.ui.util.UIConstants;
import net.sf.jdec.ui.util.UIUtil;
import net.sf.jdec.ui.util.editor.TextEditor;
import net.sf.jdec.config.Configuration;

public class ImageListener implements ActionListener {

	public void actionPerformed(ActionEvent e) {
		try {
			Manager.auxlabel.setText("");
			if (e.getActionCommand().equalsIgnoreCase("Format")) {
				UIUtil.codeReformatted = true;
				File file = UILauncher.getUIConfigRef().getCurrentOpFile();
				Formatter formatter = new Formatter(file);
				JTabbedPane tabs = UIUtil.getUIUtil().getRightTabbedPane();

				if (tabs != null) {
					tabs
							.setSelectedIndex(tabs
									.indexOfTab("Decompiled Output"));
					Component op = tabs.getSelectedComponent();
					JScrollPane opTab = (JScrollPane) op;
					Object o = opTab.getComponent(0);
					JViewport view = (JViewport) o;
					Object o2 = view.getView();
					if (o2 != null) {
						JEditorPane rdwrPane = (JEditorPane) o2;
						rdwrPane.setText(formatter.getOutput());
						JOptionPane.showMessageDialog(null,
								"Formatting done...");
					}
				}
				return;
			}
			if (e.getActionCommand().equals("back")) {
				ArrayList list = UIUtil.decompiledClasses;
				int pos = UIUtil.historyindex;
				// System.out.println(pos);
				String prev = "";
				if (pos != -1 && pos != 0) {
					if (list.size() > 1) {
						prev = (String) list.get((pos - 1));
						UIUtil.historyindex = pos - 1;
						JTabbedPane pane = getRightTabbedPane();
						displayHistoryFile(pane, prev);
					}
				} else if (pos == -1) {
					if (list.size() > 1) {
						prev = (String) list.get((list.size() - 2));
						UIUtil.historyindex = list.size() - 2;
						JTabbedPane pane = getRightTabbedPane();
						displayHistoryFile(pane, prev);
					}
				} else {
					Manager.auxlabel.setText("Could Not Load Previous FIle");
					Manager.auxlabel.setForeground(Color.BLUE);
					Manager.auxlabel.setBackground(Color.WHITE);
				}
				return;
			}
			if (e.getActionCommand().equals("forward")) {
				ArrayList list = UIUtil.decompiledClasses;
				int pos = UIUtil.historyindex;
				// System.out.println(pos);
				if (pos != -1 && pos < (list.size() - 1)) {
					if (list.size() > 1) {
						String next = (String) list.get((pos + 1));
						UIUtil.historyindex = pos + 1;
						JTabbedPane pane = getRightTabbedPane();
						displayHistoryFile(pane, next);
					}
				} else {
					Manager.auxlabel.setText("Could Not Load Next FIle");
					Manager.auxlabel.setForeground(Color.BLUE);
					Manager.auxlabel.setBackground(Color.WHITE);
				}

				return;

			}

			if (e.getActionCommand().equals("RunJDec")) {

				Thread t1 = new Thread() {

					public void run() {
						try {

							if (!UIUtil.getUIUtil().getJdecOption().equals(
									"decompileJar")) // Not Jar
							{

								String filename = UILauncher.getUIutil()
										.getJavaClassFile();
								File f = new File(filename);

								if (!f.exists()
										|| (filename != null && !filename
												.trim().endsWith(".class"))) {
									JOptionPane
											.showMessageDialog(UILauncher
													.getMainFrame(),
													"Chosen File is not a valid class file");
									Manager.getManager().setShowProgressBar(
											false);
									return;
								}

							}
							if (UIUtil.getUIUtil().getJdecOption().equals(
									"decompileJar")) {

								String jarfilename = UILauncher.getUIutil()
										.getJarFilePath();
								File f = new File(jarfilename);

								if (!f.exists()
										|| (jarfilename != null && !isValidArchiveFileExt(jarfilename
												.trim()))) {

									JOptionPane
											.showMessageDialog(UILauncher
													.getMainFrame(),
													"Chosen File is not a valid archive file");
									Manager.getManager().setShowProgressBar(
											false);
									return;
								}

							}
							Console c = UILauncher.getUIutil()
									.getConsoleFrame();
							if (c != null) {
								JEditorPane rdwr = c.getComponent();
								rdwr.setText("");
								String s = "Jdec Started..\n";
								s = rdwr.getText() + "\n\n" + s;
								rdwr.setText(s);
							}
							Manager.getManager().setShowProgressBar(true);
							DecompilerBridge bridge = DecompilerBridge
									.getInstance(UIUtil.getUIUtil());

							bridge
									.setOption(UIUtil.getUIUtil()
											.getJdecOption());

							if (c != null) {
								JEditorPane rdwr = c.getComponent();
								String s = "Current Task: "
										+ UILauncher.getUIutil()
												.getJdecOption() + "\n";
								s += "Current File :"
										+ UILauncher.getUIutil()
												.getJavaClassFile() + "\n";
								s = rdwr.getText() + "\n\n" + s;
								rdwr.setText(s);
							}

							if (UIUtil.getUIUtil().getJdecOption().equals(
									"decompileJar")) {
								// Configuration.setkFolderPath(Configuration.getOutputFolderPath()+File.separator+"JARDECOMPILED");
								UILauncher.getUIutil().setJarOption(true);
							}

							JTabbedPane tabs = getRightTabbedPane();
							int index = tabs.indexOfTab("Jdec Configuration");
							if (index != -1)
								tabs.remove(index);

							index = tabs.indexOfTab("Constant Pool");
							if (index != -1)
								tabs.remove(index);
							index = tabs.indexOfTab("Class Details");
							if (index != -1)
								tabs.remove(index);
							index = tabs.indexOfTab("Exception Tables");
							if (index != -1)
								tabs.remove(index);
							index = tabs.indexOfTab("Local Variables");
							if (index != -1)
								tabs.remove(index);
							index = tabs.indexOfTab("System Properties");
							if (index != -1)
								tabs.remove(index);

							// For LocalVariables...Special Handling
							if (UIUtil.getUIUtil().getJdecOption().equals(
									"localVariables")) {

								LocalVariableWindow lv = new LocalVariableWindow(
										"null");

								JTabbedPane rightTab = getRightTabbedPane();
								int i = rightTab.indexOfTab("Local Variables");
								if (i != -1)
									rightTab.remove(index);
								rightTab.addTab("Local Variables", lv);
								rightTab.setSelectedComponent(lv);
								Manager.getManager().setShowProgressBar(false);
								return;
							}

							bridge.execute();

							if (c != null) {
								JEditorPane rdwr = c.getComponent();
								if (!UIUtil.getUIUtil().getJdecOption().equals(
										"decompileJar")) {
									String s = "Done..Please wait for Jdec UI to display the result...\n";
									s = rdwr.getText() + "\n\n" + s;
									rdwr.setText(s);
								} else {
									String s = "Done..Please check up the Jar Tab to verify the result\n";
									s = rdwr.getText() + "\n\n" + s;
									rdwr.setText(s);
								}
							}
							if (UIUtil.getUIUtil().getJdecOption().equals(
									"decompileJar") == false) {
								bridge.setOutputFiles(new File[] { new File(
										ConsoleLauncher.getResultFileName()) });
								bridge.showResult(getRightTabbedPane());
								ConsoleLauncher.setCurrentSourceFile(new File(
										UILauncher.getUIutil()
												.getJavaClassFile()));
								ConsoleLauncher
										.setCurrentDecompiledFile(UILauncher
												.getUIutil().getJavaClassFile()); // Duplicate
								// Method...can
								// use
								// getCurrentSourceFile
								// itself...
							}

						} catch (Throwable t) // TO shut down the progress bar
						{

							Manager.getManager().setShowProgressBar(false);
							try {
								t.printStackTrace(Writer.getWriter("log"));
							} catch (IOException e) {

							}
						}

					}
				};

				Thread t2 = new Thread() {

					public void run() {

						JDialog proframe = UIUtil.launchProgressBarFrame();
						if (proframe != null) {
							proframe.setVisible(true);
						}

						while (Manager.getManager().isShowProgressBar() == true) {

							gotoSleep();
						}

						if (!Manager.getManager().isShowProgressBar()) {
							if (proframe != null) {
								proframe.setVisible(false);
								proframe.dispose();
							}
							proframe = null;
							if (ConsoleLauncher.fatalErrorOccured)
								JOptionPane.showMessageDialog(UILauncher
										.getMainFrame(),
										UIConstants.jdecTaskError,
										"Jdec Status",
										JOptionPane.INFORMATION_MESSAGE);
						}

					}

				};

				Manager.getManager().setWorkerThreadRef(t1);
				Manager.getManager().setStatusThreadRef(t2);

				if (UIUtil.checkForInvalidEntries("run")) {
					t1.setPriority(Thread.MAX_PRIORITY);
					t2.setPriority(Thread.NORM_PRIORITY);
					t1.start();
					t2.start();
				} else {
					JOptionPane
							.showMessageDialog(
									UILauncher.getMainFrame(),
									"Please Check The Decompiler Configuration Property Values Again",
									"JDEC WARNING",
									JOptionPane.INFORMATION_MESSAGE);
				}

			} else if (e.getActionCommand().equals("Save")) {
				JTabbedPane tb = getRightTabbedPane();
				int index = tb.getSelectedIndex();
				String s = UIUtil.getUIUtil().saveAsOrSave(index);
				if (s.equals("saveas"))
					saveAsWork();
				else {
					// saveWork();
					String file = UIUtil.getUIUtil().getFileNameForTab(index);
					Component editor = tb.getSelectedComponent();
					JScrollPane editorTab = (JScrollPane) editor;
					Object o = editorTab.getComponent(0);
					JViewport view = (JViewport) o;
					Object o2 = view.getView();
					if (o2 != null && file != null) {
						JEditorPane rdwrPane = (JEditorPane) o2;
						String content = rdwrPane.getText();
						File f = new File(file);
						FileOutputStream fos = new FileOutputStream(f);
						fos.write(content.getBytes());
						fos.flush();
						fos.close();
						fos = null;
					}

				}

			} else if (e.getActionCommand().equals("Home")) {
				// TODO
			} else if (e.getActionCommand().equals("Send Mail")) {
				// fixmelater : seems far fetched
				// original intention was to mail
				// decompiled file.Again something
				// fancy

			} else if (e.getActionCommand().equals("Search")) {

				FindDialog find = new FindDialog(UILauncher.getMainFrame());
				find.setVisible(true);

			} else if (e.getActionCommand().equals("Open")) {
				FileOpener opener = new FileOpener(new ClassFileFilter());
				Manager manager = Manager.getManager();
				ArrayList paneList = manager.getCurrentSplitPaneComponents();
				JTabbedPane tabs = UIUtil.getUIUtil().getRightTabbedPane();
				if (tabs != null) {
					tabs
							.setSelectedIndex(tabs
									.indexOfTab("Jdec Editor Window"));
					int pos = tabs.getSelectedIndex();
					Component editor = tabs.getSelectedComponent();
					JScrollPane editorTab = (JScrollPane) editor;
					File file = opener.getSelectedFile();
					if (file == null)
						return;
					FileReader reader = null;
					try {
						reader = new FileReader(file);
						Object o = editorTab.getComponent(0);
						// System.out.println(o.getClass());
						JViewport view = (JViewport) o;
						Object o2 = view.getView();
						if (o2 != null) {
							JEditorPane rdwrPane = (JEditorPane) o2;
							UILauncher.getUIutil().addRecentFile(file);
							UILauncher.getUIutil().setCurrentOpenFile(
									file.getAbsolutePath());
							UILauncher.getUIutil().addToTabFileMap(pos,
									file.getAbsolutePath());
							rdwrPane.read(reader, null);

						}
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(
								UILauncher.getMainFrame(), "File Not Found",
								"ERROR", JOptionPane.ERROR_MESSAGE);
					} finally {
						if (reader != null) {
							try {
								reader.close();
							} catch (IOException x) {
							}
						}
					}

				}
			} else if (e.getActionCommand().equals("FileSystem")) {

				// fixmelater No Need to implement actually
			} else if (e.getActionCommand().equals("Help")) {

				// fixmelater On Need basis
			} else if (e.getActionCommand().equals("NewTask")) {
				JPopupMenu popup = new JPopupMenu();
				JOptionPane option = new JOptionPane(
						"Please Specify Your Choice...",
						JOptionPane.INFORMATION_MESSAGE,
						JOptionPane.YES_NO_CANCEL_OPTION);
				// popup.add(option);
				final JFrame in = new JFrame("New Decompiler Task Window");
				in.setResizable(true);
				// in.getContentPane().setLayout(new GridLayout(2, 4));
				JCheckBox Class = new JCheckBox("Decompile Class File");
				// JCheckBox Jar=new JCheckBox("Decompile Archive File");
				// JCheckBox classInJar=new JCheckBox("Class In Archive Task");
				JCheckBox Jar = new JCheckBox(
						"Decompile Archive File                     [*** Updated]");
				Jar.setActionCommand("Decompile Archive File");
				JCheckBox classInJar = new JCheckBox(
						"Class In Archive                       [*** New]");
				JCheckBox versions = new JCheckBox(
						"Decompile 2 versions of class File    [*** New]");
				final String hintm = "Click on ....[for updating settings related to archive file task.]\n1>[Edit-->Archive settings...] \n\t2>Jar Level Filters[Configuration-->[Jdec(Decompiler) Filters]";
				JLabel archlbl = new JLabel(hintm);// "Click on [Edit-->Archive
				// settings...] for updating
				// settings related to
				// archive file task.");

				archlbl.setForeground(Color.BLUE);
				// JCheckBox process=new JCheckBox("Process Class File");
				// JCheckBox project=new JCheckBox("Create jdec project");
				ButtonGroup bg = new ButtonGroup();
				bg.add(Class);
				bg.add(Jar);
				bg.add(classInJar);
				bg.add(versions);
				// bg.add(process);
				// bg.add(project);
				JPanel basic = new JPanel();
				BoxLayout box = new BoxLayout(basic, BoxLayout.Y_AXIS);
				basic.setLayout(box);
				basic.add(Class);
				basic.add(Jar);
				basic.add(classInJar);
				basic.add(versions);
				JPanel pp = new JPanel();
				BoxLayout box2 = new BoxLayout(pp, BoxLayout.X_AXIS);
				pp.setLayout(box2);
				// pp.add(process);
				JLabel lab = new JLabel("              More Info");
				lab.setForeground(Color.BLUE);
				// lab.setFont(new Font("MONOSPACE",Font.BOLD,12));
				lab.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						String desc = "This task will process the class file: \n";
						desc += "Namely constant pool and determine all methods,constructors\n";
						desc += "But will not decompile any method\n";
						desc += "User will be presented the method and field list as a tree structure\n";
						desc += "When the user chooses a method, then the method will be decompiled\n";
						desc += "\nHow will this option be useful?\n";
						desc += "If the user wishes to see the content of only one method\n";
						desc += "then it will be waste of time to decompile the entire class\n";
						JOptionPane.showMessageDialog(null, desc);
					}
				});
				// pp.add(lab);
				// BoxLayout box=new BoxLayout(in,BoxLayout.Y_AXIS);
				// in.getContentPane().setLayout(box);
				in.getContentPane().add(basic, BorderLayout.NORTH);
				in.getContentPane().add(pp, BorderLayout.CENTER);
				JPanel pjp = new JPanel();
				BoxLayout box3 = new BoxLayout(pjp, BoxLayout.X_AXIS);

				pjp.setLayout(box3);
				JLabel lab2 = new JLabel("             More Info");
				lab2.setForeground(Color.BLUE);
				// lab.setFont(new Font("MONOSPACE",Font.BOLD,12));
				lab2.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						String desc = "This task will allow the user to select multiple \n";
						desc += "jars,zip files,folders containing class files\n";
						desc += "All jars will be expanded and presented as a tree structure\n";
						desc += "User can then choose any class file to decompile\n";
						desc += "\nwhats special about this option?\n";
						desc += "User can select the folder depth to choose jars/zips,class files\n";
						JOptionPane.showMessageDialog(null, desc);
					}
				});
				pjp.setLayout(box3);
				// intm="Click on ....\n1>[Edit-->Archive settings...] for
				// updating settings related to archive file task.\n\t2>Jar
				// Level Filters[Configuration-->[Jdec(Decompiler) Filters]";
				JButton hint = new JButton("Hint");
				//JButton close = new JButton("Close");
				pjp.add(hint);
				hint.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent ae) {
						JOptionPane.showMessageDialog(in, hintm);

					}

				});
				/*close.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent ae) {
						in.setVisible(false);
						in.dispose();
					}
				});*/
				pjp.add(new JLabel("			"));
				//pjp.add(close);
				// p.add(project);
				// pjp.add(lab2);
				in.getContentPane().add(pjp, BorderLayout.SOUTH);

				in.pack();
				in.setBounds(300, 300, 430, 190);
				NewTaskListener nwtsk = NewTaskListener.getInstance();
				Class.addActionListener(nwtsk);
				Jar.addActionListener(nwtsk);
				classInJar.setActionCommand("classinjar");
				classInJar.addActionListener(nwtsk);
				versions.setActionCommand("versions");
				versions.addActionListener(nwtsk);
				in.setVisible(true);
				UILauncher.addTempFrame(in);
				UILauncher.addChildRef(in);

			} else if (e.getActionCommand().equals("tech")) {
				// NO need to fix

			} else if (e.getActionCommand().equals("tip")) {
				Tips tips = new Tips();

			} else if (e.getActionCommand().equals("Stop")) {
				// fixmelater ... Will be good to implement
				// Like to stop a jar from being processed
				// If it takes too long a time or progressbar
				// gets into infinite loop

			} else if (e.getActionCommand().equals("print")) {
				// fixmelater .... More of an Editor feature..Depends
			} else if (e.getActionCommand().equals("editin")) {
				TextEditor texteditor = new TextEditor();
				texteditor.setVisible(true);
				JTabbedPane tab = getRightTabbedPane();
				Component c = tab.getSelectedComponent();
				JScrollPane pane = (JScrollPane) c;
				JViewport view = (JViewport) pane.getViewport();
				JEditorPane editor = (JEditorPane) view.getView();
				editor.selectAll();
				editor.copy();
				editor.paste();

				int counter = 1;
				JScrollPane scpane = texteditor.getTextComponent();
				JViewport jview = scpane.getViewport();
				JTextArea jtext = (JTextArea) jview.getView();
				jtext.paste();

				/***************************************************************
				 * Clipboard clip=editor.getToolkit().getSystemClipboard();
				 * Transferable tr=clip.getContents(null); if(tr!=null) {
				 * DataFlavor[] fl=tr.getTransferDataFlavors() ; if(fl!=null) {
				 * ArrayList temp=new ArrayList();
				 * 
				 * for(int s=0;s<fl.length;s++) { if(fl[s].isFlavorTextType()) {
				 * Object o=tr.getTransferData(fl[s]) ; if(o instanceof
				 * java.lang.String) { // check whether already added
				 * if(temp.size()==0)temp.add(o.toString()); else {
				 * java.lang.String str[]=(String[])temp.toArray(new
				 * String[]{}); Arrays.sort(str); for(int i=1;i<str.length;i++) {
				 * 
				 * String content=str[i]; String prev=str[i-1];
				 * if(content.equals(prev)) { } else { temp.add(o.toString());
				 * //text.append(counter++ +"\t\t"+o.toString()+"\n"); } } }
				 *  } } }
				 * 
				 * for(int y=0;y<temp.size();y++) {
				 * 
				 * String s1=(String)temp.get(y); text.append(s1); }
				 * 
				 * if(text.toString().length() > 0) { JScrollPane
				 * scpane=texteditor.getTextComponent(); JViewport
				 * jview=scpane.getViewport(); JEditorPane
				 * jtext=(JEditorPane)jview.getView();
				 * jtext.setText(text.toString()); } } }
				 **************************************************************/

			} else if (e.getActionCommand().equals("close")) {
				try {
					UIConfig uiconfig = UILauncher.getUIConfigRef();
					uiconfig.persistToFile();
					UILauncher.closeChildWindows();
				} catch (IOException ioe) {
					// Ignore
				}

				int option = JOptionPane.showConfirmDialog(UILauncher
						.getMainFrame(), "Are You Sure?", "Close Jdec UI",
						JOptionPane.YES_NO_OPTION);
				if (option == JOptionPane.YES_OPTION) {
					boolean b = UILauncher.getUIutil().hasConfigChanged();
					if (b) {
						int opt = JOptionPane
								.showConfirmDialog(
										UILauncher.getMainFrame(),
										"The Decompiler Configuration Has been updated...\nWould You Like to save the changes...",
										"Update Configuration ...?",
										JOptionPane.YES_NO_OPTION);
						if (opt == JOptionPane.YES_OPTION) {

							File src = new File(System.getProperty("user.home")
									+ File.separator + "tempconfig.properties");
							File dest = new File("config.properties");
							UILauncher.getUIutil().copyFile(src, dest);
						}
						if (opt == JOptionPane.NO_OPTION) {
							UILauncher
									.getMainFrame()
									.setDefaultCloseOperation(
											UILauncher.getMainFrame().DO_NOTHING_ON_CLOSE);
						}

						if (opt == JOptionPane.CANCEL_OPTION) {

						}

					}

					JFrame frame = UILauncher.getMainFrame();
					frame.setVisible(false);
					frame.setEnabled(false);
					frame.dispose();
				}

				LogWriter lw = LogWriter.getInstance();
				if (lw != null) {
					lw.close();
				}
			}

			else if (e.getActionCommand().equals("clear")) {
				Manager manager = Manager.getManager();
				ArrayList paneList = manager.getCurrentSplitPaneComponents();
				Console c = null;
				for (int s = 0; s < paneList.size(); s++) {
					Object current = paneList.get(s);
					if (current instanceof Console) {
						c = (Console) current;
						break;
					}

				}
				Object o = c.getComponent();

				if (o != null) {
					JEditorPane rdwrPane = (JEditorPane) o;
					rdwrPane.setText("");
				}

			} else if (e.getActionCommand().equals("editor")) {
				TextEditor editor = new TextEditor();
				// System.out.println("Simple Editor"+editor);
				editor.setDefaultCloseOperation(editor.DISPOSE_ON_CLOSE);
				editor.setVisible(true);

			} else if (e.getActionCommand().equals("sound")) {

				// fixmelater ...Something fancy :) Depends again
				// The purpose was to use java sound API
				// TO sing out to the user the creators of the tool
				// Ha ha ha

			} else if (e.getActionCommand().equals("binary")) {

				FileOpener opener = new FileOpener(new ClassFileFilter());
				Manager manager = Manager.getManager();
				ArrayList paneList = manager.getCurrentSplitPaneComponents();
				JTabbedPane tabs = UIUtil.getUIUtil().getRightTabbedPane();
				if (tabs != null) {
					tabs
							.setSelectedIndex(tabs
									.indexOfTab("Jdec Editor Window"));
					int pos = tabs.getSelectedIndex();
					Component editor = tabs.getSelectedComponent();
					// System.out.println(editor.getClass());
					JScrollPane editorTab = (JScrollPane) editor;
					File file = opener.getSelectedFile();

					if (file == null)
						return;
					FileReader reader = null;
					File temp = getBinaryContentForFile(file);
					if (temp == null) {
						throw new Exception();
					}
					try {
						reader = new FileReader(temp);
						Object o = editorTab.getComponent(0);
						// System.out.println(o.getClass());
						JViewport view = (JViewport) o;
						Object o2 = view.getView();
						if (o2 != null) {
							JEditorPane rdwrPane = (JEditorPane) o2;
							UILauncher.getUIutil().addRecentFile(file);
							UILauncher.getUIutil().setCurrentOpenFile(
									file.getAbsolutePath());
							UILauncher.getUIutil().addToTabFileMap(pos,
									file.getAbsolutePath());
							rdwrPane.read(reader, null);

						}
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(
								UILauncher.getMainFrame(), "File Not Found",
								"ERROR", JOptionPane.ERROR_MESSAGE);
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(
								UILauncher.getMainFrame(),
								"Could Not Load File", "ERROR",
								JOptionPane.ERROR_MESSAGE);
					} finally {
						if (reader != null) {
							try {
								reader.close();
							} catch (IOException x) {
							}
						}
					}

				}

			}

			else if (e.getActionCommand().equals("go")) {

				File path = UILauncher.getUIutil().getBrowserPath();
				try {
					Runtime rt = Runtime.getRuntime();
					Process p = rt.exec(path.getAbsolutePath() + " "
							+ "http://jdec.sourceforge.net/");

				} catch (Throwable t) {
					JOptionPane
							.showMessageDialog(null,
									"Please set Browser Path first[Edit-->preferences]");
					try {
						LogWriter lg = LogWriter.getInstance();
						lg
								.writeLog("[ERROR]: Method: go\n\tClass: ImageListener.class");
						lg
								.writeLog("------------------------------------------------");
						lg.writeLog("Exception Stack Trace");
						t.printStackTrace(lg.getPrintWriter());
						lg.flush();

					} catch (Exception ex) {

					}

				}

			} else if (e.getActionCommand().equals("refresh")) {
				Thread t1 = new Thread() {
					public void run() {
						Manager.getManager().setShowProgressBar(true);
						Console c = UILauncher.getUIutil().getConsoleFrame();
						if (c != null) {
							JEditorPane rdwr = c.getComponent();
							rdwr.setText("");
							String s = "Jdec Started..\n";
							s = rdwr.getText() + "\n\n" + s;
							rdwr.setText(s);
						}
						java.lang.String currentDecFile = ConsoleLauncher
								.getCurrentDecompiledFile();
						if (currentDecFile == null
								|| currentDecFile.trim().length() == 0) {
							Manager.getManager().setShowProgressBar(false);
							JOptionPane.showMessageDialog(UILauncher
									.getMainFrame(), "No file To Refresh");
							return;
						}
						DecompilerBridge bridge = DecompilerBridge
								.getInstance(UIUtil.getUIUtil());
						bridge.setClassFile(currentDecFile);

						if (c != null) {
							JEditorPane rdwr = c.getComponent();

							String s = "Current Task : Refresh Class File ->"
									+ currentDecFile;
							s = rdwr.getText() + "\n\n" + s;
							rdwr.setText(s);
						}

						bridge.execute("decompileClass", "", false, "null");
						if (c != null) {
							JEditorPane rdwr = c.getComponent();

							String s = "Done ...Please Wait For Jdec UI to Refresh the output";
							s = rdwr.getText() + "\n\n" + s;
							rdwr.setText(s);
						}
						JTabbedPane rightTab = getRightTabbedPane();
						bridge.showResult(rightTab);
						String simpleName = currentDecFile;
						int sep = simpleName.lastIndexOf(File.separator);
						if (sep != -1) {
							simpleName = simpleName.substring(sep + 1);
						}
						JOptionPane.showMessageDialog(
								UILauncher.getMainFrame(),
								"Refreshed Java Class File : " + simpleName);

					}
				};
				Thread t2 = new Thread() {

					public void run() {

						JDialog proframe = UIUtil.launchProgressBarFrame();
						if (proframe != null)
							proframe.setVisible(true);
						// proframe.requestFocusInWindow();

						while (Manager.getManager().isShowProgressBar() == true) {

							gotoSleep();
						}

						if (!Manager.getManager().isShowProgressBar()) {
							if (proframe != null) {
								proframe.setVisible(false);
								proframe.dispose();
							}
							proframe = null;

						}

					}

				};
				t1.setPriority(Thread.MAX_PRIORITY);
				Manager.getManager().setStatusThread(t1);
				t2.setPriority(Thread.NORM_PRIORITY);
				Manager.getManager().setShowProgressBar(true);
				t2.start();
				t1.start();

			} else if (e.getActionCommand().equals("jdec")) {
				// fixmelater..Will be good
				// The purpose was to educate the users
				// how a jdec works. Did not implement
				// for lack of time. THIS WILL BE USP OF THE TOOL
				// IF IMPLEMENTED. BUT HAS TO BE INNOVATIVE

			}
		} catch (Throwable t) {
			try {
				LogWriter lg = LogWriter.getInstance();
				lg
						.writeLog("[ERROR]: Method: actionPerformed\n\tClass:ImageListener.class");
				lg.writeLog("------------------------------------------------");
				lg.writeLog("Exception Stack Trace");
				t.printStackTrace(lg.getPrintWriter());
				lg.flush();

			} catch (Exception exp) {

			}
		}

	}

	private JTabbedPane getRightTabbedPane() {
		Manager manager = Manager.getManager();
		ArrayList paneList = manager.getCurrentSplitPaneComponents();
		JTabbedPane tabs = null;
		for (int s = 0; s < paneList.size(); s++) {
			Object current = paneList.get(s);
			if (current instanceof JTabbedPane) {
				tabs = (JTabbedPane) current;
				Component c = tabs.getComponent(0);
				if ((c instanceof JdecTree) == false) {
					if (tabs.getTabCount() > 2) {
						break;
					}
				}

			}
		}

		return tabs;

	}

	private void saveAsWork() {
		Manager manager = Manager.getManager();
		ArrayList paneList = manager.getCurrentSplitPaneComponents();
		JTabbedPane tabs = getRightTabbedPane();
		if (tabs != null) {

			Component editor = tabs.getSelectedComponent();
			JScrollPane editorTab = (JScrollPane) editor;
			JFileChooser chooser = new JFileChooser();
			JavaFileFilter filter = new JavaFileFilter();
			// chooser.setFileFilter(filter);
			if (chooser.showSaveDialog(UILauncher.getMainFrame()) != JFileChooser.APPROVE_OPTION)
				return;
			File file = chooser.getSelectedFile();

			if (file == null)
				return;
			FileWriter writer = null;
			try {
				String fname = file.getAbsolutePath();
				if (fname.endsWith(".java") == false) {
					file = new File(fname + ".java");
				}
				writer = new FileWriter(file);
				Object o = editorTab.getComponent(0);
				// System.out.println(o.getClass());
				JViewport view = (JViewport) o;
				Object o2 = view.getView();
				if (o2 != null) {
					JEditorPane rdwrPane = (JEditorPane) o2;

					rdwrPane.write(writer);
					UIUtil.getUIUtil().setFileTabMap(tabs.getSelectedIndex(),
							file.getAbsolutePath());
				}
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(UILauncher.getMainFrame(),
						"File Not Saved", "ERROR", JOptionPane.ERROR_MESSAGE);
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException x) {
					}
				}
			}

		}

	}

	private void gotoSleep() {
		try {
			Thread.sleep(1000);
			// i++;
		} catch (InterruptedException e) {

		}
		// System.out.println(i);

	}

	private File getBinaryContentForFile(File file) throws Exception {
		if (!file.exists()) {
			throw new IOException("File Not Valid");
		}

		java.lang.String t = Configuration.getTempDir();
		File e = new File(t);
		File out;
		if (e.exists()) {
			out = new File(t + File.separator + "binary.txt");
		} else {
			out = new File(System.getProperty("user.home") + File.separator
					+ "binary.txt");
		}

		// BufferedReader dis=new BufferedReader(new InputStreamReader(new
		// DataInputStream(new FileInputStream(file))));
		DataInputStream dis = new DataInputStream(new FileInputStream(file));
		// BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new
		// DataOutputStream(new FileOutputStream(out))));
		DataOutputStream bw = new DataOutputStream(new FileOutputStream(out));
		String i = dis.readUTF();
		while (i != null) {
			try {
				bw.writeUTF(i);
				i = dis.readUTF();
			} catch (IOException ioe) {
				break;
			}
		}
		bw.flush();
		bw.close();
		return out;

	}

	private void displayHistoryFile(JTabbedPane tabs, String s) {
		try {
			tabs.setSelectedIndex(tabs.indexOfTab("Decompiled Ouput"));
			Component editor = tabs.getSelectedComponent();
			JScrollPane editorTab = (JScrollPane) editor;
			Object o = editorTab.getComponent(0);
			JViewport view = (JViewport) o;
			Object o2 = view.getView();
			if (o2 != null) {
				JEditorPane rdwrPane = (JEditorPane) o2;

				if (s != null) {
					File f = new File(s);
					FileReader fr = new FileReader(f);
					rdwrPane.read(fr, null);
					rdwrPane.setEditable(true);
					Manager.auxlabel.setForeground(Color.BLUE);
					Manager.auxlabel.setBackground(Color.WHITE);
					int sl = s.lastIndexOf("/");
					if (sl == -1) {
						sl = s.lastIndexOf("\\");
					}
					if (sl == -1) {
						Manager.auxlabel.setForeground(Color.BLUE);
						Manager.auxlabel.setBackground(Color.WHITE);
						Manager.auxlabel.setText("Loaded File " + s);

					} else {
						s = s.substring(sl + 1);
						Manager.auxlabel.setForeground(Color.BLUE);
						Manager.auxlabel.setBackground(Color.WHITE);
						Manager.auxlabel.setText("Loaded File " + s);
					}

				}

			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Error while going back...");

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error while going back...");

		}

	}

	private boolean isValidArchiveFileExt(String filename) {
		if (filename == null)
			return false;
		String types = UILauncher.getUIConfigRef().getArchiveTypes();
		StringTokenizer st = new StringTokenizer(types, ",");
		while (st.hasMoreTokens()) {
			String c = (String) st.nextToken();
			if (filename.endsWith(c)) {
				return true;
			}
		}
		return false;
	}

}
