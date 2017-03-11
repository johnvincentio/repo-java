
package com.idc.diff.dir;

import java.io.File;
import java.io.Serializable;

public class BaseItemInfo implements Serializable {
	private static final long serialVersionUID = 1;
	private File file;
	private String path;
	private String relative;
	private String name;
	private boolean complete = false;
	public BaseItemInfo (String base, File file) {
		this.file = file;
		path = file.getAbsolutePath();
		name = file.getName();
		int num = path.indexOf(base);
//		System.out.println("num "+num);
		if (num < 0) {
			System.out.println("Trouble; File "+toString());
			System.exit(1);
		}
		relative = path.substring(base.length()+1);
	}
	public File getBase() {return file;}
	public String getName() {return name;}
	public String getPath() {return path;}
	public String getRelative() {return relative;}

	public boolean isComplete() {return complete;}
	public void setComplete() {complete = true;}

	public String toString() {
		return "("+getRelative()+")\n";
	}
}
/*
	public String toString() {
		return "("+getBase().getName()+")\n";
	}
			return "("+getRelative()+")\n";
*/