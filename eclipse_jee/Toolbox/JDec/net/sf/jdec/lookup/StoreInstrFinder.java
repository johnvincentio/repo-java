package net.sf.jdec.lookup;

import net.sf.jdec.blocks.IFBlock;
import net.sf.jdec.blocks.Loop;
import net.sf.jdec.blocks.Switch;
import net.sf.jdec.core.JvmOpCodes;

/*
 *  StoreInstrFinder.java Copyright (c) 2006,07 Swaroop Belur 
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
 * Implementation specifics: a> Implements all find methods concerning store
 * instructions here. b> throws UnsupportedOperationException for all other find
 * methods.
 * 
 * @author swaroop belur(belurs)
 * @version 1.2.1
 */

public class StoreInstrFinder extends BasicFinder {
	public static final StoreInstrFinder storeFinder = new StoreInstrFinder();

	private StoreInstrFinder() {
	}

	public boolean isCurrentInstStore(int pos) {
		boolean flag;
		if (!isThisInstrStart(pos))
			return false;
		int inst = getMethod().getCode()[pos];
		switch (inst) {

		case JvmOpCodes.ASTORE:
			flag = true;
			break;

		case JvmOpCodes.ASTORE_0:
			flag = true;
			break;
		case JvmOpCodes.ASTORE_1:
			flag = true;
			break;
		case JvmOpCodes.ASTORE_2:
			flag = true;
			break;
		case JvmOpCodes.ASTORE_3:
			flag = true;
			break;
		case JvmOpCodes.DSTORE:
			flag = true;
			break;
		case JvmOpCodes.DSTORE_0:
			flag = true;
			break;
		case JvmOpCodes.DSTORE_1:
			flag = true;
			break;
		case JvmOpCodes.DSTORE_2:
			flag = true;
			break;
		case JvmOpCodes.DSTORE_3:
			flag = true;
			break;

		case JvmOpCodes.FSTORE:
			flag = true;
			break;
		case JvmOpCodes.FSTORE_0:
			flag = true;
			break;
		case JvmOpCodes.FSTORE_1:
			flag = true;
			break;
		case JvmOpCodes.FSTORE_2:
			flag = true;
			break;
		case JvmOpCodes.FSTORE_3:
			flag = true;
			break;

		case JvmOpCodes.ISTORE:
			flag = true;
			break;
		case JvmOpCodes.ISTORE_0:
			flag = true;
			break;
		case JvmOpCodes.ISTORE_1:
			flag = true;
			break;
		case JvmOpCodes.ISTORE_2:
			flag = true;
			break;
		case JvmOpCodes.ISTORE_3:
			flag = true;
			break;
		case JvmOpCodes.LSTORE:
			flag = true;
			break;

		case JvmOpCodes.LSTORE_0:
			flag = true;
			break;
		case JvmOpCodes.LSTORE_1:
			flag = true;
			break;
		case JvmOpCodes.LSTORE_2:
			flag = true;
			break;
		case JvmOpCodes.LSTORE_3:
			flag = true;
			break;

		default:
			flag = false;
		}
		return flag;
	}

	public boolean isGotoPrecededByDUPSTORE(int current) {
		boolean yes = false;
		int prev = current - 1;
		boolean store = false;
		boolean dup = false;
		int x = -1;
		byte[] info = getMethod().getCode();
		while (prev > 0) {
			if (isThisInstrStart(prev)) {
				store = isNextInstructionStore(prev);
				x = prev;
				if (store) {
					dup = isPrevInstDup(x);
					if (dup)
						return true;
				}

				if (info[prev] == JvmOpCodes.GOTO) {
					return false;
				}
			}
			prev--;
		}

		return yes;
	}

	public boolean isHandlerEndPresentAtGuardEnd(int i) {
		throw new UnsupportedOperationException(
				"Invalid call[isHandlerEndPresentAtGuardEnd] on StoreInstrFinder Object");
	}

	public boolean isEndOfGuard(int pos) {
		throw new UnsupportedOperationException(
				"Invalid call[isEndOfGuard] on StoreInstrFinder Object");
	}

	public boolean isIfConditionForSomeOtherLoop(int pos, StringBuffer lend) {
		throw new UnsupportedOperationException(
				"Invalid call[isIfConditionForSomeOtherLoop] on StoreInstrFinder Object");
	}

