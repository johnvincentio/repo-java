package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.config.Configuration;
import net.sf.jdec.constantpool.ClassDescription;
import net.sf.jdec.constantpool.ClassInfo;
import net.sf.jdec.core.DecompilerHelper;
import net.sf.jdec.core.GlobalVariableStore;
import net.sf.jdec.core.LocalVariableHelper;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Constants;
import net.sf.jdec.util.Util;

import java.util.Stack;

public class NewCommand extends AbstractInstructionCommand {

	public NewCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 2;
	}

	public void execute() {

		boolean nextisnew = true;
		Stack newfoundstack = GlobalVariableStore.getNewfoundstack();
		boolean newfound = GlobalVariableStore.isNewfound();
		byte[] info = getCode();
		int currentForIndex = getCurrentInstPosInCode();
		nextisnew = DecompilerHelper.isNewFollowedByNew(info, currentForIndex); // returns
																				// false
		// for invoke
		Behaviour behavior = getContext();
		boolean prevNew = (newfound()) ? true : false;
		newfound = true;
		int i = getCurrentInstPosInCode();
		int newpos = i;
		int classIndex = getGenericFinder().getOffset(i);
		i += 2;
		ClassDescription cd = getContext().getClassRef().getCd();
		ClassInfo cinfo = cd.getClassInfoAtCPoolPosition(classIndex);
		Operand op = new Operand();
		java.lang.String type = cd.getUTF8String(cinfo.getUtf8pointer())
				.replace('/', '.');
		java.lang.String qType = type;
		DecompilerHelper.registerInnerClassIfAny(type.replace('.', '/'));
		op.setOperandType(Constants.IS_OBJECT_REF);
		java.lang.String Reference = "JdecGenerated" + i;
		java.lang.String newTemp = "";
		OperandStack opStack = getStack();
		boolean sz = DecompilerHelper.checkForSizeOfArrayTimesStack();
		StringBuffer fulltype = new StringBuffer();
		if (Configuration.getShowImport().equals("false")
				&& (!nextisnew && !sz && !prevNew))
			newTemp = type + " " + Reference + " = new " + type.trim() + "(";
		if (Configuration.getShowImport().equals("true")
				&& (!nextisnew && !sz && !prevNew)) {
			java.lang.String fullName = type;
			java.lang.String simpleName = "";
			int lastdot = fullName.lastIndexOf(".");
			boolean addim=false;
			if (lastdot != -1) {
				simpleName = fullName.substring(lastdot + 1);
				type = simpleName;
				addim = true;
				boolean present = LocalVariableHelper.simpleTypeAlreadyAddedToImports(simpleName,fulltype);
				if(present){
					if(fulltype.toString().trim().equals(fullName.trim())){
						addim = true;
					}
					else{
						addim = false;
						type = fullName;
					}
				}
			}
			if(addim){
				ConsoleLauncher.addImportClass(fullName);
			}
			newTemp = type + " " + Reference + " = new " + type.trim() + "(";
		}
		if (Configuration.getShowImport().equals("false")
				&& (nextisnew || sz || prevNew))
			newTemp = "new " + type.trim() + "(";
		if (Configuration.getShowImport().equals("true")
				&& (nextisnew || sz || prevNew)) {
			java.lang.String fullName = type;
			java.lang.String simpleName = "";
			int lastdot = fullName.lastIndexOf(".");
			if (lastdot != -1) {
				boolean addim=false;
				simpleName = fullName.substring(lastdot + 1);
				type = simpleName;
				addim = true;
				fulltype = new StringBuffer();
				boolean present = LocalVariableHelper.simpleTypeAlreadyAddedToImports(simpleName,fulltype);
				if(present){
					if(fulltype.toString().trim().equals(fullName.trim())){
						addim = true;
					}
					else{
						addim = false;
						type = fullName;
					}
				}
				if(addim){
					ConsoleLauncher.addImportClass(fullName);
				}
			}
			newTemp = " new " + type.trim() + "(";
		}
		newTemp = newTemp.trim();
		if (!DecompilerHelper.checkForSizeOfArrayTimesStack()) {
			boolean newinternarycond = false;// checkIFNewIsPartOFTernaryCond(currentForIndex);
			if (!prevNew)// && !checkForSizeOfArrayTimesStack()) // Removed
			// && !nextisnew from condition
			{
				if (newTemp.indexOf("=") == -1)
					newTemp = type + " " + Reference + " = " + newTemp;
				behavior.appendToBuffer( "\n"
						+ Util.formatDecompiledStatement(newTemp)); // uncommented
				op.setOperandValue(Reference);
			} else if (newinternarycond && prevNew) {
				op.setOperandValue(newTemp);
			} else {
				op.setOperandValue(newTemp);
			}
			op.setClassType(qType);
			opStack.push(op);
		} else {
			int brk = newTemp.indexOf("(");
			// doNotPop=true;

			if (brk != -1) {
				newTemp = newTemp.substring(0, brk);
			}
			Util.forceStartSpace = false;
			/*
			 * if(newfound()==false)
			 * codeStatements+=Util.formatDecompiledStatement(newTemp); else{
			 */
			op = createOperand(newTemp + "(");
			opStack.push(op);
			// }

			/*
			 * if(arraytimesstack.size() > 0) {
			 * arraytimespush=Integer.parseInt(arraytimesstack.pop().toString());
			 * arraytimespush--; if(arraytimespush==0) { } else {
			 * arraytimesstack.push(""+arraytimespush); } }
			 */

		}
		/*
		 * r=checkIFLoadInstIsPartOFTernaryCond(currentForIndex); if(r) {
		 * if(opStack.size() > 0) { java.lang.String
		 * str1=opStack.getTopOfStack().getOperandValue(); java.lang.String
		 * str2=opStack.getTopOfStack().getOperandValue(); java.lang.String
		 * str3=str2+str1; Operand d2=createOperand(str3); opStack.push(d2); } }
		 */
		newfoundstack.push("new");
		GlobalVariableStore.setNewfound(newfound);
		
	}

}
