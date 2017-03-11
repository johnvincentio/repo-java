// *****************************************************************************************************************
// Copyright (C) 2005 The Hertz Corporation, All Rights Reserved.  Unpublished.       
//                                                                                              
// The information contained herein is confidential and proprietary to the Hertz Corporation
// and may not be duplicated, disclosed to third parties, or used for any purpose not       
// expressly authorized by it.                                                              
// Any unauthorized use, duplication, or disclosure is prohibited by law.                   
// ****************************************************************************************************************
// Description:  Sort Options
// Date:	 08-13-2005     
// Developer:    Vrushali Bhavsar
// *************************************************************************    
// *************************************************************************
//   MODIFICATION INDEX 
//   
//   Index    User Id       Date      Project         Desciption
//   -----   ----------   --------   --------------  --------------------------
//   x001     DTC2073     08/13/04    SR31413 PCR5    Add Sort options. 
//   RI002    DTC9028     07/06/05    SR31413 PCR24   Add additonal sort columns
//   RI003    DTC9028     07/15/05    SR31413 PCR23   Add Closed Rental report
//   RI004    DTC9028     12/23/05    SR35880         Expand the table for specific pages
//   RI004    DTC2073     01/11/06    SR35879	      Job Name Field
// ***************************************************************************************************************

package etrieveweb.etrievebean;

import java.io.*;
import java.util.*;
import java.text.*;

public class sortOptionBean implements java.io.Serializable {
 

	String [] defaultsortFields = {"", "", ""};
	String [] defaultsortSeq = {"", "", ""};

	String [] selectedSortFields = {"", "", ""};
	String [] selectedSortSeq = {"", "", ""};

	String [] columnFields = {"", "", "", "", "", "", "", "", "", ""};
	String [] columnFieldsDes = {"", "", "", "", "", "", "", "", "", ""};
	
	String includeISEQ = "";

	int fieldCount = columnFields.length;
	int sortFields = selectedSortFields.length;


	// ***********************************************************************************
	// Get the sort options table to insert into the jsp page
	// ***********************************************************************************
		
	public String[] getSortOptions(String webPageName, String sortFld0, String sortFld1, String sortFld2, String order0, String order1, String order2) throws Exception {


		String[] returnArray = {"", "", ""};
		
		selectedSortFields[0] = sortFld0;
		selectedSortFields[1] = sortFld1;
		selectedSortFields[2] = sortFld2;
		
		selectedSortSeq[0] = order0;
		selectedSortSeq[1] = order1;
		selectedSortSeq[2] = order2;

		if ( webPageName.equals("resvQuote-rentals") || 
			 webPageName.equals("equipOnRent") || 
			 webPageName.equals("closedContracts") || 		// RI003
			 webPageName.equals("overdueContracts") )
			includeISEQ = "N";
		else
			includeISEQ = "Y";
		
		setPageDefaults(webPageName);
		
		returnArray[0] = getOrderByString(webPageName);
		
		returnArray[1] = getSortTable(webPageName);
		
		returnArray[2] = getHiddenInputFields();

		return returnArray;

	}
	
	// ***********************************************************************************
	// Check the check sort selections and retrieve the available sorts
	// for the page being displayed
	// ***********************************************************************************
	
