package com.idc.top;

import com.idc.middle.Middle;

public class Top {
	public static void main(String[]  args) {
        System.out.println("Main - stage 1");
        Middle middle = new Middle(21);
        System.out.println("Main - count is "+middle.getCount());
        System.out.println("Main - index is "+middle.getIndex());
        System.out.println("Main - stage 99");
    }
}
