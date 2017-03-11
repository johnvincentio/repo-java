package com.idc.cycles;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class Control {
	private static String m_strReport1;
	private static String m_strReport2;
	private static String m_strDirectory;

	private static int MAX_LVL = 6; // any more and out of memory
	private static int SCENARIO = 11;

	private JVNode m_topNode = new JVNode("TOPNODE", 0);
	private Alldefs m_alldefs = new Alldefs();
	private Allrefs m_allrefs = new Allrefs();
	private Singles m_singles = new Singles();

	private void makeoptions() {
		System.out.println("scenario " + SCENARIO);
		switch (SCENARIO) {
		//c:/jvWork1
		case 11:
			// 2
			m_strDirectory = "c:/development/herc_10_05";
			m_strReport1 = "c:/jvWork1/cycles_report1.lis";
			m_strReport2 = "c:/jvWork1/cycles_report2.lis";
			break;
		case 10: // ws 5 - Tim_1
			// 2
			m_strDirectory = "C:\\tmp\\tim_1\\cycles";
			m_strReport1 = "c:\\tmp\\tim_1\\report1.lis";
			m_strReport2 = "c:\\tmp\\tim_1\\report2.lis";
			break;
		case 9: // ws 5 - UTIL no refs, WEB no ref WEB, no EJB ref WEB
			// HertzCacheEJB cannot call a AdminToolEJB
			// IracFrameworkEJB no ref TranslationEJB
			// ErrorFrameworkEJB no refs
			// 2
			m_strDirectory = "C:\\tmp\\9\\PRC4031_CYCLESINT\\iRACSources";
			m_strReport1 = "c:\\tmp\\9\\report1.lis";
			m_strReport2 = "c:\\tmp\\9\\report2.lis";
			break;
		case 8: // ws 5 - UTIL no refs, WEB no ref WEB, no EJB ref WEB
			// HertzCacheEJB cannot call a AdminToolEJB
			// IracFrameworkEJB no ref TranslationEJB - 66
			//
			m_strDirectory = "C:\\tmp\\8\\PRC4031_CYCLESINT\\iRACSources";
			m_strReport1 = "c:\\tmp\\8\\report1.lis";
			m_strReport2 = "c:\\tmp\\8\\report2.lis";
			break;
		case 7: // ws 5 - UTIL no refs, WEB no ref WEB, no EJB ref WEB
			// HertzCacheEJB cannot call a AdminToolEJB - 128
			m_strDirectory = "C:\\tmp\\7\\PRC4031_CYCLESINT\\iRACSources";
			m_strReport1 = "c:\\tmp\\7\\report1.lis";
			m_strReport2 = "c:\\tmp\\7\\report2.lis";
			break;
		case 6: // ws 5 - UTIL no refs, WEB no ref WEB, no EJB ref WEB - 406
			m_strDirectory = "C:\\tmp\\6\\PRC4031_CYCLESINT\\iRACSources";
			m_strReport1 = "c:\\tmp\\6\\report1.lis";
			m_strReport2 = "c:\\tmp\\6\\report2.lis";
			break;
		case 5: // ws 5 - UTIL, no module, WEB, no WEB modules - 594
			m_strDirectory = "C:\\tmp\\5\\PRC4031_CYCLESINT\\iRACSources";
			m_strReport1 = "c:\\tmp\\5\\report1.lis";
			m_strReport2 = "c:\\tmp\\5\\report2.lis";
			break;
		case 4: // ws 5 - UTIL, no module - 601
			m_strDirectory = "C:\\tmp\\4\\PRC4031_CYCLESINT\\iRACSources";
			m_strReport1 = "c:\\tmp\\4\\report1.lis";
			m_strReport2 = "c:\\tmp\\4\\report2.lis";
			break;
		case 3: // ws 5 - Baseline - 10376
			m_strDirectory = "C:\\tmp\\3\\PRC4031_CYCLESINT\\iRACSources";
			m_strReport1 = "c:\\tmp\\3\\report1.lis";
			m_strReport2 = "c:\\tmp\\3\\report2.lis";
			break;
		case 2: // ws 6
			m_strDirectory = "C:\\tmp\\2\\iRACSrc3.0_INT";
			m_strReport1 = "c:\\tmp\\2\\report1.lis";
			m_strReport2 = "c:\\tmp\\2\\report2.lis";
			break;
		case -1: // Unix
			m_strDirectory = "/home/jv/downloads/a1/iRACSrc3.0_INT";
			m_strReport1 = "/tmp/report1.lis";
			m_strReport2 = "/tmp/report2.lis";
			break;
		case 0: // A, B, C
		default:
			m_strDirectory = "C:\\tmp\\work1\\iRACSrc3.0_INT";
			m_strReport1 = "c:\\tmp\\work1\\report1.lis";
			m_strReport2 = "c:\\tmp\\work1\\report2.lis";
		}
		System.out.println("\nDirectory :" + m_strDirectory);
		System.out.println("Report 1 :" + m_strReport1);
		System.out.println("Report 2 :" + m_strReport2);
	}

	public void doit() {
		System.out.println("Determining options");
		makeoptions();

		System.out.println("Loading modules and classpath data");
		makedefs();
		// m_alldefs.show();

		System.out.println("Make Top Node");
		makeTopNode();
		// System.out.println("++++++++++++++++++++++++++++++++++ after
		// makeTopNode()");
		// m_topNode.show();
		// System.out.println("++++++++++++++++++++++++++++++++++ Done");

		System.out.println("Make other nodes");
		makeAllNodes(m_topNode);
		// System.out.println("++++++++++++++++++++++++++++++++++ after
		// makeAllNodes");
		// m_topNode.show();
		// System.out.println("++++++++++++++++++++++++++++++++++ Done");

		System.out.println("Make Top References");
		makeTopRefs(m_topNode);
		// m_allrefs.show();

		System.out.println("Make Error Reports");
		makeErrors();
		System.out.println("Complete");
	}

	private void makeErrors() {
		JVLog.getInstance().setFile(m_strReport1, false);
		m_allrefs.reset();
		while (m_allrefs.hasNext()) {
			Worklist worklist = new Worklist();
			Ref ref = m_allrefs.getRef();
			while (ref.hasNext()) {
				worklist.add(ref.getRef());
				ref.getNext();
			}
			ref = null;
			m_allrefs.getNext();

			// worklist.show();

			ArrayList<String> myList = worklist.getList();
			for (int i = 0; i < myList.size(); i++) {
				String item = (String) myList.get(i);
				// System.out.println("item :"+item+":");
				int num = worklist.getCount(item);
				if (num > 1) {
					// System.out.println("FOUND ONE");
					makeSingleList(worklist);
					worklist.reportError();
					break;
				}
			}
			myList = null;
			worklist = null;
		}
		JVLog.getInstance().close();

		JVLog.getInstance().setFile(m_strReport2, false);
		// m_singles.show();
		ArrayList<String> myList = m_singles.getList();
		Collections.sort(myList);
		for (int i = 0; i < myList.size(); i++) {
			String item = (String) myList.get(i);
			JVLog.getInstance().println(item);
		}
		JVLog.getInstance().close();
	}

	private void makeSingleList(Worklist worklist) {
		// System.out.println(">>> makeSingleList ");
		ArrayList<String> myList = worklist.getList();
		for (int spos = 0; spos < myList.size(); spos++) {
			String item = (String) myList.get(spos);
			// System.out.println("item :"+item+":");
			int num = worklist.getCount(item, spos);
			// System.out.println("num "+num);
			if (num <= 1) continue;

			int epos = worklist.getNextPos(item, spos + 1);
			// worklist.show();
			// System.out.println("item :"+item+" spos "+spos+" epos "+epos);
			StringBuffer buf = new StringBuffer();
			for (int ptr = spos; ptr <= epos; ptr++) {
				if (ptr != spos) buf.append(",");
				buf.append(worklist.get(ptr));
			}
			m_singles.add(buf.toString());
			buf = null;
		}
		// System.out.println("<<< makeSingleList ");
	}

	private void makeTopRefs(JVNode current) {
		// System.out.println(">>> makeTopRefs; Node "+current.getName()+" level "+current.getLevel());
		Ref ref = new Ref();
		ref.add(current.getName());
		makerefs(current, ref);
		// System.out.println("<<< makeTopRefs; Node "+current.getName()+" level "+current.getLevel());
	}

	private void makerefs(JVNode current, Ref ref) {
		// System.out.println(">>> makerefs; Node "+current.getName()+" level "+current.getLevel());
		Allnodes allnodes = current.getAllnodes();
		allnodes.reset();
		while (allnodes.hasNext()) {
			JVNode next = allnodes.getNode();
			String name = next.getName();
			Ref nextRef = new Ref(ref);
			nextRef.add(name);
			// System.out.println("topRefs :"+name);
			makerefs(next, nextRef);
			next = null;
			allnodes.getNext();
		}
		m_allrefs.add(ref);
		// System.out.println("<<< makerefs; Node "+current.getName()+" level "+current.getLevel());
	}

	private void makeTopNode() {
		while (m_alldefs.hasNext()) {
			m_topNode.add (new JVNode (m_alldefs.getDef().getName(), 1));
			m_alldefs.getNext();
			// break; // remove this
		}
	}

	private void makeAllNodes(JVNode current) {
		// System.out.println(">>> makeAllNodes; Node "+current.getName()+" level "+current.getLevel());
		Allnodes allnodes = current.getAllnodes();
		while (allnodes.hasNext()) {
			JVNode next = allnodes.getNode();
			String name = next.getName();
			int level = next.getLevel();
			// System.out.println("(makeAllNodes) :"+name+": level "+level);
			Def def = m_alldefs.getDef(name);
			if (def == null) {
				System.err.println("Control::makeAllNodes; found no ref for " + name);
			} else {
				next.add(def, level + 1);
				if (level + 1 < MAX_LVL) makeAllNodes(next);
			}
			allnodes.getNext();
		}
		// System.out.println("<<< makeAllNodes; Node "+current.getName()+" level "+current.getLevel());
	}

	private void makedefs() {
		File[] allFiles = (new File(m_strDirectory)).listFiles();
		for (int i = 0; i < allFiles.length; i++) {
			File dir = allFiles[i];
			if (! dir.isDirectory()) continue;
			// System.out.println("path "+dir.getPath());
			String strFile = dir.getPath() + File.separatorChar + ".classpath";
			File file = new File(strFile);
			if (! file.exists()) continue;
			if (! file.isFile()) continue;
			JVxml jvxml = new JVxml();
			Def def = new Def(dir.getName());
			def.add(jvxml.parse(file));
			m_alldefs.add(def);
			jvxml = null;
			def = null;
		}
	}
}
