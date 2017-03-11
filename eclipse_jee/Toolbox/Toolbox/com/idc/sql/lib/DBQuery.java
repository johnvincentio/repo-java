
package com.idc.sql.lib;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;

import com.idc.trace.LogHelper;
import com.idc.utils.UtilHelper;

public class DBQuery {
	private boolean m_bDebug = false;
	private DB m_db;
	private String m_statement;
	private QueryRowInfo m_queryRowInfo;
	private QueryColumnInfo m_queryColumnInfo;
	private String m_sqlErrorMessage;

	public DBQuery(DB db, String statement) {
		m_db = db;
		m_statement = statement;
	}

	public QueryRowInfo getQueryRowInfo() {return m_queryRowInfo;}
	public void executeQuery () {
		m_queryRowInfo = new QueryRowInfo();
		boolean bGetRows = true;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		String value, str;
		try {
			m_queryColumnInfo = new QueryColumnInfo(m_statement);
			QueryColumnItemInfo queryColumnItemInfo;
			ps = m_db.getDBConnection().prepareStatement(m_statement);
			rs = ps.executeQuery();
			rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			LogHelper.info("cols "+cols);
			m_queryColumnInfo.setColumns (cols);

			for (int num=0; num < cols; num++) {
//				String catname = rsmd.getCatalogName(num+1);
//				LogHelper.info("catname :"+catname+":");
//				String name = rsmd.getColumnName(num+1);
				m_queryColumnInfo.add (new QueryColumnItemInfo(
						num + 1,
						rsmd.getColumnName(num+1),
						rsmd.getColumnType(num+1),
						rsmd.getColumnDisplaySize(num+1)));
			}
			LogHelper.info("queryColumnInfo "+m_queryColumnInfo);

//			QueryRowItemInfo queryRowItemInfo;
			int rows = 0;
			if (bGetRows) {
				while (rs.next()) {
					if (m_bDebug) LogHelper.info("found a record");
					rows++;
					for (int j=0; j < m_queryColumnInfo.getColumns(); j++) {
						value = "";
						queryColumnItemInfo = (QueryColumnItemInfo) m_queryColumnInfo.getQueryColumnItemInfo(j+1);
						if (queryColumnItemInfo.getType() == Types.CHAR) {
							value = rs.getString(j+1);
						}
						else if (queryColumnItemInfo.getType() == Types.VARCHAR) {
							str = rs.getString(j+1);
							if (! (str == null || str.length() < 1))
								value = UtilHelper.cleanForSql(str);
							else
								value = "";
						}
						else if (queryColumnItemInfo.getType() == Types.CLOB) {
							LogHelper.info ("column "+j+1+" clob found");
							java.sql.Clob myClob = rs.getClob(j+1);
							if (myClob == null) {
								LogHelper.info ("clob is null");
								value = "";
							}
							else {
								LogHelper.info ("clob length "+(int) myClob.length());
								str = myClob.getSubString(1, (int) myClob.length());
								if (! (str == null || str.length() < 1))
									value = UtilHelper.cleanForSql(str);
								else
									value = "";
								LogHelper.info("clob value :"+value+":");
							}
						}
						else if (queryColumnItemInfo.getType() == Types.SMALLINT)
							value = Integer.toString(rs.getInt(j+1));
						else if (queryColumnItemInfo.getType() == Types.INTEGER)
							value = Integer.toString(rs.getInt(j+1));
						else if (queryColumnItemInfo.getType() == Types.BIGINT)
							value = Long.toString(rs.getLong(j+1));
						else if (queryColumnItemInfo.getType() == Types.FLOAT)
							value = Float.toString(rs.getFloat(j+1));
						else if (queryColumnItemInfo.getType() == Types.DOUBLE)
							value = Double.toString(rs.getDouble(j+1));
						else if (queryColumnItemInfo.getType() == Types.DECIMAL)
							value = "" + rs.getBigDecimal(j+1);
						else if (queryColumnItemInfo.getType() == Types.DATE) {
							value = ""+rs.getTimestamp(j+1);
							//value = "'"+Utils.getDateString(rs.getDate(j+1))+"'";
						}
						else if (queryColumnItemInfo.getType() == Types.TIMESTAMP)
							value = ""+rs.getTimestamp(j+1);
						else if (queryColumnItemInfo.getType() == Types.TIME)
							value = UtilHelper.getDateString(rs.getTime(j+1));
						else if (queryColumnItemInfo.getType() == Types.NUMERIC)
							value = Integer.toString(rs.getInt(j+1));
						else
							LogHelper.error("unknown type "+queryColumnItemInfo.getType()+" column "+queryColumnItemInfo.getName());
						m_queryRowInfo.add (new QueryRowItemInfo (value));
//						LogHelper.info("value :"+value+":");
					}
				}
				m_queryRowInfo.setRows(rows);
			}
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
	public String reportQuery (long millis) {
		if (m_sqlErrorMessage != null && m_sqlErrorMessage.length() > 0) {
			return m_sqlErrorMessage;
		}

		StringBuffer buf = new StringBuffer();
		LogHelper.info("m_queryColumnInfo "+m_queryColumnInfo);
		Iterator<QueryColumnItemInfo> iter = m_queryColumnInfo.getItems();
		while (iter.hasNext()) {
			QueryColumnItemInfo item = (QueryColumnItemInfo) iter.next();
			buf.append(item.getDisplayName()).append(' ');
		}
		buf.append("\n");

		int columnCount = m_queryColumnInfo.getColumns();
		LogHelper.info("columnCount "+columnCount);
		if (columnCount < 1) columnCount = 5;

		int col = 1;
		Iterator<QueryRowItemInfo> iter2 = m_queryRowInfo.getItems();
		while (iter2.hasNext()) {
			QueryRowItemInfo item = (QueryRowItemInfo) iter2.next();
			String strValue = "";
			if (item != null && item.getValue() != null) strValue = item.getValue();
			QueryColumnItemInfo queryColumnItemInfo = m_queryColumnInfo.getQueryColumnItemInfo(col);
			buf.append (queryColumnItemInfo.getDisplayValue (strValue)).append(' ');
			if (col++ >= columnCount) {
				col = 1;
				buf.append("\n");
			}
		}
		buf.append("\n").append(m_queryRowInfo.getRows());
		if (m_queryRowInfo.getRows() == 1)
			buf.append (" row ");
		else
			buf.append (" rows ");
		buf.append ("took ").append(millis).append(" millisecs");
		return buf.toString();
	}
}
