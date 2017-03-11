
/*
 * UILauncher.java Copyright (c) 2006,07 Swaroop Belur
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

package net.sf.jdec.ui.main;

import java.awt.Component;
import java.awt.Window;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import net.sf.jdec.commonutil.StartupHelper;
import net.sf.jdec.io.Writer;
import net.sf.jdec.ui.config.UIConfig;
import net.sf.jdec.ui.core.DisplayUI;
import net.sf.jdec.ui.core.Manager;
import net.sf.jdec.ui.core.UIObserver;
import net.sf.jdec.ui.util.LogWriter;
import net.sf.jdec.ui.util.SplashScreen;
import net.sf.jdec.ui.util.UIUtil;

/***
 * TODO: Trim this class for next release
 * @author sbelur
 *
 */
public class UILauncher {
    
    private static void setUIUtilRef(UIUtil uiutil) {
        util=uiutil;
    }
    private static UIUtil util=null;
    
    public static boolean welcomeCreated=false;
    public static UIUtil getUIutil() {
        return util;
    }
    static String message="";
    
    
    
    public static void main(java.lang.String arg[]) {
        try {
            //registerShutDownHook();
            Thread sp=new SplashScreenThread();
            sp.start();
            sp.setPriority(Thread.MAX_PRIORITY);
            while(splash==null){}
            
            UIUtil uiutil=UIUtil.getUIUtil();
            uiutil.registerDecompilerConfigParams();
            splash.progress.setValue(10);
            
            setUIUtilRef(uiutil);
            UIObserver observer=UIObserver.getUIObserver();
            setObserver(observer);
            uiConfig=UIConfig.getUIConfig();
            uiConfig.readUserPrefs();
            splash.progress.setValue(20);
            
         
            Manager manager=Manager.getManager();
            manager.createAllComponents(observer);
            splash.progress.setValue(50);
            
         
            manager.regitser();
            splash.progress.setValue(60);
         
            
            new StartupHelper().start();
            
            
            
            // Now Display The UI
            DisplayUI display=new DisplayUI();
            display.setManager(manager);
            display.renderComponenets();
            
        } catch(IOException ioe) {
            try {
                ioe.printStackTrace();
                LogWriter lg=LogWriter.getInstance();
                lg.writeLog("[ERROR]: Method: main\n\tClass: UILauncher.class");
                lg.writeLog("------------------------------------------------");
                lg.writeLog("Exception Stack Trace");
                ioe.printStackTrace(lg.getPrintWriter());
                lg.flush();
            } catch(Exception e) {
                
            }
            
        } catch(Exception ep) {
            ep.printStackTrace();
            
        } catch(Throwable t) {
            t.printStackTrace();
        } 
        
    }
    
    @SuppressWarnings("unused")
	private static void registerShutDownHook() {
        Runtime rt=Runtime.getRuntime();
        rt.addShutdownHook(new Thread(new Runnable(){
            
            public void run() {
                
                Writer w=null;
                try {
                    w=Writer.getWriter("log");
                    String cmd="java.exe -cp .;jdec.jar net.sf.jdec.ui.main.UILauncher";
                    Process p=Runtime.getRuntime().exec(cmd);
                } catch (IOException e) {
                    if(w!=null)
                        e.printStackTrace(w);
                } finally {
                    System.exit(0);
                }
            }
        }));
        
        
    }
    private static UIConfig uiConfig;
    public static UIConfig getUIConfigRef() {
        return uiConfig;
        
    }
    
    
    public static void setMainFrameRef(JFrame main) {
        mainFrame=main;
    }
    
    public static JFrame getMainFrame() {
        return mainFrame;
    }
    
    
    static JFrame mainFrame=null;
    
    public static UIObserver observer=null;
    
    private static void setObserver(UIObserver observer) {
        UILauncher.observer=observer;
        //System.out.println(UILauncher.observer + " Is the Observer Object...");
    }
    
    
    public static UIObserver getObserver() {
        return observer;
    }
    
    public static void setTempFrame(JFrame[] temp) {
        UILauncher.temp=temp;
    }
    
    public static void addTempFrame(JFrame frame) {
        tempa.add(frame);
    }
    public static JFrame[] getTempFrames() {
        return (JFrame[])tempa.toArray(new JFrame[tempa.size()]);
    }
    private static JFrame[] temp;
    private static ArrayList tempa=new ArrayList();;
    
    public static void addMainFrameRefCopy(JFrame frame) {
        copies.add(frame);
    }
    private static ArrayList copies=new ArrayList();
    
    public static ArrayList getAllClones() {
        return copies;
    }
    
    public static JFrame getOriginalFrame() {
        return getMainFrame();
    }
    
    
    static ArrayList childRefs=new ArrayList();
    
    public static void addChildRef(Component c) {
        childRefs.add(c);
    }
    public static List getChildRefs() {
        return childRefs;
    }
    
    public static void closeChildWindows() {
        int total=childRefs.size();
        for(int s=0;s<total;s++) {
            Component x=(Component)childRefs.get(s);
            if(x  instanceof Window && x!=null) //? Is null check needed
            {
                Window w=(Window)x;
                w.setVisible(false);
                w.setEnabled(false);
                w=null;
                x=null;
                
            }
            
        }
        
    }
    
    
    public static volatile boolean showSplash=true;
    public volatile static SplashScreen splash=null;
    
    static class SplashScreenThread extends Thread{
        
        public void run() {
            
            splash=new SplashScreen();
            splash.splash();
            while(showSplash) {
                try{
                    sleep(1000);
                } catch(InterruptedException ie) {
                    showSplash=false;
                }
            }
            splash.dispose();
            splash.setVisible(false);
            splash=null;
        }
        
        
    }
    //Package
}
