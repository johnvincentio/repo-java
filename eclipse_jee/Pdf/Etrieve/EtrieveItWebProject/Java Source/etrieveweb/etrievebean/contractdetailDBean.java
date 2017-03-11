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
//    Index    User Id        Date      Project           Desciption
//    ------  -----------  ----------  ----------------  --------------------------------------------------------
//    x001     DTC9028      12/01/03    SR26609 PCR1      Removed the selection of RDTYPE equal "XD" or "YD"  
//    x002     DTC9028      01/14/03    SR26609 PCR1      Added fields RDMICG and RDEXM$  
//    x003     DTC9028      01/14/03    SR26609 PCR1      Added getEquipmentInfo method
//    x004     DTC9028      01/27/03    SR26609 PCR1      Removed the join to RAPKUPFL file in the getRows method
//    x005     DTC9028      01/27/03    SR26609 PCR1      Added a new method to query RAPKUPFL file
//    x006     DTC9028      01/28/03    SR26609 PCR1      Added a "VS" to the where clause
//    x007     DTC9028      02/12/03    SR26609 PCR1      Added the transaction type parameter to the getRows method
//    x008     DTC9028      02/12/03    SR26609 PCR1      Added the RDTYPE to the SQL
//    x009     DTC9028      03/22/04    SR28586 PCR25     Changed RSOQTY to RSSQTY for invoiced sales orders 
//    x010     DTC9028      01/12/05    TT404162          Corrected qty error for bulk items   
//    RI011    DTC9028      08/01/05    SR31413 PCR19     Datasource implementation modification
//    RI012    DTC2073      11/15/05    SR35880 		  Added a method to retrieve order comments  
//    RI013    DTC2073      01/31/06    SR36721 		  Add shop supply fee to WO page.
//    RI014    DTC2073      02/09/06    SR35873           Abbr. Equipment Release
// ****************************************************************************************************************

package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*; 
import etrieveweb.utility.sqlBean;

public class contractdetailDBean extends sqlBean implements java.io.Serializable{

	ResultSet result=null, result2=null, result3=null;
	Statement stmt=null, stmt2=null, stmt3=null;


