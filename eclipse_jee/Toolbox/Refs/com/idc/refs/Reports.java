package com.idc.refs;

import java.io.File;
import java.util.Iterator;

import com.idc.file.exec.PrintFile;
import com.idc.refs.buckets.BucketInfo;
import com.idc.refs.buckets.BucketItemInfo;
import com.idc.refs.buckets.SubBucketInfo;
import com.idc.refs.buckets.SubBucketItemInfo;
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
import com.idc.refs.totals.TotalInfo;
import com.idc.refs.totals.TotalItemInfo;

public class Reports {
	private static final String TAB = "\t";
	private static String showBoolean (boolean bool) {return bool ? "Y" : "N";}
	private static String showInt (int counter) {return Integer.toString(counter);}

	private static void repeatString (PrintFile output, String msg, int count) {
		for (int i = 0; i < count; i++) output.print (msg);
	}

	public static void showReport1 (NodeInfo nodeInfo) {
		File file = new File (App.REPORTSDIR+"/report1.lis");
		if (file.exists()) file.delete();
		PrintFile output = new PrintFile(file);
		output.println();
		output.println ("Report1 - For each project, list referenced projects by referencing file");
		output.println();
		Iterator<NodeItemInfo> iterator = nodeInfo.getItems();
		while (iterator.hasNext()) {
			NodeItemInfo nodeItemInfo = (NodeItemInfo) iterator.next();
			output.println();
			output.println (nodeItemInfo.getName());

			{
				output.println();
				output.println(TAB+".project");
				ProjectInfo projectInfo = nodeItemInfo.getProjectInfo();
				Iterator<ProjectItemInfo> iter = projectInfo.getItems();
				while (iter.hasNext()) {
					ProjectItemInfo item = (ProjectItemInfo) iter.next();
					if (item == null) continue;
					output.println (TAB+TAB+item.getName());
				}
			}

			{
				output.println();
				output.println(TAB+".classpath");
				ClasspathInfo classpathInfo = nodeItemInfo.getClasspathInfo();
				Iterator<ClasspathItemInfo> iter = classpathInfo.getItems();
				while (iter.hasNext()) {
					ClasspathItemInfo item = (ClasspathItemInfo) iter.next();
					if (item == null) continue;
					output.println (TAB+TAB+item.getName());
				}
			}

			{
				ManifestmfInfo manifestmfInfo = nodeItemInfo.getManifestmfInfo();
				if (! manifestmfInfo.isNone()) {
					output.println();
					output.println(TAB+"MANIFEST.MF");
					Iterator<ManifestmfItemInfo> iter = manifestmfInfo.getItems();
					while (iter.hasNext()) {
						ManifestmfItemInfo item = (ManifestmfItemInfo) iter.next();
						if (item == null) continue;
						output.println (TAB+TAB+item.getName());
					}
				}
			}

			{
				ModulemapsInfo modulemapsInfo = nodeItemInfo.getModulemapsInfo();
				if (! modulemapsInfo.isNone()) {
					output.println();
					output.println(TAB+"modulemaps.xml");
					Iterator<ModulemapsItemInfo> iter = modulemapsInfo.getItems();
					while (iter.hasNext()) {
						ModulemapsItemInfo item = (ModulemapsItemInfo) iter.next();
						if (item == null) continue;
						output.println (TAB+TAB+item.getName());
					}
				}
			}
		}
		output.println();
		output.println ("Report1 - Completed");
		output.println();
		output.close();
	}

	public static void showReport2 (NodeInfo nodeInfo) {
		File file = new File (App.REPORTSDIR+"/report2.lis");
		if (file.exists()) file.delete();
		PrintFile output = new PrintFile(file);
		output.println();
		output.println ("Report2 - For each project, list referenced projects");
		output.println();
		output.println ("Flags:");
		output.println (TAB+".project");
		output.println (TAB+".classpath");
		output.println (TAB+"MANIFEST.MF");
		output.println (TAB+"modulemaps.xml");
		output.println();
		Iterator<NodeItemInfo> iter = nodeInfo.getItems();
		while (iter.hasNext()) {
			NodeItemInfo nodeItemInfo = (NodeItemInfo) iter.next();
			output.println();
			output.println (nodeItemInfo.getName());
			TotalInfo totalInfo = nodeItemInfo.getTotalInfo();
			Iterator<TotalItemInfo> iter2 = totalInfo.getItems();
			while (iter2.hasNext()) {
				TotalItemInfo item = (TotalItemInfo) iter2.next();
				if (item == null) continue;
				output.print (TAB+"(");
				output.print (showBoolean (item.isProject())); output.print (",");
				output.print (showBoolean (item.isClasspath())); output.print (",");
				output.print (showBoolean (item.isManifest())); output.print (",");
				output.print (showBoolean (item.isModulemaps()));
				output.print (")");
				for (int i = 0; i < 5; i++) output.print (" ");
				output.print (item.getName());
				output.println();
			}
		}
		output.println();
		output.println ("Report2 - Completed");
		output.println();
		output.close();
	}

