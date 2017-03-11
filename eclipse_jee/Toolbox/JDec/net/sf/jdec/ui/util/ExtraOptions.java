/*
 *  ExtraOptions.java Copyright (c) 2006,07 Swaroop Belur
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

import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.ui.config.UIConfig;
import net.sf.jdec.ui.main.UILauncher;
import net.sf.jdec.util.Util;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class ExtraOptions extends JFrame implements ActionListener
{



    public ExtraOptions()
    {
        super("Jdec Decompiler Filters");
        loadFromUserPrefs();
        JPanel panel3=createClassImportFilter();
        getContentPane().add(panel3,BorderLayout.NORTH);
        JPanel panel1=createClassFilters();
        JPanel annotPanel = createAnnotationFilters();

        JPanel filter=new JPanel();
        panel1.setLayout(new BoxLayout(panel1,BoxLayout.Y_AXIS));
        JPanel annotCont = new JPanel();
        //filter.setLayout(new BoxLayout(filter,BoxLayout.X_AXIS));
        annotPanel.setLayout(new BoxLayout(annotPanel,BoxLayout.Y_AXIS));
        annotCont.add(annotPanel,BorderLayout.CENTER);
        filter.add(panel1);
        //filter.add(annotCont);
        //JPanel impfilter=createImportFilter();
        //filter.add(impfilter);
        JPanel panel4=createJarFilters();
        filter.add(panel4);
        getContentPane().add(filter,BorderLayout.CENTER);
        JPanel panel2=createActions();
        getContentPane().add(panel2,BorderLayout.SOUTH);
        JFrame mainf=UILauncher.getMainFrame();
        setBounds((int)mainf.getWidth()/5,mainf.getHeight()/4,725,520);
        setVisible(true);
    }

    private JPanel createClassImportFilter()
    {
        JPanel panel1=new JPanel();
        panel1.add(createImportFilter(),BorderLayout.SOUTH);
        panel1.setBorder(new EtchedBorder());
        return panel1;
    }
    private JButton session=null;
    private JButton perm=null;
    private JButton preview=null;
    private JPanel createActions()
    {
        final JPanel panel1=new JPanel();
        session=new JButton("Apply To this Session Only");
        perm=new JButton("Apply");
        JButton close=new JButton("CloseMe");
        final JButton how=new JButton("How These Options Can Be Useful");
        panel1.setLayout(new FlowLayout());//BoxLayout(panel1,BoxLayout.X_AXIS));
        panel1.add(how);
        panel1.add(perm);
        panel1.add(close);
        //session.addActionListener(this);
        perm.addActionListener(this);
        how.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae)
            {
               java.lang.String msg="";
               msg+="If The class selected to decompile has lot of static declarations and/or static initializer\n";
               msg+="Then It is noticed that decompilation of that static block can take a lot of time by jdec\n";
               msg+="The above is one example where the user can avoid decompiling parts of code which the user\n";
               msg+="feels is not all that important.\n";
               JOptionPane.showMessageDialog(null,msg);
               panel1.revalidate();
               panel1.repaint();
               ExtraOptions.this.repaint();
            }
        });
        close.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent a)
            {
                ExtraOptions.this.setVisible(false);
                ExtraOptions.this.dispose();
            }
        });
        return panel1;
    }

    // Display Filters
    private boolean showextendsobject=false;
    private boolean showfieldsfirst=false;
    private boolean showconstfirst=false;
    
    // Decompiler Filters
    private boolean dontpvtmethods=false;
    private boolean dontstaticinit=false;
    private boolean dontsynth=false;
    private boolean dontshowemptyconst=false;
    private boolean dontshownative=false;
    private boolean dontshowabs=false;
    
    // Annotation
    private boolean annotExtendsObject=true;
    

    private JCheckBox check1=null;
    private JCheckBox check2=null;
    private JCheckBox check3=null;
    private JCheckBox check4=null;
    private JCheckBox check5=null;
    private JCheckBox check6=null;
    private JCheckBox check7=null;
    private JCheckBox check8=null;
    private JCheckBox check9=null;
    private JCheckBox annotcheck1=null;
    private JPanel clzfilters=null;
    private JPanel annotfilters=null;

    
    public JPanel createAnnotationFilters()
    {

       annotfilters=new JPanel();
       annotcheck1=new JCheckBox("Dont show extends clause for java.lang.Object																		\t\t\t\t\t\t													");
       annotcheck1.setSelected(annotExtendsObject);
       
       annotfilters.setLayout(new BoxLayout(clzfilters,BoxLayout.Y_AXIS));
       annotfilters.setSize(clzfilters.getWidth()+550,clzfilters.getHeight());
       annotfilters.add(annotcheck1);
   
       
       TitledBorder tb2=new TitledBorder("Annotation Level Filters");
       annotfilters.setBorder(tb2);
       tb2.setTitleColor(Color.darkGray);
       tb2.setTitlePosition(TitledBorder.TOP);

       annotcheck1.addActionListener(this);
       

       return annotfilters ;
    }
    
    public JPanel createClassFilters()
    {

       clzfilters=new JPanel();
       check1=new JCheckBox("Dont Decompile/Disassemble private methods																		\t\t\t\t\t\t													");
       check1.setSelected(dontpvtmethods);
       check2=new JCheckBox("Dont Decompile/Disassemble static  initializers																");
       check2.setSelected(dontstaticinit);
       check3=new JCheckBox("Dont show Empty Constructors");
       check3.setSelected(dontshowemptyconst);
       check4=new JCheckBox("Show Constructors Before Methods");
       check4.setSelected(showconstfirst);
       check5=new JCheckBox("Show Fields at the top");
       check5.setSelected(showfieldsfirst);
       check6=new JCheckBox("Dont show Abstract Methods");
       check6.setSelected(dontshowabs);
       check7=new JCheckBox("Dont show Native Methods");
       check7.setSelected(dontshownative);
       check8=new JCheckBox("Dont Decompile/Disassemble Synthetic Methods");
       check8.setSelected(dontsynth);
       check9=new JCheckBox("Display superclass in case of java/lang/Object");
       check9.setSelected(showextendsobject);
       


       clzfilters.setLayout(new BoxLayout(clzfilters,BoxLayout.Y_AXIS));
       clzfilters.setSize(clzfilters.getWidth()+550,clzfilters.getHeight());
       JLabel lbl=new JLabel("These options affect how the class file gets decompiled/disassembled and/or shown in the UI");
       //clzfilters.add(lbl);
       clzfilters.add(check1);
       clzfilters.add(check2);
       clzfilters.add(check7);
       clzfilters.add(check3);
       clzfilters.add(check6);
       clzfilters.add(check8);
       clzfilters.add(check4);
       clzfilters.add(check5);
       clzfilters.add(check9);
       //clzfilters.add(imp);
       TitledBorder tb2=new TitledBorder("Class Level Filters");
       clzfilters.setBorder(tb2);
       tb2.setTitleColor(Color.darkGray);
       tb2.setTitlePosition(TitledBorder.TOP);

       check1.addActionListener(this);
       check2.addActionListener(this);
       check3.addActionListener(this);
       check4.addActionListener(this);
       check5.addActionListener(this);
       check6.addActionListener(this);
       check7.addActionListener(this);
       check8.addActionListener(this);
       check9.addActionListener(this);

       return clzfilters ;
    }
    private JPanel inclPanel=null;

    public JComboBox getInclList() {
        return inclList;
    }

    private JComboBox inclList=null;

    public JPanel getInclPanel() {
        return inclPanel;
    }

    public JPanel createJarFilters()
    {
        final JPanel panel=new JPanel();
        //panel.setLayout(new FlowLayout());
        inclList=new JComboBox();





        JLabel lbl=new JLabel("Included Packages In Jar");
        lbl.setForeground(Color.BLUE);
        final JButton but=new  JButton("Customize");
        JButton emp=new JButton("Empty List");


        JButton sh=new  JButton("Show");
        final JPanel panel2=new JPanel();
        java.lang.String []str=getIncludedList();
        if(str==null)inclList.addItem("All");
        else
        {
            for(int z=0;z<str.length;z++)
            {
                inclList.addItem(str[z]);
            }
        }

        //list.setVisible(true);

        inclPanel=new JPanel();
        inclPanel.setLayout(new BoxLayout(inclPanel,BoxLayout.X_AXIS));
        inclPanel.add(lbl);
        inclPanel.add(inclList);

         emp.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent  ae)
            {
                includedList=new ArrayList();
                inclList.removeAllItems();
                inclList.addItem("All");
                inclPanel.revalidate();
            }
        });

        //panel.setLayout(new BoxLayout(panel,BoxLayout.));


       // panel.add(prepanel);
       // panel.add(new JLabel("Jar Level Filters..."));
        panel.add(inclPanel);//,BorderLayout.NORTH);
        JPanel cust=new JPanel();
        cust.setLayout(new BoxLayout(cust,BoxLayout.Y_AXIS));

        cust.add(but);
        cust.add(panel2);

        panel.add(cust);//,BorderLayout.SOUTH);
        panel.add(emp);
        panel.add(new JPanel());
        TitledBorder tb1=new TitledBorder("Jar Level Filters");
        tb1.setTitleColor(Color.DARK_GRAY);
        tb1.setTitlePosition(TitledBorder.TOP);
        panel.setBorder(tb1);

        but.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae)
            {
                int count=panel2.getComponentCount();

                if(but.getText().equals("Customize"))
                {
                panel2.removeAll();
                JLabel addl=new JLabel("Pkg Name");
                text=new JTextField(20);
                addPkg=new JButton("Add Package");
                JButton ex=new JButton("Show Example");
                ex.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent ae)
                    {
                        JOptionPane.showMessageDialog(null,"java.lang","Package Example",JOptionPane.INFORMATION_MESSAGE);
                    }
                });

                 preview=new JButton("Launch Preview Jar");
                preview.setToolTipText("This can be used to determine which package the user would like to add...");
                preview.addActionListener(new ActionListener(){
                     public void actionPerformed(ActionEvent ae)
                     {
                        JarPreviewFrame previewFrame=new JarPreviewFrame();
                     }
                });
                panel2.setLayout(new FlowLayout());//new Boxlk;
                		//new FlowLayout());
                panel2.add(addl);
                panel2.add(text);
                panel2.add(addPkg);
                panel2.add(ex);

                panel2.add(preview);


                panel.remove(panel2);
                panel.add(panel2,BorderLayout.SOUTH);
                but.setText("Hide");
                addPkg.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e)
                    {
                        java.lang.String s=text.getText();
                        if(s!=null && s.trim().length() > 0)
                        {
                            includedList.add(s);
                            java.lang.String []str=getIncludedList();
                            inclList.removeAllItems();
                            if(str==null)inclList.addItem("All");
                            else
                            {
                                for(int z=0;z<str.length;z++)
                                {
                                    inclList.addItem(str[z]);
                                }
                            }
                        }
                    }
                });
               /* try
                {
                    UIManager.setLookAndFeel(UILauncher.getUIutil().getCurrentLNF());
                    SwingUtilities.updateComponentTreeUI(UILauncher.getMainFrame());
                }
                catch(Exception e)
                {
                    try
                    {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                        SwingUtilities.updateComponentTreeUI(UILauncher.getMainFrame());
                    }
                    catch(Exception exp){}
                }*/
                panel.revalidate();

                //panel.repaint();
                //ExtraOptions.this.repaint();
                }
                else if(but.getText().equals("Hide"))
                {
                panel2.removeAll();

                  panel.revalidate();
                  but.setText("Customize");
                }

            }
        });


        return panel;
    }

    private JTextField text=null;
    private JButton addPkg=null;
    private ArrayList includedList=new ArrayList();

    public ArrayList    getIncludedListAsArrayList()
    {
        return includedList;
    }


   /* {
        includedList.addPkg("java.lang");
        includedList.addPkg("java.lang.reflect");
        includedList.addPkg("java.math");
        includedList.addPkg("net.sf.jdec.core.ui.main");
    } */


    private java.lang.String[] getIncludedList()
    {
        if(includedList.size()==0)
        {
            return null;
        }
        else
        {
            int sz=includedList.size();
            java.lang.String temp[]=new java.lang.String[sz];
            for(int s=0;s<sz;s++)
            {
                 temp[s]=(java.lang.String)includedList.get(s);
            }

            return temp;
        }


    }

    private java.lang.String[] getImportExclList()
    {
        if(exclimportlist.size()==0)
        {
            return null;
        }
        else
        {
            int sz=exclimportlist.size();
            java.lang.String temp[]=new java.lang.String[sz];
            for(int s=0;s<sz;s++)
            {
                 temp[s]=(java.lang.String)exclimportlist.get(s);
            }

            return temp;
        }


    }

    public void actionPerformed(ActionEvent ae)
    {

    	if(ae.getSource()==annotcheck1)
        {
             annotExtendsObject=annotcheck1.isSelected();

        }


        if(ae.getSource()==check1)
        {
             dontpvtmethods=check1.isSelected();

        }
        else if(ae.getSource()==check2)
        {
            dontstaticinit=check2.isSelected();
        }
        else if(ae.getSource()==check3)
        {
            dontshowemptyconst=check3.isSelected();
        }
        else if(ae.getSource()==check4)
        {
            showconstfirst=check4.isSelected();

        }
        else if(ae.getSource()==check5)
        {
        	showfieldsfirst=check5.isSelected();
        }

        else if(ae.getSource()==check6)
        {
           dontshowabs=check5.isSelected();
        }
        else if(ae.getSource()==check7)
        {
         dontshownative=check7.isSelected();
        }
        else if(ae.getSource()==check8)
        {
           dontsynth=check8.isSelected();
        }
        else if(ae.getSource()==check9)
        {
        	showextendsobject=check9.isSelected();
        }
        else if(ae.getSource()==session)
        {
           ConsoleLauncher.setDontpvtmethods(dontpvtmethods);
           check1.setSelected(dontpvtmethods);

           ConsoleLauncher.setDontshowabs(dontshowabs);
           check5.setSelected(dontshowabs);

           ConsoleLauncher.setDontsynth(dontsynth);
           check7.setSelected(dontsynth);

           ConsoleLauncher.setDontstaticinit(dontstaticinit);
           check2.setSelected(dontstaticinit);

           ConsoleLauncher.setDontshowemptyconst(dontshowemptyconst);
           check3.setSelected(dontshowemptyconst);

           ConsoleLauncher.setDontshownative(dontshownative);
           check6.setSelected(dontshownative);

           ConsoleLauncher.setShowconstfirst(showconstfirst);
           check4.setSelected(showconstfirst);

           ConsoleLauncher.setInclListInJar(includedList);
           clzfilters.revalidate();

        }
        else if(ae.getSource()==perm)
        {
           ConsoleLauncher.setDontpvtmethods(dontpvtmethods);
           ConsoleLauncher.setDontshowabs(dontshowabs);
           ConsoleLauncher.setDontsynth(dontsynth);
           ConsoleLauncher.setDontstaticinit(dontstaticinit);
           ConsoleLauncher.setDontshowemptyconst(dontshowemptyconst);
           ConsoleLauncher.setDontshownative(dontshownative);
           ConsoleLauncher.setShowconstfirst(showconstfirst);
           ConsoleLauncher.setInclListInJar(includedList);
           ConsoleLauncher.setShowfieldsfirst(showfieldsfirst);
           ConsoleLauncher.setShowObjectSuperClass(showextendsobject);
           exclimportlist=(ArrayList)Util.genericRemoveDuplicates(exclimportlist);
           ConsoleLauncher.setExclImpList(exclimportlist);




           UIConfig.getUIConfig().addPref("DONTDECPVTMET",""+dontpvtmethods);
           UIConfig.getUIConfig().addPref("ANNOTEXTENDSOBJECT",""+annotExtendsObject);
           UIConfig.getUIConfig().addPref("DONTSHOWABSMET",""+dontshowabs);
           UIConfig.getUIConfig().addPref("DONTDECSYNMET",""+dontsynth);
           UIConfig.getUIConfig().addPref("DONTDECSTATICINIT",""+dontstaticinit);
           UIConfig.getUIConfig().addPref("DONTSHOWEMPCONS",""+dontshowemptyconst);
           UIConfig.getUIConfig().addPref("DONTSHOWNAT",""+dontshownative);
           UIConfig.getUIConfig().addPref("SHOWCONSTFIRST",""+showconstfirst);
           UIConfig.getUIConfig().addPref("SHOWFIELDSFIRST",""+showfieldsfirst);
           UIConfig.getUIConfig().addPref("SHOWEXTENDSOBJECT",""+showextendsobject);
           java.lang.String str[]=getIncludedList();
           if(str!=null)
           {
             StringBuffer buf=new StringBuffer("");
             for(int i=0;i<str.length;i++)
             {
               buf.append(str[i]);
               if(i < str.length-1)
               {
                   buf.append(",");
               }
             }
             UIConfig.getUIConfig().addPref("INCLUDEDPKGS",buf.toString());
           }
           else
           {
               UIConfig.getUIConfig().removePref("INCLUDEDPKGS");
           }


           str=getImportExclList();
           if(str!=null)
           {
             StringBuffer buf=new StringBuffer("");
             for(int i=0;i<str.length;i++)
             {
               buf.append(str[i]);
               if(i < str.length-1)
               {
                   buf.append(",");
               }
             }
             UIConfig.getUIConfig().addPref("EXCLUDEDPKGSINIMPORT",buf.toString());
           }
           else
           {
               UIConfig.getUIConfig().removePref("EXCLUDEDPKGSINIMPORT");
           }

        }

    }


    private void loadFromUserPrefs()
    {
       java.lang.String s=UIConfig.getUIConfig().getPref("DONTDECPVTMET");
       if(s!=null)
       {
          try
          {
             boolean b=new Boolean(s).booleanValue();
             dontpvtmethods=b;
          }
          catch(Exception e)
          {
             dontpvtmethods=false;
          }
       }
       else
       {
           dontpvtmethods=false;
       }

       s=UIConfig.getUIConfig().getPref("DONTSHOWABSMET");
       if(s!=null)
       {
          try
          {
             boolean b=new Boolean(s).booleanValue();
             dontshowabs=b;
          }
          catch(Exception e)
          {
             dontshowabs=false;
          }
       }
       else
       {
           dontshowabs=false;
       }
       s=UIConfig.getUIConfig().getPref("ANNOTEXTENDSOBJECT");
       if(s!=null)
       {
          try
          {
             boolean b=new Boolean(s).booleanValue();
             annotExtendsObject=b;
          }
          catch(Exception e)
          {
        	  annotExtendsObject=true;
          }
       }
       else
       {
    	   annotExtendsObject=true;
       }
       s=UIConfig.getUIConfig().getPref("DONTDECSYNMET");
       if(s!=null)
       {
          try
          {
             boolean b=new Boolean(s).booleanValue();
             dontsynth=b;
          }
          catch(Exception e)
          {
             dontsynth=false;
          }
       }
       else
       {
           dontsynth=false;
       }

        s=UIConfig.getUIConfig().getPref("DONTDECSTATICINIT");
        if(s!=null)
        {
            try
            {
                boolean b=new Boolean(s).booleanValue();
                dontstaticinit=b;
            }
            catch(Exception e)
            {
                dontstaticinit=false;
            }
        }
        else
        {
            dontstaticinit=false;
        }


        s=UIConfig.getUIConfig().getPref("DONTSHOWEMPCONS");
        if(s!=null)
        {
            try
            {
                boolean b=new Boolean(s).booleanValue();
                dontshowemptyconst=b;
            }
            catch(Exception e)
            {
                dontshowemptyconst=false;
            }
        }
        else
        {
            dontshowemptyconst=false;
        }


        s=UIConfig.getUIConfig().getPref("DONTSHOWNAT");
        if(s!=null)
        {
            try
            {
                boolean b=new Boolean(s).booleanValue();
                dontshownative=b;
            }
            catch(Exception e)
            {
                dontshownative=false;
            }
        }
        else
        {
            dontshownative=false;
        }

        s=UIConfig.getUIConfig().getPref("SHOWCONSTFIRST");
        if(s!=null)
        {
            try
            {
                boolean b=new Boolean(s).booleanValue();
                showconstfirst=b;
            }
            catch(Exception e)
            {
                showconstfirst=false;
            }
        }
        else
        {
            showconstfirst=false;
        }
        
        s=UIConfig.getUIConfig().getPref("SHOWFIELDSFIRST");
        if(s!=null)
        {
            try
            {
                boolean b=new Boolean(s).booleanValue();
                showfieldsfirst=b;
            }
            catch(Exception e)
            {
            	showfieldsfirst=false;
            }
        }
        else
        {
        	showfieldsfirst=false;
        }
        
        
        s=UIConfig.getUIConfig().getPref("SHOWEXTENDSOBJECT");
        if(s!=null)
        {
            try
            {
                boolean b=new Boolean(s).booleanValue();
                showextendsobject=b;
            }
            catch(Exception e)
            {
            	showextendsobject=false;
            }
        }
        else
        {
        	showextendsobject=false;
        }
        
        
        s=UIConfig.getUIConfig().getPref("INCLUDEDPKGS");
        if(s!=null)
        {
           StringTokenizer tokens=new StringTokenizer(s,",");
           includedList=new ArrayList(); // Make sure it is clean
           while(tokens.hasMoreTokens())
           {
               java.lang.String str=(java.lang.String)tokens.nextToken();
               includedList.add(str);
           }
        }

        s=UIConfig.getUIConfig().getPref("EXCLUDEDPKGSINIMPORT");
        if(s!=null)
        {
           StringTokenizer tokens=new StringTokenizer(s,",");
           exclimportlist=new ArrayList(); // Make sure it is clean
           while(tokens.hasMoreTokens())
           {
               java.lang.String str=(java.lang.String)tokens.nextToken();
               exclimportlist.add(str);
           }
        }


    }

    private JPanel createImportFilter(){
       final JPanel imp=new JPanel();
       imp.setLayout(new BoxLayout(imp,BoxLayout.Y_AXIS));
       JLabel msg=new JLabel("Excluded Packages In Import Statements");
       msg.setForeground(Color.BLUE);
       imp.add(msg);

       final JComboBox impcomb=new JComboBox();
       java.lang.String []str=getImportExclList();
       if(str!=null)
       {
            for(int z=0;z<str.length;z++)
            {
                impcomb.addItem(str[z]);
            }
       }
       final JPanel p=new JPanel();
         p.setLayout(new BoxLayout(p,BoxLayout.X_AXIS));
        p.add(impcomb);
       final JButton impcust=new JButton("Customize");
       p.add(impcust);
       imp.add(p);
       final JButton example=new JButton("Show Example");
       p.add(example);
       example.addActionListener(new ActionListener(){
           public void actionPerformed(ActionEvent ae)
           {
             JOptionPane.showMessageDialog(ExtraOptions.this,"java.lang","Example Usage",JOptionPane.INFORMATION_MESSAGE);
           }
       });

       final JButton empty=new JButton("Empty list");
       p.add(empty);
       empty.addActionListener(new ActionListener(){
           public void actionPerformed(ActionEvent ae)
           {
             exclimportlist=new ArrayList();
             impcomb.removeAllItems();
             p.revalidate();
             imp.revalidate();
           }
       });

       imp.add(p);
       final JPanel custImp=new JPanel();
       imp.add(custImp);
       impcust.addActionListener(new ActionListener(){
           public void actionPerformed(ActionEvent ae)
           {
              if(impcust.getText().equals("Customize"))
              {
                  custImp.removeAll();
                  custImp.setLayout(new BoxLayout(custImp,BoxLayout.X_AXIS));
                  custImp.add(new JLabel("Package Name"));
                  final JTextField impName=new JTextField(20);
                  custImp.add(impName);
                  JButton addP=new JButton("Add To List");
                  custImp.add(addP);
                  impcust.setText("Hide");
                  addP.addActionListener(new ActionListener(){

                      public void actionPerformed(ActionEvent r)
                      {
                        String nm=impName.getText();
                        impcomb.removeItem(nm);
                        impcomb.addItem(nm);
                        exclimportlist.add(nm);
                      }
                  });
                  custImp.revalidate();

              }
              else if(impcust.getText().equals("Hide"))
              {
                  custImp.removeAll();
                  impcust.setText("Customize");
                  custImp.revalidate();
              }
           }
       });
       return imp;
    }
    private ArrayList exclimportlist=new ArrayList();

}
