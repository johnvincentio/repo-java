import java.io.IOException;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import etrieveweb.etrievebean.RentalRatesBean;

public class RentalRatesServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		doPost(req, resp);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {

		RentalRatesBean rentalratesbean = new RentalRatesBean();

		try {
			
			String str_company  = req.getParameter("company").trim();
			String str_location  = req.getParameter("location").trim();
			String str_estreturndate  = req.getParameter("estreturndate").trim();
			String str_catg  = req.getParameter("catg").trim();
			String str_class  = req.getParameter("class").trim();
			String str_customer  = req.getParameter("customer").trim();
			
			if ( str_company == null )
				str_company = "";
				
			if ( str_location == null )
				str_location = "";

			if ( str_estreturndate == null )
				str_estreturndate = "0";
				
			if ( str_catg == null )
				str_catg = "0";
				
			if ( str_class == null )
				str_class = "0";
				
			if ( str_customer == null )
				str_customer = "0";

			String rates [] = rentalratesbean.getRentalRates(str_company, str_location, str_estreturndate, str_catg, str_class, str_customer);

			java.io.PrintWriter out = resp.getWriter();

			out.println("<HTML><HEADER><Title>RentalMan Rates</Title></HEADER>");
			out.println("<BODY><H2>Rates returned</H2>");
			out.println("<table border='1' cellspacing='1' cellpadding='2'>");
			out.println("<tr><td><b>Company      </b></td><td>" + str_company +       "</td></tr>");
			out.println("<tr><td><b>Location     </b></td><td>" + str_location +      "</td></tr>");
			out.println("<tr><td><b>Est.Return Date  </b></td><td>" + str_estreturndate + "</td></tr>");
			out.println("<tr><td><b>Catg-Class   </b></td><td>" + str_catg  + "</td></tr>");
			out.println("<tr><td><b>Customer     </b></td><td>" + str_customer + "</td></tr>");
			out.println("<tr><td><b>Min Rate     </b></td><td bgcolor=yellow>" + rates[0] +     "</td></tr>");
			out.println("<tr><td><b>Daily Rate   </b></td><td bgcolor=yellow>" + rates[1] +     "</td></tr>");
			out.println("<tr><td><b>Weekly Rate  </b></td><td bgcolor=yellow>" + rates[2] +     "</td></tr>");
			out.println("<tr><td><b>Monthly Rate </b></td><td bgcolor=yellow>" + rates[3] +     "</td></tr>");
			out.println("<tr><td><b>Error        </b></td><td bgcolor=red>"    + rates[4] +     "</td></tr>");
			out.println("</table>");

		} catch (Exception e) {
				System.out.println("Error encountered while retriving rates.");
		}

	}

}


