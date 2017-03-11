package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.blockhelpers.BranchHelper;
import net.sf.jdec.blockhelpers.IFHelper;
import net.sf.jdec.blockhelpers.LoopHelper;
import net.sf.jdec.blocks.IFBlock;
import net.sf.jdec.blocks.Loop;
import net.sf.jdec.core.*;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Util;

import java.util.ArrayList;
import java.util.Iterator;

public class IFEQCommand extends AbstractInstructionCommand {

	public IFEQCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 2;
	}

	public void execute() {
		handleIFEQ(getCode());
	}

	private void handleIFEQ(byte[] info) {
		int currentForIndex = getCurrentInstPosInCode();
		String tempString = "";
		Behaviour behavior = getContext();
		
		Loop thisLoop ;
		boolean encounteredAndOrComp = GlobalVariableStore
				.isEncounteredAndOrComp();
		boolean isIfInScope = GlobalVariableStore.isIfInScope();
		java.lang.String previnstret = DecompilerHelper
				.getReturnTypeIfPreviousInvoke(currentForIndex, info);
		int i = currentForIndex;
		OperandStack opStack = getStack();
		Operand op = (Operand) opStack.pop();
		int classIndex = getGenericFinder().getJumpAddress(i);
		i += 2;
		ArrayList list = getContext().getBehaviourLoops();

		IFBlock ifst = new IFBlock();
		ifst.setIfStart(currentForIndex);
		ifst.setHasIfBeenGenerated(true);
		// ifst.setIfCloseLineNumber(classIndex-3);
		boolean addBreak = LoopHelper.checkForParentLoopForIF(ifst);
		getContext().getMethodIfs().add(ifst);

		int prevStart = getGenericFinder().getPrevStartOfInst(currentForIndex);
		BranchHelper.addBranchLabel(classIndex, i, ifst, currentForIndex, info);
		boolean beyondLoop = LoopHelper.isBeyondLoop(ifst
				.getIfCloseLineNumber(), list, info);
		boolean isEndOfLoop = LoopHelper.isIndexEndOfLoop(list, ifst
				.getIfCloseLineNumber());
        thisLoop=GlobalVariableStore.getThisLoop();
        boolean correctIf = false;
		java.lang.String tempstr = "";
		prevStart = getGenericFinder().getPrevStartOfInst(currentForIndex);
		java.lang.String t3 = op.getClassType();
		java.lang.String t1 = op.getLocalVarType();
		if ((t3 != null && t3.trim().equals("boolean"))
				|| (t1 != null && t1.trim().equals("boolean"))) {
			if (previnstret != null && previnstret.trim().equals("1")) {
				previnstret = "true";
			}
			if (previnstret != null && previnstret.trim().equals("0")) {
				previnstret = "false";
			}
		}
		if (getGenericFinder().isThisInstrStart(currentForIndex - 1)
				&& info[currentForIndex - 1] != JvmOpCodes.DCMPG
				&& info[currentForIndex - 1] != JvmOpCodes.DCMPL
				&& info[currentForIndex - 1] != JvmOpCodes.FCMPG
				&& info[currentForIndex - 1] != JvmOpCodes.FCMPL
				&& info[currentForIndex - 1] != JvmOpCodes.LCMP) {

			tempstr = op.getOperandValue() + "!=" + previnstret;
		} else if (getGenericFinder().isThisInstrStart(

		currentForIndex - 3)
				&& info[currentForIndex - 3] != JvmOpCodes.INSTANCEOF) {

			tempstr = op.getOperandValue() + "!=" + previnstret;
		} else if (prevStart != currentForIndex - 1
				&& prevStart != currentForIndex - 3) {
			tempstr = op.getOperandValue() + "!=" + previnstret;
		} else {

			tempstr = op.getOperandValue();
		}
		boolean processIF = true;// checkForTernaryIf(ifst, info, tempstr);
		if (processIF) {

			if (ifst.getDonotclose() == false
					&& ifst.getIfCloseLineNumber() == -1) {
				int end = IFHelper.checkIFEndIfUnset(ifst, info,
						currentForIndex);
				ifst.setIfCloseLineNumber(end);
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
				}
			}

			if ((ifst.getIfCloseLineNumber() >= 0 && ifst
					.getIfCloseLineNumber() < info.length)
					&& info[ifst.getIfCloseLineNumber()] == JvmOpCodes.GOTO
					&& isEndOfLoop && correctIf) {
				int t = ifst.getIfCloseLineNumber();
				int gotoIndex = getGenericFinder().getJumpAddress(t);// ((info[t+1]
																		// << 8)
																		// |
				// info[t+2]) +
				// (ifst.getIfCloseLineNumber());
				if (gotoIndex < (t + 3)) {
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
							 * ifst.setHasIfBeenGenerated(true);
							 * //ifst.setIfCloseLineNumber(classIndex-3);
							 */
							ifst.setElseCloseLineNumber(gotoIndex);
							// ifHashTable.put(""+(ifLevel),ifst);

							isIfInScope = true;
							// addBranchLabel(classIndex,i,ifst,currentForIndex,info);
							boolean bb = LoopHelper.isBeyondLoop(
									getGenericFinder().getJumpAddress(
											currentForIndex), getContext()
											.getBehaviourLoops(), info);
							boolean print = true;
                            thisLoop=GlobalVariableStore.getThisLoop();
                            boolean addifbreak = false;
							java.lang.String tempString2 = "";
							if (bb && thisLoop != null && thisLoop.isInfinite()
									&& !encounteredAndOrComp && addBreak) {
								// info[currentForIndex-3]!=JvmOpCodes.INSTANCEOF
								// &&
								Loop dowl = LoopHelper.isIfInADoWhile(
										currentForIndex, ifst, getContext()
												.getBehaviourLoops());
								if (dowl != null) {
									tempString = "";
								} else {
									if (getGenericFinder().isThisInstrStart(

									currentForIndex - 1)
											&& (info[currentForIndex - 1] != JvmOpCodes.DCMPG)
											&& (info[currentForIndex - 1] != JvmOpCodes.DCMPL)
											&& info[currentForIndex - 1] != JvmOpCodes.FCMPG
											&& info[currentForIndex - 1] != JvmOpCodes.FCMPL
											&& info[currentForIndex - 1] != JvmOpCodes.LCMP)
										tempString = "\nif("
												+ op.getOperandValue() + "=="
												+ previnstret
												+ ")\n{\nbreak;\n}\n";
									else if (getGenericFinder()
											.isThisInstrStart(

											currentForIndex - 3)
											&& info[currentForIndex - 3] != JvmOpCodes.INSTANCEOF
											&& getGenericFinder()
													.isPreviousInst(
															currentForIndex,
															currentForIndex - 3)) {
										tempString = "\nif("
												+ op.getOperandValue() + "=="
												+ previnstret
												+ ")\n{\nbreak;\n}\n";
									} else if (prevStart != currentForIndex - 1
											&& prevStart != currentForIndex - 3) {
										tempString = "\nif("
												+ op.getOperandValue() + "=="
												+ previnstret
												+ ")\n{\nbreak;\n}\n";
									} else if (prevStart != currentForIndex - 1
											&& prevStart != currentForIndex - 3) {
										tempString = "\nif("
												+ op.getOperandValue() + "=="
												+ previnstret
												+ ")\n{\nbreak;\n}\n";
									} else
										tempString = "\nif("
												+ op.getOperandValue()
												+ ")\n{\nbreak;\n}\n";
									// codeStatements
									// +=Util.formatDecompiledStatement(tempString);
									tempString2 = tempString;
									addifbreak = true;
								}
								print = false;
							}
							tempstr = "";
							boolean last = IFHelper.lastIFinShortCutChain(info,
									ifst, currentForIndex);
							boolean bc = false; // boolean comparision
							if (getGenericFinder().isThisInstrStart(

							currentForIndex - 1)
									&& info[currentForIndex - 1] != JvmOpCodes.DCMPG
									&& info[currentForIndex - 1] != JvmOpCodes.DCMPL
									&& info[currentForIndex - 1] != JvmOpCodes.FCMPG
									&& info[currentForIndex - 1] != JvmOpCodes.FCMPL
									&& info[currentForIndex - 1] != JvmOpCodes.LCMP) {
								tempString = "\nif(" + op.getOperandValue()
										+ "!=" + previnstret + ")\n{\n";
								tempstr = op.getOperandValue() + "=="
										+ previnstret;
							} else if (getGenericFinder().isThisInstrStart(

							currentForIndex - 3)
									&& info[currentForIndex - 3] != JvmOpCodes.INSTANCEOF
									&& getGenericFinder().isPreviousInst(
											currentForIndex,
											currentForIndex - 3)) {
								tempString = "\nif(" + op.getOperandValue()
										+ "!=" + previnstret + ")\n{\n";
								tempstr = op.getOperandValue() + "=="
										+ previnstret;
							} else if (prevStart != currentForIndex - 1
									&& prevStart != currentForIndex - 3) {
								tempString = "\nif(" + op.getOperandValue()
										+ "!=" + previnstret + ")\n{\n";
								tempstr = op.getOperandValue() + "=="
										+ previnstret;
							} else {
								tempString = "\nif(" + op.getOperandValue()
										+ ")\n{\n";
								tempstr = op.getOperandValue();
								bc = true;
							}
							java.lang.String alt;
							if (bc == false)
								alt = op.getOperandValue() + "!=" + previnstret;
							else
								alt = op.getOperandValue();
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

							break;
						}
					}
					if (isInfiniteLoop) {
						GlobalVariableStore.setEncounteredAndOrComp(encounteredAndOrComp);
						GlobalVariableStore.setIfInScope(isIfInScope);
						GlobalVariableStore.setThisLoop(thisLoop);
						
						return;
					}
					tempstr = "";
					boolean bc = false;
					if (getGenericFinder().isThisInstrStart(

					currentForIndex - 1)
							&& info[currentForIndex - 1] != JvmOpCodes.DCMPG
							&& info[currentForIndex - 1] != JvmOpCodes.DCMPL
							&& info[currentForIndex - 1] != JvmOpCodes.FCMPG
							&& info[currentForIndex - 1] != JvmOpCodes.FCMPL
							&& info[currentForIndex - 1] != JvmOpCodes.LCMP) {
						tempString = "\nwhile(" + op.getOperandValue() + "!="
								+ previnstret + ")\n{\n";
						tempstr = op.getOperandValue() + "==" + previnstret;
					} else if (getGenericFinder().isThisInstrStart(

					currentForIndex - 3)
							&& info[currentForIndex - 3] != JvmOpCodes.INSTANCEOF
							&& getGenericFinder().isPreviousInst(
									currentForIndex, currentForIndex - 3)) {
						tempString = "\nwhile(" + op.getOperandValue() + "!="
								+ previnstret + ")\n{\n";
						tempstr = op.getOperandValue() + "==" + previnstret;
					} else if (prevStart != currentForIndex - 1
							&& prevStart != currentForIndex - 3) {
						tempString = "\nwhile(" + op.getOperandValue() + "!="
								+ previnstret + ")\n{\n";
						tempstr = op.getOperandValue() + "==" + previnstret;
					} else {
						tempString = "\nwhile(" + op.getOperandValue()
								+ ")\n{\n";
						tempstr = op.getOperandValue();
						bc = true;
					}
					boolean last = IFHelper.lastIFinShortCutChain(info, ifst,
							currentForIndex);
					java.lang.String alt;
					if (bc == false)
						alt = op.getOperandValue() + "!=" + previnstret;
					else
						alt = op.getOperandValue();
					boolean c = IFHelper.addCodeStatementWRTShortcutOR(ifst,
							tempstr, true, "while", last, alt);
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
					boolean print = true;
                    thisLoop=GlobalVariableStore.getThisLoop();
                    boolean addifbreak = false;
					java.lang.String tempString2 = "";
					// prevStart=getPrevStartOfInst(currentForIndex,info);
					if (bb && thisLoop != null && thisLoop.isInfinite()
							&& !encounteredAndOrComp && addBreak) {
						Loop dowl = LoopHelper.isIfInADoWhile(currentForIndex,
								ifst, getContext().getBehaviourLoops());
						if (dowl != null) {
							tempString = "";
						} else {
							if (getGenericFinder().isThisInstrStart(

							currentForIndex - 1)
									&& (info[currentForIndex - 1] != JvmOpCodes.DCMPG)
									&& (info[currentForIndex - 1] != JvmOpCodes.DCMPL)
									&& info[currentForIndex - 1] != JvmOpCodes.FCMPG
									&& info[currentForIndex - 1] != JvmOpCodes.FCMPL
									&& info[currentForIndex - 1] != JvmOpCodes.LCMP)
								tempString = "\nif(" + op.getOperandValue()
										+ "==" + previnstret
										+ ")\n{\nbreak;\n}\n";
							else if (getGenericFinder().isThisInstrStart(

							currentForIndex - 3)
									&& info[currentForIndex - 3] != JvmOpCodes.INSTANCEOF
									&& getGenericFinder().isPreviousInst(
											currentForIndex,
											currentForIndex - 3)) {
								tempString = "\nif(" + op.getOperandValue()
										+ "==" + previnstret
										+ ")\n{\nbreak;\n}\n";
							} else if (prevStart != currentForIndex - 1
									&& prevStart != currentForIndex - 3) {
								tempString = "\nif(" + op.getOperandValue()
										+ "==" + previnstret
										+ ")\n{\nbreak;\n}\n";
							} else
								tempString = "\nif(" + op.getOperandValue()
										+ ")\n{\nbreak;\n}\n";
							// codeStatements
							// +=Util.formatDecompiledStatement(tempString);
							tempString2 = tempString;
							addifbreak = true;
						}
						print = false;
					}
					tempstr = "";
					boolean bc = false;
					if (getGenericFinder().isThisInstrStart(

					currentForIndex - 1)
							&& info[currentForIndex - 1] != JvmOpCodes.DCMPG
							&& info[currentForIndex - 1] != JvmOpCodes.DCMPL
							&& info[currentForIndex - 1] != JvmOpCodes.FCMPG
							&& info[currentForIndex - 1] != JvmOpCodes.FCMPL
							&& info[currentForIndex - 1] != JvmOpCodes.LCMP) {
						tempString = "\nif(" + op.getOperandValue() + "!="
								+ previnstret + ")\n{\n";
						tempstr = op.getOperandValue() + "==" + previnstret;
					} else if (getGenericFinder().isThisInstrStart(

					currentForIndex - 3)
							&& info[currentForIndex - 3] != JvmOpCodes.INSTANCEOF
							&& getGenericFinder().isPreviousInst(
									currentForIndex, currentForIndex - 3)) {
						tempString = "\nif(" + op.getOperandValue() + "!="
								+ previnstret + ")\n{\n";
						tempstr = op.getOperandValue() + "==" + previnstret;
					} else if (prevStart != currentForIndex - 1
							&& prevStart != currentForIndex - 3) {
						tempString = "\nif(" + op.getOperandValue() + "!="
								+ previnstret + ")\n{\n";
						tempstr = op.getOperandValue() + "==" + previnstret;
					} else {
						tempString = "\nif(" + op.getOperandValue() + ")\n{\n";
						tempstr = op.getOperandValue();
						bc = true;
					}
					boolean last = IFHelper.lastIFinShortCutChain(info, ifst,
							currentForIndex);
					java.lang.String alt;
					if (bc == false)
						alt = op.getOperandValue() + "!=" + previnstret;
					else
						alt = op.getOperandValue();
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
				 * ifst.setIfStart(currentForIndex);
				 * ifst.setHasIfBeenGenerated(true);
				 * ifHashTable.put(""+(ifLevel),ifst);
				 */
				isIfInScope = true;
				// addBranchLabel(classIndex,i,ifst,currentForIndex,info);
				boolean bb = LoopHelper.isBeyondLoop(getGenericFinder()
						.getJumpAddress(currentForIndex), getContext()
						.getBehaviourLoops(), info);
				boolean print = true;
                thisLoop=GlobalVariableStore.getThisLoop();
                tempString = "";
				java.lang.String tempString2 = "";
				boolean addifbreak = false;
				// prevStart=getPrevStartOfInst(currentForIndex,info);
				if (bb && thisLoop != null && thisLoop.isInfinite()
						&& !encounteredAndOrComp && addBreak) {
					Loop dowl = LoopHelper.isIfInADoWhile(currentForIndex,
							ifst, getContext().getBehaviourLoops());
					if (dowl != null) {
						tempString = "";
					} else {
						if (getGenericFinder().isThisInstrStart(

						currentForIndex - 1)
								&& (info[currentForIndex - 1] != JvmOpCodes.DCMPG)
								&& (info[currentForIndex - 1] != JvmOpCodes.DCMPL)
								&& info[currentForIndex - 1] != JvmOpCodes.FCMPG
								&& info[currentForIndex - 1] != JvmOpCodes.FCMPL
								&& info[currentForIndex - 1] != JvmOpCodes.LCMP)
							tempString = "\nif(" + op.getOperandValue() + "=="
									+ previnstret + ")\n{\nbreak;\n}\n";
						else if (getGenericFinder().isThisInstrStart(

						currentForIndex - 3)
								&& info[currentForIndex - 3] != JvmOpCodes.INSTANCEOF
								&& getGenericFinder().isPreviousInst(
										currentForIndex, currentForIndex - 3)) {
							tempString = "\nif(" + op.getOperandValue() + "=="
									+ previnstret + ")\n{\nbreak;\n}\n";
						} else if (prevStart != currentForIndex - 1
								&& prevStart != currentForIndex - 3) {
							tempString = "\nif(" + op.getOperandValue() + "=="
									+ previnstret + ")\n{\nbreak;\n}\n";
						} else
							tempString = "\nif(" + op.getOperandValue()
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
				if (getGenericFinder().isThisInstrStart(currentForIndex - 1)
						&& info[currentForIndex - 1] != JvmOpCodes.DCMPG
						&& info[currentForIndex - 1] != JvmOpCodes.DCMPL
						&& info[currentForIndex - 1] != JvmOpCodes.FCMPG
						&& info[currentForIndex - 1] != JvmOpCodes.FCMPL
						&& info[currentForIndex - 1] != JvmOpCodes.LCMP) {
					tempString = "\nif(" + op.getOperandValue() + "!="
							+ previnstret + ")\n{\n";
					tempstr = op.getOperandValue() + "==" + previnstret;
				} else if (getGenericFinder().isThisInstrStart(

				currentForIndex - 3)
						&& info[currentForIndex - 3] != JvmOpCodes.INSTANCEOF
						&& getGenericFinder().isPreviousInst(currentForIndex,
								currentForIndex - 3)) {
					tempString = "\nif(" + op.getOperandValue() + "!="
							+ previnstret + ")\n{\n";
					tempstr = op.getOperandValue() + "==" + previnstret;
				} else if (prevStart != currentForIndex - 1
						&& prevStart != currentForIndex - 3) {
					tempString = "\nif(" + op.getOperandValue() + "!="
							+ previnstret + ")\n{\n";
					tempstr = op.getOperandValue() + "==" + previnstret;
				} else {
					tempString = "\nif(" + op.getOperandValue() + ")\n{\n";
					tempstr = op.getOperandValue();
					bc = true;
				}
				boolean last = IFHelper.lastIFinShortCutChain(info, ifst,
						currentForIndex);
				java.lang.String alt;
				if (bc == false)
					alt = op.getOperandValue() + "!=" + previnstret;
				else
					alt = op.getOperandValue();
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
