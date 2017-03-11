
package com.idc.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class Menu {
	private ArrayList<MenuItem> m_list = new ArrayList<MenuItem>();
	private Iterator<MenuItem> m_iter = null;
	public void add (MenuItem item) {
		if (! isExists (item)) m_list.add(item);}
	public MenuItem getNext() {return (MenuItem) m_iter.next();}
	public boolean hasNext() {
		if (m_iter == null) m_iter = m_list.iterator();
		if (m_iter.hasNext()) return true;
		m_iter = null;
		return false;
	}
	public int size() {return m_list.size();}
	public boolean isEmpty() {return size() < 1;}
	public boolean isExists (MenuItem menuItem) {
		Iterator<MenuItem> iter = m_list.iterator();
		MenuItem item;
		while (iter.hasNext()) {
			item = (MenuItem) iter.next();
			if (item.getId() == menuItem.getId()) return true;
		}
		return false;
	}
	public void show (int tab) {
		Iterator<MenuItem> iter = m_list.iterator();
		MenuItem item;
		while (iter.hasNext()) {
			item = (MenuItem) iter.next();
			item.show(tab);
		}
	}
	public void sort() {
		Collections.sort(m_list, new SortItemsAsc());
		Iterator<MenuItem> iter = m_list.iterator();
		MenuItem item;
		while (iter.hasNext()) {
			item = (MenuItem) iter.next();
			item.sort();
		}		
	}
	private class SortItemsAsc implements Comparator<MenuItem> {
		public int compare (MenuItem a, MenuItem b) {
			return a.getName().compareTo (b.getName());
		}
	}
}

/*

Items items = getAllItems();
MenuItem menuItem = new MenuItem (1,"top");
Menu = menuItem.getMenu();
int id;
String name;
Item Item;
items.reset();
while (items.hasNext()) {
item = items.getItem();
id = item.getId();
name = allCategories.getName (id);
menu.add (new MenuItem (id,name));
items.getNext();
}
menuItem.sort();
menuItem.show(1);
return menuItem;		// this is the menu

JSP

Menu topMenu = topMenuItem.getMenu();
Menu menu;
MenuItem item, subItem;
while (topMenu.hasNext()) {
	item = topMenu.getNext();
	if (item.isLeaf()) {
			......
	}
	else {
	<%= item.getName(); %>
	
	menu = item.getMenu();
	while (menu.hasNext()) {
		subItem = menu.getNext();
		.....subItem.getName();
	}
}

*/
