<!-- JSP page content header -->  

<html>
<head>
<meta http-equiv="expires" content="-1">
<meta http-equiv="pragma" content="no-cache">
<link href="images/wynnesystems.css" rel="stylesheet" type="text/css">

<style type="text/css">
#tabdiv {position:absolute; top:120px; left:0px; z-index:2; visibility:show}
#maintext {position:absolute; left:20px; width:950px; z-index:0;}
</style>
<title>Rental Man Report Listing</title>


</HEAD>

<!-- JSP page content header -->
<%@ page language="java" import="java.sql.*, 
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
                                 errorPage="ErrorPage.jsp"%>


<BODY>
<%

String inParm = "";
String outParm= "";
String outParm3= "";
int spoolpages = 0;
byte[] buf = new byte[5000000];
int bytesRead =0;
int bytestot = 0;
boolean afp = true;	
session.putValue("AS400name" , "AS4DEV");
        
// Call programs on server"
String as400name      = (String)session.getValue("AS400name");
String loginuserid    = (String)session.getValue("loginuserid");
String loginpassword  = (String)session.getValue("loginpassword"); 
String str_companyname  = (String) session.getValue("companyname");
String str_customer = (String) session.getValue("customer");
System.out.println( "System Name:   " + as400name);
    AS400 system = new AS400(as400name, loginuserid , loginpassword);


System.out.println( "Getting Parms" );
String Sname = new String(request.getParameter("Sname"));
String CSnum = new String(request.getParameter("Snum"));
String Jname = new String(request.getParameter("JName")); 
String User  = new String(request.getParameter("User")); 
String Jnum  = new String(request.getParameter("JNum")); 
String Disp  = new String(request.getParameter("Disp")); 
String Pdate  = new String(request.getParameter("Pdate")); 
String PPages  = new String(request.getParameter("Pages")); 

int Snum     = Integer.valueOf(CSnum).intValue();
int iPages   = Integer.valueOf(PPages).intValue();


System.out.println( "Sname : " + Sname);
System.out.println( "Snum : " + Snum);
System.out.println( "Jname : " + Jname);
System.out.println( "User : " + User);
System.out.println( "Jnum : " + Jnum);
System.out.println( "Disp : " + Disp);
System.out.println( "Pdate: " + Pdate);
System.out.println( "Pages: " + PPages);

String filename = "";
String Serverdate = "";


Calendar Today = Calendar.getInstance();
int temptoday = Today.get(Calendar.YEAR)*10000 + (Today.get(Calendar.MONTH)+1)*100 + Today.get(Calendar.DAY_OF_MONTH);
String ctemptoday = "";
ctemptoday = ctemptoday.valueOf(temptoday);

DecimalFormat leadingzeros = new DecimalFormat ("000000");
String Cspoolnum ="";
Cspoolnum = leadingzeros.format(Snum);
Serverdate = ctemptoday.substring(4,8) + ctemptoday.substring(0,4);
  filename = "/home/"  
         + "SPOOLER" 
         + "_"
         + Pdate
         + "_"
         + Sname
         + Jnum
         + Cspoolnum
         + ".PDF";
