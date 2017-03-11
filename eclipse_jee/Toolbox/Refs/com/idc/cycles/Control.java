package com.idc.cycles;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Control {
	private static String m_strReport1;
	private static String m_strReport2;
	private static String m_strDirectory;

	private static int MAX_LVL = 6; // any more and out of memory
	private static int SCENARIO = 0;

	private NodeItemInfo m_topNode = new NodeItemInfo("TOPNODE", 0);
	private DefInfo m_defInfo = new DefInfo();
	private RefInfo m_refInfo = new RefInfo();
	private SingleInfo m_singles = new SingleInfo();

	private void makeoptions() {
		System.out.println("scenario " + SCENARIO);
		switch (SCENARIO) {
		case 0:
		default:
			m_strDirectory = "c:/development/herc_10_05";
			m_strReport1 = "c:/jvWork1/cycles_report1.lis";
			m_strReport2 = "c:/jvWork1/cycles_report2.lis";
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
		m_defInfo.show();
		System.out.println("Make Top Node");
		makeTopNode();
		// System.out.println("++++++++++++++++++++++++++++++++++ after
		// makeTopNode()");
		m_topNode.show();
		// System.out.println("++++++++++++++++++++++++++++++++++ Done");

		System.out.println("Make other nodes");
		makeAllNodes(m_topNode);
		// System.out.println("++++++++++++++++++++++++++++++++++ after
		// makeAllNodes");
		m_topNode.show();
		// System.out.println("++++++++++++++++++++++++++++++++++ Done");

		System.out.println("Make Top References");
		makeTopRefs(m_topNode);
		m_refInfo.show();

		System.out.println("Make Error Reports");
		makeErrors();
		System.out.println("Complete");
	}

	private void makeErrors() {
		JVLog.getInstance().setFile(m_strReport1, false);
		Iterator<RefItemInfo> iter = m_refInfo.getItems();
		while (iter.hasNext()) {
			RefItemInfo refItemInfo = (RefItemInfo) iter.next();
			WorklistInfo worklist = new WorklistInfo();
			Iterator<String> iter2 = refItemInfo.getItems();
			while (iter2.hasNext()) {
				String str = (String) iter2.next();
				worklist.add(str);
			}
			refItemInfo = null;

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

	private void makeSingleList(WorklistInfo worklist) {
		System.out.println(">>> makeSingleList ");
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
		System.out.println("<<< makeSingleList ");
	}

	private void makeTopRefs(NodeItemInfo current) {
		System.out.println(">>> makeTopRefs; Node "+current.getName()+" level "+current.getLevel());
		RefItemInfo ref = new RefItemInfo();
		ref.add(current.getName());
		makerefs(current, ref);
		System.out.println("<<< makeTopRefs; Node "+current.getName()+" level "+current.getLevel());
	}

	private void makerefs(NodeItemInfo current, RefItemInfo ref) {
		System.out.println(">>> makerefs; Node "+current.getName()+" level "+current.getLevel());
		NodeInfo nodeInfo = current.getNodeInfo();
		Iterator<NodeItemInfo> iter = nodeInfo.getItems();
		while (iter.hasNext()) {
			NodeItemInfo next = (NodeItemInfo) iter.next();
			String name = next.getName();
			RefItemInfo nextRef = new RefItemInfo (ref);
			nextRef.add (name);
			// System.out.println("topRefs :"+name);
			makerefs (next, nextRef);
			next = null;
		}
		m_refInfo.add(ref);
		System.out.println("<<< makerefs; Node "+current.getName()+" level "+current.getLevel());
	}

	private void makeTopNode() {
		Iterator<DefItemInfo> iter = m_defInfo.getItems();
		while (iter.hasNext()) {
			DefItemInfo defItemInfo = (DefItemInfo) iter.next();
			m_topNode.add (new NodeItemInfo (defItemInfo.getName(), 1));
		}
	}

	private void makeAllNodes(NodeItemInfo current) {
		System.out.println(">>> makeAllNodes; Node "+current.getName()+" level "+current.getLevel());
		NodeInfo nodeInfo = current.getNodeInfo();
		Iterator<NodeItemInfo> iter = nodeInfo.getItems();
		while (iter.hasNext()) {
			NodeItemInfo next = (NodeItemInfo) iter.next();
			String name = next.getName();
			int level = next.getLevel();
			// System.out.println("(makeAllNodes) :"+name+": level "+level);
			DefItemInfo def = m_defInfo.getDef(name);
			if (def == null) {
				System.err.println("Control::makeAllNodes; found no ref for " + name);
			} else {
				next.add(def, level + 1);
				if (level + 1 < MAX_LVL) makeAllNodes(next);
			}
		}
		System.out.println("<<< makeAllNodes; Node "+current.getName()+" level "+current.getLevel());
	}

	private void makedefs() {
		File[] allFiles = (new File(m_strDirectory)).listFiles();
		for (int i = 0; i < allFiles.length; i++) {
			File dir = allFiles[i];
			if (! dir.isDirectory()) continue;
			System.out.println("path "+dir.getPath());
			String strFile = dir.getPath() + File.separatorChar + ".classpath";
			File file = new File (strFile);
			if (! file.exists()) continue;
			if (! file.isFile()) continue;
			JVxml jvxml = new JVxml();
			DefItemInfo def = new DefItemInfo(dir.getName());
			def.add(jvxml.parse(file));
			m_defInfo.add(def);
			jvxml = null;
			def = null;
		}
	}
}
