package net.sf.jdec.lookup;

import net.sf.jdec.blocks.IFBlock;
import net.sf.jdec.blocks.Loop;
import net.sf.jdec.blocks.Switch;
import net.sf.jdec.constantpool.*;
import net.sf.jdec.constantpool.CPString;
import net.sf.jdec.core.GlobalVariableStore;
import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.TernaryTracker;
import net.sf.jdec.util.Util;
import net.sf.jdec.util.ExecutionState;

import java.util.*;

/*
 *  BranchInstrFinder.java Copyright (c) 2006,07 Swaroop Belur
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
 * Implementation methods cover ifs , loops , switch cases Basically any
 * instruction which can cause the sequence of execution to be non sequential. 1
 * >If jump can lead to an instruction either ahead or behind the current
 * instruction pos <p/> 2> Loop is nothing but an if with repetition. <p/> 3>
 * Switches can be easily simulated with ifs...
 * 
 * @author swaroop belur(belurs)
 * @since 1.2.1
 */
public class BranchInstrFinder extends BasicFinder {

	public static final BranchInstrFinder branchFinder = new BranchInstrFinder();

	private BranchInstrFinder() {
	}

	public boolean isCurrentInstStore(int pos) {
		throw new UnsupportedOperationException(
				"Invalid call[isCurrentInstStore] on BranchInstrFinder object");
	}

	public boolean isGotoPrecededByDUPSTORE(int pos) {
		throw new UnsupportedOperationException(
				"Invalid call[isGotoPrecededByDUPSTORE] on BranchInstrFinder object");
	}

	public boolean isHandlerEndPresentAtGuardEnd(int i) {
		throw new UnsupportedOperationException(
				"Invalid call[isHandlerEndPresentAtGuardEnd] on BranchInstrFinder object");
	}

	public boolean isEndOfGuard(int pos) {
		throw new UnsupportedOperationException(
				"Invalid call[isEndOfGuard] on BranchInstrFinder object");
	}

	public boolean isIfConditionForSomeOtherLoop(int i, StringBuffer lend) {
		List loops = getMethod().getBehaviourLoops();
		Object[] sortedLoops = sortLoops(loops);
		byte[] info = getMethod().getCode();
		int parentLoopStart = getParentLoopStartForIf(sortedLoops, i);
		for (int z = 0; z < loops.size(); z++) {
			Loop l = (Loop) loops.get(z);
			int end = l.getEndIndex();
			boolean start = isThisInstrStart(end);
			if (start) {

				int ls = l.getStartIndex();
				if (info[end] == JvmOpCodes.GOTO && ls < end && ls < i
						&& i < end && (parentLoopStart == ls)) {
					for (int k = ls; k < end; k++) {
						start = isThisInstrStart(k);
						if (start) {
							boolean isif = isInstructionIF(k);
							if (isif) {
								if (k == i) {
									lend.append("" + end);
									return true;
								} else {
									return false;
								}
							}
						}
					}
				}
			}

		}

		return false;
	}

	private Object[] sortLoops(List list) {
		Object o[] = list.toArray();
		Arrays.sort(o);
		return o;
	}

