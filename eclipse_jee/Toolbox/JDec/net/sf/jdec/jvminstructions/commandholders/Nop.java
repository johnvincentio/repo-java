package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.NopCommand;

public class Nop extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new NopCommand());
	}
}

