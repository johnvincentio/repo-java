package com.idc.rm.equipmentOnRent;

import java.io.Serializable;

public class EquipmentOnRentItemInfo implements Serializable {

	private String m_sAccountNumber;		// dataList[num_count][0] = str_customer;
	private String m_sAccountName;			// dataList[num_count][1] = str_companyname;
	private String m_sContract;				// dataList[num_count][2] = str_contract;
	private String m_sStartDate;			// dataList[num_count][3] = str_startdate;
	private String m_sEstimatedReturnDate;	// dataList[num_count][4] = str_estRetDate;
	private String m_sItem;					// dataList[num_count][5] = equiponrentbean.getColumn("RDITEM");
	private String m_sQuantity;				// dataList[num_count][6] = equiponrentbean.getColumn("RDQTY");
	private String m_sDescription;			// dataList[num_count][7] = str_description;
	private String m_sPickupTicket;			// dataList[num_count][8]
	private String m_sOrderedBy;			// dataList[num_count][9] = equiponrentbean.getColumn("RHORDB");
	private String m_sPurchaseOrder;		// dataList[num_count][10] = equiponrentbean.getColumn("RHPO#");
	private boolean m_bOverdueContract = false;	// dataList[num_count][11]
	private String m_sJobName;				// dataList[num_count][12] = str_jobname;
	private String m_sDayRate;				// dataList[num_count][13] = str_dayrate;
	private String m_sWeekRate;				// dataList[num_count][14] = str_weekrate;
	private String m_sMonthRate;			// dataList[num_count][15] = str_monthrate;
	private String m_sTotalBilled;			// dataList[num_count][16] = str_totalbilled;
	private String m_sTotalAccrued;			// dataList[num_count][17] = str_totalaccrued;
	private String m_sTotalEstimatedCost;	// dataList[num_count][18] = str_totalestcost;

	private String m_sDetailSequence;
	private int m_nCountryCode;
	private int m_nCategory;
	private int m_nClassification;

	public StringBuffer getCsvData(boolean isUS) {
		StringBuffer buf = new StringBuffer();
		buf.append (m_sAccountNumber).append(",");		// dataList[num_count][0] = str_customer;
		buf.append (m_sAccountName).append(",");		// dataList[num_count][1] = str_companyname;
		buf.append (m_sContract).append(",");		// dataList[num_count][2] = str_contract;
		buf.append (m_sStartDate).append(",");		// dataList[num_count][3] = str_startdate;
		buf.append (m_sEstimatedReturnDate).append(",");	// dataList[num_count][4] = str_estRetDate;
		buf.append (m_sItem).append(",");			// dataList[num_count][5] = equiponrentbean.getColumn("RDITEM");
		buf.append (m_sQuantity).append(",");			// dataList[num_count][6] = equiponrentbean.getColumn("RDQTY");
		buf.append (m_sDescription).append(",");	// dataList[num_count][7] = str_description;

		buf.append (m_sPickupTicket).append(",");		// dataList[num_count][8]
		buf.append (m_sOrderedBy).append(",");			// dataList[num_count][9]
		buf.append (m_sPurchaseOrder).append(",");		// dataList[num_count][10]

		if (m_bOverdueContract)						// dataList[num_count][11]
			buf.append ("Yes,");
		else
			buf.append ("No,");

		buf.append (m_sJobName).append(",");		// dataList[num_count][12] = str_jobname;
		buf.append (m_sDayRate).append(",");		// dataList[num_count][13] = str_dayrate;
		buf.append (m_sWeekRate).append(",");		// dataList[num_count][14] = str_weekrate;
		buf.append (m_sMonthRate).append(",");		// dataList[num_count][15] = str_monthrate;
		if (m_nCountryCode == 1) {
			buf.append (m_sTotalBilled).append(",");		// dataList[num_count][16] = str_totalbilled;
			buf.append (m_sTotalAccrued).append(",");		// dataList[num_count][17] = str_totalaccrued;
			buf.append (m_sTotalEstimatedCost).append(",");		// dataList[num_count][18] = str_totalestcost;
		}

		return buf;
	}
	
