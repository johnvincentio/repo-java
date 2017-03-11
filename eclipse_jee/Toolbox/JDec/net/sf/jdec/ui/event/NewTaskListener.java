
/*
 *  NewTaskListener.java Copyright (c) 2006,07 Swaroop Belur
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





import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

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
import net.sf.jdec.ui.core.UIObserver;
import net.sf.jdec.ui.main.UILauncher;
import net.sf.jdec.ui.util.ClassFileFilter;
import net.sf.jdec.ui.util.ClassInJar;
import net.sf.jdec.ui.util.FileOpener;
import net.sf.jdec.ui.util.LogWriter;
import net.sf.jdec.ui.util.TwoVersions;
import net.sf.jdec.ui.util.UIConstants;
import net.sf.jdec.ui.util.UIUtil;
import net.sf.jdec.config.Configuration;

public class NewTaskListener implements ActionListener {
    
    private void gotoSleep(){
        try{
            Thread.sleep(1000);
            // i++;
        } catch(InterruptedException e){
            
        }
        // System.out.println(i);
        
    }
    public void actionPerformed(ActionEvent event) {
        try{
            
            
            if(event.getActionCommand().equals("versions")) {
                TwoVersions two=new TwoVersions("Decompiler 2 versions...");
            }
            if(event.getActionCommand().equals("Decompile Class File")) {
                //System.out.println("Class File");
                FileOpener chooser=new FileOpener(new ClassFileFilter());
                final File chosenFile=chooser.getSelectedFile();
                if(chosenFile==null)return;
                UILauncher.getUIutil().addRecentFile(chosenFile);
                String filename=chosenFile.getAbsolutePath();
                if(!chosenFile.exists() || (filename!=null && !filename.trim().endsWith(".class"))) {
                    JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Chosen File is not a valid class file");
                    Manager.getManager().setShowProgressBar(false);
                    
                    return;
                }
                Manager.getManager().setShowProgressBar(true);
                //Thread main=Thread.currentThread();
                JFrame tempframes[]=UILauncher.getTempFrames();
                for (int i = 0; i < tempframes.length; i++) {
                    JFrame tempframe=tempframes[i];
                    if(tempframe!=null) {
                        tempframe.setVisible(false);
                        tempframe.dispose();
                    }
                }
                
                //System.out.println("XXX"+chosenFile.getAbsolutePath());
                
                
                Thread t1=new Thread() {
                    
                    public void run()	{
                        decompileClass(chosenFile);
                        ConsoleLauncher.setFileDecompiled(true);
                        ConsoleLauncher.setCurrentSourceFile(chosenFile);  // Why is this being set after call ? Add note
                        
                        
                    }
                    
                };
                
                
                
                Thread t2=new Thread() {
                    
                    public void run()	{
                        
                        
                        
                        JDialog proframe=UIUtil.launchProgressBarFrame();
                        if(proframe!=null){
                        proframe.setVisible(true);
                        }
                        //proframe.requestFocusInWindow();
                        
                        
                        while(Manager.getManager().isShowProgressBar()==true) {
                            
                            gotoSleep();
                        }
                        
                        if(!Manager.getManager().isShowProgressBar()) {
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
                
                if(UIUtil.checkForInvalidEntries("")) {
                    t1.start();
                    t2.start();
                } else {
                    JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Please Check The Decompiler Configuration Property Values Again","JDEC WARNING",JOptionPane.INFORMATION_MESSAGE);
                }
                UIUtil.getUIUtil().getLeftTabbedPane().setSelectedIndex(0);
                
                
                
                
            }
            if(event.getActionCommand().equals("classinjar")) {
                
                ClassInJar cj=new ClassInJar();
                cj.start();
                
            }
            if(event.getActionCommand().equals("Decompile Archive File")) {
                try {
                    
                    final FileOpener chooser=new FileOpener(null);
                    JFrame tempframes[]=UILauncher.getTempFrames();
                    for (int i = 0; i < tempframes.length; i++) {
                        JFrame tempframe=tempframes[i];
                        if(tempframe!=null) {
                            tempframe.setVisible(false);
                            tempframe.dispose();
                        }
                    }
                    final File chosenFile=chooser.getSelectedFile();
                    if(chosenFile==null)return;
                    String filename=chosenFile.getAbsolutePath();
                    if(!chosenFile.exists() || (filename!=null && !isValidArchiveFileExt(filename))) {
                        JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Chosen File is not a valid archive file");
                        Manager.getManager().setShowProgressBar(false);
                        return;
                    }
                    String message="";
                    message+="Would You like Jdec to Decompile Entire Jar ...\n\n";
                    message+="[Warning:] This May take some time depending on size of Jar ...\n";
                    message+="Would You Like to Proceed...?\n";
                    message+="\n\n*** [NOTE :] *** IF Cancel is selected all The class Files in the Jar\n";
                    message+="will be shown as a directory structure in the jar tab....";
                    
                    
                    JOptionPane option=new JOptionPane();//message,JOptionPane.QUESTION_MESSAGE,JOptionPane.YES_NO_CANCEL_OPTION);
                    Object[] options = { "OK", "CANCEL" };
                    int opt=option.showOptionDialog(UILauncher.getMainFrame(),message, "Jar Decompile Option", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    option.setVisible(true);
                    if(opt==JOptionPane.CANCEL_OPTION || opt==JOptionPane.NO_OPTION) {
                        Thread prog=new Thread(){
                            public void run(){
                                JDialog archiveProf=UIUtil.launchArchiveProgressBarFrame();
                                archiveProf.setVisible(true);
                                while(UIUtil.explosionInProgress){
                                    gotoSleep();
                                }
                                if(!UIUtil.explosionInProgress){
                                    if(UIUtil.archiveproframe!=null){
                                        UIUtil.archiveproframe.setVisible(false);
                                        UIUtil.archiveproframe.dispose();
                                    }
                                }
                                
                                
                                
                                
                                
                            }
                        };
                        
                        
                        Thread work=new Thread(){
                            
                            public void run(){
                                JdecTree jarExploded=null;
                                String filePath=chosenFile.getAbsolutePath();
                                String jarDir= UIUtil.getUIUtil().explodeJar(chosenFile);
                                String s=File.separator;
                                if(jarDir.endsWith(s)==false)jarDir=jarDir.concat(File.separator);
                                if(jarDir.trim().length() > 0 && isValidArchiveFileExt(filePath))
                                    jarExploded=new JdecTree(jarDir);
                                else {
                                    jarExploded=null;
                                }
                                UIUtil.currentScannedJDecTree=jarExploded;
                                UIUtil.explosionInProgress=false;
                                
                                if(UIUtil.checkForInvalidEntries("jar")) {
                                    updateJarTabWithTree( UIUtil.currentScannedJDecTree);
                                    JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Jar Tab has been updated with directory containing Class Files..\nPlease Click on any File to see the output...","User Info...",JOptionPane.INFORMATION_MESSAGE);
                                    UIUtil.getUIUtil().getLeftTabbedPane().setSelectedIndex(UIUtil.getUIUtil().getLeftTabbedPane().indexOfTab("Jar"));
                                } else {
                                    JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Please Check The Decompiler Configuration Property Values Again","JDEC WARNING",JOptionPane.INFORMATION_MESSAGE);
                                }
                                
                            }
                        };
                        
                        work.setPriority(Thread.MAX_PRIORITY);
                        prog.setPriority(Thread.NORM_PRIORITY);
                        UIUtil.explosionInProgress=true;
                        UIUtil.continueToExplode=true;
                        work.start();
                        prog.start();
                        
                        
                    }
                    
                    if(opt==JOptionPane.OK_OPTION) {
                        JTabbedPane tab=UIUtil.getUIUtil().getLeftTabbedPane();
                        tab.setSelectedIndex(1);
                        
                        
                         final Thread decProg=new Thread() {
                            
                            public void run() {
                                
                                
                                
                                JDialog proframe=UIUtil.launchProgressBarFrame();
                                if(proframe!=null)
                                proframe.setVisible(true);
                                //proframe.requestFocusInWindow();
                                
                                
                                while(Manager.getManager().isShowProgressBar()==true) {
                                    
                                    gotoSleep();
                                }
                                
                                if(!Manager.getManager().isShowProgressBar()) {
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
                        
                        
                        Thread work=new Thread(){
                            
                            public void run() {
                                Console c=UILauncher.getUIutil().getConsoleFrame();
                                if(c!=null) {
                                    JEditorPane rdwr=c.getComponent();
                                    rdwr.setText("");
                                    String s="Jdec Started..\n";
                                    s=rdwr.getText()+"\n\n"+s;
                                    rdwr.setText(s);
                                }
                                Manager.getManager().setShowProgressBar(false);
                                JdecTree jarExploded=null;
                                String filePath=chosenFile.getAbsolutePath();
                                String jarDir= UIUtil.getUIUtil().explodeJar(chosenFile);
                                UIUtil.explosionInProgress=false;
                                
                                
                                Manager.getManager().setShowProgressBar(true);
                                decProg.start();
                                DecompilerBridge bridge=DecompilerBridge.getInstance(UIUtil.getUIUtil());
                                bridge.setJarFile(chooser.getSelectedFile().getAbsolutePath());
                                if(c!=null) {
                                    JEditorPane rdwr=c.getComponent();
                                    
                                    String s="Current Task :Decompile Jar\n";
                                    s=rdwr.getText()+"\n\n"+s;
                                    rdwr.setText(s);
                                }
                                bridge.execute("decompileJar", "", false,"null");
                                if(c!=null) {
                                    JEditorPane rdwr=c.getComponent();
                                    
                                    String s="Current File :"+ Configuration.getJarPath()+"\n";
                                    // Reinitialize current executed files....
                                    ConsoleLauncher.setCurrentJarSourceFile(null);
                                    Configuration.setJarPath(null);
                                    
                                    s=rdwr.getText()+"\n\n"+s;
                                    rdwr.setText(s);
                                }
                                if(UIUtil.getUIUtil().getJdecOption().equals("decompileJar")==false){
                                    bridge.showResult(UIUtil.getUIUtil().getRightTabbedPane());
                                    
                                }
                                if(c!=null) {
                                    JEditorPane rdwr=c.getComponent();
                                    String s;
                                    if(UIUtil.getUIUtil().getJdecOption().equals("decompileJar")==false)
                                        s="Done...Please Wait for UI to render the output....\nPlease Check up the log files for any error(s)";
                                    else
                                        s="Done...The Jar Tab Has been Updated with The output folder \nshowing the necessary decompiled files\n";
                                    s=rdwr.getText()+"\n\n"+s;
                                    rdwr.setText(s);
                                }
                                JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Jar Tab has been updated with directory containing decompiled Files..\n[Traverse the package structure]",
                                    "User Info...",JOptionPane.INFORMATION_MESSAGE);
                                
                            }
                        };
                        
                        Thread explodeprog=new Thread(){
                            public void run(){
                                JDialog archiveProf=UIUtil.launchArchiveProgressBarFrame();
                                archiveProf.setVisible(true);
                                while(UIUtil.explosionInProgress){
                                    gotoSleep();
                                }
                                if(!UIUtil.explosionInProgress){
                                    if(UIUtil.archiveproframe!=null){
                                        UIUtil.archiveproframe.setVisible(false);
                                        UIUtil.archiveproframe.dispose();
                                    }
                                }
                                
                                
                                
                                
                                
                            }
                        };
                        
                        
                       
                        
                        
                        ///
                        if(isValidArchiveFileExt(chooser.getSelectedFile().getAbsolutePath())){
                            ConsoleLauncher.setCurrentJarSourceFile(chooser.getSelectedFile());
                            UILauncher.getUIutil().setJarOption(true);
                            if(UIUtil.checkForInvalidEntries("jar")) {
                                UIUtil.explosionInProgress=true;
                                UIUtil.continueToExplode=true;
                                work.start();
                                explodeprog.start();
                            } else {
                                JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Please Check The Decompiler Configuration Property Values Again","JDEC WARNING",JOptionPane.INFORMATION_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Chosen File is not a archive file","Archive Option",JOptionPane.INFORMATION_MESSAGE);
                            
                        }
                        
                        
                    }
                    
                    
                } catch(Exception jarexcep) {
                    try {
                        LogWriter lg=LogWriter.getInstance();
                        lg.writeLog("[ERROR]: Method: actionPeformed\n\tClass: NewTaskListener.class");
                        lg.writeLog("------------------------------------------------");
                        lg.writeLog("Exception Stack Trace");
                        jarexcep.printStackTrace(lg.getPrintWriter());
                        lg.flush();
                        JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Error Processing Jar "+jarexcep);
                    } catch(Exception e) {
                        
                    }
                    
                }
                
            }
            
        } catch(Throwable t) {
            try {
                LogWriter lg=LogWriter.getInstance();
                lg.writeLog("[ERROR]: Method: actionPerformed\n\tClass: NewtaskListener.class");
                lg.writeLog("------------------------------------------------");
                lg.writeLog("Exception Stack Trace");
                t.printStackTrace(lg.getPrintWriter());
                lg.flush();
                
            } catch(Exception e) {
                
            }
        }
        
        
        
        
        
    }
    int m_counter;
    
    
    
    private void decompileClass(File f) {
        
        DecompilerBridge bridge=DecompilerBridge.getInstance(UIUtil.getUIUtil());
        if(f==null || f.getAbsolutePath().length() == 0)return;
        bridge.setClassFile(f.getAbsolutePath());
        bridge.execute("decompileClass", "", false,"null");
        JTabbedPane rightTab=UIUtil.getUIUtil().getRightTabbedPane();
        bridge.showResult(rightTab);
        ConsoleLauncher.setCurrentDecompiledFile(Configuration.getJavaClassFile());
        //Configuration.setJavaClassFile(null);
        //Configuration.setClassFilePath(null);
    }
      
    
    
    private static Object lock=null;
    static
    {
        lock=new Object();
    }
    private static NewTaskListener listener;
    
    public static NewTaskListener getInstance() {
        if(listener!=null)return listener;
        else {
            synchronized(lock) {
                listener=new NewTaskListener();
                return listener;
            }
        }
    }
    
    private boolean isValidArchiveFileExt(String filename) {
        //Constructor
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
    
    private void updateJarTabWithTree(JdecTree jarExploded) {
        if(jarExploded!=null) {
            UIObserver observer=UILauncher.getObserver();
            if(observer!=null) // Actually no need to check
            {
                observer.resetTabsPane(true);
                Manager manager=Manager.getManager();
                ArrayList list=manager.getCurrentSplitPaneComponents();
                JTabbedPane tabPane=UIUtil.getUIUtil().getLeftTabbedPane();
                if(tabPane!=null) {
                    int jarTabIndex=tabPane.indexOfTab("Jar");
                    if(jarTabIndex >= 0) {
                        tabPane.remove(jarTabIndex);
                        tabPane.addTab("Jar",jarExploded);
                    }
                }
                
            }
            JFrame mainFrame=UILauncher.getMainFrame();
            mainFrame.repaint();
        }
    }
    
    private NewTaskListener(){}
    
}
