/*
 *  IFHelper.java Copyright (c) 2006,07 Swaroop Belur
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

package net.sf.jdec.blockhelpers;

import net.sf.jdec.blocks.IFBlock;
import net.sf.jdec.blocks.Loop;
import net.sf.jdec.blocks.Switch;
import net.sf.jdec.blocks.Switch.Case;
import net.sf.jdec.constantpool.ClassDescription;
import net.sf.jdec.constantpool.MethodRef;
import net.sf.jdec.core.*;
import net.sf.jdec.core.ShortcutAnalyser.Shortcutchain;
import net.sf.jdec.core.ShortcutAnalyser.Shortcutstore;
import net.sf.jdec.lookup.FinderFactory;
import net.sf.jdec.lookup.IFinder;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.ExecutionState;
import net.sf.jdec.util.Util;

import java.util.*;

/*******************************************************************************
 * Helper functions for if blocks TODO: move remn from Decompiler.java to here
 * 
 * @author sbelur
 * 
 */
public class IFHelper {

	private IFHelper() {
	}

	private static final IFinder genericFinder = FinderFactory
			.getFinder(IFinder.BASE);

	private static final IFHelper helper = new IFHelper();

	public static IFHelper getInstance() {
		return helper;
	}

	private static Collection curentIFS;

	public static int checkIfElseCloseNumber(int end, IFBlock ifs) {
		int ifend = -1;
		Object ifsSorted[] = sortIFStructures();
		IFBlock parent = getParentBlock(ifsSorted, ifs.getIfStart());
		if (parent == null) {

			int temp = DecompilerHelper.checkLoopsAndSwitchForIfEnd(end, ifs,
					getContext());
			if (temp != -1)
				ifend = temp;
			else
				ifend = end;
		} else {
			ifend = reEvaluateIFStart(ifs, ifsSorted, parent, end);
			int temp2 = DecompilerHelper.checkLoopsAndSwitchForIfEnd(ifend,
					ifs, getContext());
			if (temp2 < ifend && temp2 != -1) {
				ifend = temp2;
			}
		}
		return ifend;
	}

	public static Object[] sortIFStructures() {
		Collection curentIFS = getCurrentIFStructues();
		if (curentIFS != null && curentIFS.size() > 0) {
			Object o[] = curentIFS.toArray();
			Arrays.sort(o);
			return o;
		} else
			return null;
	}

	private static Collection getCurrentIFStructues() {

		return getContext().getMethodIfs();
	}

	private static Behaviour getContext() {
		return ExecutionState.getMethodContext();
	}

	public static IFBlock getParentBlock(Object o[], int startOfIf) {

		IFBlock parent = null;
		int reqdPos = -1;
		for (int s = 0; s < o.length; s++) {
			if (o[s] instanceof IFBlock) {
				IFBlock IF = (IFBlock) o[s];
				if (IF.getIfStart() == startOfIf) {
					recheck: while (true) {
						if (s > 0) {
							reqdPos = s - 1;
							IFBlock pif = (IFBlock) o[reqdPos];
							boolean n = isIFShortcutORComp(getContext()
									.getCode(), pif.getIfStart());
							if (n) {
								int first = getContext().getShortCutAnalyser()
										.getFirstIfInChain(pif.getIfStart());
								// Find the position in the array
								for (int p = o.length - 1; p >= 0; p--) {
									if (((IFBlock) o[p]).getIfStart() == first) {
										s = p;
										continue recheck;
									}
								}
								return null;
							}
							return pif;
						} else {
							return null;
						}
					}
				}

			} else {
				return null;
			}
		}
		return null;
	}

	public static boolean isIFShortcutORComp(byte[] info, int j) {

		boolean b = getContext().getShortCutAnalyser().isIFShortcutIfCondition(
				j);
		return b;

	}

	private static int reEvaluateIFStart(IFBlock ifs, Object[] ifsSorted,
			IFBlock parent, int currentEnd) {
		int ifend = -1;
		int parentEnd = parent.getIfCloseLineNumber();
		int thisStart = ifs.getIfStart();
		if (thisStart < parentEnd) {
			if (currentEnd > parentEnd) {
				// belurs:
				// Test case BigInteger.class
				/***************************************************************
				 * BasicallY this part of the code is not bug free. It assumes
				 * that if the parentIF is ending before this if end is ending
				 * then this ifend is wrong. Well, it worked for every case
				 * until BigInteger ws decompiled. So basically resetting parent
				 * if end to this if end if parent if end was not a GOTO
				 * instruction.
				 */
				// Test for The above case
				// found while testing XmlReader.class
				// Above Fix Affects XmlReader.class
				// SO test for bytecode ends for all ifs
				// if all are same dont go into IF
				boolean noif = false;
				if (ifs.getIfCloseFromByteCode() == parent
						.getIfCloseFromByteCode()) {
					noif = true;
				}

				// Update: Need to also check for throw statement
				boolean athrowfoundatparentend = false;
				if (getGenericFinder().isThisInstrStart(parentEnd - 1)
						&& getContext().getCode()[parentEnd - 1] == JvmOpCodes.ATHROW) {
					athrowfoundatparentend = true;
				}
				boolean returnfoundatparentend = false;
				if (BranchHelper.checkForReturn(getContext().getCode(),
						parentEnd - 1)) {
					returnfoundatparentend = true;
				}
				if (!returnfoundatparentend
						&& !athrowfoundatparentend
						&& noif == false
						&& (getContext().getCode()[parentEnd] != JvmOpCodes.GOTO)
						&& (getContext().getCode()[parentEnd] != JvmOpCodes.GOTO_W)) {
					ifend = currentEnd;
					parent.setIfCloseLineNumber(ifend);

				} else {

					ifend = parentEnd;
				}

			} else if (currentEnd < thisStart) {
				ifend = parentEnd;
			} else
				ifend = currentEnd;

		} // Need to handle thisif end inside parent's else end
		/*
		 * else if(thisStart > parentEnd) { boolean
		 * doesParentHaveElse=parent.isHasElse(); if(doesParentHaveElse) { int
		 * parentElseEnd=parent.getElseCloseLineNumber(); if(thisStart <
		 * parentElseEnd) { // Within parent else block if(currentEnd >
		 * parentElseEnd) { ifend=parentElseEnd; } else if(currentEnd <
		 * thisStart) { ifend=parentElseEnd; } else { ifend=currentEnd; }
		 * 
		 * BigInteger b; } } }
		 */
		else {

			IFBlock superparent = getParentBlock(ifsSorted, parent.getIfStart());
			if (superparent == null) {
				int temp = DecompilerHelper.checkLoopsAndSwitchForIfEnd(
						currentEnd, ifs, getContext());
				if (temp != -1)
					ifend = temp;
				else
					ifend = currentEnd;

			} else {
				int tmp = reEvaluateIFStart(ifs, ifsSorted, superparent,
						currentEnd);
				ifend = tmp;

			}

		}

		return ifend;

	}

	public static int resetEndofIFElseWRTSwitch(ArrayList switches,
			IFBlock anIf, int curend, int curStart, java.lang.String type) {
		int k = -1;
		ArrayList possibleCaseEnds = new ArrayList();
		ArrayList possibleCaseStarts = new ArrayList();
		for (int s = 0; s < switches.size(); s++) {

			Switch swblk = (Switch) switches.get(s);
			ArrayList cases = swblk.getAllCases();
			for (int c = 0; c < cases.size(); c++) {
				Case caseblk = (Case) cases.get(c);
				int end = caseblk.getCaseEnd();
				int ifelseStart = curStart;
				int ifelseEnd;
				if (type.equals("else"))
					ifelseEnd = anIf.getElseCloseLineNumber();
				else {
					ifelseEnd = curend;
					if (ifelseEnd == -1) {
						ifelseEnd = anIf.getIfCloseFromByteCode();
					}
				}
				if (end > ifelseStart && end < ifelseEnd) {
					possibleCaseEnds.add(new Integer(end));
					possibleCaseStarts.add(new Integer(caseblk.getCaseStart()));
				}

			}

		}
		int firststart = -1;
		if (possibleCaseEnds.size() > 0) {
			Integer inte[] = (Integer[]) possibleCaseEnds
					.toArray(new Integer[possibleCaseEnds.size()]);
			Arrays.sort(inte);
			k = inte[0].intValue();

			Integer ints[] = (Integer[]) possibleCaseStarts
					.toArray(new Integer[possibleCaseStarts.size()]);
			Arrays.sort(ints);
			firststart = ints[0].intValue();

		}

		if (firststart != -1 && firststart > anIf.getIfStart()) {
			k = -1;
		}

		return k;
	}

