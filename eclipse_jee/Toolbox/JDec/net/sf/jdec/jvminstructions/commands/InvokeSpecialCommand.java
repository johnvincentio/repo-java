package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.constantpool.ClassDescription;
import net.sf.jdec.constantpool.MethodRef;
import net.sf.jdec.constantpool.NameAndType;
import net.sf.jdec.core.*;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Constants;
import net.sf.jdec.util.Util;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Hashtable;
import java.util.Stack;

public class InvokeSpecialCommand extends AbstractInstructionCommand {

	public InvokeSpecialCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 2;
	}

	public void execute() {
		// TODO:
		/***********************************************************************
		 * 1>check when result has to be pushed and when just printed and when
		 * both 2>In case of method calls and b is not null check whether this
		 * has to be added or super (no other case possible ? ) 3> Need to
		 * handle new instruction properly
		 */
		int dex;
		boolean newfound = GlobalVariableStore.isNewfound();
		boolean previnstwasinvoke = false;
		int currentForIndex = getCurrentInstPosInCode();
		int itemp = currentForIndex - 1;
		boolean pushed = false;
		Behaviour behavior = getContext();
		boolean doNotPop = GlobalVariableStore.isDoNotPop();
		java.lang.String RET = "";
		boolean appendToCodeStmt = false;
		Operand invokingObjectRef = null;
		boolean prevInstInvokeVirtual = GlobalVariableStore.isPrevInstInvokeVirtual();
		String funcCall = "";
		Operand op2=null;
		Operand op1=null;
		String tempString = "";
		Stack newfoundstack=GlobalVariableStore.getNewfoundstack();
		Stack arraytimesstack = GlobalVariableStore.getArraytimesstack();
		/*
		 * temp1=info[++i]; temp2=info[++i]; classIndex=((temp1 << 8) | temp2);
		 */
		// if(classIndex < 0)classIndex=(temp1+1)*256-Math.abs(temp2);
		int i = getCurrentInstPosInCode();
		int classIndex = getGenericFinder().getOffset(i);
		i += 2;
		ClassDescription cd = getContext().getClassRef().getCd();
		MethodRef mref = cd.getMethodRefAtCPoolPosition(classIndex);
		boolean constructor = mref.isMethodRFefForAConstructor();
		boolean behaviourisconstructor = getContext().isMethodConstructor();
		//
		// mref.isMethodRFefForAConstructor();
		// if(mref.isMethodRFefForAConstructor())
		// {
		byte[] info = getCode();
		OperandStack opStack = getStack();
		do {
			if (itemp >= 0
					&& getGenericFinder().isThisInstrStart( itemp)) {
				if (info[itemp] == JvmOpCodes.INVOKESPECIAL) {
					previnstwasinvoke = true;
					break;
				} else if (info[itemp] == JvmOpCodes.NEW) {
					previnstwasinvoke = false;
					break;
				} else {
					itemp--;
				}
			} else {
				itemp--;
			}
		} while (true && itemp >= 0);
		// }
		java.lang.String classtype = mref.getClassname();
		NameAndType ninfo = cd.getNameAndTypeAtCPoolPosition(mref.getDescriptionPointer());
		Hashtable methodLookUp = cd.getMethodLookUp();
		Behaviour b = (Behaviour) methodLookUp.get(mref.getKey());

		boolean argumentReturnTypeChecked = false;
		String typeofmet = mref.getTypeofmethod();
		Util.parseDescriptor(typeofmet);
		ArrayList paramlist = Util.getParsedSignatureAsList();
		
		boolean takeret = getStoreFinder().isNextInstAStore(currentForIndex + 3);
		int s1 = typeofmet.indexOf(")");
		ArrayList		returntype = null;
		if (s1 != -1 && s1 + 1 < typeofmet.length()) {

			String rettp = typeofmet.substring(s1 + 1);

			Util.parseReturnType(rettp);
			returntype = Util.getreturnSignatureAsList();
		}
		String pushStr = classtype;
		if (returntype != null && returntype.size() > 0) {
			pushStr = (java.lang.String) returntype.get(0);
		}
		if (pushStr != null && pushStr.trim().equalsIgnoreCase("void")) {
			pushStr = classtype;
		}
		DecompilerHelper.resetMethodParameters(opStack, paramlist, currentForIndex);
		int br = typeofmet.indexOf(")");
		if (br != -1) {
			char c;
			if (typeofmet.length() >= (br + 1))
				c = typeofmet.charAt(br + 1);
			else
				c = '?';
			RET = "" + c;

		}
		String argumentRetType = "";
		if (b != null) {
			argumentRetType = b.getReturnType();
			op1 = new Operand();
			// op1.setCategory(Constants.CATEGORY1);
			op1.setOperandType(Constants.IS_OBJECT_REF);

			int numParams = b.getMethodParams().length;
			if (!constructor)
				funcCall = b.getBehaviourName() + "(";
			if (numParams == 0) {// /
				funcCall += ")";
				op2 = (Operand) opStack.pop();
				invokingObjectRef = op2;
			} else {
				Operand[] oparr = new Operand[numParams];
				int opArrIndx = numParams - 1;
				dex = 0;
				for (int indx = numParams - 1; indx >= 0; indx--) {

					op2 = (Operand) opStack.pop();
					DecompilerHelper.adjustBracketCount(op2);
					DecompilerHelper.resetOperandValueIfNecessary(paramlist, indx, op2);
					oparr[dex++] = op2;
					opArrIndx--;

				}
				try {
					Operand objRef = (Operand) opStack.pop();
					invokingObjectRef = objRef;
				} catch (EmptyStackException ese) {
					invokingObjectRef = new Operand();
					invokingObjectRef.setOperandValue("<UNKNOWN_OBJCT_REF>");
				}

				/*
				 * if(constructor) { funcCall+="("; }
				 */

				for (int indx = numParams - 1; indx >= 0; indx--) {
					op2 = oparr[indx];
					if (indx > 0) {
						funcCall += op2.getOperandValue() + ",";

					} else {
						funcCall += op2.getOperandValue() + ")";
					}
				}

			}
			java.lang.String ThisClassName = this.getContext().getDeclaringClass(); // Class
																					// getting
																					// decompiled
			java.lang.String methodName = mref.getMethodName(); // Method being
																// invoked
			java.lang.String declaringClassname = mref.getClassname(); // Class
																		// declaring
																		// the
																		// method
																		// being
																		// invoked
			java.lang.String superClassName = cd.getSuperClassName();

			if (methodName.equals(declaringClassname)
					&& declaringClassname.replace('/', '.').equals(
							cd.getSuperClassName()) && behaviourisconstructor) {
				invokingObjectRef.setOperandValue("super");
				op1.setOperandValue(invokingObjectRef.getOperandValue()
						+ funcCall);
				funcCall = op1.getOperandValue();
			} else if (methodName.equals(declaringClassname)
					&& !declaringClassname.replace('/', '.').equals(
							cd.getSuperClassName().replace('/', '.'))
					&& behaviourisconstructor) {
				invokingObjectRef.setOperandValue("this(");
				op1.setOperandValue(invokingObjectRef.getOperandValue()
						+ funcCall);
				funcCall = op1.getOperandValue();
			}
			// Handle Case of super.<someMethod> Here
			else if (declaringClassname.replace('/', '.').equals(
					cd.getSuperClassName())
					&& !constructor) {
				invokingObjectRef.setOperandValue("super");
				if (!constructor)
					funcCall = "super." + funcCall;
				else
					funcCall = "super." + mref.getMethodName() + funcCall;
				op1.setOperandValue(funcCall);

			}

			else if (declaringClassname.replace('/', '.').equals(
					cd.getSuperClassName()) == false
					&& declaringClassname.replace('/', '.').equals(
							ThisClassName) == false && constructor) {
				invokingObjectRef.setOperandValue("super");
				if (!constructor)
					funcCall = "super." + funcCall;
				else
					funcCall = "super." + mref.getMethodName() + funcCall;
				op1.setOperandValue(funcCall);

			}
			/*
			 * if(methodName.equals(declaringClassname) &&
			 * declaringClassname.equals(ThisClassName)) { // Constructor Call
			 * /*Operand objRef = (Operand)opStack.pop();
			 * invokingObjectRef=objRef;
			 * if(superClassName.equals(declaringClassname) && !newfound()) {
			 * invokingObjectRef.setOperandValue("super");
			 * 
			 * 
			 * op1.setOperandValue(invokingObjectRef.getOperandValue()); } }
			 * else if(superClassName.equals(declaringClassname) && !newfound()) {
			 * invokingObjectRef.setOperandValue("super");
			 * funcCall=invokingObjectRef.getOperandValue()+"."+funcCall;
			 * op1.setOperandValue(funcCall); }
			 */
			
			else if(methodName.equals(getContext().getBehaviourName()) && !constructor){
				invokingObjectRef.setOperandValue("super");
				funcCall = "super." + funcCall;
			}
			else {
				if (!constructor)
					op1.setOperandValue(invokingObjectRef.getOperandValue()
							+ "." + funcCall);
				else if (constructor
						&& (DecompilerHelper.checkForSizeOfArrayTimesStack() || DecompilerHelper.prevNewPresent())) {
					op1.setOperandValue(invokingObjectRef.getOperandValue()
							+ funcCall);
				} else {
					if (funcCall.endsWith(")")
							&& funcCall.startsWith("(") == false) {
						// funcCall="("+funcCall;
					}
					op1.setOperandValue(funcCall);
				}
			}

		} else {

			java.lang.String methodSignature = mref.getTypeofmethod();
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
			java.lang.String[] funcArray = new java.lang.String[numberOfParameters];
			java.lang.String ThisClassName = this.getContext().getDeclaringClass(); // Class
																					// getting
																					// decompiled
			java.lang.String declaringClassname = mref.getClassname(); // Class
																		// declaring
																		// the
																		// method
																		// being
																		// invoked
			java.lang.String methodName = mref.getMethodName(); // Method being
																// invoked

			int index = 0;
			int funcArrayIndex = 0;
			dex = 0;
			for (int indx = numberOfParameters - 1; indx >= 0; indx--) {
				op2 = (Operand) opStack.pop();
				DecompilerHelper.adjustBracketCount(op2);
				DecompilerHelper.resetOperandValueIfNecessary(paramlist, indx, op2);
				funcArray[dex++] = (op2.getOperandValue()).toString();
				funcArrayIndex++;
			}

			boolean appendRef = false;

			// FIX ME: super class is not being traced properly
			// isSuperClass(declaringClassname,cd.getSuperClassName());
			// Removed methodName.equals(declaringClassname)
			if (getContext().isMethodConstructor()
					&& declaringClassname.replace('/', '.').equals(
							cd.getSuperClassName()) && opStack.size() > 0)// && constructor)
			{
				if (methodName.equals(declaringClassname) == true) {
					op2 = (Operand) opStack.pop();
					funcCall = "super(";
					appendToCodeStmt = true;
				} else {
					op2 = (Operand) opStack.pop();
					funcCall = "super." + mref.getMethodName() + "(";
					appendToCodeStmt = true;
				}
			}
			// Handle Case of super.<someMethod> Here
			else if (declaringClassname.replace('/', '.').equals(
					cd.getSuperClassName())
					&& !constructor) {

				op2 = (Operand) opStack.pop();
				funcCall = "super." + mref.getMethodName() + "(";
				appendToCodeStmt = true;

			}

			else if (declaringClassname.replace('/', '.').equals(
					cd.getSuperClassName()) == false
					&& declaringClassname.replace('/', '.').equals(
							ThisClassName) == false && !constructor) {

				op2 = (Operand) opStack.pop();
				funcCall = "super." + mref.getMethodName() + "(";
				appendToCodeStmt = true;

			}

			else if (newfound() || previnstwasinvoke) {
				appendToCodeStmt = true;
				op2 = (Operand) opStack.pop();
				appendRef = true;

			} else {

				appendToCodeStmt = true;
				op2 = (Operand) opStack.pop();
				appendRef = true;
			}

			for (int indx = funcArray.length - 1; indx >= 0; indx--) {
				if (indx != 0) {
					funcCall += funcArray[indx] + ",";
				} else {
					funcCall += funcArray[indx];
				}

			}

			if (appendRef && !constructor)
				funcCall = op2.getOperandValue() + funcCall + ")"; // FIXME:
																	// Dont Do
																	// it For
																	// Constructor
																	// Calls
			else if (appendRef && constructor
					&& (DecompilerHelper.checkForSizeOfArrayTimesStack() || DecompilerHelper. 
							prevNewPresent())) {
				funcCall = op2.getOperandValue() + funcCall + ")";
			} else
				funcCall += ")";

			op1 = new Operand();
			op1.setOperandValue(funcCall);

		}

		boolean prevwasnew = DecompilerHelper.prevNewPresent();
		StringBuffer dummy = new StringBuffer("");
		if (newfound() && !previnstwasinvoke && !prevwasnew) {
			if (!DecompilerHelper.checkForSizeOfArrayTimesStack()) {
				if (constructor) {
					tempString = funcCall + ";\n";
					Util.newlinebeg = false;
					behavior.appendToBuffer( Util
							.formatDecompiledStatement(tempString));
				} else {
					opStack.push(op1);
					pushed = true;
				}

			} else {
				java.lang.String tentry = (java.lang.String) arraytimesstack
						.peek();
				int number = Integer.parseInt(tentry);

				if (number == 1) {
					tempString = funcCall + "};\n";
				} else {
					tempString = funcCall + ",";
				}
				Util.forceNewLine = false;
				Util.forceStartSpace = false;
				Util.newlinebeg = false;
				behavior.appendToBuffer( Util.formatDecompiledStatement(tempString));
				doNotPop = true;
			}

		} else if (newfound() && !previnstwasinvoke && prevwasnew) {
			pushed = true;
			opStack.push(op1);
		} else if (newfoundstack.size() == 1 && previnstwasinvoke) {

			if (!DecompilerHelper.checkForSizeOfArrayTimesStack()) {
				if (constructor) {
					if (funcCall.endsWith(")")
							&& funcCall.startsWith("(") == false
							&& behavior.getCodeAsBuffer().toString().endsWith("(") == false) {
						// funcCall="("+funcCall;
					}
					tempString = funcCall + ";\n";
					Util.newlinebeg = false;
					behavior.appendToBuffer( Util
							.formatDecompiledStatement(tempString));
				} else {
					pushed = true;
					opStack.push(op1);
				}
			} else {

				java.lang.String tentry = (java.lang.String) arraytimesstack
						.peek();
				int number = Integer.parseInt(tentry);

				if (number == 1) {

					tempString = funcCall + "};\n";
				} else {
					tempString = funcCall + ",";
				}
				Util.forceNewLine = false;
				Util.forceStartSpace = false;
				behavior.appendToBuffer( Util.formatDecompiledStatement(tempString));
				doNotPop = true;
			}

		}

		else if (getStoreFinder().isInstStore0(i + 1)) {
			if (!newfound() && !previnstwasinvoke) {
				opStack.push(op1);
				op1.setClassType(pushStr);
				pushed = true;
			}
		} else if (getGenericFinder().isNextInstructionInvokeStatic((info[i + 1]))
				|| getGenericFinder().isNextInstructionInvokeVirtual(info[i + 1])
				|| getGenericFinder().isNextInstructionInvokeInterface(info[i + 1])
				|| getGenericFinder().isNextInstructionInvokeSpecial(info[i + 1])
				|| getStoreFinder().isNextInstructionStore(i + 1)
				|| getBranchFinder().isNextInstructionIf(info[i + 1])) {
			if (RET.equalsIgnoreCase("V") == false) {
				if (newfound())// || previnstwasinvoke)
				{
					op1 = opStack.peekTopOfStack();
				}
				pushed = true;
				opStack.push(op1);
				op1.setClassType(pushStr);
			} else {
				boolean n = false;
				if (!newfound() && !previnstwasinvoke) {
					tempString = Util.formatDecompiledStatement(funcCall
							+ ";\n");
					behavior.appendToBuffer( tempString);
					n = true;
				}
				if (previnstwasinvoke) {

					n = true;
					opStack.push(op1);
					op1.setClassType(pushStr);
					pushed = true;
				}
				/*
				 * if(!n) { if(newfound)// || previnstwasinvoke) {
				 * op1=opStack.peekTopOfStack(); } opStack.push(op1);
				 * op1.setClassType(pushStr); }
				 */
			}
		} else if ((info[(i + 1)] == JvmOpCodes.PUTFIELD)
				|| (info[(i + 1)] == JvmOpCodes.PUTSTATIC)) {
			if (newfound())// || previnstwasinvoke) // TODO: test invokespecial
							// tho
			{
				op1 = opStack.peekTopOfStack();
			}
			opStack.push(op1);
			op1.setClassType(pushStr);
			pushed = true;
		} else if (getLoadFinder().isNextInstructionLoad(i + 1)) {

			if (RET.equalsIgnoreCase("V") == true) {
				boolean n = false;
				// Uncommented testcase-->e:\testcases\invokesptest.class

				/*
				 * if(!newfound() && !previnstwasinvoke){
				 * tempString=Util.formatDecompiledStatement(funcCall+";\n");
				 * behavior.appendToBuffer( tempString; n=true; } if(previnstwasinvoke) {
				 * //op1=opStack.peekTopOfStack(); n=true; pushed=true;
				 * opStack.push(op1); op1.setClassType(pushStr); }
				 */
				tempString = Util.formatDecompiledStatement(funcCall + ";\n");
				behavior.appendToBuffer( tempString);
				n = true;

			} else

			{
				if (newfound())// || previnstwasinvoke)
				{
					op1 = opStack.peekTopOfStack();
				}
				pushed = true;
				opStack.push(op1);
				op1.setClassType(pushStr);

			}

		} else if (getGenericFinder().isNextInstructionPop(i + 1)
				|| getBranchFinder().isNextInstructionReturn(i + 1)) {
			boolean n = false;
			boolean skipr = false;

			if (getGenericFinder().isThisInstrStart(
					(i + 2))) {

				if (info[(i + 2)] == JvmOpCodes.GETSTATIC) {
					opStack.push(op1);
					op1.setClassType(pushStr);
					skipr = true;
					pushed = true;

				}

			}

			if (!newfound() && !previnstwasinvoke && !skipr) {
				tempString = Util.formatDecompiledStatement(funcCall + ";\n");
				behavior.appendToBuffer( tempString);
				n = true;
			}
			if (previnstwasinvoke && !skipr) {
				// op1=opStack.peekTopOfStack();
				if (!constructor && !newfound) {
					tempString = Util.formatDecompiledStatement(funcCall
							+ ";\n");
					behavior.appendToBuffer( tempString);
					n = true;
				} else {
					if (getBranchFinder().isNextInstructionReturn(i + 1) == false) {
						n = true;
						pushed = true;
						opStack.push(op1);
						op1.setClassType(pushStr);
					} else {
						tempString = Util.formatDecompiledStatement(funcCall
								+ ";\n");
						behavior.appendToBuffer( tempString);
						n = true;
					}
				}
			}
			if (!n && !skipr) {
				if (newfound())// || previnstwasinvoke)
				{
					op1 = opStack.peekTopOfStack();
				}
				pushed = true;
				opStack.push(op1);
				op1.setClassType(pushStr);
			}

		} else if (getGenericFinder().isNextInstructionConversionInst(info[i + 1])) {
			if (newfound())// || previnstwasinvoke)
			{
				op1 = opStack.peekTopOfStack();
			}
			pushed = true;
			opStack.push(op1);
			op1.setClassType(pushStr);
		}

		else if (DecompilerHelper.checkForValueReturn(info, (i + 1))) {
			if (newfound())// || previnstwasinvoke)
			{
				op1 = opStack.peekTopOfStack();
			}
			pushed = true;
			opStack.push(op1);
			op1.setClassType(pushStr);
		} else if (getGenericFinder().checkForSomeSpecificInstructions(info,(i + 1))) {
			if (newfound())// || previnstwasinvoke)
			{
				op1 = opStack.peekTopOfStack();
			}
			pushed = true;
			opStack.push(op1);
			op1.setClassType(pushStr);

		}

		else if (getGenericFinder().isInstAnyBasicPrimitiveOperation( (i + 1), dummy)
				|| (info[i + 1] == JvmOpCodes.GETFIELD || info[i + 1] == JvmOpCodes.GETSTATIC)) {
			if (newfound())// || previnstwasinvoke)
			{
				op1 = opStack.peekTopOfStack();
			}
			pushed = true;
			opStack.push(op1);
			op1.setClassType(pushStr);
		} else if (mref.isMethodRFefForAConstructor()) {
			pushed = true;
			opStack.push(op1);
			op1.setClassType(pushStr);
		} else if (!mref.isMethodRFefForAConstructor()) {
			behavior.appendToBuffer( Util.formatDecompiledStatement(op1
					.getOperandValue()
					+ ";\n"));
		} else if (previnstwasinvoke) // TODO IS THIS REQD ???
		{
			if (opStack.size() > 1) {
				Operand temp1 = opStack.peekTopOfStack();
				if (temp1.getOperandValue().equals(op2.getOperandValue())) {
					opStack.pop();
				}

			}
		}

		else {
			if (!newfound() && !previnstwasinvoke) {
				tempString = Util.formatDecompiledStatement(funcCall + ";\n");
				behavior.appendToBuffer( tempString);
			} else {
				Operand opd = new Operand();
				opd.setOperandValue(funcCall);
				if (newfound()) {
					Operand OP = opStack.peekTopOfStack();
					if (OP != null) {
						opd.setOperandValue(OP.getOperandValue());
					}
				}

			}
		}

		if (newfoundstack.size() > 0 && constructor) // FIXME: Do it For
														// Constructor Calls
														// Only
			newfoundstack.pop();

		if (pushed && constructor) {
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
		
		GlobalVariableStore.setNewfound(newfound);
		GlobalVariableStore.setDoNotPop(doNotPop);
		GlobalVariableStore.setPrevInstInvokeVirtual(prevInstInvokeVirtual);
		
	}

}
