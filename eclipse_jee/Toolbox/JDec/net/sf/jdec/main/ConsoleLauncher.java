/*
 *  ConsoleLauncher.java Copyright (c) 2006,07 Swaroop Belur
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

package net.sf.jdec.main;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.sf.jdec.commonutil.Objects;
import net.sf.jdec.commonutil.StartupHelper;
import net.sf.jdec.config.Configuration;
import net.sf.jdec.constantpool.CPool;
import net.sf.jdec.constantpool.ClassDescription;
import net.sf.jdec.core.Decompiler;
import net.sf.jdec.core.Disassembler;
import net.sf.jdec.core.InnerClassTracker;
import net.sf.jdec.core.JvmUtil;
import net.sf.jdec.core.LocalVariable;
import net.sf.jdec.core.LocalVariableHelper;
import net.sf.jdec.core.LocalVariableStructure;
import net.sf.jdec.core.LocalVariableTable;
import net.sf.jdec.core.InnerClassTracker.Node;
import net.sf.jdec.exceptions.ApplicationException;
import net.sf.jdec.exceptions.InvalidInputException;
import net.sf.jdec.exceptions.MalFormedClassException;
import net.sf.jdec.io.Reader;
import net.sf.jdec.io.Writer;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.reflection.ConstructorMember;
import net.sf.jdec.reflection.FieldMember;
import net.sf.jdec.reflection.JavaClass;
import net.sf.jdec.ui.core.JdecTree;
import net.sf.jdec.ui.core.Manager;
import net.sf.jdec.ui.core.UIObserver;
import net.sf.jdec.ui.main.UILauncher;
import net.sf.jdec.ui.util.UIUtil;
import net.sf.jdec.util.AllExceptionHandler;
import net.sf.jdec.util.AnnotationHelper;
import net.sf.jdec.util.ClassStructure;
import net.sf.jdec.util.Constants;
import net.sf.jdec.util.Util;

// import org.bb.projects.jdec.ui.core.JarTree;

/**
 * @author sbelur
 */
public class ConsoleLauncher {

	private static boolean supportedVersion = true;

	private static JavaClass clazzRef;

	private static int magicNumber = -1;

	static ArrayList tabChange = new ArrayList ();

	private static ArrayList inclList = new ArrayList ();

	private static ArrayList exclList = new ArrayList ();

	public ArrayList variablesPushedToFront = null;

	public static boolean fatalErrorOccured = false;

	public static ArrayList currentClassMethods = null;

	public static ArrayList innerClassStructures = null;

	private static boolean dontpvtmethods = false;

	private static boolean dontstaticinit = false;

	private static boolean dontsynth = false;

	private static boolean dontshowemptyconst = false;

	private static boolean dontshownative = false;

	private static boolean dontshowabs = false;

	private static boolean showconstfirst = true;

	private static boolean showfieldsfirst = true;

	private static boolean showextendsobject = false;

	public static ArrayList allClassStructures = new ArrayList ();

	public static boolean currentIsInner = false;

	public static HashMap method_names_signature = new HashMap ();

	public static HashMap classMethodMap = new HashMap ();

	public static HashMap classMethodRefMap = new HashMap ();

	public static HashMap method_names_methodref = new HashMap ();

	private static java.lang.String resultFileName = null;

	public static String currentdecompiledtext = "";

	private static InnerClassTracker.Node mainRoot = null;

	private static InnerClassTracker.Node currentNode = null;

	public static long l3 = -1;

	private static int currentDepthLevel = 0;

	public static ClassStructure mainClassStructure = null;

	private static float version = -1;

	public static void setResultFilePath (String res) {
		resultFileName = res;

	}

	public static long getStartTime () {
		return startTime;
	}

	public static long startTime;

	static java.lang.String mesg = "";

	/**
	 * @param args
	 */

	public static void main (String[] args) {

		startTime = System.currentTimeMillis ();

		try {

			/*
			 * if(args==null || args.length==0) showHelp();
			 */

			// For help ...No further processing
			if (args != null && (args.length > 0)) {
				if ((args[0].equalsIgnoreCase ("help") || args[0].equalsIgnoreCase ("/?") || args[0].equalsIgnoreCase ("-help"))) {
					showHelp ();
				}
			}
			Configuration.init (new String[] {"config", "consolefilter"});

			new StartupHelper ().start ();

			// mesg="Parameters from Configuration.properties registered...\n";

			mesg += Configuration.userInput ();
			Util.registerInputs (args);
			mesg += "Input Parameter(s) to Jdec registered ....\n";
			mesg += "User has Chosen to Run Jdec with option " + Configuration.getDecompileroption () + "\n";
			mesg += "Jdec will verify user specified input parameters...\n";
			System.out.println (mesg);

			verifyConfigProperties ();

			String path = "";
			/*
			 * if(Configuration.wasJarSpecified() &&
			 * Configuration.wasSingleClassSpecified()) { mesg+="Jdec was
			 * Provided options jar as well as input\n"; mesg+="Jdec will now
			 * OVERRIDE jar option and will proceed to decompile the input
			 * .class File specified \""+Configuration.getJavaClassFile()+"\"
			 * ...\n\n"; if(Configuration.getDecompileroption().equals("jar")) {
			 * Configuration.setDecompileroption("dc"); }
			 * path=Configuration.getJavaClassFile(); }
			 */
			if (Configuration.wasJarSpecified () && Configuration.wasSingleClassSpecified () && Configuration.getDecompileroption ().equals ("jar")) {
				mesg += "Jdec was Run with options jar\n";
				path = Configuration.getJarPath ();

			}
			if (Configuration.wasJarSpecified () && Configuration.wasSingleClassSpecified () && !Configuration.getDecompileroption ().equals ("jar")) {

				path = Configuration.getJavaClassFile ();

			}

			if (Configuration.wasJarSpecified () && Configuration.wasSingleClassSpecified () == false) {
				mesg += "Jdec was Run with options jar\n";
				path = Configuration.getJarPath ();

			}
			if (!Configuration.wasJarSpecified () && Configuration.wasSingleClassSpecified ()) {

				path = Configuration.getJavaClassFile ();

			}

			// path=Util.getClassPath();
			if (path.equalsIgnoreCase ("***Not-Initialized***")) {
				java.lang.String message = "";
				message = "Check whether the Default entry(***Not-Initialized***) has been changed or not...";
				AllExceptionHandler handler = new AllExceptionHandler (new InvalidInputException (message));
				handler.reportException ();
				System.out.println ("[ERROR] Please check the input settings to Jdec again");
				System.out.println ("Please check the log output for more details");
				System.exit (1);
			}

			// path=checkFilePath(path);
			if (!validInput (path)) {
				String message = "[ERROR :]\tClass file/Jar File not reachable....\n";
				message += "Check whether the File Exists \n";
				AllExceptionHandler handler = new AllExceptionHandler (message);
				handler.sendMessage ();
				System.out.println ("Input Path specified by user :" + path + "\n");
				System.out.println ("[ERROR] Please check the input settings to Jdec again");
				System.out.println ("Please check the log output for more details");
				System.exit (1);
			}
			else {
				// Register ClassPath in Configuration Class For Retrieveing
				// Later...
				Configuration.setClassFilePath (path);
				String tooloption = Configuration.getDecompileroption ();
				// System.out.println(tooloption+" tooloption");
				if (tooloption.equals ("jar")) {
					Util.explodeJar (new File (Configuration.getJarPath ()));
					decompileJarFromConsole (Configuration.getJarPath (), Util.getRegisteredClasses ());
				}
				else if (tooloption.equals ("llv")) {
					showLocalVariables (path);
				}
				else if (tooloption.equals ("dc")) {
					couldNotFinish = new ArrayList ();
					addLicence = true;
					try {
						decompileClass (path);
					}
					catch (Exception exp) {
						AllExceptionHandler h1 = new AllExceptionHandler (exp);
						h1.reportException ();
						System.out.println ("Exception occured while decompiling class");
						System.out.println ("Please check the log output for more details");
						System.exit (1);
					}

					currentDepthLevel = 0;
					setResultFilePath (Configuration.getOutputFolderPath () + File.separator + getClassNameWithExtension ());
					decompileInnerClasses (); // TODO Also Fix For The UI side
					// also later
					// Now need to concatenate inner class content here
					Configuration.setClassFilePath (mainRoot.getClassPath ());
					java.lang.String importedclassesInInnerclasses = "";

					// if(!Configuration.getInlineInnerClassMethodContent().equalsIgnoreCase("true"))
					// {
					if (innerClassesDecompied != null && innerClassesDecompied.size () != 0) {
						java.lang.String innerClassFileContents = "";
						for (int s = 0; s < innerClassesDecompied.size (); s++) {
							java.lang.String classfilepath = (java.lang.String) innerClassesDecompied.get (s);
							try {
								java.lang.String currentFileContent = "";
								java.lang.String cname = getClassName (classfilepath);
								if (cname.indexOf (".") != -1) {
									cname = cname.substring (0, cname.indexOf ("."));
									cname = cname + "." + Configuration.getFileExtension ();
									classfilepath = Configuration.getOutputFolderPath () + File.separator + cname;
								}
								else {
									cname = cname + "." + Configuration.getFileExtension ();
									classfilepath = Configuration.getOutputFolderPath () + File.separator + cname;
								}
								File temp = new File (classfilepath);
								FileInputStream fis = new FileInputStream (temp);
								BufferedInputStream bis = new BufferedInputStream (fis);
								BufferedReader br = new BufferedReader (new InputStreamReader (bis));
								java.lang.String line = null;
								while ((line = br.readLine ()) != null) {
									currentFileContent += line + "\n";
								}
								if (currentFileContent.trim ().length () != 0) {
									// TODO: Remove Imports and store elsewhere
									int importend = currentFileContent.indexOf ("// End of Import");
									importedclassesInInnerclasses = "";
									if (importend != -1) {
										java.lang.String p = currentFileContent.substring (0, importend);
										currentFileContent = currentFileContent.substring (importend + "// End of Import".length ());
										importedclassesInInnerclasses += p;
									}
									innerClassFileContents += currentFileContent;
									innerClassFileContents += "\n\n//End Of a Inner Class File Content...\n\n";
								}

								temp = null;
								fis = null;
								bis = null;
								br = null;
								System.gc ();

							}
							catch (IOException ioe) {

							}
						}
						if (innerClassFileContents.trim ().length () != 0) {
							innerClassFileContents = checkForLicenceAndPackageInInnerClassContent (innerClassFileContents);
							java.lang.String mainFileContent = "";
							java.lang.String cname = getClassName (mainRoot.getClassPath ());
							java.lang.String mainfilepath = "";
							if (cname.indexOf (".") != -1) {
								cname = cname.substring (0, cname.indexOf ("."));
								cname = cname + "." + Configuration.getFileExtension ();
								mainfilepath = Configuration.getOutputFolderPath () + File.separator + cname;
							}
							else {
								cname = cname + "." + Configuration.getFileExtension ();
								mainfilepath = Configuration.getOutputFolderPath () + File.separator + cname;
							}
							File mainfile = new File (mainfilepath);
							FileInputStream fis = new FileInputStream (mainfile);
							BufferedInputStream bis = new BufferedInputStream (fis);
							BufferedReader br = new BufferedReader (new InputStreamReader (bis));
							java.lang.String line = null;
							while ((line = br.readLine ()) != null) {
								mainFileContent += line + "\n";
							}

							java.lang.String completeContent = mainFileContent;
							completeContent += "\n\n//Beginning of Inner Class Content...\n\n ";
							completeContent += innerClassFileContents;

							// Write back to Main File
							if (mainfile.exists ()) {
								mainfile.delete ();
							}

							mainfile = new File (mainfilepath);
							mainfile.createNewFile ();
							try {
								FileOutputStream fos = new FileOutputStream (mainfile);
								BufferedWriter bw = new BufferedWriter (new OutputStreamWriter (fos));
								importedclassesInInnerclasses = importedclassesInInnerclasses.replaceAll (
										"/\\*\\*\\*\\* List of All Imported Classes \\*\\*\\*/", "");
								importedclassesInInnerclasses = importedclassesInInnerclasses.replaceAll ("\n\n", "\n");
								completeContent = completeContent.replaceAll ("/\\*\\*\\*\\* List of All Imported Classes \\*\\*\\*/", "");
								Iterator classNames = getClazzObjects ().keySet ().iterator ();
								java.lang.String classname = "";
								while (classNames.hasNext ()) {
									classname = (java.lang.String) classNames.next ();
									break;
								}

								/*
								 * if(classname.indexOf(".")!=-1) {
								 * java.lang.String
								 * pkg=classname.substring(0,classname.lastIndexOf("."));
								 * //bw.write("package "+pkg+";\n\n"); }
								 */
								completeContent = completeContent.replaceAll ("this\\$", "This\\$");
								bw.write (completeContent);
								// bw.write("/**** List of All Imported Classes
								// ***/");
								completeContent = completeContent.replaceAll ("\n\n", "\n");
								if (importedclassesInInnerclasses.endsWith ("\n")) {
									int n = importedclassesInInnerclasses.lastIndexOf ("\n");
									if (n != -1) {
										importedclassesInInnerclasses = importedclassesInInnerclasses.substring (0, n);
									}

								}
								// bw.write(importedclassesInInnerclasses);
								completeContent = checkForImportDuplicates (importedclassesInInnerclasses, completeContent);

								int present = completeContent.indexOf (localvariablenote);

								completeContent = completeContent.replaceAll (localVarNote, "");

								bw.flush ();
								bw.close ();
							}
							catch (IOException ioe) {

							}

						}

					}
					// }
					/*
					 * else { }
					 */
					// Restore orig classpath when over
				}
				else if (tooloption.equals ("vcp")) {

					constantPool (path);

				}
				else if (tooloption.equals ("help")) {
					showHelp ();
				}
				else if (tooloption.equals ("nocode")) {
					showSkeletonClass (path);

				}
				else if (tooloption.equals ("dis")) {
					disassemble (path);
				}
				else {
					AllExceptionHandler handler = new AllExceptionHandler (
							"Invalid Option passed to jdec...\nPlease set jdec_option as help and execute ....");
					handler.sendMessage ();
					System.exit (1);
				}

			}

		}

		catch (IOException ioe) {
			AllExceptionHandler handler = new AllExceptionHandler (ioe);
			handler.reportException ();

		}

		catch (InvalidInputException IIE) {
			try {

				System.out.println ("Invalid Options provided to Jdec....");
				System.out.println ("Jdec will now terminate....\n");
				System.out
						.println ("NOTE: If no HELP message is visible here , Please check the log files Or \n Change the Log_Mode to console....\n");
				Util.showUsage ();
			}
			catch (IOException iousage) {

			}

		}
		finally {
			closeLogWriter ();
			long endTime = System.currentTimeMillis ();
			double duration = (double) (endTime - startTime) / 1000;
			System.out.println ("\n\nJdec took " + duration + " seconds to complete...");
		}

	}

	private static void closeLogWriter () {
		Writer writer = null;
		try {
			writer = Writer.getWriter ("log");
			if (writer != null) {
				java.util.Date d = new java.util.Date ();
				writer.writeLog ("\n\nCurrent Run Of Jdec Terminated At Time " + d.toString ());
				// Let These System.outs be . SO The user will come to know when
				// the
				// jdec has terminated and can check the log files and out
				// put files
				System.out.println ("Decompiler was Run With option " + Configuration.getDecompileroption ());
				if (Configuration.getLogMode ().equalsIgnoreCase ("file")) {
					System.out.println ("Please Check The Log Files For Details");
					// System.out.println("Please Check The Output Files For
					// Details");
				}
				else if (Configuration.getLogMode ().equalsIgnoreCase ("console")) {
					System.out.println ("Please Check The Log Output in the console For Further Details");
					// System.out.println("Please Check the Decompiler output in
					// the console ");
				}
				if (Configuration.getOutputMode ().equalsIgnoreCase ("file")) {
					System.out.println ("Please Check The Decompiled Output in Output Folder");
					// System.out.println("Please Check The Output Files For
					// Details");
				}
				else if (Configuration.getOutputMode ().equalsIgnoreCase ("console")) {
					System.out.println ("Please Check The Decompiled  Output in the console ");
					// System.out.println("Please Check the Decompiler output in
					// the console ");
				}
				System.out.println ("Current Run Of Jdec Terminated At Time " + d.toString ());
				writer.flush ();
			}
		}
		catch (IOException e) {
			// Leave Blank. Cannot do anything This is the place where logwriter
			// is closed
		}
		finally {
			if (writer != null) {
				writer.close ("log");
			}
		}

	}

	private static java.lang.String showConstantPool (java.lang.String path) {
		java.lang.String cpooldesc = "";
		try {
			InputStream readerStream = null;
			try {
				readerStream = VerifyClassFile (path);
			}
			catch (MalFormedClassException mfe) {
				AllExceptionHandler handler = new AllExceptionHandler (mfe);
				handler.reportException ();
				JFrame mainFrame = UILauncher.getMainFrame ();
				if (mainFrame != null) {
					Manager.getManager ().setShowProgressBar (false);
					java.lang.String msg = "Invalid Class Specified As Input To Jdec..\n";
					msg += mfe.getMessage () + "\n";
					JOptionPane.showMessageDialog (UILauncher.getMainFrame (), msg, "Run Jdec Decompiler", JOptionPane.ERROR_MESSAGE);
					throw new Exception ();
				}
				throw new Exception ();

			}
			cpooldesc = processOnlyConstantPool (readerStream);

		}
		catch (Exception mfe) {
			AllExceptionHandler handler = new AllExceptionHandler (mfe);
			handler.reportException ();
			JFrame mainFrame = UILauncher.getMainFrame ();
			if (mainFrame != null) {
				Manager.getManager ().setShowProgressBar (false);
				try {
					UIManager.setLookAndFeel (UILauncher.getUIutil ().getCurrentLNF ());
					SwingUtilities.updateComponentTreeUI (UILauncher.getMainFrame ());
				}
				catch (Exception e) {

				}
				return null;
			}
			else {
				System.out.println ("Jdec encountered an FatalException. Jdec will now exit..");
				System.exit (1);
			}

		}
		return cpooldesc;
	}

	public static boolean validInput (String path) {

		File file = new File (path);
		if (file.exists ())
			return true;
		else
			return false;

	}

