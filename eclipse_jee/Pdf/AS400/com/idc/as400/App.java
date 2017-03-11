
package com.idc.as400;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.SecureAS400;

public class App {
	public static void main (String[] args) {
		(new App()).doTest1();
	}
	private void doTest() {
		String as400 = "AS4DEV";
		String user = "PRCABCD";
		String pwd = "xxxxx";
		try {
			AS400 system = new AS400(as400);
			SecureAS400 systemSSL = new SecureAS400 (as400);

			System.out.println("Before authentication");
			if (system.authenticate (user, pwd)) {
				System.out.println("hooray");
			}
			else
				System.out.println("Boo");
		}
		catch (Exception ex) {
			System.out.println("Exception; "+ex.getMessage());
		}
	}
	private void doTest1() {
		String as400 = "AS4DEV";
		String user = "PRC4031";
		String pwd = "xxxxxxxxxx";
		boolean bool = authenticate (as400, user, pwd);
		if (bool)
			System.out.println("hooray");
		else
			System.out.println("Boo");
	}
	public boolean authenticate (String as400, String user, String password) {
		try {
			AS400 system = new AS400(as400);
			SecureAS400 systemSSL = new SecureAS400 (as400);
			if (system.authenticate (user, password)) return true;
		}
		catch (Exception ex) {
			System.out.println("Exception; "+ex.getMessage());
		}
		return false;
	}
}
/*
select ZXDATA from RMHCQDATA.SYSCTLFL where ZXKYFL = 'ETRIV1';
to get the name of the as400 box
*/

/*
		  if ((scheme.trim().equals("http") && 
				  		system.authenticate (repuserid.trim(), reppassword.trim())) || 
						(scheme.trim().equals("https") && 
								systemSSL.authenticate(repuserid.trim(), reppassword.trim())))
				   authenticated = true;
*/

/*
 * 
 * 
 * AS400 system = new AS400(str_as400name); SecureAS400 systemSSL = new
 * SecureAS400 (str_as400name); // RI005 Connection MenuSecurityBeanconnection =
 * null;
 * 
 * 
 * 
 * 
 * Connection MenuSecurityBeanconnection = null;
 * 
 * 
 * String scheme = request.getScheme();
 * 
 * AS400 system = new AS400(str_as400name); SecureAS400 systemSSL = new
 * SecureAS400 (str_as400name); // RI005
 * 
 * 
 * 
 * MenuSecurityBeanconnection =
 * MenuSecurityBean.makeautoConnection(str_as400driver, str_as400url,
 * str_userid, str_password, str_GoldCountry, str_connectionType); // RI004
 * 
 *  
 */
