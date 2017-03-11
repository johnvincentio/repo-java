package net.sf.jdec.jvminstructions.factory;

import net.sf.jdec.jvminstructions.util.InstrConstants;
import net.sf.jdec.jvminstructions.util.InstructionHelper;

import java.util.Map;

/*
*  StoreInstructionFactoryBuilder.java Copyright (c) 2006,07 Swaroop Belur
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
public class StoreInstructionFactoryBuilder extends BaseInstructionFactoryBuilder{

    public IInstructionFactory newInstance(Map parameters) throws FactoryBuilderException {
        receivedParameters = parameters;
        doCheck();
        String param = receivedParameters.get(InstrConstants.VAR_TYPE).toString();
        boolean isPrimitive = InstructionHelper.isInstructionPrimitiveType(param);
        return isPrimitive?new PrimitiveStoreInstructionFactoryBuilder().newInstance(parameters)
                          :new ObjectStoreInstructionFactoryBuilder().newInstance(parameters);
    }



    public void doCheck() throws FactoryBuilderException {
    
        Object param = receivedParameters.get(InstrConstants.VAR_TYPE);
        if (param == null) {
            throw new FactoryBuilderException("Invalid paramters passed to LoadInstructionFactoryBuilder [Missing Variable type]");
        }


    }
}
