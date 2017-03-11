<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
<HEAD>
<meta http-equiv="expires" content="-1">
<meta http-equiv="pragma" content="no-cache">
<link href="images/wynnesystems.css" rel="stylesheet" type="text/css">

<style type="text/css">
#tabdiv {position:absolute; top:120px; left:0px; z-index:2; visibility:show}
#maintext {position:absolute; left:20px; width:650px; z-index:0;}
</style>
<%@ page 
language="java" import="java.sql.*, 
                                 java.util.*,
                                 java.text.*,  
                                 java.net.*, 
                                 java.math.*, 
                                 java.io.*,
                                 java.lang.*,
                                 java.awt.*,
                                 javax.swing.*,
                                 com.ibm.as400.access.*,
                                 com.ibm.as400.vaccess.*"                                 
                                 errorPage="ErrorPage.jsp"
contentType="text/html; charset=ISO-8859-1"
pageEncoding="ISO-8859-1"
%>

<%
int spoolpages = 0;
int bytesRead =0;
int bytestot = 0;
session.putValue("AS400name" , "AS4DEV");
  Calendar cpToday = Calendar.getInstance();
  int cpYear = cpToday.get(Calendar.YEAR) ;  
        
// Call programs on server"
String as400name      = (String)session.getValue("AS400name");
String loginuserid    = (String)session.getValue("loginuserid");
String loginpassword  = (String)session.getValue("loginpassword"); 
String str_companyname  = (String) session.getValue("companyname");
String str_customer = (String) session.getValue("customer");


   System.out.println( "System Name:   " + as400name);
    AS400 system = new AS400(as400name, loginuserid , loginpassword);

    System.out.println(" Connected");
 

  
%>
<META http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<META name="GENERATOR" content="IBM WebSphere Studio">
<TITLE>SpoolFileList.jsp</TITLE>
</HEAD>
<BODY>
<div ID=maintext>

		<table border="0" width="650" cellspacing="0" cellpadding="0">
			<tr>
				<td width="300" align="right"><img src="images/goldReports_header.gif" width="300" height="100"></td>
				<td width="100" align="center">&nbsp;</td>
				<td width="250" align="left"><img src="images/HERClogo.gif" width="250" height="100"></td>
			</tr>
		</table>


		<table border="0" width="650" cellspacing="0" cellpadding="0">

			<tr>
				<td background="images/bottom_back.gif" width="25">&nbsp;</td>
				<td background="images/bottom_back.gif" width="595"><!--COMPANY_NAME--><a class="company"><%=str_companyname.trim()%></a>&nbsp;&nbsp;-&nbsp;&nbsp;Account Number: <!--COMPANY_NUMBER--><a class="data"><%=str_customer%></a></td>
				<td width="30" align="left"><img src="images/bottom_back.gif" width="30" height="30"></td>
			</tr>

			<tr>
				<td><img src="images/empty.gif" height="10"></td>
				<td><img src="images/arrow.gif" height="10" align="right"></td>
				<td></td>
			</tr>

			<tr>
				<td><img src="images/empty.gif" height="30"></td>
				<td align="right" class="history"><a href='menu.jsp'>main menu</a>&nbsp;&nbsp;&nbsp;Rental Man Reports&nbsp;&nbsp;&nbsp;</td>
				<td></td>
			</tr>

		</table>

		<br>
<BR>
<table border=1>
	<TR>
	 <td bgcolor="#000000" background="images/empty.gif" align="center" width="12%" class="whitemid">Print File Name<br> (Click to view)</td>
     <td bgcolor="#000000" background="images/empty.gif" align="center" width="8%" class="whitemid">Create PDF</td>
     <td bgcolor="#000000" background="images/empty.gif" align="center" width="8%" class="whitemid">Number of<BR>Pages</td>
     <td bgcolor="#000000" background="images/empty.gif" align="center" width="8%" class="whitemid">Date</td>
     <td bgcolor="#000000" background="images/empty.gif" align="center" width="7%" class="whitemid">Time</td>
     <td bgcolor="#000000" background="images/empty.gif" align="center" width="25%" class="whitemid">Report Contents<BR>or User Data</td>
     <td bgcolor="#000000" background="images/empty.gif" align="center" width="5%" class="whitemid">Delete</td>
       
</TR>
<%
// IFS Stuff Begin
String directoryName = "/home"; 
//String directoryName = "/hercpdf/rntls_open/" + loginuserid;

