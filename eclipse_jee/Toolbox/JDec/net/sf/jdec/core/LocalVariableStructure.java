/*
 *  LocalVariableStructure.java Copyright (c) 2006,07 Swaroop Belur
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
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Map;

import net.sf.jdec.exceptions.ApplicationException;
import net.sf.jdec.util.Util;

public class LocalVariableStructure {

	private java.lang.String methodDescriptor = "";

	private ArrayList methodLocalVaribales = new ArrayList();

	public LocalVariableStructure() {
		super();

	}

	/***************************************************************************
	 * 
	 * This method Gives the Full list of local varibles for this method See
	 * 
	 * @link#getVariabelAtIndex Method for easier access
	 * @return List of Variables (ArrayList)
	 */

	public ArrayList getMethodLocalVaribales() {
		return methodLocalVaribales;
	}

	public void setMethodLocalVaribales(ArrayList methodLocalVaribales) {
		this.methodLocalVaribales = methodLocalVaribales;
	}

	public java.lang.String getMethodDescriptor() {
		return methodDescriptor;
	}

	public void setMethodDescription(java.lang.String methodName) {
		this.methodDescriptor = methodName;
	}

	/***************************************************************************
	 * Use This method to directly get the LocalVarible Object at a particular
	 * index in the table
	 * 
	 * @throws ApplicationException
	 *             If some problem occurs while accessing the list of variables
	 * @param index
	 *            The Index of local variable in the table
	 * @return LocalVariable Object if successful or ApplicationException
	 */

	/**
	 * sbelur: **** IMPORTANT **** USE IT ONLY FOR -g:none option
	 * 
	 * @param index
	 * @return
	 */
	public LocalVariable getVariabelAtIndex(int index)// ,java.lang.String
	// type)
	{
		LocalVariable localVar = null;
		LocalVariable temp = null;
		Iterator iter = methodLocalVaribales.iterator();
		int count = 0;
		while (iter.hasNext()) {
			temp = (LocalVariable) iter.next();
			if (temp != null && temp.getIndexPos() == index) {
				localVar = temp;
				count++;
			}
		}
		numberOfSimilarIndexVars = count;
		occurances.put(new Integer(index),
				new Integer(numberOfSimilarIndexVars));

		return localVar;

	}

	Hashtable occurances = new Hashtable();

	// Again use only for -g:none option

	public LocalVariable getVariableForLoadOrStorePos(int index, int Pos) {
		LocalVariable localVar = null;
		LocalVariable temp = null;
		Iterator iter = methodLocalVaribales.iterator();

		while (iter.hasNext()) {
			temp = (LocalVariable) iter.next();
			if (temp != null
					&& temp.getIndexPos() == index
					&& (temp.getLoadOrStorePosUsedToCreateVraiable() == Pos || temp
							.getLoadOrStorePosUsedToCreateVraiable() == 99999)) {
				localVar = temp;
				break;
			}
		}

		return localVar;

	}

	public int getNumberOfSimilarIndexVars(int index) {

		if (occurances == null || occurances.size() == 0)
			return 0;
		if ((occurances.get(new Integer(index)) == null))
			return 0;
		return ((Integer) occurances.get(new Integer(index))).intValue();
	}

	int numberOfSimilarIndexVars = -1;

	/**
	 * use it for -g option
	 * 
	 * @param index
	 * @param blockIndex
	 * @return
	 */

	public LocalVariable getVariabelAtIndex(int index, int blockIndex) {

		int lVarIndex = 0;
		LocalVariable[] localVarArr = new LocalVariable[methodLocalVaribales
				.size()];
		Iterator iter = methodLocalVaribales.iterator();
		while (iter.hasNext()) {
			LocalVariable localVar = (LocalVariable) iter.next();
			if (localVar.getIndexPos() == index) {
				int start_pc = localVar.getBlockStart();
				int end_pc = localVar.getBlockEnd();

				if (blockIndex >= start_pc && blockIndex <= end_pc) {
					return localVar;
				}

			}
		}

		iter = methodLocalVaribales.iterator();
		while (iter.hasNext()) {
			LocalVariable localVar = (LocalVariable) iter.next();
			if (localVar.getIndexPos() == index) {
				int start_pc = localVar.getBlockStart();
				int end_pc = localVar.getBlockEnd();

				if (localVar.getLoadOrStorePosUsedToCreateVraiable() == blockIndex) {
					return localVar;
				}

			}
		}

		iter = methodLocalVaribales.iterator();
		while (iter.hasNext()) {
			LocalVariable localVar = (LocalVariable) iter.next();
			if (localVar.getIndexPos() == index) {
				int start_pc = localVar.getBlockStart();
				int end_pc = localVar.getBlockEnd();

				if (localVar.getLoadOrStorePosUsedToCreateVraiable() == 99999) {
					return localVar;
				}

			}
		}

		return null;
	}

	public void setVariabelAtIndex(int index, LocalVariable localVar) {
		this.methodLocalVaribales.set(index, localVar);

	}

	public void addLocalVariable(LocalVariable local) {
		// System.out.println(local.getDataType());
		this.methodLocalVaribales.add(local);
	}

	/**
	 * NOTE: Use it only for -g:none option
	 * 
	 * @param index
	 * @param type
	 * @return
	 */

	public LocalVariable getVariabelAtIndex(int index, java.lang.String type,
			Map datatypesForParams) {
		LocalVariable localVar = null;
		LocalVariable temp = null;
		Iterator iter = methodLocalVaribales.iterator();
		int count = 0;
		while (iter.hasNext()) {
			temp = (LocalVariable) iter.next();
			/*
			 * if(temp!=null && temp.getIndexPos() == index)// &&
			 * temp.getPassedDataTypeWhileCreatingWithOutMinusG().equals(type)) {
			 * localVar=temp; count++; }
			 */

			if (temp != null && temp.getIndexPos() == index)// &&
			// temp.getPassedDataTypeWhileCreatingWithOutMinusG().equals(type))
			{
				localVar = temp;
				count++;
			}

		}

		if (localVar == null) {
			if (datatypesForParams != null && datatypesForParams.size() > 0) {
				Iterator it = datatypesForParams.keySet().iterator();
				while (it.hasNext()) {

					Integer in = (Integer) it.next();
					int iin = in.intValue();
					if (iin == index) {

						java.lang.String loctype = (java.lang.String) datatypesForParams
								.get(in);
						iter = methodLocalVaribales.iterator();

						while (iter.hasNext()) {
							temp = (LocalVariable) iter.next();
							loctype = loctype.trim();
							StringBuffer S = new StringBuffer("");
							Util.checkForImport(loctype, S);
							if (temp != null && temp.getIndexPos() == index
									&& temp.getDataType().equals(S.toString())) {
								localVar = temp;
								count = 1;
								break;
							}
						}

					}
				}

			}

		}

		numberOfSimilarIndexVars = count;
		occurances.put(new Integer(index),
				new Integer(numberOfSimilarIndexVars));

		return localVar;

	}

}
