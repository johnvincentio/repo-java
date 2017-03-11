 /*
 *  Manager.java Copyright (c) 2006,07 Swaroop Belur
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

import java.awt.Component;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.plaf.multi.MultiTabbedPaneUI;

import net.sf.jdec.ui.config.DecompilerConfigDetails;
import net.sf.jdec.ui.config.UIConfig;
import net.sf.jdec.ui.event.ActionItemListener;
import net.sf.jdec.ui.event.ImageListener;
import net.sf.jdec.ui.event.TabChangeLstener;
import net.sf.jdec.ui.main.UILauncher;
import net.sf.jdec.ui.util.Java;
import net.sf.jdec.ui.util.Javac;
import net.sf.jdec.ui.util.Preferences;
import net.sf.jdec.ui.util.RecentFileList;
import net.sf.jdec.ui.util.SystemProperties;
import net.sf.jdec.ui.util.UIUtil;
import net.sf.jdec.ui.util.editor.TextEditor;
import net.sf.jdec.ui.util.Welcome;



/**
 * @author swaroop belur
 * Class Name:/ Manager
 * Purpose:/ to Register/ UnRegister componenets on the fly
 * Manager is the authority which decides which component
 * gets rendered in the UI finally.
 *
 * This is a singleton class
 *
 */
public class Manager {

    private volatile boolean showProgressBar=false;
    private static Manager manager=null;
    ArrayList registeredComponents=new ArrayList();
    ArrayList UnRegisteredComponents=new ArrayList();
    private ArrayList allUIComponents=new ArrayList();
    private ToolbarItem file=null;
    private ToolbarItem edit=null;
    private ToolbarItem view=null;
    private ToolbarItem diffview=null;
    private ToolbarItem config=null;
    private ToolbarItem window=null;
    private ToolbarItem help=null;
    private ToolbarItem abt=null;
    private ToolbarItem util=null;
    private ToolbarItem  srch=null;

    private Manager()
    {

    }

    public static Manager getManager()
    {
        if(manager==null)  {
            manager=new Manager();
            return manager;
        }
        else{
            return manager;
        }
    }


    public void registerComponenet(UIObject component)
    {
        if(component!=null)
        {
            registeredComponents.add(component);
            validateComponent(component);
        }


    }

    public void UnRegisterComponenet(UIObject component)
    {
        if(component!=null)
        {
            UnRegisteredComponents.add(component);
            invalidateComponent(component);
        }
    }

    private void validateComponent(UIObject component)
    {
        //UnRegisteredComponents.remove(component);
        for(int s=0;s<UnRegisteredComponents.size();s++)
        {
            Object current=UnRegisteredComponents.get(s);
            if(current==component)
            {
                UnRegisteredComponents.set(s,null);
            }
        }
    }

    private void invalidateComponent(UIObject component)
    {
        //registeredComponents.remove(component);

        for(int s=0;s<registeredComponents.size();s++)
        {
            Object current=registeredComponents.get(s);
            if(current==component)
            {
                registeredComponents.set(s,null);
            }
        }

    }


static UIUtil u;

    static{
       
        u=UIUtil.getUIUtil();
    }

    public void createAllComponents(UIObserver observer)
    {
        // Step1 Get the Main Frame
        MainFrame mainFrame=new MainFrame();
       

        // Step2 Get the menuBar
        ToolBar bar=new ToolBar();
        // Now Create All menus Here
        createAllMenusItems(bar);
       
        createActionItems();
       

        AuxToolBar aux=createAuxbar(null);
        //FileInfoFrame frame,JDecStatusFrame statusFrame,JarTree treeFrame,OutputFrame outputframe,Console consoleFrame)
        //Step 4 Create All Components needed by splitPane

        FileInfoFrame info=new FileInfoFrame();
       
        JdecStatusFrame status=new JdecStatusFrame();
        JdecTree dirtree=new JdecTree();
        JTabbedPane drives=new JTabbedPane(SwingConstants.BOTTOM);
        drives.addTab("Dir", dirtree); // Dir-->string used elsewhere 
        drives.addTab("Jar",new JLabel("Jar File Viewer..."));
        String message="Users Favorite Folders will be shown here";
        //message+="\n";
        String message2="[Utilities-->Edit Favorite List]";// for Adding New Favorites";
        
        if(UILauncher.getUIConfigRef().currentFavoriteList().size() == 0)
        	drives.addTab("Favorite Folders",new JLabel(message2));
        else
        {
        	JdecTree jarExploded=new JdecTree(UILauncher.getUIConfigRef().getFavoriteListAsArray());
        	drives.addTab("Favorite Folders",jarExploded);	
        }
        drives.addTab("Class Structure",new JLabel("Class Structure will be shown here"));
        //drives.addTab("Class Structure",new JLabel("Class Structure Viewer..."));
        //drives.addTab("Jdec Project",new JLabel("Project Details"));
        drives.doLayout();
        drives.validate();
        drives.setEnabled(true);
        drives.setVisible(true);
        drives.setSelectedIndex(0);
       
        OutputFrame output=new OutputFrame(observer,"Use This Window To Do Your Editing...","plain");
        //DecompilerConfigDetails decompilerConfig=new DecompilerConfigDetails(u);
        //output.setConfigTable(decompilerConfig);
        Console console=new Console();
       


//      Componenets of Tabbed Pane in the Right Halh of Pane

        OutputFrame DISoutput=new OutputFrame(observer,"Disassmebled Output will be shown here...","plain");
        OutputFrame DECoutput=new OutputFrame(observer,"Decompiled Output will be shown here...","style");
        decoutputframe=DECoutput;
        OutputFrame COMBoutput=new OutputFrame(observer,"For Jvm blocks and corresponding Source code statements ....","plain");
        //OutputFrame =new OutputFrame(observer);
        java.lang.String startupwel=UIConfig.getUIConfig().getPref("ShowWelcomeAtStartUp");
        Welcome welcome=null;
        
        
        if(startupwel==null || startupwel.equalsIgnoreCase("true"))
        {
            welcome=new Welcome("Welcome",UIUtil.getUIUtil().showConfigWindowOnStartUp());
     
        }
        java.lang.String prefix="imgs"+File.separator;
        File img=new File(prefix+"close.gif");
        Icon icon=new ImageIcon(img.getAbsolutePath());
        UIUtil.jdecFolder=img.getAbsolutePath();
        JLabel labIcon=new JLabel();
        labIcon.setIcon(icon);
        //labIcon.addMouseListener()

        JTabbedPane tabs=new JTabbedPane(SwingConstants.TOP);
        tabs.setUI(new MultiTabbedPaneUI());
        tabs.addTab("Jdec Editor Window", output.getComponent());
        tabs.addTab("Disassembled Output",DISoutput.getComponent());
        tabs.addTab("Decompiled Output", DECoutput.getComponent());
        if(welcome!=null)
        {
            tabs.addTab("Jdec Welcome", welcome);
            tabs.setSelectedComponent(welcome);
        }
        tabs.addChangeListener(TabChangeLstener.getListener());
        if(UIUtil.getUIUtil().showConfigWindowOnStartUp()){
          
          DecompilerConfigDetails config = new DecompilerConfigDetails(UILauncher.getUIutil());
          if(tabs.indexOfTab("Jdec Configuration")!=-1)tabs.remove(tabs.indexOfTab("Jdec Configuration"));
          tabs.addTab("Jdec Configuration", config);
          if(welcome==null){
            tabs.setSelectedComponent(config);
          }
          
        }
       


        // Create the splitter
        Splitter splitPane=new Splitter(info,status,drives,tabs,console);
        setSplitterRef(splitPane);
        registerSplitPaneItem(info);
        //registerSplitPaneItem(status);
        registerSplitPaneItem(drives);
        //System.out.println(output + " oUTPUT1");
        registerSplitPaneItem(tabs);
        registerSplitPaneItem(console);

        // Store All References
        allUIComponents.add(mainFrame);
        allUIComponents.add(bar);
        allUIComponents.add(aux);
        allUIComponents.add(splitPane);
        
        //JEditorPane jedp1=(JEditorPane)((JScrollPane)output.getComponent()).getViewport().getView();
        /*jedp1.addMouseListener(new MouseAdapter() {
            
            public void mouseClicked(MouseEvent e)
            {
                JPopupMenu popup=new JPopupMenu();
                popup.setBounds(e.getX(),e.getY(),300,300);
                popup.add("Hello");
                popup.setVisible( true);
            }
        });*/
        
        
       
        if(welcome!=null){
            UILauncher.welcomeCreated=true;
        }
        
    }

