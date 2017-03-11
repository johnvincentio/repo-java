package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.FcmplCommand;
import net.sf.jdec.util.ExecutionState;

public class Fcmpl extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Fcmpl";
	}

	protected void registerCommand() {
		setCommand(new FcmplCommand(ExecutionState.getMethodContext()));
	}

}
