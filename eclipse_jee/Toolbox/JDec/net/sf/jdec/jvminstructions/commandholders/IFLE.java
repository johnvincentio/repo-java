package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IFLECommand;
import net.sf.jdec.util.ExecutionState;

public class IFLE extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IFLECommand(ExecutionState.getMethodContext()));
	}
}
