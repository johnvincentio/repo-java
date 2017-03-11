package com.idc.five.output;

public class OutputTTY implements Output {
	
	public OutputTTY() {}
	public boolean open () {return true;}
    public void close() {}

	public void print (String msg) {System.out.print (msg);}
	public void println (String msg) {print (msg+"\n");}
	public void println () {print ("\n");}
    public void print (int num) {print(Integer.toString(num));}	
}
