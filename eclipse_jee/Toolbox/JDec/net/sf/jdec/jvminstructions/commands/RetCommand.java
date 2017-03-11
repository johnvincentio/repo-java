package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.reflection.Behaviour;

public class RetCommand extends AbstractInstructionCommand {

	public RetCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 1;
	}

	public void execute() {
	}

}
