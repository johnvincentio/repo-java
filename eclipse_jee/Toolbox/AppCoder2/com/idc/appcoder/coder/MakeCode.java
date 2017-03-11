package com.idc.appcoder.coder;

import java.util.Iterator;

import com.idc.appcoder.parser.CodePair;
import com.idc.appcoder.parser.CodeTable;
import com.idc.appcoder.parser.JVString;

/**
 * @author John Vincent
 *
 */

public class MakeCode {
	public static final String NL = "\n";
	public static final String TAB = "\t";
	public static final String QUOTE = "\"";

	private CodeTable m_codeTable;
	public MakeCode (CodeTable codeTable) {m_codeTable = codeTable;}
	public CodeTable getCodeTable() {return m_codeTable;}

	public String test0() {return "abcd";}
	public String test1 (String abc) {return ":"+abc+":";}
	public String test2 (String a, String b) {return ":"+a+": :"+b+":";}
	
	public String getPackageName() {return m_codeTable.getPackageName();}
	public String getBaseClassName() {return m_codeTable.getBaseClassName();}
	public String getSuperClassName() {return m_codeTable.getSuperClass();}
	public boolean isSuperClass() {return m_codeTable.isSuperClass();}
	public String getBeanClassName() {return m_codeTable.getBaseClassName() + "ItemInfo";}
	public String getKeyBeanClassName() {return m_codeTable.getBaseClassName() + "KeyItemInfo";}
	public String getCollectionClassName() {return m_codeTable.getBaseClassName() + "Info";}
	public String getCollectionItemClassName() {return m_codeTable.getBaseClassName() + "ItemInfo";}
	public String getHelperClassName() {return m_codeTable.getBaseClassName()+"Helper";}

	public String getCollectionVariableName() {
		return JVString.initLower (getCollectionClassName());
	}
	public String getBeanVariableName() {
		return JVString.initLower (getBeanClassName());
	}
	public String getFormInfoClassName() {return m_codeTable.getBaseClassName() + "FormInfo";}
	public String getHttpPageInfoClassName() {return m_codeTable.getBaseClassName() + "HttpPageInfo";}
	public String getHttpInfoClassName() {return m_codeTable.getBaseClassName() + "HttpInfo";}

	public String methodName (String s) {
		return JVString.initUpper (s);
	}
	public String variableName (String s) {
		return JVString.initLower (s);
	}
	public String methodGetter (CodePair codePair) {
		if ("boolean".equals(codePair.getType()))
			return "is" + methodName(codePair.getName()) + "()";
		return "get" + methodName(codePair.getName()) + "()";
	}
	public String addQuotes (String s) {
		return QUOTE + s + QUOTE;
	}

	public String makePackage() {return "package "+getPackageName()+";";}

	public String makeStartClass() {
		return makeStartClass (getBaseClassName());
	}
	public String makeStartClass (String baseClassName) {
		StringBuffer buf = new StringBuffer();
		buf.append("public class ").append(baseClassName);
		buf.append(" implements Serializable");
		if (isSuperClass()) buf.append(" extends ").append(getSuperClassName());
		buf.append (" {").append(NL);
		buf.append (TAB).append ("private static final long serialVersionUID = 1L;");
		return buf.toString();
	}

	public String makeEndClass() {return "}";}

	public String makeMembers() {return makeMembers (false);}
	public String makeMembers (Boolean hash) {
		StringBuffer buf = new StringBuffer();
		CodePair codePair;
		Iterator<CodePair> iter = m_codeTable.getItems();
		boolean bFirst = true;
		while (iter.hasNext()) {
			codePair = (CodePair) iter.next();
			if (! bFirst) buf.append(NL);
			bFirst = false;
			buf.append (TAB+codePair.getVisible()+" "+codePair.getType()+" "+codePair.getName());
			if (codePair.isValue()) buf.append(" = ").append(codePair.getValue());
			buf.append (";");
			if (codePair.isComment()) buf.append("\t\t// ").append(codePair.getComment());
		}
		if (hash.booleanValue()) 
			buf.append (NL+TAB+"private int hashCode = 0;");
		return buf.toString();
	}

	public String makeConstructor (String className) {
		if (m_codeTable.isEmpty()) return "";
		return TAB+"public "+className+"() {}";
	}
	public String makeConstructorArgs() {return makeConstructorArgs (getBaseClassName(), false);}

