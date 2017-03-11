package net.sf.jdec.jvminstructions.util;

import java.io.Serializable;

/*
*  InstrConstants.java Copyright (c) 2006,07 Swaroop Belur
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
public class InstrConstants implements Serializable {

    public static final String FACTORY_TYPE = "factoryType";
    public static final String NEW_INSTANCE_TYPE = "newInstanceType";
    public static final String LOAD_INSTR_TYPE = "load";
    public static final String BRANCH_INSTR_TYPE = "branch";
    public static final String UNCLASSIFIED_INSTR_TYPE = "UNCLASSIFIED";
    public static final String OPCODE_TYPE = "opcode";
    public static final String STORE_INSTR_TYPE = "store";
    public static final String METHOD_INSTR_TYPE = "method";
    public static final String INSTR_TYPE = "instructionType";  /** Load, store,method **/
    public static final String PRIMITIVE_TYPE = "primitiveType";
    public static final String REF_TYPE = "referenceType";
    public static final String VAR_TYPE = "variableType"; /** int,long,Object etc **/
    public static final String INT = "int";
    public static final String BYTE = "byte";
    public static final String SHORT= "short";
    public static final String CHAR = "char";
    public static final String LONG = "long";
    public static final String FLOAT = "float";
    public static final String DOUBLE = "double";
    public static final String BOOLEAN = "boolean";
    public static final String OBJECT = "object";
    public static final String COMPLEX = "complex";
    public static final String SIMPLE = "simple";

    public static final Object NEWOBJECT = new Object();


  public static final String INVOKE_INTERFACE = "interface";
  public static final String INVOKE_SPECIAL = "special";
}
