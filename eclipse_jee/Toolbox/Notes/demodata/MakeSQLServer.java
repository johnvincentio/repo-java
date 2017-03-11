package com.idc.sql.app.old;

/**
* @author John Vincent
*/

import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;

import com.idc.sql.lib.ColumnInfo;
import com.idc.sql.lib.DataInfo;
import com.idc.sql.lib.Output;
import com.idc.sql.lib.RowInfo;
import com.idc.sql.lib.RowsInfo;
import com.idc.sql.lib.TableInfo;
import com.idc.trace.LogHelper;

public class MakeSQLServer {
	private Output m_output;
	private ArrayList<TableInfo> allTableInfo = new ArrayList<TableInfo>();
	private static final String OUTPUT_FILE="c:\\tmp\\out.sql";
	private static final boolean bInserts = true;
	private static final boolean bTables = true;

	public static void main(String[] args) {
		MakeSQLServer test = new MakeSQLServer();
		test.doWork();
	}

	public void doWork() {
		TableInfo tableInfo;
		ColumnInfo columnInfo;
		RowsInfo rowsInfo;
		RowInfo rowInfo;
		DataInfo dataInfo;
		boolean first;
		Iterator iterator;
		StringBuffer stmt;

		LogHelper.info("Opening file");		
		m_output = new Output();
		if (! m_output.open (OUTPUT_FILE)) {
			LogHelper.info("giving up...");
			System.exit(1);
		}

		String scenario = "airclickDev";
		String toSchema = "hercdb";
		LogHelper.info("Connecting to database using "+scenario);
		DBSQLServer db = new DBSQLServer();
		if (! db.getConnection (scenario)) {
			LogHelper.info("giving up...");
			System.exit(1);
		}
		String schema = db.getScenarios().getScenario(scenario).getSchema();

		LogHelper.info("Getting all tables for schema "+schema);
		ArrayList allTables = db.getTables();
		iterator = allTables.iterator();
		while (iterator.hasNext()) {
			String tableName = ((String) iterator.next()).toUpperCase();
			tableInfo = new TableInfo (tableName);
			allTableInfo.add (tableInfo);
		}

		LogHelper.info("Getting Primary Key and Column info");
		iterator = allTableInfo.iterator();
		while (iterator.hasNext()) {
			tableInfo = (TableInfo) iterator.next();
			tableInfo = getTable(tableInfo.getName());
			tableInfo.addPklist (db.getPrimaryKeys (tableInfo.getName()));	// get primary key info
			tableInfo.addColumnlist (db.getColumns (tableInfo.getName()));	// get column info
		}

		LogHelper.info("Rationalizing primary key info");
		Iterator iter1;
		String column;

		for (int i=0; i<allTableInfo.size(); i++) {
			tableInfo = (TableInfo) allTableInfo.get(i);
			iter1 = tableInfo.getPklist().iterator();
			while (iter1.hasNext()) {
				column = (String) iter1.next();
//				LogHelper.info("Primary key: "+column);
				columnInfo = tableInfo.getColumn(column);
				columnInfo.setPk(true);
			}
		}

		doShowInfo();	// trace the internals

		LogHelper.info("Writing the sql file");
		addMsgNL();
		addMsgNL("-- generated SQL file");
		addMsgNL();
/*
 * for each table
 */
		StringBuffer buf;
		for (int i=0; i<allTableInfo.size(); i++) {
			tableInfo = (TableInfo) allTableInfo.get(i);
			int last_col = tableInfo.getCollist().size() - 1;
			if (bTables) {

/*
 * handle drop table
 */
				LogHelper.info("Writing the drop table statements for table "+tableInfo.getName());
				addMsgNL();
				addMsgNL ("drop table "+toSchema+"."+tableInfo.getName()+";");
/*
 * handle create table
 */
				LogHelper.info("Writing the create table statements for table "+tableInfo.getName());
				addMsgNL();
				addMsgNL ("create table "+toSchema+"."+tableInfo.getName()+" (");
				for (int j=0; j<tableInfo.getCollist().size(); j++) {
					columnInfo = (ColumnInfo) tableInfo.getCollist().get(j);
					buf = null;
					buf = new StringBuffer();
					buf.append(columnInfo.getName()).append(" ");
						
					if (columnInfo.getType() == Types.CHAR) {
						buf.append("char").append("(").append(columnInfo.getLen()+")");
					}
					else if (columnInfo.getType() == Types.VARCHAR) {
						buf.append("varchar").append("(").append(columnInfo.getLen()+")");
					}
					else if (columnInfo.getType() == Types.SMALLINT)
						buf.append("smallint");
					else if (columnInfo.getType() == Types.INTEGER)
						buf.append("integer");
					else if (columnInfo.getType() == Types.BIGINT)
						buf.append("bigint");
					else if (columnInfo.getType() == Types.FLOAT)
						buf.append("float");										
					else if (columnInfo.getType() == Types.DOUBLE)
						buf.append("double");
					else if (columnInfo.getType() == Types.DECIMAL)
						buf.append("decimal");	
					else if (columnInfo.getType() == Types.NUMERIC)
						buf.append("numeric");
					else if (columnInfo.getType() == Types.DATE)
						buf.append("date");					
					else if (columnInfo.getType() == Types.TIMESTAMP)
						buf.append("timestamp");
					else if (columnInfo.getType() == Types.TIME)
						buf.append("time");
					else if (columnInfo.getType() == Types.BLOB)
						buf.append("blob");	
					else if (columnInfo.getType() == Types.CLOB)
						buf.append("clob");
					else if (columnInfo.getType() == Types.BIT)
						buf.append("bit");	
					else
						LogHelper.info("unknown type "+columnInfo.getType()+" column "+columnInfo.getName()+
							" in table "+tableInfo.getName());

					if (columnInfo.getPk())
						buf.append(" primary key");
					if (columnInfo.getNullable())
						buf.append(" not null");
					if (j < last_col) buf.append(",");
					addMsgNL (buf.toString());
				}				
				addMsgNL (") in herc_4k;");

/*
 * handle grants
 */
				LogHelper.info("Writing the grant statements for table "+tableInfo.getName());
				addMsgNL();
				buf = null;
				buf = new StringBuffer();
				buf.append ("grant select, insert, update, delete on table "+toSchema+"."+tableInfo.getName());
				addMsgNL (buf.toString()+" to user PRC4031;");
				addMsgNL (buf.toString()+" to user PRC3056;");
				addMsgNL (buf.toString()+" to user DB2LOADR;");
				addMsgNL (buf.toString()+" to user PRC0178;");
				addMsgNL (buf.toString()+" to user PRC0161;");
				addMsgNL (buf.toString()+" to user PRC1038;");
				addMsgNL (buf.toString()+" to user PRC9203;");
				addMsgNL (buf.toString()+" to user PRC0214;");
				addMsgNL (buf.toString()+" to user DB2USER;");
				addMsgNL (buf.toString()+" to user PRC7807;");
				addMsgNL (buf.toString()+" to user PRC1016;");
				addMsgNL();
				buf = null;
			}

/*
 * handle insert statements
 */
			if (bInserts) {
				LogHelper.info("Writing the insert statements for table "+tableInfo.getName());
				rowsInfo = db.getRows (tableInfo, tableInfo.getCollist());
//				LogHelper.info(rowsInfo.toString());
				stmt = null;
				stmt = new StringBuffer();
				stmt.append ("insert into ").append (toSchema).append(".").append (tableInfo.getName());
				stmt.append (" (");
				for (int j=0; j < tableInfo.getCollist().size(); j++) {
					columnInfo = (ColumnInfo) tableInfo.getCollist().get(j);
					stmt.append (columnInfo.getName());
					if (j < last_col) stmt.append(",");
				}
				stmt.append (") ");

				addMsgNL ();
				Iterator iterRow = rowsInfo.getItems();
				while (iterRow.hasNext()) {
					addMsgNL (stmt.toString());
					buf = null;
					buf = new StringBuffer();
					buf.append ("values (");
					rowInfo = (RowInfo) iterRow.next();
					Iterator iterColumns = rowInfo.getItems();
					first = true;
					while (iterColumns.hasNext()) {
						dataInfo = (DataInfo) iterColumns.next();
						if (! first) buf.append (",");
						first = false;
						buf.append (dataInfo.getData());
					}
					buf.append (");");
					addMsgNL (buf.toString());
				}
				addMsgNL();
			}
		}
		addMsgNL("-- end of generated SQL \n");
		m_output.close();

		LogHelper.info("Disconnecting from database");
		db.disConnect();

		LogHelper.info("SQL file "+OUTPUT_FILE);
		LogHelper.info("exiting...");
	}
	public TableInfo getTable(String name) {
		TableInfo info;
		Iterator iterator = allTableInfo.iterator();
		while (iterator.hasNext()) {
			info = (TableInfo) iterator.next();
			if ((info.getName()).equals(name)) return info;
		}
		return null;
	}

	private void doShowInfo() {
		TableInfo info;
		Iterator iterator = allTableInfo.iterator();
		while (iterator.hasNext()) {
			info = (TableInfo) iterator.next();
			info.doShowInfo();
		}
	}

    public void addMsg (String msg) {m_output.write(msg);}
    public void addMsgNL (String msg) {m_output.write(msg+"\n");}
    public void addMsgNL () {m_output.write("\n");}
    public void addMsg (int num) {addMsg(Integer.toString(num));}
}
