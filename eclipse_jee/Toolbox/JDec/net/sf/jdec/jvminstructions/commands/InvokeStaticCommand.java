package net.sf.jdec.jvminstructions.commands;

import java.util.ArrayList;
import java.util.Hashtable;

import net.sf.jdec.config.Configuration;
import net.sf.jdec.constantpool.ClassDescription;
import net.sf.jdec.constantpool.MethodRef;
import net.sf.jdec.constantpool.NameAndType;
import net.sf.jdec.core.DecompilerHelper;
import net.sf.jdec.core.GlobalVariableStore;
import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Constants;
import net.sf.jdec.util.Util;

public class InvokeStaticCommand extends AbstractInstructionCommand {

	public InvokeStaticCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 2;
	}

	public void execute() {
		int dex;
		boolean newfound = GlobalVariableStore.isNewfound();
		boolean previnstwasinvoke = false;
		int currentForIndex = getCurrentInstPosInCode();
		int itemp = currentForIndex - 1;
		boolean pushed = false;
		Behaviour behavior = getContext();
		boolean doNotPop = GlobalVariableStore.isDoNotPop();
		String RET = "";
		boolean codeStmtFormed = false;
		String funcCall = "";
		Operand op2;
		ClassDescription cd = getContext().getClassRef().getCd();
		cd.printAllUtf8StringInNameAndTypeObjects();
		String tempString = "";
		/*
		 * temp1=info[++i]; temp2=info[++i]; classIndex=((temp1 << 8) | temp2);
		 * if(classIndex < 0)classIndex=(temp1+1)*256-Math.abs(temp2);
		 */
		byte[] info = getCode();
		OperandStack opStack = getStack();
		int i = getCurrentInstPosInCode();
		int classIndex = getGenericFinder().getOffset(i);
		i += 2;
		MethodRef mref = cd.getMethodRefAtCPoolPosition(classIndex);
		String classname = mref.getClassname();
		String typeofmet = mref.getTypeofmethod();
		Util.parseDescriptor(typeofmet);
		ArrayList paramlist = Util.getParsedSignatureAsList();
		boolean takeret = getStoreFinder()
				.isNextInstAStore(currentForIndex + 3);
		int s1 = typeofmet.indexOf(")");
		ArrayList returntype = null;
		Operand op;
		Operand op1;
		String opvalue = "";
		if (s1 != -1 && s1 + 1 < typeofmet.length()) {

			String rettp = typeofmet.substring(s1 + 1);

			Util.parseReturnType(rettp);
			returntype = Util.getreturnSignatureAsList();
		}
		String pushStr = classname;

		if (returntype != null && returntype.size() > 0) {
			pushStr = (java.lang.String) returntype.get(0);
		}
		if (pushStr != null && pushStr.trim().equalsIgnoreCase("void")) {
			pushStr = classname;
		}
		DecompilerHelper.resetMethodParameters(opStack, paramlist,
				currentForIndex);
		DecompilerHelper.registerInnerClassIfAny(classname.replace('.', '/'));
		NameAndType ninfo = cd.getNameAndTypeAtCPoolPosition(mref
				.getDescriptionPointer());
		Hashtable methodLookUp = cd.getMethodLookUp();
		Behaviour b = (Behaviour) methodLookUp.get(mref.getKey());

		int br = typeofmet.indexOf(")");
		if (br != -1) {
			char c;
			if (typeofmet.length() >= (br + 1))
				c = typeofmet.charAt(br + 1);
			else
				c = '?';
			RET = "" + c;

		}
		boolean argumentReturnTypeChecked = false;
		String argumentRetType = "";
		if (b != null) {
			argumentRetType = b.getReturnType();
			op1 = new Operand();
			// op1.setCategory(Constants.CATEGORY1);
			op1.setOperandType(Constants.IS_OBJECT_REF);
			// if(!b.isHasBeenDissassembled()) {
			int numParams = b.getMethodParams().length;
			funcCall = b.getBehaviourName() + "(";
			dex = 0;
			/*
			 * for(int indx=numParams-1;indx>=0;indx--) { op2 =
			 * (Operand)opStack.pop();
			 * resetOperandValueIfNecessary(paramlist,indx,op2); if(indx > 0) {
			 * funcCall += op2.getOperandValue()+","; } else { funcCall +=
			 * op2.getOperandValue()+")"; } b.getOpStack().push(op2);
			 * op2.setClassType(classname); }
			 */
			java.lang.String[] funcArray = new java.lang.String[numParams];
			dex = 0;
			for (int indx = numParams - 1; indx >= 0; indx--) {
				op2 = (Operand) opStack.pop();
				DecompilerHelper.resetOperandValueIfNecessary(paramlist, indx,
						op2);
				if (op2 != null && op2.getOperandValue() != null)
					funcArray[dex++] = (op2.getOperandValue()).toString();
				else
					funcArray[dex++] = "" + (op2.getOperandValue());

			}
			for (int indx = funcArray.length - 1; indx >= 0; indx--) {
				if (indx != 0) {
					funcCall += funcArray[indx] + ",";
				} else {
					funcCall += funcArray[indx];
				}

			}

			funcCall += ");\n";

			op1.setOperandValue(funcCall);
			// opStack.push(op1);
			/*
			 * b.setParentBehaviour(behaviour); Decompiler disassembler=new
			 * Decompiler(b,cd); disassembler.disassembleCode();
			 * disassembler=null;
			 */

			// behavior.appendToBuffer( Util.formatDecompiledStatement(funcCall);
			// codeStatements+=";\n";
			// codeStmtFormed=true;
			// }
		} else {
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
			dex = 0;
			for (int indx = numberOfParameters - 1; indx >= 0; indx--) {
				op2 = (Operand) opStack.pop();
				DecompilerHelper.resetOperandValueIfNecessary(paramlist, indx,
						op2);
				if (op2 != null && op2.getOperandValue() != null)
					funcArray[dex++] = (op2.getOperandValue()).toString();
				else
					funcArray[dex++] = "" + (op2.getOperandValue());
				funcArrayIndex++;
			}

			// op2 = (Operand)opStack.pop();
			// mref.getClassname()+"."+mref.getMethodName()
			boolean funcCallFormed = false;
			if (Configuration.getShowImport().equalsIgnoreCase("false")) {
				funcCall += mref.getClassname().replace('/', '.') + "."
						+ mref.getMethodName() + "(";
				funcCallFormed = true;
			} else {
				java.lang.String simplename = "";
				java.lang.String fullName = mref.getClassname();
				int lastSlash = mref.getClassname().lastIndexOf("/");
				if (lastSlash == -1) {
					lastSlash = mref.getClassname().lastIndexOf(".");
				}
				if (lastSlash != -1) {
					simplename = fullName.substring(lastSlash + 1);
				} else
					simplename = fullName;
				funcCall += simplename + "." + mref.getMethodName() + "(";
				fullName = fullName.replace('/', '.');
				ConsoleLauncher.addImportClass(fullName);
				funcCallFormed = true;

			}
			for (int indx = funcArray.length - 1; indx >= 0; indx--) {
				if (indx != 0) {
					funcCall += funcArray[indx] + ",";
				} else {
					funcCall += funcArray[indx];
				}

			}
			if (funcCallFormed && funcCall.indexOf(";") == -1)
				funcCall += ")";
			op1 = new Operand();
			// op1.setCategory(Constants.CATEGORY1);
			op1.setOperandType(Constants.IS_OBJECT_REF);
			op1.setOperandValue(funcCall);
			// op3.setOperandValue(funcCall);
			// opStack.push(op3);
		}
		/*
		 * if(isNextInstructionInvokeVirtual(info[i+1]) ||
		 * isNextInstructionInvokeSpecial(info[i+1]) ||
		 * isNextInstructionStore(info[i+1]) || isNextInstructionIf(info[i+1])) {
		 * //TODO need to check for other cases like switch /*java.lang.String
		 * Temp=op1.getOperandValue().toString();
		 * 
		 * Temp+=";\n"; op1.setOperandValue(Temp); opStack.push(op1); } else {
		 * /*if(info[i+1] == JvmOpCodes.ATHROW && behaviour.getParentBehaviour() !=
		 * null) { // Can this be removed
		 * //behaviour.getParentBehaviour().getOpStack().push(op1);
		 * //behavior.appendToBuffer( funcCall+";\n"; } else {
		 * 
		 * opStack.push(op1); // Commented by belurs // if(!codeStmtFormed) //
		 * behavior.appendToBuffer( Util.formatDecompiledStatement(funcCall); }
		 */
		opvalue = (java.lang.String) op1.getOperandValue();
		opvalue = opvalue.trim();
		if (opvalue.endsWith(";")) {
			opvalue = opvalue.substring(0, opvalue.lastIndexOf(";"));
		}

		op1.setOperandValue(opvalue);
		opvalue = (java.lang.String) op1.getOperandValue();
		if (opvalue.startsWith("\n")) {
			opvalue = opvalue.trim();
			op1.setOperandValue(opvalue);
		}
		/*
		 * v=op1.getOperandValue(); bb=new StringBuffer("");
		 * Util.checkForImport(v,bb); // op1.setOperandValue(bb.toString());
		 */
		if (getStoreFinder().isInstStore0(i + 1)) {
			pushed = true;
			opStack.push(op1);
			op1.setClassType(pushStr);
		} else if (getGenericFinder().isNextInstructionInvokeStatic(
				(info[i + 1]))
				|| getGenericFinder().isNextInstructionInvokeVirtual(
						info[i + 1])
				|| getGenericFinder().isNextInstructionInvokeInterface(
						info[i + 1])
				|| getGenericFinder().isNextInstructionInvokeSpecial(
						info[i + 1])
				|| getStoreFinder().isNextInstructionStore(i + 1)
				|| getBranchFinder().isNextInstructionIf(info[i + 1])
				|| (info[(i + 1)] == JvmOpCodes.PUTFIELD)
				|| (info[(i + 1)] == JvmOpCodes.PUTSTATIC)) { // TODO need to
			// check for
			// other cases
			// like switch
			if (RET.equalsIgnoreCase("V") == false) {

				pushed = true;
				opStack.push(op1);
				op1.setClassType(pushStr);

			} else {
				tempString = Util.formatDecompiledStatement("\n" + funcCall
						+ ";\n");
				behavior.appendToBuffer( tempString);
			}
		} else if (getLoadFinder().isNextInstructionLoad(i + 1)) {
			if (RET.equalsIgnoreCase("V") == true) {
				tempString = Util.formatDecompiledStatement("\n" + funcCall
						+ ";\n");
				behavior.appendToBuffer( tempString);
			} else {
				pushed = true;
				opStack.push(op1);
				op1.setClassType(pushStr);
				/*
				 * tempString=Util.formatDecompiledStatement(funcCall+";\n");
				 * behavior.appendToBuffer( tempString;
				 */
			}

		} // ?
		else if (getGenericFinder().isNextInstructionPop(i + 1)
				|| getBranchFinder().isNextInstructionReturn(i + 1)) {

			tempString = Util
					.formatDecompiledStatement("\n" + funcCall + ";\n");
			behavior.appendToBuffer( tempString);
			opStack.push(op1);
			op1.setClassType(pushStr);

		} else if (getGenericFinder().isNextInstructionConversionInst(
				info[i + 1])) {
			pushed = true;
			opStack.push(op1);
			op1.setClassType(pushStr);
		}

		else if (DecompilerHelper.checkForValueReturn(info, (i + 1))) {
			pushed = true;
			opStack.push(op1);
			op1.setClassType(pushStr);
		} else if (getGenericFinder().checkForSomeSpecificInstructions(info,
				(i + 1))) {
			pushed = true;
			opStack.push(op1);
			op1.setClassType(pushStr);
		} else {
			// //opStack.push(op1);
			if (RET.equalsIgnoreCase("V") == false) {
				opStack.push(op1);
				op1.setClassType(pushStr);
				pushed = true;
			} else {
				tempString = Util.formatDecompiledStatement("\n" + funcCall
						+ ";\n");
				behavior.appendToBuffer( tempString);
			}
		}

		if (pushed) {
			boolean r = false;// checkIFLoadInstIsPartOFTernaryCond(currentForIndex);
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

		GlobalVariableStore.setNewfound(newfound);
		GlobalVariableStore.setDoNotPop(doNotPop);
		
	}

}