	public static boolean isNewEndValid(int newend, IFBlock ifs,
			java.lang.String type, int curend) {

		int end;
		if (type.equals("else"))
			end = ifs.getElseCloseLineNumber();
		else
			end = curend;
		if (newend == -1)
			return false;
		if (newend > end) {
			return false;
		}

		else
			return true;
	}

	public static int getIfCloseNumberForThisIF(byte[] info, int k) {
		IFBlock ifst = new IFBlock();
		ifst.setIfStart(k);
		ifst.setHasIfBeenGenerated(true);
		Behaviour behaviour = getContext();
		getContext().getMethodIfs().add(ifst);
		// addBranchLabel(classIndex,i,ifst,currentForIndex,info);
		int i = k;
		int classIndex = -1;//
		if ((i + 2) < info.length)
			classIndex = genericFinder.getJumpAddress(i);
		if (classIndex == -1)
			return -1;
		i++;
		i++;
		boolean continuetofind = false;
		if (classIndex < i) {
			ifst.setIfCloseLineNumber(LoopHelper.findCodeIndexFromInfiniteLoop(
					ifst, behaviour.getBehaviourLoops(), classIndex));
			if (ifst.getIfCloseLineNumber() == -1)
				continuetofind = true;
		}
		if (classIndex > i || continuetofind) {
			if (genericFinder.isThisInstrStart(classIndex - 3)
					&& info[classIndex - 3] == JvmOpCodes.GOTO) // GOTO_W?
			{

				int resetVal = IFHelper.checkIfElseCloseNumber(classIndex - 3,
						ifst);
				ifst.setIfCloseLineNumber(resetVal);
			} else {

				int resetVal = IFHelper
						.checkIfElseCloseNumber(classIndex, ifst);
				ifst.setIfCloseLineNumber(resetVal);
			}

		}

		int if_start = ifst.getIfStart();
		int if_end = ifst.getIfCloseLineNumber();
		if (if_end == -1 || if_end < if_start) {
			boolean b = false;
			int bytecodeend = ifst.getIfCloseFromByteCode();
			b = LoopHelper.isPositionALoopStart(behaviour.getBehaviourLoops(),
					bytecodeend);
			if (b) {
				int loopend = LoopHelper.getloopEndForStart(behaviour
						.getBehaviourLoops(), bytecodeend);
				if (loopend != -1) {
					ifst.setIfCloseLineNumber(loopend);
				}
			}
		}

		return ifst.getIfCloseLineNumber();
	}

	/***************************************************************************
	 * 
	 * @param parent
	 *            Immediate above IF instruction in code...MAY OR MAY NOT BE
	 *            PARENT
	 * @param ifstart
	 *            For the If Block for which we are checking end of else
	 * @param elseClose
	 *            The current end of else...for which we are checking
	 * @return
	 */

	public static int checkElseCloseLineNumber(Object ifsorted[],
			IFBlock parent, IFBlock currentIF, int ifstart, int elseClose,
			java.lang.StringBuffer needToCheck) {
		int elseToReturn = -1;
		int parentIFStart = parent.getIfStart();
		int parentIFEnd = parent.getIfCloseLineNumber();
		boolean doesParentHaveElse = parent.isHasElse();
		boolean needToFindSuperParent = false;
		int curifend = currentIF.getIfCloseLineNumber();

		// Check 1: Check whether currentIF lies withing the if of parent
		if (ifstart < parentIFEnd) // Yes it lies
		{
			if (elseClose > parentIFEnd) {
				elseToReturn = parentIFEnd;
				return elseToReturn;
			} else if (elseClose < curifend) {
				elseToReturn = parentIFEnd;
				return elseToReturn;
			} else {
				elseToReturn = elseClose; // Passes else was correct
				return elseToReturn;
			}

		}
		if (ifstart > parentIFEnd) {
			if (doesParentHaveElse) {
				int parentElseEnd = parent.getElseCloseLineNumber();
				if (currentIF.getIfCloseLineNumber() < parentElseEnd) // Lies
				// within
				// the
				// else
				// of
				// parent
				// IF
				{
					if (elseClose > parentElseEnd) {
						elseToReturn = parentElseEnd;
						return elseToReturn;
					} else if (elseClose < curifend) {
						elseToReturn = parentElseEnd;
						return elseToReturn;
					} else {
						elseToReturn = elseClose; // Passes else was correct
						return elseToReturn;
					}
				} else {
					needToFindSuperParent = true;
				}

			} else {
				needToFindSuperParent = true;
			}

		}
		if (needToFindSuperParent) {
			IFBlock superParent = getParentBlock(ifsorted, parentIFStart);
			int tmp;
			if (superParent != null) {
				StringBuffer t = new StringBuffer("false");
				tmp = checkElseCloseLineNumber(ifsorted, superParent,
						currentIF, ifstart, elseClose, t);
			} else {
				if (getContext().getBehaviourLoops().size() == 0)
					tmp = elseClose;
				else {
					tmp = elseClose;
					needToCheck.append("true");
				}

			}
			return tmp;
		} else {
			return elseClose; // Should Never come here
		}

	}

	public static int resetElseCloseNumber(ArrayList loopList, IFBlock ifs,
			int gotoStart) {

		if (loopList.size() > 0) {
			Object[] sortedLoops = LoopHelper.sortLoops(loopList);
			int parentLoopStart = LoopHelper.getParentLoopStartForIf(
					sortedLoops, ifs.getIfStart());
			int end = LoopHelper.getloopEndForStart(loopList, parentLoopStart);
			int elseEnd = ifs.getElseCloseLineNumber();
			if (elseEnd > end && end != -1 && parentLoopStart != -1)
				ifs.setElseCloseLineNumber(end);
			return ifs.getElseCloseLineNumber();
		} else
			return ifs.getElseCloseLineNumber();

	}

	public static int resetElseCloseNumber(int start, int end) {
		int newend = end;
		int t1 = start + 2;
		int t2 = end;
		for (int s = 0; s < GlobalVariableStore.getContinue_JumpOffsets()
				.size(); s++) {
			int i = ((Integer) GlobalVariableStore.getContinue_JumpOffsets()
					.get(s)).intValue();
			if ((i > t1) && (i < t2)) {
				newend = i;
				break;
			}
		}
		return newend;
	}

	public static Case isIFInCase(Behaviour behaviour, int currentForIndex,
			IFBlock ifs) {

		Case caseblk = null;
		boolean present = false;
		int ifstart = ifs.getIfStart();
		int ifend = ifs.getIfCloseLineNumber();
		ArrayList allswitches = behaviour.getAllSwitchBlks();
		for (int s = 0; s < allswitches.size(); s++) {

			ArrayList allcases = ((Switch) allswitches.get(s)).getAllCases();
			for (int k = 0; k < allcases.size(); k++) {
				Case cblk = (Case) allcases.get(k);
				int casestart = cblk.getCaseStart();
				int caseend = cblk.getCaseEnd();
				if (ifstart >= casestart && ifend < caseend) {

					present = true;
					caseblk = cblk;
					break;
				}

			}
			// System.out.println(caseblk);
			if (present)
				break;

		}

		return caseblk;

	}

	public static int getElseEndwrtcaseblk(Case caseblk, byte[] info, int start) {

		int end = -1;
		boolean found = false;
		int caseend = caseblk.getCaseEnd();
		for (int i = start; i <= caseend; i++) {
			int inst = info[i];
			if (getGenericFinder().isThisInstrStart(i)
					&& inst == JvmOpCodes.GOTO) {
				end = i;
				found = true;
				break;
			}

		}
		if (!found)
			end = caseend;
		return end;
	}

