package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.CaloadCommand;
import net.sf.jdec.util.ExecutionState;

public class Caload extends AbstractInstructionCommandHolder {

	public Caload() {
	}

	protected String getName() {
		return "caload";
	}

	protected void registerCommand() {
		CaloadCommand command = new CaloadCommand(ExecutionState.getMethodContext());
		setCommand(command);
	}

}
