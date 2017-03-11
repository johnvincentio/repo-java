package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Fconst_0Command;
import net.sf.jdec.util.ExecutionState;

public class Fconst_0 extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Fconst_0";
	}

	protected void registerCommand() {
		setCommand(new Fconst_0Command(ExecutionState.getMethodContext()));
	}

}
