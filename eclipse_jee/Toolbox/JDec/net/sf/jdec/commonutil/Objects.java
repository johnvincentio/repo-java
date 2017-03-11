package net.sf.jdec.commonutil;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.jdec.io.Writer;

/*
 *  Objects.java Copyright (c) 2006,07 Swaroop Belur 
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

/**
 * Dump any object (including arrays BUT not strings) stuff here
 * 
 * @author swaroop belur
 * @since 1.2.1
 */

public class Objects {

	private static Map objectMap = new HashMap();

	private static char[] keepAsIsChars = {

	'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
			'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B',
			'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
			'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '!', '@',
			'#', '$', '%', '^', '*', '(', ')', '-', '_', '+', '=', '?', '/',
			'>', '<', ',', '.', '`', '~'

	};

	public static boolean isPrimitive(java.lang.String type) {
		if (type.equals("int") || type.equals("short") || type.equals("char")
				|| type.equals("byte") || type.equals("long")
				|| type.equals("float") || type.equals("double")
				|| type.equals("boolean")) {
			return true;
		} else if (type.equals("I") || type.equals("S") || type.equals("C")
				|| type.equals("B") || type.equals("J") || type.equals("F")
				|| type.equals("D") || type.equals("Z")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isSuperClass(java.lang.String someClassName,
			java.lang.String currentSuperClass) {
		boolean yes = false;
		if (someClassName == null || currentSuperClass == null) {
			return false;
		}
		someClassName = someClassName.replaceAll("/", ".").trim();
		currentSuperClass = currentSuperClass.replaceAll("/", ".").trim();
		if (someClassName.equals(currentSuperClass)) {
			return !yes;
		}
		java.lang.String temp = currentSuperClass;
		do {
			try {

				Class z = Class.forName(temp + ".class");
				Class parent = z.getSuperclass();
				if (parent != null) {
					if (parent.getName().replaceAll("/", ".").trim().equals(
							someClassName)) {
						return true;
					}
				} else {
					return false;
				}
				temp = parent.getName().replaceAll("/", ".").trim();
			} catch (ClassNotFoundException cne) {
				Writer writer = null;
				try {
					writer = Writer.getWriter("log");
					cne.printStackTrace(writer);
				} catch (IOException ex) {

				}

			}
		} while (true);

	}

	public static boolean keepAsIs(char c) {

		for (int s = 0; s < keepAsIsChars.length; s++) {
			if (keepAsIsChars[s] == c) {
				return true;
			}
		}
		return false;
	}

	public static void registerObjectRef(Key key, Object object) {
		objectMap.put(key.name, object);
	}

	public static Object getObjectRef(Key key) {
		return objectMap.get(key.name);
	}

	private static class Key {

		private String name = null;

		private Key(String name) {
			this.name = name;
		}
	}

	public static boolean isNullOrEmpty(Collection c) {
		if (c == null || c.size() == 0)
			return true;
		return false;

	}

	public static boolean isNull(Collection c) {
		if (c == null)
			return true;
		return false;
	}

	public static boolean isEmpty(Collection c) {
		if (c.size() == 0)
			return true;
		return false;

	}

	public static boolean isNull(Object c) {
		if (c == null)
			return true;
		return false;

	}

	public static boolean isEmpty(Object c) {
		if (isNull(c))
			return true;
		if (c.toString().trim().length() == 0)
			return true;

		return false;

	}

	public static boolean isTrue(String c) {
		if (isEmpty(c))
			return false;
		if (c.trim().length() == 0)
			return false;
		if (c.equalsIgnoreCase("true")) {
			return true;
		}

		return false;

	}

	public static boolean isFalse(String c) {
		if (isEmpty(c))
			return false;
		if (c.trim().length() == 0)
			return false;
		if (c.equalsIgnoreCase("false")) {
			return true;
		}

		return false;

	}

	public static String getGenericPartOfGenericSignature(String signature) {

		if (signature == null || signature.length() == 0)
			return "";
		StringBuffer buffer = new StringBuffer();
		char temp;
		for (int z = 0; z < signature.length(); z++) {
			char ch = signature.charAt(z);
			if (ch == '+' || ch == '-')
				continue;
			if (ch == 'L') {
				if (z > 0) {
					temp = signature.charAt(z - 1);
					if (temp != '<' && temp != '+' && temp != '-'
							&& temp != ';' && temp != ':') {
						buffer.append(ch);
					} else if (temp == '+') {
						buffer.append("? extends ");
					} else if (temp == '-') {
						buffer.append("? super ");
					}
				}
			} else if (ch == '/') {
				buffer.append(".");
			} else if (ch == 'T') {
				if (z > 0) {
					temp = signature.charAt(z - 1);
					char next = '%';
					if ((z + 1) < signature.length()) {
						next = signature.charAt((z + 1));
					}
					if (temp == '<' && next == 'T') {
						buffer.append(ch);
					}
					if (temp == '<' && next == ':') {
						buffer.append(ch);
					}
					if (temp == ';' && next == ':') {
						buffer.append(ch);
					}
					if (temp == 'T' && (next == ';' || next == '%')) {
						buffer.append(ch);
					}
					
					else if (temp == '-') {
						buffer.append("? super ");
					} else if (temp == '+') {
						buffer.append("? extends ");
					}
				}
				
			} else if (ch == ';') {
				if ((z + 1) < signature.length()) {
					temp = signature.charAt((z + 1));
					if (temp == '>') {
						// noop
					} else {
						buffer.append(",");
					}
				}
			} else if (ch == ':') {
				if ((z + 1) < signature.length()) {
					temp = signature.charAt((z + 1));
					if (temp == 'L') {
						buffer.append(" extends ");
					}

				}
			} else if (ch == '*') {
				buffer.append("?");
			}

			else {
				if(ch != '<' && ch != '>')
					buffer.append(ch);
				if(ch == '<' && z > 0){
					buffer.append(ch);
				}
				if(ch == '>' && z < signature.length()-1){
					buffer.append(ch);
				}
			}
		}

		return buffer.toString().replaceAll("TT", "T");

	}

}