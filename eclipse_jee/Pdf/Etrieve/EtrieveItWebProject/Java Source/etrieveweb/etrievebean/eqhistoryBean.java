// *****************************************************************************************************************
// Copyright (C) 2005 The Hertz Corporation, All Rights Reserved.  Unpublished.       
//                                                                                              
// The information contained herein is confidential and proprietary to the Hertz Corporation
// and may not be duplicated, disclosed to third parties, or used for any purpose not       
// expressly authorized by it.                                                              
// Any unauthorized use, duplication, or disclosure is prohibited by law.                   
// ****************************************************************************************************************
// Description:  Rental Equipment History
// *************************************************************************    
//   MODIFICATION INDEX
//   
//    Index     User Id        Date       Project           Desciption
//    -------  -----------  -----------  ----------------  --------------------------------------------------------
//    x001      DTC9028      11/01/02     SR26609 PCR1      Corrected record count error  
//    x002      DTC9028      11/14/02     SR26609 PCR1      Replaced detail file with RACDETH3
//	  x003     	DTC2073    	 08/19/04     SR31413 PCR5      Add the order by variable to the logic. 
//	  RI004    	DTC2073    	 08/01/05     SR31413 PCR19     Datasource implementation modification 
//    RI005     DTC2073      10/19/05     SR35420			Data Retrieval Limit 
//    RI006     DTC2073      11/15/05     SR35880 		    Added filter by year logic 
// ****************************************************************************************************************
package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*;
import java.util.Calendar;	// RI005
import etrieveweb.utility.sqlBean;

public class eqhistoryBean extends sqlBean implements java.io.Serializable {

    String company = "";
    int totalrecords = 0;

    ResultSet result = null, result2 = null;
    Statement stmt = null, stmt2 = null;

	int cutOffDtVal = 0 ; // RI005

    public eqhistoryBean() {
        super();
    }

    public String Company() {
        return company;
    }
    public void setCompany(String input) {
        company = input;
    }

