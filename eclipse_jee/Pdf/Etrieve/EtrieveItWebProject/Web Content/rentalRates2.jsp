<%
// *****************************************************************************************************************
// Modification Tag		Developer	Date(DD/MM/YY)		Comments	
// RI001				DTC2051		25/05/06 			Changes made to incorporate 	
// RI001												Rental Amount in the Rates calculation									
// ******************************************************************************************************************
%>

<html>
	<head>

		<title>Rates Calculation</title>

		<meta http-equiv="expires" content="-1">
		<meta http-equiv="pragma" content="no-cache">

		<style type="text/css">
			#tabdiv {position:absolute; top:120px; left:0px; z-index:2; visibility:show}
			#maintext {position:absolute; left:20px; width:650px; z-index:0;}
		</style>

	</HEAD>


	<!-- JSP page content header -->
	<%@ page language="java" import="java.sql.*, java.util.*,com.ibm.as400.access.*" %>

	<%
	// ****************************************************************************************************
	// Called a external Stored Procedure written in RPGLE which returns the Cat/Class rates.
	// ****************************************************************************************************
	%>	
			

	<FORM name = "rentalRates" action="servlet/RentalRatesServlet2" method=POST >

		<B><BR>Get the Rates Or the Estimated Rental Amount for a Single Category/Class.</B><BR>
		<BR>
		<TABLE border="1">
			<TBODY>
				<TR>
					<TD bgcolor="#ccccff" width="211"><B> Company </B></TD>
					<TD width="62"><INPUT type="text" name="company" size="2"
				maxsize="2" value="HG" maxlength="2" tabindex="1"></TD>
				</TR>
				<TR>
					<TD bgcolor="#ccccff" width="211"><B> Location </B></TD>
					<TD width="62"><INPUT type="text" name="location" size="3"
				maxsize="3" maxlength="3" tabindex="2"></TD>
				</TR>
				<TR>
					<TD bgcolor="#ccccff" width="211"><B>Start Date (MMDDYY) </B></TD>
					<TD width="62"><INPUT type="text" name="startdate" size="6" maxsize="6" tabindex="3" maxlength="6"></TD>
				</TR>
				<TR>
					<TD bgcolor="#ccccff" width="211"><B>Est. Return Date (MMDDYY) </B></TD>
					<TD width="62"><INPUT type="text" name="estreturndate" size="6" maxsize="6" tabindex="3" maxlength="6"></TD>
				</TR>
		
				<TR>
					<TD bgcolor="#ccccff" width="211"><B>Cat</B></TD>
					<TD width="62"><INPUT type="text" name="catg" size="3" maxsize="3" maxlength="3" tabindex="4"></TD>
				</TR>
				<TR>
					<TD bgcolor="#ccccff" width="211"><B>Class</B></TD>
					<TD width="62"><INPUT type="text" name="class" size="4" maxsize="4" tabindex="5" maxlength="4"></TD>
				</TR>
				<%
				//RI001 Starts
				%>
				<TR>
					<TD bgcolor="#ccccff" width="211"><B>Customer</B></TD>
					<TD width="62"><INPUT type="text" name="customer" size="7" maxsize="7" maxlength="7" tabindex="6"></TD>
				</TR>
				
				<TR>
		
				<TD bgcolor="#ccccff" height="32" align="Left" valign="bottom" width="211"><INPUT
				type="RADIO" name="calctype" value="RATES" checked tabindex="7"><B> Rates </B><INPUT
				type="RADIO" name="calctype" value="RENTAMT" tabindex="8"><B> Rental Amount </B></TD>
					
				</TR>
				<%
				//RI001 Ends
				%>
					
			</TBODY>
		</TABLE>
	
			
		<br><br>
		

		<INPUT  type="submit" name="Submit" value="Get Rates" tabindex="9">

	</FORM>
		


</BODY>
</HTML>



