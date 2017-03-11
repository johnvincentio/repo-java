package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.LookupswitchCommand;
import net.sf.jdec.util.ExecutionState;

public class LookupSwitch extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new LookupswitchCommand(ExecutionState.getMethodContext()));
	}
}

