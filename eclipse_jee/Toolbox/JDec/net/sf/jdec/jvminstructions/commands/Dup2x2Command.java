package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.core.GlobalVariableStore;
import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;

public class Dup2x2Command extends AbstractInstructionCommand {

	public Dup2x2Command(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		OperandStack stack = getStack();
		int currentForIndex = getCurrentInstPosInCode();
		Operand op1 = (Operand) stack.pop();
		Operand op2 = (Operand) stack.pop();
		Operand op3 = (Operand) stack.pop();
		boolean doNotPop=GlobalVariableStore.isDoNotPop();
		Operand op4 = null;
		if (stack.size() > 0) {
			op4 = stack.getTopOfStack();
		}

		int next = currentForIndex + 1;
		int prev = currentForIndex - 1;
		byte[] code = getCode();
		boolean specialCase = false;
		if (code[next] == JvmOpCodes.DASTORE) {
			if (code[prev] == JvmOpCodes.DADD || code[prev] == JvmOpCodes.DSUB) {
				specialCase = true;
			}
		}
		if (!specialCase) {
			if (code[next] == JvmOpCodes.LASTORE) {
				if (code[prev] == JvmOpCodes.LADD
						|| code[prev] == JvmOpCodes.LSUB) {
					specialCase = true;
				}
			}
		}

		if (!specialCase) {
			// stack.push(op2);
			stack.push(op1);
			if (op4 != null)
				stack.push(op4);
			stack.push(op3);
			stack.push(op2);
			stack.push(op1);
			if (doNotPop) {
				doNotPop = false;
			}

		} else {

			// stack.push(op3);
			// if(op4!=null)
			// stack.push(op4);
			java.lang.String temp = op3.getOperandValue() + "["
					+ op2.getOperandValue() + "]";
			Operand otemp = new Operand();
			otemp.setOperandValue(temp);
			stack.push(otemp);
			stack.push(op3);
			stack.push(op2);
			stack.push(op1);
			if (doNotPop) {
				doNotPop = false;
			}
		}
		GlobalVariableStore.setDoNotPop(doNotPop);
	}

}
