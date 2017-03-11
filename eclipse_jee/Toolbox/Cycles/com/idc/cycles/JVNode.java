package com.idc.cycles;

public class JVNode {
	private String m_name;
	private int m_level;
	private Allnodes m_allnodes;

	public JVNode (String name, int level) {
		m_name = name;
		m_level = level;
		m_allnodes = new Allnodes();
	}
	public String getName() {return m_name;}
	public int getLevel() {return m_level;}
	public void add (JVNode node) {m_allnodes.add(node);}
	public void add (Def def, int level) {
		def.reset();
		while (def.hasNext()) {
			// System.out.println(" ref :"+def.getRef()+":");
			add(new JVNode(def.getRef(), level));
			def.getNext();
		}
	}
	public Allnodes getAllnodes() {return m_allnodes;}

	public void show() {
		for (int i = 0; i < m_level; i++)
			System.out.print("\t");
		System.out.println(m_level + " " + m_name);
		m_allnodes.show();
	}
}
