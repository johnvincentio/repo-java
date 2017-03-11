
package com.idc.loadtest;

import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.idc.trace.LogHelper;

public class AppThreads extends Thread {
	private static final long SLEEP_TIME = 5000L;
	private AppGui m_appGui;
	private int m_threads;
	private int m_repeat;
	private int m_delay;
	private String m_testFile;

	private ThreadGroup m_threadGroup = new ThreadGroup("Load Test Threads");
	private ArrayList<AppThreadItem> m_list = new ArrayList<AppThreadItem>();
	private Stages m_stages;

	private boolean m_bPleaseSuspendThisThread = true;
	private boolean m_bStop = false;

	public AppThreads (AppGui appGui, int threads, int repeat, int delay, String testFile) {
		LogHelper.info(">>> AppThreads (constructor); thread count "+getMyThreadGroup().activeCount());
		m_appGui = appGui;
		m_threads = threads;
		m_repeat = repeat;
		m_delay = delay;
		m_testFile = testFile;
		LogHelper.info("<<< AppThreads (constructor)");
	}
	public void setSuspend() {m_bPleaseSuspendThisThread = true;}
	public boolean isThreadHasBeenInstructedToStop() {return m_bStop;}
	private synchronized void setResume() {
		m_bPleaseSuspendThisThread = false;
		notify();
	}
	public void setStart () {
		m_bStop = false;
		setResume();
	}
	public void setStop() {
		addMessage ("Terminating");
		m_bStop = true;
	}
	public void run() {
		LogHelper.info(">>> AppThreads::run");

		startProgressIndicator();
		addMessage ("Working...");

		addMessage ("Parsing test cases from "+m_testFile);
		handleProgressIndicator();
		m_stages = (new JVxml()).parse (new File(m_testFile));
		LogHelper.info("stages :"+m_stages.toString());

		for (int threadId=1; threadId<=m_threads; threadId++) {
			AppThreadItem appThreadItem = new AppThreadItem (this, m_threadGroup, threadId, m_stages, m_repeat, m_delay);
			add (appThreadItem);
			appThreadItem.start();
		}

		addMessage ("Setting browser security");
		handleProgressIndicator();
		doSetupHttps();

		try {
			synchronized (this) {
				while (m_bPleaseSuspendThisThread) {
					LogHelper.info("AppThreads - suspending thread");
					wait();
				}
			}
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}	

		addMessage("Creating test threads");
		handleProgressIndicator();
		AppThreadItem appThreadItem;
		Iterator<AppThreadItem> iter = getItems();
		while (iter.hasNext()) {
			appThreadItem = (AppThreadItem) iter.next();
			appThreadItem.setStart();
		}

		addMessage("Starting test threads");
		handleProgressIndicator();
		int threadCount;
		while (true) {
			threadCount = getMyThreadGroup().activeCount();
			LogHelper.info("startThreads; thread count "+threadCount);
			if (threadCount < 1) break;
			if (isThreadHasBeenInstructedToStop()) {
				iter = getItems();
				while (iter.hasNext()) {
					appThreadItem = (AppThreadItem) iter.next();
					appThreadItem.setStop();
				}
			}
			try {
				LogHelper.info("waiting");
				Thread.sleep(SLEEP_TIME);
				LogHelper.info("woke up");
			}
			catch (InterruptedException iex) {
				LogHelper.info("InterruptedException "+iex.getMessage());
			}
		}

		addMessage("Test threads completed");
		endProgressIndicator();
		m_appGui.setStopped();
		LogHelper.info("<<< AppThreads::run");
	}
	
	public ThreadGroup getMyThreadGroup() {return m_threadGroup;}
	public Iterator<AppThreadItem> getItems() {return m_list.iterator();}
	public void add (AppThreadItem item) {m_list.add(item);}
	public int getSize() {return m_list.size();}
	public boolean isNone() {return getSize() < 1;}

	private AppGui getAppGui() {return m_appGui;}
	public void startProgressIndicator() {getAppGui().startProgressBar();}
	public void handleProgressIndicator() {getAppGui().handleProgressIndicator();}
	public void endProgressIndicator() {getAppGui().endProgressBar();}
	public void addMessage (String msg) {getAppGui().setMessagesArea(msg);}

	private void doSetupHttps() {
		LogHelper.info(">>> doSetupHttps");
		try {
		// disable SSL Certificate Authentication
		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
				LogHelper.info("Warning: URL Host: "+urlHostName+" vs. "+session.getPeerHost());
				return true;
			}
		};
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
		
		// create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[]{
				new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					public void checkClientTrusted(
							java.security.cert.X509Certificate[] certs, String authType) {
					}
					public void checkServerTrusted(
							java.security.cert.X509Certificate[] certs, String authType) {
					}
				}
		};
		
		// install the all-trusting trust manager
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		}
		catch (Exception ex) {
			LogHelper.error("exception "+ex.getMessage());
		}
		LogHelper.info("<<< doSetupHttps");
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((AppThreadItem) m_list.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
