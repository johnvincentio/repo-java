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
//    Index     User Id       Date      Project            Desciption
//    -------  ----------  ----------  -----------------  ----------------------------------------------------------
//    x001      DTC9028     11/26/02    SR26609 PCR1       Select record that have balance <= 0 and payments <> 0
//    x002      DTC9028     03/29/04    SR31413 PCR18      Changed ORDER BY in the getRows method to be AHINVD desc
//	  x003     	DTC2073    	08/19/04    SR31413 PCR5       Add the order by variable to the logic. 
//    RI004     DTC9028     07/28/05    SR31413 PCR19      Implement datasource modification
//    RI005     DTC2073     11/02/05    SR35420            Data Retrieval Limit
// ***************************************************************************************************************
package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*;
import java.util.Calendar;		// RI005
import etrieveweb.utility.sqlBean;

public class paidinvoiceBean extends sqlBean implements java.io.Serializable {

	ResultSet result = null, result2 = null;
	Statement stmt = null, stmt2 = null;

	int cutOffDtVal = 0 ;  // RI005

	public paidinvoiceBean() {
		super();
	}

	public synchronized boolean getRows(
        String customer_list,
        int customer_num,
        String company,
        String datalib,
        int start_num,
		String str_orderBy)
        throws Exception {

        // ** Note: The previous hard-coded order by was AHCMP, AHCUS#, AHINVD desc, AHINV#, AHISEQ

		if ( !str_orderBy.trim().equals("") )
			str_orderBy = " order by " + str_orderBy;

        String SQLstatement =
        "SELECT A.*, B.*, (AHAMT$-AHCBAL) as CurrBalance "
            + " FROM "
            + datalib
            + ".arihdrf2 A "
            + " LEFT OUTER JOIN "
            + datalib
            + ".rachdrfl B ON AHCMP=RHCMP AND ahcus# = rhcus# and ahinv# = rhcon# and ahiseq = rhiseq"
            + " WHERE (ahcmp = '"
            + company
            + "' AND ahcus# in "
            + customer_list
            + ") " 
            + " and AHDUED >= " + cutOffDtVal    // RI005
            + " and AHSTTS = 'PD' " 
            + " and AHCBAL <= 0 and AHTPAY <> 0 "
            + " " + str_orderBy;
                    
        // RI004	stmt = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        
        stmt = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI004
        
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
        String datalib)
        throws Exception {

		// **********************************************************************************
		// Calcluate the cust-off date to be current year plus two complete prior years
		// **********************************************************************************
		
		Calendar cutOffDt = Calendar.getInstance();		// RI005
		cutOffDt.add(Calendar.YEAR, -2 );				// RI005
		cutOffDtVal = (cutOffDt.get(Calendar.YEAR)*10000) + 100 + 01;	// RI005
				         	
        String SQLstatement2 =
        "SELECT count(*) as total_num_rows "
            + " FROM "
            + datalib
            + ".arihdrf2 "
            + " WHERE (ahcmp = '"
            + company
            + "' AND ahcus# in "
            + customer_list
            + ") " 
            + " and AHDUED >= " + cutOffDtVal    // RI005
            + " and AHSTTS = 'PD' "
            + " and AHCBAL <= 0 and AHTPAY <> 0 ";

		try {		// RI004
			            
			stmt2 = conn.createStatement();
        
        	result2 = stmt2.executeQuery(SQLstatement2);
        
        	result2.next();
        
        	return result2.getString("total_num_rows");
        
        } catch (SQLException sqle) {				// RI004
			System.err.println(sqle.getMessage());	// RI004
			return "0";							// RI004
		} catch (Exception e) {					// RI004
			System.err.println(e.getMessage());		// RI004
			return "0";							// RI004
		} finally {								// RI004
			endcurrentResultset(result2);			// RI004
			endcurrentStatement(stmt2);				// RI004
		}											// RI004
			
    }

    public synchronized String getTotalCurr(
        String customer_list,
        int customer_num,
        String company,
        String datalib)
        throws Exception {
        	
        String SQLstatement2 =
        "select SUM(AHCBAL) as total_current_balance "
            + " FROM "
            + datalib
            + ".arihdrf2 "
            + " WHERE (ahcmp = '"
            + company
            + "' AND ahcus# in "
            + customer_list
            + ") " 
            + " and AHDUED >= " + cutOffDtVal    // RI005
            + " and AHSTTS <> 'PD' and AHCBAL > 0";

      try {
              stmt2 = conn.createStatement();
              
              result2 = stmt2.executeQuery(SQLstatement2);
              
              result2.next(); 
              
              String testresult = result2.getString("total_current_balance");
              
              if (testresult != null )
                    return testresult;
                    
              else return "0.00";
      }
      catch (SQLException sqle) {
        System.err.println(sqle.getMessage());
        return "0.00";
      }
      catch (Exception e) {
        System.err.println(e.getMessage());
        return "0.00";
      }
 
    }

    public void cleanup() throws Exception {
		endcurrentResultset(result);	// RI004
		endcurrentResultset(result2);	// RI004
		endcurrentStatement(stmt);		// RI004
		endcurrentStatement(stmt2);		// RI004
    }

}