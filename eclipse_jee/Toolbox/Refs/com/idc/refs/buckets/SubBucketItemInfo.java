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
public class SubBucketItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private int counter1 = 0;
	private int counter2 = 0;
	private int counter3 = 0;
	private int counter4 = 0;

	public SubBucketItemInfo (ProjectItemInfo projectItemInfo) {
		name = projectItemInfo.getName();
		counter1++;
	}
	public SubBucketItemInfo (ClasspathItemInfo classpathItemInfo) {
		name = classpathItemInfo.getName();
		counter2++;
	}
	public SubBucketItemInfo (ManifestmfItemInfo manifestmfItemInfo) {
		name = manifestmfItemInfo.getName();
		counter3++;
	}
	public SubBucketItemInfo (ModulemapsItemInfo modulemapsItemInfo) {
		name = modulemapsItemInfo.getName();
		counter4++;
	}
	public String getName() {return name;}
	public int getCounter1() {return counter1;}
	public int getCounter2() {return counter2;}
	public int getCounter3() {return counter3;}
	public int getCounter4() {return counter4;}
	public int getTotal() {return counter1+counter2+counter3+counter4;}

	public void incrementCounter (ProjectItemInfo projectItemInfo) {this.counter1++;}
	public void incrementCounter (ClasspathItemInfo classpathItemInfo) {this.counter2++;}
	public void incrementCounter (ManifestmfItemInfo manifestmfItemInfo) {this.counter3++;}
	public void incrementCounter (ModulemapsItemInfo modulemapsItemInfo) {this.counter4++;}

	public String toString() {
		return "("+getName()+","+getCounter1()+","+getCounter2()+","+getCounter3()+","+getCounter4()+")";
	}
}
