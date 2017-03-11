package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Fload_1Command;
import net.sf.jdec.util.ExecutionState;

public class Fload_1 extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Fload_1";
	}

	protected void registerCommand() {
		setCommand(new Fload_1Command(ExecutionState.getMethodContext()));
	}

}
