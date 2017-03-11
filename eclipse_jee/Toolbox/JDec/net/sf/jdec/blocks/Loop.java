/*
 *  Loop.java Copyright (c) 2006,07 Swaroop Belur
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

package net.sf.jdec.blocks;

import java.util.ArrayList;

public class Loop implements Comparable {
	private String indexVariable;

	private int startIndex;

	private int endIndex;

	private int noOfTimes;

	private boolean isDoWhile = false;

	private boolean isInfinite = true;

	private boolean wasLoopClosedInCode = false;

	private boolean printloopStart = true;

	private boolean loopendreset = false;// Used in do-while kind of loops at
											// the time of creation.

	private int loopendDueToReset = -1; // Used in do-while kind of loops at the
										// time of creation.

	public int getLoopendDueToReset() {
		return loopendDueToReset;
	}

	public void setLoopendDueToReset(int loopendDueToReset) {
		this.loopendDueToReset = loopendDueToReset;
	}

	public void setLoopendreset(boolean loopendreset) {
		this.loopendreset = loopendreset;
	}

	public boolean getLoopendreset() {
		return loopendreset;
	}

	public boolean printloopStart() {
		return printloopStart;
	}

	public void setPrintloopStart(boolean printloopStart) {
		this.printloopStart = printloopStart;
	}

	public int getLoopEndForBracket() {
		return loopEndForBracket;
	}

	public void setLoopEndForBracket(int loopEndForBracket) {
		this.loopEndForBracket = loopEndForBracket;
	}

	private int loopEndForBracket = -1;

	public int getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}

	public String getIndexVariable() {
		return indexVariable;
	}

	public void setIndexVariable(String indexVariable) {
		this.indexVariable = indexVariable;
	}

	public boolean isInfinite() {
		return isInfinite;
	}

	public void setInfinite(boolean isInfinite) {
		this.isInfinite = isInfinite;
	}

	public int getNoOfTimes() {
		return noOfTimes;
	}

	public void setNoOfTimes(int noOfTimes) {
		this.noOfTimes = noOfTimes;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public boolean equals(Object o) {
		if (o instanceof Loop) {
			Loop input = (Loop) o;
			int start = input.getStartIndex();
			int end = input.getEndIndex();
			boolean type = input.isInfinite();

			if (start == this.getStartIndex() && end == this.getEndIndex()
					&& type == this.isInfinite()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public int compareTo(Object o) {
		if (!(o instanceof Loop)) {
			return -1;
		} else {

			boolean equal = this.equals(o);
			if (equal)
				return 0;
			else if (this.getStartIndex() > ((Loop) o).getStartIndex())
				return 1;
			else
				return -1;
		}
	}

	public boolean wasLoopClosedInCode() {
		return wasLoopClosedInCode;
	}

	public void setWasLoopClosedInCode(boolean wasLoopClosedInCode) {
		this.wasLoopClosedInCode = wasLoopClosedInCode;
	}

	public static ArrayList removeDuplicates(ArrayList input) {
		ArrayList bkp = input;
		ArrayList updatedList = new ArrayList();
		boolean ok = false;

		if (input != null && input.size() > 0) {
			Loop[] fullList = (Loop[]) input.toArray(new Loop[] {});
			java.util.Arrays.sort(fullList);
			updatedList.add(fullList[0]);
			for (int s = 1; s < fullList.length; s++) {
				if (s < fullList.length) {
					Loop currentItem = fullList[s];
					int prev = s - 1;
					Loop temp = null;
					if (prev >= 0 && prev < fullList.length) {
						temp = fullList[prev];
						if (temp.equals(currentItem) == false) {
							updatedList.add(currentItem);
						} else {
							if (temp.getLoopEndForBracket() != currentItem
									.getLoopEndForBracket()) {
								updatedList.add(currentItem);
							}

						}
					}

				}

			}
			if (updatedList.size() > 0) {
				ArrayList updatedList2 = new ArrayList();
				updatedList2.add(updatedList.get(0));
				for (int s = 1; s < updatedList.size(); s++) {
					if (s < updatedList.size()) {
						Loop currentItem = (Loop)updatedList.get(s);
						int prev = s - 1;
						Loop temp = null;
						if (prev >= 0 && prev < updatedList.size()) {
							temp = (Loop)updatedList.get(prev);
							if (temp.equals(currentItem) == false) {
								updatedList2.add(currentItem);
							} else {
								if (temp.getLoopEndForBracket() < currentItem
										.getLoopEndForBracket()) {
									updatedList2.remove(temp);
									updatedList2.add(currentItem);
								}
								

							}
						}

					}

				}
				System.out.println();
				return updatedList2;
				
			}

		}

		return updatedList;

	}

	public boolean isDoWhile() {
		return isDoWhile;
	}

	public void setDoWhile(boolean isDoWhile) {
		this.isDoWhile = isDoWhile;
	}

}