	public boolean isIfFirstIfInLoopCondition(int pos) {
		throw new UnsupportedOperationException(
				"Invalid call[isIfFirstIfInLoopCondition] on StoreInstrFinder Object");
	}

	public boolean isIfForLoadClass(IFBlock ifst) {
		throw new UnsupportedOperationException(
				"Invalid call[isIfFirstIfInLoopCondition] on StoreInstrFinder Object");
	}

	public boolean isIFForThisElseATernaryIF(int pos, StringBuffer sb) {
		throw new UnsupportedOperationException(
				"Invalid call[isIFForThisElseATernaryIF] on StoreInstrFinder Object");
	}

	public Loop isIfInADoWhile(int pos, IFBlock ifst) {
		throw new UnsupportedOperationException(
				"Invalid call[isIfInADoWhile] on StoreInstrFinder Object");
	}

	public Switch.Case isIFInCase(int pos, IFBlock ifs) {
		throw new UnsupportedOperationException(
				"Invalid call[isIFInCase] on StoreInstrFinder Object");
	}

	public boolean isIfPartOfTernaryIfCond(int pos) {
		throw new UnsupportedOperationException(
				"Invalid call[isIfPartOfTernaryIfCond] on StoreInstrFinder Object");
	}

	public boolean isIFpresentInTernaryList(IFBlock ifst) {
		throw new UnsupportedOperationException(
				"Invalid call[isIFpresentInTernaryList] on StoreInstrFinder Object");
	}

	public boolean isIFShortcutORComp(int pos) {
		throw new UnsupportedOperationException(
				"Invalid call[isIFShortcutORComp] on StoreInstrFinder Object");
	}

	public boolean isIndexEndOfLoop(int pos) {
		throw new UnsupportedOperationException(
				"Invalid call[isIndexEndOfLoop] on StoreInstrFinder Object");
	}

	public boolean isInstrPosAAload(int pos) {
		throw new UnsupportedOperationException(
				"Invalid call[isInstrPosAAload] on StoreInstrFinder Object");
	}

	public int isInstAload(int pos, StringBuffer bf) {
		throw new UnsupportedOperationException(
				"Invalid call[isInstAload] on StoreInstrFinder Object");
	}

	public boolean isInstAnyBasicPrimitiveOperation(int pos, StringBuffer sb) {
		throw new UnsupportedOperationException(
				"Invalid call[isInstAnyBasicPrimitiveOperation] on StoreInstrFinder Object");
	}

	public boolean isInstAnyCMPInst(int pos) {
		throw new UnsupportedOperationException(
				"Invalid call[isInstAnyCMPInst] on StoreInstrFinder Object");
	}

	public int isInstAnyConstInst(int pos) {
		throw new UnsupportedOperationException(
				"Invalid call[isInstAnyConstInst] on StoreInstrFinder Object");
	}

	public int isInstdConstInst(int pos) {
		throw new UnsupportedOperationException(
				"Invalid call[isInstdConstInst] on StoreInstrFinder Object");
	}

	public int isInstDloadInst(int pos, StringBuffer sb2) {
		throw new UnsupportedOperationException(
				"Invalid call[isInstDloadInst] on StoreInstrFinder Object");
	}

	public boolean isInstDup(int pos) {
		throw new UnsupportedOperationException(
				"Invalid call[isInstDup] on StoreInstrFinder Object");
	}

	public int isInstFConstInst(int pos) {
		throw new UnsupportedOperationException(
				"Invalid call[isInstFConstInst] on StoreInstrFinder Object");
	}

	public int isInstFloadInst(int pos, StringBuffer sb2) {
		throw new UnsupportedOperationException(
				"Invalid call[isInstFloadInst] on StoreInstrFinder Object");
	}

	public int isInstIConstInst(int pos) {
		throw new UnsupportedOperationException(
				"Invalid call[isInstIConstInst] on StoreInstrFinder Object");
	}

	public int isInstIloadInst(int pos, StringBuffer sb2) {
		throw new UnsupportedOperationException(
				"Invalid call[isInstIloadInst] on StoreInstrFinder Object");
	}

	public int isInstLConstInst(int pos) {
		throw new UnsupportedOperationException(
				"Invalid call[isInstLConstInst] on StoreInstrFinder Object");
	}

	public int isInstLloadInst(int pos, StringBuffer sb2) {
		throw new UnsupportedOperationException(
				"Invalid call[isInstLloadInst] on StoreInstrFinder Object");
	}