if (Disp.startsWith("Delete"))
{
    System.out.println( "Deleting print" );
    SpooledFile splF = new SpooledFile(system, Sname, Snum , Jname , User, Jnum);
    splF.delete();
    IFSFile aFile = new IFSFile(system, filename);
    if (aFile.exists())
       {
         aFile.delete();
         System.out.println(filename + "Deleted");
        }
    response.sendRedirect("SpoolFileList.jsp");
      
}         
if (Disp.startsWith("P"))
{
    try
    {
        ProgramCall program = new ProgramCall(system);
         
        IFSFile aFile = new IFSFile(system, filename);
        if (aFile.exists())
        {
          System.out.println(filename + "Exists!");
          response.sendRedirect("http://as4dev/" + filename);
                   
        }
        else
        {
          if (Disp.startsWith("PP"))
          {
          System.out.println( "Releasing print" );
          SpooledFile splF = new SpooledFile(system, Sname, Snum , Jname , User, Jnum);
          splF.release(); 
          System.out.println("Err" + AS400Exception.AS400_ERROR);
          
          }
          // Initialize the name of the program to run.
          System.out.println( "Setting up program call" );
          System.out.println(filename + " Does not Exist!");
          String programName = "/QSYS.LIB/DTV5904.LIB/TESTWEB2.PGM";
          ProgramParameter[] parameterList = new ProgramParameter[9];
          AS400Text nametext = new AS400Text(10);
          parameterList[0] = new ProgramParameter(nametext.toBytes(Jname));
	      AS400Text nametext1 = new AS400Text(10);	
          parameterList[1] = new ProgramParameter(nametext1.toBytes(Jnum));
          AS400Text nametext2 = new AS400Text(10);	
          parameterList[2] = new ProgramParameter(nametext2.toBytes(User));
          AS400Text nametext3 = new AS400Text(10);	
          parameterList[3] = new ProgramParameter(nametext3.toBytes(Sname));
    	  AS400Text nametext4 = new AS400Text(10);	
          parameterList[4] = new ProgramParameter(nametext4.toBytes(CSnum));
       	  AS400Text nametext5 = new AS400Text(7);	
          parameterList[5] = new ProgramParameter(nametext5.toBytes(Pdate));
       	  AS400Text nametext6 = new AS400Text(8);	
          parameterList[6] = new ProgramParameter(nametext6.toBytes(Serverdate));
       	  AS400Text nametext7 = new AS400Text(2);	
          parameterList[7] = new ProgramParameter(nametext7.toBytes(Disp));
          parameterList[8] = new ProgramParameter(1);

          program.setProgram(programName, parameterList);
         
                  
         // Run the program.
           if (program.run() != true)
          {
              System.out.println("Program failed!");
              AS400Message[] messagelist = program.getMessageList();
              for (int i = 0; i < messagelist.length; ++i)
              {
                  System.out.println(messagelist[i]);
              }
           }
           else
           {
        // code to get feed back from called program
            
            AS400Text RC = new AS400Text(1);
            outParm = (String) RC.toObject(parameterList[8].getOutputData());
            System.out.println("Program worked! RC=" + outParm);
            if (outParm.equals("Y"))
              {
                response.sendRedirect("http://as4dev/" + filename);
              }         
            else
              {
              System.out.println("outparm not equal Y..." + outParm);
              response.sendRedirect("programCall.jsp?Sname=" + Sname + "&Snum=" + Snum + "&JName=" + Jname + "&User=" + User + "&JNum=" + Jnum +"&Pdate=" + Pdate +"&Disp=WE"); 
              }  
           }
            
       }
    }
    catch (Exception e)
    {
      System.out.println("Exception!");
      System.out.println("Err" + AS400Exception.AS400_ERROR);
      if (Disp.startsWith("PP"))
          {
            response.sendRedirect("programCall.jsp?Sname=" + Sname + "&Snum=" + Snum + "&JName=" + Jname + "&User=" + User + "&JNum=" + Jnum +"&Pdate=" + Pdate +"&Disp=WE"); 
          }
         
      
    }

}
else
{
SpooledFile splF = new SpooledFile(system, Sname, Snum , Jname , User, Jnum);
// Set up print parameter list
PrintParameterList printParms = new PrintParameterList();
printParms.setParameter(PrintObject.ATTR_WORKSTATION_CUST_OBJECT, "/QSYS.LIB/QWPDEFAULT.WSCST");
printParms.setParameter(PrintObject.ATTR_MFGTYPE, "*WSCST");
PrintObjectPageInputStream in = splF.getPageInputStream(printParms);
if (Disp.equals("WB"))
 {
   in.selectPage(iPages);
 }
 else
 {
   in.selectPage(1);
 }  
int currentpage = 0;
       try {
        do 
        {
           currentpage = in.getCurrentPageNumber();        
     //      System.out.println( "1 Reading Page:    " + currentpage); 
     //      System.out.println( "2 Bytes in Page:   " + in.available());
          do
          {
            bytesRead = in.read( buf, bytestot, buf.length );
            if( bytesRead != -1 )
              {
       //          System.out.println( "3 Buffer offset:   " + bytestot);
       //        System.out.println( "4 Bytes Read:      " + bytesRead );
                 bytestot = bytestot + bytesRead;
              }
          }
          while( bytesRead != -1 );
          if (!in.nextPage())
           {
            currentpage = 99999;
           } 
        } 
        while  (currentpage <= 100); 
      
      //  System.out.println( "Total Read " + bytestot + " bytes" );
        in.close();
        }
    catch( Exception e )
    {
        // exception
    }


    // Done with the server.

system.disconnectAllServices();

// Assume splf is the spooled file.
// Create the spooled file viewer
//SpooledFileViewer splfv = new SpooledFileViewer(splF, 1);
//splfv.load();
//JFrame frame = new JFrame("My Window");
//frame.getContentPane().add(splfv);
//frame.pack();
//frame.show();
 // Create output file test.txt	
	 // FileOutputStream outStream = new FileOutputStream("geraci.gif");	
	 // for(int i=0;i< bytestot;++i)	
	 // outStream.write(buf , 0, bytestot);
	 // outStream.close();	
	  // Open test.txt for input
String JobName = new String (splF.getJobName());
String JobNumber = new String (splF.getJobNumber());
String JobUser = new String (splF.getJobUser());
String FileName = new String (splF.getName());
int FileNumber;
FileNumber = splF.getNumber();
int Pages; 
Pages = in.getNumberOfPages();
}
%>
<BODY class="greenscreen" BGCOLOR="#ffffff">

	<div ID=maintext>

		<table border="0" width="950" cellspacing="0" cellpadding="0">
			<tr>
				<td width="300" align="right"><img src="images/goldReports_header.gif" width="300" height="100"></td>
				<td width="400" align="center">&nbsp;</td>
				<td width="250" align="left"><img src="images/HERClogo.gif" width="250" height="100"></td>
			</tr>
		</table>


		<table border="0" width="950" cellspacing="0" cellpadding="0">

			<tr>
				<td background="images/bottom_back.gif" width="25">&nbsp;</td>
				<td background="images/bottom_back.gif" width="895"><!--COMPANY_NAME--><a class="company"><%=str_companyname.trim()%></a>&nbsp;&nbsp;-&nbsp;&nbsp;Account Number: <!--COMPANY_NUMBER--><a class="data"><%=str_customer%></a></td>
				<td width="30" align="left"><img src="images/bottom_back.gif" width="30" height="30"></td>
			</tr>

			<tr>
				<td><img src="images/empty.gif" height="10"></td>
				<td><img src="images/arrow.gif" height="10" align="right"></td>
				<td></td>
			</tr>

			<tr>
				<td><img src="images/empty.gif" height="30"></td>
				<td align="right" class="history"><a href='menu.jsp'>main menu</a>&nbsp;&nbsp;&nbsp;Rental Man Spool File Listing&nbsp;&nbsp;&nbsp;</td>
				<td></td>
			</tr>
			<%out.print("<TR>"
			    + "<td><img src=\"images/empty.gif\" height=\"21\"></td>"
			    + "<TD class=\"menu\" align=\"left\"><a href=\"programCall.jsp?" 
                + "&Sname=" + Sname 
                + "&Snum=" + Snum
                + "&JName=" + Jname
                + "&User=" + User
                + "&JNum=" + Jnum
                + "&Pdate=" + Pdate
                + "&Disp=WB" 
                + "&Pages=" + PPages
                +  "\">"  
                + "<IMG src=\"images/pagebottom.GIF\" height=\"35\" width=\"80\" border=\"0\"></a></TD></TR>");
            %>
		</table>
		<BR>
  

