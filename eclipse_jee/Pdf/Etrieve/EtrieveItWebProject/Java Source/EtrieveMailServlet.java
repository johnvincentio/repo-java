
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
//	  RI001    DTC2073     02/17/06        SR35873       Abbr. Equipment Release
// ****************************************************************************************************************

import java.io.*;
import java.text.Format;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

import etrieveweb.etrievebean.sysctlflBean; 

public class EtrieveMailServlet extends HttpServlet {
	
	private String email_From = "";	
	private String email_To = "";	
	private String email_Subject = "";	
	private String email_Text = "";	
	private String smtpServer = "";
	private String str_path = "../";
	private String errMsg = "";
	private String transactionRequest = "";
	private String contract_num = "";
	private boolean errorFound = false;
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {

		try
		{		
			//*************************************************************	
			// This section will do the following:  
			// 	(1) Identify the parent page making the request
			//	(2) Format e-mail accordingly 
			//	(3) Get the e-mail recipient 
			//	(4) Send the e-mail
			//	(5) Update the session variables accordingly
			//*************************************************************	

			transactionRequest = (String) req.getParameter("reqPage");
		
			if ( transactionRequest == null)  transactionRequest = "";
				
			if( transactionRequest.trim().equals("releaseEquipment"))
				formatRelease(req);
			else if( transactionRequest.trim().equals("extendEquipment"))
				formatExtend(req);
			else 
			{
				errMsg = "Internal error sending e-mail. No requestor page found.";
				errorRoutine(req,resp);
			}
			
			if( errorFound )
				errorRoutine(req, resp);

			if( !setRecipient(req) )		// Get the recipient
				errorRoutine(req, resp);

			if( sendEmail() )				// Send e-mail
			{
				updateSessionArrays(req);
				resp.sendRedirect(str_path + "equipRelExtConfirm.jsp?contract=" + contract_num.trim() );		
			}
			else
				errorRoutine(req,resp);
		
		}
		catch(Exception e)
		{
			System.err.println("Error formatting e-mail." + errMsg);
			System.err.println(e);
			errorRoutine(req, resp);
		}	
		
	}

	//***************************************************************
	//*  	Format data to send mail for equipment release request
	//***************************************************************

