package net.sf.jdec.lookup;

import net.sf.jdec.core.GlobalVariableStore;
import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.exceptions.ApplicationException;
import net.sf.jdec.jvminstructions.util.InstrConstants;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.ExecutionState;

import java.util.ArrayList;

/*
 *  BasicFinder.java Copyright (c) 2006,07 Swaroop Belur 
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
 * Base class for all lookup. To include all common finder methods util method
 * implementation and all those which do not easily fall under load/store etc
 * categories....
 * 
 * @author swaroop belur( belurs)
 * @since version 1.2.1
 */

public abstract class BasicFinder implements IFinder {

	protected Behaviour getMethod() {
		return ExecutionState.getMethodContext();
	}

	public boolean isThisInstrStart(int pos) {
		ArrayList list = getMethod().getInstructionStartPositions();
		boolean start = false;
		boolean beyondcode = beyondCodeArrayPos(pos);
		if (beyondcode) {
			return false;
		}
		if (list == null && getMethod().getCode() != null
				&& getMethod().getCode().length > 0) {
			throw new ApplicationException(
					"Fatal error : Start positions not set for method : "
							+ getMethod().getBehaviourName());
		}
		if (list != null) {
			for (int k = 0; k < list.size(); k++) {
				Integer in = (Integer) list.get(k);
				if (in != null) {
					int i = in.intValue();
					if (i == pos) {
						return !start;
					}
				}
			}
		}
		return start;

	}

	/**
	 * @param pos -
	 *            position in the code array to check
	 * @return true if IADD|FADD|ISUB|FSUB
	 */
	public boolean isCategory1AddSub(int pos) {

		byte info[] = getMethod().getCode();
		boolean st = isThisInstrStart(pos);
		if (st) {
			switch (info[pos]) {
			case JvmOpCodes.IADD:
			case JvmOpCodes.FADD:
			case JvmOpCodes.ISUB:
			case JvmOpCodes.FSUB:
				return true;
			}

		}
		return false;

	}

	/**
	 * @param pos -
	 *            position in the code array to check
	 * @return true if LADD|DADD|LSUB|DSUB
	 */
	public boolean isCategory2AddSub(int pos) {

		boolean st = isThisInstrStart(pos);
		if (st) {
			switch (getMethod().getCode()[pos]) {
			case JvmOpCodes.LADD:
			case JvmOpCodes.DADD:
			case JvmOpCodes.LSUB:
			case JvmOpCodes.DSUB:
				return true;
			}

		}
		return false;

	}

	/**
	 * @param pos
	 * @return
	 */
	protected boolean beyondCodeArrayPos(int pos) {
		if (pos >= getMethod().getCode().length) {

			return true;
		}

		return false;
	}

	public boolean isInstAnyBasicPrimitiveOperation(int pos, StringBuffer sb) {
		boolean b = false;
		boolean st = isThisInstrStart(pos);
		if (!st) {
			return b;
		}
		switch (getMethod().getCode()[pos]) {

		case JvmOpCodes.DADD:
			sb.append(2);
			b = true;
			break;
		case JvmOpCodes.DDIV:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.DMUL:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.DNEG:
			b = true;
			sb.append(1);
			break;
		case JvmOpCodes.DREM:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.DSUB:
			b = true;
			sb.append(2);
			break;

		case JvmOpCodes.FADD:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.FDIV:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.FMUL:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.FNEG:
			b = true;
			sb.append(1);
			break;
		case JvmOpCodes.FREM:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.IADD:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.IAND:
			b = true;
			break;
		case JvmOpCodes.IDIV:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.IMUL:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.INEG:
			b = true;
			sb.append(1);
			break;
		case JvmOpCodes.IOR:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.IREM:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.ISHL:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.ISHR:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.ISUB:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.IUSHR:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.IXOR:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.LADD:
			b = true;
			sb.append(2);
			break;

		case JvmOpCodes.LAND:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.LDIV:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.LMUL:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.LNEG:
			b = true;
			sb.append(1);
			break;
		case JvmOpCodes.LOR:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.LREM:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.LSHL:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.LSHR:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.LSUB:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.LUSHR:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.LXOR:
			b = true;
			sb.append(2);
			break;

		}

		return b;
	}

	public boolean isInstAnyCMPInst(int pos) {

		if (isThisInstrStart(pos)) {
			switch (getMethod().getCode()[pos]) {
			case JvmOpCodes.DCMPG:
			case JvmOpCodes.DCMPL:
			case JvmOpCodes.FCMPG:
			case JvmOpCodes.FCMPL:
			case JvmOpCodes.LCMP:
				return true;
			}
		}
		return false;
	}

