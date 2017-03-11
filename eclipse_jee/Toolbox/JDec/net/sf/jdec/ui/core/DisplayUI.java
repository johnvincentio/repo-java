
/*
 *  DisplayUI.java Copyright (c) 2006,07 Swaroop Belur
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

import net.sf.jdec.ui.config.UIConfig;
import net.sf.jdec.ui.main.UILauncher;
import net.sf.jdec.ui.util.Preferences;
import net.sf.jdec.ui.util.Tips;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 
 *  @author swaroop belur
 */

public class DisplayUI {
    
    private Manager manager=null;
    
    private MainFrame mainFrame=null;
    private ToolBar bar=null;
    private Splitter splitPane=null;
    private AuxToolBar auxbar=null;
    JFrame jframe=null;
    
    
    public JFrame getJframe() {
        return jframe;
    }
    
    public void setJframe(JFrame jframe) {
        this.jframe = jframe;
    }
    
    public void setManager(Manager manager) {
        this.manager=manager;
    }
    
    public void renderComponenets()  // NOTE: This will have to be changed if some more compoents will be added like
    // aux toolbar
    {
        ArrayList neededComponents=manager.getRegisteredComponents();
       
        String mesg="";
        for(int s=0;s<neededComponents.size();s++) {
            UIObject uiObject=(UIObject)neededComponents.get(s);
      
            if(uiObject instanceof MainFrame) {
                setMainFrame((MainFrame)uiObject);
            }
            if(uiObject instanceof ToolBar) {
                setBar((ToolBar)uiObject);
            }
            if(uiObject instanceof Splitter) {
                setSplitPane((Splitter)uiObject);
            }
            if(uiObject instanceof AuxToolBar) {
                setAuxToolBar((AuxToolBar)uiObject);
            }
            
        }
        
        JFrame mainWindow=mainFrame.getMainFrame();
        JMenuBar menuBar=bar.getBar();
        JSplitPane pane=splitPane.getSplitPlane();
        JToolBar aux=auxbar.getAuxBar();
        
        Dimension d=mainWindow.getToolkit().getScreenSize();
        Container c=mainWindow.getContentPane();
        mainWindow.setSize((int)d.getWidth()-10, (int)d.getHeight()-50);
        mainWindow.setJMenuBar(menuBar);
        UILauncher.setMainFrameRef(mainWindow);
        auxlabel=new JLabel("");
        auxPanel=new JPanel();
        auxPanel.add(aux,BorderLayout.EAST);
        //auxPanel.add(auxlabel,BorderLayout.WEST);
        c.add(aux,BorderLayout.NORTH);
        c.add(pane, BorderLayout.CENTER);
        WindowListener wndCloser = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    UIConfig uiconfig = UILauncher.getUIConfigRef();
                    synchronized(uiconfig) {
                        uiconfig.persistToFile();
                    }
                } catch (IOException ioe) {
                    try {
                        
                        String msg="***************************************************************";
                        msg+="\n"+"EXCEPTION STACK TRACE";
                        msg+="***************************************************************";
                        msg+="\n\n";
                        msg+="Message :"+ioe.getMessage();
                        msg+="\n"+"Cause :"+ioe.getCause();
                        ioe.printStackTrace();
                        Manager.getManager().setShowProgressBar(false);
                        
                    } catch(Exception ex){}
                }
                
