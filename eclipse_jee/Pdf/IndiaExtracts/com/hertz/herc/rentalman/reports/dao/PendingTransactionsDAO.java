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

package com.hertz.herc.rentalman.reports.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.hertz.hercutil.company.RMAccountType;
import com.hertz.hercutil.framework.HercErrorHelper;
import com.hertz.hercutil.presentation.DAOUtils;
import com.hertz.hercutil.rentalman.reports.equipmentOnRent.PendingExtendItemInfo;
import com.hertz.hercutil.rentalman.reports.equipmentOnRent.PendingReleaseItemInfo;
import com.hertz.hercutil.rentalman.reports.equipmentOnRent.PendingTransactionsInfo;
import com.hertz.irac.framework.HertzSystemException;

/**
 * Class to encapsulate the RentalMan Pending Transactions data extract 
 * 
 * @author John Vincent
 */

public class PendingTransactionsDAO {
	private static final String SQL = 
		"select * from ETWHDRFL2 left outer join ETWDETFL on THCMP = TDCMP AND THTRN# = TDTRN# where THCMP = ? and THCUS# = ?";

	/**
	 * Get pending transactions
	 * 
	 * @param connection				Database connection
	 * @param rentalManCode				RentalMan code; ex HG is USA, CR is Canada
	 * @param rmAccountType				Account
	 * @return							Pending transactions
	 * @throws HertzSystemException
	 */
	public PendingTransactionsInfo performExtract (Connection connection, String rentalManCode, 
			RMAccountType rmAccountType) throws HertzSystemException {
		PendingTransactionsInfo pendingTransactionsInfo = new PendingTransactionsInfo();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(SQL);
			ps.setString(1, rentalManCode);
			ps.setString(2, rmAccountType.getAccountNumber());

			rs = ps.executeQuery();
			while (rs.next()) {
				if (("REL").equals (DAOUtils.cleanString (rs.getString("THTYPE"))))
					pendingTransactionsInfo.add (new PendingReleaseItemInfo (
							DAOUtils.cleanString (rs.getString("THCON#")), 
							DAOUtils.cleanString (rs.getString("TDSEQ#")), 
							DAOUtils.cleanString (rs.getString("TDQTY"))));
				else
					pendingTransactionsInfo.add (new PendingExtendItemInfo (
							DAOUtils.cleanString (rs.getString("THCON#")), 
							DAOUtils.cleanString (rs.getString("THERDT")), 
							DAOUtils.cleanString (rs.getString("THSTAT"))));
			}
		}
		catch (SQLException ex) {
			throw HercErrorHelper.handleSQLException(this, ex, "Unable to PendingTransactionsDAO::performExtract()");
		}
		finally {
			DAOUtils.closeResultSet(rs);
			DAOUtils.closeStatement(ps);
		}
		return pendingTransactionsInfo;
	}
}
