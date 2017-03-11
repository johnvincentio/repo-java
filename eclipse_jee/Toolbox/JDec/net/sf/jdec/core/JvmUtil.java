/*
 *  JvmUtil.java Copyright (c) 2006,07 Swaroop Belur 
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

package net.sf.jdec.core;

import java.util.ArrayList;
import java.util.HashMap;

public class JvmUtil {

	private static HashMap instructionMap=new HashMap();
	private static int totalOpCodesToMap=JvmOpCodes.totalJvmOpCodes;


	static
	{
		JvmOpCodes.storeAllOpcodes();
		JvmOpCodes.bytesToSkip();
	}


	public JvmUtil()
	{

	}


	public  void populateMap()
	{
		ArrayList allOpcodes=JvmOpCodes.getAllJvmOpcodes();
		ArrayList bytesToSkip=JvmOpCodes.getBytesToSkip();



		/**
		 * Adding a check here. This should never happen actually
		 * Need to be careful to ensure that correct number of opcodes
		 * are stored.
		 */

		if(totalOpCodesToMap!=allOpcodes.size())
			totalOpCodesToMap=allOpcodes.size();


		for(int start=0;start<totalOpCodesToMap;start++)
		{
			Integer jvmInst=(Integer)allOpcodes.get(start);
			Integer skip=(Integer)bytesToSkip.get(start);
			instructionMap.put(jvmInst,skip);
		}

		net.sf.jdec.main.ConsoleLauncher.setInstrMap(instructionMap);
	}




}
