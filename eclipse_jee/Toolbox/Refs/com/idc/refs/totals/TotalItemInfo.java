package com.idc.refs.totals;

import java.io.Serializable;

import com.idc.refs.data.ClasspathItemInfo;
import com.idc.refs.data.ManifestmfItemInfo;
import com.idc.refs.data.ModulemapsItemInfo;
import com.idc.refs.data.ProjectItemInfo;

/**
 *	Describe a TotalItemInfo
 *
 * @author John Vincent
 */
 
public class TotalItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private boolean project = false;
	private boolean classpath = false;
	private boolean manifest = false;
	private boolean modulemaps = false;

	public TotalItemInfo (ProjectItemInfo projectItemInfo) {
		name = projectItemInfo.getName();
		project = true;
	}
	public TotalItemInfo (ClasspathItemInfo classpathItemInfo) {
		name = classpathItemInfo.getName();
		classpath = true;
	}
	public TotalItemInfo (ManifestmfItemInfo manifestmfItemInfo) {
		name = manifestmfItemInfo.getName();
		manifest = true;
	}
	public TotalItemInfo (ModulemapsItemInfo modulemapsItemInfo) {
		name = modulemapsItemInfo.getName();
		modulemaps = true;
	}
	
	public String getName() {return name;}
	public boolean isProject() {return project;}
	public boolean isClasspath() {return classpath;}
	public boolean isManifest() {return manifest;}
	public boolean isModulemaps() {return modulemaps;}

	public void setName (String name) {this.name = name;}
	public void setProject (boolean project) {this.project = project;}
	public void setClasspath (boolean classpath) {this.classpath = classpath;}
	public void setManifest (boolean manifest) {this.manifest = manifest;}
	public void setModulemaps (boolean modulemaps) {this.modulemaps = modulemaps;}

	public String toString() {
		return "("+getName()+","+isProject()+","+isClasspath()+","+isManifest()+","+isModulemaps()+")";
	}
}
