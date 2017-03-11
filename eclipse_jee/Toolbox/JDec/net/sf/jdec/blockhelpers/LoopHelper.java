package net.sf.jdec.blockhelpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.jdec.blocks.IFBlock;
import net.sf.jdec.blocks.Loop;
import net.sf.jdec.core.GlobalVariableStore;
import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.core.Decompiler.DowhileHelper;
import net.sf.jdec.lookup.FinderFactory;
import net.sf.jdec.lookup.IFinder;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.ExecutionState;

/*
 *  LoopHelper.java Copyright (c) 2006,07 Swaroop Belur 
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

public class LoopHelper {

	private LoopHelper() {
	}

	private static final LoopHelper helper = new LoopHelper();

	public static LoopHelper getInstance() {
		return helper;
	}

	public static boolean isPositionALoopStart(ArrayList loops, int index) {
		if (loops == null || loops.size() == 0)
			return false;
		for (int z = 0; z < loops.size(); z++) {
			Loop loop = (Loop) loops.get(z);
			int loopstart = loop.getStartIndex();
			if (loopstart == index) {
				return true;
			}
		}
		return false;
	}

	public static int getloopEndForStart(ArrayList list, int start) {
		for (int i = 0; i < list.size(); i++) {
			Loop l = (Loop) list.get(i);
			if (l.getStartIndex() == start) {
				return l.getEndIndex();
			}
		}
		return -1;

	}

	public static int findCodeIndexFromInfiniteLoop(IFBlock ifst,
			ArrayList LoopTable, int codeIndex) {
		Iterator iterInfLoop = LoopTable.iterator();
		int start = ifst.getIfStart();
		int loopstarts[] = new int[LoopTable.size()];
		int j = 0;
		boolean ok = false;
		while (iterInfLoop.hasNext()) {
			Loop iloop = (Loop) iterInfLoop.next();
			int lstart = iloop.getStartIndex();
			loopstarts[j] = lstart;
			j++;
			ok = true;
			/*
			 * if(iloop.getStartIndex() == codeIndex) { return
			 * iloop.getEndIndex(); }
			 */
		}
		if (ok) {
			for (int z = loopstarts.length - 1; z >= 0; z--) {

				if (loopstarts[z] < start) {
					int end = getloopEndForStart(LoopTable, loopstarts[z]);
					if (end < start)
						return -1;
					return end;
				}

			}
		}

		return -1;
	}

	public static Object[] sortLoops(ArrayList list) {
		Object o[] = list.toArray();
		Arrays.sort(o);
		return o;
	}

	public static int getParentLoopStartForIf(Object[] sortedloops, int ifbegin) {
		int reqdStart = -1;
		int max = -1;
		int pos = 0;
		int counter = sortedloops.length - 1;
		while (counter >= 0) {
			Object o = sortedloops[counter];
			if (o instanceof Loop) {
				Loop l = (Loop) o;
				int ls = l.getStartIndex();
				int thisLoopEnd = getloopEndForStart(getContext()
						.getBehaviourLoops(), ls);
				if (ls < ifbegin && !(ifbegin > thisLoopEnd)) {
					reqdStart = ls;
					break;
				}

			}
			counter--;
		}
		return reqdStart;

	}

	private static Behaviour getContext() {
		return ExecutionState.getMethodContext();
	}

	public static boolean isThisLoopEndAlso(ArrayList loops, int i, int ifstart) {
		for (int s = 0; s < loops.size(); s++) {
			Loop l = (Loop) loops.get(s);
			int lend = (l).getEndIndex();
			if (lend == i && ifstart > l.getStartIndex())
				return true;
		}
		return false;
	}

	public static int getNextLoopStart(int start) {
		int i = -1;
		ArrayList loops = getContext().getBehaviourLoops();
		for (int s = 0; s < loops.size(); s++) {
			Loop l = (Loop) loops.get(s);
			int next = s + 1;
			if (next < loops.size()) {
				Loop n = (Loop) loops.get(next);
				if (start == l.getStartIndex()) {
					return n.getStartIndex();
				}

			} else
				return i;
		}

		return i;
	}

	public static int getLoopStartForEnd(int s, ArrayList list) {
		for (int k = 0; k < list.size(); k++) {
			Loop l = (Loop) list.get(k);
			if (l.getEndIndex() == s)
				return l.getStartIndex();
		}
		return -1;
	}

	public static boolean isIndexEndOfLoop(ArrayList list, int s) {
		boolean ok = false;
		for (int st = 0; st < list.size(); st++) {
			if (((Loop) list.get(st)).getEndIndex() == s)
				return true;
		}
		return ok;
	}

	public static boolean checkForParentLoopForIF(IFBlock ifst) {
		ArrayList loops = getContext().getBehaviourLoops();
		if (loops != null) {
			Object[] sortedLoops = sortLoops(loops);
			int parentLoopStart = getParentLoopStartForIf(sortedLoops, ifst
					.getIfStart());
			if (parentLoopStart == -1) {
				return false;
			} else {
				int by = ifst.getIfCloseFromByteCode();
				Loop l = getParentLoopForIf(sortedLoops, ifst.getIfStart());
				if (l != null) {
					int le = l.getEndIndex();
					if (by > ifst.getIfStart() && by < le) {
						return false;
					}
					if (by < ifst.getIfStart() && by == l.getStartIndex()) {
						return false;
					}
					if (by > ifst.getIfStart() && by > le) {
						return true;
					}

				}

			}
		}
		return false;
	}

	public static Loop getParentLoopForIf(Object[] sortedloops, int ifbegin) {
		int reqdStart = -1;
		Loop reqdl = null;
		int max = -1;
		int pos = 0;
		int counter = sortedloops.length - 1;
		while (counter >= 0) {
			Object o = sortedloops[counter];
			if (o instanceof Loop) {
				Loop l = (Loop) o;
				int ls = l.getStartIndex();
				int thisLoopEnd = getloopEndForStart(getContext()
						.getBehaviourLoops(), ls);
				if (ls < ifbegin && !(ifbegin > thisLoopEnd)) {
					reqdStart = ls;
					reqdl = l;
					break;
				}

			}
			counter--;
		}
		return reqdl;

	}

	public static boolean checkForMatchingLoopAgain(ArrayList loops, int start,
			StringBuffer S) {
		boolean b = false;
		if (loops == null || loops.size() == 0)
			return b;
		else {
			for (int s = 0; s < loops.size(); s++) {
				Loop l = (Loop) loops.get(s);
				int loopstart = l.getStartIndex();
				if (loopstart == start) {
					b = true;
					S.append(l.getEndIndex());
					return b;
				}

			}
		}
		return b;
	}

	public static int getClosestLoopEndForThisIf(int s, ArrayList loops,
			byte[] info) {

		int end = -1;
		if (loops == null || loops.size() == 0)
			return end;
		int gotos = s + 3;
		if (info[gotos] == JvmOpCodes.GOTO
				&& getGenericFinder().isThisInstrStart(gotos)) {
			int gotoj = getGenericFinder().getJumpAddress(gotos);
			int starts[] = new int[loops.size()];
			for (int z = 0; z < loops.size(); z++) {
				starts[z] = ((Loop) loops.get(z)).getEndIndex();
			}
			Arrays.sort(starts);
			int reqdloopend = -1;
			for (int x = 0; x < starts.length; x++) {
				int cur = starts[x];
				if (gotoj > cur) {
					reqdloopend = cur;
					break;
				}
			}

			if (reqdloopend != -1) {
				int lstart = getLoopStartForEnd(reqdloopend, loops);
				if (lstart < s) {
					return reqdloopend;
				}
			} else {
				return -1;
			}

		}

		return end;
	}

	private static IFinder getGenericFinder() {
		return FinderFactory.getFinder(IFinder.BASE);
	}

	public static boolean isBeyondLoop(int ifjump, ArrayList list, byte[] info) {
		boolean b = false;
		int temp = ifjump - 3;
		Loop l;
		int currentForIndex = getCurrentForIndex();
		Loop thisLoop = GlobalVariableStore.getThisLoop();
		boolean inststart = getGenericFinder().isThisInstrStart(temp);
		if (temp >= 0 && info[temp] == JvmOpCodes.GOTO && inststart) {

			int jmp = getGenericFinder().getJumpAddress(temp);
			boolean end;
			end = isIndexEndOfLoop(list, temp);
			if (end) {
				b = true;
				l = getThisLoop(list, temp);
				thisLoop = l;
				if (currentForIndex < thisLoop.getStartIndex()) {
					b = false;
				}
			}
			/*
			 * if(!b) { int tmp2=jmp-3; end=isIndexEndOfLoop(list,tmp2); if(end) {
			 * b=true;
			 * 
			 * l=getThisLoop(list,tmp2); thisLoop=l; } }
			 */

		}

		if (b == false) {
			int if_start = currentForIndex;
			for (int y = ifjump - 1; y > if_start; y--) {
				boolean atbegin = getGenericFinder().isThisInstrStart(y);
				if (atbegin) {
					Loop someloop = getLoopGivenEnd(y, list);
					if (someloop != null) {
						int someloopstart = someloop.getStartIndex();
						if (someloopstart < if_start) {
							b = true;
							l = someloop;
							thisLoop = l;
						}
					}
				}

			}
		}

		if (b == false) {

			for (int z = 0; z < list.size(); z++) {
				Loop loop = (Loop) list.get(z);
				int loops = loop.getStartIndex();
				int loopend = loop.getEndIndex();
				for (int x = loops + 1; x <= currentForIndex && x < loopend; x++) {
					boolean st = getGenericFinder().isThisInstrStart(x);
					if (st) {
						boolean isif = getBranchFinder().isInstructionIF(x);
						if (isif) {
							if (x == currentForIndex) {

								st = getGenericFinder().isThisInstrStart(
										loopend);
								if (st && (loopend > loops)) {
									boolean gotoinst = (info[loopend] == JvmOpCodes.GOTO);
									if (gotoinst) {
										thisLoop = loop;
										b = true;
									}
								}
							}
						}
					}
				}

			}
		}
		GlobalVariableStore.setThisLoop(thisLoop);
		return b;

	}

	public static Loop getThisLoop(ArrayList list, int s) {
		Loop l;
		for (int st = 0; st < list.size(); st++) {
			Loop tmp = (Loop) list.get(st);
			if (tmp.getEndIndex() == s)
				return tmp;
		}
		return null;
	}

	private static int getCurrentForIndex() {
		return ExecutionState.getCurrentInstructionPosition();
	}

	private static Loop getLoopGivenEnd(int end, ArrayList loops) {
		for (int z = 0; z < loops.size(); z++) {
			Loop l = (Loop) loops.get(z);
			if (l.getEndIndex() == end)
				return l;
		}
		return null;
	}

	public static Loop isIfInADoWhile(int current, IFBlock ifst, ArrayList loops) {

		Loop l = getLoopGivenEnd(current, loops);
		byte[] info = getContext().getCode();
		if (l != null) {
			boolean isif = getBranchFinder().isInstructionIF(current);
			if (isif && getGenericFinder().isThisInstrStart(current))
				return l;
		}
		return null;
	}

	public static IFinder getBranchFinder() {
		return FinderFactory.getFinder(IFinder.BRANCH);
	}

	public static boolean isIfFirstIfInLoopCondition(byte[] info, int pos) {

		Loop loop = getParentLoopForIf(sortLoops(getContext()
				.getBehaviourLoops()), pos);
		if (loop != null) {
			int start = loop.getStartIndex();
			for (int x = start; x < info.length; x++) {
				boolean b = getGenericFinder().isThisInstrStart(x);
				if (b) {
					boolean isif = getBranchFinder().isInstructionIF(x);
					if (isif) {
						if (x == pos) {
							return true;
						} else {
							return false;
						}
					}
				}
			}
		}

		return false;
	}

	public static boolean addReturnAtDoWhileEnd(int current, int loopend) {
		Loop l = getLoopGivenEnd(loopend, getContext().getBehaviourLoops());
		int ls = l.getStartIndex();
		return true;

	}

	public static java.lang.String closeDoWhile(int index,
			List doWhileHelperList) {
		int currentForIndex = getCurrentForIndex();
		Behaviour behavior = getContext();
		for (int z = 0; z < doWhileHelperList.size(); z++) {
			DowhileHelper dow = (DowhileHelper) doWhileHelperList.get(z);
			if (dow.getIndex() == index) {
				if (dow.getIsClosed() == false) {
					
					/*boolean addRet = addReturnAtDoWhileEnd(
							getCurrentForIndex(), (index - 3));
*/
					if (currentForIndex == (index - 3)) {
						if (!dow.getLoop().wasLoopClosedInCode()) {
							/*
							 * boolean childIfPresent =
							 * checkForAnyChildIFEndAtWhileEnd( index,
							 * dow.getLoop().getStartIndex());
							 * dow.getLoop().setWasLoopClosedInCode(true); if
							 * (!childIfPresent) behavior.appendToBuffer(Util
							 * .formatDecompiledStatement("\n}\n")); // Added //
							 * while // decompiling // BigIntger.class //
							 * smallPrime // Method else{
							 */
							if (!resetWrtIfAtEndOfDoWhile(dow)) {
								dow.setClosed(true);
								dow.getLoop().setWasLoopClosedInCode(true);
								return "\n}\n";
							}
							// }

						}
						continue;
					}
					/*
					 * behavior.appendToBuffer(Util
					 * .formatDecompiledStatement("break;\n")); //
					 * Util.forceNewLine=false; Util.forceTrimString = false;
					 * behavior.appendToBuffer(Util.formatDecompiledStatement("\n}//1\n"));
					 */
					if (!dow.getLoop().wasLoopClosedInCode()) {
						/*
						 * boolean childIfPresent =
						 * checkForAnyChildIFEndAtWhileEnd( index,
						 * dow.getLoop().getStartIndex());
						 * dow.getLoop().setWasLoopClosedInCode(true); if
						 * (!childIfPresent) { return "\n}\n"; } else {
						 * 
						 * 
						 */
						if (!resetWrtIfAtEndOfDoWhile(dow)) {
							dow.setClosed(true);
							dow.getLoop().setWasLoopClosedInCode(true);
							return "\n}\n";
						}
						// }
					}

				}
			}
		}

		return "";
	}

	private static boolean resetWrtIfAtEndOfDoWhile(DowhileHelper dow) {
		Set ifs = getContext().getMethodIfs();
		if (ifs != null) {
			Iterator it = ifs.iterator();
			while (it.hasNext()) {
				IFBlock ifb = (IFBlock) it.next();
				if (ifb.getIfStart() == dow.getLoop().getEndIndex()) {
					if (ifb.getIfCloseFromByteCode() == dow.getLoop()
							.getStartIndex()) {
						if (dow.getIndex() < ifb.getIfCloseLineNumber()) {
							dow.setIndex(ifb.getIfCloseLineNumber());
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public static boolean checkForAnyChildIFEndAtWhileEnd(int currentForIndex,
			int loopStart) {

		Iterator currentIFs = getContext().getMethodIfs().iterator();
		while (currentIFs.hasNext()) {

			IFBlock IF = (IFBlock) currentIFs.next();
			if (IF.getIfCloseLineNumber() == currentForIndex) {
				if (loopStart < IF.getIfStart()) {
					return true;
				}
			}
		}

		return false;
	}

	public static Loop getLoopGivenStart(int start) {

		Iterator currentLoops = getContext().getBehaviourLoops().iterator();
		while (currentLoops.hasNext()) {

			Loop l = (Loop) currentLoops.next();
			if (l.getStartIndex() == start) {
				return l;
			}
		}
		return null;
	}

	public static Loop getLoopGivenEnd(int end) {

		Iterator currentLoops = getContext().getBehaviourLoops().iterator();
		while (currentLoops.hasNext()) {

			Loop l = (Loop) currentLoops.next();
			if (l.getEndIndex() == end) {
				return l;
			}
		}
		return null;
	}

}