	private static IFinder getGenericFinder() {
		return FinderFactory.getFinder(IFinder.BASE);
	}

	private static IFinder getStoreFinder() {
		return FinderFactory.getFinder(IFinder.STORE);
	}

	private static IFinder getBranchFinder() {
		return FinderFactory.getFinder(IFinder.BRANCH);
	}

	public static boolean skipGeneratingElse(ArrayList gotos, ArrayList gotoj,
			int currentForIndex, IFBlock ifs) {
		boolean skip = false;
		int temp = currentForIndex + 3;
		for (int s = 0; s < gotoj.size(); s++) {

			int jump = ((Integer) gotoj.get(s)).intValue();
			if (jump == temp) {
				int corS = ((Integer) gotos.get(s)).intValue();
				if (corS > ifs.getIfStart()
						&& corS < ifs.getIfCloseLineNumber())
					return true;
			}

		}

		return skip;

	}

	public static int getElseCloseFromInRangeIfStructures(IFBlock ifs,
			int currentForIndex) {
		int i = -1;
		ArrayList ifbytecodestarts = new ArrayList();
		IFBlock inrangeifs[] = getInRangeIFS(ifs.getIfStart(), currentForIndex);
		if (inrangeifs != null) {
			for (int s = 0; s < inrangeifs.length; s++) {
				int j = inrangeifs[s].getIfCloseFromByteCode();
				if (j > ifs.getIfCloseFromByteCode()) {
					ifbytecodestarts.add(new Integer(j));
				}

			}
			if (ifbytecodestarts.size() > 0) {
				Integer ins[] = (Integer[]) ifbytecodestarts
						.toArray(new Integer[ifbytecodestarts.size()]);
				Arrays.sort(ins);
				return ins[0].intValue();
			} else
				return i;
		}
		return i;

	}

	private static IFBlock[] getInRangeIFS(int s, int e) {
		Collection ifs = getCurrentIFStructues();
		if (ifs == null || ifs.size() == 0)
			return null;
		ArrayList list = new ArrayList();
		int start = s + 2 + 1; // ifstart+skipbytes+1 --> start of next inst
		Iterator it = ifs.iterator();
		while (it.hasNext()) {
			IFBlock cur = (IFBlock) it.next();
			int ifstart = cur.getIfStart();
			if (ifstart >= start && ifstart < e) {
				list.add(cur);
			}
		}

		if (list.size() > 0)
			return (IFBlock[]) list.toArray(new IFBlock[list.size()]);
		else
			return null;
	}

	public static int checkElseCloseWRTAnyParentLoop(IFBlock ifs,
			int gotostart, byte[] info) {

		ArrayList allloops = getContext().getBehaviourLoops();
		if (allloops == null || allloops.size() == 0)
			return -1;
		int gotojump = getGenericFinder().getJumpAddress(gotostart);
		Object[] sortedLoops = LoopHelper.sortLoops(allloops);

		for (int k = 0; k < sortedLoops.length; k++) {
			Loop cur = (Loop) sortedLoops[k];
			if (cur.getStartIndex() == gotojump) {
				int parentLoopStart = LoopHelper.getParentLoopStartForIf(
						sortedLoops, ifs.getIfStart());
				if (parentLoopStart == gotojump) {
					int loopend = LoopHelper.getloopEndForStart(allloops,
							parentLoopStart);
					return loopend;

				}
			}
		}
		return -1;
	}

	public static boolean addElseStart(int i) {
		if (GlobalVariableStore.getElsestartadded().size() == 0) {
			return true;
		}
		for (int z = 0; z < GlobalVariableStore.getElsestartadded().size(); z++) {
			Integer in = (Integer) GlobalVariableStore.getElsestartadded().get(
					z);
			if (in.intValue() == i) {
				return false;
			}
		}
		return true;
	}

	public static boolean checkForInvalidElse(int current, IFBlock ifs,
			byte[] info) {
		boolean add = true;
		int ifstart = ifs.getIfStart();
		int ifjump = getGenericFinder().getJumpAddress(ifstart);
		int from = ifstart + 3;
		int till = current - 1;
		for (int z = from; z <= till; z++) {
			boolean isStart = getGenericFinder().isThisInstrStart(z);
			if (isStart) {
				boolean isif = getBranchFinder().isInstructionIF(z);
				if (isif) {
					int jump = getGenericFinder().getJumpAddress(z);
					if (jump == ifjump) {
						return false;
					}
				}
				if (info[z] == JvmOpCodes.GOTO) {
					int jump = getGenericFinder().getJumpAddress(z);
					if (jump == ifjump) {
						return false;
					}
				}
			}

		}

		return add;
	}

	public static IFBlock getParentElseBlockForThisElse(int cur, byte[] info) {

		IFBlock ifst = null;
		Iterator it = getContext().getMethodIfs().iterator();
		HashMap tempmap = new HashMap();
		while (it.hasNext()) {
			IFBlock temp = (IFBlock) it.next();
			boolean elseg = temp.hasMatchingElseBeenGenerated();
			if (elseg) {
				int elsec = temp.getElseCloseLineNumber();
				if (elsec != -1) {
					int ifclose = temp.getIfCloseLineNumber();
					if (ifclose != -1) {
						if (cur > ifclose && cur < elsec) {
							// Possible Else Block
							tempmap.put(new Integer(ifclose), temp);
						}
					}
				}
			}

		}

		// Return correct parent else block
		if (tempmap.size() > 0) {
			Set set = tempmap.keySet();
			Integer[] keys = (Integer[]) set.toArray(new Integer[set.size()]);
			Arrays.sort(keys);
			Integer in = keys[keys.length - 1];
			return (IFBlock) tempmap.get(in);
		}
		return null;
	}

	public static boolean checkForBreakInAssociatedIf(IFBlock ifs) {

		Iterator it = GlobalVariableStore.getBranchLabels().keySet().iterator();
		while (it.hasNext()) {

			DecompilerHelper.BranchLabel bl = (DecompilerHelper.BranchLabel) it
					.next();
			IFBlock storedif = bl.getIF();
			if (storedif == ifs) {
				java.lang.String lbl = bl.getLBL();
				if (lbl != null && lbl.trim().equals("break")) {
					return true;
				}
			}

		}
		return false;
	}

	public static int checkForIfEndFromEnclosingSetIfs(byte[] info, IFBlock ifst) {

		Iterator it = getContext().getMethodIfs().iterator();
		int ifstart = ifst.getIfStart();
		ArrayList list = new ArrayList();
		while (it.hasNext()) {
			IFBlock temp = (IFBlock) it.next();
			int tempc = temp.getIfCloseLineNumber();
			if (temp.getIfStart() < ifstart && ifstart < tempc
					&& !temp.getDonotclose()) {
				list.add(new Integer(tempc));
			}
		}
		if (list.size() > 0) {
			Integer in[] = (Integer[]) list.toArray(new Integer[list.size()]);
			Arrays.sort(in);
			Integer first = in[0];
			if (first.intValue() < ifst.getIfCloseLineNumber()) {
				return first.intValue();
			}
		}
		return -1;
	}

	public static boolean lastIFinShortCutChain(byte[] info, IFBlock ifst, int i) {

		return getContext().getShortCutAnalyser().isLastIfInChain(i);
	}