	public boolean isInstLoopStart(int pos) {
		throw new UnsupportedOperationException(
				"Invalid call[isInstLoopStart] on StoreInstrFinder Object");
	}

	public boolean isInstPrimitiveArrayStore(int pos) {

		if (!isThisInstrStart(pos))
			return false;
		int inst = getMethod().getCode()[pos];

		switch (inst) {
		case JvmOpCodes.IASTORE:
		case JvmOpCodes.BASTORE:
		case JvmOpCodes.SASTORE:
		case JvmOpCodes.FASTORE:
		case JvmOpCodes.LASTORE:
		case JvmOpCodes.DASTORE:
			return true;
		}
		return false;
	}

	public boolean isInstReturnInst(int pos, StringBuffer sb) {
		throw new UnsupportedOperationException(
				"Invalid call[isInstReturnInst] on StoreInstrFinder Object");
	}

	public boolean isInstructionAnyDUP(int inst) {
		throw new UnsupportedOperationException(
				"Invalid call[isInstructionAnyDUP] on StoreInstrFinder Object");
	}

	public boolean isInstructionIF(int instruction) {
		throw new UnsupportedOperationException(
				"Invalid call[isInstructionIF] on StoreInstrFinder Object");
	}

	public boolean isInstStore0(int pos) {

		boolean flag = false;
		boolean b = isThisInstrStart(pos);
		if (!b)
			return false;
		switch (getMethod().getCode()[pos]) {
		case JvmOpCodes.AASTORE:
			flag = true;
			break;

		case JvmOpCodes.BASTORE:
			flag = true;
			break;
		case JvmOpCodes.CASTORE:
			flag = true;
			break;
		case JvmOpCodes.DASTORE:
			flag = true;
			break;

		case JvmOpCodes.FASTORE:
			flag = true;
			break;

		case JvmOpCodes.IASTORE:
			flag = true;
			break;

		case JvmOpCodes.LASTORE:
			flag = true;
			break;

		case JvmOpCodes.SASTORE:
			flag = true;
			break;

		default:
			flag = false;
		}
		return flag;
	}

	public boolean isNextInstAStore(int pos) {
		byte[] info = getMethod().getCode();
		if (isThisInstrStart(pos)) {
			if (info[pos] == JvmOpCodes.ASTORE) {

				return true;
			}
			if (info[pos] == JvmOpCodes.ASTORE_0) {
				return true;

			}
			if (info[pos] == JvmOpCodes.ASTORE_1) {

				return true;
			}
			if (info[pos] == JvmOpCodes.ASTORE_2) {
				return true;

			}
			if (info[pos] == JvmOpCodes.ASTORE_3) {
				return true;

			}
		}
		return false;

	}

	public boolean isNextInstructionLoad(int nextInstruction) {
		throw new UnsupportedOperationException(
				"Invalid call[isNextInstructionLoad] on StoreInstrFinder Object");
	}

	public boolean isNextInstructionPrimitiveStoreInst(int pos,
			StringBuffer index) {
		boolean flag = false;
		byte[] c = getMethod().getCode();
		if (isThisInstrStart(pos) == false)
			return false;
		switch (c[(pos)]) {
		case JvmOpCodes.DSTORE:
			flag = true;
			index.append(c[(pos + 1)]);
			break;
		case JvmOpCodes.DSTORE_0:
			flag = true;
			index.append(0);
			break;
		case JvmOpCodes.DSTORE_1:
			flag = true;
			index.append(1);
			break;
		case JvmOpCodes.DSTORE_2:
			flag = true;
			index.append(2);
			break;
		case JvmOpCodes.DSTORE_3:
			flag = true;
			index.append(3);
			break;

		case JvmOpCodes.FSTORE:
			flag = true;
			index.append(c[(pos + 1)]);
			break;
		case JvmOpCodes.FSTORE_0:
			flag = true;
			index.append(0);
			break;
		case JvmOpCodes.FSTORE_1:
			flag = true;
			index.append(1);
			break;
		case JvmOpCodes.FSTORE_2:
			flag = true;
			index.append(2);
			break;
		case JvmOpCodes.FSTORE_3:
			flag = true;
			index.append(3);
			break;

		case JvmOpCodes.ISTORE:
			flag = true;
			index.append(c[(pos + 1)]);
			break;
		case JvmOpCodes.ISTORE_0:
			flag = true;
			index.append(0);
			break;
		case JvmOpCodes.ISTORE_1:
			flag = true;
			index.append(1);
			break;
		case JvmOpCodes.ISTORE_2:
			flag = true;
			index.append(2);
			break;
		case JvmOpCodes.ISTORE_3:
			flag = true;
			index.append(3);
			break;

		case JvmOpCodes.LSTORE:
			flag = true;
			index.append(c[(pos + 1)]);
			break;

		case JvmOpCodes.LSTORE_0:
			flag = true;
			index.append(0);
			break;
		case JvmOpCodes.LSTORE_1:
			flag = true;
			index.append(1);
			break;
		case JvmOpCodes.LSTORE_2:
			flag = true;
			index.append(2);
			break;
		case JvmOpCodes.LSTORE_3:
			flag = true;
			index.append(3);
			break;

		}

		return flag;
	}

