package com.idc.db2xml;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Output {
	private String filename;
	private BufferedWriter buffer;
	
	public Output() {}
	public boolean open (String filename) {
		this.filename = filename;
		try {
			buffer = new BufferedWriter(new FileWriter(filename));
		}
		catch (IOException e) {
			System.out.println("Unable to open file "+filename+". Aborting");
			return false;
		}
		return true;
    }
    public void close() {
    	try {
			buffer.close();
    	} catch (IOException e) {
    		System.out.println("Could not close file "+filename+":");
    	}
    }
	public void write (String msg) {
    	try {
        	buffer.write(msg);
    	} catch (IOException e) {
    		System.out.println("Could not write to file "+filename+": Aborting...");
    		System.exit(1);
    	}
    }
}


