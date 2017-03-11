/*
 *  FieldMember.java Copyright (c) 2006,07 Swaroop Belur 
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

package net.sf.jdec.reflection;

import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.ui.util.UIUtil;
import net.sf.jdec.commonutil.Objects;
import net.sf.jdec.config.Configuration;
import net.sf.jdec.constantpool.AbstractRuntimeAnnotation;
import net.sf.jdec.util.Constants;
import net.sf.jdec.util.Util;

public class FieldMember {
	private AbstractRuntimeAnnotation runtimeVisibleAnnotations;

	private AbstractRuntimeAnnotation runtimeInvisibleAnnotations;

	private String name;

	private String genericSignature;

	private String[] accessSpecifiers = null;

	private Object value = null;

	private String dataType = null;

	private String unparseddataType = null;

	private int dimension = -1;

	private String ArrayType = null;

	public AbstractRuntimeAnnotation getRuntimeInvisibleAnnotations() {
		return runtimeInvisibleAnnotations;
	}

	public void setRuntimeInvisibleAnnotations(
			AbstractRuntimeAnnotation runtimeInvisibleAnnotations) {
		this.runtimeInvisibleAnnotations = runtimeInvisibleAnnotations;
	}

	public java.lang.String getUnParsedDataType() {
		return unparseddataType;
	}

	public java.lang.String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAccessSpecifiers() {

		if (accessSpecifiers != null && accessSpecifiers.length > 0) {
			StringBuffer temp = new StringBuffer();
			temp.append("[");
			for (int i = 0; i < accessSpecifiers.length; i++) {
				temp.append(accessSpecifiers[i]);
				if (i != accessSpecifiers.length)
					temp.append("\t");
			}
			temp.append("]");
			return temp.toString();
		} else
			return "";
		// return "Default Package Access";

	}

	public void setAccessSpecifiers(String[] accessSpecifiers) {
		this.accessSpecifiers = accessSpecifiers;
	}

	public void setFieldValue(Object o) {
		this.value = o;
	}

	public Object getFieldValue() {
		if (value != null) {
			String nonascii = UIUtil.getUIUtil().getInterpretNonAscii();
			// Collections
			if (nonascii.equals("true")) {
				StringBuffer tp = new StringBuffer("");
				boolean sf = shouldValueBeFormattedForNonAscii(
						value.toString(), tp);
				if (sf) {
					value = formatForUTF(value.toString(), tp);
					String sv = value.toString();
					if (value != null) {
						sv = sv.trim();
						boolean q = sv.startsWith("\"");

						if (!q) {
							sv = "\"" + sv;
						}
						q = sv.endsWith("\"");
						if (!q) {
							sv = sv + "\"";
						}

						return sv;
					}
				}
				return value.toString();
			} else {
				String str1 = value.toString();
				StringBuffer sbf1 = new StringBuffer("");
				for (int z = 0; z < str1.length(); z++) {
					char c1 = str1.charAt(z);
					if (c1 >= 32 && c1 < 127) {
						sbf1.append(c1);
					} else {
						sbf1.append("?");
					}

				}
				String v1 = sbf1.toString();
				v1 = v1.trim();
				boolean q = v1.startsWith("\"");

				if (!q) {
					v1 = "\"" + v1;
				}
				q = v1.endsWith("\"");
				if (!q) {
					v1 = v1 + "\"";
				}

				return v1;

			}

		} else
			return null;
	}

	public String getDataType() {
		StringBuffer sb = new StringBuffer("");
		if (dataType.startsWith("L") && dataType.endsWith(";")) {
			dataType = dataType.substring(1);
			dataType = dataType.substring(0, dataType.lastIndexOf(";"));
		} else if (dataType.startsWith("L")) {
			dataType = dataType.substring(1);
		}
		if (dataType.indexOf(Constants.ISARRAY) != -1) {
			dataType = findDataType(dataType);
		} else {
			checkForImport(dataType, sb);
			dataType = findDataType(sb.toString());
		}
		return dataType;
	}

	/***************************************************************************
	 * NOTE: DO NOT USE FOR ARRAY TYPE INSTEAD : 1> CALL set setArrayType 2>
	 * Call setDimension
	 * 
	 * @param dataType
	 */

	public void setDataType(String dataType) {
		this.dataType = dataType;
		unparseddataType = dataType;
	}

	private String findDataType(String dataType) {

		java.lang.String org = dataType;
		boolean dotfound = false;
		if (org.indexOf(".") != -1) {
			dotfound = true;
		}
		if (dataType.equals(Constants.ISINT)) {
			return "int";
		} else if (dataType.equals(Constants.ISBOOLEAN)) {
			return "boolean";
		} else if (dataType.equals(Constants.ISBYTE)) {
			return "byte";
		} else if (dataType.equals(Constants.ISCHAR)) {
			return "char";
		} else if (dataType.equals(Constants.ISDOUBLE)) {
			return "double";
		} else if (dataType.equals(Constants.ISFLOAT)) {
			return "float";
		} else if (dataType.equals(Constants.ISLONG)) {
			return "long";
		} else if (dataType.equals(Constants.ISSHORT)) {
			return "short";
		} else if (dataType.indexOf(Constants.ISARRAY) != -1) {
			String temp = "";
			for (int i = 0; i < dimension; i++) {
				temp += "[]";
			}
			String arrayType = findArrayType(dataType);
			String datatypedesc = findDataType(arrayType);
			StringBuffer s3 = new StringBuffer("");
			checkForImport(datatypedesc, s3);
			return s3.toString() + "\t" + temp;
		} else if (dataType.indexOf(Constants.ISREFERENCE) != -1
				&& dataType.indexOf("/") != -1
				&& dataType.indexOf("/") > dataType
						.indexOf(Constants.ISREFERENCE)) {
			String classType = dataType.substring(1);
			int semicolon = classType.indexOf(";");
			if (semicolon != -1) {
				classType = classType.substring(0, semicolon);
			}
			classType = classType.replace('/', '.');
			return classType;
		} else if (dotfound) {
			int semicolon = dataType.indexOf(";");
			if (semicolon != -1) {
				dataType = dataType.substring(0, semicolon);
			}
			dataType = dataType.replace('/', '.');
			return dataType;
		} else {
			return dataType;// Constants.UNKNOWNTYPE;
		}
	} // BigInteger bg;

	public int getDimension() {
		return dimension;
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

	public String getArrayType() {
		return dataType;
	}

	/***************************************************************************
	 * Example Argument [[Ljava/lang/String
	 * 
	 * @param arrayType
	 */

	public void setArrayType(String arrayType) {
		dataType = arrayType;
		unparseddataType = arrayType;
	}

	private java.lang.String findArrayType(String dataType) {
		/*
		 * int bracket = -1; if(dataType.indexOf("[") == 0) bracket = 1; else {
		 * bracket = dataType.indexOf("["); } while(bracket!=-1) {
		 * dataType=dataType.substring(bracket+1); }
		 */

		return dataType.substring(dataType.lastIndexOf("[") + 1);
	}

	public String getUserFriendlyAccessSpecifiers() {

		if (accessSpecifiers != null && accessSpecifiers.length > 0) {
			StringBuffer temp = new StringBuffer();

			for (int i = 0; i < accessSpecifiers.length; i++) {
				temp.append(accessSpecifiers[i]);
				if (i != accessSpecifiers.length)
					temp.append("  ");
			}

			return temp.toString();
		} else
			return "";
		// return "Default Package Access";

	}

	private void checkForImport(java.lang.String input, StringBuffer sb) {

		if (input.indexOf(".") == -1 && input.indexOf("/") == -1) {
			sb.append(input);
			return;
		}
		if (Configuration.getShowImport().equalsIgnoreCase("false")) {

			sb.append(input);
			return;
		}
		if (Configuration.getShowImport().equalsIgnoreCase("true")) {
			java.lang.String simplename = "";
			java.lang.String fullName = input;
			int lastSlash = fullName.lastIndexOf("/");
			if (lastSlash == -1) {
				lastSlash = fullName.lastIndexOf(".");
			}
			if (lastSlash != -1) {
				simplename = fullName.substring(lastSlash + 1);
			} else
				simplename = fullName;
			fullName = fullName.replace('/', '.');
			ConsoleLauncher.addImportClass(fullName);
			sb.append(simplename);
			return;

		}
		// Default
		sb.append(input);
		return;

	}

	public static java.lang.String checkForUTF(char c) {
		if (c >= 127)
			return "UTF";
		else if (c < 32)
			return "octal";
		return "ascii";
	}

	private boolean shouldValueBeFormattedForNonAscii(String value,
			StringBuffer tp) {

		if (this.dataType.equals("char") || this.dataType.equals("String")) {
			if (this.dataType.equals("char")) {

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

	private java.lang.String formatForUTF(String value, StringBuffer tp) {
		String s = value;
		if (this.dataType.equals("char") || this.dataType.equals("String")) {
			String forcenonascii = UIUtil.getUIUtil().getForceNonAscii();
			if (this.dataType.equals("char")) {
				char c = (char) Integer.parseInt(value);
				java.lang.String charType = "";
				if (forcenonascii.equals("true")) {
					charType = tp.toString();// checkForUTF(ch);
				} else {
					charType = checkForUTF(c);
				}

				if (charType.equals("UTF")) {

					String hexrep = Integer.toHexString(c);
					int length = hexrep.length();
					int diff = 4 - length;
					String temp = "";
					for (int x = 1; x <= diff; x++) {
						temp += "0";
					}

					s = "\'" + "\\u" + temp + hexrep + "\'";

				} else if (charType.equals("octal")) {
					String octrep = Integer.toOctalString(c);
					int length = octrep.length();
					int diff = 3 - length;
					String temp = "";
					for (int x = 1; x <= diff; x++) {
						temp += "0";
					}
					s = temp + octrep;
				} else {

					s = value;
				}

			} else// String
			{
				StringBuffer sbf = new StringBuffer();
				for (int z = 0; z < value.length(); z++) {
					char ch = value.charAt(z);
					String str = "";

					str = Util.interpretCharForUTF(ch, tp);
					sbf.append(str);
				}
				// s="\""+sbf.toString()+"\"";
				s = sbf.toString();
			}
		}
		return s;
	}

	public AbstractRuntimeAnnotation getRuntimeVisibleAnnotations() {
		return runtimeVisibleAnnotations;
	}

	public void setRuntimeVisibleAnnotations(
			AbstractRuntimeAnnotation runtimeVisibleAnnotations) {
		this.runtimeVisibleAnnotations = runtimeVisibleAnnotations;
	}

	public String getGenericSignature() {
		return genericSignature;
	}

	public void setGenericSignature(String genericSignature) {
		this.genericSignature = genericSignature;
	}

	public String getGenericPartOfSignature(String signature) {
		int start = signature.indexOf("<");
		if(start != -1){
		int end = signature.lastIndexOf(">");
		if(end != -1){
		String genericPart = signature.substring(start, end + 1);
		genericPart = Objects.getGenericPartOfGenericSignature(genericPart);
		return genericPart;
		}
		}
		return "";
	}

}
