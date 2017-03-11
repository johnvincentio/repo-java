
package com.idc.coder;

import java.util.Iterator;

/**
 * @author John Vincent
 *
 */

public class MakeFormInfo extends MakeCode {
	public MakeFormInfo (CodeTable codeTable) {
		super (codeTable);
	}

	public String makeCode() {
		appendNL();
		append (makePackage());
		appendNL();
		appendNL ("import com.hertz.hercwebutil.presentation.FormInfo;");
		appendNL ("import com.hertz.hercutil.presentation.UtilHelper;");
		appendNL ("import com.hertz.irac.framework.HertzSystemException;");
		appendNL();
		appendNL ("public class "+getFormInfoClassName()+" extends FormInfo {");
		append (makeMembers());
		appendNL();
		append (makeGetters());
		appendNL();
		append (makeSetters());

		CodePair codePair;
		appendNL();
		appendNL (TAB+"protected void doValidation() throws HertzSystemException {");
		if (! getCodeTable().isEmpty()) {
			Iterator<CodePair> iter = getCodeTable().getItems();
			while (iter.hasNext()) {
				codePair = (CodePair) iter.next();
				appendNL (TAB+TAB+"if (! UtilHelper.isSet("+codePair.getName()+"))");
				appendNL (TAB+TAB+TAB+"addError ("+QUOTE+codePair.getName()+" is required."+QUOTE+");");
			}
		}
		appendNL (TAB+"}");

		append (makeToString());
		append (makeEndClass());
		return getBuffer();
	}
}
