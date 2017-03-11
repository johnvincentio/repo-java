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
			

	<FORM action="servlet/RentalRatesServlet" method=POST>

		<TABLE border="1">
			<TBODY>
				<TR>
					<TD bgcolor="#ccccff"><B> Company </B></TD>
					<TD><INPUT type="text" name="company" size="2" maxsize = "2" value="HG"></TD>
				</TR>
				<TR>
					<TD bgcolor="#ccccff"><B> Location </B></TD>
					<TD><INPUT type="text" name="location" size="3" maxsize="3"></TD>
				</TR>
				<TR>
					<TD bgcolor="#ccccff"><B>Est. Return Date</B></TD>
					<TD><INPUT type="text" name="estreturndate" size="6" maxsize="6"></TD>
				</TR>
		
				<TR>
					<TD bgcolor="#ccccff"><B>Cat</B></TD>
					<TD><INPUT type="text" name="catg" size="3" maxsize="3"></TD>
				</TR>
				<TR>
					<TD bgcolor="#ccccff"><B>Class</B></TD>
					<TD><INPUT type="text" name="class" size="4" maxsize="4"></TD>
				</TR>
				<TR>
					<TD bgcolor="#ccccff"><B>Customer</B></TD>
					<TD><INPUT type="text" name="customer" size="7" maxsize="7"></TD>
				</TR>
			</TBODY>
		</TABLE>
			
		<br><br>

		<INPUT type="submit" name="Submit" value="Get Rates">

	</FORM>

</BODY>
</HTML>
