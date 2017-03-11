package com.idc.mysql;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import com.idc.knight.Pair;
import com.idc.mysql.data.SelectMovesInfo;
import com.idc.mysql.data.SelectMovesItemInfo;
import com.idc.mysql.data.SelectSubTotalsInfo;
import com.idc.mysql.data.SelectSubTotalsItemInfo;
import com.idc.mysql.data.SelectTotalsItemInfo;

public class DBMySQL {

	private static final String INSERT_SQL_3 = "insert into `jv_schema_1`.`KNIGHT_SUBTOTALS` (ID, SOLUTION_NUMBER, TOTAL_MOVES, TIMING) values (?, ?, ?, ?)";

	private static final String INSERT_SQL_4 = "insert into `jv_schema_1`.`KNIGHT_MOVES` (ID, SOLUTION_NUMBER, MOVE_COUNTER, X_POS, Y_POS, MOVE_AWAY_TYPE, FROM_X_POS, FROM_Y_POS) " + 
			"values (?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String SELECT_SQL_2 = "select SOLUTION_NUMBER, TOTAL_MOVES, TIMING from `jv_schema_1`.`KNIGHT_SUBTOTALS` where id = ? order by `SOLUTION_NUMBER`";

	private static final String SELECT_SQL_3 = "select MOVE_COUNTER, X_POS, Y_POS, MOVE_AWAY_TYPE, FROM_X_POS, FROM_Y_POS from `jv_schema_1`.`KNIGHT_MOVES` where id = ? and solution_number = ? order by move_counter";

	private Connection m_connection;
	private PreparedStatement m_pstmt_1, m_pstmt_2, m_pstmt_3, m_pstmt_4;
	private long m_unique_id;

	public DBMySQL() {
		try {
			String dbdriver = "com.mysql.jdbc.Driver";
			String url = "jdbc:mysql:";
			String database = "localhost:3306";
			String username = "jv";
			String password = "yeti";
			String schema = "jv_schema_1";
			String connectionUrl = url + "//" + database + "/" + schema;

			DriverManager.registerDriver ((Driver) Class.forName (dbdriver).newInstance());
			m_connection = DriverManager.getConnection (connectionUrl, username, password);
			m_pstmt_1 = m_connection.prepareStatement (INSERT_SQL_1);
			m_pstmt_2 = m_connection.prepareStatement (INSERT_SQL_2);
			m_pstmt_3 = m_connection.prepareStatement (INSERT_SQL_3);
			m_pstmt_4 = m_connection.prepareStatement (INSERT_SQL_4);
		}
		catch (Exception ex) {
			System.err.println("Error; "+ex.getMessage());
		}
	}
	public void close() {
		try {
			if (m_pstmt_1 != null) m_pstmt_1.close();
			if (m_pstmt_2 != null) m_pstmt_2.close();
			if (m_pstmt_3 != null) m_pstmt_3.close();
			if (m_pstmt_4 != null) m_pstmt_4.close();
			if (m_connection != null) m_connection.close();
		}
		catch (Exception ex) {
			System.err.println("Error on close; "+ex.getMessage());
		}
	}
	public static void main (String[] args) {
		(new DBMySQL()).doTest1();
	}
	@SuppressWarnings("unused")
	private void doTest() {
		createTotalsRecord  (new Pair(6, 7), new Pair(1, 2));
		updateTotalsRecord (5, 123456789012345L);
		doInsertSubTotals (21, 345678934567L, 4532345436346L);
		doInsertMoves (3, 1, new Pair(5, 6), 3, new Pair(3, 5));

		createTotalsRecord  (new Pair(5, 8), new Pair(3, 4));
		doInsertSubTotals (53, 789456934567L, 9876654636354L);
		doInsertMoves (6, 23, new Pair(2, 1), 7, new Pair(4, 2));

		close();
	}
	private void doTest1() {
		long id = 1402088571767L;
		SelectTotalsItemInfo selectTotalsItemInfo = getSelectTotals (id);
		System.out.println("selectTotalsItemInfo "+selectTotalsItemInfo);
		SelectSubTotalsInfo selectSubTotalsInfo = getSelectSubTotals (id);
		System.out.println("selectSubTotalsInfo "+selectSubTotalsInfo);
		SelectMovesInfo selectMovesInfo = getSelectMoves (id, 1);
		System.out.println("selectMovesInfo "+selectMovesInfo);
		close();
	}