	public String makeConstructorArgs (String className) {return makeConstructorArgs (className, false);}
	public String makeConstructorArgs (String className, Boolean hash) {
		if (m_codeTable.isEmpty()) return "";
		boolean bFirst = true;
		CodePair codePair;
		StringBuffer buf = new StringBuffer();
		buf.append (TAB+"public "+className+" (");
		Iterator<CodePair> iter = m_codeTable.getItems();
		while (iter.hasNext()) {	// declare the member variables
			codePair = (CodePair) iter.next();
			if (! bFirst) buf.append (", ");
			bFirst = false;
			buf.append (codePair.getType()+" "+codePair.getName());
		}
		buf.append (") {"+NL);

		iter = m_codeTable.getItems();
		while (iter.hasNext()) {			// copy params to member variables
			codePair = (CodePair) iter.next();
			buf.append (TAB+TAB+"this."+codePair.getName()+" = "+codePair.getName()+";"+NL);
		}

		if (hash.booleanValue()) {
			bFirst = true;
			buf.append (TAB+TAB+"hashCode = (");
			iter = m_codeTable.getItems();
			while (iter.hasNext()) {			// make hashcode
				codePair = (CodePair) iter.next();
				if (! bFirst) buf.append (" + " + QUOTE+ ";" + QUOTE + " + ");
				if (codePair.getType().equalsIgnoreCase("string"))
					buf.append (codePair.getName());
				else if (codePair.getType().equalsIgnoreCase("int"))
					buf.append ("Integer.toString("+codePair.getName()+")");
				else if (codePair.getType().equalsIgnoreCase("long"))
					buf.append ("Long.toString("+codePair.getName()+")");
				else if (codePair.getType().equalsIgnoreCase("float"))
					buf.append ("Float.toString("+codePair.getName()+")");
				else if (codePair.getType().equalsIgnoreCase("double"))
					buf.append ("Double.toString("+codePair.getName()+")");
				else
					buf.append (codePair.getType()+".toString("+codePair.getName()+")");
				bFirst = false;
			}
			buf.append (").hashCode();"+NL);
		}

		buf.append (TAB+"}");
		return buf.toString();
	}

	public String makeGetters() {
		if (m_codeTable.isEmpty()) return "";
		CodePair codePair;
		StringBuffer buf = new StringBuffer();
		Iterator<CodePair> iter = m_codeTable.getItems();
		boolean bFirst = true;
		while (iter.hasNext()) {
			codePair = (CodePair) iter.next();
			if (! bFirst) buf.append (NL);
			bFirst = false;
			buf.append (TAB+"public "+codePair.getType()+" ");
			buf.append (methodGetter(codePair));
			buf.append (" {return "+codePair.getName()+";}");
		}
		return buf.toString();
	}

	public String makeSetters() {
		if (m_codeTable.isEmpty()) return "";
		CodePair codePair;
		StringBuffer buf = new StringBuffer();
		Iterator<CodePair> iter = m_codeTable.getItems();
		boolean bFirst = true;
		while (iter.hasNext()) {
			codePair = (CodePair) iter.next();
			if (! bFirst) buf.append (NL);
			bFirst = false;
			buf.append (TAB+"public void set");
			buf.append (methodName(codePair.getName()));
			buf.append (" ("+codePair.getType()+" "+codePair.getName()+") ");
			buf.append ("{this."+codePair.getName()+" = ");
			buf.append (codePair.getName()+";}");
		}
		return buf.toString();
	}

	public String makeToString() {
		if (m_codeTable.isEmpty()) return "";
		CodePair codePair;
		StringBuffer buf = new StringBuffer();
		boolean bFirst = true;
		buf.append (TAB+"public String toString() {"+NL);
		buf.append (TAB+TAB+"return ");
		if (! getCodeTable().isEmpty()) {
			buf.append (addQuotes("("));
			Iterator<CodePair> iter = m_codeTable.getItems();
			while (iter.hasNext()) {		// declare the member variables
				codePair = (CodePair) iter.next();
 				if (! bFirst) buf.append ("+"+addQuotes(","));
				bFirst = false;
				buf.append ("+"+methodGetter(codePair));
			}
			buf.append ("+"+addQuotes(")"));
		}
		buf.append (";"+NL);
		buf.append (TAB+"}");
		return buf.toString();
	}

	public String makeArrayListToString() {
		CodePair codePair;
		boolean bFirst = true;
		StringBuffer buf = new StringBuffer();
		buf.append (TAB+"public String toString() {"+NL);
		buf.append (TAB+TAB+"StringBuffer buf = new StringBuffer();"+NL);
		buf.append (TAB+TAB+"for (int i = 0; i < m_collection.size(); i++)"+NL);
		buf.append (TAB+TAB+TAB+"buf.append((("+getCollectionItemClassName()+") m_collection.get(i)).toString());"+NL);
		buf.append (TAB+TAB+"return ");
		if (! getCodeTable().isEmpty()) {
			buf.append (QUOTE+"("+QUOTE);
			Iterator<CodePair> iter = getCodeTable().getItems();
			while (iter.hasNext()) {			// declare the member variables
				codePair = (CodePair) iter.next();
				if (! bFirst) buf.append ("+"+QUOTE+","+QUOTE);
				bFirst = false;
				buf.append ("+"+methodGetter(codePair));
			}
			buf.append ("+"+QUOTE+"),"+QUOTE+"+");
		}
		buf.append (QUOTE+"("+QUOTE+"+");
		buf.append ("buf.toString()");
		buf.append ("+"+QUOTE+")"+QUOTE);
		buf.append (";"+NL);
		buf.append (TAB+"}");
		return buf.toString();
	}