	public static void showLocalVariables (String path) {

		try {
			InputStream readerStream = null;
			try {
				readerStream = VerifyClassFile (path);
			}
			catch (MalFormedClassException mfe) {
				AllExceptionHandler handler = new AllExceptionHandler (mfe);
				handler.reportException ();
				JFrame mainFrame = UILauncher.getMainFrame ();
				if (mainFrame != null) {
					Manager.getManager ().setShowProgressBar (false);
					java.lang.String msg = "Invalid Class Specified As Input To Jdec..\n";
					msg += mfe.getMessage () + "\n";
					JOptionPane.showMessageDialog (UILauncher.getMainFrame (), msg, "Run Jdec Decompiler", JOptionPane.ERROR_MESSAGE);
					try {
						UIManager.setLookAndFeel (UILauncher.getUIutil ().getCurrentLNF ());
						SwingUtilities.updateComponentTreeUI (UILauncher.getMainFrame ());
					}
					catch (Exception e) {

					}
					throw new Exception ();
				}
				else {
					System.out.println ("Jdec encountered an FatalException. Jdec will now exit..");
					System.exit (1);
				}
			}
			processClassFile (readerStream);
			displayLocalVariables ();

		}
		catch (Exception mfe) {

		}

	}

	public static java.lang.String decompileClass (String path) throws Exception {
		UIUtil.codeReformatted = false;
		allImportedClasses = new ArrayList ();
		java.lang.String classDesc = "";
		boolean notinner = false;
		try {
			InputStream readerStream = VerifyClassFile (path);
			ClassDescription cd = processClassFile (readerStream);

			// Skip Decompiling This Class ???
			/**
			 * This is applicable for jdecoption : jar This was introduced to
			 * support filters from UI
			 */

			if (Configuration.getDecompileroption ().equals ("jar") || Configuration.getDecompileroption ().equals ("decompileJar")) {
				Iterator classNames = getClazzObjects ().keySet ().iterator (); // TODO:Modify
				// this
				// later
				// No
				// need
				// of
				// iterator
				// Here
				java.lang.String cname = (java.lang.String) classNames.next ();
				java.lang.String pkg = "";
				if (cname != null && cname.indexOf (".") != -1) {

					pkg = cname.substring (0, cname.lastIndexOf ("."));
					boolean skip = skipClazzDecompilation (pkg);
					if (skip) return "";
				}
			}

			// NOTE by belurs:
			/*******************************************************************
			 * The Folllowing 3 stmts are used to create InnerClassTracker for
			 * The current Main Class being Decompiled i.e The class file
			 * specified in config.properties File This tracker will be used for
			 * the current class and any inner class contained in this class or
			 * any inner class reachable from this class
			 * 
			 * For Jar File , it refers to the current File being decompiled in
			 * the jar
			 */
			tracker = new InnerClassTracker (cd);
			InnerClassTracker.Node node = tracker.createNode (path, getClassName (path));
			boolean b = cd.isClassCompiledWithMinusG ();
			node.setCompiledWithMinusG (b);
			if (mainRoot == null) {
				InnerClassTracker.rootnodes = new ArrayList ();
			}
			tracker.addRootNode (node);
			Configuration.setPathForCurrentClassFile (path);
			if (mainRoot == null) {

				mainRoot = node;
				notinner = true;
			}
			else {
				notinner = false;
				currentNode = node;
			}
			currentRootAdded = node;
			currentClassMethods = new ArrayList ();
			innerClassStructures = new ArrayList ();
			formCodeStatements (cd);
			java.lang.String pth = getPackagePathFromRootForThisClass ();

			File op = new File (Configuration.getBkpoppath () + File.separator + pth);
			if (op.exists () == false) op.mkdirs ();
			Configuration.setOutputFolderPath (op.getAbsolutePath ());
			classDesc = displayClass (true, "dc", cd.getClassName ());
			classDesc = classDesc.replaceAll ("this\\$", "This\\$");
			classDesc = classDesc.replaceAll ("this$", "This$");
			node.setHasBeenDecompiled (true);

		}
		catch (MalFormedClassException mfe) {
			currentdecompiledtext = "";
			AllExceptionHandler handler = new AllExceptionHandler (mfe);
			handler.reportException ();
			JFrame mainFrame = UILauncher.getMainFrame ();
			if (mainFrame != null) {

				if (Configuration.getDecompileroption ().equals ("jar") == false
						&& Configuration.getDecompileroption ().equals ("decompileJar") == false) {
					Manager.getManager ().setShowProgressBar (false);
					java.lang.String msg = "Unsupported Class Specified As Input To Jdec..\n";
					msg += mfe.getMessage () + "\n";
					JOptionPane.showMessageDialog (UILauncher.getMainFrame (), msg, "Run Jdec Decompiler", JOptionPane.ERROR_MESSAGE);
					try {
						UIManager.setLookAndFeel (UILauncher.getUIutil ().getCurrentLNF ());
						SwingUtilities.updateComponentTreeUI (UILauncher.getMainFrame ());
					}
					catch (Exception e) {

					}
					throw new RuntimeException ("Unsupported Class Version found..Current task will now stop");
				}
				if (Configuration.getDecompileroption ().equals ("jar") || Configuration.getDecompileroption ().equals ("decompileJar")) { throw mfe; }
			}
			else {
				System.out.println ("Jdec encountered an FatalException. jdec will now exit..");
				System.exit (1);
			}
		}
		catch (Exception exp) {
			currentdecompiledtext = "";
			AllExceptionHandler handler = new AllExceptionHandler (exp);
			handler.reportException ();
			JFrame mainFrame = UILauncher.getMainFrame ();
			if (mainFrame != null) {

				if (Configuration.getDecompileroption ().equals ("jar") == false
						&& Configuration.getDecompileroption ().equals ("decompileJar") == false) {
					Manager.getManager ().setShowProgressBar (false);
					throw exp;
				}
				if (Configuration.getDecompileroption ().equals ("jar") || Configuration.getDecompileroption ().equals ("decompileJar")) { throw exp; }

			}
			else {
				System.out.println ("Jdec encountered an FatalException. Jdec will now exit..");
				System.exit (1);
			}

		}

		if (notinner) {
			ClassStructure mainCS = new ClassStructure ();
			mainClassStructure = mainCS;
			mainCS.setName (mainRoot.getClassName ());
			mainCS.setMethods (currentClassMethods);
			boolean add = addClassStructure (mainRoot.getClassName ());
			if (add) allClassStructures.add (mainCS);
		}
		else {
			ClassStructure aCS = new ClassStructure ();
			aCS.setName (currentNode.getClassName ());
			aCS.setMethods (currentClassMethods);
			boolean add = addClassStructure (currentNode.getClassName ());
			if (add) allClassStructures.add (aCS);
		}

		currentdecompiledtext = classDesc;
		return classDesc;
	}

	private static void formCodeStatements (ClassDescription cd) {
		Hashtable ht = getClazzObjects ();
		Iterator it = ht.keySet ().iterator ();
		while (it.hasNext ()) {
			String classname = (java.lang.String) it.next ();
			JavaClass clazz = (JavaClass) ht.get (classname);
			// Disassemble Each Method
			/**
			 * 
			 * Basically The following 2 loops will just store the disassembled
			 * code in a manner so that API external to reflection API Can acess
			 * that behaviour and show its disassembled code...
			 * 
			 * 
			 */

			ArrayList cons = clazz.getConstructors ();
			Iterator consIt = cons.iterator ();
			if (cons.size () > 8) Configuration.setLogLevel ("2");
			while (consIt.hasNext ()) {
				l3 = System.currentTimeMillis ();
				Behaviour b = (Behaviour) consIt.next ();
				boolean skip = skipBehaviour (b);
				if (!b.isHasBeenDissassembled () && !skip) {
					b.setHasBeenDissassembled (true);
					Decompiler decompiler = new Decompiler (b, cd);
					if (b.getCode () != null) {

						if (UIUtil.getUIUtil () != null) {
							JDialog pf = UIUtil.getProgressBarFrame ();
							if (pf != null) {
								pf.setTitle ("In Progress...");
								UIUtil.proframedetails_class.setText ("");
								UIUtil.proframedetails.revalidate ();
								UIUtil.proframedetails.repaint ();
								UIUtil.proframedetails_pkg.setText ("Package :" + clazz.getPackageName ());
								UIUtil.proframedetails_class.setText ("Current Class :" + clazz.getSimpleName ());
								UIUtil.proframedetails_method.setText ("Current Method :" + b.getBehaviourName ());
								UIUtil.proframedetails_pkg.setFont (new Font ("Monospaced", Font.BOLD, 12));
								UIUtil.proframedetails_pkg.setForeground (Color.BLUE);
								UIUtil.proframedetails_class.setFont (new Font ("Monospaced", Font.BOLD, 12));
								UIUtil.proframedetails_class.setForeground (Color.BLUE);
								UIUtil.proframedetails_method.setFont (new Font ("Monospaced", Font.BOLD, 12));
								UIUtil.proframedetails_method.setForeground (Color.BLUE);
								UIUtil.proframedetails.revalidate ();
								UIUtil.proframedetails.repaint ();

								// UIUtil.getProgressBarFrame().repaint();
							}
						}
						// ADD
						String bname = b.getBehaviourName ();
						String mname = bname.concat (b.getUserFriendlyMethodParams ());
						currentClassMethods.add (mname);
						decompiler.decompileCode ();
					}
					else {
						// ADD
						String bname = b.getBehaviourName ();
						String mname = bname.concat (b.getUserFriendlyMethodParams ());
						currentClassMethods.add (mname);
						b.replaceBuffer ("");
					}
					decompiler = null;
				}

			}

			ArrayList met = clazz.getDecalaredMethods ();
			if (met.size () > 5) Configuration.setLogLevel ("2");
			Iterator metIt = met.iterator ();
			while (metIt.hasNext ()) {
				l3 = System.currentTimeMillis ();
				Behaviour b = (Behaviour) metIt.next ();
				boolean skip = skipBehaviour (b);
				if (!b.isHasBeenDissassembled () && !skip) {
					b.setHasBeenDissassembled (true);
					Decompiler decompiler = new Decompiler (b, cd);
					if (UIUtil.getUIUtil () != null) {
						JDialog pf = UIUtil.getProgressBarFrame ();
						if (pf != null) {
							pf.setTitle ("In Progress...");
							UIUtil.proframedetails_class.setText ("");
							UIUtil.proframedetails.revalidate ();
							UIUtil.proframedetails.repaint ();
							UIUtil.proframedetails_pkg.setText ("Package :" + clazz.getPackageName ());
							UIUtil.proframedetails_class.setText ("Current Class :" + clazz.getSimpleName ());
							UIUtil.proframedetails_pkg.setFont (new Font ("Monospaced", Font.BOLD, 12));
							UIUtil.proframedetails_pkg.setForeground (Color.BLUE);
							UIUtil.proframedetails_method.setText ("Current Method :" + b.getBehaviourName ());
							UIUtil.proframedetails_class.setFont (new Font ("Monospaced", Font.BOLD, 12));
							UIUtil.proframedetails_class.setForeground (Color.BLUE);
							UIUtil.proframedetails_method.setFont (new Font ("Monospaced", Font.BOLD, 12));
							UIUtil.proframedetails_method.setForeground (Color.BLUE);
							UIUtil.proframedetails.revalidate ();
							UIUtil.proframedetails.repaint ();

						}
					}
					if (b.getCode () != null) {
						// ADD
						String bname = b.getBehaviourName ();
						if (bname.equals ("this")) {
							bname = "jdecThis/**[ Originally this(method name) ]**/ ";
						}
						String mname = bname.concat (b.getUserFriendlyMethodParams ());
						currentClassMethods.add (mname);

						decompiler.decompileCode ();
					}
					else {
						String bname = b.getBehaviourName ();
						if (bname.equals ("this")) {
							bname = "jdecThis/**[ Originally this(method name) ]**/ ";
						}
						String mname = bname.concat (b.getUserFriendlyMethodParams ());
						currentClassMethods.add (mname);
						b.replaceBuffer ("");
					}
					decompiler = null;
				}
			}

			// TODO
			// Print The Class Along with disassembled code
			// for each method in that Class.

		}

	}

	public static String checkFilePath (String path) {

		if (path == null) return null;
		char oldChar = '@';
		char newChar = '@';
		int unixPath = path.indexOf ("/");
		int winPath = path.indexOf ("\\");
		if (unixPath != -1) {
			if (unixPath != File.separatorChar) {
				oldChar = '/';
				newChar = '\\';
			}
		}
		else {
			if (winPath != -1 && winPath != File.separatorChar) {
				oldChar = '\\';
				newChar = '/';
			}
		}
		if (oldChar != '@' || newChar != '@') path = path.replace (oldChar, newChar);

		return path;

	}

	private static InputStream VerifyClassFile (String path) throws MalFormedClassException {
		Reader reader = Reader.createStreamReader (path);
		InputStream readerStream = reader.getStreamReader ();

		boolean ok = checkMagic (readerStream, path);
		if (ok == true) {
			ok = checkVersion (readerStream);
			if (!ok) { throw new MalFormedClassException ("Invalid Version Number for class file " + getVesion ()); }
			magicNumber = Constants.CLASS_MAGIC_NUMBER;
		}
		else {
			throw new MalFormedClassException ("Invalid Magic Number For Class File...." + geterrormagicnumber () + "\n");
		}

		return readerStream;

	}

	public static java.lang.String geterrormagicnumber () {
		return errormagicnumber;
	}

	private static java.lang.String errormagicnumber = "-1";

	private static boolean checkMagic (InputStream is, String path) {
		int magic = -1;
		try {
			boolean validFile = false;
			DataInputStream dis = null;

			if (is instanceof DataInputStream) {
				dis = (DataInputStream) is;

			}
			else // Should Ideally Not Happen As we are passing a DIS...
			{
				dis = new DataInputStream (new BufferedInputStream (is));
			}

			// Read Magic Number

			magic = dis.readInt ();
			// LOG the Magic Number of the Class...
			Writer logwriter = Writer.getWriter ("log");
			String classname = getClassName (path);
			logwriter
					.writeLog ("[INFO]Magic Number of the Class \"" + classname + "\" Is " + new String (Integer.toHexString (magic)).toUpperCase ());
			logwriter.writeLog ("\n");
			logwriter.flush ();

		}

		catch (IOException ioe) {
			AllExceptionHandler handler = new AllExceptionHandler (ioe);
			handler.reportException ();
		}
		// Check Here
		errormagicnumber = new String (Integer.toHexString (magic)).toUpperCase ();
		if (magic == Constants.CLASS_MAGIC_NUMBER) {
			return true;
		}
		else
			return false;

	}

	public static float getVesion () {
		return version;
	}

	private static boolean checkVersion (InputStream is) {

		int minor_version = -1;
		int major_version = -1;
		float class_file_format_version = -1;
		try {
			DataInputStream dis = null;
			if (is instanceof DataInputStream) {
				dis = (DataInputStream) is;
			}
			else // Should Ideally Not Happen As we are passing a DIS...
			{
				dis = new DataInputStream (new BufferedInputStream (is));
			}

			minor_version = dis.readUnsignedShort ();
			major_version = dis.readUnsignedShort ();

			class_file_format_version = major_version + (minor_version / 100);
			Writer logwriter = Writer.getWriter ("log");
			logwriter.writeLog ("[INFO]class Version is " + class_file_format_version + "\n\n");
			logwriter.flush ();

		}
		catch (IOException ioe) {
			AllExceptionHandler handler = new AllExceptionHandler (ioe);
			handler.reportException ();

		}
		supportedVersion = true;
		if (class_file_format_version >= 45.0f && class_file_format_version <= 49.0f) {
			setClassMajorVersionNumber (major_version);
			setClassMinorVersionNumber (minor_version);
			return true;
		}
		else {
			boolean versionCheck;
			if (UILauncher.getMainFrame () != null) {
				versionCheck = UIUtil.getUIUtil ().getSkipClassVersionCheck ().equalsIgnoreCase ("true");
			}
			else {
				versionCheck = Configuration.getSkipClassVersionCheck ().equalsIgnoreCase ("true");
			}
			if (versionCheck) {
				setClassMajorVersionNumber (major_version);
				setClassMinorVersionNumber (minor_version);
				supportedVersion = false;
				try {
					Writer writer = Writer.getWriter ("log");
					writer.writeLog ("Class File Version Number: " + class_file_format_version
							+ " is not supported officially\nHowever Jdec will try to decompile the class now.\n");
					writer.writeLog ("To disable such class file decompilation , please specify false for skip_class_version_check in \t\tconfig.properties");
				}
				catch (IOException ex) {

				}
				return true;
			}
			else {
				version = class_file_format_version;
				return false;
			}
		}

	}

	private static void setClassMajorVersionNumber (int n) {
		majorversion = n;
	}

	private static void setClassMinorVersionNumber (int n) {
		minorversion = n;
	}

	private static int majorversion = -1;

	private static int minorversion = -1;

	public static int getMajorVersion () {
		return majorversion;
	}

	public static int getMinorVersion () {
		return minorversion;
	}

	public static String getClassName (String path) {
		String slash = "";
		if (path.indexOf ("\\") != -1) {
			slash = "\\";
		}
		else {
			slash = "/";
		}
		int lastSlash = path.lastIndexOf (slash);
		if (lastSlash != -1) {
			classDir = path.substring (0, lastSlash);
		}
		return path.substring (lastSlash + 1);

	}

	public static String getClassDir () {
		return classDir;
	}

	private static java.lang.String classDir = "";

	private static ClassDescription processClassFile (InputStream is) {

		initializeJvmInstructionMap ();
		ClassDescription cd = new ClassDescription ();
		try {
			Writer logr = Writer.getWriter ("log");
			DataInputStream dis = null;
			if (is instanceof DataInputStream) {
				dis = (DataInputStream) is;
			}
			else // Should Ideally Not Happen As we are passing a DIS...
			{
				dis = new DataInputStream (new BufferedInputStream (is));
			}
			JDialog proframe = UIUtil.getProgressBarFrame ();
			if (proframe != null) {
				proframe.setTitle ("Creating Constant Pool ...");
			}
			CPool.createConstantPool (dis);
			logr.writeLog ("\nConstant Pool Created ", Configuration.getLogLevel ());

			if (proframe != null) {
				proframe.setTitle ("Constant Pool Created ");
			}

			CPool cpoolobj = new CPool ();
			cd.setConstantPool (cpoolobj); // Not Needed actually.No use of
			// setting also since all members
			// are static : belurs
			cd.readAccessSpecifiers (dis);
			logr.writeLog ("\nAccess specifiers for class registered ", Configuration.getLogLevel ());
			if (proframe != null) {
				proframe.setTitle ("Access specifiers for class registered ");
			}
			cd.readClassAndSuperClassInfo (dis);
			logr.writeLog ("\nThis class and Super class Names Regsitered", Configuration.getLogLevel ());
			cd.readInterfaceInformation (dis);
			logr.writeLog ("\nInterfaces for this class registered", Configuration.getLogLevel ());
			cd.readFieldInformation (dis);
			logr.writeLog ("\nField Information for this class registered", Configuration.getLogLevel ());
			if (proframe != null) {
				proframe.setTitle ("Field Information for this class registered ");
			}
			cd.readMethodInformation (dis);
			clazzObjects = new Hashtable ();
			int attCount = dis.readUnsignedShort ();
			cd.readAttributesInfo (attCount, dis);
			logr.writeLog ("\nMethod Information for this class registered ", Configuration.getLogLevel ());
			if (proframe != null) {
				proframe.setTitle ("Method Information for this class registered ");
			}

			logr.writeLog ("\nCreating Reflection Details For This Class... ", Configuration.getLogLevel ());
			cd.populateReflection (clazzObjects);
			logr.writeLog ("\nReflection Details For This Class Registered... ", Configuration.getLogLevel ());
			if (proframe != null) {
				proframe.setTitle ("Reflection Access Details Created... ");
			}

		}
		catch (IOException ioe) {
			AllExceptionHandler handler = new AllExceptionHandler (ioe);
			handler.reportException ();
			return null;
		}
		catch (Throwable t) {
			t.printStackTrace ();
			AllExceptionHandler handler = new AllExceptionHandler (t);
			handler.reportException ();
			return null;
		}
		return cd;
	}

