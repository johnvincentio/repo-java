package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.MonitorEnterCommand;
import net.sf.jdec.util.ExecutionState;

public class MonitorEnter extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new MonitorEnterCommand(ExecutionState.getMethodContext()));
	}
}