	public EquipmentOnRentItemInfo (int countryCode, String accountNumber, String accountName, int todayDate, 
				EquipmentOnRentBean equipmentOnRentBean, 
				EquipmentOnRentPickupInfo equipmentOnRentPickupInfo,
				PendingTransactionsInfo pendingTransactionsInfo) throws Exception {

		m_nCountryCode = countryCode;
		m_sAccountNumber = accountNumber;
		m_sAccountName = accountName;
		m_sContract = equipmentOnRentBean.getColumn("RDCON#").trim();

		m_sStartDate = equipmentOnRentBean.getColumn("RHDATO");
		if (! m_sStartDate.trim().equals("0") && m_sStartDate.length() == 8)
			m_sStartDate = m_sStartDate.substring(4, 6) + "/" + m_sStartDate.substring(6, 8) + "/" + m_sStartDate.substring(2, 4);
		else
			m_sStartDate = "";

		m_sEstimatedReturnDate = equipmentOnRentBean.getColumn("RHERDT");
		if (! m_sEstimatedReturnDate.trim().equals("0") && m_sEstimatedReturnDate.length() == 8) {
			int estimatedReturn = Integer.parseInt(m_sEstimatedReturnDate);
			if (estimatedReturn < todayDate)
				m_bOverdueContract = true;
			m_sEstimatedReturnDate = m_sEstimatedReturnDate.substring(4, 6) + "/" + m_sEstimatedReturnDate.substring(6, 8) + "/" + m_sEstimatedReturnDate.substring(2, 4);
		} 
		else
			m_sEstimatedReturnDate = "";

		m_sItem = equipmentOnRentBean.getColumn("RDITEM").trim();
		m_sQuantity = equipmentOnRentBean.getColumn("RDQTY");
		m_sDescription = equipmentOnRentBean.getColumn("ECDESC").replace('"', ' ');

		m_sDetailSequence = equipmentOnRentBean.getColumn("RDSEQ#").trim();

		m_sPickupTicket = "No";
		String str = equipmentOnRentPickupInfo.getTicket (m_sContract, m_sDetailSequence, m_sItem);
		if (str != null) m_sPickupTicket = "Tkt: " + str;
		if (pendingTransactionsInfo.isRelease (m_sContract, m_sDetailSequence)) 
			m_sPickupTicket = "Pending";

		m_sOrderedBy = equipmentOnRentBean.getColumn("RHORDB");
		m_sPurchaseOrder = equipmentOnRentBean.getColumn("RHPO#");

		m_sJobName = equipmentOnRentBean.getColumn("CJNAME");

		m_sDayRate = equipmentOnRentBean.getColumn("RDDYRT");
		m_sWeekRate = equipmentOnRentBean.getColumn("RDWKRT");
		m_sMonthRate = equipmentOnRentBean.getColumn("RDMORT");
		m_sTotalBilled = equipmentOnRentBean.getColumn("RNBILL");
		m_sTotalAccrued = equipmentOnRentBean.getColumn("RNACCR");
		m_sTotalEstimatedCost = equipmentOnRentBean.getColumn("RNRCST");

		m_nCategory = Integer.valueOf(equipmentOnRentBean.getColumn("RDCATG")).intValue();		// RI016
		m_nClassification = Integer.valueOf(equipmentOnRentBean.getColumn("RDCLAS")).intValue();	// RI016


		if (m_sTotalBilled == null) m_sTotalBilled = "0.00";
		if (m_sTotalAccrued == null) m_sTotalAccrued = "0.00";
		if (m_sTotalEstimatedCost == null) m_sTotalEstimatedCost = "0.00";

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

	public String getDetailSequence() {return m_sDetailSequence;}
	public String getContractNumber() {return m_sContract;}
	public void setItemComments (String itemComments) {
		if (itemComments == null) return;
		if (itemComments.trim().length() < 1) return;
		m_sDescription += " *** COMMENT: " + itemComments.trim();
	}

	public boolean isReRentItem() {
		if (m_nCountryCode == 1 && m_nCategory == 975 && m_nClassification == 1) return true;
		return false;
	}
	public boolean isOverdueContract() {return m_bOverdueContract;}
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
