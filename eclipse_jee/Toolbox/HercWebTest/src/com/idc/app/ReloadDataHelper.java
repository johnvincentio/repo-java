
package com.idc.app;

import com.idc.http.AppCookies;
import com.idc.http.AppException;
import com.idc.http.HttpMessage;
import com.idc.http.Receiver;
import com.idc.http.Sender;
import com.idc.utils.UtilHelper;

/**
 * Class that handles Reload data tasks
 * 
 * @author John Vincent
*/
public class ReloadDataHelper {

	private static final String LOGIN_URL = "https://www.hertzequip.com/herc/handlers/LoginHandler";
	private static final String LOGIN_USER = "JobScheduler";
	private static final String LOGIN_PASSWORD = "e13i0_f@rm";
	private static final String LOGIN_FIRST_NAME = "JobSchedulerTasks";
	private static final String LOGIN_LAST_NAME = "DoNotUseOrRemoveThisUser";

	/**
	 * Provide the instruction for HercWeb to reload the data
	 * 
	 * @param migrationLogger		Reference to the logging object
	 * @return						true if tasks completed
	 */
	public static boolean doLogin () {
//		System.out.println (">>> ReloadDataHelper::doLogin");
		long startTime = UtilHelper.getCurrentTimeInMillis();
//		System.out.println ("Begin doLogin tasks at "+startTime+" milliseconds");
		AppCookies appCookies = new AppCookies();
		Sender sender;
		int stage = 1;
		try {
			sender = new Sender (LOGIN_URL);
			stage = 2;
			sender.setPostMethod();
			stage = 3;
			sender.addFormItem ("user", LOGIN_USER);
			sender.addFormItem ("password", LOGIN_PASSWORD);
			stage = 4;
			Receiver receiver = HttpMessage.getReceiverWithRedirects (sender, appCookies);
//			migrationLogger.info ("Response "+receiver.getResponseCode());
			if (! checkLoggedIn (receiver.getBody()))
				System.err.println ("Returned page suggests login failed");
			else
				stage = 100;
		}
		catch (AppException appex) {
			switch (stage) {
			case 4:
				System.err.println ("Unable to receive request; appex "+appex.getMessage());
				break;
			case 3:
				System.err.println ("Unable to sender.addFormItem; appex "+appex.getMessage());
				break;
			case 2:
				System.err.println ("Unable to sender.setGetMethod(); appex "+appex.getMessage());
				break;
			case 1:
			default:
				System.err.println ("Invalid URL; appex "+appex.getMessage());
				break;
			}
		}

		long endTime = UtilHelper.getCurrentTimeInMillis();
		long elapsedTime = endTime - startTime;
		if (stage == 100) {
			if (elapsedTime > 5000L)
				System.err.println ("ERROR in doLogin tasks; Took too long; Login took "+elapsedTime+" milliseconds");
			else
				System.out.println ("Completed doLogin tasks; Login took "+elapsedTime+" milliseconds");
		}
		else
			System.err.println ("ERRORS in doLogin tasks; Login took "+elapsedTime+" milliseconds");

//		System.out.println ("<<< ReloadDataHelper::doLogin");
		return true;
	}

	private static boolean checkLoggedIn (StringBuffer buffer) {
		if (buffer.indexOf ("WELCOME") < 0) return false;
		if (buffer.indexOf (LOGIN_FIRST_NAME) < 0) return false;
		if (buffer.indexOf (LOGIN_LAST_NAME) < 0) return false;
		return true;
	}
}
