package com.idc.sql.lib;

import java.io.*;

import com.idc.trace.LogHelper;

public class OutputFile implements Output {
	private File file;
	private BufferedWriter buffer;
	
	public OutputFile (File file) {this.file = file;}
	public boolean open() {
		try {
			buffer = new BufferedWriter (new FileWriter (file));
		}
		catch (IOException e) {
			LogHelper.error("Unable to open file "+file.getPath()+". Aborting");
			return false;
		}
		return true;
    }
    public void close() {
    	try {
			buffer.close();
    	} catch (IOException e) {
    		LogHelper.error("Could not close file "+file.getPath()+":");
    	}
    }
	public void write (String msg) {
    	try {
        	buffer.write(msg);
        	buffer.flush();
    	} catch (IOException e) {
    		LogHelper.error("Could not write to file "+file.getPath()+": Aborting...");
    		System.exit(1);
    	}
    }
	public void writeNL (String msg) {write (msg+"\n");}
	public void writeNL () {write ("\n");}
    public void write (int num) {write(Integer.toString(num));}	
}
