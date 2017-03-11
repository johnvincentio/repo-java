
package com.idc.appcoder.parser;

/**
 * @author John Vincent
 *
 */

public class CodeParser {

	public CodeTable parser (String strText) {
		System.out.println(">>> CodeParser::parser");
		CodeTable codeTable = new CodeTable();
		String strInput = cleanTabs (strText);
		System.out.println("strText\n"+strText+"\n");
		System.out.println("strInput\n"+strInput+"\n");
		StringSplitter lines = new StringSplitter (strInput, "\n");		// parse out lines
		while (lines.hasNext()) {
			String currentLine = lines.getNext();

			String comment = "";
			StringSplitter codeLines = new StringSplitter (currentLine, "//");	// parse out comments
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
				codeTable.setBaseClassName(cleanClassName(word3));		// class name
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
		System.out.println("<<< CodeParser::parser");
		return codeTable;
	}

	private String cleanTabs (String s) {
		return JVString.replace(s, "\t", " ");
	}
	private String cleanColon (String s) {
		return JVString.replace(s, ";", "");
	}
	private String cleanClassName (String s) {
		String tmp = JVString.removeLastIgnoreCase (s, "iteminfo");
		tmp = JVString.removeLastIgnoreCase (tmp, "info");
		tmp = JVString.initUpper (tmp);
		if (tmp.length() < 1) return "Dummy";
		return tmp;
	}
	public static void main (String[] args) {
		CodeParser codeParser = new CodeParser();
		System.out.println("1 "+codeParser.cleanClassName(""));
		System.out.println("2 "+codeParser.cleanClassName("Abcd"));
		System.out.println("3 "+codeParser.cleanClassName("AbcdInfo"));
		System.out.println("4 "+codeParser.cleanClassName("AbcdItemInfo"));
		System.out.println("5 "+codeParser.cleanClassName("InfoAbcd"));
		System.out.println("6 "+codeParser.cleanClassName("ItemInfoAbcd"));
		System.out.println("7 "+codeParser.cleanClassName("Abcdinfo"));
		System.out.println("8 "+codeParser.cleanClassName("Abcditeminfo"));
		System.out.println("9 "+codeParser.cleanClassName("infoAbcd"));
		System.out.println("10 "+codeParser.cleanClassName("iteminfoAbcd"));
		System.out.println("11 "+codeParser.cleanClassName("abcd"));
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