	public boolean isNextInstructionReturn(int nextInstruction) {
		throw new UnsupportedOperationException(
				"Invalid call[isNextInstructionReturn] on StoreInstrFinder object");
	}

	public boolean isNextInstructionStore(int pos) {
		boolean flag = false;

		if (!isThisInstrStart(pos))
			return false;
		int nextInstruction = getMethod().getCode()[pos];
		switch (nextInstruction) {
		/*
		 * case JvmOpCodes.AASTORE : flag = true; break;
		 */
		case JvmOpCodes.ASTORE:
			flag = true;
			break;

		case JvmOpCodes.ASTORE_0:
			flag = true;
			break;
		case JvmOpCodes.ASTORE_1:
			flag = true;
			break;
		case JvmOpCodes.ASTORE_2:
			flag = true;
			break;
		case JvmOpCodes.ASTORE_3:
			flag = true;
			break;

		case JvmOpCodes.DSTORE:
			flag = true;
			break;
		case JvmOpCodes.DSTORE_0:
			flag = true;
			break;
		case JvmOpCodes.DSTORE_1:
			flag = true;
			break;
		case JvmOpCodes.DSTORE_2:
			flag = true;
			break;
		case JvmOpCodes.DSTORE_3:
			flag = true;
			break;

		case JvmOpCodes.FSTORE:
			flag = true;
			break;
		case JvmOpCodes.FSTORE_0:
			flag = true;
			break;
		case JvmOpCodes.FSTORE_1:
			flag = true;
			break;
		case JvmOpCodes.FSTORE_2:
			flag = true;
			break;
		case JvmOpCodes.FSTORE_3:
			flag = true;
			break;

		case JvmOpCodes.ISTORE:
			flag = true;
			break;
		case JvmOpCodes.ISTORE_0:
			flag = true;
			break;
		case JvmOpCodes.ISTORE_1:
			flag = true;
			break;
		case JvmOpCodes.ISTORE_2:
			flag = true;
			break;
		case JvmOpCodes.ISTORE_3:
			flag = true;
			break;

		case JvmOpCodes.LSTORE:
			flag = true;
			break;

		case JvmOpCodes.LSTORE_0:
			flag = true;
			break;
		case JvmOpCodes.LSTORE_1:
			flag = true;
			break;
		case JvmOpCodes.LSTORE_2:
			flag = true;
			break;
		case JvmOpCodes.LSTORE_3:
			flag = true;
			break;

		default:
			flag = false;
		}
		return flag;
	}

	public boolean isPrevInstALOADInst(int pos, StringBuffer s) {
		throw new UnsupportedOperationException(
				"Invalid call[isPrevInstALOADInst] on StoreInstrFinder object");
	}

	public boolean isPrevInstIloadInst(int s, StringBuffer sb2) {
		throw new UnsupportedOperationException(
				"Invalid call[isPrevInstIloadInst] on StoreInstrFinder object");
	}

	public boolean isPrevInstPrimitiveLoad(int pos, StringBuffer sb) {
		throw new UnsupportedOperationException(
				"Invalid call[isPrevInstPrimitiveLoad] on StoreInstrFinder object");
	}

	public boolean isPrevInstructionAload(int pos, StringBuffer sb) {
		throw new UnsupportedOperationException(
				"Invalid call[isPrevInstructionAload] on StoreInstrFinder object");
	}