	private int getParentLoopStartForIf(Object[] sortedloops, int ifbegin) {
		int reqdStart = -1;
		int max = -1;
		int pos = 0;
		int counter = sortedloops.length - 1;
		while (counter >= 0) {
			Object o = sortedloops[counter];
			if (o instanceof Loop) {
				Loop l = (Loop) o;
				int ls = l.getStartIndex();
				int thisLoopEnd = getloopEndForStart(getMethod()
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

	private int getloopEndForStart(ArrayList list, int start) {
		for (int i = 0; i < list.size(); i++) {
			Loop l = (Loop) list.get(i);
			if (l.getStartIndex() == start) {
				return l.getEndIndex();
			}
		}
		return -1;

	}

	public boolean isIfFirstIfInLoopCondition(int pos) {
		Loop loop = getParentLoopForIf(sortLoops(getMethod()
				.getBehaviourLoops()), pos);
		if (loop != null) {
			int start = loop.getStartIndex();
			byte[] info = getMethod().getCode();
			for (int x = start; x < info.length; x++) {
				boolean b = isThisInstrStart(x);
				if (b) {
					boolean isif = isInstructionIF(x);
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

	private Loop getParentLoopForIf(Object[] sortedloops, int ifbegin) {
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
				int thisLoopEnd = getloopEndForStart(getMethod()
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

	public boolean isIfForLoadClass(IFBlock ifst) {
		boolean p = true;
		ClassDescription cd = ExecutionState.getMethodContext().getClassRef().getCd();
		int by = ifst.getIfCloseFromByteCode();
		byte[] info = getMethod().getCode();
		if (by > ifst.getIfStart()) {
			int j = by - 3;
			if (isThisInstrStart(j) && info[j] == JvmOpCodes.GOTO) {
				int k = getJumpAddress(j);
				if ((k - j) == 6) {
					int s = ifst.getIfStart();
					s = s + 3;
					if (info[s] == JvmOpCodes.LDC) {
						int d = (s + 1);
						int offset = info[d];
						if (offset < 0) {
							offset += 256;
						}

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
						if (isThisInstrStart(s)
								&& info[s] == JvmOpCodes.INVOKESTATIC) {
							int classIndex = getOffset(s);
							MethodRef mref = cd
									.getMethodRefAtCPoolPosition(classIndex);
							java.lang.String name = mref.getMethodName();
							if (name != null && name.indexOf("class$") != -1) {

								Operand op = new Operand();
								StringBuffer sb = new StringBuffer("");
								Util.checkForImport(stringLiteral, sb);
								java.lang.String classToLoad = sb.toString()
										+ ".class";
								boolean yes = isIfPartOfTernaryIfCond(ExecutionState
										.getCurrentInstructionPosition());
								/*
								 * if(yes) {
								 * op.setOperandValue(opStack.getTopOfStack().getOperandValue()+classToLoad); }
								 * else
								 */
								op.setOperandValue(classToLoad);
								boolean terend = isTernaryEnd(ExecutionState
										.getCurrentInstructionPosition());
								if (terend) {
									op.setOperandValue(op.getOperandValue()
											+ ")");
								}

								getMethod().getOpStack().push(op);
								op.setClassType("Class");
								int next = k + 1;
								if (info[k] == JvmOpCodes.DUP
										|| info[k] == JvmOpCodes.DUP2) {
									boolean store = isStoreInst(next);
									if (store) {
										k = next;
									}
								}
								GlobalVariableStore.getSkipWRTClassLoadIf().put(
										new Integer(ifst.getIfStart() + 3),
										new Integer(k));
								return false;
							}
						} else {
							return true;
						}

					} else if (info[s] == JvmOpCodes.LDC_W) {
						// int d=(s+1);
						int offset = getOffset(s);
						// if(offset < 0)offset+=256;
						CPString constString = cd
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
						if (isThisInstrStart(s)
								&& info[s] == JvmOpCodes.INVOKESTATIC) {
							int classIndex = getOffset(s);
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
								getMethod().getOpStack().push(op);
								op.setClassType("Class");
								GlobalVariableStore.getSkipWRTClassLoadIf().put(
										new Integer(ifst.getIfStart() + 3),
										new Integer(k));
								return false;
							}
						} else {
							return true;
						}

					} else {
						return true;

					}

				}

			}

		}

		return p;
	}

	public boolean isIFForThisElseATernaryIF(int pos, StringBuffer sb) {
		boolean yes = false;

		Set methodifs = ExecutionState.getMethodContext().getMethodIfs();
		Iterator iterIfHash = methodifs.iterator();
		int current = ExecutionState.getCurrentInstructionPosition();
		while (iterIfHash.hasNext()) {
			
			IFBlock ifs = (IFBlock) iterIfHash.next();
			int thisifclose = ifs.getIfCloseLineNumber();
			if ((ifs.getIfCloseLineNumber() - (current)) == 0) {
				boolean present = isIFpresentInTernaryList(ifs);
				if (present) {
					boolean end = isThisGotoTernaryEnd(ifs, current);
					if (end) {
						sb.append("end");
					}
					return true;
				}
			}
		}
		return yes;
	}

	private boolean isThisGotoTernaryEnd(IFBlock ifst, int end) {
		List ternList = GlobalVariableStore.getTernList();
		if (ternList.size() == 0) {
			return false;
		}
		for (int z = 0; z < ternList.size(); z++) {

			TernaryTracker ternobj = (TernaryTracker) ternList.get(z);
			IFBlock iF = ternobj.getIF();
			if (iF == ifst) {
				int t = ternobj.getTEnd();
				if (t == end) {
					return true;
				}
			}
		}
		return false;
	}

	public Loop isIfInADoWhile(int current, IFBlock ifst) {

		Loop l = getLoopGivenEnd(current, getMethod().getBehaviourLoops());
		byte[] info = getMethod().getCode();
		if (l != null) {
			boolean isif = isInstructionIF(current);
			if (isif && isThisInstrStart(current)) {
				return l;
			}
		}
		return null;
	}

	private Loop getLoopGivenEnd(int end, ArrayList loops) {
		for (int z = 0; z < loops.size(); z++) {
			Loop l = (Loop) loops.get(z);
			if (l.getEndIndex() == end) {
				return l;
			}
		}
		return null;
	}

	public Switch.Case isIFInCase(int pos, IFBlock ifs) {
		Switch.Case caseblk = null;
		boolean present = false;
		int ifstart = ifs.getIfStart();
		int ifend = ifs.getIfCloseLineNumber();
		ArrayList allswitches = getMethod().getAllSwitchBlks();
		for (int s = 0; s < allswitches.size(); s++) {

			ArrayList allcases = ((Switch) allswitches.get(s)).getAllCases();
			for (int k = 0; k < allcases.size(); k++) {
				Switch.Case cblk = (Switch.Case) allcases.get(k);
				int casestart = cblk.getCaseStart();
				int caseend = cblk.getCaseEnd();
				if (ifstart >= casestart && ifend < caseend) {

					present = true;
					caseblk = cblk;
					break;
				}

			}
			// System.out.println(caseblk);
			if (present) {
				break;
			}

		}

		return caseblk;

	}

	public boolean isIfPartOfTernaryIfCond(int pos) {
		return false;
	}

	public boolean isIFpresentInTernaryList(IFBlock ifst) {
		return false;
	}

	public boolean isIFShortcutORComp(int pos) {

		boolean b = getMethod().getShortCutAnalyser().isIFShortcutIfCondition(
				pos);
		return b;
	}

	public boolean isIndexEndOfLoop(int s) {
		List list = getMethod().getBehaviourLoops();
		boolean ok = false;
		for (int st = 0; st < list.size(); st++) {
			if (((Loop) list.get(st)).getEndIndex() == s) {
				return true;
			}
		}
		return ok;
	}

	public boolean isInstrPosAAload(int pos) {
		throw new UnsupportedOperationException();
	}

	public int isInstAload(int pos, StringBuffer bf) {
		throw new UnsupportedOperationException();
	}

	public int isInstDloadInst(int pos, StringBuffer sb2) {
		throw new UnsupportedOperationException();
	}

	public int isInstFloadInst(int pos, StringBuffer sb2) {
		throw new UnsupportedOperationException();
	}

	public int isInstIloadInst(int pos, StringBuffer sb2) {
		throw new UnsupportedOperationException();
	}

	public int isInstLloadInst(int pos, StringBuffer sb2) {
		throw new UnsupportedOperationException();
	}

	public boolean isInstLoopStart(int pos) {
		ArrayList loops = getMethod().getBehaviourLoops();
		for (int z = 0; z < loops.size(); z++) {
			Loop loop = (Loop) loops.get(z);
			int loopstart = loop.getStartIndex();
			if (loopstart == pos) {
				return true;
			}
		}
		return false;

	}

	public boolean isInstPrimitiveArrayStore(int inst) {
		throw new UnsupportedOperationException();
	}

	public boolean isInstReturnInst(byte[] code, int pos, StringBuffer sb) {

		boolean ret = false;
		if (isThisInstrStart(pos)) {
			switch (code[pos]) {
			case JvmOpCodes.IRETURN:
				sb.append("ireturn");
				ret = true;
				break;

			case JvmOpCodes.LRETURN:
				sb.append("lreturn");
				ret = true;
				break;
			case JvmOpCodes.FRETURN:
				sb.append("freturn");
				ret = true;
				break;
			case JvmOpCodes.DRETURN:
				sb.append("dreturn");
				ret = true;
				break;
			case JvmOpCodes.ARETURN:
				sb.append("areturn");
				ret = true;
				break;
			case JvmOpCodes.RETURN:
				sb.append("return");
				ret = true;
				break;
			}
		}

		return ret;

	}

	public boolean isInstReturnInst(int pos, StringBuffer sb) {

		boolean ret = false;
		if (isThisInstrStart(pos)) {
			switch (getMethod().getCode()[pos]) {
			case JvmOpCodes.IRETURN:
				sb.append("ireturn");
				ret = true;
				break;

			case JvmOpCodes.LRETURN:
				sb.append("lreturn");
				ret = true;
				break;
			case JvmOpCodes.FRETURN:
				sb.append("freturn");
				ret = true;
				break;
			case JvmOpCodes.DRETURN:
				sb.append("dreturn");
				ret = true;
				break;
			case JvmOpCodes.ARETURN:
				sb.append("areturn");
				ret = true;
				break;
			case JvmOpCodes.RETURN:
				sb.append("return");
				ret = true;
				break;
			}
		}

		return ret;

	}

	public boolean isInstructionIF(int pos) {

		if (!isThisInstrStart(pos)) {
			return false;
		}
		switch (getMethod().getCode()[pos]) {

		case JvmOpCodes.IF_ACMPEQ:
			return true;
		case JvmOpCodes.IF_ACMPNE:
			return true;
		case JvmOpCodes.IF_ICMPEQ:
			return true;
		case JvmOpCodes.IF_ICMPGE:
			return true;
		case JvmOpCodes.IF_ICMPGT:
			return true;
		case JvmOpCodes.IF_ICMPLE:
			return true;
		case JvmOpCodes.IF_ICMPLT:
			return true;
		case JvmOpCodes.IF_ICMPNE:
			return true;

		case JvmOpCodes.IFEQ:
			return true;
		case JvmOpCodes.IFGE:
			return true;
		case JvmOpCodes.IFGT:
			return true;
		case JvmOpCodes.IFLE:
			return true;
		case JvmOpCodes.IFNE:
			return true;
		case JvmOpCodes.IFLT:
			return true;
		case JvmOpCodes.IFNULL:
			return true;
		case JvmOpCodes.IFNONNULL:
			return true;
		default:
			return false;

		}

	}

	public boolean isInstStore0(int pos) {
		throw new UnsupportedOperationException();
	}

	public boolean isNextInstAStore(int pos) {
		throw new UnsupportedOperationException();
	}

	public boolean isNextInstructionLoad(int nextInstruction) {
		throw new UnsupportedOperationException();
	}

	public boolean isNextInstructionPrimitiveStoreInst(int pos,
			StringBuffer index) {
		throw new UnsupportedOperationException();
	}

	public boolean isNextInstructionStore(int nextInstruction) {
		throw new UnsupportedOperationException();
	}

	public boolean isPrevInstALOADInst(int pos, StringBuffer s) {
		throw new UnsupportedOperationException();
	}

	public boolean isPrevInstIloadInst(int s, StringBuffer sb2) {
		throw new UnsupportedOperationException();
	}

	public boolean isPrevInstPrimitiveLoad(int pos, StringBuffer sb) {
		throw new UnsupportedOperationException();
	}

	public boolean isPrevInstructionAload(int pos, StringBuffer sb) {
		throw new UnsupportedOperationException();
	}

	public boolean isPrevInstructionAload(int pos) {
		throw new UnsupportedOperationException();
	}

	public boolean isPrevInstructionAload(int pos, byte[] code, StringBuffer sb) {
		throw new UnsupportedOperationException();
	}

	public boolean isStoreInst(int index) {
		throw new UnsupportedOperationException();
	}

	public boolean isStoreInst(int index, StringBuffer varindex, StringBuffer t) {
		throw new UnsupportedOperationException();
	}

	public boolean isThisDUPSTOREAtEndOFTernaryIF(int pos, java.lang.String type) {
		return false;
	}

	public boolean isThisIfALoopCondition(IFBlock IF) {
		boolean b = true;
		int ifend = IF.getIfCloseLineNumber();
		int ifs = IF.getIfStart();
		boolean b1 = isThisLoopEndAlso(ifend, ifs);
		if (!b1) {
			return false;
		}
		if (b1) {
			byte[] info = getMethod().getCode();
			int jump = getJumpAddress(ifend);
			if (jump >= ifs) {
				return false;
			}
			for (int s = jump; s < ifs; s++) {
				// int inst=info[s];
				boolean b2 = isInstructionIF(s);
				if (b2 && isThisInstrStart(s)) {
					b = false;
					return b;
				}
			}

		}

		return b;
	}

	public boolean isThisInstASTOREInst(int pos, StringBuffer sb) {
		throw new UnsupportedOperationException();
	}

	public boolean isThisInstructionIStoreInst(int s, StringBuffer sb) {
		throw new UnsupportedOperationException();
	}

	public boolean isThisLoopEndAlso(int i, int ifstart) {
		List loops = getMethod().getBehaviourLoops();
		for (int s = 0; s < loops.size(); s++) {
			Loop l = (Loop) loops.get(s);
			int lend = (l).getEndIndex();
			if (lend == i && ifstart > l.getStartIndex()) {
				return true;
			}
		}
		return false;
	}

	public boolean isThisLoopStart(IFBlock IF) {
		boolean ok = false;
		ok = isThisIfALoopCondition(IF);
		return ok;
	}

	public boolean isThisTryStart(int i) {
		throw new UnsupportedOperationException();
	}

	public boolean lastIFinShortCutChain(IFBlock ifst, int i) {
		return getMethod().getShortCutAnalyser().isLastIfInChain(i);
	}

	public boolean isNextInstructionReturn(int pos) {
		boolean flag = false;
		if (!isThisInstrStart(pos)) {
			return flag;
		}

		switch (getMethod().getCode()[pos]) {
		case JvmOpCodes.RETURN:
			return true;
		default:
			return false;

		}
	}

	public boolean isThisInstructionIStoreInst(byte[] code, int s,
			StringBuffer sb) {
		throw new UnsupportedOperationException();
	}

	public boolean isNextInstructionIf(int nextInstruction) {
		boolean flag = false;

		switch (nextInstruction) {
		case JvmOpCodes.IF_ACMPEQ:
			flag = true;
			break;
		case JvmOpCodes.IF_ACMPNE:
			flag = true;
			break;
		case JvmOpCodes.IF_ICMPEQ:
			flag = true;
			break;
		case JvmOpCodes.IF_ICMPGE:
			flag = true;
			break;
		case JvmOpCodes.IF_ICMPGT:
			flag = true;
			break;
		case JvmOpCodes.IF_ICMPLE:
			flag = true;
			break;
		case JvmOpCodes.IF_ICMPLT:
			flag = true;
			break;
		case JvmOpCodes.IF_ICMPNE:
			flag = true;
			break;
		case JvmOpCodes.IFEQ:
			flag = true;
			break;
		case JvmOpCodes.IFGE:
			flag = true;
			break;
		case JvmOpCodes.IFGT:
			flag = true;
			break;
		case JvmOpCodes.IFLE:
			flag = true;
			break;
		case JvmOpCodes.IFLT:
			flag = true;
			break;
		case JvmOpCodes.IFNE:
			flag = true;
			break;
		case JvmOpCodes.IFNONNULL:
			flag = true;
			break;
		case JvmOpCodes.IFNULL:
			flag = true;
			break;
		default:
			flag = false;
		}
		return flag;
	}

	public boolean isStoreInst(int index, byte[] info) {
		throw new UnsupportedOperationException();
	}

}