package com.idc.diff.all.gui;

import java.io.File;
import java.util.Iterator;

import com.idc.diff.all.DiffFiles;
import com.idc.file.JVFile;
import com.idc.file.exec.ExecuteCommand;
import com.idc.file.exec.OutputLine;
import com.idc.file.exec.PrintFile;
import com.idc.trace.LogHelper;

public class Diffgui {
	private DiffallGui m_app;
	private OutputLine m_output;
	private OutputLine m_statistics = null;
	private long m_linesAdded;
	public Diffgui (DiffallGui app) {
		m_app = app;
		m_output = m_app.getMessagesArea();
	}

	public boolean isSearchStopped() {
//		LogHelper.info ("is search stopped; " + m_app.getAppThread().getStopStatus());
		return m_app.getAppThread().getStopStatus();
	}
	private void handleProgressIndicator() {m_app.handleProgressIndicator();}
	private void addMessage (String msg) {m_output.println(msg);}
	private void addStatistic (String msg) {m_statistics.println(msg);}

	public void doDiffdir (
			LogOptionsPanel.Options logOptions,
			StatisticsOptionsPanel.Options statisticsOptions,
			DirectoryOptionsPanel.Options directoryOptions,
			BasicOptionsPanel.Options basicOptions,
			CleaningOptionsPanel.Options cleaningOptions,
			CompareOptionsPanel.Options compareOptions,
			MessageOptionsPanel.Options messageOptions) {

		boolean bLogToApp = logOptions.isLogApp();
		addMessage("");
		addMessage ("Logging to App is turned " + (bLogToApp ? "on": "off"));
		if (bLogToApp)
			m_output = m_app.getMessagesArea();
		else
			m_output = new PrintFile (logOptions.getLogFile());

		boolean bStatistics = statisticsOptions.isStatistics();
		addMessage("");
		addMessage ("Statistics is turned " + (bStatistics ? "on": "off"));
		if (bStatistics)
			m_statistics = new PrintFile (statisticsOptions.getLogFile());

		File baseDirectory = directoryOptions.getBaseDirectory();
		File compareDirectory = directoryOptions.getCurrentDirectory();
		addMessage("");
		addMessage ("Base directory " + baseDirectory);
		addMessage ("Comparing directory " + compareDirectory);

		boolean bMessageShowMessages = messageOptions.isShowMessages();
		addMessage("");
		addMessage ("Show Messages is turned " + (bMessageShowMessages ? "on": "off"));

		boolean bCleaningMakeReadWrite = basicOptions.isMakeReadWrite();
		addMessage("");
		addMessage ("Make R/W privileges is turned " + (bCleaningMakeReadWrite ? "on": "off"));

		boolean bCleaningPerform = cleaningOptions.isCleaning();
		boolean bCleaningDeleteIgnoredFiles = cleaningOptions.isDeleteIgnoredFiles();
		addMessage("");
		addMessage ("Perform cleaning is turned " + (bCleaningPerform ? "on": "off"));
		addMessage ("Ignored files will be deleted is turned " + (bCleaningDeleteIgnoredFiles ? "on": "off"));

		boolean bCompareFiles = compareOptions.isCompareFiles();
		boolean bCompareDeleteIdentical = compareOptions.isDeleteIdentical();
		boolean bCompareShowDifferences = compareOptions.isShowDifferences();
		boolean bCompareIgnoreWhiteSpace = compareOptions.isIgnoreWhiteSpace();
		addMessage("");
		addMessage("Compare Files is turned " + (bCompareFiles ? "on": "off"));
		addMessage("Delete if identical is turned " + (bCompareDeleteIdentical ? "on": "off"));
		addMessage("Differences between non identical files is turned " + (bCompareShowDifferences ? "on": "off"));
		addMessage("White space ignored by the compare is turned " + (bCompareIgnoreWhiteSpace ? "on": "off"));

		addMessage("");
		addMessage ("Comparing file extensions:");
		Iterator<Extension> iter = m_app.getCompareExtensions().getItems();
		while (iter.hasNext()) {
			addMessage ("\t" + ((Extension) iter.next()).getExtension());
		}
		addMessage("");
		addMessage ("Byte Comparing file extensions:");
		iter = m_app.getByteCompareExtensions().getItems();
		while (iter.hasNext()) {
			addMessage ("\t" + ((Extension) iter.next()).getExtension());
		}
		addMessage("");
		addMessage ("Delete file extensions:");
		iter = m_app.getDeleteExtensions().getItems();
		while (iter.hasNext()) {
			addMessage ("\t" + ((Extension) iter.next()).getExtension());
		}

		/*
		 * Make all files read/write
		 */
		if (bCleaningMakeReadWrite) {
			addMessage("");
			addMessage ("Make R/W pass through "+baseDirectory);
			doMakeReadWriteDirectory (baseDirectory, bMessageShowMessages);
			addMessage("");
			addMessage ("Make R/W pass through "+compareDirectory);
			doMakeReadWriteDirectory (compareDirectory, bMessageShowMessages);
		}

		/*
		 * Perform cleaning
		 */
		if (bCleaningPerform) {
			addMessage("");
			addMessage("Cleaning pass through "+baseDirectory);
			doCleaningDirectory (baseDirectory, bCleaningDeleteIgnoredFiles, bMessageShowMessages);
			addMessage("");
			addMessage("Cleaning pass through "+compareDirectory);
			doCleaningDirectory (compareDirectory, bCleaningDeleteIgnoredFiles, bMessageShowMessages);
		}

		/*
		 * Perform compares
		 */
		if (bCompareFiles) {
			addMessage("");
			DiffFiles diffFiles = new DiffFiles();
			doDirectory (baseDirectory, compareDirectory, compareDirectory, diffFiles, 
					bCompareDeleteIdentical, bCompareShowDifferences, bCompareIgnoreWhiteSpace, bMessageShowMessages);

			addMessage("");
			addMessage("Cleaning pass through "+baseDirectory);
			doCleaningDirectory (baseDirectory, bCleaningDeleteIgnoredFiles, bMessageShowMessages);
			addMessage("");
			addMessage("Cleaning pass through "+compareDirectory);
			doCleaningDirectory (compareDirectory, bCleaningDeleteIgnoredFiles, bMessageShowMessages);
		}

		/*
		 * Perform statistics
		 */
		if (bStatistics) {
			m_linesAdded = 0;
			addStatistic ("");
			addStatistic ("Files added to "+compareDirectory);
			addStatistic ("");
			DiffFiles diffFiles = new DiffFiles();
			doStatisticsAdded (baseDirectory, compareDirectory, compareDirectory, diffFiles);
			addStatistic ("");
			addStatistic (m_linesAdded+" lines of code have been added");
			addStatistic ("Completed list of files added to "+compareDirectory);
			addStatistic ("");

			m_linesAdded = 0;
			addStatistic ("");
			addStatistic ("Files added to "+baseDirectory);
			addStatistic ("");
			diffFiles = new DiffFiles();
			doStatisticsAdded (compareDirectory, baseDirectory, baseDirectory, diffFiles);
			addStatistic ("");
			addStatistic (m_linesAdded+" lines of code have been added");
			addStatistic ("Completed list of files added to "+baseDirectory);
			addStatistic ("");

			addStatistic ("");
			addStatistic ("Files updated to "+compareDirectory);
			addStatistic ("");
			diffFiles = new DiffFiles();
			doStatisticsUpdated (baseDirectory, compareDirectory, compareDirectory, diffFiles);
			addStatistic ("");
			addStatistic ("Completed list of files updated to "+compareDirectory);
			addStatistic ("");
		}

		m_output.close();
		if (m_statistics != null) m_statistics.close();
	}

