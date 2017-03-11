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
//    Index     User Id         Date       Project            Desciption
//   --------  -----------   -----------  ----------------   ----------------------------------------------------
//    x001      DTC9028                    SR26609 PCR1       Add the 150 days bucket
//    x002      DTC9028                    SR28586            Add logic for Canada without the 150 days bucket
//    RI003     DTC9028       06/27/05     SR31413 PCR19      Implement the datasource modification 
// ***************************************************************************
package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*; 
import etrieveweb.utility.sqlBean;

public class acctsummaryBean extends sqlBean implements java.io.Serializable{


	ResultSet result=null, result2=null;
	Statement stmt=null, stmt2=null;

	public acctsummaryBean() {
		super();
	}

 
	public synchronized boolean getRows(String customer_list, int customer_num, String company, String datalib) throws Exception { 

		String SQLstatement = "";

		if ( company.equals("CR")  )  {

			SQLstatement = 
				"select sum(CMCUR) as cal_cmcur, sum(CM30A) as cal_cm30a, sum(CM60A) as cal_cm60a, " + 
					"sum(CM90A) as cal_cm90a, sum(CM120A) as cal_cm120a, 0 as cal_cm150a, sum(CMCUR+CM30A+CM60A+CM90A+CM120A) as RentAmt, " +
					"sum(CMRYTD) as cal_cmrytd, sum(CMR$LY) as cal_cmrly, sum(CMRLTD) as cal_cmrltd, " + 
					"sum(CMSYTD) as cal_cmsytd, sum(CMS$LY) as cal_cmsly, sum(CMSLTD) as cal_cmsltd, " + 
					"sum(CMEYTD) as cal_cmeytd, sum(CME$LY) as cal_cmely, sum(CMELTD) as cal_cmeltd, " + 
					"sum(CMOYTD) as cal_cmoytd, sum(CMO$LY) as cal_cmoly, sum(CMOLTD) as cal_cmoltd, " +
					"sum(CMRYTD+CMSYTD+CMEYTD+CMOYTD) as totalthis, sum(CMR$LY+CMS$LY+CME$LY+CMO$LY) as totallast, " +
					"sum(CMRLTD+CMSLTD+CMELTD+CMOLTD) as totallife, " +
					"sum(CMOPN$) as totalopenamount, sum(CMCUR+CM30A+CM60A+CM90A+CM120A+CMOPN$) as totalopen " +
					"from " + datalib + ".CUSMASFL  " +  
					" where CMCMP='" + company + "' and CMCUS# in " + customer_list + " group by CMCMP";

		}  else  {

			SQLstatement = 
				"select sum(CMCUR) as cal_cmcur, sum(CM30A) as cal_cm30a, sum(CM60A) as cal_cm60a, " + 
					"sum(CM90A) as cal_cm90a, sum(CM120A) as cal_cm120a, sum(CM150A) as cal_cm150a, sum(CMCUR+CM30A+CM60A+CM90A+CM120A+CM150A) as RentAmt, " +
					"sum(CMRYTD) as cal_cmrytd, sum(CMR$LY) as cal_cmrly, sum(CMRLTD) as cal_cmrltd, " + 
					"sum(CMSYTD) as cal_cmsytd, sum(CMS$LY) as cal_cmsly, sum(CMSLTD) as cal_cmsltd, " + 
					"sum(CMEYTD) as cal_cmeytd, sum(CME$LY) as cal_cmely, sum(CMELTD) as cal_cmeltd, " + 
					"sum(CMOYTD) as cal_cmoytd, sum(CMO$LY) as cal_cmoly, sum(CMOLTD) as cal_cmoltd, " +
					"sum(CMRYTD+CMSYTD+CMEYTD+CMOYTD) as totalthis, sum(CMR$LY+CMS$LY+CME$LY+CMO$LY) as totallast, " +
					"sum(CMRLTD+CMSLTD+CMELTD+CMOLTD) as totallife, " +
					"sum(CMOPN$) as totalopenamount, sum(CMCUR+CM30A+CM60A+CM90A+CM120A+CM150A+CMOPN$) as totalopen " +
					"from " + datalib + ".CUSMASFL left outer join " +  
					datalib + ".CUSMS2FL " +
					"on CMCMP= CMCMP2 and CMCUS# = CMCUS#2 " +
					" where CMCMP='" + company + "' and CMCUS# in " + customer_list + " group by CMCMP";
		}

 
		stmt=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
		result=stmt.executeQuery(SQLstatement);
		
		return (result != null);
	}


	public synchronized String getTotalOpenAmt(String customer_list, int customer_num, String company, String datalib) throws Exception { 

		String SQLstatement = "";

		if ( company.equals("CR")  )  {

			SQLstatement = 
				"select sum(CMCUR+CM30A+CM60A+CM90A+CM120A+CMOPN$) as totalopen " +
				"from " + datalib + ".CUSMASFL " +  
				" where CMCMP='" + company + "' and CMCRP# =" +
				" (select CMCRP# from " + datalib + ".CUSMASFL where CMCMP='" + company + "' and CMCUS#=" + customer_num + ")"; 
		}  else  {

			SQLstatement = 
				"select sum(CMCUR+CM30A+CM60A+CM90A+CM120A+CM150A+CMOPN$) as totalopen " +
				"from " + datalib + ".CUSMASFL left outer join " +  
				datalib + ".CUSMS2FL " +
				"on CMCMP= CMCMP2 and CMCUS# = CMCUS#2 " +
				" where CMCMP='" + company + "' and CMCRP# =" +
				" (select CMCRP# from " + datalib + ".CUSMASFL where CMCMP='" + company + "' and CMCUS#=" + customer_num + ")"; 
		}

						
			stmt=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
  
			result=stmt.executeQuery(SQLstatement);
  
			if ( result != null && result.next() != false )
				return result.getString("totalopen");
			else 
				return "0";				
		    
	}


	public synchronized boolean getCustomerInfo(int customer_num, String company, String datalib) throws Exception { 

		String SQLstatement = 
			"select * " + 
			"from " + datalib + ".CUSMASFL " +  
			" where CMCMP='" + company + "' and CMCUS#=" + customer_num;
                
		stmt=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

		result=stmt.executeQuery(SQLstatement);

		return (result != null);
	}


	public boolean getNext() throws Exception {
		return result.next();
	}


	public String getColumn(String colNum) throws Exception {
		return result.getString(colNum);
	}


	public synchronized String getNumRows(String customer_list, int customer_num, String company, String datalib) throws Exception {
  
		String SQLstatement2 = "select count(*) as total_num_rows " + 
			"from " + datalib + ".CUSMASFL " +  
			" where CMCMP='" + company + "' and CMCUS# in " + customer_list;

		stmt2=conn.createStatement();

		result2=stmt2.executeQuery(SQLstatement2);

		result2.next();

		return result2.getString("total_num_rows");
	}


	public void cleanup() throws Exception {
		endcurrentResultset(result);	// RI003
		endcurrentResultset(result2);	// RI003
		endcurrentStatement(stmt);		// RI003
		endcurrentStatement(stmt2);		// RI003
	}

}