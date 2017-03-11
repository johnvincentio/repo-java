package net.sf.jdec.settings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.jdec.commonutil.Objects;

/*******************************************************************************
 * @author swaroop belur
 */

public class VariableSettings {

	/***
	 * !!! IMP !!!
	 * 
	 * When u add a variable check whether it has to be cleaned
	 * up after decompiling a class. In that case add it to reset
	 * method
	 */
	
	private Set registeredVariables = new HashSet();
	
	private Map prefixOccurences = new HashMap();
	
	

	VariableSettings() {
	}

	/**
	 * CALLERS : - Need to check for null return .
	 * 
	 * @param packageName
	 * @return
	 */
	public String getPrefixName(String packageName, String className , String dataType) {
		
		if(className != null)className = className.trim();
		if(dataType != null)dataType=dataType.trim();
		if(packageName != null)packageName = packageName.trim();
		Iterator iterator = registeredVariables.iterator();
		while (iterator.hasNext()) {
			Variable variable = (Variable) iterator.next();
			
			if (!Objects.isEmpty(variable.getPkg())) {
				if (variable.getPkg().equals(packageName)) {
					if (variable.getClassName().equals(className)) {
						return variable.getPrefix();
					}
				}
			} else {
				if (Objects.isTrue(variable.getDefaultPackage())
						&& packageName == null) {
					if (variable.getClassName().equals(className)) {
						return variable.getPrefix();
					}
				}
			}
			
			/***
			 * Primitive check
			 */
			if(Objects.isEmpty(className)){
				if(Objects.isPrimitive(dataType)){
					if(variable.getClassName().equals(dataType)){
						return variable.getPrefix();
					}
				}
			}
			

		}
		if(dataType != null && dataType.indexOf("[]") != -1){
			if(className == null)className="";
			if(className.indexOf("[]") == -1)
				className = className+"array";
			else{
				className = className.substring(0,className.indexOf("[]"));
				if(className.length() > 0)
					className=className.trim();
			}
		}
		if(!Objects.isEmpty(className))
			return "a"+className;
		return "a"+dataType;
	}

	/***************************************************************************
	 * UI side : Should catch ConfigurationException and show message to user
	 * AND then exit. Console side: Should catch and exit.
	 * 
	 * @param v
	 */
	public void addVariable(Variable v) {
		
		if (!Objects.isEmpty(v.getIsPrimitive()) && Objects.isTrue(v.getIsPrimitive())){
			if (!Objects.isEmpty(v.getPrefix())) {
				registeredVariables.add(v);
				return;
			}
		}
				
				
		if ( Objects.isEmpty(v.getPkg())
				&& (Objects.isEmpty(v.getDefaultPackage()) || 
						!v.getDefaultPackage().trim().equalsIgnoreCase("true"))) {

			/*******************************************************************
			 * TODO : add a log statement here that this variable was ignored
			 */
			return;
		}
		if (Objects.isEmpty(v.getClassName())) {
			/*******************************************************************
			 * TODO : add a log statement here that this variable was ignored
			 */
			return;
		}
		if (Objects.isEmpty(v.getPrefix())) {
			/*******************************************************************
			 * TODO : add a log statement here that this variable was ignored
			 */
			return;
		}
		if(!Objects.isTrue(v.getIsPrimitive())){
			registeredVariables.add(v);
		}
	}

	public void reset() {
		prefixOccurences = new HashMap();
	}

	
	public int getPrefixOccurence(String prefix){
		if(prefixOccurences.get(prefix) != null){
			Integer  in = (Integer)prefixOccurences.get(prefix);
			prefixOccurences.put(prefix, new Integer(in.intValue()+1));
		}
		else{
			prefixOccurences.put(prefix, new Integer(1));
		}
		return ((Integer)prefixOccurences.get(prefix)).intValue();
	}
}