	public void setPageDefaults(String webPageName) throws Exception {


		if ( webPageName.equals("detailHistory") ) {
			
			String[] pageDefaultSort = {"RDSYSD", "RDCON#", ""};
			String[] pageDefaultSeq = {"DESC", "DESC", ""};
			
			String[] pageColumnFields = {"RDITEM", "RDCON#", "RDSYSD", "RDDATO", "RDDATI", "RDAMT$"};						// RI002
			String[] pageColumnDesc = {"Equip#", "Invoice", "Invoice Date", "Start Date", "Return Date", "Rental Amount"};	// RI002
		
			setSortDefault( pageDefaultSort, pageDefaultSeq, pageColumnFields, pageColumnDesc );

		} else if ( webPageName.equals("closedContracts") ) {		// RI003
			
			String[] pageDefaultSort = {"RHLRDT", "RDCON#", ""};
			String[] pageDefaultSeq = {"DESC", "DESC", ""};
 			
			String[] pageColumnFields = {"RDCON#", "RHDATO", "RHLRDT" , "RDITEM", "ECDESC", "RHORDB", "RHPO#", "CJNAME" }; // RI004
			String[] pageColumnDesc ={"Contract", "Start Date", "Close Date","Equip#", "Description", "Ordered By", "PO #", "Job Name"}; // RI004
				
			setSortDefault( pageDefaultSort, pageDefaultSeq, pageColumnFields, pageColumnDesc );
			
		} else if ( webPageName.equals("equipOnRent") ) {
			
			String[] pageDefaultSort = {"RHDATO", "RDCON#", ""};
 			String[] pageDefaultSeq = {"DESC", "DESC", ""};
 			
			String[] pageColumnFields = {"RDCON#", "RHDATO", "RDITEM", "ECDESC", "RHERDT", "RHORDB", "RHPO#","CJNAME" }; // RI004
			String[] pageColumnDesc ={"Contract", "Start Date", "Equip#", "Description", "Est Return Date", "Ordered By", "PO #", "Job Name"}; //RI004};
				
			setSortDefault( pageDefaultSort, pageDefaultSeq, pageColumnFields, pageColumnDesc );
			

		} else if ( webPageName.equals("overdueContracts") ) {
			
			String[] pageDefaultSort = {"RHERDT", "RDCON#", ""};
			String[] pageDefaultSeq = {"ASC", "ASC", ""};
 			
			String[] pageColumnFields = {"RDCON#", "RHDATO", "RDITEM", "ECDESC", "RHERDT", "RHORDB", "RHPO#", "CJNAME"}; // RI004
			String[] pageColumnDesc ={"Contract", "Start Date", "Equip#", "Description", "Est Return Date", "Ordered By", "PO #", "Job Name"}; // RI004
				
			setSortDefault( pageDefaultSort, pageDefaultSeq, pageColumnFields, pageColumnDesc );	
						
		} else if ( webPageName.equals("monthlyBillings") ) {
			
			String[] pageDefaultSort = {"RHDATO", "RHCON#", ""};
 			String[] pageDefaultSeq = {"DESC", "DESC", ""};
 			
 			String[] pageColumnFields = {"RHCON#", "RHDATO", "RHLRDT", "RHPO#", "RHLOC", "RHAMT$"};				// RI002
 			String[] pageColumnDesc = {"Contract", "Start Date", "Return Date", "PO #", "Location", "Amount"};	// RI002

			setSortDefault( pageDefaultSort, pageDefaultSeq, pageColumnFields, pageColumnDesc );
			
			
		} else if ( webPageName.equals("openInvoice") ) {
			
			String[] pageDefaultSort = {"AHINVD", "AHINV#", ""};
			String[] pageDefaultSeq = {"DESC", "ASC", ""};
			
			String[] pageColumnFields = {"AHINV#", "AHINVD", "AHLOC", "AHAMT$", "AHCBAL", "RHPO#" };				// RI002
			String[] pageColumnDesc = {"Invoice #", "Invoice Date", "Location", "Original", "Balance", "PO #"};		// RI002
			
			setSortDefault( pageDefaultSort, pageDefaultSeq, pageColumnFields, pageColumnDesc );
			
			
		} else if ( webPageName.equals("openSales") ) {
			
			String [] pageDefaultSort = {"RHCON#", "", ""};
			String [] pageDefaultSeq = {"DESC", "", ""};
			
			String [] pageColumnFields ={"RHCON#", "RHDATO", "RHPO#", "RHLOC", "RHJOBL", "RHAMT$"};		// RI002
			String [] pageColumnDesc ={"Contract", "Order Date", "PO #", "Location", "Job", "Amount"};	// RI002

			setSortDefault( pageDefaultSort, pageDefaultSeq, pageColumnFields, pageColumnDesc );
			
			
		} else if ( webPageName.equals("paidInvoice") ) {
			
			String [] pageDefaultSort ={"AHINVD", "AHINV#", ""};
			String [] pageDefaultSeq = {"DESC", "ASC", ""};
			
			String [] pageColumnFields = {"AHINV#", "AHINVD", "AHLOC", "AHAMT$", "RHPO#"};				// RI002
			String [] pageColumnDesc = {"Invoice #", "Invoice Date", "Location", "Original", "PO #"};	// RI002
						
			setSortDefault( pageDefaultSort, pageDefaultSeq, pageColumnFields, pageColumnDesc );
			
			
		} else if ( webPageName.equals("payCheckTotal") ) {
			
			String [] pageDefaultSort = {"ADSYSD", "ADPID#", ""};
			String [] pageDefaultSeq = {"DESC", "ASC", ""};
			
			String [] pageColumnFields = {"ADSYSD", "ADPID#", "SubTotal"};		// RI002
			String [] pageColumnDesc = {"Pay Date", "Check#", "Total"};			// RI002

			setSortDefault( pageDefaultSort, pageDefaultSeq, pageColumnFields, pageColumnDesc );
			
			
 		} else if ( webPageName.equals("paymentHist") ) {
 			
			String [] pageDefaultSort = {"ADSYSD", "ADPID#", "ADINV#"};
			String [] pageDefaultSeq = {"DESC", "DESC", "DESC"};
			
			String [] pageColumnFields = {"ADSYSD", "ADPID#", "Amount", "ADINV#", "AHINVD","ADLOC"};				// RI002
			String [] pageColumnDesc = {"Pay Date", "Check#", "Amount", "Invoice #", "Invoice Date", "Location"};	// RI002
			
			setSortDefault( pageDefaultSort, pageDefaultSeq, pageColumnFields, pageColumnDesc );

			
 		} else if ( webPageName.equals("rentalHistory") ) {
 			
			String [] pageDefaultSort = {"RDCATG, RDCLAS", "", "", "", ""};
			String [] pageDefaultSeq = {"ASC", "", "", "", ""};
			
			String [] pageColumnFields = {"RDCATG, RDCLAS", "ECDESC", "totalrentdays", "totalTransactions", "totalrentamount" };	// RI002
			String [] pageColumnDesc = {"Cat-Class", "Description", "Rental Days", "# of Trans", "Rental Amount"};					// RI002

			setSortDefault( pageDefaultSort, pageDefaultSeq, pageColumnFields, pageColumnDesc );
			
			
		} else if ( webPageName.equals("resvQuote-rentals") ) {
 			
			String [] pageDefaultSort = {"RHDATO", "RHCON#", ""};
			String [] pageDefaultSeq = {"DESC", "DESC", ""};
			
			String [] pageColumnFields = {"RHCON#", "RHDATO", "RHERDT", "RHPO#", "RHLOC", "RHORDB"};
			String [] pageColumnDesc = {"Contract", "Start Date", "Return Date", "PO #", "Location", "Ordered By"};

			setSortDefault( pageDefaultSort, pageDefaultSeq, pageColumnFields, pageColumnDesc );


		} else if ( webPageName.equals("resvQuote-sales") ) {
 			
			String [] pageDefaultSort = {"RHDATO", "RHCON#", ""};
			String [] pageDefaultSeq = {"DESC", "DESC", ""};
			
			String [] pageColumnFields = {"RHCON#", "RHDATO", "RHPO#", "RHLOC", "RHORDB"}; 
			String [] pageColumnDesc = {"Contract", "Start Date", "PO #", "Location", "Ordered By"};
			
			setSortDefault( pageDefaultSort, pageDefaultSeq, pageColumnFields, pageColumnDesc );


		} else if ( webPageName.equals("salesInvoices") ) {
 			
			String [] pageDefaultSort = {"RHSYSD", "RHCON#", ""};
			String [] pageDefaultSeq = {"DESC", "DESC", ""};
			
			String [] pageColumnFields = {"RHCON#", "RHSYSD", "RHPO#", "RHLOC", "RHAMT$"};			// RI002
			String [] pageColumnDesc = {"Contract", "Invoice Date", "PO #", "Location", "Amount"};	// RI002
			
			setSortDefault( pageDefaultSort, pageDefaultSeq, pageColumnFields, pageColumnDesc );
			
		}
		
	}
	
