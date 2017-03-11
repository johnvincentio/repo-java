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
import com.hertz.hercutil.rentalman.reports.equipmentHistory.EquipmentHistoryInfo;
import com.hertz.hercutil.rentalman.reports.equipmentHistory.EquipmentHistoryItemInfo;
import com.hertz.irac.framework.HertzSystemException;

/**
 * Class to encapsulate the RentalMan Equipment History data extract 
 * 
 * @author John Vincent
 */

public class EquipmentHistoryDAO {

	/**
	 * Get equipment history
	 * 
	 * @param connection				Database connection
	 * @param rentalManCode				RentalMan code; ex HG is USA, CR is Canada
	 * @param rmAccountType				Account
	 * @param jobnumber					Job number
	 * @param selectedYear				Year
	 * @param orderBy					Order by clause
	 * @param start_num					Starting record number
	 * @return							Equipment history
	 * @throws HertzSystemException
	 */
	public EquipmentHistoryInfo performExtract (Connection connection, String rentalManCode, 
					RMAccountType rmAccountType, String jobnumber, String selectedYear, 
					String orderBy, int start_num) throws HertzSystemException {
//		LogBroker.debug (this, ">>> EquipmentHistoryDAO::performExtract");
		EquipmentHistoryInfo equipmentHistoryInfo = new EquipmentHistoryInfo();
		StringBuffer buf = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			boolean bOrderBy = (orderBy != null && orderBy.trim().length() > 0);
			boolean bJobNumber = (jobnumber != null && jobnumber.trim().length() > 0);
			buf.append ("select RDCATG, RDCLAS, ECDESC, sum(RDRDYS) as totalrentdays, sum(RDRHRS) as totalrenthours, ");
			buf.append ("count(rdcon#) as totalTransactions, sum(RDAMT$) as totalrentamount ");
			buf.append ("from RACDETH3 left outer join EQPCCMF1 on ECCMP = RDCMP and ECCATG = RDCATG and ECCLAS = RDCLAS ");
			buf.append ("where ECCMP = ? and RDTYPE = 'RI' and RDCUS# in ? and RDSYSD >= ? and RDSYSD < ? ");

			if (bJobNumber) buf.append ("and RDCON# in (select RHCON# from RACHDRFL where RHCMP = ? and RHJOB# = ?) ");
			buf.append ("group by RDCATG, RDCLAS, ECDESC ");
			buf.append ("order by ");
			if (bOrderBy)
				buf.append (orderBy);
			else
				buf.append ("RDCATG ASC, RDCLAS ASC");

			int param = 0;
			ps = connection.prepareStatement(buf.toString());
			ps.setString(++param, rentalManCode);
			ps.setString(++param, rmAccountType.getAccountNumber());
			ps.setString(++param, selectedYear + "0101");
			ps.setString(++param, selectedYear + "1299");
			if (bJobNumber) {
				ps.setString(++param, rentalManCode);
				ps.setString(++param, jobnumber);
			}

			rs = ps.executeQuery();
			if (start_num > 0) rs.absolute(start_num);
			while (rs.next()) {
				equipmentHistoryInfo.add (new EquipmentHistoryItemInfo (
						rmAccountType,
						DAOUtils.cleanString (rs.getString("RDCATG")),
						DAOUtils.cleanString (rs.getString("RDCLAS")),
						DAOUtils.cleanString (rs.getString("ECDESC")),
						DAOUtils.cleanString (rs.getString("totalrentdays")),
						DAOUtils.cleanString (rs.getString("totalTransactions")),
						DAOUtils.cleanString (rs.getString("totalrentamount")),
						selectedYear));
			}
		}
		catch (SQLException ex) {
			throw HercErrorHelper.handleSQLException(this, ex, "Unable to EquipmentHistoryDAO::performExtract()");
		}
		finally {
			buf = null;
			DAOUtils.closeResultSet(rs);
			DAOUtils.closeStatement(ps);
		}
//		LogBroker.debug (this, "<<< EquipmentHistoryDAO::performExtract");
		return equipmentHistoryInfo;
	}
}
