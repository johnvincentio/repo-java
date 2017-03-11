package com.hertz.hercutil.framework;

import java.sql.SQLException;

import com.hertz.irac.framework.HertzSystemException;

public class HercErrorHelper {
	/**
	 * Handle SQL exceptions.
	 * 
	 * @param	obj					class that threw the exception.
	 * @param	exception			the SQL exception
	 * @param	message				an additonal message
	 * @return	HertzSystemException
	 */
	//TODO: write your own error handling code here....

	public static HertzSystemException handleSQLException (Object obj, SQLException exception, String message) {
		System.out.println("SQL Exception "+exception.getMessage());

		return new HertzSystemException ("SQL Exception "+exception.getMessage());
	}
}
