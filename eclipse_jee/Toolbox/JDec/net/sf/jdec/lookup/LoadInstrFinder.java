/*
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

package net.sf.jdec.lookup;

import net.sf.jdec.blocks.IFBlock;
import net.sf.jdec.blocks.Loop;
import net.sf.jdec.blocks.Switch;
import net.sf.jdec.core.JvmOpCodes;

/*
 *  LoadInstrFinder.java Copyright (c) 2006,07 Swaroop Belur 
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
 * Implementation specifics:
 * a> Implements all find methods concerning load instructions here.
 * b> throws UnsupportedOperationException for all other find methods.
 *
 * @author swaroop belur(belurs)
 * @version 1.2.1
 */
public class LoadInstrFinder extends BasicFinder {

    public static final LoadInstrFinder loadFinder = new LoadInstrFinder();

    private LoadInstrFinder() {
    }


    public boolean isCurrentInstStore(int pos) {
        throw new UnsupportedOperationException("Invalid call[isCurrentInstStore] on LoadInstrFinder Object");
    }

    public boolean isGotoPrecededByDUPSTORE(int pos) {
        throw new UnsupportedOperationException("Invalid call[isGotoPrecededByDUPSTORE] on LoadInstrFinder Object");
    }

    public boolean isHandlerEndPresentAtGuardEnd(int i) {
        throw new UnsupportedOperationException("Invalid call[isHandlerEndPresentAtGuardEnd] on LoadInstrFinder Object");
    }

    public boolean isEndOfGuard(int pos) {
        throw new UnsupportedOperationException("Invalid call[isEndOfGuard] on LoadInstrFinder Object");
    }

    public boolean isIfConditionForSomeOtherLoop(int pos, StringBuffer lend) {
        throw new UnsupportedOperationException("Invalid call[isIfConditionForSomeOtherLoop] on LoadInstrFinder Object");
    }

    public boolean isIfFirstIfInLoopCondition(int pos) {
        throw new UnsupportedOperationException("Invalid call[isIfFirstIfInLoopCondition] on LoadInstrFinder Object");
    }

    public boolean isIfForLoadClass(IFBlock ifst) {
        throw new UnsupportedOperationException("Invalid call[isIfForLoadClass] on LoadInstrFinder object");
    }

    public boolean isIFForThisElseATernaryIF(int pos, StringBuffer sb) {
        throw new UnsupportedOperationException("Invalid call[isIFForThisElseATernaryIF] on LoadInstrFinder object");
    }

    public Loop isIfInADoWhile(int pos, IFBlock ifst) {
        throw new UnsupportedOperationException("Invalid call[isIfInADoWhile] on LoadInstrFinder object");
    }

    public Switch.Case isIFInCase(int pos, IFBlock ifs) {
        throw new UnsupportedOperationException("Invalid call[isIfInCase] on LoadInstrFinder object");
    }

    public boolean isIfPartOfTernaryIfCond(int pos) {
        throw new UnsupportedOperationException("Invalid call[isIfPartOfTernaryIfCond] on LoadInstrFinder object");
    }

    public boolean isIFpresentInTernaryList(IFBlock ifst) {
        throw new UnsupportedOperationException("Invalid call[isIFpresentInTernaryList] on LoadInstrFinder object");
    }

    public boolean isIFShortcutORComp(int pos) {
        throw new UnsupportedOperationException("Invalid call[isIFShortcutORComp] on LoadInstrFinder object");
    }

    public boolean isIndexEndOfLoop(int pos) {
        throw new UnsupportedOperationException("Invalid call[isIndexEndOfLoop] on LoadInstrFinder object");
    }


    /**
     * Pass the instuction position to search for.
     */
    public boolean isInstrPosAAload(int pos) {

        boolean start = isThisInstrStart(pos);
        if (!start) return false;
        int inst = getMethod().getCode()[pos];
        return inst == JvmOpCodes.AALOAD;
    }


    /**
     * @param pos - position to be initially tested for in the code
     * @param bf  - containing the index of the aload instruction [ ex: 0 in case of aload_0]
     * @return - -1 if not found or pos of instr in code.
     * @
     */
    public int isInstAload(int pos, StringBuffer bf) {
        if (isThisInstrStart(pos)) {
            switch (getMethod().getCode()[pos]) {
                case JvmOpCodes.ALOAD_0:
                    bf.append(0);
                    return pos;
                case JvmOpCodes.ALOAD_1:
                    bf.append(1);
                    return pos;
                case JvmOpCodes.ALOAD_2:
                    bf.append(2);
                    return pos;
                case JvmOpCodes.ALOAD_3:
                    bf.append(3);
                    return pos;
                case JvmOpCodes.ALOAD:
                    bf.append(getMethod().getCode()[(pos + 1)]);
                    return pos;
            }
        }

        int temp = pos - 1;
        if (isThisInstrStart(temp)) {
            if (getMethod().getCode()[temp] == JvmOpCodes.ALOAD) {
                bf.append(getMethod().getCode()[pos]);
                return temp;
            }
        }
        return -1;
    }


