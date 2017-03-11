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

//import com.hertz.irac.util.logging.LogBroker;

/**
 *	Describe member profile role information.
 *
 * Account Administrator - Complete Account Authority
 *	Can Create/Modify/Delete Members at All Levels;
 *	Can Approve/Deny Members at All Levels; 
 *	Can Approve Rental Requests of Account Approvers & Account Users (if needed); 
 *	Can Make Rental Requests of Their Own; 
 *	Can view/search the specific reports they have access to if report is selected below.
 *
 * Account Approver
 * 	Can Create/Modify/Delete Members at Account User Level and below; 
 * 	Can Approve/Deny Account User Level and below; 
 * 	Approve Rental Requests of Account Users & Can Make Rental Requests of Their Own; 
 * 	Can view/search the specific reports they have access to if report is selected below.
 * 
 * Account User
 * 	Can Make Rental Requests, Approves own requests;
 * 	Can view/search the specific reports they have access to if selected below.
 * 
 * Account Requestor
 *  Can Make Rental Requests;
 * 	Can view/search the specific reports they have access to if selected below.
 * 
 * LDAP char(20), format is:
 * 	YNNNNNNNNNNNNNNNNNNN Account Admin
 * 	NYNNNNNNNNNNNNNNNNNN Account Approver
 * 	NNYNNNNNNNNNNNNNNNNN Account User
 * 	NNNYNNNNNNNNNNNNNNNN Account Requestor
 * 
 * @author John Vincent
 */

public class MemberRole implements Serializable {

	private static final long serialVersionUID = -3882267244038396948L;
	private boolean accountAdminRole = false;
	private boolean accountApproverRole = false;
	private boolean accountUserRole = false;
	private boolean accountRequestorRole = false;

	/**
	 * Constructor to create a MemberRole with everything disabled
	 */
	public MemberRole() {}

	/**
	 * Constructor to create a MemberRole from data in the datastore
	 * 
	 * @param data		Member Role data from LDAP
	 */
	public MemberRole (String data) {		// from LDAP
//		LogBroker.debug(this,"MemberRole; flags :"+data+":");
		String flags = data.toUpperCase() + "NNNNNNNNNNNNNNNNNNNN";
		if (flags.substring(0,1).equals("Y"))
			setAccountAdminRole();
		else if (flags.substring(1,2).equals("Y"))
			setAccountApproverRole();
		else if (flags.substring(2,3).equals("Y"))
			setAccountUserRole();
		else if (flags.substring(3,4).equals("Y"))
			setAccountRequestorRole();
//		LogBroker.debug(this,"roleType :"+toString());
	}

	/**
	 * Constructors to create a MemberRole object from booleans which will come from a
	 * formInfo object. Makes creating a MemberRole object from the presentation layer fairly simple.
	 * 
	 * @param roleType		Role Type
	 * @param requestor		true = if requestor
	 */
	public MemberRole (String roleType, boolean requestor) {
		if (roleType.equals("1"))
			setAccountAdminRole();
		else if (roleType.equals("2"))
			setAccountApproverRole();
		else if (roleType.equals("3")) {
			if (requestor)
				setAccountRequestorRole();
			else
				setAccountUserRole();
		}
	}

