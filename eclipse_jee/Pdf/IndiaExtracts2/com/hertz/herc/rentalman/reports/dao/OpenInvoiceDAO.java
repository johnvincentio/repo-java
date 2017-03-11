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
import com.hertz.irac.framework.HertzSystemException;
import com.hertz.hercutil.rentalman.reports.openInvoice.OpenInvoiceInfo;
import com.hertz.hercutil.rentalman.reports.openInvoice.OpenInvoiceItemInfo;

/**
 * Class to encapsulate the RentalMan Open Invoice data extract 
 * 
 * @author John Vincent
 */

public class OpenInvoiceDAO {

	/**
	 * Get open invoices
	 * 
	 * @param connection				Database connection
	 * @param rentalManCode				RentalMan code; ex HG is USA, CR is Canada
	 * @param rmAccountType				Account
	 * @param orderBy					Order by clause
	 * @param start_num					Starting record number
	 * @return							Open invoices
	 * @throws HertzSystemException
	 */
	public OpenInvoiceInfo performExtract (Connection connection, String rentalManCode, 
					RMAccountType rmAccountType, String orderBy, int start_num) throws HertzSystemException {
		OpenInvoiceInfo openInvoiceInfo = new OpenInvoiceInfo();
		StringBuffer buf = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			boolean bOrderBy = (orderBy != null && orderBy.trim().length() > 0);
			if ("CR".equals (rentalManCode)) {		// Canada
				buf.append ("SELECT A.*, B.*, (AHAMT$ - AHCBAL) as CurrBalance FROM arihdrf2 A LEFT OUTER JOIN rachdrfl B ");
				buf.append ("ON AHCMP = RHCMP AND ahcus# = rhcus# and ahinv# = rhcon# and ahiseq = rhiseq ");
				buf.append ("WHERE (ahcmp = ? AND ahcus# in ?) and AHSTTS <> 'PD' and AHCBAL  <> 0 ");
			}
			else {									// not Canada
				buf.append ("SELECT A.*, C.*, (A.AHAMT$ - A.AHCBAL) as CurrBalance, AHCOLS ");
				buf.append ("FROM arihdrf2 A LEFT OUTER JOIN arihd2fl B ");
				buf.append ("ON A.ahcmp = B.ahcmp AND A.ahcus# = B.ahcus# and A.ahinv# = B.ahinv# and A.ahiseq = B.ahiseq ");
				buf.append ("LEFT OUTER JOIN rachdrfl C ON A.AHCMP = RHCMP AND A.ahcus# = rhcus# and A.ahinv# = rhcon# and A.ahiseq = rhiseq ");
				buf.append ("WHERE (A.ahcmp = ? AND A.ahcus# in ?) and A.AHSTTS <> 'PD' ");
				buf.append ("and ((A.AHCBAL > 0) OR (A.AHCBAL < 0 AND B.AHCOLS >= 'B001' AND B.AHCOLS <= 'B498')) ");
			}
			buf.append ("order by ");
			if (bOrderBy)
				buf.append (orderBy);
			else
				buf.append ("A.AHINVD DESC, A.AHINV# ASC, A.AHISEQ ASC");

			int param = 0;
			ps = connection.prepareStatement(buf.toString());
			ps.setString(++param, rentalManCode);
			ps.setString(++param, rmAccountType.getAccountNumber());

			rs = ps.executeQuery();
			if (start_num > 0) rs.absolute(start_num);
			while (rs.next()) {
				openInvoiceInfo.add (new OpenInvoiceItemInfo (
						rmAccountType,
						DAOUtils.cleanString (rs.getString("AHINV#")),
						DAOUtils.cleanString (rs.getString("AHISEQ")),
						DAOUtils.cleanString (rs.getString("AHDUED")),
						DAOUtils.cleanString (rs.getString("AHLOC")),
						DAOUtils.cleanString (rs.getString("AHSTTS")),
						DAOUtils.cleanString (rs.getString("CurrBalance")),
						DAOUtils.cleanString (rs.getString("AHAMT$")),
						DAOUtils.cleanString (rs.getString("AHCBAL")),
						DAOUtils.cleanString (rs.getString("RHPO#"))));
			}
		}
		catch (SQLException ex) {
			throw HercErrorHelper.handleSQLException(this, ex, "Unable to OpenInvoiceDAO::performExtract()");
		}
		finally {
			buf = null;
			DAOUtils.closeResultSet(rs);
			DAOUtils.closeStatement(ps);
		}
		return openInvoiceInfo;
	}
}
