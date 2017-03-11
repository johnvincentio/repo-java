
package com.idc.patterns;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Utils {
	public static void main (String[] args) {
		String htmlFile = "C:/jvRadWorkAll/Tools/wrkspc/work/Patterns/test.html";
		StringBuffer buf = getHTML(htmlFile);
		System.out.println("buf :"+buf+":");
		StringBuffer newBuf = Rewrite.rewriteLinks("https://www.etrieve.com/etrieve", buf);
		System.out.println("newBuf :"+newBuf+":");
	}
	public static StringBuffer getHTML(String htmlFile) {
		File m_file = new File(htmlFile);
		StringBuffer m_buf = new StringBuffer();
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
		return m_buf;
	}
}

/*

9/28/07 14:12:39:697 EDT] 00000042 SystemOut     O [com.hertz.herc.framework.MemberEtrieveReportsHelper] etrieveServer :https://etrieve.htzpartners.com/EtrieveIt/:
[9/28/07 14:12:39:697 EDT] 00000042 SystemOut     O [com.hertz.herc.framework.MemberEtrieveReportsHelper] (1) lBuffer length 4624
[9/28/07 14:12:39:697 EDT] 00000042 SystemOut     O [com.hertz.herc.framework.MemberEtrieveReportsHelper] (2) lBuffer length 5464
[9/28/07 14:12:39:697 EDT] 00000042 SystemOut     O [com.hertz.herc.framework.MemberEtrieveReportsHelper] (3) lBuffer length 5464
[9/28/07 14:12:39:707 EDT] 00000042 SystemOut     O [com.hertz.herc.framework.MemberEtrieveReportsHelper] remove links survived
[9/28/07 14:12:39:707 EDT] 00000042 SystemOut     O [com.hertz.herc.framework.MemberEtrieveReportsHelper] (3a) lBuffer length 5464
[9/28/07 14:12:39:707 EDT] 00000042 SystemOut     O [com.hertz.herc.framework.MemberEtrieveReportsHelper] (a) remove links survived
[9/28/07 14:12:39:707 EDT] 00000042 SystemOut     O [com.hertz.herc.framework.MemberEtrieveReportsHelper] strURL :/herc/member/index.jsp?targetPage=reportsView.jsp:

*/

/*
		Pattern corpAccounts = Pattern.compile("<a .*?href=\"corpLinkAccounts.jsp\".*?>.*?</a>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Pattern closeWindow = Pattern.compile("<a .*?href=\"javascript\\:window\\.close\\(\\)\".*?>.*?</a>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Pattern [] clean = { corpAccounts, closeWindow };
		LogBroker.debug(classRef, "clean length "+clean.length);
		for (int a = 0; a < clean.length; a++) {		// delete matching patterns
			LogBroker.debug(classRef, "a is "+a);
			LogBroker.debug(classRef, "(3) lBuffer length "+lBuffer.length());
			matcher = clean[a].matcher(lBuffer);
			if (matcher == null)
				LogBroker.debug(classRef, "matcher is null");
			else
				LogBroker.debug(classRef, "matcher is not null");
	        while (matcher.find()) {
	        	LogBroker.debug(classRef, "matcher.find survived");
	        	LogBroker.debug(classRef, "(4) lBuffer length "+lBuffer.length());
	        	LogBroker.debug(classRef, "matcher.start(0) "+matcher.start(0));
	        	LogBroker.debug(classRef, "matcher.end(0) "+matcher.end(0));
//	        	LogBroker.debug(classRef, "Matched: " + matcher.group(0));
	        	lBuffer.delete(matcher.start(0), matcher.end(0));
	        	LogBroker.debug(classRef, "delete survived");
	        	LogBroker.debug(classRef, "(5) lBuffer length "+lBuffer.length());
	        }
		}
		LogBroker.debug(classRef, "remove links survived");

		Pattern corpAccounts = Pattern.compile("<a .*?href=\"corpLinkAccounts.jsp\".*?>.*?</a>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
 		LogBroker.debug(classRef, "(3) lBuffer length "+lBuffer.length());
		matcher = corpAccounts.matcher(lBuffer);
		if (matcher == null)
			LogBroker.debug(classRef, "matcher is null");
		else
			LogBroker.debug(classRef, "matcher is not null");
        while (matcher.find()) {
        	LogBroker.debug(classRef, "matcher.find survived");
        	LogBroker.debug(classRef, "(4) lBuffer length "+lBuffer.length());
        	LogBroker.debug(classRef, "matcher.start() "+matcher.start());
        	LogBroker.debug(classRef, "matcher.end() "+matcher.end());
//	        	LogBroker.debug(classRef, "Matched: " + matcher.group(0));
        	lBuffer.delete(matcher.start(), matcher.end());
        	LogBroker.debug(classRef, "delete survived");
        	LogBroker.debug(classRef, "(5) lBuffer length "+lBuffer.length());
        }
		LogBroker.debug(classRef, "remove links survived");

        Pattern closeWindow = Pattern.compile("<a .*?href=\"javascript\\:window\\.close\\(\\)\".*?>.*?</a>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		LogBroker.debug(classRef, "(3a) lBuffer length "+lBuffer.length());
		matcher = closeWindow.matcher(lBuffer);
		if (matcher == null)
			LogBroker.debug(classRef, "matcher is null");
		else
			LogBroker.debug(classRef, "matcher is not null");
        while (matcher.find()) {
        	LogBroker.debug(classRef, "matcher.find survived");
        	LogBroker.debug(classRef, "(4a) lBuffer length "+lBuffer.length());
        	LogBroker.debug(classRef, "matcher.start() "+matcher.start());
        	LogBroker.debug(classRef, "matcher.end() "+matcher.end());
//	        	LogBroker.debug(classRef, "Matched: " + matcher.group(0));
        	lBuffer.delete(matcher.start(), matcher.end());
        	LogBroker.debug(classRef, "delete survived");
        	LogBroker.debug(classRef, "(5a) lBuffer length "+lBuffer.length());
        }
		LogBroker.debug(classRef, "(a) remove links survived");

*/
