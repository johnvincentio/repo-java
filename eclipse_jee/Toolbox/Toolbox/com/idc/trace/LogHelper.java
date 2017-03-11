package com.idc.trace;

import java.sql.Time;

import org.apache.log4j.Logger;

public class LogHelper {
	private static Logger logger = Logger.getRootLogger();

	public static void debug(String msg) {logger.debug(msg);}
	public static void info(String msg) {logger.info(msg);}
	public static void warn(String msg) {logger.warn(msg);}
	public static void fatal(String msg) {logger.fatal(msg);}
	public static void error(String msg) {logger.error(msg);}

	public static String timing (String msg) {
		return (new Time(System.currentTimeMillis())).toString() + "; " + msg;
	}
}