	public contractdetailDBean() {
		super();
	}
 

 
	public synchronized boolean getRows(int customer_num, String company, String datalib, int contract_num, int sequence_num, String type, String itemtype, String str_transtype, String str_ordertype) throws Exception { 

		// *****************************************************************************************************************
		//  type = the RHTYPE field from RAOHDRFL or RACHDRFL
		//  itemtype = which section is being populated the "equipment" or "sales"
		//  transtype = "sales" when coming from Open Sales Orders, Sales Reservation-Quote, Closed Sales invoices????
		// *****************************************************************************************************************
	
		String SQLstatement = "";

		if (type.equals("S") || type.equals("I") || type.equals("W") || ( (  type.equals("G") || ( type.equals("X") && str_transtype.equals("sales") ) )  && itemtype.equals("sales")  ) ) {

			// **********************************************************************************************************************
			// SALES detail...
			// This section simulates the RentalMan display file format RACNTICC/RACNTICS in program RACNTI
			// 		S = Sales invoices	I = Inter-company Expense	W = Work Order	G = Rental purchase		X = Quote (sales only)
			// **********************************************************************************************************************

			if ( sequence_num == 0 )  
		
				SQLstatement = 
					"select RSITEM as RDITEM, RSISEQ as RDSEQ#, RSTYPE as RDTYPE, 0 as RDCATG, 0 as RDCLAS, RSSTKC as RDSTKC, RSOQTY as RDQTY, RSPRCE as RDPRCE, RSSTTS as RDSTTS, RSUNIT as RDUNIT, 0 as RDMHCD, 0 as RDMHO, 0 as RDMHI, 0 as MHTOTAL, 0 as RDMICG, 0 as RDEXM$, 0 as RDRSTK, ( ( RSPRCE*( 1-RSDSCP ) ) * RSOQTY  ) as SOLDPRICE,  0 as RDAMT$,  0 as RDDYRT,  0 as RDWKRT,  0 as RDMORT, 0 as RDRATU, RSEMPC  " + 
 		     		"from " + datalib + ".RASDETF3 " +  
 		     		" where RSCMP='" + company + "' and RSCON#=" + contract_num;  
 		     	
			else  
		
				SQLstatement = 
					"select RSITEM as RDITEM, RSISEQ as RDSEQ#, RSTYPE as RDTYPE, 0 as RDCATG, 0 as RDCLAS, RSSTKC as RDSTKC, RSSQTY as RDQTY, RSPRCE as RDPRCE, RSSTTS as RDSTTS, RSUNIT as RDUNIT, 0 as RDMHCD, 0 as RDMHO, 0 as RDMHI, 0 as MHTOTAL, 0 as RDMICG, 0 as RDEXM$, 0 as RDRSTK, ( ( RSPRCE*(1-RSDSCP) ) * RSSQTY ) as SOLDPRICE, 0 as RDAMT$,  0 as RDDYRT,  0 as RDWKRT,  0 as RDMORT, 0 as RDRATU, RSEMPC " + 
 		     		"from " + datalib + ".RASDETF3 " +  
 		     		" where RSCMP='" + company + "' and RSCON#=" + contract_num + " and RSISEQ=" + sequence_num;


		} else if (sequence_num == 0 && !type.equals("Y") &&  !type.equals("T")  ) {

			// **********************************************************************************************************************
			//  OPEN contracts, reservations, quotes details...
			//  This section simulates the RentalMan display file format RACNTIC1/RACNTIS1 from program RACNTI
			// **********************************************************************************************************************

			if ( itemtype.equals("sales") ) 
		
				SQLstatement = 
					"select RDITEM, RDSEQ#, RDTYPE, RDCATG, RDCLAS, RDSTKC, (RDQTY-RDQTYR) as RDQTY, RDPRCE, RDSTTS, RDUNIT, RDMHCD, RDMHO, RDMHI, 0 as MHTOTAL, RDMICG, RDEXM$, RDRSTK, ( (RDPRCE * (1-RDDSCP)) * (RDQTY-RDQTYR) ) as soldprice, RDDYRT, RDWKRT, RDMORT, RDRATU  " + 
		     		"from " + datalib + ".RAODETFL " +  
		     		" where RDCMP='" + company + "' and RDCON#=" + contract_num + " and RDTYPE in ('XC',  'YC',  'SI', 'VS')";

			else 
		
				SQLstatement = 
					"select RDITEM, RDSEQ#, RDTYPE, RDCATG, RDCLAS, RDSTKC, (RDQTY-RDQTYR) as RDQTY, RDPRCE, RDSTTS, RDUNIT, RDMHCD, RDMHO, RDMHI, (RDMHI-RDMHO) as MHTOTAL, RDMICG, RDEXM$, RDRSTK,( (RDPRCE * (1-RDDSCP)) * (RDQTY-RDQTYR) ) as soldprice, RDDYRT, RDWKRT, RDMORT, RDRATU  " + 
					"from " + datalib + ".RAODETFL " +
		     		" where RDCMP='" + company + "' and RDCON#=" + contract_num + " and RDTYPE not in ('XC', 'XD', 'YC', 'YD', 'SI', 'VS')";

		} else {

			// ******************************************************************************************************************
			// Invoices details
			// 		NOTE:  The quantity for the equipment section will be based on the RHTYPE and RHOTYP
			// ******************************************************************************************************************
		
		
			if ( itemtype.equals("sales") ) {
		
				SQLstatement = 
					"select RDITEM, RDSEQ#, RDTYPE, RDCATG, RDCLAS, RDSTKC, (RDQTY-RDQTYR) as RDQTY, RDPRCE, RDSTTS, RDUNIT, RDMHCD, RDMHO, RDMHI, 0 as MHTOTAL, RDMICG, RDEXM$, RDRSTK, RDAMT$, ( (RDPRCE * (1-RDDSCP) ) * (RDQTY-RDQTYR) ) as soldprice, RDDYRT, RDWKRT, RDMORT, RDRATU " +
		     		"from " + datalib + ".RACDETFL " +  
		     		" where RDCMP='" + company + "' and RDCON#=" + contract_num + " and RDISEQ =" + sequence_num + " and RDTYPE in ('XC',  'YC',  'SI', 'VS')";

			} else {

				String variable_SQLfields = "";

				if ( ( type.equals("O") && str_ordertype.equals("C") ) || type.equals("C") || type.equals("Q") || type.equals("Y") || type.equals("T") || type.equals("F") || type.equals("G") )
					variable_SQLfields = " (RDQTY - RDQTYR) as RDQTY, ( (RDPRCE * (1 - RDDSCP) ) * (RDQTY - RDQTYR) ) as soldprice ";
				else
					variable_SQLfields = " RDQTYR as RDQTY, ( (RDPRCE * (1-RDDSCP) ) * RDQTYR ) as soldprice ";


				SQLstatement = 
					"select RDITEM, RDSEQ#, RDTYPE, RDCATG, RDCLAS, RDSTKC, RDPRCE, RDSTTS, RDUNIT, RDMHCD, RDMHO, RDMHI, (RDMHI-RDMHO) as MHTOTAL, RDMICG, RDEXM$, RDRSTK, RDAMT$, RDDYRT, RDWKRT, RDMORT, RDRATU, " + variable_SQLfields +
		     		"from " + datalib + ".RACDETFL " +  
		     		" where RDCMP='" + company + "' and RDCON#=" + contract_num + " and RDISEQ =" + sequence_num + " and RDTYPE not in ('XC', 'XD', 'YC', 'YD', 'SI', 'VS')";

			}
		
		} 
  

		try {
		
			// RI011	stmt=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
			stmt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI011
			
			result=stmt.executeQuery(SQLstatement);
		
			return (result != null);
		
		} catch (SQLException e) {
		
			System.err.println(e.getMessage());
		
        	System.out.println("The record does not exist!");
        
     	   return false;
		}

	}



