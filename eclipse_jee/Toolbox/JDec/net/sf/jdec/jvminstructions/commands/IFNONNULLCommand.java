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

public class IFNONNULLCommand extends AbstractInstructionCommand {

	public IFNONNULLCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 2;
	}

	public void execute() {
		int currentForIndex = getCurrentInstPosInCode();
		String tempString = "";
		String tempstr = "";
		byte[] info = getCode();
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
		
		int classIndex = getGenericFinder().getJumpAddress( i);
		i += 2;

		
		ArrayList list = getContext().getBehaviourLoops();
		
		IFBlock ifst = new IFBlock();
		ifst.setIfStart(currentForIndex);
		ifst.setHasIfBeenGenerated(true);
		getContext().getMethodIfs().add(ifst);
		boolean addBreak = LoopHelper.checkForParentLoopForIF(ifst);
		BranchHelper.addBranchLabel(classIndex, i, ifst, currentForIndex, info);
		boolean isEndOfLoop = LoopHelper.isIndexEndOfLoop(list, ifst.getIfCloseLineNumber());
		boolean beyondLoop = LoopHelper.isBeyondLoop(ifst.getIfCloseLineNumber(), list, info);
		boolean correctIf = false;
        thisLoop = GlobalVariableStore.getThisLoop();
        boolean process = IFHelper.isIfForLoadClass(ifst, info);
		if (process) {
			process = true;//IFHelper.checkForTernaryIf(ifst, info, op.getOperandValue()					+ " == null");
		}
		if (ifst.getDonotclose() == false && ifst.getIfCloseLineNumber() == -1) {
			int if_end = IFHelper.checkIFEndIfUnset(ifst, info, currentForIndex);
			ifst.setIfCloseLineNumber(if_end);
		}
		if (isEndOfLoop) {
			int loopstart = LoopHelper.getLoopStartForEnd(ifst.getIfCloseLineNumber(), list);
			if (currentForIndex > loopstart) {
				boolean ifinstcodepresent = IFHelper.getIfinst(loopstart, info,
						currentForIndex);
				if (ifinstcodepresent) {
					correctIf = false;
				} else
					correctIf = true;
			}
		}
		if (process) {
			if ((ifst.getIfCloseLineNumber() > 0 && ifst.getIfCloseLineNumber() < info.length)
					&& info[ifst.getIfCloseLineNumber()] == JvmOpCodes.GOTO
					&& isEndOfLoop && correctIf) {
				int t = ifst.getIfCloseLineNumber();
				int gotoIndex = getGenericFinder().getJumpAddress( t);// ((info[t+1] << 8) |
														// info[t+2]) +
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

								ifst.setElseCloseLineNumber(gotoIndex);

								isIfInScope = true;
								boolean print = true;
								boolean addifbreak = false;
								boolean bb = LoopHelper.isBeyondLoop(getGenericFinder().getJumpAddress(
										currentForIndex), getContext()
										.getBehaviourLoops(), info);
                                thisLoop = GlobalVariableStore.getThisLoop();
                                if (bb && thisLoop != null
										&& thisLoop.isInfinite()
										&& !encounteredAndOrComp && addBreak) {
									Loop dowl = LoopHelper.isIfInADoWhile(currentForIndex,
											ifst, getContext().getBehaviourLoops());
									if (dowl != null) {
										tempString = "";
									} else {
										tempString = "\nif("
												+ op.getOperandValue()
												+ "!= null)\n{\nbreak;\n}\n";
										// ifst.setIfHasBeenClosed(true);
										// codeStatements
										// +=Util.formatDecompiledStatement(tempString);
										addifbreak = true;
									}
									print = false;
								}
								boolean last = IFHelper.lastIFinShortCutChain(info,
										ifst, currentForIndex);
								boolean c = IFHelper.addCodeStatementWRTShortcutOR(ifst,
										op.getOperandValue() + "!=null", print,
										"if", last, op.getOperandValue()
												+ "== null");
								if (c) {
									if (addifbreak) {
										behavior.appendToBuffer( Util
												.formatDecompiledStatement(tempString));
									}
									tempString = "\nif(" + op.getOperandValue()
											+ "== null)\n{\n";
									behavior.appendToBuffer( Util
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
						boolean last = IFHelper.lastIFinShortCutChain(info, ifst,
								currentForIndex);
						boolean c = IFHelper.addCodeStatementWRTShortcutOR(ifst, op
								.getOperandValue()
								+ "!=null", true, "while", last, op
								.getOperandValue()
								+ "==null");
						if (c) {
							tempString = "\nwhile(" + op.getOperandValue()
									+ " == null)\n{\n";
							behavior.appendToBuffer( Util
									.formatDecompiledStatement(tempString));
						}
						

					} else {

						ifst.setElseCloseLineNumber(gotoIndex);

						isIfInScope = true;
						boolean print = true;
						boolean addifbreak = false;
						boolean bb = LoopHelper.isBeyondLoop(getGenericFinder().getJumpAddress(
								currentForIndex),
								getContext().getBehaviourLoops(), info);
                        thisLoop = GlobalVariableStore.getThisLoop();
                        if (bb && thisLoop != null && thisLoop.isInfinite()
								&& !encounteredAndOrComp && addBreak) {
							Loop dowl = LoopHelper.isIfInADoWhile(currentForIndex, ifst,
									getContext().getBehaviourLoops());
							if (dowl != null) {
								tempString = "";
							} else {
								tempString = "\nif(" + op.getOperandValue()
										+ "!= null)\n{\nbreak;\n}\n";
								// ifst.setIfHasBeenClosed(true);
								// codeStatements
								// +=Util.formatDecompiledStatement(tempString);
								addifbreak = true;
							}
							print = false;
						}
						boolean last = IFHelper.lastIFinShortCutChain(info, ifst,
								currentForIndex);
						boolean c = IFHelper.addCodeStatementWRTShortcutOR(ifst, op
								.getOperandValue()
								+ "!=null", print, "if", last, op
								.getOperandValue()
								+ "==null");
						if (c) {
							if (addifbreak) {
								behavior.appendToBuffer( Util
										.formatDecompiledStatement(tempString));
							}
							tempString = "\nif(" + op.getOperandValue()
									+ "== null)\n{\n";
							behavior.appendToBuffer( Util
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

				isIfInScope = true;
				// addBranchLabel(classIndex,i,ifst,currentForIndex,info);
				boolean bb = LoopHelper.isBeyondLoop(
						getGenericFinder().getJumpAddress( currentForIndex), getContext()
								.getBehaviourLoops(), info);
                thisLoop = GlobalVariableStore.getThisLoop();
                boolean print = true;
				boolean addifbreak = false;
				if (bb && thisLoop != null && thisLoop.isInfinite()
						&& !encounteredAndOrComp && addBreak) {

					Loop dowl = LoopHelper.isIfInADoWhile(currentForIndex, ifst, getContext()
							.getBehaviourLoops());
					if (dowl != null) {
						tempString = "";
					} else {
						tempString = "\nif(" + op.getOperandValue()
								+ "!= null)\n{\nbreak;\n}\n";
						// ifst.setIfHasBeenClosed(true);
						// codeStatements
						// +=Util.formatDecompiledStatement(tempString);
						addifbreak = true;
					}
					print = false;
				}

				boolean last = IFHelper.lastIFinShortCutChain(info, ifst,
						currentForIndex);
				boolean c = IFHelper.addCodeStatementWRTShortcutOR(ifst, op
						.getOperandValue()
						+ "!=null", print, "if", last, op.getOperandValue()
						+ "==null");

				if (c) {
					if (addifbreak) {
						behavior.appendToBuffer( Util
								.formatDecompiledStatement(tempString));
					}
					tempString = "\nif(" + op.getOperandValue()
							+ "== null)\n{\n";
					behavior.appendToBuffer( Util
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
