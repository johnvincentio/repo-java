package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IFLTCommand;
import net.sf.jdec.util.ExecutionState;

public class IFLT extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IFLTCommand(ExecutionState.getMethodContext()));
	}
}
