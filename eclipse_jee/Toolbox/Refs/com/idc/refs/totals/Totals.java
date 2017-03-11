package com.idc.refs.totals;

import java.util.Iterator;

import com.idc.refs.data.ClasspathInfo;
import com.idc.refs.data.ClasspathItemInfo;
import com.idc.refs.data.ManifestmfInfo;
import com.idc.refs.data.ManifestmfItemInfo;
import com.idc.refs.data.ModulemapsInfo;
import com.idc.refs.data.ModulemapsItemInfo;
import com.idc.refs.data.NodeInfo;
import com.idc.refs.data.NodeItemInfo;
import com.idc.refs.data.ProjectInfo;
import com.idc.refs.data.ProjectItemInfo;

public class Totals {
	
	public static void handleTotals (NodeInfo nodeInfo) {
		Iterator<NodeItemInfo> iter = nodeInfo.getItems();
		while (iter.hasNext()) {
			NodeItemInfo nodeItemInfo = (NodeItemInfo) iter.next();
			nodeItemInfo.sort();
			handleProject (nodeItemInfo);
			handleClasspath (nodeItemInfo);
			handleManifest (nodeItemInfo);
			handleModulemaps (nodeItemInfo);
			nodeItemInfo.getTotalInfo().sort();
		}
	}
	private static void handleProject (NodeItemInfo nodeItemInfo) {
		TotalInfo totalInfo = nodeItemInfo.getTotalInfo();
		ProjectInfo projectInfo = nodeItemInfo.getProjectInfo();
		Iterator<ProjectItemInfo> iter = projectInfo.getItems();
		while (iter.hasNext()) {
			ProjectItemInfo projectItemInfo = (ProjectItemInfo) iter.next();
			totalInfo.add (projectItemInfo);
		}
	}
	private static void handleClasspath (NodeItemInfo nodeItemInfo) {
		TotalInfo totalInfo = nodeItemInfo.getTotalInfo();
		ClasspathInfo classpathInfo = nodeItemInfo.getClasspathInfo();
		Iterator<ClasspathItemInfo> iter = classpathInfo.getItems();
		while (iter.hasNext()) {
			ClasspathItemInfo classpathItemInfo = (ClasspathItemInfo) iter.next();
			totalInfo.add (classpathItemInfo);
		}
	}
	private static void handleManifest (NodeItemInfo nodeItemInfo) {
		TotalInfo totalInfo = nodeItemInfo.getTotalInfo();
		ManifestmfInfo manifestmfInfo = nodeItemInfo.getManifestmfInfo();
		Iterator<ManifestmfItemInfo> iter = manifestmfInfo.getItems();
		while (iter.hasNext()) {
			ManifestmfItemInfo manifestmfItemInfo = (ManifestmfItemInfo) iter.next();
			totalInfo.add (manifestmfItemInfo);
		}
	}
	private static void handleModulemaps (NodeItemInfo nodeItemInfo) {
		TotalInfo totalInfo = nodeItemInfo.getTotalInfo();
		ModulemapsInfo modulemapsInfo = nodeItemInfo.getModulemapsInfo();
		Iterator<ModulemapsItemInfo> iter = modulemapsInfo.getItems();
		while (iter.hasNext()) {
			ModulemapsItemInfo modulemapsItemInfo = (ModulemapsItemInfo) iter.next();
			totalInfo.add (modulemapsItemInfo);
		}
	}
}
