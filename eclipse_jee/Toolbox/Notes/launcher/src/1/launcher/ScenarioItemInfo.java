package com.idc.launcher;

import java.io.Serializable;

public class ScenarioItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private String application;
	private String main; 
	private String classpath;
	private String path;
	private String cwd;
	private ParamInfo paramInfo;
	private EnvInfo envInfo;

	public ScenarioItemInfo (String name, String application, String main, 
			String classpath, String path, String cwd,
			ParamInfo paramInfo, EnvInfo envInfo) {
		this.name = name;
		this.application = application;
		this.main = main;
		this.classpath = classpath;
		this.path = path;
		this.cwd = cwd;
		this.paramInfo = paramInfo;
		this.envInfo = envInfo;
	}
	public String getName() {return name;}
	public String getApplication() {return application;}
	public String getMain() {return main;}
	public String getClasspath() {return classpath;}
	public String getPath() {return path;}
	public String getCwd() {return cwd;}
	public ParamInfo getParamInfo() {return paramInfo;}
	public EnvInfo getEnvInfo() {return envInfo;}

	public void setName (String name) {this.name = name;}
	public void setApplication (String application) {this.name = application;}
	public void setMain (String main) {this.main = main;}
	public void setClasspath (String classpath) {this.classpath = classpath;}
	public void setPath (String path) {this.path = path;}
	public void setCwd (String cwd) {this.cwd = cwd;}

	public String toString() {
		return "("+getName()+","+getApplication()+","+getMain()+","+
				getClasspath()+","+getPath()+","+getCwd()+","+getParamInfo()+","+getEnvInfo()+")";
	}
}
