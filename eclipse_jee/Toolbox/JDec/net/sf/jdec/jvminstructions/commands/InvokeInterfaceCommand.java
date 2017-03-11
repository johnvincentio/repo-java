package net.sf.jdec.jvminstructions.commands;

import java.util.ArrayList;

import net.sf.jdec.constantpool.ClassDescription;
import net.sf.jdec.constantpool.ClassInfo;
import net.sf.jdec.constantpool.InterfaceMethodRef;
import net.sf.jdec.constantpool.NameAndType;
import net.sf.jdec.core.DecompilerHelper;
import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Util;

public class InvokeInterfaceCommand extends AbstractInstructionCommand {

	public InvokeInterfaceCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 4;
	}

	public void execute() {
		boolean pushed = false;
		byte[] info = getCode();
		int i = getCurrentInstPosInCode();
		int currentForIndex = i;
		Behaviour behavior = getContext();
		int classIndex = getGenericFinder().getOffset(i);
		OperandStack opStack = getStack();
		i += 2;
		ClassDescription cd = getContext().getClassRef().getCd();
		InterfaceMethodRef iref = cd
				.getInterfaceMethodAtCPoolPosition(classIndex);
		java.lang.String classname = iref.getClassname();
		java.lang.String typeofmet = iref.getTypeofmethod();
		Util.parseDescriptor(typeofmet);
		ArrayList paramlist = Util.getParsedSignatureAsList();
		boolean takeret = getStoreFinder()
				.isNextInstAStore(currentForIndex + 5);
		int s1 = typeofmet.indexOf(")");
		ArrayList returntype = null;
		
		String tempString = "";
		java.lang.String rettp = "";
		if (s1 != -1 && s1 + 1 < typeofmet.length()) {

			rettp = typeofmet.substring(s1 + 1);

			Util.parseReturnType(rettp);
			returntype = Util.getreturnSignatureAsList();
		}
		java.lang.String pushStr = classname;
		if (takeret && returntype != null && returntype.size() > 0) {
			pushStr = (java.lang.String) returntype.get(0);
		}
		if (pushStr != null && pushStr.trim().equalsIgnoreCase("void")) {
			pushStr = classname;
		}

		DecompilerHelper.resetMethodParameters(opStack, paramlist, currentForIndex);
		java.lang.String argumentRetType = "";
		int br = typeofmet.indexOf(")");
		if (br != -1) {
			char c;
			if (typeofmet.length() >= (br + 1))
				c = typeofmet.charAt(br + 1);
			else
				c = '?';
			argumentRetType = "" + c;

		}
		int nargs = paramlist.size();
		ClassInfo clazz = cd
				.getClassInfoAtCPoolPosition(iref.getClassPointer());
		NameAndType nmtype = cd.getNameAndTypeAtCPoolPosition(iref
				.getDescriptionPointer());
		java.lang.String clazzName = cd.getUTF8String(clazz.getUtf8pointer());
		DecompilerHelper.registerInnerClassIfAny(clazzName.replace('.', '/'));
		java.lang.String description = cd
				.getUTF8String(nmtype.getUtf8pointer());
		int j = i + 1;
		// int nargs=info[j]-1;
		Operand allargs[] = new Operand[nargs];
		int start = 0;

		int dex = 0;
		for (int counter = nargs - 1; counter >= 0; counter--) {
			// boolean boolparam=isParameterTypeBoolean(paramlist,counter);
			Operand op2 = opStack.getTopOfStack();
			DecompilerHelper.resetOperandValueIfNecessary(paramlist, counter, op2);
			allargs[dex++] = op2;

		}
		Operand interfaceRef = opStack.getTopOfStack();
		java.lang.String args = "";
		java.lang.String bracket = "(";
		for (int c = (allargs.length - 1); c >= 0; c--) {
			args += allargs[c].getOperandValue();
			if (c != 0)
				args += ",";
		}
		if (args.length() > 0) {
			bracket += args + ")";
		} else
			bracket += ")";
		i++;
		i++;
		Operand op1 = new Operand();
		op1.setOperandValue(interfaceRef.getOperandValue() + "." + description
				+ bracket);

		op1.setClassType(pushStr);
		java.lang.String opvalue = (java.lang.String) op1.getOperandValue();
		if (opvalue.startsWith("\n")) {
			opvalue = opvalue.trim() + "\n";
			op1.setOperandValue(opvalue);
		}
		if (getStoreFinder().isInstStore0(i + 1)) {
			pushed = true;
			opStack.push(op1);
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

			if (argumentRetType.equalsIgnoreCase("V") == false
					|| argumentRetType.equalsIgnoreCase("void") == false) {
				pushed = true;
				opStack.push(op1);
				// op1.setClassType(pushStr);
			} else {
				tempString = interfaceRef.getOperandValue() + "." + description
						+ bracket + ";\n";
				behavior.appendToBuffer( Util.formatDecompiledStatement(tempString));
			}
		} else if (getLoadFinder().isNextInstructionLoad(i + 1)) {
			if (argumentRetType.equalsIgnoreCase("V") == true
					|| argumentRetType.equalsIgnoreCase("void") == true) {
				tempString = interfaceRef.getOperandValue() + "." + description
						+ bracket + ";\n";
				behavior.appendToBuffer( Util.formatDecompiledStatement(tempString));
			} else {
				pushed = true;
				opStack.push(op1);
				// op1.setClassType(pushStr);
			}

		} else if (getGenericFinder().isNextInstructionPop(i + 1)
				|| getBranchFinder().isNextInstructionReturn(i + 1)) {

			tempString = interfaceRef.getOperandValue() + "." + description
					+ bracket + ";\n";
			behavior.appendToBuffer( Util.formatDecompiledStatement(tempString));
			opStack.push(op1);

		} else if (getGenericFinder().isNextInstructionConversionInst(info[i + 1])) {
			pushed = true;
			opStack.push(op1);

		} else if (DecompilerHelper.checkForValueReturn(info, (i + 1))) {
			pushed = true;
			opStack.push(op1);

		} else if (getGenericFinder().checkForSomeSpecificInstructions(info, (i + 1))) {
			pushed = true;
			opStack.push(op1);
			// op1.setClassType(classname);
		} else {
			/*
			 * op1=new Operand();
			 * op1.setOperandValue(interfaceRef.getOperandValue()+"."+description+bracket+";\n");
			 * opStack.push(op1);
			 */
			if (argumentRetType.equalsIgnoreCase("V") == false
					&& argumentRetType.equalsIgnoreCase("void") == false) {
				opStack.push(op1);
				pushed = true;
				// op1.setClassType(classname);
			} else {
				tempString = interfaceRef.getOperandValue() + "." + description
						+ bracket + ";\n";
				behavior.appendToBuffer( Util.formatDecompiledStatement(tempString));
			}
		}

		// TODO check this condition whether it will apply
		// in all cases . Why only if ot store....Check
		/*
		 * if(isNextInstructionStore(info[i+1]) ||
		 * isNextInstructionIf(info[i+1])) { op1=new Operand();
		 * op1.setOperandValue(interfaceRef.getOperandValue()+"."+description+bracket+";\n");
		 * opStack.push(op1); } else if(info[i+1] == JvmOpCodes.POP) // TODO:
		 * Handle POP2 { op1=new Operand();
		 * op1.setOperandValue(interfaceRef.getOperandValue()+"."+description+bracket+";\n");
		 * opStack.push(op1);
		 * tempString=interfaceRef.getOperandValue()+"."+description+bracket+";\n";
		 * codeStatements+=Util.formatDecompiledStatement(tempString); } else
		 * if(info[i+1] == JvmOpCodes.CHECKCAST) // TODO: Handle POP2 { op1=new
		 * Operand();
		 * op1.setOperandValue(interfaceRef.getOperandValue()+"."+description+bracket+";\n");
		 * opStack.push(op1); } else {
		 * tempString=interfaceRef.getOperandValue()+"."+description+bracket+";\n";
		 * codeStatements+=Util.formatDecompiledStatement(tempString); }
		 */

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

		
	}

}