	/**
	 * Constructor used in M122, update member.
	 * Create a MemberRole object from booleans which will come from a formInfo object.
	 * Makes creating a MemberRole object from the presentation layer fairly simple.
	 * The resulting role object will be contructed from the form values, with changes made for the following condition:
	 * 		If MemberRole object is not valid, then revert to the previous role object.
	 * 
	 * @param roleType		role; 1 = Account admin, 2 = Account approver, 3 = Account user
	 * @param requestor		true = account user requires approval
	 * @param approverRole	ApproverRole is the role object of the approver
	 * @param previousRole	PreviousRole is the current role of the member whose role is to be changed
	 */
	public MemberRole (String roleType, boolean requestor, MemberRole approverRole, MemberRole previousRole) {
		this (roleType, requestor);
		boolean bError = false;
		if (isAccountAdminRole()) {
			if (! approverRole.isAccountAdminRole()) bError = true;
		}
		else if (isAccountApproverRole()) {
			if ((! approverRole.isAccountAdminRole()) && (! approverRole.isAccountApproverRole())) bError = true;
		}
		else if (isAccountUserRole()) {
			if ((! approverRole.isAccountAdminRole()) && (! approverRole.isAccountApproverRole())
					&& (! approverRole.isAccountUserRole())) bError = true;
		}
		else if (isAccountRequestorRole()) {
			if ((! approverRole.isAccountAdminRole()) && (! approverRole.isAccountApproverRole())
					&& (! approverRole.isAccountUserRole()) && (! approverRole.isAccountRequestorRole())) bError = true;
		}
		else
			bError = true;
		
		if (bError) {
			accountAdminRole = previousRole.accountAdminRole;
			accountApproverRole = previousRole.accountApproverRole;
			accountUserRole = previousRole.accountUserRole;
			accountRequestorRole = previousRole.accountRequestorRole;
		}
	}

	/**
	 * Determine whether this role is an Account Admin
	 * 
	 * @return		true if role is an Account Admin
	 */
	public boolean isAccountAdminRole() {return accountAdminRole;}

	/**
	 * Determine whether this role is an Account Approver
	 * 
	 * @return		true if role is an Account Approver
	 */
	public boolean isAccountApproverRole() {return accountApproverRole;}

	/**
	 * Determine whether this role is an Account User
	 * 
	 * @return		true if role is an Account User
	 */
	public boolean isAccountUserRole() {return accountUserRole;}

	/**
	 * Determine whether this role is an Account Requestor
	 * 
	 * @return		true if role is an Account Requestor
	 */
	public boolean isAccountRequestorRole() {return accountRequestorRole;}

	/**
	 * Determine whether this role can approve own rental requests
	 * 
	 * @return		true if can approve own rental requests
	 */
	public boolean isApproverOfOwnRentalRequests() {
		if (isAccountAdminRole()) return true;
		if (isAccountApproverRole()) return true;
		if (isAccountUserRole()) return true;
		return false;
	}

	/**
	 * Determine whether this role can approve account access requests
	 * 
	 * @return		true if can approve account access requests
	 */
	public boolean isApproverOfAccounts() {
		if (isAccountAdminRole()) return true;
		if (isAccountApproverRole()) return true;
		return false;
	}

	/**
	 * Set Role to Account Admin
	 *
	 */
	private void setAccountAdminRole() {
//		LogBroker.debug(this,"admin");
		accountAdminRole = true;
	}

	/**
	 * Set Role to Account Approver
	 *
	 */
	private void setAccountApproverRole() {
//		LogBroker.debug(this,"approver");
		accountApproverRole = true;
	}

	/**
	 * Set Role to Account User
	 *
	 */
	private void setAccountUserRole() {
//		LogBroker.debug(this,"user");
		accountUserRole = true;
	}

	/**
	 * Set Role to Account Requestor
	 *
	 */
	private void setAccountRequestorRole() {
//		LogBroker.debug(this,"requestor");
		accountRequestorRole = true;
	}

	/**
	 * Return a String representation of the MemberRole object which will be stored in
	 * the datastore.
	 * 
	 * @return			Datastore string representation of the MemberRole object
	 */
	public String toDatabase() {//           12345678901234567890
		StringBuffer buf = new StringBuffer("NNNNNNNNNNNNNNNNNNNN");
		if (isAccountAdminRole())
			buf.setCharAt(0,'Y');
		else if (isAccountApproverRole())
			buf.setCharAt(1,'Y');
		else if (isAccountUserRole())
			buf.setCharAt(2,'Y');
		else if (isAccountRequestorRole())
			buf.setCharAt(3,'Y');
		return buf.toString();
	}

