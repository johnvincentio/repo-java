/*
 *  JDecTree.java Copyright (c) 2006,07 Swaroop Belur
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

import net.sf.jdec.config.Configuration;
import net.sf.jdec.core.InnerClassTracker;
import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.ui.adapter.DecompilerBridge;
import net.sf.jdec.ui.main.UILauncher;
import net.sf.jdec.ui.util.LogWriter;
import net.sf.jdec.ui.util.UIConstants;
import net.sf.jdec.ui.util.UIUtil;
import net.sf.jdec.util.ClassStructure;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class JdecTree extends JInternalFrame {

	protected JTree mainTree;

	protected DefaultTreeModel treeModel;

	private ArrayList methodList = null;

	private boolean process = true;

	public JdecTree() {
		super("File/Folder List On System");
		TreeObject to = new TreeObject("Directory-Structure");
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(to);
		DefaultMutableTreeNode node;
		File[] roots = File.listRoots();
		for (int k = 0; k < roots.length; k++) {
			node = new DefaultMutableTreeNode(new TreeObject(roots[k]));
			node.add(new DefaultMutableTreeNode("Getting Child Nodes For "
					+ roots[k].getAbsolutePath()));
			top.add(node);
		}
		to.setRoots(roots);
		treeModel = new DefaultTreeModel(top);
		mainTree = new JTree(treeModel);
		mainTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		mainTree.addTreeExpansionListener(new JdecTreeExpansionListener());
		mainTree.addTreeSelectionListener(new JdecTreeSelectionListener());
		mainTree.setShowsRootHandles(true);
		mainTree.setEditable(false);
		JScrollPane s = new JScrollPane();
		s.getViewport().add(mainTree);
		getContentPane().add(s, BorderLayout.CENTER);
		// mainTree.setBackground(Color.LIGHT_GRAY);
		setVisible(true);
	}

	public JdecTree(String dirStart) {
		super("File/Folder List On System");
		TreeObject tobj = new TreeObject("Directory-Structure");
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(tobj);
		DefaultMutableTreeNode node;
		File roots[] = { new File(dirStart) };
		for (int k = 0; k < roots.length; k++) {
			TreeObject to = new TreeObject(roots[k]);
			to.setPassedDirectoryList(dirStart);
			node = new DefaultMutableTreeNode(to);
			node.add(new DefaultMutableTreeNode("Getting Child Nodes For "
					+ roots[k].getAbsolutePath()));
			top.add(node);
		}
		tobj.setRoots(roots);
		treeModel = new DefaultTreeModel(top);
		mainTree = new JTree(treeModel);
		mainTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		mainTree.addTreeExpansionListener(new JdecTreeExpansionListener());
		mainTree.addTreeSelectionListener(new JdecTreeSelectionListener());
		mainTree.setShowsRootHandles(true);
		mainTree.setEditable(false);
		JScrollPane s = new JScrollPane();
		s.getViewport().add(mainTree);
		// mainTree.setBackground(Color.LIGHT_GRAY);
		getContentPane().add(s, BorderLayout.CENTER);
		setVisible(true);
	}

	// Use for favorite display only
	public JdecTree(String[] favorite) {
		super("Favorite Folder List On System");
		if (favorite == null || favorite.length == 0)
			return;
		TreeObject to = new TreeObject("Directory-Structure");
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(to);
		DefaultMutableTreeNode node;
		File roots[] = new File[favorite.length];
		for (int z = 0; z < favorite.length; z++) {
			roots[z] = new File(favorite[z].toString());
		}
		for (int k = 0; k < roots.length; k++) {
			node = new DefaultMutableTreeNode(new TreeObject(roots[k]));
			node.add(new DefaultMutableTreeNode("Getting Child Nodes For "
					+ roots[k].getAbsolutePath()));
			top.add(node);
		}
		to.setRoots(roots);
		treeModel = new DefaultTreeModel(top);
		mainTree = new JTree(treeModel);
		mainTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		mainTree.addTreeExpansionListener(new JdecTreeExpansionListener());
		mainTree.addTreeSelectionListener(new JdecTreeSelectionListener());
		mainTree.setShowsRootHandles(true);
		mainTree.setEditable(false);
		JScrollPane s = new JScrollPane();
		s.getViewport().add(mainTree);
		// mainTree.setBackground(Color.LIGHT_GRAY);
		getContentPane().add(s, BorderLayout.CENTER);
		setVisible(true);
	}

	// For methods display
	public JdecTree(ArrayList methods) {
		super("List of Methods In Current Class");
		methodList = methods;
		if (methods == null || methods.size() == 0)
			return;
		TreeObject to = new TreeObject("Directory-Structure");
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(to);
		DefaultMutableTreeNode node;
		String m[] = (String[]) methods.toArray(new String[methods.size()]);
		File roots[] = new File[m.length];
		for (int z = 0; z < m.length; z++) {
			roots[z] = new File(m[z].toString());
		}
		for (int k = 0; k < roots.length; k++) {
			node = new DefaultMutableTreeNode(new TreeObject(roots[k]));
			top.add(node);

		}
		to.setRoots(roots);
		treeModel = new DefaultTreeModel(top);
		mainTree = new JTree(treeModel);
		mainTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		mainTree.addTreeExpansionListener(new JdecTreeExpansionListener());
		mainTree.addTreeSelectionListener(new JdecTreeSelectionListener());
		mainTree.setShowsRootHandles(true);
		mainTree.setEditable(false);
		s = new JScrollPane();
		s.getViewport().add(mainTree);
		tiplabel = new JLabel("Hover to view ToolTip");

		UIUtil.getUIUtil().tiplabel = tiplabel;
		tiplabel.setFont(new Font("Dialog", Font.BOLD, 11));
		tiplabel.setForeground(new Color(51, 0, 51));
		// mainTree.setBackground(Color.LIGHT_GRAY);
		getContentPane().add(tiplabel, BorderLayout.NORTH);
		getContentPane().add(s, BorderLayout.CENTER);

		Thread b = new Thread(new blink());
		b.start();
		setVisible(true);
	}

	public JdecTree(ClassStructure cs, boolean b) {
		super("Class Structure");
		DefaultMutableTreeNode node;
		if (cs == null)
			return;
		DefaultMutableTreeNode header = new DefaultMutableTreeNode(
				new TreeObject("Class Structure..."));
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(new TreeObject(
				cs.getName()));
		header.add(top);
		ArrayList structure = cs.getMethods();
		String m[] = (String[]) structure.toArray(new String[structure.size()]);
		for (int k = 0; k < m.length; k++) {
			node = new DefaultMutableTreeNode(new TreeObject(m[k], true));
			top.add(node);

		}
		// Add inner classes

		ArrayList innercls = cs.getInnerclasses();
		structure = new ArrayList();
		for (int x = 0; x < innercls.size(); x++) {
			ClassStructure temp = (ClassStructure) innercls.get(x);
			String name = temp.getName();
			structure.add(name);

		}

		m = (String[]) structure.toArray(new String[structure.size()]);

		for (int k = 0; k < m.length; k++) {
			node = new DefaultMutableTreeNode(new TreeObject(m[k], true));
			node.add(new DefaultMutableTreeNode("Scanning...."));
			top.add(node);

		}

		treeModel = new DefaultTreeModel(top);
		mainTree = new JTree(treeModel);
		mainTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		mainTree
				.addTreeExpansionListener(new JDecStructureTreeExpansionListener());
		mainTree
				.addTreeSelectionListener(new JdecStructureTreeSelectionListener());
		mainTree.setShowsRootHandles(true);
		mainTree.setEditable(false);
		s = new JScrollPane();
		s.getViewport().add(mainTree);
		tiplabel = new JLabel("Hover to view ToolTip");

		UIUtil.getUIUtil().tiplabel = tiplabel;
		tiplabel.setFont(new Font("Dialog", Font.BOLD, 11));
		tiplabel.setForeground(new Color(51, 0, 51));
		// mainTree.setBackground(Color.LIGHT_GRAY);
		if (b)
			getContentPane().add(tiplabel, BorderLayout.NORTH);
		getContentPane().add(s, BorderLayout.CENTER);

		Thread blnk = new Thread(new blink());
		blnk.start();
		setVisible(true);
	}

	JLabel tiplabel = null;

	JScrollPane s;

	public JLabel getTipLabel() {
		return tiplabel;
	}

	public JScrollPane getScrollPane() {
		return s;
	}

	public JTree getTree() {
		return mainTree;
	}

	DefaultMutableTreeNode getTreeNode(TreePath path) {
		return (DefaultMutableTreeNode) (path.getLastPathComponent());
	}

	static TreeObject getFileNode(DefaultMutableTreeNode node) {
		if (node == null)
			return null;
		Object obj = node.getUserObject();
		if (obj instanceof TreeObject)
			return (TreeObject) obj;
		else
			return null;
	}

	class JdecTreeExpansionListener implements TreeExpansionListener {
		public void treeExpanded(TreeExpansionEvent event) {
			process = true;
			final DefaultMutableTreeNode node = getTreeNode(event.getPath());
			final TreeObject fnode = getFileNode(node);
			if (fnode != null
					&& TreeUtil.showChildren(node, fnode.getFile(), fnode)) {
				treeModel.reload(node);
			}
			/*
			 * Thread runner = new Thread() { public void run() { if (fnode !=
			 * null && TreeUtil.showChildren(node,fnode.getFile())) { Runnable
			 * runnable = new Runnable() { public void run() {
			 * treeModel.reload(node); } } ; 7(runnable); } } } ;
			 * runner.start();
			 */
		}

		public void treeCollapsed(TreeExpansionEvent event) {
			JdecTree.this.process = false;
		}

	}

	public class JdecStructureTreeSelectionListener implements
			TreeSelectionListener {

		private void gotoSleep() {
			try {
				Thread.sleep(1000);

			} catch (InterruptedException e) {

			}

		}

		public void valueChanged(TreeSelectionEvent event) {
			DefaultMutableTreeNode node = getTreeNode(event.getPath());
			// int in=treeModel.getIndexOfChild(node.getParent(),node);
			final TreeObject fnode = getFileNode(node);
			ClassStructure cst = null;
			String name = null;
			if (node.getParent() != null) {
				name = node.getParent().toString();
				cst = ConsoleLauncher.getClassStructure(name);
			} else {
				String x = event.getPath().toString();
				int br = x.indexOf("[");
				if (br != -1) {
					x = x.substring(br + 1);
					br = x.indexOf("]");
					if (br != -1) {
						x = x.substring(0, br);
						StringTokenizer st = new StringTokenizer(x, ",");
						ArrayList list = new ArrayList();
						while (st.hasMoreTokens()) {
							list.add(st.nextToken());
						}
						if (list.size() > 1) {
							String nm = (String) list.get(list.size() - 2);
							if (ConsoleLauncher.classMethodMap != null) {
								HashMap map = ConsoleLauncher.classMethodMap;
								if (nm != null) {
									HashMap methodmap = (HashMap) map.get(nm
											.trim());
									if (fnode.getCsName() != null) {
										name = (String) methodmap.get(fnode
												.getCsName().trim());

										String x2 = "";// cst.getSearchString(name);

										UIUtil.getUIUtil().searchText(name);
									}
								}
							}
						}
					}
				}

			}
			if (cst != null) {

				String s1 = fnode.getCsName();
				if (ConsoleLauncher.classMethodMap != null) {
					HashMap map = ConsoleLauncher.classMethodMap;
					HashMap methodmap = (HashMap) map.get(name);
					name = (String) methodmap.get(fnode.getCsName());
					// String x=cst.getSearchString(name);

					UIUtil.getUIUtil().searchText(name);
				}

			}

		}
	}

	class JdecTreeSelectionListener implements TreeSelectionListener {

		private void gotoSleep() {
			try {
				Thread.sleep(1000);

			} catch (InterruptedException e) {

			}

		}

		public void valueChanged(TreeSelectionEvent event) {
			// if(!process)return;
			try {
				DefaultMutableTreeNode node = getTreeNode(event.getPath());
				final TreeObject fnode = getFileNode(node);
				final String fileClicked = fnode.getFile().getAbsolutePath();
				if (fileClicked != null
						&& ConsoleLauncher.getCurrentSourceFile() != null
						&& fileClicked.equals(ConsoleLauncher
								.getCurrentSourceFile().getAbsolutePath())) {
					return;
				}

				if (fnode.getFile().isDirectory() == false) {

					if (isValidArchiveFileExt(fnode.getFile().getAbsolutePath())) { // .endsWith(".jar"))
																					// {

						try {

							JFrame tempframes[] = UILauncher.getTempFrames();
							for (int i = 0; i < tempframes.length; i++) {
								JFrame tempframe = tempframes[i];
								if (tempframe != null) {
									tempframe.setVisible(false);
									tempframe.dispose();
								}
							}
							final File chosenFile = fnode.getFile();
							if (chosenFile == null)
								return;
							String filename = chosenFile.getAbsolutePath();
							if (!chosenFile.exists()
									|| (filename != null && !isValidArchiveFileExt(filename))) {
								JOptionPane
										.showMessageDialog(UILauncher
												.getMainFrame(),
												"Chosen File is not a valid archive file");
								Manager.getManager().setShowProgressBar(false);
								return;
							}
							String message = "";
							message += "Would You like Jdec to Decompile Entire Jar ...\n\n";
							message += "[Warning:] This May take some time depending on size of Jar ...\n";
							message += "Would You Like to Proceed...?\n";
							message += "\n\n*** [NOTE :] *** IF Cancel is selected all The class Files in the Jar\n";
							message += "will be shown as a directory structure in the jar tab....";

							JOptionPane option = new JOptionPane();// message,JOptionPane.QUESTION_MESSAGE,JOptionPane.YES_NO_CANCEL_OPTION);
							Object[] options = { "OK", "CANCEL" };
							int opt = option.showOptionDialog(UILauncher
									.getMainFrame(), message,
									"Jar Decompile Option",
									JOptionPane.DEFAULT_OPTION,
									JOptionPane.QUESTION_MESSAGE, null,
									options, options[0]);
							option.setVisible(true);
							if (opt == JOptionPane.CANCEL_OPTION
									|| opt == JOptionPane.NO_OPTION) {
								Thread prog = new Thread() {
									public void run() {
										JDialog archiveProf = UIUtil
												.launchArchiveProgressBarFrame();
										archiveProf.setVisible(true);
										while (UIUtil.explosionInProgress) {
											gotoSleep();
										}
										if (!UIUtil.explosionInProgress) {
											if (UIUtil.archiveproframe != null) {
												UIUtil.archiveproframe
														.setVisible(false);
												UIUtil.archiveproframe
														.dispose();
											}
										}

									}
								};

								Thread work = new Thread() {

									public void run() {
										JdecTree jarExploded = null;
										String filePath = chosenFile
												.getAbsolutePath();
										String jarDir = UIUtil.getUIUtil()
												.explodeJar(chosenFile);
										String s = File.separator;
										if (jarDir.endsWith(s) == false)
											jarDir = jarDir
													.concat(File.separator);
										boolean valid = isValidArchiveFileExt(filePath);
										if (jarDir.trim().length() > 0 && valid)
											jarExploded = new JdecTree(jarDir);
										else {
											jarExploded = null;
										}
										UIUtil.currentScannedJDecTree = jarExploded;
										UIUtil.explosionInProgress = false;

										if (UIUtil
												.checkForInvalidEntries("jar")) {
											updateJarTabWithTree(UIUtil.currentScannedJDecTree);
											JOptionPane
													.showMessageDialog(
															UILauncher
																	.getMainFrame(),
															"Jar Tab has been updated with directory containing Class Files..\nPlease Click on any File to see the output...",
															"User Info...",
															JOptionPane.INFORMATION_MESSAGE);
											UIUtil
													.getUIUtil()
													.getLeftTabbedPane()
													.setSelectedIndex(
															UIUtil
																	.getUIUtil()
																	.getLeftTabbedPane()
																	.indexOfTab(
																			"Jar"));
										} else {
											JOptionPane
													.showMessageDialog(
															UILauncher
																	.getMainFrame(),
															"Please Check The Decompiler Configuration Property Values Again",
															"JDEC WARNING",
															JOptionPane.INFORMATION_MESSAGE);
										}

									}
								};

								work.setPriority(Thread.MAX_PRIORITY);
								prog.setPriority(Thread.NORM_PRIORITY);
								UIUtil.explosionInProgress = true;
								UIUtil.continueToExplode = true;
								work.start();
								prog.start();

							}

							if (opt == JOptionPane.OK_OPTION) {
								JTabbedPane tab = UIUtil.getUIUtil()
										.getLeftTabbedPane();
								tab.setSelectedIndex(1);

								final Thread decProg = new Thread() {

									public void run() {

										JDialog proframe = UIUtil
												.launchProgressBarFrame();
										if (proframe != null)
											proframe.setVisible(true);
										// proframe.requestFocusInWindow();

										while (Manager.getManager()
												.isShowProgressBar() == true) {

											gotoSleep();
										}

										if (!Manager.getManager()
												.isShowProgressBar()) {
											if (proframe != null) {
												proframe.setVisible(false);
												proframe.dispose();
											}
											proframe = null;
											if (ConsoleLauncher.fatalErrorOccured)
												JOptionPane
														.showMessageDialog(
																UILauncher
																		.getMainFrame(),
																UIConstants.jdecTaskError,
																"Jdec Status",
																JOptionPane.INFORMATION_MESSAGE);
										}

									}

								};

								Thread work = new Thread() {

									public void run() {
										Console c = UILauncher.getUIutil()
												.getConsoleFrame();
										if (c != null) {
											JEditorPane rdwr = c.getComponent();
											rdwr.setText("");
											String s = "Jdec Started..\n";
											s = rdwr.getText() + "\n\n" + s;
											rdwr.setText(s);
										}
										Manager.getManager()
												.setShowProgressBar(false);
										JdecTree jarExploded = null;
										String filePath = chosenFile
												.getAbsolutePath();
										String jarDir = UIUtil.getUIUtil()
												.explodeJar(chosenFile);
										if (UIUtil.getUIUtil().registeredClasses == null
												|| UIUtil.getUIUtil().registeredClasses
														.size() == 0) {
											Manager.getManager()
													.setShowProgressBar(false);
											JOptionPane
													.showMessageDialog(null,
															"No Files to decompile. Please check the filter settings! ");
											return;
										}
										UIUtil.explosionInProgress = false;

										Manager.getManager()
												.setShowProgressBar(true);
										decProg.start();
										DecompilerBridge bridge = DecompilerBridge
												.getInstance(UIUtil.getUIUtil());
										bridge.setJarFile(fnode.getFile()
												.getAbsolutePath());
										if (c != null) {
											JEditorPane rdwr = c.getComponent();

											String s = "Current Task :Decompile Jar\n";
											s = rdwr.getText() + "\n\n" + s;
											rdwr.setText(s);
										}
										bridge.execute("decompileJar", "",
												false, "null");
										if (c != null) {
											JEditorPane rdwr = c.getComponent();

											String s = "Current File :"
													+ Configuration
															.getJarPath()
													+ "\n";
											// Reinitialize current executed
											// files....
											ConsoleLauncher
													.setCurrentJarSourceFile(null);
											Configuration.setJarPath(null);

											s = rdwr.getText() + "\n\n" + s;
											rdwr.setText(s);
										}
										if (UIUtil.getUIUtil().getJdecOption()
												.equals("decompileJar") == false) {
											bridge.showResult(UIUtil
													.getUIUtil()
													.getRightTabbedPane());

										}
										if (c != null) {
											JEditorPane rdwr = c.getComponent();
											String s;
											if (UIUtil.getUIUtil()
													.getJdecOption().equals(
															"decompileJar") == false)
												s = "Done...Please Wait for UI to render the output....\nPlease Check up the log files for any error(s)";
											else
												s = "Done...The Jar Tab Has been Updated with The output folder \nshowing the necessary decompiled files\n";
											s = rdwr.getText() + "\n\n" + s;
											rdwr.setText(s);
										}
										JOptionPane
												.showMessageDialog(
														UILauncher
																.getMainFrame(),
														"Jar Tab has been updated with directory containing decompiled Files..\n[Traverse the package structure]",
														"User Info...",
														JOptionPane.INFORMATION_MESSAGE);

									}
								};

								Thread explodeprog = new Thread() {
									public void run() {
										JDialog archiveProf = UIUtil
												.launchArchiveProgressBarFrame();
										archiveProf.setVisible(true);
										while (UIUtil.explosionInProgress) {
											gotoSleep();
										}
										if (!UIUtil.explosionInProgress) {
											if (UIUtil.archiveproframe != null) {
												UIUtil.archiveproframe
														.setVisible(false);
												UIUtil.archiveproframe
														.dispose();
											}
										}

									}
								};

								// /
								// if(fnode.getFile().getAbsolutePath().endsWith(".jar")){
								if (isValidArchiveFileExt(fnode.getFile()
										.getAbsolutePath())) {
									ConsoleLauncher
											.setCurrentJarSourceFile(fnode
													.getFile());
									UILauncher.getUIutil().setJarOption(true);
									if (UIUtil.checkForInvalidEntries("jar")) {
										UIUtil.explosionInProgress = true;
										UIUtil.continueToExplode = true;
										work.start();
										explodeprog.start();
									} else {
										JOptionPane
												.showMessageDialog(
														UILauncher
																.getMainFrame(),
														"Please Check The Decompiler Configuration Property Values Again",
														"JDEC WARNING",
														JOptionPane.INFORMATION_MESSAGE);
									}
								} else {
									JOptionPane
											.showMessageDialog(
													UILauncher.getMainFrame(),
													"Chosen File is not a archive file",
													"Archive Option",
													JOptionPane.INFORMATION_MESSAGE);

								}

							}

						} catch (Exception jarexcep) {
							try {
								LogWriter lg = LogWriter.getInstance();
								lg
										.writeLog("[ERROR]: Method: actionPeformed\n\tClass: NewTaskListener.class");
								lg
										.writeLog("------------------------------------------------");
								lg.writeLog("Exception Stack Trace");
								jarexcep.printStackTrace(lg.getPrintWriter());
								lg.flush();
								JOptionPane.showMessageDialog(UILauncher
										.getMainFrame(),
										"Error Processing Jar " + jarexcep);
							} catch (Exception e) {

							}

						}

					} else if (fnode.getFile().getAbsolutePath().endsWith(
							"." + Configuration.getFileExtension())
							&& fnode.getFile().exists()) {

						JTabbedPane rightTab = UIUtil.getUIUtil()
								.getRightTabbedPane();
						int index = rightTab.indexOfTab("Decompiled Output");
						if (index != -1) {
							rightTab.setSelectedIndex(index);
						}
						Component editor = rightTab.getSelectedComponent();
						JScrollPane editorTab = (JScrollPane) editor;
						Object o = editorTab.getComponent(0);
						JViewport view = (JViewport) o;
						Object o2 = view.getView();
						if (o2 != null) {
							JEditorPane rdwrPane = (JEditorPane) o2;
							File res[] = new File[] { fnode.getFile() };
							if (res != null && res.length > 0) {
								File f = res[0];

								try {
									FileReader fr = new FileReader(f);
									rdwrPane.read(fr, null);
									rdwrPane.setEditable(true);
									UILauncher.getUIutil().addRecentFile(f);
									UIUtil u = UILauncher.getUIutil();
									FileInfoFrame finfo = u.getInfoFrame();

									// finfo.setFileName(u.getJavaClassFile());
									String s = f.getAbsolutePath();

									int winslash = s.indexOf("\\");
									int unixslash = s.indexOf("/");

									if (winslash != -1) {
										s = s.replace('\\', File.separator
												.charAt(0));
									}
									if (unixslash != -1) {
										s = s.replace('/', File.separator
												.charAt(0));
									}

									finfo.recreateDetailsFrame(s);
									Splitter split = Manager.getManager()
											.getSplitterRef();
									split.resetInfoFrame(finfo);
									UILauncher.getMainFrame().repaint();
									UILauncher.getUIutil().setCurrentOpenFile(
											f.getAbsolutePath());
								} catch (IOException fne) {
									try {
										LogWriter writer = LogWriter
												.getInstance();
										String msg = "***************************************************************";
										msg += "\n" + "EXCEPTION STACK TRACE";
										msg += "***************************************************************";
										msg += "\n\n";
										msg += "Message :" + fne.getMessage();
										msg += "\n" + "Cause :"
												+ fne.getCause();
										writer.writeLog(msg);
										fne.printStackTrace(writer
												.getPrintWriter());
										writer.flush();
										String desc = "An Exception occured while processing";
										desc += "\nPlease refer to the Log Files \n";
										desc += "And Report the Error At Project Home Site\n";

										desc += "UILogFile :"
												+ writer.logFilePath();
										desc += "\nDecompiler Log File :"
												+ UILauncher.getUIutil()
														.getLogPath();

										Manager.getManager()
												.setShowProgressBar(false);
										JOptionPane.showMessageDialog(
												UILauncher.getMainFrame(),
												desc, "Run Jdec Decompiler",
												JOptionPane.ERROR_MESSAGE);

									} catch (Exception ex) {
									}
								}

							}

						}

					} else if (fnode.getFile().getAbsolutePath().endsWith(
							".class")
							&& fnode.getFile().exists()) {
						// System.out.println("valueChanged"+fnode.getFile().getAbsolutePath());
						ConsoleLauncher.setCurrentSourceFile(fnode.getFile());
						final File f = ConsoleLauncher.getCurrentSourceFile();
						if (f != null) {
							try {
								// Writer
								// outputwriter=Writer.getWriter("output");
								// outputwriter.close("output");
								Thread t1 = new Thread() {

									public void run() {
										Console c = UILauncher.getUIutil()
												.getConsoleFrame();
										if (c != null) {
											JEditorPane rdwr = c.getComponent();
											rdwr.setText("");
											String s = "Jdec Started..\n";
											s = rdwr.getText() + "\n\n" + s;
											rdwr.setText(s);
										}
										Manager.getManager()
												.setShowProgressBar(true);
										DecompilerBridge bridge = DecompilerBridge
												.getInstance(UIUtil.getUIUtil());
										bridge
												.setClassFile(f
														.getAbsolutePath());
										UILauncher.getUIutil()
												.setCurrentOpenFile(
														f.getAbsolutePath());

										if (c != null) {
											JEditorPane rdwr = c.getComponent();

											String s = "Current Task :Decompile Class\n";
											s = rdwr.getText() + "\n\n" + s;
											rdwr.setText(s);
										}
										bridge.execute("decompileClass", "",
												false, "null");
										UILauncher.getUIutil().addRecentFile(f);
										if (c != null) {
											JEditorPane rdwr = c.getComponent();

											String s = "Current File :"
													+ Configuration
															.getJavaClassFile()
													+ "\n";
											s = rdwr.getText() + "\n\n" + s;
											rdwr.setText(s);
										}
										bridge.showResult(UIUtil.getUIUtil()
												.getRightTabbedPane());
										UILauncher.getUIutil().addRecentFile(
												new File(Configuration
														.getJavaClassFile()));
										if (c != null) {
											JEditorPane rdwr = c.getComponent();

											String s = "Done...Please Wait for UI to render the output....\nPlease Check up the log files for any error(s)";
											s = rdwr.getText() + "\n\n" + s;
											rdwr.setText(s);
										}
										ConsoleLauncher
												.setCurrentDecompiledFile(Configuration
														.getJavaClassFile());
										// Configuration.setJavaClassFile(null);
										// Configuration.setClassFilePath(null);

									}
								};
								// t1.setPriority(Thread.NORM_PRIORITY);
								Manager.getManager().setStatusThread(t1);

								Thread t2 = new Thread() {

									public void run() {

										JDialog proframe = UIUtil
												.launchProgressBarFrame();
										if (proframe != null)
											proframe.setVisible(true);

										while (Manager.getManager()
												.isShowProgressBar() == true) {

											gotoSleep();
										}

										if (!Manager.getManager()
												.isShowProgressBar()) {
											if (proframe != null) {
												proframe.setVisible(false);
												proframe.dispose();
											}
											proframe = null;
											if (ConsoleLauncher.fatalErrorOccured)
												JOptionPane
														.showMessageDialog(
																UILauncher
																		.getMainFrame(),
																UIConstants.jdecTaskError,
																"Jdec Status",
																JOptionPane.INFORMATION_MESSAGE);
										}

									}

								};

								if (UIUtil.checkForInvalidEntries("")) {
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
								// Manager.getManager().setStatusFrame(frame);
								// //
								Manager manager = Manager.getManager();
								ArrayList paneList = manager
										.getCurrentSplitPaneComponents();
								Console c = null;
								for (int s = 0; s < paneList.size(); s++) {
									Object current = paneList.get(s);
									if (current instanceof Console) {
										c = (Console) current;
										break;
									}

								}
								FileReader fre = null;
								try {
									if (Configuration.getConsoleDetailFile() != null) {
										fre = new FileReader(Configuration
												.getConsoleDetailFile());
										/*
										 * JScrollPane j=c.getComponent();
										 * JViewport jview=j.getViewport();
										 * Object v=jview.getView(); if(v!=null) {
										 * JEditorPane rdwr=(JEditorPane)o2;
										 * System.out.println(rdwr.getText());
										 * rdwr.setText("Over");
										 * System.out.println(rdwr.getText());
										 * rdwrPane.read(fre,null); }
										 */
										JEditorPane rdwr = c.getComponent();
										String s = "Jdec Started...";
										s = rdwr.getText() + "\n\n" + s;
										rdwr.setText(s);
									}
									// rdwr.read(fre,null);
								} catch (IOException ioe) {
								}

								// end

								// //

							} catch (Exception e) {

								try {
									LogWriter writer = LogWriter.getInstance();
									String msg = "***************************************************************";
									msg += "\n" + "EXCEPTION STACK TRACE";
									msg += "***************************************************************";
									msg += "\n\n";
									msg += "Message :" + e.getMessage();
									msg += "\n" + "Cause :" + e.getCause();
									writer.writeLog(msg);
									e.printStackTrace(writer.getPrintWriter());
									writer.flush();
									String desc = "An Exception occured while processing";
									desc += "\nPlease refer to the Log Files \n";
									desc += "And Report the Error At Project Home Site\n";

									desc += "UILogFile :"
											+ writer.logFilePath();
									desc += "\nDecompiler Log File :"
											+ UILauncher.getUIutil()
													.getLogPath();

									Manager.getManager().setShowProgressBar(
											false);
									JOptionPane.showMessageDialog(UILauncher
											.getMainFrame(), desc,
											"Run Jdec Decompiler",
											JOptionPane.ERROR_MESSAGE);

								} catch (Exception ex) {
								}
							}
						}
					} else if (fnode.getFile().getAbsolutePath().endsWith(
							".java")
							&& fnode.getFile().exists()) {
						JEditorPane editor = UIUtil.getUIUtil()
								.getEditorWindow();
						editor.read(new FileReader(fnode.getFile()), null);
					} else if (fnode.getFile().getAbsolutePath().endsWith(
							".txt")
							&& fnode.getFile().exists()) {
						JEditorPane editor = UIUtil.getUIUtil()
								.getEditorWindow();
						editor.read(new FileReader(fnode.getFile()), null);
					} else if (fnode.getFile().getAbsolutePath().endsWith(
							".properties")
							&& fnode.getFile().exists()) {
						JEditorPane editor = UIUtil.getUIUtil()
								.getEditorWindow();
						editor.read(new FileReader(fnode.getFile()), null);
					} else {
						HashMap map = ConsoleLauncher.method_names_signature;
						String x = fnode.getFile().getAbsolutePath();
						if (x != null) {
							int i = x.lastIndexOf("\\");
							if (i == -1) {
								i = x.lastIndexOf("/");
							}
							if (i != -1) {
								x = x.substring(i + 1);
								String s = (String) map.get(x);
								UIUtil.getUIUtil().searchText(s);

								// JOptionPane.showMessageDialog(null,s);
							}
						}
					}
				}

			} catch (Throwable t) {

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

		private void updateJarTabWithTree(JdecTree jarExploded) {
			if (jarExploded != null) {
				UIObserver observer = UILauncher.getObserver();
				if (observer != null) // Actually no need to check
				{
					observer.resetTabsPane(true);
					Manager manager = Manager.getManager();
					ArrayList list = manager.getCurrentSplitPaneComponents();
					JTabbedPane tabPane = UIUtil.getUIUtil()
							.getLeftTabbedPane();
					if (tabPane != null) {
						int jarTabIndex = tabPane.indexOfTab("Jar");
						if (jarTabIndex >= 0) {
							tabPane.remove(jarTabIndex);
							tabPane.addTab("Jar", jarExploded);
						}
					}

				}
				JFrame mainFrame = UILauncher.getMainFrame();
				mainFrame.repaint();
			}
		}

	}

	public class JDecStructureTreeExpansionListener implements
			TreeExpansionListener {
		public void treeExpanded(TreeExpansionEvent event) {
			process = true;
			final DefaultMutableTreeNode node = getTreeNode(event.getPath());
			final TreeObject fnode = getFileNode(node);
			// Thread runner = new Thread() {
			// public void run() {
			if (fnode != null && TreeUtil.showStructureChildren(node, fnode)) {
				// Runnable runnable = new Runnable() {
				// public void run() {
				treeModel.reload(node);
				// }
				// }
				// ;
				// SwingUtilities.invokeLater(runnable);
				// }
			}
			// }
			// ;
			// runner.start();
		}

		public void treeCollapsed(TreeExpansionEvent event) {
			JdecTree.this.process = false;
		}

	}

	class blink implements Runnable

	{
		JLabel l = UIUtil.getUIUtil().tiplabel;

		public void run() {
			if (l != null) {
				try {
					for (;;) {
						l.setForeground(Color.RED);// new Color(153,0,51));
						l.setFont(new Font("MONOSPACE", Font.BOLD, 11));
						Thread.sleep(800);
						l.setForeground(new Color(51, 0, 51));// new
																// Color(0,102,102));
						Thread.sleep(500);
					}
				} catch (InterruptedException ie) {

				}

			}
		}
	}

	public void expandTreeFully(DefaultMutableTreeNode root) {

		final DefaultMutableTreeNode node = root;
		final TreeObject fnode = getFileNode(node);
		// Thread runner = new Thread() {
		// public void run() {
		if (fnode != null && TreeUtil.showStructureChildren(node, fnode)) {
			// Runnable runnable = new Runnable() {
			// public void run() {
			treeModel.reload(node);
			// }
			// }
			// ;
			// SwingUtilities.invokeLater(runnable);
			// }
			// }
			// }
			// ;
			// runner.start();

		}

	}

}

class TreeObject {
	private String passedDirectoryList;

	protected File file;

	private java.lang.String s;

	private boolean forClassStructure = false;

	private String csName = null;

	private File[] roots;

	public File[] getRoots() {
		return roots;
	}

	public void setRoots(File[] roots) {
		this.roots = roots;
	}

	public void setPassedDirectoryList(String passedDirectoryList) {
		this.passedDirectoryList = passedDirectoryList;
	}

	public String getPassedDirectoryList() {
		return passedDirectoryList;
	}

	public String getCsName() {
		return csName;
	}

	public boolean isForClassStructure() {
		return forClassStructure;
	}

	public TreeObject(java.lang.String s) {
		this.s = s;
	}

	public TreeObject(File file) {
		this.file = file;
	}

	public TreeObject(String name, boolean isForCS) {
		this.csName = name;
		forClassStructure = isForCS;
	}

	public File getFile() {
		return file;
	}

	public String toString() {

		if (forClassStructure) {
			return csName;
		}

		if (file == null) {
			return s;
		}
		if (file.getName() == null || file.getName().trim().length() == 0) {
			return file.getPath();
		} else {
			return file.getName();
		}

	}

}

class TreeUtil {

	private static File[] getMyChildren(File file) {
		if (file == null)
			return new File[] { new File("INVALID:INVALID") };
		String path = file.getPath();
		if (path != null && !path.trim().endsWith(File.separator)) {
			path = path + File.separator;
		}
		file = new File(path);
		if (!file.isDirectory())
			return new File[] { file };
		else
			return file.listFiles();

	}

	public static boolean showChildren(DefaultMutableTreeNode parent,
			File file, TreeObject to) {
		parent.removeAllChildren();
		File[] files = null;//
		if (file != null)
			files = getMyChildren(file);
		else {
			if (to != null && to.getRoots() != null) {
				files = to.getRoots();
			}
		}
		if (files == null)
			return false;
		ArrayList list = new ArrayList();

		for (int k = 0; k < files.length; k++) {
			File f = files[k];
			TreeObject chidlNode = new TreeObject(f);
			list.add(chidlNode);
		}
		if (files.length == 1
				&& files[0].getAbsolutePath().indexOf("INVALID:INVALID") != -1) {
			list = ConsoleLauncher.getcurrentClassMethods();
			if (list == null || list.size() == 0)
				return false;
			for (int i = 0; i < list.size(); i++) {
				String nd = (String) list.get(i);
				TreeObject tnd = new TreeObject(new File(nd));
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(tnd);
				parent.add(node);

			}
			return true;

		}

		for (int i = 0; i < list.size(); i++) {
			TreeObject nd = (TreeObject) list.get(i);
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(nd);
			if (nd.getFile().isDirectory()) {
				node.add(new DefaultMutableTreeNode("Getting Child For "
						+ nd.getFile()));
			}
			parent.add(node);

		}
		return true;
	}

	public static boolean showStructureChildren(DefaultMutableTreeNode parent,
			TreeObject tobj) {

		if (parent == null || tobj.isForClassStructure() == false)
			return false;
		parent.removeAllChildren();

		String name = tobj.getCsName();
		if (name == null)
			return false;
		ArrayList list = new ArrayList();
		ClassStructure anycs = InnerClassTracker.getClassStructure(name,
				ConsoleLauncher.allClassStructures);
		if (anycs == null)
			return false;
		ArrayList structure = anycs.getMethods();

		for (int k = 0; k < structure.size(); k++) {
			String s = (String) structure.get(k);
			TreeObject chidlNode = new TreeObject(s, true);
			list.add(chidlNode);
		}

		for (int i = 0; i < list.size(); i++) {
			TreeObject nd = (TreeObject) list.get(i);
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(nd);
			parent.add(node);
			node.setParent(parent);

		}
		// Add inner classes

		structure = new ArrayList();
		ArrayList innercls = anycs.getInnerclasses();
		for (int x = 0; x < innercls.size(); x++) {
			ClassStructure temp = (ClassStructure) innercls.get(x);
			String name2 = temp.getName();
			structure.add(name2);

		}

		list = new ArrayList();

		for (int k = 0; k < structure.size(); k++) {
			String s = (String) structure.get(k);
			TreeObject chidlNode = new TreeObject(s, true);

			list.add(chidlNode);
		}

		for (int i = 0; i < list.size(); i++) {
			TreeObject nd = (TreeObject) list.get(i);
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(nd);
			node.add(new DefaultMutableTreeNode("Scanning...."));
			parent.add(node);
			node.setParent(parent);

		}
		return true;
	}

}
