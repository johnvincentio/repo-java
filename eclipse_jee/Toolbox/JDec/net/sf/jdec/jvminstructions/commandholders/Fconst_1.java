package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Fconst_1Command;
import net.sf.jdec.util.ExecutionState;

public class Fconst_1 extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Fconst_1";
	}

	protected void registerCommand() {
		setCommand(new Fconst_1Command(ExecutionState.getMethodContext()));
	}

}
