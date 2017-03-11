package net.sf.jdec.constantpool;

import java.util.ArrayList;
import java.util.List;

/*******************************************************************************
 * @author swaroop belur(belurs)
 ******************************************************************************/
public class Annotation {
	
	public class ElementValuePair {

		private String name;

		private AnnotationElementValue value;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public AnnotationElementValue getValue() {
			return value;
		}

		public void setValue(AnnotationElementValue value) {
			this.value = value;
		}
		
		
		
		

	}
	
	private int typeIndex;

	private String type;
	private int pairCount;
	
	private List elementValuePairs = new ArrayList();

	public List getElementValuePairs() {
		return elementValuePairs;
	}

	public void setElementValuePairs(List elementValuePairs) {
		this.elementValuePairs = elementValuePairs;
	}

	public int getPairCount() {
		return pairCount;
	}

	public void setPairCount(int pairCount) {
		this.pairCount = pairCount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		if(type.indexOf("L") != -1 && type.indexOf(";") != -1){
			int start = type.indexOf("L");
			int end = type.indexOf(";");
			type = type.substring(start+1,end);
		}
		this.type = type;
	}

	public int getTypeIndex() {
		return typeIndex;
	}

	public void setTypeIndex(int typeIndex) {
		this.typeIndex = typeIndex;
	}
	
	
	
}
