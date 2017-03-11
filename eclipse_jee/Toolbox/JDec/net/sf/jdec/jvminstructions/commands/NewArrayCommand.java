package net.sf.jdec.jvminstructions.commands;

import java.util.Stack;

import net.sf.jdec.core.DecompilerHelper;
import net.sf.jdec.core.GlobalVariableStore;
import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Constants;
import net.sf.jdec.util.Util;

public class NewArrayCommand extends AbstractInstructionCommand {

	public NewArrayCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 1;
	}

	public void execute() {
		java.lang.String temp = "";
		byte[] info = getCode();
		int currentForIndex = getCurrentInstPosInCode();
		Behaviour behavior = getContext();
		boolean embeddedNEWARRAY = GlobalVariableStore.isEmbeddedNEWARRAY();
		boolean embeddedNewArrayCopy = GlobalVariableStore
				.isEmbeddedANEWARRAYCopy();
		switch (info[(currentForIndex + 1)]) {
		case 4:

			temp = "boolean[";
			break;
		case 5:

			temp = "char[";
			break;
		case 6:

			temp = "float[";
			break;
		case 7:

			temp = "double[";
			break;
		case 8:

			temp = "byte[";
			break;
		case 9:

			temp = "short[";
			break;
		case 10:

			temp = "int[";
			break;
		case 11:

			temp = "long[";
			break;
		}
		Stack arraytimesstack = GlobalVariableStore.getArraytimesstack();
		OperandStack opStack = getStack();
		// Pop The Size
		Operand arSize = opStack.getTopOfStack();
		int arraytimespush = GlobalVariableStore.getArraytimespush();
		boolean dup = getGenericFinder().isInstDup((currentForIndex + 2));
		if (!dup) {

			boolean nextisastore = getStoreFinder().isNextInstAStore(
					currentForIndex + 2);

			Operand op = new Operand();
			op.setOperandType(Constants.IS_ARRAY_REF);
			java.lang.String Reference = "JdecGenerated"
					+ (currentForIndex + 1);
			op.setClassType(temp + "]");
			String temp2 = temp;
			temp = temp + "] " + Reference + " = new " + temp
					+ arSize.getOperandValue() + "]";
			if (nextisastore) {
				behavior.appendToBuffer("\n"
						+ Util.formatDecompiledStatement(temp) + ";\n");
				op.setOperandValue(Reference);
				opStack.push(op);
			}
			else{
				op.setOperandValue("new " + temp2
						+ arSize.getOperandValue() + "]");
				opStack.push(op);
			}
			// arraypush=false;
		} else {
			if (newfound()) {
				Operand op = new Operand();
				op.setOperandType(Constants.IS_ARRAY_REF);
				java.lang.String Reference = "JdecGenerated"
						+ (currentForIndex + 1);
				op.setClassType(temp + "]");
				Util.forceNewLine = false;
				Util.forceStartSpace = false;
				op.setOperandValue("new " + temp + "]{");
				boolean r = false;// checkIFLoadInstIsPartOFTernaryCond(currentForIndex);
				if (r) {
					if (opStack.size() > 0) {
						java.lang.String str = opStack.getTopOfStack()
								.getOperandValue();
						str = str + op.getOperandValue();
						op.setOperandValue(str);
					}
				}
				opStack.push(op);

				try {
					arraytimespush = Integer.parseInt(arSize.getOperandValue());
				} catch (NumberFormatException ne) {
					// Check whether prev was bipush or sipush
					int currentPos = currentForIndex;
					int prevs = getGenericFinder().getPrevStartOfInst(
							currentPos);
					if (info[prevs] == JvmOpCodes.BIPUSH) {
						int bipushvalue = info[(prevs + 1)];
						arraytimespush = bipushvalue;
					} else {
						if (info[prevs] == JvmOpCodes.SIPUSH) {
							int sipushvalue = getGenericFinder().getOffset(
									prevs);
							arraytimespush = sipushvalue;
						} else {
							arraytimespush = 0;
						}

					}

				}
				if(arraytimespush > 0)
					arraytimesstack.push("" + arraytimespush);
				if(arraytimespush == 0){
					op.setOperandValue(op.getOperandValue()+" }");
				}
			} else {
				Operand op = new Operand();
				op.setOperandType(Constants.IS_ARRAY_REF);
				java.lang.String Reference = "JdecGenerated"
						+ (currentForIndex + 1);
				op.setClassType(temp + "]");
				if (arraytimesstack.size() == 0)
					temp = temp + "] " + Reference + " = new " + temp + "]";
				else
					temp = "new " + temp + "]";
				behavior.appendToBuffer("\n"
						+ Util.formatDecompiledStatement(temp));
				behavior.appendToBuffer("{");

				boolean embed = DecompilerHelper.isNewArrayEmbedded(info);
				if (!embed) {
					op.setOperandValue(Reference);
					opStack.push(op);
				} else {
					if (opStack.size() > 0) {
						opStack.getTopOfStack();
					}
					embeddedNEWARRAY = true;
					embeddedNewArrayCopy = true;
				}

				try {
					arraytimespush = Integer.parseInt(arSize.getOperandValue());

				} catch (NumberFormatException ne) {
					// Check whether prev was bipush or sipush
					int currentPos = currentForIndex;
					int prevs = getGenericFinder().getPrevStartOfInst(
							currentPos);
					if (info[prevs] == JvmOpCodes.BIPUSH) {
						int bipushvalue = info[(prevs + 1)];
						arraytimespush = bipushvalue;
					} else {
						if (info[prevs] == JvmOpCodes.SIPUSH) {
							int sipushvalue = getGenericFinder().getOffset(
									prevs);
							arraytimespush = sipushvalue;
						} else {
							arraytimespush = 0;
						}

					}

				}
				if(arraytimespush > 0)
					arraytimesstack.push("" + arraytimespush);
				
			}
		}
		GlobalVariableStore.setEmbeddedNEWARRAY(embeddedNEWARRAY);
		GlobalVariableStore.setEmbeddedNewArrayCopy(embeddedNewArrayCopy);
		GlobalVariableStore.setArraytimespush(arraytimespush);

	}

}