	private static java.lang.String processOnlyConstantPool (InputStream is) {
		try {
			DataInputStream dis = null;
			if (is instanceof DataInputStream) {
				dis = (DataInputStream) is;
			}
			else // Should Ideally Not Happen As we are passing a DIS...
			{
				dis = new DataInputStream (new BufferedInputStream (is));
			}

			CPool.createConstantPool (dis);

		}
		catch (IOException ioe) {
			AllExceptionHandler handler = new AllExceptionHandler (ioe);
			handler.reportException ();
			JFrame mainFrame = UILauncher.getMainFrame ();
			if (mainFrame == null) {
				System.exit (1);
			}
		}
		return CPool.returnConstantPoolDesc ();
	}

	/**
	 * This method is an exception to the case of writing log statements It does
	 * not use the writer objects in this project... It directly used System.out
	 * Stream to output help statements... This is so that The user can
	 * immediately see the mistake in the console....
	 */

	public static void showHelp () {
		try {

			System.out.println ("******* Welcome to Jdec Decompiler help option *******");
			System.out.println ("\nThis tool is meant to convert a .class file to a resultant output file");
			System.out.println ("...containing the decompiled java code...");
			System.out.println ("Following are the options Supported For This Release...");
			System.out.println ("1>\tDecompile Class File...option is dc");
			System.out.println ("2>\tDecompile Jar File...option is jar");
			System.out.println ("3>\tDisplay Constant Pool contents...option is vcp");
			System.out.println ("4>\tDisplay Disassembled output...option is dis");
			System.out.println ("5>\tDisplay Skeleton Class...option is nocode");
			System.out.println ("6>\tView Help again...option is help");
			System.out.println ("\n\n");
			System.out.println ("NOTE: It is not necessary to specify any argument while running Jdec");
			System.out.println ("Just Ensure the config.properties File has Values set properly");
			System.out.println ("Also you can update the consolefilter.properties to chnage file filte level settings for jar decompilation");
			System.out
					.println ("\nHowever the user should remember that by specifying any parameter and value \nfrom Command Prompt Or By specifying arguments to the main class in the .bat file\nthose values  will then override any value in Configuration.properties File");
			Thread.sleep (5000);
			System.exit (0);
		}
		catch (Exception e) {
			// Do Nothing
		}

	}

	public static void showSkeletonClass (String path) {
		try {
			InputStream readerStream = VerifyClassFile (path);
			ClassDescription cd = processClassFile (readerStream);
			displayClass (false, null, cd.getClassName ());

		}
		catch (MalFormedClassException mfe) {
			AllExceptionHandler handler = new AllExceptionHandler (mfe);
			handler.reportException ();
			JFrame mainFrame = UILauncher.getMainFrame ();
			if (mainFrame != null) {
				Manager.getManager ().setShowProgressBar (false);
				java.lang.String msg = "Invalid Class Specified As Input To Jdec..\n";
				msg += mfe.getMessage () + "\n";
				JOptionPane.showMessageDialog (UILauncher.getMainFrame (), msg, "Run Jdec Decompiler", JOptionPane.ERROR_MESSAGE);
				try {
					UIManager.setLookAndFeel (UILauncher.getUIutil ().getCurrentLNF ());
					SwingUtilities.updateComponentTreeUI (UILauncher.getMainFrame ());
				}
				catch (Exception e) {

				}
			}
			else {
				System.out.println ("Jdec encountered an FatalException. Jdec will now exit..");
				System.exit (1);
			}
		}
	}

	/**
	 * @param showMethodCode
	 *        boolean variable to indicate whether Code has to be displayed
	 *        or not for this method...
	 *        <p/>
	 *        Usage : true: In Case of disassemble option or decompile option false: In case of dis_nocode option
	 * @param typeofcodedisplay
	 *        Indicates the manner in which code has to be displayed Usage
	 *        dis: In case of disassemble option dc : In case of decompile
	 *        option null: In case of dis_nocode option
	 *        <p/>
	 *        Example Full usage: displayClass(false,null) for dis_nocode option.
	 */
	static java.lang.String classDescdup = "";

	private static java.lang.String displayClass (boolean showMethodCode, java.lang.String typeofcodedisplay, String classname) {
		StringBuffer temp = new StringBuffer ();
		java.lang.String classDescCopy = "";
		java.lang.String importedFiles = "";
		classDescdup = "";
		java.lang.String licenceWarning = getLicenceWarning ();
		if (addLicence) temp.append (licenceWarning);
		Iterator classNames = getClazzObjects ().keySet ().iterator (); // TODO:Modify
		// this
		// later
		// No
		// need
		// of
		// iterator
		// Here
		java.lang.String cname = (java.lang.String) classNames.next ();
		java.lang.String pkg = "";
		if (cname != null && cname.indexOf (".") != -1) {

			pkg = classname.substring (0, classname.lastIndexOf ("."));
			temp.append ("package " + pkg + ";\n \n");
		}

		if (allImportedClasses != null && allImportedClasses.size () > 0) {
			importedFiles = showImportedClasses (allImportedClasses, classname);
			temp.append (importedFiles);
		}
		temp.append ("#INTERFACE_IMPORT_PLACEHOLDER");
		temp.append ("#ABSTRACT_METHOD_IMPORT_PLACEHOLDER");
		temp.append ("\n// End of Import\n\n");
		classNames = getClazzObjects ().keySet ().iterator ();
		temp.append (AnnotationHelper.toString (getClazzRef ().getRuntimeVisibleAnnotations ()));
		temp.append (AnnotationHelper.toString (getClazzRef ().getRuntimeInvisibleAnnotations ()));
		while (classNames.hasNext ()) {
			java.lang.String name = (java.lang.String) classNames.next ();

			JavaClass cl = (JavaClass) getClazzObjects ().get (name);
			String accesstypes = cl.getUserFriendlyAccessSpecifiers ();
			if (accesstypes.indexOf ("interface") != -1) cl.setInterface (true);

			/*
			 * if (!supportedVersion) { String versionStmt = "\n/***\n\tClass
			 * Version > 48.0.\n "; versionStmt += "\tCurrent supported version
			 * is upto 48.0. By default jdec tries to decompile such
			 * classes.\n"; versionStmt += "\tTo turn off support for such
			 * classes please set false to Skip_Class_Version_Check in
			 * config.properties\n "; temp.append(versionStmt + "\n"); }
			 */
			boolean isAnnotaion = false;
			for (int i = 0; i < cl.getInterfacesImplemented ().size (); i++) {
				String str = cl.getInterfacesImplemented ().get (i).toString ();
				if (str.equals ("java.lang.annotation.Annotation")) {
					isAnnotaion = true;
				}
			}

			if (accesstypes != null) temp.append (accesstypes);
			if (accesstypes.indexOf ("interface") == -1 && !isAnnotaion) temp.append ("class");
			if (isAnnotaion) {
				temp.append ("@interface");
			}

			String signature = getClazzRef ().getSignature ();
			String genericPart = "";
			if (signature != null) {
				if (signature.indexOf ("<") != -1 && signature.lastIndexOf (">") != -1) {
					getClazzRef ().toGenericStringRepresentaion ();
				}
			}

			StringBuffer st = new StringBuffer ("");
			Util.checkForImport (name, st, true);
			temp.append ("  " + st.toString ());
			temp.append (getClazzRef ().getClassgenericPart () + " ");
			boolean genericAdded = false;
			st = new StringBuffer ("");
			String supergeneric = getClazzRef ().getAnyGenericPartForSuperType (cl.getQualifiedSuperclassname ());
			Util.checkForImport (cl.getSuperclassname (), st);
			if (!cl.getSuperclassname ().equals ("I am not initialized!!!") && accesstypes.indexOf ("interface") == -1) {

				if (getClazzRef ().getQualifiedSuperClassName ().indexOf ("java.lang.Object") != -1) {
					if (getShowextendsobject ()) {
						temp.append ("extends  " + st.toString () + supergeneric);
						genericAdded = true;
					}
				}
				else {
					temp.append ("extends  " + st.toString () + supergeneric);
					genericAdded = true;
				}
			}
			StringBuffer interfaceImports = new StringBuffer ("");
			if (cl.getInterfacesImplemented ().size () > 0) {
				if (accesstypes.indexOf ("interface") == -1) {
					if (isAnnotaion && cl.getInterfacesImplemented ().size () == 1) {
						// ignore
					}
					else {
						temp.append ("  implements  ");
					}
				}
				else
					temp.append ("  extends  ");
				for (int i = 0; i < cl.getInterfacesImplemented ().size (); i++) {
					st = new StringBuffer ("");
					String str = cl.getInterfacesImplemented ().get (i).toString ();
					if (str.equals ("java.lang.annotation.Annotation")) {
						continue;
					}
					supergeneric = getClazzRef ().getAnyGenericPartForSuperType (str);
					if (importedFiles.indexOf (str) == -1) {
						java.lang.String s = Util.checkForImport (str, st);
						// if(!allImportedClasses.contains(s))
						interfaceImports.append ("import " + s + ";\n");
						temp.append (st.toString () + supergeneric);
						if (i == 0 && cl.getInterfacesImplemented ().size () > 1) temp.append (" , ");
						if (i > 0 && i != cl.getInterfacesImplemented ().size () - 1) temp.append (" , ");
					}
					else {
						java.lang.String s = Util.checkForImport (str, st);
						temp.append (st.toString () + supergeneric);
						if (i == 0 && cl.getInterfacesImplemented ().size () > 1) temp.append (" , ");
						if (i > 0 && i != cl.getInterfacesImplemented ().size () - 1) temp.append (" , ");
					}
				}
			}
			java.lang.String contentStr = temp.toString ().replaceAll ("#INTERFACE_IMPORT_PLACEHOLDER", interfaceImports.toString ());
			temp = new StringBuffer (contentStr + "\n");

			temp.append ("\n{\n");
			// System.out.println(temp.toString());
			ArrayList allfields = cl.getDecalaredFields ();
			StringBuffer fieldDesc = new StringBuffer ();
			if (allfields != null && allfields.size () > 0) fieldDesc = new StringBuffer (" /***\n **Class Fields\n ***/\n");
			collectFeildDetails (allfields, fieldDesc);
			String newLine = "";
			if (fieldDesc.toString ().trim ().length () != 0) newLine = "\n";
			java.lang.String classDesc = temp.toString ();
			if (getShowfieldsfirst ()) classDesc = classDesc.concat (newLine).concat (fieldDesc.toString ());
			// System.out.println(classDesc.toString());
			StringBuffer methodDesc = new StringBuffer ();
			ArrayList allMethods = cl.getALLMethods ();
			classDescdup = classDesc;
			StringBuffer newimports = new StringBuffer ("");
			try {
				collectMethodDetails (allMethods, methodDesc, showMethodCode, typeofcodedisplay, cl, importedFiles, newimports, isAnnotaion);
			}
			catch (ApplicationException e) {
				AllExceptionHandler handler = new AllExceptionHandler (e);
				handler.reportException ();

			}
			classDesc = classDesc.toString ().concat (methodDesc.toString ());
			classDesc = classDesc.toString ().replaceFirst ("#ABSTRACT_METHOD_IMPORT_PLACEHOLDER", newimports.toString ());
			if (!getShowfieldsfirst ()) {
				if (allfields != null && allfields.size () > 0)
					classDesc = classDesc.toString ().concat (newLine + "\n").concat (fieldDesc.toString ());
				else
					classDesc = classDesc.toString ();
			}

			classDesc = classDesc.toString ().concat ("\n}");

			classDescCopy = classDesc;
			// System.out.println(classDesc.toString());

			// Send to output
			Writer outputwriter = null;
			try {

				outputwriter = Writer.getWriter ("output");
				// Why is some part commented?
				/*
				 * if(classDesc.indexOf("JdecGenerated")!=-1)// &&
				 * Configuration.getDecompileroption().equals("dc")) {
				 * java.lang.String note="/****\nNOTE: THe decompiled
				 * statmements contain some Temporary Variables. \n(Beginning
				 * with JdecGenerated) Jdec Generates These while" ; note+="
				 * \nprocessing blocks like NEW. However the actual variable in
				 * the code\n then gets assigned to the respective temporary
				 * variable.***\n"; localvariablenote=note;
				 * classDesc+="\n\n\n"+note; }
				 */

				if (getDummyVarNoteStatus ()) {
					java.lang.String note = "/*****\nNOTE: THe decompiled statmements contain some local variables with $ as part of the name...";
					note += " \nThis means that those variables are not used in the code at all...That is there is no access to the variable\n";
					note += " Either by means of getting the variable content or by means of storing them....";
					note += "\n\n[JUST AS A POINT OF INTEREST TO THE USER, Jdec SETS THE POSITION IN THE CODE \nWHERE THE LOCAL VARIABLE IS FIRST ";
					note += "ENCOUNTERED AS PART OF THE VARIABLE NAME\n AFTER THE FIRST DASH .]\n****/\n";
					classDesc += "\n\n\n" + note;
				}
				outputwriter.writeOutput (classDesc);
				outputwriter.flush ();
			}
			catch (IOException ioe) {
				AllExceptionHandler handler = new AllExceptionHandler (ioe);
				handler.reportException ();
			}

			finally {

				// if(Configuration.getDecompileroption().equals("jar"))
				// {
				outputwriter.close ("output");
				// }

			}

		}
		return classDescCopy;
	}

	public static Hashtable getClazzObjects () {
		return clazzObjects;
	}

	public static void setClazzObjects (Hashtable clazzObjects) {
		ConsoleLauncher.clazzObjects = clazzObjects;
	}

	private static void collectFeildDetails (ArrayList allfields, StringBuffer temp) {
		Iterator it = allfields.iterator ();

		while (it.hasNext ()) {
			FieldMember f = (FieldMember) it.next ();
			int br = f.getUnParsedDataType ().indexOf ("[");
			boolean addBrackets = false;
			if (br == -1 && f.getDimension () > 0) {
				addBrackets = true;
			}
			String fieldType = f.getDataType ();
			String genericPartOfField = "";
			if (f.getGenericSignature () != null && f.getGenericSignature ().trim ().length () > 0) {
				genericPartOfField = f.getGenericPartOfSignature (f.getGenericSignature ());
			}
			String brk = "";
			if (addBrackets) {
				for (int v = 1; v <= f.getDimension (); v++) {
					brk += "[]";
				}
				fieldType = fieldType + " " + brk;
			}

			StringBuffer bf = new StringBuffer ("");
			Util.checkForImport (fieldType, bf);
			fieldType = bf.toString ();
			if (fieldType.indexOf ("/") != -1) {
				fieldType = fieldType.replaceAll ("/", ".");
			}
			if (fieldType.indexOf ("[") != -1) {
				int firstBkt = fieldType.indexOf ("[");
				int lastBkt = fieldType.lastIndexOf ("]");
				String brkts = fieldType.substring (firstBkt, lastBkt + 1);
				fieldType = fieldType.substring (0, firstBkt);
				fieldType = fieldType.trim ();
				fieldType += genericPartOfField;
				fieldType += brkts;

			}
			else {
				fieldType += genericPartOfField;
			}
			java.lang.String fieldContent = ((f.getUserFriendlyAccessSpecifiers ().trim ().length () == 0) ? "" : Util.formatFieldsOrMethodHeader (
					f.getUserFriendlyAccessSpecifiers (), "field"))
					+ fieldType + " " + f.getName ();
			fieldContent = fieldContent.replaceAll ("this", "This");
			// fieldContent = fieldContent.replaceAll("this\\$", "This\\$");
			// fieldContent = fieldContent.replaceAll("this$", "This$");
			fieldContent = Util.formatFieldsOrMethodHeader (fieldContent, "field");
			temp.append (AnnotationHelper.toString (f.getRuntimeVisibleAnnotations ()));
			temp.append (AnnotationHelper.toString (f.getRuntimeInvisibleAnnotations ()));
			temp.append (fieldContent);
			Object value = f.getFieldValue ();
			java.lang.String fieldDesc = "";// (value!=null)?"
			// ="+value.toString()+";":";";
			if (value != null) {
				fieldDesc = "=" + value.toString ();
				if (fieldType != null && fieldType.trim ().equals ("long")) {
					fieldDesc += "L" + ";";
				}
				else if (fieldType != null && fieldType.trim ().equals ("float")) {
					fieldDesc += "f" + ";";
				}
				else if (fieldType != null && fieldType.trim ().equals ("double")) {
					fieldDesc += "D" + ";";
				}
				else {
					fieldDesc += ";";
				}
			}
			else {
				fieldDesc = ";";
			}
			fieldDesc = Util.formatFieldsOrMethodHeader (fieldDesc, "field");
			temp.append (fieldDesc);
			temp.append ("\n");
			// System.out.println(temp.toString());
		}
		temp.append ("\n\n");
	}

	private static Hashtable clazzObjects = new Hashtable ();

