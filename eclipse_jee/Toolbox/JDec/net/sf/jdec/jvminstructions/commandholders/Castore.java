package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.CastoreCommand;
import net.sf.jdec.util.ExecutionState;

public class Castore extends AbstractInstructionCommandHolder {

	public Castore() {
	}

	protected String getName() {
		return "Castore";
	}

	protected void registerCommand() {
		CastoreCommand command = new CastoreCommand(ExecutionState.getMethodContext());
		setCommand(command);
	}

}
