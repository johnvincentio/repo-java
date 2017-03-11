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

import com.hertz.hercutil.framework.HercErrorHelper;
import com.hertz.hercutil.presentation.DAOUtils;
import com.hertz.irac.framework.HertzSystemException;

/**
 * Class to encapsulate the RentalMan Contract Comments data extract 
 * 
 * @author John Vincent
 */

public class ContractCommentsDAO {
	private static final String SQL = 
		"select OCCMNT From ORDCOMFL where OCCMP = ? and OCREF# = ? and OCISEQ = ? and OCTYPE = 'R' and OCASEQ = ?";

	/**
	 * Get contract comments
	 * 
	 * @param connection				Database connection
	 * @param rentalManCode				RentalMan code; ex HG is USA, CR is Canada
	 * @param contract					Contract
	 * @param seqno						Sequence
	 * @param lineno					Detailed sequence
	 * @return							contract comments
	 * @throws HertzSystemException
	 */
	public String performExtract (Connection connection, String rentalManCode,
					String contract, String seqno, String lineno) throws HertzSystemException {

		StringBuffer itemcomments = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(SQL);
			ps.setString(1, rentalManCode);
			ps.setString(2, contract);
			ps.setString(3, seqno);
			ps.setString(4, lineno);
			rs = ps.executeQuery();
			while (rs.next()) {
				itemcomments.append (" ").append (DAOUtils.cleanString(rs.getString("OCCMNT")));
			}
		}
		catch (SQLException ex) {
			throw HercErrorHelper.handleSQLException(this, ex, "Unable to ContractCommentsDAO::performExtract()");
		}
		finally {
			DAOUtils.closeResultSet(rs);
			DAOUtils.closeStatement(ps);
		}
		return itemcomments.toString();
	}
}
