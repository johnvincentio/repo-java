package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.MonitorExitCommand;
import net.sf.jdec.util.ExecutionState;

public class MonitorExit extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new MonitorExitCommand(ExecutionState.getMethodContext()));
	}
}

