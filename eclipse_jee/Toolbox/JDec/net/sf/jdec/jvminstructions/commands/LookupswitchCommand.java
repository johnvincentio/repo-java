package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.blockhelpers.BranchHelper;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Util;

public class LookupswitchCommand extends AbstractInstructionCommand {

	public LookupswitchCommand(Behaviour context) {
		super(context);

	}
	
	private int toSkip;

	public int getSkipBytes() {
		return toSkip;
	}

	public void execute() {
		  int i = getCurrentInstPosInCode(); 
		  int lookupSwitchPos=i;
		  int begin = i;
          int leave_bytes = (4 - (i % 4))-1;
          for(int indx=0;indx<leave_bytes;indx++) {
              i++;
          }
          byte[] info = getCode();
          // Read Default
          int Default=BranchHelper.getSwitchOffset(info,i,"");//(info[++i] << 24) | (info[++i] << 16) | (info[++i] << 8) |info[++i];
          i=i+4;
          int numberOfLabels=BranchHelper.getSwitchOffset(info,i,"");//(info[++i] << 24) | (info[++i] << 16) | (info[++i] << 8) |info[++i];
          i+=4;
          //int high=(info[++i] << 24) | (info[++i] << 16) | (info[++i] << 8) |info[++i];
          //int numberOfOffsets=(high-low)+1;
          int offsetValues[]=new int[numberOfLabels];
          int labels[]=new int[numberOfLabels];
          for(int start=0;start<numberOfLabels;start++) {
              int label=BranchHelper.getSwitchOffset(info,i,"label");//(info[++i] << 24) | (info[++i] << 16) | (info[++i] << 8) |info[++i];
              i+=4;
              int offsetVal=BranchHelper.getSwitchOffset(info,i,"");//(info[++i] << 24) | (info[++i] << 16) | (info[++i] << 8) |info[++i];
              i+=4;
              labels[start]=label;
              offsetValues[start]=offsetVal;
              
          }
          OperandStack opStack = getStack();
          Object ob=opStack.getTopOfStack().getOperandValue();
          // Add to each offset
          for(int start=0;start<numberOfLabels;start++) {
              
              offsetValues[start]=offsetValues[start]+lookupSwitchPos;
          }
          Default+=lookupSwitchPos;
          int end = i;
          //start=low;
          String tempString="switch("+ob.toString()+")\n{\n ";
          Behaviour behavior = getContext();
          behavior.appendToBuffer(Util.formatDecompiledStatement(tempString));
          
          toSkip = end - begin;
	}

}
