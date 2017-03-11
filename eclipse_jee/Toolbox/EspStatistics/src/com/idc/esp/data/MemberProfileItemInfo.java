/********************************************************************
*			Copyright (c) 2006 The Hertz Corporation				*
*			  All Rights Reserved.  (Unpublished.)					*
*																	*
*		The information contained herein is confidential and		*
*		proprietary to The Hertz Corporation and may not be			*
*		duplicated, disclosed to third parties, or used for any		*
*		purpose not expressly authorized by it.  Any unauthorized	*
*		use, duplication, or disclosure is prohibited by law.		*
*																	*
*********************************************************************/

package com.idc.esp.data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.idc.esp.utils.UtilHelper;

/**
 *	describe member profile information
 * @author John Vincent
 */

public class MemberProfileItemInfo implements Serializable {

	private static final long serialVersionUID = 5594900981507565022L;
	private static final String ACTIVE_FLAG_ACTIVE = "A";
	private static final String ACTIVE_FLAG_DISABLED = "D";
	private static final String ACTIVE_FLAG_LOCKED = "L";

	private static final String APPROVAL_FLAG_APPROVED = "A";
	private static final String APPROVAL_FLAG_INVITED = "I";
	private static final String APPROVAL_FLAG_PENDING = "P";
	private static final String APPROVAL_FLAG_DENIED = "D";
	private static final String APPROVAL_FLAG_MIGRATED = "M";

	private String username = "";

	private String active = "";
	private String approval = "";
	private MemberRole role;
	private MemberReports reports;

	private String password = "";
	private String question = "";
	private String answer = "";
	
	private boolean temporaryPassword;
	private int failureCounter;

	private String dialect = "";

	private String prefix = "";
	private String firstName = "";
	private String middleName = "";
	private String lastName = "";
	private String suffix = "";
	private String title = "";
	private String businessName = "";
	private String address1 = "";
	private String address2 = "";
	private String city = "";
	private String countryState = "";
	private String postalCode = "";
	private String email = "";
	private String phone = "";
	private String fax = "";
	private String poNumber = "";
	private String pcardType = "";
	private String pcardNumber = "";
	private String pcardMonth = "";
	private String pcardYear = "";
	private String securityCode = "";
	private boolean release;
	private boolean extend;
	private boolean fleet;
	private HercDate updateDate;
	private String approver = "";
	private String optIn;

	public MemberProfileItemInfo () {}

	/*
	 * Constructor only used for selecting a MemberProfile from the data store. Note the password
	 * is not returned.
	 */
	public MemberProfileItemInfo (Map<String, Object> map, String password) {
		this.username = ((String) map.get("hercUserId")).toLowerCase();
		this.active = (String) map.get("hercIsActive");
		this.approval = (String) map.get("hercIsApproved");
		if(((String) map.get("hercRole")) != null)
			this.role = new MemberRole ((String) map.get("hercRole"));
		else
			this.role = new MemberRole ();
		if(((String)map.get("hercReports"))!= null)
			this.reports = new MemberReports((String)map.get("hercReports"));
		else
			this.reports = new MemberReports();
		this.password = password;
		this.question = UtilHelper.getString(map,"hertzChallengeQuestion");
		this.answer = UtilHelper.getString(map,"hertzChallengeAnswer");

		this.temporaryPassword = UtilHelper.getBoolean ((String) map.get ("hertzIsTempPassword"));
		if (((String) map.get ("hertzLoginAttempts"))!= null)
			this.failureCounter = Integer.parseInt ((String) map.get ("hertzLoginAttempts"));
		else
			this.failureCounter = 0;
		
		this.dialect = UtilHelper.getString(map,"hertzPreferredLanguage");
		this.prefix = UtilHelper.getString(map,"hertzPrefixName");
		this.firstName = UtilHelper.getString(map,"hertzFirstName");
		this.middleName = UtilHelper.getString(map,"hertzMiddleInitial");
		this.lastName = UtilHelper.getString(map,"hertzLastName");
		this.suffix = UtilHelper.getString(map,"hertzSuffixName");
		this.title = UtilHelper.getString(map,"hercTitle");
		this.businessName = UtilHelper.getString(map,"hertzBusinessName");
		this.address1 = UtilHelper.getString(map,"hercProfileAddress1");
		this.address2 = UtilHelper.getString(map,"hercAddressSecond");
		this.city = UtilHelper.getString(map,"hercProfileCity");
		this.countryState = UtilHelper.getString(map,"hercProfileCountryState");
		this.postalCode = UtilHelper.getString(map,"hercProfilePostalCode");
		this.email = UtilHelper.getString(map,"hertzEmailAddress");
		this.phone = UtilHelper.getString(map,"hercProfilePhone");
		this.fax = UtilHelper.getString(map,"hercProfileFax");
		this.poNumber = UtilHelper.getString(map,"hercPoNumber");
		this.pcardType = UtilHelper.getString(map,"hercPCardType");
		this.pcardNumber = UtilHelper.getString(map,"hercPCardNumber");
		this.pcardMonth = UtilHelper.getString(map,"hercPCardMonth");
		this.pcardYear = UtilHelper.getString(map,"hercPCardYear");
		this.securityCode = UtilHelper.getString(map,"hercSecurityCode");

		this.release = UtilHelper.getBoolean((String) map.get("hercRelease"));
		this.extend = UtilHelper.getBoolean((String) map.get("hercExtend"));
		this.fleet = UtilHelper.getBoolean((String) map.get("hercFleet"));
		
		this.updateDate = new HercDate(HercDate.parseLDAPDate((String) map.get("hertzLastModifyDate")));
		this.approver = UtilHelper.getString(map,"hercApprover");
		this.optIn = UtilHelper.getString(map, "hercOptIn");
	}

