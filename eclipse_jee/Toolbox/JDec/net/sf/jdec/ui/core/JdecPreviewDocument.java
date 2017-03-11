/*
 * JdecPreviewDocument.java Copyright (c) 2006,07 Swaroop Belur
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package net.sf.jdec.ui.core;

import net.sf.jdec.ui.main.UILauncher;

import javax.swing.text.*;

import java.util.ArrayList;
import java.awt.*;

public class JdecPreviewDocument extends DefaultStyledDocument {
	private ArrayList keywordList = null;

	private SimpleAttributeSet keyWord = null;

	private SimpleAttributeSet number = null;

	private SimpleAttributeSet operator = null;

	private SimpleAttributeSet string = null;

	private SimpleAttributeSet annotation = null;

	private SimpleAttributeSet other = null;// Default

	private Document thisDoc = null;

	private Element root = null;

	private int endOffset = -1;

	private int startOffset = -1;

	private ArrayList endOfStringMarkers = new ArrayList();

	private ArrayList endOfCharMarkers = new ArrayList();

	public JdecPreviewDocument() {
		thisDoc = this;
		root = thisDoc.getDefaultRootElement();
		createAttributeSets();
		applyStyleConstants();
		populateJavakeywordListet();

	}

	private void createAttributeSets() {
		keyWord = new SimpleAttributeSet();
		number = new SimpleAttributeSet();
		operator = new SimpleAttributeSet();
		string = new SimpleAttributeSet();
		annotation = new SimpleAttributeSet();
		other = new SimpleAttributeSet();// Default
	}

	private Color keywordFore = null;

	private Color annFore = null;

	private Color StringFore = null;

	private Color numberFore = null;

	private Color opFore = null;

	private Color keywordBack = null;

	private Color annBack = null;

	private Color StringBack = null;

	private Color numberBack = null;

	private Color opBack = null;

	private String effectA = null;

	private String effectK = null;

	private String effectN = null;

	private String effectS = null;

	private String effectO = null;

	private void readUserPrefColors() {
		keywordFore = UILauncher.getUIConfigRef()
				.getCurrentForeGrndColor_KEYWORD_PREVW();
		if (keywordFore == null) {
			Color purple = new Color(102, 0, 153);
			keywordFore = purple;
		}

		annFore = UILauncher.getUIConfigRef()
				.getCurrentForeGrndColor_ANN_PREVW();
		if (annFore == null) {
			annFore = new Color(102, 0, 102);
		}

		StringFore = UILauncher.getUIConfigRef()
				.getCurrentForeGrndColor_STRING_PREVW();
		if (StringFore == null) {
			StringFore = Color.BLUE;
		}
		numberFore = UILauncher.getUIConfigRef()
				.getCurrentForeGrndColor_NUMBER_PREVW();
		if (numberFore == null) {
			numberFore = Color.RED;
		}
		opFore = UILauncher.getUIConfigRef()
				.getCurrentForeGrndColor_OPERATOR_PREVW();
		if (opFore == null) {
			opFore = Color.MAGENTA;
		}
		annBack = UILauncher.getUIConfigRef()
				.getCurrentBackGrndColor_ANN_PREVW();
		keywordBack = UILauncher.getUIConfigRef()
				.getCurrentBackGrndColor_KEYWORD_PREVW();
		numberBack = UILauncher.getUIConfigRef()
				.getCurrentBackGrndColor_NUMBER_PREVW();
		StringBack = UILauncher.getUIConfigRef()
				.getCurrentBackGrndColor_STRING_PREVW();
		opBack = UILauncher.getUIConfigRef()
				.getCurrentBackGrndColor_OPERATOR_PREVW();

		effectA = UILauncher.getUIConfigRef().getEffectANN_PREVW();
		if (effectA == null) {
			effectA = "Underlined";
		}
		effectK = UILauncher.getUIConfigRef().getEffectKEYWORD_PREVW();
		effectS = UILauncher.getUIConfigRef().getEffectSTRING_PREVW();
		effectO = UILauncher.getUIConfigRef().getEffectOP_PREVW();
		effectN = UILauncher.getUIConfigRef().getEffectNUMBER_PREVW();

	}

	private void applyStyleConstants() {

		readUserPrefColors();
		StyleConstants.setForeground(keyWord, keywordFore);
		StyleConstants.setForeground(number, numberFore);
		StyleConstants.setForeground(operator, opFore);
		StyleConstants.setForeground(other, Color.BLACK);
		StyleConstants.setForeground(annotation, annFore);
		StyleConstants.setForeground(string, StringFore);

		if (keywordBack != null)
			StyleConstants.setBackground(keyWord, keywordBack);
		if (annBack != null)
			StyleConstants.setBackground(annotation, annBack);
		if (numberBack != null)
			StyleConstants.setBackground(number, numberBack);
		if (StringBack != null)
			StyleConstants.setBackground(string, StringBack);
		if (opBack != null)
			StyleConstants.setBackground(operator, opBack);

		if (effectK != null) {
			boolean u = effectK.equals("Underlined");
			if (u) {
				StyleConstants.setUnderline(keyWord, true);
			}
			int index = effectK.indexOf("StrikeThrough");
			if (index != -1) {
				StyleConstants.setStrikeThrough(keyWord, true);
			}

		}
		if (effectA != null) {
			boolean u = effectA.equals("Underlined");
			if (u) {
				StyleConstants.setUnderline(annotation, true);
			}
			int index = effectA.indexOf("StrikeThrough");
			if (index != -1) {
				StyleConstants.setStrikeThrough(annotation, true);
			}

		}
		if (effectN != null) {
			boolean u = effectN.equals("Underlined");
			if (u) {
				StyleConstants.setUnderline(number, true);
			}
			int index = effectN.indexOf("StrikeThrough");
			if (index != -1) {
				StyleConstants.setStrikeThrough(number, true);
			}

		}
		if (effectS != null) {
			boolean u = effectS.equals("Underlined");
			if (u) {
				StyleConstants.setUnderline(string, true);
			}
			int index = effectS.indexOf("StrikeThrough");
			if (index != -1) {
				StyleConstants.setStrikeThrough(string, true);
			}

		}
		if (effectO != null) {
			boolean u = effectO.equals("Underlined");
			if (u) {
				StyleConstants.setUnderline(operator, true);
			}
			int index = effectO.indexOf("StrikeThrough");
			if (index != -1) {
				StyleConstants.setStrikeThrough(operator, true);
			}

		}

		String s1 = UILauncher.getUIConfigRef().getFontFamilykwd();
		if (s1 != null)
			StyleConstants.setFontFamily(keyWord, s1);
		String s2 = UILauncher.getUIConfigRef().getFontSizekwd();
		try {
			StyleConstants.setFontSize(keyWord, Integer.parseInt(s2));
		} catch (NumberFormatException ne) {

		}

		s1 = UILauncher.getUIConfigRef().getFontFamilynum();

		if (s1 != null)
			StyleConstants.setFontFamily(number, s1);
		s2 = UILauncher.getUIConfigRef().getFontSizenum();
		try {
			StyleConstants.setFontSize(number, Integer.parseInt(s2));
		} catch (NumberFormatException ne) {

		}
		s1 = UILauncher.getUIConfigRef().getFontFamilyann();

		if (s1 != null)
			StyleConstants.setFontFamily(annotation, s1);
		s2 = UILauncher.getUIConfigRef().getFontSizeann();
		try {
			StyleConstants.setFontSize(annotation, Integer.parseInt(s2));
		} catch (NumberFormatException ne) {

		}
		s1 = UILauncher.getUIConfigRef().getFontFamilyop();
		s2 = UILauncher.getUIConfigRef().getFontSizeop();
		if (s1 != null)
			StyleConstants.setFontFamily(operator, s1);

		try {
			StyleConstants.setFontSize(operator, Integer.parseInt(s2));
		} catch (NumberFormatException ne) {

		}

		s1 = UILauncher.getUIConfigRef().getFontFamilystr();
		s2 = UILauncher.getUIConfigRef().getFontSizestr();
		if (s1 != null)
			StyleConstants.setFontFamily(string, s1);

		try {
			StyleConstants.setFontSize(string, Integer.parseInt(s2));
		} catch (NumberFormatException ne) {

		}

	}

	int currentOffset = -1;

	String gotString = "";

	String currentDocText = "";

	public void insertString(int offset, String str, AttributeSet a) {
		try {
			super.insertString(offset, str, a);
			currentOffset = offset;
			gotString = str;
			currentDocText = thisDoc.getText(0, thisDoc.getLength());
			updateSyntaxandColorForGotText();
		} catch (BadLocationException e) {

		}
	}

	private void updateSyntaxandColorForGotText() {
		int offset = currentOffset;
		int stringLength = gotString.length();
		int end = offset + stringLength;
		// update the syntax/color/font for the total number of children/lines

		int startFrom = root.getElementIndex(offset); // From where to Begin
		int goTo = root.getElementIndex(end); // Go till this child pos/line

		int z = startFrom;
		while (z <= goTo) {
			processChild(currentDocText, z);
			z++;
		}

	}

	private void processChild(String text, int childIndex) {

		startOffset = root.getElement(childIndex).getStartOffset();
		// int k=startOffset;
		// if(endOffset > 0) k+=endOffset;
		endOffset = root.getElement(childIndex).getEndOffset() - 1;
		// if(k > startOffset && k < endOffset)startOffset=k;
		int childLength = endOffset - startOffset;

		// ((DefaultStyledDocument)thisDoc).setCharacterAttributes(startOffset,
		// childLength, other, true);
		if (endOffset <= startOffset)
			return;
		scanTextForCurrentCategories(text, startOffset, endOffset);

	}

	private boolean processed = false;

	private void scanTextForCurrentCategories(String text, int startOffset,
			int endOffset) {
		int k = -1;
		for (int z = startOffset; z < endOffset; z++) {

			k = z;
			processed = false;

			z = checkForSkipCharacters(text, z);

			if (z >= text.length())
				return;

			z = scanForAnyKeyWord(z, endOffset, text, z);

			if (!processed)
				z = scanForAnyNumber(z, text);

			if (!processed) {
				z = scanForAnyOperator(z, text);
			}

			if (!processed) {
				boolean b = scanForStartOfString(text, z);
				if (b) {
					int start = z;
					int endOfS = getEndOFString(text, z);
					if (endOfS != -1) {
						String str = text.substring(start + 1, endOfS);
						// System.out.println("String "+str);
						((DefaultStyledDocument) thisDoc)
								.setCharacterAttributes(start + 1,
										str.length(), string, true);
						processed = true;
						endOfStringMarkers.add(new Integer(endOfS));
						z = endOfS + 1;
					}
				}
			}
			if (!processed) {
				boolean b = scanForStartOfAnnotation(text, z);
				if (b) {
					int start = z;
					int endOfAnn = getEndOfAnnotationType(text, z);
					if (endOfAnn != -1) {
						String str = text.substring(start, endOfAnn);
						// System.out.println("String "+str);
						((DefaultStyledDocument) thisDoc)
								.setCharacterAttributes(start, str.length(),
										annotation, true);
						processed = true;
						z = endOfAnn;
					}
				}

			}
			/*
			 * if(!processed) { boolean
			 * b=scanForStartOfCharacterLiteral(text,z); if(b) { int start=z;
			 * int endOfC=getEndOFChar(text,z); if(endOfC!=-1) { String
			 * str=text.substring(start+1,endOfC);
			 * 
			 * ((DefaultStyledDocument)thisDoc).setCharacterAttributes(start+1,
			 * endOfC, string, true); processed=true; endOfStringMarkers.add(new
			 * Integer(endOfC)); z=endOfC+1; } } }
			 */

			if (!processed && z < text.length()) {
				System.out.println(text.charAt(z) + "-->Other");
				((DefaultStyledDocument) thisDoc).setCharacterAttributes(z, 1,
						other, false);
			}

			/*
			 * if(z==k) { int childLength = endOffset - z;
			 * ((DefaultStyledDocument)thisDoc).setCharacterAttributes(startOffset,
			 * childLength, other, true); }
			 */

		}

	}

	private int scanForAnyOperator(int k, String text) {

		if (k < text.length()) {
			boolean next = false;
			boolean prev = false;
			boolean isOp = checkForOperator(text.charAt(k));
			if (k + 1 < text.length() && k + 1 > 0) {
				next = checkForOperator(text.charAt(k + 1));
			}
			if (k - 1 < text.length() && k - 1 > 0) {
				prev = checkForOperator(text.charAt(k - 1));
			}
			if (isOp && (next || prev)) {
				isOp = false;
			}
			if (isOp) {
				// System.out.println(text.charAt(k)+"Is the op");
				((DefaultStyledDocument) thisDoc).setCharacterAttributes(k, 1,
						operator, true);
				processed = true;
			}
		}
		return k;

	}

	private int scanForAnyNumber(int k, String text) {

		int reqdEnd = -1;
		if (k < text.length()) {

			StringBuffer num = new StringBuffer("");

			boolean isNumber = checkForNumber(text, text.charAt(k), num, k);

			if (isNumber) {

				int end = Integer.parseInt(num.toString());
				int total = end - k;
				((DefaultStyledDocument) thisDoc).setCharacterAttributes(k,
						total, number, true);
				processed = true;

			}
		}
		return k;

	}

	private int scanForAnyKeyWord(int startOffset, int endOffset, String text,
			int pos) {

		int reqdEnd = -1;
		for (int k = startOffset; k <= endOffset && k < text.length(); k++) {
			boolean endOfOWord = checkForEndOfWord(text.charAt(k));
			if (endOfOWord) {
				reqdEnd = k;
				break;
			}
		}

		if (reqdEnd != -1 && reqdEnd > startOffset) {
			String token = text.substring(startOffset, reqdEnd);
			kwd = token;
			// System.out.println(token);
			boolean key = checkForKeyWord(token);
			if (key) {
				int end = reqdEnd - startOffset;
				// System.out.println(token+"KWD");
				((DefaultStyledDocument) thisDoc).setCharacterAttributes(
						startOffset, end, keyWord, true);
				processed = true;
				return reqdEnd;
			}

		}
		return pos;

	}

	private boolean wasThisSomeEndOfString(int pos) {
		int sz = endOfStringMarkers.size();
		if (sz == 0) {
			return false;
		}
		for (int z = 0; z < sz; z++) {
			Integer in = (Integer) endOfStringMarkers.get(z);
			if (in.intValue() == pos) {
				return true;
			}
		}
		return false;

	}

	private boolean wasThisSomeEndOfSomeChar(int pos) {
		int sz = endOfCharMarkers.size();
		if (sz == 0) {
			return false;
		}
		for (int z = 0; z < sz; z++) {
			Integer in = (Integer) endOfCharMarkers.get(z);
			if (in.intValue() == pos) {
				return true;
			}
		}
		return false;

	}

	private boolean scanForStartOfCharacterLiteral(String text, int pos) {
		if (pos < text.length()) {
			char c = text.charAt(pos);
			if (c == '\'') {
				boolean check = wasThisSomeEndOfSomeChar(pos);
				if (!check)
					return true;
				else
					return false;
			} else {
				return false;
			}
		} else {
			return false;
		}

	}

	private boolean scanForStartOfString(String text, int pos) {
		if (pos < text.length()) {
			char c = text.charAt(pos);
			if (c == '"') {
				boolean check = wasThisSomeEndOfString(pos);
				if (!check)
					return true;
				else
					return false;
			} else {
				return false;
			}
		} else {
			return false;
		}

	}

	private int getEndOFChar(String text, int start) {

		int len = text.length();
		for (int k = start + 1; k < len; k++) {

			char c = text.charAt(k);
			if (c == '\'') {

				return k;
			}

		}

		return -1;
	}

	private int getEndOFString(String text, int start) {

		int len = text.length();
		for (int k = start + 1; k < len; k++) {

			char c = text.charAt(k);
			if (c == '"') {
				int prev = k - 1;
				if (prev > 0) {
					char pc = text.charAt(prev);
					if (pc == '\\')
						continue;
					else {
						return k;
					}

				} else
					return -1;

			}

		}

		return -1;
	}

	private boolean checkForOperator(char c) {
		switch (c) {
		case '+':
		case '-':
		case '/':
		case '*':
		case '%':
			return true;
		}
		return false;

	}

	private boolean checkForKeyWord(String str) {
		return keywordList.contains(str);
	}

	private boolean checkForNumber(String text, char c, StringBuffer sb, int pos) {
		int x = pos;
		boolean isD = Character.isDigit(c);
		if (!isD) {
			return false;
		}

		forloop: for (; x < text.length(); x++) {
			c = text.charAt(x);
			switch (c) {
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				continue;
			default:
				break forloop;
			}
		}

		sb.append(x);

		return true;

	}

	private boolean checkForEndOfWord(char c) {

		switch (c) {

		case '{':
		case '}':
		case '(':
		case ')':
		case '[':
		case ']':
		case ' ':
		case '\n':
		case ';':
		case '.':
			return true;

		}
		return false;

	}

	private void populateJavakeywordListet() {
		keywordList = new ArrayList();
		keywordList.add("abstract");
		keywordList.add("boolean");
		keywordList.add("break");
		keywordList.add("byte");
		keywordList.add("case");
		keywordList.add("catch");
		keywordList.add("char");
		keywordList.add("class");
		keywordList.add("const");
		keywordList.add("continue");
		keywordList.add("default");
		keywordList.add("do");
		keywordList.add("double");
		keywordList.add("else");
		keywordList.add("extends");
		keywordList.add("false");
		keywordList.add("final");
		keywordList.add("finally");
		keywordList.add("float");
		keywordList.add("for");
		keywordList.add("goto");
		keywordList.add("if");
		keywordList.add("implements");
		keywordList.add("import");
		keywordList.add("instanceof");
		keywordList.add("int");
		keywordList.add("interface");
		keywordList.add("long");
		keywordList.add("native");
		keywordList.add("new");
		keywordList.add("null");
		keywordList.add("package");
		keywordList.add("private");
		keywordList.add("protected");
		keywordList.add("public");
		keywordList.add("return");
		keywordList.add("short");
		keywordList.add("static");
		keywordList.add("super");
		keywordList.add("switch");
		keywordList.add("synchronized");
		keywordList.add("this");
		keywordList.add("throw");
		keywordList.add("throws");
		keywordList.add("transient");
		keywordList.add("true");
		keywordList.add("try");
		keywordList.add("void");
		keywordList.add("volatile");
		keywordList.add("while");
	}

	private int checkForSkipCharacters(String text, int z) {
		int x = z;
		forloop: for (x = z; x < text.length(); x++) {
			switch (text.charAt(x)) {
			case '{':
			case '}':
			case ':':
			case ';':
			case '(':
			case ')':
			case '[':
			case ']':
			case '\'':
			case '\\':
				// case '"':
			case '!':

				continue;
			default:
				break forloop;

			}

		}

		return x;

	}

	private boolean scanForStartOfAnnotation(String text, int pos) {
		if (pos < text.length()) {
			char c = text.charAt(pos);
			if (c == '@') {

				return true;

			} else {
				return false;
			}
		} else {
			return false;
		}

	}

	private int getEndOfAnnotationType(String text, int start) {
		return text.indexOf("(", start + 1);
	}

	String kwd = null;
}
