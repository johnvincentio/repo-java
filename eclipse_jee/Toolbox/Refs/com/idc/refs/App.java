package com.idc.refs;

import java.io.File;

import com.idc.refs.buckets.BucketInfo;
import com.idc.refs.buckets.Buckets;
import com.idc.refs.data.ClasspathInfo;
import com.idc.refs.data.ManifestmfInfo;
import com.idc.refs.data.ModulemapsInfo;
import com.idc.refs.data.NodeInfo;
import com.idc.refs.data.NodeItemInfo;
import com.idc.refs.data.ProjectInfo;
import com.idc.refs.totals.Totals;

public class App {
	private static final File FILEDIR = new File ("c:/development/herc_10_06");
//	private static final File FILEDIR = new File ("c:/work3/exported_irac");
//	private static final File FILEDIR = new File ("c:/work2/irac_10_05");
	public static final File REPORTSDIR = new File ("c:/jvWork1");
	private static final boolean[] FLAGS = {true, true, true, true, true, true};

	private NodeInfo m_nodeInfo = new NodeInfo();
	public static void main (String[] args) {
		(new App()).doIt();
	}
	private void doIt() {
		System.out.println("Create reports directory");
		if (! REPORTSDIR.exists()) REPORTSDIR.mkdir();

		System.out.println("Parse configuration files");
		doDirectory (FILEDIR);
//		System.out.println (m_nodeInfo);
		System.out.println("Make totals");
		Totals.handleTotals (m_nodeInfo);

		if (FLAGS[0]) {
			System.out.println("Report 1");
			Reports.showReport1 (m_nodeInfo);
		}
		if (FLAGS[1]) {
			System.out.println("Report 2");
			Reports.showReport2 (m_nodeInfo);
		}
		if (FLAGS[2]) {
			System.out.println("Make buckets");
			int maxLevel = 6;
			BucketInfo bucketInfo = Buckets.handleBuckets (m_nodeInfo, maxLevel);
			System.out.println("Report 3");
			Reports.showReport3 (bucketInfo, maxLevel);
		}
		if (FLAGS[3]) {
			System.out.println("Report 4");
			Reports.showReport4 (m_nodeInfo);
		}
		if (FLAGS[4]) {
			System.out.println("Report 5");
			int maxLevel = 8;
			Reports.showReport5 (m_nodeInfo, maxLevel);
		}
		if (FLAGS[5]) {
			System.out.println("Report 5");
			Reports.showReport6 (m_nodeInfo);
		}
		System.out.println("App Completed");
	}

	private void doDirectory (File dir) {
//		System.out.println(">>> doDirectory; "+dir.getPath());
		if (! dir.isDirectory()) return;

		File[] allFiles = dir.listFiles();
		for (int i = 0; i < allFiles.length; i++) {
			File file = allFiles[i];
			if (file.isDirectory()) {
				int type = Utils.determineType (file);
				if (type != Utils.TYPE_UNKNOWN) handleDirectory (file, type);
			}
//			if (i > 20) break;			//TODO; remove this
		}
//		System.out.println("<<< doDirectory");
	}

//TODO; write out data - which format? makes classes? try this..make some diagrams....
//TODO; parse through data, look for dangling projects
	private void handleDirectory (File dir, int type) {
//		System.out.println(">>> handleDirectory; "+dir.getPath());
		ProjectInfo projectInfo = null;
		ClasspathInfo classpathInfo = null;
		ManifestmfInfo manifestmfInfo = new ManifestmfInfo();
		ModulemapsInfo modulemapsInfo = new ModulemapsInfo();
		switch (type) {
			case Utils.TYPE_EAR:
				projectInfo = Utils.getDotProject (dir, ".project");
				classpathInfo = Utils.getDotClasspath (dir, ".classpath");
				modulemapsInfo = Utils.getModulemaps (Utils.makeFile (dir, "META-INF", ".modulemaps"));
				break;
			case Utils.TYPE_EJB:
				projectInfo = Utils.getDotProject (dir, ".project");
				classpathInfo = Utils.getDotClasspath (dir, ".classpath");
				manifestmfInfo = Utils.getManifestmf (Utils.makeFile (dir, "ejbModule", "META-INF", "MANIFEST.MF"));
				break;
			case Utils.TYPE_EJBCLIENT:
				projectInfo = Utils.getDotProject (dir, ".project");
				classpathInfo = Utils.getDotClasspath (dir, ".classpath");
				manifestmfInfo = Utils.getManifestmf (Utils.makeFile (dir, "ejbModule", "META-INF", "MANIFEST.MF"));
				break;
			case Utils.TYPE_WEB:
				projectInfo = Utils.getDotProject (dir, ".project");
				classpathInfo = Utils.getDotClasspath (dir, ".classpath");
				manifestmfInfo = Utils.getManifestmf (Utils.makeFile (dir, "WebContent", "META-INF", "MANIFEST.MF"));
				break;
			case Utils.TYPE_UTIL:
				projectInfo = Utils.getDotProject (dir, ".project");
				classpathInfo = Utils.getDotClasspath (dir, ".classpath");
				manifestmfInfo = Utils.getManifestmf (Utils.makeFile (dir, "META-INF", "MANIFEST.MF"));
				break;
			case Utils.TYPE_UNKNOWN:
			default:
				break;
		}
		NodeItemInfo nodeItemInfo = new NodeItemInfo (dir.getName(), dir, type, 
				projectInfo, classpathInfo, manifestmfInfo, modulemapsInfo);
		m_nodeInfo.add (nodeItemInfo);
		m_nodeInfo.sort();
//		System.out.println("<<< handleDirectory; "+dir.getPath());
	}
}
