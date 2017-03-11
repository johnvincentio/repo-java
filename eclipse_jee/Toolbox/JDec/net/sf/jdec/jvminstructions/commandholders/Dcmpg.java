package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.DcmpgCommand;
import net.sf.jdec.util.ExecutionState;

public class Dcmpg extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Dcmpg";
	}

	protected void registerCommand() {
		setCommand(new DcmpgCommand(ExecutionState.getMethodContext()));
	}

}
