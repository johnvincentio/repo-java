/*
 *  ClassDetails.java Copyright (c) 2006,07 Swaroop Belur 
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

import net.sf.jdec.ui.util.UIUtil;
public class ClassDetails extends JInternalFrame
{
	protected JTable table;
	protected ClassInfo classdetaildata;
	protected JLabel label;
	private UIUtil util;
	
	
	public ClassDetails(ArrayList a)
	{
		super("General Class DetaiClassDescls");
		this.util=util;
		setSize(570, 200);
		classdetaildata = new ClassInfo(this,a);
		table = new JTable();
		table.setAutoCreateColumnsFromModel(false);
		table.setModel(classdetaildata);

		for (int k = 0; k < 2; k++)
		{
			TableCellRenderer renderer;
			DefaultTableCellRenderer textRenderer =	new DefaultTableCellRenderer();
			renderer = textRenderer;
			TableCellEditor editor;

			editor = new DefaultCellEditor(new JTextField());
			TableColumn column = new TableColumn(k,	80,	renderer, editor);
			table.addColumn(column);
		}

		
		JScrollPane ps = new JScrollPane();
		ps.setSize(550, 150);
		ps.getViewport().add(table);
		getContentPane().add(ps, BorderLayout.CENTER);

		setVisible(true);
	}



}


class ClassInfo extends AbstractTableModel
{
	public static final ClassDetailColumnData columns[] = {
		new ClassDetailColumnData( "Class Property ", 80, JLabel.LEFT ),
		new ClassDetailColumnData( "Property Value", 80, JLabel.RIGHT )

	};

	protected ClassDetails myparent;
	protected ArrayList classdetaildata;
	public ClassInfo(ClassDetails parent,ArrayList a) {
		myparent = parent;
		classdetaildata = new ArrayList();
		populateClassDetails(a);
	}

	public void populateClassDetails(ArrayList a)
	{

        for(int z=0;z<classdetaildata.size();z++)
        {
            classdetaildata.remove(z);
        }

		ArrayList details=a;
		
		int size=details.size();
		if(size > 0)
		{
			for(int s=0;s<size;s++)
			{
				String []temp=(String[])details.get(s);
				classdetaildata.add(new ClassDetailData(temp[0],temp[1]));
			}
		}

	}



	public int getRowCount() {
		return classdetaildata==null ? 0 : classdetaildata.size();
	}
	public int getColumnCount() {
		return columns.length;
	}
	public String getColumnName(int column) {
		return columns[column].coltitle;
	}
	public boolean isCellEditable(int nRow, int nCol) {
		return true;
	}
	public Object getValueAt(int nRow, int ncol) {
		if (nRow < 0 || nRow>=getRowCount())
			return "";
		ClassDetailData row = (ClassDetailData)classdetaildata.get(nRow);
		if(ncol==1)
		{

			return row.getPropvalue();

		}
		else if(ncol==0)
		{

			return row.getProptitle();

		}

		else
			return "";
	}
	public void setValueAt(Object value, int nRow, int nCol) {


		if (nRow < 0 || nRow>=getRowCount())
			return;
		ClassDetailData row = (ClassDetailData)classdetaildata.get(nRow);
		String svalue = value.toString();
		row.setPropvalue(svalue);

	}



}


class ClassDetailData
{
	private String proptitle;
	private String propvalue=null;
	private int pos=1;


	ClassDetailData(String title,String value)
	{
		this.proptitle=title;
		this.propvalue=value;

	}


	public int getPos()
	{
		return pos;
	}

	public void setPos(int i)
	{
		pos=i;
	}
	public void setPropvalue(java.lang.String s)
	{
		propvalue=s;
	}
	public String getProptitle()
	{
		return proptitle;
	}
	public String getPropvalue()
	{
		return propvalue;
	}

}

class ClassDetailColumnData
{
	public String coltitle;
	int colwidth;
	int colalignment;
	public ClassDetailColumnData(String title, int width, int alignment)
	{
		coltitle = title;
		colwidth = width;
		colalignment = alignment;
	}
}