	private void doMakeReadWriteDirectory (File currentDir, boolean bShowMessages) {
		if (isSearchStopped()) return; // user stopped the search
		LogHelper.info (">>> doMakeReadWriteDirectory; " + currentDir.getPath());
		if (! currentDir.isDirectory()) return;

		handleProgressIndicator();
		File[] allFiles = currentDir.listFiles();
		for (int i = 0; i < allFiles.length; i++) {
			if (isSearchStopped()) return; // user stopped the search
			File file = allFiles[i];
			if (file.isFile() && ! file.canWrite())
				doMakeReadWrite (file.getPath());
		}

		handleProgressIndicator();
		for (int i = 0; i < allFiles.length; i++) {
			if (isSearchStopped()) return; // user stopped the search
			File file = allFiles[i];
			if (file.isDirectory())
				doMakeReadWriteDirectory (file, bShowMessages);
		}

		allFiles = null;
		LogHelper.info ("<<< doMakeReadWriteDirectory; " + currentDir.getPath());
	}

	private void doCleaningDirectory (File currentDir, boolean bDelIgnoredFiles, boolean bShowMessages) {
		if (isSearchStopped()) return; // user stopped the search
		LogHelper.info (">>> doCleaningDirectory; " + currentDir.getPath());
		if (! currentDir.isDirectory()) return;

		handleProgressIndicator();
		if (handleJunkDirectory (currentDir, bShowMessages)) return;

		handleProgressIndicator();
		File[] allFiles = currentDir.listFiles();
		for (int i = 0; i < allFiles.length; i++) {
			if (isSearchStopped()) return; // user stopped the search
			File file = allFiles[i];
			if (file.isFile()) {
				if (isDeleteType(file.getName())) {
					removeFile (file, bShowMessages);
					continue;
				}
				if (! bDelIgnoredFiles) continue;
				if (isCompareType(file.getName())) continue;
				if (isByteCompareType(file.getName())) continue;
				removeIgnoredFile(file, bShowMessages);
			}
		}

		handleProgressIndicator();
		for (int i = 0; i < allFiles.length; i++) {
			if (isSearchStopped()) return; // user stopped the search
			File file = allFiles[i];
			if (file.isDirectory())
				doCleaningDirectory (file, bDelIgnoredFiles, bShowMessages);
		}

		handleProgressIndicator();
		if (currentDir.listFiles().length < 1) removeDirectory(currentDir, bShowMessages);

		allFiles = null;
		LogHelper.info ("<<< doCleaningDirectory; " + currentDir.getPath());
	}