	public int isInstAnyConstInst(int pos) {
		if (isThisInstrStart(pos)) {
			switch (getMethod().getCode()[pos]) {
			case JvmOpCodes.ICONST_0:
			case JvmOpCodes.ICONST_1:
			case JvmOpCodes.ICONST_2:
			case JvmOpCodes.ICONST_3:
			case JvmOpCodes.ICONST_M1:
			case JvmOpCodes.ICONST_4:
			case JvmOpCodes.ICONST_5:
			case JvmOpCodes.LCONST_0:
			case JvmOpCodes.LCONST_1:
			case JvmOpCodes.DCONST_0:
			case JvmOpCodes.DCONST_1:
			case JvmOpCodes.FCONST_0:
			case JvmOpCodes.FCONST_1:
			case JvmOpCodes.FCONST_2:
				return pos;
			default:
				return -1;
			}
		}

		return -1;

	}

	public int isInstdConstInst(int pos) {
		if (isThisInstrStart(pos)) {
			switch (getMethod().getCode()[pos]) {
			case JvmOpCodes.DCONST_0:
			case JvmOpCodes.DCONST_1:
				return pos;
			}
		}
		return -1;
	}

	public int isInstDloadInst(int pos, StringBuffer buffer) {
		if (isThisInstrStart(pos)) {

			switch (getMethod().getCode()[(pos)]) {
			case JvmOpCodes.DLOAD_0:
				buffer.append(0);
				return pos;

			case JvmOpCodes.DLOAD_1:

				buffer.append(1);
				return pos;

			case JvmOpCodes.DLOAD_2:

				buffer.append(2);
				return pos;

			case JvmOpCodes.DLOAD_3:

				buffer.append(3);
				return pos;

			case JvmOpCodes.DLOAD:
				buffer.append(getMethod().getCode()[pos + 1]);
				return pos;

			}
		}
		int temp = pos - 1;
		if (isThisInstrStart(temp)
				&& getMethod().getCode()[temp] == JvmOpCodes.DLOAD) {
			buffer.append(getMethod().getCode()[pos]);
			return temp;

		}

		return -1;

	}