	public String makeHashMapToString() {
		StringBuffer buf = new StringBuffer();
		buf.append (TAB+"public String toString() {return "+QUOTE+"("+QUOTE+"+m_map+"+QUOTE+")"+QUOTE+";}");
		return buf.toString();
	}

	public String makeSetToString() {
		CodePair codePair;
		boolean bFirst = true;
		StringBuffer buf = new StringBuffer();
		buf.append (TAB+"public String toString() {"+NL);
		buf.append (TAB+TAB+"StringBuffer buf = new StringBuffer();"+NL);
//		buf.append (TAB+TAB+"buf.append (" + QUOTE+ "(" + QUOTE + ");"+NL);
		buf.append (TAB+TAB+"Iterator iter = m_collection.iterator();"+NL);
		buf.append (TAB+TAB+"while (iter.hasNext()) {"+NL);
		buf.append (TAB+TAB+TAB+getCollectionItemClassName()+" item = ("+getCollectionItemClassName()+") iter.next();"+NL);
		buf.append (TAB+TAB+TAB+"if (item != null) buf.append (item);"+NL);
		buf.append (TAB+TAB+"}"+NL);

		buf.append (TAB+TAB+"return ");
		if (! getCodeTable().isEmpty()) {
			buf.append (QUOTE+"("+QUOTE);
			Iterator<CodePair> iter = getCodeTable().getItems();
			while (iter.hasNext()) {			// declare the member variables
				codePair = (CodePair) iter.next();
				if (! bFirst) buf.append ("+"+QUOTE+","+QUOTE);
				bFirst = false;
				buf.append ("+"+methodGetter(codePair));
			}
			buf.append ("+"+QUOTE+"),"+QUOTE+"+");
		}
		buf.append (QUOTE+"("+QUOTE+"+");
		buf.append ("buf.toString()");
		buf.append ("+"+QUOTE+")"+QUOTE);
		buf.append (";"+NL);
		buf.append (TAB+"}");
		return buf.toString();
	}

	public String makeKeyGetMethods () {
		if (m_codeTable.isEmpty()) return "";
		CodePair codePair;
		StringBuffer buf = new StringBuffer();
		buf.append (TAB+"public "+getKeyBeanClassName()+" getKeyItemInfo ("+getBeanClassName()+" item) {"+NL);
		buf.append (TAB+TAB+"return new "+getKeyBeanClassName()+" (");
		Iterator<CodePair> iter = m_codeTable.getItems();
		boolean bFirst = true;
		while (iter.hasNext()) {
			codePair = (CodePair) iter.next();
			if (! bFirst) buf.append (", ");
			bFirst = false;
			buf.append ("item."+methodGetter (codePair));
		}
		buf.append (");"+NL);
		buf.append (TAB+"}"+NL);

		buf.append (TAB+"public "+getKeyBeanClassName()+" getKeyItemInfo (");
		iter = m_codeTable.getItems();
		bFirst = true;
		while (iter.hasNext()) {
			codePair = (CodePair) iter.next();
			if (! bFirst) buf.append (", ");
			bFirst = false;
			buf.append (codePair.getType()+" "+codePair.getName());
		}
		buf.append (") {"+NL);
		buf.append (TAB+TAB+"return new "+getKeyBeanClassName()+" (");
		iter = m_codeTable.getItems();
		bFirst = true;
		while (iter.hasNext()) {
			codePair = (CodePair) iter.next();
			if (! bFirst) buf.append (", ");
			bFirst = false;
			buf.append (codePair.getName());
		}
		buf.append (");"+NL);
		buf.append (TAB+"}");
		return buf.toString();
	}