	public static void showReport3 (BucketInfo bucketInfo, int maxLevel) {
		File file = new File (App.REPORTSDIR+"/report3.lis");
		if (file.exists()) file.delete();
		PrintFile output = new PrintFile(file);
		output.println();
		output.println ("Report3 - For each project, calculate recursive counts for referenced projects.");
		output.println();
		output.print ("Maximum level of recursion is ");
		output.print (showInt (maxLevel));
		output.println();
		output.println ("Counts:");
		output.println (TAB+"Total");
		output.println (TAB+".project");
		output.println (TAB+".classpath");
		output.println (TAB+"MANIFEST.MF");
		output.println (TAB+"modulemaps.xml");
		output.println();
		Iterator<BucketItemInfo> iterator = bucketInfo.getItems();
		while (iterator.hasNext()) {
			BucketItemInfo bucketItemInfo = (BucketItemInfo) iterator.next();
			output.println();
			output.println (bucketItemInfo.getName());

			{
				output.println();
				SubBucketInfo subBucketInfo = bucketItemInfo.getSubBucketInfo();
				Iterator<SubBucketItemInfo> iter = subBucketInfo.getItems();
				while (iter.hasNext()) {
					SubBucketItemInfo item = (SubBucketItemInfo) iter.next();
					if (item == null) continue;
					output.print (TAB+"(");
					output.print (showInt (item.getTotal()));
					output.print (")  ");
					output.print (TAB+"(");
					output.print (showInt (item.getCounter1())); output.print (",");
					output.print (showInt (item.getCounter2())); output.print (",");
					output.print (showInt (item.getCounter3())); output.print (",");
					output.print (showInt (item.getCounter4()));
					output.print (")");
					for (int i = 0; i < 5; i++) output.print (" ");
					output.print (item.getName());
					output.println();
				}
			}
		}
		output.println();
		output.println ("Report3 - Completed");
		output.println();
		output.close();
	}

	public static void showReport4 (NodeInfo nodeInfo) {
		File file = new File (App.REPORTSDIR+"/report4.lis");
		if (file.exists()) file.delete();
		PrintFile output = new PrintFile(file);
		output.println();
		output.println ("Report4 - Projects with a reference in .project only");
		output.println();
		output.println ("Flags:");
		output.println (TAB+".project");
		output.println (TAB+".classpath");
		output.println (TAB+"MANIFEST.MF");
		output.println (TAB+"modulemaps.xml");
		output.println();
		Iterator<NodeItemInfo> iter = nodeInfo.getItems();
		while (iter.hasNext()) {
			NodeItemInfo nodeItemInfo = (NodeItemInfo) iter.next();
			output.println();
			output.println (nodeItemInfo.getName());
			TotalInfo totalInfo = nodeItemInfo.getTotalInfo();
			Iterator<TotalItemInfo> iter2 = totalInfo.getItems();
			while (iter2.hasNext()) {
				TotalItemInfo item = (TotalItemInfo) iter2.next();
				if (item == null) continue;
				if (! item.isProject()) continue;
				if (item.isClasspath()) continue;
				if (item.isManifest()) continue;
				if (item.isModulemaps()) continue;
				output.print (TAB+"(");
				output.print (showBoolean (item.isProject())); output.print (",");
				output.print (showBoolean (item.isClasspath())); output.print (",");
				output.print (showBoolean (item.isManifest())); output.print (",");
				output.print (showBoolean (item.isModulemaps()));
				output.print (")");
				for (int i = 0; i < 5; i++) output.print (" ");
				output.print (item.getName());
				output.println();
			}
		}
		output.println();
		output.println ("Report4 - Completed");
		output.println();
		output.close();
	}

