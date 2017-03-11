// *****************************************************************************************************************
// Copyright (C) 2005 The Hertz Corporation, All Rights Reserved.  Unpublished.       
//                                                                                              
// The information contained herein is confidential and proprietary to the Hertz Corporation
// and may not be duplicated, disclosed to third parties, or used for any purpose not       
// expressly authorized by it.                                                              
// Any unauthorized use, duplication, or disclosure is prohibited by law.                   
// ****************************************************************************************************************   

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import etrieveweb.etrievebean.loginBean;

public class EtrieveWebLoginServlet extends HttpServlet {

    /**
    * Handle the HTTP GET method by building a simple web page.
    */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        doGet(request, response);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        loginBean loginbean = new loginBean();

        String sCodeKey = "CSCode";
        String sDatalibKey = "CSDatalib";
        String srbName = "EtrieveIt";
        String sPathKey = "CSPath";
        String str_company = new String();
        String str_datalib = new String();
        String str_path = new String();
        String str_customer = new String();
        String str_corporate = new String();

        try // get the PropertyResourceBundle
            {
			String str_root = this.getServletContext().getRealPath("");
			File path = new File( str_root, "WEB-INF//classes//conf//EtrieveIt.properties"); 
			Properties settings = new Properties();
			FileInputStream sf = new FileInputStream(path);
			settings.load(sf);
			str_company = settings.getProperty(sCodeKey);
			str_datalib = settings.getProperty(sDatalibKey);
			str_path = settings.getProperty(sPathKey);
        } catch (Exception e) // error
            {
            System.err.println("Failed to load properties.");
            return;
        } // end catch

        try {
            HttpSession session = request.getSession(true);
            String theid = (String) session.getId();

            // check if the brower accept cookies
            if (!theid.equals(request.getParameter("code")))
                response.sendRedirect(str_path + "acceptCookies.jsp");
            else {

                String[] names = session.getValueNames();

                str_customer =
                    loginbean.authenticate(
                        request.getParameter("userid").trim().toUpperCase(),
                        request.getParameter("passwd").trim().toUpperCase(),
                        str_company,
                        str_datalib);
                if (!str_customer.equals("")) {
                    session.putValue("username", request.getParameter("userid").toUpperCase());
                    session.putValue("password", request.getParameter("passwd").toUpperCase());
                    session.putValue("customer", str_customer);
                    session.putValue("company", str_company);
                    session.putValue("datalib", str_datalib);
                    session.putValue("nationalyn", "");

                    str_corporate =
                        loginbean.getCorpAccount(
                            Integer.valueOf(str_customer).intValue(),
                            str_company,
                            str_datalib);
                    if (!str_corporate.equals(""))
                        response.sendRedirect(str_path + "corpLinkAccounts.jsp");
                    else
                        response.sendRedirect(str_path + "menu.jsp");
                    session.putValue("corporate", str_corporate);
                } else {
                    response.sendRedirect(str_path + "securityFailure.jsp");
                    loginbean.cleanup();
                }
            }
        } catch (Exception e) {
            System.out.println(" Error Exception....");
        }
    }
}