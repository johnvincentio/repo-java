package com.idc.rda.phase2;

import java.io.File;

import com.idc.rda.phase2.items.SectionInfo;

public class App {
	private static final String[] INPUT_XML = {"C:/irac7/wrkspc/Rda/RDA1/output/jv01.xml",
		"C:/irac7/wrkspc/Rda/RDA1/output/jv02.xml"};
	private static final String OUTPUT_XML = "C:/irac7/wrkspc/Rda/RDA1/output/summary.xml";

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
		for (String inputXml : INPUT_XML) {
			sectionInfo = JVxml.parse (sectionInfo, new File (inputXml), true);
//			break;		//TODO; remove this
		}
		System.out.println("SectionInfo is built");

		SummaryXML summaryXML = new SummaryXML (OUTPUT_XML, sectionInfo);
		summaryXML.createXML();
	}
}
