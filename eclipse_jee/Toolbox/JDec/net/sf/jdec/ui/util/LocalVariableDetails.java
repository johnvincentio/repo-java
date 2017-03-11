/*
 * LocalVariableDetails.java Copyright (c) 2006,07 Swaroop Belur 
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
import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import net.sf.jdec.core.LocalVariable;
import net.sf.jdec.ui.core.Manager;


public class LocalVariableDetails extends JInternalFrame
{
	protected JTable table;
	protected LocalVarDataReport localvardata;
	protected JLabel label;
	private ArrayList vars;
	public LocalVariableDetails(ArrayList al)
	{
		super("Local Variable Details");
		vars=al;
		localvardata=new LocalVarDataReport(this,vars);
		table = new JTable();
		table.setAutoCreateColumnsFromModel(false);
		table.setModel(localvardata);

		for (int k = 0; k < 5; k++)
		{
			TableCellRenderer renderer;
			DefaultTableCellRenderer textRenderer =
				new DefaultTableCellRenderer();
			renderer = textRenderer;
			TableCellEditor editor;

			editor = new DefaultCellEditor(new JTextField());
			TableColumn column = new TableColumn(k,
					80,
					renderer, editor);
			table.addColumn(column);
		}


		JScrollPane ps = new JScrollPane();
		ps.setSize(getRightTabbedPane().getWidth(),100);
		ps.setMaximumSize(new Dimension(getRightTabbedPane().getWidth(),100));
		ps.getViewport().add(table);
		getContentPane().add(ps, BorderLayout.CENTER);
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


class LocalVarDataReport extends AbstractTableModel
{
	public static final LocalVarColumnData localvarcolumns[] = {
		new LocalVarColumnData( "Name"),
		new LocalVarColumnData( "Method"),
		new LocalVarColumnData( "Description"),
		new LocalVarColumnData( "Begin Of Range"),
		new LocalVarColumnData( "End Of Range"),


	};


	protected LocalVariableDetails myparent;
	protected Vector localvardatavector;
	public LocalVarDataReport(LocalVariableDetails parent,ArrayList vars) {
		myparent = parent;
		localvardatavector = new Vector();
		populateLocalVariableInfo(vars);
	}

	public void populateLocalVariableInfo(ArrayList vars)
	{
		localvardatavector.removeAllElements();
		int size=vars.size();
		if(size > 0)
		{
			for(int s=0;s<size;s++)
			{
				LocalVariable localVar=(LocalVariable)vars.get(s);
				java.lang.String name=(java.lang.String)localVar.getVarName();
				java.lang.String method=(java.lang.String)localVar.getMethodName();
				java.lang.String desc=(java.lang.String)localVar.getDataType();
				int start=localVar.getBlockStart();
				int end=localVar.getBlockEnd();
				localvardatavector.addElement(new LocalVarData(name,method,desc,start,end));
			}
		}

	}



	public int getRowCount() {
		return localvardatavector==null ? 0 : localvardatavector.size();
	}
	public int getColumnCount() {
		return localvarcolumns.length;
	}
	public String getColumnName(int column) {
		return localvarcolumns[column].coltitle;
	}
	public boolean isCellEditable(int nRow, int nCol) {
		return true;
	}
	public Object getValueAt(int nRow, int ncol) {
		if (nRow < 0 || nRow>=getRowCount())
			return "";
		LocalVarData row = (LocalVarData)localvardatavector.elementAt(nRow);
		if(ncol==0)
		{

			return row.getVariableName();

		}
		else if(ncol==1)
		{

			return row.getMethodDesc();

		}
		else if(ncol==2)
		{

			return row.getVariableTypeDesc();

		}
		else if(ncol==3)
		{

			return new Integer(row.getS()).toString();

		}



		else if(ncol==4)
		{

			return new Integer(row.getE()).toString();

		}

		else
			return "";
	}
   public void setValueAt(Object value, int nRow, int nCol) {




	}



}


class LocalVarData
{
	private String variableName;
	private String methodDesc=null;
	private String variableTypeDesc=null;

	private int S=1;
	private int e=1;

	LocalVarData(String title,String m,String desc,int s,int e)
	{
		this.variableName=title;
		this.methodDesc=m;
		this.variableTypeDesc=desc;
		this.S=s;
		this.e=e;

	}

	public String getVariableTypeDesc() {
		return variableTypeDesc;
	}

	public void setVariableTypeDesc(String variableTypeDesc) {
		this.variableTypeDesc = variableTypeDesc;
	}

	public int getE() {
		return e;
	}

	public void setE(int e) {
		this.e = e;
	}

	public String getMethodDesc() {
		return methodDesc;
	}

	public void setMethodDesc(String methodDesc) {
		this.methodDesc = methodDesc;
	}

	public int getS() {
		return S;
	}

	public void setS(int s) {
		S = s;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}



}

class LocalVarColumnData
{
	public String coltitle;
	int colwidth;

	public LocalVarColumnData(String title)
	{
		coltitle = title;


	}
}






