// *****************************************************************************************************************
// Copyright (C) 2005 The Hertz Corporation, All Rights Reserved.  Unpublished.       
//                                                                                              
// The information contained herein is confidential and proprietary to the Hertz Corporation
// and may not be duplicated, disclosed to third parties, or used for any purpose not       
// expressly authorized by it.                                                              
// Any unauthorized use, duplication, or disclosure is prohibited by law.                   
// ****************************************************************************************************************
// **************************************************************************************************************
//   MODIFICATION INDEX 
//   
//    Index      User Id        Date        Project             Desciption
//    ------    -----------  -----------   -----------------   --------------------------------------------
//    RI001      DTC9028      06/06/05      SR31413 PCR30       Ensure only the payments are selected and ensure greater that zeros
//    RI002      DTC9028      07/22/05      SR31413 PCR19       Implement datasource modifications
//    RI003      DTC9028      11/08/05      SR35420             Data retrieval limit
// **************************************************************************************************************

package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.net.*;
import java.util.Calendar;
import java.util.Date;
import etrieveweb.utility.sqlBean;


public class paychecktotalBean extends sqlBean implements java.io.Serializable {

	private String sIncludeCredits = "Y",
	srbName = "etrieveweb.etrievebean.Preferences";

	ResultSet result = null, result2 = null;
	Statement stmt = null, stmt2 = null;

	public paychecktotalBean() {
		super();
		getPreferences();
	}

	private void getPreferences() {
    	
		try {
			Properties settings = new Properties();
			URL path = this.getClass().getClassLoader().getResource("conf//Preferences.properties"); 
			FileInputStream sf = new FileInputStream(path.getPath());
			settings.load(sf);
			sIncludeCredits = settings.getProperty("PrefCredits");
		} catch (Exception e) {
			System.err.println("Failed to load properties.");
		}
	}

	public synchronized boolean getRows(
		String customer_list,
		int customer_num,
		String company,
		String datalib,
		int start_num,
		String str_orderBy,
		String str_IncludeCredits)	// RI002
		throws Exception {

		// **********************************************************
		// RI002 - Check to see if the parameter value was passed
		// If so, override the value
		// **********************************************************

		if ( !str_IncludeCredits.trim().equals("") )	// RI002
				sIncludeCredits = str_IncludeCredits;	// RI002
				
		// ** Note: The previous hard-coded order by was adsysd desc, adpid#

		if ( !str_orderBy.trim().equals("") )
			str_orderBy = " order by " + str_orderBy;
			
		String SQLstatement =
			"select ADSYSD, ADPID#, SubTotal "
			+ "from "
			+  datalib
			+ ".ARIDETV1"
			+ " where ADCMP='"
			+ company
			+ "' and ADCUS# in "
			+ customer_list;

			if (sIncludeCredits.equals("N")) {
				SQLstatement += " and SubTotal > 0 ";
			}
			
		SQLstatement += " " + str_orderBy;

		// RI001 - modified SQL statement

		Calendar Today = Calendar.getInstance();

		Today.add(Calendar.YEAR, -2); // RI001
		
		int todayDate = Today.get(Calendar.YEAR)*10000 + (Today.get(Calendar.MONTH)+1)*100 + Today.get(Calendar.DAY_OF_MONTH);

		SQLstatement = 
		"SELECT adcmp, adpid#, adsysd, adcus#, sum(total) as SubTotal FROM ("
			+ " SELECT adcmp, adpid#, adsysd, adcus#, sum(adamt$ * -1) as total "     
			+ " FROM " + datalib + ".arihdrf2," + datalib + ".aridetf2 "			// RI003
			+ " where AHCMP='"+ company + "' "
			+ " and AHCUS# in " + customer_list
			+ " and ahinvd >= " + todayDate 				// RI003
			+ " and ahinv# > 0 "
			+ " and ahiseq > 0 "
			+ " AND ahtpay < 0 "
			+ " and ahcmp = adcmp "
			+ " and ahcus# = adcus# " 
			+ " and ahinv# = adinv# "
			+ " and ahiseq = adiseq "
			+ " and adstts not in ('P', 'D') "
			+ " and ADRCDC = 'P' "
			+ " GROUP BY adcmp, adpid#, adsysd, adcus# "
			+ " HAVING sum(adamt$ * -1) > 0 " 
			+ " ) as max "
			+ " group by adcmp, adpid#, adsysd, adcus# "
			+ str_orderBy;
			
		// RI002	stmt = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
		stmt = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI002
		
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

	public synchronized String[] getNumRows(
		String customer_list,
		int customer_num,
		String company,
		String datalib,
		String str_IncludeCredits)	// RI002
		throws Exception {

		// **********************************************************
		// RI002 - Check to see if the parameter value was passed
		// If so, override the value
		// **********************************************************

		if ( !str_IncludeCredits.trim().equals("") )	// RI002
				sIncludeCredits = str_IncludeCredits;	// RI002
				
		String SQLstatement2 = "";
		double TotalAmount = 0.0;
		String[] Array = { "", "" };
		DecimalFormat df = new DecimalFormat("###,###.##");

		SQLstatement2 =
			"select ADSYSD, ADPID#, SubTotal "
			+ "from "
			+ datalib
			+ ".ARIDETV1"
			+ " where ADCMP='"
			+ company
			+ "' and ADCUS# in "
			+ customer_list;

		if (sIncludeCredits.equals("N")) {
			SQLstatement2 += " and SubTotal > 0 ";
		}
		
		// RI001 - modified SQL statement

		Calendar Today = Calendar.getInstance();

		Today.add(Calendar.YEAR, -2); // RI001
		
		int todayDate = Today.get(Calendar.YEAR)*10000 + (Today.get(Calendar.MONTH)+1)*100 + Today.get(Calendar.DAY_OF_MONTH);

		SQLstatement2 = 
		"SELECT adcmp, adpid#, adsysd, adcus#, sum(total) as SubTotal FROM ("
			+ " SELECT adcmp, adpid#, adsysd, adcus#, sum(adamt$ * -1) as total "     
			+ " FROM " + datalib + ".arihdrf2," + datalib + ".aridetf2 "			// RI003
			+ " where AHCMP='"+ company + "' "
			+ " and AHCUS# in " + customer_list
			+ " and ahinvd >= " + todayDate 			// RI003
			+ " and ahinv# > 0 "
			+ " and ahiseq > 0 "
			+ " AND ahtpay < 0 "
			+ " and ahcmp = adcmp "
			+ " and ahcus# = adcus# " 
			+ " and ahinv# = adinv# "
			+ " and ahiseq = adiseq "
			+ " and adstts not in ('P', 'D') "
			+ " and ADRCDC = 'P' "
			+ " GROUP BY adcmp, adpid#, adsysd, adcus# "
			+ " HAVING sum(adamt$ * -1) > 0 " 
			+ " ) as max "
			+ " group by adcmp, adpid#, adsysd, adcus# ";


		stmt2 = conn.createStatement();
		result2 = stmt2.executeQuery(SQLstatement2);
		
		int totalrecords = 0;

		while (result2.next()) {
			TotalAmount += Double.valueOf(result2.getString("SubTotal")).doubleValue();
			totalrecords = result2.getRow();
		}
		
		Array[0] = Integer.toString(totalrecords);
		Array[1] = df.format(TotalAmount);
		
		return Array;
	}

	public void cleanup() throws Exception {
		endcurrentResultset(result);	// RI002
		endcurrentStatement(stmt);		// RI002
		endcurrentResultset(result2);	// RI002
		endcurrentStatement(stmt2);		// RI002
	}

}