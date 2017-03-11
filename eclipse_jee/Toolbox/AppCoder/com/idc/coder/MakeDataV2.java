
package com.idc.coder;

import java.util.Iterator;

/**
 * @author John Vincent
 *
 */

public class MakeDataV2 extends MakeCode {
	 
	public MakeDataV2 (CodeTable codeTable) {
		super (codeTable);
	}

	public String makeCode() {
		CodePair codePair;
//		boolean bFirst;
		Iterator<CodePair> iter;
		if (! getCodeTable().isEmpty()) {
			iter = getCodeTable().getItems();
			while (iter.hasNext()) {
				codePair = (CodePair) iter.next();
				append ("LogBroker.debug(classRef,");
				append (addQuotes (codePair.getName()+" ("));
				append ("+zlen(encrypt(c.");
				append (methodGetter(codePair));
				append ("))+"+addQuotes (") "));
				append ("+encrypt(c.");
				append (methodGetter(codePair));
				appendNL ("));");
			}
			appendNL();
			appendNL();
		}
		return getBuffer();
	}
}
//pstmt.setString(++i, creditApplicationUSInfo.getStatus());
/*
	private String submitted;
	
	private String immediateProcess;
	private String immediateDay;
	private String immediateMonthYear;
	private String businessName;
	private String businessAddress1;
	private String businessAddress2;
	private String businessCity;
	private String businessCountryState;
	private String businessPostalCode;
	private String businessPhone;
	private String businessFax;
	private String billingIsBusiness;
	private String billingContact;
	private String billingAddress1;
	private String billingAddress2;
	private String billingCity;
	private String billingCountryState;
	private String billingPostalCode;
	private String billingPhone;
	private String billingFax;
	private String businessType;
	private String businessTypeDescription;
	private String businessTypeEmployees;
	
	private String corporateTaxID;
	private String corporateMonthFormed;
	private String corporateYearFormed;
	private String corporateCountryStateFormed;
	private String corporateYearsOwned;
	private String corporateContractorLicense;
	private String corporateContractorLicenseMonth;
	private String corporateContractorLicenseYear;
	private String corporateParentCorp;
	private String corporateCity;
	private String corporateCountryState;
	private String corporatePostalCode;
	private String corporateDUN;
	private String corporateDUNNumber;
	private String corporateInitialCredit;
	private String corporateMonthlyCredit;
	
	private String bankName;
	private String bankContact;
	private String bankCheckingAccount;
	private String bankLoanAccount;
	private String bankCity;
	private String bankPhone;
	private String bondingName;
	private String bondingAddress1;
	private String bondingAddress2;
	private String bondingCity;
	private String bondingCountryState;
	private String bondingPostalCode;
	private String bondingPhone;
	private String bondingFax;
	private String additionalPurchaseOrder;
	private String additionalTaxExempt;
	private String additionalLiabilityInsurance;
	private String additionalPhysicalDamage;
	private String authorizedBank;
	private String authorizedPrefix;
	private String authorizedFirstName;
	private String authorizedMiddleName;
	private String authorizedLastName;
	private String authorizedSuffix;
	private String authorizedEmail1;
	private String authorizedEmail2;

*/
//this.eventenddate = (String) hashMap.get("event_end_date");

/*

	private String name;
	private String contact;
	private String account;
	private String city;
	private String countryState;
	private String postalCode;
	private String phone;
	private String fax;

*/