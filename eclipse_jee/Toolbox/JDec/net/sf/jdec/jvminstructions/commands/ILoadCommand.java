package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.core.DecompilerHelper;
import net.sf.jdec.core.LocalVariable;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;

public class ILoadCommand extends AbstractInstructionCommand {

	public ILoadCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 1;
	}

	public void execute(){
		int i = getCurrentInstPosInCode();
		byte[] info = getCode();
		int opValueI = info[++i];
		handleComplexIload(opValueI , info);
	}

	private void handleComplexIload(int opValueI, byte[] info) {
		int currentForIndex = getCurrentInstPosInCode();
		OperandStack opStack = getStack();
		LocalVariable local = DecompilerHelper.getLocalVariable(opValueI, "load", "int", false,
				currentForIndex);
		if (local != null) {
			boolean terEnd = false;//isLoadTernaryEnd(currentForIndex);

			Operand op = new Operand();
			boolean bo = getGenericFinder().isPrevInstIINC( currentForIndex, opValueI);
			StringBuffer addsub = new StringBuffer("");
			boolean bo2 = false;

			if (!bo) {
				bo = getGenericFinder().isNextInstIINC( currentForIndex, opValueI, "complex");
			}
			if (!bo) {
				bo = DecompilerHelper.checkForPostIncrForLoadCase(info, currentForIndex,
						"category1", true, opValueI, addsub);
				bo2 = bo;
			}
			java.lang.String ltmp = local.getTempVarName();
			if (!terEnd) {
				if (bo && ltmp != null) {
					op.setOperandValue(ltmp);
				} else {
					if (!bo2)
						op.setOperandValue(local.getVarName());
					else {
						op.setOperandValue(local.getVarName()
								+ addsub.toString());
					}
				}
			} else {
				java.lang.String v1 = opStack.getTopOfStack().getOperandValue();
				if (bo && ltmp != null) {
					op.setOperandValue(v1 + ltmp);
				} else {
					op.setOperandValue(v1 + local.getVarName());
				}
			}

			op.setLocalVarIndex(opValueI);
			op.setLocalVarType(local.getDataType());
			boolean r = false;//checkIFLoadInstIsPartOFTernaryCond(currentForIndex);
			if (r) {
				if (opStack.size() > 0) {
					java.lang.String str = opStack.getTopOfStack()
							.getOperandValue();
					str = str + op.getOperandValue();
					op.setOperandValue(str);
				}
			}
			opStack.push(op);
		}
	}

}