	public synchronized String getDescription(String company, String datalib, String equipitem, int catg_num, int class_num, String stock) throws Exception { 

		String SQLstatement2 = "";
  
		if ( catg_num != 0 || class_num != 0 ) {

			SQLstatement2 = 
				"select ECDESC as description, ECBULK  " +    // RI014 
		    	"from " + datalib + ".EQPCCMFL " +  
		    	"where ECCMP='" + company + "' and ECCATG=" + catg_num + " and ECCLAS =" + class_num;

			try {

				// RI011	stmt2=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
				stmt2=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI011
				
				result2=stmt2.executeQuery(SQLstatement2);
			
			} catch (SQLException e) {
			
        		System.err.println(e.getMessage());
        	
        		System.out.println("Description not available");
        	
        		return "";
	       }

		}  else {

			SQLstatement2 = 
				"select IMDESC as description " + 
		    	"from " + datalib + ".ITMMASFL " +  
		    	"where IMCMP='" + company + "' and IMITEM ='" + equipitem + "' and IMSTKC = '" + stock + "'";

			try {

				// RI011	stmt2=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
				stmt2=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI011
				
				result2=stmt2.executeQuery(SQLstatement2);
			
			} catch (SQLException e) {
			
	        	System.err.println(e.getMessage());
        
    	    	System.out.println("Description not available");
        
        		return "Not Found";
			}

		}

		if ( result2.next() )
			return result2.getString("description");
		else 
			return "Description not available";

	}


	//*******************************************
	// RI014 - Get bulk item flag from result2
	//******************************************
			
	public String getColumn2(String colName) throws Exception { 
		
			try {
		
				return	result2.getString(colName);
	
			} catch (SQLException e) {
			
	        	System.err.println(e.getMessage());
         
        		return "N";
			}

	}


	//*******************************************
	// RI012 - Get line item comments
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
				"  and OCISEQ=  " +  seqno +
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