	public void setAttribute (String key, String value) {
/*
		if(((String) map.get("hercRole")) != null)
			this.role = new MemberRole ((String) map.get("hercRole"));
		else
			this.role = new MemberRole ();
		if(((String)map.get("hercReports"))!= null)
			this.reports = new MemberReports((String)map.get("hercReports"));
		else
			this.reports = new MemberReports();
*/
		if (key.equalsIgnoreCase("hercUserId"))
			this.username = value;
		else if (key.equalsIgnoreCase("hercIsActive"))
			this.active = value;
		else if (key.equalsIgnoreCase("hercIsApproved"))
			this.approval = value;
		else if (key.equalsIgnoreCase("hercRole"))
			this.role = new MemberRole (value);
		else if (key.equalsIgnoreCase("hercReports"))
			this.reports = new MemberReports (value);
/*
		this.question = UtilHelper.getString(map,"hertzChallengeQuestion");
		this.answer = UtilHelper.getString(map,"hertzChallengeAnswer");

		this.temporaryPassword = UtilHelper.getBoolean ((String) map.get ("hertzIsTempPassword"));
		if (((String) map.get ("hertzLoginAttempts"))!= null)
			this.failureCounter = Integer.parseInt ((String) map.get ("hertzLoginAttempts"));
		else
			this.failureCounter = 0;
*/
		else if (key.equalsIgnoreCase("hertzChallengeQuestion"))
			this.question = value;
		else if (key.equalsIgnoreCase("hertzChallengeAnswer"))
			this.answer = value;
		else if (key.equalsIgnoreCase("hertzIsTempPassword"))
			this.temporaryPassword = UtilHelper.getBoolean (value);
/*
		this.dialect = UtilHelper.getString(map,"hertzPreferredLanguage");
		this.prefix = UtilHelper.getString(map,"hertzPrefixName");
		this.firstName = UtilHelper.getString(map,"hertzFirstName");
		this.middleName = UtilHelper.getString(map,"hertzMiddleInitial");
		this.lastName = UtilHelper.getString(map,"hertzLastName");
		this.suffix = UtilHelper.getString(map,"hertzSuffixName");
		this.title = UtilHelper.getString(map,"hercTitle");
*/
		else if (key.equalsIgnoreCase("hertzPreferredLanguage"))
			this.dialect = value;
		else if (key.equalsIgnoreCase("hertzPrefixName"))
			this.prefix = value;
		else if (key.equalsIgnoreCase("hertzFirstName"))
			this.firstName = value;
		else if (key.equalsIgnoreCase("hertzMiddleInitial"))
			this.middleName = value;
		else if (key.equalsIgnoreCase("hertzLastName"))
			this.lastName = value;
		else if (key.equalsIgnoreCase("hertzSuffixName"))
			this.suffix = value;
		else if (key.equalsIgnoreCase("hercTitle"))
			this.title = value;
/*
		this.businessName = UtilHelper.getString(map,"hertzBusinessName");
		this.address1 = UtilHelper.getString(map,"hercProfileAddress1");
		this.address2 = UtilHelper.getString(map,"hercAddressSecond");
		this.city = UtilHelper.getString(map,"hercProfileCity");
		this.countryState = UtilHelper.getString(map,"hercProfileCountryState");
		this.postalCode = UtilHelper.getString(map,"hercProfilePostalCode");
		this.email = UtilHelper.getString(map,"hertzEmailAddress");
		this.phone = UtilHelper.getString(map,"hercProfilePhone");
		this.fax = UtilHelper.getString(map,"hercProfileFax");
*/
		else if (key.equalsIgnoreCase("hertzBusinessName"))
			this.businessName = value;
		else if (key.equalsIgnoreCase("hercProfileAddress1"))
			this.address1 = value;
		else if (key.equalsIgnoreCase("hercAddressSecond"))
			this.address2 = value;
		else if (key.equalsIgnoreCase("hercProfileCity"))
			this.city = value;
		else if (key.equalsIgnoreCase("hercProfileCountryState"))
			this.countryState = value;
		else if (key.equalsIgnoreCase("hercProfilePostalCode"))
			this.postalCode = value;
		else if (key.equalsIgnoreCase("hertzEmailAddress"))
			this.email = value;
		else if (key.equalsIgnoreCase("hercProfilePhone"))
			this.phone = value;
		else if (key.equalsIgnoreCase("hercProfileFax"))
			this.fax = value;

/*
		this.release = UtilHelper.getBoolean((String) map.get("hercRelease"));
		this.extend = UtilHelper.getBoolean((String) map.get("hercExtend"));
		this.fleet = UtilHelper.getBoolean((String) map.get("hercFleet"));
*/
		else if (key.equalsIgnoreCase("hercRelease"))
			this.release = UtilHelper.getBoolean(value);
		else if (key.equalsIgnoreCase("hercExtend"))
			this.extend = UtilHelper.getBoolean(value);
		else if (key.equalsIgnoreCase("hercFleet"))
			this.fleet = UtilHelper.getBoolean(value);

/*
		this.updateDate = new HercDate(HercDate.parseLDAPDate((String) map.get("hertzLastModifyDate")));
		this.approver = UtilHelper.getString(map,"hercApprover");
		this.optIn = UtilHelper.getString(map, "hercOptIn");
*/
			else if (key.equalsIgnoreCase("hertzLastModifyDate"))
				this.updateDate = new HercDate(HercDate.parseLDAPDate(value));
			else if (key.equalsIgnoreCase("hercApprover"))
				this.fax = approver;
			else if (key.equalsIgnoreCase("hercOptIn"))
				this.fax = optIn;
	}

/*
 * isComplete() is called from LoginHandler and LoginChangePasswordHandler.
 * In both scenarios, the user is already logged in. 
 * So, isComplete() does NOT check if the profile has password present. 
 */
	public boolean isComplete() {
		if (isNotSet(getFirstName()) || isNotSet(getLastName()) || 
				isNotSet(getBusinessName()) || 
				isNotSet(getAddress1()) || isNotSet(getCity()) || 
				isNotSet(getCountryState()) || isNotSet(getPostalCode()) || isNotSet(getEmail()) || 
				isNotSet(getPhone())) return false;
		return true;
	}

