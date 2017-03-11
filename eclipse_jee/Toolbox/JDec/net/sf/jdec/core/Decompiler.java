/*
 **
 *  Decompiler.java Copyright (c) 2006,07 Swaroop Belur
 *
 * This program is free software; you can redistribute it and/itor
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
 **
 */

package net.sf.jdec.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import net.sf.jdec.blockhelpers.BranchHelper;
import net.sf.jdec.blockhelpers.IFHelper;
import net.sf.jdec.blockhelpers.LoopHelper;
import net.sf.jdec.blockhelpers.TryHelper;
import net.sf.jdec.blocks.CatchBlock;
import net.sf.jdec.blocks.FinallyBlock;
import net.sf.jdec.blocks.IFBlock;
import net.sf.jdec.blocks.Loop;
import net.sf.jdec.blocks.Switch;
import net.sf.jdec.blocks.TryBlock;
import net.sf.jdec.blocks.TryCatchFinally;
import net.sf.jdec.blocks.Switch.Case;
import net.sf.jdec.commonutil.Objects;
import net.sf.jdec.config.Configuration;
import net.sf.jdec.constantpool.CPString;
import net.sf.jdec.constantpool.ClassDescription;
import net.sf.jdec.constantpool.ClassInfo;
import net.sf.jdec.constantpool.DoublePrimitive;
import net.sf.jdec.constantpool.FieldRef;
import net.sf.jdec.constantpool.FloatPrimitive;
import net.sf.jdec.constantpool.IntPrimitive;
import net.sf.jdec.constantpool.InterfaceMethodRef;
import net.sf.jdec.constantpool.LongPrimitive;
import net.sf.jdec.constantpool.MethodRef;
import net.sf.jdec.constantpool.NameAndType;
import net.sf.jdec.constantpool.MethodInfo.ExceptionTable;
import net.sf.jdec.core.ShortcutAnalyser.Shortcutchain;
import net.sf.jdec.core.ShortcutAnalyser.Shortcutstore;
import net.sf.jdec.io.Writer;
import net.sf.jdec.jvminstructions.commandholders.IInstructionCommandHolder;
import net.sf.jdec.jvminstructions.commands.AbstractInstructionCommand;
import net.sf.jdec.jvminstructions.util.InstrConstants;
import net.sf.jdec.jvminstructions.util.InstructionHelper;
import net.sf.jdec.lookup.BranchInstrFinder;
import net.sf.jdec.lookup.FinderFactory;
import net.sf.jdec.lookup.IFinder;
import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.reflection.ConstructorMember;
import net.sf.jdec.util.AllExceptionHandler;
import net.sf.jdec.util.ExecutionState;
import net.sf.jdec.util.Util;

/*******************************************************************************
 * 
 * @author swaroop belur
 */
public class Decompiler {

	private long startTime;

	private Behaviour behaviour;

	private ClassDescription cd;

	private OperandStack opStack;

	private Hashtable synchSkips = null;

	private List sortedsynchSkips = null;

	private List frozenMonitorStarts = new ArrayList();

	private List frozenMonitorExists = new ArrayList();

	private ShortcutAnalyser sanalyser = null;

	/***************************************************************************
	 * 
	 * @param behaviour
	 *            Represents Either an Instance of Method of a class or a
	 *            constructor of a class
	 */

	public Decompiler(Behaviour behaviour, ClassDescription cd) {
		Util.resetCurrentSpace();
		ExecutionState.setMethodContext(behaviour);
		GlobalVariableStore.reset();
		behaviour.setClassRef(ConsoleLauncher.getClazzRef());
		sanalyser = behaviour.getShortCutAnalyser();
		StringBuffer t = new StringBuffer("");
		Util.checkForImport(behaviour.getReturnType(), t);
		this.behaviour = behaviour;
		this.cd = cd;
		// opStack = new OperandStack();
		opStack = behaviour.getOpStack();
		if (cd.isClassCompiledWithMinusG() == false) {

			storeDataTypesForMethodParams(this.behaviour, this.cd);
			createLocalVariablesForMethodParams(this.behaviour, this.cd);
			if (this.behaviour.getCode() != null) {
				// BigInteger b
				storeDataTypesWRTConversionInst(this.behaviour, this.cd);
				storeDataTypesWRTMethodParams(this.behaviour, this.cd);
				storeDataTypesWRTLoadNStoreCombinations(this.behaviour, this.cd);
				long l = -1, l2 = -1;
				try {
					anewarrayrefpos = new Hashtable();
					GlobalVariableStore.setProblematicInvokes(new ArrayList());
					// l=System.currentTimeMillis();
					// System.out.println("Before Invoke "+(l-Jdec.l3));
					storeDataTypesWRTInvokeInst(this.behaviour, this.cd);
					// l2=System.currentTimeMillis();
					// System.out.println("After Invoke "+(l2-l));
				} catch (RuntimeException re) {

					AllExceptionHandler handler = new AllExceptionHandler(re,
							"Runtime Exception while handling storeDataTypesWRTInvokeInst");
					handler.setBehaviour(behaviour);
					handler.reportException(); // TODO: Uncomment This later
					ConsoleLauncher.addCouldNotFinish(behaviour);
					l2 = System.currentTimeMillis();
					return;
				}

			}
			// java.lang.String s="sdfasdf";

		}
		if (cd.isClassCompiledWithMinusG()) {
			// storeDataTypesWRTInvokeInst(this.behaviour,this.cd);
		}

		findSkipRangesWRTSynchronizedBlocks();

		setSynchBlockRangesFromSynchBlocks();

	}

	/***************************************************************************
	 * Algo to correct the synch bug use the synchblocks sort by start for each :
	 * get the first above monitorexist then get first not frozen monitor enter
	 * for this..freeze this mark start as monitor enter, end as this exit
	 * 
	 * finally in resetEndOfSynchBlockIfNecessary skip those for which monitor
	 * exits have been frozen also.
	 * 
	 */
	private void setSynchBlockRangesFromSynchBlocks() {
		if (sortedsynchSkips == null)
			return;
		byte code[] = behaviour.getCode();
		ArrayList allstarts = behaviour.getInstructionStartPositions();

		outerfor: for (int k = 0; k < sortedsynchSkips.size(); k++) {
			int astart = ((SynchSkipHelper) sortedsynchSkips.get(k)).start;
			int aend = ((SynchSkipHelper) sortedsynchSkips.get(k)).end;
			for (int from = astart - 1; from >= 0; from--) {
				boolean isstart = isThisInstrStart(allstarts, from);
				if (isstart) {
					int current = code[from];
					if (current == JvmOpCodes.MONITOREXIT) {
						for (int j = from - 1; j >= 0; j--) {
							isstart = isThisInstrStart(allstarts, j);
							if (isstart) {
								int temp = code[j];
								if (temp == JvmOpCodes.MONITORENTER) {
									if (frozenMonitorStarts
											.contains(new Integer(j))) {
										continue;
									} else {
										ExceptionTable aTable = getSynchBlockGivenStart(j);
										if (aTable != null) {
											frozenMonitorStarts
													.add(new Integer(j));
											frozenMonitorExists
													.add(new Integer(from));
											aTable.setSynchEnd(from);

											continue outerfor;
										}
									}
								}
							}
						}
					}
				}

			}
		}
	}

	private ExceptionTable getSynchBlockGivenStart(int start) {
		ArrayList synchtables = behaviour.getSynchronizedEntries();
		if (!Objects.isNullOrEmpty(synchtables)) {

			for (int z = 0; z < synchtables.size(); z++) {

				ExceptionTable table = (ExceptionTable) synchtables.get(z);
				if (table.getMonitorEnterPosInCode() == start) {
					return table;
				}

			}

		}
		return null;
	}

	public void decompileCode() {

		StringBuffer buffer = new StringBuffer();
		try {
			if (behaviour == null) {
				java.lang.String mesg = "Exception Occured while Disassembling Code\n";
				mesg += "The behaviour Object was null";
				AllExceptionHandler handler = new AllExceptionHandler(
						new NullPointerException(mesg));
				handler.reportException();

			} else {
				// Log Message

				java.lang.String mesg = "\n[INFO]Attempting to generate code represented By\n";
				mesg += "[INFO]Method Name :\t" + behaviour.getBehaviourName()
						+ "\n";
				Writer writer = Writer.getWriter("log");
				writer.writeLog(mesg, Configuration.getLogLevel());
				writer.flush();

				// Write to ConsoleDetail file For UI....

				// Attempt to Dis

				byte[] bytecodes = behaviour.getCode();

				/**
				 * NOTE:
				 * 
				 * opcodes below represents the disassembled String for the
				 * behaviour... This Disasembler Object has the object ref to
				 * the behaviour passed. Since the opcode is the complete
				 * disassembled code for this behaviour, we can go and set it
				 * for the behaviour object using Reflection API. Later on The
				 * code to print out the complete representation of the Class
				 * can access the
				 * 
				 * Reflection API to print out the class...
				 * 
				 * 
				 */

				startTime = System.currentTimeMillis();
				if (Configuration.getDecompileroption().equalsIgnoreCase("dis")) {
					// disassembleJVMOpCodes(bytecodes);
				} else
					parseJVMOpCodes(bytecodes);

				if (this.behaviour != null) {
					// System.out.println(this.behaviour.getBehaviourName()+"
					// BEHAVIOUR NAME");
					// this.behaviour.setVMInstructions(opcodes);
					// this.behaviour.setCodeStatements(code)
				}

			}

		}

		catch (IOException ioe) {
			AllExceptionHandler handler = new AllExceptionHandler(ioe);
			handler.setBehaviour(behaviour);
			handler.reportException();
			// Just in Case Code Returns from
			// reportException method

		} catch (RuntimeException re) {

			AllExceptionHandler handler = new AllExceptionHandler(re,
					"Runtime Exception while handling storeDataTypesWRTInvokeInst");
			handler.setBehaviour(behaviour);
			handler.reportException(); // TODO: Uncomment This later
			ConsoleLauncher.addCouldNotFinish(behaviour);
			return;

		}

	}

	private int currentForIndex;

	private LocalVariableStructure structure = null;

	private boolean createdLocalVariableStructure;

	private ArrayList returnsaddedAtIfElse = null;

	private GeneratedIfTracker generatedIfTracker = new GeneratedIfTracker();

	public void parseJVMOpCodes(byte[] info) {

		GlobalVariableStore.setContinue_JumpOffsets(new ArrayList());
		GlobalVariableStore.setBreak_JumpOffsets(new ArrayList());
		GlobalVariableStore.setLabelAssociated(new Hashtable());
		GlobalVariableStore.setRetAtIfElseEnd(new Hashtable());
		ConsoleLauncher.setCurrentMethodBeingExecuted(this.behaviour);
		createdLocalVariableStructure = false; // for -g:none option
		GlobalVariableStore.setReturnsAtI(new HashMap());

		ArrayList methodTries = behaviour.getAllTriesForMethod();
		Operand tempOperand = null;
		java.lang.String tempString = "";

		behaviour.replaceBuffer("");
		int opValueI = Integer.MIN_VALUE;
		byte opValueB = Byte.MIN_VALUE;
		float opValueF = Float.MIN_VALUE;
		double opValueD = Double.NEGATIVE_INFINITY;
		long opValueL = Long.MIN_VALUE;
		java.lang.String errorMesg = "";
		IntPrimitive constInt = null;
		FloatPrimitive constFloat = null;
		DoublePrimitive constDouble = null;
		LongPrimitive constLong = null;
		ClassInfo constCInfo = null;
		CPString constString = null;
		MethodRef mref = null;
		FieldRef fref = null;
		NameAndType ninfo = null;
		java.lang.String stringLiteral = null;

		Operand op = null;
		Operand op1 = null;
		Operand op2 = null;
		Operand op3 = null;
		Operand op4 = null;

		IFBlock ifst = null;

		Behaviour b = null;

		java.lang.String funcCall = "";

		int type = -1;

		Hashtable methodLookUp = cd.getMethodLookUp();

		LocalVariableTable table = LocalVariableTable.getInstance();
		boolean cons = false;
		if (behaviour instanceof ConstructorMember) {
			cons = true;
		}

		structure = table
				.getLocaVarStructure(behaviour.getBehaviourName().concat(
						behaviour.getStringifiedParameters()).concat("" + cons));
		if (structure != null)
			behaviour.setMethodLocalVariables(structure);
		LocalVariable local = null;
		GlobalVariableStore.setLABELS(new Hashtable());
		returnsaddedAtIfElse = new ArrayList();

		boolean invokevirtualFound = false;
		ArrayList inststarts = behaviour.getInstructionStartPositions();
		GlobalVariableStore.setMultinewfound(false);

		forloop: for (int i = 0; i < info.length; i++) {

			///System.out.println(i+"forindex");
			currentForIndex = i;

			IFHelper.addGeneratedIfCode(currentForIndex, generatedIfTracker);

			ExecutionState.setCurrentInstructionPosition(currentForIndex);

			java.lang.String doWhileCloseStmt = closeDoWhile(i);

			java.lang.String printLater = "";

			boolean isStart = isThisInstrStart(inststarts, i);
			if (!isStart)
				continue;

			boolean skipWRTbooleanShrtAssgn = checkForSkipWRTbooleanShortcutAssignFound(i);
			if (skipWRTbooleanShrtAssgn)
				continue;

			boolean skipWRTPostIncr = checkForPostIncrSkip(GlobalVariableStore
					.getSkipsWRTPostIncr(), i);
			if (skipWRTPostIncr)
				continue;

			boolean skipIteration = skipIterationWRTIFSAtEndOFLoop(i, info);
			if (skipIteration) {
				behaviour.appendToBuffer(Util
						.formatDecompiledStatement(doWhileCloseStmt));

				continue;
			}

			StringBuffer resetPos = new StringBuffer("");

			resetEndOfSynchBlockIfNecessary(i);

			boolean avoidskip = false;

			boolean skip = false;

			if (!avoidskip)
				skip = skipCurrentIteraton(i, false, info);
			if (!skip && !avoidskip) {
				skip = checkWRTClassLoadIF(i);
			}
			ArrayList starts = behaviour.getInstructionStartPositions();
			if (skip
					&& (info[i] == JvmOpCodes.DUP || info[i] == JvmOpCodes.DUP2)
					&& isThisInstrStart(starts, i)) {
				GlobalVariableStore.setDupnothandled(true);
			}

			if (skip && Configuration.getDecompileroption().equals("dc")
					&& !avoidskip) {
				continue;
			}

			try {

				boolean needtoresetelsehasbugun = false;

				int prevstart = getPrevStartCodePos(info, i);

				StringBuffer ifelsecode = new StringBuffer("");
				StringBuffer reset = new StringBuffer("");
				boolean oktoendifelse = isItoktoendifelse(i);
				if (oktoendifelse) {
					checkForIFElseEndStatement(info, behaviour.getMethodIfs(),
							i, reset, opStack, ifelsecode, "if");
				}
				boolean ifbeforeclashedhandler = TryHelper
						.doesClashedIfWithExceptionHandlerStartBefore(i);
				if (!ifbeforeclashedhandler)
					behaviour.appendToBuffer(ifelsecode.toString());

				if (doWhileCloseStmt.trim().length() > 0) {
					IFinder finder = FinderFactory.getFinder(IFinder.BASE);
					int prev = finder.getPrevStartOfInst(currentForIndex);
					if (prev > 0) {
						finder = FinderFactory.getFinder(IFinder.BRANCH);
						boolean isif = finder.isInstructionIF(prev);
						if (isif) {
							IFBlock aif = IFHelper.getIfGivenStart(prev);
							if (aif.getIfStart() == aif.getIfCloseLineNumber()) {
								if (aif.IfHasBeenClosed()) {
									String ifendstmt = "\nbreak;\n}\n";
									ifendstmt = Util
											.formatDecompiledStatement(ifendstmt);
									behaviour.appendToBuffer(ifendstmt);
								}
							}
						}
					}
				}
				behaviour.appendToBuffer(Util
						.formatDecompiledStatement(doWhileCloseStmt));

				java.lang.String brlbl = getAnyLabelAtI(GlobalVariableStore
						.getLABELS(), i);

				if (brlbl != null && brlbl.trim().length() > 0) {
					behaviour.appendToBuffer("\n" + brlbl + ":\n");
				} else {
					behaviour.appendToBuffer("\n" + "#FORINDEX" + i + "#"
							+ "\n");
				}

				int instructionAtI = info[i];
				boolean skipexcp = skipExceptionTables(i);
				java.lang.String fromExceptionTable = "";
				if (!skipexcp)
					fromExceptionTable = pollExcepionTables(i, instructionAtI);

				boolean end = isIEndOfGuard(i, behaviour);
				boolean returnadded = false;
				int returnposincode = -1;
				if (end) {
					StringBuffer sb = new StringBuffer("");
					java.lang.String returnString = isAnyReturnPresentInSkipRegion(
							info, i, behaviour, sb);
					java.lang.String str = sb.toString();
					try {
						returnposincode = Integer.parseInt(str);
					} catch (NumberFormatException ne) {
					}

					if (returnString != null
							&& returnString.trim().length() == 0) {
						if (i == (info.length - 1)) {
							returnString = getReturnTypeInst(info, i);
							returnposincode = i;

						}
					}
					java.lang.Object val = null;

					if (returnString != null
							&& returnString.trim().length() > 0) {
						int loadIndex = -1;
						StringBuffer stb = new StringBuffer("");
						if (returnposincode != -1)
							loadIndex = getLoadIndexForReturn(info,
									returnposincode, stb);
						if (loadIndex != -1) {
							LocalVariableStructure struck = behaviour
									.getLocalVariables();
							int rangeinx = Integer.parseInt(stb.toString());
							if (rangeinx != -1) {
								if (cd.isClassCompiledWithMinusG()) {
									LocalVariable temp = struck
											.getVariabelAtIndex(loadIndex,
													rangeinx);
									if (temp != null) {
										op = new Operand();
										op.setOperandValue(temp.getVarName());
										op.setOperandName(temp.getVarName());
										opStack.push(op);
									}

								} else {
									LocalVariable temp = struck
											.getVariabelAtIndex(loadIndex);
									if (temp != null) {
										op = new Operand();
										op.setOperandValue(temp.getVarName());
										op.setOperandName(temp.getVarName());
										opStack.push(op);
									}
								}
							}
						}
						int returnPos = getReturnStringPosInCode(info, i,
								behaviour);
						if (returnString.equals("return") == false
								&& opStack.size() > 0) {
							// StackTop=opStack.peekTopOfStack();
							Operand StackTop = opStack.getTopOfStack();
							if (StackTop != null) {
								val = StackTop.getOperandValue();
								if (val != null)
									val = val.toString();
							}
							if (StackTop != null) {

								if (val != null)
									tempString = "return " + val;
								else
									tempString = "return ";
								behaviour.appendToBuffer(Util
										.formatDecompiledStatement(tempString));

								returnadded = true;
							}

						} else if (returnString.equals("return") == true) {
							int prev = currentForIndex - 1;
							boolean oktoadd = true;
							if (isThisInstrStart(behaviour
									.getInstructionStartPositions(), prev)) {
								if (info[prev] == JvmOpCodes.ATHROW) {
									oktoadd = false;
								}
							}
							if (oktoadd) {
								tempString = "return ";
								behaviour.appendToBuffer(Util
										.formatDecompiledStatement(tempString));

								returnadded = true;
							}
						}
						if (returnadded) {
							if (returnPos != -1) {
								GlobalVariableStore.getReturnsAtI().put(
										new Integer(returnPos), "true");
							} else {
								GlobalVariableStore.getReturnsAtI().put(
										new Integer(i), "true");
							}
						}

					}

				}

				if (returnadded)
					behaviour.appendToBuffer(";\n");
				if (fromExceptionTable.indexOf("finally") != -1)
					fromExceptionTable = fromExceptionTable + " ";

				else
					fromExceptionTable = fromExceptionTable;
				tempString = fromExceptionTable;

				java.lang.String printExceptionTable = "";
				if (tempString != null && tempString.length() > 0)
					printExceptionTable = tempString;

				Iterator loopIterator = behaviour.getBehaviourLoops()
						.iterator();
				while (loopIterator.hasNext()) {
					Loop iloop = (Loop) loopIterator.next();
					if (iloop.getStartIndex() == i && iloop.isInfinite()) {
						boolean clash = doesthisClashWithCaseBegin(behaviour
								.getAllSwitchBlks(), i);
						boolean loopsEndAtIf = doesLoopHaveAnIfAtEnd(i, info,
								iloop);
						if (clash) {
							if (iloop.printloopStart()) {
								printLater = "while(true)\n{\n"; // :CHECK
								if (iloop.isDoWhile()) {
									Util.forceTrimLines = false;
									printLater = Util
											.formatDecompiledStatement("while(true)\n{\n");
									printLater += "//INSERT IFCODE FOR DOWHILE HERE"
											+ iloop.getStartIndex() + "\n";
									GlobalVariableStore.getDowhileloopmarkers()
											.add(
													new Integer(iloop
															.getStartIndex()));
									Util.forceTrimLines = true;
								}

								iloop.setPrintloopStart(false);
							}
							if (loopsEndAtIf) {
								java.lang.String loopendbody = anyWhileBodyHere(
										iloop.getEndIndex(), iloop, opStack);
								if (loopendbody.trim().length() > 0) {

									printLater += loopendbody;

								}
							}

						}
						if (!clash) {
							if (iloop.printloopStart()) {
								int after = isLoopAfterTry(iloop.getEndIndex());
								if (after == 0) {
									tempString = fromExceptionTable;
									behaviour
											.appendToBuffer(Util
													.formatDecompiledStatement(tempString));
									tempString = "while(true)\n{\n";
									if (iloop.isDoWhile()) {
										Util.forceTrimLines = false;
										tempString = Util
												.formatDecompiledStatement("while(true)\n{\n");
										tempString += "//INSERT IFCODE FOR DOWHILE HERE"
												+ iloop.getStartIndex() + "\n";
										GlobalVariableStore
												.getDowhileloopmarkers()
												.add(
														new Integer(
																iloop
																		.getStartIndex()));
										Util.forceTrimLines = true;
									}
									behaviour
											.appendToBuffer(Util
													.formatDecompiledStatement(tempString));
									fromExceptionTable = "";
								} else if (after == 1) {
									tempString = "while(true)\n{\n";
									if (iloop.isDoWhile()) {
										Util.forceTrimLines = false;
										tempString = Util
												.formatDecompiledStatement("while(true)\n{\n");
										tempString += "//INSERT IFCODE FOR DOWHILE HERE"
												+ iloop.getStartIndex() + "\n";
										Util.forceTrimLines = true;
										GlobalVariableStore
												.getDowhileloopmarkers()
												.add(
														new Integer(
																iloop
																		.getStartIndex()));
									}
									behaviour
											.appendToBuffer(Util
													.formatDecompiledStatement(tempString));
									behaviour
											.appendToBuffer(Util
													.formatDecompiledStatement(fromExceptionTable));

									fromExceptionTable = "";
								} else if (after == 2) {
									tempString = "while(true)\n{\n";//
									if (iloop.isDoWhile()) {
										Util.forceTrimLines = false;
										tempString = Util
												.formatDecompiledStatement("while(true)\n{\n");
										tempString += "//INSERT IFCODE FOR DOWHILE HERE"
												+ iloop.getStartIndex() + "\n";
										Util.forceTrimLines = true;
										GlobalVariableStore
												.getDowhileloopmarkers()
												.add(
														new Integer(
																iloop
																		.getStartIndex()));
									}
									behaviour
											.appendToBuffer(Util
													.formatDecompiledStatement(tempString));
								}

								iloop.setPrintloopStart(false);
							}

							printLater = "";
							if (loopsEndAtIf) {
								java.lang.String loopendbody = anyWhileBodyHere(
										iloop.getEndIndex(), iloop, opStack);
								if (loopendbody.trim().length() > 0) {

									behaviour
											.appendToBuffer(Util
													.formatDecompiledStatement(loopendbody));
									iloop.setPrintloopStart(false);
								}
							}
						}
					} else {
						boolean dowhile = checkForDoWhile(currentForIndex,
								info, iloop);
						if (dowhile == false) {
							if (iloop.getEndIndex() == i) {
								tempString = "";// anyWhileBodyHere(i,iloop,opStack);
								if (tempString.trim().length() > 0) {
									tempString = tempString + "}\n";
									iloop.setWasLoopClosedInCode(true);
									i = i + 2;
									behaviour
											.appendToBuffer(Util
													.formatDecompiledStatement(tempString)); // TODO:
									// BUg
									// in
									// formatting
									continue forloop;
								} else {
									if (iloop.wasLoopClosedInCode() == false) {
										tempString = "}\n"; // le
										behaviour
												.appendToBuffer(Util
														.formatDecompiledStatement(tempString));
										iloop.setWasLoopClosedInCode(true);
									}
								}
							} else if (iloop.getLoopEndForBracket() == i
									&& iloop.wasLoopClosedInCode() == false) {

								tempString = "}\n";// \n"; // Loop End
								behaviour.appendToBuffer(Util
										.formatDecompiledStatement(tempString));
								iloop.setWasLoopClosedInCode(true);
							}
						}

						// Check wrt loopendduetoreset
						if (iloop.getLoopendDueToReset() == i) {
							if (iloop.wasLoopClosedInCode() == false) {
								tempString = "}\n";
								behaviour.appendToBuffer(Util
										.formatDecompiledStatement(tempString));
								iloop.setWasLoopClosedInCode(true);
							}
						}

					}

				}

				java.lang.String switchStmt = pollSwitchBlksForMethod(i, info);

				if (casefound) {

					behaviour.appendToBuffer(switchStmt);
					behaviour
							.appendToBuffer(((switchStmt.trim().length() == 0) ? ""
									: "\n"));
					behaviour.appendToBuffer(Util
							.formatDecompiledStatement(fromExceptionTable));
					behaviour.appendToBuffer(((fromExceptionTable.trim()
							.length() == 0) ? "" : "\n"));
				} else {
					Util.forceTrimLines = true;
					behaviour.appendToBuffer(Util
							.formatDecompiledStatement(fromExceptionTable));
					behaviour.appendToBuffer(((fromExceptionTable.trim()
							.length() == 0) ? "" : "\n"));
					behaviour.appendToBuffer(switchStmt);
					behaviour
							.appendToBuffer(((switchStmt.trim().length() == 0) ? ""
									: "\n"));

					if (ifbeforeclashedhandler) {
						if (ifelsecode.toString().startsWith(" ")) {
							ifelsecode = new StringBuffer(ifelsecode.toString()
									.substring(1));
						}
						if (ifelsecode.toString().startsWith(" ")) {
							ifelsecode = new StringBuffer(ifelsecode.toString()
									.substring(1));
						}
						if (ifelsecode.toString().startsWith(" ")) {
							ifelsecode = new StringBuffer(ifelsecode.toString()
									.substring(1));
						}
						if (ifelsecode.toString().startsWith(" ")) {
							ifelsecode = new StringBuffer(ifelsecode.toString()
									.substring(1));
						}
						behaviour.appendToBuffer("\n" + ifelsecode.toString());
						// Util .updateCurrentSpace(Util.getIncrementSpace(),
						// "add");
					}
					Util.forceTrimLines = true;
				}

				// OK in all cases?

				// NOTE: printLater should only be used for while statement .
				// ...Not For anything else

				if (printLater.trim().length() > 0) {
					behaviour.appendToBuffer(Util
							.formatDecompiledStatement(printLater));
					printLater = "";

				}

				java.lang.String synch = pollsynchblocks(i);
				behaviour.appendToBuffer(Util.formatDecompiledStatement(synch));

				skip = skipCurrentIteraton(i, true, info);
				if (skip
						&& (info[i] == JvmOpCodes.DUP || info[i] == JvmOpCodes.DUP2)
						&& isThisInstrStart(starts, i)) {
					GlobalVariableStore.setDupnothandled(true);
				}

				if (skip && !avoidskip)
					continue;
				GlobalVariableStore.setThisLoop(null);

				skip = skipIterationIf_IFEndOfloop(i, info);
				if (skip && !avoidskip) {
					i = i + 2;
					continue;
				}

				int skipBytes;

				if (isInstructionIF(info[currentForIndex])) {
					int loopBeginForIf = IFHelper
							.attachMarkerToIfInDoWhileKindOfLoop(currentForIndex);
					if (loopBeginForIf != -1) {
						if (GlobalVariableStore.getDowhileloopmarkers() != null
								&& GlobalVariableStore.getDowhileloopmarkers()
										.contains(new Integer(loopBeginForIf)))
							behaviour.appendToBuffer("\n<IF_AT_LOOP_END_BEGIN_"
									+ loopBeginForIf + "_>\n");

					}
				}

				switch (info[i]) {
				case JvmOpCodes.AALOAD:
					skipBytes = handleAALOAD(opStack);
					i = i + skipBytes;
					continue;
				case JvmOpCodes.AASTORE:
					skipBytes = handleAASTORECase();
					i = i + skipBytes;
					continue;
				case JvmOpCodes.ACONST_NULL:
					skipBytes = handleACONSTNULL();
					i = i + skipBytes;
					continue;
				case JvmOpCodes.ALOAD:
					skipBytes = handleAloadCase(info, i, opStack);
					i = i + skipBytes;
					continue;

				case JvmOpCodes.ALOAD_0:

					skipBytes = handlesimpleaload(0);
					i = i + skipBytes;

					continue;
				case JvmOpCodes.ALOAD_1:

					skipBytes = handlesimpleaload(1);
					i = i + skipBytes;
					continue;
				case JvmOpCodes.ALOAD_2:

					skipBytes = handlesimpleaload(2);
					i = i + skipBytes;
					continue;
				case JvmOpCodes.ALOAD_3:
					skipBytes = handlesimpleaload(3);
					i = i + skipBytes;
					continue;

				case JvmOpCodes.ANEWARRAY:

					skipBytes = handleANEWARRAYCase(info);
					i = i + skipBytes;
					continue;
				case JvmOpCodes.ARETURN:

					skipBytes = handleARETURNCase(currentForIndex,
							GlobalVariableStore.getReturnsAtI());
					i = i + skipBytes;
					continue;
				case JvmOpCodes.ARRAYLENGTH:
					skipBytes = handleARRAYLENGTHCase();
					i = i + skipBytes;
					continue;
				case JvmOpCodes.ASTORE:
					skipBytes = handleComplexAStore(info, i);
					i = i + skipBytes;

					continue;
				case JvmOpCodes.ASTORE_0:
					skipBytes = handleSimpleASTORECase(i, 0);
					i = i + skipBytes;
					continue;
				case JvmOpCodes.ASTORE_1:

					skipBytes = handleSimpleASTORECase(i, 1);
					i = i + skipBytes;
					continue;
				case JvmOpCodes.ASTORE_2:
					skipBytes = handleSimpleASTORECase(i, 2);
					i = i + skipBytes;
					continue;
				case JvmOpCodes.ASTORE_3:
					skipBytes = handleSimpleASTORECase(i, 3);
					i = i + skipBytes;
					continue;
				case JvmOpCodes.ATHROW:

					skipBytes = handleATHROW(info);
					i = i + skipBytes;
					continue;

					// LETTER B
				case JvmOpCodes.BALOAD:

					skipBytes = handleBALOAD(opStack);
					i = i + skipBytes;
					continue;
				case JvmOpCodes.BASTORE:
					skipBytes = handleBASTORE(opStack);
					i = i + skipBytes;
					continue;
				case JvmOpCodes.BIPUSH:
					skipBytes = handleBIPush(info);
					i = i + skipBytes;

					continue;

					// LETTER C
				case JvmOpCodes.CALOAD:
					skipBytes = handleCALOAD(opStack);
					i = i + skipBytes;
					continue;
				case JvmOpCodes.CASTORE:

					skipBytes = handleCASTORE(opStack);
					i = i + skipBytes;
					continue;
				case JvmOpCodes.CHECKCAST:

					handleCheckCast(opStack, info);
					i = i + 2;
					continue;

					// LETTER D
				case JvmOpCodes.D2F:
					skipBytes = handleD2F();
					i = i + skipBytes;
					continue;
				case JvmOpCodes.D2I:
					skipBytes = handleD2I();
					i = i + skipBytes;
					continue;
				case JvmOpCodes.D2L:
					skipBytes = handleD2L();
					i = i + skipBytes;
					continue;
				case JvmOpCodes.DADD:
					skipBytes = handleDADD();
					i = i + skipBytes;
					continue;
				case JvmOpCodes.DALOAD:

					skipBytes = handleDALOAD(opStack);
					i = i + skipBytes;
					continue;
				case JvmOpCodes.DASTORE:
					skipBytes = handleDASTORE(opStack);
					i = i + skipBytes;
					continue;
				case JvmOpCodes.DCMPG:
					skipBytes = handleDCMPG(opStack, info);
					i = i + skipBytes;
					continue;
				case JvmOpCodes.DCMPL:
					skipBytes = handleDCMPL(opStack, info);
					i = i + skipBytes;
					continue;
				case JvmOpCodes.DCONST_0:
					handleDCONST(opStack, 0.0);

					continue;
				case JvmOpCodes.DCONST_1:
					handleDCONST(opStack, 1.0);

					continue;

				case JvmOpCodes.DDIV:
					handleDDIV(opStack);

					continue;
				case JvmOpCodes.DLOAD:

					skipBytes = handleDLOADCase(JvmOpCodes.DLOAD);
					i = i + skipBytes;
					continue;
				case JvmOpCodes.DLOAD_0:

					handleDLOADCase(JvmOpCodes.DLOAD_0);

					continue;
				case JvmOpCodes.DLOAD_1:

					handleDLOADCase(JvmOpCodes.DLOAD_1);

					continue;
				case JvmOpCodes.DLOAD_2:

					handleDLOADCase(JvmOpCodes.DLOAD_2);

					continue;
				case JvmOpCodes.DLOAD_3:

					handleDLOADCase(JvmOpCodes.DLOAD_3);

					continue;
				case JvmOpCodes.DMUL:
					handleDMUL(opStack);

					continue;
				case JvmOpCodes.DNEG:
					handleDNEG(opStack);

					continue;
				case JvmOpCodes.DREM:
					handleDREM(opStack);

					continue;
				case JvmOpCodes.DRETURN:
					handleDRETURN(info);

					// behaviour.getParentBehaviour().getOpStack().push(op);
					/*
					 * parsedString += "DRETURN"; parsedString+="\n";
					 * parsedString+="\t";parsedString+="\t";
					 */
					continue;
				case JvmOpCodes.DSTORE:

					skipBytes = handleComplexDStore();
					i = i + skipBytes;

					continue;
				case JvmOpCodes.DSTORE_0:

					handleSimpleDstoreCaseInst(JvmOpCodes.DSTORE_0);

					continue;
				case JvmOpCodes.DSTORE_1:

					handleSimpleDstoreCaseInst(JvmOpCodes.DSTORE_1);

					continue;
				case JvmOpCodes.DSTORE_2:

					handleSimpleDstoreCaseInst(JvmOpCodes.DSTORE_2);

					continue;
				case JvmOpCodes.DSTORE_3:

					handleSimpleDstoreCaseInst(JvmOpCodes.DSTORE_3);

					continue;
				case JvmOpCodes.DSUB:
					handleDSUB();

					continue;
				case JvmOpCodes.DUP:
					handleDUP(opStack);

					continue;
				case JvmOpCodes.DUP_X1:
					handleDUPX1(opStack);
					continue;
				case JvmOpCodes.DUP_X2:
					handleDUPX2();
					continue;
				case JvmOpCodes.DUP2:
					handleDUP2(opStack);

					continue;
				case JvmOpCodes.DUP2_X1:
					handleDUP2X1(opStack);
					continue;
				case JvmOpCodes.DUP2_X2:
					handleDUP2X2(opStack);

					continue;

					// LETTER F
				case JvmOpCodes.F2D:
					handleF2D();

					continue;
				case JvmOpCodes.F2I:
					handleF2I();
					continue;
				case JvmOpCodes.F2L:
					handleF2L();
					continue;
				case JvmOpCodes.FADD:
					handleFADD();

					continue;
				case JvmOpCodes.FALOAD:

					handleFALOAD();
					continue;
				case JvmOpCodes.FASTORE:

					handleFASTORE();

					continue;
				case JvmOpCodes.FCMPG:
					handleFCMPG(opStack, info);
					continue;
				case JvmOpCodes.FCMPL:
					handleFCMPL(opStack, info);
					continue;
				case JvmOpCodes.FCONST_0:
					handleFCONST(opStack, "0.0f");

					continue;
				case JvmOpCodes.FCONST_1:
					handleFCONST(opStack, "1.0f");
					continue;
				case JvmOpCodes.FCONST_2:
					handleFCONST(opStack, "2.0f");
					continue;
				case JvmOpCodes.FDIV:
					handleFDIV(opStack);

					continue;
				case JvmOpCodes.FLOAD:

					skipBytes = handleFLOAD(JvmOpCodes.FLOAD);
					i = i + skipBytes;
					continue;
				case JvmOpCodes.FLOAD_0:

					handleFLOAD(JvmOpCodes.FLOAD_0);

					continue;
				case JvmOpCodes.FLOAD_1:

					handleFLOAD(JvmOpCodes.FLOAD_1);

					// opStack.push(element);
					continue;
				case JvmOpCodes.FLOAD_2:
					handleFLOAD(JvmOpCodes.FLOAD_2);

					// opStack.push(element);
					continue;
				case JvmOpCodes.FLOAD_3:

					handleFLOAD(JvmOpCodes.FLOAD_3);

					// opStack.push(element);
					continue;
				case JvmOpCodes.FMUL:
					handleFMUL();

					continue;
				case JvmOpCodes.FNEG:
					handleFNEG();
					continue;
				case JvmOpCodes.FREM:
					handleFREM();
					continue;
				case JvmOpCodes.FRETURN:
					handleFRETURN();

					continue;
				case JvmOpCodes.FSTORE:

					skipBytes = handleComplexFSTORE();
					i = i + skipBytes;
					continue;
				case JvmOpCodes.FSTORE_0:

					handleSimpleFstoreCaseInst(opStack, info, 0);

					continue;
				case JvmOpCodes.FSTORE_1:

					handleSimpleFstoreCaseInst(opStack, info, 1);

					continue;

				case JvmOpCodes.FSTORE_2:

					handleSimpleFstoreCaseInst(opStack, info, 2);

					continue;
				case JvmOpCodes.FSTORE_3:

					handleSimpleFstoreCaseInst(opStack, info, 3);

					continue;
				case JvmOpCodes.FSUB:
					handleFSUB();

					continue;

					// LETTER G
				case JvmOpCodes.GETFIELD: // TODO: check whether this needs to
					// push classtype intp stack

					skipBytes = handleGetField(info);

					i += skipBytes;

					continue;
				case JvmOpCodes.GETSTATIC:

					skipBytes = handleGetStatic();
					i = i + skipBytes;

					continue;
				case JvmOpCodes.GOTO: // Mistake here. GOTO of switch not

					skipBytes = execute(InstrConstants.BRANCH_INSTR_TYPE, null,
							JvmOpCodes.GOTO);
					i = i + skipBytes;
					continue;
				case JvmOpCodes.GOTO_W: // TODO Need to do Test vigorously and
					skipBytes = execute(InstrConstants.BRANCH_INSTR_TYPE, null,
							JvmOpCodes.GOTO_W);
					i = i + skipBytes;

					// LETTER I
				case JvmOpCodes.I2B:
					handleI2B();
					continue;
				case JvmOpCodes.I2C:
					handleI2C();

					continue;
				case JvmOpCodes.I2D:
					handleI2D();
					continue;
				case JvmOpCodes.I2F:
					handleI2F();
					continue;
				case JvmOpCodes.I2L:
					handleI2L();
					continue;
				case JvmOpCodes.I2S:
					handleI2S();
					continue;
				case JvmOpCodes.IADD:
					handleIADD();
					continue;
				case JvmOpCodes.IALOAD:
					handleIALOAD();

					continue;
				case JvmOpCodes.IAND:
					handleIAND();
					continue;
				case JvmOpCodes.IASTORE:

					handleIASTORE();

					continue;
				case JvmOpCodes.ICONST_0:
					handleICONST(0);
					continue;
				case JvmOpCodes.ICONST_1:
					handleICONST(1);
					continue;
				case JvmOpCodes.ICONST_2:
					handleICONST(2);
					continue;
				case JvmOpCodes.ICONST_3:
					handleICONST(3);
					continue;
				case JvmOpCodes.ICONST_4:
					handleICONST(4);
					continue;
				case JvmOpCodes.ICONST_5:
					handleICONST(5);
					continue;
				case JvmOpCodes.ICONST_M1:
					handleICONST(-1);
					continue;
				case JvmOpCodes.IDIV:
					handleIDIV();
					continue;
				case JvmOpCodes.IF_ACMPEQ:
					skipBytes = handleIFACMPEQ(info);

					i = i + skipBytes;

					continue;
				case JvmOpCodes.IF_ACMPNE:
					handleIFACMPNE(info);
					i++;
					i++;
					continue;
				case JvmOpCodes.IF_ICMPEQ:
					skipBytes = execute(InstrConstants.BRANCH_INSTR_TYPE, null,
							JvmOpCodes.IF_ICMPEQ);
					i = i + skipBytes;
					continue;

				case JvmOpCodes.IF_ICMPNE:
					skipBytes = execute(InstrConstants.BRANCH_INSTR_TYPE, null,
							JvmOpCodes.IF_ICMPNE);
					i = i + skipBytes;
					continue;

				case JvmOpCodes.IF_ICMPLT:

					skipBytes = execute(InstrConstants.BRANCH_INSTR_TYPE, null,
							JvmOpCodes.IF_ICMPLT);
					i = i + skipBytes;
					continue;
				case JvmOpCodes.IF_ICMPGE:
					skipBytes = execute(InstrConstants.BRANCH_INSTR_TYPE, null,
							JvmOpCodes.IF_ICMPGE);
					i = i + skipBytes;
					continue;
				case JvmOpCodes.IF_ICMPGT:
					skipBytes = execute(InstrConstants.BRANCH_INSTR_TYPE, null,
							JvmOpCodes.IF_ICMPGT);
					i = i + skipBytes;
					continue;
				case JvmOpCodes.IF_ICMPLE:
					skipBytes = execute(InstrConstants.BRANCH_INSTR_TYPE, null,
							JvmOpCodes.IF_ICMPLE);
					i = i + skipBytes;
					continue;
				case JvmOpCodes.IFEQ:
					handleIFEQ(info);
					i += 2;
					continue;
				case JvmOpCodes.IFNE:
					execute(InstrConstants.BRANCH_INSTR_TYPE, null,
							JvmOpCodes.IFNE);
					i += 2;
					continue;
				case JvmOpCodes.IFLT:
					execute(InstrConstants.BRANCH_INSTR_TYPE, null,
							JvmOpCodes.IFLT);
					i += 2;
					continue;
				case JvmOpCodes.IFGE:
					execute(InstrConstants.BRANCH_INSTR_TYPE, null,
							JvmOpCodes.IFGE);
					i += 2;
					continue;
				case JvmOpCodes.IFGT:
					execute(InstrConstants.BRANCH_INSTR_TYPE, null,
							JvmOpCodes.IFGT);
					i += 2;
					continue;
				case JvmOpCodes.IFLE:
					execute(InstrConstants.BRANCH_INSTR_TYPE, null,
							JvmOpCodes.IFLE);
					i += 2;
					continue;
				case JvmOpCodes.IFNONNULL:
					execute(InstrConstants.BRANCH_INSTR_TYPE, null,
							JvmOpCodes.IFNONNULL);
					i += 2;

					continue;
				case JvmOpCodes.IFNULL:
					execute(InstrConstants.BRANCH_INSTR_TYPE, null,
							JvmOpCodes.IFNULL);
					i += 2;
					continue;
				case JvmOpCodes.IINC:
					execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
							JvmOpCodes.IINC);
					i += 2;
					continue;
				case JvmOpCodes.ILOAD:

					skipBytes = handleComplexIload();
					i = i + skipBytes;

					continue;
				case JvmOpCodes.ILOAD_0:

					handleSimpleILoad(0, info);

					continue;
				case JvmOpCodes.ILOAD_1:

					handleSimpleILoad(1, info);

					continue;
				case JvmOpCodes.ILOAD_2:

					handleSimpleILoad(2, info);

					continue;
				case JvmOpCodes.ILOAD_3:

					handleSimpleILoad(3, info);

					continue;
				case JvmOpCodes.IMUL:

					execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
							JvmOpCodes.IMUL);

					continue;
				case JvmOpCodes.INEG:
					execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
							JvmOpCodes.INEG);
					continue;
				case JvmOpCodes.INSTANCEOF:

					execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
							JvmOpCodes.INSTANCEOF);
					i += 2;
					continue;
				case JvmOpCodes.INVOKEINTERFACE:

					skipBytes = execute(InstrConstants.METHOD_INSTR_TYPE, null,
							JvmOpCodes.INVOKEINTERFACE);
					i = i + skipBytes;
					continue;
				case JvmOpCodes.INVOKESPECIAL:

					// TODO:
					/***********************************************************
					 * 1>check when result has to be pushed and when just
					 * printed and when both 2>In case of method calls and b is
					 * not null check whether this has to be added or super (no
					 * other case possible ? ) 3> Need to handle new instruction
					 * properly
					 */
					skipBytes = execute(InstrConstants.METHOD_INSTR_TYPE, null,
							JvmOpCodes.INVOKESPECIAL);
					i = i + skipBytes;

					continue;

				case JvmOpCodes.INVOKESTATIC:
					skipBytes = execute(InstrConstants.METHOD_INSTR_TYPE, null,
							JvmOpCodes.INVOKESTATIC);
					i = i + skipBytes;

					continue;
				case JvmOpCodes.INVOKEVIRTUAL:
					skipBytes = execute(InstrConstants.METHOD_INSTR_TYPE, null,
							JvmOpCodes.INVOKEVIRTUAL);
					i = i + skipBytes;

					continue;
				case JvmOpCodes.IOR:
					execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
							JvmOpCodes.IOR);

					continue;
				case JvmOpCodes.IREM:

					execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
							JvmOpCodes.IREM);

					continue;
				case JvmOpCodes.IRETURN:
					handleIRETURN();
					// behaviour.getParentBehaviour().getOpStack().push(op);
					continue;
				case JvmOpCodes.ISHL:

					handleISHL(opStack);
					continue;
				case JvmOpCodes.ISHR:

					handleISHR(opStack);

					continue;
				case JvmOpCodes.ISTORE:

					skipBytes = handleISTORE(JvmOpCodes.ISTORE);
					i = i + skipBytes;
					continue;
				case JvmOpCodes.ISTORE_0:

					handleISTORE(JvmOpCodes.ISTORE_0);
					// Store the op.getOperandValue() in the local Variable;
					continue;
				case JvmOpCodes.ISTORE_1:

					handleISTORE(JvmOpCodes.ISTORE_1);
					continue;
				case JvmOpCodes.ISTORE_2:

					handleISTORE(JvmOpCodes.ISTORE_2);

					continue;
				case JvmOpCodes.ISTORE_3:

					handleISTORE(JvmOpCodes.ISTORE_3);
					continue;
				case JvmOpCodes.ISUB:
					execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
							JvmOpCodes.ISUB);
					continue;
				case JvmOpCodes.IUSHR:

					handleIUSHR();

					continue;
				case JvmOpCodes.IXOR:
					handleIXOR();

					continue;
					// LETTER J

				case JvmOpCodes.JSR_W:

					i = i + 4;

					continue;

				case JvmOpCodes.JSR:

					skipBytes = execute(InstrConstants.BRANCH_INSTR_TYPE, null,
							JvmOpCodes.JSR);
					i = i + skipBytes;
					continue;

					// Letter L
				case JvmOpCodes.L2D:

					handleL2D();

					continue;
				case JvmOpCodes.L2F:

					handleL2F();

					continue;
				case JvmOpCodes.L2I:
					execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
							JvmOpCodes.L2I);
					continue;
				case JvmOpCodes.LADD:
					execute(InstrConstants.LOAD_INSTR_TYPE,
							InstrConstants.LONG, JvmOpCodes.LADD);
					continue;
				case JvmOpCodes.LALOAD:

					execute(InstrConstants.LOAD_INSTR_TYPE,
							InstrConstants.LONG, JvmOpCodes.LALOAD);
					continue;
				case JvmOpCodes.LAND:

					execute(InstrConstants.LOAD_INSTR_TYPE,
							InstrConstants.LONG, JvmOpCodes.LAND);
					continue;
				case JvmOpCodes.LASTORE:

					handleLASTORE(opStack);
					continue;
				case JvmOpCodes.LCMP:
					handleLCMP(opStack, info);
					continue;

				case JvmOpCodes.LCONST_0:
					handleLCONST(opStack, "0");

					continue;
				case JvmOpCodes.LCONST_1:
					handleLCONST(opStack, "1");
					continue;
				case JvmOpCodes.LDC:
					handleLDC(opStack, info, i);
					i++;
					continue;
				case JvmOpCodes.LDC_W:

					handleLDCW(opStack, info, i);
					i += 2;
					continue;
				case JvmOpCodes.LDC2_W:
					execute(InstrConstants.LOAD_INSTR_TYPE,
							InstrConstants.LONG, JvmOpCodes.LDC2_W);
					continue;

				case JvmOpCodes.LDIV:
					handleLDIV(opStack);

					continue;
				case JvmOpCodes.LLOAD:
					skipBytes = handleComplexLLOAD(opStack, info,
							currentForIndex);
					i = i + skipBytes;
					continue;
				case JvmOpCodes.LLOAD_0:
					//
					handleSIMPLELLOAD(opStack, 0);

					continue;
				case JvmOpCodes.LLOAD_1:
					handleSIMPLELLOAD(opStack, 1);

					continue;
				case JvmOpCodes.LLOAD_2:
					handleSIMPLELLOAD(opStack, 2);

					continue;
				case JvmOpCodes.LLOAD_3:
					handleSIMPLELLOAD(opStack, 3);

					continue;
				case JvmOpCodes.LMUL:

					execute(InstrConstants.LOAD_INSTR_TYPE,
							InstrConstants.LONG, JvmOpCodes.LMUL);

					continue;
				case JvmOpCodes.LNEG:

					execute(InstrConstants.LOAD_INSTR_TYPE,
							InstrConstants.LONG, JvmOpCodes.LNEG);

					continue;
				case JvmOpCodes.LOOKUPSWITCH:
					skipBytes = execute(InstrConstants.BRANCH_INSTR_TYPE, null,
							JvmOpCodes.LOOKUPSWITCH);
					i = i + skipBytes;

					continue;
				case JvmOpCodes.LOR:

					execute(InstrConstants.LOAD_INSTR_TYPE,
							InstrConstants.LONG, JvmOpCodes.LOR);

					continue;
				case JvmOpCodes.LREM:

					execute(InstrConstants.LOAD_INSTR_TYPE,
							InstrConstants.LONG, JvmOpCodes.LREM);
					continue;
				case JvmOpCodes.LRETURN:
					execute(InstrConstants.BRANCH_INSTR_TYPE, null,
							JvmOpCodes.LRETURN);
					// behaviour.getParentBehaviour().getOpStack().push(op);
					continue;
				case JvmOpCodes.LSHL:

					execute(InstrConstants.LOAD_INSTR_TYPE,
							InstrConstants.LONG, JvmOpCodes.LSHL);

					continue;
				case JvmOpCodes.LSHR:

					handleLSHR(info, opStack);
					continue;
				case JvmOpCodes.LSTORE:

					handleComplexLSTORE();

					continue;
				case JvmOpCodes.LSTORE_0:

					handleSimpleLStoreCase(opStack, info, 0);

					continue;
				case JvmOpCodes.LSTORE_1:

					handleSimpleLStoreCase(opStack, info, 1);

					continue;
				case JvmOpCodes.LSTORE_2:

					handleSimpleLStoreCase(opStack, info, 2);

					continue;
				case JvmOpCodes.LSTORE_3:

					handleSimpleLStoreCase(opStack, info, 3);

					continue;
				case JvmOpCodes.LSUB:

					handleLSUB(info, opStack);
					continue;
				case JvmOpCodes.LUSHR:

					handleLUSHR(info, opStack);

					continue;
				case JvmOpCodes.LXOR:

					handleLXOR(info, opStack);

					continue;

					// Letter M
				case JvmOpCodes.MONITORENTER:

					execute(InstrConstants.BRANCH_INSTR_TYPE, null,
							JvmOpCodes.MONITORENTER);

					continue;
				case JvmOpCodes.MONITOREXIT:
					execute(InstrConstants.BRANCH_INSTR_TYPE, null,
							JvmOpCodes.MONITOREXIT);

					continue;
				case JvmOpCodes.MULTIANEWARRAY:
					skipBytes = execute(InstrConstants.LOAD_INSTR_TYPE,
							InstrConstants.OBJECT, JvmOpCodes.MULTIANEWARRAY);
					i = i + skipBytes;
					continue;
					// letter N
				case JvmOpCodes.NEW:

					handleNEW(info, opStack, i);
					i += 2;

					continue;
				case JvmOpCodes.NEWARRAY:

					handleNEWARRAYCase(info);
					i++;

					continue;
				case JvmOpCodes.NOP:
					execute(InstrConstants.UNCLASSIFIED_INSTR_TYPE, null,
							JvmOpCodes.NOP);
					continue;

					// letter p

				case JvmOpCodes.POP:
					execute(InstrConstants.UNCLASSIFIED_INSTR_TYPE, null,
							JvmOpCodes.POP);
					continue;
				case JvmOpCodes.POP2: // TODO: check this out
					execute(InstrConstants.UNCLASSIFIED_INSTR_TYPE, null,
							JvmOpCodes.POP2);
					continue;
				case JvmOpCodes.PUTFIELD: // put
					handlePUTFIELD(info, i, opStack);
					i += 2;
					continue;
				case JvmOpCodes.PUTSTATIC:
					handlePUTSTATIC(info, i, opStack);
					i += 2;
					continue;

					// Letter R
				case JvmOpCodes.RET:
					skipBytes = execute(InstrConstants.BRANCH_INSTR_TYPE, null,
							JvmOpCodes.RET);
					i = i + skipBytes;
					continue;
				case JvmOpCodes.RETURN:

					handleSimpleReturn();

					continue;

					// Letter S
				case JvmOpCodes.SALOAD:

					handleSALOAD();
					continue;
				case JvmOpCodes.SASTORE:
					handleSASTORE();
					continue;
				case JvmOpCodes.SIPUSH:

					handleSIPUSH(info);
					i += 2;

					continue;
				case JvmOpCodes.SWAP:
					handleSwapInst(opStack);
					continue;

					// Letter T
				case JvmOpCodes.TABLESWITCH: // TODO
					skipBytes = execute(InstrConstants.BRANCH_INSTR_TYPE, null,
							JvmOpCodes.TABLESWITCH);
					i = i + skipBytes;
					continue;

					// Letter W
				case JvmOpCodes.WIDE:
					byte nextEntry = info[++i];
					int constant;
					java.lang.String temp;
					Operand operand;
					int pos;
					if (nextEntry == JvmOpCodes.IINC) {
						int localVarPos = getOffset(info, i);
						i += 2;
						constant = getOffset(info, i);
						i += 2;

						local = getLocalVariable(localVarPos, "load", "int",
								false, currentForIndex);

						if (local != null) {
							temp = local.getVarName() + " += (" + constant
									+ ");";
							behaviour.appendToBuffer("\n"
									+ Util.formatDecompiledStatement(temp)
									+ "\n");
						}

					} else {
						// parsedString+="WIDE\n";
						switch (nextEntry) {
						case JvmOpCodes.ILOAD:

							handleComplexIload();
							i = i + 2;
							break;
						case JvmOpCodes.FLOAD:
							handleFLOAD(JvmOpCodes.FLOAD);
							i = i + 2;

							// parsedString+="FLOAD "+pos+"\n";
							break;
						case JvmOpCodes.ALOAD:
							handleAloadCase(info, i, opStack);
							i = i + 2;
							// parsedString+="ALOAD"+"\n";
							break;
						case JvmOpCodes.LLOAD:

							execute(InstrConstants.LOAD_INSTR_TYPE,
									InstrConstants.LONG, JvmOpCodes.LLOAD);
							i = i + 2;

							break;
						case JvmOpCodes.DLOAD:
							execute(InstrConstants.LOAD_INSTR_TYPE,
									InstrConstants.DOUBLE, JvmOpCodes.DLOAD);
							i = i + 2;

							break;
						case JvmOpCodes.ISTORE:

							execute(InstrConstants.STORE_INSTR_TYPE,
									InstrConstants.INT, JvmOpCodes.ISTORE);
							i = i + 2;
							break;
						case JvmOpCodes.FSTORE:
							i = i + 2;
							handleComplexFSTORE();
							break;
						case JvmOpCodes.ASTORE:
							execute(InstrConstants.STORE_INSTR_TYPE,
									InstrConstants.OBJECT, JvmOpCodes.ASTORE);
							i = i + 2;
							break;
						case JvmOpCodes.LSTORE:
							handleComplexLSTORE();
							i = i + 2;
							break;
						case JvmOpCodes.DSTORE:
							handleComplexDStore();
							i = i + 2;
							break;
						case JvmOpCodes.RET:
							i += 2;
							break;
						}

					}
				}

			}

			catch (Exception e) {
				AllExceptionHandler handler = new AllExceptionHandler(e);
				handler.setBehaviour(behaviour);
				handler.setCodePosition(currentForIndex);
				handler.reportException();
			}

		}

		behaviour.setLabels(GlobalVariableStore.getLABELS());
		behaviour
				.setVariablesAtFront(GlobalVariableStore.getVariablesatfront());
		closeDoWhile(behaviour.getCode().length);
		behaviour.filterCode(generatedIfTracker);

	}

	private int isLoopAfterTry(int loopend) {

		int after = 2;
		int loopstart = currentForIndex;
		List tries = behaviour.getAllTriesForMethod();
		for (int i = 0; i < tries.size(); i++) {
			TryBlock tryb = (TryBlock) tries.get(i);
			if (tryb.getStart() == loopstart) {
				if (tryb.getEnd() > loopend) {
					return 0;
				}
				if (tryb.getEnd() < loopend) {
					return 1;
				}
			}

		}
		return after;
	}

	private boolean isNextInstructionAnyInvoke(int nextinst, StringBuffer sb) {
		boolean b = isNextInstructionInvokeInterface(nextinst);
		if (b) {
			sb.append("interface");
			return b;
		}
		b = isNextInstructionInvokeSpecial(nextinst);
		if (b) {
			sb.append("special");
			return b;
		}
		b = isNextInstructionInvokeStatic(nextinst);
		if (b)
			return b;
		b = isNextInstructionInvokeVirtual(nextinst);
		if (b)
			return b;
		return false;
	}

	private boolean isNextInstructionInvokeInterface(int nextInst) {

		if (nextInst == JvmOpCodes.INVOKEINTERFACE)
			return true;
		else
			return false;
	}

	private boolean isNextInstructionInvokeSpecial(int nextInst) {

		if (nextInst == JvmOpCodes.INVOKESPECIAL)
			return true;
		else
			return false;
	}

	private boolean isNextInstructionInvokeStatic(int nextInst) {

		if (nextInst == JvmOpCodes.INVOKESTATIC)
			return true;
		else
			return false;
	}

	private int isNextInstructionConversionInst(int i, byte[] code) {
		if (isThisInstrStart(behaviour.getInstructionStartPositions(), i) == false)
			return -1;
		switch (code[i]) {

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
			return i;
		default:
			return -1;

		}

	}

	private boolean isNextInstructionInvokeVirtual(int nextInst) {

		if (nextInst == JvmOpCodes.INVOKEVIRTUAL)
			return true;
		else
			return false;
	}

	private boolean isNextInstructionConversionInst(int next) {
		boolean flag = false;
		switch (next) {

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

	private java.lang.String getSourceTypeForConversionInst(int i, byte[] code) {
		if (isThisInstrStart(behaviour.getInstructionStartPositions(), i) == false)
			return "";
		switch (code[i]) {

		case JvmOpCodes.D2L:
			return "double";
		case JvmOpCodes.D2I:
			return "double";
		case JvmOpCodes.D2F:
			return "double";
		case JvmOpCodes.I2B:
			return "int";
		case JvmOpCodes.I2C:
			return "int";

		case JvmOpCodes.I2D:
			return "int";

		case JvmOpCodes.I2F:
			return "int";

		case JvmOpCodes.I2L:
			return "int";

		case JvmOpCodes.I2S:
			return "int";

		case JvmOpCodes.L2D:
			return "long";

		case JvmOpCodes.L2F:
			return "long";

		case JvmOpCodes.L2I:
			return "long";

		case JvmOpCodes.F2D:
			return "float";

		case JvmOpCodes.F2I:
			return "float";

		case JvmOpCodes.F2L:
			return "float";
		default:
			return "";

		}

	}

	private java.lang.String getResulatantTypeForConversionInst(int i,
			byte[] code) {
		if (isThisInstrStart(behaviour.getInstructionStartPositions(), i) == false)
			return "";
		switch (code[i]) {

		case JvmOpCodes.D2L:
			return "long";
		case JvmOpCodes.D2I:
			return "int";
		case JvmOpCodes.D2F:
			return "float";
		case JvmOpCodes.I2B:
			return "byte";
		case JvmOpCodes.I2C:
			return "char";

		case JvmOpCodes.I2D:
			return "double";

		case JvmOpCodes.I2F:
			return "float";

		case JvmOpCodes.I2L:
			return "long";

		case JvmOpCodes.I2S:
			return "short";

		case JvmOpCodes.L2D:
			return "double";

		case JvmOpCodes.L2F:
			return "float";

		case JvmOpCodes.L2I:
			return "int";

		case JvmOpCodes.F2D:
			return "double";

		case JvmOpCodes.F2I:
			return "int";

		case JvmOpCodes.F2L:
			return "long";
		default:
			return "";

		}

	}

	private boolean isNextInstructionStore(int nextInstruction) {
		boolean flag = false;

		switch (nextInstruction) {
		/*
		 * case JvmOpCodes.AASTORE : flag = true; break;
		 */
		case JvmOpCodes.ASTORE:
			flag = true;
			break;

		case JvmOpCodes.ASTORE_0:
			flag = true;
			break;
		case JvmOpCodes.ASTORE_1:
			flag = true;
			break;
		case JvmOpCodes.ASTORE_2:
			flag = true;
			break;
		case JvmOpCodes.ASTORE_3:
			flag = true;
			break;

		case JvmOpCodes.DSTORE:
			flag = true;
			break;
		case JvmOpCodes.DSTORE_0:
			flag = true;
			break;
		case JvmOpCodes.DSTORE_1:
			flag = true;
			break;
		case JvmOpCodes.DSTORE_2:
			flag = true;
			break;
		case JvmOpCodes.DSTORE_3:
			flag = true;
			break;

		case JvmOpCodes.FSTORE:
			flag = true;
			break;
		case JvmOpCodes.FSTORE_0:
			flag = true;
			break;
		case JvmOpCodes.FSTORE_1:
			flag = true;
			break;
		case JvmOpCodes.FSTORE_2:
			flag = true;
			break;
		case JvmOpCodes.FSTORE_3:
			flag = true;
			break;

		case JvmOpCodes.ISTORE:
			flag = true;
			break;
		case JvmOpCodes.ISTORE_0:
			flag = true;
			break;
		case JvmOpCodes.ISTORE_1:
			flag = true;
			break;
		case JvmOpCodes.ISTORE_2:
			flag = true;
			break;
		case JvmOpCodes.ISTORE_3:
			flag = true;
			break;

		case JvmOpCodes.LSTORE:
			flag = true;
			break;

		case JvmOpCodes.LSTORE_0:
			flag = true;
			break;
		case JvmOpCodes.LSTORE_1:
			flag = true;
			break;
		case JvmOpCodes.LSTORE_2:
			flag = true;
			break;
		case JvmOpCodes.LSTORE_3:
			flag = true;
			break;

		default:
			flag = false;
		}
		return flag;
	}

	public boolean isNextInstructionReturn(int nextInstruction) {
		boolean flag = false;
		switch (nextInstruction) {
		case JvmOpCodes.RETURN:
			return true;
		default:
			return false;

		}
	}

	public boolean isNextInstructionPop(int nextInstruction) {
		boolean flag = false;
		switch (nextInstruction) {
		case JvmOpCodes.POP:
		case JvmOpCodes.POP2:
			return true;
		default:
			return false;

		}
	}

	public boolean isNextInstructionIf(int nextInstruction) {
		boolean flag = false;

		switch (nextInstruction) {
		case JvmOpCodes.IF_ACMPEQ:
			flag = true;
			break;
		case JvmOpCodes.IF_ACMPNE:
			flag = true;
			break;
		case JvmOpCodes.IF_ICMPEQ:
			flag = true;
			break;
		case JvmOpCodes.IF_ICMPGE:
			flag = true;
			break;
		case JvmOpCodes.IF_ICMPGT:
			flag = true;
			break;
		case JvmOpCodes.IF_ICMPLE:
			flag = true;
			break;
		case JvmOpCodes.IF_ICMPLT:
			flag = true;
			break;
		case JvmOpCodes.IF_ICMPNE:
			flag = true;
			break;
		case JvmOpCodes.IFEQ:
			flag = true;
			break;
		case JvmOpCodes.IFGE:
			flag = true;
			break;
		case JvmOpCodes.IFGT:
			flag = true;
			break;
		case JvmOpCodes.IFLE:
			flag = true;
			break;
		case JvmOpCodes.IFLT:
			flag = true;
			break;
		case JvmOpCodes.IFNE:
			flag = true;
			break;
		case JvmOpCodes.IFNONNULL:
			flag = true;
			break;
		case JvmOpCodes.IFNULL:
			flag = true;
			break;
		default:
			flag = false;
		}
		return flag;
	}

	// TODO: Write logic to poll for the end of the last catch
	private java.lang.String pollExcepionTables(int i, int instruction) {
		java.lang.String temp = "";
		// Algo
		// For Each ExceptionTable Do
		// 1> check whether = to start of Guard
		// If so Check Whether guardtype is try or catch
		// 2> Else check whether = to end of Guard
		// If so Just return a "}"
		// 3> Else check whether = to start of Handler
		// If so check whether type is catch or finally
		// Return appropriately
		// 4> Else check whether = to end of Handler
		// just Return "}"
		// Curszor
		// 5> Check for invalid Table values
		ArrayList alltables = this.behaviour.getExceptionTableList();
		byte code[] = behaviour.getCode();
		if (alltables != null) {
			for (int c = 0; c < alltables.size(); c++) {
				ExceptionTable table = (ExceptionTable) alltables.get(c);
				// System.out.println(i+"table.getEndOfHandlerForGuardRegion()"+table.getEndOfHandlerForGuardRegion());

				// Check for Invalid condition

				if(table.isIgnore())continue;
				
				if (code != null
						&& code[table.getStartOfGuardRegion() + 1] == JvmOpCodes.MONITOREXIT)
					continue;

				if (code != null
						&& code[table.getEndOfGuardRegion() - 1] == JvmOpCodes.MONITOREXIT) {

					continue;
				}

				if (table.getStartOfGuardRegion() == table
						.getStartOfHandlerForGuardRegion())
					continue;
				// Another invalid to check
				if (table.getEndOfGuardRegion() > table
						.getStartOfHandlerForGuardRegion())
					continue;

				// Check 1
				if (table.getStartOfGuardRegion() == i) {
					if (table.getTypeOfGuardRegion().equals("try")) {
						ArrayList Al = new ArrayList();
						temp += "try\n{\n";
						Al.add(table);
						Iterator IT = behaviour.getExceptionTableList()
								.iterator();
						while (IT.hasNext()) {
							ExceptionTable etb = (ExceptionTable) IT.next();
							if (etb.getStartOfGuardRegion() == i
									&& etb.getTypeOfGuardRegion().equals("try")) {
								boolean Present = alreadyPresent(Al, etb);
								if (Present == false) {
									temp += "\ntry\n{\n";
									Al.add(etb);
								}

							}
						}

						// Check from created Exception Table List.
						ArrayList createdList = behaviour.getCreatedTableList();
						if (createdList != null) {
							IT = createdList.iterator();
							while (IT.hasNext()) {
								ExceptionTable etb = (ExceptionTable) IT.next();
								if (etb.getStartOfGuardRegion() == i
										&& etb.getTypeOfGuardRegion().equals(
												"try")) {
									boolean Present = alreadyPresent(Al, etb);
									if (Present == false) {
										temp += "\ntry\n{\n";
										Al.add(etb);
									}

								}
							}
						}

						break;
					}
					if (table.getTypeOfGuardRegion().equals("catch")) {
						// Need to Get Exception Name
						java.lang.String expName = "";
						for (int c2 = 0; c2 < alltables.size(); c2++) {
							ExceptionTable temptable = (ExceptionTable) alltables
									.get(c2);
							if (temptable.getEndOfGuardRegion() == i) {
								expName = temptable.getExceptionName();
								expName.replace('/', '.');
								LocalVariableStructure structure = this.behaviour
										.getLocalVariables();
								int indexPos = -1;
								boolean simple = true;
								switch (instruction) {
								case JvmOpCodes.ASTORE:
									indexPos = this.behaviour.getCode()[i + 1];
									simple = false;
									break;
								case JvmOpCodes.ASTORE_0:
									indexPos = 0;
									break;
								case JvmOpCodes.ASTORE_1:
									indexPos = 1;
									break;
								case JvmOpCodes.ASTORE_2:
									indexPos = 2;
									break;
								case JvmOpCodes.ASTORE_3:
									indexPos = 3;
									break;
								// TODO: Later check for wide instruction with
								// astore.
								// Need to Handle it.

								}

								LocalVariable expVar = getLocalVariable(
										indexPos, "store", expName.replace('/',
												'.'), simple, i); //
								java.lang.String expVarName = "";
								if (expVar != null)
									expVarName = expVar.getVarName();
								if (expVar == null) {
									// TODO handle Later
									expVarName = "exception";
								}
								StringBuffer g = new StringBuffer("");
								checkForImport(expName.replace('/', '.'), g);
								temp += "\ncatch(" + g.toString() + " "
										+ expVarName + ")\n{\n";
								break;
							}
						}
						temp += "";
						break;

					}
				}
				if (table.getEndOfGuardRegion() == i) {
					// Apply Rule For Try
					// Purpose of this rule is to check whether
					// i is actually the end for this try or not
					int startOfGuard = table.getStartOfGuardRegion();
					boolean alreadyEnded = false;
					if (table.getTypeOfGuardRegion().equals("try")) {
						ArrayList excepTables = this.behaviour
								.getExceptionTableList();
						;
						for (int Start = 0; Start < excepTables.size(); Start++) {
							ExceptionTable tab = (ExceptionTable) excepTables
									.get(Start);
							if (tab.getStartOfGuardRegion() == startOfGuard) {
								int endOfGuard = tab.getEndOfGuardRegion();
								int beginofHandler = tab
										.getStartOfHandlerForGuardRegion();
								if (i > endOfGuard && i < beginofHandler) {
									alreadyEnded = true;
									break;
								}
							}
						}
					}
					boolean addGE = addGuardEnd(i);
					boolean b1 = isHandlerEndPresentAtGuardEnd(i);
					if (alreadyEnded == false
							&& (table.getTypeOfGuardRegion().equals("") == false)
							&& addGE && !b1) {
						temp += "}";
						guardEnds.put(new Integer(i), "true");
					} else
						temp += "";
					// Check for overlap
					ArrayList excepTables = this.behaviour
							.getExceptionTableList();
					;
					for (int Start = 0; Start < excepTables.size(); Start++) {
						ExceptionTable tab = (ExceptionTable) excepTables
								.get(Start);
						if (tab.getStartOfHandlerForGuardRegion() == i
								&& tab.getExceptionName().equals("<any>") == false) {
							// Added code for generating excepion Name
							LocalVariableStructure structure = this.behaviour
									.getLocalVariables();
							int indexPos = -1;
							boolean simple = true;
							switch (instruction) {
							case JvmOpCodes.ASTORE:
								indexPos = this.behaviour.getCode()[i + 1];
								simple = false;
								break;
							case JvmOpCodes.ASTORE_0:
								indexPos = 0;
								break;
							case JvmOpCodes.ASTORE_1:
								indexPos = 1;
								break;
							case JvmOpCodes.ASTORE_2:
								indexPos = 2;
								break;
							case JvmOpCodes.ASTORE_3:
								indexPos = 3;
								break;
							// TODO: Later check for wide instruction with
							// astore.
							// Need to Handle it.

							}

							LocalVariable expVar = getLocalVariable(indexPos,
									"store", tab.getExceptionName().replace(
											'/', '.'), simple, i);
							java.lang.String expName = "";
							if (expVar != null)
								expName = expVar.getVarName();
							if (expVar == null) {
								// TODO
								expName = "exception";
							}
							StringBuffer g = new StringBuffer("");
							checkForImport(tab.getExceptionName().replace('/',
									'.'), g);
							temp += "\ncatch(" + g.toString() + "  " + expName
									+ ")\n{\n";
							break;
						}
						if (tab.getStartOfHandlerForGuardRegion() == i
								&& tab.getExceptionName().equals("<any>") == true) {
							temp += "\nfinally\n{\n";
							break;
						}
					}

					// Check from newly created Table list
					ArrayList newexcepTables = this.behaviour
							.getCreatedTableList();
					if (newexcepTables != null) {
						for (int Start = 0; Start < newexcepTables.size(); Start++) {
							ExceptionTable tab = (ExceptionTable) newexcepTables
									.get(Start);
							boolean addG = addGuardEnd(i);
							boolean z = isHandlerEndPresentAtGuardEnd(i);
							if (tab.getEndOfGuardRegion() == i && addG && !z) {
								temp += "}\n";
								guardEnds.put(new Integer(i), "true");
								break;
							}
						}
					}

					// System.out.println();
					break;
				}

				if (table.getStartOfHandlerForGuardRegion() == i) {

					/*
					 * Iterator
					 * itz=behaviour.getExceptionTableList().iterator();
					 * while(itz.hasNext()) { ExceptionTable
					 * etb=(ExceptionTable)itz.next();
					 * if(etb.getEndOfHandlerForGuardRegion()==i) { //
					 * temp+="\n}// end of handler2\n"; } }
					 */

					if (table.getTypeOfHandlerForGuardRegion().equals(
							"FinallyBlock")) {
						temp += "finally\n{\n";
						break;
					}
					if (table.getTypeOfHandlerForGuardRegion().equals(
							"CatchBlock")) {
						LocalVariableStructure structure = this.behaviour
								.getLocalVariables();
						int indexPos = -1;
						int blockIndexValue = -1;
						boolean simple = true;
						switch (instruction) {
						case JvmOpCodes.ASTORE:
							indexPos = this.behaviour.getCode()[i + 1];
							blockIndexValue = i + 2;
							simple = false;
							break;
						case JvmOpCodes.ASTORE_0:
							indexPos = 0;
							blockIndexValue = i + 1;
							break;
						case JvmOpCodes.ASTORE_1:
							blockIndexValue = i + 1;
							indexPos = 1;
							break;
						case JvmOpCodes.ASTORE_2:
							blockIndexValue = i + 1;
							indexPos = 2;
							break;
						case JvmOpCodes.ASTORE_3:
							blockIndexValue = i + 1;
							indexPos = 3;
							break;
						// TODO: Later check for wide instruction with astore.
						// Need to Handle it.

						}

						boolean add = addHandlerStart(i);
						if (add) {
							StringBuffer g = new StringBuffer("");
							checkForImport(table.getExceptionName().replace(
									'/', '.'), g);
							String tp = null;
							try {
								tp = table.getExceptionName().replace('/', '.');
							} catch (Exception e) {
								tp = "java.lang.Object";
							}
							LocalVariable expVar = getLocalVariable(indexPos,
									"store", tp, simple, i);
							java.lang.String expName = "";
							if (expVar != null)
								expName = expVar.getVarName();
							else
								expName = "exception";
							temp += "catch(" + g.toString() + "  " + expName
									+ ")\n{\n";
							handlerStarts.put(new Integer(i), "true");
						}
						break;

					}
				}
				if (table.getEndOfHandlerForGuardRegion() == i) {

					boolean b = addhandlerEnd(i, table);
					if (b) {
						addedHandlerEnds.add(new Integer(i));
						handlerEndLocationsAdded.add(new Integer(i));

						if (code[i] == JvmOpCodes.ATHROW) {
							if (opStack.size() > 0) {

								boolean emptyFinally = checkForEmptyFinally(
										code, i, table);
								if (!emptyFinally) {
									String s = opStack.pop().toString();
									if (s != null && !s.equals("this")) {
										IFinder basicFinder = FinderFactory
												.getFinder(IFinder.BASE);
										int prev = basicFinder
												.getPrevStartOfInst(i);
										boolean appendAthrow = true;
										if (prev > 0) {
											IFinder loadFinder = FinderFactory
													.getFinder(IFinder.LOAD);

											int aloadpos = loadFinder
													.isInstAload(prev,
															new StringBuffer());
											if (aloadpos == prev) {
												boolean exists = LocalVariableHelper
														.checkForVariableExistenceWithName(s);
												if (!exists) {
													appendAthrow = false;
												}

											}
										}
										if (appendAthrow)
											temp += "throw " + s + ";\n";
									}

								} else {
									opStack.pop();
									// temp+="/*Empty Handler Block*/\n";
								}
								GlobalVariableStore.getAthrowmap().put(
										new Integer(currentForIndex), "true");
							}

						}
						if (table.getHandlerBlockName().indexOf("Catch") != -1) {
							boolean emptyCatch = checkForEmptyCatch(table
									.getStartOfHandlerForGuardRegion(), table
									.getEndOfHandlerForGuardRegion());
							if (emptyCatch) {
								// temp+="/*Empty Handler Block*/\n";
							}
						}
						temp += "\n}\n";
						int s = table.getStartOfHandlerForGuardRegion();
						int e = table.getEndOfHandlerForGuardRegion();
						java.lang.String t = table
								.getTypeOfHandlerForGuardRegion();
						handlertracker.put(new Integer(i),
								new handlerEndTracker(s, e, t));
						// guardEnds.put(new Integer(i),"true");
					}
					Iterator tempIterator = null;

					// Check for End of Guard
					// TODO: Get the example for This...
					tempIterator = behaviour.getExceptionTableList().iterator();
					while (tempIterator.hasNext()) {
						ExceptionTable etb = (ExceptionTable) tempIterator
								.next();
						boolean addG = addGuardEnd(i);
						boolean z = isHandlerEndPresentAtGuardEnd(i, null);
						if (etb.getEndOfGuardRegion() == i && addG && !z) {
							temp += "\n}";
							guardEnds.put(new Integer(i), "true");
							break;
						}
					}

					// Check for overlap with Created Table List
					ArrayList newList = behaviour.getCreatedTableList();
					if (newList != null) {
						tempIterator = newList.iterator();
						while (tempIterator.hasNext()) {
							ExceptionTable etb = (ExceptionTable) tempIterator
									.next();
							boolean addG = addGuardEnd(i);
							boolean z = isHandlerEndPresentAtGuardEnd(i, null);
							if (etb.getEndOfGuardRegion() == i && addG && !z) {
								temp += "}\n";
								guardEnds.put(new Integer(i), "true");
								break;
							}
						}
					}

					// Check here for overlap condition
					tempIterator = behaviour.getExceptionTableList().iterator();
					while (tempIterator.hasNext()) {
						ExceptionTable etb = (ExceptionTable) tempIterator
								.next();
						if (etb.getStartOfHandlerForGuardRegion() == i
								&& etb.getExceptionName().equals("<any>") == false) {
							java.lang.String ename = etb.getExceptionName();
							ename = ename.replace('/', '.');
							LocalVariableStructure structure = this.behaviour
									.getLocalVariables();
							int indexPos = -1;
							int blockIndexValue = -1;
							boolean simple = true;
							switch (instruction) {
							case JvmOpCodes.ASTORE:
								indexPos = this.behaviour.getCode()[i + 1];
								blockIndexValue = i + 2;
								simple = false;
								break;
							case JvmOpCodes.ASTORE_0:
								blockIndexValue = i + 1;
								indexPos = 0;
								break;
							case JvmOpCodes.ASTORE_1:
								blockIndexValue = i + 1;
								indexPos = 1;
								break;
							case JvmOpCodes.ASTORE_2:
								blockIndexValue = i + 1;
								indexPos = 2;
								break;
							case JvmOpCodes.ASTORE_3:
								blockIndexValue = i + 1;
								indexPos = 3;
								break;
							// TODO: Later check for wide instruction with
							// astore.
							// Need to Handle it.

							}

							LocalVariable expVar = getLocalVariable(indexPos,
									"store", ename.replace('/', '.'), simple, i);
							java.lang.String expName = "";
							if (expVar != null)
								expName = expVar.getVarName();
							if (expVar == null) {
								// TODO handle Later
								expName = "exception";
							}
							boolean add = addHandlerStart(i);
							if (add) {
								StringBuffer g = new StringBuffer("");
								checkForImport(ename.replace('/', '.'), g);
								temp += "catch(" + g.toString() + " " + expName
										+ ")\n{\n";
								handlerStarts.put(new Integer(i), "true");
							}
							break;
						}
					}

					// break;
				}

			}

		}
		// Check For End of Catch For Last Catch
		// Note Handled Here because This there is no entry in exception table
		// list for The end of last catch
		// as there is no finally for try

		// if(temp.length()==0) // Removing if because this was preventing last
		// catch's end of guard of to be printed
		// As that info was only held in a CatchBlock Object
		// {
		ArrayList allTryBlks = getAllTriesForBehaviour(); // All try blocks
		// for method
		if (allTryBlks != null) {
			Iterator it = allTryBlks.iterator();
			while (it.hasNext()) {
				TryBlock tryblock = (TryBlock) it.next(); // Get Each Try
				if (tryblock.hasFinallyBlk() == false) // Go in only if there
				// is no finally for
				// this try
				{
					ArrayList allcatches = tryblock.getAllCatchesForThisTry(); // Get
					// catches
					// for
					// this
					// try
					if (allcatches.size() > 0) // Try may not have any catches
					{
						CatchBlock LastCatch = (CatchBlock) allcatches
								.get(allcatches.size() - 1);
						int endOfCatch = LastCatch.getEnd(); // Get end of
						// last catch
						// block for
						// this try
						if (endOfCatch == i
								&& LastCatch
										.isUsedForDeterminingTheEndOfLastCatch()) // compare
						// with
						// i
						{

							if (code[i] == JvmOpCodes.ATHROW) {
								if (opStack.size() > 0) {
									String s = opStack.pop().toString();
									if (!s.equals("this")) {
										IFinder basicFinder = FinderFactory
												.getFinder(IFinder.BASE);
										int prev = basicFinder
												.getPrevStartOfInst(i);
										boolean appendAthrow = true;
										if (prev > 0) {
											IFinder loadFinder = FinderFactory
													.getFinder(IFinder.LOAD);

											int aloadpos = loadFinder
													.isInstAload(prev,
															new StringBuffer());
											if (aloadpos == prev) {
												boolean exists = LocalVariableHelper
														.checkForVariableExistenceWithName(s);
												if (!exists) {
													appendAthrow = false;
												}

											}
										}
										if (appendAthrow)
											temp += "throw " + s + ";\n";
									}
									GlobalVariableStore.getAthrowmap().put(
											new Integer(currentForIndex),
											"true");
								}

							}
							temp += "}\n"; // end Catch and break
							break;
						}
					}
				}

			}

		}

		return temp;

	}

	private ArrayList getAllTriesForBehaviour() {
		ArrayList alltries = behaviour.getAllTriesForMethod();
		int size = alltries.size();
		if (size == 0)
			return null;
		else
			return alltries;
	}

	private boolean skipCurrentIteraton(int i, boolean includeEndOfGuard,
			byte[] info) {
		boolean skip = false;
		boolean exit = false;
		ArrayList tries = this.behaviour.getAllTriesForMethod();
		Iterator triesIT = tries.iterator();
		while (triesIT.hasNext()) {
			TryBlock TRY = (TryBlock) triesIT.next();
			ArrayList catches = TRY.getAllCatchesForThisTry();
			Iterator catchesIT = catches.iterator();
			TryCatchFinally prevBlock = TRY;
			while (catchesIT.hasNext()) {
				CatchBlock Catch = (CatchBlock) catchesIT.next();
				TryCatchFinally curBlock = Catch;
				if (includeEndOfGuard) {
					if (i >= prevBlock.getEnd() && i < curBlock.getStart()) {
						skip = true;
						boolean retStmt = checkForReturn(info, i);
						if (retStmt == true)
							skip = false;
						exit = true;
						break;
					}
				}
				if (!includeEndOfGuard && i > prevBlock.getEnd()
						&& i < curBlock.getStart()) {
					skip = true;
					boolean retStmt = checkForReturn(info, i);
					if (retStmt == true)
						skip = false;
					exit = true;
					break;
				}
				prevBlock = curBlock;
			}
			if (exit)
				break;
			// Check for Finally Block
			FinallyBlock Finally = TRY.getFinallyBlock();
			if (Finally != null) {
				CatchBlock catchblock = TRY.getLastCatchBlock();
				if (catchblock != null) {
					if (!includeEndOfGuard && i > catchblock.getEnd()
							&& i < Finally.getStart()) {
						boolean retStmt = checkForReturn(info, i);
						if (retStmt == true) {
							skip = false;
						} else {
							skip = true;
						}
					}
					if (includeEndOfGuard && i >= catchblock.getEnd()
							&& i < Finally.getStart()) {
						boolean retStmt = checkForReturn(info, i);
						if (retStmt == true) {
							skip = false;
						} else {
							skip = true;
						}
					}
				} else {
					if (!includeEndOfGuard && i > TRY.getEnd()
							&& i < Finally.getStart()) {

						boolean retStmt = checkForReturn(info, i);
						if (retStmt == true) {
							skip = false;
						} else {
							skip = true;
						}
					}
					if (includeEndOfGuard && i >= TRY.getEnd()
							&& i < Finally.getStart()) {
						boolean retStmt = checkForReturn(info, i);
						if (retStmt == true) {
							skip = false;
						} else {
							skip = true;
						}
					}
				}

			}
		}

		/*
		 * if(!skip && behaviour.getSynchronizedEntries()!=null &&
		 * behaviour.getSynchronizedEntries().size() > 0) { ArrayList
		 * synchEntries=behaviour.getSynchronizedEntries(); for(int s=0;s<synchEntries.size();s++) {
		 * ExceptionTable synchTab=(ExceptionTable)synchEntries.get(s); int
		 * endPC=synchTab.getEndPC(); int
		 * athrowpos=getNextAthrowPos(endPC,info); if(athrowpos!=-1) { if(i
		 * >=endPC && i<= athrowpos) { skip=true; break; } } } }
		 */
		boolean skipDueToSynch = false;
		if (synchSkips != null && synchSkips.size() > 0) {

			Iterator it = synchSkips.entrySet().iterator();
			while (it.hasNext()) {

				Map.Entry e = (Map.Entry) it.next();
				Integer st = (Integer) e.getKey();
				Integer en = (Integer) e.getValue();
				if (i >= st.intValue() && i <= en.intValue()) {
					skip = true;
					skipDueToSynch = true;
					break;
				}

			}

		}

		if (skip == false) // Buggy
		{

			ArrayList tableList = Util
					.getAllTablesWithFinallyAsHandler(behaviour
							.getExceptionTableList());
			ArrayList subsetList = getFinallyCount(tableList);
			if (subsetList == null)
				return skip;
			ArrayList finList[] = new ArrayList[subsetList.size()];
			for (int x = 0; x < subsetList.size(); x++) {
				finList[x] = Util.getAllTablesWithFinallyAsHandler(behaviour
						.getExceptionTableList(), ((Integer) subsetList.get(x))
						.intValue());
				tableList = Util.getTablesSortedByGuardStart(finList[x]);
				if (tableList != null) {
					for (int z = 0; z < tableList.size(); z++) {

						ExceptionTable table1 = (ExceptionTable) tableList
								.get(z);

						if (info != null
								&& info[table1.getStartOfGuardRegion() + 1] == JvmOpCodes.MONITOREXIT)
							continue;

						if (info != null
								&& info[table1.getEndOfGuardRegion() - 1] == JvmOpCodes.MONITOREXIT) {

							continue;
						}

						if (table1.getStartOfGuardRegion() == table1
								.getStartOfHandlerForGuardRegion())
							continue;
						// Another invalid to check
						if (table1.getEndOfGuardRegion() > table1
								.getStartOfHandlerForGuardRegion())
							continue;

						int next = z + 1;
						if (next < tableList.size()) {
							ExceptionTable table2 = (ExceptionTable) tableList
									.get(next);
							int epc = table1.getEndPC();
							int spc = table2.getStartPC();
							if (i >= epc && i < spc)// &&
							// table2.getStartOfGuardRegion()
							// < table2.getS )
							{
								boolean sometrystart = isThisTryStart(i);
								ArrayList skipWithinSkip = checkForskipWithinSkip(
										epc, spc, info);
								if (!sometrystart
										&& skipWithinSkip.contains(new Integer(
												i)) == false) {

									boolean endoftrycatch = checkForEndOfTryCatch(i);

									skip = true;
									break;
								}
							}

						} else
							break;
					}
				}
			}

		}

		// test case NativeExtensionInstaller.class in plugin.jar(j2sdk)

		if (skip) {

			ArrayList alist = behaviour.getExceptionTableList();
			if (alist != null && alist.size() > 0) {
				for (int j = 0; j < alist.size(); j++) {
					ExceptionTable xt = (ExceptionTable) alist.get(j);
					boolean invalid = false;
					if (xt.getStartPC() == xt.getStartOfHandler()) {
						invalid = true;
					}
					if (xt.getStartPC() > xt.getStartOfHandler()) {
						invalid = true;
					}
					if (xt.getStartPC() == i && !invalid) {
						skip = false;
						break;
					}

				}

			}

		}
		if (skip && skipDueToSynch) {
			String synchEntry = pollsynchblocks(currentForIndex);
			if (synchEntry != null && synchEntry.trim().length() > 0) {
				behaviour.appendToBuffer(Util
						.formatDecompiledStatement(synchEntry));
			}

		}

		return skip;

	}

	private boolean alreadyPresent(ArrayList AL, ExceptionTable etb) {
		boolean present = false;
		for (int jj = 0; jj < AL.size(); jj++) {
			ExceptionTable t = (ExceptionTable) AL.get(jj);
			int start = t.getStartOfGuardRegion();
			int end = t.getEndOfGuardRegion();
			if (start == etb.getStartOfGuardRegion()
					&& end == etb.getEndOfGuardRegion()) {
				present = true;
				break;
			} else
				present = false;
		}
		if (present == false) {

			for (int jj = 0; jj < AL.size(); jj++) {
				ExceptionTable t = (ExceptionTable) AL.get(jj);
				int start = t.getStartOfGuardRegion();
				int endPC = t.getEndPC();
				int handlerPC = t.getStartOfHandler();
				if (start == etb.getStartPC()) {
					int etbendPC = etb.getEndPC();
					if (etbendPC > endPC && etbendPC < handlerPC) {
						// present=true; // NOTE: commented on 16 oct by belurs
						// As it was preventing a try from appearing

						// TODO: Continuous thoruough testing of
						// try/catch/finally
						break; // Necessary
					}

				}
			}

		}
		if (!present) {
			if (synchSkips != null && synchSkips.size() > 0) {

				Iterator it = synchSkips.entrySet().iterator();
				while (it.hasNext()) {

					Map.Entry e = (Map.Entry) it.next();
					Integer st = (Integer) e.getKey();
					Integer en = (Integer) e.getValue();
					if (etb.getStartOfHandlerForGuardRegion() >= st.intValue()
							&& etb.getStartOfHandlerForGuardRegion() <= en
									.intValue()) {
						present = true;
						break;
					}

				}

			}
		}

		return present;

	}

	boolean casefound = false;

	private java.lang.String pollSwitchBlksForMethod(int i, byte[] info) {

		casefound = false;
		java.lang.String stmt = "";
		boolean processed = false;
		ArrayList allswitches = behaviour.getAllSwitchBlks();
		ArrayList starts = behaviour.getInstructionStartPositions();
		if (allswitches != null) {

			for (int start = 0; start < allswitches.size(); start++) {
				Switch switchblk = (Switch) allswitches.get(start);
				ArrayList allcases = switchblk.getAllCases();
				allcases = sortCasesByStart(allcases);
				if (allcases.size() > 0) {
					Case lc = switchblk.getLastBlockForSwitch();
					for (int c = 0; c < allcases.size(); c++) {

						Case caseblk = (Case) allcases.get(c);
						int defEnd = switchblk.getDefaultEnd();
						if (defEnd == i) { // Commented && lc == null
							if (switchblk.isDefaultStarted()) {
								if (switchblk.defaultToBeDisplayed()
										&& !switchblk.isDefaultEnded()) {
									if (lc == null) {
										int howmany = checkForEmbeddedSwitches(
												allswitches, switchblk
														.getDefaultStart(),
												defEnd, switchblk, null);
										StringBuffer closebrackets = new StringBuffer(
												"");
										for (int t = 1; t <= howmany; t++) {
											closebrackets.append("\n}\n}\n");
										}
										stmt += Util
												.formatDecompiledStatement(closebrackets
														.toString());
									}
									StringBuffer S = new StringBuffer("");
									addAnyReurnAtDefaultEnd(i, opStack, info, S);
									stmt += Util.formatDecompiledStatement(S
											.toString()
											+ "\n");
									S = new StringBuffer("");
									addAnyAThorwAtDefaultEnd(i, opStack, info,
											S);
									stmt += Util.formatDecompiledStatement(S
											.toString()
											+ "\n");
									if (isThisInstrStart(starts,
											currentForIndex - 3)
											&& info[currentForIndex - 3] == JvmOpCodes.GOTO) {
										stmt += Util
												.formatDecompiledStatement("break;\n");
									}
									stmt += Util.formatDecompiledStatement("}");
									switchblk.setDefaultEnded(true);
									if (switchblk.getEndOfSwitch() == i
											&& !switchblk.hasBeenClosed()) {
										stmt += Util
												.formatDecompiledStatement("}");
										switchblk.setSwitchClosed(true);
									}

								}
							}

						}

						java.lang.String temp = "";
						if (caseblk.getCaseStart() == i) {
							if (caseblk.isFallsThru()) {
								// Util.forceBeginStartSpace=false;
								stmt += "\n   ";
								// stmt+=Util.formatDecompiledStatement("// Case
								// Falls Through\n");
								Util.forceBeginStartSpace = true;
							}

							StringBuffer total = new StringBuffer("");
							boolean multiple = checkForMultipleCases(switchblk,
									allcases, i, total);
							if (multiple) {
								temp = getMultipleCaseStartAsString(switchblk,
										allcases, i, total);
								if (temp != null && temp.length() > 0) {
									casefound = true;
								}
							} else {
								if (!caseblk.isWasCaseStartedPrinted()) {
									temp = "case " + caseblk.getCaseLabel()
											+ ":\n{\n";
									caseblk.setWasCaseStartedPrinted(true);
								}
								casefound = true;
							}
							processed = true;
							if (!temp.startsWith("\n")) {
								Util.forceTrimLines = false;
								temp = "\n" + temp;
							}
							stmt += Util.formatDecompiledStatement(temp);
							Util.forceTrimLines = true;
							temp = "";
							return stmt;

						}
						if (caseblk.getCaseEnd() == i
								&& !caseblk.isDoNotClose()) {

							/*
							 * if(caseblk.isGotoAsEndForCase()==false) { //TODO
							 * detect break at case end }
							 */

							if (caseblk.isFallsThru() == false
									&& caseblk.isGotoAsEndForCase())
								temp += "break;\n";
							temp = Util.formatDecompiledStatement(temp);
							int howmany = 0;
							if (lc != null)
								howmany = checkForEmbeddedSwitches(allswitches,
										caseblk.getCaseEnd(), caseblk
												.getCaseStart(), switchblk, lc);
							StringBuffer closebrackets = new StringBuffer("");
							for (int t = 1; t <= howmany; t++) {
								closebrackets.append("\n}\n}\n");
							}
							temp += Util
									.formatDecompiledStatement(closebrackets
											.toString());

							// //Case End
							int caselabelpos = switchblk
									.getPositionForLabelInCaseGroupWithSameStarts(caseblk
											.getCaseLabel());
							java.lang.String tempString = "";
							if (caselabelpos > 1) {
								tempString = "\n";
							} else {
								tempString = "\n}\n";
							}
							caseblk.setClosed(true);
							temp += Util.formatDecompiledStatement(tempString);
							ArrayList allcasesDUP = switchblk.getAllCases();
							tempString = "";
							stmt += temp;
							temp = "";
							if (allcasesDUP.size() > 0) {
								for (int c2 = 0; c2 < allcasesDUP.size(); c2++) {
									Case caseblkDUP = (Case) allcasesDUP
											.get(c2);
									if (caseblkDUP.getCaseStart() == i) {
										if (caseblkDUP.isFallsThru()) {
											// tempString+=Util.formatDecompiledStatement("//
											// Case Falls Through\n");
										}
										StringBuffer total = new StringBuffer(
												"");
										boolean multiple = checkForMultipleCases(
												switchblk, allcases, i, total);
										if (multiple) {
											java.lang.String ms = getMultipleCaseStartAsString(
													switchblk, allcases, i,
													total);
											if (ms != null && ms.length() > 0) {
												tempString += ms;
												casefound = true;
											}
										} else {
											if (!caseblkDUP
													.isWasCaseStartedPrinted()) {
												tempString += "case "
														+ caseblkDUP
																.getCaseLabel()
														+ ": \n{\n";
												caseblkDUP
														.setWasCaseStartedPrinted(true);
											}
											casefound = true;
										}

										temp += Util
												.formatDecompiledStatement(tempString);
										stmt += temp;
										break;
									}

								}
							}

							temp = "";
							tempString = "";
							// Now check with default:
							if (i == switchblk.getDefaultStart()
									&& switchblk.defaultToBeDisplayed()
									&& i != switchblk.getDefaultEnd()
									&& !switchblk.isDefaultStarted()) {
								boolean invalidDef = BranchHelper
										.checkForInvalidDefault(switchblk
												.getDefaultStart(), switchblk
												.getStartOfSwitch());
								if (!invalidDef) {
									tempString = "default:\n{\n";
									temp += Util
											.formatDecompiledStatement(tempString);
									switchblk.setDefaultStarted(true);
								} else {
									switchblk.setDisplayDefault(false);
									switchblk.setDefaultStarted(false);
								}
							}
							if (i == switchblk.getDefaultStart()
									&& !switchblk.defaultToBeDisplayed()) {
								// Case end
								tempString = "\n}\n";
								temp += Util
										.formatDecompiledStatement(tempString);
							}

							// poll for switch end
							if (lc != null) {
								if (i == lc.getCaseEnd()) {
									// //switch end
									if (!switchblk.hasBeenClosed()) {
										tempString = "\n}\n";
										temp += Util
												.formatDecompiledStatement(tempString);
										switchblk.setSwitchClosed(true);
									}

								}

							}
							stmt += temp;
							temp = "";
							tempString = "";
							// return stmt;
						}
					}
					// check default

				}
				int defStart = switchblk.getDefaultStart();
				if (defStart == i && switchblk.defaultToBeDisplayed()
						&& !switchblk.isDefaultStarted()) {
					boolean invalidDef = BranchHelper.checkForInvalidDefault(
							switchblk.getDefaultStart(), switchblk
									.getStartOfSwitch());
					if (!invalidDef) {
						stmt += Util.formatDecompiledStatement("default:\n{\n");
						// stmt=Util.formatDecompiledStatement(stmt);
						switchblk.setDefaultStarted(true);
					} else {
						switchblk.setDefaultStarted(false);
						switchblk.setDisplayDefault(false);
					}
					// return stmt;
				}
				int defEnd = switchblk.getDefaultEnd();
				if (defEnd == i) {
					if (switchblk.isDefaultStarted()) {
						if (switchblk.defaultToBeDisplayed()
								&& !switchblk.isDefaultEnded()) {
							Case lc = switchblk.getLastBlockForSwitch();
							if (lc == null) {
								int howmany = checkForEmbeddedSwitches(
										allswitches, switchblk
												.getDefaultStart(), defEnd,
										switchblk, null);
								StringBuffer closebrackets = new StringBuffer(
										"");
								for (int t = 1; t <= howmany; t++) {
									closebrackets.append("\n}\n}\n");
								}
								stmt += Util
										.formatDecompiledStatement(closebrackets
												.toString());
							}
							StringBuffer S = new StringBuffer("");
							addAnyReurnAtDefaultEnd(i, opStack, info, S);
							stmt += Util.formatDecompiledStatement(S.toString()
									+ "\n");
							S = new StringBuffer("");
							addAnyAThorwAtDefaultEnd(i, opStack, info, S);
							stmt += Util.formatDecompiledStatement(S.toString()
									+ "\n");
							if (isThisInstrStart(starts, currentForIndex - 3)
									&& info[currentForIndex - 3] == JvmOpCodes.GOTO) {
								stmt += Util
										.formatDecompiledStatement("break;\n");
							}
							if (!switchblk.isDoNotCloseDefault()) // otherwise
								// already
								// closed
								stmt += Util.formatDecompiledStatement("}\n");// Default
							// end
							if (!switchblk.hasBeenClosed())
								stmt += Util.formatDecompiledStatement("\n}\n");
							switchblk.setDefaultEnded(true);
							switchblk.setSwitchClosed(true);
						} else {
							// Switch end
							if (!switchblk.hasBeenClosed()) {
								stmt += Util.formatDecompiledStatement("}\n");
								switchblk.setSwitchClosed(true);
							}
						}

						switchblk.setDefaultEnded(true);
					}
					// return stmt;
				}

			}

		}
		return stmt;
	}

	public int checkForEmbeddedSwitches(ArrayList switches, int end, int start,
			Switch current, Case lastblk) {

		int counter = 0;
		for (int z = 0; z < switches.size(); z++) {
			Switch switchblk = (Switch) switches.get(z);
			Case currentLC = switchblk.getLastBlockForSwitch();
			int currentEnd = -1;
			if (currentLC != null) {
				currentEnd = currentLC.getCaseEnd();
			} else {
				currentEnd = switchblk.getDefaultEnd();
			}
			if (switchblk != current && currentEnd == end
					&& switchblk.getStartOfSwitch() > start) {
				if (!switchblk.hasBeenClosed()) {
					counter++;

					switchblk.setSwitchClosed(true);
					if (currentLC != null) {
						currentLC.setClosed(true);
					} else {
						switchblk.setDefaultEnded(true);
					}

				}
			}
		}
		return counter;
	}

	private boolean isPrevInstructionAload(int pos, byte[] code, StringBuffer sb) {
		boolean present = false;
		int pos1 = pos - 1;
		int pos2 = pos - 2;
		int jvmInst_1 = -1;
		int jvmInst_2 = -1;
		if (pos1 >= 0)
			jvmInst_1 = code[pos1];
		if (pos2 >= 0)
			jvmInst_2 = code[pos2];
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
		if (present)
			sb.append(pos1);
		if (present == false) {

			if (jvmInst_2 == JvmOpCodes.ALOAD) {
				present = true;
				sb.append(pos2);
			}

		}

		return present;
	}

	private boolean checkForReturn(byte[] code, int i) {
		boolean present = false;
		int jvmInst = code[i];
		boolean b = isThisInstrStart(behaviour.getInstructionStartPositions(),
				i);
		if (b == false)
			return false;
		switch (jvmInst) {
		case JvmOpCodes.ARETURN:
		case JvmOpCodes.IRETURN:
		case JvmOpCodes.FRETURN:
		case JvmOpCodes.DRETURN:
		case JvmOpCodes.LRETURN:
		case JvmOpCodes.RETURN:
			present = true;
			break;
		default:
			present = false;
			break;
		}
		return present;
	}

	private boolean isIEndOfGuard(int i, Behaviour behaviour) {
		boolean end = false;

		ArrayList alltries = behaviour.getAllTriesForMethod();
		Iterator it = alltries.iterator();
		while (it.hasNext()) {

			TryBlock Try = (TryBlock) it.next();
			if (Try != null) {
				int endoftry = Try.getEnd();
				if (endoftry == i) // check for try's end
				{
					end = true;
					break;
				} else // Check for catches
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

	private java.lang.String isAnyReturnPresentInSkipRegion(byte[] info, int i,
			Behaviour behaviour, StringBuffer stb) {
		boolean exit = false;
		java.lang.String ret = "";
		ArrayList tries = this.behaviour.getAllTriesForMethod();
		Iterator triesIT = tries.iterator();
		while (triesIT.hasNext()) {
			TryBlock TRY = (TryBlock) triesIT.next();
			ArrayList catches = TRY.getAllCatchesForThisTry();
			Iterator catchesIT = catches.iterator();
			TryCatchFinally prevBlock = TRY;
			while (catchesIT.hasNext()) {
				CatchBlock Catch = (CatchBlock) catchesIT.next();
				TryCatchFinally curBlock = Catch;

				if (i >= prevBlock.getEnd() && i < curBlock.getStart()) {

					java.lang.String retStmt = getReturnInstinRange(info, i,
							curBlock.getStart(), stb);
					ret = retStmt;
					exit = true;
					break;
				}
				prevBlock = curBlock;
			}
			if (exit)
				break;
			// Check for Finally Block
			if (!exit) {
				FinallyBlock Finally = TRY.getFinallyBlock();
				if (Finally != null) {
					CatchBlock catchblock = TRY.getLastCatchBlock();
					if (catchblock != null) {

						if (i >= catchblock.getEnd() && i < Finally.getStart()) {
							java.lang.String retStmt = getReturnInstinRange(
									info, i, Finally.getStart(), stb);
							ret = retStmt;
							break;
						}
					} else {
						if (i >= TRY.getEnd() && i < Finally.getStart()) {
							java.lang.String retStmt = getReturnInstinRange(
									info, i, Finally.getStart(), stb);
							ret = retStmt;
							break;
						}
					}

				}
			}

			if (ret.length() == 0) {

				if (i == TRY.getEnd()) {
					java.lang.String retStmt = getReturnInstinRange(info, i,
							i + 1, stb);
					ret = retStmt;
					break;
				}

			}
		}

		return ret;
	}

	private java.lang.String getReturnInstinRange(byte[] info, int i,
			int blockstart, StringBuffer sb) {

		for (int s = i; s < blockstart; s++) {
			int jvmInst = info[s];
			switch (jvmInst) {
			case JvmOpCodes.IRETURN:
				sb.append(s);
				return "ireturn";
			case JvmOpCodes.LRETURN:
				sb.append(s);
				return "lreturn";
			case JvmOpCodes.FRETURN:
				sb.append(s);
				return "freturn";
			case JvmOpCodes.DRETURN:
				sb.append(s);
				return "dreturn";
			case JvmOpCodes.ARETURN:
				sb.append(s);
				return "areturn";
			case JvmOpCodes.RETURN:
				sb.append(s);
				return "return";
			default:
				continue;

			}

		}
		sb.append("-1");
		return null;

	}

	private int getReturnStringPosInCode(byte[] info, int i, Behaviour behaviour) {
		int pos = -1;
		boolean exit = false;

		ArrayList tries = this.behaviour.getAllTriesForMethod();
		Iterator triesIT = tries.iterator();
		while (triesIT.hasNext()) {
			TryBlock TRY = (TryBlock) triesIT.next();
			ArrayList catches = TRY.getAllCatchesForThisTry();
			Iterator catchesIT = catches.iterator();
			TryCatchFinally prevBlock = TRY;
			while (catchesIT.hasNext()) {
				CatchBlock Catch = (CatchBlock) catchesIT.next();
				TryCatchFinally curBlock = Catch;

				if (i >= prevBlock.getEnd() && i < curBlock.getStart()) {

					pos = getReturnInstPosInRange(info, i, curBlock.getStart());
					exit = true;
					break;
				}
				prevBlock = curBlock;
			}
			if (exit)
				break;
			// Check for Finally Block
			if (!exit) {
				FinallyBlock Finally = TRY.getFinallyBlock();
				if (Finally != null) {
					CatchBlock catchblock = TRY.getLastCatchBlock();
					if (catchblock != null) {

						if (i >= catchblock.getEnd() && i < Finally.getStart()) {

							pos = getReturnInstPosInRange(info, i, Finally
									.getStart());
							break;
						}
					} else {
						if (i >= TRY.getEnd() && i < Finally.getStart()) {
							pos = getReturnInstPosInRange(info, i, Finally
									.getStart());

							break;
						}
					}

				}
			}
			if (i == TRY.getEnd()) {
				pos = getReturnInstPosInRange(info, i, i + 1);

				break;
			}
		} // Shutdown d;
		return pos;
	}

	private int getReturnInstPosInRange(byte[] info, int i, int blockstart) {

		for (int s = i; s < blockstart; s++) {
			int jvmInst = info[s];
			switch (jvmInst) {
			case JvmOpCodes.IRETURN:
			case JvmOpCodes.LRETURN:
			case JvmOpCodes.FRETURN:
			case JvmOpCodes.DRETURN:
			case JvmOpCodes.ARETURN:
			case JvmOpCodes.RETURN:
				return s;
			default:
				continue;

			}

		}

		return -1;

	}

	private int findCodeIndexFromInfiniteLoop(IFBlock ifst,
			ArrayList LoopTable, int codeIndex) {
		Iterator iterInfLoop = LoopTable.iterator();
		int start = ifst.getIfStart();
		int loopstarts[] = new int[LoopTable.size()];
		int j = 0;
		boolean ok = false;
		while (iterInfLoop.hasNext()) {
			Loop iloop = (Loop) iterInfLoop.next();
			int lstart = iloop.getStartIndex();
			loopstarts[j] = lstart;
			j++;
			ok = true;
			/*
			 * if(iloop.getStartIndex() == codeIndex) { return
			 * iloop.getEndIndex(); }
			 */
		}
		if (ok) {
			for (int z = loopstarts.length - 1; z >= 0; z--) {

				if (loopstarts[z] < start) {
					int end = getloopEndForStart(LoopTable, loopstarts[z]);
					if (end < start)
						return -1;
					return end;
				}

			}
		}

		return -1;
	}

	private boolean isIndexEndOfLoop(ArrayList list, int s) {
		boolean ok = false;
		for (int st = 0; st < list.size(); st++) {
			if (((Loop) list.get(st)).getEndIndex() == s)
				return true;
		}
		return ok;
	}

	private int getLoopStartForEnd(int s, ArrayList list) {
		for (int k = 0; k < list.size(); k++) {
			Loop l = (Loop) list.get(k);
			if (l.getEndIndex() == s)
				return l.getStartIndex();
		}
		return -1;
	}

	private Collection curentIFS;

	private Collection getCurrentIFStructues() {
		if (behaviour.getMethodIfs() != null
				&& behaviour.getMethodIfs().size() > 0) {
			curentIFS = behaviour.getMethodIfs();
			return curentIFS;
		} else
			return null;
	}

	private Object[] sortIFStructures() {
		if (curentIFS != null && curentIFS.size() > 0) {
			Object o[] = curentIFS.toArray();
			Arrays.sort(o);
			return o;
		} else
			return null;
	}

	private IFBlock getParentBlock(Object o[], int startOfIf) {

		IFBlock parent = null;
		int reqdPos = -1;
		for (int s = 0; s < o.length; s++) {
			if (o[s] instanceof IFBlock) {
				IFBlock IF = (IFBlock) o[s];
				if (IF.getIfStart() == startOfIf) {
					recheck: while (true) {
						if (s > 0) {
							reqdPos = s - 1;
							IFBlock pif = (IFBlock) o[reqdPos];
							boolean n = isIFShortcutORComp(behaviour.getCode(),
									pif.getIfStart());
							if (n) {
								int first = sanalyser.getFirstIfInChain(pif
										.getIfStart());
								// Find the position in the array
								for (int p = o.length - 1; p >= 0; p--) {
									if (((IFBlock) o[p]).getIfStart() == first) {
										s = p;
										continue recheck;
									}
								}
								return null;
							}
							return pif;
						} else {
							return null;
						}
					}
				}

			} else {
				return null;
			}
		}
		return null;
	}

	// Primarily use it for goto and some special cases where appplicable
	private int getJumpAddress(byte[] info, int counter) {

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

	private int getOffset(byte[] info, int counter) {

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

	private int checkIfElseCloseNumber(int end, IFBlock ifs) {
		int ifend = -1;
		this.getCurrentIFStructues();
		Object ifsSorted[] = sortIFStructures();
		IFBlock parent = getParentBlock(ifsSorted, ifs.getIfStart());
		if (parent == null) {

			int temp = checkLoopsAndSwitchForIfEnd(end, ifs, behaviour);
			if (temp != -1)
				ifend = temp;
			else
				ifend = end;
		} else {
			ifend = reEvaluateIFStart(ifs, ifsSorted, parent, end);
			int temp2 = checkLoopsAndSwitchForIfEnd(ifend, ifs, behaviour);
			if (temp2 < ifend && temp2 != -1) {
				ifend = temp2;
			}
		}
		return ifend;
	}

	private int reEvaluateIFStart(IFBlock ifs, Object[] ifsSorted,
			IFBlock parent, int currentEnd) {
		int ifend = -1;
		int parentEnd = parent.getIfCloseLineNumber();
		int thisStart = ifs.getIfStart();
		if (thisStart < parentEnd) {
			if (currentEnd > parentEnd) {
				// belurs:
				// Test case BigInteger.class
				/***************************************************************
				 * BasicallY this part of the code is not bug free. It assumes
				 * that if the parentIF is ending before this if end is ending
				 * then this ifend is wrong. Well, it worked for every case
				 * until BigInteger ws decompiled. So basically resetting parent
				 * if end to this if end if parent if end was not a GOTO
				 * instruction.
				 */
				// Test for The above case
				// found while testing XmlReader.class
				// Above Fix Affects XmlReader.class
				// SO test for bytecode ends for all ifs
				// if all are same dont go into IF
				boolean noif = false;
				if (ifs.getIfCloseFromByteCode() == parent
						.getIfCloseFromByteCode()) {
					noif = true;
				}

				if (noif == false
						&& (behaviour.getCode()[parentEnd] != JvmOpCodes.GOTO)
						&& (behaviour.getCode()[parentEnd] != JvmOpCodes.GOTO_W)) {
					ifend = currentEnd;
					parent.setIfCloseLineNumber(ifend);

				} else {

					ifend = parentEnd;
				}

			} else if (currentEnd < thisStart) {
				ifend = parentEnd;
			} else
				ifend = currentEnd;

		} // Need to handle thisif end inside parent's else end
		/*
		 * else if(thisStart > parentEnd) { boolean
		 * doesParentHaveElse=parent.isHasElse(); if(doesParentHaveElse) { int
		 * parentElseEnd=parent.getElseCloseLineNumber(); if(thisStart <
		 * parentElseEnd) { // Within parent else block if(currentEnd >
		 * parentElseEnd) { ifend=parentElseEnd; } else if(currentEnd <
		 * thisStart) { ifend=parentElseEnd; } else { ifend=currentEnd; }
		 * 
		 * BigInteger b; } } }
		 */
		else {

			IFBlock superparent = getParentBlock(ifsSorted, parent.getIfStart());
			if (superparent == null) {
				int temp = checkLoopsAndSwitchForIfEnd(currentEnd, ifs,
						behaviour);
				if (temp != -1)
					ifend = temp;
				else
					ifend = currentEnd;

			} else {
				int tmp = reEvaluateIFStart(ifs, ifsSorted, superparent,
						currentEnd);
				ifend = tmp;

			}

		}

		return ifend;

	}

	private Object[] sortLoops(ArrayList list) {
		Object o[] = list.toArray();
		Arrays.sort(o);
		return o;
	}

	private int getParentLoopStartForIf(Object[] sortedloops, int ifbegin) {
		int reqdStart = -1;
		int max = -1;
		int pos = 0;
		int counter = sortedloops.length - 1;
		while (counter >= 0) {
			Object o = sortedloops[counter];
			if (o instanceof Loop) {
				Loop l = (Loop) o;
				int ls = l.getStartIndex();
				int thisLoopEnd = getloopEndForStart(behaviour
						.getBehaviourLoops(), ls);
				if (ls < ifbegin && !(ifbegin > thisLoopEnd)) {
					reqdStart = ls;
					break;
				}

			}
			counter--;
		}
		return reqdStart;

	}

	private Loop getParentLoopForIf(Object[] sortedloops, int ifbegin) {
		int reqdStart = -1;
		Loop reqdl = null;
		int max = -1;
		int pos = 0;
		int counter = sortedloops.length - 1;
		while (counter >= 0) {
			Object o = sortedloops[counter];
			if (o instanceof Loop) {
				Loop l = (Loop) o;
				int ls = l.getStartIndex();
				int thisLoopEnd = getloopEndForStart(behaviour
						.getBehaviourLoops(), ls);
				if (ls < ifbegin && !(ifbegin > thisLoopEnd)) {
					reqdStart = ls;
					reqdl = l;
					break;
				}

			}
			counter--;
		}
		return reqdl;

	}

	private java.lang.String getBranchTypeAtI(int i, IFBlock ifst,
			StringBuffer sb) {
		boolean skip = skipBranchCheck(i);
		if (skip)
			return "";
		Iterator it = GlobalVariableStore.getBranchLabels().entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry) it.next();
			int end = ((Integer) (e.getValue())).intValue();
			if (end == i) {
				DecompilerHelper.BranchLabel b = (DecompilerHelper.BranchLabel) e
						.getKey();
				IFBlock IF = b.getIF();
				if (IF == ifst) {

					if (b.getLBL().trim().length() > 0) {
						GlobalVariableStore.getLabelAssociated().put(
								new Integer(i), "true");
					}
					int prev = i - 1;
					if (isThisInstrStart(behaviour
							.getInstructionStartPositions(), prev)) {
						if (behaviour.getCode()[prev] == JvmOpCodes.RETURN) {
							return "";
						}
					}

					sb.append(b.getBrlbl());
					return b.getLBL();
				}
			}

		}

		return "";
	}

	private boolean skipBranchCheck(int i) {
		boolean b = false;
		Iterator it = GlobalVariableStore.getLabelAssociated().entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry) it.next();
			java.lang.String skip = ((java.lang.String) (e.getValue()));
			int end = ((Integer) e.getKey()).intValue();
			if (i == end && skip.equals("true"))
				b = true;
		}
		return b;
	}

	private boolean isNewEndValid(int newend, IFBlock ifs,
			java.lang.String type, int curend) {

		int end;
		if (type.equals("else"))
			end = ifs.getElseCloseLineNumber();
		else
			end = curend;
		if (newend == -1)
			return false;
		if (newend > end) {
			return false;
		}

		else
			return true;
	}

	private int resetEndofIFElseWRTSwitch(ArrayList switches, IFBlock anIf,
			int curend, int curStart, java.lang.String type) {
		int k = -1;
		ArrayList possibleCaseEnds = new ArrayList();
		ArrayList possibleCaseStarts = new ArrayList();
		for (int s = 0; s < switches.size(); s++) {

			Switch swblk = (Switch) switches.get(s);
			ArrayList cases = swblk.getAllCases();
			for (int c = 0; c < cases.size(); c++) {
				Case caseblk = (Case) cases.get(c);
				int end = caseblk.getCaseEnd();
				int ifelseStart = curStart;
				int ifelseEnd;
				if (type.equals("else"))
					ifelseEnd = anIf.getElseCloseLineNumber();
				else {
					ifelseEnd = curend;
					if (ifelseEnd == -1) {
						ifelseEnd = anIf.getIfCloseFromByteCode();
					}
				}
				if (end > ifelseStart && end < ifelseEnd) {
					possibleCaseEnds.add(new Integer(end));
					possibleCaseStarts.add(new Integer(caseblk.getCaseStart()));
				}

			}

		}
		int firststart = -1;
		if (possibleCaseEnds.size() > 0) {
			Integer inte[] = (Integer[]) possibleCaseEnds
					.toArray(new Integer[possibleCaseEnds.size()]);
			Arrays.sort(inte);
			k = inte[0].intValue();

			Integer ints[] = (Integer[]) possibleCaseStarts
					.toArray(new Integer[possibleCaseStarts.size()]);
			Arrays.sort(ints);
			firststart = ints[0].intValue();

		}

		if (firststart != -1 && firststart > anIf.getIfStart()) {
			k = -1;
		}

		return k;
	}

	private int getloopEndForStart(ArrayList list, int start) {
		for (int i = 0; i < list.size(); i++) {
			Loop l = (Loop) list.get(i);
			if (l.getStartIndex() == start) {
				return l.getEndIndex();
			}
		}
		return -1;

	}

	private ArrayList sortCasesByStart(ArrayList allcases) {
		ArrayList sorted = new ArrayList();
		int s[] = new int[allcases.size()];
		for (int i = 0; i < allcases.size(); i++) {
			s[i] = ((Case) allcases.get(i)).getCaseStart();
		}
		Arrays.sort(s);
		for (int j = 0; j < s.length; j++) {
			int c = s[j];
			for (int k = 0; k < allcases.size(); k++) {
				Case current = (Case) allcases.get(k);
				boolean present = caseObjectAddedToSortedArray(current, sorted);
				if (!present) {
					if (current.getCaseStart() == c) {
						sorted.add(current);
						break;
					}
				}
			}
		}
		return sorted;
	}

	private int checkLoopsAndSwitchForIfEnd(int end, IFBlock ifs,
			Behaviour behaviour) {
		byte[] info = behaviour.getCode();
		int reqdEnd = -1;
		ArrayList loops = behaviour.getBehaviourLoops();
		if (loops != null && loops.size() > 0) {

			Object[] sortedLoops = sortLoops(loops);
			int parentLoopStart = getParentLoopStartForIf(sortedLoops, ifs
					.getIfStart());
			int loopend = getloopEndForStart(loops, parentLoopStart);
			if (ifs.getIfStart() < loopend
					&& (end > loopend || end < ifs.getIfStart()))
				ifs.setIfCloseLineNumber(loopend);
			reqdEnd = ifs.getIfCloseLineNumber();

		}

		ArrayList allswicthes = behaviour.getAllSwitchBlks();
		if (allswicthes != null && allswicthes.size() > 0) {

			int newifend = -1;
			newifend = resetEndofIFElseWRTSwitch(allswicthes, ifs, reqdEnd,
					currentForIndex, "if");
			boolean valid = isNewEndValid(newifend, ifs, "if", end);
			if (valid) {
				ifs.setIfCloseLineNumber(newifend);
				reqdEnd = ifs.getIfCloseLineNumber();
				// Need to check here for a goto before the case end
				int start = ifs.getIfStart();
				int bye = ifs.getIfCloseFromByteCode();
				ArrayList starts = behaviour.getInstructionStartPositions();
				for (int z = reqdEnd; z > start; z--) {

					int inst = info[z];
					boolean isSt = isThisInstrStart(starts, z);
					if (isSt && (inst == JvmOpCodes.GOTO)
							&& (getJumpAddress(info, z) == bye)) {
						reqdEnd = z;
						break;
					}

				}

			} else {
				if (ifs.getElseCloseLineNumber() == -1) {
					ifs.setIfCloseLineNumber(ifs.getIfCloseFromByteCode());
					reqdEnd = ifs.getIfCloseLineNumber();// here
				} else {
					reqdEnd = ifs.getIfCloseLineNumber();
				}
			}

		}
		return reqdEnd;

	}

	private IFBlock getParentIFforLoop(Loop cur, Object ifsorted[]) {
		IFBlock par = null;
		for (int n = (ifsorted.length - 1); n >= 0; n--) {
			if (!(ifsorted[n] instanceof IFBlock))
				continue;
			IFBlock IF = (IFBlock) ifsorted[n];
			int s = IF.getIfStart();
			if (s < cur.getStartIndex()) {
				par = IF;
				break;
			}
		}
		return par;
	}

	private int checkEndofLoopWRTIF(IFBlock parentif, Object[] ifsorted,
			Loop cur) {
		int end = -1;
		int parentIFStart = parentif.getIfStart();
		int parentIFEnd = parentif.getIfCloseLineNumber();
		boolean doesParentHaveElse = parentif.isHasElse();
		boolean needToFindSuperParent = false;

		if (cur.getStartIndex() < parentIFEnd) // Yes it lies
		{
			if (cur.getEndIndex() > parentIFEnd) {
				end = parentIFEnd;
				return end;
			} else {
				end = cur.getEndIndex(); // Passes else was correct
				return end;
			}

		}

		if (cur.getStartIndex() > parentIFEnd) {
			if (doesParentHaveElse) {
				int parentElseEnd = parentif.getElseCloseLineNumber();
				if (cur.getStartIndex() < parentElseEnd) // Lies within the
				// else of parent IF
				{
					if (cur.getEndIndex() > parentElseEnd) {
						end = parentElseEnd;
						return end;
					} else {
						end = cur.getEndIndex();
						return end;
					}
				} else {
					needToFindSuperParent = true;
				}

			} else {
				needToFindSuperParent = true;
			}

		}
		if (needToFindSuperParent) {
			IFBlock superParent = getParentBlock(ifsorted, parentIFStart);
			int tmp;
			if (superParent != null)
				tmp = checkEndofLoopWRTIF(superParent, ifsorted, cur);
			else {
				tmp = cur.getEndIndex();
			}
			return tmp;
		} else {
			return cur.getEndIndex(); // Should Never come here
		}

	}

	private boolean isThisLoopEndAlso(ArrayList loops, int i, int ifstart) {
		for (int s = 0; s < loops.size(); s++) {
			Loop l = (Loop) loops.get(s);
			int lend = (l).getEndIndex();
			if (lend == i && ifstart > l.getStartIndex())
				return true;
		}
		return false;
	}

	private boolean doesthisClashWithCaseBegin(ArrayList switches, int i) {
		if (switches == null || switches.size() == 0)
			return false;
		boolean ret = false;
		for (int s = 0; s < switches.size(); s++) {

			Switch swblk = (Switch) switches.get(s);
			ArrayList cases = swblk.getAllCases();
			for (int k = 0; k < cases.size(); k++) {
				Case c = (Case) cases.get(k);
				if (c.getCaseStart() == i) {
					ret = true;
					break;
				}
			}

		}
		return ret;
	}

	private boolean isCurrentInstStore(int inst) {
		boolean flag;
		switch (inst) {

		case JvmOpCodes.ASTORE:
			flag = true;
			break;

		case JvmOpCodes.ASTORE_0:
			flag = true;
			break;
		case JvmOpCodes.ASTORE_1:
			flag = true;
			break;
		case JvmOpCodes.ASTORE_2:
			flag = true;
			break;
		case JvmOpCodes.ASTORE_3:
			flag = true;
			break;
		case JvmOpCodes.DSTORE:
			flag = true;
			break;
		case JvmOpCodes.DSTORE_0:
			flag = true;
			break;
		case JvmOpCodes.DSTORE_1:
			flag = true;
			break;
		case JvmOpCodes.DSTORE_2:
			flag = true;
			break;
		case JvmOpCodes.DSTORE_3:
			flag = true;
			break;

		case JvmOpCodes.FSTORE:
			flag = true;
			break;
		case JvmOpCodes.FSTORE_0:
			flag = true;
			break;
		case JvmOpCodes.FSTORE_1:
			flag = true;
			break;
		case JvmOpCodes.FSTORE_2:
			flag = true;
			break;
		case JvmOpCodes.FSTORE_3:
			flag = true;
			break;

		case JvmOpCodes.ISTORE:
			flag = true;
			break;
		case JvmOpCodes.ISTORE_0:
			flag = true;
			break;
		case JvmOpCodes.ISTORE_1:
			flag = true;
			break;
		case JvmOpCodes.ISTORE_2:
			flag = true;
			break;
		case JvmOpCodes.ISTORE_3:
			flag = true;
			break;
		case JvmOpCodes.LSTORE:
			flag = true;
			break;

		case JvmOpCodes.LSTORE_0:
			flag = true;
			break;
		case JvmOpCodes.LSTORE_1:
			flag = true;
			break;
		case JvmOpCodes.LSTORE_2:
			flag = true;
			break;
		case JvmOpCodes.LSTORE_3:
			flag = true;
			break;

		default:
			flag = false;
		}
		return flag;
	}

	/***************************************************************************
	 * NOTE: This is not general purpose method tofind load index inst...Skips
	 * ceratian loads see usages
	 * 
	 * @param inst
	 * @param info
	 * @param s
	 * @return
	 */
	public int getLoadInstIndex(int inst, byte info[], int s) {
		// chkIndex is the index of the goto instruction.

		switch (inst) {

		case JvmOpCodes.ALOAD:
			return info[++s];

		case JvmOpCodes.ALOAD_0:
			return 0;

		case JvmOpCodes.ALOAD_1:
			return 1;

		case JvmOpCodes.ALOAD_2:
			return 2;

		case JvmOpCodes.ALOAD_3:
			return 3;

		case JvmOpCodes.DLOAD:
			return info[++s];

		case JvmOpCodes.DLOAD_0:
			return 0;

		case JvmOpCodes.DLOAD_1:
			return 1;

		case JvmOpCodes.DLOAD_2:
			return 2;

		case JvmOpCodes.DLOAD_3:
			return 3;

		case JvmOpCodes.FLOAD:
			return info[++s];

		case JvmOpCodes.FLOAD_0:
			return 0;

		case JvmOpCodes.FLOAD_1:
			return 1;

		case JvmOpCodes.FLOAD_2:
			return 2;

		case JvmOpCodes.FLOAD_3:
			return 3;

		case JvmOpCodes.ILOAD:
			return info[++s];

		case JvmOpCodes.ILOAD_0:
			return 0;

		case JvmOpCodes.ILOAD_1:
			return 1;

		case JvmOpCodes.ILOAD_2:
			return 2;

		case JvmOpCodes.ILOAD_3:
			return 3;

		case JvmOpCodes.LLOAD:
			return info[++s];

		case JvmOpCodes.LLOAD_0:
			return 0;
		case JvmOpCodes.LLOAD_1:
			return 1;
		case JvmOpCodes.LLOAD_2:
			return 2;
		case JvmOpCodes.LLOAD_3:
			return 3;

		}

		return -1;
	}

	private java.lang.String getAnyLabelAtI(Map labels, int i) {
		java.lang.String lbl = (java.lang.String) labels.get(new Integer(i));
		return lbl;
	}

	/***************************************************************************
	 * NOTE: This is not general purpose method tofind load inst...Skips
	 * ceratian loads see usages
	 */
	public boolean continueFindingBranchType(int i, byte info[]) {
		// chkIndex is the index of the goto instruction.

		switch (info[i]) {

		case JvmOpCodes.ALOAD:

		case JvmOpCodes.ALOAD_0:

		case JvmOpCodes.ALOAD_1:

		case JvmOpCodes.ALOAD_2:

		case JvmOpCodes.ALOAD_3:

		case JvmOpCodes.DLOAD:

		case JvmOpCodes.DLOAD_0:

		case JvmOpCodes.DLOAD_1:

		case JvmOpCodes.DLOAD_2:

		case JvmOpCodes.DLOAD_3:

		case JvmOpCodes.FLOAD:

		case JvmOpCodes.FLOAD_0:

		case JvmOpCodes.FLOAD_1:

		case JvmOpCodes.FLOAD_2:

		case JvmOpCodes.FLOAD_3:

		case JvmOpCodes.ILOAD:

		case JvmOpCodes.ILOAD_0:

		case JvmOpCodes.ILOAD_1:

		case JvmOpCodes.ILOAD_2:

		case JvmOpCodes.ILOAD_3:

		case JvmOpCodes.LLOAD:

		case JvmOpCodes.LLOAD_0:

		case JvmOpCodes.LLOAD_1:

		case JvmOpCodes.LLOAD_2:

		case JvmOpCodes.LLOAD_3:
			return true;

		}

		return false;
	}

	private boolean isThisIfALoopCondition(IFBlock IF, byte[] info,
			ArrayList loops) {
		boolean b = true;
		int ifend = IF.getIfCloseLineNumber();
		int ifs = IF.getIfStart();
		boolean b1 = isThisLoopEndAlso(loops, ifend, ifs);
		ArrayList list = behaviour.getInstructionStartPositions();
		if (!b1)
			return false;
		if (b1) {
			int jump = getJumpAddress(info, ifend);
			if (jump >= ifs)
				return false;
			for (int s = jump; s < ifs; s++) {
				int inst = info[s];
				boolean b2 = isNextInstructionIf(inst);
				if (b2 && isThisInstrStart(list, s)) {
					b = false;
					return b;
				}
			}

		}

		return b;

	}

	private java.lang.String getReturnTypeInst(byte[] info, int i) {
		switch (info[i]) {
		case JvmOpCodes.IRETURN:
			return "ireturn";
		case JvmOpCodes.LRETURN:
			return "lreturn";
		case JvmOpCodes.FRETURN:
			return "freturn";
		case JvmOpCodes.DRETURN:
			return "dreturn";
		case JvmOpCodes.ARETURN:
			return "areturn";
		case JvmOpCodes.RETURN:
			return "return";
		default:
			return "";

		}

	}

	private Hashtable handlertracker = new Hashtable();

	private class handlerEndTracker {

		private int start = -1;

		private int end = -1;

		public int getStart() {
			return start;
		}

		public int getEnd() {
			return end;
		}

		public java.lang.String getType() {
			return type;
		}

		private java.lang.String type = "";

		private boolean closed = false;

		handlerEndTracker(int s, int e, java.lang.String t) {
			start = s;
			end = e;
			type = t;
		}

		void setClose(boolean c) {
			closed = c;
		}

		boolean getClosed() {
			return closed;
		}

	}

	private boolean addhandlerEnd(int i, ExceptionTable table) {
		boolean add = true;
		Object o = handlertracker.get(new Integer(i));
		if (o == null)
			return add;
		else {
			handlerEndTracker h = (handlerEndTracker) o;
			if (h.getType().equals(table.getTypeOfHandlerForGuardRegion())) {
				if (h.getStart() == table.getStartOfHandlerForGuardRegion()
						&& h.getEnd() == table.getEndOfHandlerForGuardRegion()) {
					return false;
				}
			}

		}

		return add;
	}

	private Hashtable handlerStarts = new Hashtable();

	private boolean addHandlerStart(int i) {
		boolean add = true;
		if (handlerStarts.size() > 0) {
			java.lang.String s = (java.lang.String) handlerStarts
					.get(new Integer(i));
			if (s == null)
				return add;
			else
				return false;
		}
		return add;
	}

	private Hashtable guardEnds = new Hashtable();

	private boolean addGuardEnd(int i) {

		if (guardEnds.size() == 0)
			return true;
		else {
			java.lang.String s = (java.lang.String) guardEnds
					.get(new Integer(i));
			if (s == null)
				return true;
			else
				return false;
		}

	}

	/***************************************************************************
	 * 
	 * @param index
	 *            localvarindex
	 * @param insttype
	 *            pass it as store or load
	 * @return
	 */

	// [NOTE:] THIS METHOD CAN RETURN NULL: SO HANDLE WITH CARE IN CALLING
	// METHOD....[belurs]
	private LocalVariable getLocalVariable(int index,
			java.lang.String insttype, java.lang.String dataType,
			boolean simpleLoadStore, int instpos) {
		if (index < 0) {
			index += 256;
		}
		LocalVariable l = null;
		LocalVariableStructure structure = behaviour.getLocalVariables();
		if (cd.isClassCompiledWithMinusG()) {
			if (structure != null)// Just a double check.. Need not check
			// actually
			{
				int rangeIndex = -1;
				if (insttype.equals("store")) {

					if (simpleLoadStore == true)
						rangeIndex = instpos + 1;
					else
						rangeIndex = instpos + 2;
					LocalVariable var = structure.getVariabelAtIndex(index,
							rangeIndex);
					if (var == null) {
						Object o = cd.getMethod_name_storeNLoad_Map().get(
								this.behaviour.getBehaviourName().concat(
										behaviour.getStringifiedParameters()));
						if (o instanceof Hashtable) {
							Hashtable h = (Hashtable) o;
							if (h != null && h.size() > 0) {
								Integer il = (Integer) h.get(new Integer(
										instpos));
								if (il != null) {
									int loadpos = il.intValue();
									var = structure.getVariabelAtIndex(index,
											loadpos);
								}
							}
						}
					}
					if (var == null) {
						// Create a veriable here
						// This probably indicates the variables is unused in
						// code
						// TODO Fix Required over here

						var = new LocalVariable(behaviour.getBehaviourName()
								.concat(behaviour.getStringifiedParameters()),
								"Var_" + index, index);
						boolean duplicateVarName = isLocalVariableWithNameAlreadyPresent(
								"jdec_var_" + index, behaviour
										.getLocalVariables()
										.getMethodLocalVaribales());
						if (!duplicateVarName)
							var.setDeclarationGenerated(false);
						else
							var.setDeclarationGenerated(true);
						var.setDataType(dataType);
						var.setWasCreated(true);
						structure.addLocalVariable(var);

					}
					return var;

				} else // This is for load
				{
					LocalVariable var = structure.getVariabelAtIndex(index,
							instpos);
					if (var == null) {
						// NOT Sure what to do here// SHOULD NEVER COME
						// HERE.....
						// Possible due to a finally block
						try {
							Writer wr = Writer.getWriter("log");
							wr
									.writeLog("Could not obtain local variable While decompiling "
											+ behaviour
													.getBehaviourName()
													.concat(
															behaviour
																	.getStringifiedParameters()));
							wr.writeLog("\nDetails.......");
							wr.writeLog("\n[Index Pos " + index
									+ ",Instruction Pos " + instpos
									+ " INSTRUCTION CODE: "
									+ behaviour.getCode()[instpos] + "]\n");
							wr.flush();
						} catch (Exception ex) {
						}

					}
					return var;

				}

			} else
				return null;
		} else {
			ConsoleLauncher.setCurrentClassCompiledWithDebugInfo(false);
			LocalVariable toreturn = null;
			if (behaviour.getLocalVariables() == null) // Again shud not
			// happen...
			{
				java.lang.String methodName = behaviour.getBehaviourName();
				structure = new LocalVariableStructure();
				behaviour.setMethodLocalVariables(structure);
				structure.setMethodDescription(methodName.concat(behaviour
						.getStringifiedParameters()));
				LocalVariableTable localVarTable = LocalVariableTable
						.getInstance();
				localVarTable.addEntry(methodName.concat(behaviour
						.getStringifiedParameters().concat(
								"" + behaviour.isMethodConstructor())),
						structure);
			}
			l = structure.getVariabelAtIndex(index, dataType,
					datatypesForParams);

			LocalVariable tmp = null;
			if (l == null) // Create and Add
			{
				java.lang.String variableName = "Var" + "_" + instpos + "_"
						+ index;
				if ((this.behaviour.getUserFriendlyMethodAccessors().indexOf(
						"static") == -1 && !behaviour.getBehaviourName().trim()
						.equals("static"))
						&& (index == 0))
					variableName = "this";
				l = new LocalVariable(behaviour.getBehaviourName().concat(
						behaviour.getStringifiedParameters()), variableName,
						index);
				l.setDeclarationGenerated(false);
				l.setDataType(dataType);
				l.setWasCreated(true);
				structure.addLocalVariable(l);
				toreturn = l;
				l.setPassedDataTypeWhileCreatingWithOutMinusG(dataType);

			} else {

				if (structure.getNumberOfSimilarIndexVars(index) > 1) {
					Object o = cd.getMethod_name_storeNLoad_Map().get(
							this.behaviour.getBehaviourName().concat(
									behaviour.getStringifiedParameters()));
					if (o instanceof Hashtable) {
						Hashtable h = (Hashtable) o;
						if (h != null && h.size() > 0) {
							Integer il = (Integer) h.get(new Integer(instpos));
							if (il != null) {
								int loadpos = il.intValue();
								tmp = structure.getVariableForLoadOrStorePos(
										index, loadpos);
							}
						}
					}

				}

				if (tmp == null)
					toreturn = l;
				else
					toreturn = tmp;

			} // BigInteger b

			java.lang.String dt = getStoredDataType(toreturn.getIndexPos());
			if (dt != null && dt.trim().length() != 0)
				toreturn.setDataType(dt.trim());
			if (this.behaviour.getUserFriendlyMethodAccessors().indexOf(
					"static") == -1
					&& !behaviour.getBehaviourName().trim().equals("static")
					&& (toreturn.getIndexPos() == 0)) {
				if (toreturn.getVarName().equals("this") == false) {
					toreturn.setVarName("this");
				}
			}
			return toreturn;

		}

	}

	private int getLoadIndexForReturn(byte[] info, int i, StringBuffer sb) {
		int index = -1;
		switch (info[(i - 1)]) {

		case JvmOpCodes.ALOAD_0:

			index = 0;
			break;
		case JvmOpCodes.ALOAD_1:
			index = 1;
			break;
		case JvmOpCodes.ALOAD_2:
			index = 2;
			break;
		case JvmOpCodes.ALOAD_3:
			index = 3;
			break;

		case JvmOpCodes.ILOAD_0:
			index = 0;
			break;
		case JvmOpCodes.ILOAD_1:
			index = 1;
			break;
		case JvmOpCodes.ILOAD_2:
			index = 2;
			break;
		case JvmOpCodes.ILOAD_3:
			index = 3;
			break;

		case JvmOpCodes.LLOAD_0:
			index = 0;
			break;
		case JvmOpCodes.LLOAD_1:
			index = 1;
			break;
		case JvmOpCodes.LLOAD_2:
			index = 2;
			break;
		case JvmOpCodes.LLOAD_3:
			index = 3;
			break;

		case JvmOpCodes.FLOAD_0:
			index = 0;
			break;
		case JvmOpCodes.FLOAD_1:
			index = 1;
			break;
		case JvmOpCodes.FLOAD_2:
			index = 2;
			break;
		case JvmOpCodes.FLOAD_3:
			index = 3;
			break;

		case JvmOpCodes.DLOAD_0:
			index = 0;
			break;
		case JvmOpCodes.DLOAD_1:
			index = 1;
			break;
		case JvmOpCodes.DLOAD_2:
			index = 2;
			break;
		case JvmOpCodes.DLOAD_3:
			index = 3;
			break;
		default:
			index = -1;

		}
		if (index != -1)
			sb.append((i - 1));

		if (index == -1) {

			switch (info[(i - 2)]) {
			case JvmOpCodes.ALOAD:
				index = info[(i - 1)];
				break;

			case JvmOpCodes.DLOAD:
				index = info[(i - 1)];
				break;

			case JvmOpCodes.FLOAD:
				index = info[(i - 1)];
				break;
			case JvmOpCodes.LLOAD:
				index = info[(i - 1)];
				break;
			case JvmOpCodes.ILOAD:
				index = info[(i - 1)];
				break;

			default:
				index = -1;
			}

			if (index != -1)
				sb.append((i - 2));
		}

		return index;
	}

	private Hashtable datatypesForParams = null;

	private void storeDataTypesForMethodParams(Behaviour b, ClassDescription cd) {
		datatypesForParams = new Hashtable();
		b.setDatatypesForParams(datatypesForParams);

		int count = 1;
		java.lang.String str = b.getUserFriendlyMethodParams();
		int staticPresent = b.getUserFriendlyMethodAccessors()
				.indexOf("static");
		if (staticPresent != -1) {
			count = 0;
		}
		int total = b.getNumberofparamters();
		if (total == 0)
			return;
		int s = 0;
		int endindex = str.indexOf(")");
		int startindex = str.indexOf("(");
		startindex += 1;
		if (startindex < endindex) {
			if (endindex > startindex) {
				java.lang.String reqdStr = str.substring(startindex, endindex);
				StringTokenizer tokenizer = new StringTokenizer(reqdStr, ",");
				while (tokenizer.hasMoreElements()) {
					Object o = tokenizer.nextElement();
					java.lang.String t = (java.lang.String) o;
					datatypesForParams.put(new Integer(count), t);
					if (t.trim().equals("long") || t.trim().equals("double"))
						count++;
					count++;
				}

			} else
				return;

		}

	}

	private java.lang.String getStoredDataType(int index) {
		java.lang.String dt = "";
		if (this.datatypesForParams != null && datatypesForParams.size() > 0) {
			return (java.lang.String) datatypesForParams
					.get(new Integer(index));
		}
		return dt;

	}

	private void storeDataTypesWRTConversionInst(Behaviour b,
			ClassDescription cd) {

		byte c[] = b.getCode();
		StringBuffer sb;
		LocalVariableStructure struc = b.getLocalVariables();
		for (int i = 0; i < c.length; i++) {
			sb = new StringBuffer("");
			int pos = isNextInstructionConversionInst(i, c);

			if (pos != -1) {
				java.lang.String resType = getResulatantTypeForConversionInst(
						i, c);
				java.lang.String srcType = getSourceTypeForConversionInst(i, c);
				StringBuffer varindex = new StringBuffer("");
				boolean store = isNextInstructionPrimitiveStoreInst(c,
						(pos + 1), varindex);
				if (store) {
					StringBuffer sb1 = new StringBuffer(""); // denotes type
					StringBuffer sb2 = new StringBuffer(""); // denotes index
					getIndexNTypeForNextInst(sb1, sb2, c, (pos + 1));
					int index = Integer.parseInt(sb2.toString());
					java.lang.String varName = "Var_" + (pos + 1) + "_" + index;
					LocalVariable local = new LocalVariable(b
							.getBehaviourName().concat(
									b.getStringifiedParameters()), varName,
							index);
					local.setDeclarationGenerated(false);
					if (resType == null || resType.trim().equals(""))
						local.setDataType(sb1.toString());
					else
						local.setDataType(resType);
					local.setWasCreated(true);

					LocalVariable l = struc.getVariabelAtIndex(index);
					if (l == null) {
						b.getLocalVariables().addLocalVariable(local);
					} else {

						LocalVariable l2 = struc.getVariableForLoadOrStorePos(
								index, (pos + 1));
						if (l2 != null) {
							if (resType == null || resType.trim().equals(""))
								local.setDataType(sb1.toString());
							else
								local.setDataType(resType);
						}

					}
				}
				StringBuffer sb3 = new StringBuffer("");
				boolean prev = isPrevInstPrimitiveLoad(c, (pos), sb3);
				if (prev) {
					StringBuffer sb1 = new StringBuffer(""); // denotes type
					StringBuffer sb2 = new StringBuffer(""); // denotes index
					getIndexNTypeForPrevInst(sb1, sb2, c, (Integer.parseInt(sb3
							.toString())));
					int index = Integer.parseInt(sb2.toString());
					java.lang.String varName = "Var_"
							+ (Integer.parseInt(sb3.toString())) + "_" + index;

					LocalVariable local = new LocalVariable(b
							.getBehaviourName().concat(
									b.getStringifiedParameters()), varName,
							index);
					local.setDeclarationGenerated(false);
					if (srcType == null || srcType.trim().equals(""))
						local.setDataType(sb1.toString());
					else
						local.setDataType(srcType);
					local.setWasCreated(true);

					LocalVariable l = struc.getVariabelAtIndex(index);
					if (l == null) {
						b.getLocalVariables().addLocalVariable(local);
					} else {

						LocalVariable l2 = struc.getVariableForLoadOrStorePos(
								index, (Integer.parseInt(sb3.toString())));
						if (l2 != null) {
							if (srcType == null || srcType.trim().equals(""))
								local.setDataType(sb1.toString());
							else
								local.setDataType(srcType);
						}

					}
				}
			}

		}

	}

	private boolean isNextInstructionPrimitiveStoreInst(byte[] c, int pos,
			StringBuffer index) {
		boolean flag = false;
		if (isThisInstrStart(behaviour.getInstructionStartPositions(), pos) == false)
			return false;
		switch (c[(pos)]) {
		case JvmOpCodes.DSTORE:
			flag = true;
			index.append(c[(pos + 1)]);
			break;
		case JvmOpCodes.DSTORE_0:
			flag = true;
			index.append(0);
			break;
		case JvmOpCodes.DSTORE_1:
			flag = true;
			index.append(1);
			break;
		case JvmOpCodes.DSTORE_2:
			flag = true;
			index.append(2);
			break;
		case JvmOpCodes.DSTORE_3:
			flag = true;
			index.append(3);
			break;

		case JvmOpCodes.FSTORE:
			flag = true;
			index.append(c[(pos + 1)]);
			break;
		case JvmOpCodes.FSTORE_0:
			flag = true;
			index.append(0);
			break;
		case JvmOpCodes.FSTORE_1:
			flag = true;
			index.append(1);
			break;
		case JvmOpCodes.FSTORE_2:
			flag = true;
			index.append(2);
			break;
		case JvmOpCodes.FSTORE_3:
			flag = true;
			index.append(3);
			break;

		case JvmOpCodes.ISTORE:
			flag = true;
			index.append(c[(pos + 1)]);
			break;
		case JvmOpCodes.ISTORE_0:
			flag = true;
			index.append(0);
			break;
		case JvmOpCodes.ISTORE_1:
			flag = true;
			index.append(1);
			break;
		case JvmOpCodes.ISTORE_2:
			flag = true;
			index.append(2);
			break;
		case JvmOpCodes.ISTORE_3:
			flag = true;
			index.append(3);
			break;

		case JvmOpCodes.LSTORE:
			flag = true;
			index.append(c[(pos + 1)]);
			break;

		case JvmOpCodes.LSTORE_0:
			flag = true;
			index.append(0);
			break;
		case JvmOpCodes.LSTORE_1:
			flag = true;
			index.append(1);
			break;
		case JvmOpCodes.LSTORE_2:
			flag = true;
			index.append(2);
			break;
		case JvmOpCodes.LSTORE_3:
			flag = true;
			index.append(3);
			break;

		}

		return flag;
	}

	// sb1 type
	private void getIndexNTypeForNextInst(StringBuffer sb1, StringBuffer sb2,
			byte[] c, int pos) {
		switch (c[(pos)]) {
		case JvmOpCodes.DSTORE:
			sb1.append("double");
			sb2.append(c[(pos + 1)]);
			break;
		case JvmOpCodes.DSTORE_0:
			sb1.append("double");
			sb2.append(0);
			break;
		case JvmOpCodes.DSTORE_1:
			sb1.append("double");
			sb2.append(1);
			break;
		case JvmOpCodes.DSTORE_2:
			sb1.append("double");
			sb2.append(2);
			break;
		case JvmOpCodes.DSTORE_3:
			sb1.append("double");
			sb2.append(3);
			break;

		case JvmOpCodes.FSTORE:
			sb1.append("float");
			sb2.append(c[(pos + 1)]);
			break;
		case JvmOpCodes.FSTORE_0:
			sb1.append("float");
			sb2.append(0);
			break;
		case JvmOpCodes.FSTORE_1:
			sb1.append("float");
			sb2.append(1);
			break;
		case JvmOpCodes.FSTORE_2:
			sb1.append("float");
			sb2.append(2);
			break;
		case JvmOpCodes.FSTORE_3:
			sb1.append("float");
			sb2.append(3);
			break;

		case JvmOpCodes.ISTORE:
			sb1.append("int");
			sb2.append(c[(pos + 1)]);
			break;
		case JvmOpCodes.ISTORE_0:
			sb1.append("int");
			sb2.append(0);
			break;
		case JvmOpCodes.ISTORE_1:
			sb1.append("int");
			sb2.append(1);
			break;
		case JvmOpCodes.ISTORE_2:
			sb1.append("int");
			sb2.append(2);
			break;
		case JvmOpCodes.ISTORE_3:
			sb1.append("int");
			sb2.append(3);
			break;

		case JvmOpCodes.LSTORE:
			sb1.append("long");
			sb2.append(c[(pos + 1)]);
			break;

		case JvmOpCodes.LSTORE_0:
			sb1.append("long");
			sb2.append(0);
			break;
		case JvmOpCodes.LSTORE_1:
			sb1.append("long");
			sb2.append(1);
			break;
		case JvmOpCodes.LSTORE_2:
			sb1.append("long");
			sb2.append(2);
			break;
		case JvmOpCodes.LSTORE_3:
			sb1.append("long");
			sb2.append(3);
			break;

		}

	}

	// pos --> conversion inst
	private boolean isPrevInstPrimitiveLoad(byte c[], int pos, StringBuffer sb) {
		boolean flag = false;

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
		if (flag)
			sb.append((pos - 1));
		if (!flag) {
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
			if (flag)
				sb.append((pos - 2));
		}

		return flag;
	}

	private void getIndexNTypeForPrevInst(StringBuffer sb1, StringBuffer sb2,
			byte c[], int pos) {

		boolean flag = false;
		switch (c[(pos)]) {

		case JvmOpCodes.ILOAD_0:
			flag = true;
			sb1.append("int");
			sb2.append(0);
			break;
		case JvmOpCodes.ILOAD_1:
			flag = true;
			sb1.append("int");
			sb2.append(1);
			break;
		case JvmOpCodes.ILOAD_2:
			flag = true;
			sb1.append("int");
			sb2.append(2);
			break;
		case JvmOpCodes.ILOAD_3:
			flag = true;
			sb1.append("int");
			sb2.append(3);
			break;

		case JvmOpCodes.LLOAD_0:
			flag = true;
			sb1.append("long");
			sb2.append(0);
			break;
		case JvmOpCodes.LLOAD_1:
			flag = true;
			sb1.append("long");
			sb2.append(1);
			break;
		case JvmOpCodes.LLOAD_2:
			flag = true;
			sb1.append("long");
			sb2.append(2);

			break;
		case JvmOpCodes.LLOAD_3:
			flag = true;
			sb1.append("long");
			sb2.append(3);
			break;

		case JvmOpCodes.FLOAD_0:
			flag = true;
			sb1.append("float");
			sb2.append(0);
			break;
		case JvmOpCodes.FLOAD_1:
			flag = true;
			sb1.append("float");
			sb2.append(1);
			break;
		case JvmOpCodes.FLOAD_2:
			flag = true;
			sb1.append("float");
			sb2.append(2);
			break;
		case JvmOpCodes.FLOAD_3:
			flag = true;
			sb1.append("float");
			sb2.append(3);
			break;

		case JvmOpCodes.DLOAD_0:
			flag = true;
			sb1.append("double");
			sb2.append(0);
			break;
		case JvmOpCodes.DLOAD_1:
			flag = true;
			sb1.append("double");
			sb2.append(1);
			break;
		case JvmOpCodes.DLOAD_2:
			flag = true;
			sb1.append("double");
			sb2.append(2);
			break;
		case JvmOpCodes.DLOAD_3:
			flag = true;
			sb1.append("double");
			sb2.append(3);
			break;

		default:
			flag = false;
			break;

		}

		if (!flag) {
			switch (c[(pos)]) {
			case JvmOpCodes.ILOAD:
				flag = true;
				sb1.append("int");
				sb2.append(c[(pos + 1)]);
				break;
			case JvmOpCodes.LLOAD:
				flag = true;
				sb1.append("long");
				sb2.append(c[(pos + 1)]);
				break;

			case JvmOpCodes.FLOAD:
				flag = true;
				sb1.append("float");
				sb2.append(c[(pos + 1)]);
				break;

			case JvmOpCodes.DLOAD:
				flag = true;
				sb1.append("double");
				sb2.append(c[(pos + 1)]);
				break;
			default:
				flag = false;
				break;
			}
			if (!flag) {
				sb1.append("");
				sb2.append(-1);
			}
		}

	}

	private void storeDataTypesWRTMethodParams(Behaviour b, ClassDescription cd) {
		LocalVariableStructure struc = b.getLocalVariables();
		if (datatypesForParams != null && datatypesForParams.size() > 0) {
			byte[] code = b.getCode();
			for (int s = 0; s < code.length; s++) {
				StringBuffer sb = new StringBuffer("");
				boolean bo = isThisInstructionIStoreInst(code, s, sb);
				if (bo) {
					StringBuffer sb2 = new StringBuffer("");
					boolean bo2 = isPrevInstIloadInst(code, s, sb2);
					if (bo2) {
						int loadindex = Integer.parseInt(sb2.toString());
						java.lang.String type = (java.lang.String) datatypesForParams
								.get(new Integer(loadindex));
						LocalVariable var = struc.getVariabelAtIndex(Integer
								.parseInt(sb.toString()));
						if (var == null && type != null) {
							int thisIndex = Integer.parseInt(sb.toString());
							java.lang.String varName = "Var_" + s + "_"
									+ thisIndex;
							LocalVariable local = new LocalVariable(b
									.getBehaviourName().concat(
											b.getStringifiedParameters()),
									varName, thisIndex);
							local.setDeclarationGenerated(false);
							local.setWasCreated(true);
							local.setDataType(type);
							struc.addLocalVariable(local);
						}
					}
				}

				// TODO: Handle astore and aload combinatiobn
				sb = new StringBuffer("");
				bo = isThisInstASTOREInst(code, s, sb);
				if (bo) {
					StringBuffer sb2 = new StringBuffer("");
					boolean bo2 = isPrevInstALOADInst(code, s, sb2);
					if (bo2) {
						int loadindex = Integer.parseInt(sb2.toString());
						java.lang.String type = (java.lang.String) datatypesForParams
								.get(new Integer(loadindex));
						LocalVariable var = struc.getVariabelAtIndex(Integer
								.parseInt(sb.toString()));
						if (var == null && type != null) {
							int thisIndex = Integer.parseInt(sb.toString());
							java.lang.String varName = "Var_" + s + "_"
									+ thisIndex;
							LocalVariable local = new LocalVariable(b
									.getBehaviourName().concat(
											b.getStringifiedParameters()),
									varName, thisIndex);
							local.setDeclarationGenerated(false);
							local.setWasCreated(true);
							local.setDataType(type);
							struc.addLocalVariable(local);
						}

					}
				}
			}
		}
	}

	private boolean isThisInstructionIStoreInst(byte[] code, int s,
			StringBuffer sb) {
		boolean b = false;
		if (isThisInstrStart(behaviour.getInstructionStartPositions(), s) == false)
			return false;
		switch (code[s]) {
		case JvmOpCodes.ISTORE_0:
			b = true;
			sb.append(0);
			break;
		case JvmOpCodes.ISTORE_1:
			b = true;
			sb.append(1);
			break;
		case JvmOpCodes.ISTORE_2:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.ISTORE_3:
			b = true;
			sb.append(3);
			break;
		case JvmOpCodes.ISTORE:
			b = true;
			sb.append(code[(s + 1)]);
			break;
		}

		return b;
	}

	private boolean isPrevInstIloadInst(byte[] code, int s, StringBuffer sb2) {
		ArrayList starts = behaviour.getInstructionStartPositions();
		boolean b = false;
		if (isThisInstrStart(starts, (s - 1))) {
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

		if (!b && (s - 2) >= 0 && isThisInstrStart(starts, (s - 2))) {

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

	private void storeDataTypesWRTLoadNStoreCombinations(Behaviour b,
			ClassDescription cd) {
		byte code[] = b.getCode();
		LocalVariableStructure struc = b.getLocalVariables();
		for (int s = 0; s < code.length; s++) {
			StringBuffer sb = new StringBuffer("");
			boolean bo = isThisInstructionIStoreInst(code, s, sb);
			if (bo) {
				StringBuffer sb2 = new StringBuffer("");
				boolean bo2 = isPrevInstIloadInst(code, s, sb2);
				if (bo2) {
					int loadindex = Integer.parseInt(sb2.toString());
					LocalVariable var = struc.getVariabelAtIndex(Integer
							.parseInt(sb.toString()));
					LocalVariable loadvar = struc.getVariabelAtIndex(Integer
							.parseInt(sb2.toString()));
					if (loadvar != null) {

						// author :belurs:
						// NOTE:
						// Possible source of error here.
						// There may be more than one localvariable with the
						// same index
						// And since this code is reachable from the option
						// -g:none
						// It is not really possible to determine the exact
						// variable
						// The below code is expected to give out the correct
						// data type
						// in most cases.
						// END OF NOTE:

						// TODO: FIX ME later
						// FIX THE ambiguity in local variable at this point
						// [Possible correction : If localvar is null for this
						// load
						// Index...Keep going back till a store occurs with same
						// index....Possibly recursive here..may have to serach
						// for
						// prev local/store till local is not null...Will have
						// to do
						// This carefully

						if (var == null) {
							int thisIndex = Integer.parseInt(sb.toString());
							java.lang.String varName = "Var_" + s + "_"
									+ thisIndex;
							LocalVariable local = new LocalVariable(b
									.getBehaviourName().concat(
											b.getStringifiedParameters()),
									varName, thisIndex);
							local.setDeclarationGenerated(false);
							local.setWasCreated(true);
							local.setDataType(loadvar.getDataType());
							struc.addLocalVariable(local);
						}
					}
				}
			}

			// TODO: aload and astore
			sb = new StringBuffer("");
			boolean astore = isThisInstASTOREInst(code, s, sb);
			if (astore) {
				StringBuffer sb2 = new StringBuffer("");
				boolean aload = isPrevInstALOADInst(code, s, sb2);
				if (aload) {

					int loadindex = Integer.parseInt(sb2.toString());
					LocalVariable var = struc.getVariabelAtIndex(Integer
							.parseInt(sb.toString()));
					LocalVariable loadvar = struc.getVariabelAtIndex(Integer
							.parseInt(sb2.toString()));
					if (loadvar != null) {
						if (var == null) {
							int thisIndex = Integer.parseInt(sb.toString());
							java.lang.String varName = "Var_" + s + "_"
									+ thisIndex; //
							LocalVariable local = new LocalVariable(b
									.getBehaviourName().concat(
											b.getStringifiedParameters()),
									varName, thisIndex);
							local.setDeclarationGenerated(false);
							local.setWasCreated(true);
							local.setDataType(loadvar.getDataType());
							struc.addLocalVariable(local);
						}
					}
				}

			}

		}
	}

	private void createLocalVariablesForMethodParams(Behaviour b,
			ClassDescription cd) {
		byte[] code = b.getCode();
		ArrayList loadStoreIndexes = new ArrayList();

		if (datatypesForParams != null && datatypesForParams.size() > 0) {
			Set set = datatypesForParams.keySet();
			/*
			 * for(int s=0;s<code.length;s++) {
			 * 
			 * StringBuffer sb=new StringBuffer(""); boolean
			 * anyIndex=getAnyLoadStoreIndex(code,s,sb); if(anyIndex) { Integer
			 * i=new Integer(sb.toString()); loadStoreIndexes.add(i); } }
			 */
			Iterator it = set.iterator();
			LocalVariableStructure struc = b.getLocalVariables();
			while (it.hasNext()) {
				Integer pos = (Integer) it.next();
				if (pos != null) // Just a check ...
				{
					int ipos = pos.intValue();

					LocalVariable var = struc.getVariabelAtIndex(ipos);
					if (var == null) // Create dummy variable
					{
						java.lang.String vname = "Var_" + ipos;
						LocalVariable lv = new LocalVariable(b
								.getBehaviourName().concat(
										b.getStringifiedParameters()), vname,
								ipos);
						lv.setDeclarationGenerated(false);
						lv.setWasCreated(true);
						lv.setMethodParameterVariable(true);
						lv.setDataType((java.lang.String) datatypesForParams
								.get(pos));
						struc.addLocalVariable(lv);

					} else {
						java.lang.String vname = "param_" + ipos;
						var.setVarName(vname);
						var.setMethodParameterVariable(true);
						var.setDataType((java.lang.String) datatypesForParams
								.get(pos));
					}

				}

			}

		}

	}

	private Hashtable invokeStart_StackStart = null;

	private Hashtable invokespecialpos = null;

	private ArrayList getField = null;

	private ArrayList getStatic = null;

	private Hashtable invokeinstrefpos = null;

	private Hashtable invokeinstrefretTypes = null;

	private void storeDataTypesWRTInvokeInst(Behaviour b, ClassDescription cd) {
		// if(cd.isClassCompiledWithMinusG()==false)
		// {
		GlobalVariableStore.setVariableDimAss(new Hashtable());
		invokeStart_StackStart = new Hashtable();
		GlobalVariableStore.setInvokeStartEnd(new Hashtable());
		invokespecialpos = new Hashtable();
		getField = new ArrayList();
		getStatic = new ArrayList();
		invokeinstrefpos = new Hashtable();
		invokeinstrefretTypes = new Hashtable();

		// }
		GlobalVariableStore.setPushTypes(new Hashtable());
		byte code[] = b.getCode();
		LocalVariableStructure struc = b.getLocalVariables();
		label: for (int s = 0; s < code.length; s++) {
			// System.out.println(s+" In method "+b.getBehaviourName());
			StringBuffer sb = new StringBuffer("");
			boolean invoke = isNextInstructionAnyInvoke(code[s], sb);
			Integer skip = (Integer) ConsoleLauncher.getInstructionMap().get(
					new Integer(code[s]));
			boolean skipthisinvoke = false;
			if (skip == null)
				continue;
			else {
				if (invoke) {

					int newpresent = s - 3;
					if (code[s] == JvmOpCodes.INVOKESPECIAL) {
						if (newpresent >= 0
								&& code[newpresent] == JvmOpCodes.INVOKESPECIAL) {
							// int prevpos=
							ArrayList statpos = behaviour
									.getInstructionStartPositions();
							Integer z = (Integer) invokeinstrefpos
									.get(new Integer(newpresent));
							if (z != null) {
								int prevpos = z.intValue() - 1;
								ArrayList startpos = behaviour
										.getInstructionStartPositions();
								for (int k = prevpos; k >= 0; k--) {
									boolean st = isThisInstrStart(startpos, k);
									while (st == false && k >= 0) {
										k--;
										st = isThisInstrStart(startpos, k);
									}

									// some start pos found
									if (k >= 0) {
										if (code[k] == JvmOpCodes.NEW) {
											invokespecialpos.put(
													new Integer(s),
													new Integer(k));
											invokeinstrefpos.put(
													new Integer(s),
													new Integer(k));
											break;
										}
									}

								}
							}

						} else {
							int prevpos = s - 1;
							ArrayList startpos = behaviour
									.getInstructionStartPositions();
							for (int k = prevpos; k >= 0; k--) {
								boolean st = isThisInstrStart(startpos, k);
								while (st == false && k >= 0) {
									k--;
									st = isThisInstrStart(startpos, k);
								}

								// some start pos found
								boolean needtocontinue = false;
								if (k >= 0 && code[k] == JvmOpCodes.GETFIELD) {
									Iterator it = invokeinstrefpos.entrySet()
											.iterator();
									while (it.hasNext()) {
										Map.Entry en = (Map.Entry) it.next();
										Integer i1 = (Integer) en.getKey();
										Integer i2 = (Integer) en.getValue();

										if (i2.intValue() == (k - 1)
												&& i1.intValue() != s) {
											needtocontinue = true;
											break;
										}
										if (i2.intValue() == (k - 2)
												&& i1.intValue() != s) {
											needtocontinue = true;
											break;
										}

									}

									if (!needtocontinue)
										break;

								}
								if (k >= 0 && (code[k] == JvmOpCodes.NEW)) {
									invokespecialpos.put(new Integer(s),
											new Integer(k));
									invokeinstrefpos.put(new Integer(s),
											new Integer(k));
									break;
								}

							}

						}

					}

				}

				// ?
				if (!invoke) {

					/*
					 * if(code[s]==JvmOpCodes.ANEWARRAY) {
					 * 
					 * StringBuffer invokepos=new StringBuffer(""); boolean
					 * process=processANEWARRAYb4Invoke(code,s,invokepos);
					 * if(process) { stackHandler=new Stack(); InstParam
					 * first=new InstParam(); first.setNumberOfParamsLeft(1);
					 * first.setCurrentParamOffsetInCode(-1);
					 * stackHandler.push(first);
					 * handleBasicPrimitiveLoadOp(code,s-1); InstParam
					 * obj=(InstParam)stackHandler.pop(); int
					 * offset=obj.getCurrentParamOffsetInCode();
					 * anewarrayrefpos.put(new
					 * Integer(Integer.parseInt(invokepos.toString())),new
					 * Integer(offset)); } }
					 */

					if (code[s] == JvmOpCodes.GETSTATIC) {
						getStatic.add(new Integer(s));
					}
					if (code[s] == JvmOpCodes.GETFIELD) {
						getField.add(new Integer(s));
					}
					int toskip = skip.intValue();
					boolean xx = false;
					if (toskip == -1) // lookup // TODO: verify increment
					{
						xx = true;
						int lookupSwitchPos = s;
						int leave_bytes = (4 - (s % 4)) - 1;
						for (int indx = 0; indx < leave_bytes; indx++) {
							s++;
						}
						// Read Default
						int Default = getSwitchOffset(code, s, "");// info[++i]
						// << 24) |
						// (info[++i]
						// << 16) |
						// (info[++i]
						// << 8)
						// |info[++i];
						s += 4;
						int numberOfLabels = (getSwitchOffset(code, s, "")); // info[++i]
						// <<
						// 24)
						// |
						// (info[++i]
						// <<
						// 16)
						// |
						// (info[++i]
						// <<
						// 8)
						// |info[++i];
						// int high=(info[++i] << 24) | (info[++i] << 16) |
						// (info[++i] << 8) |info[++i];
						// int numberOfOffsets=(high-low)+1;
						int offsetValues[] = new int[numberOfLabels];
						int labels[] = new int[numberOfLabels];
						s += 4;
						for (int start = 0; start < numberOfLabels; start++) {
							int label = getSwitchOffset(code, s, "label");
							s += 4;
							int offsetVal = getSwitchOffset(code, s, "");// (info[++i]
							// <<
							// 24)
							// |
							// (info[++i]
							// <<
							// 16)
							// |
							// (info[++i]
							// <<
							// 8)
							// |info[++i];
							s += 4;
							labels[start] = label;
							offsetValues[start] = offsetVal;

						}

					}
					if (toskip == -2) // table
					{
						xx = true;
						int tableSwitchPos = s;
						int leave_bytes = (4 - (s % 4)) - 1;
						for (int indx = 0; indx < leave_bytes; indx++) {
							s++;
						}
						// Read Default
						int Default = getSwitchOffset(code, s, "");// (info[++i]
						// << 24) |
						// (info[++i]
						// << 16) |
						// (info[++i]
						// << 8)
						// |info[++i];
						s += 4;
						int low = getSwitchOffset(code, s, "label");// (info[++i]
						// << 24) |
						// (info[++i]
						// << 16) |
						// (info[++i]
						// << 8)
						// |info[++i];
						s += 4;
						int high = getSwitchOffset(code, s, "label");// (info[++i]
						// <<
						// 24) |
						// (info[++i]
						// <<
						// 16) |
						// (info[++i]
						// << 8)
						// |info[++i];
						s += 4;
						int numberOfOffsets = (high - low) + 1;
						int[] offsetValues = new int[numberOfOffsets];
						for (int start = 0; start < numberOfOffsets; start++) {
							int offsetVal = getSwitchOffset(code, s, "");
							s += 4;
							offsetValues[start] = offsetVal;

						}

					}
					if (toskip == -3) // wide // TODO ?
					{

					}
					if (!xx)
						s = s + toskip;
					else
						s = s + 1;

					continue;
				}

			}

			java.lang.String belongingClassName = "";
			java.lang.String returnTypeName = "";
			if (invoke) // && !(sb.toString().equalsIgnoreCase("special")))
			{
				int classIndex = getOffset(code, s);
				java.lang.String methodSignature = "";
				if (sb.toString().indexOf("interface") == -1) {
					MethodRef mref = cd.getMethodRefAtCPoolPosition(classIndex);
					methodSignature = mref.getTypeofmethod();
					belongingClassName = mref.getClassname();
					returnTypeName = mref.getTypeofmethod();
					int brk = returnTypeName.indexOf(")");
					if (brk != -1) {
						returnTypeName = returnTypeName.substring((brk + 1));
						brk = returnTypeName.indexOf("L");
						if (brk != -1) {
							returnTypeName = returnTypeName.substring(brk + 1,
									returnTypeName.indexOf(";"));

						}
					}
					invokeinstrefretTypes.put(new Integer(s),
							belongingClassName + "::" + returnTypeName);
				} else {
					InterfaceMethodRef mref = cd
							.getInterfaceMethodAtCPoolPosition(classIndex);
					methodSignature = mref.getTypeofmethod();
					belongingClassName = mref.getClassname();
					returnTypeName = mref.getTypeofmethod();
					int brk = returnTypeName.indexOf(")");
					if (brk != -1) {
						returnTypeName = returnTypeName.substring((brk + 1));
						brk = returnTypeName.indexOf("L");
						if (brk != -1) {
							returnTypeName = returnTypeName.substring(brk + 1,
									returnTypeName.indexOf(";"));

						}
					}
					invokeinstrefretTypes.put(new Integer(s),
							belongingClassName + "::" + returnTypeName);
				}

				Util.parseDescriptor(methodSignature);
				ArrayList list = Util.getParsedSignatureAsList();

				int num = list.size();
				int z = (num - 1);
				StringBuffer pos = null;
				int start = s - 1;
				int ipos = -1; // where local var occurs
				boolean skiploop = false;
				while (z >= 0) {
					StringBuffer index = new StringBuffer("");
					StringBuffer dim = new StringBuffer("");

					pos = new StringBuffer(""); // where local var occurs
					java.lang.String type = (java.lang.String) list.get(z);

					//
					Integer isinvokestart = (Integer) GlobalVariableStore
							.getInvokeStartEnd().get(new Integer(start));
					while (isinvokestart != null) {
						int invokestart = isinvokestart.intValue();
						Integer stackstart = (Integer) invokeStart_StackStart
								.get(new Integer(invokestart));
						Integer refstart = (Integer) invokeinstrefpos
								.get(new Integer(invokestart));
						int istart = -1;
						if (refstart == null) {
							GlobalVariableStore.getProblematicInvokes().add(
									new Integer(s));
							skipthisinvoke = true;
							skiploop = true;
							break;
						}
						if (refstart != null) {

							istart = refstart.intValue();
							StringBuffer buffer = new StringBuffer("");
							if (isNextInstructionAnyInvoke(code[istart], buffer)) {
								java.lang.String sTR = (java.lang.String) invokeinstrefretTypes
										.get(new Integer(istart));
								if (sTR != null) {
									int sep = sTR.indexOf("::");
									java.lang.String rEF = sTR
											.substring(0, sep);
									java.lang.String rET = sTR
											.substring(sep + 2);
									if (rEF.trim().equalsIgnoreCase(rET.trim())) {
										ipos = istart;
									} else {
										refstart = (Integer) invokeinstrefpos
												.get(new Integer(istart));
										if (refstart != null)
											ipos = refstart.intValue();
									}
								}

							}
							start = refstart.intValue() - 1;
						}

						z--;
						int h = -1;
						if (ipos == -1)
							h = istart - 1;
						else
							h = ipos - 1;
						isinvokestart = (Integer) GlobalVariableStore
								.getInvokeStartEnd().get(new Integer(h));

						if (z == -1) {
							skiploop = true;
							break;
						}
					}

					if (skiploop)
						break;
					//
					type = (java.lang.String) list.get(z);
					try {
						getCorrespondingLoadInst(index, dim, start, pos,
								this.behaviour, this.cd, type, s);

						Integer.parseInt(pos.toString());
					} catch (Exception ne) {
						// Skip This invoke
						// Because the current rules are not sufficient to track
						// the beginning
						// of a prameter load for a method
						GlobalVariableStore.getProblematicInvokes().add(
								new Integer(s));
						skipthisinvoke = true;
						break;

					}

					ipos = Integer.parseInt(pos.toString());
					start = (ipos - 1);
					int in = Integer.parseInt(index.toString());
					int dimensions = Integer.parseInt(dim.toString());
					LocalVariable l = struc.getVariabelAtIndex(in);

					if (cd.isClassCompiledWithMinusG() == false) {
						if (l != null) {

							int bracket = type.indexOf("[");
							if (bracket != -1) {
								int brackets = 0;
								for (int d = bracket; d < type.length(); d++) {
									if (type.charAt(d) == '[')
										brackets++;
									else
										break;
								}

								int totalDimenForVar = dimensions + brackets;
								// Assocaite here
								if (GlobalVariableStore.getVariableDimAss() != null)
									GlobalVariableStore.getVariableDimAss()
											.put(
													new Integer(in),
													new Integer(
															totalDimenForVar));
							} else {
								int L = type.indexOf("L");
								if (L != -1) {
									type = type.substring((L + 1));
								}
								int semi = type.indexOf(";");
								if (semi != -1) {
									type = type.substring(0, (semi));
								}
								// l.setDataType(type); // NOTE: commented this
								// line as it was resetting already set correct
								// datatype
							}
						}
						if (l == null && in != -100 && in != -200) {
							int bracket = type.indexOf("[");
							if (bracket == -1) {
								int L = type.indexOf("L");
								if (L != -1) {
									type = type.substring((L + 1));
								}
								int semi = type.indexOf(";");
								if (semi != -1) {
									type = type.substring(0, (semi));
								}

								java.lang.String varName = "Var_" + ipos + "_"
										+ in;
								if (this.behaviour
										.getUserFriendlyMethodAccessors()
										.indexOf("static") == -1
										&& (in == 0))
									varName = "this";
								LocalVariable newl = new LocalVariable(b
										.getBehaviourName().concat(
												b.getStringifiedParameters()),
										varName, in);
								newl.setDeclarationGenerated(false);
								newl.setWasCreated(true);
								newl.setDataType(type);
								struc.addLocalVariable(newl);

							}
							if (bracket != -1) {
								int brackets = 0;
								for (int d = bracket; d < type.length(); d++) {
									if (type.charAt(d) == '[')
										brackets++;
									else
										break;
								}

								int totalDimenForVar = dimensions + brackets;
								// Assocaite here
								if (GlobalVariableStore.getVariableDimAss() != null)
									GlobalVariableStore.getVariableDimAss()
											.put(
													new Integer(in),
													new Integer(
															totalDimenForVar));
							}
						}
					}
					if (in == -200) {
						GlobalVariableStore.getPushTypes().put(
								new Integer(ipos), type);
					}

					z--;

				}
				boolean ok = false;
				if (invoke && !skipthisinvoke) {
					StringBuffer S = new StringBuffer("");
					ok = checkForAssociatedGetField(getField, s, S, code);
					if (ok) {
						int iS = Integer.parseInt(S.toString());
						int refpos = getObjectRefForGetField(iS, code);
						if (refpos != -1) {
							if (invokeinstrefpos.get(new Integer(s)) == null) {
								invokeinstrefpos.put(new Integer(s),
										new Integer(refpos));
							}

						}
					}
					if (!ok) {
						S = new StringBuffer("");
						ok = checkForAssociatedGetStatic(getStatic, s, S, code); // TODO:
						// possible
						// bug
						// :
						// check
						// for
						// pop
						// and
						// aload
						// then
						if (ok) {
							int iS = Integer.parseInt(S.toString());
							if ((iS - 1) >= 0 && code[iS - 1] == JvmOpCodes.POP) {
								StringBuffer s2 = new StringBuffer("");
								boolean bb = isPrevInstructionAload(iS - 1,
										code, s2);
								if (bb) {
									iS = Integer.parseInt(s2.toString());
								}
							}
							if (invokeinstrefpos.get(new Integer(s)) == null) {
								invokeinstrefpos.put(new Integer(s),
										new Integer(iS));
							}
						}
					}
					if (!ok) {
						S = new StringBuffer("");
						ok = checkForAssociatedInvokeSpecial(invokespecialpos,
								s, S, code);
						if (ok) {
							int iS = Integer.parseInt(S.toString());
							if (invokeinstrefpos.get(new Integer(s)) == null) {
								invokeinstrefpos.put(new Integer(s),
										new Integer(iS));
							}
						}

					}

				}
				boolean skip2 = false;
				if (!ok && code[s] == JvmOpCodes.INVOKESTATIC && ipos == -1
						&& !skipthisinvoke) {
					ipos = s;
					if (invokeinstrefpos.get(new Integer(s)) == null) {
						invokeinstrefpos.put(new Integer(s), new Integer(ipos));
					}
					skip2 = true;

				}
				if (!ok && code[s] == JvmOpCodes.INVOKESTATIC && ipos != -1
						&& !skipthisinvoke) {
					if (invokeinstrefpos.get(new Integer(s)) == null) {
						invokeinstrefpos.put(new Integer(s), new Integer(ipos));
					}
					skip2 = true;

				}

				int prev = s - 1;
				if (ipos != -1)
					prev = ipos - 1;
				if (!ok && prev >= 0 && !skip2 && !skipthisinvoke) {
					Integer pr = new Integer(prev);
					Integer be = (Integer) GlobalVariableStore
							.getInvokeStartEnd().get(pr);
					if (be != null) {
						int Ibe = be.intValue();
						if (code[Ibe] == JvmOpCodes.INVOKEVIRTUAL
								|| code[Ibe] == JvmOpCodes.INVOKEINTERFACE) {
							java.lang.String sTR = (java.lang.String) invokeinstrefretTypes
									.get(new Integer(Ibe));
							if (sTR != null) {
								int sep = sTR.indexOf("::");
								java.lang.String rEF = sTR.substring(0, sep);
								java.lang.String rET = sTR.substring(sep + 2);
								if (rEF.trim().equalsIgnoreCase(rET.trim())) {
									Integer refpos = (Integer) invokeinstrefpos
											.get(new Integer(Ibe));
									if (refpos != null) {
										int irefpos = refpos.intValue();
										Integer reftemp = (Integer) invokeinstrefpos
												.get(new Integer(s));
										if (reftemp == null) {
											invokeinstrefpos.put(
													new Integer(s),
													new Integer(irefpos));
										}
									}
								} else {
									Integer refpos = (Integer) invokeinstrefpos
											.get(new Integer(s));
									if (refpos == null) {
										invokeinstrefpos.put(new Integer(s),
												new Integer(Ibe));
									}
								}
							}
						}
						if (code[Ibe] == JvmOpCodes.INVOKESTATIC
								|| code[Ibe] == JvmOpCodes.INVOKESPECIAL) {
							Integer refpos = (Integer) invokeinstrefpos
									.get(new Integer(Ibe));
							if (invokeinstrefpos.get(new Integer(s)) == null) {
								invokeinstrefpos.put(new Integer(s),
										new Integer(refpos.intValue()));
							}

						}
					} else {
						StringBuffer sfb = new StringBuffer("");
						boolean bol = isPrevInstructionAload(prev + 1, code,
								sfb);
						if (bol) {
							int aloadpos = Integer.parseInt(sfb.toString());
							if (invokeinstrefpos.get(new Integer(s)) == null) {
								invokeinstrefpos.put(new Integer(s),
										new Integer(aloadpos));

							}
						} else {
							if (code[prev] == JvmOpCodes.AALOAD) {
								int x = prev;
								do {
									sfb = new StringBuffer("");
									x--;
									bol = isPrevInstructionAload(x, code, sfb);
								} while (!bol);
								int aloadpos = Integer.parseInt(sfb.toString());
								if (invokeinstrefpos.get(new Integer(s)) == null) {
									invokeinstrefpos.put(new Integer(s),
											new Integer(aloadpos));
								}

							}
						}
					}

				}

				if (invokeStart_StackStart != null)
					invokeStart_StackStart.put(new Integer(s),
							new Integer(ipos)); // modify for special/static

				int end = -1;
				if (GlobalVariableStore.getInvokeStartEnd() != null) {
					end = findWhereThisInvokeEnds(s, code);
					GlobalVariableStore.getInvokeStartEnd().put(
							new Integer(end), new Integer(s));
				}
				if (end != -1) {
					int next = end + 1;
					s = end; // changed from next to end
				}

			}
		}

	}

	private boolean isInstAAload(int inst) {
		return inst == JvmOpCodes.AALOAD;
	}

	private int isInstAload(int i, byte[] code, StringBuffer bf) {
		if (isThisInstrStart(behaviour.getInstructionStartPositions(), i)) {
			switch (code[i]) {

			case JvmOpCodes.ALOAD_0:
				bf.append(0);
				return i;
			case JvmOpCodes.ALOAD_1:
				bf.append(1);
				return i;
			case JvmOpCodes.ALOAD_2:
				bf.append(2);
				return i;
			case JvmOpCodes.ALOAD_3:
				bf.append(3);
				return i;
			case JvmOpCodes.ALOAD:
				bf.append(code[(i + 1)]);
				return i;

			}
		}

		int temp = i - 1;
		if (isThisInstrStart(behaviour.getInstructionStartPositions(), temp)) {
			if (code[temp] == JvmOpCodes.ALOAD) {
				bf.append(code[i]);
				return temp;
			}
		}
		return -1;

	}

	// todo: // Search For StringBuffer from here

	private void getCorrespondingLoadInst(StringBuffer index, StringBuffer dim,
			int start, StringBuffer pos, Behaviour b, ClassDescription cd,
			java.lang.String type, int thisinvokestart) throws Exception {

		ArrayList starts = b.getInstructionStartPositions();
		boolean primitive = isPrimitive(type);

		byte[] code = b.getCode();
		int number = 0;
		boolean aaloadFound = false;
		int k = start;
		int orig = k;
		// Find out the instuction start here
		boolean istart = isThisInstrStart(starts, k);
		while (istart == false && k >= 0) {
			k = k - 1;
			istart = isThisInstrStart(starts, k);
		}

		// Now k must be inst start

		if (primitive) {

			if (code[k] == JvmOpCodes.ARRAYLENGTH) // TODO: TEST IT THOROUhLY
			{

				StringBuffer s6 = new StringBuffer("");
				boolean aloadp = isPrevInstructionAload(k, code, s6);
				if (aloadp) {
					index.append(-200);
					dim.append(0);
					pos.append(Integer.parseInt(s6.toString()));
					return;
				}
				if ((k - 1) >= 0) {
					boolean aaloadp = isInstAAload(code[k - 1]); // BUG check
					// for
					// getstatic
					// alos.:TODO
					if (aaloadp) {
						s6 = new StringBuffer("");
						int in = k - 1;
						int posFound = isInstAload(in, code, s6);
						while (posFound == -1) {
							in = in - 1;
							posFound = isInstAload(in, code, s6);
							if (posFound == -1) {
								if (code[in] == JvmOpCodes.GETSTATIC) // TODO:
								// Need
								// to
								// check
								// at
								// all
								// such
								// places
								// whether
								// the
								// index
								// is
								// start
								// of
								// instruction
								{

									if (code[in - 1] == JvmOpCodes.POP) // TODO:
									// Possible
									// bug
									// with
									// pop2
									{
										StringBuffer s3 = new StringBuffer("");
										boolean bb = isPrevInstructionAload(
												in - 1, code, s3);
										if (bb) {
											index.append(-200);
											dim.append(0);
											pos.append(Integer.parseInt(s3
													.toString()));
											return;
										}
									} else {
										index.append(-200);
										dim.append(0);
										pos.append(in);
										return;
									}

								}
								if (code[in] == JvmOpCodes.GETFIELD) {
									s6 = new StringBuffer("");
									aloadp = isPrevInstructionAload(in, code,
											s6);
									index.append(-200);
									dim.append(0);
									pos.append(Integer.parseInt(s6.toString()));
									return;
								}

							}

						}
						index.append(-200);
						dim.append(0);
						pos.append(posFound);
						return;

					}
				}
				if ((k - 3) >= 0) {
					boolean getfield = (code[k - 3] == JvmOpCodes.GETFIELD);
					if (getfield) {
						s6 = new StringBuffer("");
						aloadp = isPrevInstructionAload(k - 3, code, s6);
						index.append(-200);
						dim.append(0);
						pos.append(Integer.parseInt(s6.toString()));
						return;
					}
				}
				if ((k - 3) >= 0) {
					boolean getS = (code[k - 3] == JvmOpCodes.GETSTATIC);
					if (getS) {
						if (code[k - 4] == JvmOpCodes.POP) // TODO: Possible
						// bug with pop2
						{
							StringBuffer s3 = new StringBuffer("");
							boolean bb = isPrevInstructionAload(k - 4, code, s3);
							if (bb) {
								index.append(-200);
								dim.append(0);
								pos.append(Integer.parseInt(s3.toString()));
								return;
							}
						}
						index.append(-200);
						dim.append(0);
						pos.append(k - 3);
						return;

					}
				}

				// TODO:newarray ?

				if ((k - 4) >= 0) {
					boolean multi = (code[k - 4] == JvmOpCodes.MULTIANEWARRAY);
					if (multi) {
						stackHandler = new Stack();
						InstParam first = new InstParam();
						first.setNumberOfParamsLeft(code[k - 4 + 3]);
						first.setCurrentParamOffsetInCode(-1);
						stackHandler.push(first);
						handleBasicPrimitiveLoadOp(code, k - 5);
						index.append(-100);
						dim.append(0);
						InstParam obj = (InstParam) stackHandler.pop(); // should
						// be
						// first
						pos.append(obj.getCurrentParamOffsetInCode()); // TODO:
						// check
						// whether
						// this
						// is
						// correct
						return;

					}
				}
				if ((k - 3) >= 0) {

					boolean multi = (code[k - 3] == JvmOpCodes.ANEWARRAY);
					if (multi) {

						stackHandler = new Stack();
						InstParam first = new InstParam();
						first.setNumberOfParamsLeft(1);
						first.setCurrentParamOffsetInCode(-1);
						stackHandler.push(first);
						handleBasicPrimitiveLoadOp(code, k - 3 - 1);
						index.append(-100);
						dim.append(0);
						InstParam obj = (InstParam) stackHandler.pop(); // should
						// be
						// first
						pos.append(obj.getCurrentParamOffsetInCode()); // TODO:
						// check
						// whether
						// this
						// is
						// correct
						return;

					}
				}

				if ((k - 2) >= 0) {

					boolean multi = (code[k - 2] == JvmOpCodes.NEWARRAY);
					if (multi) {

						stackHandler = new Stack();
						InstParam first = new InstParam();
						first.setNumberOfParamsLeft(1);
						first.setCurrentParamOffsetInCode(-1);
						stackHandler.push(first);
						handleBasicPrimitiveLoadOp(code, k - 3);
						index.append(-100);
						dim.append(0);
						InstParam obj = (InstParam) stackHandler.pop(); // should
						// be
						// first
						pos.append(obj.getCurrentParamOffsetInCode()); // TODO:
						// check
						// whether
						// this
						// is
						// correct
						return;

					}
				}

			}

		}

		if (!primitive && k >= 0) // TODO: Need to check for getstatic and
		// getfield also
		{
			boolean prevAAload = isInstAAload(code[k]); // TODO !!! Can there be
			// any other option???
			boolean prevaload = false;
			if (prevAAload) {
				number++;
				k = k - 1;
				prevAAload = isInstAAload(code[k]);
				StringBuffer sb = new StringBuffer("");
				int posFound = isInstAload(k, code, sb);
				while (posFound == -1) {
					if (prevAAload)
						number++;
					k = k - 1;
					prevAAload = isInstAAload(code[k]);
					sb = new StringBuffer("");
					posFound = isInstAload(k, code, sb);
					// ///////////////////
					if (posFound == -1) {
						if (code[k] == JvmOpCodes.GETSTATIC) // TODO: Need to
						// check at all
						// such places
						// whether the
						// index is
						// start of
						// instruction
						{

							if (isThisInstrStart(behaviour
									.getInstructionStartPositions(), (k - 1))
									&& code[k - 1] == JvmOpCodes.POP) // TODO:
							// Possible
							// bug
							// with
							// pop2
							{
								StringBuffer s3 = new StringBuffer("");
								boolean bb = isPrevInstructionAload(k - 1,
										code, s3);
								if (bb) {
									index.append(-200);
									dim.append(0);
									pos.append(Integer.parseInt(s3.toString()));
									return;
								}
							} else {
								index.append(-200);
								dim.append(0);
								pos.append(k);
								return;
							}

						}
						if (code[k] == JvmOpCodes.GETFIELD) {
							StringBuffer s6 = new StringBuffer("");
							boolean aloadp = isPrevInstructionAload(k, code, s6);
							if (aloadp) {
								index.append(-200);
								dim.append(0);
								pos.append(Integer.parseInt(s6.toString()));
								return;
							}
						}

					}
					// ///////

				}

				index.append(Integer.parseInt(sb.toString()));
				dim.append(number);
				pos.append(posFound);
				return;

			}
		}
		if (primitive) {
			if (code[k] == JvmOpCodes.BALOAD || code[k] == JvmOpCodes.CALOAD
					|| code[k] == JvmOpCodes.DALOAD
					|| code[k] == JvmOpCodes.FALOAD
					|| code[k] == JvmOpCodes.LALOAD
					|| code[k] == JvmOpCodes.IALOAD
					|| code[k] == JvmOpCodes.SALOAD) {
				StringBuffer sb = new StringBuffer("");
				int posFound = isInstAload(k, code, sb);
				while (posFound == -1) {

					k = k - 1;
					sb = new StringBuffer("");
					posFound = isInstAload(k, code, sb);

				}
				index.append(Integer.parseInt(sb.toString()));
				dim.append(0);
				pos.append(posFound);
				return;

			}
		}
		if (primitive) {
			if (code[k] == JvmOpCodes.BIPUSH) {
				index.append(-200);
				dim.append(0);
				pos.append(k);
				return;
			}
		}

		if (primitive) {
			if (code[k] == JvmOpCodes.SIPUSH) {
				index.append(-200);
				dim.append(0);
				pos.append(k);
				return;
			}
		}
		StringBuffer sfb;
		if (!primitive) {
			sfb = new StringBuffer("");
			int y = isInstAload(k, code, sfb);
			if (y != -1) {
				index.append(Integer.parseInt(sfb.toString()));
				dim.append(0);
				pos.append(y);
				return;
			}
		}

		if (primitive) {
			sfb = new StringBuffer("");
			int y = isInstIloadInst(code, k, sfb);
			if (y != -1) {
				index.append(Integer.parseInt(sfb.toString()));
				dim.append(0);
				pos.append(y);
				return;
			}
		}

		if (type.equals("float") || type.equals("double")) {
			sfb = new StringBuffer("");
			int y = isInstFloadInst(code, k, sfb);
			if (y != -1) {
				index.append(Integer.parseInt(sfb.toString()));
				dim.append(0);
				pos.append(y);
				return;
			}
		}

		if (type.equals("double")) {
			sfb = new StringBuffer("");
			int y = isInstDloadInst(code, k, sfb);
			if (y != -1) {
				index.append(Integer.parseInt(sfb.toString()));
				dim.append(0);
				pos.append(y);
				return;
			}
		}

		if (type.equals("float") || type.equals("double")) {
			if (code[k] == JvmOpCodes.D2F)// || code[k]==JvmOpCodes.D2I ||
			// code[k]==JvmOpCodes.D2L)
			{

				int t = k - 1;
				StringBuffer strb = new StringBuffer("");
				int dload = isInstDloadInst(code, t, strb);
				if (dload != -1) {
					index.append(-100);
					dim.append(0);
					pos.append(dload);
					return;
				} else {
					strb = new StringBuffer("");
					int loadin = traceLoadInst(t, code, strb);
					if (loadin != -1) {
						index.append(-100);
						dim.append(0);
						pos.append(loadin);
						return;
					}

				}
				stackHandler = new Stack();
				InstParam first = new InstParam();
				first.setNumberOfParamsLeft(1);
				first.setCurrentParamOffsetInCode(-1);
				stackHandler.push(first);
				handleBasicPrimitiveLoadOp(code, k - 1);
				index.append(-100);
				dim.append(0);
				InstParam obj = (InstParam) stackHandler.pop(); // should be
				// first
				pos.append(obj.getCurrentParamOffsetInCode());
				return;

			}
		}
		if (primitive) {
			if (code[k] == JvmOpCodes.D2I)// || code[k]==JvmOpCodes.D2L)
			{

				int t = k - 1;
				StringBuffer strb = new StringBuffer("");
				int dload = isInstDloadInst(code, t, strb);
				if (dload != -1) {
					index.append(-100);
					dim.append(0);
					pos.append(dload);
					return;
				} else {
					strb = new StringBuffer("");
					int loadin = traceLoadInst(t, code, strb);
					if (loadin != -1) {
						index.append(-100);
						dim.append(0);
						pos.append(loadin);
						return;
					}

				}
				stackHandler = new Stack();
				InstParam first = new InstParam();
				first.setNumberOfParamsLeft(1);
				first.setCurrentParamOffsetInCode(-1);
				stackHandler.push(first);
				handleBasicPrimitiveLoadOp(code, k - 1);
				index.append(-100);
				dim.append(0);
				InstParam obj = (InstParam) stackHandler.pop(); // should be
				// first
				pos.append(obj.getCurrentParamOffsetInCode());
				return;
			}
		}
		if (type.equals("long") || type.equals("float")
				|| type.equals("double")) {
			if (code[k] == JvmOpCodes.D2L) {

				int t = k - 1;
				StringBuffer strb = new StringBuffer("");
				int dload = isInstDloadInst(code, t, strb);
				if (dload != -1) {
					index.append(-100);
					dim.append(0);
					pos.append(dload);
					return;
				} else {
					strb = new StringBuffer("");
					int loadin = traceLoadInst(t, code, strb);
					if (loadin != -1) {
						index.append(-100);
						dim.append(0);
						pos.append(loadin);
						return;
					}

				}
				stackHandler = new Stack();
				InstParam first = new InstParam();
				first.setNumberOfParamsLeft(1);
				first.setCurrentParamOffsetInCode(-1);
				stackHandler.push(first);
				handleBasicPrimitiveLoadOp(code, k - 1);
				index.append(-100);
				dim.append(0);
				InstParam obj = (InstParam) stackHandler.pop(); // should be
				// first
				pos.append(obj.getCurrentParamOffsetInCode());
				return;

			}
		}

		if (primitive) {
			if (code[k] == JvmOpCodes.I2B || code[k] == JvmOpCodes.I2C
					|| code[k] == JvmOpCodes.I2S)// ||
			// code[k]==JvmOpCodes.I2D
			// ||
			// code[k]==JvmOpCodes.I2F
			// ||
			// code[k]==JvmOpCodes.I2L
			// ||
			// code[k]==JvmOpCodes.I2S )
			{

				int t = k - 1;
				StringBuffer strb = new StringBuffer("");
				int Iload = isInstIloadInst(code, t, strb);
				if (Iload != -1) {
					index.append(-100);
					dim.append(0);
					pos.append(Iload);
					return;
				} else {
					strb = new StringBuffer("");
					int loadin = traceLoadInst(t, code, strb);
					if (loadin != -1) {
						index.append(-100);
						dim.append(0);
						pos.append(loadin);
						return;
					}

				}
				stackHandler = new Stack();
				InstParam first = new InstParam();
				first.setNumberOfParamsLeft(1);
				first.setCurrentParamOffsetInCode(-1);
				stackHandler.push(first);
				handleBasicPrimitiveLoadOp(code, k - 1);
				index.append(-100);
				dim.append(0);
				InstParam obj = (InstParam) stackHandler.pop(); // should be
				// first
				pos.append(obj.getCurrentParamOffsetInCode());
				return;

			}
		}

		if (type.equals("double")) {
			if (code[k] == JvmOpCodes.I2D)// || code[k]==JvmOpCodes.I2F ||
			// code[k]==JvmOpCodes.I2L ||
			// code[k]==JvmOpCodes.I2S )
			{

				int t = k - 1;
				StringBuffer strb = new StringBuffer("");
				int Iload = isInstIloadInst(code, t, strb);
				if (Iload != -1) {
					index.append(-100);
					dim.append(0);
					pos.append(Iload);
					return;
				} else {
					strb = new StringBuffer("");
					int loadin = traceLoadInst(t, code, strb);
					if (loadin != -1) {
						index.append(-100);
						dim.append(0);
						pos.append(loadin);
						return;
					}

				}
				stackHandler = new Stack();
				InstParam first = new InstParam();
				first.setNumberOfParamsLeft(1);
				first.setCurrentParamOffsetInCode(-1);
				stackHandler.push(first);
				handleBasicPrimitiveLoadOp(code, k - 1);
				index.append(-100);
				dim.append(0);
				InstParam obj = (InstParam) stackHandler.pop(); // should be
				// first
				pos.append(obj.getCurrentParamOffsetInCode());
				return;

			}

		}

		if (type.equals("double") || type.equals("float")) {
			if (code[k] == JvmOpCodes.I2F)// || code[k]==JvmOpCodes.I2L ||
			// code[k]==JvmOpCodes.I2S )
			{

				int t = k - 1;
				StringBuffer strb = new StringBuffer("");
				int Iload = isInstIloadInst(code, t, strb);
				if (Iload != -1) {
					index.append(-100);
					dim.append(0);
					pos.append(Iload);
					return;
				} else {
					strb = new StringBuffer("");
					int loadin = traceLoadInst(t, code, strb);
					if (loadin != -1) {
						index.append(-100);
						dim.append(0);
						pos.append(loadin);
						return;
					}

				}
				stackHandler = new Stack();
				InstParam first = new InstParam();
				first.setNumberOfParamsLeft(1);
				first.setCurrentParamOffsetInCode(-1);
				stackHandler.push(first);
				handleBasicPrimitiveLoadOp(code, k - 1);
				index.append(-100);
				dim.append(0);
				InstParam obj = (InstParam) stackHandler.pop(); // should be
				// first
				pos.append(obj.getCurrentParamOffsetInCode());
				return;
			}
		}

		if (type.equals("double") || type.equals("float")
				|| type.equals("long")) {
			if (code[k] == JvmOpCodes.I2L) {

				int t = k - 1;
				StringBuffer strb = new StringBuffer("");
				int Iload = isInstIloadInst(code, t, strb);
				if (Iload != -1) {
					index.append(-100);
					dim.append(0);
					pos.append(Iload);
					return;
				} else {
					strb = new StringBuffer("");
					int loadin = traceLoadInst(t, code, strb);
					if (loadin != -1) {
						index.append(-100);
						dim.append(0);
						pos.append(loadin);
						return;
					}

				}
				stackHandler = new Stack();
				InstParam first = new InstParam();
				first.setNumberOfParamsLeft(1);
				first.setCurrentParamOffsetInCode(-1);
				stackHandler.push(first);
				handleBasicPrimitiveLoadOp(code, k - 1);
				index.append(-100);
				dim.append(0);
				InstParam obj = (InstParam) stackHandler.pop(); // should be
				// first
				pos.append(obj.getCurrentParamOffsetInCode());
				return;
			}
		}

		if (type.equals("double")) {
			if (code[k] == JvmOpCodes.F2D)// || code[k]==JvmOpCodes.F2I ||
			// code[k]==JvmOpCodes.F2L)
			{

				int t = k - 1;
				StringBuffer strb = new StringBuffer("");
				int Fload = isInstFloadInst(code, t, strb);
				if (Fload != -1) {
					index.append(-100);
					dim.append(0);
					pos.append(Fload);
					return;
				} else {
					strb = new StringBuffer("");
					int loadin = traceLoadInst(t, code, strb);
					if (loadin != -1) {
						index.append(-100);
						dim.append(0);
						pos.append(loadin);
						return;
					}

				}
				stackHandler = new Stack();
				InstParam first = new InstParam();
				first.setNumberOfParamsLeft(1);
				first.setCurrentParamOffsetInCode(-1);
				stackHandler.push(first);
				handleBasicPrimitiveLoadOp(code, k - 1);
				index.append(-100);
				dim.append(0);
				InstParam obj = (InstParam) stackHandler.pop(); // should be
				// first
				pos.append(obj.getCurrentParamOffsetInCode());
				return;
			}
		}

		if (type.equals("double") || type.equals("float")
				|| type.equals("long")) {
			if (code[k] == JvmOpCodes.F2L)// || code[k]==JvmOpCodes.F2I ||
			// code[k]==JvmOpCodes.F2L)
			{

				int t = k - 1;
				StringBuffer strb = new StringBuffer("");
				int Fload = isInstFloadInst(code, t, strb);
				if (Fload != -1) {
					index.append(-100);
					dim.append(0);
					pos.append(Fload);
					return;
				} else {
					strb = new StringBuffer("");
					int loadin = traceLoadInst(t, code, strb);
					if (loadin != -1) {
						index.append(-100);
						dim.append(0);
						pos.append(loadin);
						return;
					}

				}
				stackHandler = new Stack();
				InstParam first = new InstParam();
				first.setNumberOfParamsLeft(1);
				first.setCurrentParamOffsetInCode(-1);
				stackHandler.push(first);
				handleBasicPrimitiveLoadOp(code, k - 1);
				index.append(-100);
				dim.append(0);
				InstParam obj = (InstParam) stackHandler.pop(); // should be
				// first
				pos.append(obj.getCurrentParamOffsetInCode());
				return;
			}
		}
		if (primitive) {
			if (code[k] == JvmOpCodes.F2I) {

				int t = k - 1;
				StringBuffer strb = new StringBuffer("");
				int Fload = isInstFloadInst(code, t, strb);
				if (Fload != -1) {
					index.append(-100);
					dim.append(0);
					pos.append(Fload);
					return;
				} else {
					strb = new StringBuffer("");
					int loadin = traceLoadInst(t, code, strb);
					if (loadin != -1) {
						index.append(-100);
						dim.append(0);
						pos.append(loadin);
						return;
					}

				}
				stackHandler = new Stack();
				InstParam first = new InstParam();
				first.setNumberOfParamsLeft(1);
				first.setCurrentParamOffsetInCode(-1);
				stackHandler.push(first);
				handleBasicPrimitiveLoadOp(code, k - 1);
				index.append(-100);
				dim.append(0);
				InstParam obj = (InstParam) stackHandler.pop(); // should be
				// first
				pos.append(obj.getCurrentParamOffsetInCode());
				return;
			}
		}

		if (type.equals("double")) {
			if (code[k] == JvmOpCodes.L2D)// || code[k]==JvmOpCodes.L2F ||
			// code[k]==JvmOpCodes.L2I)
			{

				int t = k - 1;
				StringBuffer strb = new StringBuffer("");
				int Fload = isInstLloadInst(code, t, strb);
				if (Fload != -1) {
					index.append(-100);
					dim.append(0);
					pos.append(Fload);
					return;
				} else {
					strb = new StringBuffer("");
					int loadin = traceLoadInst(t, code, strb);
					if (loadin != -1) {
						index.append(-100);
						dim.append(0);
						pos.append(loadin);
						return;
					}

				}
				stackHandler = new Stack();
				InstParam first = new InstParam();
				first.setNumberOfParamsLeft(1);
				first.setCurrentParamOffsetInCode(-1);
				stackHandler.push(first);
				handleBasicPrimitiveLoadOp(code, k - 1);
				index.append(-100);
				dim.append(0);
				InstParam obj = (InstParam) stackHandler.pop(); // should be
				// first
				pos.append(obj.getCurrentParamOffsetInCode());
				return;

			}
		}

		if (type.equals("double") || type.equals("float")) {
			if (code[k] == JvmOpCodes.L2F)// || code[k]==JvmOpCodes.L2I)
			{

				int t = k - 1;
				StringBuffer strb = new StringBuffer("");
				int Fload = isInstLloadInst(code, t, strb);
				if (Fload != -1) {
					index.append(-100);
					dim.append(0);
					pos.append(Fload);
					return;
				} else {
					strb = new StringBuffer("");
					int loadin = traceLoadInst(t, code, strb);
					if (loadin != -1) {
						index.append(-100);
						dim.append(0);
						pos.append(loadin);
						return;
					}

				}
				stackHandler = new Stack();
				InstParam first = new InstParam();
				first.setNumberOfParamsLeft(1);
				first.setCurrentParamOffsetInCode(-1);
				stackHandler.push(first);
				handleBasicPrimitiveLoadOp(code, k - 1);
				index.append(-100);
				dim.append(0);
				InstParam obj = (InstParam) stackHandler.pop(); // should be
				// first
				pos.append(obj.getCurrentParamOffsetInCode());
				return;

			}
		}

		if (primitive) {
			if (code[k] == JvmOpCodes.L2I) {

				int t = k - 1;
				StringBuffer strb = new StringBuffer("");
				int Fload = isInstLloadInst(code, t, strb);
				if (Fload != -1) {
					index.append(-100);
					dim.append(0);
					pos.append(Fload);
					return;
				} else {
					strb = new StringBuffer("");
					int loadin = traceLoadInst(t, code, strb);
					if (loadin != -1) {
						index.append(-100);
						dim.append(0);
						pos.append(loadin);
						return;
					}

				}
				stackHandler = new Stack();
				InstParam first = new InstParam();
				first.setNumberOfParamsLeft(1);
				first.setCurrentParamOffsetInCode(-1);
				stackHandler.push(first);
				handleBasicPrimitiveLoadOp(code, k - 1);
				index.append(-100);
				dim.append(0);
				InstParam obj = (InstParam) stackHandler.pop(); // should be
				// first
				pos.append(obj.getCurrentParamOffsetInCode());
				return;

			}
		}
		int tmp = k;
		if (tmp >= 0 && code[tmp] == JvmOpCodes.LDC) {
			index.append(-100);
			dim.append(0);
			pos.append(tmp);
			return;
		}

		if (tmp >= 0 && code[tmp] == JvmOpCodes.LDC_W)// ||
		// code[tmp]==JvmOpCodes.LDC2_W)
		{
			index.append(-100);
			dim.append(0);
			pos.append(tmp);
			return;
		}
		if (type.equals("long") || type.equals("float")
				|| type.equals("double")) {
			tmp = k;
			if (tmp >= 0 && code[tmp] == JvmOpCodes.LDC2_W) {
				index.append(-100);
				dim.append(0);
				pos.append(tmp);
				return;
			}
		}

		if (!primitive && k >= 0) {
			if (code[k] == JvmOpCodes.ACONST_NULL) {
				index.append(-100);
				dim.append(0);
				pos.append(k);
				return;
			}
		}
		if (primitive && k >= 0) {
			if (isInstIConstInst(code, k) != -1) //
			{
				index.append(-100);
				dim.append(0);
				pos.append(k);
				return;
			}
		}
		if (type.equals("float") || type.equals("double")) {
			if (k >= 0 && isInstFConstInst(code, k) != -1) {
				index.append(-100);
				dim.append(0);
				pos.append(k);
				return;
			}
		}

		if (type.equals("double")) {
			if (k >= 0 && isInstdConstInst(code, k) != -1) {
				index.append(-100);
				dim.append(0);
				pos.append(k);
				return;
			}
		}
		if (type.equals("float") || type.equals("double")
				|| type.equals("long")) {
			if (k >= 0 && isInstLConstInst(code, k) != -1) {
				index.append(-100);
				dim.append(0);
				pos.append(k);
				return;
			}
		}

		if (!primitive) {

			if (k >= 0 && code[k] == JvmOpCodes.MULTIANEWARRAY) {
				stackHandler = new Stack();
				InstParam first = new InstParam();
				first.setNumberOfParamsLeft(code[k + 3]);
				first.setCurrentParamOffsetInCode(-1);
				stackHandler.push(first);
				handleBasicPrimitiveLoadOp(code, k - 1);
				index.append(-100);
				dim.append(0);
				InstParam obj = (InstParam) stackHandler.pop(); // should be
				// first
				pos.append(obj.getCurrentParamOffsetInCode()); // TODO: check
				// whether this
				// is correct
				return;
			}
			if (k >= 0
					&& (code[k] == JvmOpCodes.ANEWARRAY || code[k] == JvmOpCodes.NEWARRAY)) {

				stackHandler = new Stack();
				InstParam first = new InstParam();
				first.setNumberOfParamsLeft(1);
				first.setCurrentParamOffsetInCode(-1);
				stackHandler.push(first);
				handleBasicPrimitiveLoadOp(code, k - 1);
				index.append(-100);
				dim.append(0);
				InstParam obj = (InstParam) stackHandler.pop(); // should be
				// first
				pos.append(obj.getCurrentParamOffsetInCode()); // TODO: check
				// whether this
				// is correct
				return;

			}

		}

		if (primitive) {

			if (k >= 0 && (code[k] == JvmOpCodes.INSTANCEOF)) {
				if (isThisInstrStart(behaviour.getInstructionStartPositions(),
						(k - 2))
						&& code[k - 2] == JvmOpCodes.NEWARRAY) {

					stackHandler = new Stack();
					InstParam first = new InstParam();
					first.setNumberOfParamsLeft(1);
					first.setCurrentParamOffsetInCode(-1);
					stackHandler.push(first);
					handleBasicPrimitiveLoadOp(code, k - 3);
					index.append(-100);
					dim.append(0);
					InstParam obj = (InstParam) stackHandler.pop(); // should be
					// first
					pos.append(obj.getCurrentParamOffsetInCode()); // TODO:
					// check
					// whether
					// this is
					// correct
					return;

				}
				StringBuffer s8 = new StringBuffer("");
				boolean aloadp = isPrevInstructionAload(k, code, s8);
				if (aloadp) {

					index.append(-100);
					dim.append(0);
					pos.append(Integer.parseInt(s8.toString()));
					return;
				}
				if (isThisInstrStart(behaviour.getInstructionStartPositions(),
						(k - 3))
						&& code[k - 3] == JvmOpCodes.ANEWARRAY) {

					stackHandler = new Stack();
					InstParam first = new InstParam();
					first.setNumberOfParamsLeft(1);
					first.setCurrentParamOffsetInCode(-1);
					stackHandler.push(first);
					handleBasicPrimitiveLoadOp(code, k - 4);
					index.append(-100);
					dim.append(0);
					InstParam obj = (InstParam) stackHandler.pop(); // should be
					// first
					pos.append(obj.getCurrentParamOffsetInCode()); // TODO:
					// check
					// whether
					// this is
					// correct
					return;
				}
				if (isThisInstrStart(behaviour.getInstructionStartPositions(),
						(k - 4))
						&& code[k - 4] == JvmOpCodes.MULTIANEWARRAY) {
					stackHandler = new Stack();
					InstParam first = new InstParam();
					first.setNumberOfParamsLeft(code[k - 4 + 3]);
					first.setCurrentParamOffsetInCode(-1);
					stackHandler.push(first);
					handleBasicPrimitiveLoadOp(code, k - 5);
					index.append(-100);
					dim.append(0);
					InstParam obj = (InstParam) stackHandler.pop(); // should be
					// first
					pos.append(obj.getCurrentParamOffsetInCode()); // TODO:
					// check
					// whether
					// this is
					// correct
					return;
				}

			}

		}

		if (!primitive) {
			if (k >= 0 && code[k] == JvmOpCodes.CHECKCAST) {
				if (isThisInstrStart(behaviour.getInstructionStartPositions(),
						(k - 4))
						&& code[k - 4] == JvmOpCodes.MULTIANEWARRAY) {
					stackHandler = new Stack();
					InstParam first = new InstParam();
					first.setNumberOfParamsLeft(code[k - 4 + 3]);
					first.setCurrentParamOffsetInCode(-1);
					stackHandler.push(first);
					handleBasicPrimitiveLoadOp(code, k - 5);
					index.append(-100);
					dim.append(0);
					InstParam obj = (InstParam) stackHandler.pop(); // should be
					// first
					pos.append(obj.getCurrentParamOffsetInCode()); // TODO:
					// check
					// whether
					// this is
					// correct
					return;
				} else if (isThisInstrStart(behaviour
						.getInstructionStartPositions(), (k - 3))
						&& code[k - 3] == JvmOpCodes.ANEWARRAY) {

					stackHandler = new Stack();
					InstParam first = new InstParam();
					first.setNumberOfParamsLeft(1);
					first.setCurrentParamOffsetInCode(-1);
					stackHandler.push(first);
					handleBasicPrimitiveLoadOp(code, k - 4);
					index.append(-100);
					dim.append(0);
					InstParam obj = (InstParam) stackHandler.pop(); // should be
					// first
					pos.append(obj.getCurrentParamOffsetInCode()); // TODO:
					// check
					// whether
					// this is
					// correct
					return;

				} else if (isThisInstrStart(behaviour
						.getInstructionStartPositions(), (k - 2))
						&& code[k - 2] == JvmOpCodes.NEWARRAY) {

					stackHandler = new Stack();
					InstParam first = new InstParam();
					first.setNumberOfParamsLeft(1);
					first.setCurrentParamOffsetInCode(-1);
					stackHandler.push(first);
					handleBasicPrimitiveLoadOp(code, k - 3);
					index.append(-100);
					dim.append(0);
					InstParam obj = (InstParam) stackHandler.pop(); // should be
					// first
					pos.append(obj.getCurrentParamOffsetInCode()); // TODO:
					// check
					// whether
					// this is
					// correct
					return;

				} else {
					StringBuffer s5 = new StringBuffer("");
					boolean aload = isPrevInstructionAload(k, code, s5);
					if (aload) {
						index.append(-100);
						dim.append(0);
						pos.append(Integer.parseInt(s5.toString()));
						return;
					}

				}

				// Check for other object refs....
				if ((k - 1) >= 0) {
					boolean aaloadp = isInstAAload(code[k - 1]); // BUG check
					// for
					// getstatic
					// alos.:TODO
					if (aaloadp) {
						StringBuffer s6 = new StringBuffer("");
						int in = k - 1;
						int posFound = isInstAload(in, code, s6);
						while (posFound == -1) {
							in = in - 1;
							posFound = isInstAload(in, code, s6);
							if (posFound == -1) {
								if (code[in] == JvmOpCodes.GETSTATIC) // TODO:
								// Need
								// to
								// check
								// at
								// all
								// such
								// places
								// whether
								// the
								// index
								// is
								// start
								// of
								// instruction
								{

									if (isThisInstrStart(behaviour
											.getInstructionStartPositions(),
											(in - 1))
											&& code[in - 1] == JvmOpCodes.POP) // TODO:
									// Possible
									// bug
									// with
									// pop2
									{
										StringBuffer s3 = new StringBuffer("");
										boolean bb = isPrevInstructionAload(
												in - 1, code, s3);
										if (bb) {
											index.append(-200);
											dim.append(0);
											pos.append(Integer.parseInt(s3
													.toString()));
											return;
										}
									} else {
										index.append(-200);
										dim.append(0);
										pos.append(in);
										return;
									}

								}
								if (code[in] == JvmOpCodes.GETFIELD) {
									s6 = new StringBuffer("");
									boolean aloadp = isPrevInstructionAload(in,
											code, s6);
									if (aloadp) {
										index.append(-200);
										dim.append(0);
										pos.append(Integer.parseInt(s6
												.toString()));
										return;
									}
								}

							}

						}
						index.append(-200);
						dim.append(0);
						pos.append(posFound);
						return;

					}
				} // here
				if ((k - 3) >= 0) {
					boolean getfield = (code[k - 3] == JvmOpCodes.GETFIELD);
					if (getfield) {
						StringBuffer s6 = new StringBuffer("");
						boolean aloadp = isPrevInstructionAload(k - 3, code, s6);
						if (aloadp) {
							index.append(-200);
							dim.append(0);
							pos.append(Integer.parseInt(s6.toString()));
							return;
						}
					}
				}
				if ((k - 3) >= 0) {
					boolean getS = (code[k - 3] == JvmOpCodes.GETSTATIC);
					if (getS) {
						if (code[k - 4] == JvmOpCodes.POP) // TODO: Possible
						// bug with pop2
						{
							StringBuffer s3 = new StringBuffer("");
							boolean bb = isPrevInstructionAload(k - 4, code, s3);
							if (bb) {
								index.append(-200);
								dim.append(0);
								pos.append(Integer.parseInt(s3.toString()));
								return;
							}
						}
						index.append(-200);
						dim.append(0);
						pos.append(k - 3);
						return;

					}
				}

				// if(isNextInstructionAnyInvoke(code))
				Integer isinvokestart = (Integer) GlobalVariableStore
						.getInvokeStartEnd().get(new Integer(k - 1));
				if (isinvokestart != null) {

					Integer refpos = (Integer) invokeinstrefpos
							.get(new Integer(isinvokestart.intValue()));
					if (refpos != null) {
						index.append(-200);
						dim.append(0);
						pos.append(refpos.intValue());
						return;
					}

				}

			}

		}

		if (primitive) {
			StringBuffer sb = new StringBuffer("");
			boolean p = isInstAnyBasicPrimitiveOperation(code, k, sb);

			if (p) {
				// belurs:

				// Here need to call a function which will handle tyes like
				// imul,lmul,ldiv etc
				// This function needs to be recursive. Handle this function
				// with care
				// In this case the code will not set the index position because
				// any complex sequence of jvm blocks can be present for this
				// instruction itself. so for time being , what is important is
				// that
				// this function should return load position for this jvm instr
				// so that
				// for the next parameter the code will know from where to
				// continue.

				stackHandler = new Stack();
				InstParam first = new InstParam();
				first.setNumberOfParamsLeft(Integer.parseInt(sb.toString()));
				first.setCurrentParamOffsetInCode(-1);
				stackHandler.push(first);
				handleBasicPrimitiveLoadOp(code, k - 1);
				index.append(-100);
				dim.append(0);
				InstParam obj = (InstParam) stackHandler.pop(); // should be
				// first
				pos.append(obj.getCurrentParamOffsetInCode()); // TODO: check
				// whether this
				// is correct
				return;

			}

		}

		// This 2 should be last.
		// Any new thing should come before this 2
		if (k >= 0 && code[k] == JvmOpCodes.GETFIELD) {
			StringBuffer sb1 = new StringBuffer("");

			int pos2 = isInstAload(k - 1, code, sb1);
			if (pos2 != -1) {
				index.append(Integer.parseInt(sb1.toString()));
				dim.append(0);
				pos.append(pos2);
				return;
			}
		}
		if (k >= 0 && code[k] == JvmOpCodes.GETSTATIC) // POSSIBLE BUG: todo
		{

			index.append(-100);
			dim.append(0);
			pos.append(k);
			return;

		}

		// }

	}

	private int isInstIloadInst(byte[] code, int s, StringBuffer sb2) {
		if (isThisInstrStart(behaviour.getInstructionStartPositions(), s)) {
			switch (code[(s)]) {
			case JvmOpCodes.ILOAD_0:
				sb2.append(0);
				return s;

			case JvmOpCodes.ILOAD_1:

				sb2.append(1);
				return s;

			case JvmOpCodes.ILOAD_2:

				sb2.append(2);
				return s;

			case JvmOpCodes.ILOAD_3:

				sb2.append(3);
				return s;

			case JvmOpCodes.ILOAD:
				sb2.append(code[s + 1]);
				return s;

			}
		}
		int temp = s - 1;
		if (temp >= 0
				&& isThisInstrStart(behaviour.getInstructionStartPositions(),
						temp) && code[temp] == JvmOpCodes.ILOAD) {
			sb2.append(code[s]);
			return temp;

		}

		return -1;

	}

	private int isInstFloadInst(byte[] code, int s, StringBuffer sb2) {
		if (isThisInstrStart(behaviour.getInstructionStartPositions(), s)) {
			switch (code[(s)]) {
			case JvmOpCodes.FLOAD_0:
				sb2.append(0);
				return s;

			case JvmOpCodes.FLOAD_1:

				sb2.append(1);
				return s;

			case JvmOpCodes.FLOAD_2:

				sb2.append(2);
				return s;

			case JvmOpCodes.FLOAD_3:

				sb2.append(3);
				return s;

			case JvmOpCodes.FLOAD:

				sb2.append(code[(s + 1)]);
				return s;

			}
		}

		int temp = s - 1;
		if (isThisInstrStart(behaviour.getInstructionStartPositions(), temp)
				&& code[temp] == JvmOpCodes.FLOAD) {
			sb2.append(code[s]);
			return temp;

		}

		return -1;

	}

	private int isInstDloadInst(byte[] code, int s, StringBuffer sb2) {
		if (isThisInstrStart(behaviour.getInstructionStartPositions(), s)) {

			switch (code[(s)]) {
			case JvmOpCodes.DLOAD_0:
				sb2.append(0);
				return s;

			case JvmOpCodes.DLOAD_1:

				sb2.append(1);
				return s;

			case JvmOpCodes.DLOAD_2:

				sb2.append(2);
				return s;

			case JvmOpCodes.DLOAD_3:

				sb2.append(3);
				return s;

			case JvmOpCodes.DLOAD:
				sb2.append(code[s + 1]);
				return s;

			}
		}
		int temp = s - 1;
		if (isThisInstrStart(behaviour.getInstructionStartPositions(), temp)
				&& code[temp] == JvmOpCodes.DLOAD) {
			sb2.append(code[s]);
			return temp;

		}

		return -1;

	}

	private int isInstLloadInst(byte[] code, int s, StringBuffer sb2) {
		if (isThisInstrStart(behaviour.getInstructionStartPositions(), s)) {
			switch (code[(s)]) {
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

				/*
				 * case JvmOpCodes.ILOAD: sb2.append(code[s+1]); return s;
				 */

			}
		}
		int temp = s - 1;
		if (isThisInstrStart(behaviour.getInstructionStartPositions(), temp)) {
			if (code[temp] == JvmOpCodes.LLOAD) {
				sb2.append(code[s]);
				return temp;

			}
		}

		return -1;

	}

	private int isInstIConstInst(byte[] code, int k) {
		if (isThisInstrStart(behaviour.getInstructionStartPositions(), k)) {
			switch (code[k]) {
			case JvmOpCodes.ICONST_0:
			case JvmOpCodes.ICONST_1:
			case JvmOpCodes.ICONST_2:
			case JvmOpCodes.ICONST_3:
			case JvmOpCodes.ICONST_M1:
			case JvmOpCodes.ICONST_4:
			case JvmOpCodes.ICONST_5:
				return k;

			}
		}
		return -1;
	}

	private int isInstLConstInst(byte[] code, int k) {
		if (isThisInstrStart(behaviour.getInstructionStartPositions(), k)) {
			switch (code[k]) {
			case JvmOpCodes.LCONST_0:
			case JvmOpCodes.LCONST_1:
				return k;
			}
		}
		return -1;

	}

	private int isInstFConstInst(byte[] code, int k) {
		if (isThisInstrStart(behaviour.getInstructionStartPositions(), k)) {
			switch (code[k]) {
			case JvmOpCodes.FCONST_0:
			case JvmOpCodes.FCONST_1:
			case JvmOpCodes.FCONST_2:
				return k;
			}
		}
		return -1;
	}

	private int isInstdConstInst(byte[] code, int k) {
		if (isThisInstrStart(behaviour.getInstructionStartPositions(), k)) {
			switch (code[k]) {
			case JvmOpCodes.DCONST_0:
			case JvmOpCodes.DCONST_1:
				return k;
			}
		}
		return -1;
	}

	private boolean isPrimitive(java.lang.String type) {
		if (type.equals("int") || type.equals("short") || type.equals("char")
				|| type.equals("byte") || type.equals("long")
				|| type.equals("float") || type.equals("long")
				|| type.equals("double") || type.equals("boolean"))
			return true;
		else
			return false;
	}

	private int traceLoadInst(int t, byte[] code, StringBuffer strb) {
		int pos = -1;
		int k = t;

		int getFieldPos = k - 2;

		int invokebegin = -1;
		if (GlobalVariableStore.getInvokeStartEnd() != null) {
			Integer in = new Integer(k);
			Integer invs = (Integer) GlobalVariableStore.getInvokeStartEnd()
					.get(in);
			if (invs != null) {
				invokebegin = invs.intValue();
			}
		}

		if (isThisInstrStart(behaviour.getInstructionStartPositions(), k)
				&& code[k] == JvmOpCodes.BALOAD || code[k] == JvmOpCodes.CALOAD
				|| code[k] == JvmOpCodes.DALOAD || code[k] == JvmOpCodes.FALOAD
				|| code[k] == JvmOpCodes.LALOAD || code[k] == JvmOpCodes.IALOAD
				|| code[k] == JvmOpCodes.SALOAD) {

			int posFound = isInstAload(k, code, strb);
			while (posFound == -1) {

				k = k - 1;
				strb = new StringBuffer("");
				posFound = isInstAload(k, code, strb);

			}
			return posFound;

		} else if (invokebegin != -1
				&& isThisInstrStart(behaviour.getInstructionStartPositions(),
						invokebegin)) {
			if (isNextInstructionAnyInvoke(code[invokebegin], new StringBuffer(
					""))) {
				if (invokeinstrefpos != null) {
					Integer in = (Integer) invokeinstrefpos.get(new Integer(
							invokebegin));
					if (in != null) {
						int start = (in).intValue();
						return start;
					}

				}
			}
		}

		else if (code[getFieldPos] == JvmOpCodes.GETFIELD
				&& isThisInstrStart(behaviour.getInstructionStartPositions(),
						getFieldPos)) {
			StringBuffer sb = new StringBuffer("");
			boolean ok = isPrevInstructionAload(getFieldPos, code, sb);
			if (ok) {
				int ref = Integer.parseInt(sb.toString());
				return ref;
			}

		} else if (isThisInstrStart(behaviour.getInstructionStartPositions(),
				getFieldPos)
				&& code[getFieldPos] == JvmOpCodes.GETSTATIC) {

			if (code[getFieldPos - 1] == JvmOpCodes.POP) // TODO: Possible
			// bug with pop2
			{
				StringBuffer s3 = new StringBuffer("");
				boolean bb = isPrevInstructionAload(getFieldPos - 1, code, s3);
				if (bb) {
					return Integer.parseInt(s3.toString());
				}
			}

			return getFieldPos;
		} else {

			// NOTE: The name is slightly misleading....what we want here is
			// actually the previous inst...
			// Using this as it serves the pupose
			if (isNextInstructionConversionInst(code[k])) {

				int g = k - 1;
				int invokened = -1;
				while (isNextInstructionConversionInst(code[g])) {
					g = g - 1;
				}
				invokebegin = -1;
				if (GlobalVariableStore.getInvokeStartEnd() != null) {
					Integer in = new Integer(g);
					Integer invs = (Integer) GlobalVariableStore
							.getInvokeStartEnd().get(in);
					if (invs != null) {
						invokebegin = invs.intValue();
					}
				}
				StringBuffer sbf = new StringBuffer("");
				if (isPrevInstPrimitiveLoad(code, (g + 1), sbf)) {
					pos = Integer.parseInt(sbf.toString());
					return pos;
				} else if (invokebegin != -1) {
					if (isNextInstructionAnyInvoke(code[invokebegin],
							new StringBuffer(""))) {
						if (invokeinstrefpos != null) {
							Integer in = (Integer) invokeinstrefpos
									.get(new Integer(invokebegin));
							if (in != null) {
								int start = (in).intValue();
								return start;
							}

						}
					}
				} else {
					k = g;
					if (isThisInstrStart(behaviour
							.getInstructionStartPositions(), k)
							&& (code[k] == JvmOpCodes.BALOAD
									|| code[k] == JvmOpCodes.CALOAD
									|| code[k] == JvmOpCodes.DALOAD
									|| code[k] == JvmOpCodes.FALOAD
									|| code[k] == JvmOpCodes.LALOAD
									|| code[k] == JvmOpCodes.IALOAD || code[k] == JvmOpCodes.SALOAD)) {

						int posFound = isInstAload(k, code, strb);
						while (posFound == -1) {

							k = k - 1;
							strb = new StringBuffer("");
							posFound = isInstAload(k, code, strb);

						}
						return posFound;

					}
				}

			}

		}

		return pos;
	}

	private int findWhereThisInvokeEnds(int s, byte[] code) {
		int temp = s;
		if (code[s] == JvmOpCodes.INVOKEINTERFACE) {
			temp = temp + 4;
		} else {
			temp += 2;
		}
		return temp;

	}

	private boolean checkForAssociatedGetField(ArrayList getField, int s,
			StringBuffer str, byte[] code) {
		boolean ret = false;

		HashMap map = ConsoleLauncher.getInstructionMap();
		if (getField == null || getField.size() == 0)
			return ret;
		else {
			for (int z = getField.size() - 1; z >= 0; z--) {
				Integer in = (Integer) getField.get(z);
				int Iin = in.intValue();
				if (s > Iin) // REQD getField
				{
					int temp1 = Iin;
					int temp2 = s;
					if (map != null) {
						Integer skip = (Integer) map.get(new Integer(temp1));
						if (skip != null) {

							int iskip = skip.intValue();
							int next = temp1 + iskip + 1;
							if (next < code.length)
								temp1 = next;
						}

					}

					for (int x = temp1; x < temp2; x++) {
						int inst = code[x];
						switch (inst) {
						case JvmOpCodes.INVOKEVIRTUAL:
						case JvmOpCodes.INVOKESTATIC:
						case JvmOpCodes.INVOKESPECIAL:
						case JvmOpCodes.INVOKEINTERFACE:
							return false;

						default:
							ret = true;
						}
					}

					if (ret) {
						str.append(Iin);
						return ret;

					}
				}

			}

		}
		return ret;
	}

	private boolean checkForAssociatedGetStatic(ArrayList getStatic, int s,
			StringBuffer str, byte[] code) {

		boolean ret = false;
		HashMap map = ConsoleLauncher.getInstructionMap();
		if (getStatic == null || getStatic.size() == 0)
			return ret;
		else {
			for (int z = 0; z < getStatic.size(); z++) {
				Integer in = (Integer) getStatic.get(z);
				int Iin = in.intValue();
				if (s > Iin) // REQD getStatic
				{
					int temp1 = Iin;
					int temp2 = s;
					if (map != null) {
						Integer skip = (Integer) map.get(new Integer(temp1));
						if (skip != null) {

							int iskip = skip.intValue();
							int next = temp1 + iskip + 1;
							if (next < code.length)
								temp1 = next;
						}

					}

					for (int x = temp1; x < temp2; x++) {
						if (isThisInstrStart(behaviour
								.getInstructionStartPositions(), x)) {
							int inst = code[x];
							switch (inst) {
							case JvmOpCodes.INVOKEVIRTUAL:
							case JvmOpCodes.INVOKESTATIC:
							case JvmOpCodes.INVOKESPECIAL:
							case JvmOpCodes.INVOKEINTERFACE:
								return false;

							default:
								ret = true;
							}
						}
					}

					if (ret) {
						str.append(Iin);
						return ret;
					}
				}

			}

		}
		return ret;

	}

	private boolean checkForAssociatedInvokeSpecial(Hashtable invokespecialpos,
			int s, StringBuffer str, byte[] code) {

		boolean ret = false;
		HashMap map = ConsoleLauncher.getInstructionMap();
		if (invokespecialpos != null && invokespecialpos.size() > 0) {
			Set set = invokespecialpos.keySet();
			Integer[] karr = (Integer[]) set.toArray(new Integer[set.size()]);
			Arrays.sort(karr);
			for (int z = 0; z < karr.length; z++) {
				Integer in = (Integer) karr[z];
				int Iin = in.intValue();
				if (s > Iin) // REQD invokespec
				{
					int temp1 = Iin;
					int temp2 = s;
					if (map != null) {
						Integer skip = (Integer) map.get(new Integer(temp1));
						if (skip != null) {

							int iskip = skip.intValue();
							int next = temp1 + iskip + 1;
							if (next < code.length)
								temp1 = next;
						}

					}

					for (int x = temp1; x < temp2; x++) {
						if (isThisInstrStart(behaviour
								.getInstructionStartPositions(), x)) {
							int inst = code[x];
							switch (inst) {
							case JvmOpCodes.INVOKEVIRTUAL:
							case JvmOpCodes.INVOKESTATIC:
							case JvmOpCodes.INVOKESPECIAL:
							case JvmOpCodes.INVOKEINTERFACE:
								return false;

							default:
								ret = true;
							}
						}
					}

					if (ret) {
						str.append(invokespecialpos.get(new Integer(Iin)));
						return ret;
					}
				}

			}

		}
		return false;

	}

	private int getObjectRefForGetField(int iS, byte[] code) {
		StringBuffer sb = new StringBuffer("");
		boolean ok = isPrevInstructionAload(iS, code, sb);
		if (ok) {
			int ref = Integer.parseInt(sb.toString());
			return ref;
		}
		// System.out.println("ERROR in getObjectRefForGetField "+iS);
		return iS;

	}

	private boolean isThisInstrStart(ArrayList list, int pos) {
		boolean ok = false;
		if (list == null)
			throw new NullPointerException("Starts pos is null in disassembler");
		if (list != null) {
			for (int k = 0; k < list.size(); k++) {
				Integer in = (Integer) list.get(k);
				if (in != null) {
					int i = in.intValue();
					if (i == pos)
						return !ok;
				}
			}
		}
		return ok;

	}

	// s --> Some if start
	// e--> goto start

	private IFBlock[] getInRangeIFS(int s, int e) {
		Collection ifs = getCurrentIFStructues();
		if (ifs == null || ifs.size() == 0)
			return null;
		ArrayList list = new ArrayList();
		int start = s + 2 + 1; // ifstart+skipbytes+1 --> start of next inst
		Iterator it = ifs.iterator();
		while (it.hasNext()) {
			IFBlock cur = (IFBlock) it.next();
			int ifstart = cur.getIfStart();
			if (ifstart >= start && ifstart < e) {
				list.add(cur);
			}
		}

		if (list.size() > 0)
			return (IFBlock[]) list.toArray(new IFBlock[list.size()]);
		else
			return null;
	}

	private java.lang.String anyWhileBodyHere(int e, Loop iloop,
			OperandStack opStack) {
		java.lang.String str = "";
		boolean sp = checkToEvaluateAnyLoopBody(e, behaviour.getCode());
		if (!sp)
			return str;
		byte[] code = behaviour.getCode();
		Operand ops[] = new Operand[2];
		java.lang.String val = "";
		if (isThisInstrStart(behaviour.getInstructionStartPositions(), e)) {
			switch (code[e]) {

			case JvmOpCodes.IF_ACMPEQ:
				ops = getIFArgs(opStack, 2);
				if (ops != null) {
					str = "\nif(" + ops[1].getOperandValue() + "=="
							+ ops[0].getOperandValue()
							+ "))\n{\n}\nelse\n{\nbreak\n}\n;";
				} else {
					str = "";
				}
				break;
			case JvmOpCodes.IF_ACMPNE:
				ops = getIFArgs(opStack, 2);
				if (ops != null) {
					str = "\nif(!" + ops[1].getOperandValue() + "=="
							+ ops[0].getOperandValue()
							+ "))\n{\n}\nelse\n{\nbreak\n}\n;";
				} else {
					str = "";
				}
				break;
			case JvmOpCodes.IF_ICMPEQ:

				ops = getIFArgs(opStack, 2);
				if (ops != null) {
					str = "\nif(" + ops[1].getOperandValue() + " == "
							+ ops[0].getOperandValue()
							+ ")\n{\n}\nelse\n{\nbreak;\n}\n";
				} else {
					str = "";
				}
				break;
			case JvmOpCodes.IF_ICMPGE:
				ops = getIFArgs(opStack, 2);
				if (ops != null) {
					str = "\nif(" + ops[1].getOperandValue() + " >= "
							+ ops[0].getOperandValue()
							+ ")\n{\n}\nelse\n{\nbreak;\n}\n";
				} else {
					str = "";
				}
				break;

			case JvmOpCodes.IF_ICMPGT:
				ops = getIFArgs(opStack, 2);
				if (ops != null) {
					str = "\nif(" + ops[1].getOperandValue() + " > "
							+ ops[0].getOperandValue()
							+ ")\n{\n}\nelse\n{\nbreak;\n}\n";
				} else {
					str = "";
				}
				break;

			case JvmOpCodes.IF_ICMPLE:
				ops = getIFArgs(opStack, 2);
				if (ops != null) {
					str = "\nif(" + ops[1].getOperandValue() + " <= "
							+ ops[0].getOperandValue()
							+ ")\n{\n}\nelse\n{\nbreak;\n}\n";
				} else {
					str = "";
				}
				break;

			case JvmOpCodes.IF_ICMPLT:
				ops = getIFArgs(opStack, 2);
				if (ops != null) {
					str = "\nif(" + ops[1].getOperandValue() + "<"
							+ ops[0].getOperandValue()
							+ ")\n{\n}\nelse\n{\nbreak;\n}\n";
				} else {
					str = "";
				}
				break;

			case JvmOpCodes.IF_ICMPNE:
				ops = getIFArgs(opStack, 2);
				if (ops != null) {
					str = "\nif(" + ops[1].getOperandValue() + "!= "
							+ ops[0].getOperandValue()
							+ ")\n{\n}\nelse\n{\nbreak;\n}\n";
				} else {
					str = "";
				}
				break;

			case JvmOpCodes.IFEQ:
				ops = getIFArgs(opStack, 1);
				val = getReturnTypeIfPreviousInvoke(currentForIndex, code);
				java.lang.String t3 = ops[0].getClassType();
				java.lang.String t4 = ops[0].getLocalVarType();
				if ((t3 != null && t3.trim().equals("boolean"))
						|| (t4 != null && t4.trim().equals("boolean"))) {
					if (val != null && val.trim().equals("1")) {
						val = "true";
					}
					if (val != null && val.trim().equals("0")) {
						val = "false";
					}
				}
				if (ops != null) {
					if ((code[currentForIndex - 1] != JvmOpCodes.DCMPG)
							&& (code[currentForIndex - 1] != JvmOpCodes.DCMPL)
							&& code[currentForIndex - 1] != JvmOpCodes.FCMPG
							&& code[currentForIndex - 1] != JvmOpCodes.FCMPL
							&& code[currentForIndex - 1] != JvmOpCodes.LCMP)
						str = "\nif(" + ops[0].getOperandValue() + "==" + val
								+ ")\n{\n}\nelse\n{\nbreak;\n}\n";
					else
						str = "\nif(" + ops[0].getOperandValue()
								+ ")\n{\nbreak;\n}\n" + "else\n" + "{\n"
								+ "}\n";
				} else {
					str = "";
				}
				break;
			case JvmOpCodes.IFGE:
				ops = getIFArgs(opStack, 1);
				if (ops != null) {
					if ((code[currentForIndex - 1] != JvmOpCodes.DCMPG)
							&& (code[currentForIndex - 1] != JvmOpCodes.DCMPL)
							&& code[currentForIndex - 1] != JvmOpCodes.FCMPG
							&& code[currentForIndex - 1] != JvmOpCodes.FCMPL
							&& code[currentForIndex - 1] != JvmOpCodes.LCMP)
						str = "\nif(" + ops[0].getOperandValue()
								+ ">=0)\n{\n}\nelse\n{\nbreak;\n}\n";
					else
						str = "\nif(" + ops[0].getOperandValue()
								+ ")\n{\nbreak;\n}\n" + "else\n" + "{\n"
								+ "}\n";
				} else {
					str = "";
				}
				break;

			case JvmOpCodes.IFGT:
				ops = getIFArgs(opStack, 1);
				if (ops != null) {
					if ((code[currentForIndex - 1] != JvmOpCodes.DCMPG)
							&& (code[currentForIndex - 1] != JvmOpCodes.DCMPL)
							&& code[currentForIndex - 1] != JvmOpCodes.FCMPG
							&& code[currentForIndex - 1] != JvmOpCodes.FCMPL
							&& code[currentForIndex - 1] != JvmOpCodes.LCMP)
						str = "\nif(" + ops[0].getOperandValue()
								+ ">0)\n{\n}\nelse\n{\nbreak;\n}\n";
					else
						str = "\nif(" + ops[0].getOperandValue()
								+ ")\n{\nbreak;\n}\n" + "else\n" + "{\n"
								+ "}\n";
				} else {
					str = "";
				}
				break;
			case JvmOpCodes.IFLE:
				ops = getIFArgs(opStack, 1);
				if (ops != null) {
					if ((code[currentForIndex - 1] != JvmOpCodes.DCMPG)
							&& (code[currentForIndex - 1] != JvmOpCodes.DCMPL)
							&& code[currentForIndex - 1] != JvmOpCodes.FCMPG
							&& code[currentForIndex - 1] != JvmOpCodes.FCMPL
							&& code[currentForIndex - 1] != JvmOpCodes.LCMP)
						str = "\nif(" + ops[0].getOperandValue()
								+ "<=0)\n{\n}\nelse\n{\nbreak;\n}\n";
					else
						str = "\nif(" + ops[0].getOperandValue()
								+ ")\n{\nbreak;\n}\n" + "else\n" + "{\n"
								+ "}\n";
				} else {
					str = "";
				}
				break;
			case JvmOpCodes.IFLT:
				ops = getIFArgs(opStack, 1);
				if (ops != null) {
					if ((code[currentForIndex - 1] != JvmOpCodes.DCMPG)
							&& (code[currentForIndex - 1] != JvmOpCodes.DCMPL)
							&& code[currentForIndex - 1] != JvmOpCodes.FCMPG
							&& code[currentForIndex - 1] != JvmOpCodes.FCMPL
							&& code[currentForIndex - 1] != JvmOpCodes.LCMP)
						str = "\nif(" + ops[0].getOperandValue()
								+ "<0)\n{\n}\nelse\n{\nbreak;\n}\n";
					else
						str = "\nif(" + ops[0].getOperandValue()
								+ ")\n{\nbreak;\n}\n" + "else\n" + "{\n"
								+ "}\n";
				} else {
					str = "";
				}
				break;
			case JvmOpCodes.IFNE:
				ops = getIFArgs(opStack, 1);
				val = getReturnTypeIfPreviousInvoke(currentForIndex, code);
				t3 = ops[0].getClassType();
				t4 = ops[0].getLocalVarType();
				if ((t3 != null && t3.trim().equals("boolean"))
						|| (t4 != null && t4.trim().equals("boolean"))) {
					if (val != null && val.trim().equals("1")) {
						val = "true";
					}
					if (val != null && val.trim().equals("0")) {
						val = "false";
					}
				}
				if (ops != null) {
					if ((code[currentForIndex - 1] != JvmOpCodes.DCMPG)
							&& (code[currentForIndex - 1] != JvmOpCodes.DCMPL)
							&& code[currentForIndex - 1] != JvmOpCodes.FCMPG
							&& code[currentForIndex - 1] != JvmOpCodes.FCMPL
							&& code[currentForIndex - 1] != JvmOpCodes.LCMP)
						str = "\nif(" + ops[0].getOperandValue() + "!=" + val
								+ ")\n{\n}\nelse\n{\nbreak;\n}\n";
					else
						str = "\nif(" + ops[0].getOperandValue()
								+ ")\n{\nbreak;\n}\n" + "else\n" + "{\n"
								+ "}\n";
				} else {
					str = "";
				}
				break;
			case JvmOpCodes.IFNONNULL:

				ops = getIFArgs(opStack, 1);
				if (ops != null) {
					str = "\nif(" + ops[0].getOperandValue()
							+ "!= null)\n{\n}\nelse\n{break;\n}\n";
				} else {
					str = "";
				}
				break;
			case JvmOpCodes.IFNULL:
				ops = getIFArgs(opStack, 1);
				if (ops != null) {
					str = "\nif(" + ops[0].getOperandValue()
							+ "== null)\n{\n}\nelse\n{break;\n}\n";
				} else {
					str = "";
				}
				break;
			default:
				str = "";
				break;

			}
		}
		return str;

	}

	private Operand[] getIFArgs(OperandStack opStack, int number) {
		Operand ops[] = new Operand[number];
		if (opStack.size() >= number) {

			for (int z = 0; z < number; z++) // ops[0] --> Original TOS
			{
				ops[z] = opStack.getTopOfStack();

			}

			return ops;
		}

		return null;
	}

	private java.lang.String getIfElseReturnAtI(int i) {
		if (GlobalVariableStore.getRetAtIfElseEnd() == null
				|| GlobalVariableStore.getRetAtIfElseEnd().size() == 0) {
			return null;
		} else {
			boolean ret = addReturnAtIFElseEnd(i);
			if (ret) {
				returnsaddedAtIfElse.add(new Integer(i));
				return (java.lang.String) ((GlobalVariableStore
						.getRetAtIfElseEnd()).get(new Integer(i)));
			} else
				return null;
		}
	}

	public static java.lang.String getClassName(java.lang.String path) {
		java.lang.String slash = "";
		if (path.indexOf("\\") != -1) {
			slash = "\\";
		} else {
			slash = "/";
		}
		int lastSlash = path.lastIndexOf(slash);
		return path.substring(lastSlash + 1);

	}

	private boolean isInstAnyBasicPrimitiveOperation(byte code[], int pos,
			StringBuffer sb) {
		boolean b = false;
		switch (code[pos]) {

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

	private void handleBasicPrimitiveLoadOp(byte[] code, int k)
			throws Exception {
		if (k < 0)
			throw new Exception(
					"Invalid argument sent to handleBasicPrimitiveLoadOp");
		ArrayList list = behaviour.getInstructionStartPositions();
		boolean b = isThisInstrStart(list, k);
		while (!b) {
			k = k - 1;
			b = isThisInstrStart(list, k);
		}
		switch (code[k]) {
		/***********************************************************************
		 * First List all recursive calls
		 */
		case JvmOpCodes.DADD:
			InstParam newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			InstParam tos = (InstParam) stackHandler.pop();
			int mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			int rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.DDIV:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.DMUL:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.DNEG:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(1);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.DREM:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.DSUB:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.FADD:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.FDIV:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.FMUL:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.FNEG:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(1);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.FREM:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.IADD:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.IAND:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.IDIV:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.IMUL:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;

			}

		case JvmOpCodes.INEG:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(1);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.IOR:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.IREM:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.ISHL:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.ISHR:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.ISUB:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.IUSHR:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.IXOR:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.LADD:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.LAND:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.LDIV:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.LMUL:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.LNEG:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(1);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.LOR:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.LREM:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.LSHL:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.LSHR:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.LSUB:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.LUSHR:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		case JvmOpCodes.LXOR:
			newentry = new InstParam();
			newentry.setNumberOfParamsLeft(2);
			newentry.setCurrentParamOffsetInCode(-1);
			stackHandler.push(newentry);
			handleBasicPrimitiveLoadOp(code, (k - 1));
			tos = (InstParam) stackHandler.pop();
			mybegin = tos.getCurrentParamOffsetInCode();
			tos = (InstParam) stackHandler.pop();
			rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(mybegin);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, (mybegin - 1));
				return;
			}

		}

		if (code[k] == JvmOpCodes.BALOAD || code[k] == JvmOpCodes.CALOAD
				|| code[k] == JvmOpCodes.DALOAD || code[k] == JvmOpCodes.FALOAD
				|| code[k] == JvmOpCodes.LALOAD || code[k] == JvmOpCodes.IALOAD
				|| code[k] == JvmOpCodes.SALOAD) {
			StringBuffer sb = new StringBuffer("");
			int posFound = isInstAload(k, code, sb);
			while (posFound == -1) {

				k = k - 1;
				sb = new StringBuffer("");
				posFound = isInstAload(k, code, sb);

			}
			InstParam tos = (InstParam) stackHandler.pop();
			int rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(posFound);
			stackHandler.push(tos);
			if (rem == 0)
				return;
			else {
				handleBasicPrimitiveLoadOp(code, posFound - 1);
				return;
			}

		}

		if (code[k] == JvmOpCodes.BIPUSH || code[k] == JvmOpCodes.SIPUSH) {
			InstParam tos = (InstParam) stackHandler.pop();
			int rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(k);
			stackHandler.push(tos);
			if (rem == 0)
				return;
			else {
				handleBasicPrimitiveLoadOp(code, k - 1);
				return;
			}

		}
		int y = isInstIloadInst(code, k, new StringBuffer(""));
		if (y != -1) {
			InstParam tos = (InstParam) stackHandler.pop();
			int rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(y);
			stackHandler.push(tos);
			if (rem == 0) {
				return;
			} else {
				handleBasicPrimitiveLoadOp(code, y - 1);
				tos = (InstParam) stackHandler.pop();
				rem = tos.getNumberOfParamsLeft();
				if (rem == 0) {
					stackHandler.push(tos);
					return;
				}

			}

		}
		y = isInstFloadInst(code, k, new StringBuffer(""));
		if (y != -1) {
			InstParam tos = (InstParam) stackHandler.pop();
			int rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(y);
			stackHandler.push(tos);
			if (rem == 0)
				return;
			else {
				handleBasicPrimitiveLoadOp(code, y - 1);
				return;
			}

		}
		y = isInstDloadInst(code, k, new StringBuffer(""));
		if (y != -1) {
			InstParam tos = (InstParam) stackHandler.pop();
			int rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(y);
			stackHandler.push(tos);
			if (rem == 0)
				return;
			else {
				handleBasicPrimitiveLoadOp(code, y - 1);
				return;
			}

		}
		if (code[k] == JvmOpCodes.LDC || code[k] == JvmOpCodes.LDC2_W
				|| code[k] == JvmOpCodes.LDC_W) {
			InstParam tos = (InstParam) stackHandler.pop();
			int rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(k);
			stackHandler.push(tos);
			if (rem == 0)
				return;
			else {
				handleBasicPrimitiveLoadOp(code, k - 1);
				return;
			}

		}
		y = isInstIConstInst(code, k);
		if (y != -1) {
			InstParam tos = (InstParam) stackHandler.pop();
			int rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(y);
			stackHandler.push(tos);
			if (rem == 0)
				return;
			else {
				handleBasicPrimitiveLoadOp(code, y - 1);
				return;
			}

		}
		y = isInstFConstInst(code, k);
		if (y != -1) {
			InstParam tos = (InstParam) stackHandler.pop();
			int rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(y);
			stackHandler.push(tos);
			if (rem == 0)
				return;
			else {
				handleBasicPrimitiveLoadOp(code, y - 1);
				return;
			}

		}
		y = isInstdConstInst(code, k);
		if (y != -1) {
			InstParam tos = (InstParam) stackHandler.pop();
			int rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(y);
			stackHandler.push(tos);
			if (rem == 0)
				return;
			else {
				handleBasicPrimitiveLoadOp(code, y - 1);
				return;
			}

		}
		y = isInstLConstInst(code, k);
		if (y != -1) {
			InstParam tos = (InstParam) stackHandler.pop();
			int rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(y);
			stackHandler.push(tos);
			if (rem == 0)
				return;
			else {
				handleBasicPrimitiveLoadOp(code, y - 1);
				return;
			}

		}
		if (code[k] == JvmOpCodes.GETFIELD) {
			StringBuffer sb1 = new StringBuffer("");
			int pos2 = isInstAload(k - 1, code, sb1);
			if (pos2 != -1) {
				InstParam tos = (InstParam) stackHandler.pop();
				int rem = tos.getNumberOfParamsLeft();
				rem = rem - 1;
				tos.setNumberOfParamsLeft(rem);
				tos.setCurrentParamOffsetInCode(pos2);
				stackHandler.push(tos);
				if (rem == 0)
					return;
				else {
					handleBasicPrimitiveLoadOp(code, pos2 - 1);
					return;
				}
			}
		}
		if (code[k] == JvmOpCodes.GETSTATIC) {

			InstParam tos = (InstParam) stackHandler.pop();
			int rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			tos.setCurrentParamOffsetInCode(k); // todo: TAKE POP INTO ACCOUNT
			stackHandler.push(tos);
			if (rem == 0)
				return;
			else {
				handleBasicPrimitiveLoadOp(code, k - 1);
				return;
			}

		}

		StringBuffer isinvoke = new StringBuffer("");
		if (isNextInstructionAnyInvoke(code[k], isinvoke)) {

			InstParam tos = (InstParam) stackHandler.pop();
			int rem = tos.getNumberOfParamsLeft();
			rem = rem - 1;
			tos.setNumberOfParamsLeft(rem);
			Integer ref = (Integer) invokeinstrefpos.get(new Integer(k));
			if (ref != null) {

				tos.setCurrentParamOffsetInCode(ref.intValue());
				stackHandler.push(tos);
				if (rem == 0) {
					return;
				} else {
					handleBasicPrimitiveLoadOp(code, ref.intValue() - 1);
					return;

				}
			}

		}

		if (code[k] == JvmOpCodes.D2F || code[k] == JvmOpCodes.D2I
				|| code[k] == JvmOpCodes.D2L) {

			int t = k - 1;
			boolean done = false;
			StringBuffer strb = new StringBuffer("");
			int dload = isInstDloadInst(code, t, strb);
			int foundpos = -1;
			if (dload != -1) {
				foundpos = dload;
				done = true;
			} else {
				strb = new StringBuffer("");
				int loadin = traceLoadInst(t, code, strb);
				if (loadin != -1) {
					foundpos = loadin;
					done = true;
				}

			}

			if (done) {

				InstParam tos = (InstParam) stackHandler.pop();
				int rem = tos.getNumberOfParamsLeft();
				rem = rem - 1;
				tos.setNumberOfParamsLeft(rem);
				tos.setCurrentParamOffsetInCode(foundpos);
				stackHandler.push(tos);
				if (rem == 0)
					return;
				else {
					handleBasicPrimitiveLoadOp(code, foundpos - 1);
					return;
				}
			}

			else {
				InstParam newentry = new InstParam();
				newentry.setNumberOfParamsLeft(1);
				newentry.setCurrentParamOffsetInCode(-1);
				stackHandler.push(newentry);
				handleBasicPrimitiveLoadOp(code, (k - 1));
				InstParam tos = (InstParam) stackHandler.pop();
				int entry = tos.getCurrentParamOffsetInCode();
				tos = (InstParam) stackHandler.pop();
				int r = tos.getNumberOfParamsLeft();
				r = r - 1;
				tos.setNumberOfParamsLeft(r);
				tos.setCurrentParamOffsetInCode(entry);
				stackHandler.push(tos);
				if (r == 0) {
					return;
				} else {
					handleBasicPrimitiveLoadOp(code, entry - 1);
					return;
				}
			}
		}
		if (code[k] == JvmOpCodes.I2B || code[k] == JvmOpCodes.I2C
				|| code[k] == JvmOpCodes.I2S || code[k] == JvmOpCodes.I2D
				|| code[k] == JvmOpCodes.I2F || code[k] == JvmOpCodes.I2L) {

			int t = k - 1;
			boolean done = false;
			StringBuffer strb = new StringBuffer("");
			int dload = isInstIloadInst(code, t, strb);
			int foundpos = -1;
			if (dload != -1) {
				foundpos = dload;
				done = true;
			} else {
				strb = new StringBuffer("");
				int loadin = traceLoadInst(t, code, strb);
				if (loadin != -1) {
					foundpos = loadin;
					done = true;
				}

			}

			if (done) {

				InstParam tos = (InstParam) stackHandler.pop();
				int rem = tos.getNumberOfParamsLeft();
				rem = rem - 1;
				tos.setNumberOfParamsLeft(rem);
				tos.setCurrentParamOffsetInCode(foundpos);
				stackHandler.push(tos);
				if (rem == 0)
					return;
				else {
					handleBasicPrimitiveLoadOp(code, foundpos - 1);
					return;
				}
			}

			else {
				InstParam newentry = new InstParam();
				newentry.setNumberOfParamsLeft(1);
				newentry.setCurrentParamOffsetInCode(-1);
				stackHandler.push(newentry);
				handleBasicPrimitiveLoadOp(code, (k - 1));
				InstParam tos = (InstParam) stackHandler.pop();
				int entry = tos.getCurrentParamOffsetInCode();
				tos = (InstParam) stackHandler.pop();
				int r = tos.getNumberOfParamsLeft();
				r = r - 1;
				tos.setNumberOfParamsLeft(r);
				tos.setCurrentParamOffsetInCode(entry);
				stackHandler.push(tos);
				if (r == 0) {
					return;
				} else {
					handleBasicPrimitiveLoadOp(code, entry - 1);
					return;
				}
			}
		}
		if (code[k] == JvmOpCodes.F2D || code[k] == JvmOpCodes.F2L
				|| code[k] == JvmOpCodes.F2I) {
			int t = k - 1;
			boolean done = false;
			StringBuffer strb = new StringBuffer("");
			int dload = isInstFloadInst(code, t, strb);
			int foundpos = -1;
			if (dload != -1) {
				foundpos = dload;
				done = true;
			} else {
				strb = new StringBuffer("");
				int loadin = traceLoadInst(t, code, strb);
				if (loadin != -1) {
					foundpos = loadin;
					done = true;
				}

			}

			if (done) {

				InstParam tos = (InstParam) stackHandler.pop();
				int rem = tos.getNumberOfParamsLeft();
				rem = rem - 1;
				tos.setNumberOfParamsLeft(rem);
				tos.setCurrentParamOffsetInCode(foundpos);
				stackHandler.push(tos);
				if (rem == 0)
					return;
				else {
					handleBasicPrimitiveLoadOp(code, foundpos - 1);
					return;
				}
			}

			else {
				InstParam newentry = new InstParam();
				newentry.setNumberOfParamsLeft(1);
				newentry.setCurrentParamOffsetInCode(-1);
				stackHandler.push(newentry);
				handleBasicPrimitiveLoadOp(code, (k - 1));
				InstParam tos = (InstParam) stackHandler.pop();
				int entry = tos.getCurrentParamOffsetInCode();
				tos = (InstParam) stackHandler.pop();
				int r = tos.getNumberOfParamsLeft();
				r = r - 1;
				tos.setNumberOfParamsLeft(r);
				tos.setCurrentParamOffsetInCode(entry);
				stackHandler.push(tos);
				if (r == 0) {
					return;
				} else {
					handleBasicPrimitiveLoadOp(code, entry - 1);
					return;
				}
			}
		}
		if (code[k] == JvmOpCodes.L2D || code[k] == JvmOpCodes.L2F
				|| code[k] == JvmOpCodes.L2I) {
			int t = k - 1;
			boolean done = false;
			StringBuffer strb = new StringBuffer("");
			int dload = isInstLloadInst(code, t, strb);
			int foundpos = -1;
			if (dload != -1) {
				foundpos = dload;
				done = true;
			} else {
				strb = new StringBuffer("");
				int loadin = traceLoadInst(t, code, strb);
				if (loadin != -1) {
					foundpos = loadin;
					done = true;
				}

			}

			if (done) {

				InstParam tos = (InstParam) stackHandler.pop();
				int rem = tos.getNumberOfParamsLeft();
				rem = rem - 1;
				tos.setNumberOfParamsLeft(rem);
				tos.setCurrentParamOffsetInCode(foundpos);
				stackHandler.push(tos);
				if (rem == 0)
					return;
				else {
					handleBasicPrimitiveLoadOp(code, foundpos - 1);
					return;
				}
			}

			else {
				InstParam newentry = new InstParam();
				newentry.setNumberOfParamsLeft(1);
				newentry.setCurrentParamOffsetInCode(-1);
				stackHandler.push(newentry);
				handleBasicPrimitiveLoadOp(code, (k - 1));
				InstParam tos = (InstParam) stackHandler.pop();
				int entry = tos.getCurrentParamOffsetInCode();
				tos = (InstParam) stackHandler.pop();
				int r = tos.getNumberOfParamsLeft();
				r = r - 1;
				tos.setNumberOfParamsLeft(r);
				tos.setCurrentParamOffsetInCode(entry);
				stackHandler.push(tos);
				if (r == 0) {
					return;
				} else {
					handleBasicPrimitiveLoadOp(code, entry - 1);
					return;
				}
			}
		}

	}

	/***************************************************************************
	 * belurs: The below 2 are used in handleBasicPrimitiveLoadOp Could Think of
	 * an appropriate names here. Refactor later if better names can be thpught
	 * of
	 */

	private Stack stackHandler = null; // will contain InstParam Objects

	class InstParam {

		private int numberOfParamsLeft; // Example imul takes 2 so initially 2

		// is populated

		private int currentParamOffsetInCode; // Example for imul the first

		// one cane be iload_1..So once

		// This is encountered, currentParamOffsetInCode will be
		// set to iload_1 position in code.

		public int getNumberOfParamsLeft() {
			return numberOfParamsLeft;
		}

		public void setNumberOfParamsLeft(int numberOfParamsLeft) {
			this.numberOfParamsLeft = numberOfParamsLeft;
		}

		public int getCurrentParamOffsetInCode() {
			return currentParamOffsetInCode;
		}

		public void setCurrentParamOffsetInCode(int currentParamOffsetInCode) {
			this.currentParamOffsetInCode = currentParamOffsetInCode;
		}

	}

	private Operand createOperand(Object val, int type, int categ) {
		Operand opr = new Operand();
		opr.setOperandValue(val);
		opr.setOperandType(type);
		opr.setCategory(categ);
		return opr;

	}

	private Operand createOperand(Object val) {
		Operand opr = new Operand();
		opr.setOperandValue(val);
		return opr;

	}

	private int getSwitchOffset(byte[] info, int counter, java.lang.String lbl) {
		int b1 = info[++counter];
		int b2 = info[++counter];
		int b3 = info[++counter];
		int b4 = info[++counter];

		if (b1 < 0)
			b1 = (256 + b1);
		if (b2 < 0)
			b2 = (256 + b2);
		if (b3 < 0)
			b3 = (256 + b3);
		if (b4 < 0)
			b4 = (256 + b4);

		int jmp = (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
		if (jmp > 65535)
			jmp = jmp - 65536;
		if (lbl.equals("label"))
			return jmp;
		if (jmp < 0)
			jmp = 512 + jmp;
		return jmp;

	}

	private java.lang.String getReturnTypeIfPreviousInvoke(int j, byte[] info) {

		// Terminator t;
		java.lang.String s = "0";
		if (GlobalVariableStore.getInvokeStartEnd() != null) {
			Integer in = (Integer) GlobalVariableStore.getInvokeStartEnd().get(
					new Integer(j - 1));
			if (in != null) {
				int iin = in.intValue();
				switch (info[iin]) {

				case JvmOpCodes.INVOKEINTERFACE:
					int classIndex = getOffset(info, iin);
					InterfaceMethodRef iref = cd
							.getInterfaceMethodAtCPoolPosition(classIndex);
					java.lang.String classname = iref.getClassname();
					java.lang.String typeofmet = iref.getTypeofmethod();
					int br = typeofmet.indexOf(")");
					if (br != -1) {
						java.lang.String ret = typeofmet.substring(br + 1);
						if (ret.trim().equals("Z")) {
							return "false";
						}

					}

					break;

				case JvmOpCodes.INVOKESPECIAL:

					classIndex = getOffset(info, iin);
					MethodRef mref = cd.getMethodRefAtCPoolPosition(classIndex);
					java.lang.String classtype = mref.getClassname();
					typeofmet = mref.getTypeofmethod();
					br = typeofmet.indexOf(")");
					if (br != -1) {
						java.lang.String ret = typeofmet.substring(br + 1);
						if (ret.trim().equals("Z")) {
							return "false";
						}

					}

					break;

				case JvmOpCodes.INVOKESTATIC:
					classIndex = getOffset(info, iin);
					mref = cd.getMethodRefAtCPoolPosition(classIndex);
					classname = mref.getClassname();
					typeofmet = mref.getTypeofmethod();
					br = typeofmet.indexOf(")");
					if (br != -1) {
						java.lang.String ret = typeofmet.substring(br + 1);
						if (ret.trim().equals("Z")) {
							return "false";
						}

					}

					break;

				case JvmOpCodes.INVOKEVIRTUAL:
					classIndex = getOffset(info, iin);
					mref = cd.getMethodRefAtCPoolPosition(classIndex);
					classname = mref.getClassname();
					typeofmet = mref.getTypeofmethod();
					br = typeofmet.indexOf(")");
					if (br != -1) {
						java.lang.String ret = typeofmet.substring(br + 1);
						if (ret.trim().equals("Z")) {
							return "false";
						}

					}

					break;

				default:
					return s;

				}
			}

			return s;

		} else {
			int iin = j - 3;
			int classIndex = -1;
			boolean isS = isThisInstrStart(behaviour
					.getInstructionStartPositions(), iin);
			if (isS) {
				switch (info[iin]) {

				/*
				 * case JvmOpCodes.INVOKEINTERFACE: int
				 * classIndex=getOffset(info,iin); InterfaceMethodRef
				 * iref=cd.getInterfaceMethodAtCPoolPosition(classIndex);
				 * java.lang.String classname=iref.getClassname();
				 * java.lang.String typeofmet=iref.getTypeofmethod(); int
				 * br=typeofmet.indexOf(")"); if(br!=-1) { java.lang.String
				 * ret=typeofmet.substring(br+1); if(ret.trim().equals("Z")) {
				 * return "false"; } }
				 * 
				 * break;
				 */

				case JvmOpCodes.INVOKESPECIAL:

					classIndex = getOffset(info, iin);
					MethodRef mref = cd.getMethodRefAtCPoolPosition(classIndex);
					java.lang.String classtype = mref.getClassname();
					java.lang.String typeofmet = mref.getTypeofmethod();
					int br = typeofmet.indexOf(")");
					if (br != -1) {
						java.lang.String ret = typeofmet.substring(br + 1);
						if (ret.trim().equals("Z")) {
							return "false";
						}

					}

					break;

				case JvmOpCodes.INVOKESTATIC:
					classIndex = getOffset(info, iin);
					mref = cd.getMethodRefAtCPoolPosition(classIndex);
					java.lang.String classname = mref.getClassname();
					typeofmet = mref.getTypeofmethod();
					br = typeofmet.indexOf(")");
					if (br != -1) {
						java.lang.String ret = typeofmet.substring(br + 1);
						if (ret.trim().equals("Z")) {
							return "false";
						}

					}

					break;

				case JvmOpCodes.INVOKEVIRTUAL:
					classIndex = getOffset(info, iin);
					mref = cd.getMethodRefAtCPoolPosition(classIndex);
					classname = mref.getClassname();
					typeofmet = mref.getTypeofmethod();
					br = typeofmet.indexOf(")");
					if (br != -1) {
						java.lang.String ret = typeofmet.substring(br + 1);
						if (ret.trim().equals("Z")) {
							return "false";
						}

					}

				}
			}
			if (isThisInstrStart(behaviour.getInstructionStartPositions(),
					j - 5)
					&& info[j - 5] == JvmOpCodes.INVOKEINTERFACE) {
				iin = j - 5;
				classIndex = getOffset(info, iin);
				InterfaceMethodRef iref = cd
						.getInterfaceMethodAtCPoolPosition(classIndex);
				java.lang.String classname = iref.getClassname();
				java.lang.String typeofmet = iref.getTypeofmethod();
				int br = typeofmet.indexOf(")");
				if (br != -1) {
					java.lang.String ret = typeofmet.substring(br + 1);
					if (ret.trim().equals("Z")) {
						return "false";
					}

				}
			}
		}
		return s;
	}

	ArrayList startlist = null;

	private void findSkipRangesWRTSynchronizedBlocks() {

		startlist = behaviour.getInstructionStartPositions();
		synchSkips = new Hashtable();
		byte code[] = behaviour.getCode();
		if (code == null)
			return;
		for (int j = 0; j < code.length; j++) {

			if (isThisInstrStart(behaviour.getInstructionStartPositions(), j)
					&& code[j] == JvmOpCodes.MONITOREXIT) {
				int loadpos = -1;
				boolean simple = true;
				int index = -1;
				switch (code[j + 1]) {

				case JvmOpCodes.ALOAD:
					simple = false;
					loadpos = j + 1;

					break;
				case JvmOpCodes.ALOAD_0:
					simple = true;
					loadpos = j + 1;
					index = 0;
					break;
				case JvmOpCodes.ALOAD_1:
					simple = true;
					loadpos = j + 1;
					index = 1;
					break;
				case JvmOpCodes.ALOAD_2:
					simple = true;
					loadpos = j + 1;
					index = 2;
					break;
				case JvmOpCodes.ALOAD_3:
					simple = true;
					loadpos = j + 1;
					index = 3;
					break;
				default:
					loadpos = -1;

				}

				if (loadpos != -1) {

					if (simple) {

						if (code[j + 1 + 1] == JvmOpCodes.ATHROW) {
							int start = j;
							while (start >= 0) {

								int st = code[start];
								if (isThisInstrStart(startlist, start)) {
									int storeindex = -1;

									switch (st) {
									case JvmOpCodes.ASTORE_0:
										storeindex = 0;
										break;

									case JvmOpCodes.ASTORE_1:
										storeindex = 1;
										break;

									case JvmOpCodes.ASTORE_2:
										storeindex = 2;
										break;

									case JvmOpCodes.ASTORE_3:
										storeindex = 3;
										break;

									default:
										storeindex = -1;
										break;

									}

									if (storeindex == index) {

										boolean toskip = confirmSynchronizedSkip(
												behaviour.getCode(), j);
										if (!toskip) {
											int skipstart = start;
											int prevstart = getPrevStartOfInst(
													start, behaviour.getCode());
											synchSkips.put(new Integer(start),
													new Integer(j + 2));
											ArrayList synchlist = behaviour
													.getSynchronizedEntries();
											ExceptionTable table = getSynchTableGivenEnd(
													synchlist, j + 1);
											if (table != null) {
												table.setSynchEnd(prevstart);
											}
											break;

										} else {
											synchSkips.put(new Integer(start),
													new Integer(j + 2));
											break;
										}
									}

								}
								start--;

							}

						}
					} else {
						if (code[j + 1 + 2] == JvmOpCodes.ATHROW) {
							index = code[j + 1 + 1];
							if (index < 0)
								index += 256; // TODO: apply this to elsewhere
							int start = j;
							while (start >= 0) {

								int st = code[start];
								if (isThisInstrStart(startlist, start)) {
									int storeindex = -1;

									switch (st) {
									case JvmOpCodes.ASTORE_0:
										storeindex = 0;
										break;

									case JvmOpCodes.ASTORE_1:
										storeindex = 1;
										break;

									case JvmOpCodes.ASTORE_2:
										storeindex = 2;
										break;

									case JvmOpCodes.ASTORE_3:
										storeindex = 3;
										break;

									case JvmOpCodes.ASTORE:
										storeindex = code[start + 1];
										if (storeindex < 0)
											storeindex += 256;
										break;

									default:
										storeindex = -1;
										break;

									}

									if (storeindex == index) {
										boolean toskip = confirmSynchronizedSkip(
												behaviour.getCode(), j);
										if (!toskip) {
											int skipstart = start;
											int prevstart = getPrevStartOfInst(
													start, behaviour.getCode());
											synchSkips.put(new Integer(start),
													new Integer(j + 3));
											ArrayList synchlist = behaviour
													.getSynchronizedEntries();
											ExceptionTable table = getSynchTableGivenEnd(
													synchlist, j + 1);
											if (table != null) {
												table.setSynchEnd(prevstart);
											}
											break;

										} else {
											synchSkips.put(new Integer(start),
													new Integer(j + 3));
											break;

										}
									}
								}
								start--;

							}

						}

					}

				}
			}

		}

		sortSynchBlocksByStart();

	}

	private void sortSynchBlocksByStart() {

		if (synchSkips != null) {
			sortedsynchSkips = new ArrayList();
			Set set = synchSkips.keySet();
			List aslist = new ArrayList(set);
			Collections.sort(aslist);
			for (int i = 0; i < aslist.size(); i++) {
				SynchSkipHelper helper = new SynchSkipHelper();
				helper.start = ((Integer) aslist.get(i)).intValue();
				helper.end = ((Integer) synchSkips.get(aslist.get(i)))
						.intValue();
				sortedsynchSkips.add(helper);
			}
		}

	}

	private class SynchSkipHelper {

		private int start;

		private int end;

		public int hashCode() {
			final int PRIME = 31;
			int result = 1;
			result = PRIME * result + end;
			result = PRIME * result + start;
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final SynchSkipHelper other = (SynchSkipHelper) obj;
			if (end != other.end)
				return false;
			if (start != other.start)
				return false;
			return true;
		}

	}

	private int handleAloadCase(byte[] info, int i, OperandStack OpStack) {

		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.OBJECT,
				JvmOpCodes.ALOAD);

	}

	// belurs:
	// only polls for end of synch tables . Not start(MonitorEnter)

	private java.lang.String pollsynchblocks(int cur) {

		ArrayList synchlist = behaviour.getSynchronizedEntries();
		if (synchlist != null && synchlist.size() > 0) {

			for (int s = 0; s < synchlist.size(); s++) {
				ExceptionTable table = (ExceptionTable) synchlist.get(s);
				int end = table.getSynchEnd();
				if (end == cur
						&& !TryHelper.synchBlockEndedForStartAt(table
								.getStartPC(), synchlist, table)) {
					checkForAthrowAtSynchEnd(end);
					table.setWasSynchClosed(true);
					return "}";
				}

			}

		}
		return "";

	}

	private void resetEndOfSynchBlockIfNecessary(int current) {

		// Step 1 : Determine the applicable IF block
		// fOR EACH MONITORENTER FIND out the closest if and end of that... ~ to
		// try handling
		ArrayList synchtables = behaviour.getSynchronizedEntries();
		byte code[] = behaviour.getCode();

		if (synchtables != null && synchtables.size() > 0) {
			ArrayList starts = behaviour.getInstructionStartPositions();
			for (int s = 0; s < synchtables.size(); s++) {

				ExceptionTable tab = (ExceptionTable) synchtables.get(s);

				int monitorStart = tab.getMonitorEnterPosInCode();
				if (frozenMonitorStarts.contains(new Integer(monitorStart))) {
					continue;
				}
				int start = tab.getStartPC();
				if (tab.getMonitorEnterPosInCode() == GlobalVariableStore
						.getCurrentMonitorEnterPos()) {

					int synchend = tab.getEndPC(); // belurs:
					if (current - 3 >= 0
							&& isNextInstructionIf(code[current - 3])) // Actually
					// The
					// previous
					// Instruction
					{
						boolean switchfound = false;
						int switchstartpos = -1;
						java.lang.String switchtype = null;
						for (int x = current - 3 - 1; x > start; x--) {
							if (isThisInstrStart(starts, x)
									&& code[x] == JvmOpCodes.MONITOREXIT) {
								GlobalVariableStore
										.setCurrentMonitorEnterPos(-1);
								return;

							}
							if (isThisInstrStart(starts, x)
									&& isNextInstructionIf(code[x])) {
								GlobalVariableStore
										.setCurrentMonitorEnterPos(-1);
								return;
							}
							if (isThisInstrStart(starts, x)
									&& (code[x] == JvmOpCodes.TABLESWITCH || code[x] == JvmOpCodes.LOOKUPSWITCH)) {
								switchfound = true;
								switchstartpos = x;
								if (code[x] == JvmOpCodes.TABLESWITCH)
									switchtype = "table";
								else
									switchtype = "lookup";

							}
						}

						if (switchfound) {
							if (switchstartpos != -1) {
								Switch swblk = getSwitchBlockGivenStart(switchstartpos);
								if (swblk != null) {

									ArrayList cases = swblk
											.sortCasesByEnd(swblk.getAllCases());
									Case endcase = null;
									if (cases != null) {
										endcase = (Case) cases.get(0);
									}
									int defend = swblk.getDefaultEnd();
									if (defend != -1) {
										if (defend > endcase.getCaseEnd()) {
											if (swblk.getStartOfSwitch() > start
													&& defend > synchend) {
												tab.setSynchEnd(defend);
												GlobalVariableStore
														.setCurrentMonitorEnterPos(-1);
												return;
											}
										} else {
											if (swblk.getStartOfSwitch() > start
													&& endcase.getCaseEnd() > synchend) {
												tab.setSynchEnd(endcase
														.getCaseEnd());
												GlobalVariableStore
														.setCurrentMonitorEnterPos(-1);
												return;
											}
										}
									} else {
										if (swblk.getStartOfSwitch() > start
												&& endcase.getCaseEnd() > synchend) {
											tab.setSynchEnd(endcase
													.getCaseEnd());
											GlobalVariableStore
													.setCurrentMonitorEnterPos(-1);
											return;
										}
									}

								}
							}

						} else // reset w.r.t if
						{

							IFBlock reqdif = null;
							Collection ifs = getCurrentIFStructues();
							Iterator it = ifs.iterator();
							while (it.hasNext()) {
								IFBlock IF = (IFBlock) it.next();
								if (IF.getIfStart() == current - 3) {
									reqdif = IF;
									break;
								}
							}
							if (reqdif != null) {
								int ifend = reqdif.getIfCloseFromByteCode();
								int elseend = reqdif.getElseCloseLineNumber();
								if (elseend == -1) {
									if (ifend > reqdif.getIfStart()) {
										if (reqdif.getIfStart() > start) {
											if (ifend > synchend) {
												GlobalVariableStore
														.setCurrentMonitorEnterPos(-1);
												tab.setSynchEnd(ifend);
												return;

											}

										}
									}

								} else {
									if (elseend > reqdif.getIfStart()) {
										if (reqdif.getIfStart() > start) {
											if (elseend > synchend) {
												GlobalVariableStore
														.setCurrentMonitorEnterPos(-1);
												tab.setSynchEnd(elseend);
												return;

											}

										}
									}

								}
							}

						}

					}

					return;
				}

			}

		}

	}

	private Switch getSwitchBlockGivenStart(int start) {
		Switch sw = null;
		ArrayList allswitches = behaviour.getAllSwitchBlks();
		for (int s = 0; s < allswitches.size(); s++) {

			Switch switchblk = (Switch) allswitches.get(s);
			if (switchblk.getStartOfSwitch() == start) {
				return switchblk;
			}
		}
		return sw;

	}

	private boolean instanceoffound = false;

	private void handleSwapInst(OperandStack stack) {
		execute(InstrConstants.UNCLASSIFIED_INSTR_TYPE, null, JvmOpCodes.SWAP);
	}

	private int handleAALOAD(OperandStack stack) {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.OBJECT,
				JvmOpCodes.AALOAD);

	}

	private boolean isInstReturnInst(byte[] code, int pos, StringBuffer sb) {

		boolean ret = false;
		if (isThisInstrStart(behaviour.getInstructionStartPositions(), pos)) {
			switch (code[pos]) {
			case JvmOpCodes.IRETURN:
				sb.append("ireturn");
				ret = true;
				break;

			case JvmOpCodes.LRETURN:
				sb.append("lreturn");
				ret = true;
				break;
			case JvmOpCodes.FRETURN:
				sb.append("freturn");
				ret = true;
				break;
			case JvmOpCodes.DRETURN:
				sb.append("dreturn");
				ret = true;
				break;
			case JvmOpCodes.ARETURN:
				sb.append("areturn");
				ret = true;
				break;
			case JvmOpCodes.RETURN:
				sb.append("return");
				ret = true;
				break;
			}
		}

		return ret;

	}

	protected IFinder getGenericFinder() {
		return FinderFactory.getFinder(IFinder.BASE);
	}

	private void addAnyReturnBeforeIfClose(byte[] info, IFBlock IF,
			java.lang.StringBuffer S, int j, OperandStack stack) {
		int ifend = IF.getIfCloseLineNumber();
		boolean addret = addReturnAtIFElseEnd(j);
		if (getGenericFinder().isPreviousInst(currentForIndex,
				currentForIndex - 1)) {
			if (info[currentForIndex - 1] == JvmOpCodes.RETURN)
				addret = false;
		}
		if (addret) {
			if (info[ifend] == JvmOpCodes.GOTO) {
				int gotojump = getJumpAddress(info, ifend);
				StringBuffer sg = new StringBuffer("");
				boolean r = isInstReturnInst(info, gotojump, sg);

				if (r) {
					if (sg.toString().equals("return") == false
							&& stack.size() > 0) {
						java.lang.String t = stack.getTopOfStack()
								.getOperandValue();
						boolean b = isMethodRetBoolean(behaviour);
						if (t != null) {
							if (t.equals("1") && b)
								t = "true";
							if (t.equals("0") && b)
								t = "false";
							java.lang.String retst = "return " + t + ";";
							boolean a = addRETURNatI(ifend, IF);
							if (a) {
								a = BranchHelper.addReturnInIfBlock(
										currentForIndex, IF);
								if (a) {
									S.append(Util
											.formatDecompiledStatement(retst));// if

									GlobalVariableStore.getReturnsAtI().put(
											new Integer(ifend), "true");
								}
							}
						}

					} else if (sg.toString().equals("return") == true) {
						boolean a = addRETURNatI(ifend, IF);
						if (a) {
							java.lang.String retst = "return ;";
							boolean continueadding = okToEndIfElseWithContinueOrReturn(
									currentForIndex, IF);
							if (continueadding) {
								continueadding = BranchHelper
										.addReturnInIfBlock(currentForIndex, IF);
								if (continueadding) {
									S.append(Util
											.formatDecompiledStatement(retst));// if

									GlobalVariableStore.getReturnsAtI().put(
											new Integer(ifend), "true");
								}
							}
						}
					}
				}

			} else {

				StringBuffer sg = new StringBuffer("");
				boolean r = isInstReturnInst(info, ifend, sg);
				if (r) {
					if (sg.toString().equals("return") == false
							&& stack.size() > 0) {
						java.lang.String t = stack.getTopOfStack()
								.getOperandValue();
						boolean b = isMethodRetBoolean(behaviour);
						if (t != null) {
							if (t.equals("1") && b)
								t = "true";
							if (t.equals("0") && b)
								t = "false";
							java.lang.String retst = "return " + t + ";";
							boolean a = addRETURNatI(ifend, IF);
							if (a) {
								a = BranchHelper.addReturnInIfBlock(
										currentForIndex, IF);
								if (a) {
									S.append(Util
											.formatDecompiledStatement(retst));// if
									// end\n");
									GlobalVariableStore.getReturnsAtI().put(
											new Integer(ifend), "true");
								}
							}
						}

					} else if (sg.toString().equals("return") == true) {
						boolean a = addRETURNatI(ifend, IF);
						if (a) {
							java.lang.String retst = "return ;";
							boolean ok = OKToAddReturn(ifend);
							if (ok) {
								boolean continueadding = okToEndIfElseWithContinueOrReturn(
										currentForIndex, IF);
								if (continueadding) {
									continueadding = BranchHelper
											.addReturnInIfBlock(
													currentForIndex, IF);
									if (continueadding) {
										S
												.append(Util
														.formatDecompiledStatement(retst));// if
										// end\n");
										GlobalVariableStore
												.getReturnsAtI()
												.put(new Integer(ifend), "true");
									}
								}
							}
						}
					} else {

					}
				}

			}

		}
	}

	private void addAnyReturnBeforeElseClose(byte[] info, IFBlock IF,
			java.lang.StringBuffer S, int j, OperandStack stack) {

		int end = IF.getElseCloseLineNumber();

		StringBuffer sg = new StringBuffer("");
		boolean r = isInstReturnInst(info, end, sg);
		if (!r) {
			if (info[end] == JvmOpCodes.GOTO) {
				int gotoj = getJumpAddress(info, end);
				sg = new StringBuffer("");
				r = isInstReturnInst(info, gotoj, sg);
			}
		}
		if (r) {
			if (sg.toString().equals("return") == false) {
				if (stack.size() > 0) {
					java.lang.String t = stack.getTopOfStack()
							.getOperandValue();
					boolean b = isMethodRetBoolean(behaviour);
					if (t != null) {
						if (t.equals("1") && b)
							t = "true";
						if (t.equals("0") && b)
							t = "false";
						java.lang.String retst = "return " + t + ";";
						boolean ok = OKToAddReturn(end);
						boolean ok2 = okToEndIfElseWithContinueOrReturn(
								currentForIndex, IF);

						int previousPos = currentForIndex - 1;
						boolean athrowfound = false;
						if (isThisInstrStart(behaviour
								.getInstructionStartPositions(), previousPos)) {
							if (behaviour.getCode()[previousPos] == JvmOpCodes.ATHROW) {
								athrowfound = true;

							}
						}
						if (!athrowfound) {
							if (ok) {
								S.append(Util.formatDecompiledStatement(retst));
								GlobalVariableStore.getReturnsAtI().put(
										new Integer(end), "true");
							}
						}
					}
				}
			}
			if (sg.toString().equals("return") == true) {
				java.lang.String retst = "return ;";
				boolean ok = OKToAddReturn(end);
				boolean ok2 = okToEndIfElseWithContinueOrReturn(
						currentForIndex, IF);
				int previousPos = currentForIndex - 1;
				boolean athrowfound = false;
				if (isThisInstrStart(behaviour.getInstructionStartPositions(),
						previousPos)) {
					if (behaviour.getCode()[previousPos] == JvmOpCodes.ATHROW) {
						athrowfound = true;

					}
				}
				if (!athrowfound) {
					if (ok) {
						if (ok2)
							S.append(Util.formatDecompiledStatement(retst));
						GlobalVariableStore.getReturnsAtI().put(
								new Integer(end), "true");
					}
				}
			}
		}

	}

	private void checkForIFElseEndStatement(byte[] info, Set methodifs, int i,
			StringBuffer reset, OperandStack stack, StringBuffer ifelsecode,
			java.lang.String w)

	{
		int position_i = i;
		List methodifsaslist = IFHelper.sortByToCloseOrder(methodifs);
		// Iterator iter = methodifs.iterator();
		List generatedifsprinted = new ArrayList();
		int y = 0;
		for (; y < methodifsaslist.size(); y++) {

			IFBlock current_ifst = (IFBlock) methodifsaslist.get(y);
			int current_ifCloseLineNo = current_ifst.getIfCloseLineNumber();
			int current_elseCloseLineNo = current_ifst.getElseCloseLineNumber();
			if (current_elseCloseLineNo != -1)// && w.equals("else"))
			{
				if (i == current_elseCloseLineNo) {
					// boolean
					// print=isThisLoopEndAlso(behaviour.getBehaviourLoops(),i);
					boolean addelseend = true;// addElseEnd(i);
					if (addelseend) {
						StringBuffer sb = new StringBuffer();
						java.lang.String lbl = getBranchTypeAtI(i,
								current_ifst, sb);
						java.lang.String lbl2 = getIfElseReturnAtI(i);
						if (lbl2 != null) {
							ifelsecode.append(Util
									.formatDecompiledStatement(lbl2 + ";\n"));
						}

						// BigInteger b
						if (lbl != null && !lbl.equals("")
								&& !lbl.equals("continue") && lbl2 == null) {

							ifelsecode.append(Util
									.formatDecompiledStatement(lbl + "  "
											+ sb.toString() + ";\n"));

						}
						if (lbl != null && !lbl.equals("")
								&& lbl.equals("continue") && lbl2 == null) {
							boolean continueadding = okToEndIfElseWithContinueOrReturn(
									currentForIndex, current_ifst);
							if (continueadding)
								ifelsecode.append(Util
										.formatDecompiledStatement(lbl + "  "
												+ sb.toString() + ";\n"));

						}
						if (lbl2 == null) {
							StringBuffer sr = new StringBuffer("");
							addAnyReturnBeforeElseClose(info, current_ifst, sr,
									currentForIndex, stack);
							ifelsecode.append(sr.toString());
						}

						StringBuffer in = new StringBuffer("");
						StringBuffer t = new StringBuffer("");

						boolean isstore = isStoreInst(i, info, in, t);
						if (isstore) {

							int ind = Integer.parseInt(in.toString());
							if (ind != -1 && opStack.size() > 0) {
								boolean simple = false;
								if ((ind == 0) || (ind == 1) || (ind == 2)
										|| (ind == 3))
									simple = true;
								LocalVariable loc = getLocalVariable(ind,
										"store", t.toString(), simple, i);
								Operand objref = (Operand) opStack.pop(); // Pop
								// The
								// Object
								// Ref
								GlobalVariableStore.setDoNotPop(true);
								if (loc != null
										&& loc.wasCreated()
										&& objref != null
										&& objref.getClassType().trim()
												.length() > 0)
									loc.setDataType(objref.getClassType());
								java.lang.String temp = (java.lang.String) objref
										.getOperandValue();

								if (loc != null
										&& !loc.isDeclarationGenerated()) {
									/**
									 * Push Variable Declaration to Front Fix
									 * For Ternary If Issue
									 */

									boolean prm = loc.isPrimitive();
									boolean bol = loc.isBoolean();
									if (prm) {
										bol = loc.isBoolean();
									}
									java.lang.String inival = "null";
									if (prm && bol) {
										inival = "false";
									}
									if (prm && !bol) {
										inival = "0";
									}
									if (!loc.isMethodParameterVariable()) {
										DecompilerHelper.VariableAtFront vaf = new DecompilerHelper.VariableAtFront(
												loc.getVarName(), loc
														.getDataType().replace(
																'/', '.'),
												inival);

										GlobalVariableStore
												.getVariablesatfront().add(vaf);
									}

									temp = "  " + loc.getVarName() + "=" + temp
											+ ";\n";
									behaviour.appendToBuffer(Util
											.formatDecompiledStatement(temp));

								} else {

									if (loc != null) {
										if (loc.getDataType() != null
												&& loc.getDataType().indexOf(
														"boolean") != -1) {
											if ("1".equals(temp)) {
												temp = "true";
											} else {
												temp = "false";
											}
										}
										temp = "  " + loc.getVarName() + " ="
												+ temp + ";\n";
									} else {
										temp = "  " + "<UNKNOWN VARIABLE>"
												+ " =" + temp + ";\n";
									}

									behaviour.appendToBuffer(Util
											.formatDecompiledStatement(temp));

								}
							} else {
								if (opStack.size() >= 3) {
									GlobalVariableStore.setDoNotPop(true);
									Operand value = opStack.getTopOfStack(); // Value
									Operand index = opStack.getTopOfStack(); // Index
									// into
									// target
									Operand arRef = opStack.getTopOfStack(); // Target
									// Arref
									java.lang.String temp = arRef
											.getOperandValue()
											+ "["
											+ index.getOperandValue()
											+ "] ="
											+ value.getOperandValue()
											+ ";";
									behaviour
											.appendToBuffer("\n"
													+ Util
															.formatDecompiledStatement(temp)
													+ "\n");
								}
							}

						} else {
							if (info[current_ifCloseLineNo] == JvmOpCodes.GOTO) {
								int gotojumpa = getJumpAddress(info,
										current_ifCloseLineNo);
								if (isThisInstrStart(behaviour
										.getInstructionStartPositions(),
										gotojumpa)) {
									in = new StringBuffer("");
									t = new StringBuffer("");

									isstore = isStoreInst(gotojumpa, info, in,
											t);
									if (isstore) {
										int ind = Integer.parseInt(in
												.toString());
										if (ind != -1 && opStack.size() > 0) {
											boolean simple = false;
											if ((ind == 0) || (ind == 1)
													|| (ind == 2) || (ind == 3))
												simple = true;
											LocalVariable loc = getLocalVariable(
													ind, "store", t.toString(),
													simple, gotojumpa);
											Operand objref = (Operand) opStack
													.pop(); // Pop The Object
											// Ref
											GlobalVariableStore
													.setDoNotPop(true);
											if (loc != null
													&& loc.wasCreated()
													&& objref != null
													&& objref.getClassType()
															.trim().length() > 0)
												loc.setDataType(objref
														.getClassType());
											java.lang.String temp = (java.lang.String) objref
													.getOperandValue();
											if (loc != null
													&& !loc
															.isDeclarationGenerated()) {
												/**
												 * Push Variable Declaration to
												 * Front Fix For Ternary If
												 * Issue
												 */

												boolean prm = loc.isPrimitive();
												boolean bol = loc.isBoolean();
												if (prm) {
													bol = loc.isBoolean();
												}
												java.lang.String inival = "null";
												if (prm && bol) {
													inival = "false";
												}
												if (prm && !bol) {
													inival = "0";
												}
												if (!loc
														.isMethodParameterVariable()) {
													DecompilerHelper.VariableAtFront vaf = new DecompilerHelper.VariableAtFront(
															loc.getVarName(),
															loc
																	.getDataType()
																	.replace(
																			'/',
																			'.'),
															inival);

													GlobalVariableStore
															.getVariablesatfront()
															.add(vaf);
												}

												temp = "  " + loc.getVarName()
														+ "=" + temp + ";\n";
												behaviour
														.appendToBuffer(Util
																.formatDecompiledStatement(temp));

											} else {

												if (loc != null)
													temp = "  "
															+ loc.getVarName()
															+ " =" + temp
															+ ";\n";
												else
													temp = "  "
															+ "<UNKNOWN VARIABLE>"
															+ " =" + temp
															+ ";\n";
												behaviour
														.appendToBuffer(Util
																.formatDecompiledStatement(temp));

											}
										} else {
											if (opStack.size() >= 3) {
												GlobalVariableStore
														.setDoNotPop(true);
												Operand value = opStack
														.getTopOfStack(); // Value
												Operand index = opStack
														.getTopOfStack(); // Index
												// into
												// target
												Operand arRef = opStack
														.getTopOfStack(); // Target
												// Arref
												java.lang.String temp = arRef
														.getOperandValue()
														+ "["
														+ index
																.getOperandValue()
														+ "] ="
														+ value
																.getOperandValue()
														+ ";";
												behaviour
														.appendToBuffer("\n"
																+ Util
																		.formatDecompiledStatement(temp)
																+ "\n");
											}
										}

									} else {
										if (info[gotojumpa] == JvmOpCodes.PUTFIELD) {
											// Add here
											GlobalVariableStore
													.setDoNotPop(true);
											int pos = getOffset(info, gotojumpa);

											FieldRef fref = cd
													.getFieldRefAtCPoolPosition(pos);
											Operand value = opStack
													.getTopOfStack();
											Operand objRef = null;
											objRef = checkAnyStoredPUTFIELDObjRef(gotojumpa);
											if (objRef == null
													&& opStack.size() > 0) {
												objRef = opStack
														.getTopOfStack();
											}

											java.lang.String temp = "";
											java.lang.String freftype = fref
													.getTypeoffield();
											if (objRef != null) {
												StringBuffer stb = new StringBuffer(
														"");
												checkForImport(objRef
														.getOperandValue(), stb);
												temp = stb.toString()
														+ "."
														+ fref.getFieldName()
														+ " = "
														+ value
																.getOperandValue()
														+ ";";
											} else {
												temp = fref.getFieldName()
														+ " = "
														+ value
																.getOperandValue()
														+ ";";
											}
											behaviour
													.appendToBuffer("\n"
															+ Util
																	.formatDecompiledStatement(temp)
															+ "\n");
										}
										if (info[gotojumpa] == JvmOpCodes.PUTSTATIC) {
											GlobalVariableStore
													.setDoNotPop(true);
											int pos = getOffset(info, gotojumpa);
											/*
											 * parsedString+="PUTSTATIC\t";
											 * parsedString+="#"+pos;
											 * parsedString+="\n";
											 * parsedString+="\t";parsedString+="\t";
											 */
											FieldRef fref = cd
													.getFieldRefAtCPoolPosition(pos);
											Operand value = opStack
													.getTopOfStack();
											java.lang.String freftype = fref
													.getTypeoffield();

											// For the code statement
											int classpointer = fref
													.getClassPointer();
											ClassInfo cinfo = cd
													.getClassInfoAtCPoolPosition(classpointer);
											java.lang.String classname = cd
													.getUTF8String(cinfo
															.getUtf8pointer());
											java.lang.String v = value
													.getOperandValue()
													.toString();
											if (v.indexOf("(") == -1
													&& v.indexOf(")") != -1) {
												v = v.replaceAll("\\)", "");

											}
											v = v.trim();
											StringBuffer stb = new StringBuffer(
													"");
											checkForImport(classname, stb);
											java.lang.String temp = stb
													.toString()
													+ "."
													+ fref.getFieldName()
													+ " = " + v + ";";
											behaviour
													.appendToBuffer("\n"
															+ Util
																	.formatDecompiledStatement(temp)
															+ "\n");
										}

									}

								}
							}
						}

						checkForATHROWAtIFelseEnd(current_ifst, ifelsecode, i);
						if (!current_ifst.getDonotclose()
								&& current_ifst.hasMatchingElseBeenGenerated()
								&& !current_ifst.elseHasBeenClosed()) {
							ifelsecode.append(Util
									.formatDecompiledStatement("}\n")); // Else
							// End\n");
							current_ifst.setElseHasBeenClosed(true);
							reset.append("true");
						}

						// elseendsadded.add(new Integer(i));
					}
					// ABove commmented by belurs
					// ifHashTable.remove(key);
					// ifLevel--;
				}

			}
			// else
			// {
			if (i == current_ifCloseLineNo)// && w.equals("if"))// &&
			// !current_ifst.hasIfBeenClosed())
			{
				boolean labelOrReturnAdded = false;

				// boolean
				// print=isThisLoopEndAlso(behaviour.getBehaviourLoops(),i);
				// System.out.println(print+"print"+i);
				// boolean print
				// =isThisIfALoopCondition(current_ifst,info,behaviour.getBehaviourLoops());
				// if(!print) // TODO:
				// {
				if (current_ifst.getIfStart() == current_ifst
						.getIfCloseLineNumber()) {
					// ifelsecode
					// return;
				}

				boolean skip = accountForAnyDoWhileIF(currentForIndex,
						current_ifst);
				StringBuffer sb = new StringBuffer();
				java.lang.String lbl = getBranchTypeAtI(i, current_ifst, sb);
				// System.out.println(lbl+"lbl"+i);
				java.lang.String lbl2 = getIfElseReturnAtI(i);
				if (lbl2 != null && lbl2.trim().length() > 0) {
					ifelsecode.append(Util.formatDecompiledStatement(lbl2
							+ ";\n"));
					labelOrReturnAdded = true;
				}

				if (lbl != null && !lbl.trim().equals("")
						&& !lbl.equals("continue") && lbl2 == null) {
					// codeStatements+=
					// Util.formatDecompiledStatement(lbl+";\n");
					int previousPos = currentForIndex - 1;
					boolean athrowfound = false;
					boolean prevretfound = false;
					if (isThisInstrStart(behaviour
							.getInstructionStartPositions(), previousPos)) {
						if (behaviour.getCode()[previousPos] == JvmOpCodes.ATHROW) {
							athrowfound = true;
						}
						IFinder branchFinder = FinderFactory
								.getFinder(IFinder.BRANCH);
						if (branchFinder.isInstReturnInst(previousPos,
								new StringBuffer())) {
							prevretfound = true;
						}

					}
					if (!athrowfound && !prevretfound)
						ifelsecode.append(Util.formatDecompiledStatement(lbl
								+ "  " + sb.toString() + ";\n"));
					labelOrReturnAdded = true;

				}
				if (lbl != null && !lbl.trim().equals("")
						&& lbl.equals("continue") && lbl2 == null) {
					boolean continueadding = okToEndIfElseWithContinueOrReturn(
							currentForIndex, current_ifst);
					if (continueadding) {
						ifelsecode.append(Util.formatDecompiledStatement(lbl
								+ "  " + sb.toString() + ";\n"));
						labelOrReturnAdded = true;
					}

				}
				if (lbl2 == null) {
					StringBuffer sr = new StringBuffer("");
					addAnyReturnBeforeIfClose(info, current_ifst, sr,
							currentForIndex, stack);
					if (sr.toString().trim().length() > 0) {
						ifelsecode.append(sr.toString());
						labelOrReturnAdded = true;
					}
				}
				int nextinstrpos = -1;
				int gotojumpvalue = -1;
				if (info[i] == JvmOpCodes.GOTO) {
					nextinstrpos = i + 3;
					int gotojumpa = getJumpAddress(info, i);
					gotojumpvalue = gotojumpa;
					boolean k = storealreadyhandledatifend(i);
					if (isThisInstrStart(behaviour
							.getInstructionStartPositions(), gotojumpa)
							&& !k) {
						StringBuffer in = new StringBuffer("");
						StringBuffer t = new StringBuffer("");

						boolean isstore = isStoreInst(gotojumpa, info, in, t);
						if (isstore) {
							int ind = Integer.parseInt(in.toString());
							if (ind != -1 && opStack.size() > 0) {
								boolean simple = false;
								if ((ind == 0) || (ind == 1) || (ind == 2)
										|| (ind == 3))
									simple = true;
								LocalVariable loc = getLocalVariable(ind,
										"store", t.toString(), simple,
										gotojumpa);
								Operand objref = (Operand) opStack.pop(); // Pop
								// The
								// Object
								// Ref
								GlobalVariableStore.setDoNotPop(true);
								if (loc != null
										&& loc.wasCreated()
										&& objref != null
										&& objref.getClassType().trim()
												.length() > 0)
									loc.setDataType(objref.getClassType());
								java.lang.String temp = (java.lang.String) objref
										.getOperandValue();
								if (loc != null
										&& !loc.isDeclarationGenerated()) {
									/**
									 * Push Variable Declaration to Front Fix
									 * For Ternary If Issue
									 */

									boolean prm = loc.isPrimitive();
									boolean bol = loc.isBoolean();
									if (prm) {
										bol = loc.isBoolean();
									}
									java.lang.String inival = "null";
									if (prm && bol) {
										inival = "false";
									}
									if (prm && !bol) {
										inival = "0";
									}
									if (!loc.isMethodParameterVariable()) {
										DecompilerHelper.VariableAtFront vaf = new DecompilerHelper.VariableAtFront(
												loc.getVarName(), loc
														.getDataType().replace(
																'/', '.'),
												inival);

										GlobalVariableStore
												.getVariablesatfront().add(vaf);
									}

									temp = "  " + loc.getVarName() + "=" + temp
											+ ";\n";
									behaviour.appendToBuffer(Util
											.formatDecompiledStatement(temp));

								} else {

									if (loc != null) {
										if (loc.getDataType() != null
												&& loc.getDataType().indexOf(
														"boolean") != -1) {
											if ("1".equals(temp)) {
												temp = "true";
											} else {
												temp = "false";
											}
										}
										temp = "  " + loc.getVarName() + " ="
												+ temp + ";\n";
									}

									else
										temp = "  " + "<UNKNOWN VARIABLE>"
												+ " =" + temp + ";\n";
									behaviour.appendToBuffer(Util
											.formatDecompiledStatement(temp));

								}
								storesatifend.add(new Integer(i));
							} else {
								if (opStack.size() >= 3) {
									GlobalVariableStore.setDoNotPop(true);
									Operand value = opStack.getTopOfStack(); // Value
									Operand index = opStack.getTopOfStack(); // Index
									// into
									// target
									Operand arRef = opStack.getTopOfStack(); // Target
									// Arref
									java.lang.String temp = arRef
											.getOperandValue()
											+ "["
											+ index.getOperandValue()
											+ "] ="
											+ value.getOperandValue()
											+ ";";
									behaviour
											.appendToBuffer("\n"
													+ Util
															.formatDecompiledStatement(temp)
													+ "\n");
									storesatifend.add(new Integer(i));
								}
							}

						} else {
							if (info[gotojumpa] == JvmOpCodes.PUTFIELD) {
								// Add here
								GlobalVariableStore.setDoNotPop(true);
								int pos = getOffset(info, gotojumpa);

								FieldRef fref = cd
										.getFieldRefAtCPoolPosition(pos);
								Operand value = opStack.getTopOfStack();
								Operand objRef = opStack.getTopOfStack();
								putfieldObjRefMap.put(new Integer(gotojumpa),
										objRef);
								java.lang.String freftype = fref
										.getTypeoffield();
								StringBuffer stb = new StringBuffer("");
								checkForImport(objRef.getOperandValue(), stb);
								java.lang.String temp = stb.toString() + "."
										+ fref.getFieldName() + " = "
										+ value.getOperandValue() + ";";
								behaviour.appendToBuffer("\n"
										+ Util.formatDecompiledStatement(temp)
										+ "\n");
							}
							if (info[gotojumpa] == JvmOpCodes.PUTSTATIC) {
								GlobalVariableStore.setDoNotPop(true);
								int pos = getOffset(info, gotojumpa);
								/*
								 * parsedString+="PUTSTATIC\t";
								 * parsedString+="#"+pos; parsedString+="\n";
								 * parsedString+="\t";parsedString+="\t";
								 */
								FieldRef fref = cd
										.getFieldRefAtCPoolPosition(pos);
								Operand value = opStack.getTopOfStack();
								java.lang.String freftype = fref
										.getTypeoffield();

								// For the code statement
								int classpointer = fref.getClassPointer();
								ClassInfo cinfo = cd
										.getClassInfoAtCPoolPosition(classpointer);
								java.lang.String classname = cd
										.getUTF8String(cinfo.getUtf8pointer());
								java.lang.String v = value.getOperandValue()
										.toString();
								if (v.indexOf("(") == -1
										&& v.indexOf(")") != -1) {
									v = v.replaceAll("\\)", "");

								}
								v = v.trim();
								StringBuffer stb = new StringBuffer("");
								checkForImport(classname, stb);
								java.lang.String temp = stb.toString() + "."
										+ fref.getFieldName() + " = " + v + ";";
								behaviour.appendToBuffer("\n"
										+ Util.formatDecompiledStatement(temp)
										+ "\n");
							}

						}

					}
				}
				if (!current_ifst.IfHasBeenClosed()
						&& current_ifst.getDonotclose() == false) {

					// Note: sbelur
					// Take if end reset due to do-while kind of loops into
					// account here
					boolean ifendreset = false;
					java.lang.String e = (java.lang.String) ifsbeginends_dowhile
							.get("" + current_ifst.getIfStart());
					int ie = -1;
					if (e != null) {
						try {
							ie = Integer.parseInt(e);
						} catch (NumberFormatException ne) {
							e = null;
						}
					}

					if (e != null) {
						int currentclose = current_ifst.getIfCloseLineNumber();
						if (currentclose != ie && ie != -1 && ie > currentclose) {
							current_ifst.setIfCloseLineNumber(ie);
							ifendreset = true;
						}
					}

					// NOTE: sbelur
					// Changes below are to fix the bug caused by if @ end of
					// this if's loop
					// that if body had to be closed before this if loop could
					// be closed

					if (!skip && !ifendreset) {
						checkForATHROWAtIFelseEnd(current_ifst, ifelsecode, i);
						// //if end +current_ifst.getIfStart()

						// TODO: w.r.t goto at if end
						// Fix for infinite.java
						if (gotojumpvalue != -1 && nextinstrpos != -1
								&& !labelOrReturnAdded) {

							if (gotojumpvalue > position_i + 3) {
								int nextpos = DecompilerHelper
										.getNextInstrPos(nextinstrpos);
								int p = i - 1;
								boolean invalid = false;
								if (isThisInstrStart(behaviour
										.getInstructionStartPositions(), p)) {
									if (info[p] == JvmOpCodes.ATHROW) {
										invalid = true;
									}
								}
								if (!invalid
										&& !TryHelper
												.doesHandlerBlockStartAtIndex(nextinstrpos)) {

									if (gotojumpvalue > nextpos) {
										boolean correct_if = false;
										IFinder branchFinder = FinderFactory
												.getFinder(IFinder.BRANCH);
										for (int from = i - 1; from >= 0; from--) {
											if (isThisInstrStart(
													behaviour
															.getInstructionStartPositions(),
													from)) {

												if (branchFinder
														.isInstructionIF(from)) {
													if (from == current_ifst
															.getIfStart()) {

														correct_if = true;
														break;
													} else {
														correct_if = false;
														break;
													}
												}
											}
										}

										if (correct_if) {
											DecompilerHelper.VariableAtFront vaf = new DecompilerHelper.VariableAtFront(
													"JdecGenerated_" + i,
													"boolean".replace('/', '.'),
													"true");

											generatedIfTracker.add(
													current_ifst, nextinstrpos,
													gotojumpvalue,
													"JdecGenerated_" + i);
											if (!generatedifsprinted
													.contains(new Integer(i))) {

												GlobalVariableStore
														.getVariablesatfront()
														.add(vaf);
												behaviour
														.appendToBuffer((Util
																.formatDecompiledStatement("\nJdecGenerated_"
																		+ i
																		+ "=false;\n")));

												generatedifsprinted
														.add(new Integer(i));
											}
										}
									}
								}
							}

						}
						// ifend" + current_ifCloseLineNo + ":" +
						// current_ifst.getIfStart() +
						if (currentForIndex != current_ifst.getIfStart())
							ifelsecode.append(Util
									.formatDecompiledStatement("\n}"
									// +current_ifst.getIfStart()+"-"+current_ifst.getIfCloseLineNumber()+"-"+currentForIndex
											+ "\n"));// if//ifend
						else {
							Loop clashedloop = IFHelper
									.getClashingLoopIfEndAtStartWRTALoop(current_ifst);
							if (clashedloop != null) {
								current_ifst.setIfCloseLineNumber(clashedloop
										.getEndIndex());
							} else {
								ifelsecode
										.append(Util
												.formatDecompiledStatement("\n}"
														+ "\n"));// if//ifend
							}
						}
						current_ifst.setIfHasBeenClosed(true);
						// end\nIFEND"+current_ifst.getIfCloseLineNumber()+"");
						int loopBeginForIf = IFHelper
								.attachMarkerToIfInDoWhileKindOfLoop(current_ifst
										.getIfStart());
						boolean skipBreakCheck = false;
						if (loopBeginForIf != -1) {
							if (GlobalVariableStore.getDowhileloopmarkers() != null
									&& GlobalVariableStore
											.getDowhileloopmarkers()
											.contains(
													new Integer(loopBeginForIf))) {
								ifelsecode.append("\n<IF_AT_LOOP_END_END_"
										+ loopBeginForIf + "_>\n");
								skipBreakCheck = true;
							}
						}

						boolean add = true;
						if (GlobalVariableStore.getBranchLabels() != null
								&& !skipBreakCheck) {
							Iterator iterator = GlobalVariableStore
									.getBranchLabels().entrySet().iterator();
							while (iterator.hasNext()) {
								Entry entry = (Entry) iterator.next();
								DecompilerHelper.BranchLabel bl = (DecompilerHelper.BranchLabel) entry
										.getKey();
								if (bl.getLBL() != null
										&& bl.getLBL().trim().equals("break")) {
									IFBlock IF = bl.getIF();
									if (IF.getIfCloseLineNumber() == i) {
										add = false;
									}
								}
							}
						}
						java.lang.String s = "";
						boolean elseadded = false;
						// BUGGY Charinfo
						if (add && !skipBreakCheck) {
							s = addAnyElseBreakForIFChain(current_ifst
									.getIfStart());
							ifelsecode.append(s);
							if (s.trim().length() > 0) {
								elseadded = true;
								current_ifst.setElsebreakadded(true);
							}
						}
						if (s.trim().length() == 0 && !skipBreakCheck) {
							boolean alreadygenerated = false;
							boolean loopEndalso = LoopHelper.isThisLoopEndAlso(
									behaviour.getBehaviourLoops(),
									currentForIndex, current_ifst.getIfStart());
							if (loopEndalso) {
								IFBlock associatedIf = IFHelper
										.getAssociatedIfAt(currentForIndex);
								if (associatedIf != null
										&& current_ifst.getIfStart() == associatedIf
												.getIfStart()
										&& current_ifst
												.getIfCloseFromByteCode() > currentForIndex) {
									alreadygenerated = true;
									java.lang.String elsestmt = "\nelse\n{\nbreak;\n}\n";
									ifelsecode
											.append(Util
													.formatDecompiledStatement(elsestmt));
									elseadded = true;
								}
							}
						}
						if (!elseadded && s.length() == 0 && !skipBreakCheck) {
							boolean elligible = BranchHelper.checkForReturn(
									behaviour.getCode(), currentForIndex - 1);
							if (!elligible) {
								if (isThisInstrStart(behaviour
										.getInstructionStartPositions(),
										currentForIndex - 1)) {
									if (info[currentForIndex - 1] == JvmOpCodes.ATHROW) {
										elligible = true;
									}
								}
							}

							if (elligible && !skipBreakCheck) {
								int reqdpos = -1;
								int ifstart = current_ifst.getIfStart();
								for (int u = ifstart + 1; u < currentForIndex; u++) {
									if (isThisInstrStart(behaviour
											.getInstructionStartPositions(), u)) {
										boolean isif = isNextInstructionIf(info[u]);
										if (isif) {
											int newreqdpos = getJumpAddress(
													info, u);
											if (reqdpos == -1)
												reqdpos = newreqdpos;
											else {
												if (newreqdpos < reqdpos) {
													reqdpos = newreqdpos;
												}
											}
										}
									}

								}
								if (reqdpos != -1 && !skipBreakCheck
										&& reqdpos > currentForIndex) {
									elseadded = true;
									current_ifst
											.setHasMatchingElseBeenGenerated(true);
									current_ifst
											.setElseCloseLineNumber(reqdpos);
									ifelsecode
											.append(Util
													.formatDecompiledStatement("\nelse"
															+ currentForIndex
															+ "\n{\n"));
								}

							}
						}
						if (!elseadded && s.length() == 0 && !skipBreakCheck) {
							int byc = current_ifst.getIfCloseFromByteCode();
							if (byc < current_ifst.getIfStart()) {
								Loop ploop = LoopHelper.getLoopGivenStart(byc);
								if (ploop != null) {
									if (info[currentForIndex] == JvmOpCodes.GOTO) {
										int gj = FinderFactory.getFinder(
												IFinder.BASE).getJumpAddress(
												currentForIndex);
										Loop ifloop = LoopHelper
												.getLoopGivenStart(gj);
										if (ifloop != null) {
											for (int f = ifloop.getStartIndex(); f <= current_ifst
													.getIfStart(); f++) {
												if (isThisInstrStart(
														behaviour
																.getInstructionStartPositions(),
														f)) {
													if (isInstructionIF(info[f])) {
														if (f == current_ifst
																.getIfStart()) {
															if (ploop
																	.getStartIndex() < ifloop
																	.getStartIndex()) {
																if (ploop
																		.getEndIndex() >= ifloop
																		.getEndIndex()) {
																	elseadded = true;
																	current_ifst
																			.setHasMatchingElseBeenGenerated(true);
																	java.lang.String elsestmt = "\nelse\n{\nbreak;\n}\n";
																	ifelsecode
																			.append(Util
																					.formatDecompiledStatement(elsestmt));
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}

					}

				}

				// }

			}

			// }
		}

	}

	private boolean accountForAnyDoWhileIF(int current, IFBlock ifst) {
		boolean skip = false;
		ArrayList loops = behaviour.getBehaviourLoops();
		ArrayList starts = behaviour.getInstructionStartPositions();
		byte[] code = behaviour.getCode();
		Loop l = getLoopGivenEnd(current, loops);
		if (l != null) {
			boolean isif = isInstructionIF(code[current]);
			if (isif && isThisInstrStart(starts, current)) {
				int jump = getJumpAddress(code, current);
				if (jump < current && jump == l.getStartIndex()) {
					int start = jump;
					int till = current;
					for (int z = start; z < till; z++) {
						boolean st = isThisInstrStart(starts, z);
						if (st) {
							isif = isInstructionIF(code[z]);
							if (isif) {
								int passedIfStart = ifst.getIfStart();
								int passedIfbyend = ifst
										.getIfCloseFromByteCode();
								if (passedIfStart == z) {
									if (passedIfbyend > l.getEndIndex()) {
										start = current + 3;
										for (int m = start; m < passedIfbyend; m++) {
											boolean ret = checkForReturn(code,
													m);
											if (ret) {
												// need to reset end of ifst and
												// loop here
												// also for found if at current
												ifst
														.setIfCloseLineNumber(passedIfbyend);
												l
														.setLoopendDueToReset(passedIfbyend);
												l.setLoopendreset(true);
												skip = true;

												// Need to check for any changes
												// to be done in branchLabels
												checkForChangeInBranchLabels(ifst);

												return skip;
											} else // check for athrow
											{
												st = isThisInstrStart(starts, m);
												if (st) {
													if (code[m] == JvmOpCodes.ATHROW) {
														// need to reset end of
														// ifst and loop here
														// also for found if at
														// current
														ifst
																.setIfCloseLineNumber(passedIfbyend);
														l
																.setLoopendDueToReset(passedIfbyend);
														l.setLoopendreset(true);
														skip = true;
														// Need to check for any
														// changes to be done in
														// branchLabels
														checkForChangeInBranchLabels(ifst);
														return skip;
													}
												}
											}

										}

									}
								}
							}

						}
					}

				}
			}

		}
		return skip;
	}

	/*
	 * private boolean pushOnStackForStoreInst(byte[] info,int cur,boolean type) {
	 * if(type) { int next=cur+1; switch(info[next]) {
	 * 
	 * case JvmOpCodes.IADD: case JvmOpCodes.IAND: case JvmOpCodes.I } } else { } }
	 */

	// belurs:
	/***************************************************************************
	 * NOTE: Currently This method takes into account only dup and dup2 NOT
	 * dup2_x1 and dup_x2 So the jdec might not output correctly if the dup inst
	 * if one oisStoreInstf dup2_x1 and dup_x2 TODO: Need to revisit This Item
	 * and fix the problem This method is used by primitive store blocks to
	 * check whether to print Or add to stack
	 */

	private boolean isPrevInstDup(byte[] info, int cur) {

		if (isThisInstrStart(behaviour.getInstructionStartPositions(), cur - 1)
				&& (info[cur - 1] == JvmOpCodes.DUP || info[cur - 1] == JvmOpCodes.DUP2))
			return true;
		else
			return false;

	}

	private boolean isInstDup(byte[] info, int cur) {

		if (isThisInstrStart(behaviour.getInstructionStartPositions(), cur)
				&& (info[cur] == JvmOpCodes.DUP || info[cur] == JvmOpCodes.DUP2))
			return true;
		else
			return false;
	}

	private void popFromStackIFNec(byte[] info, int cur, OperandStack stack)

	{
		ArrayList list = behaviour.getInstructionStartPositions();

		if (info[cur - 5] == JvmOpCodes.INVOKEINTERFACE
				&& isThisInstrStart(list, cur - 5)) {
			stack.getTopOfStack();
		}
		if (info[cur - 3] == JvmOpCodes.INVOKESPECIAL
				&& isThisInstrStart(list, cur - 3)) {
			stack.getTopOfStack();
		}
		if (info[cur - 3] == JvmOpCodes.INVOKEVIRTUAL
				&& isThisInstrStart(list, cur - 3)) {
			stack.getTopOfStack();
		}
		if (info[cur - 3] == JvmOpCodes.INVOKESTATIC
				&& isThisInstrStart(list, cur - 3)) {
			stack.getTopOfStack();
		}

	}

	private void handleSimpleLStoreCase(OperandStack stack, byte[] info,
			int index) {

		int opcodeval;
		switch (index) {

		case 0:
			opcodeval = JvmOpCodes.LSTORE_0;
			break;
		case 1:
			opcodeval = JvmOpCodes.LSTORE_1;
			break;
		case 2:
			opcodeval = JvmOpCodes.LSTORE_2;
			break;
		default:
			opcodeval = JvmOpCodes.LSTORE_3;
			break;
		}

		execute(InstrConstants.STORE_INSTR_TYPE, InstrConstants.LONG, opcodeval);
	}

	private int handleSimpleFstoreCaseInst(OperandStack stack, byte[] info,
			int index) {

		int opcodeval;

		switch (index) {
		case 0:
			opcodeval = JvmOpCodes.FSTORE_0;
			break;
		case 1:
			opcodeval = JvmOpCodes.FSTORE_1;
			break;
		case 2:
			opcodeval = JvmOpCodes.FSTORE_2;
			break;
		default:
			opcodeval = JvmOpCodes.FSTORE_3;
			break;
		}

		return execute(InstrConstants.STORE_INSTR_TYPE, InstrConstants.FLOAT,
				opcodeval);
	}

	private int handleSimpleDstoreCaseInst(int opcodevalue) {
		return execute(InstrConstants.STORE_INSTR_TYPE, InstrConstants.DOUBLE,
				opcodevalue);
	}

	private Hashtable anewarrayrefpos;

	private boolean processANEWARRAYb4Invoke(byte[] code, int s,
			StringBuffer invokepos) {

		int startpos = s + 2 + 1;
		ArrayList starts = behaviour.getInstructionStartPositions();
		boolean donotprocess = false;
		boolean foundinvoke = false;
		int pos = -1;
		for (int k = startpos; k < code.length; k++) {

			boolean startinst = isThisInstrStart(starts, k);
			if (code[k] == JvmOpCodes.INVOKEINTERFACE
					|| code[k] == JvmOpCodes.INVOKEVIRTUAL
					|| code[k] == JvmOpCodes.INVOKESTATIC) {
				foundinvoke = true;
				pos = k;
				break;
			}
		}

		if (pos != -1) {
			for (int k = startpos; k < pos; k++) {
				boolean startinst = isThisInstrStart(starts, k);
				if (startinst) {

					switch (code[k]) {

					case JvmOpCodes.NEW:
					case JvmOpCodes.NEWARRAY:
					case JvmOpCodes.MULTIANEWARRAY:
						invokepos.append(pos);
						donotprocess = true;
						return donotprocess;

					}

				}
			}
		}

		return donotprocess;

	}

	private void resetMethodParameters(OperandStack stack,
			ArrayList methodParams, int j) {
		// Check if reset needs to be done in the first place
		if (GlobalVariableStore.getProblematicInvokes() == null
				|| GlobalVariableStore.getProblematicInvokes().size() == 0)
			return;
		boolean ok = false;
		for (int n = 0; n < GlobalVariableStore.getProblematicInvokes().size(); n++) {

			Integer in = (Integer) GlobalVariableStore.getProblematicInvokes()
					.get(n);
			if (in.intValue() == j) {
				ok = true; // Yes jdec
				break;
			} else
				ok = false;

		}
		if (!ok)
			return;

		// some validations
		if (methodParams == null || stack == null)
			return;
		int count = methodParams.size();
		if (count == 0)
			return;
		if (stack.size() < count)
			return; // Should Not happen: A Bug in jdec
		Operand reqdops[] = new Operand[count];
		boolean needtoresetstack = false;
		for (int h = 0; h < count; h++) {
			reqdops[h] = stack.getTopOfStack();
			needtoresetstack = true;
		}

		// IMPORTANT : IF needtoresetstack IS TRUE method shud return only after
		// resetting stack .NOT before that

		// start Resetting
		int opstart = 0;
		for (int z = count - 1; z >= 0; z--) {

			Operand current = reqdops[opstart];
			opstart++;
			if (current != null) {
				java.lang.String type = current.getLocalVarType();
				boolean needtoreset = false;
				java.lang.String param = (java.lang.String) methodParams.get(z);
				if (type != null && type.trim().equals("int")) {

					if (param.trim().equals("byte")) {
						needtoreset = true;
					} else if (param.trim().equals("boolean")) {
						needtoreset = true;
					} else if (param.trim().equals("short")) {
						needtoreset = true;
					} else if (param.trim().equals("char")) {
						needtoreset = true;
					} else {
						needtoreset = false;
					}
					if (needtoreset) {
						int localIndex = current.getLocalVarIndex();
						java.lang.String searchFor = "#REPLACE_INT_"
								+ localIndex + "#";
						int len = searchFor.length();
						int start = behaviour.getCodeStatements().indexOf(
								searchFor);
						java.lang.String name = "";
						if (start != -1) {
							java.lang.String tp = behaviour.getCodeStatements()
									.substring(start + len, start + len + 3);
							int equalTo = -1;
							StringBuffer newpos = new StringBuffer("");
							if (tp.equals("int")) {
								// java.lang.String=codeStatements.replaceFirst(searchFor+"int",param.trim());
								name = (java.lang.String) current
										.getOperandValue();

								behaviour
										.replaceBuffer(getStringAfterReplacement(
												start, searchFor + "int", param
														.trim(), newpos, name,
												false, param.trim()));

							}
							try {
								equalTo = Integer.parseInt(newpos.toString());
							} catch (NumberFormatException ne) {
								equalTo = -1;
							}

							if (equalTo != -1) {
								int valueIn = behaviour.getCodeStatements()
										.indexOf("#VALUE" + localIndex + "#",
												equalTo);
								if (valueIn != -1) {
									java.lang.String valuehash = "#VALUE"
											+ localIndex + "#";
									java.lang.String val = behaviour
											.getCodeStatements().substring(
													valueIn
															+ valuehash
																	.length(),
													valueIn
															+ valuehash
																	.length()
															+ 1);

									behaviour
											.replaceBuffer(getStringAfterReplacement(
													valueIn, "#VALUE"
															+ localIndex + "#",
													val, newpos, name, true,
													param.trim()));
								}
							}
						}

					}

				} else if (type != null && type.trim().equals("")) {

					if (param.trim().equals("byte")
							|| param.trim().equals("short")
							|| param.trim().equals("char")) {

						current.setOperandValue("(" + param + ")"
								+ current.getOperandValue());

					}
				}
				// Check for multidimensional.....

				if (param.trim().indexOf("[") != -1) {
					int first = param.trim().indexOf("[");
					int last = param.trim().lastIndexOf("[");
					int howmany = last - first + 1;
					boolean isMulti = current.isMultiDimension();
					if (isMulti) {
						java.lang.String value = (java.lang.String) current
								.getOperandValue();
						/*
						 * if(value.indexOf("[")!=-1) { /*int cnt=1; int
						 * start=value.indexOf("["); int next=start+1;
						 * while(next < value.length()) {
						 * 
						 * if(value.charAt(next)=='[') { cnt++; } next++; }
						 * 
						 * int total=cnt+howmany;
						 */
						java.lang.String bracks = "";
						for (int s = 0; s < howmany; s++) {

							bracks += "[]";
						}
						value += bracks;
						current.setOperandValue(value);

					}

				}

			} else // Again should not happen at all. Returning if this happens
			// , so that jdec will not reset a wrong operand's value
			{
				// Restore Stack
				for (int l = reqdops.length - 1; l >= 0; l--) {
					Operand op = reqdops[l];
					stack.push(op);
				}
				return;
			}

		}

		// This should be final step before returning
		if (needtoresetstack) {
			// Restore Stack
			for (int l = reqdops.length - 1; l >= 0; l--) {
				Operand op = reqdops[l];
				stack.push(op);
			}
		}

	}

	private java.lang.String getStringAfterReplacement(int fromwhere,
			java.lang.String lookfor, java.lang.String replaceString,
			StringBuffer sb, java.lang.String name, boolean skipone,
			java.lang.String methodparam) {
		int equal = -1;
		java.lang.String codeasstring = behaviour.getCodeAsBuffer().toString();
		java.lang.String orig = codeasstring;
		java.lang.String temp1 = codeasstring.substring(0, fromwhere);
		java.lang.String temp2 = replaceString;
		java.lang.String temp3 = "";
		if (skipone) {
			if (methodparam.equalsIgnoreCase("boolean")) {

				if (replaceString.trim().equalsIgnoreCase("0")) {
					temp2 = "false";
				} else if (replaceString.trim().equalsIgnoreCase("1")) {
					temp2 = "true";
				} else {

				}

			}
			temp3 = codeasstring.substring(fromwhere + lookfor.length() + 1);
		} else {
			temp3 = codeasstring.substring(fromwhere + lookfor.length());
		}

		orig = temp1 + temp2 + temp3;
		equal = orig.indexOf("=", orig.indexOf(replaceString + "\t" + name));
		sb.append(equal);
		return orig;
	}

	private boolean isNextInstAStore(byte[] info, int pos) {

		if (isThisInstrStart(behaviour.getInstructionStartPositions(), pos)) {
			if (info[pos] == JvmOpCodes.ASTORE) {

				return true;
			}
			if (info[pos] == JvmOpCodes.ASTORE_0) {
				return true;

			}
			if (info[pos] == JvmOpCodes.ASTORE_1) {

				return true;
			}
			if (info[pos] == JvmOpCodes.ASTORE_2) {
				return true;

			}
			if (info[pos] == JvmOpCodes.ASTORE_3) {
				return true;

			}
		}
		return false;

	}

	private int handleACONSTNULL() {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.OBJECT,
				JvmOpCodes.ACONST_NULL);
	}

	private int handlesimpleaload(int index) {

		int opcodeval;
		switch (index) {
		case 0:
			opcodeval = JvmOpCodes.ALOAD_0;
			break;
		case 1:
			opcodeval = JvmOpCodes.ALOAD_1;
			break;
		case 2:
			opcodeval = JvmOpCodes.ALOAD_2;
			break;
		default:
			opcodeval = JvmOpCodes.ALOAD_3;
			break;
		}
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.OBJECT,
				opcodeval);

	}

	private int handleARRAYLENGTHCase() {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.OBJECT,
				JvmOpCodes.ARRAYLENGTH);
	}

	private int handleARETURNCase(int i, HashMap returnsAtI) {
		return execute(InstrConstants.BRANCH_INSTR_TYPE, null,
				JvmOpCodes.ARETURN);
	}

	private int sipushvalue = -1;

	private void handleSIPUSH(byte[] info) {
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
				JvmOpCodes.SIPUSH);
	}

	private int handleBIPush(byte[] info) {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
				JvmOpCodes.BIPUSH);

	}

	private void handleSASTORE() {
		execute(InstrConstants.STORE_INSTR_TYPE, InstrConstants.INT,
				JvmOpCodes.SASTORE);
	}

	private void handleSALOAD() {
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
				JvmOpCodes.SALOAD);
	}

	private void handleSimpleReturn() {
		execute(InstrConstants.BRANCH_INSTR_TYPE, null, JvmOpCodes.RETURN);

	}

	private void handleNEWARRAYCase(byte[] info) {
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.OBJECT,
				JvmOpCodes.NEWARRAY);

	}

	private boolean skipAASTORE(int pos) {
		for (int z = 0; z < GlobalVariableStore.getSkipaastores().size(); z++) {

			if (((Integer) GlobalVariableStore.getSkipaastores().get(z))
					.intValue() == pos) {
				return true;
			}
		}
		return false;
	}

	private int handleAASTORECase() {

		return execute(InstrConstants.STORE_INSTR_TYPE, InstrConstants.OBJECT,
				JvmOpCodes.AASTORE);
	}

	// Change
	private int handleANEWARRAYCase(byte[] info) {

		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.OBJECT,
				JvmOpCodes.ANEWARRAY);
	}

	private void checkForImport(java.lang.String input, StringBuffer sb) {
		DecompilerHelper.checkForImport(input, sb);
	}

	// Duplicate Method
	private int getPrevStartCodePos(byte[] info, int i) {
		int current = i;
		ArrayList allstarts = behaviour.getInstructionStartPositions();
		int z;
		for (z = current - 1; z >= 0; z--) {

			boolean ok = isThisInstrStart(allstarts, z);
			if (ok) {
				return z;
			}
		}
		return z;

	}

	private boolean isStoreInst(int index, byte[] info, StringBuffer varindex,
			StringBuffer t) {

		boolean b = isThisInstrStart(behaviour.getInstructionStartPositions(),
				index);
		if (b == false)
			return false;
		switch (info[index]) {
		case JvmOpCodes.AASTORE:
		case JvmOpCodes.BASTORE:
		case JvmOpCodes.CASTORE:
		case JvmOpCodes.DASTORE:
		case JvmOpCodes.FASTORE:
		case JvmOpCodes.IASTORE:
		case JvmOpCodes.LASTORE:
		case JvmOpCodes.SASTORE:
			varindex.append("-1");
			return true;

		case JvmOpCodes.ASTORE:
			varindex.append(info[(index + 1)]);
			t.append("java.lang.Object");
			return true;
		case JvmOpCodes.DSTORE:
			varindex.append(info[(index + 1)]);
			t.append("double");
			return true;
		case JvmOpCodes.FSTORE:
			varindex.append(info[(index + 1)]);
			t.append("float");
			return true;
		case JvmOpCodes.ISTORE:
			varindex.append(info[(index + 1)]);
			t.append("int");
			return true;
		case JvmOpCodes.LSTORE:
			varindex.append(info[(index + 1)]);
			t.append("long");
			return true;

		case JvmOpCodes.ASTORE_0:
			varindex.append("0");
			t.append("java.lang.Object");
			return true;

		case JvmOpCodes.DSTORE_0:
			varindex.append("0");
			t.append("double");
			return true;
		case JvmOpCodes.FSTORE_0:
			varindex.append("0");
			t.append("float");
			return true;
		case JvmOpCodes.ISTORE_0:
			varindex.append("0");
			t.append("int");
			return true;
		case JvmOpCodes.LSTORE_0:
			varindex.append("0");
			t.append("long");
			return true;

		case JvmOpCodes.ASTORE_1:
			varindex.append("1");
			t.append("java.lang.Object");
			return true;
		case JvmOpCodes.DSTORE_1:
			varindex.append("1");
			t.append("double");
			return true;
		case JvmOpCodes.FSTORE_1:
			varindex.append("1");
			t.append("float");
			return true;
		case JvmOpCodes.ISTORE_1:
			varindex.append("1");
			t.append("int");
			return true;
		case JvmOpCodes.LSTORE_1:
			varindex.append("1");
			t.append("long");
			return true;

		case JvmOpCodes.ASTORE_2:
			varindex.append("2");
			t.append("java.lang.Object");
			return true;
		case JvmOpCodes.DSTORE_2:
			varindex.append("2");
			t.append("double");
			return true;
		case JvmOpCodes.FSTORE_2:
			varindex.append("2");
			t.append("float");
			return true;
		case JvmOpCodes.ISTORE_2:
			varindex.append("2");
			t.append("int");
			return true;
		case JvmOpCodes.LSTORE_2:
			varindex.append("2");
			t.append("long");
			return true;

		case JvmOpCodes.ASTORE_3:
			varindex.append("3");
			t.append("java.lang.Object");
			return true;
		case JvmOpCodes.DSTORE_3:
			varindex.append("3");
			t.append("double");
			return true;
		case JvmOpCodes.FSTORE_3:
			varindex.append("3");
			t.append("float");
			return true;
		case JvmOpCodes.ISTORE_3:
			varindex.append("3");
			t.append("int");
			return true;
		case JvmOpCodes.LSTORE_3:
			varindex.append("3");
			t.append("long");
			return true;

		default:
			return false;

		}

	}

	private int handleComplexAStore(byte[] info, int i) {
		return execute(InstrConstants.STORE_INSTR_TYPE, InstrConstants.OBJECT,
				JvmOpCodes.ASTORE);
	}

	private boolean isMethodRetBoolean(Behaviour b) {

		java.lang.String type = b.getReturnType();
		if (type.equals("boolean"))
			return true;
		return false;

	}

	private int handleSimpleASTORECase(int i, int index) {
		int opcodeval;
		switch (index) {
		case 0:
			opcodeval = JvmOpCodes.ASTORE_0;
			break;

		case 1:
			opcodeval = JvmOpCodes.ASTORE_1;
			break;

		case 2:
			opcodeval = JvmOpCodes.ASTORE_2;
			break;

		default:
			opcodeval = JvmOpCodes.ASTORE_3;
			break;
		}
		return execute(InstrConstants.STORE_INSTR_TYPE, InstrConstants.OBJECT,
				opcodeval);
	}

	private boolean isThisInstASTOREInst(byte[] info, int pos, StringBuffer sb) {

		if (isThisInstrStart(behaviour.getInstructionStartPositions(), pos)) {
			if (info[pos] == JvmOpCodes.ASTORE) {
				sb.append(info[(pos + 1)]);
				return true;
			}
			if (info[pos] == JvmOpCodes.ASTORE_0) {
				sb.append(0);
				return true;

			}
			if (info[pos] == JvmOpCodes.ASTORE_1) {
				sb.append(1);
				return true;
			}
			if (info[pos] == JvmOpCodes.ASTORE_2) {
				sb.append(2);
				return true;

			}
			if (info[pos] == JvmOpCodes.ASTORE_3) {
				sb.append(3);
				return true;

			}
		}
		return false;

	}

	private boolean isPrevInstALOADInst(byte[] info, int pos, StringBuffer s) {
		if (isThisInstrStart(behaviour.getInstructionStartPositions(),
				(pos - 1))) {

			switch (info[(pos - 1)]) {

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
		if (isThisInstrStart(behaviour.getInstructionStartPositions(),
				(pos - 2))) {

			switch (info[(pos - 2)]) {
			case JvmOpCodes.ALOAD:
				s.append(info[(pos - 2 + 1)]);
				return true;

			}
		}
		return false;
	}

	private boolean checkForMatchingLoopAgain(ArrayList loops, int start,
			StringBuffer S) {
		boolean b = false;
		if (loops == null || loops.size() == 0)
			return b;
		else {
			for (int s = 0; s < loops.size(); s++) {
				Loop l = (Loop) loops.get(s);
				int loopstart = l.getStartIndex();
				if (loopstart == start) {
					b = true;
					S.append(l.getEndIndex());
					return b;
				}

			}
		}
		return b;
	}

	private int getClosestLoopEndForThisIf(int s, ArrayList loops, byte[] info) {

		int end = -1;
		if (loops == null || loops.size() == 0)
			return end;
		int gotos = s + 3;
		if (info[gotos] == JvmOpCodes.GOTO
				&& isThisInstrStart(behaviour.getInstructionStartPositions(),
						gotos)) {
			int gotoj = getJumpAddress(info, gotos);
			int starts[] = new int[loops.size()];
			for (int z = 0; z < loops.size(); z++) {
				starts[z] = ((Loop) loops.get(z)).getEndIndex();
			}
			Arrays.sort(starts);
			int reqdloopend = -1;
			for (int x = 0; x < starts.length; x++) {
				int cur = starts[x];
				if (gotoj > cur) {
					reqdloopend = cur;
					break;
				}
			}

			if (reqdloopend != -1) {
				int lstart = getLoopStartForEnd(reqdloopend, loops);
				if (lstart < s) {
					return reqdloopend;
				}
			} else {
				return -1;
			}

		}

		return end;
	}

	private int checkElseCloseWRTAnyParentLoop(IFBlock ifs, int gotostart,
			byte[] info) {

		ArrayList allloops = behaviour.getBehaviourLoops();
		if (allloops == null || allloops.size() == 0)
			return -1;
		int gotojump = getJumpAddress(info, gotostart);
		Object[] sortedLoops = sortLoops(allloops);

		for (int k = 0; k < sortedLoops.length; k++) {
			Loop cur = (Loop) sortedLoops[k];
			if (cur.getStartIndex() == gotojump) {
				int parentLoopStart = getParentLoopStartForIf(sortedLoops, ifs
						.getIfStart());
				if (parentLoopStart == gotojump) {
					int loopend = getloopEndForStart(allloops, parentLoopStart);
					return loopend;

				}
			}
		}
		return -1;
	}

	private boolean checkForSkipWRTbooleanShortcutAssignFound(int i) {

		Set set = GlobalVariableStore.getSkipWRTbooleanShortcutAssignFound()
				.entrySet();
		boolean remove = false;
		Map.Entry toremove = null;
		for (Iterator it = set.iterator(); it.hasNext();) {
			Map.Entry elem = (Map.Entry) it.next();
			Integer in1 = (Integer) elem.getKey(); // inclusive
			Integer in2 = (Integer) elem.getValue(); // exclusive
			if (i >= in1.intValue() && i < in2.intValue()) {
				return true;
			}
			if (i == in2.intValue()) {
				remove = true;
				toremove = elem;
			}
		}
		if (remove) {
			Object rkey = null;
			for (Iterator it = set.iterator(); it.hasNext();) {
				Map.Entry elem = (Map.Entry) it.next();
				Integer in1 = (Integer) elem.getKey();
				Integer in2 = (Integer) elem.getValue();
				if (in2.intValue() == i) {
					rkey = in1;
					break;
				}
			}
			if (rkey != null) {
				GlobalVariableStore.getSkipWRTbooleanShortcutAssignFound()
						.remove(rkey);
			}

		}
		return false;
	}

	private boolean doesBooleanAssignFoundExist(int currentForIndex) {
		Set set = GlobalVariableStore.getBooleanAssignMap().entrySet();
		for (Iterator it = set.iterator(); it.hasNext();) {
			Map.Entry elem = (Map.Entry) it.next();
			Integer in1 = (Integer) elem.getKey();
			Integer in2 = (Integer) elem.getValue();
			if (currentForIndex >= in1.intValue()
					&& currentForIndex < in2.intValue()) {
				return true;
			}
		}
		return false;
	}

	private boolean checkForShortCutIfBooleanAssignment(IFBlock ifst,
			byte[] info, StringBuffer end, StringBuffer msc) {

		int ifbyend = ifst.getIfCloseFromByteCode();
		ArrayList starts = behaviour.getInstructionStartPositions();
		boolean islastif = false;
		// int inital=ifbyend
		while (ifbyend < info.length) {
			if (isThisInstrStart(starts, ifbyend)
					&& info[ifbyend] == JvmOpCodes.ICONST_1) {
				int next = ifbyend + 1;
				if (isThisInstrStart(starts, next)
						&& info[next] == JvmOpCodes.GOTO) {
					next = next + 3;
					if (isThisInstrStart(starts, next)
							&& info[next] == JvmOpCodes.ICONST_0) {
						next = next + 1;
						StringBuffer sb = new StringBuffer("");

						boolean anyif = checkForIfInRange(info,
								currentForIndex + 3, ifbyend,
								new StringBuffer(), starts);
						if (!anyif)
							return false;
						if (isThisInstrStart(starts, next)
								&& (isThisInstructionIStoreInst(info, next, sb)
										|| (info[next] == JvmOpCodes.PUTFIELD) || (info[next] == JvmOpCodes.PUTSTATIC))) {

							try {
								/*
								 * int i=Integer.parseInt(sb.toString());
								 * boolean complex=false; if(i!=0 && i!=1 &&
								 * i!=2 && i!=3) complex=true; LocalVariable
								 * local=getLocalVariable(i,"store","int",complex,next);
								 */
								// if(local!=null &&
								// local.getDataType().equalsIgnoreCase("boolean")
								// ) {
								end.append("" + next);
								GlobalVariableStore
										.getSkipWRTbooleanShortcutAssignFound()
										.put(new Integer(ifbyend),
												new Integer(next));
								return true;
								// }
							} catch (Exception exp) {
								return false;
							}

						} else {
							if (isThisInstrStart(starts, next)
									&& (!isThisInstructionIStoreInst(info,
											next, sb)
											&& !(info[next] == JvmOpCodes.PUTFIELD) && !(info[next] == JvmOpCodes.PUTSTATIC))) {
								end.append("" + next);
								GlobalVariableStore
										.getSkipWRTbooleanShortcutAssignFound()
										.put(new Integer(ifbyend),
												new Integer(next));
								msc.append("true");
								return true;
							}

							return false;
						}

					} else {
						return false;
					}

				} else {
					return false;
				}
			} else if (info[ifbyend] == JvmOpCodes.ICONST_0) {

				int prev = ifbyend - 3;
				if (isThisInstrStart(starts, prev)
						&& info[prev] == JvmOpCodes.GOTO) {
					prev = prev - 1;
					if (isThisInstrStart(starts, prev)
							&& info[prev] == JvmOpCodes.ICONST_1) {
						int from = prev;
						int next = ifbyend + 1;
						StringBuffer sb = new StringBuffer("");
						boolean anyif = checkForIfInRange(info,
								currentForIndex + 3, prev, new StringBuffer(),
								starts);
						if (!anyif)
							return false;
						if (isThisInstrStart(starts, next)
								&& (isThisInstructionIStoreInst(info, next, sb)
										|| (info[next] == JvmOpCodes.PUTFIELD) || (info[next] == JvmOpCodes.PUTSTATIC))) {
							try {
								/*
								 * int i=Integer.parseInt(sb.toString());
								 * boolean complex=false; if(i!=0 && i!=1 &&
								 * i!=2 && i!=3) complex=true; LocalVariable
								 * local=getLocalVariable(i,"store","int",complex,next);
								 * //if(local!=null &&
								 * local.getDataType().equalsIgnoreCase("boolean") ) {
								 */
								GlobalVariableStore
										.getSkipWRTbooleanShortcutAssignFound()
										.put(new Integer(from),
												new Integer(next));
								end.append("" + next);
								return true;
								// }
							} catch (Exception exp) {
								return false;
							}
						} else {
							if (isThisInstrStart(starts, next)
									&& (!isThisInstructionIStoreInst(info,
											next, sb)
											&& !(info[next] == JvmOpCodes.PUTFIELD) && !(info[next] == JvmOpCodes.PUTSTATIC))) {
								GlobalVariableStore
										.getSkipWRTbooleanShortcutAssignFound()
										.put(new Integer(from),
												new Integer(next));
								end.append("" + next);
								msc.append("true");
								return true;
							}
							return false;
						}
					} else {
						return false;
					}
				} else {
					return false;
				}
			} else {
				int cur = ifbyend;
				boolean found = false;
				int s1 = cur - 1;
				int s2 = ifst.getIfStart();
				for (; s1 > s2; s1--) {
					boolean isB = isThisInstrStart(starts, s1);
					if (isB) {
						boolean isIF = isInstructionIF(info[s1]);
						if (isIF) {
							islastif = sanalyser.isLastIfInChain(s1);
							break;
						}
					}
				}
				if (islastif) {
					return false;
				}
				while (cur < info.length) {
					boolean isst = isThisInstrStart(starts, cur);
					if (isst) {
						boolean isif = isInstructionIF(info[cur]);
						if (isif) {
							ifbyend = getJumpAddress(info, cur);
							if (ifbyend < cur)
								return false; // to avoid infinite loop
							found = true;
							break;
						}
					}
					cur++;

				}
				if (found) {

				} else {
					return false;
				}
			}
		}
		return false;
	}

	public boolean addCodeStatementWRTShortcutOR(IFBlock ifst,
			java.lang.String s, boolean print, java.lang.String ifw,
			boolean last, java.lang.String alt) {

		if (sanalyser == null)
			return true;
		byte[] info = behaviour.getCode();
		boolean reverse = false;
		ArrayList starts = behaviour.getInstructionStartPositions();
		// if(!print)return true;
		boolean add = true;
		int current = currentForIndex;
		byte[] code = behaviour.getCode();
		java.lang.String str = "\nif(";
		if (ifw.equals("while")) {
			str = "\nwhile(";
		}
		java.lang.String string = "";
		/*
		 * if(last){ if(isInstAnyCMPInst(code,current-1)==false) { string=alt; }
		 * else string=s; } else // belurs NOTE: Need to revert to alt if it is
		 * shortcut and '&&' NOT shortcut or '||' { int
		 * byjump=ifst.getIfCloseFromByteCode(); byjump=byjump-3; boolean
		 * isstart=isThisInstrStart(starts,byjump);
		 * 
		 * if(isstart) { boolean isif=isInstructionIF(info[byjump]); if(isif) {
		 * int from=ifst.getIfStart()+3; int to=byjump; for(int
		 * findex=from;findex < to;findex++) {
		 * isstart=isThisInstrStart(starts,findex); if(isstart) {
		 * isif=isInstructionIF(info[findex]); if(isif) { reverse=false; break; } } } } }
		 * if(!reverse) string=s; else alt=alt; }
		 */

		// Note: sbelur
		// Check for this type first
		// boolean b=(i > j) || (or &&) (k==2) type
		// Need to check this to as else was not coming in earlier
		// implementation
		// Shortcutchain
		// somechain=sanalyser.getShortcutChainGivenIf(currentForIndex);
		boolean booleanAssignFoundProcessed = doesBooleanAssignFoundExist(currentForIndex);
		if (booleanAssignFoundProcessed == false) {
			StringBuffer end = new StringBuffer();
			GlobalVariableStore.setMethodsc(new StringBuffer(""));
			GlobalVariableStore
					.setBooleanAssignFound(checkForShortCutIfBooleanAssignment(
							ifst, info, end, GlobalVariableStore.getMethodsc()));
			if (GlobalVariableStore.isBooleanAssignFound()) {
				GlobalVariableStore.getBooleanAssignMap().put(
						new Integer(currentForIndex),
						new Integer(end.toString()));
			}
		}

		boolean andpresent = false;
		boolean stringset = false;
		/*
		 * if(booleanAssignFound){
		 * IfPartOfShortCutChain=true;encounteredAndOrComp=true; }
		 */
		boolean concatthisif = GlobalVariableStore.isIfPartOfShortCutChain();
		// if(last) {
		// if(IfPartOfShortCutChain) {

		int ifend = ifst.getIfCloseFromByteCode();
		int ifstart = ifst.getIfStart();
		int nextstart = ifstart + 3;
		int pr = ifend - 3;
		boolean isstart = isThisInstrStart(starts, pr);
		boolean isif;
		boolean lookforand = false;
		/*
		 * if(isstart) { isif=isInstructionIF(info[pr]); if(isif==false) {
		 * lookforand=true; } }
		 */
		/*
		 * if(lookforand) { int strt=nextstart; for(;nextstart < ifend
		 * ;nextstart++) { isstart=isThisInstrStart(starts,nextstart);
		 * if(isstart) { isif=isInstructionIF(info[nextstart]); if(isif) { int
		 * jump=getJumpAddress(info,nextstart); if(jump==ifend) {
		 * IfPartOfShortCutChain=true; andpresent=true; break; } else {
		 * IfPartOfShortCutChain=true; andpresent=false; break; } } else {
		 * if(info[nextstart]==JvmOpCodes.GOTO){ System.out.println("nextstart
		 * found"+nextstart+" ifend"+ifend+"start"+strt); andpresent=false;
		 * break; } } } } } else { StringBuffer pos=new StringBuffer();
		 * //boolean
		 * otherif=checkForAnyIFWithDifferentIndex(info,currentForIndex+3,pr+3,pos,starts);
		 * boolean
		 * otherif=checkForIfInRange(info,currentForIndex+3,pr,pos,starts);
		 * if(!otherif) { int nextj=getJumpAddress(info,pr)-3; boolean
		 * nextjisstart=isThisInstrStart(starts,nextj); boolean nextjisif=false;
		 * if(nextjisstart) { nextjisif=isInstructionIF(info[nextj]); boolean
		 * skip=false; if(nextj==currentForIndex) { skip=true; } if(!skip){
		 * otherif=checkForIfInRange(info,pr+3,nextj,new StringBuffer(),starts);
		 * if(nextjisif && !otherif) { IfPartOfShortCutChain=true;
		 * andpresent=true; stringset=true; } else { IfPartOfShortCutChain=true;
		 * andpresent=false; stringset=false; } } else {
		 * IfPartOfShortCutChain=false; andpresent=false; stringset=false; } } }
		 * else { boolean check=true; int nextifpos=-1; try {
		 * nextifpos=Integer.parseInt(pos.toString()); }
		 * catch(NumberFormatException ne){ check=false; }
		 * 
		 * if(check==false){ IfPartOfShortCutChain=true; andpresent=true;
		 * stringset=true; } else { int
		 * currentjump=getJumpAddress(info,currentForIndex); int
		 * nextifjump=getJumpAddress(info,nextifpos);
		 * if(nextifjump!=currentjump){ IfPartOfShortCutChain=true;
		 * andpresent=true; stringset=true; } else { IfPartOfShortCutChain=true;
		 * andpresent=false; stringset=false; } } } }
		 */

		// }
		// }
		/*
		 * if(last) { if(andpresent) string = string; // TODO: Revisit this else
		 * string = string; } else string = string;
		 */

		java.lang.String connector = sanalyser.getConnector(ifst.getIfStart());
		if (!connector.equals(""))
			connector = " " + connector;

		if (connector != null && connector.trim().equals(ShortcutAnalyser.AND)) {

			boolean reset = false;
			/**
			 * 1. Get the next if 2. Get all ifs before this if 3. Check if some
			 * before if is closing in between this if start && next if start 4.
			 * if 3 returns true then this if needs to be made a last if. 5. Get
			 * the chain for this if 6. Nullify the next chain for this chain.
			 * 7. Nullify the previous chain for next if.
			 */

			// Get Next If
			int nextifstart = getNextIfStart(ifst.getIfStart(), info);
			if (nextifstart != -1) {
				// 2 and 3
				boolean bl = checkForIfCloseInRange(ifst.getIfStart(),
						nextifstart);
				if (bl) {

					Shortcutstore store = sanalyser
							.getShortCutStoreGivenIfStart(ifst.getIfStart());
					if (store != null) {
						// Point 4
						store.setConnectortype("");
						store.makeStoreInEffective(true);
						// point 5
						Shortcutchain chain1 = sanalyser
								.getShortcutChainGivenIf(ifst.getIfStart());
						Shortcutchain chain2 = sanalyser
								.getShortcutChainGivenIf(nextifstart);

						// Point 6 and 7
						chain1.setNextChain(null);
						chain2.setPrevChain(null);

						reset = true;
					}

				}

			}

			andpresent = true;
			if (reset)
				andpresent = false;
		} else if (connector != null
				&& connector.trim().equals(ShortcutAnalyser.OR)) {
			andpresent = false;
		}
		if (andpresent)
			reverse = true;
		else
			reverse = false;

		if (!reverse) {
			reverse = sanalyser.checkToReverseCondition(ifst.getIfStart());
		}
		last = sanalyser.isLastIfInChain(ifst.getIfStart());
		if (last)
			reverse = true;
		if (!reverse)
			string = s;
		else
			string = alt;

		boolean begin = sanalyser.beginGroup(ifst.getIfStart());
		if (begin) {
			string = " (" + string;
		}
		boolean end = sanalyser.endGroup(ifst.getIfStart());
		if (end) {
			string = string + ") ";
		}

		Shortcutchain someChain = sanalyser
				.getShortcutChainGivenIf(currentForIndex);
		if (GlobalVariableStore.isIfPartOfShortCutChain() == false
				&& connector != null
				&& !connector.trim().equals(ShortcutAnalyser.NONE)) { // Prev
			// ->
			// encounteredOrComp

			int jump = getJumpAddress(code, current);
			int i = jump - 3;
			if ((jump > current
					&& i > 0
					&& i != current
					&& isThisInstrStart(behaviour
							.getInstructionStartPositions(), i) && isInstructionIF(code[i]))
					|| (GlobalVariableStore.isBooleanAssignFound())) {

				// boolean oktoopen=addOpenBracket(info,ifst);
				int jumpForNextIf = getJumpAddress(code, i);
				// if(jumpForNextIf > i) {
				// encounteredOrComp=true;
				if (!reverse) {

					GlobalVariableStore.setInitialBracketAdded(false);
					if (!GlobalVariableStore.isBooleanAssignFound())
						behaviour.appendToBuffer(Util
								.formatDecompiledStatement(str + string
										+ " ||  "));
					else {
						Operand opt = createOperand("(" + string + " || ");
						opStack.push(opt);
					}

				} else {

					GlobalVariableStore.setInitialBracketAdded(false);
					if (!GlobalVariableStore.isBooleanAssignFound())
						behaviour.appendToBuffer(Util
								.formatDecompiledStatement(str + string
										+ " &&  "));
					else {
						Operand opt = createOperand("(" + string + " &&  ");
						opStack.push(opt);
					}

				}
				ifst.setDonotclose(true);
				GlobalVariableStore.setIfPartOfShortCutChain(true);
				GlobalVariableStore.setEncounteredAndOrComp(true);
				return false;
				// }
				/*
				 * else return true;
				 */
			} else if (jump < current) {
				// boolean oktoopen=addOpenBracket(info,ifst);
				int close = ifst.getIfCloseLineNumber();
				int x = close - 3;
				if (x > 0
						&& isThisInstrStart(behaviour
								.getInstructionStartPositions(), x)
						&& isInstructionIF(code[x])) {
					// encounteredOrComp=true;
					if (!reverse) {

						GlobalVariableStore.setInitialBracketAdded(false);
						if (!GlobalVariableStore.isBooleanAssignFound())
							behaviour.appendToBuffer(Util
									.formatDecompiledStatement(str + string
											+ " ||  "));
						else {
							Operand opt = createOperand("(" + string + " ||  ");
							opStack.push(opt);
						}

					} else {

						GlobalVariableStore.setInitialBracketAdded(false);
						if (!GlobalVariableStore.isBooleanAssignFound())
							behaviour.appendToBuffer(Util
									.formatDecompiledStatement(str + string
											+ " &&  "));
						else {
							Operand opt = createOperand("(" + string + " &&  ");
							opStack.push(opt);

						}

					}
					ifst.setDonotclose(true);
					GlobalVariableStore.setIfPartOfShortCutChain(true);
					GlobalVariableStore.setEncounteredAndOrComp(true);
					return false;
				}
				/*
				 * else { return true; }
				 */
			}
			/*
			 * else { return true; }
			 */
			// Previous --> if(last && concatthisif && andpresent) {
			else if (andpresent) {
				// StringBuffer mif=new StringBuffer();
				// boolean
				// close=checkClosingBracketRuleForShortCutOp(currentForIndex,info,ifst.getIfCloseFromByteCode(),ifst.getIfStart(),mif);

				// Object
				// ob=openCloseBracketMap.get(""+ifst.getIfCloseFromByteCode());
				if (!GlobalVariableStore.isBooleanAssignFound()) {
					behaviour.appendToBuffer(Util.formatDecompiledStatement(str
							+ string + " &&  "));
				} else {

					Operand opt = createOperand("(" + string + " &&  ");
					opStack.push(opt);

					// Operand opt=opStack.peekTopOfStack();
					// opt.setOperandValue(opt.getOperandValue()+string+" && ");

				}

				ifst.setDonotclose(true);
				GlobalVariableStore.setIfPartOfShortCutChain(true);
				GlobalVariableStore.setEncounteredAndOrComp(true);
				return false;
			}
			// Seems to be wrong to put { in If block
			/*
			 * if(last && concatthisif && !andpresent) { // TODO: there might be
			 * close bracket issue here. // May be need to check whether the
			 * additional close was for the final close before start of {
			 * 
			 * Util.forceStartSpace=false; StringBuffer mif=new StringBuffer();
			 * //boolean
			 * close=checkClosingBracketRuleForShortCutOp(currentForIndex,info,ifst.getIfCloseFromByteCode(),ifst.getIfStart(),mif);
			 * if(!booleanAssignFound) codeStatements+=string+")"; // Adding
			 * close anyway else { Operand opt=opStack.peekTopOfStack();
			 * opt.setOperandValue(opt.getOperandValue()+string+")"); // Adding
			 * close anyway ifst.setDonotclose(true); }
			 * 
			 * Util.forceStartSpace=true; Util.forceTrimString=false;
			 * Util.forceNewLine=false; if(!booleanAssignFound)
			 * codeStatements+=Util.formatDecompiledStatement("\t\n{\n"); else {
			 * Operand opt=opStack.peekTopOfStack();
			 * opt.setOperandValue(opt.getOperandValue()+";\n");
			 * ifst.setDonotclose(true); }
			 * 
			 * Util.forceNewLine=true; Util.forceTrimString=true;
			 * IfPartOfShortCutChain=false; andpresent=false;
			 * concatthisif=false; return false; }
			 */

			return true;

		} else if (GlobalVariableStore.isIfPartOfShortCutChain() == true
				&& (connector != null
						&& !connector.trim().equals(ShortcutAnalyser.NONE) || (sanalyser
						.isLastIfInChain(currentForIndex)
						&& connector != null && connector.trim().equals(
						ShortcutAnalyser.NONE)))) {// Continue

			// codes
			StringBuffer mif = new StringBuffer();
			/*
			 * boolean
			 * closeb=checkClosingBracketRuleForShortCutOp(currentForIndex,info,ifst.getIfCloseFromByteCode(),ifst.getIfStart(),mif);
			 * if(closeb==false) { boolean
			 * oktoopen=addOpenBracket(info,ifst,closeb,mif); if(oktoopen) {
			 * if(!booleanAssignFound) codeStatements+="("; else { Operand
			 * opt=opStack.peekTopOfStack();
			 * opt.setOperandValue(opt.getOperandValue()+"("); } } } else {
			 * Object
			 * ob=openCloseBracketMap.get(""+ifst.getIfCloseFromByteCode());
			 * if(ob!=null) string=string+") "; else string=string; }
			 */

			if (!GlobalVariableStore.isBooleanAssignFound())
				behaviour.appendToBuffer(string);
			else {
				Operand opt = opStack.peekTopOfStack();
				if (opt != null) // Added check while testing
					// BigInteger.class [makePositive]
					opt.setOperandValue(opt.getOperandValue() + string);
				else
					behaviour.appendToBuffer(string);
			}
			int jump = getJumpAddress(code, current);
			int i = jump - 3;

			if (i != current
					&& jump > current
					&& i > 0
					&& isThisInstrStart(behaviour
							.getInstructionStartPositions(), i)
					&& isInstructionIF(code[i])) {
				// IfPartOfShortCutChain=true;
				// codes
				if (!GlobalVariableStore.isBooleanAssignFound()) {
					if (!reverse)
						behaviour.appendToBuffer(" ||  ");
					else
						behaviour.appendToBuffer(" &&  ");
				} else {
					if (!reverse) {
						Operand opt = opStack.peekTopOfStack();
						opt.setOperandValue(opt.getOperandValue() + " || ");
					} else {
						Operand opt = opStack.peekTopOfStack();
						opt.setOperandValue(opt.getOperandValue() + " && ");
					}
				}
				ifst.setDonotclose(true);

				GlobalVariableStore.setIfPartOfShortCutChain(true);
				GlobalVariableStore.setEncounteredAndOrComp(true);
				return false;
			} else if (jump < current) { // BigInteger
				int close = ifst.getIfCloseLineNumber();
				int x = close - 3;
				if (connector != null
						&& !connector.trim().equals(ShortcutAnalyser.NONE)
						&& x > 0
						&& isThisInstrStart(behaviour
								.getInstructionStartPositions(), x)
						&& isInstructionIF(code[x])) {
					// encounteredOrComp=true;
					// codes
					if (!GlobalVariableStore.isBooleanAssignFound()) {
						if (!reverse)
							behaviour.appendToBuffer(" || ");
						else
							behaviour.appendToBuffer(" && ");

					} else {
						if (!reverse) {
							Operand opt = opStack.peekTopOfStack();
							opt.setOperandValue(opt.getOperandValue() + " || ");
						} else {
							Operand opt = opStack.peekTopOfStack();
							opt.setOperandValue(opt.getOperandValue() + " && ");
						}
					}
					ifst.setDonotclose(true);

					GlobalVariableStore.setIfPartOfShortCutChain(true);
					GlobalVariableStore.setEncounteredAndOrComp(true);
					return false;
				} else {
					if (!last) {
						Util.forceTrimString = false;
						if (!GlobalVariableStore.isBooleanAssignFound())
							behaviour.appendToBuffer(" " + connector + " ");
						else {
							Operand opt = opStack.peekTopOfStack();
							opt.setOperandValue(opt.getOperandValue() + " "
									+ connector + " ");
						}
						ifst.setDonotclose(true);
						Util.forceTrimString = true;
						GlobalVariableStore.setIfPartOfShortCutChain(true);
						GlobalVariableStore.setEncounteredAndOrComp(true);

					} else {
						if (!GlobalVariableStore.isBooleanAssignFound())
							behaviour.appendToBuffer(Util
									.formatDecompiledStatement(")\n{\n"));
						else {
							Operand opt = opStack.peekTopOfStack();
							if (GlobalVariableStore.getMethodsc().toString()
									.equals("true")) {
								opt
										.setOperandValue(opt.getOperandValue()
												+ ")");
							} else {
								opt.setOperandValue(opt.getOperandValue()
										+ ");\n");
							}
							ifst.setDonotclose(true);
						}
						GlobalVariableStore.setIfPartOfShortCutChain(false);
						GlobalVariableStore.setEncounteredAndOrComp(false);
					}
					return false;
				}
			} else {
				if (!last) {
					Util.forceTrimString = false;
					if (!GlobalVariableStore.isBooleanAssignFound())
						behaviour.appendToBuffer(" " + connector + " ");
					else {
						Operand opt = opStack.peekTopOfStack();
						opt.setOperandValue(opt.getOperandValue() + " "
								+ connector + " ");
					}
					ifst.setDonotclose(true);
					Util.forceTrimString = true;
					GlobalVariableStore.setIfPartOfShortCutChain(true);
					GlobalVariableStore.setEncounteredAndOrComp(true);

				} else {
					Operand opt = opStack.peekTopOfStack();
					if (!GlobalVariableStore.isBooleanAssignFound()
							|| opt == null) {
						Util.forceTrimLines = false;
						behaviour.appendToBuffer(")\n");
						behaviour.appendToBuffer(Util
								.formatDecompiledStatement("\n{\n"));
						Util.forceTrimLines = true;

					} else {

						if (GlobalVariableStore.getMethodsc().toString()
								.equals("true")) {
							opt.setOperandValue(opt.getOperandValue() + ")");
						} else {
							opt.setOperandValue(opt.getOperandValue() + ");\n");
						}
						ifst.setDonotclose(true);
					}// 123
					GlobalVariableStore.setIfPartOfShortCutChain(false);
					GlobalVariableStore.setEncounteredAndOrComp(false);
				}

				return false;
			}

		} else {
			return true;
		}

	}

	private boolean isInstructionIF(int instruction) {

		switch (instruction) {

		case JvmOpCodes.IF_ACMPEQ:
			return true;
		case JvmOpCodes.IF_ACMPNE:
			return true;
		case JvmOpCodes.IF_ICMPEQ:
			return true;
		case JvmOpCodes.IF_ICMPGE:
			return true;
		case JvmOpCodes.IF_ICMPGT:
			return true;
		case JvmOpCodes.IF_ICMPLE:
			return true;
		case JvmOpCodes.IF_ICMPLT:
			return true;
		case JvmOpCodes.IF_ICMPNE:
			return true;

		case JvmOpCodes.IFEQ:
			return true;
		case JvmOpCodes.IFGE:
			return true;
		case JvmOpCodes.IFGT:
			return true;
		case JvmOpCodes.IFLE:
			return true;
		case JvmOpCodes.IFNE:
			return true;
		case JvmOpCodes.IFLT:
			return true;
		case JvmOpCodes.IFNULL:
			return true;
		case JvmOpCodes.IFNONNULL:
			return true;
		default:
			return false;

		}

	}

	private void checkForATHROWAtIFelseEnd(IFBlock ifst,
			StringBuffer ifelsecode, int i) {
		boolean start = isThisInstrStart(behaviour
				.getInstructionStartPositions(), i);
		byte[] info = behaviour.getCode();
		if (start && info[i] == JvmOpCodes.ATHROW) {
			boolean x = addATHROWOutput(i);
			if (opStack.size() > 0 && x) {
				Operand op = (Operand) opStack.pop();
				opStack.push(op);
				java.lang.String tempString = "throw " + op.getOperandValue()
						+ ";\n";
				ifelsecode.append(Util.formatDecompiledStatement("\n"
						+ tempString + "\n"));
				GlobalVariableStore.getAthrowmap().put(new Integer(i), "true");
			}

		}

	}

	private boolean addATHROWOutput(int i) {
		if (GlobalVariableStore.getAthrowmap().size() == 0)
			return true;
		Set entries = GlobalVariableStore.getAthrowmap().entrySet();
		Iterator it = entries.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Integer pos = (Integer) entry.getKey();
			if (pos.intValue() == i) {
				java.lang.String str = (java.lang.String) entry.getValue();
				if (str.equals("true")) {
					return false;
				}
			}
		}

		return true;

	}

	private int handleBALOAD(OperandStack stack) {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
				JvmOpCodes.BALOAD);
	}

	private int handleBASTORE(OperandStack stack) {

		return execute(InstrConstants.STORE_INSTR_TYPE, InstrConstants.INT,
				JvmOpCodes.BASTORE);
	}

	private int handleCALOAD(OperandStack stack) {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
				JvmOpCodes.CALOAD);
	}

	private int handleCASTORE(OperandStack stack) {
		return execute(InstrConstants.STORE_INSTR_TYPE, InstrConstants.INT,
				JvmOpCodes.CASTORE);
	}

	private int handleCheckCast(OperandStack stack, byte[] info) {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
				JvmOpCodes.CHECKCAST);

	}

	private int handleDALOAD(OperandStack stack) {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.DOUBLE,
				JvmOpCodes.DALOAD);
	}

	private int handleDASTORE(OperandStack opStack) {
		return execute(InstrConstants.STORE_INSTR_TYPE, InstrConstants.DOUBLE,
				JvmOpCodes.DASTORE);
	}

	private int handleDCONST(OperandStack stack, double val) {
		if (val == 0)
			return execute(InstrConstants.LOAD_INSTR_TYPE,
					InstrConstants.DOUBLE, JvmOpCodes.DCONST_0);

		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.DOUBLE,
				JvmOpCodes.DCONST_1);
	}

	private int handleDDIV(OperandStack stack) {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.DOUBLE,
				JvmOpCodes.DDIV);
	}

	private int handleDLOADCase(int opcode) {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.DOUBLE,
				opcode);
	}

	private int handleDMUL(OperandStack stack) {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.DOUBLE,
				JvmOpCodes.DMUL);
	}

	private int handleDNEG(OperandStack stack) {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.DOUBLE,
				JvmOpCodes.DNEG);
	}

	private int handleDREM(OperandStack stack) {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.DOUBLE,
				JvmOpCodes.DREM);
	}

	private int handleDUP2X2(OperandStack stack) {
		return execute(InstrConstants.UNCLASSIFIED_INSTR_TYPE, null,
				JvmOpCodes.DUP2_X2);
	}

	private int handleFCONST(OperandStack stack, java.lang.String val) {
		int opcodeval;
		if (val.equals("0.0f")) {
			opcodeval = JvmOpCodes.FCONST_0;
		} else if (val.equals("0.0f")) {
			opcodeval = JvmOpCodes.FCONST_1;
		} else {
			opcodeval = JvmOpCodes.FCONST_2;
		}

		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.FLOAT,
				opcodeval);
	}

	private int handleFDIV(OperandStack stack) {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.FLOAT,
				JvmOpCodes.FDIV);

	}

	private int handleFLOAD(int opcodeval) {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.FLOAT,
				opcodeval);
	}

	private boolean isIFShortcutORComp(byte[] info, int j) {

		boolean b = sanalyser.isIFShortcutIfCondition(j);
		return b;

	}

	private void handleLCONST(OperandStack stack, java.lang.String str) {
		int opcodeval;
		if (str.equals("0")) {
			opcodeval = JvmOpCodes.LCONST_0;
		} else {
			opcodeval = JvmOpCodes.LCONST_1;
		}
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.LONG, opcodeval);
	}

	private void handleLDIV(OperandStack stack) {
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.LONG,
				JvmOpCodes.LDIV);
	}

	private void handleSIMPLELLOAD(OperandStack stack, int index) {

		int opcodeval;
		switch (index) {
		case 0:
			opcodeval = JvmOpCodes.LLOAD_0;
			break;
		case 1:
			opcodeval = JvmOpCodes.LLOAD_1;
			break;
		case 2:
			opcodeval = JvmOpCodes.LLOAD_2;
			break;
		default:
			opcodeval = JvmOpCodes.LLOAD_3;
			break;
		}

		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.LONG, opcodeval);
	}

	private int handleDCMPG(OperandStack stack, byte[] info) {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.DOUBLE,
				JvmOpCodes.DCMPG);
	}

	private int handleDCMPL(OperandStack stack, byte[] info) {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.DOUBLE,
				JvmOpCodes.DCMPL);
	}

	private boolean addRETURNatI(int i, IFBlock ifst) {

		/*
		 * byte[] info=behaviour.getCode();
		 * 
		 * boolean shortcut=isIFShortcutORComp(info,ifst.getIfStart());
		 * if(shortcut)return true; int ifclose=ifst.getIfCloseLineNumber(); int
		 * x=ifclose-3;
		 * if(isThisInstrStart(behaviour.getInstructionStratPositions(),x) &&
		 * x!=ifst.getIfStart()) { boolean isif=isInstructionIF(info[x]);
		 * if(isif)return true; }
		 */

		boolean oktoadd = true;
		Iterator mapIT = GlobalVariableStore.getReturnsAtI().entrySet()
				.iterator();
		while (mapIT.hasNext()) {
			Map.Entry entry = (Map.Entry) mapIT.next();
			Object key = entry.getKey();
			Object retStatus = entry.getValue().toString();
			if (key instanceof Integer) {
				Integer position = (Integer) key;
				int posValue = position.intValue();
				if (posValue == i) {
					if (retStatus.equals("true")) {

						oktoadd = false;
						break;
					}
				}
			}

		}
		return oktoadd;
	}

	private boolean lastIFinShortCutChain(byte[] info, IFBlock ifst, int i) {

		return sanalyser.isLastIfInChain(i);
	}

	private boolean isInstAnyCMPInst(byte[] info, int i) {
		ArrayList starts = behaviour.getInstructionStartPositions();
		if (isThisInstrStart(starts, i)) {
			switch (info[i]) {
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

	private int handleFCMPG(OperandStack stack, byte[] info) {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.FLOAT,
				JvmOpCodes.FCMPG);
	}

	private int handleFCMPL(OperandStack stack, byte[] info) {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.FLOAT,
				JvmOpCodes.FCMPL);
	}

	private void handleLCMP(OperandStack stack, byte[] info) {
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.LONG,
				JvmOpCodes.LCMP);
	}

	private int handleISTORE(int opcodeval) {

		return execute(InstrConstants.STORE_INSTR_TYPE, InstrConstants.INT,
				opcodeval);

	}

	private void adjustBracketCount(Operand op) {
		java.lang.String value = op.getOperandValue();
		int openB = 0;
		int closeB = 0;
		if (value != null && value.indexOf("(") != -1
				&& value.indexOf(")") != -1) {
			for (int c = 0; c < value.length(); c++) {
				char ch = value.charAt(c);
				if (ch == ')')
					closeB++;
				else if (ch == '(')
					openB++;
			}
			if (closeB > openB) {

				java.lang.String temp = value.trim();
				int number = closeB - openB;
				if (temp.charAt(temp.length() - 1) == ')') {
					if (temp.length() - number < 0)
						number = 1; // A Crude way of avoiding Exception If any
					java.lang.String str = value.substring(0, temp.length()
							- number);
					op.setOperandValue(str);
				}

			}
			if (closeB < openB) {

				java.lang.String temp = value.trim();
				int number = openB - closeB;
				java.lang.String str = "";
				for (int z = 1; z <= number; z++)
					str += ")";
				op.setOperandValue(temp + str);
			}

		}

	}

	private boolean checkForLoadBeforeIINC(byte[] info, OperandStack opStack,
			int current, LocalVariable local, int index, java.lang.String c) {

		boolean b = false;
		int loadindex = -1;
		boolean ok = false;
		boolean iloadfnd = false;
		int j = getPrevStartOfInst(current, info);
		if (isThisInstrStart(behaviour.getInstructionStartPositions(), j)) {

			switch (info[j]) {

			case JvmOpCodes.ILOAD:
				loadindex = info[(j + 1)];
				iloadfnd = true;
				break;

			case JvmOpCodes.ILOAD_0:
				loadindex = 0;
				iloadfnd = true;
				break;

			case JvmOpCodes.ILOAD_1:
				loadindex = 1;
				iloadfnd = true;
				break;

			case JvmOpCodes.ILOAD_2:
				loadindex = 2;
				iloadfnd = true;
				break;

			case JvmOpCodes.ILOAD_3:
				loadindex = 3;
				iloadfnd = true;
				break;

			case JvmOpCodes.IALOAD:
				loadindex = -1;
				ok = true;
				break;

			}

			if (loadindex != -1) {
				if (loadindex == index) {

					if (c.trim().indexOf("-1") != -1) {
						b = true;
						local.setTempVarName(local.getVarName() + "--");
						opStack.peekTopOfStack().setOperandValue(
								local.getTempVarName());
					} else if (c.trim().indexOf("1") != -1) {
						b = true;
						local.setTempVarName(local.getVarName() + "++");
						opStack.peekTopOfStack().setOperandValue(
								local.getTempVarName());

					} else {
						b = false;
					}
				}
			} else {
				b = false;
			}

			if (ok) {
				if (c.trim().indexOf("-1") != -1) {
					b = true;
					Operand currentTop = opStack.peekTopOfStack();
					if (currentTop != null) {
						java.lang.String topval = currentTop.getOperandValue();
						topval = topval + "--";
						currentTop.setOperandValue(topval);
						b = true;
					} else {
						b = false;
					}
				} else if (c.trim().indexOf("1") != -1) {
					b = true;
					Operand currentTop = opStack.peekTopOfStack();
					if (currentTop != null) {
						java.lang.String topval = currentTop.getOperandValue();
						topval = topval + "++";
						currentTop.setOperandValue(topval);
						b = true;
					} else {
						b = false;
					}
				} else {
					b = false;
				}
			}
			/*
			 * if(!b && !iloadfnd) { j=current+3; StringBuffer buf=new
			 * StringBuffer(""); boolean
			 * istore=isThisInstructionIStoreInst(info,j,buf); if(istore==false) {
			 * if(c.trim().indexOf("-1")!=-1) { b=true; Operand
			 * currentTop=opStack.peekTopOfStack(); if(currentTop!=null) {
			 * java.lang.String topval=currentTop.getOperandValue();
			 * topval=topval+"--"; currentTop.setOperandValue(topval); b=true; }
			 * else { b=false; } } else if(c.trim().indexOf("1")!=-1) { b=true;
			 * Operand currentTop=opStack.peekTopOfStack(); if(currentTop!=null) {
			 * java.lang.String topval=currentTop.getOperandValue();
			 * topval=topval+"++"; currentTop.setOperandValue(topval); b=true; }
			 * else { b=false; } } else { b=false; } } }
			 */

		}

		return b;
	}

	private boolean checkForLoadAfterIINC(byte[] info, OperandStack opStack,
			int current, LocalVariable local, int index, java.lang.String c) {

		boolean b = false;
		int j = current + 1 + 1 + 1;
		int loadindex = -1;
		boolean ok = false;
		boolean iloadfnd = false;
		if (isThisInstrStart(behaviour.getInstructionStartPositions(), j)) {

			switch (info[j]) {
			case JvmOpCodes.ILOAD:
				loadindex = info[(j + 1)];
				iloadfnd = true;
				break;

			case JvmOpCodes.ILOAD_0:
				loadindex = 0;
				iloadfnd = true;
				break;

			case JvmOpCodes.ILOAD_1:
				loadindex = 1;
				iloadfnd = true;
				break;

			case JvmOpCodes.ILOAD_2:
				loadindex = 2;
				iloadfnd = true;
				break;

			case JvmOpCodes.ILOAD_3:
				loadindex = 3;
				iloadfnd = true;
				break;

			case JvmOpCodes.IALOAD:
				loadindex = -1;
				ok = true;
				break;
			}

			if (loadindex != -1) {
				if (loadindex == index) {

					if (c.trim().indexOf("-1") != -1) {
						b = true;
						local.setTempVarName("--" + local.getVarName());
					} else if (c.trim().indexOf("1") != -1) {
						b = true;
						local.setTempVarName("++" + local.getVarName());
					} else {
						b = false;
					}
				}
			} else {
				b = false;
			}

			if (ok) {
				if (c.trim().indexOf("-1") != -1) {
					b = true;
					Operand currentTop = opStack.peekTopOfStack();
					if (currentTop != null) {
						java.lang.String topval = currentTop.getOperandValue();
						topval = topval + "--";
						currentTop.setOperandValue(topval);
						b = true;
					} else {
						b = false;
					}
				} else if (c.trim().indexOf("1") != -1) {
					b = true;
					Operand currentTop = opStack.peekTopOfStack();
					if (currentTop != null) {
						java.lang.String topval = currentTop.getOperandValue();
						topval = topval + "++";
						currentTop.setOperandValue(topval);
						b = true;
					} else {
						b = false;
					}
				} else {
					b = false;
				}
			}
			/*
			 * if(!b && !iloadfnd) { j=current+1+1+1; StringBuffer buf=new
			 * StringBuffer(""); boolean
			 * istore=isThisInstructionIStoreInst(info,j,buf); if(istore==false) {
			 * if(c.trim().indexOf("-1")!=-1) { b=true; Operand
			 * currentTop=opStack.peekTopOfStack(); if(currentTop!=null) {
			 * java.lang.String topval=currentTop.getOperandValue();
			 * topval=topval+"--"; currentTop.setOperandValue(topval); b=true; }
			 * else { b=false; } } else if(c.trim().indexOf("1")!=-1) { b=true;
			 * Operand currentTop=opStack.peekTopOfStack(); if(currentTop!=null) {
			 * java.lang.String topval=currentTop.getOperandValue();
			 * topval=topval+"++"; currentTop.setOperandValue(topval); b=true; }
			 * else { b=false; } } else { b=false; } } }
			 */

		}

		return b;
	}

	private boolean isPrevInstIINC(byte[] info, int current, int index) {
		int prev = current - 3;
		if (isThisInstrStart(behaviour.getInstructionStartPositions(), prev)) {

			if (info[prev] == JvmOpCodes.IINC) {
				int j = info[prev + 1];
				if (index == j) {
					return true;
				}
			}

		}

		return false;

	}

	private boolean isNextInstIINC(byte[] info, int current, int index,
			java.lang.String type) {
		int next;
		if (type.equals("complex")) {
			next = current + 2;
		} else {
			next = current + 1;
		}
		if (isThisInstrStart(behaviour.getInstructionStartPositions(), next)) {

			if (info[next] == JvmOpCodes.IINC) {
				int j = info[next + 1];
				if (index == j) {
					return true;
				}
			}

		}

		return false;

	}

	private boolean addReturnAtIFElseEnd(int i) {

		if (returnsaddedAtIfElse.size() == 0)
			return true;
		for (int z = 0; z < returnsaddedAtIfElse.size(); z++) {
			Integer in = new Integer(i);
			Integer val = (Integer) returnsaddedAtIfElse.get(z);
			if (val.intValue() == in.intValue()) {
				return false;
			}
		}
		return true;
	}

	private boolean isByteCodeALoopStart(ArrayList loops, int bytecodeend) {
		if (loops == null || loops.size() == 0)
			return false;
		for (int z = 0; z < loops.size(); z++) {
			Loop loop = (Loop) loops.get(z);
			int loopstart = loop.getStartIndex();
			if (loopstart == bytecodeend) {
				return true;
			}
		}
		return false;
	}

	private int getIfCloseNumberForThisIF(byte[] info, int k) {
		IFBlock ifst = new IFBlock();
		ifst.setIfStart(k);
		ifst.setHasIfBeenGenerated(true);

		behaviour.getMethodIfs().add(ifst);
		// addBranchLabel(classIndex,i,ifst,currentForIndex,info);
		int i = k;
		int classIndex = getJumpAddress(info, i);
		i++;
		i++;
		boolean continuetofind = false;
		if (classIndex < i) {
			ifst.setIfCloseLineNumber(findCodeIndexFromInfiniteLoop(ifst,
					behaviour.getBehaviourLoops(), classIndex));
			if (ifst.getIfCloseLineNumber() == -1)
				continuetofind = true;
		}
		if (classIndex > i || continuetofind) {
			if (isThisInstrStart(behaviour.getInstructionStartPositions(),
					classIndex - 3)
					&& info[classIndex - 3] == JvmOpCodes.GOTO) // GOTO_W?
			{

				int resetVal = checkIfElseCloseNumber(classIndex - 3, ifst);
				ifst.setIfCloseLineNumber(resetVal);
			} else {

				int resetVal = checkIfElseCloseNumber(classIndex, ifst);
				ifst.setIfCloseLineNumber(resetVal);
			}

		}

		int if_start = ifst.getIfStart();
		int if_end = ifst.getIfCloseLineNumber();
		if (if_end == -1 || if_end < if_start) {
			boolean b = false;
			int bytecodeend = ifst.getIfCloseFromByteCode();
			b = isByteCodeALoopStart(behaviour.getBehaviourLoops(), bytecodeend);
			if (b) {
				int loopend = getloopEndForStart(behaviour.getBehaviourLoops(),
						bytecodeend);
				if (loopend != -1) {
					ifst.setIfCloseLineNumber(loopend);
				}
			}
		}

		return ifst.getIfCloseLineNumber();

	}

	private ArrayList addedHandlerEnds = new ArrayList();

	private ArrayList handlerEndLocationsAdded = new ArrayList();

	// TODO : Need to rethink why this method was introduced
	// Commented for 1.2 release Feb 25 2007
	private boolean isHandlerEndPresentAtGuardEnd(int i) {
		/*
		 * if(addedHandlerEnds.size()==0)return false; for(int z=0;z<addedHandlerEnds.size();z++) {
		 * Integer in=(Integer)addedHandlerEnds.get(z); if(in.intValue()==i) {
		 * return true; } }
		 */
		return false;
	}

	/**
	 * s--> dummy
	 * 
	 * @param i
	 * @param s
	 * @return
	 */
	private boolean isHandlerEndPresentAtGuardEnd(int i, String s) {

		if (handlerEndLocationsAdded.size() == 0)
			return false;
		for (int z = 0; z < handlerEndLocationsAdded.size(); z++) {
			Integer in = (Integer) handlerEndLocationsAdded.get(z);
			if (in.intValue() == i) {
				return true;
			}
		}

		return false;
	}

	private boolean isThisTryStart(int i) {
		ArrayList tries = behaviour.getAllTriesForMethod();
		if (tries != null) {
			for (int z = 0; z < tries.size(); z++) {

				TryBlock TRY = (TryBlock) tries.get(z);
				if (TRY.getEnd() == i)
					return true;

			}

		}
		return false;

	}

	private ArrayList storesatifend = new ArrayList();

	private boolean storealreadyhandledatifend(int i) {
		if (storesatifend.size() == 0)
			return false;
		for (int z = 0; z < storesatifend.size(); z++) {
			Integer in = (Integer) storesatifend.get(z);
			if (in.intValue() == i) {
				return true;
			}
		}

		return false;
	}

	private boolean addElseStart(int i) {
		if (GlobalVariableStore.getElsestartadded().size() == 0) {
			return true;
		}
		for (int z = 0; z < GlobalVariableStore.getElsestartadded().size(); z++) {
			Integer in = (Integer) GlobalVariableStore.getElsestartadded().get(
					z);
			if (in.intValue() == i) {
				return false;
			}
		}
		return true;
	}

	private boolean isInstStore0(byte[] info, int i) {

		boolean flag = false;
		boolean b = isThisInstrStart(behaviour.getInstructionStartPositions(),
				i);
		if (!b)
			return false;
		switch (info[i]) {
		case JvmOpCodes.AASTORE:
			flag = true;
			break;

		case JvmOpCodes.BASTORE:
			flag = true;
			break;
		case JvmOpCodes.CASTORE:
			flag = true;
			break;
		case JvmOpCodes.DASTORE:
			flag = true;
			break;

		case JvmOpCodes.FASTORE:
			flag = true;
			break;

		case JvmOpCodes.IASTORE:
			flag = true;
			break;

		case JvmOpCodes.LASTORE:
			flag = true;
			break;

		case JvmOpCodes.SASTORE:
			flag = true;
			break;

		default:
			flag = false;
		}
		return flag;
	}

	private HashMap putfieldObjRefMap = new HashMap();

	private Operand checkAnyStoredPUTFIELDObjRef(int i) {
		if (putfieldObjRefMap.size() == 0)
			return null;
		Set s = putfieldObjRefMap.entrySet();
		Iterator it = s.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Integer in = (Integer) entry.getKey();
			if (in.intValue() == i) {
				return (Operand) entry.getValue();
			}
		}
		return null;
	}

	private boolean checkForEndOfTryCatch(int i) {

		boolean flag = false;
		ArrayList list = behaviour.getAllTriesForMethod();
		if (list == null || list.size() == 0)
			return false;
		for (int z = 0; z < list.size(); z++) {
			TryBlock tryb = (TryBlock) list.get(z);
			int tryend = tryb.getEnd();
			if (tryend == i) {
				behaviour.appendToBuffer(Util
						.formatDecompiledStatement("\n}\n"));
				flag = true;
			}
			ArrayList catchlist = tryb.getAllCatchesForThisTry();
			for (int x = 0; x < catchlist.size(); x++) {
				CatchBlock cb = (CatchBlock) catchlist.get(x);
				int ce = cb.getEnd();
				if (ce == i) {
					boolean empty = checkForEmptyCatch(cb.getStart(), cb
							.getEnd());
					if (empty) {
						// codeStatements+=Util.formatDecompiledStatement("\n/*Empty
						// Handler Block*/\n");
					}
					behaviour.appendToBuffer(Util
							.formatDecompiledStatement("\n}\n"));
					flag = true;
				}
			}
			FinallyBlock finb = tryb.getFinallyBlock();
			if (finb != null) {
				int fine = finb.getEnd();
				int fins = finb.getStart();
				if (fine == i) {
					boolean empty = checkForEmptyFinally(fins, fine);
					if (empty) {
						// codeStatements+=Util.formatDecompiledStatement("\n/*Empty
						// Handler Block*/\n");
					}
					behaviour.appendToBuffer(Util
							.formatDecompiledStatement("\n}\n"));
					flag = true;
				}
				if (fins == i) {
					boolean addfin = okToaddFinally(i);
					if (addfin) {
						behaviour.appendToBuffer(Util
								.formatDecompiledStatement("\nfinally\n{\n"));
						Integer in = new Integer(i);
						somefinallystarts.put(in, in);
					}
					flag = true;
				}

			}

		}
		return flag;
	}

	private boolean okToaddFinally(int i) {
		if (somefinallystarts.size() == 0)
			return true;
		Object ob = somefinallystarts.get(new Integer(i));
		if (ob == null)
			return true;
		return false;

	}

	private HashMap somefinallystarts = new HashMap();

	private ArrayList getFinallyCount(ArrayList tableList) {
		if (tableList == null || tableList.size() == 0)
			return null;
		ArrayList temp = new ArrayList();
		for (int z = 0; z < tableList.size(); z++) {
			ExceptionTable t = (ExceptionTable) tableList.get(z);
			int x = t.getStartOfHandler();
			temp.add(new Integer(x));
		}
		/*
		 * Integer in[]=(Integer[])temp.toArray(new Integer[temp.size()]);
		 * Arrays.sort(in);
		 */
		temp = (ArrayList) Util.genericRemoveDuplicates(temp);
		return temp;

	}

	private int handleFASTORE() {
		return execute(InstrConstants.STORE_INSTR_TYPE, InstrConstants.FLOAT,
				JvmOpCodes.FASTORE);

	}

	private void handleIASTORE() {
		execute(InstrConstants.STORE_INSTR_TYPE, InstrConstants.INT,
				JvmOpCodes.IASTORE);
	}

	private void addAnyReurnAtDefaultEnd(int i, OperandStack stack,
			byte[] info, StringBuffer S) {
		StringBuffer sg = new StringBuffer("");
		boolean r = isInstReturnInst(info, i, sg);

		if (r) {
			if (sg.toString().equals("return") == false && stack.size() > 0) {
				java.lang.String t = stack.getTopOfStack().getOperandValue();
				boolean b = isMethodRetBoolean(behaviour);
				if (t != null) {
					if (t.equals("1") && b)
						t = "true";
					if (t.equals("0") && b)
						t = "false";
					java.lang.String retst = "return " + t + ";";
					boolean a = addRETURNatI(i, null);
					if (a) {
						S.append(Util.formatDecompiledStatement(retst));
						GlobalVariableStore.getReturnsAtI().put(new Integer(i),
								"true");
					}
				}

			} else if (sg.toString().equals("return") == true) {
				boolean a = addRETURNatI(i, null);
				if (a) {
					java.lang.String retst = "return ;";
					S.append(Util.formatDecompiledStatement(retst));
					GlobalVariableStore.getReturnsAtI().put(new Integer(i),
							"true");
				}
			} else {

			}
		}
	}

	private boolean checkForParentLoopForIF(IFBlock ifst) {
		ArrayList loops = behaviour.getBehaviourLoops();
		if (loops != null) {
			Object[] sortedLoops = sortLoops(loops);
			int parentLoopStart = getParentLoopStartForIf(sortedLoops, ifst
					.getIfStart());
			if (parentLoopStart == -1) {
				return false;
			} else {
				int by = ifst.getIfCloseFromByteCode();
				Loop l = getParentLoopForIf(sortedLoops, ifst.getIfStart());
				if (l != null) {
					int le = l.getEndIndex();
					if (by > ifst.getIfStart() && by < le) {
						return false;
					}
				}

				return true;
			}
		}
		return false;
	}

	private void handleSimpleILoad(int index, byte[] info) {
		int opcodeval;
		switch (index) {
		case 0:
			opcodeval = JvmOpCodes.ILOAD_0;
			break;
		case 1:
			opcodeval = JvmOpCodes.ILOAD_1;
			break;
		case 2:
			opcodeval = JvmOpCodes.ILOAD_2;
			break;
		default:
			opcodeval = JvmOpCodes.ILOAD_3;
			break;
		}

		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT, opcodeval);
	}

	private int handleComplexFSTORE() {
		return execute(InstrConstants.STORE_INSTR_TYPE, InstrConstants.FLOAT,
				JvmOpCodes.FSTORE);
	}

	private boolean isIfForLoadClass(IFBlock ifst, byte[] info) {
		boolean p = true;
		int by = ifst.getIfCloseFromByteCode();

		if (by > ifst.getIfStart()) {
			int j = by - 3;
			ArrayList list = behaviour.getInstructionStartPositions();
			if (isThisInstrStart(list, j) && info[j] == JvmOpCodes.GOTO) {
				int k = getJumpAddress(info, j);
				if ((k - j) == 6) {
					int s = ifst.getIfStart();
					s = s + 3;
					if (info[s] == JvmOpCodes.LDC) {
						int d = (s + 1);
						int offset = info[d];
						if (offset < 0)
							offset += 256;
						CPString constString = cd
								.getStringsAtCPoolPosition(offset);
						if (constString == null) {
							return true;
						}
						java.lang.String stringLiteral = cd
								.getUTF8String(constString.getUtf8pointer());
						if (stringLiteral == null) {
							return true;
						}
						s = s + 2;
						if (isThisInstrStart(list, s)
								&& info[s] == JvmOpCodes.INVOKESTATIC) {
							int classIndex = getOffset(info, s);
							MethodRef mref = cd
									.getMethodRefAtCPoolPosition(classIndex);
							java.lang.String name = mref.getMethodName();
							if (name != null && name.indexOf("class$") != -1) {

								Operand op = new Operand();
								StringBuffer sb = new StringBuffer("");
								Util.checkForImport(stringLiteral, sb);
								java.lang.String classToLoad = sb.toString()
										+ ".class";
								boolean yes = isIfPartOfTernaryIfCond(info,
										currentForIndex);
								/*
								 * if(yes) {
								 * op.setOperandValue(opStack.getTopOfStack().getOperandValue()+classToLoad); }
								 * else
								 */
								op.setOperandValue(classToLoad);
								boolean terend = isTernaryEnd(currentForIndex);
								if (terend) {
									op.setOperandValue(op.getOperandValue()
											+ ")");
								}

								opStack.push(op);
								op.setClassType("Class");
								ArrayList instStart = behaviour
										.getInstructionStartPositions();
								int next = k + 1;

								if (info[k] == JvmOpCodes.DUP
										|| info[k] == JvmOpCodes.DUP2) {
									boolean store = isStoreInst(next, info);
									if (store)
										k = next;
								}
								GlobalVariableStore.getSkipWRTClassLoadIf()
										.put(
												new Integer(
														ifst.getIfStart() + 3),
												new Integer(k));
								return false;
							}
						} else
							return true;

					} else if (info[s] == JvmOpCodes.LDC_W) {
						// int d=(s+1);
						int offset = getOffset(info, s);
						// if(offset < 0)offset+=256;
						CPString constString = cd
								.getStringsAtCPoolPosition(offset);
						if (constString == null) {
							return true;
						}
						java.lang.String stringLiteral = cd
								.getUTF8String(constString.getUtf8pointer());
						if (stringLiteral == null) {
							return true;
						}
						s = s + 3;
						if (isThisInstrStart(list, s)
								&& info[s] == JvmOpCodes.INVOKESTATIC) {
							int classIndex = getOffset(info, s);
							MethodRef mref = cd
									.getMethodRefAtCPoolPosition(classIndex);
							java.lang.String name = mref.getMethodName();
							if (name != null && name.indexOf("class$") != -1) {

								Operand op = new Operand();
								StringBuffer sb = new StringBuffer("");
								Util.checkForImport(stringLiteral, sb);
								java.lang.String classToLoad = sb.toString()
										+ ".class";
								op.setOperandValue(classToLoad);
								opStack.push(op);
								op.setClassType("Class");
								GlobalVariableStore.getSkipWRTClassLoadIf()
										.put(
												new Integer(
														ifst.getIfStart() + 3),
												new Integer(k));
								return false;
							}
						} else
							return true;

					} else {
						return true;

					}

				}

			}

		}

		return p;
	}

	private boolean checkWRTClassLoadIF(int i) {
		if (GlobalVariableStore.getSkipWRTClassLoadIf().size() == 0)
			return false;
		boolean skip = false;
		Set s = GlobalVariableStore.getSkipWRTClassLoadIf().entrySet();
		Iterator it = s.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Integer st = (Integer) entry.getKey();
			Integer end = (Integer) entry.getValue();
			if (i >= st.intValue() && i < end.intValue()) {
				skip = true;
				break;
			}

		}
		return skip;

	}

	private boolean isStoreInst(int index, byte[] info) {

		if (index < 0)
			return false;
		boolean b = isThisInstrStart(behaviour.getInstructionStartPositions(),
				index);
		if (!b)
			return false;
		switch (info[index]) {
		case JvmOpCodes.AASTORE:
		case JvmOpCodes.ASTORE:
		case JvmOpCodes.ASTORE_0:
		case JvmOpCodes.ASTORE_1:
		case JvmOpCodes.ASTORE_2:
		case JvmOpCodes.ASTORE_3:

		case JvmOpCodes.BASTORE:
		case JvmOpCodes.CASTORE:
		case JvmOpCodes.DASTORE:
		case JvmOpCodes.DSTORE:
		case JvmOpCodes.DSTORE_0:
		case JvmOpCodes.DSTORE_1:
		case JvmOpCodes.DSTORE_2:
		case JvmOpCodes.DSTORE_3:
		case JvmOpCodes.FASTORE:
		case JvmOpCodes.FSTORE:
		case JvmOpCodes.FSTORE_0:
		case JvmOpCodes.FSTORE_1:
		case JvmOpCodes.FSTORE_2:
		case JvmOpCodes.FSTORE_3:
		case JvmOpCodes.IASTORE:
		case JvmOpCodes.ISTORE:
		case JvmOpCodes.ISTORE_0:
		case JvmOpCodes.ISTORE_1:
		case JvmOpCodes.ISTORE_2:
		case JvmOpCodes.ISTORE_3:
		case JvmOpCodes.LASTORE:
		case JvmOpCodes.LSTORE:
		case JvmOpCodes.LSTORE_0:
		case JvmOpCodes.LSTORE_1:
		case JvmOpCodes.LSTORE_2:
		case JvmOpCodes.LSTORE_3:
		case JvmOpCodes.SASTORE:
			return true;
		default:
			return false;

		}

	}

	private boolean checkForCodeInRange(int start, int end, byte[] info,
			int lookfor) {
		ArrayList list = behaviour.getInstructionStartPositions();
		for (int z = start; z < end; z++) {
			if (info[z] == lookfor && isThisInstrStart(list, z)) {
				return true;
			}
		}
		return false;
	}

	private int handleGetField(byte[] info) {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.OBJECT,
				JvmOpCodes.GETFIELD);
	}

	// op.setClassType();

	private boolean checkForSizeOfArrayTimesStack() {
		if (GlobalVariableStore.getArraytimesstack().size() > 0) {
			return true;
		} else
			return false;
	}

	private int handleComplexDStore() {
		return execute(InstrConstants.STORE_INSTR_TYPE, InstrConstants.DOUBLE,
				JvmOpCodes.DSTORE);
	}

	private int checkEndOFTernary(IFBlock ifs, int terend, int gotos,
			byte[] info) {

		int start = gotos + 3;
		ArrayList list = behaviour.getInstructionStartPositions();
		for (int z = start; z < terend; z++) {
			if (info[z] == JvmOpCodes.GOTO && isThisInstrStart(list, z)) {
				return z;
			}

		}

		return terend;

	}

	private boolean isTernaryEnd(int i) {
		return false;
	}

	private boolean isIFForThisElseATernaryIF(int current, Hashtable allifs,
			StringBuffer sb, byte[] info) {
		boolean yes = false;

		Iterator iterIfHash = behaviour.getMethodIfs().iterator();
		while (iterIfHash.hasNext()) {
			IFBlock ifs = (IFBlock) iterIfHash.next();

			int thisifclose = ifs.getIfCloseLineNumber();
			if ((ifs.getIfCloseLineNumber() - (current)) == 0) {
				boolean present = isIFpresentInTernaryList(ifs);
				if (present) {
					boolean end = isThisGotoTernaryEnd(ifs, current);
					if (end) {
						sb.append("end");
					}
					return true;
				}
			}
		}
		return yes;
	}

	private boolean isIFpresentInTernaryList(IFBlock ifst) {

		return false;

	}

	private boolean isThisGotoTernaryEnd(IFBlock ifst, int end) {

		return false;

	}

	private boolean isTernaryCondition(int i, byte[] info) {

		return false;

	}

	private IFBlock getIFFromTernaryListGivenByteCodeEnd(int by, StringBuffer sb) {
		return null;

	}

	private IFBlock getIFFromTernaryListGivenIFStart(int start, StringBuffer sb) {

		return null;

	}

	private boolean isIfPartOfTernaryIfCond(byte[] info, int current) {
		return false;

	}

	private boolean isLoadTernaryEnd(int current) {
		return false;
	}

	private boolean isGotoPrecededByDUPSTORE(int current, byte[] info) // FIXME
	{
		boolean yes = false;
		int prev = current - 1;
		ArrayList list = behaviour.getInstructionStartPositions();
		boolean store = false;
		boolean dup = false;
		int x = -1;
		while (prev > 0) {
			if (isThisInstrStart(list, prev)) {
				store = isNextInstructionStore(info[prev]);
				x = prev;
				if (store) {
					dup = isPrevInstDup(info, x);
					if (dup)
						return true;
				}

				if (info[prev] == JvmOpCodes.GOTO) {
					return false;
				}
			}
			prev--;
		}

		return yes;
	}

	private int handleComplexIload() {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
				JvmOpCodes.ILOAD);
	}

	private boolean checkForDUPStoreAtEndOFTernaryIF(int current, byte[] info,
			StringBuffer in, StringBuffer ty, StringBuffer lpos) {
		int x = current - 3;
		ArrayList list = behaviour.getInstructionStartPositions();
		if (isThisInstrStart(list, x) && info[x] == JvmOpCodes.GOTO) {
			int gotoj = getJumpAddress(info, x);
			boolean dup = isPrevInstDup(info, gotoj + 1);
			if (dup) {

				boolean store = isStoreInst(gotoj + 1, info, in, ty);// /isNextInstructionStore(info[gotoj+1]);
				if (store) {
					lpos.append(gotoj + 1);
					return true;
				}
			}

		}
		return false;

	}

	private boolean isThisDUPSTOREAtEndOFTernaryIF(int pos, byte[] info,
			java.lang.String type) {

		return false;

	}

	private boolean isThisTernaryListEnd(int current) {

		return false;

	}

	private boolean isThisTernaryListEndForParentIF(int current) {

		return false;

	}

	private void handleIFEQ(byte[] info) {
		execute(InstrConstants.BRANCH_INSTR_TYPE, null, JvmOpCodes.IFEQ);
	}

	private java.lang.String tempstr = "";

	private java.lang.String tempString = "";

	private int handleComplexLSTORE() {
		return execute(InstrConstants.STORE_INSTR_TYPE, InstrConstants.LONG,
				JvmOpCodes.LSTORE);
	}

	private boolean checkIFLoadInstIsPartOFTernaryCond(int current) {

		return false;
	}

	private boolean checkIFNewIsPartOFTernaryCond(int current) {

		return false;
	}

	private boolean newfound() {
		// return true;
		if (GlobalVariableStore.getNewfoundstack().size() > 0)
			return true;
		return false;
	}

	private boolean prevNewPresent() {
		if (GlobalVariableStore.getNewfoundstack().size() < 2)
			return false;
		return true;
	}

	private boolean anydupstoreinternarybesidesthis(int current, byte[] info) {
		boolean yes = false;
		IFBlock iF = getParentIFInTernaryList();
		if (iF == null)
			return false;
		int x = iF.getIfStart() + 3;
		ArrayList list = behaviour.getInstructionStartPositions();
		while (x < current - 1) {
			if (isThisInstrStart(list, x)) {
				boolean dup = false;
				if (info[x] == JvmOpCodes.DUP || info[x] == JvmOpCodes.DUP2) {
					int next = x + 1;
					if (isStoreInst(next, info)) {
						return true;
					}
				}
			}
			x++;

		}

		return yes;

	}

	private IFBlock getParentIFInTernaryList() {

		return null;

	}

	private int getPrevStartOfInst(int current, byte[] info) {
		int start = current - 1;
		ArrayList list = behaviour.getInstructionStartPositions();
		while (start >= 0) {
			boolean s = isThisInstrStart(list, start);
			if (s)
				return start;
			start--;
		}
		return start;
	}

	private void handleICONST(int value) {
		int opcode;
		switch (value) {
		case 0:
			opcode = JvmOpCodes.ICONST_0;
			break;
		case 1:
			opcode = JvmOpCodes.ICONST_1;
			break;
		case 2:
			opcode = JvmOpCodes.ICONST_2;
			break;
		case 3:
			opcode = JvmOpCodes.ICONST_3;
			break;
		case 4:
			opcode = JvmOpCodes.ICONST_4;
			break;
		case 5:
			opcode = JvmOpCodes.ICONST_5;
			break;
		default:

			opcode = JvmOpCodes.ICONST_M1;
			break;
		}
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT, opcode);

	}

	private void handleIDIV() {
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
				JvmOpCodes.IDIV);
	}

	private int handleATHROW(byte[] info) {
		return execute(InstrConstants.BRANCH_INSTR_TYPE, null,
				JvmOpCodes.ATHROW);
	}

	private int handleD2F() {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.FLOAT,
				JvmOpCodes.D2F);

	}

	private int handleD2I() {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
				JvmOpCodes.D2I);
	}

	private int handleD2L() {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.LONG,
				JvmOpCodes.D2L);
	}

	private int handleDADD() {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.DOUBLE,
				JvmOpCodes.DADD);
	}

	private int handleDSUB() {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.DOUBLE,
				JvmOpCodes.DSUB);
	}

	private int handleF2D() {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.DOUBLE,
				JvmOpCodes.F2D);
	}

	private int handleF2I() {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
				JvmOpCodes.F2I);
	}

	private int handleF2L() {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.LONG,
				JvmOpCodes.F2L);
	}

	private int handleFADD() {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.FLOAT,
				JvmOpCodes.FADD);
	}

	private int handleFALOAD() {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.FLOAT,
				JvmOpCodes.FALOAD);
	}

	private int handleFMUL() {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.FLOAT,
				JvmOpCodes.FMUL);
	}

	private int handleFNEG() {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.FLOAT,
				JvmOpCodes.FNEG);
	}

	private int handleFREM() {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.FLOAT,
				JvmOpCodes.FREM);
	}

	private int handleFRETURN() {
		return execute(InstrConstants.BRANCH_INSTR_TYPE, null,
				JvmOpCodes.FRETURN);
	}

	private boolean isSuperClass(java.lang.String someClassName,
			java.lang.String currentSuperClass) {
		boolean yes = false;
		if (someClassName == null || currentSuperClass == null)
			return false;
		someClassName = someClassName.replaceAll("/", ".").trim();
		currentSuperClass = currentSuperClass.replaceAll("/", ".").trim();
		if (someClassName.equals(currentSuperClass)) {
			return !yes;
		}
		java.lang.String temp = currentSuperClass;
		do {
			try {

				Class z = Class.forName(temp + ".class");
				Class parent = z.getSuperclass();
				if (parent != null) {
					if (parent.getName().replaceAll("/", ".").trim().equals(
							someClassName)) {
						return true;
					}
				} else {
					return false;
				}
				temp = parent.getName().replaceAll("/", ".").trim();
			} catch (ClassNotFoundException cne) {
				Writer writer = null;
				try {
					writer = Writer.getWriter("log");
					cne.printStackTrace(writer);
				} catch (IOException ex) {

				}

			}
		} while (true);

	}

	private void handleIUSHR() {
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
				JvmOpCodes.IUSHR);
	}

	private void handleIXOR() {
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
				JvmOpCodes.IXOR);
	}

	private void handleL2D() {
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.DOUBLE,
				JvmOpCodes.L2D);
	}

	private void handleL2F() {
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.FLOAT,
				JvmOpCodes.L2F);
	}

	private int handleFSUB() {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.FLOAT,
				JvmOpCodes.FSUB);
	}

	private int handleGetStatic() {

		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.OBJECT,
				JvmOpCodes.GETSTATIC);
	}

	private void handleI2B() {
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
				JvmOpCodes.I2B);
	}

	private void handleI2C() {
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
				JvmOpCodes.I2C);
	}

	private void handleI2D() {
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
				JvmOpCodes.I2D);
	}

	private void handleI2F() {
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
				JvmOpCodes.I2F);
	}

	private void handleI2L() {
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
				JvmOpCodes.I2L);
	}

	private void handleI2S() {
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
				JvmOpCodes.I2S);
	}

	private void handleIADD() {
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
				JvmOpCodes.IADD);
	}

	private void handleIALOAD() {
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
				JvmOpCodes.IALOAD);
	}

	private void handleIAND() {
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
				JvmOpCodes.IAND);
	}

	private int handleDRETURN(byte[] info) {
		return execute(InstrConstants.BRANCH_INSTR_TYPE, null,
				JvmOpCodes.DRETURN);
	}

	private void handleIRETURN() {
		execute(InstrConstants.BRANCH_INSTR_TYPE, null, JvmOpCodes.IRETURN);
	}

	private void checkForBooleanOperandValue(Operand needToCheck, Operand op1) {
		java.lang.String type = op1.getLocalVarType();
		java.lang.String type2 = op1.getClassType();
		if ((type != null && type.trim().equals("boolean"))
				|| (type2 != null && type2.trim().equals("boolean"))) {
			java.lang.String val = needToCheck.getOperandValue();
			if (val != null && val.trim().equals("1")) {
				needToCheck.setOperandValue("true");
			}
			if (val != null && val.trim().equals("0")) {
				needToCheck.setOperandValue("false");
			}

		}

	}

	private int handleIFACMPEQ(byte[] info) {
		return execute(InstrConstants.BRANCH_INSTR_TYPE, null,
				JvmOpCodes.IF_ACMPEQ);
	}

	private boolean checkForInvalidElse(int current, IFBlock ifs, byte[] info) {
		boolean add = true;
		int ifstart = ifs.getIfStart();
		int ifjump = getJumpAddress(info, ifstart);
		int from = ifstart + 3;
		int till = current - 1;
		ArrayList starts = behaviour.getInstructionStartPositions();
		for (int z = from; z <= till; z++) {
			boolean isStart = isThisInstrStart(starts, z);
			if (isStart) {
				boolean isif = isInstructionIF(info[z]);
				if (isif) {
					int jump = getJumpAddress(info, z);
					if (jump == ifjump) {
						return false;
					}
				}
				if (info[z] == JvmOpCodes.GOTO) {
					int jump = getJumpAddress(info, z);
					if (jump == ifjump) {
						return false;
					}
				}
			}

		}

		return add;
	}

	private boolean doesLoopHaveAnIfAtEnd(int i, byte[] info, Loop iloop) {
		ArrayList loops = behaviour.getBehaviourLoops();
		ArrayList starts = behaviour.getInstructionStartPositions();
		for (int z = 0; z < loops.size(); z++) {
			Loop loop = (Loop) loops.get(z);
			int loopstart = loop.getStartIndex();
			if (loopstart == i && loop == iloop) {
				int loopend = loop.getEndIndex();
				boolean isStart = isThisInstrStart(starts, loopend);
				// NOTE: belurs
				// No Need to Do this Actually.
				// Just checking to be 100% sure that no mistake happens due to
				// this
				if (isStart) {
					boolean isIf = isInstructionIF(info[loopend]);
					if (isIf) {
						return true;
					}
				}

			}

		}
		return false;
	}

	private boolean skipIterationIf_IFEndOfloop(int i, byte[] info) {
		ArrayList loops = behaviour.getBehaviourLoops();
		ArrayList starts = behaviour.getInstructionStartPositions();
		for (Iterator iter = loops.iterator(); iter.hasNext();) {
			Loop element = (Loop) iter.next();
			int loopEnd = element.getEndIndex();
			if (loopEnd == i) {
				boolean isStart = isThisInstrStart(starts, loopEnd);
				if (isStart) {
					boolean isif = isInstructionIF(info[loopEnd]);
					if (isif) {
						int loopstart = element.getStartIndex();
						int ifjump = getJumpAddress(info, i);
						if (ifjump == loopstart && (ifjump < i)) {
							return false;
							// do while loop
						}
						return true;
					}
				}

			}

		}

		return false;

	}

	private boolean checkToEvaluateAnyLoopBody(int i, byte[] info) {
		boolean eval = true;
		ArrayList loops = behaviour.getBehaviourLoops();
		ArrayList starts = behaviour.getInstructionStartPositions();
		for (Iterator iter = loops.iterator(); iter.hasNext();) {
			Loop element = (Loop) iter.next();
			int loopEnd = element.getEndIndex();
			if (loopEnd == i) {
				boolean isStart = isThisInstrStart(starts, loopEnd);
				if (isStart) {
					boolean isif = isInstructionIF(info[loopEnd]);
					if (isif) {
						int loopstart = element.getStartIndex();
						int ifjump = getJumpAddress(info, i);
						if (ifjump == loopstart && (ifjump < i)) {
							eval = false;
							return eval;
							// do while loop
						}
						eval = true;
						return eval;
					}
				}

			}

		}

		return eval;

	}

	private HashMap ifsbeginends_dowhile = new HashMap();

	private boolean checkForDoWhile(int i, byte[] info, Loop lp) {
		ArrayList loops = behaviour.getBehaviourLoops();
		ArrayList starts = behaviour.getInstructionStartPositions();
		for (Iterator iter = loops.iterator(); iter.hasNext();) {
			Loop element = (Loop) iter.next();
			int loopEnd = element.getEndIndex();
			if (loopEnd == i
					&& (element.getLoopEndForBracket() == -1 || element
							.getLoopEndForBracket() == i)) {
				boolean isStart = isThisInstrStart(starts, loopEnd);
				if (isStart) {
					boolean isif = isInstructionIF(info[loopEnd]);
					if (isif) {
						int loopstart = element.getStartIndex();
						int ifjump = getJumpAddress(info, i);
						StringBuffer le = new StringBuffer();
						boolean invalid = isIfConditionForSomeOtherLoop(i,
								info, loops, le);
						if (ifjump == loopstart && (ifjump < i)) {
							if (!invalid) {
								boolean loopreset = element.getLoopendreset();
								if (!loopreset) {
									DowhileHelper dow = new DowhileHelper(
											i + 3, element);
									boolean present = false;
									for (int p = 0; p < doWhileHelperList
											.size(); p++) {
										DowhileHelper helper = (DowhileHelper) doWhileHelperList
												.get(p);
										if (helper.getLoop() == element) {
											present = true;
											break;
										}
									}
									if (!present)
										doWhileHelperList.add(dow);
								}
								if (loopreset) {
									int lend = element.getLoopendDueToReset();
									ifsbeginends_dowhile.put("" + loopEnd, ""
											+ lend);
								}
								return true;
							} else {
								try {
									lp.setEndIndex(Integer.parseInt(le
											.toString()));
								} catch (NumberFormatException ne) {
									return false;
								}
								return true;
							}
							// do while loop
						}
						return false;
					}
				}

			}

		}

		return false;

	}

	private boolean isIfConditionForSomeOtherLoop(int i, byte[] info,
			ArrayList loops, StringBuffer lend) {
		ArrayList starts = behaviour.getInstructionStartPositions();
		Object[] sortedLoops = sortLoops(loops);
		int parentLoopStart = getParentLoopStartForIf(sortedLoops, i);
		for (int z = 0; z < loops.size(); z++) {
			Loop l = (Loop) loops.get(z);
			int end = l.getEndIndex();
			boolean start = isThisInstrStart(starts, end);
			if (start) {

				int ls = l.getStartIndex();
				if (info[end] == JvmOpCodes.GOTO && ls < end && ls < i
						&& i < end && (parentLoopStart == ls)) {
					for (int k = ls; k < end; k++) {
						start = isThisInstrStart(starts, k);
						if (start) {
							boolean isif = isInstructionIF(info[k]);
							if (isif) {
								if (k == i) {
									lend.append("" + end);
									return true;
								} else {
									return false;
								}
							}
						}
					}
				}
			}

		}

		return false;

	}

	private java.lang.String closeDoWhile(int index) {
		return LoopHelper.closeDoWhile(index, doWhileHelperList);
	}

	private ArrayList doWhileHelperList = new ArrayList();

	public class DowhileHelper {
		private boolean closed = false;

		private int index = -1;

		private Loop loop;

		public DowhileHelper(int in, Loop loop) {
			index = in;
			this.loop = loop;

		}

		public Loop getLoop() {
			return loop;
		}

		public void setClosed(boolean isClosed) {
			closed = isClosed;
		}

		public boolean getIsClosed() {
			return closed;
		}

		public int getIndex() {
			return index;
		}
		public void setIndex(int index){
			this.index = index;
		}
		
	}

	/***************************************************************************
	 * Formerly handleIFStmtAtLoopEnd
	 * 
	 * @param current
	 * @param info
	 * @return
	 */
	private boolean markLoopAsDoWhileType(int current, byte[] info) {
		boolean gotoinst = info[current] == JvmOpCodes.GOTO;
		ArrayList starts = behaviour.getInstructionStartPositions();
		boolean isStart = isThisInstrStart(starts, current);
		if (gotoinst && isStart) {
			int gotojump = getJumpAddress(info, current);
			if (gotojump < current)
				return false;
			int nextInstPos = current + 3;
			boolean someLoopStart = isInstLoopStart(nextInstPos);
			if (!someLoopStart)
				return false;
			for (int k = gotojump; k < info.length; k++) {
				boolean b2 = isThisInstrStart(starts, k);
				if (b2) {
					boolean isif = isInstructionIF(info[k]);
					if (isif) {
						int offset = getJumpAddress(info, k);
						if (offset == nextInstPos) {

							EndOfLoopUtility utilityClass = new EndOfLoopUtility(
									current, nextInstPos, k, gotojump, k);
							endofLooplist.add(utilityClass);
							return true;
						} else if (offset > k) {
							int ifatend = doesLoopEndAtIf(info, gotojump,
									nextInstPos);
							if (ifatend != -1) {
								EndOfLoopUtility utilityClass = new EndOfLoopUtility(
										current, nextInstPos, ifatend,
										gotojump, ifatend);
								endofLooplist.add(utilityClass);
								return true;
							} else
								return false;
						} else {
							return false;
						}

					}
				}
			}
			return false;

		} else
			return false;

	}

	private ArrayList endofLooplist = new ArrayList();

	private boolean isInstLoopStart(int pos) {
		ArrayList loops = behaviour.getBehaviourLoops();
		for (int z = 0; z < loops.size(); z++) {
			Loop loop = (Loop) loops.get(z);
			int loopstart = loop.getStartIndex();
			if (loopstart == pos) {
				return true;
			}
		}
		return false;

	}

	class EndOfLoopUtility {
		private int gotostart = -1;

		private int continuepos = -1;

		private int ifpos = -1;

		private int rangeStart = -1;

		private int rangeEnd = -1;

		private boolean rangeVisited = false;

		EndOfLoopUtility(int gotos, int contpos, int IFPOS, int rstart,
				int estart) {
			gotostart = gotos;
			continuepos = contpos;
			ifpos = IFPOS;
			rangeStart = rstart;
			rangeEnd = estart;
		}

		public int getContinuepos() {
			return continuepos;
		}

		public void setContinuepos(int continuepos) {
			this.continuepos = continuepos;
		}

		public int getGotostart() {
			return gotostart;
		}

		public void setGotostart(int gotostart) {
			this.gotostart = gotostart;
		}

		public int getIfpos() {
			return ifpos;
		}

		public void setIfpos(int ifpos) {
			this.ifpos = ifpos;
		}

		public int getRangeEnd() {
			return rangeEnd;
		}

		public void setRangeEnd(int rangeEnd) {
			this.rangeEnd = rangeEnd;
		}

		public int getRangeStart() {
			return rangeStart;
		}

		public void setRangeStart(int rangeStart) {
			this.rangeStart = rangeStart;
		}

		public boolean wasRangeVisited() {
			return rangeVisited;
		}

		public void setRangeVisited(boolean rangeVisited) {
			this.rangeVisited = rangeVisited;
		}

	}

	private boolean checkForForLoopIndexReset(int i, StringBuffer resetPos,
			byte[] info) {
		if (endofLooplist.size() == 0)
			return false;
		for (int z = 0; z < endofLooplist.size(); z++) {
			EndOfLoopUtility utility = (EndOfLoopUtility) endofLooplist.get(z);
			int ifpos = utility.getIfpos();
			if (ifpos == i) {
				boolean isif = isInstructionIF(info[i]);
				if (isif) {
					int resume = utility.getContinuepos();
					if (resume != -1) {
						if (info[i] == JvmOpCodes.IFEQ
								|| info[i] == JvmOpCodes.IFNE) {
							java.lang.String val = getReturnTypeIfPreviousInvoke(
									i, info);
							Operand top = opStack.peekTopOfStack();
							if (top != null) {
								if (val != null) {
									if (val.indexOf("false") != -1) {
										val = "boolean";
									}
								}
								top.setClassType(val);
							}
						}
						resetPos.append(resume);
						utility.setRangeVisited(true);

						return true;
					}
				}
			}

		}
		return false;

	}

	private boolean skipIterationWRTIFSAtEndOFLoop(int i, byte[] info) {
		boolean skip = false;
		if (endofLooplist.size() == 0)
			return false;
		ArrayList starts = behaviour.getInstructionStartPositions();
		for (int z = 0; z < endofLooplist.size(); z++) {
			EndOfLoopUtility utility = (EndOfLoopUtility) endofLooplist.get(z);
			int beginRange = utility.getRangeStart();
			int endRange = utility.getRangeEnd();
			if (i >= beginRange && i < endRange && utility.wasRangeVisited()) {

				// Need to check here for iinc before a iload
				boolean isS = isThisInstrStart(starts, i);
				if (isS) {
					int index = -1;
					switch (info[i]) {
					case JvmOpCodes.ILOAD:
						index = info[(i + 1)];
						break;
					case JvmOpCodes.ILOAD_0:
						index = 0;
						break;
					case JvmOpCodes.ILOAD_1:
						index = 1;
						break;
					case JvmOpCodes.ILOAD_2:
						index = 2;
						break;
					case JvmOpCodes.ILOAD_3:
						index = 3;
						break;

					}
					if (index != -1) {
						int prev = i - 3;
						isS = isThisInstrStart(starts, prev);
						if (isS) {
							if (info[prev] == JvmOpCodes.IINC) {
								int varindex = info[(prev + 1)];
								java.lang.String constantStr = "";
								int constant = info[(prev + 2)];
								java.lang.String varName = "";
								if (index == varindex) {
									LocalVariable loc = getLocalVariable(
											varindex, "load", "int", true,
											currentForIndex);
									if (loc != null) {
										java.lang.String name = loc
												.getVarName();
										java.lang.String temp = name + "="
												+ name + "+" + (constant);
										behaviour.appendToBuffer(Util
												.formatDecompiledStatement("\n"
														+ temp + ";\n"));
									}

								}

							}
						}

					}
				}

				StringBuffer ifelsecode = new StringBuffer("");
				StringBuffer reset = new StringBuffer("");
				checkForIFElseEndStatement(info, behaviour.getMethodIfs(), i,
						reset, opStack, ifelsecode, "if");

				behaviour.appendToBuffer(ifelsecode.toString());
				if (ifelsecode.toString().trim().length() > 0) {
					// checkToAddElseAtIFEnd(currentForIndex);
				}
				return true;
			}
			if (i == endRange && utility.wasRangeVisited()) {
				/***************************************************************
				 * End Loop Here
				 */
				Iterator loopIterator = behaviour.getBehaviourLoops()
						.iterator();
				while (loopIterator.hasNext()) {
					Loop iloop = (Loop) loopIterator.next();

					if (iloop.getEndIndex() == i) {
						if (iloop.wasLoopClosedInCode() == false) {
							tempString = "}\n";
							behaviour.appendToBuffer(Util
									.formatDecompiledStatement(tempString));
							iloop.setWasLoopClosedInCode(true);
						}

					} else if (iloop.getLoopEndForBracket() == i
							&& iloop.wasLoopClosedInCode() == false) {

						tempString = "}\n";
						behaviour.appendToBuffer(Util
								.formatDecompiledStatement(tempString));
						iloop.setWasLoopClosedInCode(true);
					}

				}

				StringBuffer ifelsecode = new StringBuffer("");
				StringBuffer reset = new StringBuffer("");
				checkForIFElseEndStatement(info, behaviour.getMethodIfs(), i,
						reset, opStack, ifelsecode, "if");
				behaviour.appendToBuffer(ifelsecode.toString());
				if (ifelsecode.toString().trim().length() > 0) {
					// checkToAddElseAtIFEnd(currentForIndex);
				}
				return true;
			}
		}
		return skip;
	}

	private int doesLoopEndAtIf(byte[] info, int from, int lookfor) {
		int i = -1;
		ArrayList starts = behaviour.getInstructionStartPositions();
		for (int z = from; z < info.length; z++) {
			boolean start = isThisInstrStart(starts, z);
			if (start) {
				boolean b = isInstructionIF(info[z]);
				if (b) {
					int ifo = getJumpAddress(info, z);
					// Check 1
					if (ifo == lookfor) {
						return z;
					}

					if (ifo > z) {
						int x = ifo - 3;
						boolean s = isThisInstrStart(starts, x);
						if (s == false) {
							return i;
						} else {
							boolean isif = isInstructionIF(info[x]);
							if (isif) {
								int offset = getJumpAddress(info, x);
								if (offset == lookfor) {
									return x;
								}
								if (offset > x) {
									int j = doesLoopEndAtIf(info, ifo, lookfor);
									return j;
								} else {
									return i;
								}

							} else {
								return i;
							}

						}

					}

				}

			}

		}

		return i;
	}

	private boolean isItoktoendifelse(int i) {
		boolean ok = true;
		if (endofLooplist.size() == 0)
			return ok;
		for (int z = 0; z < endofLooplist.size(); z++) {
			EndOfLoopUtility utility = (EndOfLoopUtility) endofLooplist.get(z);
			int beginRange = utility.getRangeStart();
			int endRange = utility.getRangeEnd();
			if (i >= beginRange && i <= endRange && utility.wasRangeVisited()) {
				return true;
			}
			if (i >= beginRange && i <= endRange && !utility.wasRangeVisited()) {
				return false;
			}

		}

		return ok;
	}

	private ArrayList checkForskipWithinSkip(int start, int end, byte[] info) {
		ArrayList list = new ArrayList();
		ArrayList starts = behaviour.getInstructionStartPositions();
		outer: for (int z = start; z < end; z++) {
			boolean b = isThisInstrStart(starts, z);
			if (b) {
				if (info[z] == JvmOpCodes.JSR) {
					int jsroffset = getJumpAddress(info, z);
					if (jsroffset < end) {
						int jvmCode = info[jsroffset];
						int next = -1;
						if (jvmCode == JvmOpCodes.ASTORE) {
							next = jsroffset + 2;
						} else if (jvmCode == JvmOpCodes.ASTORE_0) {
							next = jsroffset + 1;
						}

						else if (jvmCode == JvmOpCodes.ASTORE_1) {
							next = jsroffset + 1;
						}

						else if (jvmCode == JvmOpCodes.ASTORE_2) {
							next = jsroffset + 1;
						}

						else if (jvmCode == JvmOpCodes.ASTORE_3) {
							next = jsroffset + 1;
						}
						if (next != -1) {
							for (int k1 = next; k1 < end; k1++) {
								b = isThisInstrStart(starts, k1);
								if (b) {
									if (info[k1] == JvmOpCodes.GOTO
											|| info[k1] == JvmOpCodes.RET) {
										break outer;
									}
									list.add(new Integer(k1));
								}

							}

						}

					}

				}

			}

		}

		return list;
	}

	private boolean skipGetStaticCall(byte[] info) {
		int n = currentForIndex + 3;
		if (n >= info.length)
			return false;
		int l = info.length;
		ArrayList starts = behaviour.getInstructionStartPositions();
		if (info[n] == JvmOpCodes.DUP || info[n] == JvmOpCodes.DUP2) {
			n = n + 1;
			boolean b = isThisInstrStart(starts, n);
			if (b) {

				boolean isif = info[n] == JvmOpCodes.IFNONNULL;
				if (isif) {
					int j = getJumpAddress(info, n);
					if (j < n)
						return false;
					boolean ok = isThisInstrStart(starts, j);
					if (ok) {
						ok = info[j] == JvmOpCodes.AASTORE;
						if (ok) {
							for (int x = n + 3; x < l; x++) {
								boolean b2 = isThisInstrStart(starts, x);
								if (b2) {
									if (info[x] == JvmOpCodes.LDC) {

										int offset = info[x + 1];
										if (offset < 0)
											offset += 256;
										CPString constString = cd
												.getStringsAtCPoolPosition(offset);
										if (constString == null) {
											return false;
										}
										java.lang.String stringLiteral = cd
												.getUTF8String(constString
														.getUtf8pointer());
										if (stringLiteral == null) {
											return false;
										}
										StringBuffer sb = new StringBuffer("");
										Util.checkForImport(stringLiteral, sb);
										java.lang.String classToLoad = sb
												.toString()
												+ ".class";
										Operand op = new Operand();
										op.setOperandValue(classToLoad);
										opStack.push(op);
										op.setClassType("Class");
										GlobalVariableStore
												.getSkipWRTClassLoadIf()
												.put(
														new Integer(
																currentForIndex + 3),
														new Integer(j));
										GlobalVariableStore
												.getSkipWRTClassLoadIfUpperLimits()
												.add(new Integer(j));
										return true;

									}
									if (info[x] == JvmOpCodes.LDC_W) {
										int offset = getOffset(info, x);
										CPString constString = cd
												.getStringsAtCPoolPosition(offset);
										if (constString == null) {
											return false;
										}
										java.lang.String stringLiteral = cd
												.getUTF8String(constString
														.getUtf8pointer());
										if (stringLiteral == null) {
											return false;
										}

										Operand op = new Operand();
										StringBuffer sb = new StringBuffer("");
										Util.checkForImport(stringLiteral, sb);
										java.lang.String classToLoad = sb
												.toString()
												+ ".class";
										op.setOperandValue(classToLoad);
										opStack.push(op);
										op.setClassType("Class");
										GlobalVariableStore
												.getSkipWRTClassLoadIf()
												.put(
														new Integer(
																currentForIndex + 3),
														new Integer(j));
										GlobalVariableStore
												.getSkipWRTClassLoadIfUpperLimits()
												.add(new Integer(j));
										return true;
									}

								}

							}

						}
					}
				}

			}
		}
		return false;

	}

	private boolean skipExceptionTables(int i) {
		if (GlobalVariableStore.getSkipWRTClassLoadIfUpperLimits().contains(
				new Integer(i))) {
			return true;
		} else
			return false;
	}

	// j-->monitorexit pos in code
	private boolean confirmSynchronizedSkip(byte[] info, int j) {
		boolean ok = false;
		j = j - 1;
		ArrayList starts = behaviour.getInstructionStartPositions();
		for (; j >= 0; j--) {
			if (isThisInstrStart(starts, j)
					&& info[j] == JvmOpCodes.MONITORENTER) {
				break;
			}
			if (isThisInstrStart(starts, j)
					&& info[j] == JvmOpCodes.MONITOREXIT) {
				ok = true;
				break;
			}
		}
		return ok;

	}

	private ExceptionTable getSynchTableGivenEnd(ArrayList synchlist, int endpc) {

		ExceptionTable table = null;
		for (Iterator iter = synchlist.iterator(); iter.hasNext();) {
			ExceptionTable element = (ExceptionTable) iter.next();
			if (element.getEndPC() == endpc) {
				table = element;
				break;
			}
		}
		return table;

	}

	private void checkForAthrowAtSynchEnd(int end) {
		boolean athrow = false;
		ArrayList starts = behaviour.getInstructionStartPositions();
		byte[] code = behaviour.getCode();
		boolean isstart = isThisInstrStart(starts, end);
		OperandStack opStack = behaviour.getOpStack();
		if (isstart) {
			if (code[end] == JvmOpCodes.ATHROW) {

				boolean x = addATHROWOutput(end);
				if (opStack.size() > 0 && x) {
					Operand op = (Operand) opStack.pop();
					opStack.push(op);
					java.lang.String tempString = "throw "
							+ op.getOperandValue() + ";\n";
					behaviour
							.appendToBuffer((Util
									.formatDecompiledStatement("\n"
											+ tempString + "\n")));
					GlobalVariableStore.getAthrowmap().put(new Integer(end),
							"true");
				}
			}
		}

	}

	private boolean isLocalVariableWithNameAlreadyPresent(
			java.lang.String name, ArrayList list) {
		boolean b = false;
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			LocalVariable element = (LocalVariable) iter.next();
			if (element.getVarName().equals(name)) {
				b = true;
				break;
			}
		}
		return b;
	}

	private boolean caseObjectAddedToSortedArray(Case current, ArrayList sorted) {
		boolean b = false;
		for (Iterator iter = sorted.iterator(); iter.hasNext();) {
			Case element = (Case) iter.next();
			if (element == current) {
				b = true;
				break;
			}

		}
		return b;
	}

	/**
	 * This method checks whether more than one case starts at the same code
	 * index.
	 * 
	 * @param allcases
	 * @param start
	 * @return
	 */

	private boolean checkForMultipleCases(Switch switchblk, ArrayList allcases,
			int start, StringBuffer sb) {
		int count = 0;
		for (int z = 0; z < allcases.size(); z++) {
			Case c = (Case) allcases.get(z);
			if (c.getCaseStart() == start) {
				count++;
			}
		}
		if (start == switchblk.getDefaultStart())
			count++;
		sb.append(count);
		if (count > 1) {
			return true;
		} else
			return false;
	}

	private java.lang.String getMultipleCaseStartAsString(Switch switchblk,
			ArrayList allcases, int start, StringBuffer sb) {
		int total = Integer.parseInt(sb.toString());
		java.lang.String s = "";
		int count = 0;
		Iterator iter;
		boolean brackedOpened = false;
		boolean added = false;
		for (iter = allcases.iterator(); iter.hasNext();) {
			Case element = (Case) iter.next();
			if (element.getCaseStart() == start) {
				if (element.isUsedinmultipleCaseCase())
					return "";
				count++;
				if (count == total && switchblk.getDefaultStart() != start) {
					if (!element.isWasCaseStartedPrinted()) {
						s += "case " + element.getCaseLabel() + ":\n{\n";
						element.setWasCaseStartedPrinted(true);
						brackedOpened = true;
					}
					casefound = true;
					added = true;
				} else {
					if (!element.isWasCaseStartedPrinted()) {
						s += "case " + element.getCaseLabel() + ": \n";
						element.setWasCaseStartedPrinted(true);
						element.setDoNotClose(true);
					}
					casefound = true;
					added = true;
				}
				element.setUsedinmultipleCaseCase(true);

			}
		}

		if (added) {
			if (brackedOpened) {
				if (switchblk.getDefaultStart() == start
						&& !switchblk.isDefaultStarted()) {
					s = "default:\n" + s;
					switchblk.setDefaultStarted(true);
					switchblk.setDoNotCloseDefault(true);
				}
			} else {
				if (switchblk.getDefaultStart() == start
						&& !switchblk.isDefaultStarted()) {
					s = s + "default:\n{\n";
					switchblk.setDefaultStarted(true);
				}
			}
		}
		return s;
	}

	private void addAnyAThorwAtDefaultEnd(int i, OperandStack opStack,
			byte[] info, StringBuffer S) {

		ArrayList starts = behaviour.getInstructionStartPositions();
		boolean b = isThisInstrStart(starts, i);
		if (b) {
			if (info[i] == JvmOpCodes.ATHROW) {
				if (opStack.size() > 0) {
					Operand op = opStack.getTopOfStack();
					java.lang.String str = op.getOperandValue();
					S.append("throw " + str + ";\n");
					GlobalVariableStore.getAthrowmap().put(new Integer(i),
							"true");
				}

			}
		}

	}

	private void addAnyAThorwAtDefaultEnd(int i, OperandStack opStack,
			byte[] info) {

		ArrayList starts = behaviour.getInstructionStartPositions();
		boolean b = isThisInstrStart(starts, i);
		if (b) {
			if (info[i] == JvmOpCodes.ATHROW) {
				if (opStack.size() > 0) {
					Operand op = opStack.getTopOfStack();
					java.lang.String str = op.getOperandValue();
					behaviour.appendToBuffer(Util
							.formatDecompiledStatement("throw " + str + ";\n"));
					GlobalVariableStore.getAthrowmap().put(new Integer(i),
							"true");
				}

			}
		}

	}

	private boolean OKToAddReturn(int i) {
		boolean oktoadd = true;
		Iterator mapIT = GlobalVariableStore.getReturnsAtI().entrySet()
				.iterator();
		while (mapIT.hasNext()) {
			Map.Entry entry = (Map.Entry) mapIT.next();
			Object key = entry.getKey();
			Object retStatus = entry.getValue().toString();
			if (key instanceof Integer) {
				Integer pos = (Integer) key;
				int temp = pos.intValue();
				if (temp == i) {
					if (retStatus.equals("true")) {

						oktoadd = false;
						break;
					}
				}
			}

		}
		return oktoadd;
	}

	private int handleDUPX2() {
		return execute(InstrConstants.UNCLASSIFIED_INSTR_TYPE, null,
				JvmOpCodes.DUP_X2);

	}

	private int handleDUP2X1(OperandStack opStack) {
		return execute(InstrConstants.UNCLASSIFIED_INSTR_TYPE, null,
				JvmOpCodes.DUP2_X1);
	}

	private int handleDUP2(OperandStack opStack) {
		// boolean
		// dupStoreForTerIf=isThisDUPSTOREAtEndOFTernaryIF(currentForIndex,info,"dup");
		return execute(InstrConstants.UNCLASSIFIED_INSTR_TYPE, null,
				JvmOpCodes.DUP2);
	}

	// Change
	private int handleDUP(OperandStack opStack) {
		// boolean
		// dupStoreForTerIf=isThisDUPSTOREAtEndOFTernaryIF(currentForIndex,info,"dup");
		return execute(InstrConstants.UNCLASSIFIED_INSTR_TYPE, null,
				JvmOpCodes.DUP);
	}

	private int handleDUPX1(OperandStack opStack) {
		return execute(InstrConstants.UNCLASSIFIED_INSTR_TYPE, null,
				JvmOpCodes.DUP_X1);
	}

	private boolean checkForPostIncrSkip(int arr[], int i) {
		if (arr == null)
			return false;
		for (int j = 0; j < arr.length; j++) {

			int temp = arr[j];
			if (temp == i)
				return true;
		}
		return false;

	}

	private int handleComplexLLOAD(OperandStack opStack, byte[] info,
			int current) {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.LONG,
				JvmOpCodes.LLOAD);
	}

	private boolean curBrackedClosed = false; // Used in association with

	// checkClosingBracketRuleForShortCutOp

	private HashMap openbracketmap = new HashMap();

	private boolean checkClosingBracketRuleForShortCutOp(int current,
			byte[] info, int bytecodeend, int startofif, StringBuffer matchingif) {
		boolean close = false; // todo: refactor to true
		ArrayList starts = behaviour.getInstructionStartPositions();
		int be = bytecodeend;
		int j = be - 3;
		boolean isstart = isThisInstrStart(starts, j);
		if (isstart) {
			boolean isif = isInstructionIF(info[j]);
			if (isif) {
				StringBuffer pos = new StringBuffer();
				if ((startofif + 3) > j) {
					// matchingif.append(""+startofif);
					return !close;
				}
				boolean ifinrange = checkForIfInRange(info, (startofif + 3), j,
						pos, starts);
				if (ifinrange) {
					try {
						int ifpos = Integer.parseInt(pos.toString());
						int jump = getJumpAddress(info, ifpos);
						if (jump == be) {
							matchingif.append("" + ifpos);
							return close;
						} else {
							// if(curBrackedClosed==false)
							// {
							// curBrackedClosed=true;
							return !close;
							// }
						}

					} catch (Exception ne) {
						return close;
					}

				} else {
					if (isThisInstrStart(starts, j) && isInstructionIF(info[j])) {
						try {
							// 1int ifpos=Integer.parseInt(pos.toString());
							matchingif.append("" + j);
						} catch (Exception n) {
						}
					}
					return close;
				}

			} else {
				StringBuffer pos = new StringBuffer();
				boolean ifinrange = checkForIfInRange(info, (startofif + 3), j,
						pos, starts);
				if (ifinrange) {
					try {
						int ifpos = Integer.parseInt(pos.toString());
						int jump = getJumpAddress(info, ifpos);
						if (jump == be) {
							int p = startofif + 3;
							for (int y = startofif - 1; y >= 0; y--) {
								boolean st1 = isThisInstrStart(starts, y);
								if (st1) {
									boolean if1 = isInstructionIF(info[y]);
									if (if1) {
										int jmp1 = getJumpAddress(info, y);
										if (jmp1 == p) {
											return !close;
										}

									}
								}
							}
							matchingif.append("" + ifpos);
							return close;
						} else {
							// if(curBrackedClosed==false)
							// {
							// curBrackedClosed=true;
							return !close;
							// }
						}

					} catch (Exception ne) {
						return close;
					}
				} else {
					return !close;
				}

			}

		}

		return close;
	}

	/**
	 * 
	 * @param info
	 * @param from
	 *            --> inclusive
	 * @param end
	 *            --> exclusive
	 * @param pos
	 *            --> if pos if any
	 * @return
	 */

	private boolean checkForIfInRange(byte[] info, int from, int end,
			StringBuffer pos, ArrayList starts) {

		for (int z = from; z < end && z < info.length; z++) {
			boolean start = isThisInstrStart(starts, z);
			if (start) {
				boolean isif = isInstructionIF(info[z]);
				if (isif) {
					pos.append("" + z);
					return true;
				}

			}

		}
		return false;
	}

	private boolean checkForAnyIFWithDifferentIndex(byte[] info, int from,
			int end, StringBuffer pos, ArrayList starts) {

		ArrayList temp = new ArrayList();
		for (int z = from; z < end && z < info.length; z++) {
			boolean start = isThisInstrStart(starts, z);
			if (start) {
				boolean isif = isInstructionIF(info[z]);
				if (isif) {
					temp.add("" + z);
					// return true;
				}

			}

		}
		int cj = getJumpAddress(info, currentForIndex);
		for (int k = 0; k < temp.size(); k++) {

			int in = Integer.parseInt((java.lang.String) temp.get(k));
			int j = getJumpAddress(info, in);
			if (j != cj) {
				pos.append("" + (java.lang.String) temp.get(k));
				return true;
			}
		}
		if (temp.size() > 0) {
			pos.append("" + (java.lang.String) temp.get(0));
			return true;
		}
		return false;
	}

	private boolean confirmOpenBracket(int mifpos, byte[] info, int be,
			int ifstart, IFBlock anIf) {
		boolean ok = true;
		int x = ifstart + 3;
		ArrayList starts = behaviour.getInstructionStartPositions();
		StringBuffer matchingIf = new StringBuffer();
		int mpos = -1;

		outerWhile: while (x < mifpos) {
			boolean instSt = isThisInstrStart(starts, x);
			boolean isif = isInstructionIF(info[x]);
			if (instSt && isif) {
				boolean b = checkClosingBracketRuleForShortCutOp(
						currentForIndex, info, anIf.getIfCloseFromByteCode(),
						anIf.getIfStart(), matchingIf);
				if (b == false) {
					while (true) {
						try {
							mpos = Integer.parseInt(matchingIf.toString());
							int jump = getJumpAddress(info, mpos);
							if (jump == be) {
								ok = false;
								break outerWhile;

							}
							matchingIf = new StringBuffer();
							boolean b1 = checkClosingBracketRuleForShortCutOp(
									currentForIndex, info, getJumpAddress(info,
											mpos), mpos, matchingIf);
							// if(b1==false)

						} catch (NumberFormatException exp) {
							ok = true;
							break outerWhile;
						}
					}
				} else {
					ok = true;
					break outerWhile;
				}
			} else {
				x++;
			}

		}

		return ok;
	}

	private HashMap openCloseBracketMap = new HashMap();

	private boolean addOpenBracket(byte[] info, IFBlock ifst) {
		ArrayList starts = behaviour.getInstructionStartPositions();
		int prev = ifst.getIfCloseFromByteCode() - 3;
		boolean isStart = isThisInstrStart(starts, prev);
		if (isStart == false)
			return false;
		StringBuffer mif = new StringBuffer();
		boolean close = checkClosingBracketRuleForShortCutOp(currentForIndex,
				info, ifst.getIfCloseFromByteCode(), ifst.getIfStart(), mif);
		boolean open = !close;
		boolean oktoopen = false;
		if (open) {
			Object ob = openbracketmap.get("" + ifst.getIfStart());
			if (ob == null) {
				// confirm before adding
				try {
					int imif = Integer.parseInt(mif.toString());
					boolean z = confirmOpenBracket(imif, info, ifst
							.getIfCloseFromByteCode(), ifst.getIfStart(), ifst);
					if (z) {
						openbracketmap.put("" + ifst.getIfStart(), "true");
						int e = getBracketClose(ifst.getIfCloseFromByteCode(),
								info, ifst.getIfStart(), imif);
						openCloseBracketMap.put("" + e, ""
								+ ifst.getIfCloseFromByteCode());
						oktoopen = true;
					}
				} catch (Exception ne) {
					// openbracketmap.put(""+ifst.getIfCloseFromByteCode(),"true");
					// int
					// e=getBracketClose(ifst.getIfCloseFromByteCode(),info,ifst.getIfStart());

					// openCloseBracketMap.put(""+e,""+ifst.getIfCloseFromByteCode());
					oktoopen = false;
				}

			} else {
				// nothing to do
			}

		}
		return oktoopen;
	}

	// when close value is known
	private boolean addOpenBracket(byte[] info, IFBlock ifst, boolean close,
			StringBuffer mif) {
		ArrayList starts = behaviour.getInstructionStartPositions();
		int prev = ifst.getIfCloseFromByteCode() - 3;
		boolean isStart = isThisInstrStart(starts, prev);
		if (isStart == false)
			return false;

		boolean open = !close;
		boolean oktoopen = false;
		if (open) {
			Object ob = openbracketmap.get("" + ifst.getIfStart());
			if (ob == null) {
				// confirm before adding
				try {
					int imif = Integer.parseInt(mif.toString());
					boolean z = confirmOpenBracket(imif, info, ifst
							.getIfCloseFromByteCode(), ifst.getIfStart(), ifst);
					if (z) {
						openbracketmap.put("" + ifst.getIfStart(), "true");
						int e = getBracketClose(ifst.getIfCloseFromByteCode(),
								info, ifst.getIfStart(), imif);
						openCloseBracketMap.put("" + e, ""
								+ ifst.getIfCloseFromByteCode());
						oktoopen = true;
					}
				} catch (Exception ne) {
					// openbracketmap.put(""+ifst.getIfCloseFromByteCode(),"true");
					// int
					// e=getBracketClose(ifst.getIfCloseFromByteCode(),info,ifst.getIfStart());
					// openCloseBracketMap.put(""+e,""+ifst.getIfCloseFromByteCode());
					oktoopen = false;
				}

			} else {
				// nothing to do
			}

		}
		return oktoopen;
	}

	private int getBracketClose(int be, byte[] info, int ifs, int imif)
			throws Exception {
		int x = be - 3;
		ArrayList starts = behaviour.getInstructionStartPositions();
		boolean st = isThisInstrStart(starts, x);
		boolean brClose = false;
		int initalStart = imif;
		if (st) {
			do {
				StringBuffer match = new StringBuffer();
				int bye = getJumpAddress(info, imif);
				brClose = checkClosingBracketRuleForShortCutOp(imif, info, bye,
						imif, match);
				if (brClose) {
					int jump = bye;
					return jump;
				} else {
					try {
						imif = Integer.parseInt(match.toString());
					} catch (Throwable t) {
						throw new Exception();
					}
				}

				// to prevent any infinite loop
				if (imif <= initalStart) {
					throw new Exception();
				}
			} while (brClose == false && imif < be);

			/*
			 * boolean isif=isInstructionIF(info[x]); if(isif) { int
			 * jump=getJumpAddress(info,x); return jump; } else { x--; for(;x
			 * >ifs ;x--) { st=isThisInstrStart(starts,x); if(st) {
			 * isif=isInstructionIF(info[x]); if(isif) { int
			 * jump=getJumpAddress(info,x); return jump; } } } }
			 */
		}

		throw new Exception();
		// return be; // because we are adding bracket in calling method
		// something wrong here.
	}

	private void handleISHL(OperandStack opStack) {
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
				JvmOpCodes.ISHL);
	}

	private void handleISHR(OperandStack opStack) {
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.INT,
				JvmOpCodes.ISHR);
	}

	private Loop getLoopGivenEnd(int end, ArrayList loops) {
		for (int z = 0; z < loops.size(); z++) {
			Loop l = (Loop) loops.get(z);
			if (l.getEndIndex() == end)
				return l;
		}
		return null;
	}

	private void handlePUTSTATIC(byte[] info, int i, OperandStack opStack) {
		execute(InstrConstants.UNCLASSIFIED_INSTR_TYPE, null,
				JvmOpCodes.PUTSTATIC);
	}

	private void handlePUTFIELD(byte[] info, int i, OperandStack opStack) {
		execute(InstrConstants.UNCLASSIFIED_INSTR_TYPE, null,
				JvmOpCodes.PUTFIELD);
	}

	private void handleLXOR(byte[] info, OperandStack opStack) {
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.LONG,
				JvmOpCodes.LXOR);
	}

	private void handleLUSHR(byte[] info, OperandStack opStack) {
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.LONG,
				JvmOpCodes.LUSHR);
	}

	private void handleLSUB(byte[] info, OperandStack opStack) {
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.LONG,
				JvmOpCodes.LSUB);

	}

	private void handleLSHR(byte[] info, OperandStack opStack) {
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.LONG,
				JvmOpCodes.LSHR);
	}

	private int handleNEW(byte[] info, OperandStack opStack, int i) {
		return execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.OBJECT,
				JvmOpCodes.NEW);
	}

	private void checkForChangeInBranchLabels(IFBlock ifst) {

		Iterator it = GlobalVariableStore.getBranchLabels().entrySet()
				.iterator();
		DecompilerHelper.BranchLabel reqd = null;
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry) it.next();
			int end = ((Integer) (e.getValue())).intValue();
			DecompilerHelper.BranchLabel b = (DecompilerHelper.BranchLabel) e
					.getKey();
			IFBlock IF = b.getIF();
			if (IF == ifst) {
				reqd = b;
				break;
			}

		}
		if (reqd != null) {
			GlobalVariableStore.getBranchLabels().remove(reqd);
			GlobalVariableStore.getBranchLabels().put(reqd,
					new Integer(ifst.getIfCloseLineNumber()));
		}
	}

	private BranchInstrFinder getBranchFinder() {
		return (BranchInstrFinder) FinderFactory.getFinder(IFinder.BRANCH);
	}

	private boolean okToEndIfElseWithContinueOrReturn(int end, IFBlock ifst) {

		ArrayList loops = behaviour.getBehaviourLoops();
		ArrayList starts = behaviour.getInstructionStartPositions();
		boolean isloopend = false;
		Loop someloop = null;
		int prevs = getPrevStartCodePos(behaviour.getCode(), end);
		for (int z = 0; z < loops.size(); z++) {
			Loop l = (Loop) loops.get(z);
			int loopend = (l).getEndIndex();
			if (loopend == end || loopend == prevs) {
				if (!getBranchFinder().isInstReturnInst(end - 1,
						new StringBuffer())) {
					isloopend = true;
					someloop = l;
					break;
				}
				else{
					return false;
				}
			}

		}
		if (someloop != null) {
			boolean closed = someloop.wasLoopClosedInCode();
			if (closed) {
				int ifstart = ifst.getIfStart();
				Object[] sortedLoops = sortLoops(loops);
				Loop parent = getParentLoopForIf(sortedLoops, ifst.getIfStart());
				if (parent == someloop) {
					return false;
				}
			}
		}
		return true;
	}

	private Case checkForEnclosingSwitchCase(byte[] info, IFBlock ifst,
			int currentForIndex) {

		ArrayList switches = behaviour.getAllSwitchBlks();
		if (switches == null || switches.size() == 0)
			return null;
		int ifstart = ifst.getIfStart();
		HashMap caseblk_cends = new HashMap();
		for (int z = 0; z < switches.size(); z++) {

			Switch s = (Switch) switches.get(z);
			ArrayList cases = s.getAllCases();

			for (int k = 0; k < cases.size(); k++) {

				Case c = (Case) cases.get(k);
				int cs = c.getCaseStart();
				int ce = c.getCaseEnd();
				if (ifstart > cs && ifstart < ce) {
					caseblk_cends.put(new Integer(cs), c);
				}

			}

		}
		if (caseblk_cends.size() > 0) {
			Set keys = caseblk_cends.keySet();
			Integer sortedkeys[] = (Integer[]) keys.toArray(new Integer[keys
					.size()]);
			Arrays.sort(sortedkeys);
			int len = sortedkeys.length;
			Integer in = sortedkeys[len - 1];
			return (Case) caseblk_cends.get(in);
		}

		return null;
	}

	private int checkForIfEndFromEnclosingSetIfs(byte[] info, IFBlock ifst) {

		Iterator it = behaviour.getMethodIfs().iterator();
		int ifstart = ifst.getIfStart();
		ArrayList list = new ArrayList();
		while (it.hasNext()) {
			IFBlock temp = (IFBlock) it.next();
			int tempc = temp.getIfCloseLineNumber();
			if (temp.getIfStart() < ifstart && ifstart < tempc
					&& !temp.getDonotclose()) {
				list.add(new Integer(tempc));
			}
		}
		if (list.size() > 0) {
			Integer in[] = (Integer[]) list.toArray(new Integer[list.size()]);
			Arrays.sort(in);
			Integer first = in[0];
			if (first.intValue() < ifst.getIfCloseLineNumber()) {
				return first.intValue();
			}
		}
		return -1;
	}

	private void checkToAddElseAtIFEnd(int x) {

		Collection cifs = getCurrentIFStructues();
		Iterator cifsIt = cifs.iterator();
		while (cifsIt.hasNext()) {

			IFBlock curif = (IFBlock) cifsIt.next();
			int start = curif.getIfStart();
			boolean loopEndalso = isThisLoopEndAlso(behaviour
					.getBehaviourLoops(), currentForIndex, start);
			int ifbyj = curif.getIfCloseFromByteCode();
			Loop loop = getLoopGivenEnd(currentForIndex, behaviour
					.getBehaviourLoops());
			if (loopEndalso && loop != null) { // Just double check here with
				// loopendalso

				if (ifbyj > currentForIndex && !curif.getDonotclose()) {
					boolean addElseHere = addElseStart(currentForIndex);
					if (addElseHere) {
						tempString = "\nelse\n{\nbreak;\n}\n";
						GlobalVariableStore.getElsestartadded().add(
								new Integer(currentForIndex));
						behaviour.appendToBuffer(Util
								.formatDecompiledStatement(tempString));
						break;
					}
				}
			}
		}
	}

	private int getNextIfStart(int x, byte[] code) {

		ArrayList starts = behaviour.getInstructionStartPositions();
		for (int z = x + 3; z < code.length; z++) {

			boolean b = isThisInstrStart(starts, z);
			if (b) {
				b = isInstructionIF(code[z]);
				if (b) {
					return z;
				}
			}
		}
		return -1;
	}

	private boolean checkForIfCloseInRange(int firstif, int secif) {

		Collection c = getCurrentIFStructues();
		Iterator it = c.iterator();
		while (it.hasNext()) {
			IFBlock curif = (IFBlock) it.next();
			int s = curif.getIfStart();
			if (!curif.getDonotclose() && s != firstif && s != secif) {
				int ifclose = curif.getIfCloseLineNumber();
				if (s < firstif && ifclose > firstif && ifclose < secif) {
					return true;
				}
			}
		}
		return false;

	}

	private int handleIFACMPNE(byte[] info) {
		return execute(InstrConstants.BRANCH_INSTR_TYPE, null,
				JvmOpCodes.IF_ACMPNE);
	}

	private void handleLASTORE(OperandStack opStack) {
		execute(InstrConstants.STORE_INSTR_TYPE, InstrConstants.LONG,
				JvmOpCodes.LASTORE);

	}

	private java.lang.String addAnyElseBreakForIFChain(int i) {
		for (int z = 0; z < GlobalVariableStore.getElsebreaksforifchain()
				.size(); z++) {
			int j = ((Integer) GlobalVariableStore.getElsebreaksforifchain()
					.get(z)).intValue();
			if (i == j) {
				String marker = "<elsebreak" + i + ">";
				String endmarker = "</elsebreak" + i + ">";
				java.lang.String s = "\n" + marker + "else\n{\nbreak;\n}\n"
						+ endmarker + "\n";
				s = Util.formatDecompiledStatement(s);
				return s;
			}
		}
		return "";
	}

	private void handleLDC(OperandStack opStack, byte[] info, int i) {
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.LONG,
				JvmOpCodes.LDC);
	}

	private void handleLDCW(OperandStack opStack, byte[] info, int i) {
		execute(InstrConstants.LOAD_INSTR_TYPE, InstrConstants.LONG,
				JvmOpCodes.LDC_W);
	}

	private boolean checkForEmptyCatch(int start, int end) {
		ArrayList starts = behaviour.getInstructionStartPositions();
		boolean present = false;
		int next = -1;
		byte[] info = behaviour.getCode();
		if (isThisInstrStart(starts, start)) {
			switch (info[start]) {
			case JvmOpCodes.ASTORE_0:
			case JvmOpCodes.ASTORE_1:
			case JvmOpCodes.ASTORE_2:
			case JvmOpCodes.ASTORE_3:
				present = true;
				next = start + 1;
				break;
			case JvmOpCodes.ASTORE:
				present = true;
				next = start + 2;
				break;
			default:
				present = false;
				break;
			}
		}
		if (next != -1 && present) {
			if (next == end) {
				return true;
			}
		}
		return false;
	}

	private boolean checkForEmptyFinally(byte[] info, int i,
			ExceptionTable table) {
		ArrayList starts = behaviour.getInstructionStartPositions();
		int start = table.getStartOfHandlerForGuardRegion();
		int end = table.getEndOfHandlerForGuardRegion();
		boolean present = false;
		if (info[end] == JvmOpCodes.ATHROW && isThisInstrStart(starts, end)) {

			int prev = getPrevStartCodePos(info, end);
			switch (info[prev]) {
			case JvmOpCodes.ALOAD_0:
			case JvmOpCodes.ALOAD_1:
			case JvmOpCodes.ALOAD_2:
			case JvmOpCodes.ALOAD_3:
			case JvmOpCodes.ALOAD:
				present = true;
				break;
			default:
				present = false;
				break;
			}
			if (!present)
				return false;
			prev = getPrevStartCodePos(info, prev);
			present = false;
			switch (info[prev]) {
			case JvmOpCodes.ASTORE_0:
			case JvmOpCodes.ASTORE_1:
			case JvmOpCodes.ASTORE_2:
			case JvmOpCodes.ASTORE_3:
			case JvmOpCodes.ASTORE:
				present = true;
				break;
			default:
				present = false;
				break;
			}
			if (!present)
				return false;
			if (prev == start) {
				return true;
			}
			return false;

		}
		return false;
	}

	private boolean checkForEmptyFinally(int start, int end) {
		ArrayList starts = behaviour.getInstructionStartPositions();
		boolean present = false;
		byte[] info = behaviour.getCode();
		if (info[end] == JvmOpCodes.ATHROW && isThisInstrStart(starts, end)) {

			int prev = getPrevStartCodePos(info, end);
			switch (info[prev]) {
			case JvmOpCodes.ALOAD_0:
			case JvmOpCodes.ALOAD_1:
			case JvmOpCodes.ALOAD_2:
			case JvmOpCodes.ALOAD_3:
			case JvmOpCodes.ALOAD:
				present = true;
				break;
			default:
				present = false;
				break;
			}
			if (!present)
				return false;
			prev = getPrevStartCodePos(info, prev);
			present = false;
			switch (info[prev]) {
			case JvmOpCodes.ASTORE_0:
			case JvmOpCodes.ASTORE_1:
			case JvmOpCodes.ASTORE_2:
			case JvmOpCodes.ASTORE_3:
			case JvmOpCodes.ASTORE:
				present = true;
				break;
			default:
				present = false;
				break;
			}
			if (!present)
				return false;
			if (prev == start) {
				return true;
			}
			return false;

		}
		return false;
	}

	private int execute(java.lang.String instrtype, java.lang.String vartype,
			int opcode) {
		// System.out.println("Executing at"+currentForIndex);
		IInstructionCommandHolder instruction = InstructionHelper
				.getInstruction(instrtype, vartype, opcode);
		AbstractInstructionCommand command = (AbstractInstructionCommand) instruction
				.getCommand();
		try {
			command.execute();
		} catch (Exception e) {
			AllExceptionHandler handler = new AllExceptionHandler(e);
			handler.setBehaviour(behaviour);
			handler.setCodePosition(ExecutionState
					.getCurrentInstructionPosition());
			handler.reportException();
		}
		return command.getSkipBytes();
	}

}