	private static void collectMethodDetails (ArrayList allMethods, StringBuffer methodDesc, boolean showCode, java.lang.String typeofdisplay,
			JavaClass cl, String importfiles, StringBuffer newimports, boolean isAnnotaion) throws ApplicationException {

		method_names_signature = new HashMap ();

		method_names_methodref = new HashMap ();
		// CASE 1: option is dis_nocode:
		if (showCode == false) {
			Iterator allMethodsIt = allMethods.iterator ();
			// methodDesc.append("\n\t");
			while (allMethodsIt.hasNext ()) {

				Behaviour method = (Behaviour) allMethodsIt.next ();
				StringBuffer t = new StringBuffer ("");
				Util.checkForImport (method.getReturnType (), t);
				String bname = method.getBehaviourName ();
				boolean thisfound = false;
				if (bname.equals ("this")) {
					bname = "jdecThis";
					thisfound = true;
				}
				java.lang.String methodAccessor = method.getUserFriendlyMethodAccessors () + " " + t.toString () + " " + bname;
				if (isAnnotaion) {
					methodAccessor = t.toString () + " " + bname;
				}
				if (method.getReturnType ().trim ().length () == 0) {
					if (method.getUserFriendlyMethodAccessors ().trim ().length () == 0) {
						methodAccessor = method.getBehaviourName ();
					}
				}
				if (method.getReturnType ().trim ().length () != 0) {
					if (method.getUserFriendlyMethodAccessors ().trim ().length () == 0) {
						// methodAccessor=method.getReturnType()+"
						// "+method.getBehaviourName();
						java.lang.String rettype = method.getReturnType ();
						int pos = rettype.lastIndexOf (".");
						if (pos == -1) pos = rettype.lastIndexOf ("/");
						if (pos != -1) {
							java.lang.String simpleName = rettype.substring (pos + 1);
							addImportClass (rettype.replace ('/', '.'));
							rettype = simpleName;

						}
						methodAccessor = rettype + " " + method.getBehaviourName ();
					}
				}
				methodAccessor = Util.formatFieldsOrMethodHeader (methodAccessor, "method");
				methodDesc.append (AnnotationHelper.toString (method.getRuntimeVisibleAnnotations ()));
				methodDesc.append (AnnotationHelper.toString (method.getRuntimeInvisibleAnnotations ()));
				methodDesc.append (methodAccessor);

				if (method.getBehaviourName ().trim ().equals ("static") == false) {

					methodDesc.append (getAllMethodParams (method.getUserFriendlyMethodParams (), method, cl, importfiles, newimports));
					java.lang.String[] exceptionTypesDecalred = method.getExceptionTypes ();
					for (int count = 0; count < exceptionTypesDecalred.length; count++) {
						if (count == 0) methodDesc.append (" \t" + Util.formatFieldsOrMethodHeader ("\n throws ", "method"));
						methodDesc.append (exceptionTypesDecalred[count].replace ('/', '.'));
						if (count != exceptionTypesDecalred.length - 1) methodDesc.append ("  ,");
					}
					if (exceptionTypesDecalred.length > 0) {
						// methodDesc.append("\n");
					}
				}
				java.lang.String accs = method.getUserFriendlyMethodAccessors ();
				if (accs.indexOf ("native") >= 0 || accs.indexOf ("abstract") >= 0) {
					methodDesc.append (";\n\n");
				}
				else {

					methodDesc.append (Util.formatFieldsOrMethodHeader ("\n{\n", "method"));
					methodDesc.append ("\n");
					methodDesc.append (Util.formatFieldsOrMethodHeader ("}\n\n", "method"));

				}

				// System.out.println(methodDesc.toString());

			}
		}
		// CASE 2 and 3
		else {
			// No need to test for showCode = true
			if (typeofdisplay.equals ("dis")) {

				Iterator allMethodsIt = allMethods.iterator ();
				// methodDesc.append("\n\t");
				while (allMethodsIt.hasNext ()) {

					Behaviour method = (Behaviour) allMethodsIt.next ();
					boolean skip = skipBehaviour (method);
					if (skip) continue;
					StringBuffer t = new StringBuffer ("");
					Util.checkForImport (method.getReturnType (), t);
					java.lang.String methodAccessor = method.getUserFriendlyMethodAccessors () + " " + t.toString () + " "
							+ method.getBehaviourName ();
					if (method.getReturnType ().trim ().length () == 0) {
						if (method.getUserFriendlyMethodAccessors ().trim ().length () == 0) {
							methodAccessor = method.getBehaviourName ();
						}
					}
					if (method.getReturnType ().trim ().length () != 0) {
						if (method.getUserFriendlyMethodAccessors ().trim ().length () == 0) {
							// methodAccessor=method.getReturnType()+"
							// "+method.getBehaviourName();
							java.lang.String rettype = method.getReturnType ();
							int pos = rettype.lastIndexOf (".");
							if (pos == -1) pos = rettype.lastIndexOf ("/");
							if (pos != -1) {
								java.lang.String simpleName = rettype.substring (pos + 1);
								addImportClass (rettype.replace ('/', '.'));
								rettype = simpleName;

							}
							methodAccessor = rettype + " " + method.getBehaviourName ();
						}
					}
					methodAccessor = Util.formatFieldsOrMethodHeader (methodAccessor, "method");
					methodDesc.append (methodAccessor);
					if (method.getBehaviourName ().trim ().equals ("static") == false) {
						methodDesc.append (getAllMethodParams (method.getUserFriendlyMethodParams (), method, cl, importfiles, newimports));
						java.lang.String[] exceptionTypesDecalred = method.getExceptionTypes ();
						for (int count = 0; count < exceptionTypesDecalred.length; count++) {
							if (count == 0) methodDesc.append (" \t" + Util.formatFieldsOrMethodHeader ("\n throws ", "method"));
							methodDesc.append (exceptionTypesDecalred[count].replace ('/', '.'));
							if (count != exceptionTypesDecalred.length - 1) methodDesc.append ("  ,");
						}
						if (exceptionTypesDecalred.length > 0) {
							// methodDesc.append("\n");
						}
					}
					java.lang.String accs = method.getUserFriendlyMethodAccessors ();
					if (accs.indexOf ("native") >= 0 || accs.indexOf ("abstract") >= 0) {
						methodDesc.append (";\n\n");
					}
					else {

						methodDesc.append (Util.formatFieldsOrMethodHeader ("\n{\n", "method"));
						boolean skipCode = didThisMethodFail (method);
						if (!skipCode) {
							// BigInteger bh;
							methodDesc.append (method.getVMInstructions ());
							methodDesc.append ("\n");
							methodDesc.append (Util.formatFieldsOrMethodHeader ("}\n\n", "method"));
							methodDesc.append (getAnyExceptionTableDetails (method));

							methodDesc.append ("\n\n-----------------------------------------------");
							// methodDesc.append("-----------------------------------------------");
							// methodDesc.append("-----------------------------------------------");
							methodDesc.append ("-----------------------------------------------\n\n");
						}
						else {
							java.lang.String message = "\n/***Could Not disassemble This Method.\n";
							message += "A Runtime Exception Occured while decompiling This method....\n";
							message += "Please Check up the log message for this method and \n";
							message += "send The following details to project admin at its home page..\n ";
							message += "    1>Log File\n";
							message += "     2>Class File\n";
							message += "     3>Java Version***/\n";
							methodDesc.append (Util.formatDecompiledStatement (message) + "\n");
							methodDesc.append (Util.formatFieldsOrMethodHeader ("}\n\n", "method"));
						}
					}

					// System.out.println(methodDesc.toString());

				}

			}
			else if (typeofdisplay.equals ("dc")) {

				Iterator allMethodsIt = allMethods.iterator ();
				// methodDesc.append("\n\t");
				while (allMethodsIt.hasNext ()) {

					Behaviour method = (Behaviour) allMethodsIt.next ();
					// System.out.println(method.getBehaviourName());
					String completedesc = "";
					boolean skip = skipBehaviour (method);
					if (skip) continue;
					String genericRetTypePart = method.getGenericPartForReturnType ();
					StringBuffer t = new StringBuffer ("");
					boolean replace = false;
					if (method.getReturnType () != null && method.getReturnType ().indexOf ("java.lang.Object") != -1) {
						if (genericRetTypePart != null && genericRetTypePart.startsWith ("<T")) {
							replace = true;
						}
					}
					Util.checkForImport (method.getReturnType (), t);
					if (replace) {
						if (t.toString ().indexOf ("java.lang.Object") != -1)
							t = new StringBuffer ("<T> " + t.toString ().replaceAll ("java.lang.Object", "T"));
						else
							t = new StringBuffer ("<T> " + t.toString ().replaceAll ("Object", "T"));

					}

					StringBuffer t2 = new StringBuffer ("");
					Util.checkForImport (method.getBehaviourName (), t2);
					boolean thisfound = false;
					if (t2.toString ().equals ("this")) {
						t2 = new StringBuffer ("jdecThis");
						thisfound = true;
					}
					String ret_type = "";
					if (replace)
						ret_type = t.toString ();
					else {
						ret_type = t.toString () + genericRetTypePart;
						if (genericRetTypePart.length () > 0) if (genericRetTypePart.indexOf ("<") == -1) {
							ret_type = genericRetTypePart;
						}
					}
					if (ret_type.equals ("V")) {
						// ret_type = "void";
						if (method instanceof ConstructorMember) {
							ret_type = "";
						}
						else {
							if (t.toString ().equals ("void")) {
								ret_type = "void";
							}
						}
					}

					java.lang.String methodAccessor = method.getUserFriendlyMethodAccessors () + " " + method.getReturnTypeMarker () + " " + ret_type
							+ " " + t2.toString ();// method.getBehaviourName();
					if (isAnnotaion) {
						methodAccessor = t.toString () + " " + t2.toString ();
					}
					if (method.getReturnType ().trim ().length () == 0) {
						if (method.getUserFriendlyMethodAccessors ().trim ().length () == 0) {
							String bname = method.getBehaviourName ();
							if (bname.equals ("this")) {
								bname = "jdecThis";
								thisfound = true;
							}
							methodAccessor = bname;
						}
					}
					if (method.getReturnType ().trim ().length () != 0) {
						if (method.getUserFriendlyMethodAccessors ().trim ().length () == 0) {
							// methodAccessor=method.getReturnType()+"
							// "+method.getBehaviourName();
							java.lang.String rettype = method.getReturnType ();
							int pos = rettype.lastIndexOf (".");
							if (pos == -1) pos = rettype.lastIndexOf ("/");
							if (pos != -1) {
								java.lang.String simpleName = rettype.substring (pos + 1);
								addImportClass (rettype.replace ('/', '.'));
								rettype = simpleName;

							}
							String bname = method.getBehaviourName ();
							if (bname.equals ("this")) {
								bname = "jdecThis";
								thisfound = true;
							}
							if (genericRetTypePart.equals ("V")) {
								// ret_type = "void";
								if (method instanceof ConstructorMember) {
									genericRetTypePart = "";
								}
								else {
									if (t.toString ().equals ("void")) {
										genericRetTypePart = "void";
									}
								}
							}
							if (genericRetTypePart.indexOf ("<") != -1)
								methodAccessor = method.getReturnTypeMarker () + " " + rettype + genericRetTypePart + " " + bname;
							else {
								if (genericRetTypePart.length () == 0) {
									genericRetTypePart = rettype;
								}
								methodAccessor = method.getReturnTypeMarker () + " " + genericRetTypePart + " " + bname;
							}
						}
					}
					methodDesc.append (AnnotationHelper.toString (method.getRuntimeVisibleAnnotations ()));
					methodDesc.append (AnnotationHelper.toString (method.getRuntimeInvisibleAnnotations ()));
					methodAccessor = Util.formatFieldsOrMethodHeader (methodAccessor, "method");

					// ClassStructure
					// cst=InnerClassTracker.getClassStructure(cl.getClassName()+".class",allClassStructures);

					String identifierComment = "";
					// "//"+
					// " DO NOT REMOVE COMMENT\n";

					identifierComment += "//  CLASS: " + cl.getClassName () + ":" + "\n";
					methodDesc.append (identifierComment);
					completedesc += identifierComment;
					completedesc += methodAccessor;
					if (completedesc.endsWith ("jdecThis")) {
						completedesc += "/**[ Originally this(method name) ]**/ ";
					}
					if (thisfound) {
						methodAccessor = methodAccessor + "/**[ Originally this(method name) ]**/ ";
					}
					methodDesc.append (methodAccessor);
					if (method.getBehaviourName ().trim ().equals ("static") == true) {
						methodDesc.append ("      //[Static Initializer]");
					}

					if (method.getBehaviourName ().trim ().equals ("static") == false) {
						String mp = getAllMethodParams (method.getUserFriendlyMethodParams (), method, cl, importfiles, newimports);
						methodDesc.append (mp);
						completedesc += mp;
						java.lang.String[] exceptionTypesDecalred = method.getExceptionTypes ();
						for (int count = 0; count < exceptionTypesDecalred.length; count++) {
							if (count == 0) methodDesc.append (" \t" + Util.formatFieldsOrMethodHeader ("\n throws ", "method"));
							methodDesc.append (exceptionTypesDecalred[count].replace ('/', '.'));
							if (count != exceptionTypesDecalred.length - 1) methodDesc.append ("  ,");
						}
						if (exceptionTypesDecalred.length > 0) {
							// methodDesc.append("\n");
						}
					}
					java.lang.String accs = method.getUserFriendlyMethodAccessors ();
					// Map names with complete Signature
					// [Static Initializer]
					String bname = method.getBehaviourName ();
					if (bname.equals ("this")) {
						bname = "jdecThis/**[ Originally this(method name) ]**/ ";
					}

					String mn = bname.concat (method.getUserFriendlyMethodParams ());

					if (method.getBehaviourName ().equals ("static")) {
						completedesc += "      //[Static Initializer]";
					}
					if (method.getBehaviourName ().equals ("this")) {

						bname = "jdecThis/**[ Originally this(method name) ]**/ ";
					}
					if (completedesc.length () > 0) {
						completedesc = completedesc.substring (0, completedesc.length () - 1);
					}
					method_names_signature.put (mn, completedesc);
					method_names_methodref.put (mn, method);
					if (accs.indexOf ("native") >= 0 || accs.indexOf ("abstract") >= 0) {
						methodDesc.append (";\n\n");
					}
					else {

						methodDesc.append (Util.formatFieldsOrMethodHeader ("\n{\n", "method"));
						boolean skipCode = didThisMethodFail (method);
						if (!skipCode) {
							// BigInteger bh;
							methodDesc.append (method.getCodeStatements ());
							methodDesc.append ("\n");
							methodDesc.append (Util.formatFieldsOrMethodHeader ("}\n\n", "method"));
						}
						else {
							java.lang.String message = "\n/***Could Not Decompile This Method.\n";
							message += "A Runtime Exception Occured while decompiling This method....\n";
							message += "Please Check up the log message for this method and \n";
							message += "send The following details to project admin at its home page..\n ";
							message += "    1>Log File\n";
							message += "     2>Class File\n";
							message += "     3>Java Version***/\n";
							methodDesc.append (Util.formatDecompiledStatement (message) + "\n");
							methodDesc.append (Util.formatFieldsOrMethodHeader ("}\n\n", "method"));
						}
					}

					// System.out.println(methodDesc.toString());

				}
				String key = cl.getClassName ();
				if (cl.getClassName ().indexOf (".") != -1) {
					int l = cl.getClassName ().lastIndexOf (".");
					key = key.substring (l + 1);
				}
				classMethodMap.put (key + ".class", method_names_signature);
				classMethodRefMap.put (cl.getClassName () + ".class", method_names_methodref);
				// methodDesc=format(methodDesc);
			}
			else // NOTE:
			// Should Never Happen. At least at the time of writing this method
			// when the options are restricted to 3: dis_nocode,dc,dis.
			// Need to have alook in case of adding More options
			{
				java.lang.String errMessage = "Unknown option encountered while adding method description to the Class Content";
				errMessage += "Application ERROR: UNKNOWN INPUT TO CollectMethodDetails method of Class ConsoleLauncher.";
				throw new ApplicationException (errMessage);
			}

		} // End of outer Else

	}// End of Method

	// Have to check whether this code will work even for inner class
	// and its methods.... ?

	public static void disassemble (java.lang.String path) {

		try {
			InputStream readerStream = VerifyClassFile (path);
			ClassDescription cd = processClassFile (readerStream);
			Hashtable ht = getClazzObjects ();
			Iterator it = ht.keySet ().iterator ();
			while (it.hasNext ()) {
				String classname = (java.lang.String) it.next ();
				JavaClass clazz = (JavaClass) ht.get (classname);
				// Disassemble Each Method
				/**
				 * 
				 * Basically The following 2 loops will just store the
				 * disassembled code in a manner so that API external to
				 * reflection API Can acess that behaviour and show its
				 * disassembled code...
				 * 
				 * 
				 */

				ArrayList cons = clazz.getConstructors ();
				Iterator consIt = cons.iterator ();
				while (consIt.hasNext ()) {
					Behaviour b = (Behaviour) consIt.next ();
					if (!b.isHasBeenDissassembled ()) {
						if (UIUtil.getUIUtil () != null) {
							JDialog pf = UIUtil.getProgressBarFrame ();
							if (pf != null) {
								pf.setTitle ("In Progress...");
								UIUtil.proframedetails_class.setText ("");
								UIUtil.proframedetails_pkg.setText ("Package :" + clazz.getPackageName ());
								UIUtil.proframedetails_class.setText ("Current Class :" + clazz.getSimpleName ());
								UIUtil.proframedetails_pkg.setFont (new Font ("Monospaced", Font.BOLD, 12));
								UIUtil.proframedetails_pkg.setForeground (Color.BLUE);
								UIUtil.proframedetails_method.setText ("Method :" + b.getBehaviourName ());
								UIUtil.proframedetails_class.setFont (new Font ("Monospaced", Font.BOLD, 12));
								UIUtil.proframedetails_class.setForeground (Color.BLUE);
								UIUtil.proframedetails_method.setFont (new Font ("Monospaced", Font.BOLD, 12));
								UIUtil.proframedetails_method.setForeground (Color.BLUE);
								UIUtil.proframedetails.revalidate ();
								UIUtil.proframedetails.repaint ();

							}
						}
						b.setHasBeenDissassembled (true);
						Disassembler dis = new Disassembler (b, cd);
						if (b.getCode () != null) {
							dis.disassembleCode ();
						}
						else {
							b.replaceBuffer ("");
						}
						dis = null;
					}

				}

				ArrayList met = clazz.getDecalaredMethods ();
				Iterator metIt = met.iterator ();
				while (metIt.hasNext ()) {
					Behaviour b = (Behaviour) metIt.next ();
					boolean skip = skipBehaviour (b);
					if (!b.isHasBeenDissassembled () && !skip) {
						if (UIUtil.getUIUtil () != null) {
							JDialog pf = UIUtil.getProgressBarFrame ();
							if (pf != null) {
								pf.setTitle ("In Progress...");
								UIUtil.proframedetails_class.setText ("");
								UIUtil.proframedetails_pkg.setText ("Package :" + clazz.getPackageName ());
								UIUtil.proframedetails_class.setText ("Current Class :" + clazz.getSimpleName ());
								UIUtil.proframedetails_pkg.setFont (new Font ("Monospaced", Font.BOLD, 12));
								UIUtil.proframedetails_pkg.setForeground (Color.BLUE);
								UIUtil.proframedetails_method.setText ("Method :" + b.getBehaviourName ());
								UIUtil.proframedetails_class.setFont (new Font ("Monospaced", Font.BOLD, 12));
								UIUtil.proframedetails_class.setForeground (Color.BLUE);
								UIUtil.proframedetails_method.setFont (new Font ("Monospaced", Font.BOLD, 12));
								UIUtil.proframedetails_method.setForeground (Color.BLUE);
								UIUtil.proframedetails.revalidate ();
								UIUtil.proframedetails.repaint ();

							}
						}
						b.setHasBeenDissassembled (true);
						Disassembler dis = new Disassembler (b, cd);
						if (b.getCode () != null) {
							dis.disassembleCode ();
						}
						else {
							b.replaceBuffer ("");
						}

						dis = null;
					}
				}

				// TODO
				// Print The Class Along with disassembled code
				// for each method in that Class.

				displayClass (true, "dis", clazz.getClassName ());

			}
		}
		catch (MalFormedClassException mfe) {
			AllExceptionHandler handler = new AllExceptionHandler (mfe);
			handler.reportException ();
			JFrame mainFrame = UILauncher.getMainFrame ();
			if (mainFrame != null) {
				Manager.getManager ().setShowProgressBar (false);
				java.lang.String msg = "Invalid Class Specified As Input To Jdec..\n";
				msg += mfe.getMessage () + "\n";
				JOptionPane.showMessageDialog (UILauncher.getMainFrame (), msg, "Run Jdec Decompiler", JOptionPane.ERROR_MESSAGE);
				try {
					UIManager.setLookAndFeel (UILauncher.getUIutil ().getCurrentLNF ());
					SwingUtilities.updateComponentTreeUI (UILauncher.getMainFrame ());
				}
				catch (Exception e) {

				}
			}
			else {
				System.out.println ("Jdec encountered an FatalException. Jdec will now exit..");
				System.exit (1);
			}
		}

	}

