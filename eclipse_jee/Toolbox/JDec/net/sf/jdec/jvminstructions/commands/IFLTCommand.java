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

public class IFLTCommand extends AbstractInstructionCommand {

	public IFLTCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 2;
	}

	public void execute() {
		int currentForIndex = getCurrentInstPosInCode();
		String tempString = "";
		String tempstr = "";
		int prevStart = getGenericFinder().getPrevStartOfInst(currentForIndex);
		OperandStack opStack = getStack();
		Behaviour behavior = getContext();
		Loop thisLoop ;
		boolean encounteredAndOrComp = GlobalVariableStore
				.isEncounteredAndOrComp();
		boolean isIfInScope = GlobalVariableStore.isIfInScope();
		Operand op = (Operand) opStack.pop();
		// op1 = (Operand)opStack.pop();
		int i = currentForIndex;
		int classIndex = getGenericFinder().getJumpAddress(i);

		i += 2;

		ArrayList list = getContext().getBehaviourLoops();

		IFBlock ifst = new IFBlock();
		ifst.setIfStart(currentForIndex);
		ifst.setHasIfBeenGenerated(true);
		// ifst.setIfCloseLineNumber(classIndex-3);
		boolean addBreak = LoopHelper.checkForParentLoopForIF(ifst);
		getContext().getMethodIfs().add(ifst);
		byte[] info = getCode();
		BranchHelper.addBranchLabel(classIndex, i, ifst, currentForIndex, info);
		boolean beyondLoop = LoopHelper.isBeyondLoop(ifst
				.getIfCloseLineNumber(), list, info);
        thisLoop = GlobalVariableStore.getThisLoop();
        boolean isEndOfLoop = LoopHelper.isIndexEndOfLoop(list, (ifst
				.getIfCloseLineNumber()));
		boolean correctIf = false;
		prevStart = getGenericFinder().getPrevStartOfInst(currentForIndex);

		if (info[currentForIndex - 1] != JvmOpCodes.DCMPG
				&& info[currentForIndex - 1] != JvmOpCodes.DCMPL
				&& info[currentForIndex - 1] != JvmOpCodes.FCMPG
				&& info[currentForIndex - 1] != JvmOpCodes.FCMPL
				&& info[currentForIndex - 1] != JvmOpCodes.LCMP) {

			tempstr = op.getOperandValue() + "<0";
		} else if (prevStart != currentForIndex - 1) {
			tempstr = op.getOperandValue() + "<0";
		} else {

			tempstr = op.getOperandValue();
		}

		boolean processIF = true;// checkForTernaryIf(ifst,info,tempstr);
		if (processIF) {
			if (ifst.getDonotclose() == false
					&& ifst.getIfCloseLineNumber() == -1) {
				int if_end = IFHelper.checkIFEndIfUnset(ifst, info,
						currentForIndex);
				ifst.setIfCloseLineNumber(if_end);
			}
			if (isEndOfLoop) {
				int loopstart = LoopHelper.getLoopStartForEnd(ifst
						.getIfCloseLineNumber(), list);
				if (currentForIndex > loopstart) {
					boolean ifinstcodepresent = IFHelper.getIfinst(loopstart,
							info, currentForIndex);
					if (ifinstcodepresent) {
						correctIf = false;
					} else
						correctIf = true;
				} else
					correctIf = false;
			}
			if ((ifst.getIfCloseLineNumber() > 0 && ifst.getIfCloseLineNumber() < info.length)
					&& info[ifst.getIfCloseLineNumber()] == JvmOpCodes.GOTO
					&& isEndOfLoop && correctIf) {
				int t = ifst.getIfCloseLineNumber();
				int gotoIndex = getGenericFinder().getJumpAddress(t);// ((info[t+1]
				// << 8)
				// |
				// info[t+2])
				// +
				// (ifst.getIfCloseLineNumber());
				if (gotoIndex < (t + 3))
					if (gotoIndex < classIndex) {
						boolean isInfiniteLoop = false;
						Iterator infLoop = getContext().getBehaviourLoops()
								.iterator();
						while (infLoop.hasNext()) {
							Loop iloop = (Loop) infLoop.next();
							if (iloop.getStartIndex() == gotoIndex
									&& iloop.isInfinite()) {
								isInfiniteLoop = true;
								/*
								 * ifLevel++; ifst = new IFBlock();
								 * ifst.setIfStart(currentForIndex);
								 * ifst.setHasIfBeenGenerated(true); //
								 * ifst.setIfCloseLineNumber(classIndex-3);
								 */
								ifst.setElseCloseLineNumber(gotoIndex);
								// ifHashTable.put(""+(ifLevel),ifst);
								isIfInScope = true;
								// addBranchLabel(classIndex,i,ifst,currentForIndex,info);
								boolean bb = LoopHelper.isBeyondLoop(
										getGenericFinder().getJumpAddress(
												currentForIndex), getContext()
												.getBehaviourLoops(), info);
                                thisLoop = GlobalVariableStore.getThisLoop();
                                boolean print = true;
								boolean addifbreak = false;
								java.lang.String tempString2 = "";
								if (bb && thisLoop != null
										&& thisLoop.isInfinite()
										&& !encounteredAndOrComp && addBreak) {
									Loop dowl = LoopHelper.isIfInADoWhile(
											currentForIndex, ifst, getContext()
													.getBehaviourLoops());
									if (dowl != null) {
										tempString = "";
									} else {
										if ((info[currentForIndex - 1] != JvmOpCodes.DCMPG)
												&& (info[currentForIndex - 1] != JvmOpCodes.DCMPL)
												&& info[currentForIndex - 1] != JvmOpCodes.FCMPG
												&& info[currentForIndex - 1] != JvmOpCodes.FCMPL
												&& info[currentForIndex - 1] != JvmOpCodes.LCMP)
											tempString = "\nif("
													+ op.getOperandValue()
													+ "<0)\n{\nbreak;\n}\n";
										else if (prevStart != currentForIndex - 1) {
											tempString = "\nif("
													+ op.getOperandValue()
													+ "<0)\n{\nbreak;\n}\n";
										} else
											tempString = "\nif("
													+ op.getOperandValue()
													+ ")\n{\nbreak;\n}\n";
										// codeStatements
										// +=Util.formatDecompiledStatement(tempString);
										addifbreak = true;
										tempString2 = tempString;
									}
									print = false;
								}
								tempstr = "";
								boolean bc = false;
								if (info[currentForIndex - 1] != JvmOpCodes.DCMPG
										&& info[currentForIndex - 1] != JvmOpCodes.DCMPL
										&& info[currentForIndex - 1] != JvmOpCodes.FCMPG
										&& info[currentForIndex - 1] != JvmOpCodes.FCMPL
										&& info[currentForIndex - 1] != JvmOpCodes.LCMP) {
									tempString = "\nif(" + op.getOperandValue()
											+ ">=0)\n{\n";
									tempstr = op.getOperandValue() + "<0";
								} else if (prevStart != currentForIndex - 1) {
									tempString = "\nif(" + op.getOperandValue()
											+ ">=0)\n{\n";
									tempstr = op.getOperandValue() + "<0";
								} else {
									tempString = "\nif(" + op.getOperandValue()
											+ ")\n{\n";
									tempstr = op.getOperandValue();
									bc = true;
								}
								java.lang.String alt;
								if (bc == false) {
									alt = op.getOperandValue() + ">=0";
								} else {
									alt = op.getOperandValue();
								}

								boolean last = IFHelper.lastIFinShortCutChain(
										info, ifst, currentForIndex);
								boolean c = IFHelper
										.addCodeStatementWRTShortcutOR(ifst,
												tempstr, print, "if", last, alt);
								if (c) {
									if (addifbreak) {
										behavior.appendToBuffer( Util
												.formatDecompiledStatement(tempString2));
									}
									behavior.appendToBuffer( Util
											.formatDecompiledStatement(tempString));
								} else {
									boolean firstIfForLoop = LoopHelper
											.isIfFirstIfInLoopCondition(info,
													currentForIndex);
									if (firstIfForLoop) {
										IFHelper
												.registerElseBreakForIfChain(currentForIndex);
									}
								}

								break;
							}
						}
						if (isInfiniteLoop) {
							GlobalVariableStore
									.setEncounteredAndOrComp(encounteredAndOrComp);
							GlobalVariableStore.setIfInScope(isIfInScope);
							GlobalVariableStore.setThisLoop(thisLoop);
							
							return;
						}
						tempstr = "";
						boolean bc = false;
						if (info[currentForIndex - 1] != JvmOpCodes.DCMPG
								&& info[currentForIndex - 1] != JvmOpCodes.DCMPL
								&& info[currentForIndex - 1] != JvmOpCodes.FCMPG
								&& info[currentForIndex - 1] != JvmOpCodes.FCMPL
								&& info[currentForIndex - 1] != JvmOpCodes.LCMP) {
							tempString = "\nwhile(" + op.getOperandValue()
									+ " >= 0)\n{\n";
							tempstr = op.getOperandValue() + " < 0";
						} else if (prevStart != currentForIndex - 1) {
							tempString = "\nwhile(" + op.getOperandValue()
									+ " >= 0)\n{\n";
							tempstr = op.getOperandValue() + " < 0";
						} else {
							tempString = "\nwhile(" + op.getOperandValue()
									+ ")\n{\n";
							tempstr = op.getOperandValue();
							bc = true;
						}
						java.lang.String alt;
						if (bc == false) {
							alt = op.getOperandValue() + ">=0";
						} else {
							alt = op.getOperandValue();
						}
						boolean last = IFHelper.lastIFinShortCutChain(info,
								ifst, currentForIndex);
						boolean c = IFHelper.addCodeStatementWRTShortcutOR(
								ifst, tempstr, true, "while", last, alt);
						if (c)
							behavior.appendToBuffer( Util
									.formatDecompiledStatement(tempString));

					} else {
						/*
						 * ifLevel++; ifst = new IFBlock();
						 * ifst.setIfStart(currentForIndex);
						 * ifst.setHasIfBeenGenerated(true); //
						 * ifst.setIfCloseLineNumber(classIndex-3);
						 */
						ifst.setElseCloseLineNumber(gotoIndex);
						// ifHashTable.put(""+(ifLevel),ifst);
						isIfInScope = true;
						// addBranchLabel(classIndex,i,ifst,currentForIndex,info);
						boolean bb = LoopHelper.isBeyondLoop(getGenericFinder()
								.getJumpAddress(currentForIndex), getContext()
								.getBehaviourLoops(), info);
                        thisLoop = GlobalVariableStore.getThisLoop();
                        boolean print = true;
						tempstr = "";
						boolean addifbreak = false;
						java.lang.String tempString2 = "";
						if (bb && thisLoop != null && thisLoop.isInfinite()
								&& !encounteredAndOrComp && addBreak) {
							Loop dowl = LoopHelper.isIfInADoWhile(
									currentForIndex, ifst, getContext()
											.getBehaviourLoops());
							if (dowl != null) {
								tempString = "";
							} else {
								if ((info[currentForIndex - 1] != JvmOpCodes.DCMPG)
										&& (info[currentForIndex - 1] != JvmOpCodes.DCMPL)
										&& info[currentForIndex - 1] != JvmOpCodes.FCMPG
										&& info[currentForIndex - 1] != JvmOpCodes.FCMPL
										&& info[currentForIndex - 1] != JvmOpCodes.LCMP) {
									tempString = "\nif(" + op.getOperandValue()
											+ "<0)\n{\nbreak;\n}\n";
								} else if (prevStart != currentForIndex - 1) {
									tempString = "\nif(" + op.getOperandValue()
											+ "<0)\n{\nbreak;\n}\n";
								} else
									tempString = "\nif(" + op.getOperandValue()
											+ ")\n{\nbreak;\n}\n";
								// codeStatements
								// +=Util.formatDecompiledStatement(tempString);
								tempString2 = tempString;
							}
							print = false;
							// ifst.setIfHasBeenClosed(true);
						}
						boolean bc = false;
						if (info[currentForIndex - 1] != JvmOpCodes.DCMPG
								&& info[currentForIndex - 1] != JvmOpCodes.DCMPL
								&& info[currentForIndex - 1] != JvmOpCodes.FCMPG
								&& info[currentForIndex - 1] != JvmOpCodes.FCMPL
								&& info[currentForIndex - 1] != JvmOpCodes.LCMP) {
							tempString = "\nif(" + op.getOperandValue()
									+ ">=0)\n{\n";
							tempstr = op.getOperandValue() + "<0";
						} else if (prevStart != currentForIndex - 1) {
							tempString = "\nif(" + op.getOperandValue()
									+ ">=0)\n{\n";
							tempstr = op.getOperandValue() + "<0";
						} else {
							tempString = "\nif(" + op.getOperandValue()
									+ ")\n{\n";
							tempstr = op.getOperandValue();
							bc = true;
						}
						java.lang.String alt;
						if (bc == false) {
							alt = op.getOperandValue() + ">=0";
						} else {
							alt = op.getOperandValue();
						}
						boolean last = IFHelper.lastIFinShortCutChain(info,
								ifst, currentForIndex);
						boolean c = IFHelper.addCodeStatementWRTShortcutOR(
								ifst, tempstr, print, "if", last, alt);
						if (c) {
							if (addifbreak) {
								behavior.appendToBuffer( Util
										.formatDecompiledStatement(tempString2));
							}
							behavior.appendToBuffer( Util
									.formatDecompiledStatement(tempString));
						} else {
							boolean firstIfForLoop = LoopHelper
									.isIfFirstIfInLoopCondition(info,
											currentForIndex);
							if (firstIfForLoop) {
								IFHelper
										.registerElseBreakForIfChain(currentForIndex);
							}
						}

					}

			} else {
				/*
				 * ifLevel++; ifst = new IFBlock();
				 * ifst.setHasIfBeenGenerated(true);
				 * ifHashTable.put(""+(ifLevel),ifst); isIfInScope=true;
				 * ifst.setIfStart(currentForIndex);
				 * addBranchLabel(classIndex,i,ifst,currentForIndex,info);
				 */
				isIfInScope = true;
				boolean bb = LoopHelper.isBeyondLoop(getGenericFinder()
						.getJumpAddress(currentForIndex), getContext()
						.getBehaviourLoops(), info);
                thisLoop = GlobalVariableStore.getThisLoop();
                boolean print = true;
				boolean addifbreak = false;
				java.lang.String tempString2 = "";
				if (bb && thisLoop != null && thisLoop.isInfinite()
						&& !encounteredAndOrComp && addBreak) {
					Loop dowl = LoopHelper.isIfInADoWhile(currentForIndex,
							ifst, getContext().getBehaviourLoops());
					if (dowl != null) {
						tempString = "";
					} else {
						addifbreak = true;
						if ((info[currentForIndex - 1] != JvmOpCodes.DCMPG)
								&& (info[currentForIndex - 1] != JvmOpCodes.DCMPL)
								&& info[currentForIndex - 1] != JvmOpCodes.FCMPG
								&& info[currentForIndex - 1] != JvmOpCodes.FCMPL
								&& info[currentForIndex - 1] != JvmOpCodes.LCMP)
							tempString = "\nif(" + op.getOperandValue()
									+ "<0)\n{\nbreak;\n}\n";
						else if (prevStart != currentForIndex - 1) {
							tempString = "\nif(" + op.getOperandValue()
									+ "<0)\n{\nbreak;\n}\n";
						} else
							tempString = "\nif(" + op.getOperandValue()
									+ ")\n{\nbreak;\n}\n";
						// codeStatements
						// +=Util.formatDecompiledStatement(tempString);
						tempString2 = tempString;
					}
					print = false;
					// ifst.setIfHasBeenClosed(true);
				}
				tempstr = "";
				boolean bc = false;
				if (info[currentForIndex - 1] != JvmOpCodes.DCMPG
						&& info[currentForIndex - 1] != JvmOpCodes.DCMPL
						&& info[currentForIndex - 1] != JvmOpCodes.FCMPG
						&& info[currentForIndex - 1] != JvmOpCodes.FCMPL
						&& info[currentForIndex - 1] != JvmOpCodes.LCMP) {
					tempString = "\nif(" + op.getOperandValue() + ">=0)\n{\n";
					tempstr = op.getOperandValue() + "<0";
				} else if (prevStart != currentForIndex - 1) {
					tempString = "\nif(" + op.getOperandValue() + ">=0)\n{\n";
					tempstr = op.getOperandValue() + "<0";
				}

				else {
					tempString = "\nif(" + op.getOperandValue() + ")\n{\n";
					tempstr = op.getOperandValue();
					bc = true;
				}
				java.lang.String alt;
				if (bc == false) {
					alt = op.getOperandValue() + ">=0";
				} else {
					alt = op.getOperandValue();
				}
				boolean last = IFHelper.lastIFinShortCutChain(info, ifst,
						currentForIndex);
				boolean c = IFHelper.addCodeStatementWRTShortcutOR(ifst,
						tempstr, print, "if", last, alt);
				if (c) {
					if (addifbreak) {
						behavior.appendToBuffer( Util
								.formatDecompiledStatement(tempString2));
					}
					behavior.appendToBuffer( Util
							.formatDecompiledStatement(tempString));
				} else {
					boolean firstIfForLoop = LoopHelper
							.isIfFirstIfInLoopCondition(info, currentForIndex);
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
