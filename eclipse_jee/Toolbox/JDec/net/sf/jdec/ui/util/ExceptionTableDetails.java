
/*
 *  ExceptionTableDetails.java Copyright (c) 2006,07 Swaroop Belur 
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
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.sf.jdec.constantpool.MethodInfo.ExceptionTable;
import net.sf.jdec.ui.core.Manager;
import net.sf.jdec.ui.main.UILauncher;



public class ExceptionTableDetails extends JInternalFrame
{
	protected JTable table;
	protected ExceptionTableDataReport excptabdata;
	protected JLabel label;
	private ArrayList vars;
	public ExceptionTableDetails(ArrayList al)
	{
		super("Exception Table Details");
		vars=al;
		this.setToolTipText("Select Help For Interpreting Table Details");
        excptabdata=new ExceptionTableDataReport(this,vars);
		table = new JTable();
		table.setAutoCreateColumnsFromModel(false);
		table.setModel(excptabdata);

		for (int k = 0; k < 5; k++)
		{
			TableCellRenderer renderer;
			DefaultTableCellRenderer textRenderer =new DefaultTableCellRenderer();
			renderer = textRenderer;
			TableCellEditor editor;

			editor = new DefaultCellEditor(new JTextField());
			TableColumn column = new TableColumn(k,80,
					renderer, editor);
			table.addColumn(column);
		}


		JScrollPane ps = new JScrollPane();
		//ps.setSize(550, 150);
		ps.setSize(getRightTabbedPane().getWidth(),100);
		ps.setMaximumSize(new Dimension(getRightTabbedPane().getWidth(),100));
		ps.getViewport().add(table);
		getContentPane().add(ps, BorderLayout.SOUTH);
		if(excptabdata.getExcepDataList().size() == 0)JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Exception Table Empty For Chosen Method...","Exception Table Message",JOptionPane.INFORMATION_MESSAGE);
		setVisible(true);
	}
	private JTabbedPane getRightTabbedPane()
	{
		Manager manager=Manager.getManager();
		ArrayList paneList=manager.getCurrentSplitPaneComponents();
	       JTabbedPane tabs=null;
	       for(int s=0;s<paneList.size();s++)
	       {
	           Object current=paneList.get(s);
	           if(current instanceof JTabbedPane)
	           {
	               tabs=(JTabbedPane)current;
	               if(tabs.getTabCount() > 2)
	               {
	                   break;
	               }

	           }
	       }

		return tabs;
	}




}


class ExceptionTableDataReport extends AbstractTableModel
{
	public static final ExceptionTableColumnClz exceptabcolumns[] = {
			new ExceptionTableColumnClz( "Exception Name"),
		new ExceptionTableColumnClz( "Start PC"),
		new ExceptionTableColumnClz( "End PC"),
		new ExceptionTableColumnClz( "Handler Pc"),
		new ExceptionTableColumnClz( "Catch Type"),
		
};

	


	protected ExceptionTableDetails myparent;
	protected ArrayList  excpdatalist;
	public ExceptionTableDataReport(ExceptionTableDetails parent,ArrayList list) {
		myparent = parent;
		excpdatalist = new ArrayList();
		populateExceptionDetails(list);
	}

	public void populateExceptionDetails(ArrayList vars)
	{
			   for(int z=0;z<excpdatalist.size();z++)
        {
            excpdatalist.remove(z);
        }
		int size=vars.size();
		if(size > 0)
		{
			for(int s=0;s<size;s++)
			{
				ExceptionTable excp=(ExceptionTable)vars.get(s);
				int spc=excp.getStartPC();
				int epc=excp.getEndPC();
				int hpc=excp.getStartOfHandler();
				int ctype=excp.getCatchType();
				String name=excp.getExceptionName();
				excpdatalist.add(new ExceptionTableEachRow(spc,epc,hpc,ctype,name));
			}
		}

	}



	public int getRowCount() {
		return excpdatalist==null ? 0 : excpdatalist.size();
	}
	public int getColumnCount() {
		return exceptabcolumns.length;
	}
	public String getColumnName(int column) {
		return exceptabcolumns[column].coltitle;
	}
	public boolean isCellEditable(int nRow, int nCol) {
		return true;
	}
	public Object getValueAt(int nRow, int ncol) {
		if (nRow < 0 || nRow>=getRowCount())
			return "";
		ExceptionTableEachRow row = (ExceptionTableEachRow)excpdatalist.get(nRow);
		if(ncol==0)
		{

			String s=row.getName();
			if(s.equals("<any>"))s="Any Exception Handler";
			else s=s.replace('/','.');
			return s;
		


		}
		else if(ncol==1)
		{
			return new Integer(row.getEpc()).toString();

		}
		else if(ncol==2)
		{

				return new Integer(row.getHpc()).toString();
		}
		else if(ncol==3)
		{

				return new Integer( row.getCtype()).toString();
		}



		else if(ncol==4)
		{
			return new Integer(row.getSpc()).toString();

		}

		else
			return "";
	}
   public void setValueAt(Object value, int nRow, int nCol) {



	}

public ArrayList getExcepDataList() {
	return excpdatalist;
}



}


class ExceptionTableEachRow
{
	private int spc;
	private int epc;
	private int hpc;

	private int ctype=1;
	private String name;;

	ExceptionTableEachRow(int spc,int epc,int hpc,int ctype,String name){
		this.spc=spc;
		this.epc=epc;
		this.hpc=hpc;
		this.ctype=ctype;
		this.name=name;

	}

	public int getCtype() {
		return ctype;
	}

	public int getEpc() {
		return epc;
	}

	public int getHpc() {
		return hpc;
	}

	public String getName() {
		return name;
	}

	public int getSpc() {
		return spc;
	}





}

class ExceptionTableColumnClz
{
	public String coltitle;


	public ExceptionTableColumnClz(String title)
	{
		coltitle = title;


	}
}






