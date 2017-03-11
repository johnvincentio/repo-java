
package com.idc.jsp.template;

import java.util.*;

public class JVTabs {
	private ArrayList<JVTabsItems> m_list = new ArrayList<JVTabsItems>();
	private int m_nSelectedIndex;
	private String m_strSelectedColor;
	private String m_strNotSelectedColor;

	public JVTabs(String s1, String s2) {
		m_strSelectedColor = s1;
		m_strNotSelectedColor = s2;
		m_nSelectedIndex = 0;
	}

	public void addItem(JVTabsItems i) {m_list.add(i);}
	public Iterator<JVTabsItems> getItems() {return m_list.iterator();}
	public int getSelectedIndex() {return m_nSelectedIndex;}
	public int getSize() {return m_list.size();}

	public String getSelectedColor() {return m_strSelectedColor;}
	public String getNotSelectedColor() {return m_strNotSelectedColor;}
	public String getSelectedBody() {
		JVTabsItems tabItem = (JVTabsItems) m_list.get(m_nSelectedIndex);
		return tabItem.getBody();
	}

	public void setSelectedIndex (int index) {m_nSelectedIndex = index;}
}
