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
//    Index    User Id         	Date      Project                            Desciption
//   -------  -------------  ----------  -------------------  ----------------------------------------------------
//    RI001    DTC9028        06/21/05    SR31413 PCR19        Implement datasource
//
// ***************************************************************************
package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*;
import java.util.*;
import etrieveweb.utility.sqlBean;

public class MenuSecurityBean extends sqlBean implements java.io.Serializable {

	private  ArrayList authorizedProgramCodes;
	private  boolean   authorizedProgramCodesInitialized = false;
	
	public MenuSecurityBean() {
		super();
	}


	public void cleanup() throws Exception{
	}


	// ******************************************************
	// Retrieve the authority for the specified RentalMan profile
	// ******************************************************

	public void getAuthorizedProgramCodes( String company,  String datalib,  String userName)   throws Exception   {

		if (authorizedProgramCodesInitialized)
			return;
		
		String SQLStatement = "";
		authorizedProgramCodes = new ArrayList();
			
		Statement stmt = null;		// RI001
		ResultSet result = null;	// RI001
		
		// Check to see if user is authorized.

		do   {

			try   {

				SQLStatement = "SELECT * from " + datalib + ".syspgmfl where ZPCMP = '" +
		        				company + "' and ZPUSER = '" + userName + "'";

				stmt = conn.createStatement();//(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

				result = stmt.executeQuery(SQLStatement);

				result.next();

				authorizedProgramCodes.add(result.getString("ZPPGMS"));

				userName = "";
				userName = result.getString("ZPGRPU").trim();

			} catch (Exception e) {
				System.out.println(e.toString());
				break;
			} finally {								// RI001
				endcurrentResultset(result);			// RI001
				endcurrentStatement(stmt);				// RI001
			}											// RI001

		} 

		while ( !userName.equals("") );
		authorizedProgramCodesInitialized = true;
	}


	// *****************************************************
	//  Determine if a user is authorized to a Rentalman Etrieve option
	// *****************************************************		

	public synchronized boolean isUserAuthorized(  String company,  String datalib,   String userName,  String menuCode  )   throws Exception {

		if ( menuCode.equals("U06")  ||  menuCode.equals("U70")  ||  menuCode.equals("U83")  ||  menuCode.equals("U84") )
			return false;

		String code = new String();
		String[] searchCodeArray = new String[4];

		// Build the authorized program codes array, if it doesn't already exist

		getAuthorizedProgramCodes(company, datalib, userName);
		
		searchCodeArray[0] = "***";
		searchCodeArray[1] = menuCode.substring(0,1) + "**";
		searchCodeArray[2] = menuCode.substring(0,2) + "*";
		searchCodeArray[3] = menuCode; 

		int valid = 1;

		for  ( int i = 0;  i < authorizedProgramCodes.size();   i++  )
		{
			for  (  int j = 0;   j < searchCodeArray.length;  j++  )
			{
				code = (String) ( authorizedProgramCodes.get( i )  );

				if ( code.indexOf( searchCodeArray[ j ] )  !=  -1 )
				{
					valid++;
				}
			}
		} 

		// **************************************************
		// Divide the valid variable by 2 and if the remainder is equal 
		// to zeros, then return valid.     Odd means not authorized, 
		// even means authorized
 		// **************************************************

		if  ( valid % 2 == 0 )  
		{
			return true;
		}
		else
		{
			return false;
		}

	}

}