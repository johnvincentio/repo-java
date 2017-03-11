package com.idc.five.output;

public interface Output {
	public boolean open (); 
    public void close();
	public void print (String msg);
	public void println (String msg);
	public void println();
    public void print (int num);
}
