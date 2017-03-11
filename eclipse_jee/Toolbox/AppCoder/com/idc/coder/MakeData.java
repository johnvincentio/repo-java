
package com.idc.coder;

import java.io.File;

/**
 * @author John Vincent
 *
 */

public class MakeData extends MakeCode {
	 
	public MakeData (CodeTable codeTable) {
		super (codeTable);
	}
	/*
	* Section broker, for example: UserBroker.java
	*
	* 1 - RentalJobLocationsInfo
	* 2 - RentalJobLocationItemInfo
	* 3 - rentalJobLocationsInfo
	* 4 - rentalJobLocationItemInfo
	*/
	public String makeCode() {
		String strFile = "DataTemplate";
		Template template = new Template(new File(strFile));
		template.process();
		template.replace(1, getCollectionClassName());
		template.replace(2, getBeanClassName());
		template.replace(3, getCollectionVariableName());
		template.replace(4, getBeanVariableName());
		template.replace(5, getPackageName());
		template.replace(6, getBaseClassName());
		return template.getTemplate();
	}
}
