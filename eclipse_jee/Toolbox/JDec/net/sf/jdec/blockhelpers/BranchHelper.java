/*
 *  BranchHelper.java Copyright (c) 2006,07 Swaroop Belur
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
import net.sf.jdec.core.DecompilerHelper;
import net.sf.jdec.core.GlobalVariableStore;
import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.core.ShortcutAnalyser;
import net.sf.jdec.core.DecompilerHelper.BranchLabel;
import net.sf.jdec.lookup.FinderFactory;
import net.sf.jdec.lookup.IFinder;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.ExecutionState;

import java.util.*;

/*******************************************************************************
 * Helper functions for loops/switches
 * 
 * @author sbelur
 * 
 */
public class BranchHelper {

	public static boolean checkForInvalidDefault(int defStart, int switchStart) {
		Behaviour b = ExecutionState.getMethodContext();
		Set ifs = b.getMethodIfs();
		if (ifs == null)
			return false;
		Iterator it = ifs.iterator();
		while (it.hasNext()) {
			IFBlock ifb = (IFBlock) it.next();
			if (ifb.getIfStart() == defStart) {
				int ifj = getGenericFinder().getJumpAddress(ifb.getIfStart());
				Loop someloop = LoopHelper.getLoopGivenStart(ifj);
				if (someloop != null) {
					if (ifj < switchStart) {
						return true;
					}
				}
			} else if (ifb.getIfCloseLineNumber() == defStart) {
				int ifsstart = ifb.getIfStart();
				if (ifsstart < switchStart) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean checkForReturn(byte[] code, int i) {
		boolean present = false;
		int jvmInst = code[i];
		boolean b = getGenericFinder().isThisInstrStart(i);
		if (b == false)
			return false;
		switch (jvmInst) {
		case JvmOpCodes.ARETURN:
		case JvmOpCodes.IRETURN:
		case JvmOpCodes.FRETURN:
		case JvmOpCodes.DRETURN:
		case JvmOpCodes.LRETURN:
		case JvmOpCodes.RETURN:
			present = true;
			break;
		default:
			present = false;
			break;
		}
		return present;
	}

	private static Behaviour getContext() {
		return ExecutionState.getMethodContext();
	}

	private static IFinder getGenericFinder() {
		return FinderFactory.getFinder(IFinder.BASE);
	}

	private static IFinder getBranchFinder() {
		return FinderFactory.getFinder(IFinder.BRANCH);
	}

	public static int getReqdGoto(int start, byte[] info, int end) {
		int x = -1;
		for (int s = (start + 3); s <= end; s++) {
			int cur = info[s];
			if (cur == JvmOpCodes.GOTO) {
				x = s;
			}// break;}
		}
		boolean b = getGenericFinder().isThisInstrStart(x);
		if (b)
			return x;
		else
			return -1;
	}

	public static java.lang.String getBranchTypeAtI(int i, IFBlock ifst,
			StringBuffer sb) {
		boolean skip = skipBranchCheck(i);
		if (skip)
			return "";
		Iterator it = GlobalVariableStore.getBranchLabels().entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry) it.next();
			int end = ((Integer) (e.getValue())).intValue();
			if (end == i) {
				DecompilerHelper.BranchLabel b = (DecompilerHelper.BranchLabel) e
						.getKey();
				IFBlock IF = b.getIF();
				if (IF == ifst) {

					if (b.getLBL().trim().length() > 0) {
						GlobalVariableStore.getLabelAssociated().put(
								new Integer(i), "true");
					}
					sb.append(b.getBrlbl());
					return b.getLBL();
				}
			}

		}

		return "";
	}

	private static boolean skipBranchCheck(int i) {
		boolean b = false;
		Iterator it = GlobalVariableStore.getLabelAssociated().entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry) it.next();
			java.lang.String skip = ((java.lang.String) (e.getValue()));
			int end = ((Integer) e.getKey()).intValue();
			if (i == end && skip.equals("true"))
				b = true;
		}
		return b;
	}

	public static java.lang.String getBranchType(int if_else_begin,
			int GotoStart, byte[] info, ArrayList loops, StringBuffer sb) {
		boolean over = false;
		java.lang.String lbl = "";
		int immGotoJmp = getGenericFinder().getJumpAddress(GotoStart);
		int i = immGotoJmp;
		boolean cont = continueFindingBranchType(immGotoJmp, info);

		// check whether immGotoJmp is loop start
		int loop_end = LoopHelper.getloopEndForStart(loops, immGotoJmp);
		boolean done = false;
		if (loop_end != -1) {
			// Some loop is present

			Object[] sortedLoops = LoopHelper.sortLoops(loops);
			int parentLoopStart = LoopHelper.getParentLoopStartForIf(
					sortedLoops, if_else_begin);
			if (parentLoopStart == immGotoJmp) {
				java.lang.String s = setJdecLabelForContinue(parentLoopStart,
						loop_end);
				sb.append(s);
				lbl = "continue";
				GlobalVariableStore.getContinue_JumpOffsets().add(
						new Integer(immGotoJmp));
				done = true;
			} else {
				int nextstart = LoopHelper.getNextLoopStart(immGotoJmp);
				if (nextstart != -1) {
					int loopend = LoopHelper.getloopEndForStart(loops,
							nextstart);
					if (loopend != -1) {
						if (if_else_begin > loopend && nextstart < GotoStart) {
							sb.append("jdecLABEL" + immGotoJmp);
							lbl = "continue ";
							over = true;
							GlobalVariableStore.getLABELS().put(
									new Integer(immGotoJmp),
									"jdecLABEL" + immGotoJmp);
						}
					}
					if (over == false && nextstart < GotoStart) {
						GlobalVariableStore.getBreak_JumpOffsets().add(
								new Integer(nextstart));
						sb.append("jdecLABEL" + nextstart);
						GlobalVariableStore.getLABELS()
								.put(new Integer(nextstart),
										"jdecLABEL" + nextstart);
						lbl = "break";
					}
				}

				done = true;
			}

		}

		if (done == false) {
			while ((i < info.length))// && cont) // TODO: check why This was
			// needed
			{
				int current = info[i];
				if (current == JvmOpCodes.GOTO) {
					// BigInteger b;

					if (i > immGotoJmp && info[immGotoJmp] != JvmOpCodes.GOTO) {
						lbl = "";
						break;
					}
					boolean end = LoopHelper.isIndexEndOfLoop(loops, i);
					if (end) {
						int start = LoopHelper.getLoopStartForEnd(i, loops);
						if (start > if_else_begin) {
							return lbl;
						}
						Object[] sortedLoops = LoopHelper.sortLoops(loops);
						int parentLoopStart = LoopHelper
								.getParentLoopStartForIf(sortedLoops,
										if_else_begin);
						if (parentLoopStart == start) {
							java.lang.String s = setJdecLabelForContinue(
									parentLoopStart, i);
							sb.append(s);
							lbl = "continue";
							GlobalVariableStore.getContinue_JumpOffsets().add(
									new Integer(immGotoJmp));
							break;
						} else {
							int nextstart = LoopHelper.getNextLoopStart(start);
							if (nextstart != -1 && nextstart < GotoStart) {
								GlobalVariableStore.getBreak_JumpOffsets().add(
										new Integer(nextstart));
								sb.append("jdecLABEL" + nextstart);
								GlobalVariableStore.getLABELS().put(
										new Integer(nextstart),
										"jdecLABEL" + nextstart);
							}
							lbl = "break";
							break;
						}

					} else {
						lbl = "";
						break;
					}

				} else
					i++;

			}
		}

		return lbl;
	}

	public static java.lang.String setJdecLabelForContinue(int start, int end) {

		// Skip If IFBlock under examination is a Loop
		ArrayList loops = getContext().getBehaviourLoops();
		for (int s1 = 0; s1 < loops.size(); s1++) {
			Loop l = (Loop) loops.get(s1);
			int st = l.getStartIndex();
			int ed = l.getEndIndex();
			if (st == start && ed == end) {
				return "";
			}
		}

		byte[] info;
		if (getContext() != null) {
			info = getContext().getCode();
			int inst = info[start];
			int loadIndex = DecompilerHelper
					.getLoadInstIndex(inst, info, start);
			if (loadIndex == -1)
				return "";
			else {
				int pos = DecompilerHelper.getStoreInstPosInCode(info, end,
						loadIndex);
				if (pos != -1) {
					GlobalVariableStore.getLABELS().put(new Integer(pos),
							"jdecLABEL" + pos);
					return "jdecLABEL" + pos;
				}

			}
		}

		return "";
	}

	/***************************************************************************
	 * NOTE: This is not general purpose method tofind load inst...Skips
	 * ceratian loads see usages
	 */
	public static boolean continueFindingBranchType(int i, byte info[]) {
		// chkIndex is the index of the goto instruction.

		switch (info[i]) {

		case JvmOpCodes.ALOAD:

		case JvmOpCodes.ALOAD_0:

		case JvmOpCodes.ALOAD_1:

		case JvmOpCodes.ALOAD_2:

		case JvmOpCodes.ALOAD_3:

		case JvmOpCodes.DLOAD:

		case JvmOpCodes.DLOAD_0:

		case JvmOpCodes.DLOAD_1:

		case JvmOpCodes.DLOAD_2:

		case JvmOpCodes.DLOAD_3:

		case JvmOpCodes.FLOAD:

		case JvmOpCodes.FLOAD_0:

		case JvmOpCodes.FLOAD_1:

		case JvmOpCodes.FLOAD_2:

		case JvmOpCodes.FLOAD_3:

		case JvmOpCodes.ILOAD:

		case JvmOpCodes.ILOAD_0:

		case JvmOpCodes.ILOAD_1:

		case JvmOpCodes.ILOAD_2:

		case JvmOpCodes.ILOAD_3:

		case JvmOpCodes.LLOAD:

		case JvmOpCodes.LLOAD_0:

		case JvmOpCodes.LLOAD_1:

		case JvmOpCodes.LLOAD_2:

		case JvmOpCodes.LLOAD_3:
			return true;

		}

		return false;
	}

	public static int getSwitchOffset(byte[] info, int counter,
			java.lang.String lbl) {
		int b1 = info[++counter];
		int b2 = info[++counter];
		int b3 = info[++counter];
		int b4 = info[++counter];

		if (b1 < 0)
			b1 = (256 + b1);
		if (b2 < 0)
			b2 = (256 + b2);
		if (b3 < 0)
			b3 = (256 + b3);
		if (b4 < 0)
			b4 = (256 + b4);

		int jmp = (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
		if (jmp > 65535)
			jmp = jmp - 65536;
		if (lbl.equals("label"))
			return jmp;
		if (jmp < 0)
			jmp = 512 + jmp;
		return jmp;

	}

	public static void addBranchLabel(int classIndex, int i, IFBlock ifst,
			int currentForIndex, byte[] info) {

		boolean add = true;
		boolean continuetofind = false;
		if (getGenericFinder().isThisInstrStart(classIndex - 3)
				&& info[classIndex - 3] == JvmOpCodes.GOTO) // GOTO_W?
		{

			int resetVal = IFHelper
					.checkIfElseCloseNumber(classIndex - 3, ifst);
			ifst.setIfCloseLineNumber(resetVal);
		} else {

			int resetVal = IFHelper.checkIfElseCloseNumber(classIndex, ifst);
			ifst.setIfCloseLineNumber(resetVal);
		}

		// }

		// Added check here for detecting any other enclosing if whose end if
		// less than
		// current if close line number
		// problem arose in case of short cut if operators where parent if block
		// is not detected properly
		if (ifst.getIfCloseLineNumber() != -1) {
			int fromenclosing = IFHelper.checkForIfEndFromEnclosingSetIfs(info,
					ifst);
			if ((fromenclosing < ifst.getIfCloseLineNumber())
					&& fromenclosing != -1) {
				ifst.setIfCloseLineNumber(fromenclosing);
			}
		}

		int if_start = ifst.getIfStart();
		int if_end = ifst.getIfCloseLineNumber();
		if (if_end == -1 || if_end < if_start) {
			boolean b = false;
			int bytecodeend = ifst.getIfCloseFromByteCode();
			b = LoopHelper.isPositionALoopStart(getContext()
					.getBehaviourLoops(), bytecodeend);
			if (b) {
				int loopend = LoopHelper.getloopEndForStart(getContext()
						.getBehaviourLoops(), bytecodeend);
				if (loopend != -1) {
					ifst.setIfCloseLineNumber(loopend);
					// CHECK IF IFEND SET AS LOOP END IS SAME AS IF START
					if (ifst.getIfStart() == ifst.getIfCloseLineNumber()) {
						ifst.setIfCloseLineNumber(ifst.getIfStart() + 2 + 1);
					}
				}
			}
		}

		// Now check w.r.t do while kind of loops
		int dowhileloopstart = IFHelper
				.attachMarkerToIfInDoWhileKindOfLoop(ifst.getIfStart());
		if (dowhileloopstart != -1) {
			Loop someloop = LoopHelper.getLoopGivenStart(dowhileloopstart);
			if (someloop != null) {
				int newLE = ifst.getIfStart() + 2 + 1;
				if (newLE > someloop.getStartIndex()) {
					ifst.setIfCloseLineNumber(ifst.getIfStart() + 2 + 1);
					someloop.setLoopendreset(true);
					someloop.setLoopendDueToReset(ifst.getIfStart() + 2 + 1);
				}
			}
		}

		int k = ifst.getIfCloseFromByteCode();
		k = k - 3;
		ShortcutAnalyser sanalyser = getContext().getShortCutAnalyser();
		if (getGenericFinder().isThisInstrStart(k) && k > ifst.getIfStart()) {
			boolean IF = getBranchFinder().isInstructionIF(k);
			int nextifjump = getGenericFinder().getJumpAddress(k);
			boolean ok = nextifjump > k;
			if (IF
					&& k != ifst.getIfStart()
					&& ok
					&& (sanalyser.getShortCutStoreGivenIfStart(ifst
							.getIfStart()) != null)) {
				ifst.setDonotclose(true);
			}
		}

		k = ifst.getIfCloseLineNumber();
		k = k - 3;
		if (getGenericFinder().isThisInstrStart(k)
				&& ifst.getIfCloseFromByteCode() < ifst.getIfStart()) {
			boolean IF = getBranchFinder().isInstructionIF(k);
			if (IF
					&& k != ifst.getIfStart()
					&& (sanalyser.getShortCutStoreGivenIfStart(ifst
							.getIfStart()) != null)) {
				ifst.setDonotclose(true);
			}
		}

		java.lang.String s = "";
		StringBuffer sb = new StringBuffer("");
		int gotos = -1;
		if (getGenericFinder().isThisInstrStart(currentForIndex + 3)
				&& info[currentForIndex + 3] == JvmOpCodes.GOTO) {
			s = getBranchType(currentForIndex, currentForIndex + 3, info,
					getContext().getBehaviourLoops(), sb, true);
			if (s.trim().length() == 0) {
				int end = LoopHelper
						.getClosestLoopEndForThisIf(currentForIndex,
								getContext().getBehaviourLoops(), info);
				if (end != -1) {
					s = "break";
				}

			}
			// TODO
			gotos = currentForIndex + 3;
		} else {
			int x = getReqdGoto(currentForIndex, info, ifst
					.getIfCloseLineNumber());
			if (x != -1) {
				s = getBranchType(currentForIndex, x, info, getContext()
						.getBehaviourLoops(), sb);
				gotos = x;
			}

		}
		if (gotos != -1) {
			// TODO: Skip if IFSTRUCTURE is a loop

			int gotoj = getGenericFinder().getJumpAddress(gotos);
			if (getGenericFinder().isThisInstrStart(gotoj)
					&& info[gotoj] == JvmOpCodes.RETURN) {
				Iterator it = GlobalVariableStore.getRetAtIfElseEnd().keySet()
						.iterator();
				int gtJump = -1;
				boolean skip = false;
				if (ifst.getIfCloseLineNumber() + 2 < info.length) {
					gtJump = getGenericFinder().getJumpAddress(
							ifst.getIfCloseLineNumber());

					while (it.hasNext()) {
						Integer ing = (Integer) it.next();
						int jp = -1;
						if (ing.intValue() + 2 < info.length) {
							jp = getGenericFinder().getJumpAddress(
									ing.intValue());
							if (jp == gtJump) {
								skip = true;
								break;
							}
						}
					}

				}
				if (!skip)
					GlobalVariableStore.getRetAtIfElseEnd().put(
							new Integer(ifst.getIfCloseLineNumber()), "return");
				add = false;
			}
		}
		if (s.length() == 0 && add) {

			ArrayList loops = getContext().getBehaviourLoops();
			for (int z = ifst.getIfStart() + 3; z < ifst.getIfCloseLineNumber(); z++) {
				int temp = info[z];
				if (getGenericFinder().isThisInstrStart(z)
						&& temp == JvmOpCodes.GOTO) {
					int jump = getGenericFinder().getJumpAddress(z);
					Object[] sortedLoops = LoopHelper.sortLoops(loops);
					int parentLoopStart = LoopHelper.getParentLoopStartForIf(
							sortedLoops, ifst.getIfStart());
					if (parentLoopStart != -1) {
						int end = LoopHelper.getloopEndForStart(loops,
								parentLoopStart);
						if (end < jump && ifst.getIfStart() > parentLoopStart
								&& ifst.getIfCloseLineNumber() < end) {
							s = "break";
							break;
						}
					}
				}
			}

		}
		if (s.length() == 0 && add && gotos != -1) {
			int y1 = gotos;
			int z1 = getGenericFinder().getJumpAddress(y1);
			ArrayList loops = getContext().getBehaviourLoops();
			Object[] sortedLoops = LoopHelper.sortLoops(loops);
			int parentLoopStart = LoopHelper.getParentLoopStartForIf(
					sortedLoops, ifst.getIfStart());
			if (parentLoopStart != -1) {
				int end = LoopHelper.getloopEndForStart(loops, parentLoopStart);
				if (end < z1 && ifst.getIfStart() > parentLoopStart
						&& ifst.getIfCloseLineNumber() < end) {
					s = "break";

				}
			}

		}

		if (s.trim().length() == 0) {

			// detect here w.r.t switch case blocks
			int byend = ifst.getIfCloseFromByteCode();
			byend = byend - 3;
			boolean st = getGenericFinder().isThisInstrStart(byend);
			if (st) {

				if (info[byend] == JvmOpCodes.GOTO) {

					int jump = getGenericFinder().getJumpAddress(byend);
					Case ecase = checkForEnclosingSwitchCase(info, ifst,
							currentForIndex);
					if (ecase != null) {
						int ecaseend = ecase.getCaseEnd();
						if (ecaseend < jump) {
							add = true;
							s = "break";
							sb = new StringBuffer("");
						}
					}
				}

			}
		}

		if (s.trim().length() == 0) {

			ArrayList loops = getContext().getBehaviourLoops();
			Object[] sortedLoops = LoopHelper.sortLoops(loops);
			int parentLoopStart = LoopHelper.getParentLoopStartForIf(
					sortedLoops, ifst.getIfStart());
			if (parentLoopStart != -1) {
				int loopEnd = LoopHelper.getloopEndForStart(loops,
						parentLoopStart);
				if (loopEnd == ifst.getIfStart()) {
					add = true;
					s = "break";
					sb = new StringBuffer("");
				}
			}

		}

		if (add)
			GlobalVariableStore.getBranchLabels().put(
					DecompilerHelper.newBranchLabel(ifst, s, sb.toString()),
					new Integer(ifst.getIfCloseLineNumber()));

		int ifby = ifst.getIfCloseFromByteCode();
		if (ifby == ifst.getIfCloseLineNumber()) {
			TryHelper.checkIFEndWRTExceptionTables(ifst);
		}

		// Finally just check if -3 is goto
		int currentIfEnd = ifst.getIfCloseLineNumber();
		if (currentIfEnd != -1) {
			int p = currentIfEnd - 3;
			boolean pst = getGenericFinder().isThisInstrStart(p);
			if (pst) {
				if (info[p] == JvmOpCodes.GOTO) {

					ifst.setIfCloseLineNumber(p);
					// update branchLabels if any
					Iterator entrys = GlobalVariableStore.getBranchLabels()
							.entrySet().iterator();
					DecompilerHelper.BranchLabel reqdbrl = null;
					while (entrys.hasNext()) {

						Map.Entry e = (Map.Entry) entrys.next();
						BranchLabel brl = (BranchLabel) e.getKey();
						Integer close = (Integer) e.getValue();
						IFBlock storedIf = brl.getIF();
						if (close != null && (close.intValue() == currentIfEnd)
								&& storedIf == ifst) {
							reqdbrl = brl;
							break;
						}
					}
					if (reqdbrl != null) {
						GlobalVariableStore.getBranchLabels().remove(reqdbrl);
						GlobalVariableStore.getBranchLabels().put(reqdbrl,
								new Integer(p));
					}

				}
			}

		}

	}

	public static java.lang.String getBranchType(int if_else_begin,
			int GotoStart, byte[] info, ArrayList loops, StringBuffer sb,
			boolean b) {
		java.lang.String lbl = "";
		boolean done = false;
		int immGotoJmp = getGenericFinder().getJumpAddress(GotoStart);
		int i = immGotoJmp;
		boolean end = LoopHelper.isIndexEndOfLoop(loops, GotoStart);
		int lend = -1;
		if (end)
			lend = GotoStart;

		if (!end) {
			StringBuffer S = new StringBuffer("");
			end = LoopHelper.checkForMatchingLoopAgain(loops, immGotoJmp, S);
			if (end)
				lend = Integer.parseInt(S.toString());
		}
		if (end) {
			int start = LoopHelper.getLoopStartForEnd(lend, loops);
			if (start > if_else_begin) {
				return lbl;
			}
			Object[] sortedLoops = LoopHelper.sortLoops(loops);
			int parentLoopStart = LoopHelper.getParentLoopStartForIf(
					sortedLoops, if_else_begin);
			if (parentLoopStart == start) {
				java.lang.String s = setJdecLabelForContinue(parentLoopStart,
						lend);
				sb.append(s);
				lbl = "continue";
				GlobalVariableStore.getContinue_JumpOffsets().add(
						new Integer(immGotoJmp));

			} else {
				int nextstart = LoopHelper.getNextLoopStart(start);
				if (nextstart != -1) {
					int loopend = LoopHelper.getloopEndForStart(loops,
							nextstart);
					if (loopend != -1) {
						if (if_else_begin > loopend && nextstart < GotoStart) {
							sb.append("jdecLABEL" + start);
							lbl = "continue";
							done = true;
							GlobalVariableStore.getLABELS().put(
									new Integer(start), "jdecLABEL" + start);
						}
					}
					if (done == false && (nextstart < GotoStart)) {
						GlobalVariableStore.getBreak_JumpOffsets().add(
								new Integer(nextstart));
						sb.append("jdecLABEL" + nextstart);
						GlobalVariableStore.getLABELS()
								.put(new Integer(nextstart),
										"jdecLABEL" + nextstart);
						lbl = "break";
					}
				}

			}

		} else {
			lbl = "";

		}
		return lbl;
	}

	public static Case checkForEnclosingSwitchCase(byte[] info, IFBlock ifst,
			int currentForIndex) {

		ArrayList switches = getContext().getAllSwitchBlks();
		if (switches == null || switches.size() == 0)
			return null;
		int ifstart = ifst.getIfStart();
		HashMap caseblk_cends = new HashMap();
		for (int z = 0; z < switches.size(); z++) {

			Switch s = (Switch) switches.get(z);
			ArrayList cases = s.getAllCases();

			for (int k = 0; k < cases.size(); k++) {

				Case c = (Case) cases.get(k);
				int cs = c.getCaseStart();
				int ce = c.getCaseEnd();
				if (ifstart > cs && ifstart < ce) {
					caseblk_cends.put(new Integer(cs), c);
				}

			}

		}
		if (caseblk_cends.size() > 0) {
			Set keys = caseblk_cends.keySet();
			Integer sortedkeys[] = (Integer[]) keys.toArray(new Integer[keys
					.size()]);
			Arrays.sort(sortedkeys);
			int len = sortedkeys.length;
			Integer in = sortedkeys[len - 1];
			return (Case) caseblk_cends.get(in);
		}

		return null;
	}

	public static boolean addReturnInIfBlock(int current, IFBlock ifb) {
		if (GlobalVariableStore.isAthrowseen()) {
			int athrowpos = GlobalVariableStore.getAthrowseenpos();
			if (athrowpos != -1) {
				if (current > athrowpos) {
					if (current <= ifb.getIfCloseLineNumber()) {
						if (current > ifb.getIfStart()
								&& athrowpos > ifb.getIfStart()) {
							return false;
						}
					}

				}
			}
		}
		return true;
	}

	public static boolean addReturnInIfBlock(int current) {

		if (GlobalVariableStore.isAthrowseen()) {
			int athrowpos = GlobalVariableStore.getAthrowseenpos();
			Set ifs = getContext().getMethodIfs();
			Iterator all = ifs.iterator();
			while (all.hasNext()) {
				IFBlock ifb = (IFBlock) all.next();
				if (athrowpos != -1) {
					if (current > athrowpos) {
						if (current <= ifb.getIfCloseLineNumber()) {
							if (current > ifb.getIfStart()
									&& athrowpos > ifb.getIfStart()) {
								return false;
							}
						}

					}
				}
			}
		}
		return true;
	}

}