	// ***********************************************************************************
	// Set the sort default for the page
	// ***********************************************************************************
	
	private void setSortDefault( String[] pageDefaultSort, String[] pageDefaultSeq, String[] pageColumnFields, String [] pageColumnDesc ) throws Exception {


		// Check for valid selections, in not valid then default to blanks
		
		for (int i = 0; i < sortFields; i++) {
			if ( selectedSortFields[i] == null ||  selectedSortFields[i].equals("(none)") ) {
				selectedSortFields[i] = "";
				selectedSortSeq[i] = "";
			}
			if ( selectedSortSeq[i] == null ) {
				selectedSortSeq[i] = "";
			}
		}


		// Initialize the default sort array 
		
		for (int i = 0; i < sortFields; i++) {
			defaultsortFields[i] = pageDefaultSort[i];
			defaultsortSeq[i] = pageDefaultSeq[i];
		}


		// Initialize the column fields and headings array

		int pageCoulumnCount = pageColumnFields.length;
	
		for (int i = 0; i < pageCoulumnCount; i++) {
			if ( pageColumnFields[i] == null ) {
				pageColumnFields[i] = "";
				pageColumnDesc[i] = "";
			}
			columnFields[i] = pageColumnFields[i];
			columnFieldsDes[i] = pageColumnDesc[i];
		}

		// If no selection were detected then use the default sort
		
		if ( selectedSortFields[0].trim().equals("") && selectedSortFields[1].trim().equals("") && selectedSortFields[2].trim().equals("")) {
			
			selectedSortFields[0] = defaultsortFields[0];
			selectedSortFields[1] = defaultsortFields[1];
			selectedSortFields[2] = defaultsortFields[2];
			selectedSortSeq[0] = defaultsortSeq[0];
			selectedSortSeq[1] = defaultsortSeq[1];
			selectedSortSeq[2] = defaultsortSeq[2];
		}
		
		// Shift selected entries to the left if blanks are found
		
		if ( selectedSortFields[0].trim().equals("") || selectedSortFields[1].trim().equals("") || selectedSortFields[2].trim().equals("")) {

			String [] tempsortFields = {"", "", ""};
			String [] tempsortSeq = {"", "", ""};
			
			int t = 0;
			
			for (int i = 0; i < sortFields; i++) {
				
				if ( !selectedSortFields[i].trim().equals("") ) {
					tempsortFields[t] = selectedSortFields[i];
					tempsortSeq[t] = selectedSortSeq[i];
					t++;
				}
			}
			
			selectedSortFields[0] = tempsortFields[0];
			selectedSortFields[1] = tempsortFields[1];
			selectedSortFields[2] = tempsortFields[2];
			selectedSortSeq[0] = tempsortSeq[0];
			selectedSortSeq[1] = tempsortSeq[1];
			selectedSortSeq[2] = tempsortSeq[2];
		}
		
			
	}

	
	// ***********************************************************************************
	// Build the sort selection table
	// ***********************************************************************************
	