    public int isInstIloadInst(int pos, StringBuffer buffer) {
        if (isThisInstrStart(pos)) {
            switch (getMethod().getCode()[pos]) {
                case JvmOpCodes.ILOAD_0:
                    buffer.append(0);
                    return pos;

                case JvmOpCodes.ILOAD_1:

                    buffer.append(1);
                    return pos;

                case JvmOpCodes.ILOAD_2:

                    buffer.append(2);
                    return pos;


                case JvmOpCodes.ILOAD_3:

                    buffer.append(3);
                    return pos;

                case JvmOpCodes.ILOAD:
                    buffer.append(getMethod().getCode()[pos + 1]);
                    return pos;

            }
        }
        int temp = pos - 1;
        if (temp >= 0 && isThisInstrStart(temp) && getMethod().getCode()[temp] == JvmOpCodes.ILOAD) {
            buffer.append(getMethod().getCode()[pos]);
            return temp;

        }


        return -1;


    }


    public int isInstLloadInst(int s, StringBuffer sb2) {
        if (isThisInstrStart(s)) {
            switch (getMethod().getCode()[(s)]) {
                case JvmOpCodes.LLOAD_0:
                    sb2.append(0);
                    return s;

                case JvmOpCodes.LLOAD_1:

                    sb2.append(1);
                    return s;

                case JvmOpCodes.LLOAD_2:

                    sb2.append(2);
                    return s;


                case JvmOpCodes.LLOAD_3:

                    sb2.append(3);
                    return s;

                    /*case JvmOpCodes.ILOAD:
                  sb2.append(code[s+1]);
                   return s;*/

            }
        }
        int temp = s - 1;
        if (isThisInstrStart(temp)) {
            if (getMethod().getCode()[temp] == JvmOpCodes.LLOAD) {
                sb2.append(getMethod().getCode()[s]);
                return temp;

            }
        }


        return -1;


    }

    public boolean isInstLoopStart(int pos) {
        throw new UnsupportedOperationException("Invalid call[isInstLoopStart] on LoadInstrFinder object");
    }

    public boolean isInstPrimitiveArrayStore(int inst) {
        throw new UnsupportedOperationException("Invalid call[isInstPrimitiveArrayStore] on LoadInstrFinder object");
    }

    public boolean isInstReturnInst(int pos, StringBuffer sb) {
        throw new UnsupportedOperationException("Invalid call[isInstReturnInst] on LoadInstrFinder object");
    }


    public boolean isInstructionIF(int instruction) {
        throw new UnsupportedOperationException("Invalid call[isInstructionIF] on LoadInstrFinder object");
    }

    public boolean isInstStore0(int pos) {
        throw new UnsupportedOperationException("Invalid call[isInstStore0] on LoadInstrFinder object");
    }

    public boolean isNextInstAStore(int pos) {
        throw new UnsupportedOperationException("Invalid call[isNextInstAStore] on LoadInstrFinder object");
    }

    public boolean isNextInstructionLoad(int pos) {
        boolean flag = false;
        if (!isThisInstrStart(pos)) return flag;

        switch (getMethod().getCode()[pos]) {

            case JvmOpCodes.BIPUSH:
                flag = true;
                break;

            case JvmOpCodes.SIPUSH:
                flag = true;
                break;


            case JvmOpCodes.AALOAD:
                flag = true;
                break;

            case JvmOpCodes.BALOAD:
                flag = true;
                break;

            case JvmOpCodes.DALOAD:
                flag = true;
                break;

            case JvmOpCodes.FALOAD:
                flag = true;
                break;

            case JvmOpCodes.LALOAD:
                flag = true;
                break;

            case JvmOpCodes.IALOAD:
                flag = true;
                break;

            case JvmOpCodes.SALOAD:
                flag = true;
                break;

            case JvmOpCodes.CALOAD:
                flag = true;
                break;


            case JvmOpCodes.ALOAD:
                flag = true;
                break;
            case JvmOpCodes.ALOAD_0:
                flag = true;
                break;
            case JvmOpCodes.ALOAD_1:
                flag = true;
                break;
            case JvmOpCodes.ALOAD_2:
                flag = true;
                break;
            case JvmOpCodes.ALOAD_3:
                flag = true;
                break;
            case JvmOpCodes.ILOAD:
                flag = true;
                break;
            case JvmOpCodes.ILOAD_0:
                flag = true;
                break;
            case JvmOpCodes.ILOAD_1:
                flag = true;
                break;
            case JvmOpCodes.ILOAD_2:
                flag = true;
                break;
            case JvmOpCodes.ILOAD_3:
                flag = true;
                break;

            case JvmOpCodes.LLOAD:
                flag = true;
                break;
            case JvmOpCodes.LLOAD_0:
                flag = true;
                break;
            case JvmOpCodes.LLOAD_1:
                flag = true;
                break;
            case JvmOpCodes.LLOAD_2:
                flag = true;
                break;
            case JvmOpCodes.LLOAD_3:
                flag = true;
                break;

            case JvmOpCodes.FLOAD:
                flag = true;
                break;
            case JvmOpCodes.FLOAD_0:
                flag = true;
                break;
            case JvmOpCodes.FLOAD_1:
                flag = true;
                break;
            case JvmOpCodes.FLOAD_2:
                flag = true;
                break;
            case JvmOpCodes.FLOAD_3:
                flag = true;
                break;

            case JvmOpCodes.DLOAD:
                flag = true;
                break;
            case JvmOpCodes.DLOAD_0:
                flag = true;
                break;
            case JvmOpCodes.DLOAD_1:
                flag = true;
                break;
            case JvmOpCodes.DLOAD_2:
                flag = true;
                break;
            case JvmOpCodes.DLOAD_3:
                flag = true;
                break;
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
                flag = true;
                break;


        }

        return flag;
    }

