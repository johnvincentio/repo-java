package net.sf.jdec.jvminstructions.commands;

import java.util.Iterator;
import java.util.Map;

import net.sf.jdec.core.DecompilerHelper;
import net.sf.jdec.core.GlobalVariableStore;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Util;

public class IReturnCommand extends AbstractInstructionCommand {

	public IReturnCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		boolean oktoadd = true;
		Behaviour behavior = getContext();
		
		String tempString = "";
		OperandStack opStack = getStack();
		int currentForIndex = getCurrentInstPosInCode();
		int i = currentForIndex;
		Map returnsAtI = GlobalVariableStore.getReturnsAtI();
		Iterator mapIT = returnsAtI.entrySet().iterator();
		while (mapIT.hasNext()) {
			Map.Entry entry = (Map.Entry) mapIT.next();
			Object key = entry.getKey();
			Object retStatus = entry.getValue().toString();
			if (key instanceof Integer) {
				Integer pos = (Integer) key;
				int temp = pos.intValue();
				if (temp == i) {
					if (retStatus.equals("true")) {

						oktoadd = false;
						break;
					}
				}
			}

		}

		if (!oktoadd) {
			returnsAtI.remove(new Integer(i));
		}

		if (oktoadd && opStack.size() > 0) {
			Operand op = (Operand) opStack.pop();
			boolean bool = DecompilerHelper.isMethodRetBoolean(getContext());
			if (bool) {
				if (op.getOperandValue().equals("1")) {
					op.setOperandValue("true");
				} else if (op.getOperandValue().equals("0")) {
					op.setOperandValue("false");
				}
			}
			tempString = "return " + op.getOperandValue() + ";\n";
			behavior.appendToBuffer( Util.formatDecompiledStatement(tempString));
		}
		
	}

}
