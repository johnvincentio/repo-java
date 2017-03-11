package com.idc.rda.phase3;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import com.idc.rda.phase2.JVxml;
import com.idc.rda.phase2.items.KeyInfo;
import com.idc.rda.phase2.items.KeyItemInfo;
import com.idc.rda.phase2.items.SectionInfo;
import com.idc.rda.phase2.items.SectionItemInfo;
import com.idc.utils.XMLUtils;

public class App {
	private static final String INPUT_XML = "C:/irac7/wrkspc/Rda/RDA1/output/summary.xml";

	public static void main(String[] args) {
		App m_app = new App();
		try {
			m_app.doTest();
		}
		catch (Exception ex) {
			System.out.println("Exception; "+ex.getMessage());
		}
	}

	private void doTest() throws Exception {
		SectionInfo sectionInfo = new SectionInfo();

		sectionInfo = JVxml.parse (sectionInfo, new File (INPUT_XML), true);
		System.out.println("SectionInfo is built");

		Iterator<SectionItemInfo> iter1 = sectionInfo.getItems();
		while (iter1.hasNext()) {
			SectionItemInfo sectionItemInfo = iter1.next();
			String sectionName = sectionItemInfo.getName();
			System.out.println("section "+sectionName);

			Iterator<KeyInfo> iter2 = sectionItemInfo.getItems();
			while (iter2.hasNext()) {
				KeyInfo keyInfo = iter2.next();
				String key = keyInfo.getName();
				System.out.println("\tkey "+key);
				Iterator<KeyItemInfo> iter3 = keyInfo.getItems();
				while (iter3.hasNext()) {
					KeyItemInfo keyItemInfo = iter3.next();
					int jira = keyItemInfo.getJira();
					System.out.println("\t\tjira "+jira);
					ArrayList<String> keyList = keyItemInfo.getKeyList();
					ArrayList<String> valueList = keyItemInfo.getValueList();
					for (int cnt = 0; cnt < keyList.size(); cnt++) {
						String name = keyList.get(cnt);
						String value = valueList.get(cnt);
						if (value == null) value = "";
						System.out.println("\t\t\tname "+name+" value "+value);
					}
				}
			}
		}
	}
}
