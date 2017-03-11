package com.idc.sql.lib;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBUpdate {
	private DB m_db;
	private String m_statement;
	private int m_updateCount = 0;
	private String m_sqlErrorMessage;
	public DBUpdate(DB db, String statement) {
		m_db = db;
		m_statement = statement;
	}
	public void executeUpdate () {
		System.out.println("executeUpdate :"+m_statement+":");
		m_sqlErrorMessage = "";
		PreparedStatement ps = null;
		try {
			ps = m_db.getDBConnection().prepareStatement(m_statement);
			boolean flag = ps.execute();
			if (! flag) {
				m_updateCount = ps.getUpdateCount();
			}
		}
		catch (SQLException sqlException) {
			m_sqlErrorMessage = sqlException.getMessage();
		}
		finally {
			if (ps != null) {
				try {
					ps.close();
				}
				catch (SQLException e) {}
			}
		}
		return;
	}
	public String reportUpdate (long millis) {
		if (m_sqlErrorMessage != null && m_sqlErrorMessage.length() > 0) {
			return m_sqlErrorMessage;
		}
		StringBuffer buf = new StringBuffer();
		buf.append("JV0000I  The SQL command completed successfully; ");
		buf.append(m_updateCount);
		if (m_updateCount == 1)
			buf.append(" record");
		else
			buf.append(" records");
		buf.append (" updated in ").append(millis).append(" millisecs");
		return buf.toString();
	}
}
