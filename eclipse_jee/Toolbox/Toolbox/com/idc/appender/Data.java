
package com.idc.appender;

import java.util.Iterator;

public class Data {
	public Rows makeData (String line) {
//		System.out.println("line :"+line+":");
		SplitString splitString = new SplitString();
		splitString.splitBuffer(line,"\n");
//		System.out.println("length "+splitString.length());
		Rows rows = new Rows();
		while (splitString.hasNext()) {
			rows.add (new Row(splitString.getNext().trim()));
		}
		rows = formatRows (rows);
		return rows;
	}
	private Rows formatRows (Rows pRows) {
		Rows rows = new Rows();
		boolean bComment = false;
		boolean bTemporaryIndent = false;
		int indent = 0;
		Iterator<Row> iter = pRows.getItems();
		while (iter.hasNext()) {
			Row row = (Row) iter.next();
			String line = row.getRow();
			System.out.println("\nNext Line :"+line+":");
			if (isComment(line)) {
				rows.add (new Row("//"+makeTabs(indent)+line.substring(2).trim()));
				continue;
			}
//			if (isStartCommentAndEndComment(line)) {
//				newline = line;
//			}
			if (isStartComment(line)) {
				System.out.println("Start Comment");
				bComment = true;
				rows.add (new Row(line));
				continue;
			}
			if (isEndComment(line)) {
				System.out.println("End Comment");
				if (startEmbeddedComment(line) < 0) {
					System.out.println("Embedded Comment");
					bComment = false;
					rows.add (new Row(line));
					continue;
				}
			}
			if (bComment) {
				System.out.println("Still a Comment");
				rows.add (new Row(line));
				continue;
			}
			if (isEmptyLine(line)) {
				System.out.println("empty line");
				rows.add (new Row(line));
				continue;
			}
			if (isOpenParam(line) && isCloseParam(line)) {
				System.out.println("Open and Close Param; indent "+indent);
				rows.add (new Row(makeTabs(indent) + line));
				continue;
			}
			if (isOpenParam(line)) {
				System.out.println("Open Param; indent "+indent);
				if (bTemporaryIndent) {
					int tmp = indent - 1;
					if (tmp < 0) tmp = 0;
					String newline = makeTabs(tmp) + line;
					rows.add (new Row(newline));
					bTemporaryIndent = false;
				}
				else {
					String newline = makeTabs(indent) + line;
					indent++;
					rows.add (new Row(newline));
				}
				continue;
			}
			if (isCloseParam(line)) {
				System.out.println("Close Param; indent "+indent);
				indent--;
				String newline = makeTabs(indent) + line;
				rows.add (new Row(newline));
				continue;
			}
			if (bTemporaryIndent) {
				System.out.println("Temporary Indent; indent "+indent);
				rows.add (new Row(makeTabs(indent) + line));
				if (isEndLine(line)) {
					System.out.println("Temporary Indent - End of line");
					indent--;
					bTemporaryIndent = false;
				}
			}
			else {
				System.out.println("Not Temporary Indent; indent "+indent);
				if (isEndLine(line)) {
					rows.add (new Row(makeTabs(indent) + line));
				}
				else {
					System.out.println("Not Temporary Indent - Not end of line");
					rows.add (new Row(makeTabs(indent) + line));
					indent++;
					bTemporaryIndent = true;
				}
			}
		}
		return rows;
	}
	private String makeTabs (int tabs) {
		String str = "";
		for (int i=0; i<tabs; i++) str += "\t";
		return str;
	}
	private boolean isComment(String str) {
		if (str.startsWith("//")) return true;
		return false;
	}
	private int startEmbeddedComment(String str) {
		int pos1a = str.indexOf("/*");
		int pos1b = str.indexOf("//");
		if (pos1a < 0 && pos1b < 0) return -1;
		if (pos1b > -1) return pos1b;

		int pos2 = str.indexOf("*/");
		if (pos2 < 0) return -1;
		if (pos1a < pos2) return pos1a;
		return -1;
	}
	private boolean isStartComment(String str) {
		if (str.startsWith("/*")) return true;
		return false;
	}
	private boolean isEndComment (String str) {
		if (str.indexOf("*/") > -1) return true;
		return false;	
	}
	private boolean isEndLine (String str) {
		String line = str.trim();
		int pos = startEmbeddedComment(line);
		String line2 = line;
		if (pos > -1) line2 = line.substring(0, pos-1);
		String line3 = line2.trim();
		return line3.trim().endsWith(";");
	}
	private boolean isEmptyLine (String str) {return str.length() < 1;}
	private boolean isOpenParam (String str) {
		int qpos = str.indexOf("{");
		if (qpos < 0) return false;
		int bpos = str.indexOf('"');
		int epos = str.lastIndexOf('"');
		if (bpos < qpos && qpos < epos) return false;
		return true;
	}
	private boolean isCloseParam (String str) {
		return str.endsWith("}");
	}
}
