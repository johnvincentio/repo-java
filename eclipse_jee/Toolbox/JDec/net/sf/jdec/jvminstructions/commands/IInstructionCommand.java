package net.sf.jdec.jvminstructions.commands;

import java.io.Serializable;


public interface IInstructionCommand extends Serializable {

    public void execute();
}
