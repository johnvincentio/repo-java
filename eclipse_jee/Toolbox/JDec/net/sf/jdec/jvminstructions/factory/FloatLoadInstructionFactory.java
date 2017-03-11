package net.sf.jdec.jvminstructions.factory;

import java.util.Map;

import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.jvminstructions.commandholders.D2F;
import net.sf.jdec.jvminstructions.commandholders.Fadd;
import net.sf.jdec.jvminstructions.commandholders.Faload;
import net.sf.jdec.jvminstructions.commandholders.Fcmpg;
import net.sf.jdec.jvminstructions.commandholders.Fcmpl;
import net.sf.jdec.jvminstructions.commandholders.Fconst_0;
import net.sf.jdec.jvminstructions.commandholders.Fconst_1;
import net.sf.jdec.jvminstructions.commandholders.Fconst_2;
import net.sf.jdec.jvminstructions.commandholders.Fdiv;
import net.sf.jdec.jvminstructions.commandholders.Fload;
import net.sf.jdec.jvminstructions.commandholders.Fload_0;
import net.sf.jdec.jvminstructions.commandholders.Fload_1;
import net.sf.jdec.jvminstructions.commandholders.Fload_2;
import net.sf.jdec.jvminstructions.commandholders.Fload_3;
import net.sf.jdec.jvminstructions.commandholders.Fmul;
import net.sf.jdec.jvminstructions.commandholders.Fneg;
import net.sf.jdec.jvminstructions.commandholders.Frem;
import net.sf.jdec.jvminstructions.commandholders.Fsub;
import net.sf.jdec.jvminstructions.commandholders.IInstructionCommandHolder;
import net.sf.jdec.jvminstructions.commandholders.L2f;
import net.sf.jdec.jvminstructions.util.InstrConstants;

/*
*  FloatLoadInstructionFactory.java Copyright (c) 2006,07 Swaroop Belur
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.

* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*
*/
public class FloatLoadInstructionFactory implements IInstructionFactory{

    public IInstructionCommandHolder newInstance(Map parameters) {
       
    	Integer temp = (Integer) parameters.get(InstrConstants.OPCODE_TYPE);
		if (temp == null) {
			throw new IllegalArgumentException("Opcode type cannot be null");
		}

		int opcodevalue = temp.intValue();
		switch (opcodevalue) {
		
		case JvmOpCodes.D2F:
			return new D2F();	
			
		case JvmOpCodes.FADD:
			return new Fadd();
	
		case JvmOpCodes.FALOAD:
			return new Faload();
			
		case JvmOpCodes.FCMPG:
			return new Fcmpg();
			
		case JvmOpCodes.FCMPL:
			return new Fcmpl();
			
			
		case JvmOpCodes.FCONST_0:
			return new Fconst_0();
			
		case JvmOpCodes.FCONST_2:
			return new Fconst_2();
			
		case JvmOpCodes.FCONST_1:
			return new Fconst_1();
			
			
		case JvmOpCodes.FDIV:
			return new Fdiv();
			
			
		case JvmOpCodes.FLOAD:
			return new Fload();
			
		case JvmOpCodes.FLOAD_0:
			return new Fload_0();
			
		case JvmOpCodes.FLOAD_1:
			return new Fload_1();
			
		case JvmOpCodes.FLOAD_2:
			return new Fload_2();
			
		case JvmOpCodes.FLOAD_3:
			return new Fload_3();
			
		case JvmOpCodes.FMUL:
			return new Fmul();
			
		case JvmOpCodes.FNEG:
			return new Fneg();	
			
		case JvmOpCodes.FREM:
			return new Frem();	
			
		case JvmOpCodes.FSUB:
			return new Fsub();
			
		case JvmOpCodes.L2F:
			return new L2f();
			
		default:
			throw new IllegalArgumentException(
					"Input argument opcodetype did not match any valid jvminstruction values");

		}
    }
}
