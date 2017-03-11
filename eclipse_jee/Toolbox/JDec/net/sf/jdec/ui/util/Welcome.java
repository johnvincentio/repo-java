/*
 * Welcome.java
 *
 * Created on November 20, 2006,07, 9:20 AM
 */

package net.sf.jdec.ui.util;




import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.io.File;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import net.sf.jdec.ui.config.DecompilerConfigDetails;
import net.sf.jdec.ui.config.UIConfig;
import net.sf.jdec.ui.main.UILauncher;
import net.sf.jdec.ui.util.highlight.CategoryChooser;

/**
 *
 * @author  sbelur
 */
public class Welcome extends javax.swing.JInternalFrame{
    
    private boolean blink=false;
  
    /** Creates new form Welcome */
    public Welcome(String s,boolean blink) {
        super(s);
        this.blink=blink;
        initComponents();
        setVisible(true);
    }
    
    
    private void initComponents() {
        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        
        
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.X_AXIS));
        
        //jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Get Familiar"));
        jLabel1.setText("<html><body><a href=\"\">In 2 Mins</a></body></html>");
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12));
        jLabel1.setToolTipText("Learn to use jdec in 2 mins [Main Functionality]");
        jLabel1.addMouseListener(new MouseAdapter() {
            
            public void mouseClicked(MouseEvent me){
                jLabel1.setCursor(new Cursor(Cursor.HAND_CURSOR));
                QuickTutorial two=new QuickTutorial("Coffee Break Tutorial");
            }
            public void mouseEntered(MouseEvent me){
                jLabel1.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(MouseEvent me){
                jLabel1.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        
        
        
        jLabel2.setText("<html><body><a href=\"\">Whats New</a></body></html>");
        jLabel2.setToolTipText("Check out the new features for this release");
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12));
        jLabel2.addMouseListener(new MouseAdapter() {
            
            public void mouseClicked(MouseEvent me){
                jLabel2.setCursor(new Cursor(Cursor.HAND_CURSOR));
                WhatsNew wn=new WhatsNew();
                wn.createMe();
            }
            public void mouseEntered(MouseEvent me){
                jLabel2.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(MouseEvent me){
                jLabel2.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        jLabel4.setText("<html><body><a href=\"\">Read FAQ</a></body></html>");
        jLabel4.setToolTipText("Read some FAQs prepared by the jdec admin");
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12));
        jLabel4.addMouseListener(new MouseAdapter() {
            
            public void mouseClicked(MouseEvent me){
                jLabel4.setCursor(new Cursor(Cursor.HAND_CURSOR));
                FAQ faq=new FAQ();
            }
            public void mouseEntered(MouseEvent me){
                jLabel4.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(MouseEvent me){
                jLabel4.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        JLabel empty1=new JLabel("          ");
        JLabel empty2=new JLabel("          ");
        JLabel empty3=new JLabel("          ");
        JLabel empty4=new JLabel("          ");
        JLabel empty5=new JLabel("          ");
        JLabel empty6=new JLabel("          ");
        JLabel empty7=new JLabel("          ");
        JLabel empty8=new JLabel("          ");
        JLabel empty9=new JLabel("          ");
        JLabel empty10=new JLabel("          ");
        JLabel empty11=new JLabel("          ");
        JLabel empty12=new JLabel("          ");
        JLabel empty13=new JLabel("          ");
        JLabel empty14=new JLabel("          ");
        JLabel empty15=new JLabel("          ");
        JLabel empty16=new JLabel("          ");
        JLabel empty17=new JLabel("          ");
        JLabel empty18=new JLabel("          ");
        JLabel empty19=new JLabel("          ");
        
        JLabel empty20=new JLabel("          ");
        JLabel empty21=new JLabel("          ");
        JLabel empty22=new JLabel("          ");
        JLabel empty23=new JLabel("          ");
        JLabel empty24=new JLabel("          ");
        JLabel empty25=new JLabel("          ");
        JLabel empty26=new JLabel("          ");
        JLabel empty27=new JLabel("          ");
        JLabel empty28=new JLabel("          ");
        JLabel empty29=new JLabel("          ");
        JLabel empty30=new JLabel("          ");
        JLabel empty31=new JLabel("          ");
        JLabel empty32=new JLabel("          ");
        
        JLabel empty33=new JLabel("          ");
        JLabel empty34=new JLabel("          ");
        JLabel empty35=new JLabel("          ");
        
        JLabel empty36=new JLabel("          ");
        JLabel empty37=new JLabel("          ");
        JLabel empty38=new JLabel("          ");
        
        
        JLabel empty39=new JLabel("          ");
        JLabel empty40=new JLabel("          ");
        JLabel empty41=new JLabel("          ");
        
        JLabel empty42=new JLabel("          ");
        JLabel empty43=new JLabel("          ");
        
        jLabel5.setBackground(java.awt.SystemColor.controlHighlight);
        jLabel5.setFont(new java.awt.Font("Courier New", 3, 16));
        jLabel5.setForeground(new java.awt.Color(204, 0, 0));
        jLabel5.setText("Get Familiar");
        jLabel5.setOpaque(true);
        
        jLabel3.setText("<html><body><a href=\"\">Read Help</a></body></html>");
        jLabel3.setToolTipText("Read detailed help explaining all features of jdec...");
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12));
        jLabel3.addMouseListener(new MouseAdapter() {
            
            public void mouseClicked(MouseEvent me){
                jLabel3.setCursor(new Cursor(Cursor.HAND_CURSOR));
                File bp=UILauncher.getUIutil().getBrowserPath();
                if(bp!=null){
                    try{
                        java.lang.String cmd=bp.getAbsolutePath();
                        cmd+=" file:///"+System.getProperty("user.dir")+File.separator+"doc/Documentaion.html";
                        Runtime rt=Runtime.getRuntime();
                        rt.exec(cmd);
                    }catch(Exception exp){
                        
                        new askbrowser();
                        
                    }
                } else{
                    
                    new askbrowser();
                    
                    
                    
                }
                
            }
            public void mouseEntered(MouseEvent me){
                jLabel3.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(MouseEvent me){
                jLabel3.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));
        //jPanel1.add(jLabel5);//,BorderLayout.NORTH);
        jPanel1.add(empty5);
        JPanel subP1=new JPanel();
        subP1.setLayout(new javax.swing.BoxLayout(subP1, javax.swing.BoxLayout.Y_AXIS));
        subP1.add(jLabel1);
        subP1.add(empty1);
        subP1.add(empty10);
        subP1.add(jLabel2);
//        subP1.add(empty8);
        JPanel subP2=new JPanel();
        subP2.setLayout(new javax.swing.BoxLayout(subP2, javax.swing.BoxLayout.Y_AXIS));
        subP2.add(jLabel3);
        subP2.add(empty3);
        subP2.add(empty7);
        subP2.add(jLabel4);
        subP2.add(empty4);
        JPanel subP3=new JPanel();
        jPanel1.add(subP1);//,BorderLayout.CENTER);
        subP1.add(empty2);
        jPanel1.add(subP2);//,BorderLayout.SOUTH);
        jPanel4.add(jPanel1);
        subP1.add(empty6);
        subP1.add(empty9);
        
        
        //jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Customize"));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));
        jLabel6.setBackground(java.awt.SystemColor.controlHighlight);
        jLabel6.setFont(new java.awt.Font("Courier New", 3, 14));
        jLabel6.setForeground(new java.awt.Color(204, 0, 0));
        jLabel6.setText("Customize");
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12));
        jLabel6.setOpaque(true);
        
        jLabel8.setText("<html><body><a href=\"\">Update Filters</a></body></html>");
        jLabel8.setToolTipText("Customize class and archive level filters ");
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12));
        jLabel8.addMouseListener(new MouseAdapter() {
            
            public void mouseClicked(MouseEvent me){
                jLabel8.setCursor(new Cursor(Cursor.HAND_CURSOR));
                ExtraOptions filters=new ExtraOptions();
                
            }
            public void mouseEntered(MouseEvent me){
                jLabel8.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(MouseEvent me){
                jLabel8.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        
        jLabel9.setText("<html><body><a href=\"\">Change Syntax Highlighting</a></body></html>");
        jLabel9.setToolTipText("Update any color/font level settings for the decompiled output...");
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12));
        jLabel9.addMouseListener(new MouseAdapter() {
            
            public void mouseClicked(MouseEvent me){
                jLabel9.setCursor(new Cursor(Cursor.HAND_CURSOR));
                CategoryChooser chooser=new CategoryChooser("Jdec Syntax Highlighting Module");
            }
            public void mouseEntered(MouseEvent me){
                jLabel9.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(MouseEvent me){
                jLabel9.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));
        //jPanel2.add(jLabel6);
        
        
        
        jPanel2.add(jLabel8);
        jPanel2.add(empty13);
        jPanel2.add(empty14);
        jPanel2.add(empty15);
        jPanel2.add(jLabel9);
        
        jPanel2.add(empty16);
        jPanel2.add(empty17);
        jPanel2.add(empty18);
        jPanel2.add(empty19);
        jPanel2.add(empty20);
        jPanel2.add(empty21);
        //jPanel2.add(empty22);
        jPanel4.add(jPanel2);
        
        //jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Get Started"));
        jLabel10.setText("<html><body><a href=\"\">Update Configuration</a></body></html>");
        blinklbl=new JLabel("Update!");
        blinklbl.setVisible(false);
        if(blink){
          Thread t=new Thread(new blink());
          t.setDaemon(true);
          t.setPriority(Thread.MAX_PRIORITY);
          t.start();
        }
        
        jLabel10.setToolTipText("Change the jdec config settings here...");
        jLabel10.addMouseListener(new MouseAdapter() {
            
            public void mouseClicked(MouseEvent me){
                jLabel10.setCursor(new Cursor(Cursor.HAND_CURSOR));
                JTabbedPane tabs = UIUtil.getUIUtil().getRightTabbedPane();
                DecompilerConfigDetails config = new DecompilerConfigDetails(UILauncher.getUIutil());
                if(tabs.indexOfTab("Jdec Configuration")!=-1)tabs.remove(tabs.indexOfTab("Jdec Configuration"));
                tabs.addTab("Jdec Configuration", config);
                tabs.setSelectedComponent(config);
                
            }
            public void mouseEntered(MouseEvent me){
                jLabel10.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(MouseEvent me){
                jLabel10.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        
        
        jLabel12.setText("<html><body><a href=\"\">Known Issues</a></body></html>");
        jLabel12.setToolTipText("Get to know the issues present in this release");
        jLabel12.addMouseListener(new MouseAdapter() {
            
            public void mouseClicked(MouseEvent me){
                jLabel12.setCursor(new Cursor(Cursor.HAND_CURSOR));
                KnownIssues ki=new KnownIssues();
                ki.createMe();
                
            }
            public void mouseEntered(MouseEvent me){
                jLabel12.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(MouseEvent me){
                jLabel12.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        jLabel11.setBackground(java.awt.SystemColor.controlHighlight);
        jLabel11.setFont(new java.awt.Font("Courier New", 3, 14));
        jLabel11.setForeground(new java.awt.Color(204, 0, 0));
        jLabel11.setText("Get Started");
        jLabel11.setOpaque(true);
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.Y_AXIS));
        //jPanel3.add(jLabel11);
        jPanel5=new JPanel();
        jPanel5.setLayout(new BoxLayout(jPanel5,BoxLayout.X_AXIS));
        jPanel5.add(jLabel10);
        jPanel5.add(blinklbl);
        JPanel jPanel6=new JPanel();
        jPanel6.setLayout(new BoxLayout(jPanel6,BoxLayout.Y_AXIS));
        jPanel6.add(empty42);
        jPanel6.add(empty43);
        jPanel6.add(jPanel5);
        JPanel jPanel7=new JPanel();
        jPanel7.setLayout(new BoxLayout(jPanel7,BoxLayout.X_AXIS));
        jPanel7.add(jLabel12);
        jPanel6.add(empty33);
        jPanel2.add(empty34);
        jPanel2.add(empty35);
        jPanel6.add(empty39);
        jPanel6.add(empty40);
        jPanel6.add(empty41);
        jPanel6.add(jPanel7);
        
        
        
       
        jPanel3.add(jPanel6);
        jPanel3.add(empty36);
        jPanel3.add(empty37);
        jPanel3.add(empty38);
        
        
        jPanel3.add(empty24);
        jPanel3.add(empty25);
   
       
     /*   SimpleHashtable
             TextField
                XmlDocument*/
        jPanel4.add(jPanel3);
        Container c=getContentPane();
        c.setLayout(new BoxLayout(c,BoxLayout.PAGE_AXIS));
        JLabel dec=new JLabel("Jdec - Java Decompiler");
        dec.setFont(new java.awt.Font("Courier New", 1, 20));
        dec.setForeground(new Color(51,0,51));
        JLabel cafe=new JLabel("<html><body>CAFEBABE....</body></html>");
        JLabel clz=new JLabel("<html><body>class decompiled<br>{<br>//.....<br>}</body></html>");
        JPanel top=new JPanel();
        top.setLayout(new BoxLayout(top,BoxLayout.X_AXIS));
        //top.add(dec);
        top.add(empty30);
        top.add(empty31);
        top.add(cafe);
        
        java.lang.String fsep=File.separator;
        java.lang.String prefix="imgs"+fsep;
        File img=new File(prefix+"jdec.gif");
        ImageIcon ic=new ImageIcon(prefix+"arrow.gif");
        JLabel ar=new JLabel(ic);
        top.add(ar);
        top.add(empty32);
        top.add(clz);
        //JLabel startup=new JLabel("Show At Startup");
        JCheckBox cbox=new JCheckBox("Show At Startup");
        cbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JCheckBox jc=(JCheckBox)e.getSource();
                boolean selected=jc.isSelected();
                if(selected) {
                    UIConfig.getUIConfig().addPref("ShowWelcomeAtStartUp","true");
                } else {
                    UIConfig.getUIConfig().addPref("ShowWelcomeAtStartUp","false");
                }
            }
        });
        //jPanel4.add(cbox);
        JPanel header=new JPanel();
        header.add(dec,BorderLayout.WEST);
        header.add(top,BorderLayout.CENTER);
        c.add(header);//, BorderLayout.NORTH);
        c.add(empty27);
        c.add(empty28);
        c.add(empty29);
        c.add(new JScrollPane(jPanel4));//, BorderLayout.CENTER);
        c.add(cbox);
        String showw=UIUtil.getUIUtil().getShowWelcome();
        if(showw==null || showw.equalsIgnoreCase("true")) {
            cbox.setSelected(true);
        } else {
            cbox.setSelected(false);
        }
        
        
        
        
        
        //pack();
    }
    
    

  class blink implements Runnable

  {
    public void run() {
      if (blinklbl != null) {
        blinklbl.setVisible(true);
        try {
          for (;;) {
            blinklbl.setForeground(new Color(153, 0, 51));
            blinklbl.setFont(new Font("MONOSPACE", Font.BOLD, 11));
            Thread.sleep(700);
            blinklbl.setForeground(Color.YELLOW);// new Color(0,102,102));
            Thread.sleep(300);
          }
        } catch (InterruptedException ie) {

        }

      }
    }
  }
    /**
     * @param args
     *          the command line arguments
     */
    
    
    // Variables declaration - do not modify
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel blinklbl;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    
    // End of variables declaration
    
    
    java.lang.String browserPath="";
    class askbrowser extends JFrame{
        
        askbrowser(){
            super("Browser Path");
            
            final JTextField text=new JTextField(20);
            JPanel panel=new JPanel();
            panel.setLayout(new FlowLayout());
            panel.add(text);
            
            JButton sel=new JButton("Select");
            sel.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser=new JFileChooser(".");
                    int option=chooser.showOpenDialog(null);
                    if(option==JFileChooser.APPROVE_OPTION) {
                        
                        File name=chooser.getSelectedFile();
                        if(name!=null){
                            browserPath=name.getAbsolutePath();
                            text.setText(browserPath);
                            setFocusable(true);
                        }
                        setFocusable(true);
                        text.setFocusable(true);
                        
                    }
                    
                    
                }
                
            });
            panel.add(sel);
            JButton button;
            getContentPane().setLayout(new FlowLayout());
            panel.setVisible(true);
            getContentPane().add(panel);
            getContentPane().add(button=new JButton("Apply"));
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    
                    java.lang.String cmd=text.getText();
                    cmd+=" file:///"+System.getProperty("user.dir")+File.separator+"doc/Documentaion.html";
                    Runtime rt=Runtime.getRuntime();
                    try{
                        rt.exec(cmd);
                        UILauncher.getUIutil().setBrowserPath(new File(browserPath));
                        UIConfig config=UILauncher.getUIConfigRef();
                        config.addPref("browserPath",browserPath);
                    } catch(Exception exp2){
                        JOptionPane.showMessageDialog(null,"Could Not show in browser");
                    } 
                }
            });
            getContentPane().add(button=new JButton("Close"));
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                }
            });
            setBounds(300,300,300,100);
            setResizable(true);
            setVisible(true);
        }
        
    }
}
