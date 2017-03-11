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

package com.hertz.herc.rentalman.reports;

import java.util.HashMap;

import com.hertz.hercutil.company.RMAccountInfo;

/**
 * 
 * @author John Vincent
 *
 */

public class Utils {

	public static RMAccountInfo makeRMAccountInfo (
			String account,
			int countryCode,
			String cmname, 
			String cmadr1,
			String cmadr2, 
			String cmcity,
			String cmstat,
			String cmzip) {
		HashMap hashMap = new HashMap();
		hashMap.put("cmname", cmname);
		hashMap.put("cmadr1", cmadr1);
		hashMap.put("cmadr2", cmadr2);
		hashMap.put("cmcity", cmcity);
		hashMap.put("cmstat", cmstat);
		hashMap.put("cmzip", cmzip);
//		hashMap.put("cmlidt", Utils.makeSQLDate ("2008-06-15"));
		return new RMAccountInfo (account, countryCode, hashMap);
	}
}
