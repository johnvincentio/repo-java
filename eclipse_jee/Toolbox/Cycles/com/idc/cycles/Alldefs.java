
package com.idc.cycles;

import java.util.ArrayList;

public class Alldefs {
	  private ArrayList<Def> m_list = new ArrayList<Def>();
	  private int m_nPos = 0;

	  public void add (Def def) {m_list.add (def);}
	  public boolean isEmpty() {return m_list.isEmpty();}
	  public boolean hasNext() {return m_nPos < m_list.size();}
	  public void getNext() {m_nPos++;}
	  public String getName() {return ((Def) m_list.get(m_nPos)).getName();}
	  public void reset() {m_nPos = 0;}
	  public Def getDef() {return ((Def) m_list.get(m_nPos));}
	  public Def getDef(String name) {
 		   Def current;
 		   reset();
 		   while (hasNext()) {
	 		   current = (Def) m_list.get(m_nPos);
	 		   if (current.getName().equals(name)) return current;
 		   getNext();
 	 	  }
// 	 	System.err.println("getDef of "+name+" returned null");
		return null;
		}
		  public void show() {
		 		   System.out.println("Showing Alldefs\n");
		 		   for (int i=0; i<m_list.size(); i++) {
		 		 		   Def def = (Def) m_list.get(i);
		 		 		   def.show();
		 		   }
		 		   System.out.println("\nDone");
		  }
}








