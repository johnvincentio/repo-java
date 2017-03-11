package net.sf.jdec.jvminstructions.factory;

import java.util.Map;

public class BranchInstructionFactoryBuilder extends
		BaseInstructionFactoryBuilder {

	public BranchInstructionFactoryBuilder() {
	}

    public IInstructionFactory newInstance(Map parameters) throws FactoryBuilderException {
        return new BranchInstructionFactory();   
    }

   

}

