package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.DloadCommand;
import net.sf.jdec.util.ExecutionState;

public class DLoad extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "DLoad";
	}

	protected void registerCommand() {
		setCommand(new DloadCommand(ExecutionState.getMethodContext()));
	}

}
