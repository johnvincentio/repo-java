package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Dload_2Command;
import net.sf.jdec.util.ExecutionState;

public class DLoad_2 extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "DLoad_2";
	}

	protected void registerCommand() {
		setCommand(new Dload_2Command(ExecutionState.getMethodContext()));
	}

}
