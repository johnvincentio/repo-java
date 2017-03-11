package com.idc.launcher;

import java.io.Serializable;
import java.util.Iterator;
import java.io.File;

public class ScenarioItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private String application;
	private String main;
	private ParamInfo pathInfo;
	private ParamInfo classPathInfo;
	private String cwd;
	private ParamInfo paramInfo;
	private EnvInfo envInfo;

	public ScenarioItemInfo (String name, String application, String main, 
			String cwd, ParamInfo pathInfo, ParamInfo classPathInfo, 
			ParamInfo paramInfo, EnvInfo envInfo) {
		this.name = name;
		this.application = application;
		this.main = main;
		this.pathInfo = pathInfo;
		this.classPathInfo = classPathInfo;
		this.cwd = cwd;
		this.paramInfo = paramInfo;
		this.envInfo = envInfo;
	}
	public String getName() {return name;}
	public String getApplication() {return application;}
	public String getMain() {return main;}
	public String getPath() {
		System.out.println(">>> getPath");
		StringBuffer buf = new StringBuffer();
		boolean bFirst = true;
		Iterator<ParamItemInfo> iter = pathInfo.getItems();
		while (iter.hasNext()) {
			ParamItemInfo paramItemInfo = (ParamItemInfo) iter.next();
			if (! bFirst) buf.append (File.pathSeparatorChar);
			bFirst = false;
			buf.append (paramItemInfo.getName());
		}
		System.out.println("<<< getPath :"+buf.toString());
		return buf.toString();
	}
	public String getClasspath() {
		System.out.println(">>> getClasspath");
		StringBuffer buf = new StringBuffer();
		boolean bFirst = true;
		Iterator<ParamItemInfo> iter = classPathInfo.getItems();
		while (iter.hasNext()) {
			ParamItemInfo paramItemInfo = (ParamItemInfo) iter.next();
			if (! bFirst) buf.append (File.pathSeparatorChar);
			bFirst = false;
			buf.append (paramItemInfo.getName());
		}
		System.out.println("<<< getClasspath :"+buf.toString());
		return buf.toString();
	}
	public String getCwd() {return cwd;}
	public ParamInfo getParamInfo() {return paramInfo;}
	public EnvInfo getEnvInfo() {return envInfo;}

//	public void setName (String name) {this.name = name;}
//	public void setApplication (String application) {this.name = application;}
//	public void setMain (String main) {this.main = main;}
//	public void setClasspath (String classpath) {this.classpath = classpath;}
//	public void setPath (String path) {this.path = path;}
//	public void setCwd (String cwd) {this.cwd = cwd;}

	public String toString() {
		return "("+getName()+","+getApplication()+","+getMain()+","+
			getPath()+","+getClasspath()+","+getCwd()+","+getParamInfo()+","+getEnvInfo()+")";
	}
}
