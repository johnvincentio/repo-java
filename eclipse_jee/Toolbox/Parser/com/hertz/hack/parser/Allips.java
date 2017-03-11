package com.hertz.hack.parser;

import java.util.ArrayList;

public class Allips {
	private ArrayList<IP> m_list = new ArrayList<IP>();
	private int m_nPos = 0;
	public Allips() {}

	public void add(IP ip) {m_list.add(ip);}
	public boolean hasNext() {return m_nPos < m_list.size();}
	public IP getNext() {return (IP) m_list.get(m_nPos++);}
	public void show() {
		for (int i=0; i<m_list.size(); i++)
			System.out.println(((IP) m_list.get(i)).toString());
	}
	public void reset() {m_nPos = 0;}
	public IP getIP (String ip) {
		IP current;
		reset();
		while (hasNext()) {
			current = (IP) m_list.get(m_nPos);
			if (current.getIp().equals(ip)) {
				return current;
			}
			getNext();
		}
		return null;
	}
}