	/**
	 * Determine whether this member is awaiting approval
	 * 
	 * @return	true if member is awaiting approval
	 */
	public boolean isAwaitingApproval() {
		if (isNotSet(getActive())) return false;
		if (isNotSet(getApproval())) return false;
		if (! getActive().equals(ACTIVE_FLAG_DISABLED)) return false;
		if (! getApproval().equals(APPROVAL_FLAG_PENDING)) return false;
		return true;
	}

/*
 * various getters
 */
	public String getUsername() {return username == null ? "":username.toLowerCase();}
	//Delete this in 12.02
	public String getUsernameWithCase() {return username;}
	public String getActive() {return active;}
	public boolean isActive() {
		return (getActive().equals(ACTIVE_FLAG_ACTIVE)) ? true : false;
	}
	public boolean isDisabled() {
		return (getActive().equals(ACTIVE_FLAG_DISABLED)) ? true : false;
	}
	public boolean isLocked() {
		return (getActive().equals(ACTIVE_FLAG_LOCKED)) ? true : false;
	}
	public String getApproval() {return approval;}
	
	public boolean isApproved() {
		return (getApproval().equals(APPROVAL_FLAG_APPROVED)) ? true : false;
	}
//For AT 1.6.2
	public String getActiveString() {
		if (this.isDisabled()) return "Disabled";
		return "Active";
	}
	//Used for AT 1.6.2
	public String getApprovalString() {
		if (this.isPending())
			return "Pending";
		else if (this.isDenied())
			return "Denied";
		else if (this.isInvited())
			return "Invited";
		else if (this.isMigrated())
			return "Migrated";
		return "Approved";
	}
	// For users report
		public String getReportsActiveString() {
			if (this.isLocked()) return "Locked";
			if (this.isDisabled()) return "Disabled";
			return "Active";
		}
	// for users report
	public String getReportsApprovalString() {
		if (this.isPending())
			return "Pending";
		else if (this.isDenied())
			return "Denied";
		else if (this.isInvited())
			return "Invited";
		else if (this.isMigrated())
			return "Migrated";
		return "Approved";
	}
	//Used for AT1.6.1
	public boolean isPending() {
		return (getApproval().equals(APPROVAL_FLAG_PENDING)) ? true : false;
	}
	//Used for AT1.6.1
	public boolean isDenied() {
		return (getApproval().equals(APPROVAL_FLAG_DENIED)) ? true : false;
	}
	// Used for L1.6
	public boolean isMigrated() {
		return (getApproval().equals(APPROVAL_FLAG_MIGRATED)) ? true : false;
	}
	
