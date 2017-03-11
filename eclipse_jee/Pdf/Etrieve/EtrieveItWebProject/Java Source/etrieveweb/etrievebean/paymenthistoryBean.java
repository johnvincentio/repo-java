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
//    x001       DTC9028      06/06/05      SR31413 PCR30       Select only payments greater that zero
//    RI002      DTC9028      07/28/05      SR31413 PCR19       Implement datasource modification
//    RI003      DTC9028      11/08/05      SR35420             Implement retrieval limit
// **************************************************************************************************************
package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*; 
import java.util.Calendar;
import java.util.Date;
import etrieveweb.utility.sqlBean;

public class paymenthistoryBean extends sqlBean implements java.io.Serializable{


	ResultSet result=null, result2=null;
	Statement stmt=null, stmt2=null;

	public paymenthistoryBean() {
		super();
	}

 
	public synchronized boolean getRows(String customer_list, int customer_num, String company, String datalib, int start_num, int sysdate, String checknumber, String str_orderBy) throws Exception { 

		// ** Note: The previous hard-coded order by was ADSYSD desc, ADPID# desc, ADINV# desc, ADISEQ, ADDSEQ"

		if ( !str_orderBy.trim().equals("") )
			str_orderBy = " order by " + str_orderBy;

		String SQLstatement = 
			" select ADSYSD, ADPID#, (ADAMT$*-1) as Amount, ADINV#, ADISEQ, ADDSEQ, ADLOC, ADRCDC, ADTYPE, ADSRC, AHDUED, AHINVD " +
				"from " + datalib + ".ARIHDRFL, " + datalib + ".ARIDETF2" 
				+ " where (AHCMP=ADCMP "
				+ " and AHCUS#=ADCUS# "
				+ "and AHINV#=ADINV# " 
				+ " and AHISEQ=ADISEQ)" 
				+ " and AHCMP='" + company + "' " 
				+ " and AHCUS# in " + customer_list 
				+ " and ADPID#='" + checknumber + "'" 
				+ " and ADSYSD=" + sysdate 
				+ " and adstts not in ('P', 'D') "
				+ str_orderBy;


		// RI001 - modified SQL statement

		Calendar Today = Calendar.getInstance();

		Today.add(Calendar.YEAR, -2); // RI002
		
		int todayDate = Today.get(Calendar.YEAR)*10000 + (Today.get(Calendar.MONTH)+1)*100 + Today.get(Calendar.DAY_OF_MONTH);

		SQLstatement = 
			"SELECT  ADSYSD, ADPID#, ADINV#, ADISEQ, ADDSEQ, ADLOC, ADRCDC, ADTYPE, ADSRC, AHDUED, AHINVD, sum(Amount)  as Amount  FROM ("
				+ " SELECT ADSYSD, ADPID#, ADINV#, ADISEQ, ADDSEQ, ADLOC, ADRCDC, ADTYPE, ADSRC, AHDUED, AHINVD, sum(ADAMT$ * -1) as Amount  "     
				+ " FROM " + datalib + ".arihdrf2," + datalib + ".aridetf2 "		// RI003
				+ " where AHCMP='"+ company + "' "
				+ " and AHCUS# in " + customer_list
				+ " and ahinvd >= " + todayDate 		// RI003
				+ " and ahinv# > 0 "
				+ " and ahiseq > 0 "
				+ " AND ahtpay < 0 "
				+ " and ahcmp = adcmp "
				+ " and ahcus# = adcus# " 
				+ " and ahinv# = adinv# "
				+ " and ahiseq = adiseq "
				+ " and adstts not in ('P', 'D') "
				+ " and ADRCDC = 'P' "
				+ " and ADPID#='" + checknumber + "'" 
				+ " and ADSYSD=" + sysdate 
				+ " GROUP BY ADSYSD, ADPID#, ADINV#, ADISEQ, ADDSEQ, ADLOC, ADRCDC, ADTYPE, ADSRC, AHDUED, AHINVD "
				+ " HAVING sum(adamt$ * -1) > 0 " 
				+ " ) as max "
				+ " group by ADSYSD, ADPID#, ADINV#, ADISEQ, ADDSEQ, ADLOC, ADRCDC, ADTYPE, ADSRC, AHDUED, AHINVD "
				+ str_orderBy;

		// RI002	stmt=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
		stmt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI002
		
		result=stmt.executeQuery(SQLstatement);

		if ( start_num > 0 )
			result.absolute(start_num);

		return (result != null);
	}