	private static void displayLocalVariables () {

		LocalVariableTable obj = LocalVariableTable.getInstance ();
		Hashtable ht = obj.getAllLocalVaraibles ();
		Iterator it = ht.keySet ().iterator ();
		StringBuffer localVars = new StringBuffer ();
		while (it.hasNext ()) {
			java.lang.String mDesc = (java.lang.String) it.next ();
			// localVars.append("*********************************\n");
			localVars.append ("Local Variables For Method " + mDesc + "\n");
			localVars.append ("*********************************\n\n\n");
			// localVars.append("Local Variables for "+mDesc);
			LocalVariableStructure structure = (LocalVariableStructure) ht.get (mDesc);
			ArrayList al = structure.getMethodLocalVaribales ();
			// localVars.append("VERIFYING METHOD DESCRIPTOR");
			// localVars.append("Local Variables for
			// "+structure.getMethodDescriptor());
			Iterator allfields = al.iterator ();

			localVars.append ("[DataType\tLocal Variable Name]\n");
			localVars.append ("--------------------------------\n");
			while (allfields.hasNext ()) {
				LocalVariable local = (LocalVariable) allfields.next ();
				localVars.append (local.toString ());
				localVars.append ("\n");

			}
			localVars.append ("\n\n");
		}
		Writer writer = null;
		// Print output
		try {
			writer = Writer.getWriter ("output");
			writer.writeOutput (localVars.toString ());
			writer.flush ();

		}
		catch (IOException ioe) {
			AllExceptionHandler handler = new AllExceptionHandler (ioe);
			handler.reportException ();
		}
		finally {
			if (writer != null) {
				writer.flush (); // Can be Avoided as flush call was made earlier
				// .put it just in case Some Data is still left
				writer.close ("output");
			}
		}

	}

	private static String getAllMethodParams (String params, Behaviour method, JavaClass cl, String importfiles, StringBuffer newimports) {

		// newimports=new StringBuffer("");
		// if(cl.isInterface())return params;

		if (params.equals ("( )") == false) {
			method.parseGenericSigature ();
			String allParams = "";
			StringTokenizer tokenizer = new StringTokenizer (params, ",");
			int localVarindex = 1;

			String[] methodAccessors = method.getMethodAccessors ();

			if (contains (methodAccessors, "static")) {
				localVarindex = 0;
			}

			LocalVariableStructure structure = method.getLocalVariables ();
			ArrayList variables = null;
			if (structure != null) variables = structure.getMethodLocalVaribales ();

			allParams = "(";
			int count = 0;
			// && count<method.getMethodParams().length
			int start = 0;
			if (method.getUserFriendlyMethodAccessors ().indexOf ("static") == -1) start = 1;
			int total = method.getNumberofparamters ();
			int current = 0;
			if (variables != null) {
				LocalVariable sortedVars[] = (LocalVariable[]) variables.toArray (new LocalVariable[variables.size ()]);
				Arrays.sort (sortedVars);
				boolean thisFound = false;
				boolean ignore = false;
				for (int s = 0; s < sortedVars.length; s++) {
					LocalVariable lv = (LocalVariable) sortedVars[s];
					String dt = lv.getDataType ();
					if (lv.getVarName ().equals ("this") && lv.getIndexPos () == 0) {
						thisFound = true;
					}
					String signature = null;
					ignore = false;
					if (lv.getVarName ().equals ("this") && lv.getIndexPos () == 0) {
						ignore = true;
					}
					else
						signature = method.getGenericSignatureForLocalVariable (lv.getIndexPos ());

					String genericPart = "";
					if (signature != null) {
						if (signature.indexOf ("<") != -1 && signature.indexOf (">") != -1) {
							genericPart = LocalVariableHelper.getGenericPartOfSignature (signature);
						}
						else {
							if (signature.indexOf ("[") != -1) {
								genericPart = LocalVariableHelper.getGenericPartOfSignature (signature.substring (signature.lastIndexOf ("[") + 1));
							}
							else
								genericPart = LocalVariableHelper.getGenericPartOfSignature (signature);
						}
					}
					else {
						if (!ignore) {
							if (thisFound) {
								genericPart = method.getGenericPartForVariablePos (s - 1);
							}
							else {
								genericPart = method.getGenericPartForVariablePos (s);
							}
						}

					}
					if (dt.indexOf (".") == -1 && dt.indexOf ("/") != -1) {
						dt = dt.replaceAll ("/", ".");
					}
					if (genericPart.indexOf ("<") != -1) {
						String brks = "";
						int startBrk = -1;
						if ((startBrk = dt.indexOf ("[")) != -1) {
							brks = dt.substring (startBrk, dt.lastIndexOf ("]") + 1);
						}
						if (startBrk == -1)
							dt = dt + genericPart;
						else {
							dt = dt.substring (0, startBrk).trim () + genericPart + brks;
						}

					}
					else if (genericPart.length () > 0) {
						int startBrk = 0;
						String brks = "";
						if ((startBrk = dt.indexOf ("[")) != -1) {
							brks = dt.substring (startBrk, dt.lastIndexOf ("]") + 1);
						}
						dt = genericPart + brks;

					}
					else
						dt = dt + genericPart;

					String name = lv.getVarName ();
					if (name.equalsIgnoreCase ("this")) continue;
					int index = lv.getIndexPos ();

					if (isCurrentClassCompiledWithDebugInfo ()) {
						int blkstart = lv.getBlockStart ();
						int blkend = lv.getBlockEnd ();
						// if(index==localVarindex)
						if (blkstart == 0 && blkend == method.getCode ().length) {
							allParams += AnnotationHelper.toString (method.getRuntimeVisibleParameterAnnotations ()).trim () + " ";
							allParams += AnnotationHelper.toString (method.getRuntimeInvisibleParameterAnnotations ()).trim () + " ";
							allParams += dt + " " + name;
							if (count < (method.getMethodParams ().length - 1)) {
								allParams += ",";
							}
							// localVarindex++;
							count++;
						}
					}
					else // -g:none
					{
						if (index == start && current < total) {
							current++;
							allParams += AnnotationHelper.toString (method.getRuntimeVisibleParameterAnnotations ()).trim () + " ";
							allParams += AnnotationHelper.toString (method.getRuntimeInvisibleParameterAnnotations ()).trim () + " ";
							allParams += dt + " " + name;
							if (current < (total)) allParams += ",";
							if (dt.trim ().equals ("long") || dt.trim ().equals ("double")) start++;
							start++;

						}

					}
				}
			}
			else {
				int pos = 0;
				allParams = "";
				StringBuffer paramImports = new StringBuffer ("");
				while (tokenizer.hasMoreTokens ()) {
					String token = (String) tokenizer.nextToken ();
					int br = token.indexOf (")");
					if (br != -1) {
						token = token.substring (0, br);
					}
					if (token.startsWith ("(")) {
						br = token.indexOf ("(");
						token = token.substring (br + 1);
					}
					StringBuffer sb = new StringBuffer ("");
					token = Util.checkForImport (token, sb);
					token = token.trim ();
					if (importfiles.indexOf (token) == -1 && paramImports.toString ().indexOf (token) == -1 && newimports.indexOf (token) == -1)
						paramImports.append ("import " + token + ";\n");
					allParams += sb.toString () + " " + "param_" + pos++;

					if (tokenizer.hasMoreTokens ()) allParams += " , ";
				}
				newimports.append (paramImports.toString ());

			}

			if (!allParams.trim ().startsWith ("(")) allParams = "(" + allParams;
			return allParams + ")";
		}
		else {
			return "( )";
		}
	}

	private static boolean contains (String[] array, String element) {
		boolean flag = false;

		for (int x = 0; x < array.length; x++) {
			if (element.equals (array[x])) {
				flag = true;
				break;
			}
		}

		return flag;
	}

	public static ArrayList getAllImportedClasses () {
		return allImportedClasses;
	}

	private static ArrayList allImportedClasses = new ArrayList ();

	public static void addImportClass (java.lang.String className) {
		allImportedClasses.add (className);
	}

	private static String showImportedClasses (ArrayList allImportedClasses, String name) {
		// String temp="";
		String formattedtemp = "";
		allImportedClasses = Util.removeDuplicates (allImportedClasses);
		if (allImportedClasses.size () > 0) {
			formattedtemp += "/**** List of All Imported Classes ***/\n\n";

		}
		for (int s = 0; s < allImportedClasses.size (); s++) {
			String fullname = (java.lang.String) allImportedClasses.get (s);
			if (fullname != null) fullname = fullname.trim ();

			if (name != null) name = name.trim ();
			if (fullname.equalsIgnoreCase (name)) continue;
			// temp=Util.formatDecompiledStatement("import "+fullname);
			String t = fullname;
			if (t != null) {
				int br = t.indexOf ("[]");
				if (br == -1) br = t.indexOf ("[");
				if (br != -1) t = t.substring (0, br);
				t = t.trim ();
				fullname = t;
			}
			if (fullname.trim ().equals ("byte") || fullname.trim ().equals ("short") || fullname.trim ().equals ("char")
					|| fullname.trim ().equals ("int") || fullname.trim ().equals ("long") || fullname.trim ().equals ("float")
					|| fullname.trim ().equals ("double")) continue;
			if (fullname.indexOf (".") == -1) continue;
			if (fullname.indexOf (ConsoleLauncher.getClazzRef ().getClassName ()) > -1) {
				continue;
			}

			boolean addImport = addimportstmt (fullname);

			if (fullname.indexOf ("$") == -1 && addImport)
				formattedtemp += "import " + fullname + ";\n";
			else if (fullname.indexOf ("$") != -1 && addImport) {
				fullname = fullname.replaceAll ("\\$", ".");
				// fullname = fullname.substring(0, fullname.indexOf("$"));
				formattedtemp += "import " + fullname + ";\n";

			}
			// formattedtemp+=temp;
			// formattedtemp+=";\n";
		}

		return formattedtemp;
	}

	// TODO : Take Filters into account
	public static void decompileJar (java.lang.String jarPath) {
		try {
			if (UILauncher.getMainFrame () == null) {
				InnerClassTracker.reinitializeStaticMembers ();
				allClassStructures = new ArrayList ();
				currentIsInner = false;
				classMethodMap = new HashMap ();
				classMethodRefMap = new HashMap ();
			}
			JarFile jf = new JarFile (jarPath);
			Enumeration alljarentries = jf.entries ();
			ArrayList jarfiles = new ArrayList ();
			while (alljarentries.hasMoreElements ()) {
				ZipEntry ze = (ZipEntry) alljarentries.nextElement ();
				String name = ze.getName ();
				if (name.endsWith (".class")) {
					InputStream is = jf.getInputStream (ze);
					FileOutputStream fos = null;
					String JarDir = Configuration.getTempDir ();
					File f = new File (JarDir);
					if (f.exists () == false) {
						Writer w = Writer.getWriter ("log");
						w.writeLog ("Temp Dir Does Not Exist...");
						w.writeLog ("Jdec will not create The Directory " + JarDir);
						w.flush ();
						f.mkdirs ();
					}
					int slash = name.lastIndexOf ("/");
					File tempFile = null;
					if (slash != -1) {
						tempFile = new File (JarDir + name.substring (slash));
						tempFile.delete ();
						fos = new FileOutputStream (tempFile);
					}
					else {
						tempFile = new File (JarDir + File.separator + name);
						tempFile.delete ();
						fos = new FileOutputStream (tempFile);
					}
					int i = is.read ();
					while (i != -1) {
						fos.write (i);
						i = is.read ();
					}
					fos.flush ();
					fos.close ();
					is.close ();
					fos = null;
					is = null;
					jarfiles.add (tempFile);
					// System.out.println("Added to jar list "+tempFile);
				}
			}

			if (jarfiles.size () > 0) {
				String classContent = "";
				java.lang.String orgpath = Configuration.getBkpoppath ();
				for (int start = 0; start < jarfiles.size (); start++) {
					mainRoot = null;
					File currentFile = (File) jarfiles.get (start);
					System.out.println ("Decompiling class ..." + currentFile.getAbsolutePath ());
					Configuration.setClassFilePath (currentFile.getAbsolutePath ());
					couldNotFinish = new ArrayList ();
					addLicence = true;
					try {
						classContent += decompileClass (currentFile.getAbsolutePath ());
						if (UILauncher.getMainFrame () == null) {
							InnerClassTracker.reinitializeStaticMembers ();
							allClassStructures = new ArrayList ();
						}
					}
					catch (RuntimeException rxe) {
						Writer w = Writer.getWriter ("log");
						AllExceptionHandler handler = new AllExceptionHandler (rxe);
						handler.reportException ();
						reInitializeConstantPoolEntries ();
						continue;
					}

					catch (Exception exp) {
						AllExceptionHandler hl = new AllExceptionHandler (exp, "Could Not decompile Class " + currentFile.getAbsolutePath ());
						hl.reportException ();
						reInitializeConstantPoolEntries ();
						continue;
					}

					currentDepthLevel = 0;
					decompileInnerClasses (); // TODO
					if (mainRoot != null) Configuration.setClassFilePath (mainRoot.getClassPath ());
					java.lang.String importedclassesInInnerclasses = "";

					if (innerClassesDecompied != null && innerClassesDecompied.size () != 0) {
						java.lang.String innerClassFileContents = "";
						for (int s = 0; s < innerClassesDecompied.size (); s++) {
							java.lang.String classfilepath = (java.lang.String) innerClassesDecompied.get (s);
							try {
								java.lang.String currentFileContent = "";
								java.lang.String cname = getClassName (classfilepath);
								if (cname.indexOf (".") != -1) {
									cname = cname.substring (0, cname.indexOf ("."));
									cname = cname + "." + Configuration.getFileExtension ();
									classfilepath = Configuration.getOutputFolderPath () + File.separator + cname;
								}
								else {
									cname = cname + "." + Configuration.getFileExtension ();
									classfilepath = Configuration.getOutputFolderPath () + File.separator + cname;
								}
								File temp = new File (classfilepath);
								FileInputStream fis = new FileInputStream (temp);
								BufferedInputStream bis = new BufferedInputStream (fis);
								BufferedReader br = new BufferedReader (new InputStreamReader (bis));
								java.lang.String line = null;
								while ((line = br.readLine ()) != null) {
									currentFileContent += line + "\n";
								}
								if (currentFileContent.trim ().length () != 0) {

									int importend = currentFileContent.indexOf ("// End of Import");
									importedclassesInInnerclasses = "";
									if (importend != -1) {
										java.lang.String p = currentFileContent.substring (0, importend);
										currentFileContent = currentFileContent.substring (importend + "// End of Import".length ());
										importedclassesInInnerclasses += p;
									}
									innerClassFileContents += currentFileContent;
									innerClassFileContents += "\n\n//End Of a Inner Class File Content...\n\n";
								}

								temp = null;
								fis = null;
								bis = null;
								br = null;
								System.gc ();

							}
							catch (IOException ioe) {

							}
						}
						if (innerClassFileContents.trim ().length () != 0) {
							innerClassFileContents = checkForLicenceAndPackageInInnerClassContent (innerClassFileContents);
							java.lang.String mainFileContent = "";
							java.lang.String cname = getClassName (mainRoot.getClassPath ());
							java.lang.String mainfilepath = "";
							if (cname.indexOf (".") != -1) {
								cname = cname.substring (0, cname.indexOf ("."));
								cname = cname + "." + Configuration.getFileExtension ();
								mainfilepath = Configuration.getOutputFolderPath () + File.separator + cname;
							}
							else {
								cname = cname + "." + Configuration.getFileExtension ();
								mainfilepath = Configuration.getOutputFolderPath () + File.separator + cname;
							}
							File mainfile = new File (mainfilepath);
							FileInputStream fis = new FileInputStream (mainfile);
							BufferedInputStream bis = new BufferedInputStream (fis);
							BufferedReader br = new BufferedReader (new InputStreamReader (bis));
							java.lang.String line = null;
							while ((line = br.readLine ()) != null) {
								mainFileContent += line + "\n";
							}

							java.lang.String completeContent = mainFileContent;
							completeContent += "\n\n//Beginning of Inner Class Content...\n\n ";
							completeContent += innerClassFileContents;

							// Write back to Main File
							if (mainfile.exists ()) {
								mainfile.delete ();
							}

							mainfile = new File (mainfilepath);
							mainfile.createNewFile ();
							try {
								FileOutputStream fos = new FileOutputStream (mainfile);
								BufferedWriter bw = new BufferedWriter (new OutputStreamWriter (fos));
								importedclassesInInnerclasses = importedclassesInInnerclasses.replaceAll (
										"/\\*\\*\\*\\* List of All Imported Classes \\*\\*\\*/", "");
								importedclassesInInnerclasses = importedclassesInInnerclasses.replaceAll ("\n\n", "\n");
								completeContent = completeContent.replaceAll ("/\\*\\*\\*\\* List of All Imported Classes \\*\\*\\*/", "");
								Iterator classNames = getClazzObjects ().keySet ().iterator ();
								java.lang.String classname = "";
								while (classNames.hasNext ()) {
									classname = (java.lang.String) classNames.next ();
									break;
								}

								bw.write (completeContent);
								// bw.write("/**** List of All Imported Classes
								// ***/");
								completeContent = completeContent.replaceAll ("\n\n", "\n");
								if (importedclassesInInnerclasses.endsWith ("\n")) {
									int n = importedclassesInInnerclasses.lastIndexOf ("\n");
									if (n != -1) {
										importedclassesInInnerclasses = importedclassesInInnerclasses.substring (0, n);
									}

								}
								// bw.write(importedclassesInInnerclasses);
								completeContent = checkForImportDuplicates (importedclassesInInnerclasses, completeContent);

								int present = completeContent.indexOf (localvariablenote);

								completeContent = completeContent.replaceAll (localVarNote, "");

								bw.flush ();
								bw.close ();
							}
							catch (IOException ioe) {

							}

						}

					}

					// Restore orig classpath when over
					System.out.println ("Decompiled class ..." + currentFile.getAbsolutePath () + "\n");
					reInitializeConstantPoolEntries ();
				}
				Manager.getManager ().setShowProgressBar (false);
				// Repaint the Jar Tab again....
				java.lang.String jarDir = Configuration.getBkpoppath ();
				File temp = new File (jarDir);
				if (temp.exists ()) {
					JdecTree jarExploded = new JdecTree (jarDir);
					UIObserver observer = UILauncher.getObserver ();
					if (observer != null) // Actually no need to check
					{
						observer.resetTabsPane (true);
						Manager manager = Manager.getManager ();
						ArrayList list = manager.getCurrentSplitPaneComponents ();
						JTabbedPane tabPane = null;
						for (int s = 0; s < list.size (); s++) {
							Object o = list.get (s);
							if (o instanceof JTabbedPane) {
								tabPane = (JTabbedPane) o;
								break;
							}
						}
						if (tabPane != null) {
							int jarTabIndex = tabPane.indexOfTab ("Jar");
							if (jarTabIndex >= 0) {
								tabPane.remove (jarTabIndex);
								tabPane.addTab ("Jar", jarExploded);
								tabPane.setSelectedComponent (jarExploded);
							}
						}

					}

					JFrame mainFrame = UILauncher.getMainFrame ();
					// mainFrame.repaint();

					// Ensure Look and Feel does not change
					try {
						UIManager.setLookAndFeel (UILauncher.getUIutil ().getCurrentLNF ());
						SwingUtilities.updateComponentTreeUI (UILauncher.getMainFrame ());
					}
					catch (Exception looknfeel) {
						// Handle todo
					}
					if (mainFrame != null) mainFrame.repaint ();

					File jd = new File (Configuration.getBkpoppath () + File.separator + "JARDECOMPILED");
					if (jd.exists ()) {
						jd.delete ();
					}
					if (jd.exists ()) {
						jd.deleteOnExit ();
					}

				}
				// /

			}

		}
		catch (FileNotFoundException fne) {
			AllExceptionHandler handler = new AllExceptionHandler (fne);
			handler.reportException ();
		}
		catch (IOException io) {
			AllExceptionHandler handler = new AllExceptionHandler (io);
			handler.reportException ();
		}

	}

