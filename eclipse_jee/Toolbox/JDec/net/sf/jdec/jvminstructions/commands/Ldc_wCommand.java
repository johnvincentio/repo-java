package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.commonutil.Numbers;
import net.sf.jdec.constantpool.ClassDescription;
import net.sf.jdec.constantpool.FloatPrimitive;
import net.sf.jdec.constantpool.IntPrimitive;
import net.sf.jdec.constantpool.CPString;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.ui.util.UIUtil;
import net.sf.jdec.util.Constants;
import net.sf.jdec.util.Util;

public class Ldc_wCommand extends AbstractInstructionCommand {

	public Ldc_wCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 2;
	}

	public void execute() {
		int i = getCurrentInstPosInCode();
		int opValueI = getGenericFinder().getOffset(i);
		i += 2;
		ClassDescription cd = getContext().getClassRef().getCd();
		IntPrimitive constInt = null;
		FloatPrimitive constFloat = null;
		java.lang.String stringLiteral = "";
		int type = -1;
		constInt = cd.getINTPrimitiveAtCPoolPosition(opValueI);
		if (constInt == null) {
			constFloat = cd.getFloatPrimitiveAtCPoolPosition(opValueI);
			if (constFloat == null) {
				CPString constString = cd.getStringsAtCPoolPosition(opValueI);
				if(constString != null)
					stringLiteral = cd.getUTF8String(constString.getUtf8pointer());
				if (stringLiteral == null || stringLiteral.length() == 0) {
					// ERROR CONDITION
				} else {
					type = Constants.IS_OBJECT_REF;
				}
			} else {
				type = Constants.IS_CONSTANT_FLOAT;
			}
		} else {
			type = Constants.IS_CONSTANT_INT;
		}
		Operand op = new Operand();
		op.setOperandType(type);
		if (type == Constants.IS_CONSTANT_INT) {
			op.setOperandValue(new Integer(constInt.getValue()));
			op.setClassType("int");
		}
		if (type == Constants.IS_CONSTANT_FLOAT) {
			op.setOperandValue(constFloat.getValue() + "f ");
			op.setClassType("float");
		}
		if (type == Constants.IS_OBJECT_REF) {
			java.lang.String nonascii = UIUtil.getUIUtil()
					.getInterpretNonAscii();
			if (nonascii.equals("true")) {
				StringBuffer tp = new StringBuffer("");
				boolean sf = Numbers.shouldValueBeFormattedForNonAscii(stringLiteral,
						"String", tp);
				if (sf) {

					stringLiteral = Util.formatForUTF(stringLiteral, "String",
							tp);

				}
			} else {
				stringLiteral = Util.returnUnInterpretedNonAscii(stringLiteral);
			}
			op.setOperandValue("\"" + stringLiteral + "\"");
			op.setClassType("java.lang.String");
		}
		boolean r = false;//checkIFLoadInstIsPartOFTernaryCond(currentForIndex);
		OperandStack opStack = getStack();
		if (r) {
			if (opStack.size() > 0) {
				java.lang.String str = opStack.getTopOfStack()
						.getOperandValue();
				str = str + op.getOperandValue();
				op.setOperandValue(str);
			}
		}
		opStack.push(op);
	}

}
