package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.FcmpgCommand;
import net.sf.jdec.util.ExecutionState;

public class Fcmpg extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Fcmpg";
	}

	protected void registerCommand() {
		setCommand(new FcmpgCommand(ExecutionState.getMethodContext()));
	}

}
