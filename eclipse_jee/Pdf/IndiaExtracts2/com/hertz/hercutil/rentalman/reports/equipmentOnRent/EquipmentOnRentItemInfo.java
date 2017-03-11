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

package com.hertz.hercutil.rentalman.reports.equipmentOnRent;

import java.io.Serializable;

import com.hertz.hercutil.company.RMAccountType;
import com.hertz.hercutil.framework.DateHelper;

/**
 * Class to encapsulate the Equipment On Rent Record
 * 
 * @author John Vincent
 */

public class EquipmentOnRentItemInfo implements Serializable {

	private RMAccountType m_RMAccountType;
	private String m_sContract;
	private String m_sStartDate;
	private String m_sEstimatedReturnDate;
	private String m_sItem;
	private String m_sQuantity;
	private String m_sDescription;
	private String m_sPickupTicket;
	private String m_sOrderedBy;
	private String m_sPurchaseOrder;
	private boolean m_bOverdueContract = false;
	private String m_sJobName;
	private String m_sDayRate;
	private String m_sWeekRate;
	private String m_sMonthRate;
	private String m_sTotalBilled;
	private String m_sTotalAccrued;
	private String m_sTotalEstimatedCost;

	private String m_sDetailSequence;
	private int m_nCategory;
	private int m_nClassification;

	public EquipmentOnRentItemInfo (RMAccountType rmAccountType, int todayDate, 
			EquipmentOnRentPickupInfo equipmentOnRentPickupInfo,
			PendingTransactionsInfo pendingTransactionsInfo,
			String rdcon, String rhdato, String rherdt, String rditem, String rdqty, String ecdesc, String rdseq, 
			String rhordb, String rhpo, String cjname, String rddyrt, String rdwkrt, String rdmort, String rnbill, 
			String rnaccr, String rnrcst, String rdcatg, String rdclas) {

		m_RMAccountType = rmAccountType;

		m_sContract = rdcon;

		this.m_sStartDate = DateHelper.formatEtrieveDate(rhdato);

		this.m_sEstimatedReturnDate = DateHelper.formatEtrieveDate(rherdt);

		m_sItem = rditem;
		m_sQuantity = rdqty;
		m_sDescription = ecdesc;

		m_sDetailSequence = rdseq;

		m_sPickupTicket = "No";
		String str = equipmentOnRentPickupInfo.getTicket (m_sContract, m_sDetailSequence, m_sItem);
		if (str != null) m_sPickupTicket = "Tkt: " + str;
		if (pendingTransactionsInfo.isRelease (m_sContract, m_sDetailSequence)) 
			m_sPickupTicket = "Pending";

		m_sOrderedBy = rhordb;
		m_sPurchaseOrder = rhpo;

		m_sJobName = cjname;

		m_sDayRate = rddyrt;
		m_sWeekRate = rdwkrt;
		m_sMonthRate = rdmort;
		
		m_sTotalBilled = rnbill;
		m_sTotalAccrued = rnaccr;
		m_sTotalEstimatedCost = rnrcst;

		m_nCategory = Integer.valueOf(rdcatg).intValue();
		m_nClassification = Integer.valueOf(rdclas).intValue();
		
		PendingExtendItemInfo pendingExtendItemInfo = pendingTransactionsInfo.getExtend(m_sContract);
		if (pendingExtendItemInfo != null && pendingExtendItemInfo.isPending()) {
			String strDate = pendingExtendItemInfo.getEstimatedReturnDate();
			if (! strDate.equals("0") && strDate.length() == 8) {
				int estimatedReturn = Integer.parseInt(strDate);
				if (estimatedReturn < todayDate)
					m_bOverdueContract = true;
				m_sEstimatedReturnDate = strDate.substring(4, 6) + "/" + strDate.substring(6, 8) + "/" + strDate.substring(2, 4);
			}
		}
	}

	public void setItemComments (String itemComments) {
		if (itemComments == null) return;
		if (itemComments.trim().length() < 1) return;
		m_sDescription += " *** COMMENT: " + itemComments.trim();
	}

	public boolean isReRentItem() {
		if (m_RMAccountType.getCountryCode() == 1 && m_nCategory == 975 && m_nClassification == 1) return true;
		return false;
	}