	/**
	 * Determine whether this role has application access to the role that has been passed as the parameter
	 * 
	 * @param role		Member role to check
	 * @return			true if this role has application access to the role that has been passed as the parameter
	 */
	public boolean hasAccess (MemberRole role) {
		if (isAccountAdminRole())
			return true;
		else if (isAccountApproverRole()) {
			if (role.isAccountAdminRole() || role.isAccountApproverRole())
				return false;
			return true;
		}
		else if (isAccountUserRole()) {
			if (role.isAccountAdminRole() || role.isAccountApproverRole()
					|| role.isAccountUserRole()) return false;
			return true;
		}
		else if (isAccountRequestorRole()) {
			if (role.isAccountAdminRole() || role.isAccountApproverRole()
					|| role.isAccountUserRole() || role.isAccountRequestorRole()) return false;
			return true;
		}
		return false;
	}

	/**
	 * Construct and return the MemberRoleOptions object representation of this members roles
	 * 
	 * @return			MemberRoleOptions object
	 */
	public MemberRoleOptions getRoleOptions() {
		MemberRoleOptions memberRoleOptions = new MemberRoleOptions();
		if (isAccountAdminRole()) {
			memberRoleOptions.setActiveAdmin(true);
			memberRoleOptions.setShowAdmin(true);
			memberRoleOptions.setShowApprover(true);
			memberRoleOptions.setShowUser(true);
			memberRoleOptions.setShowRequestor(true);
		}
		else if (isAccountApproverRole()) {
			memberRoleOptions.setActiveApprover(true);
			memberRoleOptions.setShowApprover(true);
			memberRoleOptions.setShowUser(true);
			memberRoleOptions.setShowRequestor(true);			
		}
		else if (isAccountUserRole()) {
			memberRoleOptions.setActiveUser(true);
			memberRoleOptions.setShowUser(true);
			memberRoleOptions.setShowRequestor(true);
		}
		else if (isAccountRequestorRole()) {
			memberRoleOptions.setActiveRequestor(true);
			memberRoleOptions.setShowRequestor(true);
		}
//		LogBroker.debug(this, "memberRoleOptions "+memberRoleOptions.toString());
		return memberRoleOptions;
	}

	/**
	 * Construct and return the MemberRoleOptions object representation of this members roles in
	 * relation to the MemberRoleOptions object of another members MemberRole
	 * 
	 * @param memberRole	MemberRole of a member
	 * @return				MemberRoleOptions object
	 */
	public MemberRoleOptions getRoleOptions (MemberRole memberRole) {
		MemberRoleOptions memberRoleOptions = new MemberRoleOptions();
		if (isAccountAdminRole()) {
			memberRoleOptions.setShowAdmin(true);
			memberRoleOptions.setShowApprover(true);
			memberRoleOptions.setShowUser(true);
			memberRoleOptions.setShowRequestor(true);
		}
		else if (isAccountApproverRole()) {
			memberRoleOptions.setShowApprover(true);
			memberRoleOptions.setShowUser(true);
			memberRoleOptions.setShowRequestor(true);			
		}
		else if (isAccountUserRole()) {
			memberRoleOptions.setShowUser(true);
			memberRoleOptions.setShowRequestor(true);
		}
		else if (isAccountRequestorRole()) {
			memberRoleOptions.setShowRequestor(true);
		}
		memberRoleOptions.setActiveAdmin(memberRole.isAccountAdminRole());
		memberRoleOptions.setActiveApprover(memberRole.isAccountApproverRole());
		memberRoleOptions.setActiveUser(memberRole.isAccountUserRole());
		memberRoleOptions.setActiveRequestor(memberRole.isAccountRequestorRole());
//		LogBroker.debug(this, "memberRoleOptions "+memberRoleOptions.toString());
		return memberRoleOptions;
	}

	/**
	 * Return String of this object
	 * 
	 * @return  String of this object
	 */
	public String toString() {
		return "("+isAccountAdminRole()+","+isAccountApproverRole()+","+isAccountUserRole()+
					","+isAccountRequestorRole()+")";
	}
}