                int option = JOptionPane.showConfirmDialog(UILauncher
                    .getMainFrame(), "Are You Sure?", "Close Jdec UI",
                    JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.NO_OPTION) {
                    
                    UILauncher.getMainFrame().setDefaultCloseOperation(UILauncher.getMainFrame().DO_NOTHING_ON_CLOSE);
                } else if (option == JOptionPane.YES_OPTION) {
                    boolean b=UILauncher.getUIutil().hasConfigChanged();
                    if(b) {
                        int opt=JOptionPane.showConfirmDialog(UILauncher.getMainFrame(),"The Decompiler Configuration Has been updated...\nWould You Like to save the changes...","Update Configuration ...?",JOptionPane.YES_NO_OPTION);
                        if(opt==JOptionPane.YES_OPTION) {
                            
                            File src=new File(System.getProperty("user.home")+File.separator+"tempconfig.properties");
                            File dest=new File("config.properties");
                            UILauncher.getUIutil().copyFile(src,dest);
                        }
                        if(opt==JOptionPane.NO_OPTION) {
                            UILauncher.getMainFrame().setDefaultCloseOperation(UILauncher.getMainFrame().DO_NOTHING_ON_CLOSE);
                        }
                        
                        if(opt==JOptionPane.CANCEL_OPTION) {
                            UILauncher.getMainFrame().setDefaultCloseOperation(UILauncher.getMainFrame().DO_NOTHING_ON_CLOSE);
                        }
                        
                    }
                    
                    JFrame frame = UILauncher.getMainFrame();
                    frame.setVisible(false);
                    frame.setEnabled(false);
                    frame.dispose();
                    UILauncher.closeChildWindows();
                    File replaceMeFile=new File("***REPLACEME***");
                    if(replaceMeFile.exists()){
                      replaceMeFile.delete();
                      if(replaceMeFile.exists()){
                        replaceMeFile.deleteOnExit();
                      }
                    }
                    
                }
                
            }
        };
        mainWindow.addWindowListener(wndCloser);
        
        // Store The Frame Reference for access
        this.jframe=mainWindow;
        
        // Show The Frame
        UILauncher.splash.progress.setValue(80);
        
        try {
            // Check if System Property for lookNFeel was set by user
            String def=System.getProperty("user.lookNfeel.choice");
            boolean setlnf=true;
            if(def!=null) {
                try {
                    //System.out.println(def);
                    
                    UIManager.setLookAndFeel(def);
                    setlnf=false;
                    Preferences.userProvidedAtLaunch=def;
                } catch(Exception exp) {
                    System.out.println("LookNFeel class could not be loaded/instantiated\nPlease check 1>The path of class name\n2>Supporting jar is present\n");
                    System.out.println("Specified class : "+def);
                    System.out.println("\nChecking for other lookNfeels");
                    def=null;
                    setlnf=true;
                }
            }
            if(def==null) {
                def=UILauncher.getUIutil().getDefaultLnkNFeelName();
            }
            if(def!=null) {
                try {
                    //System.out.println(def);
                    UIManager.setLookAndFeel(def);
                    setlnf=false;
                } catch(Exception exp) {
                    def=null;
                    setlnf=true;
                }
            }
            if(def==null)def=UIManager.getSystemLookAndFeelClassName();
            if(def==null)def=UIManager.getCrossPlatformLookAndFeelClassName();
            if(setlnf)
                UIManager.setLookAndFeel(def);
            UILauncher.splash.progress.setValue(90);
            SwingUtilities.updateComponentTreeUI(mainWindow);
            //UILauncher.getMainFrame().repaint();
            try {
                Thread.sleep(2000);
                UILauncher.splash.progress.setValue(100);
                UILauncher.splash.st.setText("Done Loading");
                Thread.sleep(1000); // Intentional For The splash screen to remain for some time
                
            } catch(InterruptedException ie) {
                
                UILauncher.splash.st.setText("Done Loading");
            }
            if(UILauncher.welcomeCreated){
                Splitter ref=Manager.getManager().getSplitterRef();
                //JSplitPane right=ref.getRightSplitPane();
                ref.resetRightSplitPane("hide");
                ref.getSplitPlane().revalidate();
                UILauncher.getMainFrame().validate();
            }
            
            UILauncher.showSplash=false;
            mainWindow.setVisible(true);
            mainWindow.requestFocusInWindow();
            UILauncher.getUIutil().setCurrentLNF(UILauncher.getUIutil().getTypeGivenClassName(def));
        } catch(Exception looknfeel) {
            try {
             
                String msg="***************************************************************";
                msg+="\n"+"EXCEPTION STACK TRACE";
                msg+="***************************************************************";
                msg+="\n\n";
                msg+="Message :"+looknfeel.getMessage();
                msg+="\n"+"Cause :"+looknfeel.getCause();
                Manager.getManager().setShowProgressBar(false);
                looknfeel.printStackTrace();
                
            } catch(Exception ex){}
        }
        
        
        
        UIConfig configref=UILauncher.getUIConfigRef();
        boolean show=configref.showTip();
        if(show) {
            Tips tips=new Tips();
        } else {
            String showtip=configref.getPref("ShowTip");
            if(showtip!=null && showtip.equalsIgnoreCase("true")) {
                Tips tips=new Tips();
            }
        }
        
        
    }
    
    
    public void updateUI() {
        renderComponenets();
    }
    
    private ToolBar getBar() {
        return bar;
    }
    
    private void setBar(ToolBar bar) {
        this.bar = bar;
    }
    
    private MainFrame getMainFrame() {
        return mainFrame;
    }
    
    private void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }
    
    private Splitter getSplitPane() {
        return splitPane;
    }
    
    private void setSplitPane(Splitter splitPane) {
        this.splitPane = splitPane;
    }
    private void setAuxToolBar(AuxToolBar auxBar) {
        this.auxbar=auxBar;
    }
    
    public static JPanel auxPanel=null;
    public static  JLabel auxlabel=null;
    
}