	public boolean isInvited() {
		if (isNotSet(getActive())) return false;
		if (isNotSet(getApproval())) return false;
		if (!getActive().equals(ACTIVE_FLAG_DISABLED)) return false;
		if (! getApproval().equals(APPROVAL_FLAG_INVITED)) return false;
		return true;
	}
	public MemberRole getRole() {return role;}
	public MemberReports getReports() {return reports;}
	public String getPassword() {return password;}
	public String getQuestion() {return question;}
	public String getAnswer() {return answer;}
	public boolean isTemporaryPassword() {return temporaryPassword;}
	public int getFailureCounter() {return failureCounter;}

	public String getDialect() {return dialect;}
	public String getPrefix() {return prefix;}
	public String getFirstName() {return firstName;}
	public String getMiddleName() {return middleName;}
	public String getMiddleInitial() {
		if (middleName != null && middleName.length() > 0) return getMiddleName().substring(0,1);
		return "";
	}
	public String getLastName() {return lastName;}
	public String getSuffix() {return suffix;}
	public String getTitle() {return title;}
	public String getBusinessName() {return businessName;}
	public String getAddress1() {return address1;}
	public String getAddress2() {return address2;}
	public String getCity() {return city;}
	public String getCountryState() {return countryState;}
	public String getCountry() {
		if (countryState != null && countryState.length() >= 2)
			return countryState.substring(0, 2);
		return "";
	}
	public String getPostalCode() {return postalCode;}
	public String getEmail() {return email;}
	public String getPhone() {return phone;}
	public String getFax() {return fax;}
	public String getPoNumber() {return poNumber;}
	public String getPcardType() {return pcardType;}
	public String getPcardNumber() {return pcardNumber;}
	public String getPcardMonth() {return pcardMonth;}
	public String getPcardYear() {return pcardYear;}
	public String getSecurityCode() {return securityCode;}

	public boolean isRelease() {return release;}
	public boolean isExtend() {return extend;}
	public boolean isReleaseExtendCapable() {
		if (release) return true;
		if (extend) return true;
		return false;
	}
	public boolean isFleet() {return fleet;}

	public HercDate getUpdateDate() {return updateDate;}
	public String getApprover() {return approver;}
	public String getRoleString() {
		MemberRole role = this.getRole();
		if (role.isAccountAdminRole())
			return "Admin";
		if (role.isAccountApproverRole())
			return "Approver";
		return "User";
	}
	