	public void formatRelease(HttpServletRequest req)
	{
		try
		{	
			boolean previousReleases = true;
			boolean itemalreadyReleased = false;
			
			String str_lineitemInfo = "";
			
			String item[]    = req.getParameterValues("item");
			String qtyRel[]  = req.getParameterValues("qtyRel");		
			String line[]    = req.getParameterValues("line");
			String currQty[] = req.getParameterValues("currentQty");
			
			String str_count    = (String) req.getParameter("num_count");	
			String str_contract = (String) req.getParameter("contract");
			String str_relDate  = (String) req.getParameter("relDate");
			String str_relTime  = (String) req.getParameter("relTime");
			
			if(str_count == null || item == null || qtyRel == null || currQty == null || str_contract == null) 	
			{	
				errorFound = true; 
				errMsg = "Missing information.  The release request could not be processed."; 
				return; 
			}
			
			if(str_contract.trim().equals("") || (item[0] == null && qtyRel[0] == null && currQty[0] == null) ) 
			{ 
				errorFound = true;
				errMsg = "Missing contract.  The release request could not be processed."; 
				return; 
			}

			contract_num = str_contract;
			
			String str_header = "\nA request has been received to create a pickup ticket for the information listed below." ;
			
			str_header = str_header.trim() + " This request was done via the Etrieve-It web application. ";
	    	str_header = str_header.trim() + " Contact Mike Tracy if you have any questions.  DO NOT REPLY TO THIS MESSAGE.";
			str_header = str_header.trim() + "\n\nIMPORTANT: Please update RentalMan";
			str_header = str_header.trim() + "\n\nContract#: " + str_contract;
			str_header = str_header.trim() + "\n\nRelease Date: " + str_relDate;
			str_header = str_header.trim() + "\nRelease Time: " + str_relTime;

			// *** Check if the arraylists for release equipment exists ***

			HttpSession session = req.getSession(true);
		
			ArrayList EqpRelContracts = (ArrayList) session.getAttribute("EqpRelContracts");
			ArrayList EqpRelLineNum   = (ArrayList) session.getAttribute("EqpRelLineNum");
			ArrayList EqpRelQtyRel    = (ArrayList) session.getAttribute("EqpRelQtyRel");
			
			if (EqpRelContracts == null || EqpRelLineNum == null || EqpRelQtyRel == null) 
				previousReleases = false;

			// *** Format the message string with detail information ***

			int num_count = Integer.valueOf(str_count).intValue();	
				
			nextLine :
				
			for ( int i=0; i < num_count; i++)
			{
				if ( itemalreadyReleased )
					break;
					
				String str_qty    = qtyRel[i];
				String str_item   = item[i];
				String str_line   = line[i];					
				String str_curQty = currQty[i];	
				
				if( previousReleases )				
				{
					int num_curQty = Integer.valueOf(str_curQty).intValue();

					int num_qty = 0;
							
					if (str_qty == null) str_qty = "0";
							
					int k = str_qty.indexOf(".");
									
					if (k > 0)	str_qty = str_qty.substring(0,k);
									
					num_qty = Integer.valueOf(str_qty).intValue();
					
					int size = EqpRelContracts.size();	
					int lstAllRelQty = 0;
					
					for (int j=0; j<size ; j++)
					{
						String lstCon     = (String) EqpRelContracts.get(j);
						String lstLineNum = (String) EqpRelLineNum.get(j);
						String lstQty     = (String) EqpRelQtyRel.get(j);

						if (lstCon.trim().equals(str_contract.trim()) && lstLineNum.trim().equals(str_line.trim()) )
						{
							lstAllRelQty = lstAllRelQty + Integer.valueOf(lstQty).intValue();

							if( (lstAllRelQty + num_qty ) > num_curQty )
							{
								itemalreadyReleased = true;
								break nextLine ;
							}
						}		
					}										
				}
				
				if (str_qty == null) str_qty = "";
				if (str_item == null) str_item = "";
				
				str_lineitemInfo = str_lineitemInfo + "\n\nItem#: " + str_item + "\nRelease Quantity: " + str_qty;
			}

			// *** If any one item is already released, redirect the user to error page ***
		
			if ( itemalreadyReleased ) 
			{
				errorFound = true;
				errMsg = "This request has already been submitted."; 
				return; 
			}
				
			// *** Format the message string with contact and phone information ***
		
			String str_contactinfo = "";		

			String str_contactName   = (String) req.getParameter("contactName");
			String str_contactPhone1 = (String) req.getParameter("contactPhone1");
			String str_contactPhone2 = (String) req.getParameter("contactPhone2");
			String str_contactPhone3 = (String) req.getParameter("contactPhone3");
			String str_userComments  = (String) req.getParameter("userComments");
		
			str_contactinfo = str_contactinfo + "\n\nRequested By: " + str_contactName  ;
			str_contactinfo = str_contactinfo + "\nPhone#: " + "1-"+ str_contactPhone1 + "-" +str_contactPhone2 + "-" +str_contactPhone3  ;
			str_contactinfo = str_contactinfo + "\n\nComments:\n" + str_userComments + "\n" ;

			email_From    = "hercsales@hertz.com";
			email_Text    = str_header + str_lineitemInfo + str_contactinfo ;		
			email_Subject = "Request for Equipment Release";		

			errorFound = false;
		
		}
		catch(Exception e)
		{
				System.out.println("Error found in formatting release email.");
				System.out.println(e);
				errorFound = true;
				return;		
		}
		
		return;
	}

	//***************************************************************
	//*  	Format data to send mail for contract extention request
	//***************************************************************

