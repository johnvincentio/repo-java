package com.idc.file.clean.svn.gui;

import java.io.File;

import com.idc.file.exec.OutputLine;
import com.idc.trace.LogHelper;

public class FilesClean {
	private FilesCleanGui m_app;
	private OutputLine m_output;
	public FilesClean (FilesCleanGui app) {
		m_app = app;
		m_output = m_app.getMessagesArea();
	}

	public boolean isSearchStopped() {
		LogHelper.info("is search stopped; " + m_app.getAppThread().getStopStatus());
		return m_app.getAppThread().getStopStatus();
	}
	private void handleProgressIndicator() {m_app.handleProgressIndicator();}
	private void addMessage(String msg) {m_output.println(msg);}

	public void doApp (File dir) {
		addMessage("");
		addMessage("Directory " + dir);

		addMessage("");
		addMessage("Cleaning pass through "+dir);
		doCleaningDirectory (dir);
	}

	private void doCleaningDirectory (File currentDir) {
		if (isSearchStopped()) return; // user stopped the search
		LogHelper.info(">>> doCleaningDirectory; " + currentDir.getPath());

		if (! currentDir.isDirectory()) return;

		handleProgressIndicator();
		if (handleJunkDirectory (currentDir)) return;

		handleProgressIndicator();
		File[] allFiles = currentDir.listFiles();

		for (int i = 0; i < allFiles.length; i++) {
			if (isSearchStopped()) return; // user stopped the search
			File file = allFiles[i];
			if (file.isDirectory())
				doCleaningDirectory (file);
		}

		handleProgressIndicator();
		if (currentDir.listFiles().length < 1) removeDirectory(currentDir);
		allFiles = null;

		LogHelper.info("<<< doCleaningDirectory; " + currentDir.getPath());
	}

	private void removeDirectory(File dir) {
		addMessage("Deleted directory; " + dir.getPath());
		dir.delete();
	}

	private void removeFile(File file) {
		addMessage("Deleted file; " + file.getPath());
		file.delete();
	}

	private boolean handleJunkDirectory (File file) { // true if .svn, .metadata
		String name = file.getName();
		if (name == null) return false;
		if ((! name.equals(".svn")) && (! name.equals(".metadata"))) return false;
		deleteNonEmptyDirectory(file);
		return true;
	}

	public boolean deleteNonEmptyDirectory(File path) {
		if (path.exists()) {
			addMessage("Deleted junk directory ; " + path.getPath());
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory())
					deleteNonEmptyDirectory (files[i]);
				else
					removeFile (files[i]);
			}
			files = null;
		}
		return (path.delete());
	}
}
