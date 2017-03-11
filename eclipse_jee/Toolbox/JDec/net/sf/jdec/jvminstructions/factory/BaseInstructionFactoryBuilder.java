package net.sf.jdec.jvminstructions.factory;

import net.sf.jdec.jvminstructions.util.InstrConstants;

import java.util.Map;

/*
 *  BaseInstructionFactoryBuilder.java Copyright (c) 2006,07 Swaroop Belur
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
public class BaseInstructionFactoryBuilder extends
		AbstractInstructionFactoryBuilder {

	protected Map receivedParameters = null;

	public static final BaseInstructionFactoryBuilder baseInstrFactoryBuilder = new BaseInstructionFactoryBuilder();

	public IInstructionFactory newInstance(Map parameters)
			throws FactoryBuilderException {

		receivedParameters = parameters;
		doCheck();

		Object param = parameters.get(InstrConstants.INSTR_TYPE);
		String string = param.toString();

		if (string.equals(InstrConstants.BRANCH_INSTR_TYPE)) {
			return new BranchInstructionFactoryBuilder()
					.newInstance(parameters);
		} else if (string.equals(InstrConstants.UNCLASSIFIED_INSTR_TYPE)) {
			return new UnclassifiedFactoryBuilder()
					.newInstance(parameters);
		} else if (string.equals(InstrConstants.LOAD_INSTR_TYPE)) {
			return new LoadInstructionFactoryBuilder().newInstance(parameters);
		} else if (string.equals(InstrConstants.STORE_INSTR_TYPE)) {
			return new StoreInstructionFactoryBuilder().newInstance(parameters);
		} else if (string.equals(InstrConstants.METHOD_INSTR_TYPE)) {
			return new MethodInstructionFactoryBuilder()
					.newInstance(parameters);
		} else {
			throw new FactoryBuilderException(
					"Invalid paramters passed to BaseInstructionFactoryBuilder [Unsopperted instruction type]");
		}

	}

	public void doCheck() throws FactoryBuilderException {
		Object param = receivedParameters.get(InstrConstants.INSTR_TYPE);
		if (param == null) {
			throw new FactoryBuilderException(
					"Invalid paramters passed to BaseInstructionFactoryBuilder [Missing Instruction type]");
		}

	}
}