	public String makeFormInfo() {
		StringBuffer buf = new StringBuffer();
		buf.append (makePackage());
		buf.append (NL+NL);

		buf.append ("import com.hertz.herc.framework.FormValidationHelper"+NL);
		buf.append ("import com.hertz.herc.presentation.util.FormInfo"+NL);
		buf.append ("import com.hertz.hercutil.presentation.StringMap"+NL);
		buf.append ("import com.hertz.hercutil.presentation.UtilHelper"+NL);
		buf.append ("import com.hertz.irac.framework.HertzSystemException"+NL);
		buf.append (NL);

		buf.append ("public class "+getFormInfoClassName()+" extends FormInfo {"+NL);
		buf.append (makeMembers());
		buf.append (NL+NL);
		buf.append (makeGetters());
		buf.append (NL+NL);
		buf.append (makeSetters());
		buf.append (NL+NL);

		CodePair codePair;
		buf.append (TAB+"protected void doValidation() throws HertzSystemException {"+NL);
		if (! getCodeTable().isEmpty()) {
			Iterator<CodePair> iter = getCodeTable().getItems();
			while (iter.hasNext()) {
				codePair = (CodePair) iter.next();
				if ("StringMap".equals(codePair.getType())) {
					buf.append (TAB+TAB+"if (! FormValidationHelper.isEmpty ("+codePair.getName()+"))"+NL);
					buf.append (TAB+TAB+"}"+NL);
				}
				else {
					buf.append (TAB+TAB+"if (! FormValidationHelper.is"+methodName(codePair.getName())+" ("+codePair.getName()+", true))"+NL);
					buf.append (TAB+TAB+TAB+"addErrorField (FCN_INVALID_"+codePair.getName().toUpperCase()+", "+QUOTE+codePair.getName()+QUOTE+");"+NL);
				}
			}
		}
		buf.append (TAB+"}"+NL);

		buf.append (makeToString()+NL);
		buf.append (makeEndClass());
		return buf.toString();
	}

	public String makeHelperGetters() {
		if (m_codeTable.isEmpty()) return "";
		CodePair codePair;
		StringBuffer buf = new StringBuffer();
		Iterator<CodePair> iter = m_codeTable.getItems();
		boolean bFirst = true;
		while (iter.hasNext()) {
			codePair = (CodePair) iter.next();
			if (! bFirst) buf.append (NL);
			bFirst = false;
			buf.append (TAB+"public static "+codePair.getType()+" "+methodGetter(codePair)+" (HttpServletRequest request) {"+NL);
			buf.append (TAB+TAB+"return get"+getHttpInfoClassName()+" (request)."+methodGetter(codePair)+";"+NL);
			buf.append (TAB+"}"+NL);
		}
		return buf.toString();
	}

	public String makeHelperSetters() {
		if (m_codeTable.isEmpty()) return "";
		CodePair codePair;
		StringBuffer buf = new StringBuffer();
		Iterator<CodePair> iter = m_codeTable.getItems();
		boolean bFirst = true;
		while (iter.hasNext()) {
			codePair = (CodePair) iter.next();
			if (! bFirst) buf.append (NL);
			bFirst = false;
			buf.append (TAB+"public static void set"+methodName(codePair.getName())+" (HttpServletRequest request, ");
			buf.append (codePair.getType()+" "+codePair.getName()+") {"+NL);
			buf.append (TAB+TAB+"get"+getHttpInfoClassName()+" (request).set"+methodName(codePair.getName())+"("+codePair.getName()+");"+NL);
			buf.append (TAB+TAB+"update"+getHttpInfoClassName()+" (request);"+NL);
			buf.append (TAB+"}"+NL);
		}
		return buf.toString();
	}

	public String makeIsSame() {
		if (m_codeTable.isEmpty()) return "";
		CodePair codePair;
		StringBuffer buf = new StringBuffer();
		buf.append (TAB+"public boolean isSame ("+getBeanClassName()+" item) {"+NL);
		if (! getCodeTable().isEmpty()) {
			Iterator<CodePair> iter = m_codeTable.getItems();
			while (iter.hasNext()) {
				codePair = (CodePair) iter.next();
				if (codePair.getType().equalsIgnoreCase("string"))
					buf.append (TAB+TAB+"if (! UtilHelper.isEquals ("+methodGetter(codePair)+", item."+methodGetter(codePair)+")) return false;"+NL);
				else if (codePair.getType().equalsIgnoreCase("int") ||
						codePair.getType().equalsIgnoreCase("long") || 
						codePair.getType().equalsIgnoreCase("float") ||
						codePair.getType().equalsIgnoreCase("boolean") ||
						codePair.getType().equalsIgnoreCase("double")) {
					buf.append (TAB+TAB+"if ("+methodGetter(codePair)+" != item."+methodGetter(codePair)+") return false;"+NL);
				}
				else if (codePair.getType().equalsIgnoreCase("HercDate")) {
					buf.append (TAB+TAB+"if (! "+methodGetter(codePair)+".toString().equals (item."+methodGetter(codePair)+".toString())) return false;"+NL);
				}
				else
					buf.append (codePair.getType()+"dont know what to do ("+codePair.getName()+")"+NL);
			}
		}
		buf.append (TAB+TAB+"return true;"+NL);
		buf.append (TAB+"}");
		return buf.toString();
	}

