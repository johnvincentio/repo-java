/*
 *  InstructionHelper.java Copyright (c) 2006,07 Swaroop Belur
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

package net.sf.jdec.jvminstructions.util;

import net.sf.jdec.jvminstructions.commandholders.IInstructionCommandHolder;
import net.sf.jdec.jvminstructions.factory.BaseInstructionFactoryBuilder;
import net.sf.jdec.jvminstructions.factory.IInstructionFactory;

import java.util.HashMap;
import java.util.Map;


public class InstructionHelper {

	public static boolean isInstructionPrimitiveType(String param) {

		if (param.equals(InstrConstants.INT) || param.equals(InstrConstants.BYTE)
				|| param.equals(InstrConstants.SHORT)
				|| param.equals(InstrConstants.CHAR) || param.equals(InstrConstants.LONG)
				|| param.equals(InstrConstants.FLOAT)
				|| param.equals(InstrConstants.DOUBLE)) {
			return true;
		}

		return false;
	}

	public static IInstructionCommandHolder getInstruction(String instrType,
			String varType, int opCode) {

		BaseInstructionFactoryBuilder base = BaseInstructionFactoryBuilder.baseInstrFactoryBuilder;

		Map params = new HashMap();
		params.put(InstrConstants.INSTR_TYPE, instrType);
		params.put(InstrConstants.VAR_TYPE, varType);
		params.put(InstrConstants.OPCODE_TYPE, new Integer(opCode));

		IInstructionFactory factory = base.newInstance(params);
		IInstructionCommandHolder instruction = factory.newInstance(params);

		factory = null;
		return instruction;

	}

}
