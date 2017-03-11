package net.sf.jdec.jvminstructions.factory;

import java.util.Map;

import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.jvminstructions.commandholders.Dastore;
import net.sf.jdec.jvminstructions.commandholders.Dstore;
import net.sf.jdec.jvminstructions.commandholders.Dstore_0;
import net.sf.jdec.jvminstructions.commandholders.Dstore_1;
import net.sf.jdec.jvminstructions.commandholders.Dstore_2;
import net.sf.jdec.jvminstructions.commandholders.Dstore_3;
import net.sf.jdec.jvminstructions.commandholders.IInstructionCommandHolder;
import net.sf.jdec.jvminstructions.util.InstrConstants;

/*
*  DoubleStoreInstructionFactory.java Copyright (c) 2006,07 Swaroop Belur
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
public class DoubleStoreInstructionFactory implements IInstructionFactory{

    public IInstructionCommandHolder newInstance(Map parameters) {
    	Integer temp = (Integer) parameters.get(InstrConstants.OPCODE_TYPE);
		if (temp == null) {
			throw new IllegalArgumentException("Opcode type cannot be null");
		}

		int opcodevalue = temp.intValue();
		switch (opcodevalue) {
		
		
		case JvmOpCodes.DASTORE:
			return new Dastore();
			
		case JvmOpCodes.DSTORE:
			return new Dstore();
			
		case JvmOpCodes.DSTORE_0:
			return new Dstore_0();
			
		case JvmOpCodes.DSTORE_1:
			return new Dstore_1();
			
		case JvmOpCodes.DSTORE_2:
			return new Dstore_2();
			
		case JvmOpCodes.DSTORE_3:
			return new Dstore_3();
			
			
		default:
			throw new IllegalArgumentException(
					"Input argument opcodetype did not match any valid jvminstruction values");

		}
    }
}
