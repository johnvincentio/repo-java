package net.sf.jdec.jvminstructions.commands;

import java.util.ArrayList;

import net.sf.jdec.constantpool.ClassDescription;
import net.sf.jdec.constantpool.ClassInfo;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Util;

public class CheckcastCommand extends AbstractInstructionCommand {

	public CheckcastCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 2;
	}

	public void execute() {

		int i = getCurrentInstPosInCode();
		byte[] info = getContext().getCode();
		OperandStack stack = getContext().getOpStack();
		Operand op = (Operand) stack.pop();
		int classIndex = getGenericFinder().getOffset(i);
		ClassDescription cd = getContext().getClassRef().getCd();
		ClassInfo cinfo = cd.getClassInfoAtCPoolPosition(classIndex);
		Object o = op.getOperandValue();
		op = new Operand(); //
		java.lang.String v = cd.getUTF8String(cinfo.getUtf8pointer()).replace(
				'/', '.');
		if (v.startsWith("[")) {
			Util.parseReturnType(v);
			ArrayList list = Util.getreturnSignatureAsList();
			if (list != null && list.size() > 0) {
				v = (java.lang.String) list.get(0);
			}
		}
		java.lang.String type = "";
		java.lang.String castedRef = "";
		int br = v.indexOf("[");
		if (br != -1) {
			type = v.substring(0, br).trim();
			java.lang.String bracks = v.substring(br);
			StringBuffer sb = new StringBuffer("");
			Util.checkForImport(type, sb);
			castedRef = "(" + sb.toString() + " " + bracks + ")" + o.toString();
			op.setClassType(type);
		} else {
			StringBuffer sb = new StringBuffer("");
			Util.checkForImport(v, sb);
			castedRef = "(" + sb.toString() + ")" + o.toString();
			op.setClassType(v);
		}

		op.setOperandValue(castedRef);
		boolean r = false;// checkIFLoadInstIsPartOFTernaryCond(getCurrentInstPosInCode());
		if (r) {
			if (stack.size() > 0) {
				java.lang.String str = stack.getTopOfStack().getOperandValue();
				str = str + op.getOperandValue();
				op.setOperandValue(str);
			}
		}
		stack.push(op);
	}

}
