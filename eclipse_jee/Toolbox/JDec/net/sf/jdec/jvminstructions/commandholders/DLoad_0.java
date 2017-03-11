package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Dload_0Command;
import net.sf.jdec.util.ExecutionState;

public class DLoad_0 extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "DLoad_0";
	}

	protected void registerCommand() {
		setCommand(new Dload_0Command(ExecutionState.getMethodContext()));
	}

}