String filename = "";
//         try
//         {
//
//            IFSFile directory = new IFSFile(system, directoryName);
//           // Iterator Inum = directory.enumerateFiles("*.pdf");
//            
//            Enumeration fnum = directory.enumerateFiles("*.pdf");
//         
//            while ( fnum.hasMoreElements() )
//         //while ( Inum.hasNext() )
//               
//            {
//             
//             filename = fnum.nextElement().toString();
//             //filename = Inum.next().toString();
//                        out.print("<a href=\"http:\\\\as4dev"
//                        + filename
//                        + "\">"
//                        + filename
//                        + "</a><BR>");
//             }
//            
//         }
//
//         catch (Exception e)
//         {
//
//            System.out.println("List failed");
//            System.out.println(e);
//         }
//      
//    
//
///////////////////
   
// IFS Stuff End
%>
<BR>

<%
            try
            {
            String whatq = "";
            String strSpooledFileName;
            SpooledFileList splfList = new SpooledFileList( system);
            splfList.setUserFilter(loginuserid);
            splfList.openSynchronously();
            Enumeration enum = splfList.getObjects();
           
            while( enum.hasMoreElements() )
            {
                SpooledFile splf = (SpooledFile)enum.nextElement();
                
                if ( splf != null )
                {
                   if (splf.getStringAttribute(SpooledFile.ATTR_OUTPUT_QUEUE).lastIndexOf("PRTPDFHE") > 0)
                      {
                      whatq = "P";
                      }
                      else
                      {
                       whatq = "O";
                      }    
                strSpooledFileName = splf.getStringAttribute(SpooledFile.ATTR_SPOOLFILE);
                out.print ("<TR>"
       //first         
                + "<TD bgcolor=\"#cccccc\" background=\"images/empty.gif\" align=\"center\"><a href=\"programCall.jsp?Sname=" + splf.getStringAttribute(SpooledFile.ATTR_SPOOLFILE) 
                + "&Snum=" + splf.getIntegerAttribute(SpooledFile.ATTR_SPLFNUM)
                + "&JName=" + splf.getStringAttribute(SpooledFile.ATTR_JOBNAME)
                + "&User=" + splf.getStringAttribute(SpooledFile.ATTR_JOBUSER)
                + "&JNum=" + splf.getStringAttribute(SpooledFile.ATTR_JOBNUMBER)
                + "&Pdate=" + splf.getStringAttribute(SpooledFile.ATTR_DATE)
                + "&Disp=W" 
                + "&Pages=" + splf.getIntegerAttribute(SpooledFile.ATTR_PAGES)
                +  "\">"  
                + splf.getStringAttribute(SpooledFile.ATTR_SPOOLFILE) + "</a></TD>"  
       // second  
                + "<TD bgcolor=\"#cccccc\" background=\"images/empty.gif\" align=\"center\"><a href=\"programCall.jsp?Sname="  
       //         + "<TD bgcolor=\"#cccccc\" background=\"images/empty.gif\" align=\"center\"><a href=\"javascript:window.open('programCall.jsp?Sname="
                + splf.getStringAttribute(SpooledFile.ATTR_SPOOLFILE) 
                + "&Snum=" + splf.getIntegerAttribute(SpooledFile.ATTR_SPLFNUM)
                + "&JName=" + splf.getStringAttribute(SpooledFile.ATTR_JOBNAME)
                + "&User=" + splf.getStringAttribute(SpooledFile.ATTR_JOBUSER)
                + "&JNum=" + splf.getStringAttribute(SpooledFile.ATTR_JOBNUMBER)
                + "&Pdate=" + splf.getStringAttribute(SpooledFile.ATTR_DATE)
                + "&Disp=P" + whatq 
                + "&Pages=" + splf.getIntegerAttribute(SpooledFile.ATTR_PAGES)
      //          + "'"
                + "\">" 
      //          + ")\">" 
                + "<center><IMG src=\"images/icon-pdf.gif\" height=\"30\" width=\"40\" border=\"0\"></center></a></TD>"
      //third        
                + "<TD bgcolor=\"#cccccc\" background=\"images/empty.gif\" align=\"center\">" + splf.getIntegerAttribute(SpooledFile.ATTR_PAGES) + "</TD>"
      //fourth
                + "<TD bgcolor=\"#cccccc\" background=\"images/empty.gif\" align=\"center\">"
                + splf.getStringAttribute(SpooledFile.ATTR_DATE).substring(3,5) + "/"
                + splf.getStringAttribute(SpooledFile.ATTR_DATE).substring(5) + "/20"
                + splf.getStringAttribute(SpooledFile.ATTR_DATE).substring(1,3) 
                + "</TD>"
      //fifth
                + "<TD bgcolor=\"#cccccc\" background=\"images/empty.gif\" align=\"center\">"
                + splf.getStringAttribute(SpooledFile.ATTR_TIME).substring(0,2)+":"
                + splf.getStringAttribute(SpooledFile.ATTR_TIME).substring(2,4)+":"
                + splf.getStringAttribute(SpooledFile.ATTR_TIME).substring(4,6)
                + "</TD>"
                ); 
      //sixth                         
                 if ( strSpooledFileName.startsWith("RAOPNRP2") ) 
                 {
                 out.print ("<TD bgcolor=\"#cccccc\" background=\"images/empty.gif\" align=\"Left\">Open Contract Report</TD>");
                 }
                 else 
                 {
                 if ( strSpooledFileName.startsWith("QPJOBLOG") ) 
                 {
                 out.print ("<TD bgcolor=\"#cccccc\" background=\"images/empty.gif\" align=\"Left\">Job Log.</TD>");
                 }
                 else
                 {
                 if ( strSpooledFileName.startsWith("CXINVRP6") && !splf.getStringAttribute(SpooledFile.ATTR_USERDATA).startsWith("WO_PRINT")) 
                 {
                   out.print ("<TD bgcolor=\"#cccccc\" background=\"images/empty.gif\" align=\"Left\">Quote, Reservation, or Invoice</TD>");
                 }
                 else
                 if ( splf.getStringAttribute(SpooledFile.ATTR_USERDATA).startsWith("WO_PRINT") ) 
                 {
                   out.print ("<TD bgcolor=\"#cccccc\" background=\"images/empty.gif\" align=\"Left\">Work Order Quote or Invoice</TD>");
                 }
                 else
                 {
                  out.print ("<TD bgcolor=\"#cccccc\" background=\"images/empty.gif\" align=\"Left\">" +  splf.getStringAttribute(SpooledFile.ATTR_USERDATA) + "&nbsp;</TD>");              
                  }                 
            }            
             }
     // seventh
                 out.print (
    //              "<TD><a href=\"programCall.jsp?Sname=" + splf.getStringAttribute(SpooledFile.ATTR_SPOOLFILE) 
    //            + "&Snum=" + splf.getIntegerAttribute(SpooledFile.ATTR_SPLFNUM)
    //            + "&JName=" + splf.getStringAttribute(SpooledFile.ATTR_JOBNAME)
    //            + "&User=" + splf.getStringAttribute(SpooledFile.ATTR_JOBUSER)
    //            + "&JNum=" + splf.getStringAttribute(SpooledFile.ATTR_JOBNUMBER)
    //            + "&Pdate=" + splf.getStringAttribute(SpooledFile.ATTR_DATE)
   //             + "&Disp=P" + whatq
    //            + "\">" 
    //            + "<IMG src=\"images/icon-pdf.gif\" height=\"30\" width=\"40\" border=\"0\"></a></TD>"
    //seventh ---
                 "<TD bgcolor=\"#cccccc\" background=\"images/empty.gif\" align=\"center\"><a href=\"programCall.jsp?Sname=" + splf.getStringAttribute(SpooledFile.ATTR_SPOOLFILE) 
                + "&Snum=" + splf.getIntegerAttribute(SpooledFile.ATTR_SPLFNUM)
                + "&JName=" + splf.getStringAttribute(SpooledFile.ATTR_JOBNAME)
                + "&User=" + splf.getStringAttribute(SpooledFile.ATTR_JOBUSER)
                + "&JNum=" + splf.getStringAttribute(SpooledFile.ATTR_JOBNUMBER)
                + "&Pdate=" + splf.getStringAttribute(SpooledFile.ATTR_DATE)
                + "&Disp=Delete"
                +  "\">" 
                + "<IMG src=\"images/httrash.gif\" height=\"18\" width=\"18\" border=\"0\"></a></TD>"            
                ); 
    //end of row     
                 out.print ("</TR>");  
                 }
              }
              splfList.close();
            
            }
        catch( Exception e )
        {
            e.printStackTrace();
        }        
%>
	
</table>
<BR>
<a href="javascript:history.go(-1)"><IMG src="images/back.gif" height="40" width="62" border="0"></a> 
                                          <a href="menu.jsp"><IMG src="images/menu.gif" height="40" width="62" border="0"></a> 
                                          <a href="javascript:window.close()"><IMG src="images/closeReports.gif" height="40" width="123" border="0"></a> 
                                          <a href="javascript:window.print()"><img src="images/print.gif" width="67" height="40" border="0"></a>

                                          <br>
<hr>
   <table border="0" width="650" cellspacing="0" cellpadding="0">

            <tr>
               <td background="images/empty.gif" class="footerblack"><center>&copy; <%=cpYear%> The Hertz Corporation.  All Rights Reserved.  &reg REG. U.S. PAT. OFF.</center></td>
            </tr>
 
         </table>

</BODY>
</HTML>