	private static final String INSERT_SQL_1 = "insert into `jv_schema_1`.`KNIGHT_TOTALS` (ID, SIZE_X_POS, SIZE_Y_POS, START_X_POS, START_Y_POS, SOLUTIONS, TOTAL_MOVES, START_DATE, TIMING) " + 
			"values (?, ?, ?, ?, ?, ?, ?, Now(), ?)";

	private static final String INSERT_SQL_2 = "update `jv_schema_1`.`KNIGHT_TOTALS` set TIMING = ?, SOLUTIONS = ?, TOTAL_MOVES = ? where id = ?";

	public void createTotalsRecord (Pair size, Pair start) {
//		System.out.println(">>> createTotalsRecord");
		m_unique_id = (new Date()).getTime();
//		System.out.println("m_unique_id "+m_unique_id);
		PreparedStatement myPstmt = m_pstmt_1;
		try {
			myPstmt.setLong(1, m_unique_id);
			myPstmt.setInt(2, size.getX());
			myPstmt.setInt(3, size.getY());
			myPstmt.setInt(4, start.getX());
			myPstmt.setInt(5, start.getY());
			myPstmt.setLong(6, 0);
			myPstmt.setLong(7, 0);
			myPstmt.setLong(8, -1);
			myPstmt.execute();
		}
		catch (Exception ex) {
			System.err.println("createTotalsRecord; Error; "+ex.getMessage());
		}
//		System.out.println("<<< createTotalsRecord");
	}

	public void updateTotalsRecord (long solutions, long totalMoves) {
//		System.out.println(">>> updateTotalsRecord");
		long timing = (new Date()).getTime() - m_unique_id;
//		System.out.println("m_unique_id "+m_unique_id+" timing "+timing);
		PreparedStatement myPstmt = m_pstmt_2;
		try {
			myPstmt.setLong(1, timing);
			myPstmt.setLong(2, solutions);
			myPstmt.setLong(3, totalMoves);
			myPstmt.setLong(4, m_unique_id);
			myPstmt.execute();
		}
		catch (Exception ex) {
			System.err.println("updateTotalsRecord; Error; "+ex.getMessage());
		}
//		System.out.println("<<< updateTotalsRecord");
	}

	public void doInsertSubTotals (long solution_number, long totalMoves, long timing) {
		PreparedStatement myPstmt = m_pstmt_3;
		try {
			myPstmt.setLong(1, m_unique_id);
			myPstmt.setLong(2, solution_number);
			myPstmt.setLong(3, totalMoves);
			myPstmt.setLong(4, timing);
			myPstmt.execute();
		}
		catch (Exception ex) {
			System.err.println("doInsert; Error; "+ex.getMessage());
		}		
	}

	public void doInsertMoves (long solution_number, int move_counter, Pair current, int move_away_type, Pair from) {
		PreparedStatement myPstmt = m_pstmt_4;
		try {
			myPstmt.setLong(1, m_unique_id);
			myPstmt.setLong(2, solution_number);
			myPstmt.setInt(3, move_counter);
			myPstmt.setInt(4, current.getX());
			myPstmt.setInt(5, current.getY());
			myPstmt.setInt(6, move_away_type);
			myPstmt.setInt(7, from.getX());
			myPstmt.setInt(8, from.getY());
			myPstmt.execute();
		}
		catch (Exception ex) {
			System.err.println("doInsertMoves; Error; "+ex.getMessage());
		}		
	}

	private static final String SELECT_SQL_1 = "select SIZE_X_POS, SIZE_Y_POS, START_X_POS, START_Y_POS, SOLUTIONS, TOTAL_MOVES, START_DATE, TIMING from `jv_schema_1`.`KNIGHT_TOTALS` where id = ?";

