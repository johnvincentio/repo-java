package net.sf.jdec.lookup;

import java.util.ArrayList;
import java.util.Iterator;

import net.sf.jdec.blocks.CatchBlock;
import net.sf.jdec.blocks.IFBlock;
import net.sf.jdec.blocks.Loop;
import net.sf.jdec.blocks.Switch;
import net.sf.jdec.blocks.TryBlock;

/*
 *  TryInstrFinder.java Copyright (c) 2006,07 Swaroop Belur 
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
 * a> Implements all find methods concerning exception handling related instructions here.
 * [i.e guard regions and handler regions - Read as try / catch / finally ]
 * b> throws UnsupportedOperationException for all other find methods.
 *
 * @author swaroop belur(belurs)
 * @version 1.2.1
 */

public class TryInstrFinder extends BasicFinder {

    public static final TryInstrFinder tryFinder = new TryInstrFinder();

    private TryInstrFinder() {
    }

    public boolean isCurrentInstStore(int pos) {
        throw new UnsupportedOperationException("Invalid call[isCurrentInstStore] on TryInstrFinder Object");
    }

    public boolean isGotoPrecededByDUPSTORE(int pos) {
        throw new UnsupportedOperationException("Invalid call[isGotoPrecededByDUPSTORE] on TryInstrFinder Object");
    }

    /*
    TODO : Need to rethink why this method was introduced
      Commented for 1.2 release Feb 25 2007
    */
    /*Commented for 1.2 release Feb 25 2007  */
    public boolean isHandlerEndPresentAtGuardEnd(int i) {
        return false;
    }

    public boolean isEndOfGuard(int i) {
        boolean end = false;
        ArrayList alltries = getMethod().getAllTriesForMethod();
        Iterator it = alltries.iterator();
        while (it.hasNext()) {

            TryBlock Try = (TryBlock) it.next();
            if (Try != null) {
                int endoftry = Try.getEnd();
                if (endoftry == i)  // check for try's end
                {
                    end = true;
                    break;
                } else   // Check for catches
                {
                    ArrayList catches = Try.getAllCatchesForThisTry();
                    for (int s = 0; s < catches.size(); s++) {
                        CatchBlock catchblk = (CatchBlock) catches.get(s);
                        if (catchblk != null) {
                            int catchend = catchblk.getEnd();
                            if (catchend == i) {
                                end = true;
                                break;
                            }
                        }

                    }
                }
            }
        }
        return end;
    }

    public boolean isIfConditionForSomeOtherLoop(int pos, StringBuffer lend) {
        throw new UnsupportedOperationException("Invalid call[isIfConditionForSomeOtherLoop] on TryInstrFinder Object");
    }

    public boolean isIfFirstIfInLoopCondition(int pos) {
        throw new UnsupportedOperationException("Invalid call[isIfFirstIfInLoopCondition] on TryInstrFinder Object");
    }

    public boolean isIfForLoadClass(IFBlock ifst) {
        throw new UnsupportedOperationException("Invalid call[isIfForLoadClass] on TryInstrFinder Object");
    }

    public boolean isIFForThisElseATernaryIF(int pos, StringBuffer sb) {
        throw new UnsupportedOperationException("Invalid call[isIFForThisElseATernaryIF] on TryInstrFinder Object");
    }

    public Loop isIfInADoWhile(int pos, IFBlock ifst) {
        throw new UnsupportedOperationException("Invalid call[isIfInADoWhile] on TryInstrFinder Object");
    }

    public Switch.Case isIFInCase(int pos, IFBlock ifs) {
        throw new UnsupportedOperationException("Invalid call[isIFInCase] on TryInstrFinder Object");
    }

    public boolean isIfPartOfTernaryIfCond(int pos) {
        throw new UnsupportedOperationException("Invalid call[isIfPartOfTernaryIfCond] on TryInstrFinder Object");
    }

    public boolean isIFpresentInTernaryList(IFBlock ifst) {
        throw new UnsupportedOperationException("Invalid call[isIFpresentInTernaryList] on TryInstrFinder Object");
    }

    public boolean isIFShortcutORComp(int pos) {
        throw new UnsupportedOperationException("Invalid call[isIFShortcutORComp] on TryInstrFinder Object");
    }

    public boolean isIndexEndOfLoop(int pos) {
        throw new UnsupportedOperationException("Invalid call[isIndexEndOfLoop] on TryInstrFinder Object");
    }

    public boolean isInstrPosAAload(int pos) {
        throw new UnsupportedOperationException("Invalid call[isInstrPosAAload] on TryInstrFinder Object");
    }

    public int isInstAload(int pos, StringBuffer bf) {
        throw new UnsupportedOperationException("Invalid call[isInstAload] on TryInstrFinder Object");
    }


    public int isInstFloadInst(int pos, StringBuffer sb2) {
        throw new UnsupportedOperationException("Invalid call[isInstFloadInst] on TryInstrFinder Object");
    }


    public int isInstIloadInst(int pos, StringBuffer sb2) {
        throw new UnsupportedOperationException("Invalid call[isInstIloadInst] on TryInstrFinder Object");
    }

