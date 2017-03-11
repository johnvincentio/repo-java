
package com.idc.menu;

import java.io.Serializable;

public class MenuItem implements Serializable {
	private static final long serialVersionUID = 1;
	private int id;
	private String name;
	private Menu menu;
	public MenuItem() {}
	public MenuItem(int id, String name) {
		this.id = id;
		this.name = name;
		this.menu = new Menu();
	}
	public int getId() {return id;}
	public String getName() {return name;}
	public Menu getMenu() {return menu;}
	public void setId (int id) {this.id = id;}
	public void setName (String name) {this.name = name;}
	public void setMenu (Menu menu) {this.menu = menu;}
	public boolean isLeaf() {
		if (menu == null || menu.size() < 1) return true;
		return false;
	}
	public void sort() {menu.sort();}
	public void show(int tab) {
		System.out.println("menuItem::show tab "+tab);
		for (int i=0; i<tab; i++) System.out.println("\t");
		System.out.println("MenuItem; ("+getId()+","+getName()+")");
		menu.show(++tab);
	}
}