	public RMAccountType getRmAccountType() {return m_RMAccountType;}
	public String getContract() {return m_sContract;}
	public String getStartDate() {return m_sStartDate;}
	public String getEstimatedReturnDate() {return m_sEstimatedReturnDate;}
	public String getItem() {return m_sItem;}
	public String getQuantity() {return m_sQuantity;}
	public String getDescription() {return m_sDescription;}
	public String getPickupTicket() {return m_sPickupTicket;}
	public String getOrderedBy() {return m_sOrderedBy;}
	public String getPurchaseOrder() {return m_sPurchaseOrder;}
	public boolean isOverdueContract() {return m_bOverdueContract;}
	public String getJobName() {return m_sJobName;}
	public String getDayRate() {return m_sDayRate;}
	public String getWeekRate() {return m_sWeekRate;}
	public String getMonthRate() {return m_sMonthRate;}
	public String getTotalBilled() {return m_sTotalBilled;}
	public String getTotalAccrued() {return m_sTotalAccrued;}
	public String getTotalEstimatedCost() {return m_sTotalEstimatedCost;}
	public String getDetailSequence() {return m_sDetailSequence;}
	public int getCategory() {return m_nCategory;}
	public int getClassification() {return m_nCategory;}

	public String toString() {
		return "("+getRmAccountType()+","+getContract()+","+getStartDate()+","+getEstimatedReturnDate()+","+
			getItem()+","+getQuantity()+","+getDescription()+","+getPickupTicket()+","+getOrderedBy()+","+
			getPurchaseOrder()+","+isOverdueContract()+","+getJobName()+","+
			getDayRate()+","+getWeekRate()+","+getMonthRate()+","+
			getTotalBilled()+","+getTotalAccrued()+","+getTotalEstimatedCost()+","+
			getDetailSequence()+","+getCategory()+","+getClassification()+")";
	}
}


