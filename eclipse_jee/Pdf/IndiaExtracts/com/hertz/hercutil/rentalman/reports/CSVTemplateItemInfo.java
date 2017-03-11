/********************************************************************
*			Copyright (c) 2006 The Hertz Corporation				*
*			  All Rights Reserved.  (Unpublished.)					*
*																	*
*		The information contained herein is confidential and		*
*		proprietary to The Hertz Corporation and may not be			*
*		duplicated, disclosed to third parties, or used for any		*
*		purpose not expressly authorized by it.  Any unauthorized	*
*		use, duplication, or disclosure is prohibited by law.		*
*																	*
*********************************************************************/

package com.hertz.hercutil.rentalman.reports;

import java.io.Serializable;

/**
 * Class to encapsulate one CSV template
 * 
 * @author John Vincent
 */

public class CSVTemplateItemInfo implements Serializable {
	private boolean include;
	public CSVTemplateItemInfo (boolean include) {this.include = include;}
	public boolean isInclude() {return include;}
	public String toString() {
		return "("+isInclude()+")";
	}
}
