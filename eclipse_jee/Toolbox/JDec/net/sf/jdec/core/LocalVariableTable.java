/*
 *  LocalVariable.java Copyright (c) 2006,07 Swaroop Belur 
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

import java.util.Hashtable;

public class LocalVariableTable {

	private  Hashtable allLocalVaraibles=new Hashtable();   // This stores all local variables for this Class
	
	
	private static LocalVariableTable ref=null;
	
	private static boolean classCompiledWithDebugOption=false;
	private LocalVariableTable() {
		classCompiledWithDebugOption=true;
	}
	
	public static LocalVariableTable getInstance()
	{
		if(ref==null){
			ref=new LocalVariableTable();
			return ref;
		}
		else
			return ref;
	}


	public void addEntry(String methodDesc,LocalVariableStructure localvarstructure)
	{
		allLocalVaraibles.put(methodDesc,localvarstructure);
	}

	public Hashtable getAllLocalVaraibles() {
		return allLocalVaraibles;
	}
	
	
	public LocalVariableStructure getLocaVarStructure(String mName)
	{
		
		if(allLocalVaraibles.size() > 0)
		{
			LocalVariableStructure structure=(LocalVariableStructure)allLocalVaraibles.get(mName);
			return structure;
		}
		 
		return null;
	}
	

	
	
	
	
	

}
