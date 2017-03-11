
/*
 *  JDecStatusFrame.java Copyright (c) 2006,07 Swaroop Belur 
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



import java.awt.Dimension;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

/**
 *@author swaroop belur
 */
public class JdecStatusFrame extends UIObject{

     public void setDesc(java.lang.String desc)
     {
         this.whatAmI=desc;
     }

    public void setStatus(String status) {
        Status = status;
    }

    public void setLogMode(String logMode) {
        LogMode = logMode;
    }

    public void setLoglocation(String loglocation) {
        Loglocation = loglocation;
    }

    public void setExceptionPresent(String exceptionPresent) {
        ExceptionPresent = exceptionPresent;
    }


    String Status="";
    String LogMode="";
    String Loglocation="";
    String ExceptionPresent="";


    public JInternalFrame getStatus() {
        return statusFrame;
    }

    private JInternalFrame statusFrame=null;

     public JdecStatusFrame()
     {
        createStatusFrame();
     }

     private void createStatusFrame()
     {
        Object[][] TABLE_DATA = {
               {"Run Status:", this.Status,"    "},
               {"Log Mode:", LogMode,"    " },
               {"Log File Location:", Loglocation,"    "},
               {"Exception Present:", ExceptionPresent,"    "},

        };


         String[] COLUMN_NAMES = {
             "		Title		", "		Value		"
         };
        DefaultTableModel mod =new DefaultTableModel (TABLE_DATA, COLUMN_NAMES);
        JTable table = new JTable (mod);
        table.setAutoscrolls(false);
        //table.setBounds(0,0,table.getWidth()+130, table.getHeight());
       // table.setPreferredScrollableViewportSize(new Dimension(150,180));
        table.setMinimumSize(new Dimension(240,110));
        JScrollPane pane =new JScrollPane (table,ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        statusFrame=new JInternalFrame("Project Status Details...");
        Dimension d=statusFrame.getContentPane().getSize();
        
        //statusFrame.setSize(50,100);
        statusFrame.getContentPane().add(table);
        statusFrame.setVisible(true);
     }

     public void recreateStatusFrame()
     {
         createStatusFrame();
     }


}