	public static void decompileJarFromUI (java.lang.String jarPath, ArrayList classes) {
		try {
			if (UILauncher.getMainFrame () == null) {
				InnerClassTracker.reinitializeStaticMembers ();
				allClassStructures = new ArrayList ();
				currentIsInner = false;
				classMethodMap = new HashMap ();
				classMethodRefMap = new HashMap ();
			}
			ArrayList jarfiles = classes;
			if (jarfiles.size () > 0) {
				String classContent = "";
				java.lang.String orgpath = Configuration.getBkpoppath ();
				for (int start = 0; start < jarfiles.size (); start++) {
					mainRoot = null;
					File currentFile = (File) jarfiles.get (start);
					System.out.println ("Decompiling class ..." + currentFile.getAbsolutePath ());
					Configuration.setClassFilePath (currentFile.getAbsolutePath ());
					couldNotFinish = new ArrayList ();
					addLicence = true;
					try {
						classContent += decompileClass (currentFile.getAbsolutePath ());
						if (UILauncher.getMainFrame () == null) {
							InnerClassTracker.reinitializeStaticMembers ();
							allClassStructures = new ArrayList ();
						}
					}
					catch (RuntimeException rxe) {
						Writer w = Writer.getWriter ("log");
						rxe.printStackTrace (w);
						reInitializeConstantPoolEntries ();
						continue;
						// throw new RuntimeException(rxe.getMessage());//needs
						// to be commented out...
					}

					catch (Exception exp) {
						AllExceptionHandler hl = new AllExceptionHandler (exp, "Could Not decompile Class " + currentFile.getAbsolutePath ());
						hl.reportException ();
						reInitializeConstantPoolEntries ();
						continue;
					}

					currentDepthLevel = 0;
					decompileInnerClasses (); // TODO
					if (mainRoot != null) Configuration.setClassFilePath (mainRoot.getClassPath ());
					java.lang.String importedclassesInInnerclasses = "";

					if (innerClassesDecompied != null && innerClassesDecompied.size () != 0) {
						java.lang.String innerClassFileContents = "";
						for (int s = 0; s < innerClassesDecompied.size (); s++) {
							java.lang.String classfilepath = (java.lang.String) innerClassesDecompied.get (s);
							try {
								java.lang.String currentFileContent = "";
								java.lang.String cname = getClassName (classfilepath);
								if (cname.indexOf (".") != -1) {
									cname = cname.substring (0, cname.indexOf ("."));
									cname = cname + "." + Configuration.getFileExtension ();
									classfilepath = Configuration.getOutputFolderPath () + File.separator + cname;
								}
								else {
									cname = cname + "." + Configuration.getFileExtension ();
									classfilepath = Configuration.getOutputFolderPath () + File.separator + cname;
								}
								File temp = new File (classfilepath);
								FileInputStream fis = new FileInputStream (temp);
								BufferedInputStream bis = new BufferedInputStream (fis);
								BufferedReader br = new BufferedReader (new InputStreamReader (bis));
								java.lang.String line = null;
								while ((line = br.readLine ()) != null) {
									currentFileContent += line + "\n";
								}
								if (currentFileContent.trim ().length () != 0) {

									int importend = currentFileContent.indexOf ("// End of Import");
									importedclassesInInnerclasses = "";
									if (importend != -1) {
										java.lang.String p = currentFileContent.substring (0, importend);
										currentFileContent = currentFileContent.substring (importend + "// End of Import".length ());
										importedclassesInInnerclasses += p;
									}
									innerClassFileContents += currentFileContent;
									innerClassFileContents += "\n\n//End Of a Inner Class File Content...\n\n";
								}

								temp = null;
								fis = null;
								bis = null;
								br = null;
								System.gc ();

							}
							catch (IOException ioe) {

							}
						}
						if (innerClassFileContents.trim ().length () != 0) {
							innerClassFileContents = checkForLicenceAndPackageInInnerClassContent (innerClassFileContents);
							java.lang.String mainFileContent = "";
							java.lang.String cname = getClassName (mainRoot.getClassPath ());
							java.lang.String mainfilepath = "";
							if (cname.indexOf (".") != -1) {
								cname = cname.substring (0, cname.indexOf ("."));
								cname = cname + "." + Configuration.getFileExtension ();
								mainfilepath = Configuration.getOutputFolderPath () + File.separator + cname;
							}
							else {
								cname = cname + "." + Configuration.getFileExtension ();
								mainfilepath = Configuration.getOutputFolderPath () + File.separator + cname;
							}
							File mainfile = new File (mainfilepath);
							FileInputStream fis = new FileInputStream (mainfile);
							BufferedInputStream bis = new BufferedInputStream (fis);
							BufferedReader br = new BufferedReader (new InputStreamReader (bis));
							java.lang.String line = null;
							while ((line = br.readLine ()) != null) {
								mainFileContent += line + "\n";
							}

							java.lang.String completeContent = mainFileContent;
							completeContent += "\n\n//Beginning of Inner Class Content...\n\n ";
							completeContent += innerClassFileContents;

							// Write back to Main File
							if (mainfile.exists ()) {
								mainfile.delete ();
							}

							mainfile = new File (mainfilepath);
							mainfile.createNewFile ();
							try {
								FileOutputStream fos = new FileOutputStream (mainfile);
								BufferedWriter bw = new BufferedWriter (new OutputStreamWriter (fos));
								importedclassesInInnerclasses = importedclassesInInnerclasses.replaceAll (
										"/\\*\\*\\*\\* List of All Imported Classes \\*\\*\\*/", "");
								importedclassesInInnerclasses = importedclassesInInnerclasses.replaceAll ("\n\n", "\n");
								completeContent = completeContent.replaceAll ("/\\*\\*\\*\\* List of All Imported Classes \\*\\*\\*/", "");
								Iterator classNames = getClazzObjects ().keySet ().iterator ();
								java.lang.String classname = "";
								while (classNames.hasNext ()) {
									classname = (java.lang.String) classNames.next ();
									break;
								}

								bw.write (completeContent);
								// bw.write("/**** List of All Imported Classes
								// ***/");
								completeContent = completeContent.replaceAll ("\n\n", "\n");
								if (importedclassesInInnerclasses.endsWith ("\n")) {
									int n = importedclassesInInnerclasses.lastIndexOf ("\n");
									if (n != -1) {
										importedclassesInInnerclasses = importedclassesInInnerclasses.substring (0, n);
									}

								}
								// bw.write(importedclassesInInnerclasses);
								completeContent = checkForImportDuplicates (importedclassesInInnerclasses, completeContent);

								int present = completeContent.indexOf (localvariablenote);

								completeContent = completeContent.replaceAll (localVarNote, "");

								bw.flush ();
								bw.close ();
							}
							catch (IOException ioe) {

							}

						}

					}

					// Restore orig classpath when over
					System.out.println ("Decompiled class ..." + currentFile.getAbsolutePath () + "\n");
					reInitializeConstantPoolEntries ();
				}
				Manager.getManager ().setShowProgressBar (false);
				// Repaint the Jar Tab again....
				java.lang.String jarDir = Configuration.getBkpoppath ();
				File temp = new File (jarDir);
				if (temp.exists ()) {
					JdecTree jarExploded = new JdecTree (jarDir);
					UIObserver observer = UILauncher.getObserver ();
					if (observer != null) // Actually no need to check
					{
						observer.resetTabsPane (true);
						Manager manager = Manager.getManager ();
						ArrayList list = manager.getCurrentSplitPaneComponents ();
						JTabbedPane tabPane = null;
						for (int s = 0; s < list.size (); s++) {
							Object o = list.get (s);
							if (o instanceof JTabbedPane) {
								tabPane = (JTabbedPane) o;
								break;
							}
						}
						if (tabPane != null) {
							int jarTabIndex = tabPane.indexOfTab ("Jar");
							if (jarTabIndex >= 0) {
								tabPane.remove (jarTabIndex);
								tabPane.addTab ("Jar", jarExploded);
								tabPane.setSelectedComponent (jarExploded);
							}
						}

					}

					JFrame mainFrame = UILauncher.getMainFrame ();
					// mainFrame.repaint();

					// Ensure Look and Feel does not change
					try {
						UIManager.setLookAndFeel (UILauncher.getUIutil ().getCurrentLNF ());
						SwingUtilities.updateComponentTreeUI (UILauncher.getMainFrame ());
					}
					catch (Exception looknfeel) {
						// Handle todo
					}
					if (mainFrame != null) mainFrame.repaint ();

					File jd = new File (Configuration.getBkpoppath () + File.separator + "JARDECOMPILED");
					if (jd.exists ()) {
						jd.delete ();
					}
					if (jd.exists ()) {
						jd.deleteOnExit ();
					}

				}
				// /

			}

		}
		catch (FileNotFoundException fne) {
			AllExceptionHandler handler = new AllExceptionHandler (fne);
			handler.reportException ();
		}
		catch (IOException io) {
			AllExceptionHandler handler = new AllExceptionHandler (io);
			handler.reportException ();
		}

	}

	private static void initializeJvmInstructionMap () {
		JvmUtil jvmutil = new JvmUtil ();
		jvmutil.populateMap ();
	}

	public static void setInstrMap (HashMap map) {
		InstructionMap = map;
	}

	public static void constantPool (java.lang.String path) throws IOException {
		String desc = showConstantPool (path);
		if (desc != null) {
			if (desc.trim ().length () > 0) {
				String interpret = "";
				interpret += "# Entry:   Signifies The Constant Pool Entry.\n#[NOTE ] Constant Pool always begins at Position 1\n";
				interpret += "# TagType: Signifies the Type of Entry ";
				interpret += "# Read As Follows:\n";
				interpret += "# TAG_MREF Stands For  Method Ref Type\n";
				interpret += "# TAG_CLASS Stands For Class Type\n";
				interpret += "# TAG_STR Stands For String Type\n";
				interpret += "# TAG_FREF Stands For  Field Ref Type\n";
				interpret += "# TAG_IMREF Stands For Interface Method Ref Type\n";
				interpret += "# TAG_UTF8 Stands For Interface UTF8 Type\n";
				interpret += "# TAG_NTYPE Stands For NameAndType Structure\n";
				interpret += "# TAG_INT Stands For Integer Type\n";
				interpret += "# TAG_FLOAT Stands For Float Type\n";
				interpret += "# TAG_DBL Stands For Double Type\n";
				interpret += "# TAG_LONG Stands For Long Type\n\n";
				java.lang.String header = "Entry    \t     TagType\t    PoolIndex\t    PoolIndex\t   UTF8value\n\n";
				desc = header.concat (desc);
				desc = interpret.concat (desc);
			}

			File f = new File (Configuration.getOutputFolderPath () + File.separator
					+ getSimpleName (getClassName (Configuration.getJavaClassFile ())) + "." + Configuration.getFileExtension ());
			if (f.exists ()) f.delete ();
			Writer writer = Writer.getWriter ("output");
			writer.writeOutput (desc);
			writer.flush ();
			writer.close ("output");
		}
		else {
			UIUtil uiutil = UILauncher.getUIutil ();
			if (uiutil != null) {
				uiutil.setcpdescription (null);
			}
		}
	}

	private static HashMap InstructionMap;

	public static HashMap getInstructionMap () {
		return InstructionMap;
	}

	public static void reInitializeConstantPoolEntries () {
		CPool.setAllClassInfos (new ArrayList ());
		CPool.setAllDoubles (new ArrayList ());
		CPool.setAllFieldRefs (new ArrayList ());
		CPool.setAllFloats (new ArrayList ());
		CPool.setAllIntegers (new ArrayList ());
		CPool.setAllInterfaceMethodRefs (new ArrayList ());
		CPool.setAllLongs (new ArrayList ());
		CPool.setAllMethodRefs (new ArrayList ());
		CPool.setAllNameAndTypes (new ArrayList ());
		CPool.setAllStrings (new ArrayList ());
		CPool.setAllUtf8 (new ArrayList ());

	}

	private static int poolcount = -1;

	public static int getPoolCount () {
		return poolcount;
	}

	public static void setPoolCount (int n) {
		poolcount = n;
	}

	public static int getMagicNumber () {
		return magicNumber;
	}

	public static JavaClass getClazzRef () {
		return clazzRef;
	}

	public static void setClazzRef (JavaClass clazzRef) {
		ConsoleLauncher.clazzRef = clazzRef;
	}

	public static java.lang.String getResultFileName () {
		return resultFileName;
	}

	public static void setFileDecompiled (boolean status) {
		filedecompiled = status;
	}

	private static boolean filedecompiled = false;

	public static void setCurrentSourceFile (File f) {
		currentSourceFile = f;
	}

	private static File currentSourceFile = null; // The File for which

	// ConsoleLauncher was
	// invoked

	public static File getCurrentSourceFile () {
		return currentSourceFile;
	}

	public static void setCurrentJarSourceFile (File f) {
		currentJarSourceFile = f;
	}

	private static File currentJarSourceFile = null; // The File for which

	// ConsoleLauncher was
	// invoked

	public static File getCurrentJarSourceFile () {
		return currentJarSourceFile;
	}

	public static boolean isFiledecompiled () {
		return filedecompiled;
	}

	public static void setMethodLookUp (Hashtable ht) {
		msignref = new Hashtable ();
		createmsignref (msignref, ht);
		ConsoleLauncher.ht = ht;
	}

	private static void createmsignref (Hashtable msignref, Hashtable ht) {
		Iterator i = ht.values ().iterator ();
		while (i.hasNext ()) {
			Behaviour b = (Behaviour) i.next ();
			String sign = "";
			b.getUserFriendlyMethodAccessors ();
			if (b.isMethodConstructor ()) {
				sign = b.getBehaviourName () + b.getUserFriendlyMethodParams ();
			}
			else {

				// sign+=" "+b.getReturnType();
				sign = b.getBehaviourName () + b.getUserFriendlyMethodParams ();

			}
			msignref.put ("\t\t" + sign, b);
		}

	}

	private static Hashtable ht = null;

	private static Hashtable msignref = null;

	public static Hashtable getMethodLookup () {
		return ht;
	}

	public static Hashtable getMethodSignMethodLookup () {
		return msignref;
	}

	/**
	 * Important : Do not use this variable anywhere EXCEPT for refresh purpose
	 */
	private static java.lang.String currentDecompiledFile = null;

	public static java.lang.String getCurrentDecompiledFile () {
		return currentDecompiledFile;
	}

	public static void setCurrentDecompiledFile (java.lang.String s) {
		currentDecompiledFile = s;
	}

	public static boolean isCurrentClassCompiledWithDebugInfo () {
		return currentClassCompiledWithDebugInfo;
	}

	public static void setCurrentClassCompiledWithDebugInfo (boolean b) {
		currentClassCompiledWithDebugInfo = b;
	}

	private static boolean currentClassCompiledWithDebugInfo = false;

	private static boolean addNoteWRTDummyVar = false;

	public static void dummyVariablesCreated (boolean b) {
		addNoteWRTDummyVar = b;
	}

	public static boolean getDummyVarNoteStatus () {
		return addNoteWRTDummyVar;
	}

	public static Behaviour getCurrentMethodBeingExecuted () {
		return currentMethodBeingExecuted;
	}

	public static void setCurrentMethodBeingExecuted (Behaviour cur) {
		currentMethodBeingExecuted = cur;
	}

	private static Behaviour currentMethodBeingExecuted = null;

	public static InnerClassTracker getTracker () {
		return tracker;
	}

	private static InnerClassTracker tracker = null;

	private static ArrayList innerClassesDecompied = null;

