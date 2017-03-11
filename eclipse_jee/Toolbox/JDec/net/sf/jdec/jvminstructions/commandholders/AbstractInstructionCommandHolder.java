package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.AbstractInstructionCommand;
import net.sf.jdec.jvminstructions.commands.IInstructionCommand;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.ExecutionState;

public abstract class AbstractInstructionCommandHolder implements IInstructionCommandHolder {


    private IInstructionCommand command;



    public AbstractInstructionCommandHolder(){
        registerCommand();
    }

    public IInstructionCommand getCommand() {
        return command;
    }

    public void setCommand(IInstructionCommand command) {
        this.command = command;
    }

    public String toString() {
        StringBuffer string = new StringBuffer();
        string.append("[ "+ getName() + " -Instruction Code Pos : "+ ExecutionState.getCurrentInstructionPosition()+"]");
        string.append(System.getProperty("line.separator") + "[Method Name : "+((AbstractInstructionCommand)getCommand()).getContext().getBehaviourName()+"]");
        return string.toString();
    }
    
    public Behaviour getContext(){
    	return ExecutionState.getMethodContext();
    }

    protected String getName(){
    	return getClass().getName(); 
    }
    protected abstract void registerCommand();


    
}