    public synchronized boolean getRows(
        String customer_list,
        int customer_num,
        String company,
        String datalib,
        int start_num,
        String jobnumber,
		String str_orderBy,
		String selectedYear)	// RI006
        throws Exception {

        String SQLstatement = "";

		// ** Note: The previous hard-coded order by was RDCATG, RDCLAS

		if ( !str_orderBy.trim().equals("") )
			str_orderBy = " order by " + str_orderBy;
			
        if (jobnumber.equals(""))
            SQLstatement =
                "select RDCATG, RDCLAS, ECDESC, sum(RDRDYS) as totalrentdays, sum(RDRHRS) as totalrenthours,count(rdcon#) as totalTransactions, sum(RDAMT$) as totalrentamount "
                    + "from "
                    + datalib
                    + ".RACDETH3 left outer join "
                    + datalib
                    + ".EQPCCMF1 "
                    + "on ECCMP= RDCMP and ECCATG=RDCATG and ECCLAS=RDCLAS"
                    + " where ECCMP='"
                    + company
                    + "' and RDTYPE='RI' and RDCUS# in "
                    + customer_list
                    + " and RDSYSD >= " + selectedYear + "0101"    // RI006
                    + " and RDSYSD < " + selectedYear + "1299"    // RI006
                    + " group by RDCATG, RDCLAS, ECDESC "
                    + str_orderBy;
        else
            SQLstatement =
                "select RDCATG, RDCLAS, ECDESC, sum(RDRDYS) as totalrentdays, sum(RDRHRS) as totalrenthours, count(rdcon#) as totalTransactions, sum(RDAMT$) as totalrentamount, "
                    + "from "
                    + datalib
                    + ".RACDETH3 left outer join "
                    + datalib
                    + ".EQPCCMF1 "
                    + "on ECCMP= RDCMP and ECCATG=RDCATG and ECCLAS=RDCLAS"
                    + " where ECCMP='"
                    + company
                    + "' and RDTYPE='RI' and RDCUS# in "
                    + customer_list
                    + " and RDSYSD >= " + selectedYear + "0101"    // RI006
                    + " and RDSYSD < " + selectedYear + "1299"    // RI006
                    + " and RDCON# in "
                    + "(select RHCON# from "
                    + datalib
                    + ".RACHDRFL "
                    + "where RHCMP='"
                    + company
                    + "' and RHJOB# ='"
                    + jobnumber
                    + "')"
                    + " group by RDCATG, RDCLAS, ECDESC "
                    + str_orderBy;

        // RI004	stmt =  conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
      
        stmt =  conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI004
        
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

    public synchronized String getNumRowsAll(
        String customer_list,
        int customer_num,
        String company,
        String datalib,
        String jobnumber,
        String selectedYear)	// RI006
        throws Exception {

        String SQLstatement2 = "";

        if (jobnumber.equals(""))
            SQLstatement2 =
                "select count(*) as total_num_rows "
                    + "from "
                    + datalib
                    + ".RACDETH3  "
                    + " where RDCMP='"
                    + company
                    + "' and RDTYPE='RI' and RDCUS# in "
                    + customer_list
                    + " and RDSYSD >= " + selectedYear + "0101"    // RI006
                    + " and RDSYSD < " + selectedYear + "1299";    // RI006
        else
            SQLstatement2 =
                "select count(*) as total_num_rows "
                    + "from "
                    + datalib
                    + ".RACDETH3 "
                    + " where RDCMP='"
                    + company
                    + "' and RDTYPE='RI' and RDCUS# in "
                    + customer_list
                    + " and RDSYSD >= " + selectedYear + "0101"    // RI006
                    + " and RDSYSD < " + selectedYear + "1299"    // RI006
                    + " and RDCON# in "
                    + "(select RHCON# from "
                    + datalib
                    + ".RACHDRFL "
                    + "where RHCMP='"
                    + company
                    + "' and RHJOB# ='"
                    + jobnumber
                    + "')";

        // RI004	stmt2 = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

        stmt2 = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI004
        
        result2 = stmt2.executeQuery(SQLstatement2);
        
        result2.next();
        
        return result2.getString("total_num_rows");
    }

    public synchronized String getNumRows(
        String customer_list,
        int customer_num,
        String company,
        String datalib,
        String jobnumber,
        String selectedYear)	// RI006
        throws Exception {

		// **********************************************************************************
		// Calcluate the cust-off date to be current year plus two complete prior years
		// **********************************************************************************
		
		Calendar cutOffDt = Calendar.getInstance();		// RI005
		cutOffDt.add(Calendar.YEAR, -2 );				// RI005
		cutOffDtVal = (cutOffDt.get(Calendar.YEAR)*10000) + 100 + 01;	// RI005
		
        String SQLstatement2 = "";
        String rows ="";

        if (jobnumber.equals(""))
            SQLstatement2 =
                "select RDCATG, RDCLAS, ECDESC "
                    + "from "
                    + datalib
                    + ".RACDETH3 left outer join "
                    + datalib
                    + ".EQPCCMF1 "
                    + "on ECCMP= RDCMP and ECCATG=RDCATG and ECCLAS=RDCLAS"
                    + " where ECCMP='"
                    + company
                    + "' and RDTYPE='RI' and RDCUS# in "
                    + customer_list
                    + " and RDSYSD >= " + selectedYear + "0101"    // RI006
                    + " and RDSYSD < " + selectedYear + "1299"    // RI006
                    + " group by RDCATG, RDCLAS, ECDESC";
        else
            SQLstatement2 =
                "select RDCATG, RDCLAS, ECDESC "
                    + "from "
                    + datalib
                    + ".RACDETH3 left outer join "
                    + datalib
                    + ".EQPCCMF1 "
                    + "on ECCMP= RDCMP and ECCATG=RDCATG and ECCLAS=RDCLAS"
                    + " where ECCMP='"
                    + company
                    + "' and RDTYPE='RI' and RDCUS# in "
                    + customer_list
                    + " and RDSYSD >= " + selectedYear + "0101"    // RI006
                    + " and RDSYSD < " + selectedYear + "1299"    // RI006
                    + " and RDCON# in "
                    + "(select RHCON# from "
                    + datalib
                    + ".RACHDRFL "
                    + "where RHCMP='"
                    + company
                    + "' and RHJOB# ='"
                    + jobnumber
                    + "')"
                    + " group by RDCATG, RDCLAS, ECDESC";
        try {

            // RI004	stmt2 = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            
            stmt2 = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI004
            
            result2 = stmt2.executeQuery(SQLstatement2);
            
            while (result2.next()) {
                 totalrecords = result2.getRow();
            }
            
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            System.err.println(SQLstatement2);
		} finally {								// RI004
			endcurrentResultset(result2);			// RI004
        	endcurrentStatement(stmt2);				// RI004
        }
        
        rows = Integer.toString(totalrecords);      
        return rows;
    }

    public void cleanup() throws Exception {
		endcurrentResultset(result);	// RI004
		endcurrentResultset(result2);	// RI004
		endcurrentStatement(stmt);		// RI004
		endcurrentStatement(stmt2);		// RI004
    }

}