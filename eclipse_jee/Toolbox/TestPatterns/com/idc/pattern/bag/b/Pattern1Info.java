package com.idc.pattern.bag.b;

import java.io.Serializable;

import com.idc.pattern.bag.b.arrayList.HercInfo;
import com.idc.pattern.bag.b.item.DddItemInfo;

public class Pattern1Info extends HercInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	public Pattern1Info () {super();}

	public Pattern1Info (int capacity) {super (capacity);}

	public void add (DddItemInfo item) {super.add (item);}
}
