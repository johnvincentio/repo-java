/*
 *  LocalVariableHelper.java Copyright (c) 2006,07 Swaroop Belur 
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

import net.sf.jdec.commonutil.Objects;
import net.sf.jdec.main.ConsoleLauncher;

/***
 * TODO: Move all variabled utility methods over here.
 * @author sbelur
 *
 */
public class LocalVariableHelper {
	
	public static boolean checkForVariableExistenceWithName(String name){
		ArrayList atfront = GlobalVariableStore.getVariablesatfront();
		if(Objects.isNullOrEmpty(atfront)){
			return false;
		}
		for(int i=0;i<atfront.size();i++){
			DecompilerHelper.VariableAtFront v=(DecompilerHelper.VariableAtFront)atfront.get(i);
			if(v.getName().equals(name)){
				return true;
			}
		}
		return false;
	}
	
	
	
	public static boolean simpleTypeAlreadyAddedToImports(String type,
			StringBuffer fulltype) {

		ArrayList list = ConsoleLauncher.getAllImportedClasses();
		for (int s = 0; s < list.size(); s++) {
			String str = (String) list.get(s);
			if (str != null) {

				int index = str.lastIndexOf(".");
				if (index != -1) {
					String simple = str.substring(index + 1);
					if (simple.trim().equals(type.trim())) {
						fulltype.append(str);
						return true;
					}
				}
			}
		}

		return false;
	}
	
	public static String getGenericPartOfSignature(String signature){
		int start = signature.indexOf("<");
		int end = signature.lastIndexOf(">");
		if(start != -1){
			String genericPart = signature.substring(start,end+1);
			genericPart = Objects.getGenericPartOfGenericSignature(genericPart);
			genericPart = "<"+genericPart+">";
			return genericPart;
		}
		else{
			String genericPart = signature;
			genericPart = Objects.getGenericPartOfGenericSignature(genericPart);
			return genericPart;
		}
	}
}
