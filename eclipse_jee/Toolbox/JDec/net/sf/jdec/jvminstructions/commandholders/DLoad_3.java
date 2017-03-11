package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Dload_3Command;
import net.sf.jdec.util.ExecutionState;

public class DLoad_3 extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "DLoad_3";
	}

	protected void registerCommand() {
		setCommand(new Dload_3Command(ExecutionState.getMethodContext()));
	}

}
