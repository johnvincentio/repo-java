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

package com.hertz.hercutil.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.hertz.hercutil.account.data.CreditApplicationDataInfo;
import com.hertz.hercutil.account.data.CreditApplicationDataItemInfo;
import com.hertz.hercutil.account.data.CreditApplicationUSInfo;
import com.hertz.hercutil.framework.HercErrorHelper;
import com.hertz.hercutil.presentation.DAOUtils;
import com.hertz.hercutil.presentation.HercDate;
import com.hertz.irac.framework.HertzSystemException;
import com.hertz.irac.util.logging.LogBroker;

/**
 * Add CreditApplication data into the db.
 * 
 * @author John Vincent
 */

public class CreditApplicationDAO {
	private static final Class classRef = CreditApplicationDAO.class;

	/**
	 * Add credit application object to the database.
	 *  
	 * @param id								credit application id
	 * @param CreditApplicationDataItemInfo		credit application object
	 * @throws HertzSystemException
	 */
	public void addCreditApplication (DataSource datasource, CreditApplicationDataItemInfo creditApplicationDataItemInfo) throws HertzSystemException {
		LogBroker.debug(classRef, ">>> CreditApplicationDAO addCreditApplication; id "+creditApplicationDataItemInfo.getId());
		Connection conn = null;
		PreparedStatement pstmt = null;
		CreditApplicationUSInfo creditApplicationUSInfo = creditApplicationDataItemInfo.getCreditApplicationUSInfo();
		String sql = "insert into "+DAOUtils.SCHEMA+"CreditApplicationsV2 (id,createDate,status,fname,lname,comments,data)" +
				"values (?, ?, ?, ?, ?, ?, ?)";
		try {
			conn = DAOUtils.getDBConnection (datasource);
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, creditApplicationDataItemInfo.getId());
			pstmt.setDate(2, DAOUtils.getHercDate(creditApplicationDataItemInfo.getCreateDate()));
			pstmt.setString(3, creditApplicationDataItemInfo.getStatus());
			pstmt.setString(4, creditApplicationDataItemInfo.getFname());
			pstmt.setString(5, creditApplicationDataItemInfo.getLname());
			pstmt.setString(6, creditApplicationDataItemInfo.getComments());
			pstmt.setBytes(7, DAOUtils.getBytes (creditApplicationUSInfo));
			if(!(DAOUtils.getBytes (creditApplicationUSInfo).length <=0))
				pstmt.executeUpdate();
			else
				HercErrorHelper.logException(classRef, "CreditApplication object incorrect. Not inserted in DB.");
		}
		catch (SQLException ex) {
			throw HercErrorHelper.handleSQLException(this, ex, "Unable to CreditApplicationDAO::addCreditApplication()");
		}
		catch (Exception ex) {
			throw HercErrorHelper.handleException(classRef, ex);
		}
		finally {
			DAOUtils.closeStatement(pstmt);
			DAOUtils.closeConnection(conn);
		}
		LogBroker.debug(classRef, "<<< CreditApplicationDAO addCreditApplication");
	}

	/**
	 * Get CreditApplicationDataItemInfo object from the database.
	 * 
	 * @param id						credit application id
	 * @return							credit application object
	 * @throws HertzSystemException
	 */
	public CreditApplicationDataItemInfo getCreditApplication (DataSource datasource, long id) throws HertzSystemException {
		LogBroker.debug(classRef, ">>> CreditApplicationDAO getCreditApplication; id "+id);
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		CreditApplicationDataItemInfo info = null;
		CreditApplicationUSInfo creditApplicationUSInfo = null;
		String sql = "select createDate,status,updateDate,fname,lname,comments,data from "+DAOUtils.SCHEMA+"CreditApplicationsV2 where id = ?";

		try {
			conn = DAOUtils.getDBConnection (datasource);
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong (1, id);
			rs = pstmt.executeQuery();
			while (rs.next()) {
//				LogBroker.debug(classRef, "Getting next record");
				HercDate createDate = new HercDate(rs.getDate("createDate"));
				String status = rs.getString("status");
				HercDate updateDate = new HercDate(rs.getDate("updateDate"));
				String fname = rs.getString("fname");
				String lname = rs.getString("lname");
				String comments = rs.getString("comments");
				byte[] data = rs.getBytes("data");
				if(!(data.length <=0))
					creditApplicationUSInfo = (CreditApplicationUSInfo) DAOUtils.getObject (data);
				info = new CreditApplicationDataItemInfo(id, createDate, status, updateDate, fname, lname, comments, 
						creditApplicationUSInfo );
			}
		}
		catch (SQLException ex) {
			throw HercErrorHelper.handleSQLException(this, ex, "Unable to CreditApplicationDAO::getCreditApplication()");
		}
		catch (Exception ex) {
			throw HercErrorHelper.handleException(classRef, ex);
		}
		finally {
			DAOUtils.closeStatement(pstmt);
			DAOUtils.closeConnection(conn);
		}
		LogBroker.debug(classRef, "<<< CreditApplicationDAO getCreditApplication");
		return info;
	}

	/**
	 * Get CreditApplicationDataInfo object from the database.
	 * 
	 * @param id						credit application id
	 * @return							credit application object
	 * @throws HertzSystemException
	 */
	public CreditApplicationDataInfo getCreditApplications(DataSource datasource) throws HertzSystemException {
		LogBroker.debug(classRef, ">>> CreditApplicationDAO getCreditApplications");
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		CreditApplicationUSInfo creditApplicationUSInfo = null;
		CreditApplicationDataInfo info = new CreditApplicationDataInfo();
		String sql = "select id,createDate,status,updateDate,fname,lname,comments,data from "+DAOUtils.SCHEMA+"CreditApplicationsV2 order by id";

		try {
			conn = DAOUtils.getDBConnection (datasource);
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
//				LogBroker.debug(classRef, "Getting next record");
				long id = rs.getLong("id");
				HercDate createDate = new HercDate(rs.getDate("createDate"));
				String status = rs.getString("status");
				HercDate updateDate = new HercDate(rs.getDate("updateDate"));
				String fname = rs.getString("fname");
				String lname = rs.getString("lname");
				String comments = rs.getString("comments");
				byte[] data = rs.getBytes("data");
				if(!(data.length <=0))
					creditApplicationUSInfo = (CreditApplicationUSInfo) DAOUtils.getObject (data);
				info.add(new CreditApplicationDataItemInfo(id, createDate, status, updateDate, fname, lname, comments, 
						creditApplicationUSInfo));
			}
		}
		catch (SQLException ex) {
			throw HercErrorHelper.handleSQLException(this, ex, "Unable to CreditApplicationDAO::getCreditApplication()");
		}
		catch (Exception ex) {
			throw HercErrorHelper.handleException(classRef, ex);
		}
		finally {
			DAOUtils.closeStatement(pstmt);
			DAOUtils.closeConnection(conn);
		}
		LogBroker.debug(classRef, "<<< CreditApplicationDAO getCreditApplication");
		return info;
	}

	/**
	 * @param id
	 * @param info
	 * @throws HertzSystemException
	 */
	public void updateCreditApplication (DataSource datasource, long id, CreditApplicationDataItemInfo info) throws HertzSystemException {
		LogBroker.debug(classRef, ">>> CreditApplicationDAO addCreditApplication; id "+info.getId());
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "update "+DAOUtils.SCHEMA+"CreditApplicationsV2 set status = ? , updatedate= ? , fname = ?, " +
				"lname = ?, comments = ?, data = ? where id = ?";
		try {
			conn = DAOUtils.getDBConnection (datasource);
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, info.getStatus());
			pstmt.setDate(2, DAOUtils.getHercDate(info.getUpdateDate()));
			pstmt.setString(3, info.getFname());
			pstmt.setString(4, info.getLname());
			pstmt.setString(5, info.getComments());
			pstmt.setBytes(6, DAOUtils.getBytes (info.getCreditApplicationUSInfo()));
			pstmt.setLong (7, id);
			pstmt.executeUpdate();
		}
		catch (SQLException ex) {
			throw HercErrorHelper.handleSQLException(this, ex, "Unable to CreditApplicationDAO::addCreditApplication()");
		}
		catch (Exception ex) {
			throw HercErrorHelper.handleException(classRef, ex);
		}
		finally {
			DAOUtils.closeStatement(pstmt);
			DAOUtils.closeConnection(conn);
		}
		LogBroker.debug(classRef, "<<< CreditApplicationDAO addCreditApplication");
	}
}