	private void doDirectory (File baseDir, File compareDir, File currentDir, DiffFiles diffFiles, 
			boolean bDeleteIdentical, boolean bShowDifferences, boolean bIgnoreWhiteSpace, boolean bShowMessages) {
//		LogHelper.info(">>> doDirectory");
		handleProgressIndicator();
		File[] allFiles = currentDir.listFiles();
		for (int i = 0; i < allFiles.length; i++) {
			if (isSearchStopped()) return; // user stopped the search
			File file = allFiles[i];
			if (file.isFile()) {
				String newFile = file.getPath();
				String baseFile = baseDir.getPath() + file.getPath().substring(compareDir.getPath().length());
				if (bShowMessages) {
					addMessage(" ");
					addMessage ("Comparing: (< = baseline) " + baseFile);
					addMessage ("with: (> = compare) " + newFile);
				}
				if (! (new File(baseFile)).isFile()) {
					if (bShowMessages) addMessage ("Not found in the baseline: " + newFile);
				}
				else {
					if (diffFiles.compareFiles (newFile, baseFile)) {
						if (bShowMessages) addMessage ("Identical");
						if (bDeleteIdentical) {
							removeFile (file, bShowMessages);
							removeFile (new File(baseFile), bShowMessages);
						}
					} else {
						if (bShowMessages) addMessage ("Different");
						if (bShowDifferences && isCompareType(baseFile)) doCompare (baseFile, newFile, bIgnoreWhiteSpace);
					}
				}
			}
			file = null;
		}

		handleProgressIndicator();
		for (int i = 0; i < allFiles.length; i++) {
			if (isSearchStopped()) return; // user stopped the search
			File file = allFiles[i];
			if (file.isDirectory())
				doDirectory(baseDir, compareDir, file, diffFiles, bDeleteIdentical, bShowDifferences, bIgnoreWhiteSpace, bShowMessages);
		}

		handleProgressIndicator();
		if (currentDir.listFiles().length < 1) removeDirectory(currentDir, bShowMessages);

		allFiles = null;
//		LogHelper.info("<<< doDirectory");
	}

	private void doStatisticsAdded (File baseDir, File compareDir, File currentDir, DiffFiles diffFiles) {
		LogHelper.info(">>> doStatisticsAdded");
		
		handleProgressIndicator();
		File[] allFiles = currentDir.listFiles();
		for (int i = 0; i < allFiles.length; i++) {
			if (isSearchStopped()) return; // user stopped the search
			File file = allFiles[i];
			if (file.isFile()) {
				String newFile = file.getPath();
				String baseFile = baseDir.getPath() + file.getPath().substring(compareDir.getPath().length());
				if (! (new File(baseFile)).isFile()) {
					addStatistic (newFile);
					m_linesAdded += JVFile.getFileLineCount (newFile);
				}
			}
			file = null;
		}

		handleProgressIndicator();
		for (int i = 0; i < allFiles.length; i++) {
			if (isSearchStopped()) return; // user stopped the search
			File file = allFiles[i];
			if (file.isDirectory())
				doStatisticsAdded (baseDir, compareDir, file, diffFiles);
		}

		handleProgressIndicator();

		allFiles = null;
		LogHelper.info("<<< doStatisticsAdded");
	}