<BR>
<NOBR>
<%
if (Disp.startsWith("WE"))
{ 
 out.print("<table border=\"0\" width=\"950\" cellspacing=\"0\" cellpadding=\"0\">" 
			+ "<tr><td width=\"20\">&nbsp;</td><td width=\"40\"  align=\"right\"><img src=\"images/postit.JPG\" width=\"38\" height=\"38\"></td><td width=\"590\" align=\"left\" valign=\"middle\" class=\"redbold\">PDF is unavailable at this time.</td></tr></table>");
} 
 
%>   
<BR>

<%
try {

int chrperline = 0;
int ii= 0;
String x = new String(buf);
String contract = "       ";
String hotlink;
String quote = "\"";
//out.print("<class=\"greenscreen\">" );
do 
{
  ++ii;
  if ((Sname.startsWith("CXINVRP6") || Sname.startsWith("WOPRTRPF")) && buf[ii] == 13 && buf[ii+1] == 32)
  {
   ++ii;
   do
   ++ii;
   while (buf[ii] != 13);
  }
   
  if ( buf[ii] == 13)
   {
     out.print("<BR>");
   }
  if ( buf[ii] == 32)
   {
       out.print("&nbsp;");
 
   }   
  else
   {
     if (buf[ii] == 42 && buf[ii+2] != 00 && Sname.startsWith("RAOPNRP2")) 
     {
     out.print(x.charAt(ii));
     ++ii; 
     out.print(x.charAt(ii));
     ++ii;
     contract = x.substring(ii+2, ii+9);
     out.print("<a href=\"contractDetail.jsp?contract=" + contract + "&sequence=000\">");  
     out.print(contract);
     out.print("</a>");
       ii = ii+8;
     }
     else if(buf[ii] == 12)
     {
      out.print("<HR WIDTH=100% color=blue>");
      response.flushBuffer();
      }
     else
     {
      out.print(x.charAt(ii));
//      out.print(buf[ii]);
     } 
   }
}
while ( ii< bytestot ) ;
//out.print(buf[ii-1]);
}
catch (Exception e)
{
 System.out.println( "Exception    " ); 
          }
      

