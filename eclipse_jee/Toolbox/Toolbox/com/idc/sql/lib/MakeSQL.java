package com.idc.sql.lib;

import java.sql.Types;
import java.util.Iterator;

import com.idc.trace.LogHelper;

public class MakeSQL {
	private static final String TAB = "\t";

	public static void dropTable (Output output, String toSchema, TableInfo tableInfo) {
		LogHelper.info("Writing the drop table statements for table "+tableInfo.getName());
		output.writeNL();
		output.writeNL ("drop table "+toSchema+"."+tableInfo.getName()+";");
	}

	public static void makeCreateTable (Output output, String toSchema, TableInfo tableInfo) {
		LogHelper.info("Writing the create table statements for table "+tableInfo.getName());
		StringBuffer buf;
		output.writeNL();
		output.writeNL ("create table "+toSchema+"."+tableInfo.getName()+" (");
		int last_col = tableInfo.getCollist().size() - 1;
		for (int j=0; j<tableInfo.getCollist().size(); j++) {
			ColumnInfo columnInfo = (ColumnInfo) tableInfo.getCollist().get(j);
			buf = null;
			buf = new StringBuffer();
			buf.append(TAB+columnInfo.getName()).append(" ");
					
			if (columnInfo.getType() == Types.CHAR) {
				if (columnInfo.getLen() > 255)
					buf.append("varchar").append("(").append(columnInfo.getLen()+")");
				else
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
				buf.append("clob");
			else if (columnInfo.getType() == Types.LONGVARBINARY)
				buf.append("longvarbinary");
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
			output.writeNL (buf.toString());
		}				
		output.writeNL (") in herc_4k;");
	}

	public static void makeIndexes (Output output, String toSchema, TableInfo tableInfo) {
		LogHelper.info("Writing the create index statements for table "+tableInfo.getName());
		IndexesInfo indexesInfo = tableInfo.getIdxlist();
		if (! indexesInfo.isNone()) output.writeNL();
		for (Iterator<IndexInfo> iter = indexesInfo.getItems(); iter.hasNext(); ) {
			IndexInfo indexInfo = (IndexInfo) iter.next();
			output.write ("create");
			if (indexInfo.isUnique()) output.write (" unique");
			output.write (" index "+toSchema+"."+indexInfo.getIndexName());
			output.write (" on "+toSchema+"."+tableInfo.getName() + " (");
			boolean bFirst = true;
			for (Iterator<IndexItemInfo> iter2 = indexInfo.getItems(); iter2.hasNext(); ) {
				IndexItemInfo indexItemInfo = (IndexItemInfo) iter2.next();
				if (! bFirst) output.write(", ");
				bFirst = false;
				output.write (indexItemInfo.getColumnName() + " ");
				output.write ((indexItemInfo.isAsc() ? "ASC" : "DESC"));
			}
			output.writeNL (");");
		}
	}

	public static void makeGrants (Output output, String toSchema, String table) {
		LogHelper.info("Writing the grant statements for table "+table);
		output.writeNL();
		StringBuffer buf = new StringBuffer();
		buf.append ("grant select, insert, update, delete, alter on table "+toSchema+"."+table);
		output.writeNL (buf.toString()+" to user PRC4031;");		// JV
		output.writeNL (buf.toString()+" to user DB2LOADR;");
		buf = new StringBuffer();
		buf.append ("grant select, insert, update, delete on table "+toSchema+"."+table);
		output.writeNL (buf.toString()+" to user DB2USER;");
		output.writeNL (buf.toString()+" to user PRC3056;");		// KV
		output.writeNL (buf.toString()+" to user PRC1038;");		// manasi
		output.writeNL (buf.toString()+" to user PRC0214;");		// pablo
		output.writeNL (buf.toString()+" to user PRC3046;");		// pawel
		output.writeNL (buf.toString()+" to user PRC0178;");		// danielle
		output.writeNL (buf.toString()+" to user PRC0216;");		// ravi

		output.writeNL (buf.toString()+" to user PRC7807;");		// parimal
		output.writeNL (buf.toString()+" to user PRC1016;");		// catalin

		output.writeNL (buf.toString()+" to user DTC9028;");		// Bob Iacobucci
		output.writeNL (buf.toString()+" to user DTC0056;");		// TANUSHREE THAKUR
		output.writeNL (buf.toString()+" to user DTC0839;");		// SWAPNIL JOSHI
		output.writeNL (buf.toString()+" to user DTC1552;");		// SAMAD SHAIKH
	}

	public static void makeCreateInserts (DB db, Output output, String toSchema, TableInfo tableInfo) {
		LogHelper.info ("Writing insert statements for table "+tableInfo.getName());
		db.makeSQLForRows (output, toSchema, tableInfo);
		output.writeNL("commit;");
		output.writeNL();
		LogHelper.info ("Finished writing insert statements for table "+tableInfo.getName());
	}

	public static void makeCreateInsert (Output output, RowInfo rowInfo, String toSchema, TableInfo tableInfo) {
		output.write ("insert into " + toSchema + "." + tableInfo.getName() + " (");
		int last_col = tableInfo.getCollist().size() - 1;
		for (int j = 0; j < tableInfo.getCollist().size(); j++) {
			ColumnInfo columnInfo = (ColumnInfo) tableInfo.getCollist().get(j);
			output.write (columnInfo.getName());
			if (j < last_col) output.write (",");
		}
		output.writeNL (") ");

		output.write ("values (");
		boolean first = true;
		for (Iterator<DataInfo> iterColumns = rowInfo.getItems(); iterColumns.hasNext(); ) {
			DataInfo dataInfo = (DataInfo) iterColumns.next();
			if (! first) output.write (",");
			first = false;
			output.write (dataInfo.getData());
		}
		output.writeNL (");");
		output.writeNL();
	}
}
