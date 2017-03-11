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

/**
 * @author John Vincent
 *
 */

public class MemberRoleOptions implements Serializable {

	private static final long serialVersionUID = 3696342598830702194L;
	private boolean activeAdmin;
	private boolean activeApprover;
	private boolean activeUser;
	private boolean activeRequestor;

	private boolean showAdmin;
	private boolean showApprover;
	private boolean showUser;
	private boolean showRequestor;

	public boolean isActiveAdmin() {return activeAdmin;}
	public boolean isActiveApprover() {return activeApprover;}
	public boolean isActiveUser() {return activeUser;}
	public boolean isActiveRequestor() {return activeRequestor;}

	public boolean isShowAdmin() {return showAdmin;}
	public boolean isShowApprover() {return showApprover;}
	public boolean isShowUser() {return showUser;}
	public boolean isShowRequestor() {return showRequestor;}

	public String getRoleType() {
		if (isActiveAdmin()) return "1";
		if (isActiveApprover()) return "2";
		return "3";
	}
	public String setRoleTypeUserNeedsApproval() {
		if (isActiveRequestor()) return "Y";
		return "";
	}
	public void setActiveAdmin (boolean activeAdmin) {this.activeAdmin = activeAdmin;}
	public void setActiveApprover (boolean activeApprover) {this.activeApprover = activeApprover;}
	public void setActiveUser (boolean activeUser) {this.activeUser = activeUser;}
	public void setActiveRequestor (boolean activeRequestor) {this.activeRequestor = activeRequestor;}

	public void setShowAdmin (boolean showAdmin) {this.showAdmin = showAdmin;}
	public void setShowApprover (boolean showApprover) {this.showApprover = showApprover;}
	public void setShowUser (boolean showUser) {this.showUser = showUser;}
	public void setShowRequestor (boolean showRequestor) {this.showRequestor = showRequestor;}

	public String toString() {
		return "("+isActiveAdmin()+","+isActiveApprover()+","+isActiveUser()+","+isActiveRequestor()+","+
			isShowAdmin()+","+isShowApprover()+","+isShowUser()+","+isShowRequestor()+")";
	}
}
