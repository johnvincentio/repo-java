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
//     Index      Date       User Id      Project        Desciption
//    --------  ----------  -----------  ---------------  ----------------------------------------------------
//     x001     12/04/01     Wynne        20384          Preference of if they are using international
//     x002     01/22/03     DTC9028      SR26609 PCR1   Modified SQL to select non-paid with a balance 
//     x003     01/24/03     DTC9028      SR26609 PCR1   Added the 150 days total 
//     x004     01/24/03     DTC9028      SR26609 PCR1   Include partial paid invoices 
//     x005     02/07/03     DTC9028      SR26609 PCR1   Make provision for a Java month where Jan = 0, Dec=11 
//     x006     03/02/04     DTC9028      SR28586 PCR27  Include credit balances for Canadia Etrieve (company = CR)
//     x007     02/06/04     DTC2073      SR31413 PCR7   Include 'Validated Credit' balance invoices as Open Invoices.
//     RI008    06/27/05     DTC9028      SR31413 PCR19  Implement the datasource modification
// ***************************************************************************

package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.net.*;
import etrieveweb.utility.sqlBean;

public class invoiceagingBean extends sqlBean implements java.io.Serializable {

	String company = "";

	ResultSet result = null, result2 = null;
	Statement stmt = null, stmt2 = null;
	String isUsingInternational = "Y",
	srbName = "etrieveweb.etrievebean.Preferences";

	public invoiceagingBean() {
		super();
		getPreferences();
	}

	// *****************************************************
	// Retrieve Rentalman properties file parameters
	// *****************************************************
	
	private void getPreferences() {

		try {
			
			Properties settings = new Properties();
			
			URL path =	this.getClass().getClassLoader().getResource("conf//Preferences.properties");
			
			FileInputStream sf = new FileInputStream(path.getPath());
			
			settings.load(sf);
			
			isUsingInternational = settings.getProperty("PrefInternational");
			
		} catch (Exception e) {
			System.err.println("Failed to load properties.");
		}
	}

	// *****************************************************
	// Retrieve company
	// *****************************************************
	
	public String Company() {
		return company;
	}

	// *****************************************************
	// Set company
	// *****************************************************
	public void setCompany(String input) {
		company = input;
	}

	// *****************************************************
	// Retrieve all rows
	// *****************************************************
	public synchronized boolean getRows(
		String customer_list,
		int customer_num,
		String company,
		String datalib,
		int start_num,
		String agingdate,
		String thrdate,
		String str_isUsingInternational		// RI008
		)
		throws Exception {

		String SQLstatement = "";

		String str_variableselect = "";

		// ********************************************************************
		// If a date selection is requested, then setup the query field
		// ********************************************************************

		if (thrdate.length() == 8)
			str_variableselect = " and AHDUED<=" + thrdate + " ";

		// *******************************************************************************************
		// Canada (company=CR) will ALWAYS select balances not equal to zeros (including negatives)
		// *******************************************************************************************

		if (company.trim().equals("CR")) {

			SQLstatement =
				"select AHINV#, AHISEQ, AHDUED, AHCBAL, AHLPDT "
					+ "from "
					+ datalib
					+ ".ARIHDRFL "
					+ "where AHCMP='"
					+ company
					+ "' and AHCUS# in "
					+ customer_list
					+ str_variableselect
					+ " and AHSTTS <> 'PD' and AHCBAL <> 0";

		} else {

			// ****************************************************************************************
			// Domestic will select invoices with a positive balance or invoices with a negative 
			// balance that are considered to be 'Valid Credit' credits.  A valid credit is 
			// determined by the AHCOLS field having the code range from B001 to B498.  - x005
			// ****************************************************************************************

			SQLstatement =
				"select A.AHINV#, A.AHISEQ, A.AHDUED, A.AHCBAL, A.AHLPDT,AHCOLS  "
					+ "FROM  "
					+ datalib
					+ ".ARIHDRFL  A  "
					+ "  LEFT OUTER JOIN "
					+ datalib
					+ ".ARIHD2FL B ON A.ahcmp=B.ahcmp AND A.ahcus# = B.ahcus# and "
					+ "  A.ahinv# = B.ahinv# and A.ahiseq = B.ahiseq "
					+ " WHERE (A.ahcmp = '"
					+ company
					+ "' AND A.ahcus# in "
					+ customer_list
					+ ") and A.AHSTTS <> 'PD' and "
					+ " ( ( A.AHCBAL > 0  ) OR "
					+ "   ( A.AHCBAL < 0 AND B.AHCOLS >= 'B001' AND B.AHCOLS <= 'B498') ) "
					+ str_variableselect;

		}

		// ****************************************************
		// Determine the flag was passed in
		// ****************************************************
		
		if ( !str_isUsingInternational.equals("") )			// RI008
			isUsingInternational = str_isUsingInternational;	// RI008


		if (isUsingInternational.equals("Y")) {
			SQLstatement += "and AHCURC in ('USD',' ')";
		}


		SQLstatement += " order by AHDUED, AHINV#, AHISEQ";

		stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

		result = stmt.executeQuery(SQLstatement);

		if (start_num > 0)
			result.absolute(start_num);

		return (result != null);

	}

