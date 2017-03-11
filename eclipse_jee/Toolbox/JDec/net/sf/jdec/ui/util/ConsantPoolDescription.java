
/*
 *  ConstantPoolDescription.java Copyright (c) 2006,07 Swaroop Belur 
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
public class ConsantPoolDescription extends JInternalFrame
	{
		protected JTable table;
		protected ConstantPoolDataReport cpdata;
		protected JLabel label;
		private UIUtil util;
		
		
		public ConsantPoolDescription(ArrayList a)
		{
			super("Constant Pool Details");
			this.util=util;
			setSize(570, 200);
			cpdata = new ConstantPoolDataReport(this,a);
			table = new JTable();
			table.setAutoCreateColumnsFromModel(false);
			table.setModel(cpdata);

			for (int k = 0; k < 5; k++)
			{
				TableCellRenderer renderer;
				DefaultTableCellRenderer textRenderer =new DefaultTableCellRenderer();
				renderer = textRenderer;
				TableCellEditor editor;

				editor = new DefaultCellEditor(new JTextField());
				TableColumn column = new TableColumn(k,
						80,
						renderer, editor);
				table.addColumn(column);
			}

		
			JScrollPane ps = new JScrollPane();
			ps.setSize(550, 150);
			ps.getViewport().add(table);
			getContentPane().add(ps, BorderLayout.CENTER);

			setVisible(true);
		}



	}


	class ConstantPoolDataReport extends AbstractTableModel
	{
		public static final ConstantPoolColumnclz cpcolumns[] = {
			new ConstantPoolColumnclz( "Entry "),
			new ConstantPoolColumnclz( "TagType"),
			new ConstantPoolColumnclz( "PoolIndex "),
			new ConstantPoolColumnclz( "PoolIndex "),
			new ConstantPoolColumnclz( "UTF8value ")

		};

		protected ConsantPoolDescription myparent;
		protected ArrayList cpdatalist;
		public ConstantPoolDataReport(ConsantPoolDescription parent,ArrayList a) {
			myparent = parent;
			cpdatalist = new ArrayList();
			populateCPOOLInfo(a);
		}

		public void populateCPOOLInfo(ArrayList a)
		{
			   for(int z=0;z<cpdatalist.size();z++)
        {
            cpdatalist.remove(z);
        }

			ArrayList details=a;
			
			int size=details.size();
			if(size > 0)
			{
				for(int s=0;s<size;s++)
				{
					String []temp=(String[])details.get(s);
					cpdatalist.add(new ConstantPoolEachRow(temp[0],temp[1],temp[2],temp[3],temp[4]));
				}
			}

		}



		public int getRowCount() {
			return cpdatalist==null ? 0 : cpdatalist.size();
		}
		public int getColumnCount() {
			return cpcolumns.length;
		}
		public String getColumnName(int column) {
			return cpcolumns[column].cptitle;
		}
		public boolean isCellEditable(int nRow, int nCol) {
			return true;
		}
		public Object getValueAt(int nRow, int ncol) {
			if (nRow < 0 || nRow>=getRowCount())
				return "";
			ConstantPoolEachRow row = (ConstantPoolEachRow)cpdatalist.get(nRow);
			if(ncol==0)
			{

				return row.getEntry();

			}
			else if(ncol==1)
			{

				return row.getTag();

			}

			else if(ncol==2)
			{

				return row.getPoolpos1();

			}

			else if(ncol==3)
			{

				return row.getPoolpos2();

			}

			else if(ncol==4)
			{

				return row.getUtf8();

			}

			else
				return "";
		}
		public void setValueAt(Object value, int nRow, int nCol) {


			
		}



	}


	class ConstantPoolEachRow
	{
		private String entry;
		private String tag=null;
		private String poolpos1;
		private String poolpos2;
		private String utf8;
		
		ConstantPoolEachRow(String s1,String s2,String s3,String s4,String s5)
		{
			entry=s1;
			tag=s2;
			poolpos1=s3;
			poolpos2=s4;
			utf8=s5;

		}


	

		/**
		 * @return Returns the entry.
		 */
		public String getEntry() {
			return entry;
		}
		/**
		 * @param entry The entry to set.
		 */
		public void setEntry(String entry) {
			this.entry = entry;
		}
		/**
		 * @return Returns the poolpos1.
		 */
		public String getPoolpos1() {
			return poolpos1;
		}
		/**
		 * @param poolpos1 The poolpos1 to set.
		 */
		public void setPoolpos1(String poolpos1) {
			this.poolpos1 = poolpos1;
		}
		/**
		 * @return Returns the poolpos2.
		 */
		public String getPoolpos2() {
			return poolpos2;
		}
		/**
		 * @param poolpos2 The poolpos2 to set.
		 */
		public void setPoolpos2(String poolpos2) {
			this.poolpos2 = poolpos2;
		}
		/**
		 * @return Returns the tag.
		 */
		public String getTag() {
			return tag;
		}
		/**
		 * @param tag The tag to set.
		 */
		public void setTag(String tag) {
			this.tag = tag;
		}
		/**
		 * @return Returns the utf8.
		 */
		public String getUtf8() {
			return utf8;
		}
		/**
		 * @param utf8 The utf8 to set.
		 */
		public void setUtf8(String utf8) {
			this.utf8 = utf8;
		}
	}

	class ConstantPoolColumnclz
	{
		public String cptitle;
		public ConstantPoolColumnclz(String title)
		{
			cptitle = title;
		}		
	}






