package net.sf.jdec.util;

import java.util.List;

import net.sf.jdec.constantpool.AbstractMethodParamterAnnotaions;
import net.sf.jdec.constantpool.AbstractRuntimeAnnotation;
import net.sf.jdec.constantpool.Annotation;
import net.sf.jdec.constantpool.AnnotationElementValue;
import net.sf.jdec.constantpool.RuntimeVisibleParameterAnnotations;
import net.sf.jdec.constantpool.Annotation.ElementValuePair;
import net.sf.jdec.constantpool.AnnotationElementValue.ArrayValue;
import net.sf.jdec.constantpool.AnnotationElementValue.EnumConstValue;

/*******************************************************************************
 * @author swaroop belur(belurs)
 ******************************************************************************/
public class AnnotationHelper {

	public static String toString(Annotation ann) {
		StringBuffer buffer = new StringBuffer();
		String desc = ann.getType();
		if(desc.startsWith("L") && desc.endsWith(";")){
			desc = desc.substring(1,desc.length()-1);
			
		}
		desc = desc.replaceAll("/", ".");
		buffer.append(" @").append(desc).append("(");
		List valuepairs = ann.getElementValuePairs();
		if (valuepairs.size() == 0) {
			return buffer.append(")").toString();
		}
		StringBuffer temp = new StringBuffer();
		for (int z = 0; z < valuepairs.size(); z++) {
			ElementValuePair pair = (ElementValuePair) valuepairs.get(z);
			AnnotationElementValue elementval = pair.getValue();
			if (elementval != null) {
				if (elementval.isPrimitiveType()) {
					String eleVal = elementval.getConstantValueIndexValue();
					String eleName = pair.getName();
					if (elementval.getTagAsChar() == 's') {
						temp.append(eleName + " = " + "\"" + eleVal + "\"");
					} else {
						temp.append(eleName + " = " + eleVal);
					}
					
				}
				if(elementval.getClassInfoIndex() != -999){
					String clzType = elementval.getReifiedReturnType();
					if(clzType.startsWith("L") && clzType.endsWith(";")){
						clzType = clzType.substring(1,clzType.length()-1);
						clzType = clzType.replaceAll("/", ".");
						clzType = clzType + ".class";
					}
					String eleName = pair.getName();
					temp.append(eleName + " = " + clzType);
					
					
				}
				if(elementval.getEnumConstValueRef() != null){
					EnumConstValue enumv = elementval.getEnumConstValueRef();
					String binName = enumv.getBinaryName();
					String simpleName = enumv.getSimpleName();
					String eleName = pair.getName();
					if(binName.startsWith("L") && binName.endsWith(";")){
						binName = binName.substring(1,binName.length()-1);
						binName = binName.replaceAll("/", ".");
					}
					temp.append(eleName + " = " + binName+"."+simpleName);
				}
				if(elementval.getAnnotationValue() != null){
					String nestedAnn = toString(elementval.getAnnotationValue());
					temp.append(nestedAnn);
				}
				if(elementval.getArrayValueRef() != null){
					ArrayValue av = elementval.getArrayValueRef();
					List arrayElements = av.getElements();
					String name = av.getName();
					
					temp.append(name + " = {");
					for(int k=0;k<arrayElements.size();k++){
						AnnotationElementValue  arrayPairVal = (AnnotationElementValue)arrayElements.get(k);
						
						String  arrayComp = arrayPairVal.getConstantValueIndexValue();
						if(arrayPairVal.getArrayType().equals("int")){
							temp.append(arrayComp);
						}
						else if(arrayPairVal.getArrayType().equals("float")){
							temp.append(arrayComp);
						}
						else if(arrayPairVal.getArrayType().equals("long")){
							temp.append(arrayComp);
						}
						else if(arrayPairVal.getArrayType().equals("double")){
							temp.append(arrayComp);
						}
						
						else if(arrayPairVal.getArrayType().equals("String")){
								temp.append("\""+arrayComp+"\"");
						}
						else if(arrayPairVal.getArrayType().equals("class")){
							String clzType = arrayPairVal.getReifiedReturnType();
							if(clzType.startsWith("L") && clzType.endsWith(";")){
								String clazz = clzType.substring(1,clzType.length()-1);
								clazz = clazz.replaceAll("/", ".");
								clazz = clazz + ".class";
								temp.append(clazz);
							}
							else{
								temp.append(clzType);
							}
						}
						else if(arrayPairVal.getArrayType().equals("enum")){
							EnumConstValue enumval = arrayPairVal.getEnumConstValueRef();
							String clzType = enumval.getBinaryName();
							if(clzType.startsWith("L") && clzType.endsWith(";")){
								String clazz = clzType.substring(1,clzType.length()-1);
								clazz = clazz.replaceAll("/", ".");
								clzType = clazz;
								
							}
							
							temp.append(clzType+"."+enumval.getSimpleName());
						}
						else if(arrayPairVal.getArrayType().equals("Annotation")){
							Annotation ann_val = arrayPairVal.getAnnotationValue();
							String nestedAnn = toString(ann_val);
							temp.append(nestedAnn);
						}	
						
						
						if(k < arrayElements.size() - 1)
							temp.append(",");
						
					}
					temp.append("}");
				}
				
			}
			if(z < valuepairs.size() - 1)
				temp.append(",");
		}

		return buffer.append(temp.toString()).toString()+")\n";
	}

	public static String toString(AbstractRuntimeAnnotation rva) {
		if (rva == null)
			return "";
		List list = rva.getAnnotations();
		StringBuffer buffer = new StringBuffer();
		for (int z = 0; z < list.size(); z++) {
			Annotation a = (Annotation) list.get(z);
			buffer.append(toString(a));
			if(list.size() > 1 && z < list.size() - 1){
				//buffer.append("\n");
			}

		}
		return "\n"+buffer.toString()+"\n";

	}
	public static String toString(AbstractMethodParamterAnnotaions rva) {
		if (rva == null)
			return "";
		List list = rva.getAnnotations();
		StringBuffer buffer = new StringBuffer();
		for (int z = 0; z < list.size(); z++) {
			Annotation a = (Annotation) list.get(z);
			buffer.append(toString(a));
			if(list.size() > 1 && z < list.size() - 1){
				//buffer.append("\n");
			}

		}
		return "\n"+buffer.toString()+"\n";

	}
}
