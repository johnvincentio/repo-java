// *****************************************************************************************************************
// Copyright (C) 2005 The Hertz Corporation, All Rights Reserved.  Unpublished.       
//                                                                                              
// The information contained herein is confidential and proprietary to the Hertz Corporation
// and may not be duplicated, disclosed to third parties, or used for any purpose not       
// expressly authorized by it.                                                              
// Any unauthorized use, duplication, or disclosure is prohibited by law.                   
// ****************************************************************************************************************
// Description:  Purchase Order Activity Bean
// Date:	       11-20-2003     
// Developer:    Bob Iacobucci	            
// *************************************************************************    
// *************************************************************************
//   MODIFICATION INDEX 
//   
//    Index    User Id       Date      Project         Desciption
//   ------- -----------  ----------  --------------- ---------------------------------------------------
//    x001     DTC9028     11/14/03    SR28586 PCR8    Created bean
//    x002     DTC2073     07/06/04    SR31413 PCR7    Include 'Validated Credit' balance invoices as Open Invoices.
//    x003     DTC9028     01/12/05    TT404162        Corrected qty error for bulk items   
//    RI004    DTC9028     04/25/05    SR31413 PCR29   Add overdue contracts requirement
//    RI005    DTC9028     08/01/05    SR31413 PCR19   Datasource implementation modification
//    RI006    DTC2073     10/19/05    SR35420		   Data Retrieval Limit
//    RI007    DTC2073     12/15/05    SR35880         Add comments for re-rent items (catg/class 975-0001)
// ****************************************************************************************************************
package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*;
import etrieveweb.utility.sqlBean;

public class poActivityBean extends sqlBean implements java.io.Serializable {

	String company = "";

	ResultSet result = null,
		result2 = null,
		result3 = null,
		result4 = null,
		result5 = null,
		result6 = null,
		result7 = null,
		result8 = null;
		
	Statement stmt = null,
		stmt2 = null,
		stmt3 = null,
		stmt4 = null,
		stmt5 = null,
		stmt6 = null,
		stmt7 = null,
		stmt8 = null;

	public poActivityBean() {
		super();
	}

	public String Company() {
		return company;
	}
	
	public void setCompany(String input) {
		company = input;
	}

	public synchronized boolean getRows(
		String customer_list,
		int customer_num,
		String company,
		String datalib,
		int start_num,
		String pojobselect)
		throws Exception {

		String SQLstatement = "";

		SQLstatement =
			"select RDCON#, RDSEQ#, RDITEM, RDCATG, RDCLAS, (RDQTY - RDQTYR) as RDQTY, RDLOC, RDLBDT, RD$BLD, RHJOB#, RHPO#, RHORDB, RHDATO, ECDESC, RHERDT  "
				+ "from "
				+ datalib
				+ ".RAODETFL,  "
				+ datalib
				+ ".EQPCCMFL,  "
				+ datalib
				+ ".RAOHDRFL  "
				+ "where "
				+ " RHCMP='"
				+ company
				+ "' and RHTYPE in ('O', 'F')  and RHCUS# in "
				+ customer_list
				+ " and RHCMP = RDCMP and RHCON# = RDCON#  and RDTYPE = 'RI' and RDSTTS <> 'EX' and "
				+ "RDCMP = ECCMP AND RDCATG = ECCATG and RDCLAS = ECCLAS "
				+ pojobselect
				+ " ORDER BY RDCON#, RDITEM ";

		// RI005	stmt = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
		stmt = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI005
		
		result = stmt.executeQuery(SQLstatement);
		
		if (start_num > 0)
			result.absolute(start_num);
			
		return (result != null);
	}

	public boolean getNext() throws Exception {
		return result.next();
	}

	public String getColumn(String colNum) throws Exception {
		return result.getString(colNum);
	}


	//*******************************************
	// RI006 - Get line item comments
	//******************************************
			
