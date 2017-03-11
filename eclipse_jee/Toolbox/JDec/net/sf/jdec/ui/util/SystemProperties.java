
/*
 * SystemProperties.java Copyright (c) 2006,07 Swaroop Belur 
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
public class SystemProperties extends JInternalFrame
{
	protected JTable systable;
	protected SystemPropertyDetails spd;
	protected JLabel title;
	private UIUtil util;

    public SystemProperties(UIUtil util)
	{
		super("List Of System Properties");
		this.util=util;
		setSize(570, 200);
		spd = new SystemPropertyDetails(this,util);
		systable = new JTable();
		systable.setAutoCreateColumnsFromModel(false);
		systable.setModel(spd);

		for (int k = 0; k < 2; k++)
		{
			TableCellRenderer renderer;
			DefaultTableCellRenderer textRenderer =new DefaultTableCellRenderer();
			renderer = textRenderer;
			TableCellEditor editor;
			editor = new DefaultCellEditor(new JTextField());
			TableColumn column = new TableColumn(k,80,renderer, editor);
			systable.addColumn(column);
		}


		JScrollPane ps = new JScrollPane();
		ps.setSize(550, 150);
		ps.getViewport().add(systable);
		getContentPane().add(ps, BorderLayout.CENTER);
		setVisible(true);
	}



}


class SystemPropertyDetails extends AbstractTableModel
{
	public static final SystemPropertyColumnClz MCOLUMNS[] =
    {
		new SystemPropertyColumnClz( "System Property Name"),
		new SystemPropertyColumnClz( "Property Value")

	};


	protected SystemProperties myparent;
	protected ArrayList info;

    public SystemPropertyDetails(SystemProperties parent,UIUtil util)
    {
		myparent = parent;
		info = new ArrayList();
		populateSystemPropsInfo(util);
	}

	public void populateSystemPropsInfo(UIUtil util)
	{
		 for(int z=0;z<info.size();z++)
        {
            info.remove(z);
        }
    	ArrayList propNames=util.propNames;
		ArrayList propVals=util.propValues;
		int size=propNames.size();
		if(size > 0)
		{
			for(int s=0;s<size;s++)
			{
				java.lang.String name=(java.lang.String)propNames.get(s);
				java.lang.String val=(java.lang.String)propVals.get(s);
				info.add(new SystemPropEachRow(name,val));
			}
		}

	}
    public int getRowCount() {
		return info==null ? 0 : info.size();
	}
	public int getColumnCount() {
		return MCOLUMNS.length;
	}
	public String getColumnName(int column) {
		return MCOLUMNS[column].SystemPropColTitle;
	}
	public boolean isCellEditable(int nRow, int nCol) {
		return true;
	}
	public Object getValueAt(int nRow, int ncol) {
		if (nRow < 0 || nRow>=getRowCount())
			return "";
		SystemPropEachRow row = (SystemPropEachRow)info.get(nRow);
		if(ncol==1)
		{

			return row.getPropval();

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
		SystemPropEachRow row = (SystemPropEachRow)info.get(nRow);
		String svalue = value.toString();
		row.setPropval(svalue);

	}



}


class SystemPropEachRow
{
	private String proptitle;
	private String propval=null;
	private int pos=1;


	SystemPropEachRow(String title,String value)
	{
		this.proptitle=title;
		this.propval=value;

	}


	public int getPos()
	{
		return pos;
	}

	public void setPos(int i)
	{
		pos=i;
	}
	public void setPropval(java.lang.String s)
	{
		propval=s;
	}
	public String getProptitle()
	{
		return proptitle;
	}
	public String getPropval()
	{
		return propval;
	}

}

class SystemPropertyColumnClz
{
	public String SystemPropColTitle;

	public SystemPropertyColumnClz(String title)
	{
		SystemPropColTitle = title;

	}
}





