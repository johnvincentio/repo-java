package com.idc.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.htmlparser.*;
import org.htmlparser.filters.*;
import org.htmlparser.util.*;
import org.htmlparser.tags.*;

import com.idc.http.AppCookies;
import com.idc.http.HttpMessage;
import com.idc.http.Receiver;
import com.idc.http.Sender;

public class A1 {
	private static final String MY_SITE = "http://www.stampalbums.com/worldwide_list.asp";
	private static final String MY_URL = "file:///Users/jv/Desktop/MyDevelopment/repo_ui_2/list-a-href/index.html";
	private static final String MY_OUT = "/Users/jv/tmp/stamps/wget.txt";

	public static void main(String[] args) {
		(new A1()).doit();
	}

	@SuppressWarnings("unused")
	private void do1() {
		try {
			AppCookies appCookies = new AppCookies();
			Receiver receiver;
			Sender sender = new Sender(MY_SITE);
			sender.setPostMethod();
			sender.addFormItem ("USERNAME", "jv2351mf");
			sender.addFormItem ("PASSWORD", "workout671");
			System.out.println("sender: "+sender.toString());
			receiver = HttpMessage.getReceiverWithRedirects (sender, appCookies);
			System.out.println("receiver: "+receiver.toString());
		}
		catch (Exception appex) {
			System.out.println("App exception "+appex.getMessage());
		}
	}

	private void doit() {
		List<String> list = getLinksOnPage (MY_URL);

		File file = new File (MY_OUT);
		BufferedWriter buffer;
		try {
			buffer = new BufferedWriter (new FileWriter (file));
			for (String str : list) {
//				System.out.println("url "+str);
				buffer.write ("wget "+str+"\n");
				buffer.flush ();
			}
			buffer.close ();
		}
		catch (IOException e) {
			System.out.println ("Unable to open file " + file.getPath () + ". Aborting");
		}
	}

	public List<String> getLinksOnPage(final String url) {
	    final List<String> result = new LinkedList<String>();
	    try {
		    final Parser htmlParser = new Parser(url);
	        final NodeList tagNodeList = htmlParser.extractAllNodesThatMatch(new NodeClassFilter(LinkTag.class));
	        for (int j = 0; j < tagNodeList.size(); j++) {
	            final LinkTag loopLink = (LinkTag) tagNodeList.elementAt(j);
	            final String loopLinkStr = loopLink.getLink();
	            result.add(loopLinkStr);
	        }
	    } catch (ParserException e) {
	        e.printStackTrace();
	    }
	    return result;
	}
}
