package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.GOTO_WCommand;
import net.sf.jdec.util.ExecutionState;

public class Goto_W extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new GOTO_WCommand(ExecutionState.getMethodContext()));
	}

}
