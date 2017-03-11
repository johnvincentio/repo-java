
import java.util.regex.*;

public class ScrabblePacketReader {
	private String m_buffer;
	private static final String m_div = ":";
	private String[] m_splitStrings;
	private int m_nextSplit = 0;

	public ScrabblePacketReader (String strbuf) {
		m_buffer = strbuf;
		splitBuffer();
	}
	private void splitBuffer() {
		Pattern pattern = Pattern.compile(m_div);
		m_splitStrings = pattern.split(m_buffer);
	}
	public String getNext() {return m_splitStrings[m_nextSplit++];}
	public int getNextInt() {
		String strMsg = m_splitStrings[m_nextSplit++];
		Integer intNumber = new Integer(strMsg);
		return intNumber.intValue();
	}
	public int length() {return m_splitStrings.length;}
	public boolean hasNext() {return hasNext(1);}
	public boolean hasNext(int items) {
		int num = m_splitStrings.length - m_nextSplit;
		if (num < items)
			return false;
		else
			return true;
	}
}

