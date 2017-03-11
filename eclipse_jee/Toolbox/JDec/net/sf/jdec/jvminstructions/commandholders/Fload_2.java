package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Fload_2Command;
import net.sf.jdec.util.ExecutionState;

public class Fload_2 extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Fload_2";
	}

	protected void registerCommand() {
		setCommand(new Fload_2Command(ExecutionState.getMethodContext()));
	}

}
