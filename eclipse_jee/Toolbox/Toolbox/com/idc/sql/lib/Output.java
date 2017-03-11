package com.idc.sql.lib;

public interface Output {
	public boolean open (); 
    public void close();
	public void write (String msg);
	public void writeNL (String msg);
	public void writeNL ();
    public void write (int num);
}
