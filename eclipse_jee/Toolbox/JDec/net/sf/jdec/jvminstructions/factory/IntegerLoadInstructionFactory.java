package net.sf.jdec.jvminstructions.factory;

import java.util.Map;

import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.jvminstructions.commandholders.Baload;
import net.sf.jdec.jvminstructions.commandholders.Bipush;
import net.sf.jdec.jvminstructions.commandholders.Caload;
import net.sf.jdec.jvminstructions.commandholders.Checkcast;
import net.sf.jdec.jvminstructions.commandholders.D2I;
import net.sf.jdec.jvminstructions.commandholders.F2I;
import net.sf.jdec.jvminstructions.commandholders.I2B;
import net.sf.jdec.jvminstructions.commandholders.I2C;
import net.sf.jdec.jvminstructions.commandholders.I2D;
import net.sf.jdec.jvminstructions.commandholders.I2F;
import net.sf.jdec.jvminstructions.commandholders.I2L;
import net.sf.jdec.jvminstructions.commandholders.I2S;
import net.sf.jdec.jvminstructions.commandholders.IConst_0;
import net.sf.jdec.jvminstructions.commandholders.IConst_1;
import net.sf.jdec.jvminstructions.commandholders.IConst_2;
import net.sf.jdec.jvminstructions.commandholders.IConst_3;
import net.sf.jdec.jvminstructions.commandholders.IConst_4;
import net.sf.jdec.jvminstructions.commandholders.IConst_5;
import net.sf.jdec.jvminstructions.commandholders.IInstructionCommandHolder;
import net.sf.jdec.jvminstructions.commandholders.ILoad;
import net.sf.jdec.jvminstructions.commandholders.ILoad_0;
import net.sf.jdec.jvminstructions.commandholders.ILoad_1;
import net.sf.jdec.jvminstructions.commandholders.ILoad_2;
import net.sf.jdec.jvminstructions.commandholders.ILoad_3;
import net.sf.jdec.jvminstructions.commandholders.Iadd;
import net.sf.jdec.jvminstructions.commandholders.Iaload;
import net.sf.jdec.jvminstructions.commandholders.Iand;
import net.sf.jdec.jvminstructions.commandholders.IconstM1;
import net.sf.jdec.jvminstructions.commandholders.Idiv;
import net.sf.jdec.jvminstructions.commandholders.Iinc;
import net.sf.jdec.jvminstructions.commandholders.Imul;
import net.sf.jdec.jvminstructions.commandholders.Ineg;
import net.sf.jdec.jvminstructions.commandholders.InstanceOf;
import net.sf.jdec.jvminstructions.commandholders.Ior;
import net.sf.jdec.jvminstructions.commandholders.Irem;
import net.sf.jdec.jvminstructions.commandholders.Ishl;
import net.sf.jdec.jvminstructions.commandholders.Ishr;
import net.sf.jdec.jvminstructions.commandholders.Isub;
import net.sf.jdec.jvminstructions.commandholders.Iushr;
import net.sf.jdec.jvminstructions.commandholders.Ixor;
import net.sf.jdec.jvminstructions.commandholders.L2I;
import net.sf.jdec.jvminstructions.commandholders.Saload;
import net.sf.jdec.jvminstructions.commandholders.Sipush;
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
public class IntegerLoadInstructionFactory implements IInstructionFactory {

	public IInstructionCommandHolder newInstance(Map parameters) {

		Integer temp = (Integer) parameters.get(InstrConstants.OPCODE_TYPE);
		if (temp == null) {
			throw new IllegalArgumentException("Opcode type cannot be null");
		}

		int opcodevalue = temp.intValue();
		switch (opcodevalue) {

		case JvmOpCodes.I2B: {
			return new I2B();
		}
		case JvmOpCodes.I2C: {
			return new I2C();
		}
		case JvmOpCodes.I2D: {
			return new I2D();
		}
		case JvmOpCodes.I2F: {
			return new I2F();
		}
		case JvmOpCodes.I2L: {
			return new I2L();
		}
		case JvmOpCodes.I2S: {
			return new I2S();
		}
		case JvmOpCodes.IADD: {
			return new Iadd();
		}
		case JvmOpCodes.IALOAD: {
			return new Iaload();
		}
		case JvmOpCodes.IAND: {
			return new Iand();
		}
		case JvmOpCodes.ICONST_0: {
			return new IConst_0();
		}
		case JvmOpCodes.ICONST_1: {
			return new IConst_1();
		}
		case JvmOpCodes.ICONST_2: {
			return new IConst_2();
		}
		case JvmOpCodes.ICONST_3: {
			return new IConst_3();
		}
		case JvmOpCodes.ICONST_4: {
			return new IConst_4();
		}
		case JvmOpCodes.ICONST_5: {
			return new IConst_5();
		}
		case JvmOpCodes.ICONST_M1: {
			return new IconstM1();
		}
		case JvmOpCodes.IDIV: {
			return new Idiv();
		}
		
		case JvmOpCodes.IINC: {
			return new Iinc();
		}
		case JvmOpCodes.ILOAD: {
			return new ILoad();
		}
		case JvmOpCodes.ILOAD_0: {
			return new ILoad_0();
		}
		case JvmOpCodes.ILOAD_1: {
			return new ILoad_1();
		}
		case JvmOpCodes.ILOAD_2: {
			return new ILoad_2();
		}
		case JvmOpCodes.ILOAD_3: {
			return new ILoad_3();
		}
		
		case JvmOpCodes.IMUL: {
			return new Imul();
		}
		case JvmOpCodes.INEG: {
			return new Ineg();
		}
		
		case JvmOpCodes.INSTANCEOF: {
			return new InstanceOf();
		}
		
		case JvmOpCodes.IOR: {
			return new Ior();
		}
		
		case JvmOpCodes.IREM: {
			return new Irem();
		}
		
		case JvmOpCodes.ISHL: {
			return new Ishl();
		}
		
		case JvmOpCodes.ISHR: {
			return new Ishr();
		}
		
		case JvmOpCodes.ISUB: {
			return new Isub();
		}
		
		case JvmOpCodes.IUSHR: {
			return new Iushr();
		}
		
		case JvmOpCodes.IXOR: {
			return new Ixor();
		}
		
		case JvmOpCodes.BALOAD:
			return new Baload();
			
		case JvmOpCodes.BIPUSH:
			return new Bipush();
			
		case JvmOpCodes.CALOAD:
			return new Caload();
		
		case JvmOpCodes.CHECKCAST:
			return new Checkcast();	
			

		case JvmOpCodes.D2I:
			return new D2I();
			
		case JvmOpCodes.F2I:
			return new F2I();

		case JvmOpCodes.SALOAD: {
			return new Saload();
		}
		
		case JvmOpCodes.SIPUSH:{
			return new Sipush();
		}
		
		case JvmOpCodes.L2I:
			return new L2I();
		
		default:
			throw new IllegalArgumentException(
					"Input argument opcodetype did not match any valid jvminstruction values");

		}

	}
}
