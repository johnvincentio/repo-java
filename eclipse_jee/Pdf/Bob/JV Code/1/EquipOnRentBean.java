package com.idc.rm;

import java.sql.*;
import java.io.*;

public class EquipOnRentBean extends SQLBean implements java.io.Serializable {

	String company = "";

	ResultSet result = null, result2 = null, result3 = null;
	Statement stmt = null, stmt2 = null, stmt3 = null;

    public EquipOnRentBean() {
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
		String str_orderBy)
        throws Exception {

        String SQLstatement = "";

		// ** Note: The previous hard-coded order by was RHDATO desc, RDCON# desc

		if ( !str_orderBy.trim().equals("") )
			str_orderBy = " order by " + str_orderBy;

        if (jobnumber.equals("")) {
        
			if ( !company.trim().equals("HG") )  {	// RI014
		     
            	SQLstatement =
                "select distinct RDCON#, RDSEQ#, RDITEM, RDCATG ,RDCLAS, (RDQTY-RDQTYR) as RDQTY, RDLOC, RDDYRT, RDWKRT, RDMORT, RHJOBL, RHDATO, RHPO#, ECDESC, RHORDB, RDSEQ#, RHERDT, CJNAME, 0 as RNBILL, 0 as RNACCR, 0 as RNRCST  "	// RI014
                    + "from "
                    + datalib
                    + ".RAODETFL, "
                    + datalib
                    + ".EQPCCMFL, "
                    + datalib
                    + ".RAOHDRFL "
                    + "  Left outer join "		// RI012
                    + datalib 					// RI012
                    + ".CUSJOBFL On RHCMP=CJCMP and RHCUS#=CJCUS#  and RHJOB#=CJJOB# "	// RI012 
                    + "where (RDCMP=ECCMP and RDCATG=ECCATG and RDCLAS=ECCLAS) and "	// RI010
                    + "(RHCMP=RDCMP and RHCON#=RDCON#) and "
                    + "RDCMP='"
                    + company
                    + "' and RDTYPE='RI' and RDCUS# in "
                    + customer_list
                    + " " + str_orderBy;
                    
			} else {	// RI014
				
				SQLstatement =     
				"select distinct T.RDCMP, T.RDCON#, T.RDSEQ#, T.RDCUS#, T.RDITEM, T.RDCATG, T.RDCLAS, T.RDQTY, T.RDLOC, T.RDDYRT, T.RDWKRT, T.RDMORT, T.ECDESC, RHJOBL, " 
					+ " RHDATO, RHPO#, RHORDB, RHERDT, RHJOB#, CJNAME, T.RNBILL, T.RNACCR, T.RNRCST "
					+ "from "
					+ 	"( select RDCMP, RDCON#, RDSEQ#, RDCUS#, RDITEM, RDCATG, RDCLAS,(RDQTY-RDQTYR) as RDQTY, RDLOC, RDDYRT, RDWKRT, RDMORT, ECDESC, RNBILL, (RNBILL + RNACCR) as RNACCR, RNRCST "
							+ " from "
							+ datalib 
							+ ".EQPCCMFL," 
							+ datalib 
							+ ".RAODETFL" 
							+ "  Left outer join " 
							+ datalib 
							+ ".RAOAMTFL On RDCMP = RNCMP and RDCON# = RNCON# and RDSEQ# = RNSEQ# "
							+ " WHERE ECCMP = RDCMP "
							+ " and ECCATG=RDCATG AND ECCLAS=RDCLAS "
							+ " and ECCMP='"
							+ company
							+ "' and RDCUS# in "
							+ customer_list
							+ " and RDTYPE='RI' )as T , "
					+ datalib
					+ ".RAOHDRFL, "
					+ datalib 
					+ ".CUSJOBFL "
					+ "where "	
					+ "t.RDCMP= RHCMP "
					+ " and T.RDCON# = RHCON# "
					+ " and RHCMP = CJCMP "
					+ " and RHCUS# = CJCUS# "
					+ " and RHJOB# = CJJOB# " 
					+ " and RHCMP='"
					+ company
					+ "' and RHCUS# in " + customer_list
					+ " and RHTYPE='O' "
					+ " " + str_orderBy;  
					
			}	// RI014
			
        } else {

			if ( !company.trim().equals("HG") )  {	// RI014
            	
            	SQLstatement =
                "select distinct RDCON#, RDSEQ#, RDITEM, RDCATG ,RDCLAS, (RDQTY-RDQTYR) as RDQTY, RDLOC, RDDYRT, RDWKRT, RDMORT, RHJOBL, RHDATO, RHPO#, ECDESC, RHORDB, RDSEQ#, RHERDT, CJNAME, 0 as RNBILL, 0 as RNACCR, 0 as RNRCST  "	// RI014
                    + "from "
                    + datalib
                    + ".RAODETFL, "
                    + datalib
                    + ".EQPCCMFL, "
                    + datalib
                    + ".RAOHDRFL "
                    + "  Left outer join "		// RI012
                    + datalib					// RI012 
                    + ".CUSJOBFL On RHCMP=CJCMP and RHCUS#=CJCUS#  and RHJOB#=CJJOB# "	// RI012 
                    + "where (RDCMP=ECCMP and RDCATG=ECCATG and RDCLAS=ECCLAS) and "	// RI010
                    + "(RHCMP=RDCMP and RHCON#=RDCON#) and "
                    + "RDCMP='"
                    + company
                    + "' and RDTYPE='RI' and RDCUS# in "
                    + customer_list
                    + " and RDCON# in "
                    + "(select RHCON# from "
                    + datalib
                    + ".RAOHDRFL "
                    + "where RHCMP='"
                    + company
                    + "' and RHJOB# ='"
                    + jobnumber
                    + "') "
                    + " " + str_orderBy;

			} else {	// RI014
				
				SQLstatement =     
				"select distinct T.RDCMP, T.RDCON#, T.RDSEQ#, T.RDCUS#, T.RDITEM, T.RDCATG, T.RDCLAS, T.RDQTY, T.RDLOC, T.RDDYRT, T.RDWKRT, T.RDMORT, T.ECDESC, RHJOBL, " 
					+ " RHDATO, RHPO#, RHORDB, RHERDT, RHJOB#, CJNAME, T.RNBILL, T.RNACCR, T.RNRCST "
					+ "from "
					+ 	"( select RDCMP, RDCON#, RDSEQ#, RDCUS#, RDITEM, RDCATG, RDCLAS,(RDQTY-RDQTYR) as RDQTY, RDLOC, RDDYRT, RDWKRT, RDMORT, ECDESC, RNBILL, (RNBILL + RNACCR) as RNACCR, RNRCST "
							+ " from "
							+ datalib 
							+ ".EQPCCMFL," 
							+ datalib 
							+ ".RAODETFL" 
							+ "  Left outer join " 
							+ datalib 
							+ ".RAOAMTFL On RDCMP = RNCMP and RDCON# = RNCON# and RDSEQ# = RNSEQ# "
							+ " WHERE ECCMP = RDCMP "
							+ " and ECCATG=RDCATG AND ECCLAS=RDCLAS "
							+ " and ECCMP='"
							+ company
							+ "' and RDCUS# in "
							+ customer_list
							+ " and RDTYPE='RI' )as T , "
					+ datalib
					+ ".RAOHDRFL, "
					+ datalib 
					+ ".CUSJOBFL "
					+ "where "	
					+ "t.RDCMP= RHCMP "
					+ " and T.RDCON# = RHCON# "
					+ " and RHCMP = CJCMP "
					+ " and RHCUS# = CJCUS# "
					+ " and RHJOB# = CJJOB# " 
					+ " and RHCMP='"
					+ company
					+ "' and RHCUS# in " + customer_list
					+ " and RHTYPE='O' "
					+ " and RDCON# in "
                    	+ "(select RHCON# from "
                    	+ datalib
                    	+ ".RAOHDRFL "
                    	+ "where RHCMP='"
                    	+ company
                    	+ "' and RHJOB# ='"
                    	+ jobnumber
                    	+ "') "
					+ " " + str_orderBy;  
					
			}	// RI014
        }
        
        // RI009	stmt = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        
		stmt = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI009

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
        String jobnumber)
        throws Exception {

        String SQLstatement2 = "";

        if (jobnumber.equals(""))
            SQLstatement2 =
                "select distinct RDCON#, RDITEM, RDQTY, RDLOC, RHJOBL, RHPO#, ECDESC, RHORDB, RDSEQ#  "
                    + "from "
                    + datalib
                    + ".RAODETFL, "
                    + datalib
                    + ".EQPCCMFL, "
                    + datalib
                    + ".RAOHDRFL "
                    + "where (RDCMP=ECCMP and RDCATG=ECCATG and RDCLAS=ECCLAS) and "	// RI010
                    + "(RHCMP=RDCMP and RHCON#=RDCON#) and "
                    + "RDCMP='"
                    + company
                    + "' and RDTYPE='RI' and RDCUS# in "
                    + customer_list;
        else
            SQLstatement2 =
                "select distinct RDCON#, RDITEM, RDQTY, RDLOC, RHJOBL, RHPO#, ECDESC, RHORDB, RDSEQ#  "
                    + "from "
                    + datalib
                    + ".RAODETFL, "
                    + datalib
                    + ".EQPCCMFL, "
                    + datalib
                    + ".RAOHDRFL "
                    + "where (RDCMP=ECCMP and RDCATG=ECCATG and RDCLAS=ECCLAS) and "	// RI010
                    + "(RHCMP=RDCMP and RHCON#=RDCON#) and "
                    + "RDCMP='"
                    + company
                    + "' and RDTYPE='RI' and RDCUS# in "
                    + customer_list
                    + " and RDCON# in "
                    + "(select RHCON# from "
                    + datalib
                    + ".RAOHDRFL "
                    + "where RHCMP='"
                    + company
                    + "' and RHJOB# ='"
                    + jobnumber
                    + "') ";

        // RI009	stmt2 = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        
        stmt2 = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);		// RI009
        