    public boolean isNextInstructionPrimitiveStoreInst(int pos, StringBuffer index) {
        throw new UnsupportedOperationException("Invalid call[isNextInstructionPrimitiveStoreInst] on LoadInstrFinder object");
    }

    public boolean isNextInstructionReturn(int nextInstruction) {
        throw new UnsupportedOperationException("Invalid call[isNextInstructionReturn] on LoadInstrFinder object");
    }

    public boolean isNextInstructionStore(int nextInstruction) {
        throw new UnsupportedOperationException("Invalid call[isNextInstructionStore] on LoadInstrFinder object");
    }

    public boolean isPrevInstALOADInst(int pos, StringBuffer s) {
        if (isThisInstrStart((pos - 1))) {

            switch (getMethod().getCode()[(pos - 1)]) {

                case JvmOpCodes.ALOAD_0:
                    s.append(0);
                    return true;

                case JvmOpCodes.ALOAD_1:
                    s.append(1);
                    return true;

                case JvmOpCodes.ALOAD_2:
                    s.append(2);
                    return true;

                case JvmOpCodes.ALOAD_3:
                    s.append(3);
                    return true;

            }

        }
        if (isThisInstrStart((pos - 2))) {

            switch (getMethod().getCode()[(pos - 2)]) {
                case JvmOpCodes.ALOAD:
                    s.append(getMethod().getCode()[(pos - 1)]);
                    return true;

            }
        }
        return false;
    }


    public boolean isPrevInstIloadInst(int s, StringBuffer sb2) {

        byte[] code = getMethod().getCode();
        boolean b = false;
        if (isThisInstrStart((s - 1))) {
            switch (code[(s - 1)]) {
                case JvmOpCodes.ILOAD_0:
                    b = true;
                    sb2.append(0);
                    break;
                case JvmOpCodes.ILOAD_1:
                    b = true;
                    sb2.append(1);
                    break;
                case JvmOpCodes.ILOAD_2:
                    b = true;
                    sb2.append(2);
                    break;

                case JvmOpCodes.ILOAD_3:
                    b = true;
                    sb2.append(3);
                    break;

            }
        }

        if (!b && (s - 2) >= 0 && isThisInstrStart((s - 2))) {

            switch (code[s - 2]) {

                case JvmOpCodes.ILOAD:
                    b = true;
                    sb2.append(code[s - 1]);
                    break;
                default:
                    b = false;
                    break;

            }
        }

        return b;
    }