	public synchronized String getItemComments(String company, String datalib, int contract, int seqno, int lineno) throws Exception { 

		String itemcomments = "";
		ResultSet result4=null ;
		Statement stmt4=null ;
				

		try 
		{		
			
			String SQLstatement4 = "";
				
			SQLstatement4 = 
				"select OCCMNT" +
				"  From "+ datalib + ".ORDCOMFL  " +
				"  where OCCMP='" + company + "'   " +
				"  and OCREF#= " +  contract +
				"  and OCISEQ= " + seqno +
				"  and OCTYPE= 'R' " +
				"  and OCASEQ= " + lineno ;

			stmt4=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	
				
			result4=stmt4.executeQuery(SQLstatement4);
					
			if (result4 != null)
			{		
					
				while(result4.next())
				{
					itemcomments = itemcomments.trim() + " " + result4.getString("OCCMNT").trim();
				}
									 							
			}										
		 		
		} catch (SQLException e) {
			
			System.err.println(e.getMessage());
			itemcomments = "";
		}
        
		endcurrentResultset(result4);			
		endcurrentStatement(stmt4);
				
		return itemcomments;

	}
	
	public synchronized String getItemSeq(String company, String datalib, int contract, int seqno, String item) throws Exception { 

		String linenumber = "0";
		ResultSet result4=null ;
		Statement stmt4=null ;
				
		try 
		{		
			
			String SQLstatement4 = "";
				
			SQLstatement4 = 
				"select RDSEQ#" +
				"  From "+ datalib + ".RACDETFL " +
				"  where RDCMP='" + company + "'   " +
				"  and RDCON#= " +  contract +
				"  and RDISEQ= " + seqno +
				"  and RDITEM = '" + item + "' ";

			stmt4=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	
				
			result4=stmt4.executeQuery(SQLstatement4);
					
			if (result4 != null)
			{			
				result4.next();
				
				linenumber = result4.getString("RDSEQ#").trim();									 							
			}										
		 		
		} catch (SQLException e) {
			
			System.err.println(e.getMessage());
			linenumber = "0";
		}
        
		endcurrentResultset(result4);			
		endcurrentStatement(stmt4);
				
		return linenumber;

	}	
	
	
	
	public synchronized boolean getReturnedRA(
		String customer_list,
		int customer_num,
		String company,
		String datalib,
		int start_num,
		String pojobselect)
		throws Exception {

		String SQLstatement2 = "";

		SQLstatement2 =
			"select distinct RHCON#  "
				+ "from "
				+ datalib
				+ ".RACHDRF4  "		// RI006
				+ "where "
				+ " RHCMP='"
				+ company
				+ "' "
				+ " and RHCUS# in "		// RI006
				+ customer_list			// RI006
				+ " and RHTYPE in ('O', 'F')   and  RHOTYP in ('P', 'F') "
				+ pojobselect;
				
		// RI055	stmt = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
		stmt2 = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI005
		
		result2 = stmt2.executeQuery(SQLstatement2);
		
		if (start_num > 0)
			result2.absolute(start_num);
			
		return (result2 != null);

	}

	public boolean getNext2() throws Exception {
		return result2.next();
	}

	public String getColumn2(String colNum) throws Exception {
		return result2.getString(colNum);
	}

