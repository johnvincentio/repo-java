package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.blockhelpers.BranchHelper;
import net.sf.jdec.blockhelpers.IFHelper;
import net.sf.jdec.blockhelpers.LoopHelper;
import net.sf.jdec.blockhelpers.TryHelper;
import net.sf.jdec.blocks.IFBlock;
import net.sf.jdec.blocks.Switch.Case;
import net.sf.jdec.constantpool.ClassDescription;
import net.sf.jdec.core.DecompilerHelper;
import net.sf.jdec.core.GlobalVariableStore;
import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.core.Operand;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class GotoCommand extends AbstractInstructionCommand {

    public GotoCommand(Behaviour context) {
        super(context);

    }

    public int getSkipBytes() {
        return 2;
    }

    public void execute() {
        Behaviour behavior = getContext();

        int currentForIndex = getCurrentInstPosInCode();
        int i = currentForIndex;
        int instructionPos = i;
        int thisGotoJump = -1;
        byte[] info = getCode();
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
        StringBuffer tend = new StringBuffer("");
        Set methodifs = getContext().getMethodIfs();
        int elseCloseLineNo = GlobalVariableStore.getElseCloseLineNo();
        boolean isIfInScope = GlobalVariableStore.isIfInScope();
        ClassDescription cd = getContext().getClassRef().getCd();
        boolean ternaryCond = false;// isIFForThisElseATernaryIF(currentForIndex,
        // methodifs, tend, info);

        if (ternaryCond) {
            boolean prec = DecompilerHelper.isGotoPrecededByDUPSTORE(
                    currentForIndex, info);
            java.lang.String val;
            if (!prec) {
                Operand f = getStack().getTopOfStack();
                Operand f1 = getStack().getTopOfStack();

                if (tend.toString().equals("end") == false)
                    val = f1.getOperandValue() + f.getOperandValue() + ":";
                else
                    val = f1.getOperandValue() + f.getOperandValue() + ")";
            } else {
                Operand f = getStack().getTopOfStack();
                if (tend.toString().equals("end") == false)
                    val = f.getOperandValue() + ":";
                else
                    val = f.getOperandValue() + ")";
            }
            Operand res = new Operand();
            res.setOperandValue(val);
            getStack().push(res);

        }
        if (isIfInScope && !ternaryCond) {
            Iterator iterIfHash = methodifs.iterator();
          whileloop:  
            while (iterIfHash.hasNext()) {
                IFBlock ifs = (IFBlock) iterIfHash.next();
                boolean prevGotoPresent = false;
                int if_start = ifs.getIfStart();
                boolean loop_start = getBranchFinder().isThisLoopStart(ifs);
                int thisifclose = ifs.getIfCloseLineNumber();
                boolean donotgeneratelese = false;
                if (getGenericFinder().isThisInstrStart((thisifclose - 3))
                        && getBranchFinder().isInstructionIF(
                        thisifclose - 3)) {
                    donotgeneratelese = true;
                }
                if ((ifs.getIfCloseLineNumber() - (i - 2)) == 0 && !loop_start
                        && !donotgeneratelese && !ifs.getDonotclose()) { // Removed
                    // Math.abs
                    // :
                    // belurs
                    elseCloseLineNo = indexInst;
                    // System.out.println("elseCloseLineNo "+elseCloseLineNo);
                    java.lang.String checkAgain = "";
                    // if(elseCloseLineNo > i)
                    // {

                    Object ifsSorted[] = IFHelper.sortIFStructures();
                    IFBlock parent = IFHelper.getParentBlock(ifsSorted, ifs
                            .getIfStart());
                    ArrayList loopList = getContext().getBehaviourLoops();
                    int loopSize = loopList.size();
                    java.lang.String checkSwitch = "";
                    StringBuffer again = new StringBuffer("");
                    if (parent == null) {
                        ifs.setHasElse(true);
                        if (loopSize > 0) {
                            ifs.setElseCloseLineNumber(elseCloseLineNo);
                            checkAgain = "true";
                        } else
                            ifs.setElseCloseLineNumber(elseCloseLineNo);

                        // ifs.setHasMatchingElseBeenGenerated(true);
                        if (!checkAgain.equalsIgnoreCase("true")
                                && getContext().getAllSwitchBlks() != null
                                && getContext().getAllSwitchBlks().size() > 0)
                            checkSwitch = "true";
                        elseCloseLineNo = ifs.getElseCloseLineNumber();
                    }

                    else {
                        ifs.setHasElse(true);
                        ifs.setElseCloseLineNumber(elseCloseLineNo); // Check
                        // This
                        // logic
                        // ifs.setHasMatchingElseBeenGenerated(true);

                        int tmpend = IFHelper.checkElseCloseLineNumber(
                                ifsSorted, parent, ifs, ifs.getIfStart(),
                                elseCloseLineNo, again);
                        if (tmpend != -1 && !again.toString().equals("true")) {
                            ifs.setElseCloseLineNumber(tmpend);
                            elseCloseLineNo = ifs.getElseCloseLineNumber();
                        }

                    }
                    if (checkAgain.equals("true")
                            || again.toString().equals("true")) {
                        elseCloseLineNo = ifs.getElseCloseLineNumber(); //
                        elseCloseLineNo = IFHelper.resetElseCloseNumber(
                                loopList, ifs, currentForIndex);
                        ifs.setElseCloseLineNumber(elseCloseLineNo);

                    }
                    ArrayList switches = getContext().getAllSwitchBlks();

                    int newelseend = -1;
                    if (switches != null && switches.size() > 0) {
                        newelseend = IFHelper.resetEndofIFElseWRTSwitch(
                                switches, ifs, ifs.getElseCloseLineNumber(),
                                currentForIndex, "else");
                        boolean valid = IFHelper.isNewEndValid(newelseend, ifs,
                                "else", ifs.getElseCloseLineNumber());
                        if (valid) {
                            ifs.setElseCloseLineNumber(newelseend);
                            elseCloseLineNo = ifs.getElseCloseLineNumber();
                        }
                    }

                    //
                    if (GlobalVariableStore.getContinue_JumpOffsets().size() > 0) {
                        elseCloseLineNo = IFHelper.resetElseCloseNumber(
                                currentForIndex, elseCloseLineNo);
                        ifs.setElseCloseLineNumber(elseCloseLineNo);
                    }

                    if (elseCloseLineNo < (currentForIndex + 3)
                            && getContext().getAllSwitchBlks() != null
                            && getContext().getAllSwitchBlks().size() > 0) {
                        Case caseblk = null;
                        caseblk = IFHelper.isIFInCase(getContext(),
                                currentForIndex, ifs);
                        if (caseblk != null) {
                            elseCloseLineNo = IFHelper.getElseEndwrtcaseblk(
                                    caseblk, info, currentForIndex + 3);
                            ifs.setElseCloseLineNumber(elseCloseLineNo);
                        }

                    }
                    // }
                    /*
                          * else if(elseCloseLineNo < i) // TODO: DOUBLE CHECK IF
                          * THIS IS REQD!!! { elseCloseLineNo =
                          * findElseCloseLineNumber(i,elseCloseLineNo,info); }
                          */
                    // TOFIX Problem of generating an else when it is not reqd
                    boolean loopEndalso = LoopHelper.isThisLoopEndAlso(
                            getContext().getBehaviourLoops(), currentForIndex,
                            ifs.getIfStart());
                    if (loopEndalso)
                        ifs.setElseCloseLineNumber(-1);
                    ArrayList gotos = cd.getGotoStarts();
                    ArrayList gotoj = cd.getGotojumps();
                    boolean skipElse = IFHelper.skipGeneratingElse(gotos,
                            gotoj, currentForIndex, ifs);
                    if (skipElse)
                        ifs.setElseCloseLineNumber(-1);



                        if (!loopEndalso && !skipElse){
                            if ((elseCloseLineNo > ifs.getIfCloseLineNumber()) == false) {

                                elseCloseLineNo = IFHelper
                                        .getElseCloseFromInRangeIfStructures(ifs,
                                                currentForIndex);
                                if (elseCloseLineNo != -1) {
                                    ifs.setElseCloseLineNumber(elseCloseLineNo);
                                }
                            }
                            if (elseCloseLineNo == -1) {
                                elseCloseLineNo = IFHelper
                                        .checkElseCloseWRTAnyParentLoop(ifs,
                                                currentForIndex, info);
                            }
                            boolean addelsestart = IFHelper
                                    .addElseStart(currentForIndex);
                            if (elseCloseLineNo == -1) {
                                ArrayList lps = getContext().getBehaviourLoops();
                                if (lps != null && lps.size() > 0) {
                                    Object[] sortedLoops = LoopHelper
                                            .sortLoops(lps);
                                    int parentLoopStart = LoopHelper
                                            .getParentLoopStartForIf(sortedLoops,
                                                    ifs.getIfStart());
                                    if (parentLoopStart == -1) {
                                        int by = ifs.getIfCloseFromByteCode();
                                        if (by > currentForIndex) {
                                            for (int z1 = by; z1 < info.length; z1++) {
                                                if (getGenericFinder()
                                                        .isThisInstrStart(z1)) {
                                                    if (info[z1] == JvmOpCodes.ATHROW) {
                                                        elseCloseLineNo = z1;
                                                        break;
                                                    }
                                                    boolean retb = BranchHelper
                                                            .checkForReturn(info,
                                                                    z1);
                                                    if (retb) {
                                                        elseCloseLineNo = z1;
                                                        break;
                                                    }
                                                }
                                            }

                                            if (elseCloseLineNo != -1
                                                    && elseCloseLineNo > thisGotoJump
                                                    && thisGotoJump > currentForIndex) {
                                                elseCloseLineNo = thisGotoJump;
                                            }

                                        }
                                    }
                                }
                            }
                            if (addelsestart) {
                                addelsestart = IFHelper.checkForInvalidElse(currentForIndex,
                                        ifs, info);
                            }
                            if (addelsestart) {
                                if (elseCloseLineNo == thisGotoJump) {
                                    elseCloseLineNo = TryHelper.checkForElseEndWRTExcepionTables(
                                            ifs, elseCloseLineNo);
                                }
                            }
                            if (addelsestart) {

                                IFBlock parElseBlock = IFHelper.getParentElseBlockForThisElse(
                                        currentForIndex, info);
                                if (parElseBlock != null) {
                                    boolean elseg = parElseBlock
                                            .hasMatchingElseBeenGenerated();
                                    int ec = parElseBlock.getElseCloseLineNumber();
                                    int ifcl = parElseBlock.getIfCloseLineNumber();
                                    if (elseg) {
                                        if (info[ifcl] == JvmOpCodes.GOTO) {
                                            if ((ec - 3) == currentForIndex) {
                                                addelsestart = false;
                                            }
                                        }
                                    }
                                }

                            }
                            if (addelsestart && elseCloseLineNo == -1) {
                                elseCloseLineNo = thisGotoJump;
                            }
                            if (addelsestart
                                    && elseCloseLineNo != -1
                                    && elseCloseLineNo != ifs
                                    .getIfCloseLineNumber()
                                    && elseCloseLineNo > ifs.getIfCloseLineNumber()) // changed
                            // by
                            // belurs
                            {
                                boolean breaks = IFHelper.checkForBreakInAssociatedIf(ifs);
                                if (!breaks) {
                                    ifs.setHasMatchingElseBeenGenerated(true);
                                    ifs.setElseCloseLineNumber(elseCloseLineNo);
                                    java.lang.String s = "";
                                    int x = BranchHelper.getReqdGoto(currentForIndex, info, ifs
                                            .getElseCloseLineNumber());
                                    StringBuffer sb = new StringBuffer("");
                                    if (x != -1)
                                        s = BranchHelper.getBranchType(currentForIndex, x, info,
                                                getContext().getBehaviourLoops(), sb);
                                    GlobalVariableStore.getBranchLabels().put(
                                            DecompilerHelper.newBranchLabel(ifs, s, sb
                                                    .toString()), new Integer(ifs
                                            .getElseCloseLineNumber()));
                                    behavior.appendToBuffer( "\n");
                                    String tempString = "else\n{\n";
                                    if(ifs.isElsebreakadded())
                                    	
                                    	ifs.setElsebreakinvalid(true);
                                    GlobalVariableStore.getElsestartadded()
                                            .add(new Integer(currentForIndex));
                                    behavior.appendToBuffer( Util
                                            .formatDecompiledStatement(tempString));
                                    GlobalVariableStore.setElsehasbegun(true);
                                }
                                // System.out.println("else has begun for else at
                                // "+currentForIndex);
                            } else // TODO : Recheck this else blk
                            {
                                if (elseCloseLineNo > ifs.getIfCloseLineNumber()) {
                                    // ifs.setHasMatchingElseBeenGenerated(true);
                                    ifs.setElseCloseLineNumber(elseCloseLineNo);
                                }
                            }
                        }
                    /////}
                    // ifs.setIfCloseLineNumber(-1);

                    ifs.setIfHasBeenClosed(true);
                }
            }
        }
        /*if (isWhileInScope) // TODO: Check if this can be removed
          {
              if (whileIndex == 1)
                  isWhileInScope = false;
              whileIndex--;
              // Changed by belurs ..This line was producing an extra bracket in
              // output
              // tempString="\n}\n";
              // behaviour.appendToBuffer( Util.formatDecompiledStatement(tempString); //
              // Definitly source of bug
          }*/


        GlobalVariableStore.setElseCloseLineNo(elseCloseLineNo);
        GlobalVariableStore.setIfInScope(isIfInScope);
    }

}