    public int isInstLloadInst(int pos, StringBuffer sb2) {
        throw new UnsupportedOperationException("Invalid call[isInstLloadInst] on TryInstrFinder Object");
    }

    public boolean isInstLoopStart(int pos) {
        throw new UnsupportedOperationException("Invalid call[isInstLoopStart] on TryInstrFinder Object");
    }

    public boolean isInstPrimitiveArrayStore(int inst) {
        throw new UnsupportedOperationException("Invalid call[isInstPrimitiveArrayStore] on TryInstrFinder Object");
    }

    public boolean isInstReturnInst(int pos, StringBuffer sb) {
        throw new UnsupportedOperationException("Invalid call[isInstReturnInst] on TryInstrFinder Object");
    }

    public boolean isInstructionIF(int instruction) {
        throw new UnsupportedOperationException("Invalid call[isInstructionIF] on TryInstrFinder Object");
    }

    public boolean isInstStore0(int pos) {
        throw new UnsupportedOperationException("Invalid call[isInstStore0] on TryInstrFinder Object");
    }

    public boolean isNextInstAStore(int pos) {
        throw new UnsupportedOperationException("Invalid call[isNextInstAStore] on TryInstrFinder Object");
    }

    public boolean isNextInstructionLoad(int nextInstruction) {
        throw new UnsupportedOperationException("Invalid call[isNextInstructionLoad] on TryInstrFinder Object");
    }

    public boolean isNextInstructionPrimitiveStoreInst(int pos, StringBuffer index) {
        throw new UnsupportedOperationException("Invalid call[isNextInstructionPrimitiveStoreInst] on TryInstrFinder Object");
    }

    public boolean isNextInstructionReturn(int nextInstruction) {
        throw new UnsupportedOperationException("Invalid call[isNextInstructionReturn] on TryInstrFinder Object");
    }

    public boolean isNextInstructionStore(int nextInstruction) {
        throw new UnsupportedOperationException("Invalid call[isNextInstructionStore] on TryInstrFinder Object");
    }

    public boolean isPrevInstALOADInst(int pos, StringBuffer s) {
        throw new UnsupportedOperationException("Invalid call[isPrevInstALOADInst] on TryInstrFinder Object");
    }


    public boolean isPrevInstIloadInst(int s, StringBuffer sb2) {
        throw new UnsupportedOperationException("Invalid call[isPrevInstIloadInst] on TryInstrFinder Object");
    }

    public boolean isPrevInstPrimitiveLoad(int pos, StringBuffer sb) {
        throw new UnsupportedOperationException("Invalid call[isPrevInstPrimitiveLoad] on TryInstrFinder Object");
    }

    public boolean isPrevInstructionAload(int pos, StringBuffer sb) {
        throw new UnsupportedOperationException("Invalid call[isPrevInstructionAload] on TryInstrFinder Object");
    }

    public boolean isPrevInstructionAload(int pos) {
        throw new UnsupportedOperationException("Invalid call[isPrevInstructionAload] on TryInstrFinder Object");
    }


    public boolean isStoreInst(int index) {
        throw new UnsupportedOperationException("Invalid call[isStoreInst] on TryInstrFinder Object");
    }

    public boolean isStoreInst(int index, StringBuffer varindex, StringBuffer t) {
        throw new UnsupportedOperationException("Invalid call[isStoreInst] on TryInstrFinder Object");
    }

    public boolean isThisDUPSTOREAtEndOFTernaryIF(int pos, String type) {
        throw new UnsupportedOperationException("Invalid call[isThisDUPSTOREAtEndOFTernaryIF] on TryInstrFinder Object");
    }

    public boolean isThisIfALoopCondition(IFBlock IF) {
        throw new UnsupportedOperationException("Invalid call[isThisIfALoopCondition] on TryInstrFinder Object");
    }

    public boolean isThisInstASTOREInst(int pos, StringBuffer sb) {
        throw new UnsupportedOperationException("Invalid call[isThisInstASTOREInst] on TryInstrFinder Object");
    }

    public boolean isThisInstructionIStoreInst(int s, StringBuffer sb) {
        throw new UnsupportedOperationException("Invalid call[isThisInstructionIStoreInst] on TryInstrFinder Object");
    }

    public boolean isThisLoopEndAlso(int i, int ifstart) {
        throw new UnsupportedOperationException("Invalid call[isThisLoopEndAlso] on TryInstrFinder Object");
    }

    public boolean isThisLoopStart(IFBlock IF) {
        throw new UnsupportedOperationException("Invalid call[isThisLoopStart] on TryInstrFinder Object");
    }

    public boolean isThisTryStart(int i) {
        ArrayList tries = getMethod().getAllTriesForMethod();
        if (tries != null) {
            for (int z = 0; z < tries.size(); z++) {

                TryBlock aTry = (TryBlock) tries.get(z);
                if (aTry.getEnd() == i) return true;
            }
        }
        return false;
    }

    public boolean lastIFinShortCutChain(IFBlock ifst, int i) {
    	throw new UnsupportedOperationException();
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

