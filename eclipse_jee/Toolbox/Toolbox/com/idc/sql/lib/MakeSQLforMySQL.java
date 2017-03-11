package com.idc.sql.lib;

import java.sql.Types;
import java.util.Iterator;

import com.idc.trace.LogHelper;
import com.idc.utils.JVString;

public class MakeSQLforMySQL {
	private static final String TAB = "\t";
	private static final String TIC = "`";

	public static void dropTable (Output output, String toSchema, TableInfo tableInfo) {
//		LogHelper.info ("Writing the drop table statements for table "+tableInfo.getName());
		output.writeNL();
		output.writeNL ("drop table "+TIC+toSchema+TIC+"."+TIC+tableInfo.getName()+TIC+";");
	}

	public static void makeCreateTable (Output output, String toSchema, TableInfo tableInfo) {
//		LogHelper.info ("Writing the create table statements for table "+tableInfo.getName());
//		tableInfo.doShowPrimaryKeys();
		
		StringBuffer buf;
		output.writeNL();
		output.writeNL ("create table "+TIC+toSchema+TIC+"."+TIC+tableInfo.getName()+TIC+" (");
		int last_col = tableInfo.getCollist().size() - 1;
		for (int j=0; j<tableInfo.getCollist().size(); j++) {
			ColumnInfo columnInfo = (ColumnInfo) tableInfo.getCollist().get(j);
			buf = null;
			buf = new StringBuffer();
			buf.append(TAB+TIC+columnInfo.getName()+TIC).append(" ");
					
			if (columnInfo.getType() == Types.CHAR) {
				if (columnInfo.getLen() > 255)
					buf.append("varchar").append("(").append(columnInfo.getLen()+")");
				else
					buf.append("char").append("(").append(columnInfo.getLen()+")");
			}
			else if (columnInfo.getType() == Types.VARCHAR) {
				if (columnInfo.getLen() < 21845) {
					if (columnInfo.getLen() > 255 && columnInfo.getPk()) 
						buf.append("varchar").append("(255)");
					else
						buf.append("varchar").append("(").append(columnInfo.getLen()+")");
				}
				else
					buf.append("text");
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
				buf.append("decimal").append("(").append(columnInfo.getLen()).append(",").append(columnInfo.getScale()+")");
			else if (columnInfo.getType() == Types.NUMERIC)
				buf.append("numeric").append("(").append(columnInfo.getLen()+")");
			else if (columnInfo.getType() == Types.DATE)
				buf.append("date");
			else if (columnInfo.getType() == Types.TIMESTAMP)
				buf.append("timestamp");
			else if (columnInfo.getType() == Types.TIME)
				buf.append("time");
			else if (columnInfo.getType() == Types.BLOB)
				buf.append("blob");	
			else if (columnInfo.getType() == Types.CLOB)
				buf.append("text");
			else if (columnInfo.getType() == Types.LONGVARBINARY)
				buf.append("longvarbinary");
			else if (columnInfo.getType() == Types.BIT)
				buf.append("bit");
			else
				LogHelper.info("unknown type "+columnInfo.getType()+" column "+columnInfo.getName()+" in table "+tableInfo.getName());

			if (columnInfo.getNullable())
				buf.append(" not null");
			if (j < last_col)
				buf.append(",");
//			else
//				if (tableInfo.isExistPkList()) buf.append(",");
			output.writeNL (buf.toString());
		}
/*
		if (tableInfo.isExistPkList()) {
			buf = null;
			buf = new StringBuffer();
			buf.append ("CONSTRAINT PRIMARY KEY (");
			int last_id = tableInfo.getPklist().size() - 1;
			for (int k=0; k<tableInfo.getPklist().size(); k++) {
				String pk = tableInfo.getPklist().get(k);
				buf.append (pk);
				if (k < last_id)
					buf.append(",");
			}
			buf.append (")");
			output.writeNL (buf.toString());
		}
*/
		output.writeNL (");");
	}

	public static void makeIndexes (Output output, String toSchema, TableInfo tableInfo) {
//		LogHelper.info("Writing the create index statements for table "+tableInfo.getName());
		IndexesInfo indexesInfo = tableInfo.getIdxlist();
		if (! indexesInfo.isNone()) output.writeNL();
		for (Iterator<IndexInfo> iter = indexesInfo.getItems(); iter.hasNext(); ) {
			IndexInfo indexInfo = (IndexInfo) iter.next();
			output.write ("create");
			if (indexInfo.isUnique()) output.write (" unique");
			output.write (" index "+TIC+indexInfo.getIndexName()+TIC);
			output.write (" on "+TIC+toSchema+TIC+"."+TIC+tableInfo.getName()+TIC + " (");
			boolean bFirst = true;
			for (Iterator<IndexItemInfo> iter2 = indexInfo.getItems(); iter2.hasNext(); ) {
				IndexItemInfo indexItemInfo = (IndexItemInfo) iter2.next();
				if (! bFirst) output.write(", ");
				bFirst = false;
				output.write (TIC+indexItemInfo.getColumnName()+TIC + " ");
				output.write ((indexItemInfo.isAsc() ? "ASC" : "DESC"));
			}
			output.writeNL (");");
		}
	}

	public static void makeCreateInserts (DB db, Output output, String toSchema, TableInfo tableInfo) {
//		LogHelper.info ("Writing insert statements for table "+tableInfo.getName());
//		db.makeSQLForRows (output, toSchema, tableInfo);
		RowsInfo rowsInfo = db.getRows(tableInfo);
		Iterator<RowInfo> iter = rowsInfo.getItems();
		while (iter.hasNext()) {
			RowInfo rowInfo = iter.next();
			makeCreateInsert (output, rowInfo, toSchema, tableInfo);
			rowInfo = null;
		}
		output.writeNL("commit;");
		output.writeNL();
//		LogHelper.info ("Finished writing insert statements for table "+tableInfo.getName());
	}

	public static void makeCreateInsert (Output output, RowInfo rowInfo, String toSchema, TableInfo tableInfo) {
		output.write ("insert into " + TIC + toSchema + TIC + "." + TIC + tableInfo.getName() + TIC + " (");
		int last_col = tableInfo.getCollist().size() - 1;
		for (int j = 0; j < tableInfo.getCollist().size(); j++) {
			ColumnInfo columnInfo = (ColumnInfo) tableInfo.getCollist().get(j);
			output.write (TIC+columnInfo.getName()+TIC);
			if (j < last_col) output.write (",");
			columnInfo = null;
		}
		output.writeNL (") ");

		output.write ("values (");
		boolean first = true;
		for (Iterator<DataInfo> iterColumns = rowInfo.getItems(); iterColumns.hasNext(); ) {
			DataInfo dataInfo = (DataInfo) iterColumns.next();
			if (! first) output.write (",");
			first = false;
			output.write (JVString.cleanString(dataInfo.getData()));
			dataInfo = null;
		}
		output.writeNL (");");
		output.writeNL();
	}
}
