package com.idc.refs.data;

import java.io.File;
import java.io.Serializable;

import com.idc.refs.totals.TotalInfo;

/**
 *	Describe a NodeItemInfo
 *
 * @author John Vincent
 */
 
public class NodeItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private File dir;
	private int type;
	private ProjectInfo projectInfo;
	private ClasspathInfo classpathInfo;
	private ManifestmfInfo manifestmfInfo;
	private ModulemapsInfo modulemapsInfo;

	private TotalInfo totalInfo = new TotalInfo();

	public NodeItemInfo (String name, File dir, int type, ProjectInfo projectInfo, ClasspathInfo classpathInfo, 
			ManifestmfInfo manifestmfInfo, ModulemapsInfo modulemapsInfo) {
		this.name = name;
		this.dir = dir;
		this.type = type;
		this.projectInfo = projectInfo;
		this.classpathInfo = classpathInfo;
		this.manifestmfInfo = manifestmfInfo;
		this.modulemapsInfo = modulemapsInfo;
	}
	public void sort() {
		getProjectInfo().sort();
		getClasspathInfo().sort();
		getManifestmfInfo().sort();
		getModulemapsInfo().sort();
	}

	public String getName() {return name;}
	public File getDir() {return dir;}
	public int getType() {return type;}
	public ProjectInfo getProjectInfo() {return projectInfo;}
	public ClasspathInfo getClasspathInfo() {return classpathInfo;}
	public ManifestmfInfo getManifestmfInfo() {return manifestmfInfo;}
	public ModulemapsInfo getModulemapsInfo() {return modulemapsInfo;}

	public TotalInfo getTotalInfo() {return totalInfo;}

	public String toString() {
		return "("+getName()+","+getDir()+","+getType()+","+getProjectInfo()+","+getClasspathInfo()+","+
				getManifestmfInfo()+","+getModulemapsInfo()+")";
	}
}
