package net.sf.jdec.format;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;

import net.sf.jdec.main.ConsoleLauncher;

/*
 *  Formatter.java Copyright (c) 2006,07 Swaroop Belur 
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

/*******************************************************************************
 * 
 * TODO: Need to rewrite formatting
 * 
 * Aim of this class is to evolve the formatting logic present in 1.2 version to
 * an extent so that
 * 
 * these 2 basic requirements are satisified.
 * 
 * 1> No Hacks are required 2> No Mandatory newline characters at some
 * particular slots like before '{' and after '}'
 * 
 * If possible , allow the user to specify the positions of some key characters
 * like
 * 
 * 1> bracket { placement 2> space after if , while , switch 3> Ignoring { , }
 * for case blocks.
 * 
 * 
 * 
 * 
 * @author swaroop belur
 * 
 */
public class Formatter {

	private String unformattedInput = null;

	private String output;

	private int indentSpaces = 2;

	private int currentSpaces = 0;

	public Formatter(String input) {
		unformattedInput = input;
		init();
		applySettings();
		output = format();

	}

	public Formatter(File input) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(input)));

			StringBuffer buffer = new StringBuffer("");
			String temp = reader.readLine();
			while (temp != null) {
				buffer.append(temp);
				temp = reader.readLine();
			}
			unformattedInput = buffer.toString();
			init();
			applySettings();
			output = format();
			formatLicence();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"Could not read from input file");
			e.printStackTrace();
		}
	}

	private void formatLicence() {
		String licB = "// Decompiled by jdec";
		String licE = "// See the File 'COPYING' For more details.";
		int index = output.indexOf(licE);
		if (index != -1) {
			if ((index + licE.length()) < output.length()) {
				String lic = output.substring(0, index + licE.length());
				String rest = output.substring(index + licE.length());
				output = ConsoleLauncher.getLicenceWarning() + rest;
			}
		}
	}

	private boolean isNextCharCloseFlowerBracker(int pos, String input) {

		int k = pos + 1;
		if (k < input.length()) {
			for (int temp = k; temp < input.length(); temp++) {
				char ch = input.charAt(temp);
				if (ch == ' ') {
					continue;
				}
				if (ch == '}') {
					return true;
				} else {
					return false;
				}
			}

		}
		return false;
	}

	private boolean isNextSeqCLASSMarker(int pos, String input) {
		int k = pos + 1;
		if (k < input.length()) {
			for (int temp = k; temp < input.length(); temp++) {
				char ch = input.charAt(temp);
				if (ch == ' ') {
					continue;
				}
				if (ch == '\n') {
					continue;
				}
				if (ch == '/') {
					int k1 = temp + 1;
					if (k1 < input.length()) {
						char ch1 = input.charAt(k1);
						if (ch1 == '/') {
							k1 = k1 + 1;
							if (k1 < input.length()) {
								ch1 = input.charAt(k1);
								if (ch1 == ' ') {
									k1 = k1 + 1;
									if (k1 < input.length()) {
										ch1 = input.charAt(k1);
										if (ch1 == ' ') {
											boolean b = checkForClASSString(k1,
													input);
											if (b)
												return b;
										} else {
											boolean b = checkForClASSString(
													k1 - 1, input);
											if (b)
												return b;
										}
									}

								}
							}
						}
					}

				} else {
					return false;
				}
			}

		}
		return false;
	}

	private boolean checkForClASSString(int k1, String input) {
		k1 = k1 + 1;
		char ch1;
		if (k1 < input.length()) {
			ch1 = input.charAt(k1);
			if (ch1 == 'C') {
				k1 = k1 + 1;
				if (k1 < input.length()) {
					ch1 = input.charAt(k1);
					if (ch1 == 'L') {
						k1 = k1 + 1;
						if (k1 < input.length()) {
							ch1 = input.charAt(k1);
							if (ch1 == 'A') {
								k1 = k1 + 1;
								if (k1 < input.length()) {
									ch1 = input.charAt(k1);
									if (ch1 == 'S') {
										k1 = k1 + 1;
										if (k1 < input.length()) {
											ch1 = input.charAt(k1);
											if (ch1 == 'S') {
												k1 = k1 + 1;
												if (k1 < input.length()) {
													ch1 = input.charAt(k1);
													if (ch1 == ':') {
														return true;
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	private String format() {

		StringBuffer temp = new StringBuffer("");
		boolean addSpace = false;
		forloop: for (int i = 0; i < unformattedInput.length(); i++) {
			String clazzString = "";
			char ch = unformattedInput.charAt(i);
			if (ch == '{') {
				if (!Settings.flowerBracketAtSameLine) {
					for (int j = 1; j <= currentSpaces; j++) {
						temp.append(" ");
					}
					temp.append(" ");
				}

				temp.append(ch);
				temp.append("\n");
				boolean reduceSpace = isNextCharCloseFlowerBracker(i,
						unformattedInput);
				if (!reduceSpace) {
					currentSpaces = currentSpaces + indentSpaces;
				}
				addSpace = true;

			} else if (ch == '}') {
				boolean addLine = checkToAddNewLineForCLoseBracket(i,temp);
				if(addLine){
					temp.append("\n");
					if (currentSpaces > 0){
						int curCurrentSpaces=currentSpaces;
						currentSpaces = currentSpaces - indentSpaces;
						if(currentSpaces <= 0){
							currentSpaces = curCurrentSpaces;
						}
					}
					for (int j = 1; j <= currentSpaces; j++) {
						temp.append(" ");
					}
					temp.append(ch);
				}
				else{
					temp.append(ch);
				}
				temp.append("\n\n");
				addSpace = true;
				boolean reduceSpace = isNextCharCloseFlowerBracker(i,
						unformattedInput);
				if (reduceSpace) {
					if (currentSpaces > 0)
						currentSpaces = currentSpaces - indentSpaces;
				}

			} else if (ch == ';') {
				temp.append(ch);
				temp.append("\n");
				addSpace = true;
				boolean reduceSpace = isNextCharCloseFlowerBracker(i,
						unformattedInput);
				if (reduceSpace) {
					if (currentSpaces > 0)
						currentSpaces = currentSpaces - indentSpaces;
				}
				boolean isabstract = isNextSeqCLASSMarker(i, unformattedInput);
				if (isabstract) {
					temp.append("\n");
				}
			} else {
				if (addSpace) {
					for (int j = 1; j <= currentSpaces; j++) {
						temp.append(" ");
					}
					addSpace = false;
					clazzString = "";

				}
				clazzString += ch;
				if ((i + 5) < unformattedInput.length()) {
					if (ch == 'C' && unformattedInput.charAt((i + 1)) == 'L'
							&& unformattedInput.charAt((i + 2)) == 'A'
							&& unformattedInput.charAt((i + 3)) == 'S'
							&& unformattedInput.charAt((i + 4)) == 'S'
							&& unformattedInput.charAt((i + 5)) == ':') {
						i = i + 6;
						if (i < unformattedInput.length()) {
							temp.append("CLASS:");
							for (int u = i; u < unformattedInput.length(); u++) {
								if (unformattedInput.charAt(u) == ':') {
									temp.append(unformattedInput.charAt(u));
									temp.append("\n");
									i = u;
									addSpace = true;
									continue forloop;
								} else {
									temp.append(unformattedInput.charAt(u));
								}
							}
						}
					}
				}
				temp.append(ch);
			}

		}

		return temp.toString();
	}

	private boolean checkToAddNewLineForCLoseBracket(int i, StringBuffer temp) {
		String tempString = temp.toString();
		int from  = temp.length()-1;
		for(int j=from;j>=0;j--){
			char ch = tempString.charAt(j);
			if(ch == ' '){
				continue;
			}
			if(ch == '\n'){
				return false;
			}
			return true;
		}
		return false;
	}

	private void init() {
		unformattedInput = unformattedInput.replaceAll("\n", "");
		unformattedInput = unformattedInput.replaceAll("\\s+", " ");
		unformattedInput = unformattedInput.replaceAll("End of Import",
				"End of Import\n\n");
		unformattedInput = unformattedInput.replaceAll(
				"/\\*\\*\\* \\*\\*Class Fields \\*\\*\\*/",
				"/*** **Class Fields ***/\n");
		unformattedInput = unformattedInput.replaceAll(
				"/\\*\\*\\*\\* List of All Imported Classes \\*\\*\\*/",
				"\n/**** List of All Imported Classes ***/\n\n");
		unformattedInput = unformattedInput.replaceFirst(
				"See the File 'COPYING' For more details\\.",
				"See the File 'COPYING' For more details.\n\n\n");
		unformattedInput = unformattedInput.replaceAll(
				"//Beginning of Inner Class Content\\.\\.\\.",
				"//Beginning of Inner Class Content...\n");
		unformattedInput = unformattedInput.replaceAll(
				"//End Of a Inner Class File Content\\.\\.\\.",
				"//End Of a Inner Class File Content...\n");

	}

	private void applySettings() {
		if (Settings.flowerBracketAtSameLine) {
			unformattedInput = unformattedInput.replaceAll("\\{", "{");
		} else {
			unformattedInput = unformattedInput.replaceAll("\\{", "\n{");
		}
	}

	public static void main(String[] args) {
		new Formatter(new File("c:/out/org/apache/log4j/AsyncAppender.jdec"));
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

}