package net.sf.jdec.constantpool;

import java.util.ArrayList;
import java.util.List;

/***
 @author swaroop belur(belurs)
 ***/
public class AbstractRuntimeAnnotation {

	private List annotations = new ArrayList();
	private int count;

	public AbstractRuntimeAnnotation() {
		super();
	}

	public List getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List annotations) {
		this.annotations = annotations;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void addAnnotation(Annotation a) {
		annotations.add(a);
	}

}
