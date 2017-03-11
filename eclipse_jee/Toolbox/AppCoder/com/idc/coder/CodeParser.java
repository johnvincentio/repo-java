
package com.idc.coder;

import com.idc.coder.JVString;
import com.idc.coder.StringSplitter;

/**
 * @author John Vincent
 *
 */

public class CodeParser {

	public CodeTable parser(String strText) {
		CodeTable codeTable = new CodeTable();
		String strInput = cleanTabs (strText);
		System.out.println("strText\n"+strText+"\n");
		System.out.println("strInput\n"+strInput+"\n");
		StringSplitter lines = new StringSplitter (strInput, "\n");		// parse out lines
		while (lines.hasNext()) {
			String comment = "";
			StringSplitter codeLines = new StringSplitter (lines.getNext(), "//");	// parse out comments
			String code = cleanColon(codeLines.getNext().trim());
			if (codeLines.hasNext()) comment = codeLines.getNext().trim();
			System.out.println("\ncode :"+code+":");
			System.out.println("Comment :"+comment+":");

			StringSplitter words = new StringSplitter (code, " ");	// parse out words
			String word1 = words.getNext();
			if (! words.hasNext()) continue;
			String word2 = words.getNext();
			System.out.println("word1 :"+word1+":");
			System.out.println("word2 :"+word2+":");
			if ("package".equals(word1)) {
				codeTable.setPackageName(word2);		// package name
				continue;
			}

			if ("static".equals(word1) || "static".equals(word2)) continue;
			if ("final".equals(word1) || "final".equals(word2)) continue;

			String word3, word4, word5;
			if ("class".equals(word1) || "class".equals(word2)) {		// class
				if ("class".equals(word1))
					word3 = word2;
				else {
					if (! words.hasNext()) continue;
					word3 = words.getNext().trim();
				}
				codeTable.setBaseClassName(word3);		// class name
				if (! words.hasNext()) continue;
				word4 = words.getNext().trim();
				if (! words.hasNext()) continue;
				word5 = words.getNext().trim();
				if ("extends".equals(word4)) {
					codeTable.setSuperClass(word5);
					if (! words.hasNext()) continue;
					word4 = words.getNext().trim();
					if (! words.hasNext()) continue;
					word5 = words.getNext().trim();
				}
				if ("implements".equals(word4)) {
					codeTable.setImplementsClass(word5);
				}
				continue;				
			}

			CodePair codePair = new CodePair();
			if ("public".equals(word1) || "private".equals(word1) || "protected".equals(word1)) {
				codePair.setVisible(word1);
				codePair.setType(word2);
				if (! words.hasNext()) continue;
				word2 = words.getNext().trim();
			}
			else
				codePair.setType(word1);
			codePair.setName(word2);
			codePair.setComment(comment);

			if (words.hasNext()) {
				word3 = words.getNext().trim();
				if ("=".equals(word3)) {
					if (words.hasNext()) codePair.setValue(words.getNext().trim());
				}
			}
			codeTable.add (codePair);
		}
		System.out.println("codeTable "+codeTable.toString());
		return codeTable;
	}

	private String cleanTabs (String s) {
		JVString jvstr = new JVString(s);
		jvstr.replace("\t"," ");
		return jvstr.getString();
	}
	private String cleanColon (String s) {
		JVString jvstr = new JVString(s);
		jvstr.replace(";","");
		return jvstr.getString();
	}
}

/*
	private String test() {
		return cleanCode (
		"		 int barcodeid; " +
		"		 boolean itemid; " +
		"		 		 String company; " +
		"		 		 		 long imageurl; float name; double dblFred; char chrPloy;");
	}
*/

/*
public CodeTable parserOLD(String strText) {
	CodeTable codeTable = new CodeTable();
	String strInput = cleanCode (strText);
	System.out.println("strText\n"+strText+"\n");
	System.out.println("strInput\n"+strInput+"\n");

	StringSplitter splits = new StringSplitter (strInput, " ");
	if (splits.length() < 1) {
		System.out.println ("\n\n\t\tYour code makes no sense\n\n");
		return codeTable;
	}

	codeTable.setPackageName("com.idc.coder");
	codeTable.setBaseClassName("MyClass");
	String strText1, strText2;
	while (splits.hasNext()) {
		strText1 = splits.getNext();
		if (strText1 == null || strText1.length() < 1) continue;
		if ("{".equals(strText1)) continue;
		if ("package".equals(strText1)) {
			if (! splits.hasNext()) break;
			codeTable.setPackageName(splits.getNext().trim());
			continue;
		}
		if ("public".equals(strText1)) continue;
		if ("private".equals(strText1)) continue;
		if ("class".equals(strText1)) {
			if (! splits.hasNext()) break;
			codeTable.setBaseClassName(splits.getNext().trim());
			continue;
		}
		if ("implements".equals(strText1)) {
			if (! splits.hasNext()) break;
			strText2 = splits.getNext().trim();
			continue;
		}
		if ("extends".equals(strText1)) {
			if (! splits.hasNext()) break;
			codeTable.setSuperClass(splits.getNext().trim());
			continue;
		}
		if (! splits.hasNext()) break;		 		 // no matching pair
		strText2 = splits.getNext();
		System.out.println("Code Pair :"+strText1+" "+strText2);
		codeTable.add (new CodePair(strText1, strText2));
	}
	System.out.println("codeTable "+codeTable.toString());
	return codeTable;
}

private String cleanCode (String s) {
	JVString jvstr = new JVString(s);
	jvstr.replace("\t"," ");
	jvstr.replace("\n"," ");
	jvstr.replace(";"," ");
	return jvstr.getString();
}
*/
