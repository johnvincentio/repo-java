package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.GetfieldCommand;
import net.sf.jdec.util.ExecutionState;

public class GetField extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "GetField";
	}

	protected void registerCommand() {
		setCommand(new GetfieldCommand(ExecutionState.getMethodContext()));
	}

}