	// *****************************************************
	// Retrieve next row
	// *****************************************************
	
	public boolean getNext() throws Exception {
		return result.next();
	}

	// *****************************************************
	// Retrieve column
	// *****************************************************

	public String getColumn(String colNum) throws Exception {
		return result.getString(colNum);
	}

	// *****************************************************
	// Retrieve total number of rows
	// *****************************************************

	public synchronized String[] getNumRows(
		String customer_list,
		int customer_num,
		String company,
		String datalib,
		String agingdate,
		String thrdate,
		String str_isUsingInternational		// RI008
		)
		throws Exception {

		Calendar AgeDueDate = Calendar.getInstance();
		Calendar Age30 = Calendar.getInstance();
		Calendar Age60 = Calendar.getInstance();
		Calendar Age90 = Calendar.getInstance();
		Calendar Age120 = Calendar.getInstance();
		Calendar Age150 = Calendar.getInstance();

		String SQLstatement2 = "";

		String str_variableselect = "";

		int year = 0, month = 0, day = 0;

		double TotalAmount = 0.0,
			CurrentAmount = 0.0,
			Day30Amount = 0.0,
			Day60Amount = 0.0,
			Day90Amount = 0.0,
			Day120Amount = 0.0,
			Day150Amount = 0.0;

		String[] Array = { "", "", "", "", "", "", "", "" };

		DecimalFormat df = new DecimalFormat("###,###,###,###.0#");

		// ********************************************************************
		// If a date selection is requested, then setup the query field
		// ********************************************************************

		if (thrdate.length() == 8)
			str_variableselect = " and AHDUED <= " + thrdate + " ";

		// ****************************************************************
		// Determine and format the aging date
		// ****************************************************************

		if (agingdate.length() == 8) {

			year = Integer.valueOf(agingdate.substring(0, 4)).intValue();
			month = Integer.valueOf(agingdate.substring(4, 6)).intValue();
			day = Integer.valueOf(agingdate.substring(6, 8)).intValue();

			// *************************************************************
			// Account for the Java date handling where January = 0 and December = 11
			// *************************************************************

			month = month - 1;

			Age30.set(year, month, day);
			Age60.set(year, month, day);
			Age90.set(year, month, day);
			Age120.set(year, month, day);
			Age150.set(year, month, day);

			Age30.add(Calendar.DATE, -29);
			Age60.add(Calendar.DATE, -59);
			Age90.add(Calendar.DATE, -89);
			Age120.add(Calendar.DATE, -119);
			Age150.add(Calendar.DATE, -149);

		}

		// *******************************************************************************************
		// Canada (company=CR) will ALWAYS select balances not equal to zeros (including negatives)
		// *******************************************************************************************

		if (company.trim().equals("CR")) {

			SQLstatement2 =
				"select AHINV#, AHISEQ, AHDUED, AHCBAL, AHLPDT "
					+ "from "
					+ datalib
					+ ".ARIHDRFL "
					+ "where AHCMP='"
					+ company
					+ "' and AHCUS# in "
					+ customer_list
					+ str_variableselect
					+ " and AHSTTS <> 'PD' and AHCBAL <> 0";

		} else {

			// ****************************************************************************************
			// Domestic will select invoices with a positive balance or invoices with a negative 
			// balance that are considered to be 'Valid Credit' credits.  A valid credit is 
			// determined by the AHCOLS field having the code range from B001 to B498.  - x005
			// ****************************************************************************************

			SQLstatement2 =
				"select A.AHINV#, A.AHISEQ, A.AHDUED, A.AHCBAL, A.AHLPDT,AHCOLS  "
					+ "FROM  "
					+ datalib
					+ ".ARIHDRFL  A  "
					+ "  LEFT OUTER JOIN "
					+ datalib
					+ ".ARIHD2FL B ON A.ahcmp=B.ahcmp AND A.ahcus# = B.ahcus# and "
					+ "  A.ahinv# = B.ahinv# and A.ahiseq = B.ahiseq "
					+ " WHERE (A.ahcmp = '"
					+ company
					+ "' AND A.ahcus# in "
					+ customer_list
					+ ") and A.AHSTTS <> 'PD' and "
					+ " ( ( A.AHCBAL > 0  ) OR "
					+ "   ( A.AHCBAL < 0 AND B.AHCOLS >= 'B001' AND B.AHCOLS <= 'B498') ) "
					+ str_variableselect;
		}

		// ****************************************************
		// Determine the flag was passed in
		// ****************************************************
		
		if ( !str_isUsingInternational.equals("") )			// RI008
			isUsingInternational = str_isUsingInternational;	// RI008



		if (isUsingInternational.equals("Y")) {
			SQLstatement2 += "and AHCURC in ('USD',' ')";
		}


		try {		// RI08
			
			stmt2 = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

			result2 = stmt2.executeQuery(SQLstatement2);

			int totalrecords = 0;

			while (result2.next()) {

				totalrecords = result2.getRow();

				if (result2.getString("AHDUED").length() == 8
					&& agingdate.length() == 8) {

					year = Integer.valueOf(result2.getString("AHDUED").substring(0, 4)).intValue();
					month = Integer.valueOf(result2.getString("AHDUED").substring(4, 6)).intValue();
					day = Integer.valueOf(result2.getString("AHDUED").substring(6, 8)).intValue();

					// *************************************************************
					// Account for the Java date handling where January = 0 and December = 11
					// *************************************************************

					month = month - 1;

					AgeDueDate.set(year, month, day);

					if (AgeDueDate.before(Age150))
						Day150Amount
							+= Double
								.valueOf(result2.getString("AHCBAL"))
								.doubleValue();
					else if (AgeDueDate.before(Age120))
						Day120Amount
							+= Double
								.valueOf(result2.getString("AHCBAL"))
								.doubleValue();
					else if (AgeDueDate.before(Age90))
						Day90Amount
							+= Double
								.valueOf(result2.getString("AHCBAL"))
								.doubleValue();
					else if (AgeDueDate.before(Age60))
						Day60Amount
							+= Double
								.valueOf(result2.getString("AHCBAL"))
								.doubleValue();
					else if (AgeDueDate.before(Age30))
						Day30Amount
							+= Double
								.valueOf(result2.getString("AHCBAL"))
								.doubleValue();
					else
						CurrentAmount
							+= Double
								.valueOf(result2.getString("AHCBAL"))
								.doubleValue();
				}
			}


			TotalAmount =
				CurrentAmount
					+ Day30Amount
					+ Day60Amount
					+ Day90Amount
					+ Day120Amount
					+ Day150Amount;
					
			Array[0] = Integer.toString(totalrecords);
			Array[1] = df.format(CurrentAmount);
			Array[2] = df.format(Day30Amount);
			Array[3] = df.format(Day60Amount);
			Array[4] = df.format(Day90Amount);
			Array[5] = df.format(Day120Amount);
			Array[6] = df.format(TotalAmount);
			Array[7] = df.format(Day150Amount);
			
		} catch (SQLException sqle) {
			System.err.println(sqle.getMessage());
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {								// RI008
			endcurrentResultset(result2);			// RI008
			endcurrentStatement(stmt2);				// RI008
		}											// RI008
								
		return Array;
	}

	// *****************************************************
	// Retrieve customer information
	// *****************************************************
	public synchronized String[] getCustomerInfo(
		int customer_num,
		String company,
		String datalib)
		throws Exception {

		String SQLstatement2 = "";
		String[] Array = { "", "", "" };

		SQLstatement2 =
			"select CMRCDT, CMLPDT, CMLAMT "
				+ "from "
				+ datalib
				+ ".CUSMASFL "
				+ "where CMCMP='"
				+ company
				+ "' and CMCUS#="
				+ customer_num;

		try {		// RI008
			
			stmt2 = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

			result2 = stmt2.executeQuery(SQLstatement2);

			if (result2.next()) {

				if (result2.getString("CMRCDT").length() == 8)
					Array[0] =
						result2.getString("CMRCDT").substring(4, 6)
							+ "/"
							+ result2.getString("CMRCDT").substring(6, 8)
							+ "/"
							+ result2.getString("CMRCDT").substring(2, 4);
							
				if (result2.getString("CMLPDT").length() == 8)
					Array[1] =
						result2.getString("CMLPDT").substring(4, 6)
							+ "/"
							+ result2.getString("CMLPDT").substring(6, 8)
							+ "/"
							+ result2.getString("CMLPDT").substring(2, 4);

				if (Double.valueOf(result2.getString("CMLAMT")).doubleValue() != 0)
					Array[2] = "$" + result2.getString("CMLAMT");

			}
			
		} catch (SQLException sqle) {				// RI008
			System.err.println(sqle.getMessage());	// RI008
		} catch (Exception e) {					// RI008
			System.err.println(e.getMessage());		// RI008
		} finally {								// RI008
			endcurrentResultset(result2);			// RI008
			endcurrentStatement(stmt2);				// RI008
		}											// RI008
					
		return Array;
	}

	// *****************************************************
	// Perform cleanup
	// *****************************************************
	public void cleanup() throws Exception {
		result.close();		// RI008
		stmt.close();
	}

}