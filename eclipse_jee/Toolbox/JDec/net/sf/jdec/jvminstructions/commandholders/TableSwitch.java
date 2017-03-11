package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.TableSwitchCommand;
import net.sf.jdec.util.ExecutionState;

public class TableSwitch extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new TableSwitchCommand(ExecutionState.getMethodContext()));
	}
}

