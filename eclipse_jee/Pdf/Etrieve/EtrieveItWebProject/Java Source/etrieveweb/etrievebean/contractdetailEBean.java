// *****************************************************************************************************************
// Copyright (C) 2005 The Hertz Corporation, All Rights Reserved.  Unpublished.       
//                                                                                              
// The information contained herein is confidential and proprietary to the Hertz Corporation
// and may not be duplicated, disclosed to third parties, or used for any purpose not       
// expressly authorized by it.                                                              
// Any unauthorized use, duplication, or disclosure is prohibited by law.                   
// ****************************************************************************************************************
// Description:  Retrieve Credit and Adjustment Amount for an invoice
// Date:	         11-22-2002     
// Developer:    Robert Iacobucci	            
// *************************************************************************    
//   MODIFICATION INDEX
//   
//    Index   User Id     Date     Project          Desciption
//    ------  --------- --------  ---------------  ----------------------------------------------------------------
//    x001    DTC9028              SR26609 PCR1     Created new bean to retrieve the current balance, payments and adjustments.
//    x002    DTC2073              SR31413 PCR7     Display 'Validated Credit' balances for domestic.  
//    RI003   DTC9028    08/01/05  SR31413 PCR19    Datasource implementation modification                                   
// ********************************************************************************************************************

package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*;
import etrieveweb.utility.sqlBean;

public class contractdetailEBean
	extends sqlBean
	implements java.io.Serializable {

	ResultSet result = null, result2 = null;
	Statement stmt = null, stmt2 = null;

	public contractdetailEBean() {
		super();
	}

	public synchronized boolean getRows(
		int customer,
		String company,
		String datalib,
		int contract_num,
		int sequence_num)
		throws Exception {

		String SQLstatement = "";

		// *******************************************************************************************
		// Canada (company=CR) will ALWAYS select balances not equal to zeros (including negatives)
		// *******************************************************************************************

		if (company.trim().equals("CR")) {
			
			SQLstatement = "select AHCBAL, AHTPAY, AHADJ$  " + 
				"from " 
				+ datalib 
				+ ".ARIHDRFL "  
				+ " where AHCMP='" + company 
				+ "'  and AHCUS# = " 
				+ customer 
				+ "  and AHINV#=" 
				+ contract_num 
				+ " and AHISEQ=" 
				+ sequence_num;  
			
		} else {
			
			// ****************************************************************************************
			// Domestic will select invoices with a positive balance or invoices with a negative 
			// balance that are considered to be 'Valid Credit' credits.  A valid credit is 
			// determined by the AHCOLS field having the code range from B001 to B498.  - x005
			// ****************************************************************************************

			SQLstatement =
			" Select A.AHCBAL, A.AHTPAY, A.AHADJ$, AHCOLS, AHSTTS "
				+ " From "
				+ datalib
				+ ".ARIHDRFL A "
				+ " LEFT OUTER JOIN "
				+ datalib
				+ ".arihd2fl B "
				+ " ON A.ahcmp=B.ahcmp AND A.ahcus# = B.ahcus# and "
				+ "  A.ahinv# = B.ahinv# and A.ahiseq = B.ahiseq "
				+ " Where  A.AHCMP='"
				+ company
				+ "'  and A.AHCUS# = "
				+ customer
				+ "  and A.AHINV#="
				+ contract_num
				+ " and A.AHISEQ="
				+ sequence_num;
		}

		try {

			// RI003	stmt = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			stmt = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI003
			
			result = stmt.executeQuery(SQLstatement);
			
			return (result != null);
			
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			System.out.println("Record not found");
			return false;
		} catch (Exception e) {
			System.out.println("Error found");
			return false;
		}
	}

	public boolean getNext() throws Exception {
		return result.next();
	}

	public String getColumn(String colNum) throws Exception {
		return result.getString(colNum);
	}

	public void cleanup() throws Exception {
		endcurrentResultset(result);	// RI003
		endcurrentStatement(stmt);		// RI003
	}

}