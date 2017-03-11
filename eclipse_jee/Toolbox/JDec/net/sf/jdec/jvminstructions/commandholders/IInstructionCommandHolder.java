package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IInstructionCommand;

import java.io.Serializable;


public interface IInstructionCommandHolder extends Serializable {

    public IInstructionCommand getCommand();

    public void setCommand(IInstructionCommand command);

}
