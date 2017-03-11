package net.sf.jdec.jvminstructions.commands;

import java.util.Hashtable;

import net.sf.jdec.commonutil.Numbers;
import net.sf.jdec.core.GlobalVariableStore;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.ui.util.UIUtil;

public class SipushCommand extends AbstractInstructionCommand {

	public SipushCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 2;
	}

	public void execute() {
		Hashtable pushTypes=GlobalVariableStore.getPushTypes();
		int currentForIndex = getCurrentInstPosInCode();
		int opValueI = getGenericFinder().getOffset(currentForIndex);
		int sipushvalue = opValueI;
		java.lang.String sopValueI = "";

		java.lang.String nonascii = UIUtil.getUIUtil().getInterpretNonAscii();
		StringBuffer sbf = new StringBuffer("");
		sopValueI = Numbers.formatCharForNonAsciiValue(opValueI, sbf);
		if (sbf.toString().equals("nonascii")) {
			if (nonascii.equals("true")) {

			} else {
				sopValueI = "" + opValueI;
			}
		}
		Operand op = new Operand();
		java.lang.String tp = null;
		if (pushTypes != null) {
			tp = (java.lang.String) pushTypes.get(new Integer(currentForIndex));
			if (tp != null) {
				tp = "(" + tp + ")" + sopValueI;
			}

		}

		if (tp != null)

			op.setOperandValue(tp);// // new Integer(opValueI));
		else
			op.setOperandValue("" + sopValueI);
		boolean r = false;//heckIFLoadInstIsPartOFTernaryCond(currentForIndex);
		OperandStack opStack =getStack();
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
