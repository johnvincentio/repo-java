
/*
*  DecompilerConfigDetails.java Copyright (c) 2006,07 Swaroop Belur
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
package net.sf.jdec.ui.config;
import net.sf.jdec.ui.main.UILauncher;
import net.sf.jdec.ui.util.LogWriter;
import net.sf.jdec.ui.util.UIUtil;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class DecompilerConfigDetails extends JInternalFrame implements ActionListener
{
    protected JTable table;
    protected JdecConfigDataModel model;
    protected JLabel coltitle;
    private UIUtil util;

    private LogWriter log;
    public DecompilerConfigDetails(UIUtil util)
    {
        super("Configuration Details");
        log=LogWriter.getInstance();
        this.util=util;
        setSize(570, 200);
        model = new JdecConfigDataModel(this,util);
        table = new JTable();
        table.setAutoCreateColumnsFromModel(false);
        table.setModel(model);

        for (int k = 0; k < 2; k++)
        {
            TableCellRenderer renderer;
            DefaultTableCellRenderer textRenderer =	new DefaultTableCellRenderer();
            if(k==0)
            {
            	textRenderer.setBackground(Color.LIGHT_GRAY);
            	textRenderer.setForeground(new Color(75,0,75));
            	textRenderer.setBorder(new EtchedBorder());
            	textRenderer.setFont(new Font("Courier Bold Italic",Font.BOLD,14));
            }
            renderer = textRenderer;
            TableCellEditor editor;
            editor = new DefaultCellEditor(new JTextField());
            TableColumn column = new TableColumn(k,80,renderer,editor);
            table.addColumn(column);
        }


        JScrollPane ps = new JScrollPane();
        ps.setSize(550, 150);
        ps.getViewport().add(table);

        JToggleButton upd=new JToggleButton("Update Changes");
        upd.setToolTipText("Click Here to update your changes...");
        upd.addActionListener(this);

        JButton com=new JButton("Show ConfigFile Comments");
        com.setActionCommand("comments");
        com.addActionListener(this);

        JButton exam=new JButton("Example Values");
        exam.setActionCommand("examples");
        exam.addActionListener(this);

        getContentPane().add(ps, BorderLayout.CENTER);

        JPanel buts=new JPanel();
        buts.add(upd,BorderLayout.WEST);
        buts.add(com,BorderLayout.CENTER);
        buts.add(exam,BorderLayout.EAST);


        JPanel panel=new JPanel();
        JLabel note=new JLabel("Double click On The Value to Update...");
        note.setForeground(Color.RED);
        note.setFont(new Font("BOLD",0,10));
        panel.add(note,BorderLayout.WEST);
        panel.add(buts,BorderLayout.CENTER);
        getContentPane().add(panel, BorderLayout.SOUTH);




        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae)
    {

        if(ae.getActionCommand().equals("examples"))
        {
            JFrame exm=new JFrame("Example Values For Configuration Properties");
            exm.removeAll();
            //exm.validate();

            StringBuffer sb=new StringBuffer("");
            sb.append("Output_Folder_Path=c:/temp/output\n\n");
            sb.append("Log_File_Path=c:/temp/output/log.txt\n\n");
            sb.append("UI_LOG_FILE_PATH=c:/temp/output/uilog.txt\n\n");
            sb.append("JAVA_CLASS_FILE=c:/test.class\n\n");
            sb.append("JAR_FILE_PATH=c:/test.jar\n\n");
            sb.append("Temp_Dir=d:/temp/output\n\n");
            
            StackTraceElement st;



            final JFrame frame=new JFrame("Example Values For Configuration Properties");
            JTextArea area=new JTextArea(sb.toString());
            area.setEditable(false);
            area.setWrapStyleWord(true);
            JScrollPane pane=new JScrollPane();
            JViewport port=pane.getViewport();
            port.setView(area);
            frame.getContentPane().add(pane,BorderLayout.CENTER);
            JFrame main=UILauncher.getMainFrame();
            frame.setBounds(main.getWidth()/5,main.getHeight()/4,670,550);
            frame.setVisible(true);

            WindowListener wndCloser = new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    frame.setVisible(false);
                    frame.dispose();

                }
            };
            frame.addWindowListener(wndCloser);

        }



        if(ae.getActionCommand().equals("comments"))
        {

            final JFrame frame=new JFrame("Configuration File Comments");
            JTextArea area=new JTextArea(getConfigFileComments());
            JScrollPane pane=new JScrollPane();
            JViewport port=pane.getViewport();
            port.setView(area);
            frame.getContentPane().add(pane,BorderLayout.CENTER);
            JFrame main=UILauncher.getMainFrame();
            frame.setBounds(main.getWidth()/5,main.getHeight()/4,670,550);
            frame.setVisible(true);

            WindowListener wndCloser = new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    frame.setVisible(false);
                    frame.dispose();

                }
            };
            frame.addWindowListener(wndCloser);

        }

        if(ae.getActionCommand().equals("Update Changes"))
        {
            StringBuffer temp=new StringBuffer("");
            int s;
            for(int r=0;r<table.getRowCount();r++)
            {
                for(s=0;s<table.getColumnCount();s++)
                {
                    java.lang.String propName=(String)table.getValueAt(r, s);
                    java.lang.String propValue=(String)table.getValueAt(r, ++s);
                    temp.append(propName+"="+propValue);
                    temp.append("\n");
                }
            }
            if(temp.toString().length() > 0)
            {
                String content=UILauncher.getUIutil().getDecompilerPropertyFileComment();
                content+="\n\n";
                content+=temp.toString();
                //System.out.println(content);
                try
                {
                    File config=new File(System.getProperty("user.home")+File.separator+"tempconfig.properties");
                    if(config.exists())
                    {
                        config.delete();
                    }
                    BufferedWriter br=new BufferedWriter(new OutputStreamWriter(new  FileOutputStream(config)));
                    br.write(content);
                    br.flush();
                    //System.out.println("Before updating util"+util.getJavaClassFile());
                    reinitializeUtilObject(util);
                    // System.out.println("After updating"+util.getJavaClassFile());
                    UILauncher.getUIutil().setConfigChanged(true);

                    JOptionPane.showMessageDialog(UILauncher.getMainFrame(), "Change(s) Made...");
                }
                catch(Exception e)
                {
                    JOptionPane.showMessageDialog(UILauncher.getMainFrame(), "Could Not Save Change(s)...");


                }



            }

        }

    }
    private void reinitializeUtilObject(UIUtil util)
    {
        util.reLoadDecompilerConfigParams();
    }


    private java.lang.String getConfigFileComments()
    {
        return "#Inner_Depth ---> Number of Levels considered when decompiling inner classes     \n" +
                "#Inline_Anonymous_Inner_Class_Content-->The purpose is to show methods of anonymous inner class\n" +
                "#                 in main class File which contains the anonymous inner class\n" +
                "#\t\t\t\t\t\t\t    \n" +
                "#Interpret_Exception_Table --> This is used only while disassembling.It has No effect on showing decompiling\n" +
                "#                 output or decompiling itself. Only meant for the end user to get more information as to \n" +
                "#                 how the jdec has used the table list to produce try-catch-finally blocks\n" +
                "#\n" +
                "#Interpret_Non_ASCII---> This flag is used while processing String characters or chars whose\n"+
                "#                 values fall outside the normal ascii range\n\n\n"+
                "#Force_Non_ASCII--> This flag if set to true will cause all ascii characters in String or chars \nto be displayed in  Unicode\n"+
                "# [NOTE:] Valid Values For JDec_Option...\n\n\n" +
                "#  1>decompileClass\n" +
                "#  2>disassemble\n" +
                "#  3>decompileJar\n" +
                "#  4>ConstantPool\n" +
                "#  5>localVariables\n" +
                "#  6>Skeleton\n" +
                "# \n" +
                "# [NOTE:] Valid Values For Show_imports\n" +
                "#  1>true\n" +
                "#  2>false\n" +
                "# \n" +
                "# [NOTE:] IF the user sets file extension as java or class For the parameter \"Output_File_Extension\"\n" +
                "#         Then jdec ignores it as it will replace the corresponding java file IF PRESENT\n" +
                "#         or it may  overwrite the class File\n" +
                "#\n" +
                "#\n" +
                "# [NOTE:] Interpret_Exception_Table only tells you how Jdec has interpreted the input given by \n" +
                "#         compiler.If the try-catch-finally output is misleading it only means the interpretation\n" +
                "#         is not fully correct\n" +
                "#\n" +
                "# [NOTE:] Inner_Depth: If this is set to 0, no inner classes will be decompiled.\n" +
                "#         Setting to 1 will decompile inner classes to a depth of 1 and so on   \n" +
                "###############################################################################################################";
    }
}


class JdecConfigDataModel extends AbstractTableModel
{
    public static final ConfigColumnDesc columns[] = {
        new ConfigColumnDesc( "Configuration Property Name", 80, JLabel.LEFT ),
        new ConfigColumnDesc( "Configuration Property Value", 80, JLabel.RIGHT )

    };


    protected DecompilerConfigDetails myparent;
    protected ArrayList configData;
    public JdecConfigDataModel(DecompilerConfigDetails parent,UIUtil util) {
        myparent = parent;
        configData = new ArrayList();
        populaConfigData(util);
    }

    public void populaConfigData(UIUtil util)
    {

        for(int z=0;z<configData.size();z++)
        {
            configData.remove(z);
        }
        configData.add(new EachConfigRow("Output_Mode",util.getMode()));
        configData.add(new EachConfigRow("Log_Mode",util.getLogMode()));
        configData.add(new EachConfigRow("Output_Folder_Path",util.getOutputFilePath()));
        configData.add(new EachConfigRow("Output_File_Extension",util.getExtension()));

        configData.add(new EachConfigRow("LOG_LEVEL",util.getLogLevel()));
        configData.add(new EachConfigRow("Log_File_Path",util.getLogPath()));
        configData.add(new EachConfigRow("UI_LOG_FILE_PATH",util.getUilogfile()));

        configData.add(new EachConfigRow("JAVA_CLASS_FILE",util.getJavaClassFile()));
        configData.add(new EachConfigRow("JAR_FILE_PATH",util.getJarFilePath()));
        configData.add(new EachConfigRow("Show_Imports",util.getShowImport()));
        configData.add(new EachConfigRow("JDec_Option",util.getJdecOption()));
        configData.add(new EachConfigRow("Temp_Dir",util.getProjectTempDir()));
        configData.add(new EachConfigRow("Inner_Depth",util.getInnerDepth()));
        configData.add(new EachConfigRow("Inline_Anonymous_Inner_Class_Content",util.getInlineAnonymous()));
        configData.add(new EachConfigRow("Interpret_Exception_Table",util.getInterpret()));
        configData.add(new EachConfigRow("Interpret_Non_ASCII",util.getInterpretNonAscii()));
        
        configData.add(new EachConfigRow("Force_Non_ASCII",util.getForceNonAscii()));
        configData.add(new EachConfigRow("Skip_Class_Version_Check",util.getSkipClassVersionCheck()));
        configData.add(new EachConfigRow("Formatting_Show_Flower_Bracket_at_same_line",util.getShowFlowerBracketAtSameLine()));

        
        
        
    }



    public int getRowCount() {
        return configData==null ? 0 : configData.size();
    }
    public int getColumnCount() {
        return columns.length;
    }
    public String getColumnName(int column) {
        return columns[column].configtitle;
    }
    public boolean isCellEditable(int row, int col) {
        if(col==1)
            return true;
        else
            return false;
    }
    public Object getValueAt(int nRow, int ncol) {
        if (nRow < 0 && nRow>=getRowCount())
            return "";
        EachConfigRow row = (EachConfigRow)configData.get(nRow);
        if(ncol==1)
        {

            return row.getConfigValue();

        }
        else if(ncol==0)
        {

            return row.getConfigName();

        }

        else
            return "";
    }
    public void setValueAt(Object value, int nRow, int nCol) {


        if (nRow < 0 && nRow>=getRowCount())
            return;
        EachConfigRow row = (EachConfigRow)configData.get(nRow);
        if(nCol==1 && value!=null){
            String svalue = value.toString();

            if(nRow==0 || nRow==1)
            {
                if(svalue!=null && !svalue.equalsIgnoreCase("file"))
                {
                    JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Valid Values For UI version of jdec : file ");
                    return;
                }
            }

            if(nRow==2)
            {
                if(svalue!=null)
                {
                    int winslash=svalue.indexOf("\\");
                    if(winslash!=-1)
                    {
                        svalue=svalue.replaceAll("\\\\","/");
                    }
                    if(svalue.trim().endsWith("/"))
                    {
                        int lastS=svalue.lastIndexOf("/");
                        if(lastS!=-1)
                        {
                            svalue=svalue.trim();
                            svalue=svalue.substring(0,lastS);
                        }
                    }

                    File t=new File(svalue);
                    if(!t.exists()){
                        int o=JOptionPane.showConfirmDialog(UILauncher.getMainFrame(),"Output Folder Does Not Exist\nWould You Like Jdec To Create the Folder ?","[ERROR] :",JOptionPane.YES_NO_OPTION);
                        if(o==JOptionPane.YES_OPTION){
                            t.mkdirs();
                        }
                        else
                        {
                            svalue="";
                        }
                    }
                }
            }
            if(nRow==4  )
            {
                if(svalue!=null && !svalue.equals("1") && !svalue.equals("2"))
                {
                    JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Valid Values: 1 , 2");
                    return;
                }
            }
            if(nRow==5 ||  nRow==6)
            {
                int winslash=svalue.indexOf("\\");
                if(winslash!=-1)
                {
                    svalue=svalue.replaceAll("\\\\","/");
                }



                if(svalue!=null)
                {
                    File p=new File(svalue);
                    int dot=svalue.indexOf(".");
                    if(dot!=-1)
                    {
                        p=p.getParentFile();
                        if(p==null || !p.exists())
                        {
                            int o=JOptionPane.showConfirmDialog(UILauncher.getMainFrame(),"Log Folder For The Log File Does Not Exist\nWould You Like Jdec To Create the Folder ?","[ERROR] :",JOptionPane.YES_NO_OPTION);
                            if(o==JOptionPane.YES_OPTION){
                                p.mkdirs();
                            }
                            else
                            {
                                svalue="";
                            }
                        }
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Please Enter The Full Path of Log File including Name...Like c:/temp/log.txt");
                        return;
                    }
                }

            }

            if(nRow==7)
            {
                int winslash=svalue.indexOf("\\");
                if(winslash!=-1)
                {
                    svalue=svalue.replaceAll("\\\\","/");
                }
            }


            if(nRow==9)
            {
                if(!svalue.equalsIgnoreCase("true") && !svalue.equalsIgnoreCase("false"))
                {
                    JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Valid Values: true , false");
                    return;
                }
            }
            if(nRow==10 )
            {
                if(!svalue.equals("decompileClass") && !svalue.equals("disassemble") && !svalue.equals("ConstantPool") && !svalue.equals("localVariables") && !svalue.equals("Skeleton") && !svalue.equals("decompileJar") && !svalue.equals("help"))
                {
                    JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Valid Values: decompileClass ,decompileJar, disassemble , ConstantPool , localVariables , Skeleton , help");
                    return;
                }
            }
            if(nRow==11)
            {
               if(svalue!=null)
                {
                    int winslash=svalue.indexOf("\\");
                    if(winslash!=-1)
                    {
                        svalue=svalue.replaceAll("\\\\","/");
                    }
                    if(svalue.trim().endsWith("/"))
                    {
                        int lastS=svalue.lastIndexOf("/");
                        if(lastS!=-1)
                        {
                            svalue=svalue.trim();
                            svalue=svalue.substring(0,lastS);
                        }
                    }

                    File t=new File(svalue);
                    if(!t.exists()){
                        int o=JOptionPane.showConfirmDialog(UILauncher.getMainFrame(),"Temp Folder Does Not Exist\nWould You Like Jdec To Create the Folder ?","[ERROR] :",JOptionPane.YES_NO_OPTION);
                        if(o==JOptionPane.YES_OPTION){
                            t.mkdirs();
                        }
                        else
                        {
                            svalue="";
                        }
                    }
                }
            }
            if(nRow==13)
            {
                if(svalue.equalsIgnoreCase("true"))
                {
                    JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Enabling This Flag Has no effect in This Release of Jdec...\nPlease check up the Project Home Site For any updates to this feature");
                    return;
                }
            }
            if(nRow==14)
            {
                if(svalue.equalsIgnoreCase("true"))
                {
                    JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"This Feature is meant For Disassembling of The class File. No effect on decompilation of Class File");
                    row.setConfigValue(svalue);
                    return;
                }
            }
            if(nRow==15 || nRow==16 || nRow==17)
            {
                if(!svalue.equals("true") && !svalue.equals("false"))
                {
                    JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Valid Values: true ,false");
                    return;
                }
            }
            


            row.setConfigValue(svalue);
        }
        if(nCol==0){


        }

    }



}


class EachConfigRow
{
    private String configName;
    private String configValue=null;
    private int pos=1;


    EachConfigRow(String title,String value)
    {
        this.configName=title;
        this.configValue=value;

    }


    public int getPos()
    {
        return pos;
    }

    public void setPos(int i)
    {
        pos=i;
    }
    public void setConfigValue(java.lang.String s)
    {
        configValue=s;
    }
    public String getConfigName()
    {
        return configName;
    }
    public String getConfigValue()
    {
        return configValue;
    }

}

class ConfigColumnDesc
{
    public String configtitle;
    int configwidth;
    int configalignment;
    public ConfigColumnDesc(String title, int width, int alignment)
    {
        configtitle = title;
        configwidth = width;
        configalignment = alignment;
    }



}





