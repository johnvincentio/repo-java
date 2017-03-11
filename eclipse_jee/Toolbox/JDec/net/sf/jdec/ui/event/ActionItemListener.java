/*
 *  ActionItemListener.java Copyright (c) 2006,07 Swaroop Belur
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

import net.sf.jdec.config.Configuration;
import net.sf.jdec.io.Writer;
import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.reflection.JavaClass;
import net.sf.jdec.ui.adapter.DecompilerBridge;
import net.sf.jdec.ui.config.DecompilerConfigDetails;
import net.sf.jdec.ui.config.KeyAssist;
import net.sf.jdec.ui.config.UIConfig;
import net.sf.jdec.ui.core.*;
import net.sf.jdec.ui.main.UILauncher;
import net.sf.jdec.ui.util.*;
import net.sf.jdec.ui.util.editor.TextEditor;
import net.sf.jdec.ui.util.highlight.CategoryChooser;
import net.sf.jdec.ui.core.Console;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;

public class ActionItemListener implements ActionListener {

	// private TextEditor editor=null;

	public void actionPerformed(ActionEvent event) {

		try {

			LogWriter lw = LogWriter.getInstance();
			final Writer writer = Writer.getWriter("log");
			String md = UILauncher.getUIutil().getLogLevel();
			String msg = "";
			msg = "Action Invoked :" + event.getActionCommand();
			lw.writeLog(msg, md);

			if (event.getActionCommand().equals("showall")) {
				ArrayList v1lines = (ArrayList) UIUtil.getUIUtil()
						.getVersionlines().get("version1");
				ArrayList v2lines = (ArrayList) UIUtil.getUIUtil()
						.getVersionlines().get("version2");

			}
			if (event.getActionCommand().equals("showdiff")) {
				ArrayList v1lines = (ArrayList) UIUtil.getUIUtil()
						.getVersionlines().get("version1");
				ArrayList v2lines = (ArrayList) UIUtil.getUIUtil()
						.getVersionlines().get("version2");
			}
			if (event.getActionCommand().equals("showmatch")) {
				ArrayList v1lines = (ArrayList) UIUtil.getUIUtil()
						.getVersionlines().get("version1");
				ArrayList v2lines = (ArrayList) UIUtil.getUIUtil()
						.getVersionlines().get("version2");
			}
			if (event.getActionCommand().equals("changecolorfnt")) {
				ArrayList v1lines = (ArrayList) UIUtil.getUIUtil()
						.getVersionlines().get("version1");
				ArrayList v2lines = (ArrayList) UIUtil.getUIUtil()
						.getVersionlines().get("version2");
			}

			if (event.getActionCommand().equals("keys")) {
				KeyAssist assist = new KeyAssist(UIUtil.getUIUtil());
			}
			if (event.getActionCommand().equals("showmethods")) {
				ShowMethods sm = new ShowMethods();
				if (!sm.showMethods()) {
					JOptionPane
							.showMessageDialog(UILauncher.getMainFrame(),
									"No methods to show...Please decompile a class first");
				}

			}
			if (event.getActionCommand().equals("Send Feedback")) {
				final JFrame frame = new JFrame("Send Feedback to jdec admin");
				JPanel p1 = new JPanel();
				JLabel lbl1 = new JLabel("Subject: ");
				final JTextField txt = new JTextField();
				txt.setColumns(30);
				p1.add(lbl1);
				p1.add(txt);
				JLabel lbl2 = new JLabel("Body: ");
				JPanel p2 = new JPanel();
				final JTextArea area = new JTextArea(10, 35);
				area.setLineWrap(true);
				// area.setColumns(20);
				area
						.setText("Please enter email body  here....\n\n\n\n\n\n\n\n");
				p2.add(lbl2);
				p2.add(area);
				JPanel p3 = new JPanel();
				JLabel cc = new JLabel("CC: ");
				final JTextField cct = new JTextField();
				cct.setColumns(20);
				JPanel p4 = new JPanel();
				p4.add(cc);
				p4.add(cct);
				JButton b1 = new JButton("Send Feedback");
				JButton b2 = new JButton("Close");
				p3.add(b1);
				p3.add(b2);
				b2.addActionListener(new ActionItemListener() {
					public void actionPerformed(ActionEvent ae) {
						frame.setVisible(false);
						frame.dispose();
					}
				});
				b1.addActionListener(new ActionItemListener() {
					public void actionPerformed(ActionEvent ae) {

						try {
							Runtime rt = Runtime.getRuntime();
							File br = UILauncher.getUIutil().getBrowserPath();
							if (br == null) {
								throw new IOException(
										"Please set browser path first");
							} else {
								if (br.exists() == false) {
									throw new IOException(
											"Please check  browser path ");
								}
							}
							String s = br.getAbsolutePath()
									+ " mailto:swaroop.belur@gmail.com?subject="
									+ txt.getText() + "&body=" + area.getText();
							Process p = rt.exec(s);
						} catch (IOException e) {
							e.printStackTrace(writer);
							String m = e.getMessage();
							if (e == null || m.length() == 0)
								JOptionPane.showMessageDialog(frame,
										"Could Not send email");
							else
								JOptionPane.showMessageDialog(frame, m);
						}

					}

				});
				JPanel p5 = new JPanel();
				p5.add(p4, BorderLayout.NORTH);
				p5.add(p1, BorderLayout.SOUTH);
				frame.getContentPane().add(p1, BorderLayout.NORTH);
				frame.getContentPane().add(p2, BorderLayout.CENTER);
				frame.getContentPane().add(p3, BorderLayout.SOUTH);
				frame.setBounds(300, 300, 400, 300);
				frame.setVisible(true);
			}
			if (event.getActionCommand().equals("customed")) {
				String editor = UILauncher.getUIConfigRef()
						.getCustomeEditorPath();
				if (editor == null) {
					JOptionPane.showMessageDialog(UILauncher.getMainFrame(),
							"Please set the custom editor path properly first");
					JOptionPane.showMessageDialog(UILauncher.getMainFrame(),
							"Go to Edit--->Preferences option for setting");
					return;
				}
				File f = new File(editor);
				if (f.exists() && f.isFile()) {
					CustomProcess process = new CustomProcess();
					JTabbedPane tabs = UIUtil.getUIUtil().getRightTabbedPane();
					Component c = tabs.getSelectedComponent();
					JScrollPane editorTab = (JScrollPane) c;
					Object o = editorTab.getComponent(0);
					JViewport view = (JViewport) o;
					Object o2 = view.getView();
					String text = null;
					File file = null;
					if (o2 != null) {
						JEditorPane rdwrPane = (JEditorPane) o2;
						text = rdwrPane.getText();
						file = File.createTempFile("currenttext", ".tmp");
						FileOutputStream fos = new FileOutputStream(file);
						fos.write(text.getBytes());
						fos.flush();
					}

					if (file != null && text != null && text.length() > 0)
						process.launch(editor, new Object[] { file
								.getAbsolutePath() });
					else
						JOptionPane.showMessageDialog(
								UILauncher.getMainFrame(), "No File to show");

				} else {
					JOptionPane.showMessageDialog(UILauncher.getMainFrame(),
							"Please set the custom editor path properly first");
					JOptionPane.showMessageDialog(UILauncher.getMainFrame(),
							"Go to Edit--->Preferences option for setting");
				}
			}

			if (event.getActionCommand().equals("Open UI LogFile")) {

				try {
					JEditorPane editor = UIUtil.getUIUtil().getEditorWindow();
					editor.setText("");
					String s = UILauncher.getUIutil().getUilogfile();
					File f = new File(s);
					if (f.exists() == false) {
						JOptionPane
								.showMessageDialog(UILauncher.getMainFrame(),
										"Could Not Find UILog File\nPlease reload the jdec config details...");
						return;
					} else {
						FileReader fr = new FileReader(f);
						editor.read(fr, null);
						UIUtil u = UILauncher.getUIutil();
						FileInfoFrame finfo = u.getInfoFrame();
						finfo.setFileName(f.getAbsolutePath());
						finfo.recreateDetailsFrame(f.getAbsolutePath());
						Splitter split = Manager.getManager().getSplitterRef();
						split.resetInfoFrame(finfo);
						UILauncher.getMainFrame().repaint();
						UILauncher.getUIutil().setCurrentOpenFile(
								f.getAbsolutePath());

					}
				} catch (HeadlessException e) {

				} catch (FileNotFoundException e) {
					LogWriter lgw = LogWriter.getInstance();
					e.printStackTrace(lgw.getPrintWriter());
				} catch (IOException e) {
					LogWriter lgw = LogWriter.getInstance();
					e.printStackTrace(lgw.getPrintWriter());
				}

			}
			if (event.getActionCommand().equals("Open Decompiler LogFile")) {
				try {
					JEditorPane editor = UIUtil.getUIUtil().getEditorWindow();
					editor.setText("");
					String s = UILauncher.getUIutil().getLogPath();
					File f = new File(s);
					if (f.exists() == false) {
						String logFilePath = System.getProperty("user.dir")
								+ File.separator + "jdec_created_log.txt";
						f = new File(logFilePath);
						if (f.exists() == false) {
							JOptionPane
									.showMessageDialog(UILauncher
											.getMainFrame(),
											"Could Not Load Decompiler Log File\nPlease verify whether the file exists...");
							return;
						}

					}
					FileReader fr = new FileReader(f);
					editor.read(fr, null);
					UIUtil u = UILauncher.getUIutil();
					FileInfoFrame finfo = u.getInfoFrame();
					finfo.setFileName(f.getAbsolutePath());
					finfo.recreateDetailsFrame(f.getAbsolutePath());
					Splitter split = Manager.getManager().getSplitterRef();
					split.resetInfoFrame(finfo);
					UILauncher.getMainFrame().repaint();
					UILauncher.getUIutil().setCurrentOpenFile(
							f.getAbsolutePath());
				} catch (HeadlessException e) {

				} catch (FileNotFoundException e) {
					LogWriter lgw = LogWriter.getInstance();
					e.printStackTrace(lgw.getPrintWriter());
				} catch (IOException e) {
					LogWriter lgw = LogWriter.getInstance();
					e.printStackTrace(lgw.getPrintWriter());
				}

			}
			if (event.getActionCommand().equals("Open Both in Separate Tabs")) {

				JTabbedPane tab = UIUtil.getUIUtil().getRightTabbedPane();
				String uiname = "UILog Output";
				String dcname = "DecompilerLog Output";
				OutputFrame o1 = new OutputFrame(UILauncher.getObserver(), "",
						"plain");
				OutputFrame o2 = new OutputFrame(UILauncher.getObserver(), "",
						"plain");
				int i = tab.indexOfTab("UILog Output");
				if (i != -1)
					tab.remove(i);
				i = tab.indexOfTab("DecompilerLog Output");
				if (i != -1)
					tab.remove(i);
				tab.addTab(uiname, o1.getComponent());
				tab.addTab(dcname, o2.getComponent());

				// Read UIlog

				try {
					int p1 = tab.indexOfTab("UILog Output");
					tab.setSelectedIndex(p1);
					JScrollPane pane1 = (JScrollPane) tab
							.getSelectedComponent();
					JEditorPane editor = (JEditorPane) pane1.getViewport()
							.getView();
					editor.setText("");
					String s = UILauncher.getUIutil().getUilogfile();
					File f = new File(s);
					if (f.exists() == false) {
						JOptionPane
								.showMessageDialog(UILauncher.getMainFrame(),
										"Could Not Find UI Log File\nPlease reload the jdec config details...");
						return;
					} else {
						FileReader fr = new FileReader(f);
						editor.read(fr, null);
					}
				} catch (HeadlessException e) {

				} catch (FileNotFoundException e) {
					LogWriter lgw = LogWriter.getInstance();
					e.printStackTrace(lgw.getPrintWriter());
				} catch (IOException e) {
					LogWriter lgw = LogWriter.getInstance();
					e.printStackTrace(lgw.getPrintWriter());
				}

				// end

				// Read DClog

				try {
					int p1 = tab.indexOfTab("DecompilerLog Output");
					tab.setSelectedIndex(p1);
					JScrollPane pane1 = (JScrollPane) tab
							.getSelectedComponent();
					JEditorPane editor = (JEditorPane) pane1.getViewport()
							.getView();
					editor.setText("");
					String s = UILauncher.getUIutil().getLogPath();
					File f = new File(s);
					if (f.exists() == false) {
						JOptionPane
								.showMessageDialog(
										UILauncher.getMainFrame(),
										"Could Not Load Decompiler Log File\n"
												+ "Please verify whether the file exists...");
						return;
					} else {
						FileReader fr = new FileReader(f);
						editor.read(fr, null);
					}
				} catch (HeadlessException e) {

				} catch (FileNotFoundException e) {
					LogWriter lgw = LogWriter.getInstance();
					e.printStackTrace(lgw.getPrintWriter());
				} catch (IOException e) {
					LogWriter lgw = LogWriter.getInstance();
					e.printStackTrace(lgw.getPrintWriter());
				}

				// end

			}
			if (event.getActionCommand().equals("contr")) {
				JFrame abt = new JFrame("Jdec Contribution");
				JPanel panel1 = new JPanel();
				String s3 = "Jdec is an open source project licenced under GPL";
				String s5 = "\t\tYou can also mail to the Following address regarding any other query...";
				String s4 = "Please Feel Free to Look up the project Home site for any Further Details";

				panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
				panel1.add(new JLabel(s3));
				panel1.add(new JLabel("\n "));
				panel1.add(new JLabel(s4));
				panel1.add(new JLabel("\n "));
				panel1.add(new JLabel(s5));
				panel1.add(new JLabel("\n "));
				// panel1.add(new JLabel(m));
				panel1.setVisible(true);
				abt.getContentPane().add(panel1, BorderLayout.CENTER);
				Dimension d = UILauncher.getMainFrame().getToolkit()
						.getScreenSize();
				abt.setBounds((int) (d.getWidth() / 2) / 4 + 75, (int) (d
						.getHeight() / 2) / 4 + 150, 380, 280);
				// abt.pack();
				abt.setVisible(true);

			}
			if (event.getActionCommand().equals("ViewLicenceInfo")) {
				final JFrame f = new JFrame("Jdec Licence information");
				File fl = new File("COPYING");
				FileInputStream fis = new FileInputStream(fl);
				BufferedReader br = new BufferedReader(new InputStreamReader(
						fis));
				String s = null;
				StringBuffer sb = new StringBuffer("");
				while ((s = br.readLine()) != null) {
					sb.append(s + "\n");
				}
				JTextArea text = new JTextArea(sb.toString());
				JScrollPane sp = new JScrollPane(text);
				f.getContentPane().add(sp, BorderLayout.CENTER);
				f.setBounds(200, 200, 470, 300);
				f.setVisible(true);
				f.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent w) {
						f.setVisible(false);
						f.dispose();
					}
				});

			}
			if (event.getActionCommand().equals("bugsubmit")) {

				final JFrame abt = new JFrame("Bug Reporting...");
				JPanel panel1 = new JPanel();
				String s3 = "In order To Report a Bug Please Go to Project Home Site...\n";
				String s4 = "Before Filing The Bug, Please check up the Known issues in the site/packaged Zip File\n";
				String s6 = "URL : http://sourceforge.net/tracker/?group_id=162452&atid=823843";
				String s5 = "You can also mail to the Following address regarding any other query...\n";
				String m = "mailto: swaroop.belur@gmail.com";
				JTextArea ta2 = new JTextArea();
				ta2.setFont(new Font(null, Font.ROMAN_BASELINE, 10));

				ta2.setText(ta2.getText() + s3 + s4 + "\n" + s5 + m + "\n\n"
						+ s6);
				ta2.setEditable(false);

				panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
				panel1.add(ta2, BorderLayout.CENTER);
				/*
				 * panel1.add(new JLabel(s3)); panel1.add(new JLabel("\n "));
				 * panel1.add(new JLabel(s4)); panel1.add(new JLabel("\n "));
				 * panel1.add(new JLabel(s5)); panel1.add(new JLabel("\n "));
				 * panel1.add(new JLabel(m));
				 */
				panel1.setVisible(true);
				abt.getContentPane().add(panel1, BorderLayout.CENTER);
				Dimension d = UILauncher.getMainFrame().getToolkit()
						.getScreenSize();
				abt.setBounds((int) (d.getWidth() / 2) / 4 + 75, (int) (d
						.getHeight() / 2) / 4 + 150, 460, 200);
				// abt.pack();
				abt.setVisible(true);

				/*
				 * Dimension
				 * d=UILauncher.getMainFrame().getToolkit().getScreenSize();
				 * 
				 * abt.setBounds((int)(d.getWidth()/2)/2,(int)(d.getHeight()/2)/2,380,280);
				 * abt.setVisible(true);
				 * 
				 * abt.setDefaultCloseOperation(abt.DISPOSE_ON_CLOSE);
				 * UILauncher.addChildRef(abt);
				 */

			}
			if (event.getActionCommand().equals("extra")) {
				filterframe = new ExtraOptions();
				UILauncher.getUIutil().setFilterFrameRef(filterframe);
			}

			if (event.getActionCommand().equals("Find Jar")) {
				FindJar findj = new FindJar("Jdec Find Jar");
			}

			if (event.getActionCommand().equals("What Is Jdec ?")) {

				JLabel ver = new JLabel(
						"                         Jdec Current Version: 2.0");
				ver.setForeground(Color.BLUE);
				JLabel jl1 = new JLabel("Jdec is a Java Decompiler");
				JLabel jl2 = new JLabel(
						"capable of generating detailed information ");
				JLabel jl3 = new JLabel("for a java class file like...");
				JLabel jl4 = new JLabel("Viewing Source Code ");
				JLabel jl5 = new JLabel("Viewing disassembled code");
				JLabel jl6 = new JLabel("among other useful information");
				final JFrame abt = new JFrame("What is Jdec");
				JPanel panel1 = new JPanel();
				String s3 = "\t\tCopyRight\tSWAROOP BELUR";
				s3 += "\n\t\tEmail: <swaroop.belur@gmail.com>\t\t\n\n";
				JTextArea ta2 = new JTextArea();
				ta2.setFont(new Font(null, Font.CENTER_BASELINE, 10));
				ta2.setText(ta2.getText() + s3);
				ta2.setEditable(false);
				panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
				panel1.add(new JSeparator());
				panel1.add(ver);
				panel1.add(new JSeparator());
				panel1.add(jl1);
				panel1.add(jl2);
				panel1.add(jl3);
				panel1.add(jl4);
				panel1.add(jl5);
				panel1.add(jl6);
				JLabel lic = new JLabel("Licence");

				lic.setForeground(Color.RED);
				lic.setFont(new Font("MONOSPACE", Font.BOLD, 12));

				JLabel licValue = new JLabel("Jdec is licenced under");
				JLabel GPL = new JLabel("GPL");
				GPL.setForeground(Color.BLUE);
				GPL.setBackground(Color.WHITE);
				JPanel licp = new JPanel();
				licp.add(lic);
				licp.add(licValue);
				licp.add(GPL);
				panel1.add(new JSeparator());
				panel1.add(licp);
				panel1.add(new JSeparator());
				JButton credits = new JButton("Other Credits");
				JButton close = new JButton("Close");

				close.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						abt.setVisible(false);
						abt.dispose();
					}

				});
				credits.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						java.lang.String others = "I would also like to thank Kartik Bettadapura\n";
						others += "\nEmail : kbettada@gmail.com\n";
						// JOptionPane.showMessageDialog(abt,others);
						JOptionPane.showMessageDialog(abt, others, "THANKS",
								JOptionPane.INFORMATION_MESSAGE);
					}
				});
				JPanel panel2 = new JPanel();
				panel2.add(ta2, BorderLayout.EAST);
				JPanel pan = new JPanel();
				pan.add(credits);
				pan.add(close);
				panel2.add(pan, BorderLayout.WEST);
				abt.getContentPane().add(panel1, BorderLayout.NORTH);
				// abt.getContentPane().add(new
				// JSeparator(),BorderLayout.CENTER);
				abt.getContentPane().add(panel2, BorderLayout.CENTER);

				abt.pack();
				Dimension d = UILauncher.getMainFrame().getToolkit()
						.getScreenSize();
				abt.setBounds((int) d.getWidth() / 2 - 140,
						(int) d.getHeight() / 2 - 140, 380, 280);
				abt.setVisible(true);
				abt.setResizable(true);
				abt.setDefaultCloseOperation(abt.DISPOSE_ON_CLOSE);
				UILauncher.addChildRef(abt);

			} else if (event.getActionCommand().equals("jdec_tips")) {

				Tips tips = new Tips();

			} else if (event.getActionCommand().equals("faq")) {

				FAQ faq = new FAQ();

			}

			else if (event.getActionCommand().equals("TwoMins")) {

				QuickTutorial quick = new QuickTutorial("2 Mins Tutorial");

			}

			else if (event.getActionCommand().equals("Refresh...")) {

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
						JTabbedPane rightTab = UIUtil.getUIUtil()
								.getRightTabbedPane();
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

						}

					}

				};
				t1.setPriority(Thread.MAX_PRIORITY);
				Manager.getManager().setStatusThread(t1);
				t2.setPriority(Thread.NORM_PRIORITY);
				Manager.getManager().setShowProgressBar(true);
				t2.start();
				t1.start();

			}

			else if (event.getActionCommand().equals("Archive settings...")) {
				ArchiveSettings ref = new ArchiveSettings();

			}

			else if (event.getActionCommand().equals("New Decompiler task...")) {

				final JFrame in = new JFrame("New Decompiler Task Window");
				in.setResizable(true);
				// in.getContentPane().setLayout(new GridLayout(2, 4));
				JCheckBox Class = new JCheckBox("Decompile Class File");
				JCheckBox Jar = new JCheckBox(
						"Decompile Archive File                     [*** Updated]");
				Jar.setActionCommand("Decompile Archive File");
				JCheckBox classInJar = new JCheckBox(
						"Class In Archive                        [*** New]");
				JCheckBox versions = new JCheckBox(
						"Decompile 2 versions of class File    [*** New]");
				Jar.setActionCommand("Decompile Archive File");
				ButtonGroup bg = new ButtonGroup();
				bg.add(Class);
				bg.add(Jar);
				bg.add(classInJar);
				bg.add(versions);
				// bg.add(process);
				// bg.add(project);
				// JLabel archlbl=new JLabel("Click on [Edit-->Archive
				// settings...] for updating settings related to archive file
				// task.");
				final String hintm = "Click on ....[for updating settings related to archive file task.]\n1>[Edit-->Archive settings...] \n\t2>Jar Level Filters[Configuration-->[Jdec(Decompiler) Filters]";

				JButton hint = new JButton("Hint");
				JButton close = new JButton("Close");
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
				// pp.add(archlbl);
				// BoxLayout box=new BoxLayout(in,BoxLayout.Y_AXIS);
				// in.getContentPane().setLayout(box);
				in.getContentPane().add(basic, BorderLayout.NORTH);
				in.getContentPane().add(pp, BorderLayout.CENTER);
				JPanel pjp = new JPanel();
				BoxLayout box3 = new BoxLayout(pjp, BoxLayout.X_AXIS);

				pjp.setLayout(box3);
				JLabel lab2 = new JLabel("             More Info");

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
				// pjp.add(project);
				pjp.add(hint);
				hint.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent ae) {
						JOptionPane.showMessageDialog(in, hintm);

					}

				});
				close.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent ae) {
						in.setVisible(false);
						in.dispose();
					}
				});
				pjp.add(new JLabel("\t\t"));
				pjp.add(close);
				in.getContentPane().add(pjp, BorderLayout.SOUTH);

				in.pack();
				in.setBounds(300, 300, 450, 190);
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

			} else if (event.getActionCommand().equals("Open File...")) {
				FileOpener opener = new FileOpener(new ClassFileFilter());
				Manager manager = Manager.getManager();
				ArrayList paneList = manager.getCurrentSplitPaneComponents();
				JTabbedPane tabs = null;

				for (int s = 0; s < paneList.size(); s++) {
					Object current = paneList.get(s);
					if (current instanceof JTabbedPane) {
						tabs = (JTabbedPane) current;
						if (tabs.getTabCount() > 2) {
							break;
						}

					}
				}

				if (tabs != null && tabs.getTabCount() > 2) // Check imp
				{
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
			} else if (event.getActionCommand().equals("Syntax and Coloring")) {

				CategoryChooser chooser = new CategoryChooser(
						"Jdec Syntax Highlighting Module");

			} else if (event.getActionCommand().equals("Open Simple Editor...")) {
				TextEditor editor = new TextEditor();
				editor.setDefaultCloseOperation(editor.DISPOSE_ON_CLOSE);
				editor.setVisible(true);

			} else if (event.getActionCommand().equals("New Java File...")) {
				JOptionPane option = new JOptionPane();
				String message = "You Have chosen to Create a new Java File...\n";
				message += "Jdec UI is Capable of Handling Simple Text/Java File Editing \n";
				message += "However if you would like to Edit in a separate Editor window \n";
				message += "Then Click Yes ...\n";
				message += "\n\n[Yes --Seaparate Editor]\n";
				message += "[No --Jdec Output Frame will act as Editor]\n";
				message += "[Cancel --No Action]\n";
				int str = option.showConfirmDialog(UILauncher.getMainFrame(),
						message);
				if (str == option.YES_OPTION) {
					TextEditor editor = new TextEditor();
					editor.setDefaultCloseOperation(editor.DISPOSE_ON_CLOSE);
					editor.setVisible(true);
				}
				if (str == option.NO_OPTION) {
					Manager manager = Manager.getManager();
					ArrayList paneList = manager
							.getCurrentSplitPaneComponents();
					JTabbedPane tabs = UIUtil.getUIUtil().getRightTabbedPane();
					if (tabs != null) // Check imp
					{
						tabs.setSelectedIndex(tabs
								.indexOfTab("Jdec Editor Window"));
						JScrollPane pane = (JScrollPane) tabs
								.getSelectedComponent();
						((JEditorPane) (pane.getViewport().getView()))
								.selectAll();
						((JEditorPane) (pane.getViewport().getView())).cut();

					}
				}
				if (str == option.CANCEL_OPTION) {

				}
			}

			else if (event.getActionCommand().indexOf("Save") != -1
					&& event.getActionCommand().indexOf("SaveAs") == -1) {

				JTabbedPane tb = UIUtil.getUIUtil().getRightTabbedPane();
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

			}

			else if (event.getActionCommand().indexOf("SaveAs") != -1) {

				saveAsWork();

			} else if (event.getActionCommand().equals("View Java File")) {

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
					JFileChooser chooser = new JFileChooser(".");
					JavaFileFilter filter = new JavaFileFilter();
					chooser.setFileFilter(filter);
					if (chooser.showOpenDialog(UILauncher.getMainFrame()) != JFileChooser.APPROVE_OPTION)
						return;
					File file = chooser.getSelectedFile();
					if (file == null)
						return;
					FileReader reader = null;
					try {
						reader = new FileReader(file);
						Object o = editorTab.getComponent(0);
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

							UIUtil u = UILauncher.getUIutil();
							FileInfoFrame finfo = u.getInfoFrame();
							finfo.setFileName(file.getAbsolutePath());
							finfo.recreateDetailsFrame(file.getAbsolutePath());
							Splitter split = Manager.getManager()
									.getSplitterRef();
							split.resetInfoFrame(finfo);
							UILauncher.getMainFrame().repaint();

						}
					} catch (Error error) {
						JOptionPane.showMessageDialog(
								UILauncher.getMainFrame(), error.getMessage(),
								"ERROR", JOptionPane.ERROR_MESSAGE);
					}

					catch (IOException ex) {
						JOptionPane.showMessageDialog(
								UILauncher.getMainFrame(), "File Not Found",
								"ERROR", JOptionPane.ERROR_MESSAGE);
					} catch (Throwable error2) {
						JOptionPane.showMessageDialog(
								UILauncher.getMainFrame(), error2.getMessage(),
								"ERROR", JOptionPane.ERROR_MESSAGE);
					}

					finally {
						if (reader != null) {
							try {
								reader.close();
							} catch (IOException x) {
							}
						}
					}

				}

			} else if (event.getActionCommand().equals("Copy...")) {

				JTabbedPane tab = UIUtil.getUIUtil().getRightTabbedPane();
				Component c = tab.getSelectedComponent();
				JScrollPane pane = (JScrollPane) c;
				JViewport view = (JViewport) pane.getViewport();
				JEditorPane editor = (JEditorPane) view.getView();
				UIUtil.addSelection(UIUtil.currentSelString);
				String s1 = editor.getSelectedText();
				int i1 = editor.getSelectionStart();
				int i2 = editor.getSelectionEnd();
				editor.select(i1, i2);
				editor.copy();

			} else if (event.getActionCommand().equals("Cut...")) {

				JTabbedPane tab = UIUtil.getUIUtil().getRightTabbedPane();
				Component c = tab.getSelectedComponent();
				JScrollPane pane = (JScrollPane) c;
				JViewport view = (JViewport) pane.getViewport();
				JEditorPane editor = (JEditorPane) view.getView();
				UIUtil.addSelection(UIUtil.currentSelString);
				editor.cut();

			}

			else if (event.getActionCommand().equals("Paste...")) {
				JEditorPane editor = UIUtil.getUIUtil().getEditorWindow();
				editor.paste();
			}

			else if (event.getActionCommand().equals("Select All...")) {
				JTabbedPane tab = UIUtil.getUIUtil().getRightTabbedPane();
				Component c = tab.getSelectedComponent();
				JScrollPane pane = (JScrollPane) c;
				JViewport view = (JViewport) pane.getViewport();
				JEditorPane editor = (JEditorPane) view.getView();
				editor.selectAll();
			} else if (event.getActionCommand().equals(
					"Jdec(Decompiler) Configuration ...")) {
				JTabbedPane tabs = UIUtil.getUIUtil().getRightTabbedPane();
				DecompilerConfigDetails config = new DecompilerConfigDetails(
						UILauncher.getUIutil());
				if (tabs.indexOfTab("Jdec Configuration") != -1)
					tabs.remove(tabs.indexOfTab("Jdec Configuration"));
				tabs.addTab("Jdec Configuration", config);
				tabs.setSelectedComponent(config);
			}

			else if (event.getActionCommand().equals("Hide Console")) {

				Splitter ref = Manager.getManager().getSplitterRef();
				// JSplitPane right=ref.getRightSplitPane();
				ref.resetRightSplitPane("hide");
				ref.getSplitPlane().revalidate();
				UILauncher.getMainFrame().validate();
				// UILauncher.getMainFrame().validate();

			} else if (event.getActionCommand().equals("Show Console")) {

				Splitter ref = Manager.getManager().getSplitterRef();
				JSplitPane right = ref.getRightSplitPane();
				JSplitPane rightOld = right;
				int w = right.getWidth();
				Component console = ref.getconsoleFrameComponent();
				Component c4 = ref.getOutputTabFrameComponent();
				right = new JSplitPane(JSplitPane.VERTICAL_SPLIT, c4, console);
				// ref.getLeftSplitPane().setSize(ref.getLeftSplitPane().getWidth(),c4.getHeight()+75);
				// c4.setSize(w, c4.getHeight()+75);
				// right.setDividerSize(8);
				right.setContinuousLayout(true);
				right.setOrientation(0);
				right.setDividerSize(5);
				right.setOneTouchExpandable(true);
				right.setLastDividerLocation(1);
				right.setResizeWeight(0.8);
				right.setBounds(253, 1, 752, 631);
				right.setMinimumSize(new Dimension(rightOld.getWidth() - 75,
						rightOld.getHeight()));
				ref.setSpRight(right);
				// ref.getSplitPlane().remove(1);
				// ref.getLeftSplitPane().setDividerLocation(0.3);
				ref.getSplitPlane().setResizeWeight(0.5);
				ref.getSplitPlane().setOneTouchExpandable(true);
				ref.getSplitPlane().setDividerSize(5);
				// ref.getSplitPlane().resetToPreferredSizes();
				Method m = ref.getSplitPlane().getClass()
						.getDeclaredMethod(
								"addImpl",
								new Class[] { Component.class, Object.class,
										int.class });
				m.setAccessible(true);

				m.invoke(ref.getSplitPlane(), new Object[] { right,
						JSplitPane.RIGHT, new Integer(1) });
				// ref.getSplitPlane().revalidate();
				// ref.getSplitPlane().repaint();
				// UILauncher.getMainFrame().getContentPane().add(s.getSplitPlane(),BorderLayout.CENTER);
				// UILauncher.getMainFrame().validate();

			} else if (event.getActionCommand().equals("Reset Tabs...")) {
				JTabbedPane tabs = UIUtil.getUIUtil().getRightTabbedPane();
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
				index = tabs.indexOfTab("Jdec Help");
				if (index != -1)
					tabs.remove(index);
				index = tabs.indexOfTab("Skeleton Output");
				if (index != -1)
					tabs.remove(index);
				index = tabs.indexOfTab("UILog Output");
				if (index != -1)
					tabs.remove(index);
				index = tabs.indexOfTab("DecompilerLog Output");
				if (index != -1)
					tabs.remove(index);
				index = tabs.indexOfTab("Version1");
				if (index != -1)
					tabs.remove(index);
				index = tabs.indexOfTab("Version2");
				if (index != -1)
					tabs.remove(index);
				index = tabs.indexOfTab("Jdec Welcome");
				if (index != -1)
					tabs.remove(index);
				index = tabs.indexOfTab("Diff View");
				if (index != -1)
					tabs.remove(index);

			} else if (event.getActionCommand().equals(
					"Hide All Configuration Tabs...")) {
				JTabbedPane tabs = UIUtil.getUIUtil().getRightTabbedPane();
				int index = tabs.indexOfTab("Jdec Configuration");
				if (index != -1) {
					tabs.remove(index);
				}
				index = tabs.indexOfTab("UI Configuration...");
				if (index != -1) {
					tabs.remove(index);
				}
			} else if (event.getActionCommand().equals("Show System Time")) {
				Manager mgr = Manager.getManager();
				mgr.showTime = true;
				Thread t = new ShowTime();
				t.setDaemon(true);
				t.setPriority(Thread.NORM_PRIORITY);
				t.start();
			} else if (event.getActionCommand().equals("fullHelp")) {
				File bp = UILauncher.getUIutil().getBrowserPath();
				if (bp != null) {
					try {
						java.lang.String cmd = bp.getAbsolutePath();
						cmd += " file:///" + System.getProperty("user.dir")
								+ File.separator + "doc/Documentaion.html";
						Runtime rt = Runtime.getRuntime();
						rt.exec(cmd);
					} catch (Exception exp) {

						new askbrowser();

					}
				} else {

					new askbrowser();

				}
			}

			else if (event.getActionCommand().equals("Hide System Time")) {
				Manager mgr = Manager.getManager();
				mgr.showTime = false;
				Thread t = new UnShowTime();
				t.setDaemon(true);
				t.setPriority(Thread.MAX_PRIORITY);
				t.start();
			} else if (event.getActionCommand().equals("GoTo User Home Folder")) {
				Manager manager = Manager.getManager();
				ArrayList paneList = manager.getCurrentSplitPaneComponents();
				JTabbedPane tabs = null;
				tabs = UIUtil.getUIUtil().getRightTabbedPane();
				if (tabs != null) {
					tabs
							.setSelectedIndex(tabs
									.indexOfTab("Jdec Editor Window"));
					int pos = tabs.getSelectedIndex();
					Component editor = tabs.getSelectedComponent();
					JScrollPane editorTab = (JScrollPane) editor;
					JFileChooser chooser = new JFileChooser(System
							.getProperty("user.home"));
					if (chooser.showOpenDialog(UILauncher.getMainFrame()) != JFileChooser.APPROVE_OPTION)
						return;
					File file = chooser.getSelectedFile();
					if (file == null)
						return;
					FileReader reader = null;
					try {
						reader = new FileReader(file);
						Object o = editorTab.getComponent(0);

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

			} else if (event.getActionCommand().equals("Favorite list")) {
				ArrayList list = UIUtil.getUIUtil().getFavoriteList();
				String allf[] = (String[]) list
						.toArray(new String[list.size()]);
				for (int x = 0; x < allf.length; x++) {
					String t = allf[x];
					JMenuItem m = new JMenuItem(t);
					Manager.getManager().proj.getItem().add(m);
				}
			} else if (event.getActionCommand().equals("Edit Favorite List")) {
				final JFrame fav = new JFrame(
						"Edit your favorite folder list here...");
				JPanel txtp = new JPanel();
				final JTextField txt = new JTextField();
				txt.setColumns(35);
				txtp.add(txt);
				JLabel n = new JLabel(
						"[NOTE:] Jdec needs to be restarted for Favorite Folder Tab to be updated...");
				n.setForeground(Color.RED);
				txtp.add(n);
				JLabel lbl = new JLabel("Please enter folder path");
				lbl.setFont(new Font("Monospaced", Font.BOLD, 12));
				JButton app = new JButton("Add to my favorite list");
				JButton cl = new JButton("Close");
				fav.getContentPane().add(lbl, BorderLayout.NORTH);
				fav.getContentPane().add(txtp, BorderLayout.CENTER);
				JPanel p = new JPanel();
				p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
				p.add(app);
				p.add(cl);
				fav.getContentPane().add(p, BorderLayout.SOUTH);
				fav.setBounds(200, 200, 421, 200);
				fav.setVisible(true);
				cl.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						fav.setVisible(false);
						fav.dispose();
					}

				});
				app.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String f = txt.getText();
						if (f != null) {
							if (f.trim().length() == 0)
								return;
							File fl = new File(f);
							if (fl.exists() && fl.isDirectory()) {
								UIUtil.getUIUtil().addFavorite(f);
								ArrayList list = UIUtil.getUIUtil()
										.getFavoriteList();
								String allf[] = (String[]) list
										.toArray(new String[list.size()]);
								Manager.getManager().proj.getItem().removeAll();
								for (int x = 0; x < allf.length; x++) {
									String t = allf[x];
									JMenuItem m = new JMenuItem(t);
									Manager.getManager().proj.getItem().add(m);

								}
								UILauncher.getMainFrame().repaint();
								UILauncher.getUIConfigRef().setFavoriteList(
										UIUtil.getUIUtil().getFavoriteList());

							} else if (fl.exists() == false
									|| (fl.isDirectory() == false)) {
								String fnm = fl.getAbsolutePath();
								if (fnm == null || fnm.trim().length() == 0)
									return;
								int c = JOptionPane.showOptionDialog(fav,
										"[Folder " + f + " does not exist]",
										"Create Folder",
										JOptionPane.YES_NO_OPTION,
										JOptionPane.INFORMATION_MESSAGE, null,
										null, null);
								if (c == JOptionPane.YES_OPTION) {
									fl.mkdirs();
									UIUtil.getUIUtil().addFavorite(f);
									ArrayList list = UIUtil.getUIUtil()
											.getFavoriteList();
									String allf[] = (String[]) list
											.toArray(new String[list.size()]);
									Manager.getManager().proj.getItem()
											.removeAll();
									for (int x = 0; x < allf.length; x++) {
										String t = allf[x];
										JMenuItem m = new JMenuItem(t);
										Manager.getManager().proj.getItem()
												.add(m);
									}
									UILauncher.getMainFrame().repaint();
									UILauncher.getUIConfigRef()
											.setFavoriteList(
													UIUtil.getUIUtil()
															.getFavoriteList());
								}

							}
						}
					}
				});
			}

			else if (event.getActionCommand().equals("GoTo Project Folder")) {
				Manager manager = Manager.getManager();
				ArrayList paneList = manager.getCurrentSplitPaneComponents();
				JTabbedPane tabs = null;
				tabs = UIUtil.getUIUtil().getRightTabbedPane();
				if (tabs != null) {
					tabs
							.setSelectedIndex(tabs
									.indexOfTab("Jdec Editor Window"));
					int pos = tabs.getSelectedIndex();
					Component editor = tabs.getSelectedComponent();
					JScrollPane editorTab = (JScrollPane) editor;
					String str = UIUtil.getUserProjFol();
					if (str != null && str.trim().length() > 0) {
						JFileChooser chooser = new JFileChooser(str);
						if (chooser.showOpenDialog(UILauncher.getMainFrame()) != JFileChooser.APPROVE_OPTION)
							return;
						File file = chooser.getSelectedFile();
						if (file == null)
							return;
						FileReader reader = null;
						try {
							reader = new FileReader(file);
							Object o = editorTab.getComponent(0);

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
							JOptionPane.showMessageDialog(UILauncher
									.getMainFrame(), "File Not Found", "ERROR",
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
			} else if (event.getActionCommand().equals("Set Project Folder")) {
				try {
					String input = JOptionPane.showInputDialog(UILauncher
							.getMainFrame(), "User Project Folder Path :",
							"Please Specify Your Project Folder Path",
							JOptionPane.OK_CANCEL_OPTION);
					File f = new File(input);
					if (f.exists()) {
						boolean exit = false;
						UILauncher.getUIutil().setUserProjFol(
								f.getAbsolutePath());
						JMenuBar bar = UILauncher.getMainFrame().getJMenuBar();
						Component menus[] = bar.getComponents();
						for (int s = 0; s < menus.length; s++) {
							JMenu menu = (JMenu) menus[s];
							if (menu.getText().toLowerCase().trim().equals(
									"utilities")) {

								JPopupMenu popup = menu.getPopupMenu();
								Component c = popup.getComponent(7);
								if (c instanceof JMenuItem) {
									JMenuItem item = (JMenuItem) c;
									item.setEnabled(true);
									String name = "userprojectHome";
									String value = f.getAbsolutePath();
									UIConfig uiconfig = UILauncher
											.getUIConfigRef();
									uiconfig.addPref(name, value);
								}

							}

						}

					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(UILauncher.getMainFrame(),
							"Path Invalid...");
					String input = JOptionPane.showInputDialog(UILauncher
							.getMainFrame(), "User Project Folder Path :",
							"Please Specify Your Project Folder Path",
							JOptionPane.OK_CANCEL_OPTION);
				}
			} else if (event.getActionCommand().equals("Close UI...")) {
				try {
					UIConfig uiconfig = UILauncher.getUIConfigRef();
					uiconfig.persistToFile();
					UILauncher.closeChildWindows();
				} catch (IOException ioe) {
					try {
						LogWriter lg = LogWriter.getInstance();
						lg
								.writeLog("[ERROR]: Method: actionPerformed\n\tClass: ActionItemListener.class");
						lg
								.writeLog("------------------------------------------------");
						lg.writeLog("Exception Stack Trace");
						ioe.printStackTrace(lg.getPrintWriter());
						lg.flush();
					} catch (Exception e) {

					}

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

					}

					JFrame frame = UILauncher.getMainFrame();
					frame.setVisible(false);
					frame.setEnabled(false);
					frame.dispose();
					if (lw != null) {
						lw.flush();

					}
					UILauncher.closeChildWindows();
					File replaceMeFile = new File("***REPLACEME***");
					if (replaceMeFile.exists()) {
						replaceMeFile.delete();
						if (replaceMeFile.exists()) {
							replaceMeFile.deleteOnExit();
						}
					}

				}

			} else if (event.getActionCommand().equals("System Properties")) {

				UIUtil util = UILauncher.getUIutil();
				util.loadSystemProperties();
				SystemProperties ref = Manager.getManager()
						.getSystemPropertyRef();
				if (ref != null) {
					JTabbedPane tabs = UIUtil.getUIUtil().getRightTabbedPane();
					int index = tabs.indexOfTab("System Properties");
					if (index != -1)
						tabs.remove(index);
					tabs.addTab("System Properties", ref);
					tabs.setSelectedComponent(ref);
				} else {
					JOptionPane
							.showMessageDialog(
									UILauncher.getMainFrame(),
									"[ERROR] :System Properties Could Not be loaded.....",
									"Loading Error ", JOptionPane.ERROR_MESSAGE);

				}

			} else if (event.getActionCommand().equals("Find Target")) {

				JTabbedPane tabpane = UIUtil.getUIUtil().getRightTabbedPane();
				int selindex = tabpane.getSelectedIndex();
				String openfile = UILauncher.getUIutil().getCurrentOpenFile();
				// String openfile=(String)ht.get(new Integer(selindex));
				if (openfile == null)
					openfile = "";
				String curOpenFile = openfile;// UILauncher.getUIutil().getCurrentOpenFile();
				if (curOpenFile.lastIndexOf(File.separator) != -1) {
					String folder = curOpenFile.substring(0, curOpenFile
							.lastIndexOf(File.separator));
					Manager manager = Manager.getManager();
					ArrayList paneList = manager
							.getCurrentSplitPaneComponents();
					JTabbedPane tabs = null;
					tabs = UIUtil.getUIUtil().getRightTabbedPane();
					if (tabs != null) {
						tabs.setSelectedIndex(tabs
								.indexOfTab("Jdec Editor Window"));
						int pos = tabs.getSelectedIndex();
						Component editor = tabs.getSelectedComponent();
						JScrollPane editorTab = (JScrollPane) editor;
						JFileChooser chooser = new JFileChooser(folder);
						chooser.setSelectedFile(new File(curOpenFile));

						if (chooser.showOpenDialog(UILauncher.getMainFrame()) != JFileChooser.APPROVE_OPTION)
							return;
						File file = chooser.getSelectedFile();
						if (file == null)
							return;
						FileReader reader = null;
						try {
							reader = new FileReader(file);
							Object o = editorTab.getComponent(0);
							JViewport view = (JViewport) o;
							Object o2 = view.getView();
							if (o2 != null) {
								JEditorPane rdwrPane = (JEditorPane) o2;
								UILauncher.getUIutil().setCurrentOpenFile(
										file.getAbsolutePath());
								UILauncher.getUIutil().addToTabFileMap(pos,
										file.getAbsolutePath());
								rdwrPane.read(reader, null);
							}
						} catch (IOException ex) {
							JOptionPane.showMessageDialog(UILauncher
									.getMainFrame(), "File Not Found", "ERROR",
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

				} else {
					String m = "[ERROR]   Could not Find Target Folder...\n\n";
					/*
					 * m += "Find Target Works in these scenarios...\n"; m +=
					 * "1>User Selects \"View Java File\" Operation \n"; m +=
					 * "2>User Selects \"GoTo User Home Folder\" AND opens a
					 * File \n"; m += "3>User Selects \"GoTo Project Folder\"
					 * AND opens a File \n";
					 */
					JOptionPane.showMessageDialog(UILauncher.getMainFrame(), m,
							"Target Not Found", JOptionPane.ERROR_MESSAGE);
				}

			} else if (event.getActionCommand().equals("Show Clipboard")) {
				ClipBoardViewer clipBrdViewer = new ClipBoardViewer(
						"Jdec ClipBoard Viewer");
				clipBrdViewer.display();
			} else if (event.getActionCommand().equals("Clear History")) {
				int opt = JOptionPane.showConfirmDialog(UILauncher
						.getMainFrame(),
						"This will clear your recent file list...",
						"Are you Sure ?", JOptionPane.YES_NO_OPTION);
				if (opt == JOptionPane.YES_OPTION) {
					UILauncher.getUIutil().clearHistory();
					JOptionPane.showMessageDialog(UILauncher.getMainFrame(),
							"User History cleared", "Clear History",
							JOptionPane.INFORMATION_MESSAGE);
				}

			} else if (event.getActionCommand()
					.equals("Show Recent File(s)...")) {
				if (UILauncher.getUIutil().getRecentFileList() != null
						&& UILauncher.getUIutil().getRecentFileList().size() > 0) {
					RecentFileList recent = new RecentFileList(UILauncher
							.getUIutil());
					Manager.getManager().setRecentFileRef(recent);
				} else {
					JOptionPane
							.showMessageDialog(UILauncher.getMainFrame(),
									"[INFO] :Recent File List Is Empty...",
									"Show Recent Fies",
									JOptionPane.INFORMATION_MESSAGE);
				}
			} else if (event.getActionCommand().equals("Preferences...")) {
				Preferences prefs = new Preferences(new JFrame());
				Manager.getManager().setPreferencesRef(prefs);
				UILauncher.addChildRef(prefs);
			} else if (event.getActionCommand().equals("Compile Java...")) {
				Javac javac = new Javac(new JFrame());
				Manager.getManager().setJavacRef(javac);
				UILauncher.addChildRef(javac);
			} else if (event.getActionCommand().equals(
					"Run Java Application...")) {

				Java java = new Java(new JFrame());
				Manager.getManager().setJavaRef(java);
				UILauncher.addChildRef(java);

			}

			else if (event.getActionCommand().equals("welcome")) {

				Welcome w = new Welcome("Welcome", false);
				JTabbedPane rt = UIUtil.getUIUtil().getRightTabbedPane();
				int i = rt.indexOfTab("Jdec Welcome");
				if (i != -1) {
					rt.setSelectedIndex(i);
					return;
				} else {
					rt.addTab("Jdec Welcome", w);
					rt.setSelectedComponent(w);
					Splitter ref = Manager.getManager().getSplitterRef();
					// JSplitPane right=ref.getRightSplitPane();
					ref.resetRightSplitPane("hide");
					ref.getSplitPlane().revalidate();
					UILauncher.getMainFrame().validate();
					return;
				}

			} else if (event.getActionCommand().equals("wnew")) {

				WhatsNew w = new WhatsNew();
				w.createMe();

			} else if (event.getActionCommand().equals("Find ...")) {

				FindDialog find = new FindDialog(UILauncher.getMainFrame());
				find.setVisible(true);
				UILauncher.addChildRef(find);

			} else if (event.getActionCommand().equals(
					"Exception Table Details...")) {
				Thread t1 = new Thread() {

					public void run() {

						ExceptionTableWindow ep = new ExceptionTableWindow();
						if (DecompilerBridge
								.getInstance(UILauncher.getUIutil())
								.isUserCanceled())
							return;
						JTabbedPane rightTab = UIUtil.getUIUtil()
								.getRightTabbedPane();
						int present = rightTab.indexOfTab("Exception Tables");
						if (present != -1)
							rightTab.remove(present);
						rightTab.addTab("Exception Tables", ep);

						UIUtil u = UILauncher.getUIutil();
						FileInfoFrame finfo = u.getInfoFrame();

						// finfo.setFileName(u.getJavaClassFile());
						String s = Configuration.getJavaClassFile();

						int winslash = s.indexOf("\\");
						int unixslash = s.indexOf("/");

						if (winslash != -1) {
							s = s.replace('\\', File.separator.charAt(0));
						}
						if (unixslash != -1) {
							s = s.replace('/', File.separator.charAt(0));
						}

						finfo.recreateDetailsFrame(s);
						Splitter split = Manager.getManager().getSplitterRef();
						split.resetInfoFrame(finfo);
						UILauncher.getMainFrame().repaint();

						rightTab.setSelectedComponent(ep);
					}
				};
				Thread t2 = new Thread() {

					public void run() {

						JDialog proframe = UIUtil.launchProgressBarFrame();
						proframe.setVisible(true);

						while (Manager.getManager().isShowProgressBar() == true) {

							gotoSleep();
						}

						if (!Manager.getManager().isShowProgressBar()) {
							proframe.setVisible(false);
							proframe.dispose();
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
				if (UIUtil.checkForInvalidEntries("")) {
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

			} else if (event.getActionCommand().equals("Constant Pool...")) {
				final JTabbedPane rightTab = UIUtil.getUIUtil()
						.getRightTabbedPane();
				int cp = rightTab.indexOfTab("Constant Pool");
				if (cp != -1)
					rightTab.remove(cp);
				/*
				 * if(rightTab.indexOfTab("Constant Pool")!=-1) { // fixmelater :
				 * Need to write better check here //
				 * JOptionPane.showMessageDialog(UILauncher.getMainFrame(),
				 * "Constant Pool Is Already Displayed"); } else {
				 */
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

						DecompilerBridge bridge = DecompilerBridge
								.getInstance(UILauncher.getUIutil());
						if (c != null) {
							JEditorPane rdwr = c.getComponent();
							String s = "Current Task: View Constant Pool\n";
							s = rdwr.getText() + "\n\n" + s;
							rdwr.setText(s);
						}
						bridge.execute("ConstantPool", "", false, "view");
						if (bridge.isUserCanceled())
							return;
						if (c != null && !bridge.isUserCanceled()) {
							JEditorPane rdwr = c.getComponent();
							String s = "Current File: "
									+ Configuration.getJavaClassFile()
									+ "\nDone...Please Wait for UI to render the output....\nPlease Check up the log files for any error(s)\n";
							s = rdwr.getText() + "\n\n" + s;
							rdwr.setText(s);
						}
						// bridge.showResult(rightTab);

						Manager.getManager().setShowProgressBar(false);
						ConsantPoolDescription ref = null;
						if (UIUtil.getUIUtil().getcpdesc() != null
								&& UIUtil.getUIUtil().getcpdesc().size() > 0)
							ref = new ConsantPoolDescription(UIUtil.getUIUtil()
									.getcpdesc());
						else
							ref = null;
						// SystemProperties ref =
						// Manager.getManager().getSystemPropertyRef();
						if (ref != null) {
							JTabbedPane tabs = UIUtil.getUIUtil()
									.getRightTabbedPane();
							int index = tabs.indexOfTab("Constant Pool");
							if (index != -1)
								tabs.remove(index);
							tabs.addTab("Constant Pool", ref);
							tabs.setSelectedComponent(ref);

							UIUtil u = UILauncher.getUIutil();
							FileInfoFrame finfo = u.getInfoFrame();

							// finfo.setFileName(u.getJavaClassFile());
							String s = Configuration.getJavaClassFile();
							if (s.indexOf(File.separator) == -1) {
								int winslash = s.indexOf("\\");
								int unixslash = s.indexOf("/");

								if (winslash != -1) {
									s = s.replace('\\', File.separator
											.charAt(0));
								}
								if (unixslash != -1) {
									s = s
											.replace('/', File.separator
													.charAt(0));
								}

							}
							finfo.recreateDetailsFrame(s);
							Splitter split = Manager.getManager()
									.getSplitterRef();
							split.resetInfoFrame(finfo);
							UILauncher.getMainFrame().repaint();

						}
						if (ref == null && !bridge.isUserCanceled()) {
							JOptionPane
									.showMessageDialog(
											UILauncher.getMainFrame(),
											"[ERROR] :Constant Pool could not be loaded.....",
											"Loading Error ",
											JOptionPane.ERROR_MESSAGE);

						}

					}
				};
				Thread t2 = new Thread() {

					public void run() {

						JDialog proframe = UIUtil.launchProgressBarFrame();
						proframe.setVisible(true);

						while (Manager.getManager().isShowProgressBar() == true) {

							gotoSleep();
						}

						if (!Manager.getManager().isShowProgressBar()) {
							proframe.setVisible(false);
							proframe.dispose();
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

				if (UIUtil.checkForInvalidEntries("")) {
					t1.setPriority(Thread.MAX_PRIORITY);
					t2.setPriority(Thread.NORM_PRIORITY);
					t1.start();
					// t2.start();
				} else {
					JOptionPane
							.showMessageDialog(
									UILauncher.getMainFrame(),
									"Please Check The Decompiler Configuration Property Values Again",
									"JDEC WARNING",
									JOptionPane.INFORMATION_MESSAGE);
				}

			} else if (event.getActionCommand().equals(
					"Class Local Variables...")) {
				Thread t1 = new Thread() {

					public void run() {

						Console c = UILauncher.getUIutil().getConsoleFrame();
						if (c != null) {
							JEditorPane rdwr = c.getComponent();
							rdwr.setText("");
							String s = "Jdec Started..\n";
							s = rdwr.getText() + "\n\n" + s;
							rdwr.setText(s);
						}
						Manager.getManager().setShowProgressBar(true);
						LocalVariableWindow lv = new LocalVariableWindow("view");
						DecompilerBridge bridge = DecompilerBridge
								.getInstance(UILauncher.getUIutil());

						if (bridge.isUserCanceled() == false) {
							JTabbedPane rightTab = UIUtil.getUIUtil()
									.getRightTabbedPane();
							int index = rightTab.indexOfTab("Local Variables");
							if (index != -1)
								rightTab.remove(index);
							rightTab.addTab("Local Variables", lv);
							rightTab.setSelectedComponent(lv);

							UIUtil u = UILauncher.getUIutil();
							FileInfoFrame finfo = u.getInfoFrame();

							// finfo.setFileName(u.getJavaClassFile());
							String s = Configuration.getJavaClassFile();
							if (s.indexOf(File.separator) == -1) {
								int winslash = s.indexOf("\\");
								int unixslash = s.indexOf("/");

								if (winslash != -1) {
									s = s.replace('\\', File.separator
											.charAt(0));
								}
								if (unixslash != -1) {
									s = s
											.replace('/', File.separator
													.charAt(0));
								}

							}
							finfo.recreateDetailsFrame(s);
							Splitter split = Manager.getManager()
									.getSplitterRef();
							split.resetInfoFrame(finfo);
							UILauncher.getMainFrame().repaint();

						} else {
							Manager.getManager().setShowProgressBar(false);
						}
					}
				};
				t1.setPriority(Thread.MAX_PRIORITY);
				Manager.getManager().setStatusThread(t1);
				Thread t2 = new Thread() {

					public void run() {

						JDialog proframe = UIUtil.launchProgressBarFrame();
						proframe.setVisible(true);
						// proframe.requestFocusInWindow();

						while (Manager.getManager().isShowProgressBar() == true) {

							gotoSleep();
						}

						if (!Manager.getManager().isShowProgressBar()) {
							proframe.setVisible(false);
							proframe.dispose();
							proframe = null;

						}

					}

				};
				if (UIUtil.checkForInvalidEntries("")) {
					t1.setPriority(Thread.MAX_PRIORITY);
					t2.setPriority(Thread.NORM_PRIORITY);
					t1.start();
					// t2.start();
				} else {
					JOptionPane
							.showMessageDialog(
									UILauncher.getMainFrame(),
									"Please Check The Decompiler Configuration Property Values Again",
									"JDEC WARNING",
									JOptionPane.INFORMATION_MESSAGE);
				}

			}

			else if (event.getActionCommand().equals(
					"Class General   Information...")) {

				JTabbedPane rightTab = UIUtil.getUIUtil().getRightTabbedPane();
				int cp = rightTab.indexOfTab("Class Details");
				if (cp != -1)
					rightTab.remove(cp);
				/*
				 * if(rightTab.indexOfTab("Class Details")!=-1) {
				 * JOptionPane.showMessageDialog(UILauncher.getMainFrame(),
				 * "Class Details Is Already Displayed"); } else {
				 */

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
							// }

							DecompilerBridge bridge = DecompilerBridge
									.getInstance(UILauncher.getUIutil());
							if (c != null) {

								s = "Current Task: Class General Information\n";
								s = rdwr.getText() + "\n\n" + s;
								rdwr.setText(s);
							}

							bridge.execute("general",
									"Class General Information", true, "view");
							// bridge.execute("decompileClass", "Class General
							// Information",true, "view");
							// bridge.showResult(rightTab); // Dont USE THIS AS
							// this will
							// show decompiled output
							if (bridge.isUserCanceled())
								return;
							ArrayList cd = formClasDetails();
							if (c != null) {

								s = "Current File: "
										+ Configuration.getJavaClassFile()
										+ "\n";
								s += "Done...Please Wait for UI to render the output...\nPlease check the log file for any error(s)\n";
								s = rdwr.getText() + "\n\n" + s;
								rdwr.setText(s);
							}

							Manager.getManager().setShowProgressBar(false);
							ClassDetails ref = new ClassDetails(cd);

							if (ref != null) {
								JTabbedPane tabs = UIUtil.getUIUtil()
										.getRightTabbedPane();
								int index = tabs.indexOfTab("Class Details");
								if (index != -1)
									tabs.remove(index);
								tabs.addTab("Class Details", ref);
								tabs.setSelectedComponent(ref);

								UIUtil u = UILauncher.getUIutil();
								FileInfoFrame finfo = u.getInfoFrame();

								// finfo.setFileName(u.getJavaClassFile());
								String cn = Configuration.getJavaClassFile();
								if (cn.indexOf(File.separator) == -1) {
									int winslash = cn.indexOf("\\");
									int unixslash = cn.indexOf("/");

									if (winslash != -1) {
										cn = cn.replace('\\', File.separator
												.charAt(0));
									}
									if (unixslash != -1) {
										cn = cn.replace('/', File.separator
												.charAt(0));
									}

								}
								finfo.recreateDetailsFrame(cn);
								Splitter split = Manager.getManager()
										.getSplitterRef();
								split.resetInfoFrame(finfo);
								UILauncher.getMainFrame().repaint();

							} else {
								JOptionPane
										.showMessageDialog(
												UILauncher.getMainFrame(),
												"[ERROR] :Class Details could not be loaded.....",
												"Loading Error ",
												JOptionPane.ERROR_MESSAGE);

							}

						} else {
							Manager.getManager().setShowProgressBar(false);
						}

					}

				};

				Thread t2 = new Thread() {

					public void run() {

						JDialog proframe = UIUtil.launchProgressBarFrame();
						if (proframe != null) {
							proframe.setFocusable(true);
							proframe.setFocusableWindowState(true);
							proframe.setVisible(true);
						}
						// proframe.requestFocusInWindow();

						while (Manager.getManager().isShowProgressBar() == true) {

							gotoSleep();
						}

						if (!Manager.getManager().isShowProgressBar()) {
							proframe.setVisible(false);
							proframe.dispose();
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
				if (UIUtil.checkForInvalidEntries("")) {
					t1.setPriority(Thread.MAX_PRIORITY);
					t2.setPriority(Thread.NORM_PRIORITY);
					t1.start();
					// t2.start();
				} else {
					JOptionPane
							.showMessageDialog(
									UILauncher.getMainFrame(),
									"Please Check The Decompiler Configuration Property Values Again",
									"JDEC WARNING",
									JOptionPane.INFORMATION_MESSAGE);
				}

			} else if (event.getActionCommand().equals("GTKLookAndFeel")) {
				UILauncher.getUIutil().setCurrentLNF("GTKLookAndFeel");
				updateLookAndFeel(UILauncher.getUIutil().getGtkClass());

			}

			else if (event.getActionCommand().equals("WindowsLookAndFeel")) {
				UILauncher.getUIutil().setCurrentLNF("WindowsLookAndFeel");
				updateLookAndFeel(UILauncher.getUIutil().getWindowsClass());

			} else if (event.getActionCommand().equals("MetalLookAndFeel")) {
				UILauncher.getUIutil().setCurrentLNF("MetalLookAndFeel");
				updateLookAndFeel(UILauncher.getUIutil().getMetalClass());

			}

			else if (event.getActionCommand().equals("MotifLookAndFeel")) {
				UILauncher.getUIutil().setCurrentLNF("MotifLookAndFeel");
				updateLookAndFeel(UILauncher.getUIutil().getMotifClass());

			}

		} catch (Throwable t) {
			try {
				LogWriter lg = LogWriter.getInstance();
				lg
						.writeLog("[ERROR]: Method: actionPerformed\n\tClass: ActionItemListener.class");
				lg.writeLog("------------------------------------------------");
				lg.writeLog("Exception Stack Trace");
				t.printStackTrace(lg.getPrintWriter());
				lg.flush();

			} catch (Exception e) {

			}
		}

	}

	private void updateLookAndFeel(String name) {
		try {

			try {
				UIManager.setLookAndFeel(name);
			} catch (ClassNotFoundException c) {

				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			}

			SwingUtilities.updateComponentTreeUI(UILauncher.getMainFrame());
			// UILauncher.getMainFrame().pack();
			ArrayList allComps = Manager.getManager().getAllUIComponents();
			for (int s = 0; s < allComps.size(); s++) {
				if (allComps.get(s) instanceof AuxToolBar
						&& (UILauncher.getUIutil().getCurrentLNF().equals(
								"WindowsLookAndFeel") || UILauncher.getUIutil()
								.getCurrentLNF().equals("MetalLookAndFeel"))) {
					AuxToolBar aux = (AuxToolBar) allComps.get(s);
					Manager.getManager().reCreateAuxbar(aux);
				}
			}

		} catch (ClassNotFoundException cne) {

			try {
				LogWriter lg = LogWriter.getInstance();
				lg
						.writeLog("[ERROR]: Method: updateLookAndFeel\n\tClass: ActionItemListener.class");
				lg.writeLog("------------------------------------------------");
				lg.writeLog("Exception Stack Trace");
				cne.printStackTrace(lg.getPrintWriter());
				lg.flush();

			} catch (Exception e) {

			}

		} catch (InstantiationException ine) {
			try {
				LogWriter lg = LogWriter.getInstance();
				lg
						.writeLog("[ERROR]: Method: updateLookAndFeel\n\tClass: ActionItemListener.class");
				lg.writeLog("------------------------------------------------");
				lg.writeLog("Exception Stack Trace");
				ine.printStackTrace(lg.getPrintWriter());
				lg.flush();
				JOptionPane.showMessageDialog(UILauncher.getMainFrame(),
						"Unable to Instantiate the Class " + name);
			} catch (Exception e) {

			}

		} catch (IllegalAccessException ile) {
			try {
				LogWriter lg = LogWriter.getInstance();
				lg
						.writeLog("[ERROR]: Method: updateLookAndFeel\n\tClass: ActionItemListener.class");
				lg.writeLog("------------------------------------------------");
				lg.writeLog("Exception Stack Trace");
				ile.printStackTrace(lg.getPrintWriter());
				lg.flush();
				JOptionPane.showMessageDialog(UILauncher.getMainFrame(),
						"Problem while  accessing the Class " + name);
			} catch (Exception e) {

			}

		} catch (UnsupportedLookAndFeelException uns) {
			try {
				LogWriter lg = LogWriter.getInstance();
				lg
						.writeLog("[ERROR]: Method: updateLookAndFeel\n\tClass: ActionItemListener.class");
				lg.writeLog("------------------------------------------------");
				lg.writeLog("Exception Stack Trace");
				uns.printStackTrace(lg.getPrintWriter());
				lg.flush();
				JOptionPane.showMessageDialog(UILauncher.getMainFrame(), name
						+ " Not Supported!!!");
			} catch (Exception e) {

			}

		}

	}

	private ArrayList formClasDetails() {
		ArrayList classdetails = new ArrayList();
		int magic = ConsoleLauncher.getMagicNumber();
		int major = ConsoleLauncher.getMajorVersion();
		int minor = ConsoleLauncher.getMinorVersion();
		JavaClass clazzRef = ConsoleLauncher.getClazzRef();
		java.lang.String thisClass = clazzRef.getClassName();
		java.lang.String parent = clazzRef.getSuperclassname();
		java.lang.String pkg = clazzRef.getPackageName();
		int metCount = clazzRef.getALLMethods().size();
		int fCount = clazzRef.getDecalaredFields().size();
		int intCount = clazzRef.getInterfacesImplemented().size();
		int constCount = clazzRef.getConstructors().size();
		ArrayList intnames = clazzRef.getInterfacesImplemented();

		StringBuffer details = new StringBuffer("Class Details For Class "
				+ thisClass + ".class\n\n");
		details.append("Class Property\t\t\tProperty Value\n\n");
		UIUtil util = UILauncher.getUIutil();
		details.append("Magic Number:\t\t" + magic + "  [Hex Value]: "
				+ Integer.toHexString(magic) + "\n");

		String magicStr[] = new String[2];
		magicStr[0] = "Magic Number";
		magicStr[1] = Integer.toHexString(magic);
		classdetails.add(magicStr);

		details.append("Major Number:\t\t\t" + major + "\n");

		String majornum[] = new String[2];
		majornum[0] = "Major Number";
		majornum[1] = new Integer(major).toString();
		classdetails.add(majornum);

		details.append("Minor Number:\t\t\t" + minor + "\n");

		String minornum[] = new String[2];
		minornum[0] = "Minor Number";
		minornum[1] = new Integer(minor).toString();
		classdetails.add(minornum);
		details.append("Class Name:\t\t\t" + thisClass + "\n");

		String name[] = new String[2];
		name[0] = "This Class";
		name[1] = thisClass;
		classdetails.add(name);
		details.append("Super Class:\t\t\t" + parent + "\n");

		String supername[] = new String[2];
		supername[0] = "Super Class";
		supername[1] = parent;
		classdetails.add(supername);
		details.append("Package:\t\t\t\t" + pkg + "\n");

		String pckg[] = new String[2];
		pckg[0] = "Package";
		pckg[1] = pkg;
		classdetails.add(pckg);
		details.append("Method Count:\t\t\t" + metCount + "\n");

		String mc[] = new String[2];
		mc[0] = "Method Count";
		mc[1] = new Integer(metCount).toString();
		classdetails.add(mc);

		details.append("Field Count:\t\t\t" + fCount + "\n");

		String fc[] = new String[2];
		fc[0] = "Field Count";
		fc[1] = new Integer(fCount).toString();
		classdetails.add(fc);
		details.append("Interfaces:\t\t\t" + intCount + "\n");

		String I[] = new String[2];
		I[0] = "Interfaces";
		I[1] = new Integer(intCount).toString();
		classdetails.add(I);

		details.append("Constructors:\t\t\t" + constCount + "\n");

		String c[] = new String[2];
		c[0] = "Constructors";
		c[1] = new Integer(constCount).toString();
		classdetails.add(c);
		details.append("Pool Size:\t\t\t" + ConsoleLauncher.getPoolCount()
				+ "\n" + "\n");

		String pc[] = new String[2];
		pc[0] = "Pool Count";
		pc[1] = new Integer(ConsoleLauncher.getPoolCount()).toString();
		classdetails.add(pc);
		return classdetails;

	}

	class ShowTime extends Thread {
		public void run() {
			while (Manager.getManager().showTime) {
				Date d = new Date(System.currentTimeMillis());
				java.lang.String curTime = d.toString();
				java.lang.String title = "Welcome To Jdec Decompiler        Current time    \" "
						+ curTime + " \"";
				UILauncher.getMainFrame().setTitle(title);
				try {
					Thread.sleep(1000); // Sleep Needed to show correctly in the
										// UI
				} catch (InterruptedException ie) {
				}
			}
		}

	}

	class UnShowTime extends Thread {
		public void run() {
			java.lang.String title = "Welcome To Jdec Decompiler";
			UILauncher.getMainFrame().setTitle(title);

		}

	}

	private void saveAsWork() {
		Manager manager = Manager.getManager();
		ArrayList paneList = manager.getCurrentSplitPaneComponents();
		JTabbedPane tabs = UIUtil.getUIUtil().getRightTabbedPane();

		if (tabs != null) {
			// tabs.setSelectedIndex(tabs.indexOfTab("Jdec Editor Window"));
			Component editor = tabs.getSelectedComponent();
			// System.out.println(editor.getClass());
			JScrollPane editorTab = (JScrollPane) editor;
			JFileChooser chooser = new JFileChooser();
			JavaFileFilter filter = new JavaFileFilter();
			chooser.setFileFilter(filter);
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

		} catch (InterruptedException e) {
			LogWriter l = LogWriter.getInstance();
			e.printStackTrace(l.getPrintWriter());
		}

	}

	private static ActionItemListener listener = null;

	private static Object lock;
	static {
		lock = new Object();
	}

	public static ActionItemListener getListener() {
		if (listener != null) {
			return listener;
		} else {
			synchronized (lock) {
				listener = new ActionItemListener();
				return listener;
			}
		}

	}

	private ActionItemListener() {
	}

	ExtraOptions filterframe = null;

	java.lang.String browserPath = "";

	class askbrowser extends JFrame {

		askbrowser() {
			super("Browser Path");

			final JTextField text = new JTextField(20);
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout());
			panel.add(text);

			JButton sel = new JButton("Select");
			sel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser chooser = new JFileChooser(".");
					int option = chooser.showOpenDialog(UILauncher
							.getMainFrame());
					if (option == JFileChooser.APPROVE_OPTION) {

						File name = chooser.getSelectedFile();
						if (name != null) {
							browserPath = name.getAbsolutePath();
							text.setText(browserPath);
							java.lang.String cmd = browserPath;
							cmd += " file:///" + System.getProperty("user.dir")
									+ File.separator + "doc/Documentaion.html";
							Runtime rt = Runtime.getRuntime();
							try {
								rt.exec(cmd);
								UILauncher.getUIutil().setBrowserPath(
										new File(browserPath));
								UIConfig config = UILauncher.getUIConfigRef();
								config.addPref("browserPath", browserPath);
							} catch (Exception exp2) {
								JOptionPane.showMessageDialog(null,
										"Could Not show in browser");
							}
						}
					}

					setVisible(false);
				}

			});
			panel.add(sel);
			JButton button;
			getContentPane().setLayout(new FlowLayout());
			panel.setVisible(true);
			getContentPane().add(panel);
			getContentPane().add(button = new JButton("Apply"));
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

				}
			});
			setBounds(300, 300, 300, 100);
			setResizable(true);
			setVisible(true);
		}

	}
}