	public static void showReport5 (NodeInfo nodeInfo, int maxLevel) {
		Iterator<NodeItemInfo> iter = nodeInfo.getItems();
		while (iter.hasNext()) {
			NodeItemInfo nodeItemInfo = (NodeItemInfo) iter.next();
			if (nodeItemInfo == null) continue;

			File file = new File (App.REPORTSDIR+"/report5_"+nodeItemInfo.getName()+".lis");
			if (file.exists()) file.delete();
			PrintFile output = new PrintFile (file);
			output.println();
			output.println ("Report5 - Recursively list references for project "+nodeItemInfo.getName());
			output.println();
			output.println ("Flags:");
			output.println (TAB+".project");
			output.println (TAB+".classpath");
			output.println (TAB+"MANIFEST.MF");
			output.println (TAB+"modulemaps.xml");
			output.println();
			output.println();
			output.println (nodeItemInfo.getName());
			int counter = 0;
			handleRecursiveReferences (counter, maxLevel, nodeInfo, output, nodeItemInfo);
			output.println();
			output.println ("Report5 - Completed");
			output.println();
			output.close();
			output = null;
			file = null;
		}
	}
	private static void handleRecursiveReferences (int counter, int maxLevel, NodeInfo nodeInfo, PrintFile output, NodeItemInfo currentNodeItemInfo) {
		if (currentNodeItemInfo == null) return;
		counter++;
		if (counter > maxLevel) return;
		TotalInfo totalInfo = currentNodeItemInfo.getTotalInfo();
		Iterator<TotalItemInfo> iter = totalInfo.getItems();
		while (iter.hasNext()) {
			TotalItemInfo totalItemInfo = (TotalItemInfo) iter.next();
			if (totalItemInfo == null) continue;
			repeatString (output, TAB, counter);
			output.print ("(");
			output.print (showBoolean (totalItemInfo.isProject())); output.print (",");
			output.print (showBoolean (totalItemInfo.isClasspath())); output.print (",");
			output.print (showBoolean (totalItemInfo.isManifest())); output.print (",");
			output.print (showBoolean (totalItemInfo.isModulemaps()));
			output.print (")");
			repeatString (output, " ", 5);
			output.print (totalItemInfo.getName());
			output.println();

			NodeItemInfo nextNodeItemInfo = nodeInfo.getItem (totalItemInfo.getName());
			handleRecursiveReferences (counter, maxLevel, nodeInfo, output, nextNodeItemInfo);
		}
	}

	public static void showReport6 (NodeInfo nodeInfo) {
		File file = new File (App.REPORTSDIR+"/report6.lis");
		if (file.exists()) file.delete();
		PrintFile output = new PrintFile(file);
		output.println();
		output.println ("Report6 - For each project, list .project entries");
		output.println();
		output.println ("Flags:");
		output.println (TAB+".project");
		output.println (TAB+".classpath");
		output.println (TAB+"MANIFEST.MF");
		output.println (TAB+"modulemaps.xml");
		output.println();
		Iterator<NodeItemInfo> iter = nodeInfo.getItems();
		while (iter.hasNext()) {
			NodeItemInfo nodeItemInfo = (NodeItemInfo) iter.next();
			output.println();
			output.println (nodeItemInfo.getName());
			TotalInfo totalInfo = nodeItemInfo.getTotalInfo();
			Iterator<TotalItemInfo> iter2 = totalInfo.getItems();
			while (iter2.hasNext()) {
				TotalItemInfo item = (TotalItemInfo) iter2.next();
				if (item == null) continue;
				if (! item.isProject()) continue;
				output.print (TAB+"(");
				output.print (showBoolean (item.isProject())); output.print (",");
				output.print (showBoolean (item.isClasspath())); output.print (",");
				output.print (showBoolean (item.isManifest())); output.print (",");
				output.print (showBoolean (item.isModulemaps()));
				output.print (")");
				for (int i = 0; i < 5; i++) output.print (" ");
				output.print (item.getName());
				output.println();
			}
		}
		output.println();
		output.println ("Report6 - Completed");
		output.println();
		output.close();
	}
}