	public synchronized String[] getEquipmentInfo(String company, String datalib, String equipitem, int catg_num, int class_num) throws Exception { 

		String SQLstatement3 = "";
		String[] Array = { "", "", ""};

		if ( catg_num != 0 || class_num != 0 ) {

			SQLstatement3 = 
				"select EMMAKE, EMMODL, EMSER# " + 
		    	"from " + datalib + ".EQPMASFL " +  
		    	"where EMCMP='" + company.trim() + "' and EMEQP#='" + equipitem.trim() + "'";

			try {

				// RI011	stmt3=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

				stmt3=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI001
				
				result3=stmt3.executeQuery(SQLstatement3);
				
				if ( result3.next() )   {
					Array[0] = result3.getString("EMMAKE");
					Array[1] = result3.getString("EMMODL");
					Array[2] = result3.getString("EMSER#");
				}
				
			} catch (SQLException e) {
				System.err.println(e.getMessage());
				return Array;
			} finally {								// RI011
				endcurrentResultset(result3);			// RI011
				endcurrentStatement(stmt3);				// RI011
			}											// RI011

		}

		return Array;

	}
    


	public synchronized String[] getFreeMiles(String company, String datalib, int contract_num, int sequence_num, int catg_num, int class_num) throws Exception { 

		String SQLstatement3 = "";
		String[] Array = { "", "", ""};

		if ( catg_num != 0 || class_num != 0 ) {

			SQLstatement3 = 
				"select ADDYFMIL, ADWKFMIL, ADMOFMIL " + 
		    	"from " + datalib + ".RAOADTFL " +  
		    	"where ADCMP='" + company.trim() + "' and ADCON#=" + contract_num +  " and ADTYPE = 'RI' and ADSEQ#=" + sequence_num;

			try {

				// RI011	stmt3=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

				stmt3=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI011
				
				result3=stmt3.executeQuery(SQLstatement3);
          
				if ( result3.next() )   {
					Array[0] = result3.getString("ADDYFMIL");
					Array[1] = result3.getString("ADWKFMIL");
					Array[2] = result3.getString("ADMOFMIL");
				}
				
			} catch (SQLException e) {
				System.err.println(e.getMessage());
				return Array;
			} finally {								// RI011
				endcurrentResultset(result3);			// RI011
				endcurrentStatement(stmt3);				// RI011
			}											// RI011

		}

		return Array;

	}



	public synchronized String getPickupStatus(String company, String datalib, int contract_num, int sequence_num, String equipitem) throws Exception { 

		String SQLstatement3 = "";
		String str_pickupStatus = "";

		SQLstatement3 = 
			"select distinct RUSTTS " + 
			"from " + datalib + ".RAPKUPFL " +  
			"where RUCMP='" + company.trim() + "' and RUCON#=" + contract_num +  " and RUSTTS = 'OP' and RUEQP#='" + equipitem.trim() + "' and RUSEQ#=" + sequence_num;

		try {
          
			// RI011	stmt3=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
          	
          	stmt3=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI011
          	
			result3=stmt3.executeQuery(SQLstatement3);
          
			if ( result3.next() )   {
				str_pickupStatus = result3.getString("RUSTTS");
				return str_pickupStatus;
			}
			
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			return str_pickupStatus;
		} finally {								// RI011
			endcurrentResultset(result3);			// RI011
			endcurrentStatement(stmt3);				// RI011
		}											// RI011

		return str_pickupStatus;

	}




	public boolean getNext() throws Exception {
		return result.next();
	}


	public String getColumn(String colNum) throws Exception {
		return result.getString(colNum);
	}


	public void cleanup() throws Exception {
		endcurrentResultset(result);	// RI011
		endcurrentResultset(result2);	// RI011
		endcurrentResultset(result3);	// RI011
		endcurrentStatement(stmt);		// RI011
		endcurrentStatement(stmt2);		// RI011
		endcurrentStatement(stmt3);		// RI011
	}

}