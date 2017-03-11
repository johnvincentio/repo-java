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

package com.hertz.hercutil.member.data;

import java.io.Serializable;
import java.util.Map;

import com.hertz.hercutil.presentation.UtilHelper;

/**
* @author John Vincent
*
 */

public class MemberDataAccountItemInfo implements Serializable {
	private static final long serialVersionUID = -3508359957547298481L;
	public static final String APPROVAL_FLAG_APPROVED = "A";
	public static final String APPROVAL_FLAG_PENDING = "P";
	public static final String APPROVAL_FLAG_DELETED = "D";

	private long memberid;
	private long accountid;
	private String approved;
	private long approverid;
	public MemberDataAccountItemInfo (Map<String, Object> hashMap) {
		this.memberid = ((Long) hashMap.get("memberid")).longValue();
		this.accountid = ((Long) hashMap.get("accountid")).longValue();
		this.approved = (String) hashMap.get("approved");
		this.approverid = ((Long) hashMap.get("approverid")).longValue();
	}

	public MemberDataAccountItemInfo (long memberid, long accountid, String approved, long approverid) {
		this.memberid = memberid;
		this.accountid = accountid;
		this.approved = approved;
		this.approverid = approverid;
	}
	public MemberDataAccountItemInfo (long memberid, long accountid, long approverid) {
		this (memberid, accountid, APPROVAL_FLAG_APPROVED, approverid);
	}

	public long getMemberid() {return memberid;}
	public long getAccountid() {return accountid;}
	public String getApproved() {return approved;}
	public long getApproverid() {return approverid;}
	public boolean isApproved() {
		if (isNotSet(getApproved())) return false;
		if (getApproved().equals(APPROVAL_FLAG_APPROVED)) return true;
		return false;	
	}
	public boolean isAwaitingApproval() {
		if (isNotSet(getApproved())) return false;
		if (! getApproved().equals(APPROVAL_FLAG_PENDING)) return false;
		return true;
	}
	public boolean isDeleted(){
		if(getApproved().equals(APPROVAL_FLAG_DELETED)) return true;
		return false;
	}
	public void setApproved() {approved = APPROVAL_FLAG_APPROVED;}
	public void setDeleted() {approved = APPROVAL_FLAG_DELETED;}
	public void setPending() {approved = APPROVAL_FLAG_PENDING;}
	public void setApproverid (long approverid) {this.approverid = approverid;}

	private boolean isNotSet (String str) {
		if (str == null || str.length() < 1) return true;
		return false;
	}
	public boolean isSame (MemberDataAccountItemInfo item) {
		if (getMemberid() != item.getMemberid()) return false;
		if (getAccountid() != item.getAccountid()) return false;
		if (getApproverid() != item.getApproverid()) return false;
		if (! UtilHelper.isEquals (getApproved(), item.getApproved())) return false;
		return true;
	}
	public String toString() {
		return "("+getMemberid()+","+getAccountid()+","+getApproved()+","+getApproverid()+")";
	}
}
