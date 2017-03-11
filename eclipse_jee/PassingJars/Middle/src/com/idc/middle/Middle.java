package com.idc.middle;

import com.idc.base.Base;
import com.idc.base.Item;

public class Middle {
	private int count;
	private Base base;
	private Item item;

	public Middle(int count) {
		this.count = count;
		base = new Base(count + 500);
		item = new Item("Abcd");
	}

	public int getCount() {return count;}
	public int getIndex() {return base.getIndex();}

	public static void main(String[]  args)  {
		(new Middle(15)).doTest1();
    }
	public void doTest1() {
        System.out.println("Main - stage 1");
        Base base = new Base(99);
        item = new Item("fred");
        System.out.println("Main - index is "+base.getIndex());
        System.out.println("Main - name  is  "+item.getName());
        System.out.println("Main - stage 99");
    }
}
