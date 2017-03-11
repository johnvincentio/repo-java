package com.idc.between;

import java.io.File;
import com.idc.file.JVFile;
import com.idc.trace.LogHelper;

public class TheApp extends TheAppTemplate {
	private FilesBetweenGui m_app;
	private long m_cntr = 0;
	public TheApp (FilesBetweenGui app) {m_app = app;}
	private boolean m_bAppOver = false;
	public boolean isAppOver() {return m_bAppOver;}
	public void setAppOver (boolean b) {m_bAppOver = b;}
	
	public boolean isTheAppStopped() {return m_app.getAppThread().getStopStatus();}
	public void setStarted() {m_app.setButtonText(false);}
	public void setStopped() {m_app.setButtonText(true);}

	public void addMessage (String msg) {m_app.addMessage(msg);}
	public void setProgressMessage (String msg) {m_app.setProgressMessage(msg);}

	public void initProgressBar (int iMin, int iMax) {m_app.initProgressBar(iMin, iMax);}
	public void setProgressBar (int value) {m_app.setProgressBar(value);}
	public void setProgressBar () {m_app.setProgressBar();}
	public void setMaxProgressBar () {m_app.setMaxProgressBar();}
	
	public void doTask() {
		LogHelper.info(">>> doTask");
		File file = new File (m_app.getOutputDirField());
		if (! file.isDirectory()) file.mkdir();

		String strSearchDir = m_app.getSearchDirField();
		String strStartDate = m_app.getStartDateField();
		String strEndDate = m_app.getEndDateField();
		
		setProgressMessage("searching...");
		initProgressBar(0,100);
		addMessage("");
		addMessage("Searching from "+strSearchDir);
		addMessage("    for files dated between "+strStartDate+" and "+strEndDate);
		addMessage("");

		long lStart = AppTasks.getDateFieldValue(m_app.getStartDateField());
		long lEnd = AppTasks.getDateFieldValue(m_app.getEndDateField());

		doDirectory (new File(strSearchDir), lStart, lEnd);

		if (isTheAppStopped()) {
			addMessage("---------------------------------------");
			addMessage("Search stopped by user");
			addMessage("---------------------------------------");
			setProgressMessage("Stopped...");
		}
		else {
			addMessage("---------------------------------------");
			addMessage("Search is complete");
			addMessage("---------------------------------------");
			setMaxProgressBar();
			setProgressMessage("Finished...");
		}
		LogHelper.info("<<< doTask");
	}

	private void doDirectory (final File dir, long lStart, long lEnd) {
		if (isTheAppStopped()) return;        // user stopped the search
//		LogHelper.info(">>> doDirectory "+dir.getPath());
		if (! dir.isDirectory()) return;
//		if (! isDirectoryOK(dir, "/home/jv")) return;

		File file;
		File[] allFiles = dir.listFiles();
		for (int i=0; i<allFiles.length; i++) {
			file = allFiles[i];
			if (! file.isFile()) continue;
			if (file.lastModified() > lStart && file.lastModified() < lEnd) {
				String strToFile = getToFilepath (file, m_app.getOutputDirField());
//				LogHelper.info("File "+file.getPath()+" modified "+file.lastModified()+" copied to "+strToFile);
				addMessage("File "+file.getPath()+" modified "+file.lastModified()+" copied to "+strToFile);
				JVFile.copyFile(file.getPath(), strToFile);
			}
		}
		for (int i=0; i<allFiles.length; i++) {
			file = allFiles[i];
			if (file.isDirectory()) doDirectory(file, lStart, lEnd);
		}
//		LogHelper.info("<<< doDirectory");
	}
	private String getToFilepath (File file, String strDir) {
		String strName = JVFile.getName(file.getName());
		String strExt = JVFile.getExtension(file.getName());
		return m_app.getOutputDirField() + File.separatorChar + strName + "_" + ++m_cntr + "." + strExt;
	}
}
