package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.constantpool.ClassDescription;
import net.sf.jdec.constantpool.DoublePrimitive;
import net.sf.jdec.constantpool.LongPrimitive;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Constants;

public class Ldc2_wCommand extends AbstractInstructionCommand {

	public Ldc2_wCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 2;
	}

	public void execute() {
		int i = getCurrentInstPosInCode();
		int opValueI = getGenericFinder().getOffset(i);
		i += 2;
		LongPrimitive constLong = null;
		DoublePrimitive constDouble = null;
		int type = -1;
		ClassDescription cd = getContext().getClassRef().getCd();
		constLong = cd.getLongPrimitiveAtCPoolPosition(opValueI);
		if (constLong == null) {
			constDouble = cd.getDoublePrimitiveAtCPoolPosition(opValueI);
			if (constDouble == null) {
				// ERROR CONDITION
			} else {
				type = Constants.IS_CONSTANT_DOUBLE;
			}
		} else {
			type = Constants.IS_CONSTANT_LONG;
		}
		Operand op = new Operand();
		op.setOperandType(type);
		if (type == Constants.IS_CONSTANT_DOUBLE) {
			op.setOperandValue(new Double(constDouble.getValue()));
		}
		if (type == Constants.IS_CONSTANT_LONG) {
			op.setOperandValue(new Long(constLong.getValue()) + "L ");
		}
		OperandStack opStack = getStack();
		boolean r = false;// checkIFLoadInstIsPartOFTernaryCond(currentForIndex);
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
