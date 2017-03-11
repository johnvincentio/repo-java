package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IFACMPEQCommand;
import net.sf.jdec.util.ExecutionState;

public class IFACMPEQ  extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IFACMPEQCommand(ExecutionState.getMethodContext()));
	}
}
