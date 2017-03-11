package net.sf.jdec.jvminstructions.factory;

import java.util.Map;

import net.sf.jdec.jvminstructions.util.InstrConstants;

/*
*  PrimitiveStoreInstructionFactoryBuilder.java Copyright (c) 2006,07 Swaroop Belur
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
public class PrimitiveStoreInstructionFactoryBuilder extends StoreInstructionFactoryBuilder{

     public IInstructionFactory newInstance(Map parameters) throws FactoryBuilderException {
    	 receivedParameters = parameters;
         doCheck();
         Object param = receivedParameters.get(InstrConstants.VAR_TYPE);
         if(param.equals(InstrConstants.INT)){
           return new IntegerStoreInstructionFactory();  
         }else if(param.equals(InstrConstants.LONG)){
            return new LongStoreInstructionFactory();
         }
         else if(param.equals(InstrConstants.FLOAT)){
           return new FloatStoreInstructionFactory();
         }
         else if(param.equals(InstrConstants.DOUBLE)){
           return new DoubleStoreInstructionFactory();
         }
         else if(param.equals(InstrConstants.BOOLEAN)){
             return new IntegerStoreInstructionFactory();
         }
         else if(param.equals(InstrConstants.BYTE)){
             return new IntegerStoreInstructionFactory();
         }
         else if(param.equals(InstrConstants.SHORT)){
             return new IntegerStoreInstructionFactory();
         }
         else if(param.equals(InstrConstants.CHAR)){
             return new IntegerStoreInstructionFactory();
         }else{
              throw new FactoryBuilderException("Invalid paramters passed to PrimitiveStoreInstructionFactoryBuilder [Invalid Variable type]");
         }
     }
     
     public void doCheck() throws FactoryBuilderException {
         Object param = receivedParameters.get(InstrConstants.VAR_TYPE);
         if(param == null){
             throw new FactoryBuilderException("Invalid paramters passed to PrimitiveStoreInstructionFactoryBuilder [Missing Variable type]");
         }
}

}
