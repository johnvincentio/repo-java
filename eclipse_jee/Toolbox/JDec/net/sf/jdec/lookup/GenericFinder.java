package net.sf.jdec.lookup;

import net.sf.jdec.blocks.IFBlock;
import net.sf.jdec.blocks.Loop;
import net.sf.jdec.blocks.Switch;
import net.sf.jdec.exceptions.ApplicationException;

/*
*  GenericFinder.java Copyright (c) 2006,07 Swaroop Belur
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
/***
 * @author swaroop belur
 * @since 1.2.1
 */
public class GenericFinder extends BasicFinder{

    public  static final GenericFinder genericFinder = new GenericFinder();

    private GenericFinder(){}

    public boolean isCurrentInstStore(int pos) throws ApplicationException {
        throw new UnsupportedOperationException();
    }

    public boolean isGotoPrecededByDUPSTORE(int pos) throws ApplicationException {
        throw new UnsupportedOperationException();
    }

    public boolean isHandlerEndPresentAtGuardEnd(int i) throws ApplicationException {
        throw new UnsupportedOperationException();
    }

    public boolean isEndOfGuard(int pos) throws ApplicationException {
        throw new UnsupportedOperationException();
    }

    public boolean isIfConditionForSomeOtherLoop(int pos, StringBuffer lend) throws ApplicationException {
        throw new UnsupportedOperationException();
    }

    public boolean isIfFirstIfInLoopCondition(int pos) throws ApplicationException {
        throw new UnsupportedOperationException();
    }

    public boolean isIfForLoadClass(IFBlock ifst) throws ApplicationException {
        throw new UnsupportedOperationException();
    }

    public boolean isIFForThisElseATernaryIF(int pos, StringBuffer sb) throws ApplicationException {
        throw new UnsupportedOperationException();
    }

    public Loop isIfInADoWhile(int pos, IFBlock ifst) throws ApplicationException {
        throw new UnsupportedOperationException();
    }

    public Switch.Case isIFInCase(int pos, IFBlock ifs) throws ApplicationException {
        throw new UnsupportedOperationException();
    }

    public boolean isIfPartOfTernaryIfCond(int pos) throws ApplicationException {
        throw new UnsupportedOperationException();
    }

    public boolean isIFpresentInTernaryList(IFBlock ifst) throws ApplicationException {
        throw new UnsupportedOperationException();
    }

    public boolean isIFShortcutORComp(int pos) throws ApplicationException {
        throw new UnsupportedOperationException();
    }

    public boolean isIndexEndOfLoop(int pos) throws ApplicationException {
        throw new UnsupportedOperationException();
    }

    public boolean isInstrPosAAload(int pos) throws ApplicationException {
        throw new UnsupportedOperationException();
    }

    public int isInstAload(int pos, StringBuffer bf) throws ApplicationException {
        throw new UnsupportedOperationException();
    }

    public int isInstIloadInst(int pos, StringBuffer sb2) {
        throw new UnsupportedOperationException();
    }

    public int isInstLloadInst(int pos, StringBuffer sb2) {
        throw new UnsupportedOperationException();
    }

    public boolean isInstLoopStart(int pos) {
        throw new UnsupportedOperationException();
    }

    public boolean isInstPrimitiveArrayStore(int inst) {
        throw new UnsupportedOperationException();
    }

    public boolean isInstReturnInst(int pos, StringBuffer sb) {
        throw new UnsupportedOperationException();
    }

    public boolean isInstructionIF(int instruction) {
        throw new UnsupportedOperationException();
    }

    public boolean isInstStore0(int pos) {
        throw new UnsupportedOperationException();
    }

    public boolean isNextInstAStore(int pos) {
        throw new UnsupportedOperationException();
    }

    public boolean isNextInstructionLoad(int pos) {
        throw new UnsupportedOperationException();
    }

    public boolean isNextInstructionPrimitiveStoreInst(int pos, StringBuffer index) {
        throw new UnsupportedOperationException();
    }

    public boolean isNextInstructionReturn(int nextInstruction) {
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

    public boolean isStoreInst(int index, StringBuffer varindex, StringBuffer t) {
        throw new UnsupportedOperationException();
    }

    public boolean isThisDUPSTOREAtEndOFTernaryIF(int pos, String type) {
        throw new UnsupportedOperationException();
    }

    public boolean isThisIfALoopCondition(IFBlock IF) {
        throw new UnsupportedOperationException();
    }

    public boolean isThisInstASTOREInst(int pos, StringBuffer sb) {
        throw new UnsupportedOperationException();
    }

    public boolean isThisInstructionIStoreInst(int s, StringBuffer sb) {
        throw new UnsupportedOperationException();
    }

    public boolean isThisLoopEndAlso(int i, int ifstart) {
        throw new UnsupportedOperationException();
    }

    public boolean isThisLoopStart(IFBlock IF) {
        throw new UnsupportedOperationException();
    }

    public boolean isThisTryStart(int i) {
        throw new UnsupportedOperationException();
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

	public boolean isThisInstructionIStoreInst(byte[] code, int s, StringBuffer sb) {
		throw new UnsupportedOperationException();
	}

	public boolean isStoreInst(int index, byte[] info) {
		throw new UnsupportedOperationException();
	}
}
