
package com.idc.coder;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author John Vincent
 *
 */

public class Template {
	private File m_file;
	private StringBuffer m_buf = new StringBuffer();
	public Template (final File file) {m_file = file;}
	public Template (final String str) {m_file = new File(str);} 

	public void process() {
		BufferedReader buf = null;
		String line;
		try {
			buf = new BufferedReader(new FileReader(m_file));
			while ((line = buf.readLine()) != null) {m_buf.append(line+"\n");}
			buf = null;
		}
		catch (IOException exception) {
			System.out.println("Exception "+exception.getMessage());
			System.out.println("Trouble reading file "+m_file.getPath());
//			exception.printStackTrace();
		}
		finally {
			try {
				if (buf != null) buf.close();
			}
			catch (IOException exception2) {
				System.out.println("Exception "+exception2.getMessage());
				System.out.println("Trouble closing file "+m_file.getPath());
				exception2.printStackTrace();
			}
		}		
	}
	public void replace (int num, String strReplace) {
		String from = "{--("+Integer.toString(num)+")--}";
		JVString str = new JVString(m_buf.toString());
//		System.out.println("(1) str :"+str.getString()+":");
		str.replace(from, strReplace);
//		System.out.println("(2) str :"+str.getString()+":");
		m_buf = new StringBuffer(str.getString());
	}
	public String getTemplate() {return m_buf.toString();}
}
