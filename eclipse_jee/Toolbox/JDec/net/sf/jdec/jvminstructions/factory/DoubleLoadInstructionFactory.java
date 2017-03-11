package net.sf.jdec.jvminstructions.factory;

import java.util.Map;

import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.jvminstructions.commandholders.DConst_0;
import net.sf.jdec.jvminstructions.commandholders.DConst_1;
import net.sf.jdec.jvminstructions.commandholders.DLoad;
import net.sf.jdec.jvminstructions.commandholders.DLoad_0;
import net.sf.jdec.jvminstructions.commandholders.DLoad_1;
import net.sf.jdec.jvminstructions.commandholders.DLoad_2;
import net.sf.jdec.jvminstructions.commandholders.DLoad_3;
import net.sf.jdec.jvminstructions.commandholders.Dadd;
import net.sf.jdec.jvminstructions.commandholders.Daload;
import net.sf.jdec.jvminstructions.commandholders.Dcmpg;
import net.sf.jdec.jvminstructions.commandholders.Dcmpl;
import net.sf.jdec.jvminstructions.commandholders.Ddiv;
import net.sf.jdec.jvminstructions.commandholders.Dmul;
import net.sf.jdec.jvminstructions.commandholders.Dneg;
import net.sf.jdec.jvminstructions.commandholders.Drem;
import net.sf.jdec.jvminstructions.commandholders.Dsub;
import net.sf.jdec.jvminstructions.commandholders.Dup;
import net.sf.jdec.jvminstructions.commandholders.Dup2;
import net.sf.jdec.jvminstructions.commandholders.Dup2x1;
import net.sf.jdec.jvminstructions.commandholders.Dup2x2;
import net.sf.jdec.jvminstructions.commandholders.Dupx1;
import net.sf.jdec.jvminstructions.commandholders.Dupx2;
import net.sf.jdec.jvminstructions.commandholders.F2d;
import net.sf.jdec.jvminstructions.commandholders.IInstructionCommandHolder;
import net.sf.jdec.jvminstructions.commandholders.L2d;
import net.sf.jdec.jvminstructions.util.InstrConstants;

/*
 *  IntegerLoadInstructionFactory.java Copyright (c) 2006,07 Swaroop Belur
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
public class DoubleLoadInstructionFactory implements IInstructionFactory {

	public IInstructionCommandHolder newInstance(Map parameters) {
		Integer temp = (Integer) parameters.get(InstrConstants.OPCODE_TYPE);
		if (temp == null) {
			throw new IllegalArgumentException("Opcode type cannot be null");
		}

		int opcodevalue = temp.intValue();
		switch (opcodevalue) {
		
		
		case JvmOpCodes.DADD:
			return new Dadd();
			
		
		case JvmOpCodes.DALOAD:
			return new Daload();
			
		case JvmOpCodes.DCMPG:
			return new Dcmpg();
			
		case JvmOpCodes.DCMPL:
			return new Dcmpl();
			
		case JvmOpCodes.DCONST_0:
			return new DConst_0();
			
		case JvmOpCodes.DCONST_1:
			return new DConst_1();
			
		case JvmOpCodes.DDIV:
			return new Ddiv();
			
		case JvmOpCodes.DLOAD:
			return new DLoad();
			
		case JvmOpCodes.DLOAD_0:
			return new DLoad_0();
			
		case JvmOpCodes.DLOAD_1:
			return new DLoad_1();
			
		case JvmOpCodes.DLOAD_2:
			return new DLoad_2();
			
		case JvmOpCodes.DLOAD_3:
			return new DLoad_3();
			
			
		case JvmOpCodes.DMUL:
			return new Dmul();
			
		case JvmOpCodes.DNEG:
			return new Dneg();
			
		case JvmOpCodes.DREM:
			return new Drem();
			
		case JvmOpCodes.DSUB:
			return new Dsub();
			
	
			
			
		case JvmOpCodes.F2D:
			return new F2d();
			
		case JvmOpCodes.L2D:
			return new L2d();
			
		default:
			throw new IllegalArgumentException(
					"Input argument opcodetype did not match any valid jvminstruction values");

		}
	}
}
