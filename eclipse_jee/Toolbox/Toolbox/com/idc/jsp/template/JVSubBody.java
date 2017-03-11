
package com.idc.jsp.template;

import java.util.*;

public class JVSubBody {
	private ArrayList<JVColumn> m_columns = new ArrayList<JVColumn>();

	public JVSubBody () {}

	public void addColumn (JVColumn column) {m_columns.add (column);}
	public Iterator<JVColumn> getColumns() {return m_columns.iterator();}
	public int getSize() {return m_columns.size();}
}
