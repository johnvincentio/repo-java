package com.idc.rda.phase1.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import com.idc.rda.phase1.JiraItemInfo;
import com.idc.rda.phase1.items.AlphaInfo;
import com.idc.rda.phase1.items.AlphaItemInfo;
import com.idc.utils.PrintFile;
import com.idc.utils.XMLUtils;

public class JiraXML {

	private PrintFile printFile;
	private JiraItemInfo jiraItemInfo;

	public JiraXML (String outputfile, JiraItemInfo jiraItemInfo) {
		System.out.println(">>> JiraXML (constructor); file "+outputfile);
		printFile = new PrintFile (new File (outputfile));
		this.jiraItemInfo = jiraItemInfo;
		System.out.println("<<< JiraXML (constructor)");
	}
	public void createXML () {
		System.out.println(">>> JiraXML::createXML");
		output (0, "<?xml " + XMLUtils.makeQuotedEntry ("version", "1.0") + " " + XMLUtils.makeQuotedEntry ("encoding", "UTF-8") + "?>");
		output (0, XMLUtils.makeOpenNode ("jira"));

		int jira = jiraItemInfo.getJira();
		ArrayList <AlphaInfo> list = jiraItemInfo.getList();
		Iterator<AlphaInfo> sections = list.iterator();
		while (sections.hasNext()) {
			AlphaInfo alphaInfo = sections.next();
			String sectionName = alphaInfo.getSection();
//			System.out.println("sectionName "+sectionName);
			output (1, XMLUtils.makeOpenNode (sectionName));

			AlphaItemInfo subHeaderItemInfo = alphaInfo.getHeader();
			String[] str1Array = subHeaderItemInfo.getData();
//			System.out.println("str1Array "+Utils.traceStringArray (str1Array));

			Iterator<AlphaItemInfo> iter = alphaInfo.getItems();
			while (iter.hasNext()) {
				AlphaItemInfo alphaItemInfo = iter.next();
				String[] str2Array = alphaItemInfo.getData();
				output (2, XMLUtils.makeOpenNode ("key", "type", str2Array[0]));
				output (3, XMLUtils.makeOpenNode ("jira", "value", Integer.toString(jira)));
				for (int cnt = 1; cnt < str1Array.length; cnt++) {
					String name = str1Array[cnt];
					String value = str2Array[cnt];
					if (value == null) value = "";
					output (4, XMLUtils.makeEntry (name, value));
				}
				output (3, XMLUtils.makeClosedNode ("jira"));
				output (2, XMLUtils.makeClosedNode ("key"));
			}
			output (1, XMLUtils.makeClosedNode (sectionName));
		}

		output (0, XMLUtils.makeClosedNode ("jira"));
		System.out.println("<<< JiraXML::createXML");
	}

	public void output (int tabs, String msg) {
		printFile.println (XMLUtils.makeTabs(tabs) + msg);
	}
}

/*
HeaderItemInfo headerItemInfo = jiraItemInfo.getHeaderItemInfo();
output (1, XMLUtils.makeOpenNode ("Header"));
output (2, XMLUtils.makeEntry ("jira", headerItemInfo.getJira()));
output (2, XMLUtils.makeEntry ("sr", headerItemInfo.getSr()));
output (2, XMLUtils.makeEntry ("description", headerItemInfo.getDescription()));
output (2, XMLUtils.makeEntry ("template", headerItemInfo.getTemplateVersion()));
output (1, XMLUtils.makeClosedNode ("Header"));
*/
