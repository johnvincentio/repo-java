package com.idc.jmx;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class ThreadMonitorConsole {

	public static void main(String[] args) {
		JMXConnector jmxc = null;
		try {
			System.out.println("Connect to JMX service.");
			String host = "localhost";
			String port = "9999";
			JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi");
			System.out.println("stage 1");
			System.out.println("url "+url.getURLPath());
			jmxc = JMXConnectorFactory.connect (url, null);
			System.out.println("stage 2");
			System.out.println("jmxc "+jmxc.toString());
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
			System.out.println("stage 3");
			System.out.println("mbsc "+mbsc.toString());

			// Construct proxy for the the MBean object
			ObjectName mbeanName = new ObjectName ("com.idc.jmx:type=ThreadMonitor");
			System.out.println("stage 4");
			ThreadMonitorMBean mbeanProxy = JMX.newMBeanProxy (mbsc, mbeanName, ThreadMonitorMBean.class, true);
			System.out.println("stage 5");
			if (mbeanProxy == null) System.out.println("mbeanProxy is null");
			System.out.println("stage 6");

			System.out.println("Connected to: " + mbeanProxy.getName() + ", the app is " + (mbeanProxy.isRunning() ? "" : "not ") + "running");

			// parse command line arguments
			if (args[0].equalsIgnoreCase("start")) {
				System.out.println("Invoke \"start\" method");
				mbeanProxy.start();
			}
			else if (args[0].equalsIgnoreCase("stop")) {
				System.out.println("Invoke \"stop\" method");
				mbeanProxy.stop();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			// clean up and exit
			try {
				if (jmxc != null) jmxc.close();
			}
			catch (Exception ex) {}
			System.out.println("Done.");			
		}
	}
}