	private static void decompileInnerClasses () {
		// ArrayList decompiledclasses=new ArrayList();
		// Now decompile Inner classes here // TODO:
		if (mainRoot == null) return;
		addLicence = false;
		currentIsInner = true;
		boolean decompileInnerClass = (mainRoot.getChildren ().size () > 0);
		if (decompileInnerClass && Configuration.getInnerdepth () > 0) {
			innerClassesDecompied = new ArrayList ();
			reInitializeConstantPoolEntries ();
			InnerClassTracker.Node rootUnderConsideration = mainRoot;
			while (rootUnderConsideration != null) {
				InnerClassTracker.Node firstchild = tracker.getParentsFirstNode (rootUnderConsideration);
				if (firstchild != null) {
					java.lang.String classpath = firstchild.getClassPath ();
					// Backup current Main Class File // mainRoot should reflect
					// this
					// NOTE: mainRoot should onlu be set once per main Class
					Configuration.setClassFilePath (classpath); // This call
					// needs to be
					// called again
					// after every
					// inner class
					// decompilation
					// is over to
					// mainRoot
					couldNotFinish = new ArrayList ();
					File f = new File (classpath);
					boolean exists = f.exists ();
					boolean alreadyDec = shouldInnerClassBeDecompiled (classpath);
					/*
					 * if(alreadyDec) { innerClassesDecompied.add(classpath); }
					 */
					if (exists) {// && !alreadyDec) {
						try {
							decompileClass (classpath);
							innerClassesDecompied.add (classpath);
							ClassStructure innerCS = new ClassStructure ();
							innerCS.setName (getClassName (classpath));
							innerCS.setMethods (currentClassMethods);
							ClassStructure parent = getClassStructure (rootUnderConsideration.getClassName ());
							boolean add = addClassStructure (getClassName (classpath));
							if (add) allClassStructures.add (innerCS);
							if (parent != null) {
								parent.addInnerClass (innerCS);
								innerCS.setParent (parent);
							}

						}
						catch (Exception exp) {
							AllExceptionHandler h1 = new AllExceptionHandler (exp);
							h1.reportException ();
						}
					}
					reInitializeConstantPoolEntries ();

					// Done with First child of Main Node
					// Now iterate through a loop and decompile each of its
					// siblings
					InnerClassTracker.Node sibling = firstchild.getNextSibling (rootUnderConsideration);
					Node tempNode = null;
					while (sibling != null) {
						classpath = sibling.getClassPath ();
						Configuration.setClassFilePath (classpath);
						couldNotFinish = new ArrayList (); // TODO: Add this
						// later to UI side
						// also.
						f = new File (classpath);
						exists = f.exists ();

						if (exists) {
							try {
								decompileClass (classpath);
								innerClassesDecompied.add (classpath);
								ClassStructure innerCS = new ClassStructure ();
								innerCS.setName (getClassName (classpath));
								innerCS.setMethods (currentClassMethods);
								ClassStructure parent = getClassStructure (rootUnderConsideration.getClassName ());
								boolean add = addClassStructure (getClassName (classpath));
								if (add) allClassStructures.add (innerCS);
								if (parent != null) {
									parent.addInnerClass (innerCS);
									innerCS.setParent (parent);
								}
							}
							catch (Exception exp) {
								AllExceptionHandler h1 = new AllExceptionHandler (exp);
								h1.reportException ();
							}
						}
						reInitializeConstantPoolEntries ();
						tempNode = sibling;
						sibling = sibling.getNextSibling (rootUnderConsideration);

					}
					/***********************************************************
					 * * Sibling is null what does it mean ??? 1 Level is
					 * over...So if the user has specified .So need to check
					 * with user set level over here either to exit from the
					 * current main ... Class File or to continue.
					 **********************************************************/
					if (tempNode != null) {
						Node anyOther = InnerClassTracker.getNextSiblingForParentWithChildren (tempNode.getParent ());
						if (anyOther == null) currentDepthLevel++; // Increment Inner class
						// Level depth
					}
					else
						currentDepthLevel++;

					// Sould the decomoile stop decompiling inner clases ...?
					if (currentDepthLevel == Configuration.getInnerdepth ()) {
						break;
					}
					else {

						InnerClassTracker.Node newroot = tracker.getCurrentRoot ();
						rootUnderConsideration = newroot;

					}

				}
				else
					break;

			}

		}
		else {
			innerClassesDecompied = new ArrayList ();
		}

		// end of inner class decompilation

	}

	public static InnerClassTracker.Node getCurrentRootAdded () {
		return currentRootAdded;
	}

	private static InnerClassTracker.Node currentRootAdded = null;

	private static java.lang.String checkForImportDuplicates (java.lang.String importedclassesInInnerclasses, java.lang.String completeContent) {
		java.lang.String temp = completeContent;
		int end = temp.indexOf ("// End of Import");
		java.lang.String importsinmainclass = "";
		java.lang.String temp2 = completeContent.substring (end + "// End of Import".length ());
		if (end != -1) {
			importsinmainclass = temp.substring (0, end);
			java.lang.String validImports = removeDuplicateImports (importedclassesInInnerclasses, importsinmainclass);
			if (validImports.length () > 0) {
				validImports += "\n\n// End of Import\n\n";
			}
			return validImports + temp2;
		}
		return temp;
	}

	private static java.lang.String removeDuplicateImports (java.lang.String importedclassesInInnerclasses, java.lang.String importsinmainclass) {
		java.lang.String valid = "\n";
		StringTokenizer st = new StringTokenizer (importedclassesInInnerclasses, "\n");
		ArrayList importedClasses = new ArrayList ();
		while (st.hasMoreTokens ()) {
			java.lang.String token = (java.lang.String) st.nextToken ();
			importedClasses.add (token);
		}
		st = new StringTokenizer (importsinmainclass, "\n");
		while (st.hasMoreTokens ()) {
			java.lang.String token = (java.lang.String) st.nextToken ();
			importedClasses.add (token);
		}
		importedClasses = Util.removeDuplicates (importedClasses);
		for (int i = 0; i < importedClasses.size (); i++) {
			valid += (java.lang.String) importedClasses.get (i) + "\n";
		}
		return valid;

	}

	private static java.lang.String localVarNote = "";

	private static java.lang.String localvariablenote = "";

	static {
		localVarNote = "/\\*\\*\\*\\*\nNOTE: THe decompiled statmements contain some Temporary Variables. \n(Beginning with JdecGenerated) Jdec Generates These while";
		localVarNote += "\nprocessing blocks like NEW. However the actual variable in the code\n then gets assigned to the respective  temporary variable.\\*\\*\\*\\*/\n";
	}

	private static ArrayList couldNotDecompileMethods () {
		return couldNotFinish;
	}

	public static void addCouldNotFinish (Behaviour couldNotFinish) {
		if (couldNotFinish != null) ConsoleLauncher.couldNotFinish.add (couldNotFinish);
	}

	private static ArrayList couldNotFinish;

	private static boolean didThisMethodFail (Behaviour method) {
		if (couldNotFinish != null) {
			for (int i = 0; i < couldNotFinish.size (); i++) {

				Behaviour cur = (Behaviour) couldNotFinish.get (i);
				if (cur == method) return true;
			}
			return false;

		}
		else
			return false;

	}

	public static void reInitializeConstantPoolDesc () {
		CPool.resetCpoolDesc ();
	}

	public static java.lang.String getLicenceWarning () {
		java.lang.String mesg = "//  Decompiled by jdec\n" + "//  DECOMPILER HOME PAGE: jdec.sourceforge.net\n"
				+ "//  Main HOSTING SITE: sourceforge.net\n" + "//  Copyright (C)2006,2007,2008 Swaroop Belur.\n"
				+ "//  jdec comes with ABSOLUTELY NO WARRANTY;\n" + "//  This is free software, and you are welcome to redistribute\n"
				+ "//  it under certain conditions;\n" + "//  See the File 'COPYING' For more details.\n\n";
		return mesg;

	}

	private static java.lang.String getAnyExceptionTableDetails (Behaviour method) {
		method.createExceptionTableInStringifiedForm ();
		java.lang.String synchDesc = method.getSynchTableDetails ();
		java.lang.String det = Configuration.getDetailedExceptionTableInfo ();
		java.lang.String excep = "";

		if (det.equalsIgnoreCase ("true")) {
			excep = method.getDetailedExceptionTableDetails ();
		}
		if (det.equalsIgnoreCase ("false")) {
			excep = method.getShortExceptionTableDetails ();
		}
		if (synchDesc.length () > 0) {
			java.lang.String desc = synchDesc;
			if (excep.length () > 0) {
				desc += "\n\n";
				desc += excep;
			}
			return desc;
		}
		else {
			java.lang.String desc = "";
			if (excep.length () > 0) {
				desc += excep;
			}
			return desc;

		}

	}

	private static java.lang.String getSimpleName (java.lang.String name) {
		if (name == null || name.length () == 0) return name;
		int index = name.indexOf (".");
		java.lang.String simplename = "";
		if (index != -1)
			simplename = name.substring (0, index);
		else
			simplename = name;

		return simplename;

	}

	private static java.lang.String getPackagePathFromRootForThisClass () {

		Iterator classNames = getClazzObjects ().keySet ().iterator ();
		java.lang.String classname = "";
		while (classNames.hasNext ()) {
			classname = (java.lang.String) classNames.next ();
			break;
		}
		StringTokenizer token = new StringTokenizer (classname, ".");
		ArrayList list = new ArrayList ();
		while (token.hasMoreTokens ()) {
			java.lang.String cur = token.nextToken ();
			list.add (cur);
		}
		java.lang.String path = "";
		for (int z = 0; z < list.size () - 1; z++) {
			java.lang.String cur = (java.lang.String) list.get (z);
			if (z < list.size () - 2)
				path = path + cur + File.separator;
			else
				path = path + cur;
		}
		return path;

	}

	private static void verifyConfigProperties () {
		try {
			// HashMap mp;
			java.lang.String s = "***REPLACE_ME***";
			java.lang.String msg = "Please Set The Properties To Valid Values In config.properties File Before Running jdec.\n";
			msg += "You Can also pass parameters to the Main class(ConsoleLauncher) instead of setting in config.properties File...\n";

			System.out.println ("verifyConfigProperties - stage 1");
			if (Configuration.getOutputMode () != null && Configuration.getOutputMode ().equals ("file")) {
				java.lang.String outputpath = Configuration.getOutputFolderPath ();
				if (outputpath == null || outputpath.trim ().equals (s)) { throw new InvalidInputException (msg
						+ "(INVALID PARAMETER IS Output_Folder_Path)"); }
			}
			System.out.println ("verifyConfigProperties - stage 2");
			if (Configuration.getLogMode () != null && Configuration.getLogMode ().equals ("file")) {
				java.lang.String logpath = Configuration.getLogFilePath ();
				if (logpath == null || logpath.trim ().equals (s)) { throw new InvalidInputException (msg + "(INVALID PARAMETER IS Log_File_Path)"); }
			}
			System.out.println ("verifyConfigProperties - stage 3");
			if (Configuration.getDecompileroption () != null && Configuration.getDecompileroption ().equals ("jar") == false) {
				java.lang.String file = Configuration.getJavaClassFile ();
				if (file == null || file.trim ().equals (s)) { throw new InvalidInputException (msg + "(INVALID PARAMETER IS JAVA_CLASS_FILE)"); }
			}
			System.out.println ("verifyConfigProperties - stage 4");
			if (Configuration.getDecompileroption () != null && Configuration.getDecompileroption ().equals ("jar") == true) {
				java.lang.String file = Configuration.getJarPath ();
				if (file == null || file.trim ().equals (s)) { throw new InvalidInputException (msg + "(INVALID PARAMETER IS JAR_FILE_PATH)"); }
			}

			System.out.println ("verifyConfigProperties - stage 5");
			if (Configuration.getDecompileroption () != null && Configuration.getDecompileroption ().equals ("jar") == true) {
				java.lang.String file = Configuration.getTempDir ();
				if (file == null || file.trim ().equals (s)) { throw new InvalidInputException (msg + "(INVALID PARAMETER IS Temp_Dir)"); }
			}

			System.out.println ("verifyConfigProperties - stage 5a");
			System.out.println ("outputFolder :" + Configuration.getOutputFolderPath () + ":");
			File f = new File (Configuration.getOutputFolderPath ());
			if (f.exists () == false) { throw new InvalidInputException (
					"Output Folder Path Does Not Exist...Please Create This Path For Jdec to proceed.\n"); }
			System.out.println ("verifyConfigProperties - stage 6");
			if (Configuration.getDecompileroption () != null && Configuration.getDecompileroption ().equals ("jar")) {
				f = new File (Configuration.getTempDir ());
				if (f.exists () == false) { throw new InvalidInputException (
						"Temp Dir Path Does Not Exist...Please Create This Path For Jdec to proceed.\n"); }
			}
			System.out.println ("verifyConfigProperties - stage 10");

		}
		catch (InvalidInputException ie) {
			System.out.println ("verifyConfigProperties - stage 11");
			AllExceptionHandler handler = new AllExceptionHandler ("\n\n" + "[ERROR :]" + ie.getMessage ());
			handler.sendMessage ();
			System.out.println ("[ERROR] Please check the input settings to Jdec again");
			System.out.println ("Please check the log output for more details");
			System.exit (1);
		}
		System.out.println ("verifyConfigProperties - stage 100");
	}

	private static boolean addLicence = true;

	public static void decompileClassFromUI (java.lang.String path) {
		try {

			mainRoot = null;
			couldNotFinish = new ArrayList ();
			addLicence = true;
			tabChange.add (0, getCurrentSourceFile ());
			classMethodMap = new HashMap ();
			classMethodRefMap = new HashMap ();
			InnerClassTracker.reinitializeStaticMembers ();
			allClassStructures = new ArrayList ();
			decompileClass (path);
			currentDepthLevel = 0;
			setResultFilePath (Configuration.getOutputFolderPath () + File.separator + getClassNameWithExtension ());
			decompileInnerClasses (); // TODO Also Fix For The UI side also
			// later
			// Now need to concatenate inner class content here
			Configuration.setClassFilePath (mainRoot.getClassPath ());
			java.lang.String importedclassesInInnerclasses = "";

			// if(!Configuration.getInlineInnerClassMethodContent().equalsIgnoreCase("true"))
			// {
			if (innerClassesDecompied != null && innerClassesDecompied.size () != 0) {
				java.lang.String innerClassFileContents = "";
				for (int s = 0; s < innerClassesDecompied.size (); s++) {
					java.lang.String classfilepath = (java.lang.String) innerClassesDecompied.get (s);
					try {
						java.lang.String currentFileContent = "";
						java.lang.String cname = getClassName (classfilepath);
						if (cname.indexOf (".") != -1) {
							cname = cname.substring (0, cname.indexOf ("."));
							cname = cname + "." + Configuration.getFileExtension ();
							classfilepath = Configuration.getOutputFolderPath () + File.separator + cname;
						}
						else {
							cname = cname + "." + Configuration.getFileExtension ();
							classfilepath = Configuration.getOutputFolderPath () + File.separator + cname;
						}
						File temp = new File (classfilepath);
						FileInputStream fis = new FileInputStream (temp);
						BufferedInputStream bis = new BufferedInputStream (fis);
						BufferedReader br = new BufferedReader (new InputStreamReader (bis));
						java.lang.String line = null;
						while ((line = br.readLine ()) != null) {
							currentFileContent += line + "\n";
						}
						if (currentFileContent.trim ().length () != 0) {
							// TODO: Remove Imports and store elsewhere
							int importend = currentFileContent.indexOf ("// End of Import");
							importedclassesInInnerclasses = "";
							if (importend != -1) {
								java.lang.String p = currentFileContent.substring (0, importend);
								currentFileContent = currentFileContent.substring (importend + "// End of Import".length ());
								importedclassesInInnerclasses += p;
							}
							innerClassFileContents += currentFileContent;
							innerClassFileContents += "\n\n//End Of a Inner Class File Content...\n\n";
						}

						temp = null;
						fis = null;
						bis = null;
						br = null;
						System.gc ();

					}
					catch (IOException ioe) {

					}
				}
				if (innerClassFileContents.trim ().length () != 0) {
					innerClassFileContents = checkForLicenceAndPackageInInnerClassContent (innerClassFileContents);
					java.lang.String mainFileContent = "";
					java.lang.String cname = getClassName (mainRoot.getClassPath ());
					java.lang.String mainfilepath = "";
					if (cname.indexOf (".") != -1) {
						cname = cname.substring (0, cname.indexOf ("."));
						cname = cname + "." + Configuration.getFileExtension ();
						mainfilepath = Configuration.getOutputFolderPath () + File.separator + cname;
					}
					else {
						cname = cname + "." + Configuration.getFileExtension ();
						mainfilepath = Configuration.getOutputFolderPath () + File.separator + cname;
					}
					File mainfile = new File (mainfilepath);
					FileInputStream fis = new FileInputStream (mainfile);
					BufferedInputStream bis = new BufferedInputStream (fis);
					BufferedReader br = new BufferedReader (new InputStreamReader (bis));
					java.lang.String line = null;
					while ((line = br.readLine ()) != null) {
						mainFileContent += line + "\n";
					}

					java.lang.String completeContent = mainFileContent;
					completeContent += "\n\n//Beginning of Inner Class Content...\n\n ";
					completeContent += innerClassFileContents;

					// Write back to Main File
					if (mainfile.exists ()) {
						mainfile.delete ();
					}

					mainfile = new File (mainfilepath);
					mainfile.createNewFile ();
					try {
						FileOutputStream fos = new FileOutputStream (mainfile);
						BufferedWriter bw = new BufferedWriter (new OutputStreamWriter (fos));
						importedclassesInInnerclasses = importedclassesInInnerclasses.replaceAll (
								"/\\*\\*\\*\\* List of All Imported Classes \\*\\*\\*/", "");
						importedclassesInInnerclasses = importedclassesInInnerclasses.replaceAll ("\n\n", "\n");
						completeContent = completeContent.replaceAll ("/\\*\\*\\*\\* List of All Imported Classes \\*\\*\\*/", "");
						Iterator classNames = getClazzObjects ().keySet ().iterator ();
						java.lang.String classname = "";
						while (classNames.hasNext ()) {
							classname = (java.lang.String) classNames.next ();
							break;
						}

						/*
						 * if(classname.indexOf(".")!=-1) {
						 * java.lang.String
						 * pkg=classname.substring(0,classname.lastIndexOf("."));
						 * //bw.write("package "+pkg+";\n\n"); }
						 */
						completeContent = completeContent.replaceAll ("this\\$", "This\\$");
						bw.write (completeContent);
						// bw.write("/**** List of All Imported Classes ***/");
						completeContent = completeContent.replaceAll ("\n\n", "\n");
						if (importedclassesInInnerclasses.endsWith ("\n")) {
							int n = importedclassesInInnerclasses.lastIndexOf ("\n");
							if (n != -1) {
								importedclassesInInnerclasses = importedclassesInInnerclasses.substring (0, n);
							}

						}
						// bw.write(importedclassesInInnerclasses);
						completeContent = checkForImportDuplicates (importedclassesInInnerclasses, completeContent);

						int present = completeContent.indexOf (localvariablenote);

						completeContent = completeContent.replaceAll (localVarNote, "");

						bw.flush ();
						bw.close ();
					}
					catch (IOException ioe) {

					}

				}

			}

			tabChange.add (0, getCurrentSourceFile ());

		}
		catch (Exception exp) {
			AllExceptionHandler handler = new AllExceptionHandler (exp);
			handler.reportException ();
		}
		testPrintClassStructure ();
	}

