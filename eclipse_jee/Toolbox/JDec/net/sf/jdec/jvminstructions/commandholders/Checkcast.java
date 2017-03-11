package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.CheckcastCommand;
import net.sf.jdec.util.ExecutionState;

public class Checkcast extends AbstractInstructionCommandHolder {


	protected void registerCommand() {
		CheckcastCommand command = new CheckcastCommand(ExecutionState.getMethodContext());
		setCommand(command);
	}

}
