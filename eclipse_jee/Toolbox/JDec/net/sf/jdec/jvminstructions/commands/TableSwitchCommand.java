package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.blockhelpers.BranchHelper;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Util;

public class TableSwitchCommand extends AbstractInstructionCommand {

	public TableSwitchCommand(Behaviour context) {
		super(context);

	}
	
	private int toSkip;

	public int getSkipBytes() {
		return toSkip;
	}

	public void execute() {
		int i = getCurrentInstPosInCode();
		int tableSwitchPos = i;
		int begin = i;
		int leave_bytes = (4 - (i % 4)) - 1;
		for (int indx = 0; indx < leave_bytes; indx++) {
			i++;
		}
		// Read Default
		byte[] info = getCode();
		int Default = BranchHelper.getSwitchOffset(info, i, "");// (info[++i]
		// <<
		// 24) |
		// (info[++i] << 16) |
		// (info[++i] << 8) |info[++i];
		i += 4;
		int low = BranchHelper.getSwitchOffset(info, i, "label");// (info[++i]
		// << 24) |
		// (info[++i] << 16) |
		// (info[++i] << 8)
		// |info[++i];
		i += 4;
		int high = BranchHelper.getSwitchOffset(info, i, "label");// (info[++i]
		// <<
		// 24) |
		// (info[++i] << 16) |
		// (info[++i] << 8)
		// |info[++i];
		i += 4;
		int numberOfOffsets = (high - low) + 1;

		int[]  offsetValues = new int[numberOfOffsets];
		int start;
		for (start = 0; start < numberOfOffsets; start++) {
			int offsetVal = BranchHelper.getSwitchOffset(info, i, "");// (info[++i] << 24)
			// | (info[++i] <<
			// 16) | (info[++i]
			// << 8) |info[++i];
			i = i + 4;
			offsetValues[start] = offsetVal;

		}
		OperandStack opStack = getStack();
		String ob = opStack.getTopOfStack().getOperandValue();
		Integer Index = null;
		// Add to each offset
		for (start = 0; start < numberOfOffsets; start++) {

			offsetValues[start] = offsetValues[start] + tableSwitchPos;
		}
		Default += tableSwitchPos;
		// parsedString+="\ntableswitch\t"+low+" "+high+": default
		// "+Default+"\n";
		start = low;
		String tempString = "switch(" + ob.toString() + ")\n{\n";
		Behaviour behavior = getContext(); 
		behavior.appendToBuffer( Util.formatDecompiledStatement(tempString));
		
		int end = i;
		toSkip = end - begin;
	}

}