	private static java.lang.String getClassNameWithExtension () {
		String classpath = Configuration.getClassPath ();
		String className = "";
		int dot = classpath.lastIndexOf (".");
		int start = dot - 1;
		if (dot != -1) {
			boolean ok = false;
			while (start >= 0) {
				char ch = classpath.charAt (start);
				if (ch == '\\' || ch == '/') {
					ok = true;
					break;
				}
				start--;

			}
			if (ok) {

				className = classpath.substring (start + 1);
				dot = className.lastIndexOf (".");
				if (dot != -1) className = className.substring (0, dot).concat ("." + Configuration.getFileExtension ());
			}

		}
		return className;
	}

	private static java.lang.String checkForLicenceAndPackageInInnerClassContent (java.lang.String innerClassFileContents) {
		java.lang.String temp = innerClassFileContents;
		// java.lang.String licence=getLicenceWarning();
		// temp=temp.replaceAll(licence,"");
		int i = temp.indexOf ("package");
		while (i != -1) {
			int semi = temp.indexOf (";", i);
			if (semi != -1) {
				java.lang.String str = temp.substring (i, semi + 1);
				temp = temp.replaceAll (str, "");
			}
			else {
				break;
			}
			i = temp.indexOf ("package");

		}
		return temp;
	}

	public static ArrayList getTabChangeList () {
		return tabChange;
	}

	private static boolean shouldInnerClassBeDecompiled (java.lang.String cp) {

		for (int z = 0; z < innerClassesDecompied.size (); z++) {
			if (((java.lang.String) innerClassesDecompied.get (z)).equals (cp)) { return true; }
		}
		return false;

	}

	public static void showGeneralInformation (String path) {

		try {
			InputStream readerStream = VerifyClassFile (path);
			processClassFile (readerStream);
		}
		catch (MalFormedClassException mfe) {
			AllExceptionHandler handler = new AllExceptionHandler (mfe);
			handler.reportException ();
			JFrame mainFrame = UILauncher.getMainFrame ();
			if (mainFrame != null) {
				Manager.getManager ().setShowProgressBar (false);
				java.lang.String msg = "Invalid Class Specified As Input To Jdec..\n";
				msg += mfe.getMessage () + "\n";
				JOptionPane.showMessageDialog (UILauncher.getMainFrame (), msg, "Run Jdec Decompiler", JOptionPane.ERROR_MESSAGE);

			}

		}
		catch (Exception e) {
			AllExceptionHandler handler = new AllExceptionHandler (e);
			handler.reportException ();

		}

	}

	public static boolean isDontpvtmethods () {
		return dontpvtmethods;
	}

	public static void setDontpvtmethods (boolean dontpvtmethods) {
		ConsoleLauncher.dontpvtmethods = dontpvtmethods;
	}

	public static boolean isDontstaticinit () {
		return dontstaticinit;
	}

	public static void setDontstaticinit (boolean dontstaticinit) {
		ConsoleLauncher.dontstaticinit = dontstaticinit;
	}

	public static boolean isDontsynth () {
		return dontsynth;
	}

	public static void setDontsynth (boolean dontsynth) {
		ConsoleLauncher.dontsynth = dontsynth;
	}

	public static boolean isDontshowemptyconst () {
		return dontshowemptyconst;
	}

	public static void setDontshowemptyconst (boolean dontshowemptyconst) {
		ConsoleLauncher.dontshowemptyconst = dontshowemptyconst;
	}

	public static boolean isDontshownative () {
		return dontshownative;
	}

	public static void setDontshownative (boolean dontshownative) {
		ConsoleLauncher.dontshownative = dontshownative;
	}

	public static boolean isDontshowabs () {
		return dontshowabs;
	}

	public static void setDontshowabs (boolean dontshowabs) {
		ConsoleLauncher.dontshowabs = dontshowabs;
	}

	public static boolean isShowconstfirst () {
		return showconstfirst;
	}

	public static void setShowconstfirst (boolean showconstfirst) {
		ConsoleLauncher.showconstfirst = showconstfirst;
	}

	public static void setShowfieldsfirst (boolean b) {
		showfieldsfirst = b;
	}

	public static void setShowObjectSuperClass (boolean b) {
		showextendsobject = b;
	}

	public static ArrayList getInclList () {
		return inclList;
	}

	public static ArrayList getExclList () {
		return exclList;
	}

	public static void setInclListInJar (ArrayList inclList) {
		ConsoleLauncher.inclList = inclList;
	}

	public static void setExclImpList (ArrayList list) {
		exclList = list;
	}

	private static boolean skipBehaviour (Behaviour b) {
		boolean bl = false;
		if (dontpvtmethods) {
			java.lang.String accs = b.getUserFriendlyMethodAccessors ();
			if (accs.indexOf ("private") != -1) { return true; }
		}
		if (dontstaticinit) {
			if (b.getBehaviourName ().equals ("static")) { return true; }
		}
		if (dontsynth) {
			if (b.getBehaviourName ().indexOf ("$") != -1) { return true; }
		}
		if (dontshowabs) {
			java.lang.String accs = b.getUserFriendlyMethodAccessors ();
			if (accs.indexOf ("abstract") != -1) { return true; }
		}
		if (dontshownative) {
			java.lang.String accs = b.getUserFriendlyMethodAccessors ();
			if (accs.indexOf ("native") != -1) { return true; }
		}
		if (dontshowemptyconst) {
			if (b instanceof ConstructorMember) {
				byte[] c = b.getCode ();
				if (c == null || c.length == 5) { return true; }

			}
		}

		return bl;
	}

	private static boolean skipClazzDecompilation (java.lang.String pkg) {
		if (inclList == null || inclList.size () == 0) { return false; }
		for (int z = 0; z < inclList.size (); z++) {
			java.lang.String cpkg = (java.lang.String) inclList.get (z);
			if (cpkg.equals (pkg)) return false;
		}
		return true;

	}

	private static boolean addimportstmt (java.lang.String fullname) {

		if (exclList == null || exclList.size () == 0) return true;
		for (int z = 0; z < exclList.size (); z++) {
			String s1 = (String) exclList.get (z);
			int lastDot = fullname.lastIndexOf (".");
			if (lastDot != -1) {
				String str = fullname.substring (0, lastDot);
				if (fullname != null && str.equals (s1) == true) { return false; }
			}

		}

		return true;
	}

	public static void currentClassMethods (ArrayList mlist) {
		currentClassMethods = mlist;
	}

	public static ArrayList getcurrentClassMethods () {
		return currentClassMethods;

	}

	/*
	 * public static void processClassInJar(File jar,String clx) { try { String
	 * path=getFilePathForClassEntryInJar(jar,clx); if(path!=null)
	 * decompileClass(path); } catch(Throwable e) { AllExceptionHandler h=new
	 * AllExceptionHandler(e); h.reportException(); fatalErrorOccured=true;
	 * Manager.getManager().setShowProgressBar(false); } }
	 */

	// folder can be null
	// if not null a new folder with specified name is created if does not exist
	public static String getFilePathForClassEntryInJar (File jar, String clx, String folder) {
		try {

			if (jar.exists () == false) {
				JOptionPane.showMessageDialog (UILauncher.getMainFrame (), "Jar File does not exist");
				Manager.getManager ().setShowProgressBar (false);
				throw new Throwable ();
			}
			JarFile jf = new JarFile (jar);
			JarEntry je = jf.getJarEntry (clx);
			if (je == null) {
				JOptionPane.showMessageDialog (UILauncher.getMainFrame (), "Class does not exist within jar");
				Manager.getManager ().setShowProgressBar (false);
				throw new Throwable ();
			}
			String name = je.getName ();
			String outputf = Configuration.getOutputFolderPath ();
			if (outputf == null || outputf.trim ().length () == 0) {
				JOptionPane.showMessageDialog (UILauncher.getMainFrame (), "Please set output folder path");
				Manager.getManager ().setShowProgressBar (false);
				throw new Throwable ();
			}

			InputStream is = jf.getInputStream (je);
			FileOutputStream fos = null;
			String JarDir = Configuration.getTempDir ();
			File f = new File (JarDir);
			if (f.exists () == false) {
				f = new File (System.getProperty ("user.dir"));
				JarDir = System.getProperty ("user.dir");
			}
			if (folder != null) {
				if (JarDir.endsWith (File.separator) == false)
					JarDir = JarDir + File.separator + folder;
				else
					JarDir = JarDir + folder;
			}

			if (new File (JarDir).exists () == false) {
				new File (JarDir).mkdirs ();
			}
			int slash = name.lastIndexOf ("/");
			File tempFile = null;
			if (slash != -1) {
				tempFile = new File (JarDir + name.substring (slash));
				tempFile.delete ();
				fos = new FileOutputStream (tempFile);
			}
			else {
				tempFile = new File (JarDir + File.separator + name);
				tempFile.delete ();
				fos = new FileOutputStream (tempFile);
			}
			int i = is.read ();
			while (i != -1) {
				fos.write (i);
				i = is.read ();
			}
			fos.flush ();
			fos.close ();
			is.close ();
			fos = null;
			is = null;

			int i2 = name.lastIndexOf ("/");
			if (i2 != -1) {
				name = name.substring (i2 + 1);
			}
			String path = JarDir + File.separator + name;
			Writer w = Writer.getWriter ("log");
			w.writeLog ("For Task Class in Jar Task");
			w.writeLog ("Writing class bytes to File " + path);
			w.flush ();
			return JarDir + File.separator + name;
		}
		catch (Throwable e) {
			AllExceptionHandler h = new AllExceptionHandler (e);
			h.reportException ();
			fatalErrorOccured = true;
			Manager.getManager ().setShowProgressBar (false);
			return null;
		}

	}

	public static HashMap getMethod_names_methodref () {
		return method_names_methodref;
	}

	public static boolean getShowextendsobject () {
		return showextendsobject;
	}

	public static boolean getShowfieldsfirst () {
		return showfieldsfirst;
	}

	public static ClassStructure getClassStructure (String string) {
		for (int x = 0; x < allClassStructures.size (); x++) {
			ClassStructure cur = (ClassStructure) allClassStructures.get (x);
			if (cur.getName ().equals (string)) { return cur; }
		}
		return null;
	}

	/*
	 * public static ClassStructure getClassStructureForMethodName(String
	 * string) { for(int x=0;x<allClassStructures.size();x++){ ClassStructure
	 * cur=(ClassStructure)allClassStructures.get(x); ArrayList
	 * methods=cur.getMethods(); for(int z=0;z<methods.size();z++){ String
	 * m=(String)methods.get(z); if(m.equals(string)){ } } } }
	 */
	private static void testPrintClassStructure () {

		for (int x = 0; x < allClassStructures.size (); x++) {
			ClassStructure cs = (ClassStructure) allClassStructures.get (x);
			String s = "s";
		}

	}

	private static boolean addClassStructure (String name) {
		for (int x = 0; x < allClassStructures.size (); x++) {
			ClassStructure cs = (ClassStructure) allClassStructures.get (x);
			if (cs.getName ().equals (name)) { return false; }
		}
		return true;
	}

	public static void decompileJarFromConsole (java.lang.String jarPath, ArrayList classes) {
		try {
			if (UILauncher.getMainFrame () == null) {
				InnerClassTracker.reinitializeStaticMembers ();
				allClassStructures = new ArrayList ();
				currentIsInner = false;
				classMethodMap = new HashMap ();
				classMethodRefMap = new HashMap ();
			}
			ArrayList jarfiles = classes;
			if (jarfiles.size () > 0) {
				// String classContent = "";
				java.lang.String orgpath = Configuration.getBkpoppath ();
				for (int start = 0; start < jarfiles.size (); start++) {
					mainRoot = null;
					File currentFile = (File) jarfiles.get (start);
					System.out.println ("Decompiling class ..." + currentFile.getAbsolutePath ());
					Configuration.setClassFilePath (currentFile.getAbsolutePath ());
					couldNotFinish = new ArrayList ();
					addLicence = true;
					try {
						decompileClass (currentFile.getAbsolutePath ());
						if (UILauncher.getMainFrame () == null) {
							InnerClassTracker.reinitializeStaticMembers ();
							allClassStructures = new ArrayList ();
						}
					}
					catch (RuntimeException rxe) {
						Writer w = Writer.getWriter ("log");
						rxe.printStackTrace (w);
						reInitializeConstantPoolEntries ();
						continue;
					}

					catch (Exception exp) {
						AllExceptionHandler hl = new AllExceptionHandler (exp, "Could Not decompile Class " + currentFile.getAbsolutePath ());
						hl.reportException ();
						reInitializeConstantPoolEntries ();
						continue;
					}

					currentDepthLevel = 0;
					decompileInnerClasses (); // TODO
					if (mainRoot != null) Configuration.setClassFilePath (mainRoot.getClassPath ());
					java.lang.String importedclassesInInnerclasses = "";

					if (innerClassesDecompied != null && innerClassesDecompied.size () != 0) {
						java.lang.String innerClassFileContents = "";
						for (int s = 0; s < innerClassesDecompied.size (); s++) {
							java.lang.String classfilepath = (java.lang.String) innerClassesDecompied.get (s);
							File temp;
							FileInputStream fis;
							BufferedInputStream bis;
							BufferedReader br;
							try {
								java.lang.String currentFileContent = "";
								java.lang.String cname = getClassName (classfilepath);
								if (cname.indexOf (".") != -1) {
									cname = cname.substring (0, cname.indexOf ("."));
									cname = cname + "." + Configuration.getFileExtension ();
									classfilepath = Configuration.getOutputFolderPath () + File.separator + cname;
								}
								else {
									cname = cname + "." + Configuration.getFileExtension ();
									classfilepath = Configuration.getOutputFolderPath () + File.separator + cname;
								}
								temp = new File (classfilepath);
								fis = new FileInputStream (temp);
								bis = new BufferedInputStream (fis);
								br = new BufferedReader (new InputStreamReader (bis));
								java.lang.String line = null;
								while ((line = br.readLine ()) != null) {
									currentFileContent += line + "\n";
								}
								if (currentFileContent.trim ().length () != 0) {

									int importend = currentFileContent.indexOf ("// End of Import");
									importedclassesInInnerclasses = "";
									if (importend != -1) {
										java.lang.String p = currentFileContent.substring (0, importend);
										currentFileContent = currentFileContent.substring (importend + "// End of Import".length ());
										importedclassesInInnerclasses += p;
									}
									innerClassFileContents += currentFileContent;
									innerClassFileContents += "\n\n//End Of a Inner Class File Content...\n\n";
								}

							}
							catch (IOException ioe) {

							}
							finally {
								temp = null;
								fis = null;
								bis = null;
								br = null;
								System.gc ();
							}
						}
						if (innerClassFileContents.trim ().length () != 0) {
							innerClassFileContents = checkForLicenceAndPackageInInnerClassContent (innerClassFileContents);
							java.lang.String mainFileContent = "";
							java.lang.String cname = getClassName (mainRoot.getClassPath ());
							java.lang.String mainfilepath = "";
							if (cname.indexOf (".") != -1) {
								cname = cname.substring (0, cname.indexOf ("."));
								cname = cname + "." + Configuration.getFileExtension ();
								mainfilepath = Configuration.getOutputFolderPath () + File.separator + cname;
							}
							else {
								cname = cname + "." + Configuration.getFileExtension ();
								mainfilepath = Configuration.getOutputFolderPath () + File.separator + cname;
							}
							File mainfile = new File (mainfilepath);
							FileInputStream fis = new FileInputStream (mainfile);
							BufferedInputStream bis = new BufferedInputStream (fis);
							BufferedReader br = new BufferedReader (new InputStreamReader (bis));
							java.lang.String line = null;
							while ((line = br.readLine ()) != null) {
								mainFileContent += line + "\n";
							}

							java.lang.String completeContent = mainFileContent;
							completeContent += "\n\n//Beginning of Inner Class Content...\n\n ";
							completeContent += innerClassFileContents;

							// Write back to Main File
							if (mainfile.exists ()) {
								mainfile.delete ();
							}

							mainfile = new File (mainfilepath);
							mainfile.createNewFile ();
							try {
								FileOutputStream fos = new FileOutputStream (mainfile);
								BufferedWriter bw = new BufferedWriter (new OutputStreamWriter (fos));
								importedclassesInInnerclasses = importedclassesInInnerclasses.replaceAll (
										"/\\*\\*\\*\\* List of All Imported Classes \\*\\*\\*/", "");
								importedclassesInInnerclasses = importedclassesInInnerclasses.replaceAll ("\n\n", "\n");
								completeContent = completeContent.replaceAll ("/\\*\\*\\*\\* List of All Imported Classes \\*\\*\\*/", "");
								Iterator classNames = getClazzObjects ().keySet ().iterator ();
								java.lang.String classname = "";
								while (classNames.hasNext ()) {
									classname = (java.lang.String) classNames.next ();
									break;
								}

								bw.write (completeContent);
								// bw.write("/**** List of All Imported Classes
								// ***/");
								completeContent = completeContent.replaceAll ("\n\n", "\n");
								if (importedclassesInInnerclasses.endsWith ("\n")) {
									int n = importedclassesInInnerclasses.lastIndexOf ("\n");
									if (n != -1) {
										importedclassesInInnerclasses = importedclassesInInnerclasses.substring (0, n);
									}

								}
								// bw.write(importedclassesInInnerclasses);
								completeContent = checkForImportDuplicates (importedclassesInInnerclasses, completeContent);

								int present = completeContent.indexOf (localvariablenote);

								completeContent = completeContent.replaceAll (localVarNote, "");

								bw.flush ();
								bw.close ();
							}
							catch (IOException ioe) {

							}

						}

					}

					// Restore orig classpath when over
					System.out.println ("Decompiled class ..." + currentFile.getAbsolutePath () + "\n");
					reInitializeConstantPoolEntries ();
				}

				// Repaint the Jar Tab again....
				java.lang.String jarDir = Configuration.getBkpoppath ();
				File temp = new File (jarDir);
				if (temp.exists ()) {
					File jd = new File (Configuration.getBkpoppath () + File.separator + "JARDECOMPILED");
					if (jd.exists ()) {
						jd.delete ();
					}
					if (jd.exists ()) {
						jd.deleteOnExit ();
					}

				}
				// /

			}

		}
		catch (FileNotFoundException fne) {
			AllExceptionHandler handler = new AllExceptionHandler (fne);
			handler.reportException ();
		}
		catch (IOException io) {
			AllExceptionHandler handler = new AllExceptionHandler (io);
			handler.reportException ();
		}

	}

}
