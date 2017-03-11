package net.sf.jdec.ui.util;

import java.io.File;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Delete {


public static void main(String a[]) throws Exception
{
	File f=new File("d:\\Software\\IDE\\eclipse\\startup.jar");
	JarFile jarFile=new JarFile(f.getAbsolutePath());
	Enumeration e=jarFile.entries();
	while(e.hasMoreElements())
	{

	    JarEntry entry=(JarEntry)e.nextElement();

	    File f1=new File(entry.getName());

	    String s=f1.getAbsolutePath();
	    
	    if(f1.isFile())
	    f1.createNewFile();

	}

}
}
