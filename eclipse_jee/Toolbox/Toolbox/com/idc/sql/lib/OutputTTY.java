package com.idc.sql.lib;

import com.idc.swing.JVMessagesArea;

public class OutputTTY implements Output {
	private JVMessagesArea jvMessagesArea;
	
	public OutputTTY (JVMessagesArea jvMessagesArea) {this.jvMessagesArea = jvMessagesArea;}
	public boolean open () {return true;}
    public void close() {}

	public void write (String msg) {jvMessagesArea.addNoNL (msg);}
	public void writeNL (String msg) {write (msg+"\n");}
	public void writeNL () {write ("\n");}
    public void write (int num) {write(Integer.toString(num));}	
}