	public String makeDataHelper() {
		StringBuffer buf = new StringBuffer();
		String str = JVString.removeLastIgnoreCase (getBaseClassName(), "data");
		buf.append ("public class Replicated"+getBaseClassName()+"Helper {"+NL);
		buf.append (NL);
		buf.append (TAB+"public static void doTasks (MigrationLogger logger, ");
		buf.append (getCollectionClassName()+" infoDC, ");
		buf.append (getCollectionClassName()+" infoRC, ");
		buf.append (getCollectionClassName()+" infoDC2) throws Exception {"+NL);

		buf.append (TAB+TAB+"logger.info ("+QUOTE+str+" checking has started."+QUOTE+");"+NL);
		buf.append (NL);

/*
		logger.info ("DC is master checks.");
		Iterator iteratorDC = infoDC.getItems();
		while (iteratorDC.hasNext()) {
			`getBeanClassName()` itemInfoDC = (`getBeanClassName()`) iteratorDC.next();
			if (itemInfoDC == null) continue;
			long companyid = itemInfoDC.getCompanyid();
			`getBeanClassName()` itemInfoRC = infoRC.getItem (companyid);
			if (itemInfoRC == null)
				logger.error ("Company "+companyid+" exists on DC but does NOT exist on RC.");
			else {
				if (! itemInfoDC.isSame (itemInfoRC))
					logger.error ("Company "+companyid+" DC and RC are NOT the same.");
			}

			`getBeanClassName()` itemInfoDC2 = infoDC2.getItem (companyid);
			if (itemInfoDC2 == null)
				logger.error ("Company "+companyid+" exists on DC but does NOT exist on DC2.");
			else {
				if (! itemInfoDC.isSame (itemInfoDC2))
					logger.error ("Company "+companyid+" DC and DC2 are NOT the same.");
			}
		}
		logger.info ("DC as master checks are complete.");
*/

		buf.append (TAB+TAB+"logger.info ("+QUOTE+">>> DC as master checks."+QUOTE+");"+NL);
		buf.append (TAB+TAB+"Iterator iteratorDC = infoDC.getItems();"+NL);
		buf.append (TAB+TAB+"while (iteratorDC.hasNext()) {"+NL);
		buf.append (TAB+TAB+TAB+getBeanClassName()+" itemInfoDC = ("+getBeanClassName()+") iteratorDC.next();"+NL);
		buf.append (TAB+TAB+TAB+"if (itemInfoDC == null) continue;"+NL);
		
		Iterator<CodePair> iter = getCodeTable().getItems();
		while (iter.hasNext()) {
			CodePair codePair = (CodePair) iter.next();
			buf.append (TAB+TAB+TAB+codePair.getType()+" "+codePair.getName()+" = itemInfoDC.get"+methodName(codePair.getName())+"();"+NL);
		}

		buf.append (TAB+TAB+TAB+getBeanClassName()+" itemInfoRC = infoRC.getItem (");
		boolean bFirst = true;
		iter = getCodeTable().getItems();
		while (iter.hasNext()) {
			CodePair codePair = (CodePair) iter.next();
			if (! bFirst) buf.append (", ");
			bFirst = false;
			buf.append (codePair.getName());
		}
		buf.append (");"+NL);

		buf.append (TAB+TAB+TAB+"if (itemInfoRC == null)"+NL);
		buf.append (TAB+TAB+TAB+TAB+"logger.error ("+QUOTE+str+" "+QUOTE);
		bFirst = true;
		iter = getCodeTable().getItems();
		while (iter.hasNext()) {
			CodePair codePair = (CodePair) iter.next();
			if (! bFirst) buf.append ("+"+QUOTE+" "+QUOTE);
			bFirst = false;
			buf.append ("+");
			buf.append (codePair.getName());
		}
		buf.append ("+"+QUOTE+" exists on DC but does NOT exist on RC."+QUOTE+");"+NL);

		buf.append (TAB+TAB+TAB+"else {"+NL);
		buf.append (TAB+TAB+TAB+TAB+"if (! itemInfoDC.isSame (itemInfoRC))"+NL);
		buf.append (TAB+TAB+TAB+TAB+TAB+"logger.error ("+QUOTE+str+" "+QUOTE);
		bFirst = true;
		iter = getCodeTable().getItems();
		while (iter.hasNext()) {
			CodePair codePair = (CodePair) iter.next();
			if (! bFirst) buf.append ("+"+QUOTE+" "+QUOTE);
			bFirst = false;
			buf.append ("+");
			buf.append (codePair.getName());
		}
		buf.append ("+"+QUOTE+" DC and RC are NOT the same."+QUOTE+");"+NL);
		buf.append (TAB+TAB+TAB+"}"+NL);

		buf.append (TAB+TAB+TAB+getBeanClassName()+" itemInfoDC2 = infoDC2.getItem (");
		bFirst = true;
		iter = getCodeTable().getItems();
		while (iter.hasNext()) {
			CodePair codePair = (CodePair) iter.next();
			if (! bFirst) buf.append (", ");
			bFirst = false;
			buf.append (codePair.getName());
		}
		buf.append (");"+NL);
		buf.append (TAB+TAB+TAB+"if (itemInfoDC2 == null)"+NL);
		buf.append (TAB+TAB+TAB+TAB+"logger.error ("+QUOTE+str+" "+QUOTE);
		bFirst = true;
		iter = getCodeTable().getItems();
		while (iter.hasNext()) {
			CodePair codePair = (CodePair) iter.next();
			if (! bFirst) buf.append ("+"+QUOTE+" "+QUOTE);
			bFirst = false;
			buf.append ("+");
			buf.append (codePair.getName());
		}
		buf.append ("+"+QUOTE+" exists on DC but does NOT exist on DC2."+QUOTE+");"+NL);

		buf.append (TAB+TAB+TAB+"else {"+NL);
		buf.append (TAB+TAB+TAB+TAB+"if (! itemInfoDC.isSame (itemInfoDC2))"+NL);
		buf.append (TAB+TAB+TAB+TAB+TAB+"logger.error ("+QUOTE+str+" "+QUOTE);
		bFirst = true;
		iter = getCodeTable().getItems();
		while (iter.hasNext()) {
			CodePair codePair = (CodePair) iter.next();
			if (! bFirst) buf.append ("+"+QUOTE+" "+QUOTE);
			bFirst = false;
			buf.append ("+");
			buf.append (codePair.getName());
		}
		buf.append ("+"+QUOTE+" DC and DC2 are NOT the same."+QUOTE+");"+NL);
		buf.append (TAB+TAB+TAB+"}"+NL);
		buf.append (TAB+TAB+"}"+NL);
		buf.append (TAB+TAB+"logger.info ("+QUOTE+"<<< DC as master checks are complete."+QUOTE+");"+NL);

/*	
		logger.info ("RC as master checks.");
		Iterator iteratorRC = infoRC.getItems();
		while (iteratorRC.hasNext()) {
			`getBeanClassName()` itemInfoRC = (`getBeanClassName()`) iteratorRC.next();
			if (itemInfoRC == null) continue;
			long companyid = itemInfoRC.getCompanyid();
			if (! infoDC.isExists (companyid)) logger.error ("Company "+companyid+" exists on RC but does NOT exist on DC.");
			if (! infoDC2.isExists (companyid)) logger.error ("Company "+companyid+" exists on RC but does NOT exist on DC2.");
		}
		logger.info ("RC as master checks are complete.");
*/
		buf.append (NL);
		buf.append (TAB+TAB+"logger.info ("+QUOTE+">>> RC as master checks."+QUOTE+");"+NL);
		buf.append (TAB+TAB+"Iterator iteratorRC = infoRC.getItems();"+NL);
		buf.append (TAB+TAB+"while (iteratorRC.hasNext()) {"+NL);
		buf.append (TAB+TAB+TAB+getBeanClassName()+" itemInfoRC = ("+getBeanClassName()+") iteratorRC.next();"+NL);
		buf.append (TAB+TAB+TAB+"if (itemInfoRC == null) continue;"+NL);

		iter = getCodeTable().getItems();
		while (iter.hasNext()) {
			CodePair codePair = (CodePair) iter.next();
			buf.append (TAB+TAB+TAB+codePair.getType()+" "+codePair.getName()+" = itemInfoRC.get"+methodName(codePair.getName())+"();"+NL);
		}

		buf.append (TAB+TAB+TAB+"if (! infoDC.isExists (");
		bFirst = true;
		iter = getCodeTable().getItems();
		while (iter.hasNext()) {
			CodePair codePair = (CodePair) iter.next();
			if (! bFirst) buf.append (", ");
			bFirst = false;
			buf.append (codePair.getName());
		}
		buf.append (")) "+NL);
		buf.append (TAB+TAB+TAB+TAB+"logger.error ("+QUOTE+str+" "+QUOTE);
		bFirst = true;
		iter = getCodeTable().getItems();
		while (iter.hasNext()) {
			CodePair codePair = (CodePair) iter.next();
			if (! bFirst) buf.append ("+"+QUOTE+" "+QUOTE);
			bFirst = false;
			buf.append ("+");
			buf.append (codePair.getName());
		}
		buf.append ("+"+QUOTE+" exists on RC but does NOT exist on DC."+QUOTE+");"+NL);

		buf.append (TAB+TAB+TAB+"if (! infoDC2.isExists (");
		bFirst = true;
		iter = getCodeTable().getItems();
		while (iter.hasNext()) {
			CodePair codePair = (CodePair) iter.next();
			if (! bFirst) buf.append (", ");
			bFirst = false;
			buf.append (codePair.getName());
		}
		buf.append (")) "+NL);
		buf.append (TAB+TAB+TAB+TAB+"logger.error ("+QUOTE+str+" "+QUOTE);
		bFirst = true;
		iter = getCodeTable().getItems();
		while (iter.hasNext()) {
			CodePair codePair = (CodePair) iter.next();
			if (! bFirst) buf.append ("+"+QUOTE+" "+QUOTE);
			bFirst = false;
			buf.append ("+");
			buf.append (codePair.getName());
		}
		buf.append ("+"+QUOTE+" exists on RC but does NOT exist on DC2."+QUOTE+");"+NL);
		buf.append (TAB+TAB+"}"+NL);
		buf.append (TAB+TAB+"logger.info ("+QUOTE+"<<< RC as master checks are complete."+QUOTE+");"+NL);

//3rd phase here

/*
		logger.info ("DC2 as master checks.");
		Iterator iteratorDC2 = infoDC2.getItems();
		while (iteratorDC2.hasNext()) {
			`getBeanClassName()` itemInfoDC2 = (`getBeanClassName()`) iteratorDC2.next();
			if (itemInfoDC2 == null) continue;
			long companyid = itemInfoDC2.getCompanyid();
			if (! infoRC.isExists (companyid)) logger.error ("Company "+companyid+" exists on DC2 but does NOT exist on RC.");
			if (! infoDC.isExists (companyid)) logger.error ("Company "+companyid+" exists on DC2 does NOT exist on DC.");
		}
		logger.info ("DC2 as master checks are complete.");
*/
		buf.append (NL);
		buf.append (TAB+TAB+"logger.info ("+QUOTE+">>> DC2 as master checks."+QUOTE+");"+NL);
		buf.append (TAB+TAB+"Iterator iteratorDC2 = infoDC2.getItems();"+NL);
		buf.append (TAB+TAB+"while (iteratorDC2.hasNext()) {"+NL);
		buf.append (TAB+TAB+TAB+getBeanClassName()+" itemInfoDC2 = ("+getBeanClassName()+") iteratorDC2.next();"+NL);
		buf.append (TAB+TAB+TAB+"if (itemInfoDC2 == null) continue;"+NL);

		iter = getCodeTable().getItems();
		while (iter.hasNext()) {
			CodePair codePair = (CodePair) iter.next();
			buf.append (TAB+TAB+TAB+codePair.getType()+" "+codePair.getName()+" = itemInfoDC2.get"+methodName(codePair.getName())+"();"+NL);
		}

		buf.append (TAB+TAB+TAB+"if (! infoRC.isExists (");
		bFirst = true;
		iter = getCodeTable().getItems();
		while (iter.hasNext()) {
			CodePair codePair = (CodePair) iter.next();
			if (! bFirst) buf.append (", ");
			bFirst = false;
			buf.append (codePair.getName());
		}
		buf.append (")) "+NL);
		buf.append (TAB+TAB+TAB+TAB+"logger.error ("+QUOTE+str+" "+QUOTE);
		bFirst = true;
		iter = getCodeTable().getItems();
		while (iter.hasNext()) {
			CodePair codePair = (CodePair) iter.next();
			if (! bFirst) buf.append ("+"+QUOTE+" "+QUOTE);
			bFirst = false;
			buf.append ("+");
			buf.append (codePair.getName());
		}
		buf.append ("+"+QUOTE+" exists on DC2 but does NOT exist on RC."+QUOTE+");"+NL);

		buf.append (TAB+TAB+TAB+"if (! infoDC.isExists (");
		bFirst = true;
		iter = getCodeTable().getItems();
		while (iter.hasNext()) {
			CodePair codePair = (CodePair) iter.next();
			if (! bFirst) buf.append (", ");
			bFirst = false;
			buf.append (codePair.getName());
		}
		buf.append (")) "+NL);
		buf.append (TAB+TAB+TAB+TAB+"logger.error ("+QUOTE+str+" "+QUOTE);
		bFirst = true;
		iter = getCodeTable().getItems();
		while (iter.hasNext()) {
			CodePair codePair = (CodePair) iter.next();
			if (! bFirst) buf.append ("+"+QUOTE+" "+QUOTE);
			bFirst = false;
			buf.append ("+");
			buf.append (codePair.getName());
		}
		buf.append ("+"+QUOTE+" exists on DC2 but does NOT exist on DC."+QUOTE+");"+NL);
		buf.append (TAB+TAB+"}"+NL);
		buf.append (TAB+TAB+"logger.info ("+QUOTE+"<<< DC2 as master checks are complete."+QUOTE+");"+NL);

/*	
logger.info ("`getBaseClassName()` checking is complete.");
*/
		buf.append (TAB+TAB+"logger.info ("+QUOTE+str+" checking is complete."+QUOTE+");"+NL);

		//Done
		buf.append (TAB+"}"+NL);
		buf.append ("}"+NL);
		return buf.toString();
	}
}
/*

public class Replicated`getBaseClassName()`Helper {

	public static void doTasks (MigrationLogger logger, `getCollectionClassName()` infoDC, `getCollectionClassName()` infoRC, `getCollectionClassName()` infoDC2) throws Exception {
		logger.info ("`getBaseClassName()` checking has started.");
		
		logger.info ("DC is master checks.");
		Iterator iteratorDC = infoDC.getItems();
		while (iteratorDC.hasNext()) {
			`getBeanClassName()` itemInfoDC = (`getBeanClassName()`) iteratorDC.next();
			if (itemInfoDC == null) continue;
			long companyid = itemInfoDC.getCompanyid();
			`getBeanClassName()` itemInfoRC = infoRC.getItem (companyid);
			if (itemInfoRC == null)
				logger.error ("Company "+companyid+" exists on DC but does NOT exist on RC.");
			else {
				if (! itemInfoDC.isSame (itemInfoRC))
					logger.error ("Company "+companyid+" DC and RC are NOT the same.");
			}

			`getBeanClassName()` itemInfoDC2 = infoDC2.getItem (companyid);
			if (itemInfoDC2 == null)
				logger.error ("Company "+companyid+" exists on DC but does NOT exist on DC2.");
			else {
				if (! itemInfoDC.isSame (itemInfoDC2))
					logger.error ("Company "+companyid+" DC and DC2 are NOT the same.");
			}
		}
		logger.info ("DC as master checks are complete.");

		logger.info ("RC as master checks.");
		Iterator iteratorRC = infoRC.getItems();
		while (iteratorRC.hasNext()) {
			`getBeanClassName()` itemInfoRC = (`getBeanClassName()`) iteratorRC.next();
			if (itemInfoRC == null) continue;
			long companyid = itemInfoRC.getCompanyid();
			if (! infoDC.isExists (companyid)) logger.error ("Company "+companyid+" exists on RC but does NOT exist on DC.");
			if (! infoDC2.isExists (companyid)) logger.error ("Company "+companyid+" exists on RC but does NOT exist on DC2.");
		}
		logger.info ("RC as master checks are complete.");

		logger.info ("DC2 as master checks.");
		Iterator iteratorDC2 = infoDC2.getItems();
		while (iteratorDC2.hasNext()) {
			`getBeanClassName()` itemInfoDC2 = (`getBeanClassName()`) iteratorDC2.next();
			if (itemInfoDC2 == null) continue;
			long companyid = itemInfoDC2.getCompanyid();
			if (! infoRC.isExists (companyid)) logger.error ("Company "+companyid+" exists on DC2 but does NOT exist on RC.");
			if (! infoDC.isExists (companyid)) logger.error ("Company "+companyid+" exists on DC2 does NOT exist on DC.");
		}
		logger.info ("DC2 as master checks are complete.");

		logger.info ("`getBaseClassName()` checking is complete.");
	}
*/

