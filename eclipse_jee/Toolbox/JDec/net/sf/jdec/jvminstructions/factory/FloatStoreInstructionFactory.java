package net.sf.jdec.jvminstructions.factory;

import java.util.Map;

import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.jvminstructions.commandholders.Fastore;
import net.sf.jdec.jvminstructions.commandholders.Fstore;
import net.sf.jdec.jvminstructions.commandholders.Fstore_0;
import net.sf.jdec.jvminstructions.commandholders.Fstore_1;
import net.sf.jdec.jvminstructions.commandholders.Fstore_2;
import net.sf.jdec.jvminstructions.commandholders.Fstore_3;
import net.sf.jdec.jvminstructions.commandholders.IInstructionCommandHolder;
import net.sf.jdec.jvminstructions.util.InstrConstants;

/*
*  FloatStoreInstructionFactory.java Copyright (c) 2006,07 Swaroop Belur
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
public class FloatStoreInstructionFactory implements IInstructionFactory{

    public IInstructionCommandHolder newInstance(Map parameters) {
    	Integer temp = (Integer) parameters.get(InstrConstants.OPCODE_TYPE);
		if (temp == null) {
			throw new IllegalArgumentException("Opcode type cannot be null");
		}

		int opcodevalue = temp.intValue();
		switch (opcodevalue) {
	
		case JvmOpCodes.FASTORE:
			return new Fastore();
			
			
		case JvmOpCodes.FSTORE:
			return new Fstore();
			
		case JvmOpCodes.FSTORE_0:
			return new Fstore_0();
			
		case JvmOpCodes.FSTORE_1:
			return new Fstore_1();
			
		case JvmOpCodes.FSTORE_2:
			return new Fstore_2();
			
		case JvmOpCodes.FSTORE_3:
			return new Fstore_3();	
			
			
		
		default:
			throw new IllegalArgumentException(
					"Input argument opcodetype did not match any valid jvminstruction values");

		}
    }
}