	public static boolean addCodeStatementWRTShortcutOR(IFBlock ifst,
			java.lang.String s, boolean print, java.lang.String ifw,
			boolean last, java.lang.String alt) {

		ShortcutAnalyser sanalyser = getContext().getShortCutAnalyser();
		Behaviour behavior = getContext();
		if (sanalyser == null)
			return true;
		byte[] info = getContext().getCode();
		boolean reverse = false;
		boolean add = true;
		boolean booleanAssignFound = GlobalVariableStore.isBooleanAssignFound();
		int currentForIndex = getCurrentForIndex();
		int current = currentForIndex;
		java.lang.String str = "\nif(";
		if (ifw.equals("while")) {
			str = "\nwhile(";
		}
		StringBuffer methodsc = GlobalVariableStore.getMethodsc();
		java.lang.String string = "";

		/*
		 * if(last){ if(isInstAnyCMPInst(code,current-1)==false) { string=alt; }
		 * else string=s; } else // belurs NOTE: Need to revert to alt if it is
		 * shortcut and '&&' NOT shortcut or '||' { int
		 * byjump=ifst.getIfCloseFromByteCode(); byjump=byjump-3; boolean
		 * isstart=isThisInstrStart(starts,byjump);
		 * 
		 * if(isstart) { boolean isif=isInstructionIF(info[byjump]); if(isif) {
		 * int from=ifst.getIfStart()+3; int to=byjump; for(int
		 * findex=from;findex < to;findex++) {
		 * isstart=isThisInstrStart(starts,findex); if(isstart) {
		 * isif=isInstructionIF(info[findex]); if(isif) { reverse=false; break; } } } } }
		 * if(!reverse) string=s; else alt=alt; }
		 */

		// Note: sbelur
		// Check for this type first
		// boolean b=(i > j) || (or &&) (k==2) type
		// Need to check this to as else was not coming in earlier
		// implementation
		// Shortcutchain
		// somechain=sanalyser.getShortcutChainGivenIf(currentForIndex);
		boolean booleanAssignFoundProcessed = doesBooleanAssignFoundExist(currentForIndex);
		if (booleanAssignFoundProcessed == false) {
			StringBuffer end = new StringBuffer();
			booleanAssignFound = checkForShortCutIfBooleanAssignment(ifst,
					info, end, methodsc);
			if (booleanAssignFound) {
				GlobalVariableStore.getBooleanAssignMap().put(
						new Integer(currentForIndex),
						new Integer(end.toString()));
			}
		}

		boolean andpresent = false;
		boolean stringset = false;
		boolean encounteredAndOrComp = GlobalVariableStore
				.isEncounteredAndOrComp();

		/*
		 * if(booleanAssignFound){
		 * IfPartOfShortCutChain=true;encounteredAndOrComp=true; }
		 */
		boolean IfPartOfShortCutChain = GlobalVariableStore
				.isIfPartOfShortCutChain();
		// boolean concatthisif = IfPartOfShortCutChain;
		// if(last) {
		// if(IfPartOfShortCutChain) {

		int ifend = ifst.getIfCloseFromByteCode();
		int ifstart = ifst.getIfStart();
		int nextstart = ifstart + 3;
		int pr = ifend - 3;
		boolean isstart = getGenericFinder().isThisInstrStart(pr);
		boolean isif;
		boolean lookforand = false;
		boolean initialBracketAdded = GlobalVariableStore
				.isInitialBracketAdded();
		OperandStack opStack = getContext().getOpStack();
		/*
		 * if(isstart) { isif=isInstructionIF(info[pr]); if(isif==false) {
		 * lookforand=true; } }
		 */
		/*
		 * if(lookforand) { int strt=nextstart; for(;nextstart < ifend
		 * ;nextstart++) { isstart=isThisInstrStart(starts,nextstart);
		 * if(isstart) { isif=isInstructionIF(info[nextstart]); if(isif) { int
		 * jump=getGenericFinder().getJumpAddress(nextstart); if(jump==ifend) {
		 * IfPartOfShortCutChain=true; andpresent=true; break; } else {
		 * IfPartOfShortCutChain=true; andpresent=false; break; } } else {
		 * if(info[nextstart]==JvmOpCodes.GOTO){ System.out.println("nextstart
		 * found"+nextstart+" ifend"+ifend+"start"+strt); andpresent=false;
		 * break; } } } } } else { StringBuffer pos=new StringBuffer();
		 * //boolean
		 * otherif=checkForAnyIFWithDifferentIndex(info,currentForIndex+3,pr+3,pos,starts);
		 * boolean
		 * otherif=checkForIfInRange(info,currentForIndex+3,pr,pos,starts);
		 * if(!otherif) { int nextj=getGenericFinder().getJumpAddress(pr)-3;
		 * boolean nextjisstart=isThisInstrStart(starts,nextj); boolean
		 * nextjisif=false; if(nextjisstart) {
		 * nextjisif=isInstructionIF(info[nextj]); boolean skip=false;
		 * if(nextj==currentForIndex) { skip=true; } if(!skip){
		 * otherif=checkForIfInRange(info,pr+3,nextj,new StringBuffer(),starts);
		 * if(nextjisif && !otherif) { IfPartOfShortCutChain=true;
		 * andpresent=true; stringset=true; } else { IfPartOfShortCutChain=true;
		 * andpresent=false; stringset=false; } } else {
		 * IfPartOfShortCutChain=false; andpresent=false; stringset=false; } } }
		 * else { boolean check=true; int nextifpos=-1; try {
		 * nextifpos=Integer.parseInt(pos.toString()); }
		 * catch(NumberFormatException ne){ check=false; }
		 * 
		 * if(check==false){ IfPartOfShortCutChain=true; andpresent=true;
		 * stringset=true; } else { int
		 * currentjump=getGenericFinder().getJumpAddress(currentForIndex); int
		 * nextifjump=getGenericFinder().getJumpAddress(nextifpos);
		 * if(nextifjump!=currentjump){ IfPartOfShortCutChain=true;
		 * andpresent=true; stringset=true; } else { IfPartOfShortCutChain=true;
		 * andpresent=false; stringset=false; } } } }
		 */

		// }
		// }
		/*
		 * if(last) { if(andpresent) string = string; // TODO: Revisit this else
		 * string = string; } else string = string;
		 */

		java.lang.String connector = sanalyser.getConnector(ifst.getIfStart());
		if (!connector.equals(""))
			connector = " " + connector;

		if (connector != null && connector.trim().equals(ShortcutAnalyser.AND)) {

			boolean reset = false;
			/**
			 * 1. Get the next if 2. Get all ifs before this if 3. Check if some
			 * before if is closing in between this if start && next if start 4.
			 * if 3 returns true then this if needs to be made a last if. 5. Get
			 * the chain for this if 6. Nullify the next chain for this chain.
			 * 7. Nullify the previous chain for next if.
			 */

			// Get Next If
			int nextifstart = getNextIfStart(ifst.getIfStart(), info);
			if (nextifstart != -1) {
				// 2 and 3
				boolean bl = checkForIfCloseInRange(ifst.getIfStart(),
						nextifstart);
				if (bl) {

					Shortcutstore store = sanalyser
							.getShortCutStoreGivenIfStart(ifst.getIfStart());
					if (store != null) {
						// Point 4
						store.setConnectortype("");
						store.makeStoreInEffective(true);
						// point 5
						Shortcutchain chain1 = sanalyser
								.getShortcutChainGivenIf(ifst.getIfStart());
						Shortcutchain chain2 = sanalyser
								.getShortcutChainGivenIf(nextifstart);

						// Point 6 and 7
						chain1.setNextChain(null);
						
						if(chain2 != null)
							chain2.setPrevChain(null);

						reset = true;
					}

				}

			}

			andpresent = true;
			if (reset)
				andpresent = false;
		} else if (connector != null
				&& connector.trim().equals(ShortcutAnalyser.OR)) {
			andpresent = false;
		}
		if (andpresent)
			reverse = true;
		else
			reverse = false;

		if (!reverse) {
			reverse = sanalyser.checkToReverseCondition(ifst.getIfStart());
		}
		last = sanalyser.isLastIfInChain(ifst.getIfStart());
		if (last)
			reverse = true;
		if (!reverse)
			string = s;
		else
			string = alt;

		boolean begin = sanalyser.beginGroup(ifst.getIfStart());
		if (begin) {
			string = " (" + string;
		}
		boolean end = sanalyser.endGroup(ifst.getIfStart());
		if (end) {
			string = string + ") ";
		}

		Shortcutchain someChain = sanalyser
				.getShortcutChainGivenIf(currentForIndex);
		if (IfPartOfShortCutChain == false && connector != null
				&& !connector.trim().equals(ShortcutAnalyser.NONE)) { // Prev
			// ->
			// encounteredOrComp

			int jump = getGenericFinder().getJumpAddress(current);
			int i = jump - 3;
			if ((jump > current && i > 0 && i != current
					&& getGenericFinder().isThisInstrStart(i) && getBranchFinder()
					.isInstructionIF(i))
					|| (booleanAssignFound)) {

				// boolean oktoopen=addOpenBracket(info,ifst);
				int jumpForNextIf = getGenericFinder().getJumpAddress(i);
				// if(jumpForNextIf > i) {
				// encounteredOrComp=true;
				if (!reverse) {

					initialBracketAdded = false;
					if (!booleanAssignFound)
						behavior.appendToBuffer(Util
								.formatDecompiledStatement(str + string
										+ " ||  "));
					else {
						Operand opt = DecompilerHelper.createOperand("("
								+ string + " || ");
						opStack.push(opt);
					}

				} else {

					initialBracketAdded = false;
					if (!booleanAssignFound)
						behavior.appendToBuffer(Util
								.formatDecompiledStatement(str + string
										+ " &&  "));
					else {
						Operand opt = DecompilerHelper.createOperand("("
								+ string + " &&  ");
						opStack.push(opt);
					}

				}
				ifst.setDonotclose(true);
				IfPartOfShortCutChain = true;
				encounteredAndOrComp = true;
				GlobalVariableStore
						.setEncounteredAndOrComp(encounteredAndOrComp);
				GlobalVariableStore.setMethodsc(methodsc);
				GlobalVariableStore.setBooleanAssignFound(booleanAssignFound);
				GlobalVariableStore
						.setIfPartOfShortCutChain(IfPartOfShortCutChain);
				GlobalVariableStore.setInitialBracketAdded(initialBracketAdded);

				return false;
				// }
				/*
				 * else return true;
				 */
			} else if (jump < current) {
				// boolean oktoopen=addOpenBracket(info,ifst);
				int close = ifst.getIfCloseLineNumber();
				int x = close - 3;
				if (x > 0) {// && getGenericFinder().isThisInstrStart(x) &&
							// getBranchFinder().isInstructionIF(x)) {
					// encounteredOrComp=true;
					if (!reverse) {

						initialBracketAdded = false;
						if (!booleanAssignFound)
							behavior.appendToBuffer(Util
									.formatDecompiledStatement(str + string
											+ " ||  "));
						else {
							Operand opt = DecompilerHelper.createOperand("("
									+ string + " ||  ");
							opStack.push(opt);
						}

					} else {

						initialBracketAdded = false;
						if (!booleanAssignFound)
							behavior.appendToBuffer(Util
									.formatDecompiledStatement(str + string
											+ " &&  "));
						else {
							Operand opt = DecompilerHelper.createOperand("("
									+ string + " &&  ");
							opStack.push(opt);

						}

					}
					ifst.setDonotclose(true);
					IfPartOfShortCutChain = true;
					encounteredAndOrComp = true;
					GlobalVariableStore
							.setEncounteredAndOrComp(encounteredAndOrComp);
					GlobalVariableStore.setMethodsc(methodsc);
					GlobalVariableStore
							.setBooleanAssignFound(booleanAssignFound);
					GlobalVariableStore
							.setIfPartOfShortCutChain(IfPartOfShortCutChain);
					GlobalVariableStore
							.setInitialBracketAdded(initialBracketAdded);

					return false;
				}
				/*
				 * else { return true; }
				 */
			}
			/*
			 * else { return true; }
			 */
			// Previous --> if(last && concatthisif && andpresent) {
			else if (andpresent) {
				// StringBuffer mif=new StringBuffer();
				// boolean
				// close=checkClosingBracketRuleForShortCutOp(currentForIndex,info,ifst.getIfCloseFromByteCode(),ifst.getIfStart(),mif);

				// Object
				// ob=openCloseBracketMap.get(""+ifst.getIfCloseFromByteCode());
				if (!booleanAssignFound) {
					behavior.appendToBuffer(Util.formatDecompiledStatement(str
							+ string + " &&  "));
				} else {

					Operand opt = DecompilerHelper.createOperand("(" + string
							+ " &&  ");
					opStack.push(opt);

					// Operand opt=opStack.peekTopOfStack();
					// opt.setOperandValue(opt.getOperandValue()+string+" && ");

				}

				ifst.setDonotclose(true);
				IfPartOfShortCutChain = true;
				encounteredAndOrComp = true;
				GlobalVariableStore
						.setEncounteredAndOrComp(encounteredAndOrComp);
				GlobalVariableStore.setMethodsc(methodsc);
				GlobalVariableStore.setBooleanAssignFound(booleanAssignFound);
				GlobalVariableStore
						.setIfPartOfShortCutChain(IfPartOfShortCutChain);
				GlobalVariableStore.setInitialBracketAdded(initialBracketAdded);

				return false;
			}
			// Seems to be wrong to put { in If block
			/*
			 * if(last && concatthisif && !andpresent) { // TODO: there might be
			 * close bracket issue here. // May be need to check whether the
			 * additional close was for the final close before start of {
			 * 
			 * Util.forceStartSpace=false; StringBuffer mif=new StringBuffer();
			 * //boolean
			 * close=checkClosingBracketRuleForShortCutOp(currentForIndex,info,ifst.getIfCloseFromByteCode(),ifst.getIfStart(),mif);
			 * if(!booleanAssignFound) codeStatements+=string+")"; // Adding
			 * close anyway else { Operand opt=opStack.peekTopOfStack();
			 * opt.setOperandValue(opt.getOperandValue()+string+")"); // Adding
			 * close anyway ifst.setDonotclose(true); }
			 * 
			 * Util.forceStartSpace=true; Util.forceTrimString=false;
			 * Util.forceNewLine=false; if(!booleanAssignFound)
			 * codeStatements+=Util.formatDecompiledStatement("\t\n{\n"); else {
			 * Operand opt=opStack.peekTopOfStack();
			 * opt.setOperandValue(opt.getOperandValue()+";\n");
			 * ifst.setDonotclose(true); }
			 * 
			 * Util.forceNewLine=true; Util.forceTrimString=true;
			 * IfPartOfShortCutChain=false; andpresent=false;
			 * concatthisif=false; return false; }
			 */
			GlobalVariableStore.setEncounteredAndOrComp(encounteredAndOrComp);
			GlobalVariableStore.setMethodsc(methodsc);
			GlobalVariableStore.setBooleanAssignFound(booleanAssignFound);
			GlobalVariableStore.setIfPartOfShortCutChain(IfPartOfShortCutChain);
			GlobalVariableStore.setInitialBracketAdded(initialBracketAdded);

			return true;

		} else if (IfPartOfShortCutChain == true
				&& (connector != null
						&& !connector.trim().equals(ShortcutAnalyser.NONE) || (sanalyser
						.isLastIfInChain(currentForIndex)
						&& connector != null && connector.trim().equals(
						ShortcutAnalyser.NONE)))) {// Continue

			// codes
			StringBuffer mif = new StringBuffer();
			/*
			 * boolean
			 * closeb=checkClosingBracketRuleForShortCutOp(currentForIndex,info,ifst.getIfCloseFromByteCode(),ifst.getIfStart(),mif);
			 * if(closeb==false) { boolean
			 * oktoopen=addOpenBracket(info,ifst,closeb,mif); if(oktoopen) {
			 * if(!booleanAssignFound) codeStatements+="("; else { Operand
			 * opt=opStack.peekTopOfStack();
			 * opt.setOperandValue(opt.getOperandValue()+"("); } } } else {
			 * Object
			 * ob=openCloseBracketMap.get(""+ifst.getIfCloseFromByteCode());
			 * if(ob!=null) string=string+") "; else string=string; }
			 */

			if (!booleanAssignFound)
				behavior.appendToBuffer(string);
			else {
				Operand opt = opStack.peekTopOfStack();
				if (opt != null) // Added check while testing
					// BigInteger.class [makePositive]
					opt.setOperandValue(opt.getOperandValue() + string);
				else
					behavior.appendToBuffer(string);
			}
			int jump = getGenericFinder().getJumpAddress(current);
			int i = jump - 3;

			if (i != current && jump > current && i > 0
					&& getGenericFinder().isThisInstrStart(i)
					&& getBranchFinder().isInstructionIF(i)
					&& connector != null
					&& !connector.trim().equals(ShortcutAnalyser.NONE)) {
				// IfPartOfShortCutChain=true;
				// codes
				if (!booleanAssignFound) {
					if (!reverse)
						behavior.appendToBuffer(" ||  ");
					else
						behavior.appendToBuffer(" &&  ");
				} else {
					if (!reverse) {
						Operand opt = opStack.peekTopOfStack();
						opt.setOperandValue(opt.getOperandValue() + " || ");
					} else {
						Operand opt = opStack.peekTopOfStack();
						opt.setOperandValue(opt.getOperandValue() + " && ");
					}
				}
				ifst.setDonotclose(true);

				IfPartOfShortCutChain = true;
				encounteredAndOrComp = true;
				GlobalVariableStore
						.setEncounteredAndOrComp(encounteredAndOrComp);
				GlobalVariableStore.setMethodsc(methodsc);
				GlobalVariableStore.setBooleanAssignFound(booleanAssignFound);
				GlobalVariableStore
						.setIfPartOfShortCutChain(IfPartOfShortCutChain);
				GlobalVariableStore.setInitialBracketAdded(initialBracketAdded);

				return false;
			} else if (jump < current) { // BigInteger
				int close = ifst.getIfCloseLineNumber();
				int x = close - 3;
				if (connector != null
						&& !connector.trim().equals(ShortcutAnalyser.NONE)
						&& x > 0 && getGenericFinder().isThisInstrStart(x)
						&& getBranchFinder().isInstructionIF(x)) {
					// encounteredOrComp=true;
					// codes
					if (!booleanAssignFound) {
						if (!reverse)
							behavior.appendToBuffer(" || ");
						else
							behavior.appendToBuffer(" && ");

					} else {
						if (!reverse) {
							Operand opt = opStack.peekTopOfStack();
							opt.setOperandValue(opt.getOperandValue() + " || ");
						} else {
							Operand opt = opStack.peekTopOfStack();
							opt.setOperandValue(opt.getOperandValue() + " && ");
						}
					}
					ifst.setDonotclose(true);

					IfPartOfShortCutChain = true;
					encounteredAndOrComp = true;
					GlobalVariableStore
							.setEncounteredAndOrComp(encounteredAndOrComp);
					GlobalVariableStore.setMethodsc(methodsc);
					GlobalVariableStore
							.setBooleanAssignFound(booleanAssignFound);
					GlobalVariableStore
							.setIfPartOfShortCutChain(IfPartOfShortCutChain);
					GlobalVariableStore
							.setInitialBracketAdded(initialBracketAdded);

					return false;
				} else {
					if (!last) {
						Util.forceTrimString = false;
						if (!booleanAssignFound)
							behavior.appendToBuffer(" " + connector + " ");
						else {
							Operand opt = opStack.peekTopOfStack();
							opt.setOperandValue(opt.getOperandValue() + " "
									+ connector + " ");
						}
						ifst.setDonotclose(true);
						Util.forceTrimString = true;
						IfPartOfShortCutChain = true;
						encounteredAndOrComp = true;

					} else {
						if (!booleanAssignFound)
							behavior.appendToBuffer(Util
									.formatDecompiledStatement(")\n{\n"));
						else {
							Operand opt = opStack.peekTopOfStack();
							if (methodsc.toString().equals("true")) {
								opt
										.setOperandValue(opt.getOperandValue()
												+ ")");
							} else {
								opt.setOperandValue(opt.getOperandValue()
										+ ");\n");
							}
							ifst.setDonotclose(true);
						}
						IfPartOfShortCutChain = false;
						encounteredAndOrComp = false;
					}
					GlobalVariableStore
							.setEncounteredAndOrComp(encounteredAndOrComp);
					GlobalVariableStore.setMethodsc(methodsc);
					GlobalVariableStore
							.setBooleanAssignFound(booleanAssignFound);
					GlobalVariableStore
							.setIfPartOfShortCutChain(IfPartOfShortCutChain);
					GlobalVariableStore
							.setInitialBracketAdded(initialBracketAdded);

					return false;
				}
			} else {
				if (!last) {
					Util.forceTrimString = false;
					if (!booleanAssignFound)
						behavior.appendToBuffer(" " + connector + " ");
					else {
						Operand opt = opStack.peekTopOfStack();
						opt.setOperandValue(opt.getOperandValue() + " "
								+ connector + " ");
					}
					ifst.setDonotclose(true);
					Util.forceTrimString = true;
					IfPartOfShortCutChain = true;
					encounteredAndOrComp = true;

				} else {
					Operand opt = opStack.peekTopOfStack();
					if (!booleanAssignFound || opt == null) {
						Util.forceTrimLines = false;
						behavior.appendToBuffer(")\n");
						behavior.appendToBuffer(Util
								.formatDecompiledStatement("\n{\n"));
						Util.forceTrimLines = true;

					} else {

						if (methodsc.toString().equals("true")) {
							opt.setOperandValue(opt.getOperandValue() + ")");
						} else {
							opt.setOperandValue(opt.getOperandValue() + ");\n");
						}
						ifst.setDonotclose(true);
					}// 123
					IfPartOfShortCutChain = false;
					encounteredAndOrComp = false;
				}
				GlobalVariableStore
						.setEncounteredAndOrComp(encounteredAndOrComp);
				GlobalVariableStore.setMethodsc(methodsc);
				GlobalVariableStore.setBooleanAssignFound(booleanAssignFound);
				GlobalVariableStore
						.setIfPartOfShortCutChain(IfPartOfShortCutChain);
				GlobalVariableStore.setInitialBracketAdded(initialBracketAdded);

				return false;
			}

		} else {
			GlobalVariableStore.setEncounteredAndOrComp(encounteredAndOrComp);
			GlobalVariableStore.setMethodsc(methodsc);
			GlobalVariableStore.setBooleanAssignFound(booleanAssignFound);
			GlobalVariableStore.setIfPartOfShortCutChain(IfPartOfShortCutChain);
			GlobalVariableStore.setInitialBracketAdded(initialBracketAdded);

			return true;
		}

	}