/*
	public String makeMembersInit() {
		StringBuffer buf = new StringBuffer();
		CodePair codePair;
		Iterator iter = m_codeTable.getItems();
		while (iter.hasNext()) {
			codePair = (CodePair) iter.next();
			buf.append (TAB+"private "+codePair.getType()+" "+codePair.getName());
			buf.append (" = "+addQuotes("")+";"+NL);
		}
		return buf.toString();
	}
	public String makeBoolean() {
	if (m_codeTable.isEmpty()) return "";
	CodePair codePair;
	StringBuffer buf = new StringBuffer();
	Iterator iter = m_codeTable.getItems();
	while (iter.hasNext()) {
		codePair = (CodePair) iter.next();
		if ("boolean".equals(codePair.getType())) {
//			codePair = (CodePair) iter.next();
			buf.append (TAB+"public "+codePair.getType());
			buf.append (" is"+methodName(codePair.getName())+"() ");
			buf.append ("{return "+codePair.getName());
			buf.append (" = true;}"+NL);
		}
	}
	return buf.toString();
}
	public String addKeyPair (String key, String value) {
		return key + "=" + addQuotes (value);
	}
*/

/*
		Iterator iter = getCodeTable().getItems();
		while (iter.hasNext()) {
			CodePair codePair = (CodePair) iter.next();
			if ("StringMap".equals(codePair.getType())) {
				buf.append (TAB+TAB+"if (! FormValidationHelper.isEmpty ("+codePair.getName()+"))"+NL);
				buf.append (TAB+TAB+"}"+NL);
			}
			else {
				buf.append (TAB+TAB+"if (! FormValidationHelper.is"+methodName(codePair.getName())+" ("+codePair.getName()+", true))"+NL);
				buf.append (TAB+TAB+TAB+"addErrorField (FCN_INVALID_"+codePair.getName().toUpperCase()+", "+QUOTE+codePair.getName()+QUOTE+");"+NL);
			}
		}

		while (iter.hasNext()) {
			codePair = (CodePair) iter.next();
			if (! bFirst) buf.append (NL);
			bFirst = false;
			buf.append (TAB+"public void set");
			buf.append (methodName(codePair.getName()));
			buf.append (" ("+codePair.getType()+" "+codePair.getName()+") ");
			buf.append ("{this."+codePair.getName()+" = ");
			buf.append (codePair.getName()+";}");
		}
*/
