/*
 * RecentFileList.java Copyright (c) 2006,07 Swaroop Belur 
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
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.table.*;

import net.sf.jdec.ui.core.JdecTree;
import net.sf.jdec.ui.core.Manager;
import net.sf.jdec.ui.main.UILauncher;
import net.sf.jdec.ui.util.UIUtil;
public class RecentFileList extends JFrame
{
	String fileSelected=null;
	protected JTable m_table;
	protected Report m_data;
	protected JLabel m_title;
	private UIUtil util;
	public RecentFileList(UIUtil util)
	{
		super("Recent File List...");
		this.util=util;
		Dimension d=getToolkit().getScreenSize();
		m_data = new Report(this,util);
		m_table = new JTable();
		m_table.setAutoCreateColumnsFromModel(false);
		m_table.setModel(m_data);
		
		for (int k = 0; k < 4; k++)
		{
			TableCellRenderer renderer;
			DefaultTableCellRenderer textRenderer =
				new DefaultTableCellRenderer();
			renderer = textRenderer;
			TableCellEditor editor;
			
			editor = new DefaultCellEditor(new JTextField());
			int w;
			if(k==0)w=50;
			else w=120;
			TableColumn column = new TableColumn(k,
					w,
					renderer, editor);
			m_table.addColumn(column);
		}
		

		JScrollPane ps = new JScrollPane();

		ps.getViewport().add(m_table);
		getContentPane().add(ps, BorderLayout.CENTER);
		JLabel label=new JLabel();
		label.setText("  																	");
		JComboBox combo=new JComboBox();
		combo.setName("combo");
		combo.setActionCommand("combo");
		combo.setLightWeightPopupEnabled(true);
		Listener l=new Listener();
		//combo.addComponentListener(l);
		combo.addActionListener(l);
		ArrayList v=m_data.getRecentFileListData();
		if(v!=null)
		{
			for(int s=0;s<v.size();s++)
			{
				RecentFileEachRow rd=(RecentFileEachRow)v.get(s);
				String name=rd.getFileName();
				combo.addItem(name);
			}
		}
		JInternalFrame intFr=new JInternalFrame();
		intFr.setTitle("Select File To Open...");
		intFr.getContentPane().add(label,BorderLayout.WEST);
		intFr.getContentPane().add(combo,BorderLayout.CENTER);
		JButton open=new JButton("Open File",null);
		open.addActionListener(l);
		open.setIconTextGap(0);
		//open.setMaximumSize(new Dimension(30,30));
		intFr.getContentPane().add(open,BorderLayout.EAST);
		intFr.setVisible(true);
		getContentPane().add(intFr, BorderLayout.SOUTH);

		pack();
		setSize(600,200);
		setVisible(true);
	}
	
	
	
}


class Report extends AbstractTableModel
{
	public static final RecFileColClz m_columns[] = {

			new RecFileColClz( "File Name"),
			new RecFileColClz( "File Type"),
			new RecFileColClz( "Absolute Path"),
			new RecFileColClz( "Last Modified"),
			
	};
	

	
	
	protected RecentFileList m_parent;
	protected ArrayList recentFileListData;
	public Report(RecentFileList parent,UIUtil util) {
		m_parent = parent;
		recentFileListData = new ArrayList();
		populateRecentFileList(util);
	}
	
	public void populateRecentFileList(UIUtil util)
	{
		 for(int z=0;z<recentFileListData.size();z++)
        {
            recentFileListData.remove(z);
        }
		int counter=1;
		ArrayList fileList=UILauncher.getUIutil().getRecentFileList();
		for(int s=0;s<fileList.size();s++)
		{
			String index=""+counter;
			File curFile=(File)fileList.get(s);
			String name=curFile.getName();
			String path=curFile.getAbsolutePath();
			long l=curFile.lastModified();
			int dot=path.indexOf(".");
			String ext="";
			if(dot!=-1)
				ext=path.substring(dot+1);
			recentFileListData.add(new RecentFileEachRow(index,name,path,l,ext));
			counter++;
		}
		
	}
	
	
	
	public int getRowCount() {
		return recentFileListData==null ? 0 : recentFileListData.size();
	}
	public int getColumnCount() {
		return m_columns.length;
	}
	public String getColumnName(int column) {
		return m_columns[column].title;
	}
	public boolean isCellEditable(int nRow, int nCol) {
		return true;
	}
	public Object getValueAt(int nRow, int ncol) {
		if (nRow < 0 || nRow>=getRowCount())
			return "";
		RecentFileEachRow row = (RecentFileEachRow)recentFileListData.get(nRow);
		if(ncol==1)
		{
			
			return row.getExt();
			
		}
		if(ncol==2)
		{
			
			return row.getAbsPath();
			
		}
		
		if(ncol==3)
		{
			
			return new Date(row.getModified()).toString();
			
		}
		
		else if(ncol==0)
		{
			
			String s=row.getFileName();
			int i=s.indexOf(".");
			if(i!=-1)s=s.substring(0,i);
			return s;
			
		}
		/*if(ncol==0)
		 {
		 
		 return row.getIndex();
		 
		 }*/
		
		else
			return "";
	}
	public void setValueAt(Object value, int nRow, int nCol) {
		
		
		if (nRow < 0 || nRow>=getRowCount())
			return;
		RecentFileEachRow row = (RecentFileEachRow)recentFileListData.get(nRow);
		String svalue = value.toString();
		row.setAbsPath(svalue);
		
	}
	
	public ArrayList getRecentFileListData() {
		return recentFileListData;
	}
	
	
	
}


