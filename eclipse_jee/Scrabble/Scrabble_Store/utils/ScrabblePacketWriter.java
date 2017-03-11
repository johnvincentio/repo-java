
public class ScrabblePacketWriter {
	private StringBuffer m_buffer;
	private char m_div = ':';
	private int m_type;
	static final private int MAX_BUFFER_SIZE=2048;

	public ScrabblePacketWriter (int type) {
		m_type = type;
		m_buffer = new StringBuffer(MAX_BUFFER_SIZE);
		m_buffer.append(type);
	}
	public void append (int i) {
		m_buffer.append(m_div);
		m_buffer.append(i);
	}
	public void append (String s) {
		m_buffer.append(m_div);
		m_buffer.append(s);
	}
	public String getString() {
		return m_buffer.toString();
	}
}

