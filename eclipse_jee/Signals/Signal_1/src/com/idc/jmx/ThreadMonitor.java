package com.idc.jmx;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

/*
-Dcom.sun.management.jmxremote
-Dcom.sun.management.jmxremote.port=9999
-Dcom.sun.management.jmxremote.authenticate=false
-Dcom.sun.management.jmxremote.ssl=false
-Djava.rmi.server.hostname=localhost
 */
public class ThreadMonitor implements ThreadMonitorMBean {
	private Thread m_thread = null;

	public ThreadMonitor (Thread thread) {
		m_thread = thread;
	}

	@Override
	public String getName() {
		return "JMX Controlled App";
	}

	@Override
	public void start() {
		System.out.println("remote start called");
	}

	@Override
	public void stop() {
		System.out.println("remote stop called");
		m_thread.interrupt();
	}

	public boolean isRunning() {
		return Thread.currentThread().isAlive();
	}

	public static void main(String[] args) {
		try {
			System.out.println("JMX started");

			ThreadMonitorMBean monitor = new ThreadMonitor (Thread.currentThread());

			MBeanServer server = ManagementFactory.getPlatformMBeanServer();

			ObjectName name = new ObjectName ("com.idc.jmx:type=ThreadMonitor");

			server.registerMBean (monitor, name);

			while (! Thread.interrupted()) {
				// loop until interrupted
				System.out.println(".");
				try {
					Thread.sleep(1000);
				}
				catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		} 
		finally {
			System.out.println("JMX stopped");
		}
	}
}