/*
read the trans bean.
only keep if THSTAT == 'P'
for extends EXT then override est return date with THERDT from extend transaction.
if THERDT > current date, then set overdue = true;

releases REL

if a trans records exists, it is Pending. (this contract and sequence).
thus, do not need to get quantity.


	// ************************************************************************
	//  RI018 - Check if this equipment qualifies for online release or extend.
	//  (1) USA - Determine if releases are pending
	//  (2) Canada - Determine if a release has already been done for equip in this session  
	// ************************************************************************
						
	boolean allowReleases = true;
						
	if( prevReleases && str_pickup.equals("No") && equipChangeAuth.equals("Yes") )	// RI018
	{	
		String str_qty    = equiponrentbean.getColumn("RDQTY").trim();				// RI018
												
		int num_qty = 0;								// RI018
							
		if (str_qty == null) str_qty = "0";				// RI018
							
		int i = str_qty.indexOf(".");					// RI018
								
		if (i > 0)	str_qty = str_qty.substring(0,i);	// RI018
									
		num_qty = Integer.valueOf(str_qty).intValue();	// RI018

		int lstAllRelQty = 0;							// RI018
		int size = EqpRelContracts.size();				// RI018
							
		for	(i=0; i < size; i++)						// RI018
		{
			String lstCon     = (String) EqpRelContracts.get(i);				// RI018
			String lstLineNum = (String) EqpRelLineNum.get(i);					// RI018
			String lstQty     = (String) EqpRelQtyRel.get(i);					// RI018
										
			if (lstCon.trim().equals(str_contract) && lstLineNum.trim().equals(str_dseq) )		// RI018
				lstAllRelQty = lstAllRelQty + Integer.valueOf(lstQty).intValue() ;	// RI018
		}	
								
		if(lstAllRelQty >= num_qty ) {	// RI018 RI024
			str_pickup = "Yes";			// RI018			
			if ( str_company.equals("HG") )		// RI024
				str_pickup = "Pending";			// RI024
		} // RI024
	}

	if ( str_allowReleases.equals("N") || str_pickup.equals("Yes") )	// RI024
		allowReleases = false;			// RI024

							
							
	// ************************************************************************
	//  RI018 - Check if this contract qualifies for online extension.  This 
	//  section will determine if an earlier extension has been done for the
	//  contract in the same session.       
	// ************************************************************************
						
	boolean allowExtension = true;	// RI018
	int svInd = -1 ; //RI025
						
	if( prevExtend )				// RI018
	{
						
		int size = EqpExtContracts.size();		// RI018
		
		for	( int i=0; i<size ; i++ )			// RI018
		{					
			String lstCon = (String)EqpExtContracts.get(i);		// RI018
								
			if (lstCon.trim().equals(str_contract.trim()) )		// RI018
			{
				svInd = i ;				// RI025
				allowExtension = false;	// RI018
				break;					// RI018				
			}
		}
								
	}

	if ( str_allowExtend.equals("N") )	// RI025
		allowExtension = false;			// RI025
							
	//*****************************************************************
	// RI025 Start
	// If a contract has a Pending Extension Request, display new
	// estimated return date.
	//*****************************************************************

	String str_pendingExt = "";	// bob1
						
	if (svInd != -1)
	{
		String conStatus = (String)EqpExtConStatus.get(svInd);

		if (conStatus.equals("P") || conStatus.equals("p"))
		{	
			String Save_estRetDate = str_estRetDate;
								
			str_estRetDate = (String)EqpExtConEstDate.get(svInd);
			allowReleases = false;
			allowExtension = false;
								
			if ( !str_estRetDate.trim().equals("0") && str_estRetDate.length() == 8) 
			{
				int estimatedReturn = Integer.parseInt(str_estRetDate);

				if ( estimatedReturn < todayDate )
					overdueContract = true;

				str_estRetDate = str_estRetDate.substring(4, 6) + "/" + str_estRetDate.substring(6, 8) + "/" + str_estRetDate.substring(2, 4);
			} 
								
			str_pendingExt = "Pending";		// bob1
			str_estRetDate = Save_estRetDate;
		}
	}
	// RI025 End
						
						// **************************************************************
						// RI014 - Add the detail to the two-dimentional array used later to output to a CSV file
						//         Ensure that null values are changes to blanks
						// **************************************************************
						
						dataList[num_count][0] = str_customer;								// RI014
						dataList[num_count][1] = str_companyname;							// RI014
						dataList[num_count][2] = str_contract;								// RI014
						dataList[num_count][3] = str_startdate;								// RI014
						dataList[num_count][4] = str_estRetDate;							// RI014
						dataList[num_count][5] = equiponrentbean.getColumn("RDITEM");		// RI014
						dataList[num_count][6] = equiponrentbean.getColumn("RDQTY");		// RI014
						dataList[num_count][7] = str_description;							// RI014
						dataList[num_count][8] = str_pickup;								// RI014
						if ( str_pickup.equals("Yes") )										// RI024
							dataList[num_count][8] = "Tkt: " + str_PUTicket;			        // RI024
						dataList[num_count][9] = equiponrentbean.getColumn("RHORDB");		// RI014
						dataList[num_count][10] = equiponrentbean.getColumn("RHPO#");		// RI014
						dataList[num_count][12] = str_jobname;								// RI017
						dataList[num_count][13] = str_dayrate;								// RI019
						dataList[num_count][14] = str_weekrate;								// RI019
						dataList[num_count][15] = str_monthrate;							// RI019
						dataList[num_count][16] = str_totalbilled;							// RI019
						dataList[num_count][17] = str_totalaccrued;							// RI019
						dataList[num_count][18] = str_totalestcost;							// RI019
						
						if ( !str_company.equals("HG") ) { 		// RI019
							dataList[num_count][16] = "";		// RI019
							dataList[num_count][17] = "";		// RI019
							dataList[num_count][18] = "";		// RI019
						}										// RI019
						
						if ( overdueContract )
							dataList[num_count][11] = "Yes";								// RI014
						else
							dataList[num_count][11] = "No";									// RI014

						if ( !str_itemcomments.trim().equals("") )	{						// RI016
							dataList[num_count][7] = dataList[num_count][7].trim() + " *** COMMENT: " + str_itemcomments.trim();	// RI016
							str_itemcomments = "<BR>" + str_itemcomments.trim();			// RI016
						}
*/