	public boolean isInstDup(int pos) {
		if (isThisInstrStart(pos)
				&& (getMethod().getCode()[pos] == JvmOpCodes.DUP || getMethod()
						.getCode()[pos] == JvmOpCodes.DUP2)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isPrevInstDup(int cur) {

		byte[] info = getMethod().getCode();

		if (isThisInstrStart(cur - 1)
				&& (info[cur - 1] == JvmOpCodes.DUP || info[cur - 1] == JvmOpCodes.DUP2))
			return true;
		else
			return false;

	}

	public int getOffset(int counter) {

		byte[] info = getMethod().getCode();
		int b1 = info[++counter];
		int b2 = info[++counter];
		int z;
		if (b1 < 0)
			b1 = (256 + b1);
		if (b2 < 0)
			b2 = (256 + b2);

		int indexInst = (((b1 << 8) | b2));
		if (indexInst > 65535)
			indexInst = indexInst - 65536;
		if (indexInst < 0)
			indexInst = 256 + indexInst;
		return indexInst;
	}

	public int getJumpAddress(int counter) {

		byte[] info = getMethod().getCode();
		int b1 = info[++counter];
		int b2 = info[++counter];
		int z;
		if (b1 < 0)
			b1 = (256 + b1);
		if (b2 < 0)
			b2 = (256 + b2);

		int indexInst = ((((b1 << 8) | b2)) + (counter - 2));
		if (indexInst > 65535)
			indexInst = indexInst - 65536;
		if (indexInst < 0)
			indexInst = 256 + indexInst;
		return indexInst;
	}

	public boolean isInstructionAnyDUP(int pos) {
		boolean start = isThisInstrStart(pos);
		if (!start) {
			return false;
		}
		switch (getMethod().getCode()[pos]) {
		case JvmOpCodes.DUP:
		case JvmOpCodes.DUP2:
		case JvmOpCodes.DUP_X1:
		case JvmOpCodes.DUP_X2:
		case JvmOpCodes.DUP2_X1:
		case JvmOpCodes.DUP2_X2:
			return true;

		}
		return false;
	}

	public int isInstFConstInst(int k)

	{
		if (isThisInstrStart(k)) {
			switch (getMethod().getCode()[k]) {
			case JvmOpCodes.FCONST_0:
			case JvmOpCodes.FCONST_1:
			case JvmOpCodes.FCONST_2:
				return k;
			}
		}
		return -1;
	}

	public int isInstFloadInst(int pos, StringBuffer buffer)

	{
		if (isThisInstrStart(pos)) {
			switch (getMethod().getCode()[(pos)]) {
			case JvmOpCodes.FLOAD_0:
				buffer.append(0);
				return pos;

			case JvmOpCodes.FLOAD_1:

				buffer.append(1);
				return pos;

			case JvmOpCodes.FLOAD_2:

				buffer.append(2);
				return pos;

			case JvmOpCodes.FLOAD_3:

				buffer.append(3);
				return pos;

			case JvmOpCodes.FLOAD:

				buffer.append(getMethod().getCode()[(pos + 1)]);
				return pos;

			}
		}

		int temp = pos - 1;
		if (isThisInstrStart(temp)
				&& getMethod().getCode()[temp] == JvmOpCodes.FLOAD) {
			buffer.append(getMethod().getCode()[pos]);
			return temp;

		}

		return -1;

	}

	public int isInstIConstInst(int pos) {
		if (isThisInstrStart(pos)) {
			switch (getMethod().getCode()[pos]) {
			case JvmOpCodes.ICONST_0:
			case JvmOpCodes.ICONST_1:
			case JvmOpCodes.ICONST_2:
			case JvmOpCodes.ICONST_3:
			case JvmOpCodes.ICONST_M1:
			case JvmOpCodes.ICONST_4:
			case JvmOpCodes.ICONST_5:
				return pos;

			}
		}
		return -1;
	}

	public int isInstLConstInst(int pos) {
		if (isThisInstrStart(pos)) {
			switch (getMethod().getCode()[pos]) {
			case JvmOpCodes.LCONST_0:
			case JvmOpCodes.LCONST_1:
				return pos;
			}
		}
		return -1;

	}

	public boolean isNextInstIINC(int pos, int index, java.lang.String type) {
		int next;
		if (type.equals(InstrConstants.COMPLEX)) {
			next = pos + 2;
		} else {
			next = pos + 1;
		}
		if (isThisInstrStart(next)) {

			if (getMethod().getCode()[next] == JvmOpCodes.IINC) {
				int j = getMethod().getCode()[next + 1];
				if (index == j) {
					return true;
				}
			}

		}

		return false;

	}

	public boolean isPrevInstIINC(int current, int index) {
		int prev = current - 3;
		byte[] info = getMethod().getCode();
		if (isThisInstrStart(prev)) {

			if (info[prev] == JvmOpCodes.IINC) {
				int j = info[prev + 1];
				if (index == j) {
					return true;
				}
			}

		}

		return false;

	}

	public boolean isPreviousInst(int current, int lookfor) {

		for (int s = current - 1; s >= 0; s--) {
			boolean ok = isThisInstrStart(s);
			if (ok) {
				if (s == lookfor) {
					return true;
				}
				return false;
			}
		}

		return false;
	}

	public boolean isNextInstructionAnyInvoke(int pos, StringBuffer sb) {

		if (!isThisInstrStart(pos))
			return false;

		int instr = getMethod().getCode()[pos];
		boolean ok = isNextInstructionInvokeInterface(instr);
		if (ok) {
			sb.append(InstrConstants.INVOKE_INTERFACE);
			return ok;
		}
		ok = isNextInstructionInvokeSpecial(instr);
		if (ok) {
			sb.append(InstrConstants.INVOKE_SPECIAL);
			return ok;
		}
		ok = isNextInstructionInvokeStatic(instr);
		if (ok)
			return ok;
		ok = isNextInstructionInvokeVirtual(instr);
		if (ok)
			return ok;
		return false;
	}

	/**
	 * WARNING : NO CHECK FOR START OF INSTRUCTION !!!
	 * 
	 * @param nextInst
	 * @return
	 */
	public boolean isNextInstructionInvokeInterface(int nextInst) {

		if (nextInst == JvmOpCodes.INVOKEINTERFACE)
			return true;
		else
			return false;
	}

	/**
	 * WARNING : NO CHECK FOR START OF INSTRUCTION !!!
	 * 
	 * @param nextInst
	 * @return
	 */
	public boolean isNextInstructionInvokeSpecial(int nextInst) {

		if (nextInst == JvmOpCodes.INVOKESPECIAL)
			return true;
		else
			return false;
	}

	/**
	 * WARNING : NO CHECK FOR START OF INSTRUCTION !!!
	 * 
	 * @param nextInst
	 * @return
	 */

	public boolean isNextInstructionInvokeStatic(int nextInst) {

		if (nextInst == JvmOpCodes.INVOKESTATIC)
			return true;
		else
			return false;
	}

	/**
	 * WARNING : NO CHECK FOR START OF INSTRUCTION !!!
	 * 
	 * @param nextInst
	 * @return
	 */

	public boolean isNextInstructionInvokeVirtual(int nextInst) {

		if (nextInst == JvmOpCodes.INVOKEVIRTUAL)
			return true;
		else
			return false;
	}

	public boolean isNextInstructionConversionInst(int i) {
		if (!isThisInstrStart(i))
			return false;
		switch (getMethod().getCode()[i]) {

		case JvmOpCodes.D2L:
		case JvmOpCodes.D2I:
		case JvmOpCodes.D2F:
		case JvmOpCodes.I2B:
		case JvmOpCodes.I2C:
		case JvmOpCodes.I2D:
		case JvmOpCodes.I2F:
		case JvmOpCodes.I2L:
		case JvmOpCodes.I2S:
		case JvmOpCodes.L2D:
		case JvmOpCodes.L2F:
		case JvmOpCodes.L2I:
		case JvmOpCodes.F2D:

		case JvmOpCodes.F2I:
		case JvmOpCodes.F2L:
			return true;
		default:
			return false;

		}

	}

	public int isNextInstructionConversionInst(int pos, StringBuffer value) {
		if (!isThisInstrStart(pos))
			return -1;

		switch (getMethod().getCode()[pos]) {

		case JvmOpCodes.D2L:
			value.append("long");

			break;
		case JvmOpCodes.D2I:
			value.append("int");

			break;
		case JvmOpCodes.D2F:
			value.append("float");

			break;
		case JvmOpCodes.I2B:
			value.append("byte");

			break;
		case JvmOpCodes.I2C:
			value.append("char");

			break;
		case JvmOpCodes.I2D:
			value.append("double");

			break;
		case JvmOpCodes.I2F:
			value.append("float");

			break;
		case JvmOpCodes.I2L:
			value.append("long");

			break;
		case JvmOpCodes.I2S:
			value.append("short");

			break;
		case JvmOpCodes.L2D:
			value.append("double");

			break;
		case JvmOpCodes.L2F:
			value.append("float");

			break;
		case JvmOpCodes.L2I:
			value.append("int");

			break;
		case JvmOpCodes.F2D:
			value.append("double");

			break;

		case JvmOpCodes.F2I:
			value.append("int");

			break;
		case JvmOpCodes.F2L:
			value.append("long");

			break;

		default:
			return -1;

		}

		return pos;

	}

	public boolean isNextInstructionPop(int pos) {
		boolean flag = false;

		if (!isThisInstrStart(pos)) {
			return flag;
		}

		switch (getMethod().getCode()[pos]) {
		case JvmOpCodes.POP:
		case JvmOpCodes.POP2:
			return true;
		default:
			return false;

		}
	}

	public boolean newfound() {
		if ((GlobalVariableStore.getNewfoundstack()).size() > 0)
			return true;
		return false;
	}

	/**
	 * No ternary support yet in jdec
	 * 
	 * @param i
	 * @return
	 */
	public boolean isTernaryEnd(int i) {
		return false;
	}

	public int getPrevStartOfInst(int current) {
		int start = current - 1;
		while (start >= 0) {
			boolean s = isThisInstrStart(start);
			if (s)
				return start;
			start--;
		}
		return start;
	}

/*	public boolean isInstPrimitiveArrayStore(int inst) {
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
	}*/

	public boolean checkForSomeSpecificInstructions(byte[] code, int i)

	{
		int inst = code[i];
		switch (inst) {

		// LDC // TODO: Need to Regressoion Testing Here // uncommented bcoz of
		// trytrest_4
		// case JvmOpCodes.LDC2_W:
		// case JvmOpCodes.LDC_W:

		// case JvmOpCodes.ACONST_NULL:
		case JvmOpCodes.DUP:
		case JvmOpCodes.DUP2:
			// case JvmOpCodes.NEW:
		case JvmOpCodes.CHECKCAST:
		case JvmOpCodes.ATHROW:

		case JvmOpCodes.ANEWARRAY:
		case JvmOpCodes.NEWARRAY:
		case JvmOpCodes.MULTIANEWARRAY:

		case JvmOpCodes.LOOKUPSWITCH:
		case JvmOpCodes.TABLESWITCH:
			return true;

		}
		/*
		 * if(inst==JvmOpCodes.LDC &&
		 * (isThisInstrStart(behaviour.getInstructionStartPositions(),(i-3)) &&
		 * code[(i-3)]!=JvmOpCodes.INVOKESPECIAL)) { return true; }
		 */
		if (inst == JvmOpCodes.LDC) {
			return false;
		}
		return false;

	}
}
