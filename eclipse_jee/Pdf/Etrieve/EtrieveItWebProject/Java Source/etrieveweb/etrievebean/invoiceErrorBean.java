// *****************************************************************************************************************
// Copyright (C) 2005 The Hertz Corporation, All Rights Reserved.  Unpublished.       
//                                                                                              
// The information contained herein is confidential and proprietary to the Hertz Corporation
// and may not be duplicated, disclosed to third parties, or used for any purpose not       
// expressly authorized by it.                                                              
// Any unauthorized use, duplication, or disclosure is prohibited by law.                   
// ****************************************************************************************************************
// *************************************************************************
// Description:  Retrieve location information 
// Date:	         02-24-2004     
// Developer:    Robert Iacobucci	            
// *************************************************************************    
//   MODIFICATION INDEX
//   
//    Index       User Id       Project           Desciption
//   ----------  ------------  ----------------  ----------------------------------------------------
//    x001        DTC9028       SR28586 PCR22     Created new bean to retrieve the location information for Canada.  
//    RI002       DTC9028       SR31413 PCR19     Datasource implementation modification  
// ******************************************************************************************************************

package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*; 
import etrieveweb.utility.sqlBean;

public class invoiceErrorBean extends sqlBean implements java.io.Serializable{

	ResultSet result=null;
	Statement stmt=null;


	public invoiceErrorBean() {
  		super();
	}
 

	public synchronized String getLocInfo(String company, String datalib, int customer_num, int contract_num, int sequence_num, String transtype) throws Exception { 

		String SQLstatement   = "";
		String SQLstatement2 = "";
		String infotext = "";

		if (sequence_num == 0 && !transtype.equals("sales") )  {

			SQLstatement = 
				"select distinct ZLCITY, ZLST, ZLAREA, ZLPHON  " + 
				"from " + datalib + ".RAOHDRFL, " + datalib +  ".SYSLOCFL "  +
				" where RHCMP='" + company + "'  and RHCON#  = " +  contract_num + 
				" and RHCMP = ZLCMP and RHLOC = ZLLOC ";  

			SQLstatement2 = 
				"select distinct ZLCITY, ZLST, ZLAREA, ZLPHON  " + 
				"from " + datalib + ".ARIHDRFL, " + datalib +  ".SYSLOCFL "  +
				" where AHCMP='" + company + "'  and AHCUS# = " +  customer_num +
				" and AHINV#  = " +  contract_num + 
				" and AHCMP = ZLCMP and AHLOC = ZLLOC ";  

		}  else  {

			SQLstatement = 
				"select distinct ZLCITY, ZLST, ZLAREA, ZLPHON  " + 
				"from " + datalib + ".RACHDRFL, " + datalib +  ".SYSLOCFL "  +
				" where RHCMP='" + company + "'  and RHCON#  = " +  contract_num + 
				" and RHISEQ = " + sequence_num +
				" and RHCMP = ZLCMP and RHLOC = ZLLOC ";  

			SQLstatement2 = 
				"select distinct ZLCITY, ZLST, ZLAREA, ZLPHON  " + 
				"from " + datalib + ".ARIHDRFL, " + datalib +  ".SYSLOCFL "  +
				" where AHCMP='" + company + "'  and AHCUS# = " +  customer_num +
				" and AHINV#  = " +  contract_num + " and AHISEQ = " + sequence_num +
				" and AHCMP = ZLCMP and AHLOC = ZLLOC ";  

		}


		try {

			// RI002	stmt=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			stmt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI002
			
			result=stmt.executeQuery(SQLstatement);

			String str_gotlocation = "";

			if ( result.next() )  {
				
				str_gotlocation = "Yes";
			
			} else {
				
				// RI002	stmt=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

				stmt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI002
				
				result=stmt.executeQuery(SQLstatement2);
          				
				if ( result.next() ) 
					str_gotlocation = "Yes";
			
			}

			if ( str_gotlocation.equals("Yes") )  {
				
				String str_city = result.getString("ZLCITY").trim();
				String str_state    = result.getString("ZLST").trim();
				String str_area     = result.getString("ZLAREA").trim();
				String str_phone = result.getString("ZLPHON").trim();
				
				infotext = str_city;

				if (  !str_city.equals("")  &&   !str_state.equals("")  )  
					infotext = infotext.trim() + ",";  
					
				infotext = infotext.trim() + " " + str_state;  

				if (  !str_phone.equals("0")  )  
					infotext = infotext.trim() + " at";  

				if (  !str_area.equals("0")  &&  !str_phone.equals("0")  )  
					infotext = infotext.trim() + " (" + str_area.trim() + ")";  

				if ( !str_phone.trim().equals("0") )  {

					if ( str_phone.length() == 7 )
						str_phone = str_phone.substring(0, 3) + "-" + str_phone.substring(3, 7);

					infotext = infotext.trim() + " " + str_phone;  
				}
				
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			return "";
		}

		return infotext.trim();

	}


	public void cleanup() throws Exception {
		endcurrentResultset(result);	// RI002
		endcurrentStatement(stmt);		// RI002
	}

}

