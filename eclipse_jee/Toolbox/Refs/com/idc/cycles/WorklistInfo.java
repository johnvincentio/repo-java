package com.idc.cycles;

import java.util.ArrayList;

public class WorklistInfo {
	private ArrayList<String> m_list;

	public WorklistInfo() {m_list = new ArrayList<String>();}
	public void add(String item) {m_list.add(item);}
	public ArrayList<String> getList() {return m_list;}
	public int getCount(String name) {
		int num = 0;
		for (int i = 0; i < m_list.size(); i++) {
			String item = (String) m_list.get(i);
			if (item.equals(name)) num++;
		}
		return num;
	}

	public int getCount(String name, int spos) {
		int num = 0;
		for (int i = spos; i < m_list.size(); i++) {
			String item = (String) m_list.get(i);
			if (item.equals(name)) num++;
		}
		return num;
	}

	public int getNextPos(String name, int pos) {
		int num = 0;
		for (num = pos; num < m_list.size(); num++) {
			String item = (String) m_list.get(num);
			if (item.equals(name)) break;
		}
		return num;
	}

	public String get(int pos) {
		if (pos < 0 || pos >= m_list.size()) return "";
		return (String) m_list.get(pos);
	}

	public void reportError() {
		boolean bFirst = true;
		// JVLog.getInstance().println("\nBuild cycle");
		for (int i = 0; i < m_list.size(); i++) {
			String item = (String) m_list.get(i);
			if (item.equals("TOPNODE")) continue;
			if (! bFirst) JVLog.getInstance().print(",");
			// if (i < 1) JVLog.getInstance().print("\t");
			// if (i > 0) JVLog.getInstance().print(",");
			JVLog.getInstance().print(item);
			bFirst = false;
		}
		JVLog.getInstance().println("");
		// JVLog.getInstance().println("\nEnd of build cycle");
	}

	public void show() {
		System.out.println("\nWorklist ");
		for (int i = 0; i < m_list.size(); i++) {
			String ref = (String) m_list.get(i);
			if (i > 0) System.out.print(",");
			System.out.print(ref);
		}
		System.out.println("\nWorklist complete");
	}
}