	public SelectTotalsItemInfo getSelectTotals (long id) {
		PreparedStatement myPstmt = null;
		ResultSet rs = null;
		try {
			myPstmt = m_connection.prepareStatement (SELECT_SQL_1);
			myPstmt.setLong(1, id);
			rs = myPstmt.executeQuery();
			while (rs.next()) {
				return new SelectTotalsItemInfo (id, rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getLong(5), rs.getLong(6), rs.getTimestamp(7), rs.getLong(8));
			}
		}
		catch (Exception ex) {
			System.err.println("getSelectTotals; Error; "+ex.getMessage());
		}
		finally {
			try {
				if (rs != null) rs.close();
				if (myPstmt!= null) myPstmt.close();
			}
			catch (Exception ex) {
				System.err.println("getSelectTotals::close; Error; "+ex.getMessage());
			}
		}
		return null;
	}

	private static final String SELECT_SQL_4 = "select ID, SOLUTIONS, TOTAL_MOVES, START_DATE, TIMING from `jv_schema_1`.`KNIGHT_TOTALS` " +
			"where SIZE_X_POS = ? and SIZE_Y_POS = ? and START_X_POS = ? and START_Y_POS = ?";

	public SelectTotalsItemInfo getSelectTotals (Pair size, Pair start) {
		PreparedStatement myPstmt = null;
		ResultSet rs = null;
		try {
			myPstmt = m_connection.prepareStatement (SELECT_SQL_4);
			myPstmt.setInt(1, size.getX());
			myPstmt.setInt(2, size.getY());
			myPstmt.setInt(3, start.getX());
			myPstmt.setInt(4, start.getY());
			rs = myPstmt.executeQuery();
			while (rs.next()) {
				return new SelectTotalsItemInfo (rs.getLong(1), size.getX(), size.getY(), start.getX(), start.getY(), rs.getLong(2), rs.getLong(3), rs.getTimestamp(4), rs.getLong(5));
			}
		}
		catch (Exception ex) {
			System.err.println("getSelectTotals; Error; "+ex.getMessage());
		}
		finally {
			try {
				if (rs != null) rs.close();
				if (myPstmt!= null) myPstmt.close();
			}
			catch (Exception ex) {
				System.err.println("getSelectTotals::close; Error; "+ex.getMessage());
			}
		}
		return null;
	}

	public SelectSubTotalsInfo getSelectSubTotals (long id) {
		SelectSubTotalsInfo selectSubTotalsInfo = new SelectSubTotalsInfo();
		PreparedStatement myPstmt = null;
		ResultSet rs = null;
		try {
			myPstmt = m_connection.prepareStatement (SELECT_SQL_2);
			myPstmt.setLong(1, id);
			rs = myPstmt.executeQuery();
			while (rs.next()) {
				selectSubTotalsInfo.add (new SelectSubTotalsItemInfo (id, rs.getLong(1), rs.getLong(2), rs.getLong(3)));
			}
		}
		catch (Exception ex) {
			System.err.println("getSelectSubTotals; Error; "+ex.getMessage());
		}
		finally {
			try {
				if (rs != null) rs.close();
				if (myPstmt!= null) myPstmt.close();
			}
			catch (Exception ex) {
				System.err.println("getSelectSubTotals::close; Error; "+ex.getMessage());
			}
		}
		return selectSubTotalsInfo;
	}

	public SelectMovesInfo getSelectMoves (long id, long solutionNumber) {
		SelectMovesInfo selectMovesInfo = new SelectMovesInfo();
		PreparedStatement myPstmt = null;
		ResultSet rs = null;
		try {
			myPstmt = m_connection.prepareStatement (SELECT_SQL_3);
			myPstmt.setLong(1, id);
			myPstmt.setLong(2, solutionNumber);
			rs = myPstmt.executeQuery();
			while (rs.next()) {
				selectMovesInfo.add (new SelectMovesItemInfo (id, solutionNumber, rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getInt(6)));
			}
		}
		catch (Exception ex) {
			System.err.println("getSelectMoves; Error; "+ex.getMessage());
		}
		finally {
			try {
				if (rs != null) rs.close();
				if (myPstmt!= null) myPstmt.close();
			}
			catch (Exception ex) {
				System.err.println("getSelectMoves::close; Error; "+ex.getMessage());
			}
		}
		return selectMovesInfo;
	}
}