	public void formatExtend(HttpServletRequest req)
	{
		try
		{
			boolean extendPending = false;
						
			// *** Retrieve parameters from request.
		
			String str_contract   = (String) req.getParameter("contract");
			String str_revEstDate = (String) req.getParameter("revEstDate");

			// *** If contract number could not be found, return with error.
	
			if(str_contract == null || str_revEstDate == null) 
			{	
				errorFound = true;
				errMsg = "Missing information.  The extend request could not be processed."; 
				return; 
			}
		
			if(str_contract.trim().equals("") || str_revEstDate.trim().equals("")) 
			{ 
				errorFound = true;
				errMsg = "Missing information.  The extend request could not be processed."; 
				return; 
			}

			contract_num = str_contract;
			
			// *** Format the message string with header information ***
		
			String str_header = "\nA request has been received to extend equipment on rent.  The information is listed below. " ;
			str_header = str_header.trim() + " This request was done via the Etrieve-It web application. ";
	    	str_header = str_header.trim() + " Contact Mike Tracy if you have any questions.  DO NOT REPLY TO THIS MESSAGE.";
			str_header = str_header.trim() + "\n\nIMPORTANT: Please update RentalMan";
			str_header = str_header.trim() + "\n\nContract#: " + str_contract ;
			str_header = str_header.trim() + "\n\nNew Estimated Return Date: " + str_revEstDate ;

			// *** Check if the arraylist for equipment extension to see if this contract is already extended in this session ***
	
			HttpSession session = req.getSession(true);
		
			ArrayList EqpExtContracts = (ArrayList)session.getAttribute("EqpExtContracts");
		
			if (EqpExtContracts != null) 
			{
				int size = EqpExtContracts.size();	
			
				nextLine:
			
				for(int i=0; i<size ; i++)
				{					
					String lstCon = (String)EqpExtContracts.get(i);
					if (lstCon.trim().equals(str_contract.trim()) )
					{
						extendPending = true;
						break nextLine;				
					}
				}
			}						

			if( extendPending ) 
			{	
				errorFound = true;
				errMsg = "This request has already been submitted."; 
				return; 
			}

			// *** Format the message string with contact and phone information ***
		
			String str_contactinfo = "";		

			String str_contactName   = (String) req.getParameter("contactName");
			String str_contactPhone1 = (String) req.getParameter("contactPhone1");
			String str_contactPhone2 = (String) req.getParameter("contactPhone2");
			String str_contactPhone3 = (String) req.getParameter("contactPhone3");
			String str_userComments  = (String) req.getParameter("userComments");
		
			str_contactinfo = str_contactinfo + "\n\nRequested By: " + str_contactName  ;
			str_contactinfo = str_contactinfo + "\nPhone#: " + "1-"+ str_contactPhone1 + "-" +str_contactPhone2 + "-" +str_contactPhone3  ;
			str_contactinfo = str_contactinfo + "\n\nComments:\n" + str_userComments + "\n" ;

			email_From    = "hercsales@hertz.com";
			email_Text    = str_header + str_contactinfo ;		
			email_Subject = "Request for Equipment Extension";		

			errorFound = false;
		}
		catch(Exception e)
		{
			System.out.println("Error found in formatting email information. - Eqp Extension Req.");
			System.out.println(e);
			errorFound = true;
			return ;		
		}
		
		return ;
	}

	//******************************************************************
	// **  Determine the recipient 
	//******************************************************************

