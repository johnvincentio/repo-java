
/*
*  KeyAssist.java Copyright (c) 2006,07 Swaroop Belur
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
import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.sf.jdec.ui.util.LogWriter;
import net.sf.jdec.ui.util.UIUtil;

public class KeyAssist extends JFrame
{
    protected JTable table;
    protected KeyAssistDataModel model;
    protected JLabel coltitle;
    private UIUtil util;

    private LogWriter log;
    public KeyAssist(UIUtil util)
    {
        super("Jdec Key Assist(Read Only)");
        log=LogWriter.getInstance();
        this.util=util;
        setBounds(300,250,400,400);
        model = new KeyAssistDataModel(this,util);
        table = new JTable();
        table.setAutoCreateColumnsFromModel(false);
        table.setModel(model);

        for (int k = 0; k < 2; k++)
        {
            TableCellRenderer renderer;
            DefaultTableCellRenderer textRenderer =	new DefaultTableCellRenderer();
            renderer = textRenderer;
            TableCellEditor editor;
            editor = new DefaultCellEditor(new JTextField());
            TableColumn column = new TableColumn(k,80,renderer,editor);
            table.addColumn(column);
        }


        JScrollPane ps = new JScrollPane();
        ps.setSize(550, 150);
        ps.getViewport().add(table);
                getContentPane().add(ps, BorderLayout.CENTER);


        setVisible(true);
    }

  
    


    
}


class KeyAssistDataModel extends AbstractTableModel
{
    public static final ConfigColumnDesc columns[] = {
        new ConfigColumnDesc( "Action", 80, JLabel.LEFT ),
        new ConfigColumnDesc( "Shortcut", 80, JLabel.RIGHT )

    };


    protected KeyAssist myparent;
    protected ArrayList KeyAssistData;
    public KeyAssistDataModel(KeyAssist parent,UIUtil util) {
        myparent = parent;
        KeyAssistData = new ArrayList();
        populaConfigData(util);
    }

    public void populaConfigData(UIUtil util)
    {

        for(int z=0;z<KeyAssistData.size();z++)
        {
            KeyAssistData.remove(z);
        }
        KeyAssistData.add(new KeyAssistRow("New Decompiler Task","CTRL-N"));
        KeyAssistData.add(new KeyAssistRow("New Java File","CTRL-J"));
        KeyAssistData.add(new KeyAssistRow("View Java File","CTRL-SHIFT-V"));
        KeyAssistData.add(new KeyAssistRow("Refresh","CTRL-R"));
        KeyAssistData.add(new KeyAssistRow("Save","CTRL-S"));
        KeyAssistData.add(new KeyAssistRow("Show Recent Files","CTRL-SHIFT-R"));
        KeyAssistData.add(new KeyAssistRow("Copy","CTRL-C"));
        KeyAssistData.add(new KeyAssistRow("Cut","CTRL-X"));
        KeyAssistData.add(new KeyAssistRow("Paste","CTRL-V"));
        KeyAssistData.add(new KeyAssistRow("Select All","CTRL-A"));
        KeyAssistData.add(new KeyAssistRow("Preferences","CTRL-P"));
        KeyAssistData.add(new KeyAssistRow("Search","CTRL-F"));
        KeyAssistData.add(new KeyAssistRow("Class General File","CTRL-SHIFT-G"));
        KeyAssistData.add(new KeyAssistRow("Constant Pool","CTRL-SHIFT-C"));
        KeyAssistData.add(new KeyAssistRow("Class Local Variables","CTRL-SHIFT-L"));
        KeyAssistData.add(new KeyAssistRow("Show Methods For Current class","CTRL-M"));
        KeyAssistData.add(new KeyAssistRow("Jdec(Decompiler) Configuration","Alt Shift A"));
        KeyAssistData.add(new KeyAssistRow("Open UI Log File","CTRL-SHIFT-U"));
        KeyAssistData.add(new KeyAssistRow("Open Decompiler Log File","CTRL-SHIFT-D"));
        KeyAssistData.add(new KeyAssistRow("Open Both In Separate Tabs","CTRL-SHIFT-B"));
        KeyAssistData.add(new KeyAssistRow("Open Simple Editor","ALT-SHIFT-E"));
        KeyAssistData.add(new KeyAssistRow("Show Clipboard","ALT-SHIFT-B"));
        KeyAssistData.add(new KeyAssistRow("System Properties","ALT-SHIFT-S"));
        KeyAssistData.add(new KeyAssistRow("Go to user home folder","ALT-SHIFT-H"));
        KeyAssistData.add(new KeyAssistRow("Find Target","ALT-SHIFT-F"));
        KeyAssistData.add(new KeyAssistRow("Show System Time","ALT-SHIFT-T"));
        KeyAssistData.add(new KeyAssistRow("Hide System Time","ALT-SHIFT-U"));
        KeyAssistData.add(new KeyAssistRow("Compile Java","ALT-SHIFT-C"));
        KeyAssistData.add(new KeyAssistRow("Run Java Application","ALT-SHIFT-R"));
        KeyAssistData.add(new KeyAssistRow("Show Java Tips","CTRL-T"));
        KeyAssistData.add(new KeyAssistRow("Key Assist","ctrl shift F"));
        
        /*KeyAssistData.add(new KeyAssistRow("Temp_Dir",util.getProjectTempDir()));
        KeyAssistData.add(new KeyAssistRow("Inner_Depth",util.getInnerDepth()));
        KeyAssistData.add(new KeyAssistRow("Inline_Anonymous_Inner_Class_Content",util.getInlineAnonymous()));
        KeyAssistData.add(new KeyAssistRow("Interpret_Exception_Table",util.getInterpret()));*/
    }



    public int getRowCount() {
        return KeyAssistData==null ? 0 : KeyAssistData.size();
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
        KeyAssistRow row = (KeyAssistRow)KeyAssistData.get(nRow);
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
    }



}


class KeyAssistRow
{
    private String action;
    private String shortcut=null;
    private int pos=1;


    KeyAssistRow(String title,String value)
    {
        this.action=title;
        this.shortcut=value;

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
        shortcut=s;
    }
    public String getConfigName()
    {
        return action;
    }
    public String getConfigValue()
    {
        return shortcut;
    }

}

class KeyAssistColumnDesc
{
    public String action;
    int colwidth;
    int colalign;
    public KeyAssistColumnDesc(String title, int width, int alignment)
    {
        action = title;
        colwidth = width;
        colalign = alignment;
    }



}





