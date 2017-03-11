/*
 * CategoryChooser.java Copyright (c) 2006,07 Swaroop Belur
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


package net.sf.jdec.ui.util.highlight;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;

import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.jdec.ui.core.JdecEditorKit;
import net.sf.jdec.ui.core.JdecPreviewKit;
import net.sf.jdec.ui.main.UILauncher;
import net.sf.jdec.ui.config.UIConfig;

public class CategoryChooser extends JFrame implements ActionListener {

    ListListener listL=null;


    public static void main(String[] args) {


        new CategoryChooser("Jdec Syntax Highlighting Module");


    }

    private JPanel catg=new JPanel();
    private JPanel values=new JPanel();
    private static String[] data = {"Keyword                    ", "Number                    ", "Operator                   ", "String                    ","Annotation                    ","Select One From Above...."};
    private static JList list=new JList(data);
    private JEditorPane ta=new JEditorPane();
    private JPanel applyP=new JPanel();
    private JButton apply=new JButton("Apply Change");
    private JButton preview=new JButton("Preview");
    private JPanel fonts=null;
    private JPanel main=null;

    public CategoryChooser(String s) {

        super(s);
        list.setSelectedIndex(5);
        UILauncher.getUIConfigRef().cc=this;
        createList();
        //catg.add(list);
        createValues();
        createApplyPanel();
        //getContentPane().setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
        getContentPane().add(new JSeparator());
        main=new JPanel();
        getContentPane().add(new JSeparator());
        main.add(catg,BorderLayout.WEST);
        TitledBorder tb1=new TitledBorder("Categories");
        catg.setBorder(tb1);
        main.add(fonts,BorderLayout.NORTH);
        main.add(values,BorderLayout.EAST);
        main.add(applyP,BorderLayout.SOUTH);
        values.setBorder(new TitledBorder("Set Color and Effects"));
        getContentPane().add(main,BorderLayout.CENTER);
        getContentPane().add(new JSeparator());
        getContentPane().add(main,BorderLayout.CENTER);
        main.setBorder(new TitledBorder(""));
        main.setBackground(Color.lightGray);
// TODO : call a method here to apply the color changes to sample code

        JdecEditorKit kit=new JdecEditorKit();
        ta.setEditorKitForContentType("text/java", kit);
        ta.setContentType("text/java");
        ta.setText(getSampleCode());
        JScrollPane sp=new JScrollPane(ta);
        getContentPane().add(sp,BorderLayout.SOUTH);
        pack();
        setSize(700,500);
        listL=ListListener.getListener();
        list.addListSelectionListener(listL);


        /*try
        {
            String def=UILauncher.getUIutil().getDefaultLnkNFeelName();
            if(def==null)def=UIManager.getSystemLookAndFeelClassName();
            if(def==null)def=UIManager.getCrossPlatformLookAndFeelClassName();
            UIManager.setLookAndFeel(def);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch(Exception exp)
        {


        }*/


        setVisible(true);

    }

    private void createApplyPanel()
    {
        //applyP.setLayout(new BoxLayout(applyP,BoxLayout.X_AXIS));
        applyP.add(apply);
        applyP.add(preview);
        JButton cl=new JButton("Close");
        cl.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae)
            {
                CategoryChooser.this.setVisible(false);
                CategoryChooser.this.dispose();
            }
        });
        applyP.add(cl);
        applyP.add(ta);
        apply.addActionListener(this);
        preview.addActionListener(this);
        pack();

    }
    private static JComboBox backg=null;
    private static JComboBox foreg=null;
    private static JComboBox effects=null;
    private JPanel types2=null;
    private void createValues(){

        JLabel fore=new JLabel("Set Foreground");
        fore.setForeground(Color.blue);
        JLabel back=new JLabel("Set Background");
        back.setForeground(Color.blue);
        JLabel eft=new JLabel("Effects");
        eft.setForeground(Color.blue);
        JLabel dummy1=new JLabel("    ");
        JLabel dummy2=new JLabel(" ");
        JLabel dummy3=new JLabel(" ");
        effects=new JComboBox();
        effects.addItem("Underlined");
        effects.addItem("StrikeThrough");
        effects.addItem("None");
        effects.setSelectedItem("None");


        foreg=new JComboBox();
        foreg.removeActionListener(this);
        //foreg.addActionListener(this);
        foreg.setActionCommand("foregr");
        foreg.addItem("---");
        foreg.addItem("Choose Foreground");
        foreg.setSelectedIndex(0);

        backg=new JComboBox();
        backg.removeActionListener(this);
        //backg.addActionListener(this);
        backg.setActionCommand("backgr");
        backg.addItem("---");
        backg.addItem("Choose Background");
        backg.setSelectedIndex(0);

        //values.setLayout(new BoxLayout(values,BoxLayout.Y_AXIS));

        fonts=new JPanel();
        fonts.setBorder(new TitledBorder("Select Font"));
        JLabel font=new JLabel("Configure Font");
        font.setForeground(Color.blue);
        JButton confF=new JButton("Go");
        confF.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae)
            {

                Object cat=list.getSelectedValue();
                if(cat!=null)
                {
                    FontChooser fc=new FontChooser(new JFrame("Jdec Font Chooser"));
                    fc.setCategory((String)cat.toString());
                    fc.createAllComponents();
                    fc.showFontWindow();
                }
                else
                {
                    JOptionPane.showMessageDialog(CategoryChooser.this,"Please select a category type first");
                }

            }

        });
        fonts.add(font);
        fonts.add(confF);
        JLabel selfontfml=new JLabel("Selected FontFamily");
        JLabel selfontsz=new JLabel("Selected FontSize");
        //fonts.add(selfontfml);
        // fonts.add(selfontsz);


        JPanel types=new JPanel();
        types.setLayout(new BoxLayout(types,BoxLayout.Y_AXIS));
        types.add(dummy1);
        types.add(fore);
        types.add(dummy2);
        types.add(back);
        types.add(dummy3);
        types.add(eft);
        types2=new JPanel();
        types2.setLayout(new BoxLayout(types2,BoxLayout.Y_AXIS));
        types2.add(foreg,BorderLayout.NORTH);
        types2.add(new JLabel("       "));
        types2.add(backg,BorderLayout.CENTER);
        types2.add(new JLabel("       "));
        types2.add(effects,BorderLayout.SOUTH);

        /* JPanel forep=new JPanel();
        forep.setLayout(new BoxLayout(forep,BoxLayout.X_AXIS));
        forep.add(fore);forep.add(foreg);

        JPanel backp=new JPanel();
        backp.setLayout(new BoxLayout(backp,BoxLayout.X_AXIS));
        backp.add(back);backp.add(backg);

        JPanel effp=new JPanel();
        effp.setLayout(new BoxLayout(effp,BoxLayout.X_AXIS));
        effp.add(eft);effp.add(effects);*/

        //values.setLayout(new GridLayout(3,3) );
        //values.add(fonts,BorderLayout.CENTER);
        values.add(types,BorderLayout.WEST);
        values.add(types2,BorderLayout.EAST);

        //values.add(backp);//,BorderLayout.CENTER);
        //values.add(effp);//,BorderLayout.SOUTH);
        foreg.addActionListener(new ActionListener()
                {
            public void actionPerformed(ActionEvent ae)
            {

                Object o=foreg.getSelectedItem();
                if(o!=null && o.toString().equalsIgnoreCase("Choose Foreground"))
                {
                    ColorChooser colorc=new ColorChooser();
                    UILauncher.getUIConfigRef().setColorChooserRef(colorc);
                    colorc.setForeGround(true);
                }
            }
        });

        backg.addActionListener(new ActionListener()
                {
            public void actionPerformed(ActionEvent ae)
            {
                Object o=backg.getSelectedItem();
                if(o!=null &&  o.toString().equalsIgnoreCase("Choose Background"))
                {
                    ColorChooser colorc=new ColorChooser();
                    UILauncher.getUIConfigRef().setColorChooserRef(colorc);
                    colorc.setForeGround(false);
                }
            }
        });
    }

    private void createList()
    {




        JScrollPane sp = new JScrollPane(list,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        //list.setSize(500,300);
        list.setMinimumSize(new Dimension(300,300));
        catg.add(sp,BorderLayout.CENTER);
        //catg.setVisible(true);
        //list.getSelectionModel().addListSelectionListener(this);
    }


    private java.lang.String getSampleCode()
    {

        String code="@Sample(someValue=1)\n";
        code+="class sample";
        code+="\n{\n";
        code+="\tpublic static void main(String args[])";
        code+="\n\t{\n";
        code+="\t\tint j=1;\n";
        code+="\t\tint i=123456;\n";
        code+="\t\ti=i+1;\n";
        code+="\t\ti=i*j;\n";
        code+="\t\tSystem.out.println(\"In main method\");\n";
        code+="\t}\n}\n";
        return code;


    }

    public void actionPerformed(ActionEvent e) {


        if(e.getSource()==list)return;


        if(e.getSource()==apply)
        {
            Object ls=list.getSelectedValue();
            if(ls!=null)
            {
                String listv=ls.toString();

                if(listv!=null)
                {
                    if(listv.indexOf("Keyword")!=-1)
                    {
                        String bk=backg.getSelectedItem().toString();
                        String fr=foreg.getSelectedItem().toString();
                        String ef=effects.getSelectedItem().toString();

                        if(bk!=null && bk.indexOf("Choose")==-1)
                        {
                            Color c=getColor(bk);
                            if(c!=null)
                            {
                                UILauncher.getUIConfigRef().setCurrentBackGrndColor_KEYWORD(c);
                                UILauncher.getUIConfigRef().addPref("keyword_backg_color","["+c.getRed()+","+c.getGreen()+","+c.getBlue()+"]");
                            }
                            else
                            {
                                c=UIConfig.defaultBackgColor;
                                UILauncher.getUIConfigRef().setCurrentBackGrndColor_KEYWORD(c);
                                UILauncher.getUIConfigRef().addPref("keyword_backg_color","["+c.getRed()+","+c.getGreen()+","+c.getBlue()+"]");
                            }

                        }
                        if(bk==null || bk.indexOf("---")!=-1)
                        {
                            UILauncher.getUIConfigRef().removePref("keyword_backg_color");
                            UILauncher.getUIConfigRef().setCurrentBackGrndColor_KEYWORD(null);
                        }


                        if(fr!=null && fr.indexOf("Choose")==-1)
                        {
                            Color c=getColor(fr);
                            if(c!=null)
                            {
                                UILauncher.getUIConfigRef().setCurrentForeGrndColor_KEYWORD(c);
                                UILauncher.getUIConfigRef().addPref("keyword_foreg_color","["+c.getRed()+","+c.getGreen()+","+c.getBlue()+"]");
                            }
                        }
                        if(fr==null || fr.indexOf("---")!=-1)
                        {
                            UILauncher.getUIConfigRef().removePref("keyword_backg_color");
                            UILauncher.getUIConfigRef().setCurrentForeGrndColor_KEYWORD(null);
                        }


                        if(ef!=null && ef.indexOf("None")==-1)
                        {
                            UILauncher.getUIConfigRef().setEffectKEYWORD(ef);
                            UILauncher.getUIConfigRef().addPref("keyword_effect",ef);
                        }
                        if(ef==null || ef.indexOf("None")!=-1)
                        {
                            UILauncher.getUIConfigRef().removePref("keyword_effect");
                            UILauncher.getUIConfigRef().setEffectKEYWORD(null);
                        }
                    }
                    JFrame f;
                    if(listv.indexOf("Number")!=-1)
                    {
                        String bk=backg.getSelectedItem().toString();
                        String fr=foreg.getSelectedItem().toString();
                        String ef=effects.getSelectedItem().toString();
                        if(bk!=null && bk.indexOf("Choose")==-1)
                        {
                            Color c=getColor(bk);
                            if(c!=null)
                            {
                                UILauncher.getUIConfigRef().setCurrentBackGrndColor_NUMBER(c);
                                UILauncher.getUIConfigRef().addPref("number_backg_color","["+c.getRed()+","+c.getGreen()+","+c.getBlue()+"]");
                            }
                            else
                            {
                                c=UIConfig.defaultBackgColor;
                                UILauncher.getUIConfigRef().setCurrentBackGrndColor_NUMBER(c);
                                UILauncher.getUIConfigRef().addPref("number_backg_color","["+c.getRed()+","+c.getGreen()+","+c.getBlue()+"]");
                            }


                        }
                        if(bk==null || bk.indexOf("---")!=-1)
                        {
                            UILauncher.getUIConfigRef().removePref("number_backg_color");
                            UILauncher.getUIConfigRef().setCurrentBackGrndColor_NUMBER(null);
                        }

                        if(fr!=null && fr.indexOf("Choose")==-1)
                        {
                            Color c=getColor(fr);
                            if(c!=null)
                            {
                                UILauncher.getUIConfigRef().setCurrentForeGrndColor_NUMBER(c);
                                UILauncher.getUIConfigRef().addPref("number_foreg_color","["+c.getRed()+","+c.getGreen()+","+c.getBlue()+"]");
                            }
                        }
                        if(fr==null || fr.indexOf("---")!=-1)
                        {
                            UILauncher.getUIConfigRef().removePref("number_foreg_color");
                            UILauncher.getUIConfigRef().setCurrentForeGrndColor_NUMBER(null);
                        }

                        if(ef!=null && ef.indexOf("None")==-1)
                        {
                            UILauncher.getUIConfigRef().setEffectNUMBER(ef);
                            UILauncher.getUIConfigRef().addPref("number_effect",ef);
                        }
                        if(ef==null || ef.indexOf("None")!=-1)
                        {
                            UILauncher.getUIConfigRef().removePref("number_effect");
                            UILauncher.getUIConfigRef().setEffectNUMBER(null);
                        }
                    }
                    
                    if(listv.indexOf("Operator")!=-1)
                    {
                        String bk=backg.getSelectedItem().toString();
                        String fr=foreg.getSelectedItem().toString();
                        String ef=effects.getSelectedItem().toString();
                        if(bk!=null && bk.indexOf("Choose")==-1)
                        {
                            Color c=getColor(bk);
                            if(c!=null)
                            {
                                UILauncher.getUIConfigRef().setCurrentBackGrndColor_OPERATOR(c);
                                UILauncher.getUIConfigRef().addPref("operator_backg_color","["+c.getRed()+","+c.getGreen()+","+c.getBlue()+"]");
                            }
                            else
                            {
                                c=UIConfig.defaultBackgColor;
                                UILauncher.getUIConfigRef().setCurrentBackGrndColor_OPERATOR(c);
                                UILauncher.getUIConfigRef().addPref("operator_backg_color","["+c.getRed()+","+c.getGreen()+","+c.getBlue()+"]");
                            }

                        }
                        if(bk==null || bk.indexOf("---")!=-1)
                        {
                            UILauncher.getUIConfigRef().removePref("operator_backg_color");
                            UILauncher.getUIConfigRef().setCurrentBackGrndColor_OPERATOR(null);
                        }


                        if(fr!=null && fr.indexOf("Choose")==-1)
                        {
                            Color c=getColor(fr);
                            if(c!=null)
                            {
                                UILauncher.getUIConfigRef().setCurrentForeGrndColor_OPERATOR(c);
                                UILauncher.getUIConfigRef().addPref("operator_foreg_color","["+c.getRed()+","+c.getGreen()+","+c.getBlue()+"]");
                            }
                        }
                         if(fr==null || fr.indexOf("---")!=-1)
                        {
                            UILauncher.getUIConfigRef().removePref("operator_foreg_color");
                            UILauncher.getUIConfigRef().setCurrentBackGrndColor_OPERATOR(null);
                        }


                        if(ef!=null && ef.indexOf("None")==-1)
                        {
                            UILauncher.getUIConfigRef().setEffectOP(ef);
                            UILauncher.getUIConfigRef().addPref("operator_effect",ef);
                        }
                        if(ef==null || ef.indexOf("None")!=-1)
                        {
                             UILauncher.getUIConfigRef().removePref("operator_effect");
                            UILauncher.getUIConfigRef().setEffectOP(null);
                        }


                    }
                    if(listv.indexOf("String")!=-1)
                    {
                        String bk=backg.getSelectedItem().toString();
                        String fr=foreg.getSelectedItem().toString();
                        String ef=effects.getSelectedItem().toString();
                        if(bk!=null && bk.indexOf("Choose")==-1)
                        {
                            Color c=getColor(bk);
                            if(c!=null)
                            {
                                UILauncher.getUIConfigRef().setCurrentBackGrndColor_STRING(c);
                                UILauncher.getUIConfigRef().addPref("string_backg_color","["+c.getRed()+","+c.getGreen()+","+c.getBlue()+"]");
                            }
                            else
                            {
                                c=UIConfig.defaultBackgColor;
                                UILauncher.getUIConfigRef().setCurrentBackGrndColor_STRING(c);
                                UILauncher.getUIConfigRef().addPref("string_backg_color","["+c.getRed()+","+c.getGreen()+","+c.getBlue()+"]");
                            }


                        }
                         if(bk==null || bk.indexOf("---")!=-1)
                        {
                            UILauncher.getUIConfigRef().removePref("string_backg_color");
                            UILauncher.getUIConfigRef().setCurrentBackGrndColor_STRING(null);
                        }


                        if(fr!=null && fr.indexOf("Choose")==-1)
                        {
                            Color c=getColor(fr);
                            if(c!=null)
                            {
                                UILauncher.getUIConfigRef().setCurrentForeGrndColor_STRING(c);
                                UILauncher.getUIConfigRef().addPref("string_foreg_color","["+c.getRed()+","+c.getGreen()+","+c.getBlue()+"]");
                            }
                        }
                         if(fr==null || fr.indexOf("---")!=-1)
                        {
                            UILauncher.getUIConfigRef().removePref("string_foreg_color");
                            UILauncher.getUIConfigRef().setCurrentForeGrndColor_STRING(null);
                        }


                        if(ef!=null && ef.indexOf("None")==-1)
                        {
                            UILauncher.getUIConfigRef().setEffectSTRING(ef);
                            UILauncher.getUIConfigRef().addPref("string_effect",ef);
                        }

                        if(ef==null || ef.indexOf("None")!=-1)
                        {
                            UILauncher.getUIConfigRef().removePref("string_effect");
                            UILauncher.getUIConfigRef().setEffectSTRING(null);
                        }

                    }
                    
                    if(listv.indexOf("Annotation")!=-1)
                    {
                        String bk=backg.getSelectedItem().toString();
                        String fr=foreg.getSelectedItem().toString();
                        String ef=effects.getSelectedItem().toString();
                        if(bk!=null && bk.indexOf("Choose")==-1)
                        {
                            Color c=getColor(bk);
                            if(c!=null)
                            {
                                UILauncher.getUIConfigRef().setCurrentBackGrndColor_ANN(c);
                                UILauncher.getUIConfigRef().addPref("ann_backg_color","["+c.getRed()+","+c.getGreen()+","+c.getBlue()+"]");
                            }
                            else
                            {
                                c=UIConfig.defaultBackgColor;
                                UILauncher.getUIConfigRef().setCurrentBackGrndColor_ANN(c);
                                UILauncher.getUIConfigRef().addPref("ann_backg_color","["+c.getRed()+","+c.getGreen()+","+c.getBlue()+"]");
                            }


                        }
                         if(bk==null || bk.indexOf("---")!=-1)
                        {
                            UILauncher.getUIConfigRef().removePref("ann_backg_color");
                            UILauncher.getUIConfigRef().setCurrentBackGrndColor_ANN(null);
                        }


                        if(fr!=null && fr.indexOf("Choose")==-1)
                        {
                            Color c=getColor(fr);
                            if(c!=null)
                            {
                                UILauncher.getUIConfigRef().setCurrentForeGrndColor_ANN(c);
                                UILauncher.getUIConfigRef().addPref("ann_foreg_color","["+c.getRed()+","+c.getGreen()+","+c.getBlue()+"]");
                            }
                        }
                         if(fr==null || fr.indexOf("---")!=-1)
                        {
                            UILauncher.getUIConfigRef().removePref("ann_foreg_color");
                            UILauncher.getUIConfigRef().setCurrentForeGrndColor_ANN(null);
                        }


                        if(ef!=null && ef.indexOf("None")==-1)
                        {
                            UILauncher.getUIConfigRef().setEffectANN(ef);
                            UILauncher.getUIConfigRef().addPref("ann_effect",ef);
                        }

                        if(ef==null || ef.indexOf("None")!=-1)
                        {
                            UILauncher.getUIConfigRef().removePref("ann_effect");
                            UILauncher.getUIConfigRef().setEffectANN(null);
                        }

                    }
                }
            }


        }
        if(e.getSource()==preview)
        {

            Object ls=list.getSelectedValue();
            if(ls!=null)
            {
                String listv=ls.toString();
                if(listv!=null)
                {
                    if(listv.indexOf("Keyword")!=-1)
                    {
                        String bk=backg.getSelectedItem().toString();
                        String fr=foreg.getSelectedItem().toString();
                        String ef=effects.getSelectedItem().toString();
                        if(bk!=null && bk.indexOf("Choose")==-1)  // FIXME
                        {
                            Color c=getColor(bk);
                            UILauncher.getUIConfigRef().setCurrentBackGrndColor_KEYWORD_PREVW(c);
                        }
                        if(fr!=null && fr.indexOf("Choose")==-1)
                        {
                            Color c=getColor(fr);
                            UILauncher.getUIConfigRef().setCurrentForeGrndColor_KEYWORD_PREVW(c);
                        }
                        if(ef!=null && ef.indexOf("None")==-1)
                        {
                            UILauncher.getUIConfigRef().setEffectKEYWORD_PREVW(ef);
                        }

                    }
                    if(listv.indexOf("Number")!=-1)
                    {
                        String bk=backg.getSelectedItem().toString();
                        String fr=foreg.getSelectedItem().toString();
                        String ef=effects.getSelectedItem().toString();
                        if(bk!=null && bk.indexOf("Choose")==-1)
                        {
                            Color c=getColor(bk);
                            UILauncher.getUIConfigRef().setCurrentBackGrndColor_NUMBER_PREVW(c);

                        }
                        if(fr!=null && fr.indexOf("Choose")==-1)
                        {
                            Color c=getColor(fr);
                            UILauncher.getUIConfigRef().setCurrentForeGrndColor_NUMBER_PREVW(c);

                        }
                        if(ef!=null && ef.indexOf("None")==-1)
                        {
                            UILauncher.getUIConfigRef().setEffectNUMBER_PREVW(ef);

                        }
                    }
                    if(listv.indexOf("Operator")!=-1)
                    {
                        String bk=backg.getSelectedItem().toString();
                        String fr=foreg.getSelectedItem().toString();
                        String ef=effects.getSelectedItem().toString();
                        if(bk!=null && bk.indexOf("Choose")==-1)
                        {
                            Color c=getColor(bk);
                            UILauncher.getUIConfigRef().setCurrentBackGrndColor_OPERATOR_PREVW(c);

                        }
                        if(fr!=null && fr.indexOf("Choose")==-1)
                        {
                            Color c=getColor(fr);
                            UILauncher.getUIConfigRef().setCurrentForeGrndColor_OPERATOR_PREVW(c);

                        }
                        if(ef!=null && ef.indexOf("None")==-1)
                        {
                            UILauncher.getUIConfigRef().setEffectOP_PREVW(ef);

                        }
                    }
                    if(listv.indexOf("String")!=-1)
                    {
                        String bk=backg.getSelectedItem().toString();
                        String fr=foreg.getSelectedItem().toString();
                        String ef=effects.getSelectedItem().toString();
                        if(bk!=null && bk.indexOf("Choose")==-1)
                        {
                            Color c=getColor(bk);
                            UILauncher.getUIConfigRef().setCurrentBackGrndColor_STRING_PREVW(c);

                        }
                        if(fr!=null && fr.indexOf("Choose")==-1)
                        {
                            Color c=getColor(fr);
                            UILauncher.getUIConfigRef().setCurrentForeGrndColor_STRING_PREVW(c);

                        }
                        if(ef!=null && ef.indexOf("None")==-1)
                        {
                            UILauncher.getUIConfigRef().setEffectSTRING_PREVW(ef);

                        }
                    }
                    if(listv.indexOf("Annotation")!=-1)
                    {
                        String bk=backg.getSelectedItem().toString();
                        String fr=foreg.getSelectedItem().toString();
                        String ef=effects.getSelectedItem().toString();
                        if(bk!=null && bk.indexOf("Choose")==-1)
                        {
                            Color c=getColor(bk);
                            UILauncher.getUIConfigRef().setCurrentBackGrndColor_ANN_PREVW(c);

                        }
                        if(fr!=null && fr.indexOf("Choose")==-1)
                        {
                            Color c=getColor(fr);
                            UILauncher.getUIConfigRef().setCurrentForeGrndColor_ANN_PREVW(c);

                        }
                        if(ef!=null && ef.indexOf("None")==-1)
                        {
                            UILauncher.getUIConfigRef().setEffectANN_PREVW(ef);

                        }
                    }
                }
            }




            JdecPreviewKit kit=new JdecPreviewKit();
            ta.setEditorKitForContentType("text/java", kit);
            ta.setContentType("text/java");
            ta.setText(getSampleCode());
            ta.setBackground(Color.LIGHT_GRAY);
            applyP.validate();
            main.validate();
            CategoryChooser.this.validate();


        }
    }

    private Color getColor(String s)
    {
        int s1=s.indexOf("[");
        if(s1==-1)return null;
        int s2=s.indexOf("]");
        String s3=s.substring(s1+1,s2);
        StringTokenizer sk=new StringTokenizer(s3,",");
        int cls[]=new int[3];
        int r=-1;
        int g=-1;
        int b=-1;
        int i=0;
        while(sk.hasMoreTokens())
        {
            String t=(String)sk.nextToken();
            t=t.trim();
            cls[i]=Integer.parseInt(t);
            i++;
        }

        r=cls[0];
        g=cls[1];
        b=cls[2];
        return new Color(r,g,b);
    }

    public JComboBox getBackg() {
        return backg;
    }

    public JComboBox getEffects() {
        return effects;
    }

    public JComboBox getForeg() {
        return foreg;
    }

    public JPanel getTypes2() {
        return types2;
    }

    public JPanel getValues() {
        return values;
    }

    static class ListListener implements ListSelectionListener
    {

        private static ListListener ll=null;
        private ListListener()
        {

        }
        public static ListListener getListener()
        {
            if(ll!=null)return ll;
            else
            {
                ll=new ListListener();
                return ll;
            }

        }

        public void valueChanged(ListSelectionEvent e) {

            if(e.getSource()==list)
            {
                Object o=list.getSelectedValue();
                if(o!=null)
                {

                    String s=o.toString();
                    if(s.indexOf("Keyword")!=-1)
                    {
                        Object at1=null;

                        if(UILauncher.getUIConfigRef().getCurrentColorChooser()!=null)
                        {
                            UILauncher.getUIConfigRef().getCurrentColorChooser().setVisible(false);
                            UILauncher.getUIConfigRef().getCurrentColorChooser().dispose();
                        }
                        Color fc=UILauncher.getUIConfigRef().getCurrentForeGrndColor_KEYWORD();
                        Color bc=UILauncher.getUIConfigRef().getCurrentBackGrndColor_KEYWORD();
                        String eff=UILauncher.getUIConfigRef().getEffectKEYWORD();
                        if(fc!=null)
                        {
                            at1= foreg.getItemAt(1);
                            if(at1!=null && at1.toString().indexOf("Choose")==-1)
                            {
                                foreg.removeItemAt(1);
                            }

                            foreg.insertItemAt("["+fc.getRed()+","+fc.getGreen()+","+fc.getBlue()+"]",1);
                            foreg.setSelectedIndex(1);
                        }
                        else
                        {
                            int noneInd=getItemIndex("---",foreg);
                            if(noneInd!=-1)
                                foreg.setSelectedIndex(noneInd);

                        }

                        if(bc!=null)
                        {
                            at1=backg.getItemAt(1);
                            if(at1!=null && at1.toString().indexOf("Choose")==-1)
                            {
                                backg.removeItemAt(1);
                            }
                            backg.insertItemAt("["+bc.getRed()+","+bc.getGreen()+","+bc.getBlue()+"]",1);
                            backg.setSelectedIndex(1);
                        }
                         else
                        {
                            int noneInd=getItemIndex("---",backg);
                            if(noneInd!=-1)
                                backg.setSelectedIndex(noneInd);

                        }


                        if(eff!=null)
                        {
                            for(int k=0;k<effects.getItemCount();k++)
                            {
                                String name=effects.getItemAt(k).toString();
                                if(name.indexOf(eff)!=-1)
                                {
                                    effects.setSelectedIndex(k);
                                    break;
                                }

                            }
                        }
                        else
                        {
                            int noneInd=getItemIndex("None",effects);
                            if(noneInd!=-1)
                                effects.setSelectedIndex(noneInd);
                        }

                    }
                    if(s.indexOf("Annotation")!=-1)
                    {
                        Object at1=null;

                        if(UILauncher.getUIConfigRef().getCurrentColorChooser()!=null)
                        {
                            UILauncher.getUIConfigRef().getCurrentColorChooser().setVisible(false);
                            UILauncher.getUIConfigRef().getCurrentColorChooser().dispose();
                        }
                        Color fc=UILauncher.getUIConfigRef().getCurrentForeGrndColor_ANN();
                        Color bc=UILauncher.getUIConfigRef().getCurrentBackGrndColor_ANN();
                        String eff=UILauncher.getUIConfigRef().getEffectANN();
                        if(fc!=null)
                        {
                            at1= foreg.getItemAt(1);
                            if(at1!=null && at1.toString().indexOf("Choose")==-1)
                            {
                                foreg.removeItemAt(1);
                            }

                            foreg.insertItemAt("["+fc.getRed()+","+fc.getGreen()+","+fc.getBlue()+"]",1);
                            foreg.setSelectedIndex(1);
                        }
                        else
                        {
                            int noneInd=getItemIndex("---",foreg);
                            if(noneInd!=-1)
                                foreg.setSelectedIndex(noneInd);

                        }

                        if(bc!=null)
                        {
                            at1=backg.getItemAt(1);
                            if(at1!=null && at1.toString().indexOf("Choose")==-1)
                            {
                                backg.removeItemAt(1);
                            }
                            backg.insertItemAt("["+bc.getRed()+","+bc.getGreen()+","+bc.getBlue()+"]",1);
                            backg.setSelectedIndex(1);
                        }
                         else
                        {
                            int noneInd=getItemIndex("---",backg);
                            if(noneInd!=-1)
                                backg.setSelectedIndex(noneInd);

                        }


                        if(eff!=null)
                        {
                            for(int k=0;k<effects.getItemCount();k++)
                            {
                                String name=effects.getItemAt(k).toString();
                                if(name.indexOf(eff)!=-1)
                                {
                                    effects.setSelectedIndex(k);
                                    break;
                                }

                            }
                        }
                        else
                        {
                            int noneInd=getItemIndex("None",effects);
                            if(noneInd!=-1)
                                effects.setSelectedIndex(noneInd);
                        }

                    }
                    if(s.indexOf("Number")!=-1)
                    {
                        Object at1=null;


                        if(UILauncher.getUIConfigRef().getCurrentColorChooser()!=null)
                        {
                            UILauncher.getUIConfigRef().getCurrentColorChooser().setVisible(false);
                            UILauncher.getUIConfigRef().getCurrentColorChooser().dispose();
                        }
                        Color fc=UILauncher.getUIConfigRef().getCurrentForeGrndColor_NUMBER();
                        Color bc=UILauncher.getUIConfigRef().getCurrentBackGrndColor_NUMBER();
                        String eff=UILauncher.getUIConfigRef().getEffectNUMBER();
                        if(fc!=null)
                        {
                            at1=foreg.getItemAt(1);
                            if(at1!=null && at1.toString().indexOf("Choose")==-1)
                            {
                                foreg.removeItemAt(1);
                            }
                            foreg.insertItemAt("["+fc.getRed()+","+fc.getGreen()+","+fc.getBlue()+"]",1);
                            foreg.setSelectedIndex(1);
                        }
                        else
                        {
                            int noneInd=getItemIndex("---",foreg);
                            if(noneInd!=-1)
                                foreg.setSelectedIndex(noneInd);

                        }
                        if(bc!=null)
                        {
                            at1=backg.getItemAt(1);
                            if(at1!=null && at1.toString().indexOf("Choose")==-1)
                            {
                                backg.removeItemAt(1);
                            }
                            backg.insertItemAt("["+bc.getRed()+","+bc.getGreen()+","+bc.getBlue()+"]",1);
                            backg.setSelectedIndex(1);
                        }
                        else
                        {
                            int noneInd=getItemIndex("---",backg);
                            if(noneInd!=-1)
                                backg.setSelectedIndex(noneInd);

                        }
                        if(eff!=null)
                        {
                            for(int k=0;k<effects.getItemCount();k++)
                            {
                                String name=effects.getItemAt(k).toString();
                                if(name.indexOf(eff)!=-1)
                                {
                                    effects.setSelectedIndex(k);
                                    break;
                                }

                            }
                        }
                        else
                        {
                            int noneInd=getItemIndex("None",effects);
                            if(noneInd!=-1)
                                effects.setSelectedIndex(noneInd);
                        }

                    }
                    if(s.indexOf("Operator")!=-1)
                    {
                        Object at1=null;//foreg.getItemAt(1);



                        if(UILauncher.getUIConfigRef().getCurrentColorChooser()!=null)
                        {
                            UILauncher.getUIConfigRef().getCurrentColorChooser().setVisible(false);
                            UILauncher.getUIConfigRef().getCurrentColorChooser().dispose();
                        }
                        Color fc=UILauncher.getUIConfigRef().getCurrentForeGrndColor_OPERATOR();
                        Color bc=UILauncher.getUIConfigRef().getCurrentBackGrndColor_OPERATOR();
                        String eff=UILauncher.getUIConfigRef().getEffectOP();
                        if(fc!=null)
                        {
                            foreg.getItemAt(1);
                            if(at1!=null && at1.toString().indexOf("Choose")==-1)
                            {
                                foreg.removeItemAt(1);
                            }
                            foreg.insertItemAt("["+fc.getRed()+","+fc.getGreen()+","+fc.getBlue()+"]",1);
                            foreg.setSelectedIndex(1);
                        }
                         else
                        {
                            int noneInd=getItemIndex("---",foreg);
                            if(noneInd!=-1)
                                foreg.setSelectedIndex(noneInd);

                        }
                        if(bc!=null)
                        {
                            at1=backg.getItemAt(1);
                            if(at1!=null && at1.toString().indexOf("Choose")==-1)
                            {
                                backg.removeItemAt(1);
                            }
                            backg.insertItemAt("["+bc.getRed()+","+bc.getGreen()+","+bc.getBlue()+"]",1);
                            backg.setSelectedIndex(1);
                        }
                         else
                        {
                            int noneInd=getItemIndex("---",backg);
                            if(noneInd!=-1)
                                backg.setSelectedIndex(noneInd);

                        }
                        if(eff!=null)
                        {
                            for(int k=0;k<effects.getItemCount();k++)
                            {
                                String name=effects.getItemAt(k).toString();
                                if(name.indexOf(eff)!=-1)
                                {
                                    effects.setSelectedIndex(k);
                                    break;
                                }

                            }
                        }
                        else
                        {
                            int noneInd=getItemIndex("None",effects);
                            if(noneInd!=-1)
                                effects.setSelectedIndex(noneInd);
                        }

                    }
                    if(s.indexOf("String")!=-1)
                    {




                        Object at1=null;
                        if(UILauncher.getUIConfigRef().getCurrentColorChooser()!=null)
                        {
                            UILauncher.getUIConfigRef().getCurrentColorChooser().setVisible(false);
                            UILauncher.getUIConfigRef().getCurrentColorChooser().dispose();
                        }
                        Color fc=UILauncher.getUIConfigRef().getCurrentForeGrndColor_STRING();
                        Color bc=UILauncher.getUIConfigRef().getCurrentBackGrndColor_STRING();
                        String eff=UILauncher.getUIConfigRef().getEffectSTRING();
                        if(fc!=null)
                        {
                            at1=foreg.getItemAt(1);
                            if(at1!=null && at1.toString().indexOf("Choose")==-1)
                            {
                                foreg.removeItemAt(1);
                            }
                            foreg.insertItemAt("["+fc.getRed()+","+fc.getGreen()+","+fc.getBlue()+"]",1);
                            foreg.setSelectedIndex(1);
                        }
                         else
                        {
                            int noneInd=getItemIndex("---",foreg);
                            if(noneInd!=-1)
                                foreg.setSelectedIndex(noneInd);

                        }
                        if(bc!=null)
                        {
                            at1=backg.getItemAt(1);
                            if(at1!=null && at1.toString().indexOf("Choose")==-1)
                            {
                                backg.removeItemAt(1);
                            }
                            backg.insertItemAt("["+bc.getRed()+","+bc.getGreen()+","+bc.getBlue()+"]",1);
                            backg.setSelectedIndex(1);
                        }
                         else
                        {
                            int noneInd=getItemIndex("---",backg);
                            if(noneInd!=-1)
                                backg.setSelectedIndex(noneInd);

                        }
                        if(eff!=null)
                        {
                            for(int k=0;k<effects.getItemCount();k++)
                            {
                                String name=effects.getItemAt(k).toString();
                                if(name.indexOf(eff)!=-1)
                                {
                                    effects.setSelectedIndex(k);
                                    break;
                                }

                            }
                        }
                        else
                        {
                            int noneInd=getItemIndex("None",effects);
                            if(noneInd!=-1)
                                effects.setSelectedIndex(noneInd);
                        }


                    }



                }



            }

        }
    }

    private static int getItemIndex(String s,JComboBox effects)
    {
        int i=-1;
        for(int k=0;k<effects.getItemCount();k++)
        {
            String name=effects.getItemAt(k).toString();
            if(name.indexOf(s)!=-1)
            {
                return k;
            }

        }
        return i;


    }
}