	public synchronized boolean getReturnedEquip(
		String customer_list,
		int customer_num,
		String company,
		String datalib,
		int start_num,
		String contractselect,
		String pojobselect)
		throws Exception {

		String SQLstatement3 = "";

		SQLstatement3 =
			"select distinct RDCON#, RDISEQ, RDITEM, RDCATG, RDCLAS, sum(RDQTY - RDQTYR) as RDQTYR, RDLOC, RHJOB#, RHPO#, RHORDB, RHDATO, ECDESC, sum(RDAMT$) as RDAMT$  "
				+ "from "
				+ datalib
				+ ".RACHDRF4,  "		// RI006
				+ datalib
				+ ".RACDETFL,  "
				+ datalib
				+ ".EQPCCMFL  "
				+ "where "
				+ " RHCMP='"
				+ company
				+ "' and RHCUS# in "
				+ customer_list
				+ " and RHCON# in ("
				+ contractselect
				+ ") and RHTYPE in ('O', 'F')  and RHOTYP not in ('P', 'F') "
				+ " and RHCMP = RDCMP and RHCON# = RDCON#  and RHISEQ = RDISEQ  "
				+ " and RDTYPE = 'RI' and RDSTTS <> 'EX' "
				+ " and RDCMP = ECCMP and RDCATG = ECCATG and RDCLAS = ECCLAS "
				+ pojobselect
				+ "  GROUP BY RDCON#, RDISEQ, RDITEM, RDCATG, RDCLAS, RDQTYR, RDLOC,  RHJOB#, RHPO#, RHORDB, RHDATO, ECDESC  "
				+ " UNION "
				+ "select distinct RDCON#, RDISEQ, RDITEM, RDCATG, RDCLAS, RDQTYR, RDLOC, RHJOB#, RHPO#, RHORDB, RHDATO, ECDESC, sum(RDAMT$) as RDAMT$  "
				+ "from "
				+ datalib
				+ ".RACHDRF4,  "		// RI006
				+ datalib
				+ ".RACDETFL,  "
				+ datalib
				+ ".EQPCCMFL  "
				+ "where "
				+ " RHCMP='"
				+ company
				+ "' and RHCUS# in "
				+ customer_list
				+ " and RHCON# in ("
				+ contractselect
				+ ") and RHTYPE in ('O', 'F')  and RHOTYP in ('P', 'F') "
				+ " and RHCMP = RDCMP and RHCON# = RDCON#  and RHISEQ = RDISEQ  "
				+ " and RDTYPE = 'RI' and RDSTTS <> 'EX' "
				+ " and RDCMP = ECCMP and RDCATG = ECCATG and RDCLAS = ECCLAS "
				+ pojobselect
				+ "  GROUP BY RDCON#, RDISEQ, RDITEM, RDCATG, RDCLAS, RDQTYR, RDLOC,  RHJOB#, RHPO#, RHORDB, RHDATO, ECDESC  "
				+ " ORDER BY RDCON#, RDITEM ";
		// RI005	stmt = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
		stmt3 = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI005
		
		result3 = stmt3.executeQuery(SQLstatement3);
		
		if (start_num > 0)
			result3.absolute(start_num);
			
		return (result3 != null);

	}

	public boolean getNext3() throws Exception {
		return result3.next();
	}

	public String getColumn3(String colNum) throws Exception {
		return result3.getString(colNum);
	}

