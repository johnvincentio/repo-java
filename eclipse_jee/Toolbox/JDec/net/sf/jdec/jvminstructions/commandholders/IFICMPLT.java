package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IFICMPLTCommand;
import net.sf.jdec.util.ExecutionState;

public class IFICMPLT extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IFICMPLTCommand(ExecutionState.getMethodContext()));
	}
}

