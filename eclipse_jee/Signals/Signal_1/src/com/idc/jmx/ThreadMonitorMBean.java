package com.idc.jmx;

public interface ThreadMonitorMBean {
	String getName();
	void start();
	void stop();
	boolean isRunning();
}
