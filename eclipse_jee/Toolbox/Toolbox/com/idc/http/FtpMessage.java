package com.idc.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

public class FtpMessage {
	private static final String m_newline = "\n";

	private FtpMessage() {}

// Program arguments -vmargs -Djava.net.preferIPv4Stack=true
	public static void main (String[] args) {
		String urlName = "ftp://anonymous:anonymous@ftp.nasdaqtrader.com/symboldirectory/mfundslist.txt;type=i";
		try {
			String jv = getString (urlName);
			System.out.println ("string : \n"+jv);
		}
		catch (Exception e) {
			System.out.println ("Error; "+e.getMessage ());
		}
	}

	public static String getString (String strUrl) throws Exception {
		System.out.println (">>> FtpMessage::getString; strUrl :"+strUrl+":");
		URL url = null;
		URLConnection urlCon = null;
		InputStream inputStream = null;
		BufferedReader rdr = null;
		StringBuffer sb = new StringBuffer();
		try {
			url = new URL (strUrl);
			urlCon = url.openConnection();
			inputStream = urlCon.getInputStream();
			String compression = urlCon.getHeaderField ("Content-Encoding");
			System.out.println ("compression "+compression);
			if (compression == null)
				rdr = new BufferedReader (new InputStreamReader (inputStream));
			else {
			    if ("gzip".equals(compression))
			    	rdr = new BufferedReader (new InputStreamReader (new GZIPInputStream (inputStream)));
			    else 
			        throw new Exception (compression +" not supported");
			}
			String line;
			while ((line = rdr.readLine()) != null) {
				sb.append(line).append(m_newline);
			}
			rdr.close();
			System.out.println ("<<< FtpMessage::getString");
			return sb.toString();
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			throw new Exception ("MalformedURLException error; "+e.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new Exception ("Exception error; "+e.getMessage());
		}
		finally {
			if (rdr != null) rdr.close();
			if (inputStream != null) inputStream.close();
			urlCon = null;
			url = null;

		}
	}
}
