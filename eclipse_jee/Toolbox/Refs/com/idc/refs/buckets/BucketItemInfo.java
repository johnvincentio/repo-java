package com.idc.refs.buckets;

import java.io.Serializable;

import com.idc.refs.data.ClasspathItemInfo;
import com.idc.refs.data.ManifestmfItemInfo;
import com.idc.refs.data.ModulemapsItemInfo;
import com.idc.refs.data.ProjectItemInfo;

/**
 *	Describe a BucketItemInfo
 *
 * @author John Vincent
 */
 
public class BucketItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private SubBucketInfo subBucketInfo = new SubBucketInfo();

	public BucketItemInfo (String name) {this.name = name;}

	public String getName() {return name;}
	public SubBucketInfo getSubBucketInfo() {return subBucketInfo;}

	public void addToBucket (ProjectItemInfo projectItemInfo) {subBucketInfo.add (projectItemInfo);}
	public void addToBucket (ClasspathItemInfo classpathItemInfo) {subBucketInfo.add (classpathItemInfo);}
	public void addToBucket (ManifestmfItemInfo manifestmfItemInfo) {subBucketInfo.add (manifestmfItemInfo);}
	public void addToBucket (ModulemapsItemInfo modulemapsItemInfo) {subBucketInfo.add (modulemapsItemInfo);}
	public void sortBucket() {subBucketInfo.sort();}

	public String toString() {
		return "("+getName()+","+getSubBucketInfo()+")";
	}
}
