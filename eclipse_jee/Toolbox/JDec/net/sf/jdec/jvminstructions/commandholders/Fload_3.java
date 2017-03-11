package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Fload_3Command;
import net.sf.jdec.util.ExecutionState;

public class Fload_3 extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Fload_3";
	}

	protected void registerCommand() {
		setCommand(new Fload_3Command(ExecutionState.getMethodContext()));
	}

}