	public boolean isStoreInst(int index, StringBuffer varindex, StringBuffer t) {

		boolean b = isThisInstrStart(index);
		if (b == false)
			return false;
		byte[] info = getMethod().getCode();
		switch (info[index]) {
		case JvmOpCodes.AASTORE:
		case JvmOpCodes.BASTORE:
		case JvmOpCodes.CASTORE:
		case JvmOpCodes.DASTORE:
		case JvmOpCodes.FASTORE:
		case JvmOpCodes.IASTORE:
		case JvmOpCodes.LASTORE:
		case JvmOpCodes.SASTORE:
			varindex.append("-1");
			return true;

		case JvmOpCodes.ASTORE:
			varindex.append(info[(index + 1)]);
			t.append("java.lang.Object");
			return true;
		case JvmOpCodes.DSTORE:
			varindex.append(info[(index + 1)]);
			t.append("double");
			return true;
		case JvmOpCodes.FSTORE:
			varindex.append(info[(index + 1)]);
			t.append("float");
			return true;
		case JvmOpCodes.ISTORE:
			varindex.append(info[(index + 1)]);
			t.append("int");
			return true;
		case JvmOpCodes.LSTORE:
			varindex.append(info[(index + 1)]);
			t.append("long");
			return true;

		case JvmOpCodes.ASTORE_0:
			varindex.append("0");
			t.append("java.lang.Object");
			return true;

		case JvmOpCodes.DSTORE_0:
			varindex.append("0");
			t.append("double");
			return true;
		case JvmOpCodes.FSTORE_0:
			varindex.append("0");
			t.append("float");
			return true;
		case JvmOpCodes.ISTORE_0:
			varindex.append("0");
			t.append("int");
			return true;
		case JvmOpCodes.LSTORE_0:
			varindex.append("0");
			t.append("long");
			return true;

		case JvmOpCodes.ASTORE_1:
			varindex.append("1");
			t.append("java.lang.Object");
			return true;
		case JvmOpCodes.DSTORE_1:
			varindex.append("1");
			t.append("double");
			return true;
		case JvmOpCodes.FSTORE_1:
			varindex.append("1");
			t.append("float");
			return true;
		case JvmOpCodes.ISTORE_1:
			varindex.append("1");
			t.append("int");
			return true;
		case JvmOpCodes.LSTORE_1:
			varindex.append("1");
			t.append("long");
			return true;

		case JvmOpCodes.ASTORE_2:
			varindex.append("2");
			t.append("java.lang.Object");
			return true;
		case JvmOpCodes.DSTORE_2:
			varindex.append("2");
			t.append("double");
			return true;
		case JvmOpCodes.FSTORE_2:
			varindex.append("2");
			t.append("float");
			return true;
		case JvmOpCodes.ISTORE_2:
			varindex.append("2");
			t.append("int");
			return true;
		case JvmOpCodes.LSTORE_2:
			varindex.append("2");
			t.append("long");
			return true;

		case JvmOpCodes.ASTORE_3:
			varindex.append("3");
			t.append("java.lang.Object");
			return true;
		case JvmOpCodes.DSTORE_3:
			varindex.append("3");
			t.append("double");
			return true;
		case JvmOpCodes.FSTORE_3:
			varindex.append("3");
			t.append("float");
			return true;
		case JvmOpCodes.ISTORE_3:
			varindex.append("3");
			t.append("int");
			return true;
		case JvmOpCodes.LSTORE_3:
			varindex.append("3");
			t.append("long");
			return true;

		default:
			return false;

		}
	}

	public boolean isThisDUPSTOREAtEndOFTernaryIF(int pos, String type) {
		throw new UnsupportedOperationException(
				"Invalid call[isThisDUPSTOREAtEndOFTernaryIF] on StoreInstrFinder object");
	}

	public boolean isThisIfALoopCondition(IFBlock IF) {
		throw new UnsupportedOperationException(
				"Invalid call[isThisIfALoopCondition] on StoreInstrFinder object");
	}

	public boolean isThisInstASTOREInst(int pos, StringBuffer sb) {
		byte[] info = getMethod().getCode();
		if (isThisInstrStart(pos)) {
			if (info[pos] == JvmOpCodes.ASTORE) {
				sb.append(info[(pos + 1)]);
				return true;
			}
			if (info[pos] == JvmOpCodes.ASTORE_0) {
				sb.append(0);
				return true;

			}
			if (info[pos] == JvmOpCodes.ASTORE_1) {
				sb.append(1);
				return true;
			}
			if (info[pos] == JvmOpCodes.ASTORE_2) {
				sb.append(2);
				return true;

			}
			if (info[pos] == JvmOpCodes.ASTORE_3) {
				sb.append(3);
				return true;

			}
		}
		return false;
	}

