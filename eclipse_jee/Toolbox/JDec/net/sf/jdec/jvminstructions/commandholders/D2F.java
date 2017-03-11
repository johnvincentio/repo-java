package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.D2FCommand;
import net.sf.jdec.util.ExecutionState;

public class D2F extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "d2f";
	}

	protected void registerCommand() {
		D2FCommand command = new D2FCommand(ExecutionState.getMethodContext());
		setCommand(command);
	}

}
