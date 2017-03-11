package net.sf.jdec.format;
/*
 *  Settings.java Copyright (c) 2006,07 Swaroop Belur
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
 * TODO: Store any config related to formatting over here.
 * Refer javadoc for Formatter
 * @author swaroop belur
 * @since 1.2.1
 */


public class Settings {
	
	public static boolean flowerBracketAtSameLine = true;
	
	public static boolean spaceAfteBlockKeyword = false; 
	
	public static boolean bracketsForCaseBlocks = true;

	public static boolean isBracketsForCaseBlocks() {
		return bracketsForCaseBlocks;
	}

	public static void setBracketsForCaseBlocks(boolean bracketsForCaseBlocks) {
		Settings.bracketsForCaseBlocks = bracketsForCaseBlocks;
	}

	public static boolean isFlowerBracketAtSameLine() {
		return flowerBracketAtSameLine;
	}

	public static void setFlowerBracketAtSameLine(boolean flowerBracketAtSameLine) {
		Settings.flowerBracketAtSameLine = flowerBracketAtSameLine;
	}

	public static boolean isSpaceAfteBlockKeyword() {
		return spaceAfteBlockKeyword;
	}

	public static void setSpaceAfteBlockKeyword(boolean spaceAfteBlockKeyword) {
		Settings.spaceAfteBlockKeyword = spaceAfteBlockKeyword;
	}
	
	
	
	
	
	
}