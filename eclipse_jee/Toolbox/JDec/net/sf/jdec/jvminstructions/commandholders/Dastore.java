package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.DastoreCommand;

public class Dastore extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Dastore";
	}

	protected void registerCommand() {
		setCommand(new DastoreCommand(getContext()));
	}

}