	public boolean getNext() throws Exception {
		return result.next();
	}


	public String getColumn(String colNum) throws Exception {
		return result.getString(colNum);
	}


	public synchronized String getNumRows(String customer_list, int customer_num, String company, String datalib, int sysdate, String checknumber) throws Exception {

		String SQLstatement2 = 
			"select count(*) as total_num_rows " +
				"from " + datalib + ".ARIHDRFL, " + datalib + ".ARIDETF2" +    
				" where (AHCMP=ADCMP and AHCUS#=ADCUS# and AHINV#=ADINV# and AHISEQ=ADISEQ)" 
				+ " and AHCMP='" + company + "' "
				+ " and AHCUS# in " + customer_list 
				+ " and ADPID#='" + checknumber + "'" 
				+ " and ADSYSD=" + sysdate 
				+ " and adstts not in ('P', 'D') ";

		// RI001 - modified SQL statement

		Calendar Today = Calendar.getInstance();

		Today.add(Calendar.YEAR, -2); // RI002
		
		int todayDate = Today.get(Calendar.YEAR)*10000 + (Today.get(Calendar.MONTH)+1)*100 + Today.get(Calendar.DAY_OF_MONTH);

		SQLstatement2 = 
			"SELECT adcmp, sum(records) as total_num_rows, sum(total) as SubTotal FROM ("
				+ " SELECT adcmp, count(*) as records, sum(adamt$ * -1) as total "     
				+ " FROM " + datalib + ".arihdrf2, " + datalib + ".aridetf2 "		// RI003
				+ " where AHCMP='"+ company + "'"
				+ " and AHCUS# in " + customer_list
				+ " and ahinvd >= " + todayDate 			// RI003
				+ " and ahinv# > 0 "
				+ " and ahiseq > 0 "
				+ " and AHTPAY < 0 "
				+ " and ahcmp = adcmp "
				+ " and ahcus# = adcus# " 
				+ " and ahinv# = adinv# "
				+ " and ahiseq = adiseq "  
				+ " and adstts not in ('P', 'D') "
				+ " and ADRCDC = 'P' "        
				+ " and ADPID#='" + checknumber + "'" 
				+ " and ADSYSD=" + sysdate                                           
				+ " GROUP BY adcmp "
				+ " HAVING sum(adamt$ * -1) > 0 " 
				+ " ) as max "
				+ " group by adcmp"; 


		stmt2=conn.createStatement();
		
		result2=stmt2.executeQuery(SQLstatement2);
		
		result2.next();
		
		return result2.getString("total_num_rows");
	}

	public synchronized String getDescription(int customer_num, String company, String datalib, String loc, String adjtype) throws Exception {

		String SQLstatement2 = "select MDSDSC " +
			"from " + datalib + ".MARACDFL" +    
			" where MDCMP='" + company + "' and MDLOC='" + loc + "' and MDCODE='" + adjtype + "'";
                          
		stmt2=conn.createStatement();
		result2=stmt2.executeQuery(SQLstatement2);
		result2.next();
		
		try {
			return result2.getString("MDSDSC");
		}  catch (SQLException sqle) {
			System.err.println(sqle.getMessage());
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
			return "";
	}


	public void cleanup() throws Exception {
		endcurrentResultset(result);	// RI002
		endcurrentResultset(result2);	// RI002
		endcurrentStatement(stmt);		// RI002
		endcurrentStatement(stmt2);		// RI002
	}

}