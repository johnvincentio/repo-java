package com.idc.scrabble.utils;

public class PacketWriter {
	private StringBuffer m_buffer;
	private int m_type;
	public PacketWriter (int type) {
		m_type = type;
		m_buffer = new StringBuffer(Constants.MAX_BUFFER_SIZE);
		m_buffer.append(type);
	}
	public void append (int i) {
		m_buffer.append(Constants.DIVIDER);
		m_buffer.append(i);
	}
	public void append (String s) {
		m_buffer.append(Constants.DIVIDER);
		m_buffer.append(s);
	}
	public String getString() {
		return m_buffer.toString();
	}
}

