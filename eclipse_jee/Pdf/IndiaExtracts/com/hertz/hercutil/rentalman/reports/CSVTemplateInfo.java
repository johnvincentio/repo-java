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
import java.util.ArrayList;

/**
 * Class to encapsulate a CSV Template 
 * 
 * @author John Vincent
 */

public class CSVTemplateInfo implements Serializable {
	private ArrayList m_list = new ArrayList();
	private boolean header;
	public CSVTemplateInfo (boolean header) {
		this.header = header;
	}

	public void add (CSVTemplateItemInfo item) {m_list.add(item);}
	public void add (boolean flag) {m_list.add (new CSVTemplateItemInfo (flag));}
	public boolean isHeader() {return header;}

	/**
	 * Determine whether the data represented by parameter column should be included
	 * 
	 * @param column	Column number
	 * @return			true if data should be included
	 */
	public boolean isInclude (int column) {
		if (column < 0 || column >= m_list.size()) return false;
		return ((CSVTemplateItemInfo) m_list.get(column)).isInclude();
	}

	/**
	 * Apply data to the StringBuffer, if the data represented by parameter column should be included.
	 * 
	 * @param buf		Data will be applied to this object
	 * @param column	Column number
	 * @param data		Data
	 */
	public void applyColumn (StringBuffer buf, int column, String data) {
		applyColumn (buf, column, data, false);
	}

	/**
	 * Apply data to the StringBuffer, if the data represented by parameter column should be included.
	 * 
	 * @param buf		Data will be applied to this object
	 * @param column	Column number
	 * @param data		Data
	 * @param last		true if the last column of a row.
	 */
	public void applyColumn (StringBuffer buf, int column, String data, boolean last) {
		if (! isInclude (column)) return;
		String str = data.replace('"', ' ');
		buf.append ("\"").append (str).append("\"");
		if (! last) buf.append (",");
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((CSVTemplateItemInfo) m_list.get(i)).toString());
		return "("+isHeader()+"),"+"("+buf.toString()+")";
	}
}
