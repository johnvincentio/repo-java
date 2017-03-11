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
//    Index     User Id        Date       Project           Desciption
//    -------  -----------  -----------  ----------------  ----------------------------------------------------
//    x001      DTC9028      01/30/03     SR26609 PCR1      Add the invoice date, start date and return date
//	  x002     	DTC2073    	 08/19/04     SR31413 PCR5      Add the order by variable to the logic. 
//	  RI003    	DTC9028    	 08/01/05     SR31413 PCR19     Datasource implementation modification 
//    RI004     DTC2073      10/20/05     SR35420           Data Retrieval Limit
//    RI005     DTC2073      11/15/05     SR35880 		    Added filter by year logic  
// ****************************************************************************************************************
package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*;
import java.util.Calendar;	// RI004
import etrieveweb.utility.sqlBean;

public class eqdetailhistoryBean
    extends sqlBean
    implements java.io.Serializable {

	ResultSet result = null, result2 = null;
	Statement stmt = null, stmt2 = null;

	int cutOffDtVal = 0 ; // RI004

	public eqdetailhistoryBean() {
		super();
	}

	public synchronized boolean getRows(
		String customer_list,
		int customer_num,
		String company,
		String datalib,
		String str_category,
		String str_class,
		String str_jobnumber,
		int start_num,
		String str_orderBy,
		String selectedYear)	// RI005
		throws Exception {

		String SQLstatement = "";
		
		// ** Note: The previous hard-coded order by was RDSYSD desc, RDCON# desc, RDISEQ desc

		if ( !str_orderBy.trim().equals("") )
			str_orderBy = " order by " + str_orderBy;
			
			
        if (str_jobnumber.equals("") )

            SQLstatement =
                "select RDITEM, RDCON#, RDISEQ, RDSEQ#, RDDYRT, RDWKRT, RDMORT, RDRATU ,RDAMT$, RDSYSD, RDDATO, RDDATI "
                    + "from "
                    + datalib
                    + ".RACDETF3 "
                    + " where RDCMP='"
                    + company
                    + "' and RDCATG="
                    + str_category
                    + " and RDCLAS ="
                    + str_class
                    + " and RDSYSD >= " + selectedYear + "0101"		// RI005
                    + " and RDSYSD < " + selectedYear + "1299"		// RI005
                    + " and RDTYPE='RI' and RDCUS# in "
                    + customer_list
                    + " " + str_orderBy;
        else
            SQLstatement =
                "select RDITEM, RDCON#, RDISEQ, RDSEQ#, RDDYRT, RDWKRT, RDMORT, RDRATU ,RDAMT$, RDSYSD, RDDATO, RDDATI "
                    + "from "
                    + datalib
                    + ".RACDETF3 "
                    + " where RDCMP='"
                    + company
                    + "' and RDCATG="
                    + str_category
                    + " and RDCLAS ="
                    + str_class
                    + " and RDSYSD >= " + selectedYear + "0101"		// RI005
                    + " and RDSYSD < " + selectedYear + "1299"		// RI005
                    + " and RDTYPE='RI' and RDCUS# in "
                    + customer_list
                    + " and RDCON# in "
                    + "(select RHCON# from "
                    + datalib
                    + ".RACHDRFL "
                    + "where RHCMP='"
                    + company
                    + "' and RHJOB# ='"
                    + str_jobnumber
                    + "') "
                    + " " + str_orderBy;

        // RI003	stmt = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        
        stmt = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI003
        
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

    public synchronized String getNumRows(
        String customer_list,
        int customer_num,
        String company,
        String datalib,
        String str_category,
        String str_class,
        String str_jobnumber,
        String selectedYear)	// RI005
        throws Exception {

		// **********************************************************************************
		// Calcluate the cust-off date to be current year plus two complete prior years
		// **********************************************************************************
		
		Calendar cutOffDt = Calendar.getInstance();		// RI004
		cutOffDt.add(Calendar.YEAR, -2 );				// RI004
		cutOffDtVal = (cutOffDt.get(Calendar.YEAR)*10000) + 100 + 01;	// RI004
		
        String SQLstatement2 = "";
        String totalNumRows = "0";	// RI003

        if (str_jobnumber.equals(""))
            SQLstatement2 =
                "select count(*) as total_num_rows "
                    + "from "
                    + datalib
                    + ".RACDETF3 "
                    + " where RDCMP='"
                    + company
                    + "' and RDCATG="
                    + str_category
                    + " and RDCLAS ="
                    + str_class
                    + " and RDSYSD >= " + selectedYear + "0101"		// RI005
                    + " and RDSYSD < " + selectedYear + "1299"    	// RI005
                    + " and RDTYPE='RI' and RDCUS# in "
                    + customer_list;
        else
            SQLstatement2 =
                "select count(*) as total_num_rows "
                    + "from "
                    + datalib
                    + ".RACDETF3 "
                    + " where RDCMP='"
                    + company
                    + "' and RDCATG="
                    + str_category
                    + " and RDCLAS ="
                    + str_class
                    + " and RDSYSD >= " + selectedYear + "0101"		// RI005
                    + " and RDSYSD < " + selectedYear + "1299"		// RI005
                    + " and RDTYPE='RI' and RDCUS# in "
                    + customer_list
                    + " and RDCON# in "
                    + "(select RHCON# from "
                    + datalib
                    + ".RACHDRFL "
                    + "where RHCMP='"
                    + company
                    + "' and RHJOB# ='"
                    + str_jobnumber
                    + "')";
        try {

            stmt2 = conn.createStatement();
            
            result2 = stmt2.executeQuery(SQLstatement2);
            
            result2.next();

			totalNumRows = result2.getString("total_num_rows");	// RI003

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            System.err.println(SQLstatement2);
			} finally {								// RI003
				endcurrentResultset(result2);			// RI003
				endcurrentStatement(stmt2);				// RI003
			}											// RI003
        
        // RI003  return result2.getString("total_num_rows");
        
        return totalNumRows;	// RI003
        
    }

    public synchronized String getTotalAmount(
        String customer_list,
        int customer_num,
        String company,
        String datalib,
        String str_category,
        String str_class,
        String str_jobnumber,
        String selectedYear)	// RI005
        throws Exception {

        String SQLstatement2 = "";

        if (str_jobnumber.equals(""))
            SQLstatement2 =
                "select sum(RDAMT$) as total_amount "
                    + "from "
                    + datalib
                    + ".RACDETF3 "
                    + " where RDCMP='"
                    + company
                    + "' and RDCATG="
                    + str_category
                    + " and RDCLAS ="
                    + str_class
                    + " and RDSYSD >= " + selectedYear + "0101"		// RI005
                    + " and RDSYSD < " + selectedYear + "1299"		// RI005
                    + " and RDTYPE='RI' and RDCUS# in "
                    + customer_list;
        else
            SQLstatement2 =
                "select sum(RDAMT$) as total_amount "
                    + "from "
                    + datalib
                    + ".RACDETF3 "
                    + " where RDCMP='"
                    + company
                    + "' and RDCATG="
                    + str_category
                    + " and RDCLAS ="
                    + str_class
                    + " and RDSYSD >= " + selectedYear + "0101"		// RI005
                    + " and RDSYSD < " + selectedYear + "1299"		// RI005
                    + " and RDTYPE='RI' and RDCUS# in "
                    + customer_list
                    + " and RDCON# in "
                    + "(select RHCON# from "
                    + datalib
                    + ".RACHDRFL "
                    + "where RHCMP='"
                    + company
                    + "' and RHJOB# ='"
                    + str_jobnumber
                    + "')";

        stmt2 = conn.createStatement();
        
        result2 = stmt2.executeQuery(SQLstatement2);
        
        result2.next();
        
        String total_amount = result2.getString("total_amount");
        
        if (total_amount == null)
        	total_amount = "0";
        	
        return total_amount;
        
    }


	//*******************************************
	// RI005 - Get line item comments
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

    public void cleanup() throws Exception {
		endcurrentResultset(result);	// RI003
		endcurrentResultset(result2);	// RI003
		endcurrentStatement(stmt);		// RI003
		endcurrentStatement(stmt2);		// RI003
    }

}