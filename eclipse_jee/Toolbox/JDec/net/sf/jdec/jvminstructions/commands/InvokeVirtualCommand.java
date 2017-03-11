package net.sf.jdec.jvminstructions.commands;

import java.util.ArrayList;
import java.util.Hashtable;

import net.sf.jdec.constantpool.ClassDescription;
import net.sf.jdec.constantpool.MethodRef;
import net.sf.jdec.constantpool.NameAndType;
import net.sf.jdec.core.DecompilerHelper;
import net.sf.jdec.core.GlobalVariableStore;
import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Constants;
import net.sf.jdec.util.Util;

public class InvokeVirtualCommand extends AbstractInstructionCommand {

	public InvokeVirtualCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 2;
	}

	public void execute() {
		boolean pushed = false;
		String tempString = "";
		boolean prevInstInvokeVirtual = GlobalVariableStore.isPrevInstInvokeVirtual();
		
		String funcCall = "";

		/*
		 * temp1=info[++i]; temp2=info[++i]; classIndex=((temp1 << 8) | temp2);
		 * if(classIndex < 0)classIndex = (temp1+1)*256-Math.abs(temp2);
		 */
		int currentForIndex = getCurrentInstPosInCode();
		int i=currentForIndex;
		int classIndex = getGenericFinder().getOffset(i);
		i += 2;
		ClassDescription cd = getContext().getClassRef().getCd();
		MethodRef mref = cd.getMethodRefAtCPoolPosition(classIndex);
		String classname = mref.getClassname();
		DecompilerHelper.registerInnerClassIfAny(classname.replace('.', '/'));
		NameAndType ninfo = cd.getNameAndTypeAtCPoolPosition(mref.getDescriptionPointer());
		String typeofmet = mref.getTypeofmethod();
		int s1 = typeofmet.indexOf(")");
		String rettp = "";
		ArrayList returntype = null;
		boolean takeret = getStoreFinder().isNextInstAStore(currentForIndex + 3);
		if (s1 != -1 && s1 + 1 < typeofmet.length()) {

			rettp = typeofmet.substring(s1 + 1);

			Util.parseReturnType(rettp);
			returntype = Util.getreturnSignatureAsList();
		}
		String pushStr = classname;
		// NOTE: removed takeret : sbelur
		if (returntype != null && returntype.size() > 0) {
			pushStr = (java.lang.String) returntype.get(0);
		}
		if (pushStr != null && pushStr.trim().equalsIgnoreCase("void")) {
			pushStr = classname;//
		}
		int dex;
		Operand op1,op2;
		Util.parseDescriptor(typeofmet);
		ArrayList paramlist = Util.getParsedSignatureAsList();
		OperandStack opStack = getStack();
		DecompilerHelper.	resetMethodParameters(opStack, paramlist, currentForIndex);
		Hashtable methodLookUp = cd.getMethodLookUp();
		Behaviour b = (Behaviour) methodLookUp.get(mref.getKey());
		boolean argumentReturnTypeChecked = false;
		String argumentRetType = "";
		int br = typeofmet.indexOf(")");
		String RET = "";
		byte[] info = getCode();
		Behaviour behavior = getContext();
		if (br != -1) {
			char c;
			if (typeofmet.length() >= (br + 1))
				c = typeofmet.charAt(br + 1);
			else
				c = '?';
			RET = "" + c;

		}
		if (b != null) {
			argumentReturnTypeChecked = true;
			argumentRetType = b.getReturnType();
			op1 = new Operand();
			// op1.setCategory(Constants.CATEGORY1);
			op1.setOperandType(Constants.IS_OBJECT_REF);
			int numParams = b.getMethodParams().length;
			funcCall = b.getBehaviourName() + "(";
			if (numParams == 0) {
				funcCall += ")";
				Operand objRef = opStack.getTopOfStack();
				java.lang.String temp = funcCall;
				funcCall = "";
				funcCall = objRef.getOperandValue() + "." + temp;
			} else {
				Operand[] oparr = new Operand[numParams];
				int opArrIndx = numParams - 1;
				dex = 0;
				for (int indx = numParams - 1; indx >= 0; indx--) {
					op2 = (Operand) opStack.pop();
					DecompilerHelper.resetOperandValueIfNecessary(paramlist, indx, op2);
					oparr[dex++] = op2;
					opArrIndx--;
					/*
					 * if(indx == numParams - 1) { funcCall +=
					 * op2.getOperandValue()+")"; } else { funcCall +=
					 * op2.getOperandValue()+","; }
					 */
					// b.getOpStack().push(op2);
				}

				for (int indx = numParams - 1; indx >= 0; indx--) {
					op2 = oparr[indx];
					if (indx > 0) {
						funcCall += op2.getOperandValue() + ",";

					} else {
						funcCall += op2.getOperandValue() + ")";
					}
				}
				Operand objRef = opStack.getTopOfStack();
				java.lang.String temp = funcCall;
				funcCall = "";
				funcCall = objRef.getOperandValue() + "." + temp;
			}

			op1.setOperandValue(funcCall);

			// Commented by belurs
			// Do we have to do it ?

			// opStack.push(op1);
			/*
			 * b.setParentBehaviour(behaviour); Decompiler disassembler=new
			 * Decompiler(b,cd); disassembler.disassembleCode();
			 * disassembler=null; //TODO : Call the parseJVMCodes Function
			 * recrusively to parse the invokedfunction
			 */
			// b.setHasBeenDissassembled(true); // TODO: Commented by belurs
			// Need to check where all this method is used
			// and if and how it shud be is used
			// behavior.appendToBuffer( funcCall+";\n";
		} else {
			argumentReturnTypeChecked = true;
			java.lang.String methodSignature = mref.getTypeofmethod(); // Should
																		// Be
																		// Refactored...Or
																		// getting
																		// called
																		// wrongly
			br = methodSignature.indexOf(")");
			if (br != -1) {
				char c;
				if (methodSignature.length() >= (br + 1))
					c = methodSignature.charAt(br + 1);
				else
					c = '?';
				argumentRetType = "" + c;

			}
			methodSignature = methodSignature.substring(1, methodSignature
					.indexOf(")"));
			int numberOfParameters = paramlist.size();
			// int numberOfParameters =
			java.lang.String[] funcArray = new java.lang.String[numberOfParameters];
			int index = 0;
			int funcArrayIndex = 0;
			for (int indx = numberOfParameters - 1; indx >= 0; indx--) {
				op2 = (Operand) opStack.pop();
				DecompilerHelper.resetOperandValueIfNecessary(paramlist, indx, op2);

				if (op2.getOperandValue() != null)
					funcArray[indx] = (op2.getOperandValue()).toString();
				else
					funcArray[indx] = "" + (op2.getOperandValue());
				funcArrayIndex++;
			}

			op2 = (Operand) opStack.pop();
			// System.out.println(op2.getOperandValue().getClass()+"
			// op2.getOperandValue().class");
			funcCall += (op2.getOperandValue() != null ? op2.getOperandValue()
					.toString() : "")
					+ "." + mref.getMethodName() + "(";
			for (int indx = 0; indx < funcArray.length; indx++) {
				if (indx < funcArray.length - 1) {
					funcCall += funcArray[indx] + ",";
				} else {
					funcCall += funcArray[indx];
				}

			}
			funcCall += ")";
			op1 = new Operand();
			// op1.setCategory(Constants.CATEGORY1);
			op1.setOperandType(Constants.IS_OBJECT_REF);
			op1.setOperandValue(funcCall);
			// op3.setOperandValue(funcCall);
			// opStack.push(op3);
		}

		/*
		 * int h=funcCall.indexOf(".",t+1); if(h!=-1) {
		 * h=funcCall.indexOf(".",h+1); if(h!=-1) { int
		 * h2=funcCall.indexOf(".",h+1); if(h2!=-1) { java.lang.String
		 * temps=funcCall.substring(0,h2); StringBuffer sb=new StringBuffer("");
		 * checkForImport(temps,sb);
		 * funcCall=sb.toString()+funcCall.substring(h2);
		 * op1.setOperandValue(funcCall);
		 *  } } }
		 */

		if (getStoreFinder().isInstStore0(i + 1)) {
			pushed = true;
			opStack.push(op1);
			op1.setClassType(pushStr);
		} else if (getGenericFinder().isNextInstructionInvokeStatic((info[i + 1]))
				|| getGenericFinder().isNextInstructionInvokeVirtual(info[i + 1])
				|| getGenericFinder().isNextInstructionInvokeInterface(info[i + 1])
				|| getGenericFinder().isNextInstructionInvokeSpecial(info[i + 1])
				|| getStoreFinder().isNextInstructionStore(i + 1)
				|| getBranchFinder().isNextInstructionIf(info[i + 1])
				|| (info[(i + 1)] == JvmOpCodes.PUTFIELD)
				|| (info[(i + 1)] == JvmOpCodes.PUTSTATIC)) { // TODO need to
																// check for
																// other cases
																// like switch
			if (RET.equalsIgnoreCase("V") == false) {
				opStack.push(op1);
				op1.setClassType(pushStr);
				pushed = true;
			} else {
				tempString = Util.formatDecompiledStatement(funcCall + ";\n");
				behavior.appendToBuffer( tempString)	;
			}
		} else if (getLoadFinder().isNextInstructionLoad(i + 1)) {
			if (RET.equalsIgnoreCase("V") == true) {
				tempString = Util.formatDecompiledStatement(funcCall + ";\n");
				behavior.appendToBuffer( tempString);
			} else {
				opStack.push(op1);
				op1.setClassType(pushStr);
				pushed = true;
			}

		} else if (getGenericFinder().isNextInstructionPop(i + 1)
				|| getBranchFinder().isNextInstructionReturn(i + 1)) {

			tempString = Util.formatDecompiledStatement(funcCall + ";\n");
			behavior.appendToBuffer( tempString);

			opStack.push(op1);
			op1.setClassType(pushStr);
			// pushed=true;

		} else if (getGenericFinder().isNextInstructionConversionInst(info[i + 1])) {
			opStack.push(op1);
			op1.setClassType(pushStr);
			pushed = true;
		} else if (DecompilerHelper.checkForValueReturn(info, (i + 1))) {
			opStack.push(op1);
			op1.setClassType(pushStr);
			pushed = true;
		} else if (getGenericFinder().checkForSomeSpecificInstructions(info, (i + 1))) // BUG here
																	// w.r.t LDC
		{
			opStack.push(op1);
			op1.setClassType(pushStr);
			pushed = true;
		}

		else {
			// opStack.push(op1);
			if (RET.equalsIgnoreCase("V") == false) {
				opStack.push(op1);
				op1.setClassType(pushStr);
				pushed = true;
			}

			else {
				tempString = Util.formatDecompiledStatement(funcCall + ";\n");
				behavior.appendToBuffer( tempString);
			}
		}

		if (pushed) {
			boolean r = false;//checkIFLoadInstIsPartOFTernaryCond(currentForIndex);
			if (r) {
				if (opStack.size() > 0) {
					java.lang.String str1 = opStack.getTopOfStack()
							.getOperandValue();
					java.lang.String str2 = opStack.getTopOfStack()
							.getOperandValue();
					java.lang.String str = str2 + str1;
					Operand d = createOperand(str);
					opStack.push(d);
				}
			}
		}
		GlobalVariableStore.setPrevInstInvokeVirtual(prevInstInvokeVirtual);
		
	}

}
