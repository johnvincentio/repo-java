
package com.idc.patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rewrite {

	/**
	 * Rewrites html for display within HERC
	 * 
	 * @param request	HttpServletRequest
	 * @param buffer	HTML to be changed
	 * @throws HertzSystemException
	 */
	public static StringBuffer rewriteLinks (String etrieveServer, StringBuffer pBuffer) {		
		System.out.println(">>> MemberEtrieveReportsHelper.rewriteLinks - version 111");
//		LogBroker.debug(classRef, "JV buffer :"+pBuffer+":");
//		LogBroker.debug(classRef, "etrieveServer :"+etrieveServer+":");
/*
 *	rewrite relative links to absolute
 */
		StringBuffer lBuffer = new StringBuffer (pBuffer.toString());
		try {
			Pattern link = Pattern.compile("<link .*?href=\"(.+?)\".*?>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
			Pattern img = Pattern.compile("<img .*?src=\"(.+?)\".*?>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
			Pattern input = Pattern.compile("<input .*?src=\"(.+?)\".*?>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
			Pattern script = Pattern.compile("<script .*?src=\"(.+?)\".*?>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
			Pattern background = Pattern.compile("background=\"(.+?)\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
			Pattern [] absolute = { link, img, input, script, background };
			Matcher matcher;
			for (int a = 0; a < absolute.length; a++) {
				matcher = absolute[a].matcher(lBuffer);
				while (matcher.find()) {
	        		System.out.println("Matched: " + matcher.group(0) + " (" + matcher.group(1) + ")");
					lBuffer.insert(matcher.start(1), etrieveServer);
				}
			}
		}
		catch (Exception ex) {
			System.out.println("rewriteLinks; Could not rewrite links (Exception) at 1");
			return lBuffer;
		}
/*
 * remove links:
 * 		filter out "corp accounts" link that allows access to all etrieve accounts.
 * 		filter out "close window" link as etrieve is no longer shown in a popup.
 */
		try {
			Pattern corpAccounts = Pattern.compile("<a .*?href=\"corpLinkAccounts.jsp\".*?>.*?</a>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
			Matcher matcher = corpAccounts.matcher(lBuffer);
			String sbuf = matcher.replaceAll("");
			lBuffer = new StringBuffer (sbuf);
		}
		catch (Exception ex) {
			System.out.println("rewriteLinks; Could not rewrite links (Exception) at 2");
			return lBuffer;
		}

		try {
			Pattern closeWindow = Pattern.compile("<a .*?href=\"javascript\\:window\\.close\\(\\)\".*?>.*?</a>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
			Matcher matcher = closeWindow.matcher(lBuffer);
			String sbuf = matcher.replaceAll("");
			lBuffer = new StringBuffer (sbuf);
		}
		catch (Exception ex) {
			System.out.println("rewriteLinks; Could not rewrite links (Exception) at 3");
			return lBuffer;
		}
/*
 * change the menu links
 */
//		Pattern mainMenu1 = Pattern.compile("<a .*?href=\"menu.jsp\".*?>.*?</a>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
//		Pattern mainMenu2 = Pattern.compile("<a .*?href=\'menu.jsp\'.*?>.*?</a>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

//		String strURL = URLHelper.buildIndexPath (request, URLConstants.PATH_MEMBER, "reportsView.jsp");
		String strURL = "https://www.hertzequip.com/herc/reportsView.jsp";
		try {
			Pattern mainMenu1 = Pattern.compile("<a .*?href=\"(.*?)\".*?>.*?</a>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
			Pattern mainMenu2 = Pattern.compile("<a .*?href=\'(.*?)\'.*?>.*?</a>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
			Pattern [] change = {mainMenu1, mainMenu2};
			for (int a = 0; a < change.length; a++) {		// delete matching patterns
				Matcher matcher = change[a].matcher(lBuffer);
				while (matcher.find()) {
					if (matcher.groupCount() < 1) continue;		// need the jsp file name
					System.out.println("Matched: " + matcher.group(1));
					if (matcher.group(1).equals("menu.jsp")) {			// only change menu.jsp
						lBuffer.delete(matcher.start(1), matcher.end(1));
						lBuffer.insert(matcher.start(1), strURL);
					}
	        	}
				matcher.reset();
	        }
		}
		catch (Exception ex) {
			System.out.println("rewriteLinks; Could not rewrite links (Exception) at 4");
			return lBuffer;
		}
		System.out.println("<<< MemberEtrieveReportsHelper.rewriteLinks");
		return lBuffer;
	}
}
