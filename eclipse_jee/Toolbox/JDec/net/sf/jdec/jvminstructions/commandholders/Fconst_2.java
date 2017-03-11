package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Fconst_2Command;
import net.sf.jdec.util.ExecutionState;

public class Fconst_2 extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Fconst_2";
	}

	protected void registerCommand() {
		setCommand(new Fconst_2Command(ExecutionState.getMethodContext()));
	}

}
