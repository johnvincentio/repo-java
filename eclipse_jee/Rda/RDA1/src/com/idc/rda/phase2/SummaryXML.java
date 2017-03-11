package com.idc.rda.phase2;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import com.idc.rda.phase2.items.KeyInfo;
import com.idc.rda.phase2.items.KeyItemInfo;
import com.idc.rda.phase2.items.SectionInfo;
import com.idc.rda.phase2.items.SectionItemInfo;
import com.idc.utils.PrintFile;
import com.idc.utils.XMLUtils;

public class SummaryXML {

	private PrintFile printFile;
	private SectionInfo sectionInfo;

	public SummaryXML (String outputfile, SectionInfo sectionInfo) {
		System.out.println(">>> SummaryXML (constructor); file "+outputfile);
		printFile = new PrintFile (new File (outputfile));
		this.sectionInfo = sectionInfo;
		System.out.println("<<< SummaryXML (constructor)");
	}

	public void createXML () {
		System.out.println(">>> SummaryXML::createXML");
		output (0, "<?xml " + XMLUtils.makeQuotedEntry ("version", "1.0") + " " + XMLUtils.makeQuotedEntry ("encoding", "UTF-8") + "?>");
		output (0, XMLUtils.makeOpenNode ("jira"));

		Iterator<SectionItemInfo> iter1 = sectionInfo.getItems();
		while (iter1.hasNext()) {
			SectionItemInfo sectionItemInfo = iter1.next();
			String sectionName = sectionItemInfo.getName();
			System.out.println("sectionName "+sectionName);
			output (1, XMLUtils.makeOpenNode (sectionName));

			Iterator<KeyInfo> iter2 = sectionItemInfo.getItems();
			while (iter2.hasNext()) {
				KeyInfo keyInfo = iter2.next();
				String key = keyInfo.getName();
				System.out.println("key "+key);
				output (2, XMLUtils.makeOpenNode ("key", "type", key));
				Iterator<KeyItemInfo> iter3 = keyInfo.getItems();
				while (iter3.hasNext()) {
					KeyItemInfo keyItemInfo = iter3.next();
					int jira = keyItemInfo.getJira();
					output (3, XMLUtils.makeOpenNode ("jira", "value", Integer.toString(jira)));
					ArrayList<String> keyList = keyItemInfo.getKeyList();
					ArrayList<String> valueList = keyItemInfo.getValueList();
					for (int cnt = 0; cnt < keyList.size(); cnt++) {
						System.out.println("keyList "+keyList.get(cnt)+" valueList "+valueList.get(cnt));
						String name = keyList.get(cnt);
						String value = valueList.get(cnt);
						if (value == null) value = "";
						output (4, XMLUtils.makeEntry (name, value));
					}
					output (3, XMLUtils.makeClosedNode ("jira"));
				}
				output (2, XMLUtils.makeClosedNode ("key"));
			}
			output (1, XMLUtils.makeClosedNode (sectionName));
		}
		output (0, XMLUtils.makeClosedNode ("jira"));
		System.out.println("<<< SummaryXML::createXML");
	}

	public void output (int tabs, String msg) {
		printFile.println (XMLUtils.makeTabs(tabs) + msg);
	}
}