	public boolean setRecipient(HttpServletRequest req)
	{
		try 
		{	
			HttpSession session = req.getSession(true);

			smtpServer = (String) session.getAttribute("smtpServer");
			
			String str_demoAccount = (String) session.getAttribute("demoAccount");
				
			if (smtpServer == null) 
				smtpServer = "127.0.0.1";

			// *** Check if an email address is available from the Gold Service database. ***
		
			String emailTo_override =(String) session.getAttribute("equipChangeEmail");
			
			if ( emailTo_override == null)	
				emailTo_override = "";

			// *** If email address is NOT available from the Gold Service database,   ***
			// *** try getting it from the SYSCTLFL Location / Company record ***  

			if ( !emailTo_override.equals("") )
				email_To = emailTo_override;
			else 
			{	
				String str_location    = req.getParameter("location");
				
				String str_comp        = (String) session.getAttribute("company");
				
				if(str_location == null) 
					str_location = "";
					
				if(str_comp == null) 
					str_comp = "";
					
				if(str_demoAccount == null) 
					str_demoAccount = "";
				
				sysctlflBean sysctlflbean = new sysctlflBean();
				
				boolean useDatasource = sysctlflbean.getConnectionFlag(str_demoAccount);

		   		if( useDatasource ) 
					email_To = sysctlflbean.getdefaultEmail(str_demoAccount, str_comp,str_location);
			}
		
			// *** If address could not be found in the sysctlfl, it is an error *** 	

			if(email_To.equals("")) 
			{ 
				errorFound = true;
				errMsg = "Recipient Address not found. Mail could not be sent."; 
				return false; 
			}

			// ***********************************
			//  Determine if in a test environmonet 
			// ***********************************
			
			String systemName = (String) session.getAttribute("AS400name");

			// *** Make sure that the mail is not sent to central reservations if *** 
			// *** this is being tested in Dev Environment ***

			if ( systemName.equals("AS4DEV") || systemName.equals("AS4HRCCD") || str_demoAccount.trim().equals("Y") )
			{
				email_Subject = "Test Only !!! " + email_Subject;	
				if( email_To.trim().equals("centralres@hertz.com"))
					email_To = "dummy@xyz.com";	
			}
	
			email_To   = email_To.trim();
			smtpServer = smtpServer.trim();
			email_From = email_From.trim();
		
		} 
		catch (Exception e) 
		{
			System.out.println("Error in retrieving e-mail address from SYSCTLFL" );		
			System.out.println(e);	
			email_To = "";
			return false;
		}		
		  
		return true ;
	}

	//***************************************************************
	//  	Send e-mail 
	//***************************************************************

	public boolean sendEmail() throws MessagingException
	{
		boolean debug = false;

		if( !errorFound )
		{	
			try
			{
				//  **** Set the host smtp address ****
			 	Properties props = new Properties();
			 	
			 	props.put("mail.smtp.host", smtpServer);

				//  ****  create some properties and get the default Session  ****
				Session session = Session.getDefaultInstance(props, null);
				session.setDebug(debug);

				//   **** create a message ****
				Message msg = new MimeMessage(session);

				// **** set the from and to address  ****
				InternetAddress addressFrom = new InternetAddress(email_From);
				msg.setFrom(addressFrom);

				InternetAddress[] addressTo = new InternetAddress[1]; 
				addressTo[0] = new InternetAddress(email_To);
		
				msg.setRecipients(Message.RecipientType.TO, addressTo);
   
				//   ***** Setting the Subject and Content Type  ***
				msg.setSubject(email_Subject);
				msg.setContent(email_Text, "text/plain");
				Transport.send(msg);
			
				return true ;
			
			}
			catch(Exception e)
			{
				System.out.println("Error encountered sending e-mail to:" + email_To);
				System.out.println(e);	
				return false ;
			}
		}
		else
			return false;		
	}
	
	//*********************************************************************************
	//  Update session arrays to reflect the new  releases or extension requests made
	//*********************************************************************************