	public String getOptIn() {return optIn;}
	//If OptIn is is null or empty String, it is not set
	//If OptIn is any value other than true or false, it is not set
	public boolean isOptInSet() {		
		if (optIn == null || "".equals(optIn)) return false;
		if ( ! ("true".equalsIgnoreCase(optIn)) && ! ("false".equalsIgnoreCase(optIn))) return false;
		return true;
	}

/*
 * various setters
 */
	public void setUsername(String username) {this.username = username == null ? "":username.toLowerCase();}
	public void setActive() {active = ACTIVE_FLAG_ACTIVE;}
	public void setDisabled() {active = ACTIVE_FLAG_DISABLED;}
	public void setLocked() {active = ACTIVE_FLAG_LOCKED;}
	public void setApproved() {approval = APPROVAL_FLAG_APPROVED;}
	public void setPending() {approval = APPROVAL_FLAG_PENDING;}
	public void setDenied() {approval = APPROVAL_FLAG_DENIED;}
	public void setRole (MemberRole r) {role = r;}
	public void setReports (MemberReports r) {reports = r;}
	public void setTemporaryPassword(String password) {
		this.password = password;
		temporaryPassword = true;
	}
	public void setPermanentPassword(String password) {
		this.password = password;
		temporaryPassword = false;
	}
	public void setQuestion(String question) {this.question = question;}
	public void setAnswer(String answer) {this.answer = answer;}
	public void incrementFailureCounter() {
		failureCounter++;
		if (failureCounter > 3) setLocked();
	}
	public void resetFailureCounter() {failureCounter = 0;}

	public void setDialect(String dialect) {this.dialect = dialect;}
	public void setPrefix(String prefix) {this.prefix = prefix;}
	public void setFirstName(String firstName) {this.firstName = firstName;}
	public void setMiddleName(String middleName) {this.middleName = middleName;}
	public void setLastName(String lastName) {this.lastName = lastName;}
	public void setSuffix(String suffix) {this.suffix = suffix;}
	public void setTitle(String title) {this.title = title;}
	public void setBusinessName(String businessName) {this.businessName = businessName;}
	public void setAddress1(String address1) {this.address1 = address1;}
	public void setAddress2(String address2) {this.address2 = address2;}
	public void setCity(String city) {this.city = city;}
	public void setCountryState(String countryState) {this.countryState = countryState;}
	public void setPostalCode(String postalCode) {this.postalCode = postalCode;}
	public void setEmail(String email) {this.email = email;}
	public void setPhone(String phone) {this.phone = phone;}
	public void setFax(String fax) {this.fax = fax;}
	public void setPoNumber(String poNumber) {this.poNumber = poNumber;}
	public void setPcardType(String pcardType) {this.pcardType = pcardType;}
	public void setPcardNumber(String pcardNumber) { 
		if (pcardNumber == null)
			 this.pcardNumber = "";
		else
			this.pcardNumber = pcardNumber;
	}
	public void setPcardMonth(String pcardMonth) {this.pcardMonth = pcardMonth;}
	public void setPcardYear(String pcardYear) {this.pcardYear = pcardYear;}
	public void setSecurityCode(String securityCode) {this.securityCode = securityCode;}
	
	public void setRelease(boolean bool) {this.release = bool;}
	public void setExtend(boolean bool) {this.extend = bool;}
	public void setFleet(boolean bool) {this.fleet = bool;}
    public void setUpdateDate(Date date) {this.updateDate = new HercDate(date);}
    public void setApprover(String approver) {this.approver = approver;}
	public void setOptIn(String optIn) {this.optIn = optIn;}

/*
 * various helper methods
 */
	private boolean isNotSet (String str) {
		if (str == null || str.length() < 1) return true;
		return false;
	}
	
	/**
	 * Active		Approval		User Status
	 *   A				A			Active
	 * 	 A				M			Migrated
	 *   L				* 			Locked
	 *   *				D			Denied
	 *   D				P			Pending
	 * 	 D				I			Invited
	 *   D				*			Disabled			
	 * @param profileItem
	 * @return
	 */
	public String getStatus() {
		if (this.isMigrated() || this.isInvited() || this.isPending() || this.isDenied())
			return this.getApprovalString();
		return this.getActiveString();
	}

	public String toString() {
		return "("+getUsernameWithCase()+","+
				getActive()+","+getApproval()+","+getDialect()+","+getRole()+","+getReports()+","+
				getPassword()+","+getQuestion()+","+getAnswer()+
				","+isTemporaryPassword()+","+getFailureCounter()+
				","+getPrefix()+","+
				getFirstName()+","+getMiddleName()+","+getLastName()+","+getSuffix()+","+
				getBusinessName()+","+
				getAddress1()+","+getAddress2()+","+getCity()+","+getCountryState()+","+getPostalCode()+","+
				getEmail()+","+getPhone()+","+getFax()+","+
				getPoNumber()+","+getPcardType()+","+
				getPcardNumber()+","+getPcardMonth()+","+getPcardYear()+","+getSecurityCode()+","+
				isRelease()+","+isExtend()+","+isFleet()+","+getUpdateDate().getDateString()+","+
				getApprover()+","+getOptIn()+")";
	}
}
