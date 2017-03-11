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

package com.hertz.herc.rentalman.business.dao;

import com.hertz.hercutil.company.RMAccountType;

/**
 * @author john Vincent
 *
 * handle RentalMan data access constants
 */

public class DBRentalManConstants {
	public static final String US_RENTALMAN_DATASOURCE_SP = "db/DataSourceSP";
	public static final String US_RENTALMAN_DATASOURCE_SQL = "db/DataSourceSQL";

	public static final String CA_RENTALMAN_DATASOURCE_SP = "db/CADataSourceSP";
	public static final String CA_RENTALMAN_DATASOURCE_SQL = "db/CADataSourceSQL";

	public static String getDatasourceName (int countryCode) {
		if (countryCode == RMAccountType.CA_CODE) return CA_RENTALMAN_DATASOURCE_SQL;
		return US_RENTALMAN_DATASOURCE_SQL;
	}

	public static String getRentalManCode (int countryCode) {
		if (countryCode == RMAccountType.CA_CODE) return "CR";
		return "HG";
	}
}
