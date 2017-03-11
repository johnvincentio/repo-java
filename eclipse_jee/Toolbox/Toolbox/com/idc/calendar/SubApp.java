package com.idc.calendar;

public class SubApp {
	private App m_app;
	public SubApp  (App app) {m_app = app;}
	public boolean isStopped() {
		return m_app.getAppThread().getStopStatus();
	}
//	private void handleProgressIndicator() {m_app.handleProgressIndicator();}
//	private void addMessage (String msg) {m_app.setMessagesArea(msg);}
	public void doApp (String strYear, String strFile) {
		MyBook book = new MyBook(strYear, strFile);
		book.makeBook();
	}
}
