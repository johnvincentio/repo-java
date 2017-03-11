package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.blockhelpers.BranchHelper;
import net.sf.jdec.blockhelpers.IFHelper;
import net.sf.jdec.blockhelpers.LoopHelper;
import net.sf.jdec.blocks.IFBlock;
import net.sf.jdec.blocks.Loop;
import net.sf.jdec.core.GlobalVariableStore;
import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Util;

import java.util.ArrayList;
import java.util.Iterator;

public class IFACMPEQCommand extends AbstractInstructionCommand {

	public IFACMPEQCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 2;
	}

	public void execute() {
		int currentForIndex = getCurrentInstPosInCode();
		OperandStack opStack = getStack();
		int i = currentForIndex;
		Operand op = (Operand) opStack.pop();
		Operand op1 = (Operand) opStack.pop();
		/*
		 * classIndex = ((info[++i] << 8) | info[++i]); classIndex += i - 2;
		 */
		Loop thisLoop;
		boolean encounteredAndOrComp=GlobalVariableStore.isEncounteredAndOrComp();
		boolean isIfInScope = GlobalVariableStore.isIfInScope();
		int classIndex = getGenericFinder().getJumpAddress(i);
		i++;
		i++;
		String tempString="";
		IFBlock ifst = new IFBlock();
		ifst.setIfStart(currentForIndex);
		ifst.setHasIfBeenGenerated(true);
		getContext().getMethodIfs().add(ifst);
		boolean addBreak = LoopHelper.checkForParentLoopForIF(ifst);
		byte[] info = getCode();
		BranchHelper.addBranchLabel(classIndex, i, ifst, currentForIndex, info);
		ArrayList list = getContext().getBehaviourLoops();
		boolean isEndOfLoop = LoopHelper.isIndexEndOfLoop(list, ifst
				.getIfCloseLineNumber());
		int loopstart = -1;
		boolean correctIf = false;
		Behaviour behaviour = getContext();
		
		boolean beyondLoop = LoopHelper.isBeyondLoop(ifst.getIfCloseLineNumber(), list,
				info);
        thisLoop=GlobalVariableStore.getThisLoop();
		boolean processIF = true;//checkForTernaryIf(ifst, info, op1.getOperandValue()	+ "!=" + op.getOperandValue());
		if (processIF) {

			if (ifst.getDonotclose() == false
					&& ifst.getIfCloseLineNumber() == -1) {
				int end = IFHelper.checkIFEndIfUnset(ifst, info, currentForIndex);
				ifst.setIfCloseLineNumber(end);
			}

			if (isEndOfLoop) {
				loopstart = LoopHelper.getLoopStartForEnd(ifst.getIfCloseLineNumber(),
						list);
				if (currentForIndex > loopstart) {
					boolean ifinstcodepresent = IFHelper.getIfinst(loopstart, info,
							currentForIndex);
					if (ifinstcodepresent) {
						correctIf = false;
					} else
						correctIf = true;
				}
			}
			// StringBuffer
			if ((ifst.getIfCloseLineNumber() > 0 && ifst.getIfCloseLineNumber() < info.length)
					&& info[ifst.getIfCloseLineNumber()] == JvmOpCodes.GOTO
					&& isEndOfLoop && correctIf) {
				int t = ifst.getIfCloseLineNumber();
				int gotoIndex = getGenericFinder().getJumpAddress(t);// (info[t+1] << 8) |
														// info[t+2]) +
														// (ifst.getIfCloseLineNumber());
				if (gotoIndex < (t + 3)) {
					boolean isInfiniteLoop = false;
					Iterator infLoop = getContext().getBehaviourLoops().iterator();
					while (infLoop.hasNext()) {
						Loop iloop = (Loop) infLoop.next();
						if (iloop.getStartIndex() == gotoIndex
								&& iloop.isInfinite()) {
							isInfiniteLoop = true;
							/*
							 * ifLevel++; ifst = new IFBlock();
							 * ifst.setIfStart(currentForIndex);
							 * ifst.setHasIfBeenGenerated(true);
							 */
							// ifst.setIfCloseLineNumber(classIndex-3);
							ifst.setElseCloseLineNumber(gotoIndex);
							/*
							 * ifHashTable.put(""+(ifLevel),ifst);
							 * addBranchLabel(classIndex,i,ifst,currentForIndex,info);
							 */
							boolean bb = LoopHelper.isBeyondLoop(getGenericFinder().getJumpAddress(
									currentForIndex), getContext()
									.getBehaviourLoops(), info);
                            thisLoop=GlobalVariableStore.getThisLoop();
                            boolean print = true;
							boolean addifbreak = false;
							if (bb && thisLoop != null && thisLoop.isInfinite()
									&& !encounteredAndOrComp && addBreak) {
								Loop dowl = LoopHelper.isIfInADoWhile(currentForIndex,
										ifst, getContext().getBehaviourLoops());
								if (dowl != null) {
									tempString = "";
								} else {
									tempString = "\nif("
											+ op1.getOperandValue() + "=="
											+ op.getOperandValue()
											+ ")\n{\nbreak;\n}\n";
									// codeStatements
									// +=Util.formatDecompiledStatement(tempString);
									addifbreak = true;
								}
								// ifst.setIfHasBeenClosed(true);
								print = false;
							}
							isIfInScope = true;
							boolean c;
							boolean last = IFHelper.lastIFinShortCutChain(info, ifst,
									currentForIndex);
							c = IFHelper.addCodeStatementWRTShortcutOR(ifst, op1
									.getOperandValue()
									+ " == " + op.getOperandValue(), print,
									"if", last, op1.getOperandValue() + " != "
											+ op.getOperandValue());
							if (c) {
								if (addifbreak) {
									behaviour.appendToBuffer( Util
											.formatDecompiledStatement(tempString));
								}

								tempString = "\nif(!" + op1.getOperandValue()
										+ "==" + op.getOperandValue()
										+ "))\n{\n";
								behaviour.appendToBuffer( Util
										.formatDecompiledStatement(tempString));
							} else {
								boolean firstIfForLoop = LoopHelper.isIfFirstIfInLoopCondition(
										info, currentForIndex);
								if (firstIfForLoop) {
									IFHelper.registerElseBreakForIfChain(currentForIndex);
								}
							}
							break;
						}
					}
					if (isInfiniteLoop) {
						GlobalVariableStore.setEncounteredAndOrComp(encounteredAndOrComp);
						GlobalVariableStore.setIfInScope(isIfInScope);
						GlobalVariableStore.setThisLoop(thisLoop);
						
						return;
					}
					boolean last = IFHelper.lastIFinShortCutChain(info, ifst,currentForIndex);
					boolean c = IFHelper.addCodeStatementWRTShortcutOR(ifst, op1
							.getOperandValue()
							+ " == " + op.getOperandValue(), true, "while",
							last, op1.getOperandValue() + " != "
									+ op.getOperandValue());
					if (c) {
						tempString = "\nwhile(!" + op1.getOperandValue() + "=="
								+ op.getOperandValue() + "))\n{\n";
						behaviour.appendToBuffer( Util
								.formatDecompiledStatement(tempString));
					}
					/*isWhileInScope = true;
					whileIndex++;*/

				}
				// int x = 0;
				else {

					/*
					 * ifLevel++; ifst = new IFBlock();
					 * ifst.setIfStart(currentForIndex);
					 * ifst.setHasIfBeenGenerated(true);
					 * //ifst.setIfCloseLineNumber(classIndex-3);
					 */

					ifst.setElseCloseLineNumber(gotoIndex);
					/*
					 * ifHashTable.put(""+(ifLevel),ifst); isIfInScope = true;
					 */

					// addBranchLabel(classIndex,i,ifst,currentForIndex,info);
					isIfInScope = true;
					boolean bb = LoopHelper.isBeyondLoop(getGenericFinder().getJumpAddress(currentForIndex), getContext().getBehaviourLoops(),
							info);
                    thisLoop=GlobalVariableStore.getThisLoop();
                    boolean print = true;
					boolean addifbreak = false;
					if (bb && thisLoop != null && thisLoop.isInfinite()
							&& !encounteredAndOrComp && addBreak) {
						Loop dowl = LoopHelper.isIfInADoWhile(currentForIndex, ifst,
								getContext().getBehaviourLoops());
						if (dowl != null) {
							tempString = "";
						} else {
							tempString = "\nif(" + op1.getOperandValue() + "=="
									+ op.getOperandValue()
									+ ")\n{\nbreak;\n}\n";
							// codeStatements
							// +=Util.formatDecompiledStatement(tempString);
							addifbreak = true;
						}
						print = false;

					}
					boolean last = IFHelper.lastIFinShortCutChain(info, ifst,
							currentForIndex);
					boolean c = IFHelper.addCodeStatementWRTShortcutOR(ifst, op1
							.getOperandValue()
							+ " == " + op.getOperandValue(), print, "if", last,
							op1.getOperandValue() + " != "
									+ op.getOperandValue());
					if (c) {
						if (addifbreak) {
							behaviour.appendToBuffer( Util
									.formatDecompiledStatement(tempString));
						}

						tempString = "\nif(!" + op1.getOperandValue() + "=="
								+ op.getOperandValue() + "))\n{\n";
						behaviour.appendToBuffer( Util
								.formatDecompiledStatement(tempString));
					} else {
						boolean firstIfForLoop = LoopHelper.isIfFirstIfInLoopCondition(
								info, currentForIndex);
						if (firstIfForLoop) {
							IFHelper.registerElseBreakForIfChain(currentForIndex);
						}
					}

				}
			} else {
				/*
				 * ifLevel++; ifst = new IFBlock();
				 * ifst.setIfStart(currentForIndex);
				 * ifst.setHasIfBeenGenerated(true); isIfInScope = true;
				 * ifHashTable.put(""+(ifLevel),ifst);
				 * 
				 * addBranchLabel(classIndex,i,ifst,currentForIndex,info);
				 */
				isIfInScope = true;
				boolean bb = LoopHelper.isBeyondLoop(
						getGenericFinder().getJumpAddress(currentForIndex), getContext()
								.getBehaviourLoops(), info);
				boolean print = true;
                thisLoop=GlobalVariableStore.getThisLoop();
                boolean addifbreak = false;
				if (bb && thisLoop != null && thisLoop.isInfinite()
						&& !encounteredAndOrComp && addBreak) {
					Loop dowl = LoopHelper.isIfInADoWhile(currentForIndex, ifst, getContext()
							.getBehaviourLoops());
					if (dowl != null) {
						tempString = "";
					} else {
						tempString = "\nif(" + op1.getOperandValue() + "=="
								+ op.getOperandValue() + ")\n{\nbreak;\n}\n";
						// odeStatements
						// +=Util.formatDecompiledStatement(tempString);
						addifbreak = true;
					}
					print = false;
					// ifst.setIfHasBeenClosed(true);

				}
				boolean last = IFHelper.lastIFinShortCutChain(info, ifst,
						currentForIndex);
				boolean c = IFHelper.addCodeStatementWRTShortcutOR(ifst, op1
						.getOperandValue()
						+ " == " + op.getOperandValue(), print, "if", last, op1
						.getOperandValue()
						+ " != " + op.getOperandValue());
				if (c) {
					if (addifbreak) {
						behaviour.appendToBuffer( Util
								.formatDecompiledStatement(tempString));
					}
					if(op1.getOperandValue().equals("null")){
						tempString = "\nif(" + op1.getOperandValue() + " !="
						+ op.getOperandValue() + "))\n{\n";
					}
					else
					tempString = "\nif(!(" + op1.getOperandValue() + "=="
							+ op.getOperandValue() + "))\n{\n";
					behaviour.appendToBuffer( Util
							.formatDecompiledStatement(tempString));
				} else {
					boolean firstIfForLoop = LoopHelper.isIfFirstIfInLoopCondition(info,
							currentForIndex);
					if (firstIfForLoop) {
						IFHelper.registerElseBreakForIfChain(currentForIndex);
					}
				}
			}
		}
		
		GlobalVariableStore.setEncounteredAndOrComp(encounteredAndOrComp);
		GlobalVariableStore.setIfInScope(isIfInScope);
		GlobalVariableStore.setThisLoop(thisLoop);
	
	}

}
