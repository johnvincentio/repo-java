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

/**
 * @author John Vincent
 *
 */

import java.io.Serializable;
import java.util.Map;

import com.hertz.hercutil.presentation.UtilHelper;

public class MemberDataItemInfo implements Serializable {
	private static final long serialVersionUID = -3370181435937587100L;
	private long memberid;
	private String username;
	private long companyid;
	private long reportsAccountid;
	private String defaultlocation;
	public MemberDataItemInfo (Map<String, Object> hashMap) { 
		this.memberid = ((Long) hashMap.get("memberid")).longValue();
		this.username = (String) hashMap.get("username");
		this.companyid = ((Long) hashMap.get("companyid")).longValue();
		this.reportsAccountid = ((Long) hashMap.get("reports_accountid")) == null ? 0L : ((Long) hashMap.get("reports_accountid")).longValue();
		this.defaultlocation = (String) hashMap.get("defaultlocation");
	}

	public MemberDataItemInfo (long memberid, String username, long companyid, long reportsAccountid) { 
		this.memberid = memberid;
		this.username = username;
		this.companyid = companyid;
		this.reportsAccountid = reportsAccountid;
		this.defaultlocation = "";
	}

	public MemberDataItemInfo (long memberid, String username, long companyid) { 
		this.memberid = memberid;
		this.username = username;
		this.companyid = companyid;
		this.defaultlocation = "";
	}

	public long getMemberid() {return memberid;}
	public String getUsername() {return username;}
	public long getCompanyid() {return companyid;}
	public String getDefaultlocation() {return defaultlocation;}
	public long getReportsAccountid() {return reportsAccountid;}

	public boolean isSame (MemberDataItemInfo item) {
		if (getMemberid() != item.getMemberid()) return false;
		if (! UtilHelper.isEquals (getUsername(), item.getUsername())) return false;
		if (getCompanyid() != item.getCompanyid()) return false;
		if (getReportsAccountid() != item.getReportsAccountid()) return false;
		if (! UtilHelper.isEquals (getDefaultlocation(), item.getDefaultlocation())) return false;
		return true;
	}

	public String toString() {
		return "("+getMemberid()+","+getUsername()+","+
					getCompanyid()+","+getReportsAccountid()+", "+getDefaultlocation()+")";
	}
}
