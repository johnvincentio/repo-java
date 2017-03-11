<%
// *****************************************************************************************************************
// Copyright (C) 2005 The Hertz Corporation, All Rights Reserved.  Unpublished.       
//                                                                                              
// The information contained herein is confidential and proprietary to the Hertz Corporation
// and may not be duplicated, disclosed to third parties, or used for any purpose not       
// expressly authorized by it.                                                              
// Any unauthorized use, duplication, or disclosure is prohibited by law.                   
// ****************************************************************************************************************
%>
<html>
<head>

<title>Hertz Equipment Rental Online Reporting</title>


</HEAD>

<!-- JSP page content header -->
<%@ page language="java" import="java.security.*, java.security.spec.*, javax.crypto.*, javax.crypto.spec.*" errorPage="ErrorPage.jsp"%>
<jsp:useBean id="maskbean"  class="etrieveweb.etrievebean.maskBean" scope="page" />

<%
        String str_cleartext = request.getParameter("string").trim();
        String str_noncleartext = request.getParameter("nonstring").trim();
        String str_originaltext = "";
        
        if ( str_noncleartext.equals(null) )
        	str_noncleartext = "";
        	
        if ( str_cleartext.equals(null) )
        	str_cleartext = "";
        	
        String str_encrypted = "";
        String str_decrypted = "";

        if ( !str_cleartext.equals("") && str_noncleartext.equals("") ) {

			str_originaltext = str_cleartext;
			
            // Encrypt
            str_encrypted = maskbean.encrypt(str_cleartext);
    
            // Decrypt
            str_decrypted = maskbean.decrypt(str_encrypted);

        } else if ( !str_noncleartext.equals("") ) {
        
			str_originaltext = str_noncleartext;
			
            // Decrypt
            str_decrypted = maskbean.decrypt(str_noncleartext);

            // Encrypt
            str_encrypted = maskbean.encrypt(str_decrypted);
    
        }

%>


<BODY BGCOLOR="#ffffff">

<br>

 <table border="1" cellspacing="0" cellpadding="1" width="100%">

  <tr>
      <td width="15%">Original string </td>
      <td width="85%"><b><%=str_originaltext%></b></td>
  </tr>


  <tr>
      <td>Encrypted string</td>
      <td><b><%=str_encrypted%></b></td>
  </tr>

  <tr>
     <td>Decrypted string </td>
      <td><b> <%=str_decrypted%></b></td>
  </tr>

</table>


<form>
Test the copy step by pasting the string here:<br>
  Test: <input type="text" name="test" size="100">
</form>

</BODY>
</HTML>

