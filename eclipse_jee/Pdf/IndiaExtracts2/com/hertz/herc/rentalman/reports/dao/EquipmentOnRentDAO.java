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
import java.util.Calendar;
import java.util.Iterator;

import com.hertz.hercutil.company.RMAccountType;
import com.hertz.hercutil.framework.HercErrorHelper;
import com.hertz.hercutil.presentation.DAOUtils;
import com.hertz.hercutil.rentalman.reports.equipmentOnRent.EquipmentOnRentInfo;
import com.hertz.hercutil.rentalman.reports.equipmentOnRent.EquipmentOnRentItemInfo;
import com.hertz.hercutil.rentalman.reports.equipmentOnRent.EquipmentOnRentPickupInfo;
import com.hertz.hercutil.rentalman.reports.equipmentOnRent.EquipmentOnRentPickupItemInfo;
import com.hertz.hercutil.rentalman.reports.equipmentOnRent.PendingTransactionsInfo;
import com.hertz.irac.framework.HertzSystemException;

/**
 * Class to encapsulate the RentalMan Equipment On Rent data extract 
 * 
 * @author John Vincent
 */

public class EquipmentOnRentDAO {

	private static final String SQL1 = 
"select distinct RDCON#, RDSEQ#, RDITEM, RDCATG ,RDCLAS, (RDQTY-RDQTYR) as RDQTY, RDLOC, RDDYRT, RDWKRT, RDMORT, RHJOBL, " +
"RHDATO, RHPO#, ECDESC, RHORDB, RDSEQ#, RHERDT, CJNAME, 0 as RNBILL, 0 as RNACCR, 0 as RNRCST " +
"from RAODETFL, EQPCCMFL, RAOHDRFL left outer join CUSJOBFL On RHCMP = CJCMP and RHCUS# = CJCUS# and RHJOB# = CJJOB# " +
"where (RDCMP = ECCMP and RDCATG = ECCATG and RDCLAS = ECCLAS) and " +
"(RHCMP = RDCMP and RHCON# = RDCON#) and RDCMP = ? and RDTYPE = 'RI' and RDCUS# in ? " +
"and RDCON# in (select RHCON# from RAOHDRFL where RHCMP = ? and RHJOB# = ? ";

	private static final String SQL2 = 
"select distinct T.RDCMP, T.RDCON#, T.RDSEQ#, T.RDCUS#, T.RDITEM, T.RDCATG, T.RDCLAS, " +
"T.RDQTY, T.RDLOC, T.RDDYRT, T.RDWKRT, T.RDMORT, T.ECDESC, RHJOBL, " +
"RHDATO, RHPO#, RHORDB, RHERDT, RHJOB#, CJNAME, T.RNBILL, T.RNACCR, T.RNRCST " +
"from (select RDCMP, RDCON#, RDSEQ#, RDCUS#, RDITEM, RDCATG, RDCLAS,(RDQTY-RDQTYR) as RDQTY, " +
"RDLOC, RDDYRT, RDWKRT, RDMORT, ECDESC, RNBILL, (RNBILL + RNACCR) as RNACCR, RNRCST " +
"from EQPCCMFL, RAODETFL left outer join RAOAMTFL On RDCMP = RNCMP and RDCON# = RNCON# and RDSEQ# = RNSEQ# " +
"WHERE ECCMP = RDCMP and ECCATG = RDCATG AND ECCLAS = RDCLAS and ECCMP = ? and RDCUS# in ? " +
"and RDTYPE = 'RI') as T, RAOHDRFL, CUSJOBFL " +
"where t.RDCMP = RHCMP and T.RDCON# = RHCON# and RHCMP = CJCMP and RHCUS# = CJCUS# " +
"and RHJOB# = CJJOB# and RHCMP = ? and RHCUS# in ? and RHTYPE = 'O' ";

	private static final String SQL3 = 
"select distinct T.RDCMP, T.RDCON#, T.RDSEQ#, T.RDCUS#, T.RDITEM, T.RDCATG, T.RDCLAS, T.RDQTY, " +
"T.RDLOC, T.RDDYRT, T.RDWKRT, T.RDMORT, T.ECDESC, RHJOBL, " +
"RHDATO, RHPO#, RHORDB, RHERDT, RHJOB#, CJNAME, T.RNBILL, T.RNACCR, T.RNRCST from " +
"(select RDCMP, RDCON#, RDSEQ#, RDCUS#, RDITEM, RDCATG, RDCLAS, (RDQTY-RDQTYR) as RDQTY, " +
"RDLOC, RDDYRT, RDWKRT, RDMORT, ECDESC, RNBILL, (RNBILL + RNACCR) as RNACCR, RNRCST " +
"from EQPCCMFL, RAODETFL left outer join RAOAMTFL On RDCMP = RNCMP and RDCON# = RNCON# and RDSEQ# = RNSEQ# " +
"WHERE ECCMP = RDCMP and ECCATG = RDCATG AND ECCLAS = RDCLAS and ECCMP = ? " +
"and RDCUS# in ? and RDTYPE = 'RI') as T, RAOHDRFL, CUSJOBFL " +
"where t.RDCMP = RHCMP and T.RDCON# = RHCON# and RHCMP = CJCMP and RHCUS# = CJCUS# and RHJOB# = CJJOB# " +
"and RHCMP = ? and RHCUS# in ? and RHTYPE = 'O' " +
"and RDCON# in (select RHCON# from RAOHDRFL where RHCMP = ? and RHJOB# = ? ";