	public boolean isThisInstructionIStoreInst(int s, StringBuffer sb) {
		boolean b = false;
		byte[] code = getMethod().getCode();
		if (isThisInstrStart(s) == false)
			return false;
		switch (code[s]) {
		case JvmOpCodes.ISTORE_0:
			b = true;
			sb.append(0);
			break;
		case JvmOpCodes.ISTORE_1:
			b = true;
			sb.append(1);
			break;
		case JvmOpCodes.ISTORE_2:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.ISTORE_3:
			b = true;
			sb.append(3);
			break;
		case JvmOpCodes.ISTORE:
			b = true;
			sb.append(code[(s + 1)]);
			break;
		}

		return b;
	}

	public boolean isThisLoopEndAlso(int i, int ifstart) {
		throw new UnsupportedOperationException(
				"Invalid call[isThisLoopEndAlso] on StoreInstrFinder object");
	}

	public boolean isThisLoopStart(IFBlock IF) {
		throw new UnsupportedOperationException(
				"Invalid call[isThisLoopStart] on StoreInstrFinder object");
	}

	public boolean isThisTryStart(int i) {
		throw new UnsupportedOperationException(
				"Invalid call[isThisTryStart] on StoreInstrFinder object");
	}

	public boolean lastIFinShortCutChain(IFBlock ifst, int i) {
		throw new UnsupportedOperationException(
				"Invalid call[lastIFinShortCutChain] on StoreInstrFinder object");
	}

	public boolean isThisInstructionIStoreInst(byte[] code, int s,
			StringBuffer sb) {
		boolean b = false;
		if (isThisInstrStart(s) == false)
			return false;
		switch (code[s]) {
		case JvmOpCodes.ISTORE_0:
			b = true;
			sb.append(0);
			break;
		case JvmOpCodes.ISTORE_1:
			b = true;
			sb.append(1);
			break;
		case JvmOpCodes.ISTORE_2:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.ISTORE_3:
			b = true;
			sb.append(3);
			break;
		case JvmOpCodes.ISTORE:
			b = true;
			sb.append(code[(s + 1)]);
			break;
		}

		return b;
	}

	public boolean isInstReturnInst(byte[] code, int pos, StringBuffer sb) {
		throw new UnsupportedOperationException();
	}

	public boolean isNextInstructionIf(int nextInstruction) {
		throw new UnsupportedOperationException();
	}

	public boolean isStoreInst(int index, byte[] info) {

		if (index < 0)
			return false;
		boolean b = isThisInstrStart(index);
		if (!b)
			return false;
		switch (info[index]) {
		case JvmOpCodes.AASTORE:
		case JvmOpCodes.ASTORE:
		case JvmOpCodes.ASTORE_0:
		case JvmOpCodes.ASTORE_1:
		case JvmOpCodes.ASTORE_2:
		case JvmOpCodes.ASTORE_3:

		case JvmOpCodes.BASTORE:
		case JvmOpCodes.CASTORE:
		case JvmOpCodes.DASTORE:
		case JvmOpCodes.DSTORE:
		case JvmOpCodes.DSTORE_0:
		case JvmOpCodes.DSTORE_1:
		case JvmOpCodes.DSTORE_2:
		case JvmOpCodes.DSTORE_3:
		case JvmOpCodes.FASTORE:
		case JvmOpCodes.FSTORE:
		case JvmOpCodes.FSTORE_0:
		case JvmOpCodes.FSTORE_1:
		case JvmOpCodes.FSTORE_2:
		case JvmOpCodes.FSTORE_3:
		case JvmOpCodes.IASTORE:
		case JvmOpCodes.ISTORE:
		case JvmOpCodes.ISTORE_0:
		case JvmOpCodes.ISTORE_1:
		case JvmOpCodes.ISTORE_2:
		case JvmOpCodes.ISTORE_3:
		case JvmOpCodes.LASTORE:
		case JvmOpCodes.LSTORE:
		case JvmOpCodes.LSTORE_0:
		case JvmOpCodes.LSTORE_1:
		case JvmOpCodes.LSTORE_2:
		case JvmOpCodes.LSTORE_3:
		case JvmOpCodes.SASTORE:
			return true;
		default:
			return false;

		}

	}

}