	private static int getCurrentForIndex() {
		return ExecutionState.getCurrentInstructionPosition();
	}

	public static boolean doesBooleanAssignFoundExist(int currentForIndex) {
		Set set = GlobalVariableStore.getBooleanAssignMap().entrySet();
		for (Iterator it = set.iterator(); it.hasNext();) {
			Map.Entry elem = (Map.Entry) it.next();
			Integer in1 = (Integer) elem.getKey();
			Integer in2 = (Integer) elem.getValue();
			if (currentForIndex >= in1.intValue()
					&& currentForIndex < in2.intValue()) {
				return true;
			}
		}
		return false;
	}

	private static boolean checkForShortCutIfBooleanAssignment(IFBlock ifst,
			byte[] info, StringBuffer end, StringBuffer msc) {

		int ifbyend = ifst.getIfCloseFromByteCode();
		ShortcutAnalyser sanalyser = getContext().getShortCutAnalyser();
		boolean islastif = false;
		int currentForIndex = getCurrentForIndex();
		// int inital=ifbyend
		while (ifbyend < info.length) {
			if (getGenericFinder().isThisInstrStart(ifbyend)
					&& info[ifbyend] == JvmOpCodes.ICONST_1) {
				int next = ifbyend + 1;
				if (getGenericFinder().isThisInstrStart(next)
						&& info[next] == JvmOpCodes.GOTO) {
					next = next + 3;
					if (getGenericFinder().isThisInstrStart(next)
							&& info[next] == JvmOpCodes.ICONST_0) {
						next = next + 1;
						StringBuffer sb = new StringBuffer("");
						ArrayList starts = getContext()
								.getInstructionStartPositions();
						boolean anyif = checkForIfInRange(info,
								currentForIndex + 3, ifbyend,
								new StringBuffer(), starts);
						if (!anyif)
							return false;
						if (getGenericFinder().isThisInstrStart(next)
								&& (getStoreFinder()
										.isThisInstructionIStoreInst(info,
												next, sb)
										|| (info[next] == JvmOpCodes.PUTFIELD) || (info[next] == JvmOpCodes.PUTSTATIC))) {

							try {
								/*
								 * int i=Integer.parseInt(sb.toString());
								 * boolean complex=false; if(i!=0 && i!=1 &&
								 * i!=2 && i!=3) complex=true; LocalVariable
								 * local=getLocalVariable(i,"store","int",complex,next);
								 */
								// if(local!=null &&
								// local.getDataType().equalsIgnoreCase("boolean")
								// ) {
								end.append("" + next);
								GlobalVariableStore
										.getSkipWRTbooleanShortcutAssignFound()
										.put(new Integer(ifbyend),
												new Integer(next));
								return true;
								// }
							} catch (Exception exp) {
								return false;
							}

						} else {
							if (getGenericFinder().isThisInstrStart(next)
									&& (!getStoreFinder()
											.isThisInstructionIStoreInst(info,
													next, sb)
											&& !(info[next] == JvmOpCodes.PUTFIELD) && !(info[next] == JvmOpCodes.PUTSTATIC))) {
								end.append("" + next);
								GlobalVariableStore
										.getSkipWRTbooleanShortcutAssignFound()
										.put(new Integer(ifbyend),
												new Integer(next));
								msc.append("true");
								return true;
							}

							return false;
						}

					} else {
						return false;
					}

				} else {
					return false;
				}
			} else if (info[ifbyend] == JvmOpCodes.ICONST_0) {

				int prev = ifbyend - 3;
				if (getGenericFinder().isThisInstrStart(prev)
						&& info[prev] == JvmOpCodes.GOTO) {
					prev = prev - 1;
					if (getGenericFinder().isThisInstrStart(prev)
							&& info[prev] == JvmOpCodes.ICONST_1) {
						int from = prev;
						int next = ifbyend + 1;
						StringBuffer sb = new StringBuffer("");
						ArrayList starts = getContext()
								.getInstructionStartPositions();
						boolean anyif = checkForIfInRange(info,
								currentForIndex + 3, prev, new StringBuffer(),
								starts);
						if (!anyif)
							return false;
						if (getGenericFinder().isThisInstrStart(next)
								&& (getStoreFinder()
										.isThisInstructionIStoreInst(info,
												next, sb)
										|| (info[next] == JvmOpCodes.PUTFIELD) || (info[next] == JvmOpCodes.PUTSTATIC))) {
							try {
								/*
								 * int i=Integer.parseInt(sb.toString());
								 * boolean complex=false; if(i!=0 && i!=1 &&
								 * i!=2 && i!=3) complex=true; LocalVariable
								 * local=getLocalVariable(i,"store","int",complex,next);
								 * //if(local!=null &&
								 * local.getDataType().equalsIgnoreCase("boolean") ) {
								 */
								GlobalVariableStore
										.getSkipWRTbooleanShortcutAssignFound()
										.put(new Integer(from),
												new Integer(next));
								end.append("" + next);
								return true;
								// }
							} catch (Exception exp) {
								return false;
							}
						} else {
							if (getGenericFinder().isThisInstrStart(next)
									&& (!getStoreFinder()
											.isThisInstructionIStoreInst(info,
													next, sb)
											&& !(info[next] == JvmOpCodes.PUTFIELD) && !(info[next] == JvmOpCodes.PUTSTATIC))) {
								GlobalVariableStore
										.getSkipWRTbooleanShortcutAssignFound()
										.put(new Integer(from),
												new Integer(next));
								end.append("" + next);
								msc.append("true");
								return true;
							}
							return false;
						}
					} else {
						return false;
					}
				} else {
					return false;
				}
			} else {
				int cur = ifbyend;
				boolean found = false;
				int s1 = cur - 1;
				int s2 = ifst.getIfStart();
				for (; s1 > s2; s1--) {
					boolean isB = getGenericFinder().isThisInstrStart(s1);
					if (isB) {
						boolean isIF = getBranchFinder().isInstructionIF(s1);
						if (isIF) {
							islastif = sanalyser.isLastIfInChain(s1);
							break;
						}
					}
				}
				if (islastif) {
					return false;
				}
				while (cur < info.length) {
					boolean isst = getGenericFinder().isThisInstrStart(cur);
					if (isst) {
						boolean isif = getBranchFinder().isInstructionIF(cur);
						if (isif) {
							ifbyend = getGenericFinder().getJumpAddress(cur);
							if (ifbyend < cur)
								return false; // to avoid infinite loop
							found = true;
							break;
						}
					}
					cur++;

				}
				if (found) {

				} else {
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @param info
	 * @param from
	 *            --> inclusive
	 * @param end
	 *            --> exclusive
	 * @param pos
	 *            --> if pos if any
	 * @return
	 */

	private static boolean checkForIfInRange(byte[] info, int from, int end,
			StringBuffer pos, ArrayList starts) {

		for (int z = from; z < end && z < info.length; z++) {
			boolean start = getGenericFinder().isThisInstrStart(z);
			if (start) {
				boolean isif = getBranchFinder().isInstructionIF(z);
				if (isif) {
					pos.append("" + z);
					return true;
				}

			}

		}
		return false;
	}

	private static int getNextIfStart(int x, byte[] code) {

		for (int z = x + 3; z < code.length; z++) {

			boolean b = getGenericFinder().isThisInstrStart(z);
			if (b) {
				b = getBranchFinder().isInstructionIF(z);
				if (b) {
					return z;
				}
			}
		}
		return -1;
	}

	public static boolean checkForIfCloseInRange(int firstif, int secif) {

		Collection c = getCurrentIFStructues();
		Iterator it = c.iterator();
		while (it.hasNext()) {
			IFBlock curif = (IFBlock) it.next();
			int s = curif.getIfStart();
			if (!curif.getDonotclose() && s != firstif && s != secif) {
				int ifclose = curif.getIfCloseLineNumber();
				if (s < firstif && ifclose > firstif && ifclose < secif) {
					return true;
				}
			}
		}
		return false;

	}

	public static int checkIFEndIfUnset(IFBlock ifst, byte[] info, int current) {
		int i = ifst.getIfCloseFromByteCode();

		int end = ifst.getIfCloseFromByteCode();
		boolean start = getGenericFinder().isThisInstrStart(end);
		if (start) {
			if (getBranchFinder().isInstReturnInst(info, end,
					new StringBuffer(""))) {
				return end;
			}
			if (info[end] == JvmOpCodes.ATHROW) {
				return end;
			}
		}
		int prev = getGenericFinder().getPrevStartOfInst(end);
		if (getBranchFinder()
				.isInstReturnInst(info, prev, new StringBuffer(""))) {
			return prev;
		}
		if (info[prev] == JvmOpCodes.ATHROW) {
			return prev;
		}

		return i;
	}

	public static boolean getIfinst(int start, byte[] info, int tillWhere) {
		boolean ok = false;
		for (int k = start; k < tillWhere; k++) {
			int current = info[k];
			ok = getBranchFinder().isNextInstructionIf(current);
			if (ok && getGenericFinder().isThisInstrStart(k))
				return ok;
		}
		return ok;
	}

	// pos-> first if in chain
	public static void registerElseBreakForIfChain(int pos) {
		ShortcutAnalyser sanalyser = getContext().getShortCutAnalyser();
		int reqd = sanalyser.getChainEnd(pos);
		ArrayList elsebreaksforifchain = GlobalVariableStore
				.getElsebreaksforifchain();
		if (reqd != -1) {
			if (!elsebreaksforifchain.contains(new Integer(reqd)))
				elsebreaksforifchain.add(new Integer(reqd));
		}
	}

	public static void checkForBooleanOperandValue(Operand needToCheck,
			Operand op1) {
		java.lang.String type = op1.getLocalVarType();
		java.lang.String type2 = op1.getClassType();
		if ((type != null && type.trim().equals("boolean"))
				|| (type2 != null && type2.trim().equals("boolean"))) {
			java.lang.String val = needToCheck.getOperandValue();
			if (val != null && val.trim().equals("1")) {
				needToCheck.setOperandValue("true");
			}
			if (val != null && val.trim().equals("0")) {
				needToCheck.setOperandValue("false");
			}

		}

	}

	public static boolean isIfForLoadClass(IFBlock ifst, byte[] info) {
		boolean p = true;
		OperandStack opStack = getContext().getOpStack();
		Map skipWRTClassLoadIf = GlobalVariableStore.getSkipWRTClassLoadIf();
		int by = ifst.getIfCloseFromByteCode();
		ClassDescription cd = getContext().getClassRef().getCd();
		int currentForIndex = getCurrentForIndex();

		if (by > ifst.getIfStart()) {
			int j = by - 3;

			if (getGenericFinder().isThisInstrStart(j)
					&& info[j] == JvmOpCodes.GOTO) {
				int k = getGenericFinder().getJumpAddress(j);
				if ((k - j) == 6) {
					int s = ifst.getIfStart();
					s = s + 3;
					if (info[s] == JvmOpCodes.LDC) {
						int d = (s + 1);
						int offset = info[d];
						if (offset < 0)
							offset += 256;
						net.sf.jdec.constantpool.CPString constString = cd
								.getStringsAtCPoolPosition(offset);
						if (constString == null) {
							return true;
						}
						java.lang.String stringLiteral = cd
								.getUTF8String(constString.getUtf8pointer());
						if (stringLiteral == null) {
							return true;
						}
						s = s + 2;
						if (getGenericFinder().isThisInstrStart(s)
								&& info[s] == JvmOpCodes.INVOKESTATIC) {
							int classIndex = getGenericFinder().getOffset(s);
							MethodRef mref = cd
									.getMethodRefAtCPoolPosition(classIndex);
							java.lang.String name = mref.getMethodName();
							if (name != null && name.indexOf("class$") != -1) {

								Operand op = new Operand();
								StringBuffer sb = new StringBuffer("");
								Util.checkForImport(stringLiteral, sb);
								java.lang.String classToLoad = sb.toString()
										+ ".class";
								boolean yes = false;// isIfPartOfTernaryIfCond(info,
								// currentForIndex);
								/*
								 * if(yes) {
								 * op.setOperandValue(opStack.getTopOfStack().getOperandValue()+classToLoad); }
								 * else
								 */
								op.setOperandValue(classToLoad);
								boolean terend = false;// isTernaryEnd(currentForIndex);
								if (terend) {
									op.setOperandValue(op.getOperandValue()
											+ ")");
								}

								opStack.push(op);
								op.setClassType("Class");
								int next = k + 1;

								if (info[k] == JvmOpCodes.DUP
										|| info[k] == JvmOpCodes.DUP2) {
									boolean store = getStoreFinder()
											.isStoreInst(next,
													getContext().getCode());
									if (store)
										k = next;
								}
								skipWRTClassLoadIf.put(new Integer(ifst
										.getIfStart() + 3), new Integer(k));
								return false;
							}
						} else
							return true;

					} else if (info[s] == JvmOpCodes.LDC_W) {
						// int d=(s+1);
						int offset = getGenericFinder().getOffset(s);
						// if(offset < 0)offset+=256;
						net.sf.jdec.constantpool.CPString constString = cd
								.getStringsAtCPoolPosition(offset);
						if (constString == null) {
							return true;
						}
						java.lang.String stringLiteral = cd
								.getUTF8String(constString.getUtf8pointer());
						if (stringLiteral == null) {
							return true;
						}
						s = s + 3;
						if (getGenericFinder().isThisInstrStart(s)
								&& info[s] == JvmOpCodes.INVOKESTATIC) {
							int classIndex = getGenericFinder().getOffset(s);
							MethodRef mref = cd
									.getMethodRefAtCPoolPosition(classIndex);
							java.lang.String name = mref.getMethodName();
							if (name != null && name.indexOf("class$") != -1) {

								Operand op = new Operand();
								StringBuffer sb = new StringBuffer("");
								Util.checkForImport(stringLiteral, sb);
								java.lang.String classToLoad = sb.toString()
										+ ".class";
								op.setOperandValue(classToLoad);
								opStack.push(op);
								op.setClassType("Class");
								skipWRTClassLoadIf.put(new Integer(ifst
										.getIfStart() + 3), new Integer(k));
								return false;
							}
						} else
							return true;

					} else {
						return true;

					}

				}

			}

		}

		return p;
	}

	public static void addGeneratedIfCode(int i,
			GeneratedIfTracker generatedIfTracker) {

		String name = generatedIfTracker.startsAtIndex(i);
		if (name != null) {
			getContext().appendToBuffer(
					Util.formatDecompiledStatement("\nif(" + name + ")\n{\n"));
		}
		int count = generatedIfTracker.getCountOfCloseIfsAt(i);
		for (int j = 1; j <= count; j++) {
			getContext().appendToBuffer(Util.formatDecompiledStatement("\n}"));
		}

	}

	public static List sortByToCloseOrder(Set set) {

		if (set == null)
			return new ArrayList();
		List list = new ArrayList(set);
		Collections.sort(list);
		Collections.reverse(list);
		return list;

	}

	/***************************************************************************
	 * 
	 * @param i =
	 *            expected else start
	 * @return
	 */
	public static IFBlock getAssociatedIfAt(int i) {
		Iterator iterator = ExecutionState.getMethodContext().getMethodIfs()
				.iterator();
		byte[] code = getContext().getCode();
		for (int start = i - 1; start >= 0; start--) {
			boolean isStart = getGenericFinder().isThisInstrStart(start);
			if (isStart) {
				while (iterator.hasNext()) {
					IFBlock ifb = (IFBlock) iterator.next();
					if (ifb.getIfStart() == start) {
						return ifb;
					}
				}
			}
		}
		return null;
	}

	public static int attachMarkerToIfInDoWhileKindOfLoop(int index) {
		int jumpIndex = getGenericFinder().getJumpAddress(index);
		if (jumpIndex != -1) {
			if (getGenericFinder().isThisInstrStart(jumpIndex)) {
				boolean loopStart = LoopHelper.isPositionALoopStart(
						getContext().getBehaviourLoops(), jumpIndex);
				if (loopStart) {
					int temp = jumpIndex - 3;
					if (getGenericFinder().isThisInstrStart(temp)) {
						if (getContext().getCode()[temp] == JvmOpCodes.GOTO) {
							int gotojump = getGotoJump(temp);
							if (gotojump != -1) {
								for (int z = gotojump; z <= index; z++) {
									if (getGenericFinder().isThisInstrStart(z)) {
										if (getBranchFinder()
												.isInstructionIF(z)) {
											if (z == index) {
												return jumpIndex;
											} else {
												return -1;
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
		return -1;

	}

	public static int getGotoJump(int index) {
		int i = index;
		int thisGotoJump = -1;
		byte[] info = getContext().getCode();
		int b1 = info[++i]; // TODO: Replace by jumpaddress
		int b2 = info[++i];
		int z;
		if (b1 < 0)
			b1 = (256 + b1);
		if (b2 < 0)
			b2 = (256 + b2);

		int indexInst = ((((b1 << 8) | b2)) + (i - 2));
		if (indexInst > 65535)
			indexInst = indexInst - 65536;
		if (indexInst < 0)
			indexInst = 256 + indexInst;
		thisGotoJump = indexInst;
		return thisGotoJump;
	}

	public static int getIfStartGivenEnd(int end) {
		Set ifs = getContext().getMethodIfs();
		Iterator iterator = ifs.iterator();
		while (iterator.hasNext()) {
			IFBlock ifb = (IFBlock) iterator.next();
			if (ifb.getIfCloseLineNumber() == end) {
				return ifb.getIfStart();
			}
		}
		return -1;
	}

	public static IFBlock getIfGivenStart(int start) {
		Set ifs = getContext().getMethodIfs();
		Iterator iterator = ifs.iterator();
		while (iterator.hasNext()) {
			IFBlock ifb = (IFBlock) iterator.next();
			if (ifb.getIfStart() == start) {
				return ifb;
			}
		}
		return null;
	}

	
	public static Loop getClashingLoopIfEndAtStartWRTALoop(IFBlock ifb) {
		if (ifb.getIfStart() == ifb.getIfCloseLineNumber()) {
			Loop loop = LoopHelper.getLoopGivenEnd(ifb.getIfStart());
			if (loop != null) {
				return loop;
			}

		}
		return null;
	}

}