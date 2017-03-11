  
/*
 * UIUtil.java Copyright (c) 2006,07 Swaroop Belur
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import net.sf.jdec.config.Configuration;
import net.sf.jdec.format.Settings;
import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.ui.adapter.DecompilerBridge;
import net.sf.jdec.ui.core.Console;
import net.sf.jdec.ui.core.FileInfoFrame;
import net.sf.jdec.ui.core.JdecTree;
import net.sf.jdec.ui.core.Manager;
import net.sf.jdec.ui.core.OutputFrame;
import net.sf.jdec.ui.main.UILauncher;
import net.sf.jdec.util.AllExceptionHandler;
import net.sf.jdec.util.Util;


public class UIUtil {
    
    public static String jdecFolder="";
    
    public static boolean codeReformatted = false;
    public String getMode() {
        return mode;
    }
    
    public String getOutputFilePath() {
        return outputFilePath;
    }
    
    public String getLogMode() {
        return logMode;
    }
    
    public String getLogPath() {
        return logPath;
    }
    
    public String getLogLevel() {
        return logLevel;
    }
    
    public String getJavaClassFile() {
        return javaClassFile;
    }
    
    public String getJarFilePath() {
        return jarFilePath;
    }
    
    public String getJdecOption() {
        return JDecOption;
    }
    
    public String getShowImport() {
        return showImport;
    }
    
    public java.lang.String mode;
    
    public String getExtension() {
        return extension;
    }
    
    public void setExtension(String extension) {
        this.extension = extension;
    }
    
    public String getInterpret() {
        return interpret;
    }
    
    public void setInterpret(String interpret) {
        this.interpret = interpret;
    }
    
    private java.lang.String extension="jdec";
    private java.lang.String outputFilePath=System.getProperty("user.dir");
    private java.lang.String logMode="file";
    private java.lang.String logPath=System.getProperty("user.dir")+File.separator+"jdeclog.txt";
    private java.lang.String logLevel="1";
    private java.lang.String javaClassFile;
    private java.lang.String jarFilePath;
    private java.lang.String JDecOption="dc";
    private java.lang.String showImport="true";
    private java.lang.String projectTempDir="";
    
    public String getUilogfile() {
        return uilogfile;
    }
    
    public void setUilogfile(String uilogfile) {
        this.uilogfile = uilogfile;
    }
    
    private java.lang.String uilogfile=System.getProperty("user.dir")+File.separator+"jdecuilog.txt";;
    private java.lang.String inlineAnonymous="false";
    private java.lang.String innerDepth="1";
    private java.lang.String interpret="false";
    private static HashMap versionlines=new HashMap();
    
    public static HashMap getVersionlines() {
        return versionlines;
    }
    
    
    public static void setLinesForVersion(String v,ArrayList lines) {
        versionlines.put(v,lines);
        
    }
    private UIUtil() {
    }
    public static UIUtil getUIUtil() {
        if(ref==null) {
            ref=new UIUtil();
            return ref;
        } else
            return ref;
    }
    private static UIUtil ref=null;
    
    public void reLoadDecompilerConfigParams() {
                /*ResourceBundle rb=ResourceBundle.getBundle("tempconfig");
                 readAllParameters(rb);*/
        HashMap values=new HashMap();
        BufferedReader br=null;
        try {
            br=new BufferedReader(new InputStreamReader(new FileInputStream(System.getProperty("user.home")+File.separator+"tempconfig.properties")));
            String s=br.readLine();
            while(s!=null) {
                if(s.trim().indexOf("#")!=-1) {
                    s=br.readLine();
                } else {
                    String str=s;
                    if(str.trim().length()==0) {
                        s=br.readLine();
                    } else {
                        int eq=str.indexOf("=");
                        if(eq!=-1) {
                            String key=str.substring(0,eq);
                            String val=str.substring(eq+1);
                            
                            values.put(key,val);
                            s=br.readLine();
                        } else {
                            s=br.readLine();
                        }
                    }
                }
                
                
            }
        } catch(IOException ioe) {
            try {
                
                
                ioe.printStackTrace();
                
                
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        Iterator it=values.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry entry=(Map.Entry)it.next();
            String k=(String)entry.getKey();
            String v=(String)entry.getValue();
            
            if(k.equals("Output_Mode")) {
                mode=v;
            }
            if(k.equals("Log_Mode")) {
                logMode=v;
            }
            if(k.equals("Output_Folder_Path")) {
                outputFilePath=v;
                
            }
            
            if(k.equals("Output_File_Extension")) {
                extension=v;
            }
            if(k.equals("LOG_LEVEL")) {
                logLevel=v;
            }
            if(k.equals("Log_File_Path")) {
                logPath=v;
            }
            if(k.equals("UI_LOG_FILE_PATH")) {
                uilogfile=v;
                
            }
            if(k.equals("JAVA_CLASS_FILE")) {
                javaClassFile=v;
            }
            if(k.equals("JAR_FILE_PATH")) {
                jarFilePath=v;
            }
            if(k.equals("Show_Imports")) {
                showImport=v;
            }
            if(k.equals("JDec_Option")) {
                JDecOption= v;
            }
            
            if(k.equals("Temp_Dir")) {
                projectTempDir=v;
            }
            if(k.equals("Inline_Anonymous_Inner_Class_Content")) {
                inlineAnonymous=v;
            }
            if(k.equals("Inner_Depth")) {
                innerDepth=v;
            }
            
            if(k.equals("Interpret_Exception_Table")) {
                interpret=v;
            }
            
            if(k.equals("Interpret_Non_ASCII")) {
                interpretNonAscii=v;
            }
            
            
            
            if(k.equals("Force_Non_ASCII")) {
                forceNonAscii=v;
            }
            
            if(k.equals("Skip_Class_Version_Check")) {
                skipClassVersionCheck=v;
            }
            
            if(k.equals("Formatting_Show_Flower_Bracket_at_same_line")){
            	showFlowerBracketAtSameLine = v;
            	try{
            		Settings.setFlowerBracketAtSameLine(new Boolean(v.trim()).booleanValue());
            	}
            	catch(Exception e){}
            }
            
         
            
        }
        
        //System.out.println("javaClassFile"+javaClassFile);
        Configuration.setJavaClassFile(javaClassFile);
        Configuration.setJarPath(jarFilePath);
        DecompilerBridge bridge=DecompilerBridge.getInstance(this);
        bridge.setOption(JDecOption);
        
        
        
        String op=getOutputFilePath();
        File f=new File(op);
        
        String jaroutput=getOutputFilePath()+File.separator+"JARDECOMPILED";
        f=new File(jaroutput);
        if(f.exists()){
            
        } else {
            f.mkdirs();
        }
        
        try {
            if(br!=null) {
                br.close();
            }
            
        } catch(IOException ie){} finally {
            br=null;
        }
        
        
    }
    
    
    public void registerDecompilerConfigParams() {
        try {
            ResourceBundle rb=ResourceBundle.getBundle("config");
            readAllParameters(rb);
            //verifyConfigProperties();
            String op=getOutputFilePath();
            File f=new File(op);
            if(f.exists()){
                
            }
           
        } catch(Exception e) {
            AllExceptionHandler handler=new AllExceptionHandler(e);
            handler.reportException();
        }
    }
    
    
    private boolean showConfigWindowOnStartUp=false;
    
    
    
    public boolean showConfigWindowOnStartUp() {
      return showConfigWindowOnStartUp;
    }

    public void setShowConfigWindowOnStartUp(boolean showConfigWindowOnStartUp) {
      this.showConfigWindowOnStartUp = showConfigWindowOnStartUp;
    }

    private void readAllParameters(ResourceBundle rb) {
        
        String temp=null;
        temp=rb.getString("Output_Mode");
        if(temp!=null){
          setMode(temp.trim());
        }
        
        temp=rb.getString("Output_Folder_Path");
        if (temp != null) 
        {
          if (temp.indexOf("REPLACEME") != -1)
          {
            setShowConfigWindowOnStartUp(true);
          }
          
            setOutputFilePath(temp.trim());
            Configuration.backupOriginalOutputFilePath(temp.trim());
          
        }
        else 
        {
          setShowConfigWindowOnStartUp(true);
         }
        
        
        temp=rb.getString("Output_File_Extension");
        if(temp!=null){
          setExtension(temp.trim());
        }
        temp=rb.getString("Log_Mode");
        if(temp!=null){
          setLogMode(temp.trim());
        }
            
        temp=rb.getString("Log_File_Path");
        if(temp!=null){
          if(temp.indexOf("REPLACEME")!=-1){
            setShowConfigWindowOnStartUp(true);
           }
            setLogPath(temp.trim());
        }   
        else{
          setShowConfigWindowOnStartUp(true);
        }
        temp=rb.getString("LOG_LEVEL");
        if(temp!=null){
          setLogLevel(temp.trim());
        }
        temp=rb.getString("JAVA_CLASS_FILE");
        if(temp!=null){
          setJavaClassFile(temp.trim());
          
        }
        temp=rb.getString("JAR_FILE_PATH");
        if(temp!=null){
          setJarFilePath(temp.trim());
        }
        temp=rb.getString("JDec_Option");
        if(temp!=null){
          setJDecOption(temp.trim());
        }
        temp=rb.getString("Show_Imports");
        if(temp!=null){
          setShowImport(temp.trim());
        }
        temp=rb.getString("Temp_Dir");
        if(temp!=null){
          setProjectTempDir(temp.trim());
        }
        
        temp=rb.getString("UI_LOG_FILE_PATH");
        if(temp!=null){
          if(temp.indexOf("REPLACEME")!=-1){
            setShowConfigWindowOnStartUp(true);
          }
            setUilogfile(temp.trim());
        }
        else{
          
            setShowConfigWindowOnStartUp(true);
          
        }
        
        temp=rb.getString("Inline_Anonymous_Inner_Class_Content");
        if(temp!=null){
          setInlineAnonymous(temp.trim());
        }
        temp=rb.getString("Inner_Depth");
        if(temp!=null){
          setInnerDepth(temp.trim());
        }
        temp=rb.getString("Interpret_Exception_Table");
        if(temp!=null){
          setInterpret(temp.trim());
        }
        temp=rb.getString("Interpret_Non_ASCII");
        if(temp!=null){
          setInterpretNonAscii(temp.trim());
        }
        temp=rb.getString("Force_Non_ASCII");
        if(temp!=null){
          setForceNonAscii(temp.trim());
        }
        temp=rb.getString("Skip_Class_Version_Check");
        if(temp!=null){
          setSkipClassVersionCheck(temp.trim());
        }
        
        temp = rb.getString("Formatting_Show_Flower_Bracket_at_same_line");
        if(temp!=null){
        	setShowFlowerBracketAtSameLine(temp.trim());
        	try{
        		Settings.setFlowerBracketAtSameLine(new Boolean(temp.trim()).booleanValue());
        	}
        	catch(Exception e){}
        }
        if(logPath!=null && logPath.trim().length() > 0) {
            String s=logPath+File.separator+"console.txt";
            setConsoleDetailFile(s);
        }
        String s=getConsoleDetailFile();
        Configuration.setConsoleDetailFile(s);
        
        //System.out.println("javaClassFile"+javaClassFile);
        Configuration.setJavaClassFile(javaClassFile);
        Configuration.setJarPath(jarFilePath);
        DecompilerBridge bridge=DecompilerBridge.getInstance(this);
        bridge.setOption(JDecOption);
        
        Configuration.setSkipClassVersionCheck(skipClassVersionCheck);
        
        
    }
    public static volatile boolean  explosionInProgress=false;
    
    public static ArrayList registeredClasses=null;
    // TODO: FIXME by taking filters and archive settings into account
    public java.lang.String explodeJar(File f) {
            
         
        if(f==null || !f.exists()) return "";
        try {
            registeredClasses=new ArrayList();
            explosionInProgress=true;
            JarFile jarFile=new JarFile(f.getAbsolutePath());
            
            Enumeration e=jarFile.entries();
            if(projectTempDir==null || projectTempDir.trim().length() == 0)
                projectTempDir=System.getProperty("user.dir");
            
            String name=f.getName();
            if(name.indexOf(".")!=-1)name=name.substring(0,name.indexOf("."));
            File root=new File(projectTempDir+File.separator+name);
            if(root.exists()==false)root.mkdir();
            
            while(e.hasMoreElements() && continueToExplode) {
                
                ZipEntry entry=(ZipEntry)e.nextElement();
                File currentDir=null;
                
                
                if(entry.isDirectory()) {
                    currentDir=new File(root.getAbsolutePath()+File.separator+entry.getName());
                    if(!currentDir.exists())currentDir.mkdirs();
                    
                } else {
                    boolean b=checkFilterSetting(entry);
                    if(!b)continue;
                    if(currentDir==null){
                        
                        currentDir=root;
                    }
                    File currentFile=new File(currentDir+File.separator+entry.getName());
                    boolean write=false;
                    try {
                        //System.out.println("currentFile!!! "+currentFile.getAbsolutePath());
                        if(currentExplodeEntry!=null && archiveproframedetails!=null && archiveproframe!=null){
                            
                            archiveproframedetails.remove(currentExplodeEntry);
                            currentExplodeEntry.setText("Registering Entry "+ entry.getName());
                            //currentExplodeEntry.setBackground(Color.BLUE);
                            currentExplodeEntry.setForeground(Color.BLUE);
                            archiveproframedetails.add(currentExplodeEntry);
                            archiveproframedetails.revalidate();
                            archiveproframedetails.repaint();
                            archiveproframe.repaint();
                            
                        }
                        Thread.sleep(500);
                        currentFile.createNewFile();
                        write=true;
                        writeToFile(currentFile,entry,jarFile);
                        registeredClasses.add(currentFile);
                        write=false;
                    } catch(Exception e2) {
                        
                        String path=currentFile.getAbsolutePath();
                        int lastSlash=path.lastIndexOf(File.separator);
                        String s=path.substring(0,lastSlash);
                        File f3=new File(s);
                        if(!f3.exists())f3.mkdirs();
                        currentFile.createNewFile(); // Should not throw Exception
                        if(write)
                            writeToFile(currentFile,entry,jarFile);
                        
                    }
                    
                    
                }
                
                
                
            }
             if(currentExplodeEntry!=null && archiveproframedetails!=null){
                    explodeStatus.setText("Finished Scanning Jar");
                    currentExplodeEntry.setText("");
                    archiveproframedetails.revalidate();
                    archiveproframedetails.repaint();
                    archiveproframe.repaint();
                    if(archiveproframe!=null){
                        archiveproframe.setVisible(false);
                        archiveproframe.dispose();
                        archiveproframe=null;
                    }
                }
            return root.getAbsolutePath();
        } catch(Exception e) {
            
            AllExceptionHandler handler=new AllExceptionHandler(e);
            handler.reportException();
            JOptionPane.showMessageDialog(null,"Error while reading from archive file.\nPlease check the log file");
            return "";
            
        } finally {
            UIUtil.continueToExplode=false;
            explosionInProgress=false;
        }
        
    }
    private void writeToFile(File currentFile,ZipEntry entry,JarFile jarFile) {
        ArrayList bytes=new ArrayList();
        try {
            InputStream is=jarFile.getInputStream(entry);
            OutputStream os=new FileOutputStream(currentFile.getAbsolutePath());
            //DataOutputStream dos=new DataOutputStream(os);
            int x=is.read();
            while(x!=-1) {
                os.write(x);
                x=is.read();
            }
            os.flush();
            os.close();
            
                        
                        
            
        } catch(IOException ioe) {
            try {
                LogWriter lg=LogWriter.getInstance();
                lg.writeLog("[ERROR]: Method: writeToFile\n\tClass: UIUtil.class");
                lg.writeLog("------------------------------------------------");
                lg.writeLog("Exception Stack Trace");
                ioe.printStackTrace(lg.getPrintWriter());
                lg.flush();
            } catch(Exception e) {
//
            }
            
        }
        
    }
    
    
    public java.lang.String getProjectTempDir() {
        return projectTempDir;
    }
    
    
    public void loadSystemProperties() {
        Properties p=System.getProperties();
        Enumeration e=p.keys();
        while(e.hasMoreElements()) {
            Object k=e.nextElement();
            propNames.add(k);
            propValues.add(p.get(k));
        }
        SystemProperties props=new SystemProperties(this);
        Manager.getManager().setSystemPropRef(props);
    }
    public static void setUserProjFol(java.lang.String userProjFol) {
        UIUtil.userProjFol = userProjFol;
    }
    public static java.lang.String getUserProjFol() {
        return userProjFol;
    }
    private static java.lang.String userProjFol=null;
    
    private java.lang.String currentFile="";
    public void setCurrentOpenFile(java.lang.String str) {
        currentFile=str;
    }
    
    
    public ArrayList propNames=new ArrayList();
    public ArrayList propValues=new ArrayList();
    
    public java.lang.String getCurrentOpenFile() {
        return currentFile;
    }
    
    private ArrayList recentfiles=new ArrayList();
    public void addRecentFile(File f) {
        recentfiles.add(f);
        
    }
    public ArrayList getRecentFileList() {
        return recentfiles;
    }
    
    public void clearHistory() // Can Extend Later to clear  other user things as needed
    { 						   // Right now only recent file list
       
        recentfiles=new ArrayList();
    }
    
    
    // Used while opening recent File
    // Do not use Elsewhere
    public java.lang.String fileSelected=null;
    public java.lang.String fileSelectedPath=null;
    File javaFolder=null;
    public void setJavaFolderPath(File f) {
        javaFolder=f;
    }
    
    public String getJavaHomePath() {
        if(javaFolder!=null)
            return javaFolder.getAbsolutePath();
        else
            return null;
    }
    File browserPath=null;
    public void setBrowserPath(File f) {
        browserPath=f;
    }
    
    public  File getBrowserPath() {
        return browserPath;
    }
    
    public void setFileToBeCompiledOrRun(File f) {
        compileFile=f;
    }
    
    public File getFileToBeCompiled() {
        return compileFile;
    }
    private File compileFile=null;
    private java.lang.String  javacResFile=null;
    public void setJavacResultFile(java.lang.String file) {
        javacResFile=file;
    }
    public java.lang.String getJavacOutputFile() {
        return javacResFile;
    }
    
    private String decompilerPropertyFileComment="##############################################################################################################\n"+
        "#                     Jdec Property Descriptions                                                                  #\n"+
        "##############################################################################################################\n"+
        
        "#Output_Mode ---> Represents the kind of Destination The tool o/p is sent to. Valid values are file and console\n"+
        "#File_Path   ---> Represents the folder which will contain the output file(If the above mode is file)\n"+
        "#Log_Mode    ---> Similar to Output-Mode except it represnts log and not output\n"+
        "#Log_File_Path    ---> Represents the Full path of Log File to which log will be sent\n"+
        "#LOG_LEVEL ---> Decides the level of detail to which log is to be created. Valid value: 1 or 2; 2 is for higher log\n"+
        "#JAVA_CLASS_FILE -->Represents the input java class file which has to be worked on by this jdec\n"+
        "#JAR_FILE_PATH --> Represents the complete path to the jar the user wants to decompile\n"+
        "#JDec_Option --> Represents the Option to Run Jdec With\n"+
        
        "# [NOTE:] Valid Values For JDec_Option...\n"+
        "#  1>decompileClass\n"+
        "#  2>disassemble\n"+
        "#  3>decompileJar\n"+
        "#  4>ConstantPool\n"+
        "#  5>localVariables\n"+
        "#  6>Skeleton\n"+
        "#  7>help\n"+
        
        "# [NOTE:] Valid Values For Show_imports\n"+
        "#  1>true\n"+
        "#  2>false\n"+
        
        "IMPORTANT : Please do not use Windows Type Slash for updating values .Example use c:/test.class NOT c:\test.class \n"+
        "###############################################################################################################\n";
    
    public String getDecompilerPropertyFileComment() {
        return decompilerPropertyFileComment;
    }
    
    public void setDecompilerPropertyFileComment(
        String decompilerPropertyFileComment) {
        this.decompilerPropertyFileComment = decompilerPropertyFileComment;
    }
    
    private String gtkClass="com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
    private String windowsClass="com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
    private String metalClass="javax.swing.plaf.metal.MetalLookAndFeel";
    private String motifClass="com.sun.java.swing.plaf.motif.MotifLookAndFeel";
    
    public String getGtkClass() {
        return gtkClass;
    }
    
    public String getMetalClass() {
        return metalClass;
    }
    
    public String getMotifClass() {
        return motifClass;
    }
    
    public String getWindowsClass() {
        return windowsClass;
    }
    
    private String currentLNF="GTKLookAndFeel";
    
    
    public String getLookAndFeelClass(String s) {
        
        if(s.equals("GTKLookAndFeel")) {
            return getGtkClass();
        } else if(s.equals("WindowsLookAndFeel")) {
            return getWindowsClass();
        } else if(s.equals("MetalLookAndFeel")) {
            return getMetalClass();
        } else if(s.equals("MotifLookAndFeel")) {
            return getMotifClass();
        } else
            return getGtkClass();
        
        
    }
    
    public String getCurrentLNF() {
        return currentLNF;
    }
    
    public void setCurrentLNF(String currentLNF) {
        this.currentLNF = currentLNF;
    }
    
        /* public void performCleanUp(String s)
         {
         File f=new File(s);
         if(f.isDirectory())
         {
         File list[]=f.listFiles();
         for(int i=0;i<list.length;i++)
         {
         File temp=list[i];
         if(temp.isDirectory())
         performCleanUp(temp.getAbsolutePath());
         else
         temp.delete();
         }
         }
         else
         f.delete();
         }*/
    
    private java.lang.String consoleDetailFile=null;
    
    
    /**
     * @return Returns the consoleDetailFile.
     */
    public java.lang.String getConsoleDetailFile() {
        File f=new File(consoleDetailFile);
        try {
            if(!f.exists()) {
                f.createNewFile();
                
            } else
                f.delete();
            
        }
        
        catch(Exception e){
            
            consoleDetailFile=System.getProperty("user.home")+File.separator+"console.txt";
        }
        return consoleDetailFile;
    }
    /**
     * @param consoleDetailFile The consoleDetailFile to set.
     */
    public void setConsoleDetailFile(java.lang.String consoleDetailFile) {
        this.consoleDetailFile = consoleDetailFile;
    }
    
    public Console getConsoleFrame() {
        Manager manager=Manager.getManager();
        ArrayList paneList=manager.getCurrentSplitPaneComponents();
        Console c=null;
        for(int s=0;s<paneList.size();s++) {
            Object current=paneList.get(s);
            if(current instanceof Console) {
                c=(Console)current;
                break;
            }
            
        }
        return c;
        
    }
    
    private boolean jarOption=false;
    
    
    /**
     * @return Returns the jarOption.
     */
    public boolean isJarOption() {
        return jarOption;
    }
    /**
     * @param jarOption The jarOption to set.
     */
    public void setJarOption(boolean jarOption) {
        this.jarOption = jarOption;
    }
    
    
    
    public FileInfoFrame getInfoFrame() {
        Manager manager=Manager.getManager();
        ArrayList paneList=manager.getCurrentSplitPaneComponents();
        for(int s=0;s<paneList.size();s++) {
            Object current=paneList.get(s);
            if(current instanceof FileInfoFrame) {
                FileInfoFrame finfo=(FileInfoFrame)current;
                return finfo;
            }
        }
        return null;
        
    }
    
    public JTabbedPane getLeftTabbedPane() {
        Manager manager=Manager.getManager();
        ArrayList paneList=manager.getCurrentSplitPaneComponents();
        JTabbedPane tabs=null;
        for(int s=0;s<paneList.size();s++) {
            Object current=paneList.get(s);
            if(current instanceof JTabbedPane) {
                tabs=(JTabbedPane)current;
                Component c=tabs.getComponent(0);
                if(c instanceof JdecTree) {
                    
                    tabs=(JTabbedPane)current;
                    break;
                }
                
            }
        }
        
        return tabs;
    }
    
    public JTabbedPane getRightTabbedPane() {
        Manager manager = Manager.getManager();
        ArrayList paneList = manager.getCurrentSplitPaneComponents();
        JTabbedPane tabs = null;
        for (int s = 0; s < paneList.size(); s++) {
            Object current = paneList.get(s);
            if (current instanceof JTabbedPane) {
                tabs = (JTabbedPane) current;
                Component c=tabs.getComponent(0);
                if((c instanceof JdecTree)==false) {
                    if (tabs.getTabCount() > 2) {
                        break;
                    }
                }
                
            }
        }
        
        return tabs;
    }
    public  void setConstantPoolResultFile(String s) {
        constantPoolResultFile=s;
    }
    
    private String constantPoolResultFile=null;
    
    public String getConstantPoolResultFilePath() {
        return constantPoolResultFile;
    }
    
    public void setcpdescription(ArrayList list) {
        cpdesc=list;
    }
    public ArrayList getcpdesc() {
        return cpdesc;
    }
    
    private ArrayList cpdesc=null;
    private boolean configChanged=false;
    
    public void setConfigChanged(boolean b) {
        configChanged=b;
    }
    public boolean hasConfigChanged() {
        return configChanged;
    }
    public void copyFile(File src,File dest) {
        try {
            if(!src.exists()) {
                //JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"[ERROR] Could Not Make changed Permanent");
                return;
            } else {
                if(dest.exists()) {
                    dest.delete();
                }
                
                BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(src)));
                BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dest)));
                
                String s1=br.readLine();
                while(s1!=null) {
                    bw.write(s1);
                    bw.write("\n");
                    s1=br.readLine();
                }
                bw.flush();
                bw.close();
                br.close();
                bw=null;
                br=null;
                
                
                
            }
        } catch(IOException ioe) {
            try {
                LogWriter lg=LogWriter.getInstance();
                lg.writeLog("[ERROR]: Method: copyFile\n\tClass: UIUtil.class");
                lg.writeLog("------------------------------------------------");
                lg.writeLog("Exception Stack Trace");
                ioe.printStackTrace(lg.getPrintWriter());
                lg.flush();
            } catch(Exception e) {
            }
            
        }
        
        
        
        
    }
    
    
    
    public Hashtable getTabOpenFileMap() {
        
        return tabfilemap;
        
    }
    private Hashtable tabfilemap=new Hashtable();
    
    public void addToTabFileMap(int pos,String path) {
        tabfilemap.put(new Integer(pos),path);
    }
    
    private String defaultLnkNFeelName=getMetalClass();
    private String showWelcome="true";
    
    /**
     * @return Returns the defaultLnkNFeelName.
     */
    public String getDefaultLnkNFeelName() {
        return defaultLnkNFeelName;
    }
    /**
     * @param defaultLnkNFeelName The defaultLnkNFeelName to set.
     */
    public void setDefaultLnkNFeelName(String defaultLnkNFeelName) {
        this.defaultLnkNFeelName = defaultLnkNFeelName;
    }
    
    public void setShowWelcomeScreenAtStartUp(String show) {
        this.showWelcome=show;
    }
    
    public String getTypeGivenClassName(String className) {
        if(className.equals(gtkClass)) {
            return "GTKLookAndFeel";
        }
        if(className.equals(windowsClass)) {
            return "WindowsLookAndFeel";
        }
        if(className.equals(metalClass)) {
            return "MetalLookAndFeel";
        }
        if(className.equals(motifClass)) {
            return "MotifLookAndFeel";
        } else
            return "UNKNOWN";
        
    }
    /**
     * @return Returns the inlineAnonymous.
     */
    public java.lang.String getInlineAnonymous() {
        return inlineAnonymous;
    }
    /**
     * @param inlineAnonymous The inlineAnonymous to set.
     */
    public void setInlineAnonymous(java.lang.String inlineAnonymous) {
        this.inlineAnonymous = inlineAnonymous;
    }
    /**
     * @return Returns the innerDepth.
     */
    public java.lang.String getInnerDepth() {
        return innerDepth;
    }
    /**
     * @param innerDepth The innerDepth to set.
     */
    public void setInnerDepth(java.lang.String innerDepth) {
        this.innerDepth = innerDepth;
    }
    
    
    
    public static JLabel proframedetails_class=new JLabel();
    
    public String getShowWelcome() {
        return showWelcome;
    }
    public static JLabel proframedetails_method=new JLabel();
    public static JLabel versionproframedetails_class=new JLabel();
    public static JLabel versionproframedetails_method=new JLabel();
    public final static JPanel proframedetails=new JPanel();
    public final static JPanel versionproframedetails=new JPanel();
    public final static JPanel archiveproframedetails=new JPanel();
    public static JLabel proframedetails_pkg;
    public static JLabel explodeStatus;
    public static JLabel currentExplodeEntry;
    public static JDialog launchProgressBarFrame() {
        
        if(proframe!=null){
            proframe.setVisible(false);
            proframe.dispose();
            proframe=null;
        }
         if(archiveproframe!=null){
            archiveproframe.setVisible(false);
            archiveproframe.dispose();
            archiveproframe=null;
        }
        archiveproframe=null;
        proframe = new JDialog(UILauncher.getMainFrame(),"Jdec Progress...",false);
        proframe.setFocusableWindowState(true);
        proframe.setFocusable(true);
        
        JLabel statusLabel = new JLabel();
        proframedetails_pkg = new JLabel("");
        proframedetails_class = new JLabel("Please wait for jdec to finish task...");
        proframedetails_method = new JLabel("");
        statusLabel.setText("Please Wait for Jdec to finish executing the current task....");
        Dimension d=UILauncher.getMainFrame().getToolkit().getScreenSize();
        proframe.setBounds((int)d.getWidth()/2-130,(int)d.getHeight()/2-140,450, 120);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        JPanel contents = (JPanel)proframe.getContentPane();
        contents.setSize(430,350);
        proframedetails.setLayout(new BoxLayout(proframedetails,BoxLayout.Y_AXIS));
        proframedetails.removeAll();
        proframedetails.add(proframedetails_pkg); 
        proframedetails.add(proframedetails_class);
        proframedetails.add(proframedetails_method);
        proframedetails.revalidate();
        contents.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        //contents.add(statusLabel, BorderLayout.NORTH);
        contents.add(proframedetails, BorderLayout.NORTH);
        contents.add(progressBar);
        if(proframe!=null){
        proframe.getContentPane( ).add(progressBar);
        proframe.addWindowListener(wndCloser);
        }
        return proframe;
        
    }
    
    
    public static JDialog launchArchiveProgressBarFrame() {
        
        if(archiveproframe!=null){
            archiveproframe.setVisible(false);
            archiveproframe.dispose();
            archiveproframe=null;
        }
         if(proframe!=null){
            proframe.setVisible(false);
            proframe.dispose();
            proframe=null;
        }
        proframe=null;
        archiveproframe = new JDialog(UILauncher.getMainFrame(),"Jdec Progress...",false);
        archiveproframe.setFocusableWindowState(true);
        archiveproframe.setFocusable(true);
        currentExplodeEntry=new JLabel("");
        explodeStatus=new JLabel("Please wait. Jdec is now scanning the jar");
        explodeStatus.setFont(new Font("MONOSPACE",Font.BOLD,11));
        JLabel statusLabel = new JLabel();
        proframedetails_pkg = new JLabel("");
        proframedetails_class = new JLabel("Please wait for jdec to finish task...");
        proframedetails_method = new JLabel("");
        statusLabel.setText("Please Wait for Jdec to finish executing the current task....");
        Dimension d=UILauncher.getMainFrame().getToolkit().getScreenSize();
        archiveproframe.setBounds((int)d.getWidth()/2-130,(int)d.getHeight()/2-140,450, 120);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        JPanel contents = (JPanel)archiveproframe.getContentPane();
        contents.setSize(430,350);
        archiveproframedetails.setLayout(new BoxLayout(archiveproframedetails,BoxLayout.Y_AXIS));
        archiveproframedetails.removeAll();
        archiveproframedetails.add(explodeStatus);
        archiveproframedetails.add(currentExplodeEntry);
        /*archiveproframedetails.add(proframedetails_pkg);
        archiveproframedetails.add(proframedetails_class);
        archiveproframedetails.add(proframedetails_method);*/
        archiveproframedetails.revalidate();
        contents.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        //contents.add(statusLabel, BorderLayout.NORTH);
        JButton stop=new JButton("Stop");
        stop.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                continueToExplode=false;
            }
        });
        contents.add(archiveproframedetails, BorderLayout.NORTH);
        contents.add(progressBar,BorderLayout.SOUTH);
        contents.add(stop, BorderLayout.EAST);
        archiveproframe.getContentPane( ).add(progressBar);
        archiveproframe.addWindowListener(wndCloser);
        return archiveproframe;
        
    }
    
    
    public static JDialog launchVersionProgressBarFrame() {
        
        JDialog versionproframe = new JDialog(UILauncher.getMainFrame(),"Jdec Progress...",false);
        if(proframe!=null){
            proframe.setVisible(false);
        }
         if(archiveproframe!=null){
            archiveproframe.setVisible(false);
            archiveproframe.dispose();
            archiveproframe=null;
        }
        proframe=null;
        archiveproframe=null;

        proframedetails_pkg=null;
        proframedetails_class=null;
        proframedetails_method=null;
        JLabel statusLabel = new JLabel();
        versionproframedetails_class = new JLabel("Please wait for jdec to finish task...");
        versionproframedetails_method = new JLabel("");
        statusLabel.setText("Please Wait for Jdec to finish executing the current task....");
        Dimension d=UILauncher.getMainFrame().getToolkit().getScreenSize();
        versionproframe.setBounds((int)d.getWidth()/2-130,(int)d.getHeight()/2-140,450, 120);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        JPanel contents = (JPanel)versionproframe.getContentPane();
        contents.setSize(430,350);
        versionproframedetails.setLayout(new BoxLayout(versionproframedetails,BoxLayout.Y_AXIS));
        versionproframedetails.removeAll();
        versionproframedetails.add(versionproframedetails_class);
        versionproframedetails.add(versionproframedetails_method);
        versionproframedetails.revalidate();
        contents.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        //contents.add(statusLabel, BorderLayout.NORTH);
        contents.add(versionproframedetails, BorderLayout.NORTH);
        contents.add(progressBar);
        versionproframe.getContentPane( ).add(progressBar);
        versionproframe.addWindowListener(wndCloser);
        return versionproframe;
        
    }
    
    
    static WindowListener wndCloser = new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
            if(proframe!=null){
            proframe.setVisible(false);
            proframe.dispose();
            proframe=null;
            }
        }
    };
    
    public static JDialog archiveproframe=null;
    private static JDialog proframe=null;
    private static JDialog versionproframe=null;
    public static JDialog getProgressBarFrame() {
        return proframe;
    }
    public static JDialog getVersionProgressBarFrame() {
        return versionproframe;
    }
    public static boolean checkForInvalidEntries(java.lang.String type) {
        boolean ok=true;
        if(type.equals("run")) {
            
            UIUtil util=UILauncher.getUIutil();
            if(util==null)return false;
            Console cf=util.getConsoleFrame();
            JEditorPane pane=null;
            if(cf!=null) {
                pane=cf.getComponent();
            }
            if(util.getJdecOption()!=null && util.getJdecOption().trim().length()==0) {
                if(pane!=null)
                    pane.setText("Please set the jdec option to a proper value");
                return false;
            }
            if(util.getJdecOption()!=null && util.getJdecOption().equals("decompileJar")) {
                
                if(util==null)return false;
                java.lang.String opdir=util.getOutputFilePath();
                File test=new File(opdir);
                if(test==null || !test.exists()) {
                    if(pane!=null)
                        pane.setText("Please set The output Directory to a valid Value");
                    return false;
                }
                java.lang.String log=UIUtil.getUIUtil().getLogPath();
                test=new File(log);
                test=test.getParentFile();
                if(test==null || !test.exists()) {
                    java.lang.String s="Please set The Decompile Log File Path Properly\n";
                    s+="Please include the name of The log file also\n";
                    s+="As an Example You can set it to d:/temp/log.txt if it is a Windows Box\n\n";
                    if(pane!=null)
                        pane.setText(s);
                    return false;
                }
                log=UIUtil.getUIUtil().getUilogfile();
                test=new File(log);
                test=test.getParentFile();
                if(test==null || !test.exists()) {
                    java.lang.String s="Please set The UI Log File Path Properly\n";
                    s+="Please include the name of The log file also\n";
                    s+="As an Example You can set it to d:/temp/log.txt if it is a Windows Box\n\n";
                    if(pane!=null)
                        pane.setText(s);
                    return false;
                }
                java.lang.String tmp=UIUtil.getUIUtil().getProjectTempDir();
                test=new File(tmp);
                if(test==null || !test.exists()) {
                    java.lang.String s="Please set The Temp Dir Path Properly\n";
                    if(pane!=null)
                        pane.setText(s);
                    return false;
                }
                
            } else if(util.getJdecOption()!=null && !util.getJdecOption().equals("decompileJar")) {
                util=UILauncher.getUIutil();
                if(util==null)return false;
                java.lang.String opdir=util.getOutputFilePath();
                File test=new File(opdir);
                cf=util.getConsoleFrame();
                if(cf!=null)
                    pane=cf.getComponent();
                if(test==null || !test.exists()) {
                    if(pane!=null)
                        pane.setText("Please set The output Directory to a valid Value");
                    return false;
                }
                java.lang.String log=UIUtil.getUIUtil().getLogPath();
                test=new File(log);
                test=test.getParentFile();
                if(test==null || !test.exists()) {
                    java.lang.String s="Please set The Decompile Log File Path Properly\n";
                    s+="Please include the name of The log file also\n";
                    s+="As an Example You can set it to d:/temp/log.txt if it is a Windows Box\n\n";
                    if(pane!=null)
                        pane.setText(s);
                    return false;
                }
                log=UIUtil.getUIUtil().getUilogfile();
                test=new File(log);
                test=test.getParentFile();
                if(test==null || !test.exists()) {
                    java.lang.String s="Please set The UI Log File Path Properly\n";
                    s+="Please include the name of The log file also\n";
                    s+="As an Example You can set it to d:/temp/log.txt if it is a Windows Box\n\n";
                    if(pane!=null)
                        pane.setText(s);
                    return false;
                }
                
                
            }
            
            
            
        } else if(type.equals("")) {
            UIUtil util=UILauncher.getUIutil();
            if(util==null)return false;
            java.lang.String opdir=util.getOutputFilePath();
            File test=new File(opdir);
            Console cf=util.getConsoleFrame();
            JEditorPane pane=null;
            if(cf!=null) {
                pane=cf.getComponent();
                if(test==null || !test.exists()) {
                    pane.setText("Please set The output Directory to a valid Value");
                    return false;
                }
            }
            java.lang.String log=UIUtil.getUIUtil().getLogPath();
            test=new File(log);
            test=test.getParentFile();
            if(test==null || !test.exists()) {
                java.lang.String s="Please set The Decompile Log File Path Properly\n";
                s+="Please include the name of The log file also\n";
                s+="As an Example You can set it to d:/temp/log.txt if it is a Windows Box\n\n";
                if(pane!=null)
                    pane.setText(s);
                return false;
            }
            log=UIUtil.getUIUtil().getUilogfile();
            test=new File(log);
            test=test.getParentFile();
            if(test==null || !test.exists()) {
                java.lang.String s="Please set The UI Log File Path Properly\n";
                s+="Please include the name of The log file also\n";
                s+="As an Example You can set it to d:/temp/log.txt if it is a Windows Box\n\n";
                if(pane!=null)
                    pane.setText(s);
                return false;
            }
            
            
            
            
        } else if(type.equals("jar")) {
            
            UIUtil util=UILauncher.getUIutil();
            if(util==null)return false;
            java.lang.String opdir=util.getOutputFilePath();
            File test=new File(opdir);
            Console cf=util.getConsoleFrame();
            JEditorPane pane=null;
            if(cf!=null) {
                
                pane=cf.getComponent();
                if(test==null || !test.exists()) {
                    pane.setText("Please set The output Directory to a valid Value");
                    return false;
                }
            }
            java.lang.String log=UIUtil.getUIUtil().getLogPath();
            test=new File(log);
            test=test.getParentFile();
            if(test==null || !test.exists()) {
                java.lang.String s="Please set The Decompile Log File Path Properly\n";
                s+="Please include the name of The log file also\n";
                s+="As an Example You can set it to d:/temp/log.txt if it is a Windows Box\n\n";
                if(pane!=null)
                    pane.setText(s);
                return false;
            }
            log=UIUtil.getUIUtil().getUilogfile();
            test=new File(log);
            test=test.getParentFile();
            if(test==null || !test.exists()) {
                java.lang.String s="Please set The UI Log File Path Properly\n";
                s+="Please include the name of The log file also\n";
                s+="As an Example You can set it to d:/temp/log.txt if it is a Windows Box\n\n";
                if(pane!=null)
                    pane.setText(s);
                return false;
            }
            java.lang.String tmp=UIUtil.getUIUtil().getProjectTempDir();
            test=new File(tmp);
            if(test==null || !test.exists()) {
                java.lang.String s="Please set The Temp Dir Path Properly\n";
                if(pane!=null)
                    pane.setText(s);
                return false;
            }
            
            
        }
        
        return ok;
    }
    
    public ExtraOptions getFilterframe() {
        return filterframe;
    }
    
    private ExtraOptions filterframe=null;
    
    public void setFilterFrameRef(ExtraOptions f) {
        filterframe=f;
    }
    
    public boolean searchForClassName() {
        return search;
    }
    
    public void setSearch(boolean search) {
        this.search = search;
    }
    
    private volatile boolean  search = false;
    
    public static ArrayList getCurrentSels() {
        return currentSels;
    }
    
    public static void setCurrentSels(ArrayList list) {
        currentSels = list;
    }
    
    static ArrayList currentSels=new ArrayList();
    
    public static void addSelection(String text) {
        currentSels.add(text);
    }
    
    public static String currentSelString="";
    
    public static ArrayList decompiledClasses=new ArrayList();
    
    public static int historyindex=-1;
    
    
    private HashMap filetabmap=new HashMap();
    
    public void setFileTabMap(int index,String file) {
        filetabmap.put(""+index,file);
    }
    
    public String saveAsOrSave(int index) {
        String s=(String)filetabmap.get(""+index);
        if(s==null)
            return "saveas";
        else
            return "save";
    }
    
    public String getFileNameForTab(int index) {
        return (String)filetabmap.get(""+index);
    }
    
    
    public void removeFileTabMap(int i) {
        filetabmap.remove(""+i);
    }
    
    ArrayList favList=new ArrayList();
    public ArrayList getFavoriteList() {
        favList=Util.removeDuplicates(favList);
        return favList;
    }
    public void addFavorite(String s) {
        favList.add(s);
    }
    
    public void setFavList(ArrayList favList) {
        this.favList = favList;
    }
    
    
    private boolean checkFilterSetting(ZipEntry entry) {
      
        String name=entry.getName();
        if(entry.isDirectory()) {
            name=name.replaceAll("/",".");
            ArrayList list= ConsoleLauncher.getInclList();
            if(list==null || list.size()==0)return true;
            if(list.get(0).toString().equals("pkg-all") || list.get(0).toString().equals("All") || list.get(0).toString().equals("[]") || list.get(0).toString().equals("[ ]")){
                return true;
            }
            for (Iterator iter = list.iterator(); iter.hasNext();) {
                String element = (String) iter.next();
                if(element.equals(name)) {
                    return true;
                }
            }
            return false;
        } else {
            if(name.endsWith(".class")) {
                String defaultClz=UILauncher.getUIConfigRef().getOnly_default_classes_in_archive();
                int sl=name.indexOf("/");
                if(sl==-1)return true;
                if(defaultClz!=null && defaultClz.equals("true")) {
                    if(sl!=-1) {
                        return false;
                    } else
                        return true;
                }
                name=name.substring(0,name.lastIndexOf("/"));
                name=name.replaceAll("/",".");
                ArrayList list= ConsoleLauncher.getInclList();
                if(list.size() == 0)return true;
                for (Iterator iter = list.iterator(); iter.hasNext();) {
                    String element = (String) iter.next();
                    if(element.equals(name)) {
                        return true;
                    }
                }
                return false;
                
                
            } else {
                String enclosedArch=UILauncher.getUIConfigRef().getEnclosed_archives_in_archive();
                int dot=name.indexOf(".");
                if(dot!=-1) {
                    String ext=name.substring(dot+1);
                    if(enclosedArch!=null && enclosedArch.equalsIgnoreCase("true")) {
                        String types=UILauncher.getUIConfigRef().getArchiveTypes();
                        StringTokenizer st=new StringTokenizer(types,",");
                        while(st.hasMoreTokens()) {
                            String c=(String)st.nextToken();
                            if(c.equalsIgnoreCase(ext)) {
                                return true;
                            }
                        }
                        return false;
                        
                    } else
                        return false;
                    
                }
                return false;
                
                
            }
        }
        
        
    }

    public JEditorPane getCurrentFocussedInRightTabPane() {
        JEditorPane rdwrPane=null;
        Manager manager=Manager.getManager();
        ArrayList paneList=manager.getCurrentSplitPaneComponents();
        JTabbedPane tabs=UIUtil.getUIUtil().getRightTabbedPane();
        
        if(tabs!=null){
            
            Component editor=tabs.getSelectedComponent();
            
            JScrollPane editorTab=(JScrollPane)editor;
            Object o=editorTab.getComponent(0);
            JViewport view=(JViewport)o;
            Object o2=view.getView();
            if(o2!=null) {
                rdwrPane=(JEditorPane)o2;
            }
        }
        return rdwrPane;
        
        
    }
    public JEditorPane getEditorWindow() {
        JEditorPane rdwrPane=null;
        Manager manager=Manager.getManager();
        ArrayList paneList=manager.getCurrentSplitPaneComponents();
        JTabbedPane tabs=UIUtil.getUIUtil().getRightTabbedPane();
        
        if(tabs!=null){
            tabs.setSelectedIndex(tabs.indexOfTab("Jdec Editor Window"));
            Component editor=tabs.getSelectedComponent();
            
            JScrollPane editorTab=(JScrollPane)editor;
            Object o=editorTab.getComponent(0);
            JViewport view=(JViewport)o;
            Object o2=view.getView();
            if(o2!=null) {
                rdwrPane=(JEditorPane)o2;
            }
        }
        return rdwrPane;
        
        
    }
    
    
    public  void searchText(String s) {
        UIUtil.getUIUtil().getCurrentFocussedInRightTabPane().setCaret(new WideCaret());
        JEditorPane editorwindow = getCurrentFocussedInRightTabPane();
        int pos = editorwindow.getCaretPosition();
        boolean toSearchUp = false;
        String searchData="";
        try {
            Document doc = editorwindow.getDocument();
            searchData = doc.getText(0, doc.getLength());
            int searchIndex = pos;
            String key = "";
            key=s;
            start = -1;
            finish = -1;
            start = searchData.toLowerCase().indexOf(key.toLowerCase());
            if(start < 0) {
                throw new Exception();
            }
            finish = start+key.length();
            //start = start+searchIndex;
            //finish = finish+searchIndex;
            selectAnyTextFound(start, finish, toSearchUp);
        } catch (Exception ex) {
        	if(UIUtil.codeReformatted){
        		JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"This feature does not work after code is formatted from the toolbar action...\nPlease refresh/decompile this class to use this feature");
        	}
        	else
            JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Error while selecting  method in frame");
        }
        
    }
    
    public void selectAnyTextFound(int fromWhere, int tillWhere, boolean updownstate) {
        //UIUtil.getUIUtil().getEditorWindow().setCaret(new WideCaret());
        
        //StyleConstants.setBackground(orig,new Color(-16777216));
        //StyleConstants.setForeground(orig,new Color(-16777216));
        StyleConstants.getBackground(method);
        StyleConstants.setForeground(method, Color.WHITE);//new Color(69,47,43));//250,30,80));
        StyleConstants.setBackground(method,new Color(51,0,51));
        StyleConstants.setBold(method,true);
        StyleConstants.getForeground(method);
        StyleConstants.getBackground(method);
        UIUtil.getUIUtil().getCurrentFocussedInRightTabPane().setCaretPosition(fromWhere);
        UIUtil.getUIUtil().getCurrentFocussedInRightTabPane().moveCaretPosition(tillWhere);
        UIUtil.getUIUtil().getCurrentFocussedInRightTabPane().select(fromWhere, tillWhere);
        start = UIUtil.getUIUtil().getCurrentFocussedInRightTabPane().getSelectionStart();
        finish = UIUtil.getUIUtil().getCurrentFocussedInRightTabPane().getSelectionEnd();
        JEditorPane ep=UIUtil.getUIUtil().getCurrentFocussedInRightTabPane();
        Document d=ep.getDocument();
        Iterator s=resetShades.keySet().iterator();
        while(s.hasNext()) {
            String str1=(String)s.next();
            String str2=(String)resetShades.get(str1);
            int istr1=Integer.parseInt(str1);
            int istr2=Integer.parseInt(str2);
            ((DefaultStyledDocument)d).setCharacterAttributes(istr1,istr2,orig,true);
        }
        if(d instanceof DefaultStyledDocument) {
            resetShades.put(""+start,""+(finish-start+1));
            ((DefaultStyledDocument)d).setCharacterAttributes(start,(finish-start+1),method,true);
            //UIUtil.getUIUtil().getEditorWindow().moveCaretPosition(tillWhere+20);
        }
        
    }
    int start=-1;
    int finish=-1;
    public static volatile boolean continueToExplode=true;
    private SimpleAttributeSet method=new SimpleAttributeSet();
    private SimpleAttributeSet orig=new SimpleAttributeSet();
    private HashMap resetShades=new HashMap();
    
    /**
     * Only call it to set it to null
     * @param versionproframe
     */
    public static void setVersionproframe(JDialog versionproframe) {
        UIUtil.versionproframe = versionproframe;
    }
    
    
    
    
    private DefaultEditorKit defedkit=null;
    
    
    {
        defedkit=new DefaultEditorKit() {
            public Document createDefaultDocument() {
                PlainDocument plainDoc=new PlainDocument();
                return plainDoc;
            }
        };
    }
    
    public DefaultEditorKit getDefaultEditorKit() {
        return defedkit;
    }
    
    public OutputFrame decompileFrame=null;

    public String getSkipClassVersionCheck() {
        return skipClassVersionCheck;
    }

    public void setSkipClassVersionCheck(String skipClassVersionCheck) {
        this.skipClassVersionCheck = skipClassVersionCheck;
    }
    

    
    
    private String interpretNonAscii="true";
    private String forceNonAscii="false";
    private String skipClassVersionCheck="true";
    private String showFlowerBracketAtSameLine="true";
    
    private static String intPrefix="anInt";
    private static String longPrefix="aLong";
    private static String floatPrefix="aFloat";
    private static String doublePrefix="aDouble";
    private static String stringPrefix="aString";
    private static String bytePrefix="aByte";
    private static String shortPrefix="aShort";
    private static String charPrefix="aChar";
    private static String otherPrefix="Var";
    private static String booleanPrefix="aBoolean";

    public String getBooleanPrefix() {
        return booleanPrefix;
    }

    public void setBooleanPrefix(String booleanPrefix) {
        this.booleanPrefix = booleanPrefix;
    }
    

    
        public void setStringPrefix(String stringPrefix) {
        this.stringPrefix = stringPrefix;
    }

    public void setShortPrefix(String shortPrefix) {
        this.shortPrefix = shortPrefix;
    }

    public void setOtherPrefix(String otherPrefix) {
        this.otherPrefix = otherPrefix;
    }

    public void setLongPrefix(String longPrefix) {
        this.longPrefix = longPrefix;
    }

    public void setIntPrefix(String intPrefix) {
        this.intPrefix = intPrefix;
    }

    public void setFloatPrefix(String floatPrefix) {
        this.floatPrefix = floatPrefix;
    }

    public void setDoublePrefix(String doublePrefix) {
        this.doublePrefix = doublePrefix;
    }

    public void setCharPrefix(String charPrefix) {
        this.charPrefix = charPrefix;
    }

    public void setBytePrefix(String bytePrefix) {
        this.bytePrefix = bytePrefix;
    }

    public String getStringPrefix() {
        return stringPrefix;
    }

    public String getBytePrefix() {
        return bytePrefix;
    }

    public String getCharPrefix() {
        return charPrefix;
    }

    public String getDoublePrefix() {
        return doublePrefix;
    }

    public String getFloatPrefix() {
        return floatPrefix;
    }

    public String getIntPrefix() {
        return intPrefix;
    }

    public String getLongPrefix() {
        return longPrefix;
    }

    public String getOtherPrefix() {
        return otherPrefix;
    }

    public String getShortPrefix() {
        return shortPrefix;
    }




    
    public String getForceNonAscii() {
        if(forceNonAscii==null)
            forceNonAscii="false";
        return forceNonAscii;
    }
    
    public void setForceNonAscii(String forceNonAscii) {
        this.forceNonAscii = forceNonAscii;
    }
    
    public String getInterpretNonAscii() {
        if(interpretNonAscii==null)
            interpretNonAscii="true";
        return interpretNonAscii;
    }
    
    public void setInterpretNonAscii(String interpretNonAscii) {
        this.interpretNonAscii = interpretNonAscii;
    }

   
    public JLabel tiplabel=null;
    
    public int prevMouseEventX=-1;
    public int prevMouseEventY=-1;
    
    public static JdecTree currentScannedJDecTree=null;
    public static JDialog getArchiveproframe() {
      return archiveproframe;
    }

    public static void setArchiveproframe(JDialog archiveproframe) {
      UIUtil.archiveproframe = archiveproframe;
    }

    public static JPanel getArchiveproframedetails() {
      return archiveproframedetails;
    }

    public static boolean isContinueToExplode() {
      return continueToExplode;
    }

    public static void setContinueToExplode(boolean continueToExplode) {
      UIUtil.continueToExplode = continueToExplode;
    }

    public static JLabel getCurrentExplodeEntry() {
      return currentExplodeEntry;
    }

    public static void setCurrentExplodeEntry(JLabel currentExplodeEntry) {
      UIUtil.currentExplodeEntry = currentExplodeEntry;
    }

    public static JdecTree getCurrentScannedJDecTree() {
      return currentScannedJDecTree;
    }

    public static void setCurrentScannedJDecTree(JdecTree currentScannedJDecTree) {
      UIUtil.currentScannedJDecTree = currentScannedJDecTree;
    }

    public static String getCurrentSelString() {
      return currentSelString;
    }

    public static void setCurrentSelString(String currentSelString) {
      UIUtil.currentSelString = currentSelString;
    }

    public static ArrayList getDecompiledClasses() {
      return decompiledClasses;
    }

    public static void setDecompiledClasses(ArrayList decompiledClasses) {
      UIUtil.decompiledClasses = decompiledClasses;
    }

    public static JLabel getExplodeStatus() {
      return explodeStatus;
    }

    public static void setExplodeStatus(JLabel explodeStatus) {
      UIUtil.explodeStatus = explodeStatus;
    }

    public static boolean isExplosionInProgress() {
      return explosionInProgress;
    }

    public static void setExplosionInProgress(boolean explosionInProgress) {
      UIUtil.explosionInProgress = explosionInProgress;
    }

    public static int getHistoryindex() {
      return historyindex;
    }

    public static void setHistoryindex(int historyindex) {
      UIUtil.historyindex = historyindex;
    }

    public static String getJdecFolder() {
      return jdecFolder;
    }

    public static void setJdecFolder(String jdecFolder) {
      UIUtil.jdecFolder = jdecFolder;
    }

    public static JDialog getProframe() {
      return proframe;
    }

    public static void setProframe(JDialog proframe) {
      UIUtil.proframe = proframe;
    }

    public static JPanel getProframedetails() {
      return proframedetails;
    }

    public static JLabel getProframedetails_class() {
      return proframedetails_class;
    }

    public static void setProframedetails_class(JLabel proframedetails_class) {
      UIUtil.proframedetails_class = proframedetails_class;
    }

    public static JLabel getProframedetails_method() {
      return proframedetails_method;
    }

    public static void setProframedetails_method(JLabel proframedetails_method) {
      UIUtil.proframedetails_method = proframedetails_method;
    }

    public static JLabel getProframedetails_pkg() {
      return proframedetails_pkg;
    }

    public static void setProframedetails_pkg(JLabel proframedetails_pkg) {
      UIUtil.proframedetails_pkg = proframedetails_pkg;
    }

    public static UIUtil getRef() {
      return ref;
    }

    public static void setRef(UIUtil ref) {
      UIUtil.ref = ref;
    }

    public static ArrayList getRegisteredClasses() {
      return registeredClasses;
    }

    public static void setRegisteredClasses(ArrayList registeredClasses) {
      UIUtil.registeredClasses = registeredClasses;
    }

    public static JPanel getVersionproframedetails() {
      return versionproframedetails;
    }

    public static JLabel getVersionproframedetails_class() {
      return versionproframedetails_class;
    }

    public static void setVersionproframedetails_class(
        JLabel versionproframedetails_class) {
      UIUtil.versionproframedetails_class = versionproframedetails_class;
    }

    public static JLabel getVersionproframedetails_method() {
      return versionproframedetails_method;
    }

    public static void setVersionproframedetails_method(
        JLabel versionproframedetails_method) {
      UIUtil.versionproframedetails_method = versionproframedetails_method;
    }

    public static WindowListener getWndCloser() {
      return wndCloser;
    }

    public static void setWndCloser(WindowListener wndCloser) {
      UIUtil.wndCloser = wndCloser;
    }

    public File getCompileFile() {
      return compileFile;
    }

    public void setCompileFile(File compileFile) {
      this.compileFile = compileFile;
    }

    public ArrayList getCpdesc() {
      return cpdesc;
    }

    public void setCpdesc(ArrayList cpdesc) {
      this.cpdesc = cpdesc;
    }

    public java.lang.String getCurrentFile() {
      return currentFile;
    }

    public void setCurrentFile(java.lang.String currentFile) {
      this.currentFile = currentFile;
    }

    public OutputFrame getDecompileFrame() {
      return decompileFrame;
    }

    public void setDecompileFrame(OutputFrame decompileFrame) {
      this.decompileFrame = decompileFrame;
    }

    public DefaultEditorKit getDefedkit() {
      return defedkit;
    }

    public void setDefedkit(DefaultEditorKit defedkit) {
      this.defedkit = defedkit;
    }

    public java.lang.String getFileSelected() {
      return fileSelected;
    }

    public void setFileSelected(java.lang.String fileSelected) {
      this.fileSelected = fileSelected;
    }

    public java.lang.String getFileSelectedPath() {
      return fileSelectedPath;
    }

    public void setFileSelectedPath(java.lang.String fileSelectedPath) {
      this.fileSelectedPath = fileSelectedPath;
    }

    public HashMap getFiletabmap() {
      return filetabmap;
    }

    public void setFiletabmap(HashMap filetabmap) {
      this.filetabmap = filetabmap;
    }

    public int getFinish() {
      return finish;
    }

    public void setFinish(int finish) {
      this.finish = finish;
    }

    public java.lang.String getJavacResFile() {
      return javacResFile;
    }

    public void setJavacResFile(java.lang.String javacResFile) {
      this.javacResFile = javacResFile;
    }

    public File getJavaFolder() {
      return javaFolder;
    }

    public void setJavaFolder(File javaFolder) {
      this.javaFolder = javaFolder;
    }

    public SimpleAttributeSet getMethod() {
      return method;
    }

    public void setMethod(SimpleAttributeSet method) {
      this.method = method;
    }

    public SimpleAttributeSet getOrig() {
      return orig;
    }

    public void setOrig(SimpleAttributeSet orig) {
      this.orig = orig;
    }

    public int getPrevMouseEventX() {
      return prevMouseEventX;
    }

    public void setPrevMouseEventX(int prevMouseEventX) {
      this.prevMouseEventX = prevMouseEventX;
    }

    public int getPrevMouseEventY() {
      return prevMouseEventY;
    }

    public void setPrevMouseEventY(int prevMouseEventY) {
      this.prevMouseEventY = prevMouseEventY;
    }

    public ArrayList getPropNames() {
      return propNames;
    }

    public void setPropNames(ArrayList propNames) {
      this.propNames = propNames;
    }

    public ArrayList getPropValues() {
      return propValues;
    }

    public void setPropValues(ArrayList propValues) {
      this.propValues = propValues;
    }

    public ArrayList getRecentfiles() {
      return recentfiles;
    }

    public void setRecentfiles(ArrayList recentfiles) {
      this.recentfiles = recentfiles;
    }

    public HashMap getResetShades() {
      return resetShades;
    }

    public void setResetShades(HashMap resetShades) {
      this.resetShades = resetShades;
    }

    public int getStart() {
      return start;
    }

    public void setStart(int start) {
      this.start = start;
    }

    public Hashtable getTabfilemap() {
      return tabfilemap;
    }

    public void setTabfilemap(Hashtable tabfilemap) {
      this.tabfilemap = tabfilemap;
    }

    public JLabel getTiplabel() {
      return tiplabel;
    }

    public void setTiplabel(JLabel tiplabel) {
      this.tiplabel = tiplabel;
    }

    public static JDialog getVersionproframe() {
      return versionproframe;
    }

    public boolean isConfigChanged() {
      return configChanged;
    }

    public String getConstantPoolResultFile() {
      return constantPoolResultFile;
    }

    public ArrayList getFavList() {
      return favList;
    }

    public boolean isSearch() {
      return search;
    }

    public static void setVersionlines(HashMap versionlines) {
      UIUtil.versionlines = versionlines;
    }

    public void setFilterframe(ExtraOptions filterframe) {
      this.filterframe = filterframe;
    }

    public void setOutputFilePath(java.lang.String folderPath) {
      this.outputFilePath = folderPath;
    }

    public void setGtkClass(String gtkClass) {
      this.gtkClass = gtkClass;
    }

    public void setJarFilePath(java.lang.String jarFilePath) {
      this.jarFilePath = jarFilePath;
    }

    public void setJavaClassFile(java.lang.String javaClassFile) {
      this.javaClassFile = javaClassFile;
    }

    public void setJDecOption(java.lang.String decOption) {
      JDecOption = decOption;
    }

    public void setLogLevel(java.lang.String logLevel) {
      this.logLevel = logLevel;
    }

    public void setLogMode(java.lang.String logMode) {
      this.logMode = logMode;
    }

    public void setLogPath(java.lang.String logPath) {
      this.logPath = logPath;
    }

    public void setMetalClass(String metalClass) {
      this.metalClass = metalClass;
    }

    public void setMotifClass(String motifClass) {
      this.motifClass = motifClass;
    }

    public void setMode(java.lang.String outputMode) {
      this.mode = outputMode;
    }

    public void setProjectTempDir(java.lang.String projectTempDir) {
      this.projectTempDir = projectTempDir;
    }

    public void setShowImport(java.lang.String showImport) {
      this.showImport = showImport;
    }

    public void setShowWelcome(String showWelcome) {
      this.showWelcome = showWelcome;
    }

    public void setWindowsClass(String windowsClass) {
      this.windowsClass = windowsClass;
    }

	public String getShowFlowerBracketAtSameLine() {
		return showFlowerBracketAtSameLine;
	}

	public void setShowFlowerBracketAtSameLine(String showFlowerBracketAtSameLine) {
		this.showFlowerBracketAtSameLine = showFlowerBracketAtSameLine;
	}
    
}


