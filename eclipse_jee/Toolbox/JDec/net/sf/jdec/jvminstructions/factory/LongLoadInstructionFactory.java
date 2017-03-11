package net.sf.jdec.jvminstructions.factory;

import java.util.Map;

import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.jvminstructions.commandholders.D2L;
import net.sf.jdec.jvminstructions.commandholders.F2L;
import net.sf.jdec.jvminstructions.commandholders.IInstructionCommandHolder;
import net.sf.jdec.jvminstructions.commandholders.LADD;
import net.sf.jdec.jvminstructions.commandholders.Laload;
import net.sf.jdec.jvminstructions.commandholders.Land;
import net.sf.jdec.jvminstructions.commandholders.Lcmp;
import net.sf.jdec.jvminstructions.commandholders.Lconst_0;
import net.sf.jdec.jvminstructions.commandholders.Lconst_1;
import net.sf.jdec.jvminstructions.commandholders.Ldc;
import net.sf.jdec.jvminstructions.commandholders.Ldc2_w;
import net.sf.jdec.jvminstructions.commandholders.Ldc_w;
import net.sf.jdec.jvminstructions.commandholders.Ldiv;
import net.sf.jdec.jvminstructions.commandholders.Lload;
import net.sf.jdec.jvminstructions.commandholders.Lload_0;
import net.sf.jdec.jvminstructions.commandholders.Lload_1;
import net.sf.jdec.jvminstructions.commandholders.Lload_2;
import net.sf.jdec.jvminstructions.commandholders.Lload_3;
import net.sf.jdec.jvminstructions.commandholders.Lmul;
import net.sf.jdec.jvminstructions.commandholders.Lneg;
import net.sf.jdec.jvminstructions.commandholders.Lor;
import net.sf.jdec.jvminstructions.commandholders.Lrem;
import net.sf.jdec.jvminstructions.commandholders.Lshl;
import net.sf.jdec.jvminstructions.commandholders.Lshr;
import net.sf.jdec.jvminstructions.commandholders.Lsub;
import net.sf.jdec.jvminstructions.commandholders.Lushr;
import net.sf.jdec.jvminstructions.commandholders.Lxor;
import net.sf.jdec.jvminstructions.util.InstrConstants;

/*
 *  LongLoadInstructionFactory.java Copyright (c) 2006,07 Swaroop Belur
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
public class LongLoadInstructionFactory implements IInstructionFactory {

	public IInstructionCommandHolder newInstance(Map parameters) {

		Integer temp = (Integer) parameters.get(InstrConstants.OPCODE_TYPE);
		if (temp == null) {
			throw new IllegalArgumentException("Opcode type cannot be null");
		}

		int opcodevalue = temp.intValue();
		switch (opcodevalue) {

		case JvmOpCodes.D2L:
			return new D2L();

		case JvmOpCodes.F2L:
			return new F2L();

		case JvmOpCodes.LADD:
			return new LADD();

		case JvmOpCodes.LALOAD:
			return new Laload();

		case JvmOpCodes.LAND:
			return new Land();

		case JvmOpCodes.LCMP:
			return new Lcmp();

		case JvmOpCodes.LCONST_0:
			return new Lconst_0();

		case JvmOpCodes.LCONST_1:
			return new Lconst_1();

		case JvmOpCodes.LDC:
			return new Ldc();

		case JvmOpCodes.LDC_W:
			return new Ldc_w();

		case JvmOpCodes.LDC2_W:
			return new Ldc2_w();

		case JvmOpCodes.LLOAD:
			return new Lload();

		case JvmOpCodes.LDIV:
			return new Ldiv();

		case JvmOpCodes.LLOAD_0:
			return new Lload_0();

		case JvmOpCodes.LLOAD_1:
			return new Lload_1();

		case JvmOpCodes.LLOAD_2:
			return new Lload_2();

		case JvmOpCodes.LLOAD_3:
			return new Lload_3();

		case JvmOpCodes.LMUL:
			return new Lmul();

		case JvmOpCodes.LNEG:
			return new Lneg();

		case JvmOpCodes.LREM:
			return new Lrem();

		case JvmOpCodes.LOR:
			return new Lor();

		case JvmOpCodes.LSHL:
			return new Lshl();

		case JvmOpCodes.LSHR:
			return new Lshr();

		case JvmOpCodes.LUSHR:
			return new Lushr();

		case JvmOpCodes.LSUB:
			return new Lsub();

		case JvmOpCodes.LXOR:
			return new Lxor();

		default:
			throw new IllegalArgumentException(
					"Input argument opcodetype did not match any valid jvminstruction values");

		}
	}
}