    public boolean isPrevInstPrimitiveLoad(int pos, StringBuffer sb) {
        boolean flag = false;
        byte[] c = getMethod().getCode();
        if (isThisInstrStart((pos - 1))) {
            switch (c[(pos - 1)]) {

                case JvmOpCodes.ILOAD_0:
                    flag = true;
                    break;
                case JvmOpCodes.ILOAD_1:
                    flag = true;
                    break;
                case JvmOpCodes.ILOAD_2:
                    flag = true;
                    break;
                case JvmOpCodes.ILOAD_3:
                    flag = true;
                    break;


                case JvmOpCodes.LLOAD_0:
                    flag = true;
                    break;
                case JvmOpCodes.LLOAD_1:
                    flag = true;
                    break;
                case JvmOpCodes.LLOAD_2:
                    flag = true;
                    break;
                case JvmOpCodes.LLOAD_3:
                    flag = true;
                    break;


                case JvmOpCodes.FLOAD_0:
                    flag = true;
                    break;
                case JvmOpCodes.FLOAD_1:
                    flag = true;
                    break;
                case JvmOpCodes.FLOAD_2:
                    flag = true;
                    break;
                case JvmOpCodes.FLOAD_3:
                    flag = true;
                    break;


                case JvmOpCodes.DLOAD_0:
                    flag = true;
                    break;
                case JvmOpCodes.DLOAD_1:
                    flag = true;
                    break;
                case JvmOpCodes.DLOAD_2:
                    flag = true;
                    break;
                case JvmOpCodes.DLOAD_3:
                    flag = true;
                    break;

                default:
                    flag = false;
                    break;

            }
        }

        if (flag) sb.append((pos - 1));
        if (!flag) {
            if (isThisInstrStart((pos - 2))) {
                switch (c[(pos - 2)]) {
                    case JvmOpCodes.ILOAD:
                        flag = true;
                        break;
                    case JvmOpCodes.LLOAD:
                        flag = true;
                        break;

                    case JvmOpCodes.FLOAD:
                        flag = true;
                        break;

                    case JvmOpCodes.DLOAD:
                        flag = true;
                        break;
                    default:
                        flag = false;
                        break;
                }
                if (flag) sb.append((pos - 2));
            }

        }

        return flag;
    }


    public boolean isPrevInstructionAload(int pos, StringBuffer sb) {
        boolean present = false;
        int pos1 = pos - 1;
        int pos2 = pos - 2;
        int jvmInst_1 = -1;
        int jvmInst_2 = -1;
        if (pos1 >= 0)
            jvmInst_1 = getMethod().getCode()[pos1];
        if (pos2 >= 0)
            jvmInst_2 = getMethod().getCode()[pos2];
        if (pos1 != -1) {
            switch (jvmInst_1) {

                case JvmOpCodes.ALOAD_0:
                case JvmOpCodes.ALOAD_1:
                case JvmOpCodes.ALOAD_2:
                case JvmOpCodes.ALOAD_3:
                    present = true;
                    break;
                default:
                    present = false;
                    break;

            }
        }
        if (present) sb.append(pos1);
        if (present == false) {

            if (jvmInst_2 == JvmOpCodes.ALOAD) {
                present = true;
                sb.append(pos2);
            }

        }

        return present;
    }


    public boolean isStoreInst(int index) {
        throw new UnsupportedOperationException("Invalid call[isStoreInst] on LoadInstrFinder object");
    }

    public boolean isStoreInst(int index, StringBuffer varindex, StringBuffer t) {
        throw new UnsupportedOperationException("Invalid call[isStoreInst] on LoadInstrFinder object");
    }

    public boolean isThisDUPSTOREAtEndOFTernaryIF(int pos, String type) {
        throw new UnsupportedOperationException("Invalid call[isThisDUPSTOREAtEndOFTernaryIF] on LoadInstrFinder object");
    }

    public boolean isThisIfALoopCondition(IFBlock IF) {
        throw new UnsupportedOperationException("Invalid call[isThisIfALoopCondition] on LoadInstrFinder object");
    }

    public boolean isThisInstASTOREInst(int pos, StringBuffer sb) {
        throw new UnsupportedOperationException("Invalid call[isThisInstASTOREInst] on LoadInstrFinder object");
    }

    public boolean isThisInstructionIStoreInst(int s, StringBuffer sb) {
        throw new UnsupportedOperationException("Invalid call[isThisInstructionIStoreInst] on LoadInstrFinder object");
    }

    public boolean isThisLoopEndAlso(int i, int ifstart) {
        throw new UnsupportedOperationException("Invalid call[isThisLoopEndAlso] on LoadInstrFinder object");
    }

    public boolean isThisLoopStart(IFBlock IF) {
        throw new UnsupportedOperationException("Invalid call[isThisLoopStart] on LoadInstrFinder object");
    }

    public boolean isThisTryStart(int i) {
        throw new UnsupportedOperationException("Invalid call[isThisTryStart] on LoadInstrFinder object");
    }

    public boolean lastIFinShortCutChain(IFBlock ifst, int i) {
        throw new UnsupportedOperationException("Invalid call[lastIFinShortCutChain] on LoadInstrFinder object");
    }


	public boolean isInstReturnInst(byte[] code, int pos, StringBuffer sb) {
		 throw new UnsupportedOperationException();
	}


	public boolean isNextInstructionIf(int nextInstruction) {
		throw new UnsupportedOperationException();
	}


	public boolean isStoreInst(int index, byte[] info) {
		throw new UnsupportedOperationException();
	}


	public boolean isThisInstructionIStoreInst(byte[] code, int s, StringBuffer sb) {
		throw new UnsupportedOperationException();
	}
}