package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IConstM1Command;
import net.sf.jdec.util.ExecutionState;

public class IconstM1 extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IConstM1Command(ExecutionState.getMethodContext()));
	}
}
