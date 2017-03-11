package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.core.DecompilerHelper;
import net.sf.jdec.core.GlobalVariableStore;
import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Util;

public class FastoreCommand extends AbstractInstructionCommand {

	public FastoreCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		byte[] info = getCode();
		Operand op = null;
		Operand op1 = null;
		Operand op2 = null;
		boolean primitiveastore = GlobalVariableStore.isPrimitiveastore();
		int arraytimespush = GlobalVariableStore.getArraytimespush();
		Behaviour behavior=getContext();
		boolean specialIAStore = GlobalVariableStore.isSpecialIASTORE();
		OperandStack opStack = getStack();
		StringBuffer addtype = new StringBuffer();
		int currentForIndex = getCurrentInstPosInCode();
		boolean b = DecompilerHelper.checkForArrayPostIncrement(
				currentForIndex, opStack, getCode(), "category1", addtype);
		boolean bl2 = DecompilerHelper
				.checkForArrayMultiAssignablePostIncrement(currentForIndex,
						opStack, getCode(), "category1");
		if (b && opStack.size() > 0
				&& GlobalVariableStore.getArraytimesstack().size() == 0) {
			op = (Operand) opStack.pop();
			op1 = (Operand) opStack.pop();
			op2 = (Operand) opStack.pop();
			java.lang.String temp = "";
			if (addtype.toString().equals("add"))
				temp += op2.getOperandValue() + "[" + op1.getOperandValue()
						+ "]" + "++";
			else
				temp += op2.getOperandValue() + "[" + op1.getOperandValue()
						+ "]" + "--";
			Operand o = opStack.peekTopOfStack();
			o.setOperandValue(temp);

		} else if (bl2 && opStack.size() > 0
				&& GlobalVariableStore.getArraytimesstack().size() == 0) {
			op = (Operand) opStack.pop();
			op1 = (Operand) opStack.pop();
			op2 = (Operand) opStack.pop();
			java.lang.String temp = "";
			temp += op2.getOperandValue() + "[" + op1.getOperandValue() + "]"
					+ "=";
			Operand o = opStack.peekTopOfStack();
			temp = temp + o.getOperandValue();
			o.setOperandValue(temp);
		} else {
			if (GlobalVariableStore.getSkipPrimitiveArrayStores().contains(
					new Integer(currentForIndex)) == false) {
				java.lang.String tempString = "";
				op = (Operand) opStack.pop();
				op1 = (Operand) opStack.pop();
				if (opStack.size() > 0) {
					op2 = (Operand) opStack.pop();
					tempString = op2.getOperandValue() + "["
							+ op1.getOperandValue() + "]="
							+ op.getOperandValue() + ";\n";
				}
				if (GlobalVariableStore.getArraytimesstack().size() == 0) {

					int curpos = currentForIndex;
					if (curpos - 2 > 0
							&& (info[curpos - 1] == JvmOpCodes.DUP_X2
									|| info[curpos - 1] == JvmOpCodes.DUP
									|| info[curpos - 1] == JvmOpCodes.DUP2
									|| info[curpos - 1] == JvmOpCodes.DUP_X1
									|| info[curpos - 1] == JvmOpCodes.DUP2_X1 || info[curpos - 1] == JvmOpCodes.DUP2_X2)
							&& info[curpos - 2] == JvmOpCodes.FADD) {
						tempString = "++" + op2.getOperandValue() + "["
								+ op1.getOperandValue() + "]";
						java.lang.String v1 = op2.getOperandValue() + "["
								+ op1.getOperandValue() + "]";
						java.lang.String v2 = opStack.peekTopOfStack()
								.getOperandValue();
						if (v2.equals(v1)) {
							opStack.pop();
						}
						opStack.push(createOperand(tempString));
						specialIAStore = true;
					} else if (curpos - 2 > 0
							&& (info[curpos - 1] == JvmOpCodes.DUP_X2
									|| info[curpos - 1] == JvmOpCodes.DUP
									|| info[curpos - 1] == JvmOpCodes.DUP2
									|| info[curpos - 1] == JvmOpCodes.DUP_X1
									|| info[curpos - 1] == JvmOpCodes.DUP2_X1 || info[curpos - 1] == JvmOpCodes.DUP2_X2)
							&& info[curpos - 2] == JvmOpCodes.FSUB) {
						tempString = "--" + op2.getOperandValue() + "["
								+ op1.getOperandValue() + "]";
						java.lang.String v1 = op2.getOperandValue() + "["
								+ op1.getOperandValue() + "]";
						java.lang.String v2 = opStack.peekTopOfStack()
								.getOperandValue();
						if (v2.equals(v1)) {
							opStack.pop();
						}
						opStack.push(createOperand(tempString));
						specialIAStore = true;
					} else if (curpos - 3 > 0
							&& info[curpos - 1] == JvmOpCodes.FADD
							&& info[curpos - 2] == JvmOpCodes.FCONST_1
							&& (info[curpos - 3] == JvmOpCodes.DUP_X2
									|| info[curpos - 3] == JvmOpCodes.DUP
									|| info[curpos - 3] == JvmOpCodes.DUP2
									|| info[curpos - 3] == JvmOpCodes.DUP_X1
									|| info[curpos - 3] == JvmOpCodes.DUP2_X1 || info[curpos - 3] == JvmOpCodes.DUP2_X2)) {
						tempString = op2.getOperandValue() + "["
								+ op1.getOperandValue() + "]++";
						java.lang.String v1 = op2.getOperandValue() + "["
								+ op1.getOperandValue() + "]";
						java.lang.String v2 = opStack.peekTopOfStack()
								.getOperandValue();
						if (v2.equals(v1)) {
							opStack.pop();
						}
						opStack.push(createOperand(tempString));
						specialIAStore = true;
					} else if (curpos - 3 > 0
							&& info[curpos - 1] == JvmOpCodes.FSUB
							&& info[curpos - 2] == JvmOpCodes.FCONST_1
							&& (info[curpos - 3] == JvmOpCodes.DUP_X2
									|| info[curpos - 3] == JvmOpCodes.DUP
									|| info[curpos - 3] == JvmOpCodes.DUP2
									|| info[curpos - 3] == JvmOpCodes.DUP_X1
									|| info[curpos - 3] == JvmOpCodes.DUP2_X1 || info[curpos - 3] == JvmOpCodes.DUP2_X2)) {
						tempString = op2.getOperandValue() + "["
								+ op1.getOperandValue() + "]--";
						java.lang.String v1 = op2.getOperandValue() + "["
								+ op1.getOperandValue() + "]";
						java.lang.String v2 = opStack.peekTopOfStack()
								.getOperandValue();
						if (v2.equals(v1)) {
							opStack.pop();
						}
						opStack.push(createOperand(tempString));
						specialIAStore = true;
					} else {
						if (GlobalVariableStore.getSkipPrimitiveArrayStores()
								.contains(new Integer(currentForIndex)) == false) {
							behavior.appendToBuffer( Util
									.formatDecompiledStatement(tempString));
						}
					}
				} else {
					primitiveastore = true;
					java.lang.String newvalue = "";
					arraytimespush = Integer.parseInt(GlobalVariableStore
							.getArraytimesstack().peek().toString());
					if (arraytimespush > 0) {

						int curpos = currentForIndex;

						if (curpos - 2 > 0
								&& DecompilerHelper.isArrayElement(curpos + 1)
								&& (info[curpos - 1] == JvmOpCodes.DUP_X2
										|| info[curpos - 1] == JvmOpCodes.DUP
										|| info[curpos - 1] == JvmOpCodes.DUP2
										|| info[curpos - 1] == JvmOpCodes.DUP_X1
										|| info[curpos - 1] == JvmOpCodes.DUP2_X1 || info[curpos - 1] == JvmOpCodes.DUP2_X2)
								&& info[curpos - 2] == JvmOpCodes.FADD) {
							if (!newfound())
								newvalue = "++" + op2.getOperandValue() + "["
										+ op1.getOperandValue() + "]";
							else {
								if (opStack.size() > 0) {
									java.lang.String topv = opStack
											.getTopOfStack().getOperandValue();
									if (topv.trim().startsWith("++")) {
										newvalue = op2.getOperandValue() + topv;
									} else
										newvalue = "++" + op2.getOperandValue()
												+ topv;
									if (topv.equals(opStack.peekTopOfStack()
											.getOperandValue())) {
										opStack.pop();
									}
								}
							}

						} else if (curpos - 2 > 0
								&& DecompilerHelper.isArrayElement(curpos + 1)
								&& (info[curpos - 1] == JvmOpCodes.DUP_X2
										|| info[curpos - 1] == JvmOpCodes.DUP
										|| info[curpos - 1] == JvmOpCodes.DUP2
										|| info[curpos - 1] == JvmOpCodes.DUP_X1
										|| info[curpos - 1] == JvmOpCodes.DUP2_X1 || info[curpos - 1] == JvmOpCodes.DUP2_X2)
								&& info[curpos - 2] == JvmOpCodes.FSUB) {
							if (!newfound())
								newvalue = "--" + op2.getOperandValue() + "["
										+ op1.getOperandValue() + "]";
							else {
								if (opStack.size() > 0) {

									java.lang.String topv = opStack
											.getTopOfStack().getOperandValue();
									if (topv.trim().startsWith("--")) {
										newvalue = op2.getOperandValue() + topv;
									} else
										newvalue = "--" + op2.getOperandValue()
												+ topv;//
									if (topv.equals(opStack.peekTopOfStack()
											.getOperandValue())) {
										opStack.pop();
									}

								}
							}

						} else if (curpos - 3 > 0
								&& DecompilerHelper.isArrayElement(curpos + 1)
								&& info[curpos - 1] == JvmOpCodes.FADD
								&& info[curpos - 2] == JvmOpCodes.FCONST_1
								&& (info[curpos - 3] == JvmOpCodes.DUP_X2
										|| info[curpos - 3] == JvmOpCodes.DUP
										|| info[curpos - 3] == JvmOpCodes.DUP2
										|| info[curpos - 3] == JvmOpCodes.DUP_X1
										|| info[curpos - 3] == JvmOpCodes.DUP2_X1 || info[curpos - 3] == JvmOpCodes.DUP2_X2)) {
							if (!newfound())
								newvalue = op2.getOperandValue() + "["
										+ op1.getOperandValue() + "]++";
							else {
								if (opStack.size() > 0) {

									java.lang.String topv = opStack
											.getTopOfStack().getOperandValue();
									newvalue = op2.getOperandValue() + topv
											+ "++";
									if (topv.equals(opStack.peekTopOfStack()
											.getOperandValue())) {
										opStack.pop();
									}

								}
							}

						} else if (curpos - 3 > 0
								&& DecompilerHelper.isArrayElement(curpos + 1)
								&& info[curpos - 1] == JvmOpCodes.FSUB
								&& info[curpos - 2] == JvmOpCodes.FCONST_1
								&& (info[curpos - 3] == JvmOpCodes.DUP_X2
										|| info[curpos - 3] == JvmOpCodes.DUP
										|| info[curpos - 3] == JvmOpCodes.DUP2
										|| info[curpos - 3] == JvmOpCodes.DUP_X1
										|| info[curpos - 3] == JvmOpCodes.DUP2_X1 || info[curpos - 3] == JvmOpCodes.DUP2_X2)) {

							if (!newfound())
								newvalue = op2.getOperandValue() + "["
										+ op1.getOperandValue() + "]--";
							else {
								if (opStack.size() > 0) {

									java.lang.String topv = opStack
											.getTopOfStack().getOperandValue();
									newvalue = op2.getOperandValue() + topv
											+ "--";
									if (topv.equals(opStack.peekTopOfStack()
											.getOperandValue())) {
										opStack.pop();
									}
								}
							}

						} else
							newvalue = "";
						if (newvalue.length() > 0) {
							StringBuffer v = new StringBuffer("");
							int c = getGenericFinder()
									.isNextInstructionConversionInst(
											currentForIndex + 1, v);
							if (c != -1 && newvalue.indexOf(v.toString()) == -1) {
								newvalue = "(" + v.toString() + ")" + newvalue;
							}
						}

						// ///////////////
						if (GlobalVariableStore.getSkipPrimitiveArrayStores()
								.contains(new Integer(currentForIndex)) == false) {

							if (newfound()) {
								// z=createOperand(op.getOperandValue());
								// Operand y=opStack.getTopOfStack();
								java.lang.String newv = op2.getOperandValue()
										+ op.getOperandValue();
								if (newvalue.length() > 0) {
									newv = newvalue;
									specialIAStore = true;
								}
								arraytimespush = Integer
										.parseInt(GlobalVariableStore
												.getArraytimesstack().pop()
												.toString());
								arraytimespush--;
								if (arraytimespush == 0) {
									newv += "}";
								} else {
									GlobalVariableStore.getArraytimesstack()
											.push("" + arraytimespush);
									newv += ",";
								}
								opStack.push(createOperand(newv));
							} else {
								arraytimespush = Integer
										.parseInt(GlobalVariableStore
												.getArraytimesstack().pop()
												.toString());
								arraytimespush--;
								if (arraytimespush == 0) {

									boolean closeImmediate = false; // for the
									// parent
									// actually
									if (GlobalVariableStore.getArraytimesstack()
											.size() > 0) {
										int oldstacksize = Integer
												.parseInt(GlobalVariableStore
														.getArraytimesstack()
														.pop().toString());
										int newstacksize = oldstacksize - 1;
										if (newstacksize != 0)
											GlobalVariableStore
													.getArraytimesstack().push(
															"" + newstacksize);
										else
											closeImmediate = true;

									}

									if (GlobalVariableStore.getArraytimesstack()
											.isEmpty()) {
										if (newvalue.length() > 0) {
											behavior.appendToBuffer( newvalue + "}");
											int count = arrayClosingBracketCount(currentForIndex);
											java.lang.String temp = "";
											for (int z = 1; z <= count; z++) {
												temp += "\n}";
												if (z < count)
													temp += "\n";

											}
											// Util.forceNewLine=false;
											Util.forceTrimLines = false;
											behavior.appendToBuffer( Util
													.formatDecompiledStatement(temp)
													+ ";\n");
											Util.forceNewLine = true;
											Util.forceTrimLines = true;
											specialIAStore = true;
										} else {
											behavior.appendToBuffer( op
													.getOperandValue()
													+ "}");
											int count = arrayClosingBracketCount(currentForIndex);
											java.lang.String temp = "";
											for (int z = 1; z <= count; z++) {
												temp += "\n}";
												if (z < count)
													temp += "\n";
											}
											Util.forceStartSpace = false;
											Util.forceNewLine = false;
											Util.forceTrimLines = false;
											behavior.appendToBuffer( Util
													.formatDecompiledStatement(temp)
													+ ";\n");
											Util.forceStartSpace = true;
											Util.forceTrimLines = false;
											Util.forceNewLine = true;
										}
									} else {
										if (newvalue.length() > 0) {
											behavior.appendToBuffer( newvalue + "},");
											specialIAStore = true;
										} else {
											behavior.appendToBuffer( op
													.getOperandValue()
													+ "},");
										}
										if (closeImmediate) {
											do {
												Util.forceStartSpace = false;
												Util.forceNewLine = false;
												Util.forceTrimLines = false;
												if (GlobalVariableStore
														.getArraytimesstack()
														.size() > 0)
													behavior.appendToBuffer( Util
															.formatDecompiledStatement("\n},\n"));
												else {
													behavior.appendToBuffer( Util
															.formatDecompiledStatement("\n};\n"));
												}
												Util.forceStartSpace = true;
												Util.forceNewLine = true;
												Util.forceTrimLines = true;

												if (GlobalVariableStore
														.getArraytimesstack()
														.size() > 0) {
													int oldstacksize = Integer
															.parseInt(GlobalVariableStore
																	.getArraytimesstack()
																	.pop()
																	.toString());
													int newstacksize = oldstacksize - 1;
													if (newstacksize != 0) {
														GlobalVariableStore
																.getArraytimesstack()
																.push(
																		""
																				+ newstacksize);
														closeImmediate = false;
													} else {
														closeImmediate = true;
													}
												} else {
													closeImmediate = false;
												}

											} while (closeImmediate);

										}

									}
								} else {
									GlobalVariableStore.getArraytimesstack()
											.push("" + arraytimespush);
									/*
									 * if(isThisInstrStart(starts,(currentForIndex-1)) &&
									 * isInstructionAnyDUP(info[currentForIndex-1]) &&
									 * newvalue!=null &&
									 * newvalue.trim().length() > 0){
									 * if(newvalue!=null &&
									 * (newvalue.trim().startsWith("++") ||
									 * newvalue.trim().startsWith("--")) &&
									 * opStack.size() > 0) { java.lang.String
									 * tpv=opStack.peekTopOfStack().getOperandValue().trim();
									 * if("++".concat(tpv).equals(newvalue) ||
									 * "--".concat(tpv).equals(newvalue)){
									 * opStack.pop(); } }
									 * 
									 * opStack.push(createOperand(newvalue)); }
									 */
									// else{
									if (newvalue.length() > 0) {
										behavior.appendToBuffer( newvalue + ",");
										specialIAStore = true;
									} else {

										if (getStoreFinder()
												.isInstPrimitiveArrayStore(
														(currentForIndex + 1))
												&& tempString != null
												&& tempString.length() > 0)
											behavior.appendToBuffer( tempString);
										else
											behavior.appendToBuffer( op
													.getOperandValue()
													+ ",");
									}
									// }
								}
							}
						}
					}
				}
			} else {

				if (opStack.size() > 2 && !newfound()) {
					opStack.pop();
					opStack.pop();
				}

			}
			int nextinst = info[currentForIndex + 1];
			if (getGenericFinder().isThisInstrStart(currentForIndex + 1)
					&& nextinst == JvmOpCodes.AASTORE) {
				GlobalVariableStore.getSkipaastores().add(
						new Integer(currentForIndex + 1));
			}

		}
		
		GlobalVariableStore.setPrimitiveastore(primitiveastore);
		GlobalVariableStore.setSpecialIASTORE(specialIAStore);
		GlobalVariableStore.setArraytimespush(arraytimespush);
		
	}

}
