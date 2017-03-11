package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.core.GlobalVariableStore;
import net.sf.jdec.core.Operand;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Util;

public class MonitorEnterCommand extends AbstractInstructionCommand {

	public MonitorEnterCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		Operand op = getStack().getTopOfStack();
		String tempString = "synchronized(" + op.getOperandValue() + ")\n{\n";
		Behaviour behavior = getContext();
		behavior.appendToBuffer( Util.formatDecompiledStatement(tempString));
		GlobalVariableStore.setCurrentMonitorEnterPos(getCurrentInstPosInCode());

	}

}