    private AuxToolBar createAuxbar(AuxToolBar aux)
    {
        // Step 3 Create Auxilliary Bar
        // Create Aux ToolBar
        if(aux==null)
             aux=new AuxToolBar();
        ImageListener imgl=new ImageListener();
        // Create All components needed By Aux bar
        java.lang.String fsep=File.separator;
        java.lang.String prefix="imgs"+fsep;
        //File img=new File(prefix+"start.gif");
        File img=new File(prefix+"Run.gif");
        File l=new File(".");
        String s[]=l.list();


        //System.out.println(img.getAbsolutePath());
        JButton run=new JButton(new ImageIcon(img.getAbsolutePath()));
        run.setActionCommand("RunJDec");
        run.setToolTipText("Run Jdec");
        run.addActionListener(imgl);
        run.setMargin(new Insets(0,0,0,0));

        img=new File(prefix+"Save.gif");
        JButton save=new JButton(new ImageIcon(img.getAbsolutePath()));
        save.setActionCommand("Save");
        save.addActionListener(imgl);
        save.setToolTipText("Save Work");
        save.setMargin(new Insets(0,0,0,0));





        img=new File(prefix+"newtask.gif");

        JButton newTask=new JButton(new ImageIcon(img.getAbsolutePath()));
        newTask.setToolTipText("New Jdec Task");
        newTask.setActionCommand("NewTask");
        newTask.addActionListener(imgl);
        newTask.setMargin(new Insets(0,0,0,0));

        img=new File(prefix+"newtask.gif");

        JButton format=new JButton("Format Code...(If reqd)");
        format.setToolTipText("Use this if you feel the default formatting was not good enough....");
        format.setActionCommand("Format");
        format.addActionListener(imgl);
        format.setMargin(new Insets(0,0,0,0));




        img=new File(prefix+"tip.gif");
        JButton tip=new JButton(new ImageIcon(img.getAbsolutePath()));
        tip.setToolTipText("Show Tip");
        tip.setActionCommand("tip");
        tip.addActionListener(imgl);
        tip.setMargin(new Insets(0,0,0,0));






        img=new File(prefix+"edit.gif");
        JButton edit=new JButton(new ImageIcon(img.getAbsolutePath()));
        edit.setToolTipText("Edit Output in Editor");
        edit.setActionCommand("editin");
        edit.addActionListener(imgl);
        edit.setMargin(new Insets(0,0,0,0));


        img=new File(prefix+"close.gif");

        /*JButton close=new JButton(new ImageIcon(img.getAbsolutePath()));
        close.setToolTipText("Close Jdec UI");
        close.setActionCommand("close");
        close.addActionListener(imgl);
        close.setMargin(new Insets(0,0,0,0));*/

        img=new File(prefix+"clear2.gif");
        JButton clear=new JButton(new ImageIcon(img.getAbsolutePath()));
        clear.setToolTipText("Clear console frame");
        clear.setActionCommand("clear");
        clear.addActionListener(imgl);
        clear.setMargin(new Insets(0,0,0,0));




        img=new File(prefix+"bin.gif");

        JButton binary=new JButton(new ImageIcon(img.getAbsolutePath()));
        binary.setToolTipText("Show Binary Form of The Class File");
        binary.setActionCommand("binary");
        binary.addActionListener(imgl);
        binary.setMargin(new Insets(0,0,0,0));

        img=new File(prefix+"home2.gif");
        //img=new File("c:\\go.gif");
        /*JButton go=new JButton(new ImageIcon(img.getAbsolutePath()));
        go.setToolTipText("Open Browser");
        go.setActionCommand("go");
        go.addActionListener(imgl);*/


        img=new File(prefix+"Refresh2.gif");

        JButton refresh=new JButton(new ImageIcon(img.getAbsolutePath()));
        refresh.setToolTipText("Refresh");
        refresh.setActionCommand("refresh");
        refresh.addActionListener(imgl);
        refresh.setMargin(new Insets(0,0,0,0));

        String os=System.getProperty("os.name");
        //System.out.println(os);
        boolean unix=true;
        if(os.indexOf("Windows")!=-1)
        {
        	unix=false;
        }
        if(os.indexOf("win")!=-1 && unix)
        {
        	unix=false;
        }
        if(os.indexOf("Win")==-1 && unix)
        {
        	unix=false;
        }
        if(unix==false)
        {
	        addAuxComponent(aux,Manager.auxlabel);
	        addAuxComponent(aux,newTask);
        }
        else
        {
        	addAuxComponent(aux,new JLabel("	 "));
        	addAuxComponent(aux,Manager.auxlabel);
	        addAuxComponent(aux,newTask);
        }
        /*addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,binary);
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));*/
        /*addAuxComponent(aux,open);
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));*/
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,run);
//		/addAuxComponent(aux,stop);
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,save);
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,refresh);
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,edit);
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));

        //addAuxComponent(aux,editor);
        //addAuxComponent(aux,clear);
        /*addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));*/
        
        //addAuxComponent(aux,home);
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));



        //addAuxComponent(aux,print);
        //addAuxComponent(aux,new JLabel("	 "));
        //addAuxComponent(aux,new JLabel("	 "));
        //addAuxComponent(aux,search);
        //addAuxComponent(aux,mail);
        //addAuxComponent(aux,new JLabel("	 "));
        //addAuxComponent(aux,new JLabel("	 "));
        img=new File(prefix+"home2.gif");

                JButton go=new JButton(new ImageIcon(img.getAbsolutePath()));
                go.setToolTipText("Open Browser(Jdec Home)");
                go.setActionCommand("go");
                go.addActionListener(imgl);
        go.setMargin(new Insets(0,0,0,0));

        addAuxComponent(aux,go);
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));

        img=new File(prefix+"back3.gif");
        back=new JButton(new ImageIcon(img.getAbsolutePath()));
        back.setToolTipText("Show previous decompiled File");
        back.setActionCommand("back");
        back.addActionListener(imgl);
        back.setMargin(new Insets(0,0,0,0));
        //back.setEnabled(false);
        

        img=new File(prefix+"forward3.gif");
        fwd=new JButton(new ImageIcon(img.getAbsolutePath()));
        fwd.setToolTipText("Show next decompiled File");
        fwd.setActionCommand("forward");
        fwd.addActionListener(imgl);
        fwd.setMargin(new Insets(0,0,0,0));
        
        img=new File(prefix+"nextdiff.gif");
        nextd=new JButton(new ImageIcon(img.getAbsolutePath()));
        nextd.setToolTipText("Next diff");
        nextd.setActionCommand("Nextdiff");
        nextd.addActionListener(imgl);
        nextd.setMargin(new Insets(0,0,0,0));
        
        img=new File(prefix+"prevdiff.gif");
        prevd=new JButton(new ImageIcon(img.getAbsolutePath()));
        prevd.setToolTipText("Prev diff");
        prevd.setActionCommand("Prevdiff");
        prevd.addActionListener(imgl);
        prevd.setMargin(new Insets(0,0,0,0));
        //fwd.setEnabled(false);

        addAuxComponent(aux,back);
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,fwd);
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,format);
//        addAuxComponent(aux,new JLabel("	 "));
//        addAuxComponent(aux,nextd);
//        addAuxComponent(aux,new JLabel("	 "));
//        addAuxComponent(aux,prevd);
        if(unix==false)
        	shiftHelpButtonsToRight(aux);
        else
        	shiftHelpButtonsToRightForUnix(aux);
        

        addAuxComponent(aux,tip);
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        //addAuxComponent(aux,tech);
        /*addAuxComponent(aux,jdec);
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,sound);
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));*/
        //addAuxComponent(aux,empty);
       // addAuxComponent(aux,close);
        return aux;

    }

    public void removeFromAllUICompList(UIObject component)
    {
        if(component!=null)
        {
            for(int s=0;s<allUIComponents.size();s++)
            {
                Object current=allUIComponents.get(s);
                if(current==component)
                {
                    allUIComponents.set(s,null);
                }
            }
        }

    }

    public void addToAllUICompList(UIObject obj)
    {

        allUIComponents.add(obj);

    }


    public void regitser()  // Modify This Function later if registering/unregistering of components is to be implemented
    {
        for(int s=0;s<allUIComponents.size();s++)
        {
            UIObject c=(UIObject)allUIComponents.get(s);
            registerComponenet(c);

        }

    }

    public ArrayList getRegisteredComponents() {
        return registeredComponents;
    }


    public void reCreateAuxbar(AuxToolBar aux)
    {
        aux.getAuxBar().removeAll();
        createAuxbar(aux);
        /*ImageListener imgl=new ImageListener();
        // Create All components needed By Aux bar
        java.lang.String fsep=File.separator;
        java.lang.String prefix="imgs"+fsep;
        File img=new File(prefix+"start.gif");

        JButton run=new JButton(new ImageIcon(img.getAbsolutePath()));
        run.setActionCommand("RunJDec");
        run.setToolTipText("Run Jdec");
        run.addActionListener(imgl);
        run.setMargin(new Insets(0,0,0,0));

        img=new File(prefix+"save.gif");
        JButton save=new JButton(new ImageIcon(img.getAbsolutePath()));
        save.setActionCommand("Save");
        save.addActionListener(imgl);
        save.setToolTipText("Save Work");
        save.setMargin(new Insets(0,0,0,0));

        img=new File(prefix+"home2.gif");//gryhome.gif");
        JButton home=new JButton(new ImageIcon(img.getAbsolutePath()));
        home.setToolTipText("Jdec Home");
        home.setActionCommand("Home");
        home.addActionListener(imgl);
        home.setMargin(new Insets(0,0,0,0));

        img=new File(prefix+"grymail.gif");
        JButton mail=new JButton(new ImageIcon(img.getAbsolutePath()));
        mail.setToolTipText("Send Mail");
        mail.setActionCommand("Send Mail");
        mail.addActionListener(imgl);


        img=new File(prefix+"search.gif");
        JButton search=new JButton(new ImageIcon(img.getAbsolutePath()));
        search.setToolTipText("Search");
        search.setActionCommand("Search");
        search.addActionListener(imgl);


        img=new File(prefix+"open.gif");
        JButton open=new JButton(new ImageIcon(img.getAbsolutePath()));
        open.setToolTipText("Open File");
        open.setActionCommand("Open");
        open.addActionListener(imgl);
        open.setMargin(new Insets(0,0,0,0));

        img=new File(prefix+"Computer.gif");
        JButton fs=new JButton(new ImageIcon(img.getAbsolutePath()));
        fs.setToolTipText("Show File System");
        fs.setActionCommand("FileSystem");
        fs.addActionListener(imgl);


        img=new File(prefix+"Help.gif");
        JButton help=new JButton(new ImageIcon(img.getAbsolutePath()));
        help.setToolTipText("Show Help");
        help.setActionCommand("Help");
        help.addActionListener(imgl);
        help.setMargin(new Insets(0,0,0,0));



        img=new File(prefix+"newtask.gif");
        JButton newTask=new JButton(new ImageIcon(img.getAbsolutePath()));
        newTask.setToolTipText("New Jdec Task");
        newTask.setActionCommand("NewTask");
        newTask.addActionListener(imgl);
        newTask.setMargin(new Insets(0,0,0,0));



        img=new File(prefix+"cofee.gif");
        JButton tech=new JButton(new ImageIcon(img.getAbsolutePath()));
        tech.setToolTipText("Technology");
        tech.setActionCommand("tech");
        tech.addActionListener(imgl);


        img=new File(prefix+"Tip.gif");
        JButton tip=new JButton(new ImageIcon(img.getAbsolutePath()));
        tip.setToolTipText("Show Tip");
        tip.setActionCommand("tip");
        tip.addActionListener(imgl);
        tip.setMargin(new Insets(0,0,0,0));


        img=new File(prefix+"stop.gif");
        JButton stop=new JButton(new ImageIcon(img.getAbsolutePath()));
        stop.setToolTipText("Terminate Jdec");
        stop.setActionCommand("Stop");
        stop.addActionListener(imgl);


        /*img=new File(prefix+"print.gif");
        JButton print=new JButton(new ImageIcon(img.getAbsolutePath()));
        print.setToolTipText("Print..");
        print.setActionCommand("print");
        print.addActionListener(imgl);

        //img=new File(prefix+"edit.gif");
        img=new File("c:/temp.gif");
        JButton edit=new JButton(new ImageIcon("c:/temp.gif"));
        edit.setToolTipText("Edit Output in Editor");
        edit.setActionCommand("editin");
        edit.addActionListener(imgl);
        edit.setMargin(new Insets(0,0,0,0));


        //img=new File(prefix+"close.gif");
        img=new File(prefix+"close.gif");
        JButton close=new JButton(new ImageIcon(img.getAbsolutePath()));
        close.setToolTipText("Close Jdec UI");
        close.setActionCommand("close");
        close.addActionListener(imgl);
        close.setMargin(new Insets(0,0,0,0));

        img=new File(prefix+"clear2.gif");
        JButton clear=new JButton(new ImageIcon(img.getAbsolutePath()));
        clear.setToolTipText("Clear console frame");
        clear.setActionCommand("clear");
        clear.addActionListener(imgl);
       clear.setMargin(new Insets(0,0,0,0));


        img=new File(prefix+"editor.gif");
        JButton editor=new JButton(new ImageIcon(img.getAbsolutePath()));
        editor.setToolTipText("Open Separate Editor");
        editor.setActionCommand("editor");
        editor.addActionListener(imgl);
        editor.setMargin(new Insets(0,0,0,0));

        img=new File(prefix+"sound.gif");
        JButton sound=new JButton(new ImageIcon(img.getAbsolutePath()));
        sound.setToolTipText("Describe Me");
        sound.setActionCommand("sound");
        sound.addActionListener(imgl);

        img=new File(prefix+"binary.gif");
        JButton binary=new JButton(new ImageIcon(img.getAbsolutePath()));
        binary.setToolTipText("Show Binary Form of The Class File");
        binary.setActionCommand("binary");
        binary.addActionListener(imgl);
        binary.setMargin(new Insets(0,0,0,0));

        img=new File(prefix+"home2.gif");
        JButton go=new JButton(new ImageIcon(img.getAbsolutePath()));
        go.setToolTipText("Open Browser");
        go.setActionCommand("go");
        go.addActionListener(imgl);
        go.setMargin(new Insets(0,0,0,0));


        img=new File(prefix+"Refresh2.gif");
        JButton refresh=new JButton(new ImageIcon(img.getAbsolutePath()));
        refresh.setToolTipText("Refresh");
        refresh.setActionCommand("refresh");
        refresh.addActionListener(imgl);
        refresh.setMargin(new Insets(0,0,0,0));


        img=new File(prefix+"jdec.gif");
        JButton jdec=new JButton(new ImageIcon(img.getAbsolutePath()));
        jdec.setToolTipText("Teach me How Decompiler works");
        jdec.setActionCommand("jdec");
        jdec.addActionListener(imgl);


       // JButton empty=new JButton(new ImageIcon("e://sbelur/Images/whexpbar.gif"));
        //empty.setToolTipText("Teach me How Decompiler works");



        String os=System.getProperty("os.name");
        boolean unix=true;
        if(os.indexOf("Windows")!=-1)
        {
        	unix=false;
        }
        if(os.indexOf("win")!=-1 && unix)
        {
        	unix=false;
        }
        if(os.indexOf("Win")==-1 && unix)
        {
        	unix=false;
        }
        if(unix==false)
        {
	        addAuxComponent(aux,Manager.auxlabel);
	        addAuxComponent(aux,newTask);
        }
        else
        {
        	addAuxComponent(aux,new JLabel("	 "));
        	addAuxComponent(aux,Manager.auxlabel);
	        addAuxComponent(aux,newTask);
        }
        /*addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,binary);
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        /*addAuxComponent(aux,open);
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,run);
//		/addAuxComponent(aux,stop);
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,save);
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,refresh);
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,edit);
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));

        //addAuxComponent(aux,editor);
        /*addAuxComponent(aux,clear);
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,home);
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        //addAuxComponent(aux,print);
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        //addAuxComponent(aux,search);
        //addAuxComponent(aux,mail);
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,go);
        if(unix==false)
        	shiftHelpButtonsToRight(aux);
        else
        	shiftHelpButtonsToRightForUnix(aux);

        addAuxComponent(aux,tip);
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        //addAuxComponent(aux,tech);
        /*addAuxComponent(aux,jdec);
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,sound);
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        //addAuxComponent(aux,empty);
        addAuxComponent(aux,close);*/
                


    }

    private void createAllMenusItems(ToolBar bar)
    {
        ToolbarItem file=new ToolbarItem("File ");
        file.getItem().setMnemonic('f');
        setFileMenu(file);
        ToolbarItem edit=new ToolbarItem("Edit ");
        edit.getItem().setMnemonic('e');
        setEditMenu(edit);
        ToolbarItem srch=new ToolbarItem("Search ");
        srch.getItem().setMnemonic('s');
        setSearchMenu(srch);
        ToolbarItem view=new ToolbarItem("View ");
        view.getItem().setMnemonic('v');
        setViewMenu(view);
        ToolbarItem diffview=new ToolbarItem("Diff View");
        diffview.getItem().setMnemonic('d');
        setDiffViewMenu(diffview);
        ToolbarItem config=new ToolbarItem("Configuration ");
        config.getItem().setMnemonic('c');
        setConfigMenu(config);

        ToolbarItem window=new ToolbarItem("Window");
        window.getItem().setMnemonic('w');
        setWindowMenu(window);

        ToolbarItem util=new ToolbarItem("Utilities ");

        util.getItem().setMnemonic('u');
        setUtilMenu(util);
        ToolbarItem help=new ToolbarItem("Help ");
        help.getItem().setMnemonic('h');
        setHelpMenu(help);
        ToolbarItem abt=new ToolbarItem("About ");
        abt.getItem().setMnemonic('a');
        setAbtMenu(abt);
        ToolbarItem space=new ToolbarItem("							");

        bar.getBar().add(file.getItem());

        bar.getBar().add(space.getItem());
        bar.getBar().add(space.getItem());

        bar.getBar().add(edit.getItem());

        bar.getBar().add(space.getItem());
        bar.getBar().add(space.getItem());

        bar.getBar().add(srch.getItem());

        bar.getBar().add(space.getItem());
        bar.getBar().add(space.getItem());

        bar.getBar().add(view.getItem());

        bar.getBar().add(space.getItem());
        bar.getBar().add(space.getItem());

        /*bar.getBar().add(diffview.getItem());

        bar.getBar().add(space.getItem());
        bar.getBar().add(space.getItem());*/
        
        bar.getBar().add(config.getItem());

        bar.getBar().add(space.getItem());
        bar.getBar().add(space.getItem());

        bar.getBar().add(window.getItem());

        bar.getBar().add(space.getItem());
        bar.getBar().add(space.getItem());

        bar.getBar().add(util.getItem());

        bar.getBar().add(space.getItem());
        bar.getBar().add(space.getItem());

        bar.getBar().add(help.getItem());

        bar.getBar().add(space.getItem());
        bar.getBar().add(space.getItem());

        bar.getBar().add(abt.getItem());

    }



    private void createActionItems()
    {
      
        if(this.file!=null)
        {
            ActionItem New=new ActionItem("New Decompiler task...");
            ActionItem Open=new ActionItem("Open File...");
            ActionItem newJava=new ActionItem("New Java File...");
            ActionItem refresh=new ActionItem("Refresh...");
            ActionItem save=new ActionItem("Save");
            ActionItem saveas=new ActionItem("SaveAs");
            ActionItem viewJava=new ActionItem("View Java File");
            //ActionItem print=new ActionItem("Print...");
            //ActionItem preview=new ActionItem("Print Preview...");
            ActionItem recent=new ActionItem("Show Recent File(s)...");
            ActionItem close=new ActionItem("Close UI...");

            this.file.getItem().add(New.getItem());
            //this.file.getItem().add(Open.getItem());
            this.file.getItem().add(newJava.getItem());
            this.file.getItem().add(viewJava.getItem());
            this.file.getItem().add(refresh.getItem());
            this.file.getItem().add(save.getItem());
            this.file.getItem().add(saveas.getItem());
            //this.file.getItem().add(print.getItem());
            //this.file.getItem().add(preview.getItem());
            this.file.getItem().add(recent.getItem());
            this.file.getItem().add(new JSeparator());
            this.file.getItem().add(close.getItem());



            New.getItem().setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
            newJava.getItem().setAccelerator(KeyStroke.getKeyStroke("ctrl J"));
            viewJava.getItem().setAccelerator(KeyStroke.getKeyStroke("ctrl shift V"));
            refresh.getItem().setAccelerator(KeyStroke.getKeyStroke("ctrl R"));
            save.getItem().setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
            recent.getItem().setAccelerator(KeyStroke.getKeyStroke("ctrl shift R"));
            close.getItem().setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
        }
        if(this.edit!=null)
        {
            ActionItem copy=new ActionItem("Copy...");
            ActionItem cut=new ActionItem("Cut...");
            ActionItem paste=new ActionItem("Paste...");
            ActionItem selectAll=new ActionItem("Select All...");
            ActionItem pref=new ActionItem("Preferences...");
            ActionItem archsettings=new ActionItem("Archive settings...");
            this.edit.getItem().add(copy.getItem());
            this.edit.getItem().add(cut.getItem());
            this.edit.getItem().add(paste.getItem());
            this.edit.getItem().add(selectAll.getItem());
            this.edit.getItem().add(new JSeparator());
            this.edit.getItem().add(pref.getItem());
            this.edit.getItem().add(archsettings.getItem());
            


            copy.getItem().setAccelerator(KeyStroke.getKeyStroke("ctrl C"));
            cut.getItem().setAccelerator(KeyStroke.getKeyStroke("ctrl X"));
            paste.getItem().setAccelerator(KeyStroke.getKeyStroke("ctrl V"));
            selectAll.getItem().setAccelerator(KeyStroke.getKeyStroke("ctrl A"));
            pref.getItem().setAccelerator(KeyStroke.getKeyStroke("ctrl P"));

        }
        if(this.srch!=null)
        {
            ActionItem find=new ActionItem("Find ...");
            ActionItem rep=new ActionItem("Replace..");

            this.srch.getItem().add(find.getItem());
            find.getItem().setAccelerator(KeyStroke.getKeyStroke("ctrl F"));
        }
        
        if(this.view!=null)
        {
            ActionItem excp=new ActionItem("Exception Table Details...");
            ActionItem cp=new ActionItem("Constant Pool...");
            ActionItem line=new ActionItem("Class Line Number Info...");
            ActionItem local=new ActionItem("Class Local Variables...");
            ActionItem ginfo=new ActionItem("Class General   Information...");
            this.view.getItem().add(ginfo.getItem());
            this.view.getItem().add(cp.getItem());
            //this.view.getItem().add(excp.getItem());
            this.view.getItem().add(local.getItem());
            this.view.getItem().add(new JSeparator());
            ToolbarItem logs=new ToolbarItem("Log Files");
            ActionItem uilog=new ActionItem("Open UI LogFile");
            ActionItem declog=new ActionItem("Open Decompiler LogFile");
            ActionItem both=new ActionItem("Open Both in Separate Tabs");
            ActionItem methods=new ActionItem("Show Methods For Current Class");
            currentmethods=methods;
            methods.getItem().setActionCommand("showmethods");
            logs.getItem().add(uilog.getItem());
            logs.getItem().add(declog.getItem());
            logs.getItem().add(new JSeparator());
            logs.getItem().add(both.getItem());
            logs.getItem().add(new JSeparator());
            this.view.getItem().add(methods.getItem());
            this.view.getItem().add(new JSeparator());
            this.view.getItem().add(logs.getItem());
                        excp.getItem().setAccelerator(KeyStroke.getKeyStroke("ctrl E"));
            cp.getItem().setAccelerator(KeyStroke.getKeyStroke("ctrl shift C"));
            //line.getItem().setAccelerator(KeyStroke.getKeyStroke("ctrl shift C"));
            local.getItem().setAccelerator(KeyStroke.getKeyStroke("ctrl shift L"));
            ginfo.getItem().setAccelerator(KeyStroke.getKeyStroke("ctrl shift G"));
            uilog.getItem().setAccelerator(KeyStroke.getKeyStroke("ctrl shift U"));
            declog.getItem().setAccelerator(KeyStroke.getKeyStroke("ctrl shift D"));
            both.getItem().setAccelerator(KeyStroke.getKeyStroke("ctrl shift B"));
            methods.getItem().setAccelerator(KeyStroke.getKeyStroke("ctrl M"));

        }
        
        if(this.diffview!=null)
        {
            ActionItem all=new ActionItem("Show All ...");
            all.getItem().setActionCommand("showall");
            ActionItem differ=new ActionItem("Show Just Differences");
            differ.getItem().setActionCommand("showdiff");
            ActionItem match=new ActionItem("Show Just Matching");
            match.getItem().setActionCommand("showmatch");
            ActionItem colorf=new ActionItem("Change Color/Font  Configuration");
            colorf.getItem().setActionCommand("changecolorfnt");
            
            this.diffview.getItem().add(all.getItem());
            this.diffview.getItem().add(differ.getItem());
            this.diffview.getItem().add(match.getItem());
            this.diffview.getItem().add(new JSeparator());
            this.diffview.getItem().add(colorf.getItem());
            
        }
        
        if(this.window!=null)
        {
            ActionItem hideConfig=new ActionItem("Reset Tabs...");
            ToolbarItem console=new ToolbarItem("Console");
            ActionItem hideConsole=new ActionItem("Hide Console");
            ActionItem showConsole=new ActionItem("Show Console");
            ActionItem hideConfig2=new ActionItem("Hide All Configuration Tabs...");
            console.getItem().add(showConsole.getItem());
            console.getItem().add(hideConsole.getItem());
            this.window.getItem().add(console.getItem());
            this.window.getItem().add(hideConfig.getItem());
        }


        if(this.config!=null)
        {
            ActionItem config=new ActionItem("Jdec(Decompiler) Configuration ...");
            ToolbarItem look=new ToolbarItem("UI Look And Feel...");




            ActionItem extraConfig=new ActionItem("Jdec(Decompiler) Filters");
            extraConfig.getItem().setActionCommand("extra");

            ActionItem syn=new ActionItem("Syntax and Coloring");


            //this.config.getItem().add(look.getItem());
             this.config.getItem().add(config.getItem());
             this.config.getItem().add(config.getItem());
            this.config.getItem().add(extraConfig.getItem());
            this.config.getItem().add(syn.getItem());
            //this.config.getItem().add(new JSeparator());

            // Look and Feel Items
            ActionItem gtk=new ActionItem("GTKLookAndFeel");
            ActionItem windows=new ActionItem("WindowsLookAndFeel");
            ActionItem metal=new ActionItem("MetalLookAndFeel");
            ActionItem motif=new ActionItem("MotifLookAndFeel");



            look.getItem().add(gtk.getItem());
            look.getItem().add(windows.getItem());
            look.getItem().add(metal.getItem());
            look.getItem().add(motif.getItem());


            config.getItem().setAccelerator(KeyStroke.getKeyStroke("alt shift A"));
            //setproject.getItem().setAccelerator(KeyStroke.getKeyStroke("alt shift P"));
            gtk.getItem().setAccelerator(KeyStroke.getKeyStroke("shift G"));
            windows.getItem().setAccelerator(KeyStroke.getKeyStroke("shift W"));
            metal.getItem().setAccelerator(KeyStroke.getKeyStroke("shift M"));
            motif.getItem().setAccelerator(KeyStroke.getKeyStroke("shift O"));






        }
        if(this.util!=null)
        {
            //ActionItem browser=new ActionItem("Open Simple Web browser ...");
            //ActionItem wpr=new ActionItem("Open Simple Word Processor ...");
            ActionItem sed=new ActionItem("Open Simple Editor...");
            ActionItem ced=new ActionItem("Show In Custom Editor....");
            ced.getItem().setActionCommand("customed");

            //ActionItem cal=new ActionItem("Open Simple Calculator...");
            ActionItem javac=new ActionItem("Compile Java...");
            ActionItem java=new ActionItem("Run Java Application...");
            ActionItem findj=new ActionItem("Find Jar");
            //ActionItem jar=new ActionItem("Create Jar...");
            ActionItem clipb=new ActionItem("Show Clipboard");
            ActionItem props=new ActionItem("System Properties");
            ActionItem home=new ActionItem("GoTo User Home Folder");
            proj=new ToolbarItem("Favorite list");
            proj.getItem().addActionListener(ActionItemListener.getListener());
            ArrayList list=UIUtil.getUIUtil().getFavoriteList();
            if(list.size()==0)
            {
            	proj.getItem().add(new JMenuItem("Empty"));
            }
            else
            {
            	proj.getItem().removeAll();
				for(int x=0;x<list.size();x++)
				{
					String t=(String)list.get(x);
					JMenuItem m=new JMenuItem(t);
					Manager.getManager().proj.getItem().add(m);
					
				}
            	
            }
            ActionItem setproject=new ActionItem("Edit Favorite List");
            ActionItem clear=new ActionItem("Clear History");
            ActionItem javadoc=new ActionItem("Javadoc");
            //ActionItem Zip=new ActionItem("Zip/Unzip Files");
            ActionItem find=new ActionItem("Find Target");
            ActionItem systime=new ActionItem("Show System Time");
            ActionItem unshow=new ActionItem("Hide System Time");

            sed.getItem().setAccelerator(KeyStroke.getKeyStroke("alt shift E"));
            javac.getItem().setAccelerator(KeyStroke.getKeyStroke("alt shift C"));
            java.getItem().setAccelerator(KeyStroke.getKeyStroke("alt shift R"));
            clipb.getItem().setAccelerator(KeyStroke.getKeyStroke("alt shift B"));
            props.getItem().setAccelerator(KeyStroke.getKeyStroke("alt shift S"));
            home.getItem().setAccelerator(KeyStroke.getKeyStroke("alt shift H"));
            //proj.getItem().setAccelerator(KeyStroke.getKeyStroke("alt shift G"));
            //setproject.getItem().setAccelerator(KeyStroke.getKeyStroke("alt shift P"));
            clear.getItem().setAccelerator(KeyStroke.getKeyStroke("alt shift W"));
            javadoc.getItem().setAccelerator(KeyStroke.getKeyStroke("alt shift J"));
            find.getItem().setAccelerator(KeyStroke.getKeyStroke("alt shift F"));
            systime.getItem().setAccelerator(KeyStroke.getKeyStroke("alt shift T"));
            unshow.getItem().setAccelerator(KeyStroke.getKeyStroke("alt shift U"));



//			this.util.getItem().add(browser.getItem());
            //this.util.getItem().add(wpr.getItem());
            this.util.getItem().add(sed.getItem());
            this.util.getItem().add(ced.getItem());
            //this.util.getItem().add(syn.getItem());
            //sed.getItem().addActionListener(new ActionItemListener());
            this.util.getItem().add(new JSeparator());
            //this.util.getItem().add(cal.getItem());
            this.util.getItem().add(clipb.getItem());
            this.util.getItem().add(props.getItem());
            this.util.getItem().add(new JSeparator());
            this.util.getItem().add(home.getItem());
            this.util.getItem().add(proj.getItem());
            this.util.getItem().add(setproject.getItem());
            this.util.getItem().add(new JSeparator());
            //this.util.getItem().add(clear.getItem());
            //this.util.getItem().add(Zip.getItem());
            this.util.getItem().add(find.getItem());
            this.util.getItem().add(new JSeparator());

            this.util.getItem().add(systime.getItem());
            this.util.getItem().add(unshow.getItem());
            this.util.getItem().add(new JSeparator());


            this.util.getItem().add(javac.getItem());
            this.util.getItem().add(java.getItem());
            this.util.getItem().add(findj.getItem());
            //this.util.getItem().add(jar.getItem());
            //this.util.getItem().add(javadoc.getItem());


            java.lang.String projPath=UILauncher.getUIutil().getUserProjFol();
            /*if(projPath==null || projPath.trim().length()==0)
            {
                proj.getItem().setEnabled(false);
            }*/


        }
        if(this.help!=null)
        {
            ActionItem tips=new ActionItem("Show Jdec Tips...");
            ActionItem full=new ActionItem("Open Full help");
            full.getItem().setActionCommand("fullHelp");
            ActionItem keys=new ActionItem("Key Assist");
            
            ActionItem howToUse=new ActionItem("Learn How to Use Jdec In 2 mins...");
            howToUse.getItem().setActionCommand("TwoMins");
            ActionItem insights=new ActionItem("Some Insights on Decompiler...");

            //this.help.getItem().add(howToUse.getItem());
            this.help.getItem().add(full.getItem());
            this.help.getItem().add(new JSeparator());
            this.help.getItem().add(tips.getItem());
            this.help.getItem().add(keys.getItem());
            tips.getItem().setActionCommand("jdec_tips");
            keys.getItem().setActionCommand("keys");
            keys.getItem().setAccelerator(KeyStroke.getKeyStroke("ctrl shift F"));
            //this.help.getItem().add(insights.getItem());
            tips.getItem().setAccelerator(KeyStroke.getKeyStroke("ctrl T"));


        }
        if(this.abt!=null)
        {
            ActionItem whatIs=new ActionItem("What Is Jdec ?");
            ActionItem feedbck=new ActionItem("Send Feedback");
            ActionItem licence=new ActionItem("View Licence Info");
            licence.getItem().setActionCommand("ViewLicenceInfo");
            ActionItem submit=new ActionItem("How Do I Submit a bug ?");
            submit.getItem().setActionCommand("bugsubmit");
            
            ActionItem wc=new ActionItem("Welcome");
            wc.getItem().setActionCommand("welcome");
            ActionItem contribute=new ActionItem("Finally ... How Do I Contribute to Jdec");
            contribute.getItem().setActionCommand("contr");
            this.abt.getItem().add(whatIs.getItem());
            this.abt.getItem().add(wc.getItem());
            this.abt.getItem().add(licence.getItem());
            this.abt.getItem().add(new JSeparator());
            this.abt.getItem().add(submit.getItem());
            this.abt.getItem().add(feedbck.getItem());



        }



    }


    private void setFileMenu(ToolbarItem file)
    {
        this.file=file;
    }
    private void setEditMenu(ToolbarItem edit)
    {
        this.edit=edit;
    }
    private void setViewMenu(ToolbarItem view)
    {
        this.view=view;
    }
    private void setDiffViewMenu(ToolbarItem view)
    {
        this.diffview=view;
    }

    private void setWindowMenu(ToolbarItem window)
    {
        this.window=window;
    }
    private void setConfigMenu(ToolbarItem config)
    {
        this.config=config;
    }
    private void setUtilMenu(ToolbarItem util)
    {
        this.util=util;
    }
    private void setHelpMenu(ToolbarItem help)
    {
        this.help=help;
    }
    private void setAbtMenu(ToolbarItem abt)
    {
        this.abt=abt;
    }

    private void setSearchMenu(ToolbarItem srch)
    {
        this.srch=srch;
    }

    private void addAuxComponent(AuxToolBar aux,Component c)
    {
        aux.getAuxBar().add(c);
    }

    
    private void shiftHelpButtonsToRightForUnix(AuxToolBar aux){
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        //addAuxComponent(aux,fs);
        //addAuxComponent(aux,help);
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
       
    }


    private void shiftHelpButtonsToRight(AuxToolBar aux){
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        //addAuxComponent(aux,fs);
        //addAuxComponent(aux,help);
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 "));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));
        addAuxComponent(aux,new JLabel("	 	"));


    }

    private ArrayList allSplitPaneItems=new ArrayList();
    public void registerSplitPaneItem(Object item)
    {

        allSplitPaneItems.add(item);

    }

    public void nullifySplitPaneItems()
    {

        for(int s=0;s<allSplitPaneItems.size();s++)
        {
            allSplitPaneItems.set(s,null);
        }

    }


    public ArrayList getCurrentSplitPaneComponents()
    {

        return allSplitPaneItems;
    }

    public void updateSplitPaneItems(ArrayList update)
    {
        allSplitPaneItems=new ArrayList(update);
        getSplitterRef().recreateSplitter(allSplitPaneItems);


    }
    public static void resetOutputFrame(java.lang.String type)
    {
        UIObserver observer=UILauncher.getObserver();
        if(observer!=null)
        {
            observer.resetOutputFrame(type);
        }
    }

    public void setSplitterRef(Splitter splitPane)
    {
        SplitterRef=splitPane;
    }

    private Splitter SplitterRef=null;


    public Splitter getSplitterRef()
    {
        return SplitterRef;
    }

    static TextEditor editor=null;

    public static TextEditor getEditor() {
        return editor;
    }

    public static void setEditor(TextEditor editor) {
        Manager.editor = editor;
    }

    public volatile boolean showTime=false;

    public void setSystemPropRef(SystemProperties prop)
    {
        this.prop=prop;
    }

    SystemProperties prop=null;

    public SystemProperties getSystemPropertyRef()
    {
        return prop;
    }
    private RecentFileList recentFilelistRef=null;

    public void setRecentFileRef(RecentFileList ref)
    {
        recentFilelistRef=ref;
    }
    public RecentFileList getRecentFileListRef()
    {
        return recentFilelistRef;
    }
    public void setPreferencesRef(Preferences prefs)
    {
            pref=prefs;
    }

    public Preferences getPrefRef()
    {
        return pref;
    }
    public void setJavacRef(Javac javac)
    {
        this.javac=javac;
    }
    public void setJavaRef(Java java)
    {
        javaRef=java;
    }
    private Java javaRef;
    private Preferences pref=null;
    private Javac javac=null;

    public ArrayList getAllUIComponents() {
        return allUIComponents;
    }


    private boolean versionprogressbar=false;
    
    
    /**
     * @return Returns the showProgressBar.
     */
    public boolean isShowProgressBar() {
        return showProgressBar;
    }
    /**
     * @param showProgressBar The showProgressBar to set.
     */
    public void setShowProgressBar(boolean showProgressBar) {
        this.showProgressBar = showProgressBar;
    }
    public void setStatusThread(Thread t)
    {
        status=t;
    }
    public Thread getStatusThread(){return status;}
    private Thread status=null;
    private JFrame statusFrame=null;
    public void setStatusFrame(JFrame t)
    {
        statusFrame=t;
    }
    public JFrame getStatusFrame(){return statusFrame;}
    Thread worker=null;
    Thread statusThread=null;
    public void setWorkerThreadRef(Thread t)
    {
        worker=t;

    }
    public void setStatusThreadRef(Thread t)
    {
        statusThread=t;

    }
    public Thread getStatusThreadRef()
    {
        return statusThread;
    }


    /**
     * @return Returns the worker.
     */
    public Thread getWorker() {
        return worker;
    }
    
    public JButton fwd=null;
    public JButton back=null;
    public JButton nextd=null;
    public JButton prevd=null;
    public static  JLabel auxlabel=new JLabel("");
    public ToolbarItem proj;

	public boolean isVersionprogressbar() {
		return versionprogressbar;
	}

	public void setVersionprogressbar(boolean versionprogressbar) {
		this.versionprogressbar = versionprogressbar;
	}
	public ActionItem currentmethods=null;
        public OutputFrame decoutputframe=null;
}