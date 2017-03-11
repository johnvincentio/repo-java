package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.reflection.Behaviour;

public class MonitorExitCommand extends AbstractInstructionCommand {

	public MonitorExitCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		byte[] info = getCode();
		int i = getCurrentInstPosInCode();
		if (info[i + 1] != JvmOpCodes.ATHROW)
			getStack().getTopOfStack();
	}

}
