package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.D2ICommand;
import net.sf.jdec.util.ExecutionState;

public class D2I extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "D2I";
	}

	protected void registerCommand() {
		D2ICommand command = new D2ICommand(ExecutionState.getMethodContext());
		setCommand(command);
	}

}
