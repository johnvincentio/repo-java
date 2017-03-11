package com.idc.post;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletInputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileUploadBean {
	private String m_savePath, m_filepath, m_filename, m_contentType;
	private Dictionary<String, String> m_fields;

	public String getFilename() {return m_filename;}
	public String getFilepath() {return m_filepath;}
	public void setSavePath(String savePath) {this.m_savePath = savePath;}
	public String getContentType() {return m_contentType;}

	public String getFieldValue(String fieldName) {
		if (m_fields == null || fieldName == null)
			return null;
		return (String) m_fields.get(fieldName);
	}
	private void setFilename(String s) {
		if (s==null) return;
		int pos = s.indexOf("filename=\"");
		if (pos != -1) {
			m_filepath = s.substring(pos+10, s.length()-1);
// Windows browsers include the full path on the client
// But Linux/Unix and Mac browsers only send the filename
// test if this is from a Windows browser
			pos = m_filepath.lastIndexOf("\\");
			if (pos != -1)
				m_filename = m_filepath.substring(pos + 1);
			else
				m_filename = m_filepath;
		}
	}
	private void setContentType(String s) {
		if (s==null) return;
		int pos = s.indexOf(": ");
		if (pos != -1)
			m_contentType = s.substring(pos+2, s.length());
	}
	public void doUpload(HttpServletRequest request) throws IOException {
		System.out.println(">>> FileUploadBean::doUpload");
		ServletInputStream in = request.getInputStream();

		byte[] line = new byte[128];
System.out.println("line :"+line+":");
		int i = in.readLine(line, 0, 128);
System.out.println("i "+i);
		System.out.println("FileUploadBean::doUpload; stage 1");
		if (i < 3) return;
		int boundaryLength = i - 2;
		System.out.println("FileUploadBean::doUpload; stage 2");

		String boundary = new String(line, 0, boundaryLength); //-2 discards the newline character
		m_fields = new Hashtable<String, String>();
		System.out.println("FileUploadBean::doUpload; stage 3");
		while (i != -1) {
			String newLine = new String(line, 0, i);
			System.out.println("FileUploadBean::doUpload; stage 4");
			if (newLine.startsWith("Content-Disposition: form-data; name=\"")) {
				System.out.println("FileUploadBean::doUpload; stage 5");
				if (newLine.indexOf("filename=\"") != -1) {
					System.out.println("FileUploadBean::doUpload; stage 6");
					setFilename(new String(line, 0, i-2));
					if (m_filename==null)
						return;
//this is the file content
					System.out.println("FileUploadBean::doUpload; stage 7");
					i = in.readLine(line, 0, 128);
					setContentType(new String(line, 0, i-2));
					i = in.readLine(line, 0, 128);
// blank line
					i = in.readLine(line, 0, 128);
					newLine = new String(line, 0, i);
					PrintWriter pw = new PrintWriter(new BufferedWriter(new
						FileWriter((m_savePath==null? "" : m_savePath) + m_filename)));
// the problem is the last line of the file content
// contains the new line character.
// So, we need to check if the current line is
// the last line.
					System.out.println("FileUploadBean::doUpload; stage 8");
					while (i != -1 && !newLine.startsWith(boundary)) {
						System.out.println("FileUploadBean::doUpload; stage 9");
						i = in.readLine(line, 0, 128);
						if ((i==boundaryLength+2 || i==boundaryLength+4) // + 4 is eof
								&& (new String(line, 0, i).startsWith(boundary)))
							pw.print(newLine.substring(0, newLine.length()-2));
						else
							pw.print(newLine);
						newLine = new String(line, 0, i);
					}
					System.out.println("FileUploadBean::doUpload; stage 10");
					pw.close();
				}
				else {
//this is a field, get the field name
					System.out.println("FileUploadBean::doUpload; stage 11");
					int pos = newLine.indexOf("name=\"");
					String fieldName = newLine.substring(pos+6, newLine.length()-3);
					System.out.println("fieldName:" + fieldName);
// blank line
					i = in.readLine(line, 0, 128);
					i = in.readLine(line, 0, 128);
					newLine = new String(line, 0, i);
					StringBuffer fieldValue = new StringBuffer(128);
// The last line of the field
// contains the new line character.
// So, we need to check if the current line is
// the last line.
					System.out.println("FileUploadBean::doUpload; stage 12");
					while (i != -1 && !newLine.startsWith(boundary)) {
						System.out.println("FileUploadBean::doUpload; stage 13");
						i = in.readLine(line, 0, 128);
						if ((i==boundaryLength+2 || i==boundaryLength+4) // + 4 is eof
									&& (new String(line, 0, i).startsWith(boundary)))
							fieldValue.append(newLine.substring(0, newLine.length()-2));
						else
							fieldValue.append(newLine);
						newLine = new String(line, 0, i);
					}
					System.out.println("fieldValue:" + fieldValue.toString());
					m_fields.put(fieldName, fieldValue.toString());
				}
				System.out.println("FileUploadBean::doUpload; stage 14");
			}
			System.out.println("FileUploadBean::doUpload; stage 15");
			i = in.readLine(line, 0, 128);
		} // end while
		System.out.println("<<< FileUploadBean::doUpload");
	}
}