	public String getSortTable(String webPageName)
		throws Exception {

		String str_TableFmt = new String();
		String tableWidth = "640";
		
		if ( webPageName.trim().equals("resvQuote-sales") )
			tableWidth = "660";
		else if ( webPageName.trim().equals("monthlyBillings") )
			tableWidth = "655";
		else if ( webPageName.trim().equals("equipOnRent") )		// RI004 
			tableWidth = "890";										// RI004
		else if ( webPageName.trim().equals("detailHistory") )		// RI004
			tableWidth = "890";										// RI004
		else if ( webPageName.trim().equals("overdueContracts") )	// RI004 
			tableWidth = "890";										// RI004
		else if ( webPageName.trim().equals("closedContracts") )	// RI004
			tableWidth = "890";										// RI004
		else if ( webPageName.trim().equals("rentalHistory") )		// RI004 
			tableWidth = "890";										// RI004


		try {

			str_TableFmt = " <TABLE border=\"0\" width=\"" + tableWidth + "\" cellspacing=\"0\" cellpadding=\"3\"> ";
			
			str_TableFmt = str_TableFmt + "<TR bgcolor=\"#cccccc\">";

			// **************************************************************************************************
			// Create three cells with the available sort fields with the associated label cell (i.e. Sort by).   
			// Each sort field cell will contain a selection list containing all the column names available to 
			// sort and two radio buttons to specify whether to sort in ascending or descending order.
			// **************************************************************************************************

			for (int cellNum = 0; cellNum < sortFields; cellNum++) {

				if (cellNum == 0)
					str_TableFmt = str_TableFmt + "<TD width=\"50\" valign=\"top\">Sort by</TD> ";
				else
					str_TableFmt = str_TableFmt + "<TD width=\"50\" valign=\"top\">Then by</TD> ";

				str_TableFmt = str_TableFmt + "<TD width=\"130\" align=\"left\" valign=\"top\" class=\"tabledata\">";
				
				str_TableFmt = str_TableFmt + "<Select name=\"sortFld" + cellNum + "\">";
				
				str_TableFmt = str_TableFmt + "<OPTION value= \"(none)\"></OPTION>";

				// ***************************
				// Build the selection list
				// ***************************

				for (int column = 0; column < fieldCount; column++) {
					
					if ( !columnFields[column].trim().equals("") )  {
						
						str_TableFmt = str_TableFmt + "<OPTION value=\"" + columnFields[column].trim() + "\"";

						if (selectedSortFields[cellNum].trim().equals(columnFields[column]))
							str_TableFmt = str_TableFmt.trim() + " selected";

						str_TableFmt = str_TableFmt + ">" + columnFieldsDes[column].trim() + "</OPTION>";
					}
						
				}

				str_TableFmt = str_TableFmt.trim() + "</SELECT><br>";

				// **************************
				// Build the radio buttons
				// **************************

				boolean str_checked = false;

				for (int radioField = 0; radioField < 2; radioField++) {

					str_TableFmt = str_TableFmt.trim() + "<INPUT type=\"radio\" name=\"order" + cellNum + "\" value=\"";

					if (radioField == 0) {

						str_TableFmt = str_TableFmt.trim() + "ASC\"";

						if (!selectedSortFields[cellNum].trim().equals("")) {
							if (!selectedSortSeq[cellNum].trim().equals("DESC")) {
								str_TableFmt = str_TableFmt.trim() + " checked ";
								str_checked = true;
							}
						}

						str_TableFmt = str_TableFmt + ">Ascending";

					} else {

						str_TableFmt = str_TableFmt.trim() + "DESC\"";

						if (!selectedSortFields[cellNum].trim().equals(""))
							if (!selectedSortSeq[cellNum].trim().equals("ASC") && !str_checked) {
								str_TableFmt = str_TableFmt.trim() + " checked ";
							}

						str_TableFmt = str_TableFmt + ">Descending";
					}
				}

				str_TableFmt = str_TableFmt + "</TD>";

			}

			// *********************************
			// Submit button and end of table 
			// *********************************

			str_TableFmt = str_TableFmt.trim() + "<TD width=\"100\" align=\"center\"><Input TYPE=\"submit\" value = \"Sort Now\" ></TD>";
			str_TableFmt = str_TableFmt.trim() + " </TR></Table>   ";

			return str_TableFmt;

		} catch (Exception e) {
			System.out.println(e);
			return " ";
		}
	}