	public void updateSessionArrays(HttpServletRequest req)
	{
		try
		{
			if( transactionRequest.trim().equals("releaseEquipment") )	
			{
				// *** Retrieve parameters from request object ***

				String str_contract = (String)req.getParameter("contract");
				String item[]       = req.getParameterValues("item");
				String qtyRel[]     = req.getParameterValues("qtyRel");		
				String line[]       = req.getParameterValues("line");
				String str_count    = (String)	req.getParameter("num_count");	
				
				// *** Verify that there are items to be relased **
		
				if (item == null || qtyRel == null || line == null || str_count == null) 
					return ;

				int num_count = Integer.valueOf(str_count).intValue();	
		
				// *** Check if the arraylists for release equipment exists ***
				// *** If not found, create arraylist objects and put them in session ***


				HttpSession session = req.getSession(true);
		
				ArrayList EqpRelContracts = (ArrayList) session.getAttribute("EqpRelContracts");
				ArrayList EqpRelLineNum   = (ArrayList) session.getAttribute("EqpRelLineNum");
				ArrayList EqpRelQtyRel    = (ArrayList) session.getAttribute("EqpRelQtyRel");
				
				if (EqpRelContracts == null) 
					EqpRelContracts = new ArrayList();	
				else 
					session.removeAttribute("EqpRelContracts");


				if (EqpRelLineNum == null) 
					EqpRelLineNum = new ArrayList();	
				else
					session.removeAttribute("EqpRelLineNum");	


				if (EqpRelQtyRel == null) 
					EqpRelQtyRel = new ArrayList();	
				else
					session.removeAttribute("EqpRelQtyRel");			
		
				// *** Add all released item information to the arraylists ***
		
				int siz = EqpRelContracts.size();		
				int count = 0 ;

				if (siz != 0) count = siz - 1 ;
		
				for ( int i=0; i < num_count; i++)
				{
					String str_qty = qtyRel[i];
					String str_line = line[i];					
				
					if (str_qty == null) str_qty = "0";
					if (str_line == null) str_line = "";
				
					EqpRelContracts.add(count,str_contract);
					EqpRelLineNum.add(count,str_line);
					EqpRelQtyRel.add(count,str_qty);
					
					count++;				
				}
		
				// *** Replace old arrayLists with the new ones *****
		
				session.setAttribute("EqpRelContracts", EqpRelContracts);
				session.setAttribute("EqpRelLineNum", EqpRelLineNum);	
				session.setAttribute("EqpRelQtyRel", EqpRelQtyRel);

	  		}	
	  
  			// ***** Update session arrays for Equipment Extension Request.  ****

			if( transactionRequest.trim().equals("extendEquipment"))	
			{
	
				// *** Retrieve parameters from request object ***

				String str_contract = (String)req.getParameter("contract");
		
				if (str_contract == null ) return ;
		
				if (str_contract.equals("")) return ;
		
				// *** Check if the arraylist for equipment extension exists ***
				// *** If not found, create arraylist object and put them in session ***
		
				HttpSession session = req.getSession(true);
		
				ArrayList EqpExtContracts = (ArrayList)session.getAttribute("EqpExtContracts");
					
				if (EqpExtContracts == null) 
					EqpExtContracts = new ArrayList();	
				else 
					session.removeAttribute("EqpExtContracts");
		
				// *** Add extended contract number into the arraylist ***
		
				int siz = EqpExtContracts.size();		
				int ind = 0 ;
					
				if (siz != 0) ind = siz - 1 ;
	
				EqpExtContracts.add(ind,str_contract);
		
				// *** Replace old arrayList with the new one *****
		
				session.setAttribute("EqpExtContracts",EqpExtContracts);
			}
		}
		catch(Exception e)
		{
			System.err.println("Session arrays not updated.");
			System.err.println(e);
			return;	
		}	 	  
	
		return;			
	}	
	
	//******************* 
	//  Servlet Method 
	//*******************

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {

		doGet(req,resp);

	}
	
	//******************* 
	//  Error Routine
	//*******************

	public void errorRoutine(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {

		try
		{	
			if (errMsg.trim().equals("")) 
				errMsg = "Internal error sending E-Mail. Please try later.";
				
			System.err.println(errMsg+" ErrorRoutine Msg");
			
			req.setAttribute("errorMsg",errMsg);
			req.setAttribute("pageUse","Error");
			
			resp.sendRedirect(str_path + "equipRelExtConfirm.jsp?errorMsg=" + errMsg + "&pageUse=Error");
			
		}
		catch(Exception e)
		{	
			resp.sendRedirect(str_path + "securityFailure.jsp");
		}
	}

}
