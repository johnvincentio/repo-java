// *****************************************************************************************************************
// Copyright (C) 2005 The Hertz Corporation, All Rights Reserved.  Unpublished.       
//                                                                                              
// The information contained herein is confidential and proprietary to the Hertz Corporation
// and may not be duplicated, disclosed to third parties, or used for any purpose not       
// expressly authorized by it.                                                              
// Any unauthorized use, duplication, or disclosure is prohibited by law.                   
// ****************************************************************************************************************   
//   MODIFICATION INDEX
//   
//    Index     User Id       Date       Project           Desciption
//   --------  -----------  ----------  ----------------  ----------------------------------------------------
//    x001      DTC9028                  SR26609 PCR1      Add the invoice amount totals to the SQL 
//    x002      DTC9028                  SR26609 PCR1      Add the account# to the SQL 
//    x003      DTC9028      01/14/03    SR26609 PCR1      Add the bill from/to dates, rent days and hours to the SQL 
//    x004      DTC9028      01/27/03    SR26609 PCR1      Added the labor charges to the SQL
//    x005      DTC9028      02/12/03    SR26609 PCR1      Add a method to retrieve work order header info
//    x006      DTC9028      02/18/03    SR26609 PCR1      Added the employee number to the SQL
//    RI007     DTC9028      08/01/05    SR31413 PCR19     Datasource implementation modification
// ****************************************************************************************************************

package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*; 
import etrieveweb.utility.sqlBean;

public class contractdetailBean extends sqlBean implements java.io.Serializable{

	ResultSet result=null, result2=null, result3=null;
	Statement stmt=null, stmt2=null, stmt3=null;


	public contractdetailBean() {
		super();
	}
 

 
	public synchronized boolean getRows(String customer_list, int customer_num, String company, String datalib, int contract_num, int sequence_num, String transtype) throws Exception { 

		String SQLstatement = "";

		if (sequence_num == 0 && !transtype.equals("sales") )
			SQLstatement = 
				"select RHJOBL, RHJOB#, RHLOC, RHPO#, RHORDB, RHSIG, RH#CYB, RHDATO, RHTIMO, RHERDT, RHERTM, RHLRDT, RHLRTM, RHLBDT, RHDELC, RHDELB, RHDLVD, RHPKUB, RHTYPE, RHMANI, RHSYSD, RHEMP#, " +
				" CMCUS#, CMNAME, CMADR1, CMADR2, CMCITY, CMSTAT, CMZIP, CMAREA, CMPHON " + 
				"from " + datalib + ".RAOHDRFL left outer join " + datalib + ".CUSMASFL " +  
				"on RHCMP=CMCMP and RHCUS#=CMCUS# " +
				" where RHCMP='" + company + "' and RHCON#=" + contract_num;                  
		else 
			SQLstatement = 
				"select RHJOBL, RHJOB#, RHLOC, RHPO#, RHORDB, RHSIG, RH#CYB, RHDATO, RHTIMO, RHERDT, RHERTM, RHLRDT, RHLRTM, RHLBDT, RHCBDF, RHDELC, RHDELB, RHDLVD, RHPKUB, RHTYPE, RHMANI, RHSYSD, RHEMP#, RHRHRS, RHRLOC, RHRDYS, RHOTYP,  RHRNT$, RHDW$, RHSLS$, RHTAX$, RHFUEL, RHLBR$, RHRTN$, RHMSC$, RHAMT$, RHDEL$, RHPKU$, " +  
				" CMCUS#, CMNAME, CMADR1, CMADR2, CMCITY, CMSTAT, CMZIP, CMAREA, CMPHON " +  
				"from " + datalib + ".RACHDRFL left outer join " + datalib + ".CUSMASFL " +  
				"on RHCMP=CMCMP and RHCUS#=CMCUS# " +
				" where RHCMP='" + company + "' and RHCON#=" + contract_num +  " and RHISEQ=" + sequence_num;   
 
		try {
			
			// RI007	stmt=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			stmt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI007
			
			result=stmt.executeQuery(SQLstatement);
			
			return (result != null);
      
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			System.out.println("Record not found");
			return false;
		}

	}


	public boolean getNext() throws Exception {
		return result.next();
	}


	public String getColumn(String colNum) throws Exception {
		return result.getString(colNum);
	}


	public synchronized String getworkorder(String company, String datalib, int contract_num) throws Exception { 

		String SQLstatement3 = "";
		String str_workorder = "";

		SQLstatement3 = 
			"select  VHEQP#, VHMAKE, VHMODL, VHSER# " + 
			"from " + datalib + ".WOHEADFL " +  
			"where VHCMP='" + company.trim() + "' and VHWO# =" + contract_num;

		try {
          
			// RI007	stmt3=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

			stmt3=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI007
			
			result3=stmt3.executeQuery(SQLstatement3);

			if ( result3.next() )   {
				str_workorder = result3.getString("VHEQP#");
				return str_workorder;
			}
			
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			return str_workorder;
		}

		return str_workorder;

	}

	public void cleanup() throws Exception {
		endcurrentResultset(result);	// RI007
		endcurrentResultset(result2);	// RI007
		endcurrentResultset(result3);	// RI007
		endcurrentStatement(stmt);		// RI007
		endcurrentStatement(stmt2);		// RI007
		endcurrentStatement(stmt3);		// RI007
	}

}