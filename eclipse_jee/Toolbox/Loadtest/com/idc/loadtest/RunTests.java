
package com.idc.loadtest;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import com.idc.file.JVFile;
import com.idc.http.AppCookies;
import com.idc.http.AppException;
import com.idc.http.Browser;
import com.idc.http.Form;
import com.idc.http.FormItem;
import com.idc.http.HttpMessage;
import com.idc.http.Receiver;
import com.idc.http.Sender;
import com.idc.trace.LogHelper;

public class RunTests {
	private AppThreadItem m_appThreadItem;
	private AppThreads m_appThreads;
	private Stages m_stages;
	public RunTests (AppThreadItem appThreadItem, AppThreads appThreads, Stages stages) {
		m_appThreadItem = appThreadItem;
		m_appThreads = appThreads;
		m_stages = stages;
	}
	public boolean isThreadHasBeenInstructedToStop() {
		LogHelper.info("if (isThreadHasBeenInstructedToStop()) "+m_appThreadItem.isThreadHasBeenInstructedToStop());
		return m_appThreadItem.isThreadHasBeenInstructedToStop();
	}
	private void handleProgressIndicator() {m_appThreads.handleProgressIndicator();}
	private void addMessage (String msg) {m_appThreads.addMessage(msg);}
	public void runTests (int threadId) {
		AppCookies appCookies = new AppCookies();
		Stage stage;
		Iterator<Stage> iter = m_stages.getItems();
		while (iter.hasNext()) {
			stage = (Stage) iter.next();
			LogHelper.info("---runTests::runTests; stage "+stage);
			if (isThreadHasBeenInstructedToStop()) return;
			addMessage ("("+threadId+") Running test case "+stage.getMessage());
			handleProgressIndicator();
			runTest (appCookies, m_stages.getUrlNoPort(), stage);
		}
		addMessage ("("+threadId+") Test cases complete");
	}
	private void runTest(AppCookies appCookies, String url, Stage stage) {
		Sender sender;
		Receiver receiver;
//		String strURL = url + "/" + stage.getUrl();
		String strURL = url + stage.getUrl();
		try {
			sender = new Sender(strURL);
			if (stage.isPost())
				sender.setPostMethod();
			else
				sender.setGetMethod();
			Form form = stage.getForm();
			FormItem formItem;
			Iterator<FormItem> iter = form.getItems();
			while (iter.hasNext()) {
				formItem = (FormItem) iter.next();
				sender.addFormItem (formItem.getKey(), formItem.getValue());
			}
	
			LogHelper.info("sender: "+sender.toString());
			receiver = HttpMessage.getReceiverWithRedirects (sender, appCookies);
			LogHelper.info("receiver: "+receiver.toString());

			if (isThreadHasBeenInstructedToStop()) return;
			if (stage.isBrowser()) runBrowser (receiver);
		}
		catch (AppException appex) {
			LogHelper.info("App exception "+appex.getMessage());
		}
	}

	private void runBrowser (Receiver receiver) {
		String strFile = "";
		try {
			File file = File.createTempFile ("Load_Testing_", ".html", new File("c:\\tmp"));
			strFile = file.getAbsolutePath();
		}
		catch (IOException ioex) {
			LogHelper.info("runBrowser::IOException");
		}

		addMessage ("Running browser with file "+strFile);
		handleProgressIndicator();
		try {
			JVFile.writeFile(receiver.getBody().toString(), strFile);
			String strCmd = "C:\\Program Files\\Internet Explorer\\iexplore.exe";
			strCmd = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
			String[] cmd = {strCmd, strFile};
			Browser.start(cmd);
		}
		catch (AppException appex) {
			LogHelper.info("runBrowser::AppException "+appex.getMessage());
		}
	}
}
