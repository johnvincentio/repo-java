package net.sf.jdec.commonutil;

import net.sf.jdec.util.Util;

/*
 *  Numbers.java Copyright (c) 2006,07 Swaroop Belur 
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
 * * Dump any number related util method here.
 * 
 * @author swaroop belur(belurs)
 * @since 1.2.1
 */

public class Numbers {

	public static boolean isNumber(int i) {
		try {

			Integer.parseInt("" + i);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}

	}

	public static java.lang.String formatCharForNonAsciiValue(int c,
			StringBuffer sb) {

		if (Numbers.isNumber(c)) {
			return "" + c;
		}
		if (c < 32) {
			java.lang.String s = Util.formatForUTF("" + c, "char",
					new StringBuffer("octal"));
			if (s != null && !s.trim().startsWith("\'")) {
				s = "\'" + s;
			}
			if (s != null && !s.trim().endsWith("\'")) {
				s = s + "\'";
			}
			sb.append("nonascii");

			return s;
		} else if (c > 127) {
			java.lang.String s = Util.formatForUTF("" + c, "char",
					new StringBuffer("UTF"));
			sb.append("nonascii");
			if (s != null && !s.trim().startsWith("\'")) {
				s = "\'" + s;
			}
			if (s != null && !s.trim().endsWith("\'")) {
				s = s + "\'";
			}
			// s+=" //"+c;
			return s;
		} else
			return "" + c;
	}

	public static boolean shouldValueBeFormattedForNonAscii(
			java.lang.String value, java.lang.String dataType, StringBuffer tp) {
		
		if (dataType.equals("char") || dataType.equals("String")) {
			if (dataType.equals("char")) {

			} else {

				for (int k = 0; k < value.length(); k++) {
					char c1 = value.charAt(k);
					if (c1 < 32 || c1 >= 127) {
						if (c1 != '\t' && c1 != '\n' && c1 != '\r'
								&& c1 != '\\' && c1 != '\"' && c1 != '\'') {
							if (c1 < 32)
								tp.append("octal");
							if (c1 > 127)
								tp.append("UTF");
							return true;
						}
					}

				}
			}
		}
		return false;
	}

}