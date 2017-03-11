package net.sf.jdec.core;

import java.util.ArrayList;
import java.util.List;

/***
 @author swaroop belur(belurs)
 ***/
public class LocalVariableTypeTable {
	
	private int local_variable_type_table_length;
	
	public class LocalVariableTypeTableElement{
		private String name;
		
		private String signature;
		
		private int index;

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getSignature() {
			return signature;
		}

		public void setSignature(String signature) {
			this.signature = signature;
		}
		
		
		
	}
	
	public List elements = new ArrayList();
	
	public String getGenericSignatureForLocalVariable(int index){
		
		for(int z=0;z<elements.size();z++){
			LocalVariableTypeTableElement e = (LocalVariableTypeTableElement)elements.get(z);
			if(e.getIndex() == index){
				return e.getSignature();
			}
		}
		return null;
	}
	
	
}

