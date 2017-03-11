// *****************************************************************************************************************
// Copyright (C) 2005 The Hertz Corporation, All Rights Reserved.  Unpublished.       
//                                                                                              
// The information contained herein is confidential and proprietary to the Hertz Corporation
// and may not be duplicated, disclosed to third parties, or used for any purpose not       
// expressly authorized by it.                                                              
// Any unauthorized use, duplication, or disclosure is prohibited by law.                   
// ****************************************************************************************************************
// Description:  Year filter options
// Date:         12-14-2005     
// Developer:    Vrushali Bhavsar
// ****************************************************************************************************************
//   MODIFICATION INDEX 
//   
//   Index    User Id       Date      Project     Desciption
//   -----   ----------   --------   -----------  -----------------------------------------------------------------
//	 RI001    DTC2073     12/14/05    SR35880     Add comments for re-rent items and year filter 
//	 RI002    DTC2073     01/19/06    SR35879     Display Job Name And Add year filter.
// ****************************************************************************************************************

package etrieveweb.etrievebean;

import java.util.Calendar;

public class yearOptionsBean implements java.io.Serializable {
 
	// ******************************************
	// Create selections based on full years
	// ******************************************
	public String getFilterOptions(String curYear)
		throws Exception {

		Calendar todayDate = Calendar.getInstance();
		
		int yearVal = todayDate.get(Calendar.YEAR) ;
		int totalYears = 5;   // RI002
		
		String str_yearOptions = "";

		if (curYear.equals(""))
			curYear = Integer.toString(yearVal);
	
		try {

			str_yearOptions = str_yearOptions + "<Select name=\"selectYear\"  onchange=\"submit();\">";
				
			// ***************************
			// Build the selection list
			// ***************************

			for (int column = 0; column < totalYears ; column++) {
					
				str_yearOptions = str_yearOptions + "<OPTION value=\"" + yearVal + "\"";

				if (yearVal == Integer.valueOf(curYear).intValue() )
					str_yearOptions = str_yearOptions.trim() + " selected ";

				str_yearOptions = str_yearOptions + ">" + yearVal + "</OPTION>";
						
				yearVal = yearVal - 1;
					
			}

			str_yearOptions = str_yearOptions.trim() + "</SELECT>";
				
			return str_yearOptions;

		} catch (Exception e) {
			System.out.println(e);
			return " ";
		}
	}
	

	// **************************************************
	// Create customized selections based on web page 
	// **************************************************
	
	public String getFilterOptions(String curYear,String webPageName)
		throws Exception {


		String str_yearOptions = "";
			
		try {

			if( webPageName.equals("closedContracts"))
			{
				Calendar todayDate = Calendar.getInstance();
		
				int yearVal = todayDate.get(Calendar.YEAR) ;
				int totalYears = 5;   
		
				
				if (curYear.equals(""))
					curYear = Integer.toString(yearVal);
					
	
				str_yearOptions = str_yearOptions + "<Select name=\"selectYear\"  onchange=\"submit();\">";
				
				// ***************************
				// Build the selection list
				// ***************************
				
				int isFullYear = 1 ;
				
					if (curYear.equals("Last 6 Months"))
				{
					str_yearOptions = str_yearOptions.trim() + "<OPTION value=\"" + curYear +"\"selected >" + curYear + "</OPTION>" ;
					isFullYear = 0 ;
				}
				else
				{
					str_yearOptions = str_yearOptions.trim() + "<OPTION value=\"Last 6 Months\" >Last 6 Months</OPTION>" ;
				}		


				for (int column = 0; column < totalYears ; column++) {
					
					str_yearOptions = str_yearOptions + "<OPTION value=\"" + yearVal + "\"";

					if (isFullYear == 1 )
					{
						if (yearVal == Integer.valueOf(curYear).intValue() )
							str_yearOptions = str_yearOptions.trim() + " selected ";
					}		

					str_yearOptions = str_yearOptions + ">" + "Full year: " + yearVal + "</OPTION>";
							
					yearVal = yearVal - 1;
					
				}

				str_yearOptions = str_yearOptions.trim() + "</SELECT>";
					
			 }
			 
			 return str_yearOptions;
		
		} 
		catch (Exception e) {
			System.out.println(e);
			return "";
		}
		
	}
	
} 
