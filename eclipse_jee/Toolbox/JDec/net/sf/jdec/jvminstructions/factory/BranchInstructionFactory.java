package net.sf.jdec.jvminstructions.factory;

import java.util.Map;

import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.jvminstructions.commandholders.Areturn;
import net.sf.jdec.jvminstructions.commandholders.Athrow;
import net.sf.jdec.jvminstructions.commandholders.Dreturn;
import net.sf.jdec.jvminstructions.commandholders.Freturn;
import net.sf.jdec.jvminstructions.commandholders.Goto;
import net.sf.jdec.jvminstructions.commandholders.Goto_W;
import net.sf.jdec.jvminstructions.commandholders.IFACMPEQ;
import net.sf.jdec.jvminstructions.commandholders.IFACMPNE;
import net.sf.jdec.jvminstructions.commandholders.IFEQ;
import net.sf.jdec.jvminstructions.commandholders.IFGE;
import net.sf.jdec.jvminstructions.commandholders.IFGT;
import net.sf.jdec.jvminstructions.commandholders.IFICMPEQ;
import net.sf.jdec.jvminstructions.commandholders.IFICMPGE;
import net.sf.jdec.jvminstructions.commandholders.IFICMPGT;
import net.sf.jdec.jvminstructions.commandholders.IFICMPLE;
import net.sf.jdec.jvminstructions.commandholders.IFICMPLT;
import net.sf.jdec.jvminstructions.commandholders.IFICMPNE;
import net.sf.jdec.jvminstructions.commandholders.IFLE;
import net.sf.jdec.jvminstructions.commandholders.IFLT;
import net.sf.jdec.jvminstructions.commandholders.IFNE;
import net.sf.jdec.jvminstructions.commandholders.IFNONNULL;
import net.sf.jdec.jvminstructions.commandholders.IFNULL;
import net.sf.jdec.jvminstructions.commandholders.IInstructionCommandHolder;
import net.sf.jdec.jvminstructions.commandholders.Ireturn;
import net.sf.jdec.jvminstructions.commandholders.Jsr;
import net.sf.jdec.jvminstructions.commandholders.Jsr_w;
import net.sf.jdec.jvminstructions.commandholders.LookupSwitch;
import net.sf.jdec.jvminstructions.commandholders.Lreturn;
import net.sf.jdec.jvminstructions.commandholders.MonitorEnter;
import net.sf.jdec.jvminstructions.commandholders.MonitorExit;
import net.sf.jdec.jvminstructions.commandholders.Ret;
import net.sf.jdec.jvminstructions.commandholders.Return;
import net.sf.jdec.jvminstructions.commandholders.TableSwitch;
import net.sf.jdec.jvminstructions.util.InstrConstants;

public class BranchInstructionFactory implements IInstructionFactory {

	public BranchInstructionFactory() {
	}

	public IInstructionCommandHolder newInstance(Map parameters) {

		Integer temp = (Integer) parameters.get(InstrConstants.OPCODE_TYPE);
		if (temp == null) {
			throw new IllegalArgumentException("Opcode type cannot be null");
		}

		int opcodevalue = temp.intValue();
		switch (opcodevalue) {
		case JvmOpCodes.IF_ACMPEQ:
				return new IFACMPEQ();
				
		case JvmOpCodes.IF_ACMPNE:
			return new IFACMPNE();
			
		case JvmOpCodes.IF_ICMPEQ:
			return new IFICMPEQ();
			
		case JvmOpCodes.IF_ICMPGE:
			return new IFICMPGE();
		
		case JvmOpCodes.IF_ICMPGT:
			return new IFICMPGT();
			
		case JvmOpCodes.IF_ICMPLE:
			return new IFICMPLE();
	
		case JvmOpCodes.IF_ICMPLT:
			return new IFICMPLT();
			
		case JvmOpCodes.IF_ICMPNE:
			return new IFICMPNE();
		
		case JvmOpCodes.IFEQ:
			return new IFEQ();
			

		case JvmOpCodes.IFNE:
			return new IFNE();

		case JvmOpCodes.IFLT:
			return new IFLT();
			
		case JvmOpCodes.IFGT:
			return new IFGT();
			
		case JvmOpCodes.IFGE:
			return new IFGE();
			
		case JvmOpCodes.IFLE:
			return new IFLE();
			
		case JvmOpCodes.IFNONNULL:
			return new IFNONNULL();	
		
		case JvmOpCodes.IFNULL:
			return new IFNULL();
			
		case JvmOpCodes.IRETURN:
			return new Ireturn();	
			
		case JvmOpCodes.ARETURN:
			return new Areturn();
			
		case JvmOpCodes.ATHROW:
			return new Athrow();
			
		case JvmOpCodes.DRETURN:
			return new Dreturn();
			
		case JvmOpCodes.FRETURN:
			return new Freturn();
			
		case JvmOpCodes.GOTO:
			return new Goto();
			
		case JvmOpCodes.GOTO_W:
			return new Goto_W();
			
		case JvmOpCodes.JSR:
			return new Jsr();
		
		case JvmOpCodes.JSR_W:
			return new Jsr_w();
		
		case JvmOpCodes.MONITORENTER:
			return new MonitorEnter();
			
		case JvmOpCodes.MONITOREXIT:
			return new MonitorExit();
			
		case JvmOpCodes.RET:
			return new Ret();
			
		case JvmOpCodes.RETURN:
			return new Return();
			
		case JvmOpCodes.TABLESWITCH:
			return new TableSwitch();
			
		case JvmOpCodes.LOOKUPSWITCH:
			return new LookupSwitch();
			
		case JvmOpCodes.LRETURN:
			return new Lreturn();
			
		default:
			throw new IllegalArgumentException(
					"Input argument opcodetype did not match any valid jvminstruction values");
		}
	}

}
