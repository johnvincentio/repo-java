
/*
 *  FileiInfoFrame.java Copyright (c) 2006,07 Swaroop Belur 
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
import java.awt.Dimension;
import java.io.File;
import java.util.Date;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 *@author swaroop belur
 * 
 *
 */
public class FileInfoFrame extends UIObject  {

    String fileName="																		";
    String fileSize="";
    String location="										";
    String type="										";

    public Component getDetails() {
         
		return details;
    }

    private JInternalFrame details=null;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public void setFileSize(String fileSize) {
         this.fileSize = fileSize;
    }

    public void setLocation(String location) {
        this.location = location;
    }


     public void setDesc(java.lang.String desc)
     {
         this.whatAmI=desc;
     }

     public FileInfoFrame()
     {
        createDetailsFrame();
     }
     
     private String read,write,lm;
     private void createDetailsFrame()
     {
        Object[][] TABLE_DATA = {
        		
               {"File Name:", fileName,"	"},
               {"File Size:", fileSize,"    "},
               {"File Location:", location,"    "},
               {"File type:", type,"    "},
			   {"Read-Permission",read,""},
			   {"Write-Permission",write,""},
			   {"Last-Modified",lm,""},

        };


         String[] COLUMN_NAMES = {
             "		Title		", "		Value		"
         };
        mod =new DefaultTableModel (TABLE_DATA, COLUMN_NAMES);
        
        JTable table = new JTable (mod);
       // table.setBackground(Color.LIGHT_GRAY);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setAutoscrolls(true);
        
        int c=table.getSelectedColumn();
        int r=table.getSelectedRow();
        if(r >=0 && c>=0){
        String v=TABLE_DATA[r][c].toString();
        table.setToolTipText(v);
        }
        
								 
       //table.setMinimumSize(new Dimension(300,180));
       // table.setMinimumSize(new Dimension(100,180));
        //table.setBounds(0,0,table.getWidth()+100, table.getHeight());
        //table.setAutoscrolls(true);
        JScrollPane pane =new JScrollPane (table,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        details=new JInternalFrame("Project Details...");
        //table.setMinimumSize(new Dimension(240,110));
        Dimension d=details.getContentPane().getSize();
        //details.setSize(50,100);
        details.getContentPane().add(table);
        table.setAutoCreateColumnsFromModel(true);
        //table.setSize(300,300);
        details.setAutoscrolls(true);
        //details.setMinimumSize(new Dimension(details.getWidth()+150,details.getHeight()+30));
        pane.add(details);
        TableColumnModel columnModel = table.getColumnModel();

        for (int col=0; col<table.getColumnCount(); col++) {

        int maxwidth = 0;            
		for (int row=0; row<table.getRowCount(); row++) {
		TableCellRenderer rend =
				table.getCellRenderer(row, col); 
			Object value = table.getValueAt (row, col); 
			Component comp =
				rend.getTableCellRendererComponent (table, 
										value, 
										false, 
										false, 
										row, 
										col);
			maxwidth = Math.max (comp.getPreferredSize().width, maxwidth)+20; 
		} // for row
		TableColumn column = columnModel.getColumn (col);
		if(col!=0)
		column.setPreferredWidth (maxwidth+20);
        }
        
        
        details.setVisible(true);
     }

     public void recreateDetailsFrame(String filename)
     {
     	 File f=new File(filename);
     	 
     	 String length=""+f.length()+" Bytes";
     	 int sep=filename.lastIndexOf(File.separator);
     	 String baseName="";
     	 String loc="";
     	 String ext=baseName;
     	 if(sep!=-1){
     	 	loc=filename.substring(0,sep);
     	 	ext=filename.substring(sep+1);
     	 	baseName=ext;
     	 	int dot=ext.indexOf(".");
     	 	ext=ext.substring(dot+1);
     	 	baseName=baseName.substring(0,dot);
     	 	fileName=baseName;
     	 }
     	 this.location=loc;	
     	 this.fileSize=length;
     	 read=""+f.canRead();
     	 write=""+f.canWrite();
     	 lm=""+new Date(f.lastModified()).toString();
     	 this.type=ext;
     	 details=null;
		 updateDetaisFrame();
		 
		 
     }
     
     private void updateDetaisFrame()
     {
     	 Object[][] TABLE_DATA = {
        		
               {"File Name:", fileName,"	"},
               {"File Size:", fileSize,"    "},
               {"File Location:", location,"    "},
               {"File type:", type,"    "},
			   {"Read-Permission",read,""},
			   {"Write-Permission",write,""},
			   {"Last-Modified",lm,""}

        };


         String[] COLUMN_NAMES = {
             "		Title		", "		Value		"
         };
        mod =new DefaultTableModel (TABLE_DATA, COLUMN_NAMES);
        
        JTable table = new JTable (mod);
        //table.setBackground(Color.LIGHT_GRAY);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setAutoscrolls(true);
        
        int c=table.getSelectedColumn();
        int r=table.getSelectedRow();
        if(r >=0 && c>=0){
        String v=TABLE_DATA[r][c].toString();
        table.setToolTipText(v);
        }
        
								 
       //table.setMinimumSize(new Dimension(300,180));
       // table.setMinimumSize(new Dimension(100,180));
        //table.setBounds(0,0,table.getWidth()+100, table.getHeight());
        //table.setAutoscrolls(true);
        JScrollPane pane =new JScrollPane (table,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        details=new JInternalFrame("Project Details...");
        //table.setMinimumSize(new Dimension(240,110));
        Dimension d=details.getContentPane().getSize();
        //details.setSize(50,100);
        details.getContentPane().add(table);
        table.setAutoCreateColumnsFromModel(true);
        //table.setSize(300,300);
        details.setAutoscrolls(true);
        //details.setMinimumSize(new Dimension(details.getWidth()+150,details.getHeight()+30));
        pane.add(details);
        TableColumnModel columnModel = table.getColumnModel();

        for (int col=0; col<table.getColumnCount(); col++) {

        int maxwidth = 0;            
		for (int row=0; row<table.getRowCount(); row++) {
		TableCellRenderer rend =
				table.getCellRenderer(row, col); 
			Object value = table.getValueAt (row, col); 
			Component comp =
				rend.getTableCellRendererComponent (table, 
										value, 
										false, 
										false, 
										row, 
										col);
			maxwidth = Math.max (comp.getPreferredSize().width, maxwidth)+40; 
		} // for row
		TableColumn column = columnModel.getColumn (col);
		if(col==0)
		column.setPreferredWidth (maxwidth+40);
		else{
			column.setPreferredWidth (maxwidth+40);
			
			int i;
		}
		
        }
        details.setToolTipText(location);
        
        details.setVisible(true);
     
     }

     private DefaultTableModel mod;
     
     public DefaultTableModel getModel(){return mod;}

}