	private static final String SQL4 = 
"select distinct RDCON#, RDSEQ#, RDITEM, RDCATG ,RDCLAS, (RDQTY-RDQTYR) as RDQTY, " +
"RDLOC, RDDYRT, RDWKRT, RDMORT, RHJOBL, RHDATO, RHPO#, ECDESC, RHORDB, " +
"RDSEQ#, RHERDT, CJNAME, 0 as RNBILL, 0 as RNACCR, 0 as RNRCST " +
"from RAODETFL, EQPCCMFL, RAOHDRFL Left outer join CUSJOBFL " +
"on RHCMP = CJCMP and RHCUS# = CJCUS# and RHJOB# = CJJOB# " +
"where (RDCMP = ECCMP and RDCATG = ECCATG and RDCLAS = ECCLAS) " +
"and (RHCMP = RDCMP and RHCON# = RDCON#) and " +
"RDCMP = ? and RDTYPE = 'RI' and RDCUS# in ? ";

	private static final String SQL5 = 
"select RUPKU#, RUCON#, RUSEQ#, RUEQP# from RAPKUPF4 where RUCMP = ? and RUCUS# in ? and RUCON# > 0 and RUSTTS = 'OP'";

	/**
	 * Get equipment on rent
	 * 
	 * @param connection					Database connection
	 * @param rentalManCode					RentalMan code; ex HG is USA, CR is Canada
	 * @param equipmentOnRentPickupInfo		Equipment on Rent Pickup
	 * @param pendingTransactionsInfo		Pending transactions
	 * @param bEquipmentOnRent				true if Equipment on rent, else Overdue Rentals
	 * @param rmAccountType					Account
	 * @param jobNumber						Job number
	 * @param orderBy						Order by clause
	 * @return								Equipment on rent
	 * @throws HertzSystemException
	 */
	public EquipmentOnRentInfo performExtract (Connection connection, String rentalManCode, 
			EquipmentOnRentPickupInfo equipmentOnRentPickupInfo,
			PendingTransactionsInfo pendingTransactionsInfo, boolean bEquipmentOnRent,
			RMAccountType rmAccountType, String jobnumber, String orderBy) throws HertzSystemException {

//		LogBroker.debug (this, ">>> EquipmentOnRentDAO::performExtract");
		boolean bJobNumber = (jobnumber != null && jobnumber.trim().length() > 0);

		boolean bOrderBy = (orderBy != null && orderBy.trim().length() > 0);
		if(bEquipmentOnRent) { //Equipment on rent
			if (bOrderBy) 
				orderBy = "order by " + orderBy ;
			else
				orderBy = "order by RHDATO DESC, RDCON# DESC";
		} else {			//over due rentals
			if (bOrderBy) 
				orderBy = "order by " + orderBy ;
			else
				orderBy = "order by RHERDT ASC, RDCON# ASC";
		}
		

		Calendar Today = Calendar.getInstance();
		int todayDate = Today.get(Calendar.YEAR)*10000 + (Today.get(Calendar.MONTH)+1)*100 + Today.get(Calendar.DAY_OF_MONTH);

		int scenario = 1;
		if (rmAccountType.getCountryCode() != 1) {		// not US
			scenario = (bJobNumber) ? 3 : 4;
		}
		else {		// US only
			scenario = (bJobNumber) ? 1 : 2;
		}
//		LogBroker.debug (this, "scenario "+scenario);

		EquipmentOnRentInfo equipmentOnRentInfo = new EquipmentOnRentInfo (rmAccountType.getCountryCode());
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			switch (scenario) {
			case 4:
				ps = connection.prepareStatement(SQL4 + orderBy);
				ps.setString(1, rentalManCode);
				ps.setString(2, rmAccountType.getAccountNumber());
				break;
			case 3:
				ps = connection.prepareStatement(SQL3 + orderBy);
				ps.setString(1, rentalManCode);
				ps.setString(2, rmAccountType.getAccountNumber());
				ps.setString(3, rentalManCode);
				ps.setString(4, rmAccountType.getAccountNumber());
				ps.setString(5, rentalManCode);
				ps.setString(6, jobnumber);
				break;
			case 2:
				ps = connection.prepareStatement(SQL2 + orderBy);
				ps.setString(1, rentalManCode);
				ps.setString(2, rmAccountType.getAccountNumber());
				ps.setString(3, rentalManCode);
				ps.setString(4, rmAccountType.getAccountNumber());
				break;
			case 1:
			default:
				ps = connection.prepareStatement(SQL1 + orderBy);
				ps.setString(1, rentalManCode);
				ps.setString(2, rmAccountType.getAccountNumber());
				ps.setString(3, rentalManCode);
				ps.setString(4, jobnumber);
				break;
			}

			rs = ps.executeQuery();

			while (rs.next()) {
				EquipmentOnRentItemInfo item = new EquipmentOnRentItemInfo (
						rmAccountType, todayDate, equipmentOnRentPickupInfo, pendingTransactionsInfo,
						DAOUtils.cleanString (rs.getString("RDCON#")),
						DAOUtils.cleanString (rs.getString("RHDATO")),
						DAOUtils.cleanString (rs.getString("RHERDT")),
						DAOUtils.cleanString (rs.getString("RDITEM")),
						DAOUtils.cleanString (rs.getString("RDQTY")),
						DAOUtils.cleanString (rs.getString("ECDESC")),
						DAOUtils.cleanString (rs.getString("RDSEQ#")),
						DAOUtils.cleanString (rs.getString("RHORDB")),
						DAOUtils.cleanString (rs.getString("RHPO#")),
						DAOUtils.cleanString (rs.getString("CJNAME")),
						DAOUtils.cleanString (rs.getString("RDDYRT")),
						DAOUtils.cleanString (rs.getString("RDWKRT")),
						DAOUtils.cleanString (rs.getString("RDMORT")),
						DAOUtils.cleanString (rs.getString("RNBILL"), "0.00"),
						DAOUtils.cleanString (rs.getString("RNACCR"), "0.00"),
						DAOUtils.cleanString (rs.getString("RNRCST"), "0.00"),
						DAOUtils.cleanString (rs.getString("RDCATG")),
						DAOUtils.cleanString (rs.getString("RDCLAS")));
				if (bEquipmentOnRent || item.isOverdueContract())
					equipmentOnRentInfo.add (item);
			}
		}
		catch (SQLException ex) {
			throw HercErrorHelper.handleSQLException(this, ex, "Unable to EquipmentOnRentDAO::performExtract()");
		}
		finally {
			DAOUtils.closeResultSet(rs);
			DAOUtils.closeStatement(ps);
		}

