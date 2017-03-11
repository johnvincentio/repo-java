/*
 *  IFinder.java Copyright (c) 2006,07 Swaroop Belur 
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
/*
 *  IFinder.java Copyright (c) 2006,07 Swaroop Belur 
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
 * Interface containing all find methods
 * Basically some sort of look up of the
 * concrete implementation is required.
 * Then the returned finder can then be used
 * to find what is exactly required.
 * <p/>
 * Example:
 * <p/>
 * IFinder finder  = FinderFactory.getFinder(IFinder.LOAD);
 * finder.<find_some_load_inst>(<any_params>);
 *
 * @author swaroop belur(belurs)
 * @since 1.2.1
 */

public interface IFinder {


    static final int LOAD = 1;

    static final int STORE = 2;

    static final int TRY = 3;

    static final int BRANCH = 4;

    static final int BASE = 5;

    boolean isCategory1AddSub(int pos);

    boolean isCategory2AddSub(int pos);

    boolean isCurrentInstStore(int pos);

    boolean isGotoPrecededByDUPSTORE(int pos);

    boolean isHandlerEndPresentAtGuardEnd(int i);

    boolean isEndOfGuard(int pos);

    boolean isIfConditionForSomeOtherLoop(int pos, StringBuffer lend);

    boolean isIfFirstIfInLoopCondition(int pos);

    boolean isIfForLoadClass(IFBlock ifst);

    boolean isIFForThisElseATernaryIF(int pos, StringBuffer sb);

    Loop isIfInADoWhile(int pos, IFBlock ifst);

    Switch.Case isIFInCase(int pos, IFBlock ifs);

    boolean isIfPartOfTernaryIfCond(int pos);

    boolean isIFpresentInTernaryList(IFBlock ifst);

    boolean isIFShortcutORComp(int pos);

    boolean isIndexEndOfLoop(int pos);

    boolean isInstrPosAAload(int pos);

    int isInstAload(int pos, StringBuffer bf);

    boolean isInstAnyBasicPrimitiveOperation(int pos, StringBuffer sb);


    boolean isInstAnyCMPInst(int pos);


    int isInstAnyConstInst(int pos);

    int isInstdConstInst(int pos);

    int isInstDloadInst(int pos, StringBuffer sb2);

    boolean isInstDup(int pos);

    int isInstFConstInst(int pos);

    int isInstFloadInst(int pos, StringBuffer sb2);

    int isInstIConstInst(int pos);

    int isInstIloadInst(int pos, StringBuffer sb2);

    int isInstLConstInst(int pos);

    int isInstLloadInst(int pos, StringBuffer sb2);

    boolean isInstLoopStart(int pos);

    boolean isInstPrimitiveArrayStore(int inst);

    boolean isInstReturnInst(int pos, StringBuffer sb);

    boolean isInstructionAnyDUP(int inst);

    boolean isInstructionIF(int instructionpos);

    boolean isInstStore0(int pos);

    boolean isNextInstAStore(int pos);

    boolean isNextInstIINC(int pos, int index, java.lang.String type);

    boolean isNextInstructionAnyInvoke(int pos, StringBuffer sb);

    boolean isNextInstructionConversionInst(int pos);

    public int isNextInstructionConversionInst(int pos, StringBuffer value);

    boolean isNextInstructionInvokeInterface(int nextInst);

    boolean isNextInstructionInvokeSpecial(int nextInst);

    boolean isNextInstructionInvokeStatic(int nextInst);

    boolean isNextInstructionInvokeVirtual(int nextInst);

    boolean isNextInstructionLoad(int pos);

    boolean isNextInstructionPop(int pos);

    boolean isNextInstructionPrimitiveStoreInst(int pos, StringBuffer index);

    boolean isNextInstructionReturn(int nextInstruction);


    boolean isNextInstructionStore(int nextInstruction);


    boolean isPrevInstALOADInst(int pos, StringBuffer s);


    boolean isPrevInstIINC(int current, int index);

    boolean isPrevInstIloadInst(int s, StringBuffer sb2);

    boolean isPrevInstPrimitiveLoad(int pos, StringBuffer sb);

    boolean isPrevInstructionAload(int pos, StringBuffer sb);

    boolean isPreviousInst(int current, int lookfor);

    boolean isStoreInst(int index, StringBuffer varindex, StringBuffer t);
    
    boolean isStoreInst(int index, byte[] info);

    boolean isThisDUPSTOREAtEndOFTernaryIF(int pos, java.lang.String type);

    boolean isThisIfALoopCondition(IFBlock IF);

    boolean isThisInstASTOREInst(int pos, StringBuffer sb);

    boolean isThisInstrStart(int pos);

    boolean isThisInstructionIStoreInst(int s, StringBuffer sb);
    
    boolean isThisInstructionIStoreInst(byte []code,int s,StringBuffer sb);

    boolean isThisLoopEndAlso(int i, int ifstart);

    boolean isThisLoopStart(IFBlock IF);

    boolean isThisTryStart(int i);

    boolean isPrevInstDup(int pos);


    boolean lastIFinShortCutChain(IFBlock ifst, int i);

    int getPrevStartOfInst(int current);

    int getOffset(int counter);
    
    int getJumpAddress(int counter); 

    boolean isInstReturnInst(byte[] code,int pos,StringBuffer sb);
    
    boolean isNextInstructionIf(int nextInstruction);
    
    boolean checkForSomeSpecificInstructions(byte[] code,int i);
}
