
package com.idc.file.dir;

import java.io.Serializable;

public class LineItemInfo implements Serializable {
	private static final long serialVersionUID = 1;
    private String line;
    public LineItemInfo(String line) {this.line = line;}
    public String getLine() {return line;}
    public String getExtension() {
    	int n1 = line.lastIndexOf('.');
    	if (n1 < 0) return "";
    	return line.substring(n1+1);
    }
    public String toString() {return "("+getLine()+")";}
}