	public synchronized boolean getOpenInvoices(
		String customer_list,
		int customer_num,
		String company,
		String datalib,
		int start_num,
		String pojobselect)
		throws Exception {

		String SQLstatement4 = "";

		// *******************************************************************************************
		// Canada (company=CR) will ALWAYS select balances equal to zeros with all negatives. 
		// *******************************************************************************************

		if ( company.trim().equals("CR")  )
		{		
			
			SQLstatement4 =
				"SELECT A.*, B.*, (AHAMT$-AHCBAL) as CurrBalance "
					+ " FROM "
					+ datalib
					+ ".arihdrf2 A "
					+ " LEFT OUTER JOIN "
					+ datalib
					+ ".rachdrfl B ON AHCMP=RHCMP AND ahcus# = rhcus# and ahinv# = rhcon# and ahiseq = rhiseq"
					+ " WHERE (ahcmp = '"
					+ company
					+ "' AND ahcus# in "
					+ customer_list
					+ ") and AHSTTS <> 'PD' and AHCBAL <> 0 "
					+ pojobselect
					+ " ORDER BY AHINV#, AHISEQ, AHINVD ";
		}
		else
		{
			
		// ****************************************************************************************
		// Domestic will select invoices with a zero balance or invoices with a negative 
		// balance that are Not Validated Credits.  A valid credit is  determined by the 
		// AHCOLS field having the code range from B001 to B498. 
		// All the rest are Non Validated Credits. - x002
		// ****************************************************************************************

			SQLstatement4 =
				"SELECT A.*, C.*, (AHAMT$-AHCBAL) as CurrBalance "
					+ " FROM "
					+ datalib
					+ ".arihdrf2 A "
					+ " LEFT OUTER JOIN "
					+ datalib
					+ ".arihd2fl B ON A.ahcmp=B.ahcmp AND A.ahcus# = B.ahcus# and "
					+ "  A.ahinv# = B.ahinv# and A.ahiseq = B.ahiseq "
					+ "  LEFT OUTER JOIN "
					+ datalib
					+ ".rachdrfl C ON A.AHCMP=RHCMP AND A.ahcus# = rhcus# and A.ahinv# = rhcon# and A.ahiseq = rhiseq"
					+ " WHERE (A.ahcmp = '"
					+ company
					+ "' AND A.ahcus# in "
					+ customer_list
					+ ") and A.AHSTTS <> 'PD'  and "
					+ " ( ( A.AHCBAL > 0  ) OR " 
					+ "   ( A.AHCBAL < 0 AND B.AHCOLS >= 'B001' AND B.AHCOLS <= 'B498') ) "
					+ pojobselect
					+ " ORDER BY AHINV#, AHISEQ, AHINVD ";
		}	


		// RI005	stmt = 	conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
		stmt4 = 	conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI005
		
		result4 = stmt4.executeQuery(SQLstatement4);
		
		if (start_num > 0)
			result4.absolute(start_num);
			
		return (result4 != null);

	}

	public boolean getNext4() throws Exception {
		return result4.next();
	}

	public String getColumn4(String colNum) throws Exception {
		return result4.getString(colNum);
	}

	public synchronized boolean getPaidInvoices(
		String customer_list,
		int customer_num,
		String company,
		String datalib,
		int start_num,
		String pojobselect)
		throws Exception {

		String SQLstatement5 = "";

		SQLstatement5 =
			"SELECT A.*, B.*, (AHAMT$-AHCBAL) as CurrBalance "
				+ " FROM "
				+ datalib
				+ ".arihdrf2 A "
				+ " LEFT OUTER JOIN "
				+ datalib
				+ ".rachdrfl B ON AHCMP=RHCMP AND ahcus# = rhcus# and ahinv# = rhcon# and ahiseq = rhiseq"
				+ " WHERE (ahcmp = '"
				+ company
				+ "' AND ahcus# in "
				+ customer_list
				+ ") and AHSTTS =  'PD' and AHCBAL <=  0  "
				+ pojobselect
				+ " ORDER BY AHINV#, AHISEQ, AHINVD ";

		// RI005	stmt = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
		stmt5 = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI005
		
		result5 = stmt5.executeQuery(SQLstatement5);
		
		if (start_num > 0)
			result5.absolute(start_num);
			
		return (result5 != null);

	}

	public boolean getNext5() throws Exception {
		return result5.next();
	}

	public String getColumn5(String colNum) throws Exception {
		return result5.getString(colNum);
	}

	public synchronized boolean getInvoiceSum(
		String customer_list,
		int customer_num,
		String company,
		String datalib,
		int start_num,
		String pojobselect)
		throws Exception {

		String SQLstatement6 = "";

		SQLstatement6 =
			"SELECT RHPO#, RHJOB#, sum(AHAMT$) as AHAMT$, sum(AHCBAL) as AHCBAL "
				+ " FROM "
				+ datalib
				+ ".arihdrf2 A "
				+ " LEFT OUTER JOIN "
				+ datalib
				+ ".rachdrfl B ON AHCMP=RHCMP AND ahcus# = rhcus# and ahinv# = rhcon# and ahiseq = rhiseq"
				+ " WHERE (ahcmp = '"
				+ company
				+ "' AND ahcus# in "
				+ customer_list
				+ ")  "
				+ pojobselect
				+ "GROUP BY  RHPO#, RHJOB#  "
				+ " ORDER BY RHPO#, RHJOB# ";

		// RI005	stmt = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
		stmt6 = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI005
		
		result6 = stmt6.executeQuery(SQLstatement6);
		
		if (start_num > 0)
			result6.absolute(start_num);
			
		return (result6 != null);

	}

