package com.idc.signal;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/*
 * To get refs recognized:

Go to the Build Path settings in the project properties.
Remove the JRE System Library
Add it back; Select "Add Library" and select the JRE System Library.

*/

public class SignalHandlerExample implements SignalHandler {

	private SignalHandler oldHandler;

	public static SignalHandler install (String signalName) {
		System.out.println(">>> SignalHandlerExample::install; signalName :"+signalName+":");
		Signal diagSignal = new Signal (signalName);
		SignalHandlerExample instance = new SignalHandlerExample();
		instance.oldHandler = Signal.handle (diagSignal, instance);
		System.out.println("<<< SignalHandlerExample::install");
		return instance;
	}

	public void handle (Signal signal) {
		System.out.println(">>> SignalHandlerExample::handle");
		System.out.println("Signal handler called for signal " + signal);
		try {
			signalAction (signal);
			if (oldHandler != SIG_DFL && oldHandler != SIG_IGN) {	// Chain back to previous handler, if one exists
				oldHandler.handle(signal);
			}
		}
		catch (Exception e) {
			System.out.println("handle|Signal handler failed, reason " 	+ e.getMessage());
			e.printStackTrace();
		}
		System.out.println("<<< SignalHandlerExample::handle");
	}

	public void signalAction (Signal signal) {
		System.out.println(">>> SignalHandlerExample::signalAction");
		System.out.println("Handling " + signal.getName());
		System.out.println("Just sleep for 5 seconds.");
		try {
			Thread.sleep(5000);
		}
		catch (InterruptedException e) {
			System.out.println("Interrupted: " + e.getMessage());
		}
		System.out.println("<<< SignalHandlerExample::signalAction");
	}

	public static void main (String[] args) {
		System.out.println(">>> SignalHandlerExample::main");
		SignalHandlerExample.install ("TERM");
//		SignalHandlerExample.install ("INT");
//		SignalHandlerExample.install ("ABRT");

		System.out.println("Signal handling example.");
		try {
			Thread.sleep(10000);
		}
		catch (InterruptedException e) {
			System.out.println("Interrupted: " + e.getMessage());
		}
		System.out.println("<<< SignalHandlerExample::main");
	}
}
