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
// Date:	         03-18-2003     
// Developer:    Robert Iacobucci	            
// *************************************************************************    
//   MODIFICATION INDEX
//   
//    Index     User Id       Date     Project          Desciption
//   --------  -----------  --------- ---------------  ----------------------------------------------------
//    x001      DTC9028                SR28586 PCR2     Created new bean to retrieve the  location information.  
//    x002      DTC2073                SR28586 PCR9     Added ZLADR1, ZLADR2 and ZLZIP to the SQL in getLocInfo
//    x003      DTC2073                SR28586 PCR9     Modified logic to return a two element array instead of a string. 
//    RI004     DTC9028      08/01/05  SR31413 PCR19    Datasource implementation modification
//    RI005     DTC2073      02/03/06  SR35873          Abbreviated Equipment Release
// ****************************************************************************************************************

package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*; 
import etrieveweb.utility.sqlBean;

public class contractdetailFBean extends sqlBean implements java.io.Serializable{

	ResultSet result=null, result2=null;
	Statement stmt=null, stmt2=null;


	public contractdetailFBean() {
  		super();
	}
 
	public synchronized String[] getLocInfo(String company,  String location, String datalib) throws Exception { 

  		String SQLstatement = "";
  		String infotext = "";

		// ***************************************************
		// x003 - Create new array to return location information
		// ***************************************************

		String [] locationArray = new String[4];	// RI005  

		// ********************************************************************************
		//  x002 - Added ZLADR1, ZLADR2 and ZLZIP to the SQL
		// ********************************************************************************

  		SQLstatement = 
  			"select ZLADR1, ZLADR2, ZLCITY, ZLST, ZLZIP, ZLAREA, ZLPHON, ZLNAME, ZLHRS1, ZLHRS2, ZLHRS3    " + 	// RI005
			"from " + datalib + ".SYSLOCFL "  +
			" where ZLCMP='" + company + "'  and ZLLOC = '" + location + "'";  
             
  		try {
  			
			// RI004	stmt=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
       			
			stmt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI004
       			
			result=stmt.executeQuery(SQLstatement);

			if ( result.next() )   {

				// *************************************************************
				// x002 - retrieve the new variables ZLADR1, ZLADR2 and ZLZIP
				// *************************************************************

				String str_adr1  = result.getString("ZLADR1").trim();
				String str_adr2  = result.getString("ZLADR2").trim();
				String str_city  = result.getString("ZLCITY").trim();
				String str_state = result.getString("ZLST").trim();
				String str_zip   = result.getString("ZLZIP").trim();
				String str_area  = result.getString("ZLAREA").trim();
				String str_phone = result.getString("ZLPHON").trim();

				// ***************************************************				
				//  Create first element of the array - the contact information
				// ***************************************************

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

				locationArray[0] = infotext.trim();  // Mod x003

				// **************************************************************				
				//  x003 - Create second element of the array - the address information
				//  x003 - Logic will also check to see if the phone number is in address 1 or 2 
				// **************************************************************
				
				infotext = "";
				String str_phoneinaddress = "";

				int decpos = str_adr1.indexOf( str_phone ); 

				if (decpos == -1 )
					str_phoneinaddress = "No";
				else
					str_phoneinaddress = "Yes";

				if ( !str_adr1.trim().equals("")  &&  str_phoneinaddress.equals("No") )
					infotext = str_adr1.trim() + "<BR>";
				

				decpos = str_adr2.indexOf( str_phone ); 

				if (decpos == -1 )
					str_phoneinaddress = "No";
				else
					str_phoneinaddress = "Yes";

				if ( !str_adr2.trim().equals("")    &&  str_phoneinaddress.equals("No")  )
					infotext = infotext.trim() + str_adr2.trim() + "<BR>";
				
				infotext = infotext.trim() + str_city;

				if (  !str_city.equals("")  &&   !str_state.equals("")  )  
					infotext = infotext.trim() + ",";  
					
				infotext = infotext.trim() + " " + str_state.trim() + " " + str_zip.trim() + "<BR>";  

				if (  !str_area.equals("0")  &&  !str_phone.equals("0")  )  
					infotext = infotext.trim() + " (" + str_area.trim() + ")";  

				if ( !str_phone.trim().equals("0") )  {

					if ( str_phone.length() == 7 )
						str_phone = str_phone.substring(0, 3) + "-" + str_phone.substring(3, 7);

					infotext = infotext.trim() + " " + str_phone;  
				}

				locationArray[1] = infotext.trim();
				
				locationArray[2] = result.getString("ZLNAME").trim();	// RI005

				locationArray[3] =  									// RI005
						result.getString("ZLHRS1").trim() + "<BR>" +	// RI005 
						result.getString("ZLHRS2").trim() + "<BR>" +	// RI005
						result.getString("ZLHRS3").trim();				// RI005
				
			}

      	} catch (SQLException e) {

			System.err.println(e.getMessage());

			// ************************************
			// x003 - return the array not just blank
			// ************************************

			locationArray[0] = "";
			locationArray[1] = "";

        	// ****DELETED    return "";

			
			return locationArray;
       	}


	// ************************************
	// x003 - modified to return the array
	// ************************************

	//***DELETED   return infotext.trim();
	return locationArray;

	}

	public void cleanup() throws Exception {
		endcurrentResultset(result);	// RI004
		endcurrentStatement(stmt);		// RI004
	}

}