	public boolean getNext6() throws Exception {
		return result6.next();
	}

	public String getColumn6(String colNum) throws Exception {
		return result6.getString(colNum);
	}

	public synchronized String getPickupStatus(
		String company,
		String datalib,
		int contract_num,
		int sequence_num,
		String equipitem)
		throws Exception {

		String SQLstatement7 = "";
		String str_pickupStatus = "";

		SQLstatement7 =
			"select distinct RUSTTS "
				+ "from "
				+ datalib
				+ ".RAPKUPFL "
				+ "where RUCMP='"
				+ company.trim()
				+ "' and RUCON#="
				+ contract_num
				+ " and RUSTTS = 'OP' and RUEQP#='"
				+ equipitem.trim()
				+ "' and RUSEQ#="
				+ sequence_num;

		try {
			
			// RI005	stmt7 = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			stmt7 = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI005
			
			result7 = stmt7.executeQuery(SQLstatement7);
			
			if (result7.next()) {
				str_pickupStatus = result7.getString("RUSTTS");
				
				return str_pickupStatus;
			}
			
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			return str_pickupStatus;
		}

		return str_pickupStatus;

	}

	public synchronized boolean getHistoryEndDate(
		String customer_list,
		int customer_num,
		String company,
		String datalib,
		int start_num,
		String contractselect,
		String str_item)
		throws Exception {

		String SQLstatement8 = "";

		SQLstatement8 =
			"select RHOTYP, RHLRDT  "
				+ "from "
				+ datalib
				+ ".RACHDRFL,  "
				+ datalib
				+ ".RACDETFL  "
				+ "where "
				+ " RHCMP='"
				+ company
				+ "'  and RHCON#  = "
				+ contractselect
				+ " and RHCMP = RDCMP and RHCON# = RDCON#  and RHISEQ = RDISEQ  "
				+ " and RDTYPE = 'RI' and RDSTTS <> 'EX' "
				+ " and RDITEM = '"
				+ str_item.trim()
				+ "'  "
				+ " ORDER BY RHLRDT desc ";

		// RI005	stmt = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
		stmt8 = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI005
		
		result8 = stmt8.executeQuery(SQLstatement8);
		
		if (start_num > 0)
			result8.absolute(start_num);
			
		return (result8 != null);

	}

	public boolean getNext8() throws Exception {
		return result8.next();
	}

	public String getColumn8(String colNum) throws Exception {
		return result8.getString(colNum);
	}

	public void cleanup() throws Exception {
		endcurrentResultset(result);	// RI005
		endcurrentResultset(result2);	// RI005
		endcurrentResultset(result3);	// RI005
		endcurrentResultset(result4);	// RI005
		endcurrentResultset(result5);	// RI005
		endcurrentResultset(result6);	// RI005
		endcurrentResultset(result7);	// RI005
		endcurrentResultset(result8);	// RI005	
		endcurrentStatement(stmt);		// RI005
		endcurrentStatement(stmt2);		// RI005
		endcurrentStatement(stmt3);		// RI005
		endcurrentStatement(stmt4);		// RI005
		endcurrentStatement(stmt5);		// RI005
		endcurrentStatement(stmt6);		// RI005
		endcurrentStatement(stmt7);		// RI005
		endcurrentStatement(stmt8);		// RI005
	}

}