        result2 = stmt2.executeQuery(SQLstatement2);
        
        int totalrecords = 0;
        
        // while (result2.next()) {
        //    totalrecords = result2.getRow();
        // }
        
    	if ( result2 != null ) {	// RI015
    		result2.last();			// RI015 
    		totalrecords = result2.getRow() ;	// RI015
    	}										// RI015
    	
        return Integer.toString(totalrecords);
    }


    public synchronized boolean getPickup(
        String customer_list,
        int customer_num,
        String company,
        String datalib)
        throws Exception {

        String SQLstatement3 = "";

            SQLstatement3 =
                "select RUPKU#, RUCON#, RUSEQ#, RUEQP#  "
                    + "from "
                    + datalib
                    + ".RAPKUPF4 "
                    + "where RUCMP='"
                    + company.trim()
                    + "' and RUCUS# in "
                    + customer_list
                    + " and RUCON# > 0 and RUSTTS = 'OP' ";

        // RI009	stmt3 = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        
        stmt3 = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);		// RI009
        
        result3 = stmt3.executeQuery(SQLstatement3);
        
        return (result3 != null);
    }

    public boolean getNext3() throws Exception {
        return result3.next();
    }

    public String getColumn3(String colNum) throws Exception {
        return result3.getString(colNum);
    }


	//*******************************************
	// RI011 - Get line item comments
	//******************************************
			
	public synchronized String getItemComments(String company, String datalib, int contract, int lineno) throws Exception { 

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
				"  and OCISEQ=  0 " +
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
		endcurrentResultset(result);	// RI009
		endcurrentResultset(result2);	// RI009
		endcurrentResultset(result3);	// RI009
		endcurrentStatement(stmt);		// RI009
		endcurrentStatement(stmt2);		// RI009
		endcurrentStatement(stmt3);		// RI009
    }

}