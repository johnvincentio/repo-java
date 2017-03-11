package com.idc.sql.lib;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;

import com.idc.trace.LogHelper;
import com.idc.utils.UtilHelper;

public class DBDescribe {
	private DB m_db;
	private String m_statement;
	private QueryColumnInfo m_queryColumnInfo;
	private String m_sqlErrorMessage;

	public DBDescribe(DB db, String statement) {
		m_db = db;
		m_statement = statement;
	}

	public void execute() {
		System.out.println("DBDescribe::execute; "+m_statement);
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		try {
			m_queryColumnInfo = new QueryColumnInfo(m_statement);
			ps = m_db.getDBConnection().prepareStatement("select * from " +m_statement);
			rs = ps.executeQuery();
			rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			LogHelper.info("cols "+cols);
			m_queryColumnInfo.setColumns (cols);

			for (int num=0; num < cols; num++) {
//				String catname = rsmd.getCatalogName(num+1);
//				LogHelper.info("catname :"+catname+":");
//				String name = rsmd.getColumnName(num+1);
				m_queryColumnInfo.add (new QueryColumnItemInfo (
						num + 1,
						rsmd.getColumnName(num+1),
						rsmd.getColumnType(num+1),
						rsmd.getColumnDisplaySize(num+1),
						rsmd.getColumnTypeName(num+1),
						rsmd.isNullable(num+1),
						rsmd.getPrecision(num+1),
						rsmd.getScale(num+1)));
			}
			LogHelper.info("queryColumnInfo "+m_queryColumnInfo);
		}
		catch (SQLException sqlException) {
			m_sqlErrorMessage = sqlException.getMessage();
		}
		finally {
			if (rs != null) {
				try {
					rs.close();
				}
				catch (SQLException e) {}
			}
			if (ps != null) {
				try {
					ps.close();
				}
				catch (SQLException e) {}
			}
		}
		return;
	}
	public String report (long millis) {
		if (m_sqlErrorMessage != null && m_sqlErrorMessage.length() > 0) {
			return m_sqlErrorMessage;
		}

		StringBuffer buf = new StringBuffer();
//		LogHelper.info("m_queryColumnInfo "+m_queryColumnInfo);
		Iterator<QueryColumnItemInfo> iter = m_queryColumnInfo.getItems();
		while (iter.hasNext()) {
			QueryColumnItemInfo item = (QueryColumnItemInfo) iter.next();
			buf.append(item.getName());
			buf.append(UtilHelper.makeSpace(30 - item.getName().length()));
			System.out.println("length "+buf.length());
			buf.append(item.getTypeName());
			buf.append(UtilHelper.makeSpace(18 - item.getTypeName().length()));
			buf.append(item.getPrecision());
			buf.append(UtilHelper.makeSpace(8));
			buf.append(item.getScale());
			buf.append(UtilHelper.makeSpace(8));
			buf.append(item.isNullable());
			buf.append(UtilHelper.makeSpace(6));
			buf.append("\n");
		}
		buf.append("\n");

		/*
Column                         Type      Type

name                           schema    name               Length   Scale Nulls
123456789012345678901234567890           123456789012345678 12345678
------------------------------ --------- ------------------ -------- ----- ------

MEMBERID                       SYSIBM    BIGINT                    8     0 No    

USERNAME                       SYSIBM    VARCHAR                 128     0 No    

COMPANYID                      SYSIBM    BIGINT                    8     0 No    

DEFAULTLOCATION                SYSIBM    VARCHAR                  50     0 Yes   

LOCKED_BY_MEMBERID             SYSIBM    BIGINT                    8     0 Yes   

LOCK_EXPIRY_TIME               SYSIBM    BIGINT                    8     0 Yes   



  6 record(s) selected.
		*/

		buf.append ("took ").append(millis).append(" millisecs");
		return buf.toString();
	}
}
