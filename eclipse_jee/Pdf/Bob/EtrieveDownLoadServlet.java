// *****************************************************************************************************************
// Copyright (C) 2005 The Hertz Corporation, All Rights Reserved.  Unpublished.       
//                                                                                              
// The information contained herein is confidential and proprietary to the Hertz Corporation
// and may not be duplicated, disclosed to third parties, or used for any purpose not       
// expressly authorized by it.                                                              
// Any unauthorized use, duplication, or disclosure is prohibited by law.                   
// ****************************************************************************************************************
//	 MODIFICATION INDEX 
//   
//	  Index    User Id       Date          Project       Desciption
//	 -------  ----------  ----------  ----------------  ------------------------------------------------------
//	  x001     DTC2073     11/05/04    SR31413 PCR24     Data download feature.
//	  x002     DTC2073     15/05/05    SR31413 PCR24     Do not remove dataList object from session.
// ****************************************************************************************************************

import java.io.*;
import java.text.Format;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;


public class EtrieveDownLoadServlet extends HttpServlet {
   
	/**
	   * Handle the HTTP GET method by building a simple web page.
	   */
	   public void doPost(HttpServletRequest request, HttpServletResponse response)
		   throws ServletException, IOException {
		   	
		   doGet(request, response);
		   
	   }

	   public void doGet(HttpServletRequest request, HttpServletResponse response)
		   throws ServletException, IOException {
		   	
			HttpSession session = request.getSession();

			String dataList[][] = (String[][]) session.getAttribute("dataList");
			
			String filename = request.getParameter("filename");;

			response.setContentType("text/csv");
			
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".csv\"");
			
			PrintWriter out = response.getWriter();
						
			if(filename == null || dataList == null)
				return;
		
				
			// Iterate through the two-dimentional array and output the data to a CSV file
			
			for(int i = 0; i < dataList.length; i++)
			
			{
				boolean gotOne = false;
				
				for(int j = 0; j < dataList[i].length; j++)
				
				{
					if(gotOne)
						out.write(",");

					String field = dataList[i][j].replace(',', ' ');  // Remove any commas

					if (field == null)  
						field = "";

					out.write(field);
					
					gotOne = true;
					
				}
				
				out.write("\n");
			}
			
			out.close();
			
		}
  
}
