package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Fload_0Command;
import net.sf.jdec.util.ExecutionState;

public class Fload_0 extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Fload_0";
	}

	protected void registerCommand() {
		setCommand(new Fload_0Command(ExecutionState.getMethodContext()));
	}

}
