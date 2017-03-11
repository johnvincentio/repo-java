package net.sf.jdec.constantpool;

import java.util.ArrayList;
import java.util.List;

/*******************************************************************************
 * @author swaroop belur(belurs)
 ******************************************************************************/
public class AnnotationElementValue {

	private int tag;
	
	private String type;
	
	private String arrayType;
	
	private boolean isPrimitiveType = false;

	private String value;

	private int constantValueIndex;
	
	private EnumConstValue enumConstValueRef;

	public class EnumConstValue {

		private int typeNameIndex;

		private int constNameIndex;
		
		private String binaryName;
		
		private String simpleName;
		

		public int getConstNameIndex() {
			return constNameIndex;
		}

		public void setConstNameIndex(int constNameIndex) {
			this.constNameIndex = constNameIndex;
		}

		public int getTypeNameIndex() {
			return typeNameIndex;
		}

		public void setTypeNameIndex(int typeNameIndex) {
			this.typeNameIndex = typeNameIndex;
		}

		public String getBinaryName() {
			return binaryName;
		}

		public void setBinaryName(String binaryName) {
			this.binaryName = binaryName;
		}

		public String getSimpleName() {
			return simpleName;
		}

		public void setSimpleName(String simpleName) {
			this.simpleName = simpleName;
		}
		
		

	}

	private int classInfoIndex = -999;
	
	private String reifiedReturnType;
	

	private Annotation annotationValue;
	
	private ArrayValue arrayValueRef;

	private String constantValueIndexValue;
	
	public class ArrayValue{
		
		private int count;
		
		private String name;
		
		
		private List elements = new ArrayList();

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public List getElements() {
			return elements;
		}

		public void setElements(List elements) {
			this.elements = elements;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		
		
	}

	public Annotation getAnnotationValue() {
		return annotationValue;
	}

	public void setAnnotationValue(Annotation annotationValue) {
		this.annotationValue = annotationValue;
	}

	public int getClassInfoIndex() {
		return classInfoIndex;
	}

	public void setClassInfoIndex(int classInfoIndex) {
		this.classInfoIndex = classInfoIndex;
	}

	public int getConstantValueIndex() {
		return constantValueIndex;
	}

	public void setConstantValueIndex(int constantValueIndex) {
		this.constantValueIndex = constantValueIndex;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getTag() {
		return tag;
	}
	public char getTagAsChar() {
		return (char)tag;
	}
	public void setTag(int tag) {
		this.tag = tag;
	}

	public String getReifiedReturnType() {
		return reifiedReturnType;
	}

	public void setReifiedReturnType(String reifiedReturnType) {
		this.reifiedReturnType = reifiedReturnType;
	}

	public EnumConstValue getEnumConstValueRef() {
		return enumConstValueRef;
	}

	public void setEnumConstValueRef(EnumConstValue enumConstValueRef) {
		this.enumConstValueRef = enumConstValueRef;
	}

	public ArrayValue getArrayValueRef() {
		return arrayValueRef;
	}

	public void setArrayValueRef(ArrayValue arrayValueRef) {
		this.arrayValueRef = arrayValueRef;
	}

	public void setConstantValueIndexValue(String string) {
		this.constantValueIndexValue = string;
	}

	public String getConstantValueIndexValue() {
		return constantValueIndexValue;
	}

	public String getArrayType() {
		return arrayType;
	}

	public void setArrayType(String arrayType) {
		this.arrayType = arrayType;
	}

	public boolean isPrimitiveType() {
		return isPrimitiveType;
	}

	public void setPrimitiveType(boolean isPrimitiveType) {
		this.isPrimitiveType = isPrimitiveType;
	}
	
	

}