class RecentFileEachRow
{
	private String fileName;
	private String index;
	private String absPath=null;
	private int pos=1;
	
	public long getModified() {
		return modified;
	}
	
	public void setModified(long modified) {
		this.modified = modified;
	}
	
	public String getExt() {
		return ext;
	}
	
	public void setExt(String ext) {
		this.ext = ext;
	}
	
	private long modified;
	private String ext="";
	
	RecentFileEachRow(String pos,String title,String value,long l,String ext)
	{
		this.index=pos;
		this.fileName=title;
		this.absPath=value;
		modified=l;
		this.ext=ext;
	}
	
	
	public int getPos()
	{
		return pos;
	}
	
	public void setPos(int i)
	{
		pos=i;
	}
	public void setAbsPath(java.lang.String s)
	{
		absPath=s;
	}
	public String getFileName()
	{
		return fileName;
	}
	public String getAbsPath()
	{
		return absPath;
	}
	
	
	public String getIndex() {
		return index;
	}
	
	
	public void setIndex(String index) {
		this.index = index;
	}
	
}

class RecFileColClz
{
	public String title;

	public RecFileColClz(String title)
	{
		this.title = title;
		
	}
}


class Listener implements ActionListener
{
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("combo"))
		{
			JComboBox comBox=(JComboBox)e.getSource();
			UILauncher.getUIutil().fileSelected=comBox.getSelectedItem().toString();
		//	System.out.println(UILauncher.getUIutil().fileSelected+ "UILauncher.getUIutil().fileSelected");
			
		}
		if(e.getActionCommand().equals("Open File"))
		{
			String name=UILauncher.getUIutil().fileSelected;
			String p=null;
			if(name!=null)
			{
				ArrayList fileList=UILauncher.getUIutil().getRecentFileList();
				for(int s=0;s<fileList.size();s++)
				{
					
					File curFile=(File)fileList.get(s);
					String n=curFile.getName();
					if(n.equals(name))
					{
						p=curFile.getAbsolutePath();
						break;
					}
				}
				if(p!=null)
				{
					JEditorPane rdwrPane=null;
					Manager manager=Manager.getManager();
					ArrayList paneList=manager.getCurrentSplitPaneComponents();
					JTabbedPane tabs=getRightTabbedPane();
					
					if(tabs!=null){	
						tabs.setSelectedIndex(tabs.indexOfTab("Jdec Editor Window"));
						Component editor=tabs.getSelectedComponent();
						JScrollPane editorTab=(JScrollPane)editor;
						Object o=editorTab.getComponent(0);
						JViewport view=(JViewport)o;
						Object o2=view.getView();
						if(o2!=null)
						{
							rdwrPane=(JEditorPane)o2;
							try{
								if(p.endsWith(".class")==false)
								{
									FileReader reader = new FileReader(new File(p));
									rdwrPane.read(reader, null);
									RecentFileList recentRef=Manager.getManager().getRecentFileListRef();
									recentRef.setVisible(false);
									recentRef.dispose();
								}
								else
								{
									// Show In hex
									StringBuffer sb=new StringBuffer("");
									DataInputStream dis=new DataInputStream(new FileInputStream(p));
									int counter=0;
									while(true)
									{
										try
										{
											int b=dis.read();
											counter++;
											if(b==-1)break;
											String t=Integer.toHexString(b);
											if(t.length()==1)
											{
												t="0"+t;
											}
											if(counter > 10)
											{
												sb.append(System.getProperty("line.separator"));
												counter=1;
											}
											sb.append(t).append("    ");
										
										}
										catch(EOFException eof)
										{
											break;
										}
										catch(IOException io)
										{
											// Some problem
											break;
										}
									}
									rdwrPane.setText(sb.toString());
									
								}
								
							}
							catch(Exception exp)
							{
								JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"[ERROR] :Could not open file");
							}
						}
					}
					
					
					
				}
				
				
				
			}
			else
			{
				JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"[ERROR] :Could not open file","Show Recent Fies",JOptionPane.INFORMATION_MESSAGE);
			}
		}
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
                Component c=tabs.getComponent(0);
				if((c instanceof JdecTree)==false)
				{
	                if(tabs.getTabCount() > 2)
	                {
	                    break;
	                }
				}

            }
        }

        return tabs;

    }
}





