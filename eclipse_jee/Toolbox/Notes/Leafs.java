package com.idc.refs;

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

public class Leafs {

	public static BucketInfo handleLeafs (NodeInfo nodeInfo) {
		BucketInfo bucketInfo = new BucketInfo();
		Iterator iter = nodeInfo.getItems();
		while (iter.hasNext()) {
			NodeItemInfo nodeItemInfo = (NodeItemInfo) iter.next();
			if (nodeItemInfo == null) continue;
			BucketItemInfo bucketItemInfo = bucketInfo.getItem (nodeItemInfo.getName());
			if (bucketItemInfo == null) {
				bucketItemInfo = new BucketItemInfo (nodeItemInfo.getName());
				bucketInfo.add (bucketItemInfo);
			}
			int counter = 1;
			handleNodeItemInfo (counter, nodeInfo, bucketItemInfo, nodeItemInfo);
			bucketItemInfo.sortBucket();
		}
		return bucketInfo;
	}

	private static void handleNodeItemInfo (int counter, NodeInfo nodeInfo, BucketItemInfo bucketItemInfo, NodeItemInfo currentNodeItemInfo) {
		counter++;
		if (counter > 6) return;
		handleProjectInfo (counter, nodeInfo, bucketItemInfo, currentNodeItemInfo.getProjectInfo());
		handleClasspathInfo (counter, nodeInfo, bucketItemInfo, currentNodeItemInfo.getClasspathInfo());
		handleManifestmfInfo (counter, nodeInfo, bucketItemInfo, currentNodeItemInfo.getManifestmfInfo());
		handleModulemapsInfo (counter, nodeInfo, bucketItemInfo, currentNodeItemInfo.getModulemapsInfo());
	}
	private static void handleProjectInfo (int counter, NodeInfo nodeInfo, BucketItemInfo bucketItemInfo, ProjectInfo projectInfo) {
		Iterator iter = projectInfo.getItems();
		while (iter.hasNext()) {
			ProjectItemInfo projectItemInfo = (ProjectItemInfo) iter.next();
			if (projectItemInfo == null) continue;
			bucketItemInfo.addToBucket (projectItemInfo);
			String name = projectItemInfo.getName();
			NodeItemInfo nodeItemInfo = nodeInfo.getItem (name);
			if (nodeItemInfo != null)
				handleNodeItemInfo (counter, nodeInfo, bucketItemInfo, nodeItemInfo);
		}
	}
	private static void handleClasspathInfo (int counter, NodeInfo nodeInfo, BucketItemInfo bucketItemInfo, ClasspathInfo classpathInfo) {
		Iterator iter = classpathInfo.getItems();
		while (iter.hasNext()) {
			ClasspathItemInfo classpathItemInfo = (ClasspathItemInfo) iter.next();
			if (classpathItemInfo == null) continue;
			bucketItemInfo.addToBucket (classpathItemInfo);
			String name = classpathItemInfo.getName();
			NodeItemInfo nodeItemInfo = nodeInfo.getItem (name);
			if (nodeItemInfo != null)
				handleNodeItemInfo (counter, nodeInfo, bucketItemInfo, nodeItemInfo);
		}
	}
	private static void handleManifestmfInfo (int counter, NodeInfo nodeInfo, BucketItemInfo bucketItemInfo, ManifestmfInfo manifestmfInfo) {
		Iterator iter = manifestmfInfo.getItems();
		while (iter.hasNext()) {
			ManifestmfItemInfo manifestmfItemInfo = (ManifestmfItemInfo) iter.next();
			if (manifestmfItemInfo == null) continue;
			bucketItemInfo.addToBucket (manifestmfItemInfo);
			String name = manifestmfItemInfo.getName();
			NodeItemInfo nodeItemInfo = nodeInfo.getItem (name);
			if (nodeItemInfo != null)
				handleNodeItemInfo (counter, nodeInfo, bucketItemInfo, nodeItemInfo);
		}
	}
	private static void handleModulemapsInfo (int counter, NodeInfo nodeInfo, BucketItemInfo bucketItemInfo, ModulemapsInfo modulemapsInfo) {
		Iterator iter = modulemapsInfo.getItems();
		while (iter.hasNext()) {
			ModulemapsItemInfo modulemapsItemInfo = (ModulemapsItemInfo) iter.next();
			if (modulemapsItemInfo == null) continue;
			bucketItemInfo.addToBucket (modulemapsItemInfo);
			String name = modulemapsItemInfo.getName();
			NodeItemInfo nodeItemInfo = nodeInfo.getItem (name);
			if (nodeItemInfo != null)
				handleNodeItemInfo (counter, nodeInfo, bucketItemInfo, nodeItemInfo);
		}
	}
}