%>
<BR>
<%//out.print("<TR>"
//			    + "<td><img src=\"images/empty.gif\" height=\"21\"></td>"
//			    + "<TD class=\"menu\" align=\"left\"><a href=\"programCall.jsp?" 
//                + "&Sname=" + Sname 
//                + "&Snum=" + Snum
//                + "&JName=" + Jname
//                + "&User=" + User
//                + "&JNum=" + Jnum
//                + "&Pdate=" + Pdate
//                + "&Disp=WT" 
//                + "&Pages=" + PPages
//                +  "\">"  
//                + "<IMG src=\"images/pagetop.gif\" height=\"35\" width=\"30\" border=\"0\">Top</a></TD></TR>");
 //           %>
<br>
<BR>
  <a href="javascript:history.go(-1)"><IMG src="images/back.gif" height="40" width="62" border="0"></a> 
  <a href="menu.jsp"><IMG src="images/menu.gif" height="40" width="62" border="0"></a> 
  <a href="javascript:window.close()"><IMG src="images/closeReports.gif" height="40" width="123" border="0"></a> 
  <a href="javascript:window.print()"><img src="images/print.gif" width="67" height="40" border="0"></a>

<hr>
   <table border="0" width="650" cellspacing="0" cellpadding="0">

            <tr>
               <td background="images/empty.gif" class="footerblack"><center>&copy; <%=Serverdate.substring(4)%> The Hertz Corporation.  All Rights Reserved.  &reg REG. U.S. PAT. OFF.</center></td>
            </tr>
 
         </table>

<BR>
</div>

</BODY>
</HTML>
