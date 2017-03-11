package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.GetStaticCommand;
import net.sf.jdec.util.ExecutionState;

public class GetStatic extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "GetStatic";
	}

	protected void registerCommand() {
		setCommand(new GetStaticCommand(ExecutionState.getMethodContext()));
	}

}
