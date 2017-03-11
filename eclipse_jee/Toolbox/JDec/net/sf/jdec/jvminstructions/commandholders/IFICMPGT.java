package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IFICMPGTCommand;
import net.sf.jdec.util.ExecutionState;

public class IFICMPGT extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IFICMPGTCommand(ExecutionState.getMethodContext()));
	}
}