		/*
		 * check for item comments
		 */
		for (Iterator iter = equipmentOnRentInfo.getItems(); iter.hasNext(); ) {
			EquipmentOnRentItemInfo item = (EquipmentOnRentItemInfo) iter.next();
			if (item.isReRentItem()) {
				item.setItemComments ((new ContractCommentsDAO()).performExtract (connection, rentalManCode, 
						item.getContract(), "0", item.getDetailSequence()));
			}
		}
//		LogBroker.debug (this, "<<< EquipmentOnRentDAO::performExtract");
		return equipmentOnRentInfo;
	}

	/**
	 * Get Equipment on Rent Pickup data
	 * 
	 * @param connection				Database connection
	 * @param rentalManCode				RentalMan code; ex HG is USA, CR is Canada
	 * @param rmAccountType				Account
	 * @return							Equipment on Rent Pickup data
	 * @throws HertzSystemException
	 */
	public EquipmentOnRentPickupInfo getPickup (Connection connection, String rentalManCode, RMAccountType rmAccountType) 
						throws HertzSystemException {
		EquipmentOnRentPickupInfo equipmentOnRentPickupInfo = new EquipmentOnRentPickupInfo();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(SQL5);
			ps.setString(1, rentalManCode);
			ps.setString(2, rmAccountType.getAccountNumber());
			
			rs = ps.executeQuery();
			while (rs.next()) {
				equipmentOnRentPickupInfo.add (new EquipmentOnRentPickupItemInfo (
						DAOUtils.cleanString (rs.getString("RUCON#")),
						DAOUtils.cleanString (rs.getString("RUSEQ#")),
						DAOUtils.cleanString (rs.getString("RUEQP#")),
						DAOUtils.cleanString (rs.getString("RUPKU#"))));
			}
		}
		catch (SQLException ex) {
			throw HercErrorHelper.handleSQLException(this, ex, "Unable to EquipmentOnRentDAO::getPickup()");
		}
		finally {
			DAOUtils.closeResultSet(rs);
			DAOUtils.closeStatement(ps);
		}
		return equipmentOnRentPickupInfo;
	}
}
