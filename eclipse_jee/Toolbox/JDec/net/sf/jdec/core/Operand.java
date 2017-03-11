
/*
 *  Operand.java Copyright (c) 2006,07 Swaroop Belur 
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

public class Operand {

	private String operandName;
	private int operandType;
	private Object operandValue;
	private int category;

    public boolean isMultiDimension() {
        return isMultiDimension;
    }

    public void setMultiDimension(boolean multiDimension) {
        isMultiDimension = multiDimension;
    }

    private boolean isMultiDimension=false;



    private java.lang.String classType="";
    private java.lang.String localVarType="";

    public String getLocalVarType() {
        return localVarType;
    }

    public void setLocalVarType(String localVarType) {
        this.localVarType = localVarType;
    }

    public int getLocalVarIndex() {
        return localVarIndex;
    }

    public void setLocalVarIndex(int localVarIndex) {
        this.localVarIndex = localVarIndex;
    }

    private int localVarIndex;







	public String getOperandName() {
		return operandName;
	}

	public void setOperandName(String operandName) {
		this.operandName = operandName;
	}

	public int getOperandType() {
		return operandType;
	}

	public void setOperandType(int operandType) {
		this.operandType = operandType;
	}

	public java.lang.String getOperandValue() {
        if(operandValue!=null)
		return operandValue.toString();
        else
            return null;
	}

	public void setOperandValue(Object operandValue) {
		this.operandValue = operandValue;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

    public String getClassType() {
          return classType;
      }

      public void setClassType(String classType) {
          this.classType = classType;
      }




      public java.lang.String toString()
      {
    	  return operandValue.toString();
      }



}