	// ***********************************************************************************
	// Format the order by string
	// ***********************************************************************************

	public String getOrderByString(String webPageName)
		throws Exception {

		String str_OrderBy = "";

		for (int cellNum = 0; cellNum < 3; cellNum++) {

			if (!selectedSortFields[cellNum].trim().equals("")) {

				if (!str_OrderBy.trim().equals(""))
					str_OrderBy = str_OrderBy.trim() + ", ";

				// ****************************************************************
				// Determine if sorting by catg/class in the rentalHistory page
				// If so, override the logic
				// ****************************************************************
				
				if ( webPageName.equals("rentalHistory") && selectedSortFields[cellNum].equals("RDCATG, RDCLAS") ) {
				
					if (selectedSortSeq[cellNum].equals("DESC") )
						str_OrderBy = str_OrderBy.trim() + "RDCATG DESC, RDCLAS DESC  ";
					else
						str_OrderBy = str_OrderBy.trim() + "RDCATG, RDCLAS ";
				
				} else {
					
					str_OrderBy = str_OrderBy.trim() + " " + selectedSortFields[cellNum].trim() + " " + selectedSortSeq[cellNum];
				}
				
				// *************************************************************************
				// Determine whether one of the sort fields is contract/invoice number.
				// If so, add the sequence field to the sort
				// *************************************************************************

				if (includeISEQ.equals("Y") && selectedSortFields[cellNum].trim().equals("RHCON#"))
					str_OrderBy = str_OrderBy.trim() + ", RHISEQ " + selectedSortSeq[cellNum];

				if (includeISEQ.equals("Y") && selectedSortFields[cellNum].trim().equals("RDCON#"))
					str_OrderBy = str_OrderBy.trim() + ", RDISEQ " + selectedSortSeq[cellNum];

				if (selectedSortFields[cellNum].trim().equals("AHINV#"))
					str_OrderBy = str_OrderBy.trim() + ", AHISEQ " + selectedSortSeq[cellNum];

				if (selectedSortFields[cellNum].trim().equals("ADINV#"))
					str_OrderBy = str_OrderBy.trim() + ", ADISEQ " + selectedSortSeq[cellNum];

			}
			

		}

		// ******************************************************
		// Add additional sort fields that are not a part of 
		// the page but should still be used
		// ******************************************************
		
		if ( webPageName.trim().equals("paymentHist") )
			str_OrderBy = str_OrderBy.trim()  + ", ADDSEQ";
		
		return str_OrderBy;

	}


	// ***********************************************************************************
	// Format the input hidden fields used in page scrolling 
	// ***********************************************************************************

	public String getHiddenInputFields()
		throws Exception {

		String hiddenInputField = "";

		for (int cellNum = 0; cellNum < 3; cellNum++) {

			hiddenInputField = hiddenInputField.trim() + "<input type=\"hidden\" name=\"sortFld" + cellNum + "\" value=\"" + selectedSortFields[cellNum].trim() + "\">";

			hiddenInputField = hiddenInputField.trim() + "<input type=\"hidden\" name=\"order" + cellNum + "\" value=\"" + selectedSortSeq[cellNum].trim() + "\">";
			
		}
		
		return hiddenInputField;

	}













}