	private void doStatisticsUpdated (File baseDir, File compareDir, File currentDir, DiffFiles diffFiles) {
		LogHelper.info(">>> doStatisticsUpdated");
		
		handleProgressIndicator();
		File[] allFiles = currentDir.listFiles();
		for (int i = 0; i < allFiles.length; i++) {
			if (isSearchStopped()) return; // user stopped the search
			File file = allFiles[i];
			if (file.isFile()) {
				String newFile = file.getPath();
				String baseFile = baseDir.getPath() + file.getPath().substring(compareDir.getPath().length());
				if (! (new File(baseFile)).isFile()) continue;
				addStatistic (newFile);
			}
			file = null;
		}

		handleProgressIndicator();
		for (int i = 0; i < allFiles.length; i++) {
			if (isSearchStopped()) return; // user stopped the search
			File file = allFiles[i];
			if (file.isDirectory())
				doStatisticsUpdated (baseDir, compareDir, file, diffFiles);
		}

		handleProgressIndicator();

		allFiles = null;
		LogHelper.info("<<< doStatisticsUpdated");
	}

	private void doMakeReadWrite (String baseFile) {
//		LogHelper.info(">>> doMakeReadWrite; baseFile :"+baseFile+":");		
		ExecuteCommand exec = new ExecuteCommand();
		String[] strCmd = { "attrib", "-R", baseFile };
		exec.executeCommand(strCmd, m_output);
		exec = null;
//		LogHelper.info("<<< doMakeReadWrite");
	}

	private void doCompare (String baseFile, String newFile, boolean bIgnoreWhiteSpace) {
		System.out.println (">>> doCompare; baseFile :"+baseFile+": newFile :"+newFile+":");		
		ExecuteCommand exec = new ExecuteCommand();
		if (ExecuteCommand.isWindows()) {
			System.out.println("Windows compare");
			if (bIgnoreWhiteSpace) {
				String[] strCmd = { "fc", "/W", "/N", baseFile, newFile };
				exec.executeCommand (strCmd, m_output);
			} else {
				String[] strCmd = { "fc", "/N", baseFile, newFile };
				exec.executeCommand (strCmd, m_output);
			}
			exec = null;
		}
		else {
			if (bIgnoreWhiteSpace) {
				System.out.println("Unix compare");
				String[] strCmd = { "diff", "-w", "--strip-trailing-cr", baseFile, newFile };
				exec.executeCommand (strCmd, m_output);
			} else {
				String[] strCmd = { "diff", "--strip-trailing-cr", baseFile, newFile };
				exec.executeCommand (strCmd, m_output);
			}
			exec = null;
		}
		System.out.println ("<<< doCompare");
	}

	private void removeDirectory(File dir, boolean bShowMessages) {
		if (bShowMessages) addMessage("Deleted directory; " + dir.getPath());
		dir.delete();
	}

	private void removeFile(File file, boolean bShowMessages) {
		if (bShowMessages) addMessage("Deleted file; " + file.getPath());
		file.delete();
	}

	private void removeIgnoredFile(File file, boolean bShowMessages) {
		if (bShowMessages) addMessage("Deleted ignored file; " + file.getPath());
		file.delete();
	}

	private boolean handleJunkDirectory (File file, boolean bShowMessages) { // true if .svn, .metadata
		String name = file.getName();
		if (name == null) return false;
		if ((! name.equals(".svn")) && (! name.equals(".metadata"))) return false;
		deleteNonEmptyDirectory(file, bShowMessages);
		return true;
	}

	public boolean deleteNonEmptyDirectory(File path, boolean bShowMessages) {
		if (path.exists()) {
			if (bShowMessages) addMessage("Deleted junk directory ; " + path.getPath());
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory())
					deleteNonEmptyDirectory (files[i], bShowMessages);
				else
					files[i].delete();
			}
			files = null;
		}
		return (path.delete());
	}

	private boolean isCompareType(String strFile) {
		return m_app.getCompareExtensions().isMatchAndChecked(strFile);
	}
	private boolean isByteCompareType(String strFile) {
		return m_app.getByteCompareExtensions().isMatchAndChecked(strFile);
	}
	private boolean isDeleteType(String strFile) {
		return m_app.getDeleteExtensions().isMatchAndChecked(strFile);
	}
}
