package net.sf.jdec.constantpool;

import java.util.ArrayList;
import java.util.List;

/***
 @author swaroop belur(belurs)
 ***/
public abstract class AbstractMethodParamterAnnotaions {

	private int numParameters;
	private List paramaterAnnotations = new ArrayList();
	
	private ParameterAnnotations parameterAnnotationsRef = new ParameterAnnotations();

	public AbstractMethodParamterAnnotaions() {
		super();
	}
	public class ParameterAnnotations {
		private int annotationCount;
		
		private List annotations = new ArrayList();
	}
	
	public void addAnnotation(Annotation a){
		parameterAnnotationsRef.annotations.add(a);
	}
	
	public List getAnnotations(){
		return parameterAnnotationsRef.annotations;
	}
	public int getNumParameters() {
		return numParameters;
	}
	public void setNumParameters(int numParameters) {
		this.numParameters = numParameters;
	}
	public List getParamaterAnnotations() {
		return paramaterAnnotations;
	}
	public void setParamaterAnnotations(List paramaterAnnotations) {
		this.paramaterAnnotations = paramaterAnnotations;
	}
	public ParameterAnnotations getParameterAnnotationsRef() {
		return parameterAnnotationsRef;
	}
	public void setParameterAnnotationsRef(
			ParameterAnnotations parameterAnnotationsRef) {
		this.parameterAnnotationsRef = parameterAnnotationsRef;
	}
	
	
	
}
