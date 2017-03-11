package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Dload_1Command;
import net.sf.jdec.util.ExecutionState;

public class DLoad_1 extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "DLoad_1";
	}

	protected void registerCommand() {
		setCommand(new Dload_1Command(ExecutionState.getMethodContext()));
	}

}
