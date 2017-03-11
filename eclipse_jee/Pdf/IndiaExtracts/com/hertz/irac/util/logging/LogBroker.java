package com.hertz.irac.util.logging;

import com.hertz.irac.framework.HertzSystemException;

//TODO; make own log manager

public class LogBroker {
//	private static LogManager logger = LogManager.getLoggerInstance();

	public static void debug(Class classRef, String msg) {
//		logger.debug(classRef, msg);
	}
	public static void info(Class classRef, String msg) {
//		logger.info(classRef, msg);
	}
	public static void warn(Class classRef, String msg) {
//		logger.warn(classRef, msg);
	}
	public static void fatal(Class classRef, String msgId, String msg) {
//		logger.fatal(classRef, msgId, msg);
	}	
	public static void error(Class classRef, String code, String msg, HertzSystemException ex) {
//		logger.error(classRef, code, msg, ex);
	}
	
	public static void debug (Object obj, String msg) {
//		logger.debug(obj.getClass(), msg);
	}
	public static void info (Object obj, String msg) {
//		logger.info(obj.getClass(), msg);
	}
	public static void warn(Object obj, String msg) {
//		logger.warn(obj.getClass(), msg);
	}
	public static void error(Object obj, String msgId, String msg) {
//		logger.error(obj.getClass(), msgId, msg);
	}
	public static void fatal(Object obj, String msgId, String msg) {
//		logger.fatal(obj.getClass(), msgId, msg);
	}	
	public static void error (Object obj, String code, String msg, HertzSystemException ex) {
//		logger.error(obj.getClass(), code, msg, ex);
	}
}
