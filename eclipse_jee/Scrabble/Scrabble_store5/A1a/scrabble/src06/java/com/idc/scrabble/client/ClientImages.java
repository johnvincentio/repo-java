package com.idc.scrabble.client;

//import com.idc.scrabble.utils.Debug;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

public class ClientImages {
	private ClientApp m_app;
	private final int m_boardSize;
	private Image m_imgBack[];
	private Image m_imgLetters[];
	private Image m_imgBlankLetters[];
	private static final int m_tileTypesByPosition[] = 
	       {4, 0, 0, 1, 0, 0, 0, 4, 0, 0, 0, 1, 0, 0, 4,
  			0, 2, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 2, 0,
  			0, 0, 2, 0, 0, 0, 1, 0, 1, 0, 0, 0, 2, 0, 0,
  			1, 0, 0, 2, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 1,
  			0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0,
  			0, 3, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 3, 0,
  			0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0,
  			4, 0, 0, 1, 0, 0, 0, 5, 0, 0, 0, 1, 0, 0, 4,
  			0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0,
  			0, 3, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 3, 0,
  			0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0,
  			1, 0, 0, 2, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 1,
  			0, 0, 2, 0, 0, 0, 1, 0, 1, 0, 0, 0, 2, 0, 0,
  			0, 2, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 2, 0,
  			4, 0, 0, 1, 0, 0, 0, 4, 0, 0, 0, 1, 0, 0, 4};
	private static final int MAX_LETTERS=27;
	private static final int BLANK_INC=1000;

	public ClientImages (ClientApp app, int boardSize) {
		m_app = app;
		m_boardSize = boardSize;
		m_imgBack = new Image[6];
		m_imgBack[0] = getImage("gifs/tongue.gif"); // error image
		m_imgBack[1] = getImage("gifs/double_letter.gif");
		m_imgBack[2] = getImage("gifs/double_word.gif");
		m_imgBack[3] = getImage("gifs/triple_letter.gif");
		m_imgBack[4] = getImage("gifs/triple_word.gif");
		m_imgBack[5] = getImage("gifs/center.gif");
		m_imgLetters = new Image[MAX_LETTERS];
		m_imgBlankLetters = new Image[MAX_LETTERS];
		for (int i=0; i<MAX_LETTERS; i++)
			m_imgLetters[i] = m_imgBlankLetters[i] = null;
	}
	private int getPosition (int row, int col) {return row * m_boardSize + col;}
	public Image getTileImage(int row, int col) {
		int type = m_tileTypesByPosition[getPosition(row, col)];
		if (type < 1) return null;
		return m_imgBack[type];
	}

	public Image getLetterImage(int index) {
		if (index > BLANK_INC)
				return getBlankLetterImage (index - BLANK_INC);
		if (index < 0 || index >= MAX_LETTERS)
			return null;
		if (m_imgLetters[index] == null)
			m_imgLetters[index] = getImage(getImagePath(index,false));
		return m_imgLetters[index];
	}
	private Image getBlankLetterImage(int index) {
		if (index < 0 || index >= MAX_LETTERS)
			return m_imgBack[0];
		if (m_imgBlankLetters[index] == null)
			m_imgBlankLetters[index] = getImage(getImagePath(index,false));
		return m_imgBlankLetters[index];
	}
	private String getImagePath (int index, boolean bBlank) {
//		Debug.println(">>> getImagepath; index "+index+" bBlank "+bBlank);
		StringBuffer buffer = new StringBuffer();
		buffer.append("gifs/");
		char cLetter = (char) (index + 96);
		buffer.append(cLetter);
		if (bBlank) buffer.append ("_blk");
		buffer.append(".gif");
//		Debug.println("<<< getImagepath; string |"+buffer+"|");
		return buffer.toString();
	}
// load from a JAR
	private Image getImage (String filename) {
		URL url = ClassLoader.getSystemResource(filename);
		return (new ImageIcon(url)).getImage();
	}
/* if gifs are in the classpath
	private Image getImage (String filename) {
		Debug.println("getImage; "+filename);
		return m_app.getToolkit().getImage(filename);
	}
*/
/*  Applet only
	private Image getImage (String filename) {
		URL m_url = m_app.getCodeBase();
		return m_app.getImage(m_url,filename);
	}
*/
}

