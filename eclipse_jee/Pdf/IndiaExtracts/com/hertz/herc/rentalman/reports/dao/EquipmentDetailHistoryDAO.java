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
import com.hertz.hercutil.rentalman.reports.equipmentDetailHistory.EquipmentDetailHistoryInfo;
import com.hertz.hercutil.rentalman.reports.equipmentDetailHistory.EquipmentDetailHistoryItemInfo;
import com.hertz.irac.framework.HertzSystemException;

/**
 * Class to encapsulate the RentalMan Detailed Equipment History data extract 
 * 
 * @author John Vincent
 */

public class EquipmentDetailHistoryDAO {

	/**
	 * Get detailed equipment history
	 * 
	 * @param connection				Database connection
	 * @param rentalManCode				RentalMan code; ex HG is USA, CR is Canada
	 * @param rmAccountType				Account
	 * @param category					Category
	 * @param classification			Class
	 * @param selectedYear				Year
	 * @param jobNumber					Job number
	 * @param orderBy					Order by clause
	 * @return							Detailed equipment history
	 * @throws HertzSystemException
	 */
	public EquipmentDetailHistoryInfo performExtract (Connection connection, String rentalManCode, 
			RMAccountType rmAccountType, 
			String category, String classification, String selectedYear,
			String jobNumber, String orderBy) throws HertzSystemException {

		EquipmentDetailHistoryInfo equipmentDetailHistoryInfo = new EquipmentDetailHistoryInfo();
		StringBuffer buf = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;

		boolean bOrderBy = (orderBy != null && orderBy.trim().length() > 0);
		boolean bJobNumber = (jobNumber != null && jobNumber.trim().length() > 0);

		try {
			buf.append ("select RDITEM, RDCON#, RDISEQ, RDSEQ#, RDDYRT, RDWKRT, RDMORT, RDRATU ,RDAMT$, RDSYSD, RDDATO, RDDATI ");
			buf.append ("from RACDETF3 where RDCMP = ? and RDCATG = ? and RDCLAS = ? ");
			buf.append ("and RDSYSD >= ? and RDSYSD < ? and RDTYPE = 'RI' and RDCUS# in ? ");

			if (bJobNumber) buf.append ("and RDCON# in (select RHCON# from RACHDRFL where RHCMP = ? and RHJOB# = ?) ");
			if (bOrderBy) buf.append ("order by ").append (orderBy);

			int param = 0;
			ps = connection.prepareStatement(buf.toString());
			ps.setString(++param, rentalManCode);
			ps.setString(++param, category);
			ps.setString(++param, classification);
			ps.setString(++param, selectedYear + "0101");
			ps.setString(++param, selectedYear + "1299");
			ps.setString(++param, rmAccountType.getAccountNumber());
			if (bJobNumber) {
				ps.setString(++param, rentalManCode);
				ps.setString(++param, jobNumber);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				equipmentDetailHistoryInfo.add (new EquipmentDetailHistoryItemInfo (
						rmAccountType,
						category,
						classification,
						DAOUtils.cleanString (rs.getString("RDITEM")),
						DAOUtils.cleanString (rs.getString("RDCON#")),
						DAOUtils.cleanString (rs.getString("RDISEQ")),
						DAOUtils.cleanString (rs.getString("RDSEQ#")),
						DAOUtils.cleanString (rs.getString("RDSYSD")),
						DAOUtils.cleanString (rs.getString("RDDATO")),
						DAOUtils.cleanString (rs.getString("RDDATI")),
						DAOUtils.cleanString (rs.getString("RDRATU")),
						DAOUtils.cleanString (rs.getString("RDAMT$")),
						selectedYear));
			}
		}
		catch (SQLException ex) {
			throw HercErrorHelper.handleSQLException(this, ex, "Unable to EquipmentDetailHistoryDAO::performExtract()");
		}
		finally {
			buf = null;
			DAOUtils.closeResultSet(rs);
			DAOUtils.closeStatement(ps);
		}
		return equipmentDetailHistoryInfo;
	}
}
