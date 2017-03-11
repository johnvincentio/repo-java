package com.hertz.hack.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Parser {
	private DB m_db;
	private static final String DB_PROPERTIES_FILE="db2.properties";
	private static final String IP_PROPERTIES_FILE="ips.properties";
	private static final String OUTPUT_FILE="c:\\temp\\parser.txt";
	private static boolean m_bFileOutput = false;
	
	public Parser() {}
	public void doWork() {
		System.out.println("Reading list of ip addresses");
		Allips allips = new Allips();
        BufferedReader buf = null;
        String line;
        try {
            buf = new BufferedReader(new FileReader(
            	new File(IP_PROPERTIES_FILE)));
            while ((line = buf.readLine()) != null) {
            	allips.add(new IP(line));
            }
            buf = null;
        }
        catch (IOException exception) {
          	System.out.println("Exception "+exception.getMessage());
           	System.out.println("Trouble reading ip file "+IP_PROPERTIES_FILE);
        }
		allips.show();

		System.out.println("Connecting to database");
		m_db = new DB();
		if (! m_db.getConnection (DB_PROPERTIES_FILE)) {
			System.out.println("giving up...");
			System.exit(1);
		}
		System.out.println("Getting date");
		Data data = m_db.getData();
		System.out.println("Disconnecting...");
		m_db.disConnect();

		System.out.println("Look for matches");
		OutputLine out;
		if (m_bFileOutput)
			out = new FileLine();
		else
			out = new PrintLine();
		out.open(OUTPUT_FILE);
		Item item;
		String ip;
		IP iprecord;
		while (data.hasNext()) {
			item = data.getNext();
			ip = item.getIp();
			iprecord = allips.getIP(ip);
			if (iprecord != null) {
				showParts (item, out);
			}
//			else {
//				out.println("no match; Item :"+item.show());
//			}
		}
		out.close();
		System.out.println("Complete");
	}
	private void showParts (Item item, OutputLine out) {
		out.println("\n\n******************* Match "+item.getIp()+"\n\n");
		String data = item.getData();

		String delim = " ~ ";
		int s = 0;
		int e = 0;
		StringBuffer buf;
		while ((e = data.indexOf(delim, s)) >= 0) {
			buf = new StringBuffer();
			buf.append(data.substring(s, e));
//			System.out.println(" s "+s+" e "+e);
			s = e + delim.length();
			out.println(buf.toString());
		}
		if (s < data.length()) {
			buf = new StringBuffer();
			buf.append(data.substring(s));
//			System.out.println(" s "+s+" e "+e);
			s = e + delim.length();
			out.println(buf.toString());
		}
//		System.out.println("len "+data.length()+" s "+s+" e "+e);
		out.println("\n\n******************* End of Match "+item.getIp()+"\n\n");
	}
}
